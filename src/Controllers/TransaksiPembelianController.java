/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Config.DB;
import Helper.Currency;
import Helper.Notification;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class TransaksiPembelianController {

    public static boolean spIsClick;

    private JTable table;
    private JComboBox suplierCom;
    private JComboBox obatCom;
    private JTextField qty;
    private JTextField harga;

    //array 
    private ArrayList<Object[]> dataTable = new ArrayList<>();

    public TransaksiPembelianController(Object[] com) {
        this.table = (JTable) com[0];
        this.suplierCom = (JComboBox) com[1];
        this.obatCom = (JComboBox) com[2];
        this.qty = (JTextField) com[3];
        this.harga = (JTextField) com[4];
    }

    
    public void tampilData() {
        resetTable();
        resetAll();
        ResultSet dataSp = DB.query("select * from supplier");
        ResultSet dataOb = DB.query("select * from data_obat");
        try {
            suplierCom.removeAllItems();
            suplierCom.addItem("Pilih Suplier");
            while (dataSp.next()) {
                suplierCom.addItem(dataSp.getString("nama_suplier"));
            }
            obatCom.removeAllItems();
            obatCom.addItem("Pilih Obat");
            while (dataOb.next()) {
                obatCom.addItem(dataOb.getString("nama_obat"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransaksiPembelianController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void tambahData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public void simpanData(Object[] object) {
        try {
            if (dataTable.size() == 0) {
                Notification.showInfo("Silahkan pilih obat yg akan di restok", table);
                return;
            }
            long totalHargaPembelian = 0;
            DB.getConnection().setAutoCommit(false);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyykkmmss");
            String date2 = simpleDateFormat.format(new Date());
            String codeTRX = "TRXP_" + date2;
            String datasupp = suplierCom.getSelectedItem().toString();
            String issupp = "";
            String issuppbaru = "";
            ResultSet dataS = DB.query("select kode_suplier from supplier where nama_suplier = '" + datasupp + "'");
            dataS.next();

            issupp = dataS.getString("kode_suplier");
//            if (datasupp.equalsIgnoreCase("Pilih Supplier")) {
//                issupp = "";
//            } else {
//                issupp = datasupp.split("-")[0];
//
//            }
            int totalDataObat = table.getRowCount();
            if (issupp.isEmpty() || totalDataObat < 1) {
                JOptionPane.showMessageDialog(table, "Id Supplier harus diisi dan row harus lebih dari 1");
            } else {

                java.sql.Connection connection = DB.getConnection();
//                issupp = issupp.split(">")[1];

                String insertDetailQuery = "INSERT INTO detail_pembelian(kode_transaksi, kode_obat, harga, qty, subtotal) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertDetailStatement = connection.prepareStatement(insertDetailQuery);

                String insertStokQuery = "INSERT INTO stok_obat(kode_obat, kode_suplier, jumlah_obat, harga_beli, tanggal_kadaluarsa,tanggal_masuk) VALUES (?, ?, ?, ?, ?,curdate())";
                PreparedStatement insertStokStatement = connection.prepareStatement(insertStokQuery);

                String updateObatQuery = "UPDATE obat SET jumlah_obat = jumlah_obat + ? WHERE kode_obat = ?";
                PreparedStatement updateObatStatement = connection.prepareStatement(updateObatQuery);

                for (int i = 0; i < totalDataObat; i++) {

                    String kodeObat = table.getValueAt(i, 0) != null ? table.getValueAt(i, 0).toString() : "";
                    String hargasatuan = table.getValueAt(i, 3) != null ? table.getValueAt(i, 3).toString() : "";

                    String tambahStok = table.getValueAt(i, 4) != null ? table.getValueAt(i, 4).toString() : "";
                    String tglKadaluarsa = table.getValueAt(i, 5) != null ? table.getValueAt(i, 5).toString() : "";
                    System.out.println("kode obat " + kodeObat + "\n hargaSatuan " + hargasatuan + "\ntambahStok " + tambahStok + "\ntglkadaluarsa " + tglKadaluarsa);
                    if (!tambahStok.isEmpty() && !hargasatuan.isEmpty() && !tglKadaluarsa.isEmpty()) {

                        DateFormat formatAwal = new SimpleDateFormat("dd-MM-yyyy");
                        Date tglKadaluarsadate = formatAwal.parse(tglKadaluarsa);

                        // Konversi ke LocalDate
                        Instant instant = tglKadaluarsadate.toInstant();
                        LocalDate tanggalTujuanLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                        // Bandingkan dengan tanggal saat ini
                        if (tanggalTujuanLocalDate.isBefore(LocalDate.now()) || tanggalTujuanLocalDate.isEqual(LocalDate.now())) {
                            JOptionPane.showMessageDialog(table, "Tanggal Kadaluarsa Tidak Valid");
                            return;
                        }
//                       
                        int hargatotal = Integer.parseInt(tambahStok) * Integer.parseInt(hargasatuan);

                        insertDetailStatement.setString(1, codeTRX);
                        insertDetailStatement.setString(2, kodeObat);
                        insertDetailStatement.setString(3, hargasatuan);
                        insertDetailStatement.setString(4, tambahStok);
                        insertDetailStatement.setInt(5, hargatotal);
                        insertDetailStatement.addBatch(); // Add the query to the batch

                        insertStokStatement.setString(1, kodeObat);
                        insertStokStatement.setString(2, issupp);
                        insertStokStatement.setString(3, tambahStok);
                        insertStokStatement.setString(4, hargasatuan);
                        insertStokStatement.setString(5, tanggalTujuanLocalDate.toString());
                        insertStokStatement.addBatch(); // Add the query to the batch

                        updateObatStatement.setString(1, tambahStok);
                        updateObatStatement.setString(2, kodeObat);
                        updateObatStatement.addBatch();

                        totalHargaPembelian += hargatotal;
                    } else {
                        JOptionPane.showMessageDialog(table, "semua colom harap diisi");
                        return;
                    }
                }

                String insertTransaksiQuery = "INSERT INTO transaksi_pembelian (kode_transaksi, kode_suplier, total_harga) VALUES (?, ?, ?)";
                PreparedStatement insertTransaksiStatement = connection.prepareStatement(insertTransaksiQuery);
                insertTransaksiStatement.setString(1, codeTRX);
                insertTransaksiStatement.setString(2, issupp);
                insertTransaksiStatement.setString(3, String.valueOf(totalHargaPembelian));
                insertTransaksiStatement.addBatch();
                insertTransaksiStatement.executeBatch();
                insertDetailStatement.executeBatch();
                insertStokStatement.executeBatch();
                updateObatStatement.executeBatch();
                JOptionPane.showMessageDialog(table, "Pembelian Berhasil");
                resetAll();
            }

        } catch (Exception ex) {
            Logger.getLogger(View.PenjualanView.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void editData(Object[] id) {
    }

    
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public void hapusData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setTable() throws SQLException {
        if (!spIsClick) {
            return;
        }
        if (suplierCom.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(table, "error");
            return;
        }
        String namaSp = suplierCom.getSelectedItem().toString();

        ResultSet dataSp = getSuplier(namaSp);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        while (dataSp.next()) {
            Object[] rowData = {dataSp.getString("kode_obat"), dataSp.getString("nama_obat"), dataSp.getString("satuan")};
            model.addRow(rowData);
            dataTable.add(rowData);
        }
    }

    public void tambahKeTable() {
        try {
            String namaOb = obatCom.getSelectedItem().toString();
            if (obatCom.getSelectedIndex() == 0) {
                Notification.showInfo("Silahkan Pilih Obat  Terlebih Dahulu", table);
                return;
            };
            String qty = this.qty.getText();
            String harga = this.harga.getText();
            Pattern pattern = Pattern.compile("Rp\\.\\d+", Pattern.CASE_INSENSITIVE);  // Pola regex untuk mencocokkan "Rp." dan nilai numerik
            Matcher matcher = pattern.matcher(harga);

            if (matcher.find()) {
                harga = matcher.group(0).substring(3);
            }
            int index = 0;
            for (Object[] data : dataTable) {
                if (namaOb.equalsIgnoreCase(data[1].toString())) {
                    table.setValueAt(Currency.deformat(harga), index, 3);
                    table.setValueAt(qty, index, 4);
                    Object[] rowData = {dataTable.get(index)[0], dataTable.get(index)[1], dataTable.get(index)[2], Currency.deformat(harga), qty};
                    dataTable.add(rowData);
                    return;
                }
                index++;
            }
            ResultSet dataSp = DB.query("SELECT * from data_obat where nama_obat  = '" + namaOb + "'");
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            while (dataSp.next()) {
                Object[] rowData = {dataSp.getString("kode_obat"), dataSp.getString("nama_obat"), dataSp.getString("satuan"), harga, qty};
                model.addRow(rowData);
                dataTable.add(rowData);

            }
            this.qty.setText("0");
            this.harga.setText("Rp.0");
            obatCom.setSelectedIndex(0);
            
        } catch (Exception e) {
        }

    }

    public void resetAll() {
        table.removeAll();
        qty.setText("0");
        harga.setText("Rp.0");
        obatCom.setSelectedIndex(0);
        suplierCom.setSelectedIndex(0);
        ((DefaultTableModel) table.getModel()).setRowCount(0);

    }

    public void resetTable() {
        try {

            int index = table.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(index);
        } catch (Exception e) {
        }

    }

    public ResultSet getSuplier(String nama) {
        return DB.query("select nama_obat,satuan,data_obat.kode_obat from  penjualan join data_obat on penjualan.kode_obat=data_obat.kode_obat join supplier on penjualan.kode_suplier= supplier.kode_suplier where nama_suplier ='" + nama + "'");
    }
}
