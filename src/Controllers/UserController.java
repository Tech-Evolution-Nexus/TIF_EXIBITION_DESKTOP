/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Config.DB;
import Core.Controller;
import Helper.Notification;
import View.UserView;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import App.Model.UserModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class UserController  extends Controller {

    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
 
    private ArrayList<Object[]> userList = new ArrayList<>();
    private UserView view = new UserView();
    private UserModel model = new UserModel();
    public UserController() {
        view.getSearch().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
               tampilData(true);
            }
        });
        tampilData(false);
        view.getBtnUbah().addActionListener(e->editData());
        view.getBtnHapus().addActionListener(e->hapusData());
        view.getBtnTambah().addActionListener(e->tambahData());
        view.getBtnSimpan().addActionListener(e -> simpanData());
        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
        public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            tampilData(false);        }
        public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
        }
        public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
        }
    });
    }

    public void tampilData(boolean cari) {
        try {
            String kunci = view.getSearch().getText();
            // mengambil data dari table kategori       
            ResultSet data = model.orderBy("id", "desc").get();
            if (cari) {
                data = model.where("nama", "like", "%" + kunci + "%").orWhere("alamat","like", "%" + kunci + "%").orderBy("id", "desc").get();
            }
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) view.getTable().getModel();
            tables.setRowCount(0);
            userList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("no_hp"), data.getString("nama"), data.getString("alamat"), data.getString("username"), data.getString("role")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                userList.add(new Object[]{data.getInt("id"), data.getString("no_hp"), data.getString("nama"), data.getString("username"), data.getString("password"), data.getString("alamat"), data.getString("role")});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }
    }

    
    public void tambahData() {
      showForm();
    }

    
    public void hapusData() {
        try {
            int row = view.getTable().getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(view.getTable(), "Tidak ada baris yang dipilih");
                return;

            }
            int confirm = JOptionPane.showConfirmDialog(view.getTable(), "Yakin menghapus data?");
            if (confirm != 0)return;

            int id = (int) userList.get(row)[0];
            ResultSet dataUserTrx = DB.query("SELECT count(*) as count from transaksi_penjualan where id_user='" + id + "'");
            dataUserTrx.next();
            if (dataUserTrx.getInt("count") > 0) {
                JOptionPane.showMessageDialog(view.getTable(), "Data user gagal dihapus, Data sedang digunakan");
                return;
            }
            model.delete("id='" + id + "'");
            JOptionPane.showMessageDialog(view.getTable(), "Data Berhasil Di Hapus");

            tampilData(false);

        } catch (SQLException e) {
             if ( e.getErrorCode() == 1451) {
                 Notification.showError(Notification.DATA_IN_USE_ERROR , view.getTable());
                 return;
             }
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), view.getTable());
        }
    }

    

    
    public void simpanData() {
       

        String notlp = view.getNo_hp().getText();
        String nama = view.getNamaUser().getText();
        String username = view.getUsername().getText();
        String password = view.getPassword().getText();
        String alamat = view.getAlamat().getText();
        String role = view.getRole().getSelectedItem().toString();
        try {
           ResultSet checkNoTlp =  DB.query("SELECT * FROM users where no_hp = '"+notlp+"' AND id <> '"+idEdit+"'");
           ResultSet checkNoUsername =  DB.query("SELECT * FROM users where username = '"+username+"' AND id <> '"+idEdit+"'");
             if (checkNoTlp.next()) {
                JOptionPane.showMessageDialog(view.getForm(), "Nomor Telepon Sudah Ada");
            }else if (checkNoUsername.next()) {
                JOptionPane.showMessageDialog(view.getForm(), "Username Sudah Ada");
            }else if (notlp.equals("") || nama.equals("") || username.equals("") || password.equals("") || alamat.equals("") || role.equals("")) {
                JOptionPane.showMessageDialog(view.getForm(), "Field Tidak Boleh Kosong");

            }else if (!notlp.matches("(^\\+62|0)(\\d{8,15})$")) {
                JOptionPane.showMessageDialog(view.getForm(), "Nomor telepon tidak valid ");
            }  else {
                
                if (status == 1) {
                    DB.query2("INSERT INTO users (no_hp,nama,alamat,username,password,role)Values ('" + notlp + "','" + nama + "','" + alamat + "','" + username + "','" + password + "','" + role + "')");
                } else {
                    DB.query2("UPDATE users SET no_hp = '" + notlp + "', nama = '" + nama + "', alamat = '" + alamat + "', username = '" + username + "', password = '" + password + "', role = '" + role + "' WHERE id = '" + idEdit + "'");
                    status = 1;
                    idEdit = -1;
                }
                JOptionPane.showMessageDialog(view.getForm(), "Data Berhasil Di Simpan");

               view.getForm().dispose();
               view.getNo_hp().setText("");
               view.getNamaUser().setText("");
               view.getUsername().setText("");
               view.getPassword().setText("");
               view.getAlamat().setText("");
                tampilData(false);
               view.getRole().setSelectedIndex(0);
            }

        } catch (Exception e) {
            System.out.println("error simpan data " + e.getMessage());
        }
    }

    public void editData() {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view.getTable(), "Tidak ada baris yang dipilih");
            return;
        }
        idEdit = (int) userList.get(row)[0];
        view.getNo_hp().setText(userList.get(row)[1].toString());
        view.getNamaUser().setText(userList.get(row)[2].toString());
        view.getUsername().setText(userList.get(row)[3].toString());
        view.getPassword().setText(userList.get(row)[4].toString());
        view.getAlamat().setText(userList.get(row)[5].toString());
        view.getRole().setSelectedItem(userList.get(row)[6].toString());
        status = 2;

      showForm();
    }



    
    private void showForm(){
        view.getForm().pack();
        view.getForm().setLocationRelativeTo(null);
        view.getForm().setVisible(true);
    }

    @Override
    public Component getView() {
        return view;
    }

}
