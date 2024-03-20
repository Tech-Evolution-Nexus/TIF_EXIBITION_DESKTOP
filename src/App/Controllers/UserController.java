/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.Controllers;

import Config.DB;
import Helper.DataFormat;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class UserController  {

    private ArrayList<Integer> id = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private ArrayList<Object[]> userList = new ArrayList<>();

    public void tampilData(JTable table) {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM users order by id desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.setRowCount(0);
            id.clear();
            userList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no,  data.getString("nama"),data.getString("no_hp"), data.getString("alamat"), data.getString("username"), data.getString("role")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                id.add(data.getInt("id"));
                userList.add(new Object[]{data.getInt("id"), data.getString("no_hp"), data.getString("nama"), data.getString("username"), data.getString("password"), data.getString("alamat"), data.getString("role")});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }
    }

    
    public void tambahData(JDialog form) {
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    
    public void hapusData(int index ,JTable table) {
        try {
            int row = (int) index;
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


        } catch (Exception e) {
        }
    }

    public void cariData(String kunci,JTable table) {
        try {
            ResultSet data = DB.query("SELECT * FROM users where nama like '%" + kunci + "%' OR  alamat like '%" + kunci + "%' order by id desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            id.clear();
            userList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("no_hp"), data.getString("nama"), data.getString("alamat"), data.getString("username"), data.getString("role")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                id.add(data.getInt("id"));
                userList.add(new Object[]{data.getInt("id"), data.getString("no_hp"), data.getString("nama"), data.getString("username"), data.getString("password"), data.getString("alamat"), data.getString("role")});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

    
    public int simpanData(DataFormat data,JTable table,JDialog form) {
        String notlp = data.get("no_hp").toString();
        String nama = data.get("nama_user").toString();
        String username = data.get("username").toString();
        String password = data.get("password").toString();
        String alamat = data.get("alamat").toString();
        String role = data.get("role").toString();
        try {
           ResultSet checkNoTlp =  DB.query("SELECT * FROM users where no_hp = '"+notlp+"' AND id <> '"+idEdit+"'");
           ResultSet checkNoUsername =  DB.query("SELECT * FROM users where username = '"+username+"' AND id <> '"+idEdit+"'");
             if (checkNoTlp.next()) {
                JOptionPane.showMessageDialog(form, "Nomor Telepon Sudah Ada");
            }else if (checkNoUsername.next()) {
                JOptionPane.showMessageDialog(form, "Username Sudah Ada");
            }else if (notlp.equals("") || nama.equals("") || username.equals("") || password.equals("") || alamat.equals("") || role.equals("")) {
                JOptionPane.showMessageDialog(form, "Field Tidak Boleh Kosong");

            }else if (!notlp.matches("(^\\+62|0)(\\d{8,15})$")) {
                JOptionPane.showMessageDialog(form, "Nomor telepon tidak valid ");
            }  else {
                
                if (status == 1) {
                    DB.query2("INSERT INTO users (no_hp,nama,alamat,username,password,role)Values ('" + notlp + "','" + nama + "','" + alamat + "','" + username + "','" + password + "','" + role + "')");
                } else {
                    DB.query2("UPDATE users SET no_hp = '" + notlp + "', nama = '" + nama + "', alamat = '" + alamat + "', username = '" + username + "', password = '" + password + "', role = '" + role + "' WHERE id = '" + idEdit + "'");
                    status = 1;
                    idEdit = -1;
                }
                JOptionPane.showMessageDialog(form, "Data Berhasil Di Simpan");

                form.dispose();
//                ktpField.setText("");
//                namaUserField.setText("");
//                usernameField.setText("");
//                passwordField.setText("");
//                alamatField.setText("");
//                roleField.setSelectedIndex(0);
                  return 1;
            }
                  return 0;

        } catch (Exception e) {
            System.out.println("error simpan data " + e.getMessage());
                        return 0;
        }
    }

    public void editData(int index ,DataFormat data,JTable table,JDialog form) {
        int row = (int) index;
        if (row < 0) {
            JOptionPane.showMessageDialog(table, "Tidak ada baris yang dipilih");
            return;
        }
        idEdit = (int) userList.get(row)[0];
        ((JTextField) data.get("nama_user")).setText(userList.get(row)[2].toString());
        ((JTextField) data.get("no_hp")).setText(userList.get(row)[1].toString());
        ((JTextField) data.get("username")).setText(userList.get(row)[3].toString());
        ((JTextField) data.get("password")).setText(userList.get(row)[4].toString());
        ((JTextField) data.get("alamat")).setText(userList.get(row)[5].toString());
        ((JComboBox) data.get("role")).setSelectedItem(userList.get(row)[6].toString());
        status = 2;

        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
