/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Config.DB;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class UserController  {

    private ArrayList<Integer> id = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private JTable table;
    private JDialog form;
    private ArrayList<Object[]> userList = new ArrayList<>();

    public UserController(JTable table, JDialog form) {
        this.table = table;
        this.form = form;
    }

    public void tampilData() {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM users order by id desc");
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

}
