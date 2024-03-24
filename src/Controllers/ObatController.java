/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;


import Config.DB;
import Core.Controller;
import Helper.Currency;
import Helper.FormatTanggal;
import Helper.KodeGenerator;
import Helper.Notification;
import View.ObatView;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import App.Model.KategoriModel;
import App.Model.ObatModel;
import App.Model.SatuanModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class ObatController  extends Controller{

    private ArrayList<Integer> idKategori = new ArrayList<Integer>();
    private ArrayList<Object[]> obatList = new ArrayList<Object[]>();
    private String idEdit = "";
    int status = 1;
    private ArrayList<JComboBox> satuanList = new ArrayList<JComboBox>();
    private ArrayList<JTextField> satuanTotalList = new ArrayList<JTextField>();
    private ArrayList<JTextField> hargaList = new ArrayList<JTextField>();
    private ObatView view = new ObatView();
    private ObatModel model = new ObatModel();
    private KategoriModel kategoriModel = new KategoriModel();
    private SatuanModel satuanModel = new SatuanModel();
    public ObatController() {
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
            ResultSet data = DB.query("SELECT * FROM data_obat order by tanggal_dibuat desc");
            if (cari) {
                data = DB.query("SELECT * FROM data_obat where data_obat.nama_obat like '%" + kunci + "%'  order by tanggal_dibuat desc");
                
            }
            DefaultTableModel tabelData = (DefaultTableModel) view.getTable().getModel();
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

    public void tambahData() {
        try {
            clearDialog();
            view.getTitleForm().setText("Tambah  Obat");
            ResultSet kategoriData = DB.query("SELECT * from kategori");
            // addSatuan(null, 0, 1);
            view.getKategori().removeAllItems();
            while (kategoriData.next()) {
                view.getKategori().addItem(kategoriData.getString("nama_kategori"));
            }
            
            showForm();
        } catch (Exception e) {
        }
    }

    
    public void hapusData() {
        try {

            if (view.getTable().getSelectedRow() < 0) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, view.getTable());
                return;
            }
            if (!Notification.showConfirmDelete(view.getTable())) {
                Notification.showInfo(Notification.NO_DATA_SELECTED_INFO, view.getTable());
                return;
            }
            String id = view.getTable().getValueAt(view.getTable().getSelectedRow(), 1).toString();
            ResultSet transaksiData = DB.query("SELECT count(*) as count from detail_pembelian where kode_obat= '" + id + "'");
            transaksiData.next();
            if (transaksiData.getInt("count") > 0) {
                Notification.showInfo(Notification.DATA_IN_USE_ERROR, view.getTable());
                return;
            }
            model.delete("kode_obat='" + id + "'");
            tampilData(false);
            Notification.showInfo(Notification.DATA_DELETED_SUCCESS, view.getTable());

        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR + " " + e.getMessage(), view.getTable());
        }
    }

   

    
    public void simpanData() {
        String namaObat = view.getNamaObat().getText();
        String kategori = view.getKategori().getSelectedItem().toString();
        String kandungan = view.getKandungan().getText();

        String kodeObat = idEdit.equals("") ? KodeGenerator.generateKodeObat() : idEdit;

        Set<String> uniqueBarangList = new HashSet<>();

        try {
            if (obatList.stream().anyMatch(satuan -> satuan[2].toString().trim().equalsIgnoreCase(namaObat.trim()) && !satuan[1].equals(idEdit))) {
                Notification.showInfo(Notification.DUPLICATE_DATA, view.getForm());
            } else if (namaObat.equals("") || kandungan.equals("") || kategori.equals("")) {
                Notification.showInfo(Notification.EMPTY_VALUE, view.getForm());

            } else {
                for (int i = 0; i < satuanList.size(); i++) {
                    String value = satuanList.get(i).getSelectedItem().toString();
                    String value2 = satuanTotalList.get(i).getText();
                    String harga = hargaList.get(i).getText();
                    if (!uniqueBarangList.add(value)) {
                        Notification.showInfo("Nilai duplikat dalam satuan: " + value, view.getForm());
                        return;
                    }
                    if (value2.equals("")) {
                        Notification.showInfo("Total Satuan Harap Diisi: " + value, view.getForm());
                        return;
                    }
                    if (!harga.matches("[0-9]+")) {
                        Notification.showInfo("Total Satuan harus berupa angka: ", view.getForm());
                        return;
                    }

                }
                String namaSatuan = satuanList.get(0).getSelectedItem().toString();
                ResultSet dataKategori =kategoriModel.select("id").where("nama_kategori","=", kategori).get();
                ResultSet dataSatuan = satuanModel.select("id").where("nama_satuan", "=", namaSatuan).get();
                dataKategori.next();
                dataSatuan.next();
                DB.query2("DELETE FROM jenis_penjualan where kode_obat = '" + kodeObat + "'");
                DB.query2("CALL simpanDataObat('" + kodeObat + "','" + namaObat + "','" + dataSatuan.getString("id") + "','" + dataKategori.getString("id") + "','" + kandungan + "')");
                for (int i = 0; i < satuanList.size(); i++) {
                    namaSatuan = satuanList.get(i).getSelectedItem().toString();
                    String total = satuanTotalList.get(i).getText();
                    String harga = hargaList.get(i).getText();
                    dataSatuan = satuanModel.select("id").where("nama_satuan", "=", namaSatuan).get();
                    dataSatuan.next();
                    DB.query2("call simpanJenisPenjualan('" + kodeObat + "','" + total + "','" + harga + "','"
                            + dataSatuan.getInt("id") + "') ");
                }

               
                Notification.showSuccess(Notification.DATA_ADDED_SUCCESS, view.getForm());
                tampilData(false);
                view.getForm().dispose();
                idEdit = "";
                view.getTable().clearSelection();
            }
        } catch (Exception e) {
            Notification.showError(Notification.SERVER_ERROR, view.getForm());
            System.out.println("error simpan data " + e.getMessage());
        }
    }
    
    public void editData() {
        try {
            clearDialog();
            if (view.getTable().getSelectedRow() < 0) return;
            int row = view.getTable().getSelectedRow();
            String idObat = view.getTable().getValueAt(row, 1).toString();
            String namaObat = view.getTable().getValueAt(row, 2).toString();
            String kandungan = view.getTable().getValueAt(row, 6).toString();
            view.getNamaObat().setText(namaObat);
            view.getKandungan().setText(kandungan);
            view.getTitleForm().setText("Ubah Obat");
            status = 2;
            idEdit = idObat;

            ResultSet kategoriData = DB.query("SELECT * from kategori");
            view.getKategori().removeAllItems();
            while (kategoriData.next()) {
                view.getKategori().addItem(kategoriData.getString("nama_kategori"));
                String namaKategori = view.getTable().getValueAt(row, 5).toString();
                boolean cekNamaKategori = namaKategori.equals(kategoriData.getString("nama_kategori"));
                if (cekNamaKategori) {
                    view.getKategori().setSelectedItem(namaKategori);
                }
            }
            ResultSet satuanData = DB.query("SELECT * from jenis_penjualan where kode_obat='" + idObat + "'");
            while (satuanData.next()) {
                // addSatuan(satuanData.getString("id_bentuk_sediaan"), satuanData.getInt("harga"), satuanData.getInt("total"));
            }

          showForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getTable(), "Sistem error " + e.getMessage());

        }
    }
    
    public void detail(){
        try {
            if (view.getTable().getSelectedRow() < 0) {
                return;
            }
            clearDialog();
            int row = view.getTable().getSelectedRow();
            String idObat = view.getTable().getValueAt(row, 1).toString();
            String namaObat = view.getTable().getValueAt(row, 2).toString();
            String kandungan = view.getTable().getValueAt(row, 6).toString();
            view.getDetailNama().setText(namaObat);
            view.getDetailKandungan().setText(kandungan);

            DefaultTableModel model  = (DefaultTableModel) view.getSatuanTable().getModel();
            ResultSet satuanData = DB.query("SELECT * from data_jenis_penjualan where kode_obat='" + idObat + "'");
            model.setRowCount(0);
            while (satuanData.next()) {
                Object[] objectRow = {satuanData.getString("satuan"), satuanData.getInt("total"), satuanData.getInt("harga")};
                model.addRow(objectRow);
            }

             model  = (DefaultTableModel) view.getStokTable().getModel();
            ResultSet stokData = DB.query("SELECT * from stok_obat join supplier on stok_obat.kode_suplier = supplier.kode_suplier where kode_obat='" + idObat + "' order by tanggal_masuk desc");
            model.setRowCount(0);
            while (stokData.next()) {
                Object[] objectRow = {stokData.getString("jumlah_obat"), stokData.getString("nama_suplier"),FormatTanggal.formatDate(stokData.getDate("tanggal_kadaluarsa")), FormatTanggal.formatDate(stokData.getDate("tanggal_masuk"))};
                model.addRow(objectRow);
            }

            view.getDetail().pack();
            view.getDetail().setLocationRelativeTo(null);
            view.getDetail().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getTable(), "Sistem error " + e.getMessage());

        }
    }

    
  

