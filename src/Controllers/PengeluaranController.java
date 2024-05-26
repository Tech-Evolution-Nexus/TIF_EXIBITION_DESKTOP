/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import View.Auth.login;
import Config.DB;
import Helper.Currency;
import Helper.FormatTanggal;
import View.PengeluaranView;
import de.wannawork.jcalendar.JCalendarComboBox;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class PengeluaranController {

    private ArrayList<Integer> id = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private JTable table;
    private JDialog form;
    private JTable tbl_pengeluaran;
    private JLabel totharga;
    private JTextField txt_harga;
    private JTextField txt_ket;
    private JCalendarComboBox datechoser;
    private JButton btn_edit;
    private JButton btn_hapus;
    private ArrayList<Object[]> userList = new ArrayList<>();
    int totalHargaPenjualan = 0;
    Preferences userPreferences = Preferences.userNodeForPackage(login.class);

    public PengeluaranController(JTable table, JDialog form, Object[] com) {
        this.table = table;
        this.form = form;
        this.tbl_pengeluaran = (JTable) com[0];
        this.totharga = (JLabel) com[1];
        this.txt_harga = (JTextField) com[2];
        this.txt_ket = (JTextField) com[3];
        this.datechoser = (JCalendarComboBox) com[4];
        this.btn_edit = (JButton) com[5];
        this.btn_hapus = (JButton) com[6];
    }

    public void tampilData() {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT nama,total_pengeluaran,keterangan,tanggal_pengeluaran,pengeluaran.id FROM pengeluaran join users on  pengeluaran.id_user =users.id order by pengeluaran.id desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
            id.clear();
            userList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("nama"), Currency.format(data.getInt("total_pengeluaran")), data.getString("keterangan"), FormatTanggal.formatDate(data.getDate("tanggal_pengeluaran"))};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                userList.add(new Object[]{data.getInt("id"), data.getString("nama"), data.getString("total_pengeluaran"), data.getString("keterangan"), data.getDate("tanggal_pengeluaran")});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error pengeluaran " + e.getMessage());
        }
    }

    public void tambahData(Object[] object) {
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    public void hapusData(Object[] object) {
        try {
            int row = (int) object[0];
            if (row < 0) {
                JOptionPane.showMessageDialog(table, "Tidak ada baris yang dipilih");
                return;

            }
            int confirm = JOptionPane.showConfirmDialog(table, "Yakin menghapus data?");
            if (confirm != 0) {
                return;
            }

            int id = (int) userList.get(row)[0];
            ResultSet dataUserTrx = DB.query("SELECT count(*) as count from transaksi_penjualan where id_user='" + id + "'");
            dataUserTrx.next();
            if (dataUserTrx.getInt("count") > 0) {
                JOptionPane.showMessageDialog(table, "Data user gagal dihapus, Data sedang digunakan");
                return;
            }
            DB.query2("DELETE FROM users where id='" + id + "'");
            JOptionPane.showMessageDialog(table, "Data Berhasil Di Hapus");

            tampilData();

        } catch (Exception e) {
        }
    }

    public void cariData(String kunci) {
        try {
            ResultSet data = DB.query("SELECT * FROM users where nama like '%" + kunci + "%' OR  alamat like '%" + kunci + "%' order by id desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
            id.clear();
            userList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("no_ktp"), data.getString("nama"), data.getString("alamat"), data.getString("username"), data.getString("role")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                id.add(data.getInt("id"));
                userList.add(new Object[]{data.getInt("id"), data.getString("no_ktp"), data.getString("nama"), data.getString("username"), data.getString("password"), data.getString("alamat"), data.getString("role")});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

    public void simpanData(Object[] object) {
        JTextField ktpField = (JTextField) object[0];
        JTextField namaUserField = (JTextField) object[1];
        JTextField usernameField = (JTextField) object[2];
        JTextField passwordField = (JTextField) object[3];
        JTextField alamatField = (JTextField) object[4];
        JComboBox roleField = (JComboBox) object[5];

        String ktp = ktpField.getText();
        String nama = namaUserField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String alamat = alamatField.getText();
        String role = roleField.getSelectedItem().toString();
        try {
            if (userList.stream().anyMatch(satuan -> satuan[0].toString().trim().equalsIgnoreCase(ktp.trim()) && satuan[3].toString().trim().equalsIgnoreCase(username.trim()) && ((int) satuan[0]) != idEdit)) {
                JOptionPane.showMessageDialog(form, "Nama Kategori Sudah Ada");
            } else if (ktp.equals("") || nama.equals("") || username.equals("") || password.equals("") || alamat.equals("") || role.equals("")) {
                JOptionPane.showMessageDialog(form, "Field Tidak Boleh Kosong");

            } else {
                if (status == 1) {
                    DB.query2("INSERT INTO users (no_ktp,nama,alamat,username,password,role)Values ('" + ktp + "','" + nama + "','" + alamat + "','" + username + "','" + password + "','" + role + "')");
                } else {
                    DB.query2("UPDATE users SET no_ktp = '" + ktp + "', nama = '" + nama + "', alamat = '" + alamat + "', username = '" + username + "', password = '" + password + "', role = '" + role + "' WHERE id = '" + idEdit + "'");
                    status = 1;
                    idEdit = -1;
                }
                JOptionPane.showMessageDialog(table, "Data Berhasil Di Simpan");

                tampilData();
                form.dispose();
                ktpField.setText("");
                namaUserField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                alamatField.setText("");
                roleField.setSelectedIndex(0);
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
        idEdit = (int) userList.get(row)[0];
        ((JTextField) rowTable[1]).setText(userList.get(row)[1].toString());
        ((JTextField) rowTable[2]).setText(userList.get(row)[2].toString());
        ((JTextField) rowTable[3]).setText(userList.get(row)[3].toString());
        ((JTextField) rowTable[4]).setText(userList.get(row)[4].toString());
        ((JTextField) rowTable[5]).setText(userList.get(row)[5].toString());
        ((JComboBox) rowTable[6]).setSelectedItem(userList.get(row)[6].toString());
        status = 2;

        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void tbl_pengeluaranMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            btn_edit.setEnabled(true);
            btn_hapus.setEnabled(true);
            int row = tbl_pengeluaran.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) tbl_pengeluaran.getModel();
            DateFormat formatTujuan = new SimpleDateFormat("dd-MM-yyyy");
            String dte = model.getValueAt(row, 2).toString();
            //            String defaulttgl = formatTujuan.format(dte);
            datechoser.setDate(formatTujuan.parse(dte));
            txt_ket.setText(model.getValueAt(row, 0).toString());
            txt_harga.setText(model.getValueAt(row, 1).toString());

        } catch (ParseException ex) {
            Logger.getLogger(PengeluaranView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {
        String ket = txt_ket.getText();
        String harga = txt_harga.getText();
        DateFormat formatTujuan = new SimpleDateFormat("dd-MM-yyyy");
        String tglpengeluaran = formatTujuan.format(datechoser.getDate());
        //        String tgl = ;
        if (!ket.isEmpty() && !harga.isEmpty()) {
            DefaultTableModel tbl = (DefaultTableModel) tbl_pengeluaran.getModel();
            Object[] dataArray = {ket, harga, tglpengeluaran};
            tbl.addRow(dataArray);
            totalHargaPenjualan += Integer.parseInt(harga);
            totharga.setText(Currency.format(totalHargaPenjualan));
            clearr();
        } else {
            JOptionPane.showMessageDialog(form, "field harus di isi semua");
        }
    }

    public void btn_editActionPerformed(java.awt.event.ActionEvent evt) {
        String ket = txt_ket.getText();
        String harga = txt_harga.getText();
        DateFormat formatTujuan = new SimpleDateFormat("dd-MM-yyyy");
        String tglpengeluaran = formatTujuan.format(datechoser.getDate());
        int row = tbl_pengeluaran.getSelectedRow();
        if (!ket.isEmpty() && !harga.isEmpty()) {
            DefaultTableModel tbl = (DefaultTableModel) tbl_pengeluaran.getModel();
            tbl.setValueAt(ket, row, 0);
            tbl.setValueAt(harga, row, 1);
            tbl.setValueAt(tglpengeluaran, row, 2);
            clearr();
            totalfn();

        } else {
            JOptionPane.showMessageDialog(form, "field harus di isi semua");
        }
    }

    public void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tbl_pengeluaran.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tbl_pengeluaran.getModel();
        model.removeRow(row);
        clearr();
        totalfn();
    }

    public void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            DefaultTableModel model = (DefaultTableModel) tbl_pengeluaran.getModel();
            model.setRowCount(0);
            DateFormat formatTujuan = new SimpleDateFormat("dd-MM-yyyy");

            txt_ket.setText("");
            txt_harga.setText("");
            totharga.setText("");
            String defaulttgl = formatTujuan.format(new Date());
            datechoser.setDate(formatTujuan.parse(defaulttgl));
        } catch (ParseException ex) {
            Logger.getLogger(PengeluaranView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void txt_hargaKeyTyped(java.awt.event.KeyEvent evt) {
        char character = evt.getKeyChar();
        if (!Character.isDigit(character) || Character.isWhitespace(character)) {
            evt.consume();
        }
    }

    public void btn_simpanActionPerformed(java.awt.event.ActionEvent evt) {
        String datalogin = userPreferences.get("localLogin", null);

        if (datalogin != null) {

            try {
                JSONArray retrievedArray = new JSONArray(datalogin);
                java.sql.Connection connection = DB.getConnection();

                String insertDetailQuery = "INSERT INTO pengeluaran(id_user, total_pengeluaran, keterangan, tanggal_pengeluaran) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertDetailQuery);
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                int totalDataObat = tbl_pengeluaran.getRowCount();
                for (int i = 0; i < totalDataObat; i++) {
                    String keterangan = tbl_pengeluaran.getValueAt(i, 0).toString();
                    String harga = tbl_pengeluaran.getValueAt(i, 1).toString();
                    Date tgl = null;

                    Date date = inputFormat.parse(tbl_pengeluaran.getValueAt(i, 2).toString());
                    String formattedDate = outputFormat.format(date);
                    insertStatement.setString(1, retrievedArray.getString(0));
                    insertStatement.setInt(2, Integer.parseInt(harga));
                    insertStatement.setString(3, keterangan);
                    insertStatement.setString(4, formattedDate);
                    insertStatement.addBatch();
                }
                insertStatement.executeBatch();
                JOptionPane.showMessageDialog(form, "Data Berhasil Ditambahkan");
                DefaultTableModel model = (DefaultTableModel) tbl_pengeluaran.getModel();
                model.setRowCount(0);
                form.dispose();
                totharga.setText("");
                clearr();
            } catch (JSONException ex) {
                Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(PengeluaranView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(PengeluaranView.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void totalfn() {
        int rowCount = tbl_pengeluaran.getRowCount();//mengambil total data yg ada

        for (int i = 0; i < rowCount; i++) {
            if (i == rowCount - 1) {
                break;//meloncati pengulangin untuk data pada baris terakhir karena datanya kosong
            }
            totalHargaPenjualan += Integer.parseInt(tbl_pengeluaran.getValueAt(i, 1).toString());//menjumlahkan data total harga dan disimpan pada total harga penjualan
        }
        totharga.setText(Currency.format(totalHargaPenjualan));

    }

    public void clearr() {
        try {
            txt_ket.setText("");
            txt_harga.setText("");
//            totharga.setText("");
            DateFormat formatTujuan = new SimpleDateFormat("dd-MM-yyyy");
            String defaulttgl = formatTujuan.format(new Date());
            datechoser.setDate(formatTujuan.parse(defaulttgl));
        } catch (ParseException ex) {
            Logger.getLogger(PengeluaranView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
