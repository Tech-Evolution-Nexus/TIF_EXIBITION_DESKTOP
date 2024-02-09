package Controllers;

import Config.DB;
import Helper.Notification;
import java.sql.ResultSet;
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
    private JTable table;
    private JDialog form;
    private ArrayList<Object[]> kategoriList = new ArrayList<>();

    public KategoriController(JTable table, JDialog form) {
        this.table = table;
        this.form = form;
    }

    public void tampilData() {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM kategori ORDER BY id DESC");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
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
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(),table);
        }
    }

    public void tambahData(Object[] object) {
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    public void hapusData(Object[] object) {
        try {
            if (!Notification.showConfirmDelete(table)) {
                return;
            }
            int row = (int) object[0];
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO,table);
                return;
            }
            int id = (int) kategoriList.get(row)[0];
            ResultSet dataObat = DB.query("SELECT COUNT(*) AS count FROM obat WHERE id_kategori = '" + id + "'");
            dataObat.next();
            if (dataObat.getInt("count") > 0) {
                Notification.showError(Notification.DATA_IN_USE_ERROR,table);
                return;
            }
            DB.query2("DELETE FROM kategori WHERE id='" + id + "'");
            Notification.showSuccess(Notification.DATA_DELETED_SUCCESS,table);
//            tampilData();

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(),table);
        }
    }

    public void cariData(String kunci) {
        try {
            ResultSet data = DB.query("SELECT * FROM kategori WHERE nama_kategori LIKE '%" + kunci + "%' ORDER BY id DESC");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
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
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(),table);
        }
    }

    public void simpanData(Object[] object) {
        JTextField namaKategoriField = (JTextField) object[0];
        String namaKategori = namaKategoriField.getText();
        try {
            if (kategoriList.stream().anyMatch(satuan -> satuan[1].toString().trim().equalsIgnoreCase(namaKategori.trim()) && ((int) satuan[0]) != idEdit)) {
                Notification.showError(Notification.DUPLICATE_DATA,form);
            } else if (namaKategori.equals("")) {
                Notification.showError(Notification.EMPTY_VALUE,form);
            } else {
                if (status == 1) {
                    DB.query2("INSERT INTO kategori (nama_kategori) VALUES ('" + namaKategori + "')");
                } else {
                    DB.query2("UPDATE kategori SET nama_kategori = '" + namaKategori + "' WHERE id ='" + idEdit + "'");
                    status = 1;
                    idEdit = -1;
                }

                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS,form);
                tampilData();
                form.dispose();
                namaKategoriField.setText("");
            }

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + e.getMessage(),form);
        }
    }

    public void editData(Object[] rowTable) {
        int row = (int) rowTable[0];
        if (row < 0) {
            Notification.showInfo(Notification.NO_DATA_SELECTED_INFO,form);
            return;
        }
        JTextField namaKategori = (JTextField) rowTable[1];
        String namaFromTable = table.getValueAt(row, 1).toString();
        namaKategori.setText(namaFromTable);
        status = 2;
        idEdit = (int) kategoriList.get(row)[0];
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
