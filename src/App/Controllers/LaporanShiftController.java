/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.Controllers;

import Config.DB;
import Helper.Currency;
import Helper.FormatTanggal;
import Laporan.ShiftView;
import de.wannawork.jcalendar.JCalendarComboBox;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.PreparedStatement;

/**
 *
 * @author admin
 */
public class LaporanShiftController {

    JTable table;
    JDialog form;
    JCalendarComboBox jdata3;
    JCalendarComboBox jdata4;

    
   public LaporanShiftController(JTable table, JDialog form,JCalendarComboBox jdata3,JCalendarComboBox jdata4) {
        this.table = table;
        this.form = form;
        this.jdata3 = jdata3;
        this.jdata4 = jdata4;
    }
    
    public void tampilData() {
         try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT users.nama,shift.saldo_awal_kas,shift.saldo_akhir_kas,shift.waktu_buka,shift.waktu_tutup,shift.total_penjualan,shift.total_pembayaran FROM users INNER JOIN shift ON shift.id_user=users.id order by shift.tanggal_dibuat desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
//            penjualanList.clear();

            while (data.next()) {
                Object[] dataTable = {
                    no,
                    data.getString("nama"),
                    data.getString("saldo_awal_kas"),
                    data.getString("saldo_akhir_kas"),
                    data.getString("waktu_buka"),
                    data.getString("waktu_tutup"),
                    data.getString("total_penjualan"),
                    data.getString("total_pembayaran")
                };

                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
//                penjualanList.add(new Object[]{data.getString("kode_transaksi"),
//                    data.getString("nama_suplier"),
//                    data.getString("jumlah_obat"),
//                    Currency.format(data.getInt("total_harga")),
//                    FormatTanggal.format(data.getTimestamp("tanggal_transaksi"))});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }
    }

    
    public void tambahData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public void simpanData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public void editData(Object[] id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public void hapusData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public void filter() {
        String tampilan1 = "yyyy-MM-dd";
        SimpleDateFormat tgl1 = new SimpleDateFormat(tampilan1);
        Date date1 = jdata3.getDate(); // Ganti jdata1 dengan objek Date yang dimiliki
        Date date2 = jdata4.getDate(); // Ganti jdata2 dengan objek Date yang dimiliki

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
            int no = 1;
            ResultSet data = DB.query("SELECT users.nama,shift.saldo_awal_kas,shift.saldo_akhir_kas,shift.waktu_buka,shift.waktu_tutup,shift.total_penjualan,shift.total_pembayaran FROM users INNER JOIN shift ON shift.id_user=users.id WHERE shift.tanggal_dibuat BETWEEN '" + awal + "' and '" + akhir + "'");
            DefaultTableModel tabel = (DefaultTableModel) table.getModel();
            tabel.setRowCount(0);
            while (data.next()) {
               Object[] dataTable = {
                    no,
                    data.getString("nama"),
                    data.getString("saldo_awal_kas"),
                    data.getString("saldo_akhir_kas"),
                    data.getString("waktu_buka"),
                    data.getString("waktu_tutup"),
                    data.getString("total_penjualan"),
                    data.getString("total_pembayaran")
                };
                tabel.addRow(dataTable);
                no++;
            }
        } catch (Exception e) {
            Logger.getLogger(ShiftView.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public void export() {
        try {
            // Koneksi ke database

            java.sql.Connection connection = DB.getConnection();
            // Query
            String sql = "SELECT users.nama,shift.saldo_awal_kas,shift.saldo_akhir_kas,shift.waktu_buka,shift.waktu_tutup,shift.total_penjualan,shift.total_pembayaran ,shift.tanggal_dibuat FROM users INNER JOIN shift ON shift.id_user=users.id order by shift.tanggal_dibuat desc";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            // Membuat workbook Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Datalaporan Shift");

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
            String fp = System.getProperty("user.home") + "/Downloads/laporan Shift " + timestamp + " .xlsx";
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
