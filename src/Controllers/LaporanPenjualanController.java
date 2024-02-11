/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

/**
 *
 * @author Muhammad Nor Kholit
 */
import Config.DB;
import Helper.Currency;
import Helper.FormatTanggal;
import Laporan.PenjualanView;
import de.wannawork.jcalendar.JCalendarComboBox;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LaporanPenjualanController{

    ArrayList<String> datat = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private JTable table;
    private JDialog form;
    private JLabel txtkodetransaksi;
    private JLabel txttotalh;
    private JLabel tgltrx;
    private JLabel value_tot_obat;
    private JLabel value_users;
    private JLabel value_bayar;
    private JLabel value_kem;
    private JTable TBLdetail_1;
    private JCalendarComboBox jdata1;
    private JCalendarComboBox jdata2;
    private ArrayList<Object[]> penjualanList = new ArrayList<>();

    public LaporanPenjualanController(JTable table, JDialog form, Object[] com) {
        this.table = table;
        this.form = form;
        this.txtkodetransaksi = (JLabel) com[0];
        this.txttotalh = (JLabel) com[1];
        this.tgltrx = (JLabel) com[2];
        this.value_tot_obat = (JLabel) com[3];
        this.value_users = (JLabel) com[4];
        this.value_bayar = (JLabel) com[5];
        this.value_kem = (JLabel) com[6];
        this.TBLdetail_1 = (JTable) com[7];
        this.jdata1 = (JCalendarComboBox) com[8];
        this.jdata2 = (JCalendarComboBox) com[9];

    }

   
    public void tampilData() {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM laporan_penjualan  order by tanggal_transaksi desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
            penjualanList.clear();
            String[] dt;
            while (data.next()) {
                //  menyimpan data dalam bentuk array

                Object[] dataTable = {
                    no,
                    data.getString("kode_transaksi"),
                    data.getString("nama_pengguna"),
                    data.getString("total_obat"),
                    Currency.format(data.getInt("total_harga")),
                    FormatTanggal.format(data.getTimestamp("tanggal_transaksi"))

                };
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                penjualanList.add(new Object[]{data.getString("kode_transaksi"),
                    data.getString("total_obat"),
                    data.getString("total_harga"),
                    data.getDate("tanggal_transaksi"),
                    data.getString("nama_pengguna")});
                no++;
                dt = new String[]{ // Initialize dt with values
                    data.getString("pembayaran"),
                    data.getString("kembalian")
                };
                String concatenated = String.join(", ", dt); // Example: "pembayaranValue, kembalianValue"

                datat.add(concatenated);
            }

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }
    }

   
    public void tambahData(Object[] object) {
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

   
    public void hapusData(Object[] object) {
        try {
            int confirm = JOptionPane.showConfirmDialog(table, "Yakin menghapus data?");
            if (confirm != 0) {
                return;
            }
            int row = (int) object[0];
            if (row < 0) {
                JOptionPane.showMessageDialog(table, "Tidak ada baris yang dipilih");
                return;

            }
            int id = (int) penjualanList.get(row)[0];
            DB.query2("DELETE FROM kategori where id='" + id + "'");
            tampilData();

        } catch (Exception e) {
        }
    }

    public void cariData(String kunci) {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM laporan_penjualan ");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
            penjualanList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {
                    no,
                    data.getString("kode_transaksi"),
                    data.getString("nama_pengguna"),
                    data.getString("total_obat"),
                    Currency.format(data.getInt("total_harga")),
                    data.getString("tanggal_transaksi")

                };
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                penjualanList.add(new Object[]{data.getString("kode_transaksi"),
                    data.getString("total_obat"),
                    data.getString("total_harga"),
                    data.getDate("tanggal_transaksi"),
                    data.getTimestamp("nama_pengguna")});
                no++;
            }
        } catch (Exception e) {
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

   
    public void simpanData(Object[] object) {
        JTextField namaKategoriField = (JTextField) object[0];
        String namaKategori = namaKategoriField.getText();
        try {
            if (penjualanList.stream().anyMatch(satuan -> satuan[1].toString().trim().equalsIgnoreCase(namaKategori.trim()) && ((int) satuan[0]) != idEdit)) {
                JOptionPane.showMessageDialog(form, "Nama Kategori Sudah Ada");
            } else if (namaKategori.equals("")) {
                JOptionPane.showMessageDialog(form, "Nama Kategori Tidak Boleh Kosong");

            } else {
                if (status == 1) {
                    DB.query2("INSERT INTO kategori (nama_kategori)Values ('" + namaKategori + "')");
                } else {
                    DB.query2("UPDATE kategori set nama_kategori = '" + namaKategori + "' where id ='" + idEdit + "'");
                    status = 1;
                    idEdit = -1;
                }

                tampilData();
                form.dispose();
                namaKategoriField.setText("");
            }

        } catch (Exception e) {
            System.out.println("error simpan data " + e.getMessage());
        }
    }

    public void editData(Object[] rowTable) {
        int row = (int) rowTable[0];
        if (row < 0) {
            JOptionPane.showMessageDialog(table, "Tidak ada baris yang dipilih");
            return;
        }
        JTextField namaKategori = (JTextField) rowTable[1];
        String namaFromTable = table.getValueAt(row, 1).toString();
        namaKategori.setText(namaFromTable);
        status = 2;
        idEdit = (int) penjualanList.get(row)[0];
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

   
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void showDetail() {
        try {
            int no = 1;
            String kodeTrx = table.getValueAt(table.getSelectedRow(), 1).toString();
            String totqty = table.getValueAt(table.getSelectedRow(), 3).toString();
            String totalHarga = table.getValueAt(table.getSelectedRow(), 4).toString();
            String tglTrx = table.getValueAt(table.getSelectedRow(), 5).toString();
            String user = table.getValueAt(table.getSelectedRow(), 2).toString();
            String dataa = this.datat.get(table.getSelectedRow());
            String[] dataa_split = dataa.split(",");
            String pembayaran = dataa_split[0];
            String kembali = dataa_split[1];

            txtkodetransaksi.setText(kodeTrx);
            txttotalh.setText(totalHarga);
            tgltrx.setText(tglTrx);
            value_tot_obat.setText(totqty);
            value_users.setText(user);
            value_bayar.setText(Currency.format(Integer.parseInt(pembayaran.trim())));
            value_kem.setText(Currency.format(Integer.parseInt(kembali.trim())));

//            System.out.println("SELECT nama_obat,harga,qty,total_harga from detail_penjualan join obat on detail_penjualan.kode_obat = obat.kode_obat where kode_transaksi='" + kodeTrx + "'");
            ResultSet DBSetup = DB.query("SELECT nama_obat,harga,qty,subtotal from detail_penjualan join obat on detail_penjualan.kode_obat = obat.kode_obat where kode_transaksi='" + kodeTrx + "'");
//            ResultSet DBSetup = DB.query("SELECT nama_obat,harga,qty,subtotal from detail_penjualan join obat on detail_penjualan.kode_obat = obat.kode_obat where kode_transaksi='" + kodeTrx + "'");

            DefaultTableModel table1 = (DefaultTableModel) TBLdetail_1.getModel();
            table1.setRowCount(0);
            while (DBSetup.next()) {
                System.out.println(DBSetup.getString("qty"));
                Object[] data = {
                    no++,
                    DBSetup.getString("nama_obat"),
                    Currency.format(DBSetup.getInt("harga")),
                    DBSetup.getString("qty"),
                    Currency.format(DBSetup.getInt("subtotal"))

                };
                table1.addRow(data);

            }
            form.pack();
            form.setLocationRelativeTo(null);
            form.setVisible(true);

        } catch (Exception e) {
            Logger.getLogger(Laporan.PenjualanView.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void filter() {
        String tampilan1 = "yyyy-MM-dd";
        SimpleDateFormat tgl1 = new SimpleDateFormat(tampilan1);
        Date date1 = jdata1.getDate(); // Ganti jdata1 dengan objek Date yang dimiliki
        Date date2 = jdata2.getDate(); // Ganti jdata2 dengan objek Date yang dimiliki

        String tanggalawal = tgl1.format(date1);
        String tanggalakhir = tgl1.format(date2);

        // Konversi string tanggal ke objek LocalDate
        LocalDate awal = LocalDate.parse(tanggalawal);
        LocalDate akhir = LocalDate.parse(tanggalakhir);
        if (!awal.isBefore(akhir)) {
            LocalDate temp = awal;
            awal = akhir;
            akhir = temp;
        }

        try {
            int No = 1;
            ResultSet data = DB.query("SELECT * FROM laporan_penjualan WHERE DATE(tanggal_transaksi) BETWEEN '" + awal + "' AND '" + akhir + " ' ");
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.setRowCount(0);
            int no = 1;
            while (data.next()) {
                Object[] dataTable = {
                    no,
                    data.getString("kode_transaksi"),
                    data.getString("nama_pengguna"),
                    data.getString("total_obat"),
                    Currency.format(data.getInt("total_harga")),
                    FormatTanggal.format(data.getTimestamp("tanggal_transaksi"))

                };
                tables.addRow(dataTable);
                no++;

            }
        } catch (Exception e) {
            Logger.getLogger(PenjualanView.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }

    public void export() {
        try {
            // Koneksi ke database

            java.sql.Connection connection = DB.getConnection();
            // Query
            String sql = "SELECT * FROM laporan_penjualan";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            // Membuat workbook Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Datalaporan penjualan");

            // Menambahkan header
            Row headerRow = sheet.createRow(0);
            int columnCount = result.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                Cell cell = headerRow.createCell(i - 1);
                cell.setCellValue(result.getMetaData().getColumnName(i));
            }

            // Menambahkan data
            int rownum = 1;
            while (result.next()) {
                Row row = sheet.createRow(rownum++);
                for (int i = 1; i <= columnCount; i++) {
                    Cell cell = row.createCell(i - 1);
                    cell.setCellValue(result.getString(i));
                }
            }

            // Menyesuaikan lebar kolom
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy,HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            // Menyimpan ke file Excel
            String fp = System.getProperty("user.home") + "/Downloads/laporan penjualan" + timestamp + " .xlsx";
            FileOutputStream outputStream = new FileOutputStream(fp);
            workbook.write(outputStream);
            JOptionPane.showMessageDialog(table, "Berhasil Disimpan" + fp);
            workbook.close();
            connection.close();
            outputStream.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Printer() {

        String kodeTrx = table.getValueAt(table.getSelectedRow(), 1).toString();
        try {
            String sqlQuery = "SELECT * FROM `printerview` where kode_transaksi = '"+kodeTrx+"'";
            String path = "src/iReportdata/printpenjualan.jrxml";
            JasperDesign jasperDesign = JRXmlLoader.load(path);

            // Membuat objek JRDesignQuery
            JRDesignQuery newQuery = new JRDesignQuery();
            newQuery.setText(sqlQuery);

            // Mengaitkan JRDesignQuery dengan JasperDesign
            jasperDesign.setQuery(newQuery);

            // Langkah 3: Mengisi data ke laporan JasperReports
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            Map<String, Object> parameters = new HashMap<>();
            // Mengisi laporan dengan data dari database
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, DB.getConnection());

            // Menampilkan laporan (opsional)
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setVisible(true);
            
        } catch (JRException ex) {
            Logger.getLogger(View.PenjualanView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
