 package App.Controllers;

import Config.DB;
import Helper.Notification;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class KategoriController {

    private ArrayList<Integer> id = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;

    private ArrayList<Object[]> kategoriList = new ArrayList<>();
//s

    public void tampilData(JTable table) {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM kategori ORDER BY id DESC");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.setRowCount(0);
            id.clear();
            kategoriList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("nama_kategori")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                id.add(data.getInt("id"));
                kategoriList.add(new Object[]{data.getInt("id"), data.getString("nama_kategori")});
                no++;
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), table);
        }
    }

    public void showForm(JDialog form) {
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    public void hapusData(int index, JTable table) {
        try {
          
            int row = (int) index;
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return;
            }
              if (!Notification.showConfirmDelete(table)) {
                return;
            }
            int id = (int) kategoriList.get(row)[0];
            ResultSet dataObat = DB.query("SELECT COUNT(*) AS count FROM obat WHERE id_kategori = '" + id + "'");
            dataObat.next();
            
            DB.query2("DELETE FROM kategori WHERE id='" + id + "'");
            Notification.showSuccess(Notification.DATA_DELETED_SUCCESS, table);
        } catch (SQLException e) {     
            int errorCode = e.getErrorCode();
            if (errorCode == 1451) {
                Notification.showError(Notification.DATA_IN_USE_ERROR , table);
                return;
            }
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), table);
        }
    }

    public void cariData(String kunci, JTable table) {
        try {
            ResultSet data = DB.query("SELECT * FROM kategori WHERE nama_kategori LIKE '%" + kunci + "%' ORDER BY id DESC");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
            id.clear();
            kategoriList.clear();
            while (data.next()) {
                //  menyimpan data dalam bentuk array
                Object[] dataTable = {no, data.getString("nama_kategori")};
                //  memasukkan data kepada tabel
                tables.addRow(dataTable);
                id.add(data.getInt("id"));
                kategoriList.add(new Object[]{data.getInt("id"), data.getString("nama_kategori")});
                no++;
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), table);
        }
    }

    public void simpanData(Object input, JDialog form) {
        JTextField namaKategoriField = (JTextField) input;
        String namaKategori = namaKategoriField.getText();
        try {
            boolean dataExist = kategoriList.stream().anyMatch(satuan -> satuan[1].toString().trim().equalsIgnoreCase(namaKategori.trim()) && ((int) satuan[0]) != idEdit);
            if (dataExist) {
                Notification.showError("Nama kategori sudah ada", form);
            } else if (namaKategori.equals("")) {
                Notification.showError(Notification.EMPTY_VALUE, form);
            } else {
                DB.query("CALL simpanKategori('"+namaKategori+"',"+idEdit+")");
                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, form);
                idEdit = 0;
                form.dispose();
                namaKategoriField.setText("");
            }

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(), form);
        }
    }

    public void editData(int row) {
       
        status = 2;
        idEdit = (int) kategoriList.get(row)[0];
    }
}
