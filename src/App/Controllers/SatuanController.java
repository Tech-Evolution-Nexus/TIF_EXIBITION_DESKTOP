
package App.Controllers;

import Config.DB;
import Helper.DataFormat;
import Helper.Notification;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class SatuanController  {

    private ArrayList<Object[]> satuanList = new ArrayList<>();
    private int idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;


   
    public void tampilData(JTable table) {
        try {
            ResultSet dataSatuan = DB.query("SELECT * FROM `bentuk_sediaan_obat` ORDER BY `bentuk_sediaan_obat`.`id` DESC");
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            int no = 1;
            tables.setRowCount(0);
            satuanList.clear();
            while (dataSatuan.next()) {
                Object[] rowData = {no, dataSatuan.getString("nama_bentuk_sediaan"), dataSatuan.getString("deskripsi")};
                tables.addRow(rowData);
                satuanList.add(new Object[]{dataSatuan.getInt("id"), dataSatuan.getString("nama_bentuk_sediaan")});
                no++;
            }
        } catch (Exception e) {
            System.out.println("error dari tampil data satuan" + e.getMessage());
        }
    }

   


   
    public int simpanData(DataFormat data ,JDialog form,JTable table) {
        String namaSatuan = data.getString("nama_satuan");
        String keterangan = data.getString("keterangan");
        try {
            if (satuanList.stream().anyMatch(satuan -> satuan[1].toString().trim().equalsIgnoreCase(namaSatuan.trim()) && ((int) satuan[0]) != idEdit)) {
                JOptionPane.showMessageDialog(form, "Nama Satuan Sudah Ada");
            } else if (namaSatuan.equals("")) {
                JOptionPane.showMessageDialog(form, "Nama Kategori Tidak Boleh Kosong");

            } else {
                if (keterangan.equals("")) {
                    keterangan = "-";
                }
                if (status == 1) {
                    DB.query2("INSERT INTO bentuk_sediaan_obat (nama_bentuk_sediaan,deskripsi)VALUES ('" + namaSatuan + "','" + keterangan + "')");
                } else {

                    DB.query2("UPDATE bentuk_sediaan_obat SET nama_bentuk_sediaan = '" + namaSatuan + "' , deskripsi='" + keterangan + "' WHERE id ='" + idEdit + "'");
                    status = 1;
                    idEdit = -1;
                }

                JOptionPane.showMessageDialog(form, "Data Berhasil Di Simpan");

                tampilData(table);
                form.dispose();
              return 1;

            }
        } catch (Exception e) {
            System.out.println("error simpan data " + e.getMessage());
        }
        return 0;

    }

   
    public void editData(int index) {
        int row = (int) index;
       
        status = 2;
        idEdit = (int) satuanList.get(row)[0];
 
    }

   
 

   
    public void hapusData(int index,JTable table) {
        try {
            int row = (int) index;
            int confirm = JOptionPane.showConfirmDialog(table, "Yakin menghapus data?");
            if (confirm != 0) {
                return;
            }
            if (row < 0) {
                JOptionPane.showMessageDialog(table, "Tidak ada baris yang dipilih");
                return;

            }
            int id = (int) satuanList.get(row)[0];
            ResultSet data = DB.query("SELECT count(*)as count from obat where id_bentuk_sediaan = '"+id+"'" );
            data.next();
            if(data.getInt("count") > 0){
                Notification.showInfo(Notification.DATA_IN_USE_ERROR, table);
                return;
            }
            DB.query2("delete from bentuk_sediaan_obat where id = '"+id+"'");
            tampilData(table);
            JOptionPane.showMessageDialog(table, "Data Berhasil Di Hapus");

        } catch (Exception e) {
            System.out.println("error dari hapus data satuan " + e.getMessage());
        }
    }

    public void cariData(String kunci,JTable table) {
        try {
            ResultSet dataSatuan = DB.query("SELECT * FROM `bentuk_sediaan_obat` WHERE nama_bentuk_sediaan like '%" + kunci + "%' ORDER BY `bentuk_sediaan_obat`.`id` DESC");
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            int no = 1;
            tables.setRowCount(0);
            satuanList.clear();
            while (dataSatuan.next()) {
                Object[] rowData = {no, dataSatuan.getString("nama_bentuk_sediaan"), dataSatuan.getString("deskripsi")};
                tables.addRow(rowData);
                satuanList.add(new Object[]{dataSatuan.getInt("id"), dataSatuan.getString("nama_bentuk_sediaan")});
                no++;
            }
        } catch (Exception e) {
            System.out.println("error dari tampil data satuan" + e.getMessage());
        }
    }

}
