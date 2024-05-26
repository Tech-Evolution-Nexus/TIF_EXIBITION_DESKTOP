/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Config.DB;
import Helper.Currency;
import Helper.FormatStrings;
import Helper.Notification;
import Helper.Validasi;
import View.PembelianView;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import App.Model.PembelianModel;
import App.Model.DetailObatModel;
import App.Model.JenisPenjualanModel;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class TransaksiPembelianController {

    public static boolean spIsClick;
    private String kodeObtSelect;
    private String kode_supplier;
    private String kode_obat;
    private String satuan;
    private PembelianModel modelpembelian = new PembelianModel();
    int indexEdit = -1;
    PembelianView view = new PembelianView();

    private ArrayList<Object[]> dataTable = new ArrayList<>();
    private ArrayList<Object[]> comboBoxList = new ArrayList<Object[]>();
    private ArrayList<Object[][]> hargaList = new ArrayList<Object[][]>();
    private ArrayList<Object[]> hargaListTemp = new ArrayList<Object[]>();
    
    public TransaksiPembelianController() {
event();
    }

    public void validasiharga() {
        try {
            String pembayaranText = view.getHarga().getText();
            if (pembayaranText.equals("")) {
                view.getHarga().setText("Rp. 0");
                return;
            }
            boolean isOnlyNumber = Validasi.onlyNumber(pembayaranText);
            long pembayaran = 0;
            if (isOnlyNumber) {
                pembayaran = Long.parseLong(pembayaranText);
                // Jika hanya berisi angka, langsung format dan tampilkan
                view.getHarga().setText(Currency.format(pembayaran));
            } else {
                // Jika tidak hanya berisi angka, deformat dulu menjadi int dan format ulang
                pembayaran = Currency.deformat(pembayaranText);
                view.getHarga().setText(Currency.format(pembayaran));
            }
        } catch (Exception e) {
            view.getHarga().setText("Rp. 0");
        }
    }

    public void tampilData() {
        resetTable();
        resetAll();
    }

    private void showHargaForm() {
      try {
          if ( kodeObtSelect == null) {
              JOptionPane.showMessageDialog(null, "Silahkan pilih obat terlebih dahulu");
            return;
        }
        ResultSet jenisObat = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat = '" + kodeObtSelect
                    + "'");
        DefaultTableModel model = (DefaultTableModel) view.getHargaList().getModel();
        model.setRowCount(0);
        comboBoxList.clear();
        while (jenisObat.next()) {
            int harga =(int) Helper.Currency.deformat(view.getHarga().getText());
            int hargaJual = harga;
            if (jenisObat.getString("margin_harga") != null) {
                hargaJual += jenisObat.getInt("margin_harga");
             
          } else {
              float hargaTotal = harga * (jenisObat.getFloat("margin_persen") / 100f);
              hargaJual += hargaTotal;
          }
          hargaJual = hargaJual * jenisObat.getInt("total");
        comboBoxList.add(new Object[] {Helper.Currency.format(hargaJual),Helper.Currency.format(jenisObat.getInt("harga"))});
        model.addRow(new Object[] { jenisObat.getString("satuan"),
                                    Helper.Currency.format(hargaJual)});
        }
        
        view.getHargaList().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox()) {

            JComboBox input;  
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                input = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row,
                column);
                ;
                input.removeAllItems();;
                for (Object data : comboBoxList.get(row)) {
                    input.addItem(data);
                }
              input.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            fireEditingStopped(); 
                        }
                    });
                return input;
            }

            @Override
            public Object getCellEditorValue() {
                return input.getSelectedItem();
            }
            });
            view.getHargaList().getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JComboBox comboBox = new JComboBox<>();
                ;
                comboBox.removeAllItems();
                for (Object data : comboBoxList.get(row)) {
                comboBox.addItem(data);
                }
            if (value != null) {
                        comboBox.setSelectedItem(value);
                    }else if(hargaListTemp.size() > 0){
                       comboBox.setSelectedItem(hargaListTemp.get(row)[1]) ;
                    } else {
                        comboBox.setSelectedItem(0);
                    }
                return comboBox;
            }
            });
        
        view.getHargaForm().pack();
        view.getHargaForm().setLocationRelativeTo(null);
        view.getHargaForm().setVisible(true);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

   }

    public void simpanData() throws SQLException, ParseException {
//        try {
        if (dataTable.size() == 0) {
            Notification.showInfo("Silahkan pilih obat yg akan di restok", view.getTable());
            return;
        }
        long totalHargaPembelian = 0;
        DB.getConnection().setAutoCommit(false);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyykkmmss");
        String date2 = simpleDateFormat.format(new Date());
//            String codeTRX = "TRXP_" + date2;
        String nofak = view.getNofak().getText();
        String carap = view.getCarapembayaran().getSelectedItem().toString();
//            String issupp = "";
//            ResultSet dataS = DB.query("select kode_suplier from supplier where nama_suplier = '" + datasupp + "'");
//            dataS.next();
//
//            issupp = dataS.getString("kode_suplier");
//            if (datasupp.equalsIgnoreCase("Pilih Supplier")) {
//                issupp = "";
//            } else {
//                issupp = datasupp.split("-")[0];
//
//            }
        int totalDataObat = view.getTable().getRowCount();
        ResultSet ceknofaktur = modelpembelian.where("kode_transaksi", "=", view.getNofak().getText()).get();

        if (ceknofaktur.next()) {
            JOptionPane.showMessageDialog(view.getTable(), "No Faktur Sudah Ada");
        } else if (kode_supplier.isEmpty() || totalDataObat < 1) {
            JOptionPane.showMessageDialog(view.getTable(), "Id Supplier harus diisi dan row harus lebih dari 1");
        } else {
            HashMap<String, Object[]> uniqueDataMap = new HashMap<>();
            java.sql.Connection connection = DB.getConnection();
//                issupp = issupp.split(">")[1];

            String insertDetailQuery = "INSERT INTO detail_pembelian(kode_transaksi,no_batch, harga, qty, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertDetailStatement = connection.prepareStatement(insertDetailQuery);

            String insertStokQuery = "INSERT INTO detail_obat(no_batch,kode_obat, kode_suplier, jumlah_obat, harga_beli, tanggal_kadaluarsa,tanggal_masuk) VALUES (?,?, ?, ?, ?, ?,curdate())";
            PreparedStatement insertStokStatement = connection.prepareStatement(insertStokQuery);

            String updateObatQuery = "UPDATE obat SET jumlah_obat = jumlah_obat + ? WHERE kode_obat = ?";
            PreparedStatement updateObatStatement = connection.prepareStatement(updateObatQuery);

            for (int i = 0; i < totalDataObat; i++) {
                String nobatch = view.getTable().getValueAt(i, 0) != null ? view.getTable().getValueAt(i, 0).toString() : "";

                String kodeObat = view.getTable().getValueAt(i, 1) != null ? view.getTable().getValueAt(i, 1).toString() : "";
                String hargasatuan = view.getTable().getValueAt(i, 4) != null ?String.valueOf(Helper.Currency.deformat( view.getTable().getValueAt(i, 4).toString())) : "";

                String tambahStok = view.getTable().getValueAt(i, 5) != null ? view.getTable().getValueAt(i, 5).toString() : "";
                String tglKadaluarsa = view.getTable().getValueAt(i, 6) != null ? view.getTable().getValueAt(i, 6).toString() : "";
                ResultSet s = DB.query("SELECT * FROM detail_obat WHERE no_batch = '" + nobatch + "'");
                if (s.next()) {
                    JOptionPane.showMessageDialog(view.getTable(), "No Batch Obat Sudah Ada");
                    return;

                } else {
                    Object[] data = { kodeObat, hargasatuan, tambahStok, tglKadaluarsa };
                    uniqueDataMap.put(nobatch, data);
                }
                
                for (Object[] row : hargaList.get(i)) {
                    System.out.println("select * from data_jenis_penjualan where satuan='"+row[0]+"'");
                    ResultSet data = DB.query("select * from data_jenis_penjualan where satuan='"+row[0]+"'");
                    data.next();
                    String idSatuan = data.getString("id_satuan");
                    DB.query2("UPDATE jenis_penjualan set harga = '"+Helper.Currency.deformat(row[1].toString())+"' where kode_obat = '"+kodeObat+"' AND id_satuan = '"+idSatuan+"'");
                }
            }
            for (Map.Entry<String, Object[]> entry : uniqueDataMap.entrySet()) {
                String nobatch = entry.getKey();
                Object[] data = entry.getValue();
                String kodeObat = (String) data[0];
                String hargasatuan = String.valueOf(Helper.Currency.deformat((String) data[1]));
                String tambahStok = (String) data[2];
                String tglKadaluarsa = (String) data[3];
                DateFormat formatAwal = new SimpleDateFormat("dd-MM-yyyy");
                Date tglKadaluarsadate = formatAwal.parse(tglKadaluarsa);
                SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
                // Ubah objek Date ke string dengan format yang diinginkan
                String outputDate = outputFormatter.format(tglKadaluarsadate);
                int hargatotal = Integer.parseInt(tambahStok) * Integer.parseInt(hargasatuan);
                insertStokStatement.setString(1, nobatch);
                insertStokStatement.setString(2, kodeObat);
                insertStokStatement.setString(3, kode_supplier);
                insertStokStatement.setString(4, tambahStok);
                insertStokStatement.setString(5, hargasatuan);
                insertStokStatement.setString(6, outputDate);
                insertStokStatement.addBatch();

                insertDetailStatement.setString(1, nofak);
                insertDetailStatement.setString(2, nobatch);
                insertDetailStatement.setString(3, hargasatuan);
                insertDetailStatement.setString(4, tambahStok);
                insertDetailStatement.setInt(5, hargatotal);
                insertDetailStatement.addBatch(); // Add the query to the batch

                // Add the query to the batch
                updateObatStatement.setString(1, tambahStok);
                updateObatStatement.setString(2, kodeObat);
                updateObatStatement.addBatch();

                totalHargaPembelian += hargatotal;
            }

            String insertTransaksiQuery = "INSERT INTO transaksi_pembelian (kode_transaksi, kode_suplier,cara_pembayaran, total_harga) VALUES (?, ?,?, ?)";
            PreparedStatement insertTransaksiStatement = connection.prepareStatement(insertTransaksiQuery);
            insertTransaksiStatement.setString(1, nofak);
            insertTransaksiStatement.setString(2, kode_supplier);
            insertTransaksiStatement.setString(3, carap.toLowerCase());
            insertTransaksiStatement.setString(4, String.valueOf(totalHargaPembelian));
            insertTransaksiStatement.addBatch();
            insertTransaksiStatement.executeBatch();
            insertStokStatement.executeBatch();
            insertDetailStatement.executeBatch();

            updateObatStatement.executeBatch();
            JOptionPane.showMessageDialog(view.getTable(), "Pembelian Berhasil");
            resetAll();
        }

//        } catch (SQLException ex) {
//            Logger.getLogger(View.PenjualanView.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void updateTotal() {
        int total = 0;

        for (int i = 0; i < view.getTable().getRowCount(); i++) {
            // Tambahkan nilai kolom kedua ke total
            total += Integer.parseInt(view.getTable().getValueAt(i, 4).toString())*Integer.parseInt(view.getTable().getValueAt(i, 5).toString());
        }

        view.getTotalbayar().setText("Rp. " + total);
    }

    public void setTable() throws SQLException {
        if (!spIsClick) {
            return;
        }
        String namaSp = view.getDataSuplier().getText();

        ResultSet dataSp = getSuplier(namaSp);
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        model.setRowCount(0);
        while (dataSp.next()) {
            Object[] rowData = {dataSp.getString("kode_obat"), dataSp.getString("nama_obat"), dataSp.getString("satuan")};
            model.addRow(rowData);
            dataTable.add(rowData);
        }
    }

    public void tambahKeTable() {
        try {
            String namaOb = view.getDataObat().getText();
            String qty = view.getQty().getText();
            String harga = view.getHarga().getText();
            String nobatch = view.getNobatch().getText();
            Date date = view.getDatechose_exp().getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = sdf.format(date);
            String s = formattedDate;
            Instant instant = date.toInstant();
            LocalDate tanggalTujuanLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fiveDaysLater = LocalDate.now().plusDays(5);
            if (namaOb.equals("")) {
                Notification.showInfo("Silahkan Pilih Obat  Terlebih Dahulu", view);
                return;
            } else if(kode_obat.isEmpty()){
                Notification.showInfo("Kode Atau Nama Obat Tidak Ditemukan", view);
                return;
            }else if (Integer.parseInt(qty) <= 0 ) {
                Notification.showInfo("Qty harus lebih dari 0", view);
                return;
            } else if (tanggalTujuanLocalDate.isBefore(LocalDate.now()) || tanggalTujuanLocalDate.isEqual(LocalDate.now()) || tanggalTujuanLocalDate.isEqual(fiveDaysLater)) {
                Notification.showInfo("Tanggal Kadaluarsa Tidak Valid", view);
                return;
            } else if (nobatch.equals("-")) {
                Notification.showInfo("Silahkan Masukan Nobatch Terlebih Dahulu", view);
                return;
            }else if(hargaListTemp.isEmpty()){
                Notification.showInfo("Silahkan Sesuaikan Harga Terlebih Dahulu", view);
                return;
            };
            if (isNobatchExists(nobatch) && indexEdit ==-1) {
                Notification.showInfo("Nobatch sudah ada dalam tabel", view);
                return;
            }
            Pattern pattern = Pattern.compile("Rp\\.\\d+", Pattern.CASE_INSENSITIVE);  // Pola regex untuk mencocokkan "Rp." dan nilai numerik
            Matcher matcher = pattern.matcher(harga);

            if (matcher.find()) {
                harga = matcher.group(0).substring(3);
            }
            int index = 0;
            for (Object[] data : dataTable) {
                if (kode_obat.equalsIgnoreCase(data[1].toString()) && nobatch.equalsIgnoreCase(data[0].toString())) {
                    view.getTable().setValueAt(nobatch, index, 0);
                    view.getTable().setValueAt(Currency.deformat(harga), index, 4);
                    view.getTable().setValueAt(qty, index, 5);
                    Object[] rowData = { dataTable.get(index)[0], dataTable.get(index)[1], dataTable.get(index)[2],
                            dataTable.get(index)[3], Currency.deformat(harga), qty };
                    dataTable.add(rowData);
                    break;
                }
                index++;
            }
            
            DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
            if (indexEdit == -1) {
                Object[] rowData = { nobatch, this.kode_obat, namaOb, this.satuan, Currency.deformat(harga), qty, s };
                model.addRow(rowData);
                dataTable.add(rowData);
            }

            String[][] temp =new String[ hargaListTemp.size()][2];
            for (int i = 0; i < hargaListTemp.size(); i++) {
                temp[i][0] = hargaListTemp.get(i)[0].toString();
                temp[i][1] = hargaListTemp.get(i)[1].toString();
            }   
            if (indexEdit < 0) {
                hargaList.add(temp);
            } else {
                hargaList.set(indexEdit, temp);
            }
            updateTotal();
            indexEdit = -1;
            hargaListTemp.clear();
            view.getQty().setText("0");
            view.getHarga().setText("Rp.0");
            view.getDataObat().setText("");
            view.getNobatch().setText("");

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public boolean isNobatchExists(String nobatch) {
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String nobatchInTable = (String) model.getValueAt(i, 0); // Mengambil nilai nobatch dari kolom pertama (index 0)
            if (nobatch.equals(nobatchInTable)) {
                return true; // nobatch sudah ada dalam JTable
            }
        }
        return false; // nobatch tidak ditemukan dalam JTable
    }

    public void tambahkefield() {
        indexEdit = view.getTable().getSelectedRow();
        view.getNobatch().setText(view.getTable().getValueAt(view.getTable().getSelectedRow(), 0).toString());
        kode_obat = view.getTable().getValueAt(view.getTable().getSelectedRow(), 1).toString();
        view.getDataObat().setText(view.getTable().getValueAt(view.getTable().getSelectedRow(), 2).toString());
        satuan = view.getTable().getValueAt(view.getTable().getSelectedRow(), 3).toString();
        view.getQty().setText(view.getTable().getValueAt(view.getTable().getSelectedRow(), 5).toString());
        view.getHarga().setText(view.getTable().getValueAt(view.getTable().getSelectedRow(), 4).toString());
        view.getBtn_batal().setEnabled(true);
//        view.getNobatch().enable(false);
        view.getDataObat().setEnabled(false);
        hargaListTemp.clear();
        for (Object[] row : hargaList.get(indexEdit)) {
            hargaListTemp.add(row)  ;
        }
    }

    public void popup_obat() {
        if (!view.getDataObat().getText().equals("")) {
            view.getjPopupMenu2().show(view.getDataObat(), 0, view.getDataObat().getHeight());
        }

        try {

            ResultSet dataSp = DB.query("SELECT * FROM `data_obat` where nama_obat LIKE '%" + view.getDataObat().getText() + "%'");
            DefaultTableModel model = (DefaultTableModel) view.getTabelobat().getModel();
            model.setRowCount(0);
            while (dataSp.next()) {
                Object[] rowData = {dataSp.getString("kode_obat"), dataSp.getString("nama_obat"), dataSp.getString("jumlah_obat"), dataSp.getString("nama_kategori"), dataSp.getString("satuan"), dataSp.getString("kandungan")};
                model.addRow(rowData);

            }
        } catch (SQLException ex) {
            Logger.getLogger(PembelianView.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        view.getDataObat().requestFocus();
    }

    public void popup_supplier() {
        view.getjPopupMenu1().show(view.getDataSuplier(), 0, view.getDataSuplier().getHeight());

        try {
            ResultSet dataSp = DB.query("SELECT * FROM supplier where nama_suplier LIKE '%" + view.getDataSuplier().getText() + "%'");
            DefaultTableModel model = (DefaultTableModel) view.getTabelsupp().getModel();
            model.setRowCount(0);
            while (dataSp.next()) {
                Object[] rowData = {dataSp.getString("kode_suplier"), dataSp.getString("nama_suplier"), dataSp.getString("alamat"), dataSp.getString("nomor_telepon")};
                model.addRow(rowData);

            }
        } catch (SQLException ex) {
            Logger.getLogger(PembelianView.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        view.getDataSuplier().requestFocus();
    }

    public void tabelobclick() {
      try {
          String nama = view.getTabelobat().getValueAt(view.getTabelobat().getSelectedRow(), 1).toString();
        String kode = view.getTabelobat().getValueAt(view.getTabelobat().getSelectedRow(), 0).toString();
        String satuan = view.getTabelobat().getValueAt(view.getTabelobat().getSelectedRow(), 4).toString();
        kodeObtSelect = kode;
        this.kode_obat = kode;
        this.satuan = satuan;
        view.getjPopupMenu2().setVisible(false);
        view.getDataObat().setText(nama);

       
      } catch (Exception e) {
       JOptionPane.showMessageDialog(view, "Terjadi kesalahan pada sistem");
      }
    }

    public void tabelsuppclick() {

        String nama = view.getTabelsupp().getValueAt(view.getTabelsupp().getSelectedRow(), 1).toString();
        String kode = view.getTabelsupp().getValueAt(view.getTabelsupp().getSelectedRow(), 0).toString();
        this.kode_supplier = kode;
        view.getjPopupMenu1().setVisible(false);
        view.getDataSuplier().setText(nama);
    }


    public void simpanHarga() {
        // hargaList
        int rowCount = view.getHargaList().getRowCount();
        hargaListTemp.clear();
        for (int i = 0; i < rowCount; i++) {
            String satuan = view.getHargaList().getValueAt(i, 0).toString();
            String harga = view.getHargaList().getValueAt(i, 1).toString();
            hargaListTemp.add(new String[] {satuan,harga});
        }
        view.getHargaForm().dispose();
    }
    public void resetAll() {
        view.getBtn_batal().enable(false);
        view.getNofak().enable(true);
        view.getNobatch().enable(true);
        view.getDataSuplier().enable(true);
        view.getDataObat().enable(true);

        view.getTable().removeAll();
        view.getQty().setText("0");
        view.getHarga().setText("Rp.0");
        view.getDataObat().setText("");
        view.getNofak().setText("");
        view.getNobatch().setText("");
        view.getDataSuplier().setText("");
        kode_supplier = "";
        kode_obat = "";
        satuan = "";

//        suplierCom.setSelectedIndex(0);
        ((DefaultTableModel) view.getTable().getModel()).setRowCount(0);

    }

    public void resetobat() {
        view.getDataObat().setText("-");
        view.getHarga().setText("Rp.0");
        view.getQty().setText("0");
        view.getNobatch().setText("-");
        view.getNobatch().enable(true);
        view.getDataObat().enable(true);
        view.getBtn_batal().enable(false);
    }

    public void resetTable() {
        try {

            int index = view.getTable().getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
            model.removeRow(index);
            hargaList.remove(index);
            updateTotal();
        } catch (Exception e) {
        }

    }

    public ResultSet getSuplier(String nama) {
        return DB.query("select nama_obat,satuan,data_obat.kode_obat from  penjualan join data_obat on penjualan.kode_obat=data_obat.kode_obat join supplier on penjualan.kode_suplier= supplier.kode_suplier where nama_suplier ='" + nama + "'");
    }

    public Component getView() {
        return view;
    }
    private void event() {
        view.getBtnBayar().addActionListener(e -> {
            try {
                simpanData();
            } catch (SQLException ex) {
                Logger.getLogger(TransaksiPembelianController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(TransaksiPembelianController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        view.getAddList().addActionListener(e -> tambahKeTable());
        view.getBtn_batal().addActionListener(e -> resetobat());
        view.getBtnReset().addActionListener(e -> resetAll());
        view.getBtn_harga().addActionListener(e -> showHargaForm());
        view.getSimpanHarga().addActionListener(e -> simpanHarga());
        
        view.getNofak().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String regex = "[a-zA-Z0-9_-]";
                if (!String.valueOf(c).matches(regex)) {
                    e.consume(); // Mencegah karakter dimasukkan ke dalam teks
                }
            }

        });
        view.getHarga().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                validasiharga();
            }
        });
        view.getQty().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String regex = "\\d+";
                if (!String.valueOf(c).matches(regex)) {
                    e.consume(); // Mencegah karakter dimasukkan ke dalam teks
                }
            }

        });

        view.getDataObat().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                popup_obat();
            }
        });
        view.getDataSuplier().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                popup_supplier();

            }
        });
        view.getTabelobat().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelobclick();
            }
        });
        view.getTabelsupp().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelsuppclick();
            }
        });
        view.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int columnClicked = view.getTable().columnAtPoint(evt.getPoint());

                // Kolom ke-6 adalah kolom dengan index 5
                if (columnClicked == 7) {
                    resetTable();
                    resetobat();
                } else {
                    tambahkefield();
                }
            }
        });

        

  view.getQty().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
          String value = view.getQty().getText();
                    value = String.valueOf(Helper.Validasi.getNumeric(value));
                if (value.equals("") || Integer.parseInt(value) < 0) {
                    value = "0";
                }
            
                view.getQty().setText(value);

            }
        });
    }
}