//     public void addList(Object[] component) {
//         try {

//             JComboBox satuan = (JComboBox) component[0];
//             String satuanText = satuan.getSelectedItem().toString();
//             DefaultTableModel model = (DefaultTableModel) view.getlist.getModel();
// //            if (satuanList.size() == 5) {
// //                JOptionPane.showMessageDialog(form, "Multi Satuan Maksimal 5");
// //                return;
// //            }
// //            satuanList.add(satuanText);

//             model.addRow(new Object[]{satuanText, "test"});

//             ResultSet dataSatuan = getSatuan();
//             satuan.removeAllItems();
//             while (dataSatuan.next()) {
//                 satuan.addItem(dataSatuan.getString("nama_bentuk_sediaan"));
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//     }

//     public ResultSet getSatuan() {
//         try {
//             String sql;
//             sql = "SELECT * FROM bentuk_sediaan_obat ";
//             return DB.query(sql);

//         } catch (Exception e) {
//             return null;
//         }
//     }

  

    // public void addSatuan(String idSelect, int harga, int total) {
    //     ResultSet dataObat = getSatuan();

    //     int w = satuanListCom.getBounds().width;
    //     JPanel panel = new JPanel();
    //     JTextField field = new JTextField("" + total);
    //     JLabel label = new JLabel();
    //     JComboBox combo = new JComboBox();
    //     ButtonIcon button = new ButtonIcon();
    //     button.setHorizontal(true);
    //     button.setIcon("Assets/svg/deleteIcon.svg");

    //     panel.add(combo);
    //     if (satuanList.size() > 0) {
    //         panel.add(field);
    //         panel.add(label);
    //         label.setText("/" + satuanList.get(0).getSelectedItem().toString());
    //         label.setPreferredSize(new Dimension(70, 42));
    //         combo.setPreferredSize(new Dimension(310, 42));
    //         field.setPreferredSize(new Dimension(70, 42));
    //         satuanList.get(0).addItemListener(new ItemListener() {
                
    //             public void itemStateChanged(ItemEvent e) {
    //                 label.setText("/ " + satuanList.get(0).getSelectedItem().toString());
    //             }
    //         });
    //         panel.add(button);
    //     } else {
    //         combo.setPreferredSize(new Dimension(450, 42));

    //     }

    //     panel.setBackground(Color.white);
    //     button.setBackground(new Color(215, 9, 83));
    //     panel.setMaximumSize(new Dimension(w, 50));
    //     button.setPreferredSize(new Dimension(42, 42));

    //     try {
    //         while (dataObat.next()) {
    //             combo.addItem(dataObat.getString("nama_bentuk_sediaan"));
    //             if (idSelect != null && idSelect.equals(dataObat.getString("id").toString())) {
    //                 combo.setSelectedItem(dataObat.getString("nama_bentuk_sediaan"));
    //             }

    //         }
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //     }
    //     satuanTotalList.add(field);
    //     satuanListCom.add(panel);
    //     satuanList.add(combo);
    //     satuanListCom.repaint();
    //     satuanListCom.revalidate();

    //     generateHarga(combo, harga, button);
    //     button.addMouseListener(new MouseAdapter() {
            
    //         public void mouseClicked(MouseEvent e) {
    //             super.mouseClicked(e);
    //             if (satuanList.size() == 1) {
    //                 return;
    //             }
    //             deleteSatuanList(panel, combo, field);
    //         }

    //     });
    // }

    // public void generateHarga(JComboBox combos, int harga, javax.swing.JButton button) {
    //     int w = satuanListCom.getBounds().width;
    //     JPanel panel = new JPanel(new GridLayout());
    //     JTextField field = new JTextField("" + harga);
    //     JLabel label2 = new JLabel();
    //     JLabel label = new JLabel("Def. Harga Satuan #" + (hargaList.size() + 1));

    //     panel.add(label);
    //     panel.add(field);
    //     panel.add(label2);
    //     label2.setText("/" + satuanList.get(0).getSelectedItem().toString());
    //     label2.setPreferredSize(new Dimension(70, 42));
    //     field.setPreferredSize(new Dimension(70, 42));
    //     label.setBorder(new EmptyBorder(0, 0, 0, 20));
    //     label2.setBorder(new EmptyBorder(0, 20, 0, 0));
    //     panel.setBackground(Color.white);
    //     panel.setMaximumSize(new Dimension(w, 42));
    //     panel.setBorder(new EmptyBorder(5, 0, 5, 0));
    //     combos.addItemListener(new ItemListener() {
            
    //         public void itemStateChanged(ItemEvent e) {
    //             label2.setText("/ " + combos.getSelectedItem().toString());
    //         }
    //     });

    //     button.addMouseListener(new MouseAdapter() {
            
    //         public void mouseClicked(MouseEvent e) {
    //             super.mouseClicked(e);
    //             if (satuanList.size() == 1) {
    //                 return;
    //             }
    //             hargaList.remove(field);
    //             hargaListCom.remove(panel);
    //             satuanListCom.repaint();
    //             satuanListCom.revalidate();
    //         }

    //     });
    //     hargaList.add(field);
    //     hargaListCom.add(panel);
    //     hargaListCom.repaint();
    //     hargaListCom.revalidate();

    // }

    // public void deleteSatuanList(JPanel panel, JComboBox combo, JTextField total) {
    //     satuanList.remove(combo);
    //     satuanTotalList.remove(total);
    //     satuanListCom.remove(panel);
    //     satuanListCom.repaint();
    //     satuanListCom.revalidate();

    //     System.out.println("dihapus");
    // }

    public void clearDialog() {
        view.getSatuanList().removeAll();
        view.getHargaList().removeAll();
        satuanList.clear();
        hargaList.clear();
        satuanTotalList.clear();
        view.getKandungan().setText("");
        view.getNamaObat().setText("");
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
