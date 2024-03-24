/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import App.Model.SuplierModel;
import Config.DB;
import Core.Controller;
import Helper.KodeGenerator;
import Helper.Notification;
import View.SuplierView;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class SupplierController  extends Controller{

    private String idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private ArrayList<Object[]> suplierList = new ArrayList<>();
    private SuplierView view = new SuplierView();
    private SuplierModel model = new SuplierModel();
    public SupplierController() {
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
    }

   
    private void tampilData(boolean cari) {
        try {
            String kunci = view.getSearch().getText();
            // mengambil data dari table kategori       
            ResultSet data = model.orderBy("nama_suplier", "asc").get();
            if (cari) {
                data = model.where("nama_suplier","like","%"+kunci+"%").orderBy("nama_suplier", "asc").get();
            }
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) view.getTable().getModel();
            tables.setRowCount(0);
            suplierList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("kode_suplier"), data.getString("nama_suplier"), data.getString("alamat"), data.getString("nomor_telepon")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                suplierList.add(new Object[]{data.getString("kode_suplier"), data.getString("nama_suplier"), data.getString("alamat"), data.getString("nomor_telepon")});
                no++;
            }

        } catch (Exception e) {
            System.out.println("error " + e.getMessage());
        }
    }

   
    private void tambahData() {
        view.getTitleForm().setText("Tambah Suplier");
        view.getNamaPemasok().setText("");
        view.getAlamat().setText("");
        view.getNo_telepon().setText("");
        showForm();
        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
        public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
            tampilData(false);        }
        public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
        }
        public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
        }
    });
    }

  

   
    private void simpanData() {
        String namaSuplier = view.getNamaPemasok().getText();
        String alamat = view.getAlamat().getText();
        String notlp = view.getNo_telepon().getText();
        String codeTRX = KodeGenerator.generateKodeSuplier();

        
        try {
            ResultSet checkNoTlp = model.where("nomor_telepon", "=", notlp).andWhere("kode_suplier", "<>", idEdit).get();
           ResultSet cekNamaSuplier = model.where("nama_suplier", "=", namaSuplier).andWhere("kode_suplier", "<>", idEdit).get();
            if (cekNamaSuplier.next()) {
                Notification.showError("Nama suplier sudah ada", view.getForm());
            }else if (checkNoTlp.next()) {
                Notification.showError("No telepon sudah ada", view.getForm());
            }  else if (namaSuplier.equals("") || alamat.equals("") || notlp.equals("")) {
                Notification.showError(Notification.EMPTY_VALUE, view.getForm());

            } else {
                String kodeSp = codeTRX;
                String[] fields= {"kode_suplier","nama_suplier","alamat","nomor_telepon"};
                String[] values= {codeTRX,namaSuplier,alamat,notlp};
                if (status == 1) {
                    model.insert( fields, values);
                } else {
                    model.update(fields, values, " kode_suplier = '" + idEdit + "'");
                    status = 1;
                    kodeSp = idEdit;
                    idEdit = "";
                }
              
                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, view.getForm());
                tampilData(false);
                view.getForm().dispose();
                view.getTable().clearSelection();
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR, view.getForm());
            System.out.println("error simpan data " + e.getMessage());
        }
    }

    private void editData() {
        try {
           
            view.getTitleForm().setText("Ubah  Suplier ");

            int row = view.getTable().getSelectedRow();
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, view.getTable());
                return;
            }
            String kodeSp = view.getTable().getValueAt(row, 1).toString();
            String nama = view.getTable().getValueAt(row, 2).toString();
            String alamat = view.getTable().getValueAt(row, 3).toString();
            String notlp = view.getTable().getValueAt(row, 4).toString();
            view.getNamaPemasok().setText(nama);
            view.getAlamat().setText(alamat);
            view.getNo_telepon().setText(notlp);
            status = 2;
            idEdit = kodeSp;
            showForm();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Notification.showError(Notification.SERVER_ERROR, view.getTable());
        }
    }

   
    
    private void hapusData() {
        try {
            boolean confirm = Notification.showConfirmDelete(view.getTable());
            if (!confirm) {
                return;
            }
            int row = view.getTable().getSelectedRow();
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, view.getTable());
                return;

            }
            String id = suplierList.get(row)[0].toString();
            ResultSet transaksiData = DB.query("SELECT count(*) as count from transaksi_pembelian where kode_suplier = '" + id + "'");
            transaksiData.next();
            if (transaksiData.getInt("count") > 0) {
                Notification.showInfo(Notification.DATA_IN_USE_ERROR, view.getTable());
                return;
            }
            model.delete(" kode_suplier = '" + id + "'");
            // DB.query2("DELETE FROM supplier where kode_suplier='" + id + "'");
            tampilData(false);
            Notification.showInfo(Notification.DATA_DELETED_SUCCESS, view.getTable());

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                Notification.showError(Notification.DATA_IN_USE_ERROR, view.getTable());
                return;
            }
             System.out.println(e.getMessage());
            Notification.showError(Notification.SERVER_ERROR + " " + e.getMessage(), view.getTable());
        }
    }

    private void showForm() {
      view.getForm().pack();
        view.getForm().setLocationRelativeTo(null);
        view.getForm().setVisible(true);
    }  
   
  

    @Override
    public Component getView() {
        return view;
    }
}
