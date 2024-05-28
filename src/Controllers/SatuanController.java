
package Controllers;

import Config.DB;
import Core.Controller;
import Helper.Notification;
import View.SatuanView;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import App.Model.SatuanModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class SatuanController  extends Controller{

    private ArrayList<Object[]> satuanList = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private SatuanView view = new SatuanView();
    private SatuanModel model = new SatuanModel();

    public SatuanController() {
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
                data = model.where("nama_satuan","like","%"+kunci+"%").orderBy("id", "desc").get();
            }
            DefaultTableModel tables = (DefaultTableModel) view.getTable().getModel();
            int no = 1;
            tables.setRowCount(0);
            satuanList.clear();
            while (data.next()) {
                Object[] rowData = {no, data.getString("nama_satuan"), data.getString("deskripsi")};
                tables.addRow(rowData);
                satuanList.add(new Object[]{data.getInt("id"), data.getString("nama_satuan")});
                no++;
            }
        } catch (Exception e) {
            System.out.println("error dari tampil data satuan" + e.getMessage());
        }
    }
   
    public void tambahData() {
        view.getNamaSatuan().setText("");
        view.getKeterangan().setText("");
        view.getTitleForm().setText("Tambah satuan");
        showForm();

    }

    public void simpanData() {
        String namaSatuan = view.getNamaSatuan().getText();
        String keterangan = view.getKeterangan().getText();
        try {
              ResultSet cekNamaSatuan = model.where("nama_satuan", "=", namaSatuan).andWhere("id", "<>", idEdit).get();
            if (cekNamaSatuan.next()) {
                Notification.showError( "Nama Satuan Sudah Ada",view.getForm());
            } else if (namaSatuan.equals("")) {
                Notification.showError( "Nama Kategori Tidak Boleh Kosong",view.getForm());
            } else {
                if (keterangan.equals("")) {
                    keterangan = "-";
                }
                 String[] fields= {"nama_satuan","deskripsi"};
                String[] values= {namaSatuan,keterangan};
                if (idEdit == 0) {
                    model.insert(fields, values);
                    Notification.showSuccess( "Satuan Berhasil Ditambah",view.getForm());
                } else {
                    model.update(fields, values, "id ='" + idEdit + "'");
                    idEdit = 0;
                    Notification.showSuccess( "Satuan Berhasil Diubah",view.getForm());
                }
                tampilData(false);
                view.getForm().dispose();
                view.getNamaSatuan().setText("");
                view.getKeterangan().setText("");
            }

        } catch (Exception e) {
            System.out.println("error simpan data " + e.getMessage());
        }
    }

   
    public void editData() {
        int row = view.getTable().getSelectedRow();
         if (view.getTable().getSelectedRow() < 0) {
                Notification.showError(Notification.NO_DATA_SELECTED_INFO, view.getForm());
                return;
            }
        view.getTitleForm().setText("Ubah satuan");
        String namaFromTable = view.getTable().getValueAt(row, 1).toString();
        String keteranganFromTable = view.getTable().getValueAt(row, 2).toString();
        //set value ke text field
        view.getNamaSatuan().setText(namaFromTable);
        view.getKeterangan().setText(keteranganFromTable);
        idEdit = (int) satuanList.get(row)[0];
        showForm();
    }
   
    public void hapusData() {
        try {
            int row = view.getTable().getSelectedRow();
             if (view.getTable().getSelectedRow() < 0) {
                Notification.showError(Notification.NO_DATA_SELECTED_INFO, view.getForm());
                return;
            }
            boolean confirm = Notification.showConfirmDelete(view.getTable());
            if (confirm) {
                return;
            }
            int id = (int) satuanList.get(row)[0];
            ResultSet data = DB.query("SELECT count(*)as count from obat where id_satuan = '"+id+"'" );
            data.next();
            if (data.getInt("count") > 0) {
                Notification.showInfo(Notification.DATA_IN_USE_ERROR, view.getTable());
                return;
            }
            model.delete(" id = '" + id + "' ");
            tampilData(false);
            JOptionPane.showMessageDialog(view.getTable(), "Data Berhasil Di Hapus");

        } catch (Exception e) {
            System.out.println("error dari hapus data satuan " + e.getMessage());
        }
    }

    

    @Override
    public Component getView() {
        return view;
    }

    private void showForm() {
         view.getForm().pack();
        view.getForm().setLocationRelativeTo(null);
        view.getForm().setVisible(true);
    }

}
