/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Components.ButtonIcon;
import Config.DB;
import Helper.KodeGenerator;
import Helper.Notification;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class SupplierController  {

    private String idEdit;
    //status 1 untuk tambah 2 untuk edit
    private int status = 1;
    private JTable table;
    private JDialog form;
    private JPanel barangListCom;
    private JTextField namaPemasok;
    private JTextField alamat;
    private JTextField notlp;
    private JLabel titleForm;
    private ArrayList<Object[]> suplierList = new ArrayList<>();
    private Set<JComboBox> barangList = new HashSet<>();

    public SupplierController(JTable table, JDialog form, JPanel barangListCom, JTextField namaPemasok, JTextField alamat, JTextField notlp, JLabel titleForm) {
        this.table = table;
        this.barangListCom = barangListCom;
        this.form = form;
        this.namaPemasok = namaPemasok;
        this.alamat = alamat;
        this.notlp = notlp;
        this.titleForm = titleForm;
    }

   
    public void tampilData() {
        try {
            // mengambil data dari table kategori       
            ResultSet data = DB.query("SELECT * FROM supplier order by kode_suplier desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];
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

   
    public void tambahData(Object[] object) {
        namaPemasok.setEnabled(true);
        alamat.setEnabled(true);
        notlp.setEnabled(true);
        barangList.clear();
        titleForm.setText("Tambah Suplier Baru");
        addBarangPasok(null);
        form.pack();
        form.setLocationRelativeTo(null);
        form.setVisible(true);
    }

   
    public void hapusData(Object[] object) {
        try {
            int confirm = JOptionPane.showConfirmDialog(table, "Yakin menghapus data?");
            if (confirm != 0) {
                return;
            }
            int row = (int) object[0];
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return;

            }
            String id = suplierList.get(row)[0].toString();
            ResultSet transaksiData = DB.query("SELECT count(*) as count from transaksi_pembelian where kode_suplier = '" + id + "'");
            transaksiData.next();
            if (transaksiData.getInt("count") > 0) {
                Notification.showInfo(Notification.DATA_IN_USE_ERROR, table);
                return;
            }
            DB.query2("DELETE FROM supplier where kode_suplier='" + id + "'");
            tampilData();
            Notification.showInfo(Notification.DATA_DELETED_SUCCESS, table);

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + " " + e.getMessage(), table);
        }
    }

    public void cariData(String kunci) {
        try {
            ResultSet data = DB.query("SELECT * FROM supplier where nama_suplier like '%" + kunci + "%' OR alamat like '%" + kunci + "' order by kode_suplier desc");
            int no = 1;
            // menggunakan DefaultTableModel supaya bisa menambahkan data
            DefaultTableModel tables = (DefaultTableModel) table.getModel();
            tables.fireTableDataChanged();
            tables.setRowCount(0);
            int[] arrayId = new int[10];

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
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

   
    public void simpanData(Object[] object) {
        String namaSuplier = namaPemasok.getText();
        String alamat = this.alamat.getText();
        String notlp = this.notlp.getText();

        String codeTRX = KodeGenerator.generateKodeSuplier();

        Set<String> uniqueBarangList = new HashSet<>();
        for (JComboBox combo : barangList) {
            String value = combo.getSelectedItem().toString();
            if (!uniqueBarangList.add(value)) {
                Notification.showInfo("Nilai duplikat dalam barang pasok: " + value, form);
                return;
            }
            int index = combo.getSelectedIndex();
            if (index == 0) {
                Notification.showInfo(Notification.EMPTY_VALUE, form);
                return;
            }

        }
        try {
            if (suplierList.stream().anyMatch(satuan -> satuan[1].toString().trim().equalsIgnoreCase(namaSuplier.trim()) && !satuan[0].equals(idEdit))) {
                Notification.showInfo(Notification.DUPLICATE_DATA, form);
            } else if (namaSuplier.equals("") || alamat.equals("") || notlp.equals("")) {
                Notification.showInfo(Notification.EMPTY_VALUE, form);

            } else {
                String kodeSp = codeTRX;
                System.out.println("kesimpan");
                if (status == 1) {
                    DB.query2("INSERT INTO supplier (kode_suplier,nama_suplier,alamat,nomor_telepon)Values ('" + codeTRX + "','" + namaSuplier + "','" + alamat + "','" + notlp + "')");
                } else {
                    DB.query2("UPDATE supplier SET nama_suplier = '" + namaSuplier + "', alamat = '" + alamat + "', nomor_telepon = '" + notlp + "' WHERE kode_suplier = '" + idEdit + "'");
                    status = 1;
                    kodeSp = idEdit;
                    idEdit = "";
                }
                DB.query2("DELETE FROM penjualan where  kode_suplier = '" + kodeSp + "'");
                for (JComboBox combo : barangList) {
                    String value = combo.getSelectedItem().toString();

                    ResultSet kodeObat = DB.query("SELECT kode_obat from data_obat where nama_obat = '" + value + "'");
                    kodeObat.next();
                    DB.query2("INSERT INTO penjualan (`kode_obat`, `kode_suplier`) values('" + kodeObat.getString("kode_obat") + "','" + kodeSp + "')");

                }
                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, form);
                tampilData();
                form.dispose();
                table.clearSelection();
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR, form);
            System.out.println("error simpan data " + e.getMessage());
        }
    }

    public void editData(Object[] rowTable) {
        try {
            namaPemasok.setEnabled(true);
            alamat.setEnabled(true);
            notlp.setEnabled(true);
            titleForm.setText("Edit Data Suplier ");

            int row = table.getSelectedRow();
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return;
            }
            String kodeSp = table.getValueAt(row, 1).toString();
            String nama = table.getValueAt(row, 2).toString();
            String alamat = table.getValueAt(row, 3).toString();
            String notlp = table.getValueAt(row, 4).toString();
            namaPemasok.setText(nama);
            this.alamat.setText(alamat);
            this.notlp.setText(notlp);
            status = 2;
            idEdit = kodeSp;
            barangList.clear();
            ResultSet dataOb = DB.query("select nama_obat,penjualan.kode_obat from penjualan join obat on penjualan.kode_obat = obat.kode_obat  where kode_suplier = '" + kodeSp + "'");
            while (dataOb.next()) {
                addBarangPasok(dataOb.getString("kode_obat"));
            }
            form.pack();
            form.setLocationRelativeTo(null);
            form.setVisible(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Notification.showError(Notification.SERVER_ERROR, table);
        }
    }

    public void detail(Object[] rowTable) {
        try {
            namaPemasok.setEnabled(false);
            alamat.setEnabled(false);
            notlp.setEnabled(false);

            titleForm.setText("Detail Data Pemasok");
            int row = table.getSelectedRow();
            if (row < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return;
            }
            String kodeSp = table.getValueAt(row, 1).toString();
            String nama = table.getValueAt(row, 2).toString();
            String alamat = table.getValueAt(row, 3).toString();
            String notlp = table.getValueAt(row, 4).toString();
            namaPemasok.setText(nama);
            this.alamat.setText(alamat);
            this.notlp.setText(notlp);
            status = 2;
            idEdit = kodeSp;
            barangList.clear();
            ResultSet dataOb = DB.query("select nama_obat,penjualan.kode_obat from penjualan join obat on penjualan.kode_obat = obat.kode_obat  where kode_suplier = '" + kodeSp + "'");
            while (dataOb.next()) {
                addBarangPasok(dataOb.getString("kode_obat"));
            }
            for (JComboBox combo : barangList) {
                combo.setEnabled(false);
            }
            form.pack();
            form.setLocationRelativeTo(null);
            form.setVisible(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Notification.showError(Notification.SERVER_ERROR, table);
        }
    }

   
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void addBarangPasok(String idSelect) {
        ResultSet dataObat = getObat();

        int w = barangListCom.getBounds().width;
        JPanel panel = new JPanel();
        JComboBox combo = new JComboBox();
        ButtonIcon button = new ButtonIcon();
        button.setHorizontal(true);
        button.setIcon("Assets/svg/deleteIcon.svg");
        panel.add(combo);
        panel.add(button);

        panel.setBackground(Color.white);
        button.setBackground(new Color(215, 9, 83));
        panel.setMaximumSize(new Dimension(w, 50));
        combo.setPreferredSize(new Dimension(512, 42));
        button.setPreferredSize(new Dimension(42, 42));

        try {
            combo.addItem("Pilih Obat");
            while (dataObat.next()) {
                combo.addItem(dataObat.getString("nama_obat"));
                if (idSelect != null && idSelect.equals(dataObat.getString("kode_obat").toString())) {
                    combo.setSelectedItem(dataObat.getString("nama_obat"));
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        barangListCom.add(panel);
        barangList.add(combo);
        barangListCom.repaint();
        barangListCom.revalidate();
        button.addMouseListener(new MouseAdapter() {
           
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (barangList.size() == 1) {
                    return;
                }
                deleteBarangList(panel, combo);
            }

        });
    }

    public ResultSet getObat() {
        String sql = "SELECT * FROM data_obat ";

        return DB.query(sql);
    }

    public void deleteBarangList(JPanel panel, JComboBox combo) {
        barangList.remove(combo);
        barangListCom.remove(panel);
        barangListCom.repaint();
        barangListCom.revalidate();

        System.out.println(barangList.size());
        System.out.println("dihapus");
    }
}
