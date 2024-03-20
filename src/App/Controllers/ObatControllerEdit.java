/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package App.Controllers;

import Components.ButtonIcon;

import Config.DB;
import Helper.Currency;
import Helper.DataFormat;
import Helper.KodeGenerator;
import Helper.Notification;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class ObatControllerEdit  {

    private ArrayList<Integer> idKategori = new ArrayList<Integer>();
    private ArrayList<Object[]> obatList = new ArrayList<Object[]>();
    private String idEdit = "";
    int status = 1;
    private ArrayList<JComboBox> satuanList = new ArrayList<JComboBox>();
    private ArrayList<JTextField> satuanTotalList = new ArrayList<JTextField>();
    private ArrayList<JTextField> hargaList = new ArrayList<JTextField>();


    
    public void tampilData(JTable table) {
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

    
    public ResultSet tambahData() {
        try {
            return  DB.query("SELECT * from kategori");
        } catch (Exception e) {
            return null;
        }

    }

    
    public int hapusData(int index,JTable table) {
        try {

            if (index < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return 0;
            }
            if (!Notification.showConfirmDelete(table)) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, table);
                return 0;

            }
            String id = table.getValueAt(table.getSelectedRow(), 1).toString();
            ResultSet transaksiData = DB.query("SELECT count(*) as count from detail_pembelian where kode_obat= '" + id + "'");
            transaksiData.next();
        
            DB.query2("DELETE FROM obat where kode_obat='" + id + "'");
            Notification.showInfo(Notification.DATA_DELETED_SUCCESS, table);
            return 1;
        } catch (SQLException e) {     
            int errorCode = e.getErrorCode();
            if (errorCode == 1451) {
                Notification.showError(Notification.DATA_IN_USE_ERROR , table);
                return 0;
            }
            Notification.showError(Notification.SERVER_ERROR + " " + e.getMessage(), table);
            return 0;
        }
    }

    public void cariData(String kunci,JTable table) {
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

    
    public int  simpanData(DataFormat data,JDialog form) {
        String namaObat = data.getString("nama_obat");
        String kategori = data.getString("kategori");
        String kandungan = data.getString("kandungan");

        String kodeObat = idEdit.equals("") ? KodeGenerator.generateKodeObat() : idEdit;

        Set<String> uniqueBarangList = new HashSet<>();

        try {
            if (obatList.stream().anyMatch(satuan -> satuan[2].toString().trim().equalsIgnoreCase(namaObat.trim()) && !satuan[1].equals(idEdit))) {
                Notification.showInfo(Notification.DUPLICATE_DATA, form);
            } else if (namaObat.equals("") || kandungan.equals("") || kategori.equals("")) {
                Notification.showInfo(Notification.EMPTY_VALUE, form);

            } else {
                for (int i = 0; i < satuanList.size(); i++) {
                    String value = satuanList.get(i).getSelectedItem().toString();
                    String value2 = satuanTotalList.get(i).getText();
                    String harga = hargaList.get(i).getText();
                    if (!uniqueBarangList.add(value)) {
                        Notification.showInfo("Nilai duplikat dalam satuan: " + value, form);
                        return 0;
                    }
                    if (value2.equals("")) {
                        Notification.showInfo("Total Satuan Harap Diisi: " + value, form);
                        return 0;
                    }
                    if (!harga.matches("[0-9]+")) {
                        Notification.showInfo("Total Satuan harus berupa angka: ", form);
                        return 0;
                    }
                }
                ResultSet dataKategori = DB.query("SELECT id from kategori where nama_kategori= '" + kategori + "'");
                ResultSet dataSatuan = DB.query("SELECT id from satuan where nama_satuan= '" + satuanList.get(0).getSelectedItem().toString() + "'");
                dataKategori.next();
                dataSatuan.next();
                DB.query2("DELETE FROM jenis_penjualan where kode_obat = '" + kodeObat + "'");
                System.out.println("CALL simpanDataObat('" + kodeObat + "','" + namaObat + "','" + dataSatuan.getString("id") + "','" + dataKategori.getString("id") + "','" + kandungan + "')");
                DB.query2("CALL simpanDataObat('" + kodeObat + "','" + namaObat + "','" + dataSatuan.getString("id") + "','" + dataKategori.getString("id") + "','" + kandungan + "')");
                for (int i = 0; i < satuanList.size(); i++) {
                    String namaSatuan = satuanList.get(i).getSelectedItem().toString();
                    String total = satuanTotalList.get(i).getText();
                    String harga = hargaList.get(i).getText();
                    ResultSet dataS = DB.query("SELECT id from satuan where nama_satuan  = '" + namaSatuan + "' ");
                    dataS.next();
                    System.out.println("call simpanJenisPenjualan('" + kodeObat + "','" + total + "','" + harga + "','" + dataS.getInt("id") + "') ");

                    DB.query2("call simpanJenisPenjualan('" + kodeObat + "','" + total + "','" + harga + "','" + dataS.getInt("id") + "') ");
                }

                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, form);
                form.dispose();
                idEdit = "";
                return 1;
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR, form);
            System.out.println("error simpan data " + e.getMessage());
            return 0;   
        }
        return 0;

    }

 

    
    public ResultSet editData(String idObat,JTable table,JPanel satuanListCom,JPanel hargaListCOm) {
        try {
           
            status = 2;
            idEdit = idObat;

           
            ResultSet satuanData = DB.query("SELECT * from jenis_penjualan where kode_obat='" + idObat + "'");
            while (satuanData.next()) {
                addSatuan(satuanData.getString("id_satuan"), satuanData.getInt("harga"), satuanData.getInt("total"),satuanListCom,hargaListCOm);
            }
            return DB.query("SELECT * from kategori");

          
        } catch (Exception e) {
            JOptionPane.showMessageDialog(table, "Sistem error " + e.getMessage());
            return null;

        }
    }
    
    public ResultSet[] detail(String idObat,JTable table){
        try {
         

            ResultSet satuanData = DB.query("SELECT * from data_jenis_penjualan where kode_obat='" + idObat + "'");
            ResultSet stokData = DB.query("SELECT * from batch_obat join supplier on batch_obat.kode_suplier = supplier.kode_suplier where kode_obat='" + idObat + "' order by tanggal_masuk desc");
            ResultSet[] result = {satuanData, stokData};
            return result;

            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(table, "Sistem error " + e.getMessage());
            return null;

        }
    }

    
    public void updateData(Object[] object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    public void addList(Object[] component) {
//        try {
//
//            JComboBox satuan = (JComboBox) component[0];
//            String satuanText = satuan.getSelectedItem().toString();
//            DefaultTableModel model = (DefaultTableModel) listParent.getModel();
////            if (satuanList.size() == 5) {
////                JOptionPane.showMessageDialog(form, "Multi Satuan Maksimal 5");
////                return;
////            }
////            satuanList.add(satuanText);
//
//            model.addRow(new Object[]{satuanText, "test"});
//
//            ResultSet dataSatuan = getSatuan();
//            satuan.removeAllItems();
//            while (dataSatuan.next()) {
//                satuan.addItem(dataSatuan.getString("nama_satuan"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public ResultSet getSatuan() {
        try {
            String sql;
            sql = "SELECT * FROM satuan ";
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

    public void addSatuan(String idSelect, int harga, int total,JPanel satuanListCom,JPanel hargaListCOm) {
        ResultSet dataObat = getSatuan();
        if (satuanTotalList.size()==4) {
             JOptionPane.showMessageDialog(hargaListCOm, "Satuan Penjualan maksimal 4");
             return;
        }
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
                   if(satuanList.get(0).getSelectedItem() !=null){
                        label.setText("/ " + satuanList.get(0).getSelectedItem().toString());
                   }
                }
            });
            panel.add(button);
        } else {
            combo.setPreferredSize(new Dimension(500, 42));

        }

        panel.setBackground(Color.white);
        button.setBackground(new Color(215, 9, 83));
        panel.setMaximumSize(new Dimension(w, 50));
        button.setPreferredSize(new Dimension(42, 42));

        try {
            while (dataObat.next()) {
                combo.addItem(dataObat.getString("nama_satuan"));
                if (idSelect != null && idSelect.equals(dataObat.getString("id").toString())) {
                    combo.setSelectedItem(dataObat.getString("nama_satuan"));
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

        generateHarga(combo, harga, button,satuanListCom,hargaListCOm);
        button.addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (satuanList.size() == 1) {
                    return;
                }
                deleteSatuanList(panel, combo, field,satuanListCom);
            }

        });
    }

    public void generateHarga(JComboBox combos, int harga, javax.swing.JButton button,JPanel satuanListCom,JPanel hargaListCom) {
        int w = satuanListCom.getBounds().width;
        JPanel panel = new JPanel(new GridLayout());
        JTextField field = new JTextField("" + harga);
        JTextField label2 = new JTextField();
        JLabel label = new JLabel("Margin Persen " + (hargaList.size() + 1));

        panel.add(label);
        panel.add(field);
        panel.add(label2);
        if(satuanList.get(0).getSelectedItem() !=null){
                label2.setText("/" + satuanList.get(hargaList.size()).getSelectedItem().toString());
        }
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

    public void deleteSatuanList(JPanel panel, JComboBox combo, JTextField total,JPanel satuanListCom) {
        satuanList.remove(combo);
        satuanTotalList.remove(total);
        satuanListCom.remove(panel);
        satuanListCom.repaint();
        satuanListCom.revalidate();

        System.out.println("dihapus");
    }

    public void clearDialog(JPanel satuanListCom,JPanel hargaListCom) {
        satuanListCom.removeAll();
        hargaListCom.removeAll();
        satuanList.clear();
        hargaList.clear();
        satuanTotalList.clear();
       
    }
}
