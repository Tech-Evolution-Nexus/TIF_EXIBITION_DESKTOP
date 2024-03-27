/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Components.ButtonIcon;
import Components.CustomButton;
import Config.DB;
import Helper.Currency;
import Helper.FormatTanggal;
import Helper.KodeGenerator;
import Helper.Notification;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class ObatController  {

    private ArrayList<Integer> idKategori = new ArrayList<Integer>();
    private ArrayList<Object[]> obatList = new ArrayList<Object[]>();
    private String idEdit = "";
    int status = 1;
    private JTable table;
    private JTable tableSatuan;
    private JTable tableStok;
    private JDialog form;
    private JDialog detail;
    private JTable listParent;
    private JTextField namaObat;
    private JTextField aturanPakai;
    private JComboBox kategori;
    private JTextField namaObat1;
    private JTextField aturanPakai1;
    private JTextField kategori1;
    private JPanel satuanListCom;
    private JPanel hargaListCom;
    private JLabel titleForm;
    private ArrayList<JComboBox> satuanList = new ArrayList<JComboBox>();
    private ArrayList<JTextField> satuanTotalList = new ArrayList<JTextField>();
    private ArrayList<JTextField> hargaList = new ArrayList<JTextField>();

    public ObatController(JTable table, JDialog form, Object[] component) {
        this.table = table;
        this.form = form;
        this.namaObat = (JTextField) component[0];
        this.kategori = (JComboBox) component[1];
        this.aturanPakai = (JTextField) component[2];
        this.satuanListCom = (JPanel) component[3];
        this.hargaListCom = (JPanel) component[4];
        this.titleForm = (JLabel) component[5];
        this.detail = (JDialog) component[6];
        this.namaObat1 = (JTextField) component[7];
        this.kategori1 = (JTextField) component[8];
        this.aturanPakai1 = (JTextField) component[9];
        this.tableStok = (JTable) component[10];
        this.tableSatuan = (JTable) component[11];
    }

    
    public void tampilData() {
        try {
            ResultSet data = DB.query("SELECT data_obat.kode_obat,data_obat.nama_obat,jumlah_obat,data_obat.satuan,nama_kategori,kandungan,harga FROM `data_obat`  join data_jenis_penjualan on data_obat.kode_obat =data_jenis_penjualan.kode_obat AND data_obat.satuan =data_jenis_penjualan.satuan  order by tanggal_dibuat desc");
            DefaultTableModel tabelData = (DefaultTableModel) table.getModel();
            tabelData.setRowCount(0);
            int no = 1;
            obatList.clear();
            while (data.next()) {
                Object[] dataArray = {no, data.getString("kode_obat"), data.getString("nama_obat"), data.getString("jumlah_obat"), data.getString("satuan"), data.getString("nama_kategori"), data.getString("kandungan"), Currency.format(data.getLong("harga"))};
                tabelData.addRow(dataArray);
                obatList.add(dataArray);
                no++;

            }
        } catch (Exception e) {
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

    
    public void tambahData(Object[] object) {
        try {
            titleForm.setText("Tambah Data Obat");
            clearDialog();
            ResultSet kategoriData = DB.query("SELECT * from kategori");
            addSatuan(null, 0, 1);
            kategori.removeAllItems();
            while (kategoriData.next()) {
                kategori.addItem(kategoriData.getString("nama_kategori"));
            }
            form.pack();
            form.setLocationRelativeTo(null);
            form.setVisible(true);
        } catch (Exception e) {
        }

    }

    
    public void hapusData(Object[] object) {
        try {

            if (table.getSelectedRow() < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return;
            }
            if (!Notification.showConfirmDelete(table)) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return;

            }
            String id = table.getValueAt(table.getSelectedRow(), 1).toString();
            ResultSet transaksiData = DB.query("SELECT count(*) as count from detail_pembelian where kode_obat= '" + id + "'");
            transaksiData.next();
            if (transaksiData.getInt("count") > 0) {
                Notification.showInfo(Notification.DATA_IN_USE_ERROR, table);
                return;
            }
            DB.query2("DELETE FROM obat where kode_obat='" + id + "'");
            tampilData();
            Notification.showInfo(Notification.DATA_DELETED_SUCCESS, table);

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + " " + e.getMessage(), table);
        }
    }

    public void cariData(String kunci) {
        try {
            String sql = "SELECT data_obat.kode_obat,data_obat.nama_obat,jumlah_obat,data_obat.satuan,nama_kategori,kandungan,harga FROM `data_obat`  join data_jenis_penjualan on data_obat.kode_obat =data_jenis_penjualan.kode_obat AND data_obat.satuan =data_jenis_penjualan.satuan where data_obat.nama_obat like '%" + kunci + "%'  order by tanggal_dibuat desc";
            ResultSet data = DB.query(sql);
            DefaultTableModel tabelData = (DefaultTableModel) table.getModel();
            tabelData.setRowCount(0);
            int no = 1;
            obatList.clear();
            while (data.next()) {
                Object[] dataArray = {no, data.getString("kode_obat"), data.getString("nama_obat"), data.getString("jumlah_obat"), data.getString("satuan"), data.getString("nama_kategori"), data.getString("kandungan"), Currency.format(data.getLong("harga"))};
                tabelData.addRow(dataArray);
                obatList.add(dataArray);
                no++;

            }
        } catch (Exception e) {
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

    
    public void simpanData(Object[] object) {
        String namaObat = this.namaObat.getText();
        String kategori = this.kategori.getSelectedItem().toString();
        String aturanPakai = this.aturanPakai.getText();

        String kodeObat = idEdit.equals("") ? KodeGenerator.generateKodeObat() : idEdit;

        Set<String> uniqueBarangList = new HashSet<>();

        try {
            if (obatList.stream().anyMatch(satuan -> satuan[2].toString().trim().equalsIgnoreCase(namaObat.trim()) && !satuan[1].equals(idEdit))) {
                Notification.showInfo(Notification.DUPLICATE_DATA, form);
            } else if (namaObat.equals("") || aturanPakai.equals("") || kategori.equals("")) {
                Notification.showInfo(Notification.EMPTY_VALUE, form);

            } else {
                for (int i = 0; i < satuanList.size(); i++) {
                    String value = satuanList.get(i).getSelectedItem().toString();
                    String value2 = satuanTotalList.get(i).getText();
                    String harga = hargaList.get(i).getText();
                    if (!uniqueBarangList.add(value)) {
                        Notification.showInfo("Nilai duplikat dalam satuan: " + value, form);
                        return;
                    }
                    if (value2.equals("")) {
                        Notification.showInfo("Total Satuan Harap Diisi: " + value, form);
                        return;
                    }
                    if (!harga.matches("[0-9]+")) {
                        Notification.showInfo("Total Satuan harus berupa angka: ", form);
                        return;
                    }

                }
                ResultSet dataKategori = DB.query("SELECT id from kategori where nama_kategori= '" + kategori + "'");
                ResultSet dataSatuan = DB.query("SELECT id from bentuk_sediaan_obat where nama_bentuk_sediaan= '" + satuanList.get(0).getSelectedItem().toString() + "'");
                dataKategori.next();
                dataSatuan.next();
                DB.query2("DELETE FROM jenis_penjualan where kode_obat = '" + kodeObat + "'");
                DB.query2("CALL simpanDataObat('" + kodeObat + "','" + namaObat + "','" + dataSatuan.getString("id") + "','" + dataKategori.getString("id") + "','" + aturanPakai + "')");
                for (int i = 0; i < satuanList.size(); i++) {
                    String namaSatuan = satuanList.get(i).getSelectedItem().toString();
                    String total = satuanTotalList.get(i).getText();
                    String harga = hargaList.get(i).getText();
                    ResultSet dataS = DB.query("SELECT id from bentuk_sediaan_obat where nama_bentuk_sediaan  = '" + namaSatuan + "' ");
                    dataS.next();
                    DB.query2("call simpanJenisPenjualan('" + kodeObat + "','" + total + "','" + harga + "','" + dataS.getInt("id") + "') ");
                }

                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, form);
                tampilData();
                form.dispose();
                idEdit = "";
                table.clearSelection();
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR, form);
            System.out.println("error simpan data " + e.getMessage());
        }
    }

    public void editData(Object[] id, JPanel Field) {

    }

    
    public void editData(Object[] id) {
        try {
            if (table.getSelectedRow() < 0) {
                return;
            }
            clearDialog();
            int row = table.getSelectedRow();
            String idObat = table.getValueAt(row, 1).toString();
            namaObat.setText(table.getValueAt(row, 2).toString());
            aturanPakai.setText(table.getValueAt(row, 6).toString());
            titleForm.setText("Ubah Data Obat");
            status = 2;
            idEdit = idObat;

            ResultSet kategoriData = DB.query("SELECT * from kategori");
            kategori.removeAllItems();
            while (kategoriData.next()) {
                kategori.addItem(kategoriData.getString("nama_kategori"));
                if (table.getValueAt(row, 5).toString().equals(kategoriData.getString("nama_kategori"))) {

                    kategori.setSelectedItem(table.getValueAt(row, 5).toString());
                }
            }
            ResultSet satuanData = DB.query("SELECT * from jenis_penjualan where kode_obat='" + idObat + "'");
            while (satuanData.next()) {
                addSatuan(satuanData.getString("id_bentuk_sediaan"), satuanData.getInt("harga"), satuanData.getInt("total"));
            }

            form.pack();
            form.setLocationRelativeTo(null);
            form.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(table, "Sistem error " + e.getMessage());

        }
    }
    
    public void detail(){
        try {
            if (table.getSelectedRow() < 0) {
                return;
            }
            clearDialog();
            int row = table.getSelectedRow();
            String idObat = table.getValueAt(row, 1).toString();
            namaObat1.setText(table.getValueAt(row, 2).toString());
            aturanPakai1.setText(table.getValueAt(row, 6).toString());

            DefaultTableModel model1  = (DefaultTableModel) tableSatuan.getModel();
            ResultSet satuanData = DB.query("SELECT * from data_jenis_penjualan where kode_obat='" + idObat + "'");
            model1.setRowCount(0);
            while (satuanData.next()) {
                Object[] objectRow = {satuanData.getString("satuan"), satuanData.getInt("total"), satuanData.getInt("harga")};
                model1.addRow(objectRow);
            }

            DefaultTableModel model2  = (DefaultTableModel) tableStok.getModel();
            ResultSet stokData = DB.query("SELECT * from stok_obat join supplier on stok_obat.kode_suplier = supplier.kode_suplier where kode_obat='" + idObat + "' order by tanggal_masuk desc");
            model2.setRowCount(0);
            while (stokData.next()) {
                Object[] objectRow = {stokData.getString("jumlah_obat"), stokData.getString("nama_suplier"),FormatTanggal.formatDate(stokData.getDate("tanggal_kadaluarsa")), FormatTanggal.formatDate(stokData.getDate("tanggal_masuk"))};
                model2.addRow(objectRow);
            }

            detail.pack();
            detail.setLocationRelativeTo(null);
            detail.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(table, "Sistem error " + e.getMessage());

        }
    }

    
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void addList(Object[] component) {
        try {

            JComboBox satuan = (JComboBox) component[0];
            String satuanText = satuan.getSelectedItem().toString();
            DefaultTableModel model = (DefaultTableModel) listParent.getModel();
//            if (satuanList.size() == 5) {
//                JOptionPane.showMessageDialog(form, "Multi Satuan Maksimal 5");
//                return;
//            }
//            satuanList.add(satuanText);

            model.addRow(new Object[]{satuanText, "test"});

            ResultSet dataSatuan = getSatuan();
            satuan.removeAllItems();
            while (dataSatuan.next()) {
                satuan.addItem(dataSatuan.getString("nama_bentuk_sediaan"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ResultSet getSatuan() {
        try {
            String sql;
            sql = "SELECT * FROM bentuk_sediaan_obat ";
            return DB.query(sql);

        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet getKategori() {
        try {
            return DB.query("SELECT * FROM kategori ");

        } catch (Exception e) {
            return null;
        }
    }

    public void addSatuan(String idSelect, int harga, int total) {
        ResultSet dataObat = getSatuan();

        int w = satuanListCom.getBounds().width;
        JPanel panel = new JPanel();
        JTextField field = new JTextField("" + total);
        JLabel label = new JLabel();
        JComboBox combo = new JComboBox();
        ButtonIcon button = new ButtonIcon();
        button.setHorizontal(true);
        button.setIcon("Assets/svg/deleteIcon.svg");

        panel.add(combo);
        if (satuanList.size() > 0) {
            panel.add(field);
            panel.add(label);
            label.setText("/" + satuanList.get(0).getSelectedItem().toString());
            label.setPreferredSize(new Dimension(70, 42));
            combo.setPreferredSize(new Dimension(310, 42));
            field.setPreferredSize(new Dimension(70, 42));
            satuanList.get(0).addItemListener(new ItemListener() {
                
                public void itemStateChanged(ItemEvent e) {
                    label.setText("/ " + satuanList.get(0).getSelectedItem().toString());
                }
            });
            panel.add(button);
        } else {
            combo.setPreferredSize(new Dimension(450, 42));

        }

        panel.setBackground(Color.white);
        button.setBackground(new Color(215, 9, 83));
        panel.setMaximumSize(new Dimension(w, 50));
        button.setPreferredSize(new Dimension(42, 42));

        try {
            while (dataObat.next()) {
                combo.addItem(dataObat.getString("nama_bentuk_sediaan"));
                if (idSelect != null && idSelect.equals(dataObat.getString("id").toString())) {
                    combo.setSelectedItem(dataObat.getString("nama_bentuk_sediaan"));
                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        satuanTotalList.add(field);
        satuanListCom.add(panel);
        satuanList.add(combo);
        satuanListCom.repaint();
        satuanListCom.revalidate();

        generateHarga(combo, harga, button);
        button.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (satuanList.size() == 1) {
                    return;
                }
                deleteSatuanList(panel, combo, field);
            }

        });
    }

    public void generateHarga(JComboBox combos, int harga, javax.swing.JButton button) {
        int w = satuanListCom.getBounds().width;
        JPanel panel = new JPanel(new GridLayout());
        JTextField field = new JTextField("" + harga);
        JLabel label2 = new JLabel();
        JLabel label = new JLabel("Def. Harga Satuan #" + (hargaList.size() + 1));

        panel.add(label);
        panel.add(field);
        panel.add(label2);
        label2.setText("/" + satuanList.get(0).getSelectedItem().toString());
        label2.setPreferredSize(new Dimension(70, 42));
        field.setPreferredSize(new Dimension(70, 42));
        label.setBorder(new EmptyBorder(0, 0, 0, 20));
        label2.setBorder(new EmptyBorder(0, 20, 0, 0));
        panel.setBackground(Color.white);
        panel.setMaximumSize(new Dimension(w, 42));
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        combos.addItemListener(new ItemListener() {
            
            public void itemStateChanged(ItemEvent e) {
                label2.setText("/ " + combos.getSelectedItem().toString());
            }
        });

        button.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (satuanList.size() == 1) {
                    return;
                }
                hargaList.remove(field);
                hargaListCom.remove(panel);
                satuanListCom.repaint();
                satuanListCom.revalidate();
            }

        });
        hargaList.add(field);
        hargaListCom.add(panel);
        hargaListCom.repaint();
        hargaListCom.revalidate();

    }

    public void deleteSatuanList(JPanel panel, JComboBox combo, JTextField total) {
        satuanList.remove(combo);
        satuanTotalList.remove(total);
        satuanListCom.remove(panel);
        satuanListCom.repaint();
        satuanListCom.revalidate();

        System.out.println("dihapus");
    }

    public void clearDialog() {
        satuanListCom.removeAll();
        hargaListCom.removeAll();
        satuanList.clear();
        hargaList.clear();
        satuanTotalList.clear();
        aturanPakai.setText("");
        namaObat.setText("");
    }
}
