 package Controllers;

import Config.DB;
import Core.Controller;
import Helper.Notification;
import View.KategoriView;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import App.Model.KategoriModel;

public class KategoriController  extends Controller{

    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private KategoriView view= new KategoriView();
    private KategoriModel model= new KategoriModel();

    private ArrayList<Object[]> kategoriList = new ArrayList<>();

    
    public KategoriController() {
        view.getSearchObat().addKeyListener(new KeyAdapter() {
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
            String kunci = view.getSearchObat().getText();
            // mengambil data dari table kategori       ''
            ResultSet data = model.orderBy("id", "desc").get();
            if (cari) {
                data = model.where("nama_kategori", "like", "%" + kunci + "%").orderBy("id", "desc").get(); 
            }
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) view.getTable().getModel();
            tables.setRowCount(0);
            kategoriList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("nama_kategori")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                kategoriList.add(new Object[]{data.getInt("id"), data.getString("nama_kategori")});
                no++;
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), view.getTable());
        }
    }

    public void showForm() {
        view.getForm().pack();
        view.getForm().setLocationRelativeTo(null);
        view.getForm().setVisible(true);
    }

    public void hapusData() {
        try {
            int row =  view.getTable().getSelectedRow();
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, view.getTable());
                return;
            }
            if (!Notification.showConfirmDelete(view.getTable()))return;
            int id = (int) kategoriList.get(row)[0];
            ResultSet data = DB.query("SELECT * from kategori");
            data.next();
            model.delete(" id = '" + id + "'");
            Notification.showSuccess(Notification.DATA_DELETED_SUCCESS, view.getTable());
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
        String namaKategori =view.getNamaKategori().getText();
        try {
            ResultSet namaExist = model.where("nama_kategori","=",namaKategori).andWhere("id", "<>",idEdit).get();
            if (namaExist.next()) {
                Notification.showError("Nama kategori sudah ada", view.getForm());
            } else if (namaKategori.equals("")) {
                Notification.showError(Notification.EMPTY_VALUE, view.getForm());
            } else {
                 String[] fields= {"nama_kategori"};
                String[] values= {namaKategori};

                if(idEdit==0)model.insert(fields, values);
                else model.update(fields, values, " id = '" + idEdit + "'");
                
                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, view.getForm());
                idEdit = 0;
                view.getForm().dispose();
                view.getNamaKategori().setText("");
                tampilData(false);
            }

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), view.getForm());
        }
    }

    public void editData() {
        status = 2;
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            Notification.showError(Notification.NO_DATA_SELECTED_INFO, view.getForm());
            return;
        }
        view.getFormTitle().setText("Ubah Kategori");
        idEdit = (int) kategoriList.get(row)[0];
        String name = view.getTable().getValueAt(row, 1).toString();
        view.getNamaKategori().setText(name);
        showForm();

    }
    public void tambahData() {
        status = 1;
        view.getFormTitle().setText("Tambah Kategori");
        view.getNamaKategori().setText("");
        showForm();

    }

    @Override
    public Component getView() {
        return view;
    }
}
