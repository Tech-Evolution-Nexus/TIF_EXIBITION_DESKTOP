/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.Controllers;

import Config.DB;
import Helper.Currency;
import Helper.FormatTanggal;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author rexy
 */
public class LaporanLabaBersihController {

    private JTable table;
    private JDialog form;
    private ArrayList<Object[]> penjualanList = new ArrayList<>();

    public LaporanLabaBersihController(JTable table, JDialog form) {
        this.table = table;
        this.form = form;
    }

     
    public void tampilData() {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM laporan order by bulan_tahun desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
            penjualanList.clear();

            while (data.next()) {
                Object[] dataTable = {
                    no,
                    data.getString("pendapatan"),
                    data.getString("pengeluaran"),
                    data.getString("laba_bersih"),
                    data.getString("bulan_tahun")

                };
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);

                no++;
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

    }

    public void editData(Object[] rowTable) {

    }

    
    public void updateData(Object[] object) {
    }

    public void filterPeriode(Object[] component) {
        try {

            int index = ((JComboBox) component[0]).getSelectedIndex();

            String sql = "";
            if (index == 0) {
                sql = "SELECT * FROM laporan   order by bulan_tahun desc";
            } else if (index == 1) {
                sql = "SELECT SUM(pendapatan) AS pendapatan,SUM(pengeluaran) AS pengeluaran,SUM(laba_bersih) AS laba_bersih,LEFT(bulan_tahun, 4) as bulan_tahun FROM `laporan` GROUP by bulan_tahun";
            } 

            ResultSet data = DB.query(sql);
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
                   data.getString("pendapatan"),
                    data.getString("pengeluaran"),
                    data.getString("laba_bersih"),
                    data.getString("bulan_tahun")

                };
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);

                no++;
            }
        } catch (Exception e) {
            System.out.println("eror" + e.getMessage());
        }
    }
    public void export() {
        try {
            // Koneksi ke database

            java.sql.Connection connection = DB.getConnection();
            // Query
            String sql = "SELECT * FROM laporan order by bulan_tahun desc";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            // Membuat workbook Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Datalaporan laba bersih");

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
            String fp = System.getProperty("user.home") + "/Downloads/Laporan Bersih " + timestamp + " .xlsx";
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
    
}
