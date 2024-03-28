/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import View.Auth.login;
import Config.DB;
import Core.Controller;
import Helper.Currency;
import Helper.KodeGenerator;
import Helper.Notification;
import Helper.Validasi;
import View.KategoriView;
import View.PenjualanView;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.w3c.dom.events.MouseEvent;

import App.Model.DetailPenjualanModel;
import App.Model.KategoriModel;
import App.Model.ObatModel;
import App.Model.TransaksiPenjualanModel;
import Components.btnAction.obatAction.ActionEvent;
import Components.btnAction.obatAction.BtnEditor;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class TransaksiPenjualanController  extends Controller{

   
    private PenjualanView view = new PenjualanView();
    private ObatModel obatModel= new ObatModel();
    private TransaksiPenjualanModel transaksiPenjualanModel= new TransaksiPenjualanModel();
    private DetailPenjualanModel detailPenjualanModel= new DetailPenjualanModel();
    private int hargaTotal = 0;
    private int kembalian = 0;
    private int pembayaran = 0;
    private ArrayList<Object[]> listData = new ArrayList<>();

    public TransaksiPenjualanController() {
     
        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
        public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                // tampilData(false);
             }
        public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
        }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        view.getBtnCari().addActionListener(e -> {
            String kunci = view.getCariObat().getText();
            cariData(kunci);
            showForm();
        });
        view.getBtnBayar().addActionListener(e -> simpanPenjualan());
         view.getCariDialogForm().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                 String kunci = view.getCariDialogForm().getText();
                 cariData(kunci);
            }
         });
         view.getQty().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                 String value = view.getQty().getText();
                 if (value.equals("")) {
                     value = "0";
                 }
                
                 if (Integer.parseInt(value)>0) view.getAddList().setEnabled(true);
                  else view.getAddList().setEnabled(true);
                    
            }
         });
         view.getBayar().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                 String bayar =view.getBayar().getText();
                 if (bayar.equals("")) {
                     bayar = "0";
                 }
                 kembalian = Integer.parseInt(bayar)-hargaTotal ;
                 pembayaran = Integer.parseInt(bayar);
                 view.getKembalian().setText(Helper.Currency.format(kembalian));
                 if (Integer.parseInt(bayar) >= hargaTotal) view.getBtnBayar().setEnabled(true);
                 else  view.getBtnBayar().setEnabled(false);
                    
            }
         });
        view.getTableCari().addMouseListener(new MouseAdapter() {
        public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
            JTable table =(JTable) mouseEvent.getSource();
            Point point = mouseEvent.getPoint();
            int row = table.rowAtPoint(point);
            if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    selectObat(row);
            }
        }
        });
        view.getAddList().addActionListener(e -> addList());
         ActionEvent event = new ActionEvent() {

            @Override
            public void onEdit(int row) {
               
            }

            @Override
             public void onDelete(int row) {
            }
        };
                view.getTable().getColumnModel().getColumn(5).setCellEditor(new BtnEditor(event));

    }

    
    private void cariData(String kunci) {
        try {
            DefaultTableModel model = (DefaultTableModel) view.getTableCari().getModel();
            System.out.println("SELECT * FROM `data_jenis_penjualan` WHERE nama_obat like '%" + kunci
                    + "%' OR kode_obat  = '%" + kunci + "%'");
            ResultSet datas = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE nama_obat like '%" + kunci
                    + "%' OR kode_obat  = '%" + kunci + "%'");
            model.setRowCount(0);
            while (datas.next()) {
                ResultSet obat = obatModel.where("kode_obat", "=", datas.getString("kode_obat")).get();
                obat.next();
                int stok = obat.getInt("jumlah_obat") / datas.getInt("total");
                if (stok > 0) {
                    Object[] data = { datas.getString("kode_obat"), datas.getString("nama_obat"),
                            datas.getString("harga"), datas.getString("satuan"), stok };
                    model.addRow(data);
                }
            }
        } catch (Exception e) {
            Notification.showError("Terjadi kesalahan dengan sistem", view.getTable());
        }
    }

    private void selectObat(int row) {
         String kodeObat = view.getTableCari().getValueAt(row, 0).toString();
         String namaObat = view.getTableCari().getValueAt(row, 1).toString();
         String harga = view.getTableCari().getValueAt(row, 2).toString();
         String satuan = view.getTableCari().getValueAt(row, 3).toString();
         String stok = view.getTableCari().getValueAt(row, 4).toString();

         view.getJenisPenjualan().setText(satuan);
         view.getQty().setText("1");
         view.getStok().setText(stok);
         view.getHarga().setText(harga);
         view.getKodeObat().setText(kodeObat);
         view.getNamaObat().setText(namaObat);
         view.getAddList().setEnabled(true);
         view.getCariDialog().dispose();
    }

    private void showForm() {
        view.getCariDialog().pack();
        view.getCariDialog().setLocationRelativeTo(null);
        view.getCariDialog().setVisible(true);
    }
    
    private void addList() {
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        String satuan = view.getJenisPenjualan().getText();
        String qty = view.getQty().getText();
        String stok = view.getStok().getText();
        int harga = Integer.parseInt(view.getHarga().getText());
        String kodeObat = view.getKodeObat().getText();
        String namaObat = view.getNamaObat().getText();
        int subTotal = harga * Integer.parseInt(qty);
        Object[] data = { kodeObat, namaObat, satuan, harga, qty, subTotal };
        boolean isExist = false;
        int indexExist = -1;
        for (int i = 0; i < listData.size(); i++) {
            if (kodeObat.equals(listData.get(i)[0]) && satuan.equals(listData.get(i)[2])) {
                isExist = true;
                indexExist = i;
            }
        }

        if (isExist) {
            listData.set(indexExist, data);
            model.setValueAt(kodeObat, indexExist, 0);
            model.setValueAt(namaObat, indexExist, 1);
            model.setValueAt(satuan, indexExist, 2);
            model.setValueAt(harga, indexExist, 3);
            int qtyTotal = Integer.parseInt(model.getValueAt(indexExist, 4).toString()) + Integer.parseInt(qty);
            subTotal += qtyTotal * harga;
            model.setValueAt(qtyTotal, indexExist, 4);
            model.setValueAt(subTotal, indexExist, 5);
        } else {
            model.addRow(data);
            listData.add(data);
        }
        sumHarga(subTotal);
        resetForm();
    }

    private void sumHarga(int harga) {
        hargaTotal += harga;
        view.getTotalHarga().setText(Helper.Currency.format(hargaTotal));
    }
    
    private void resetForm() {
        view.getJenisPenjualan().setText("");
        view.getQty().setText("1");
        view.getStok().setText("");
        view.getHarga().setText("");
        view.getKodeObat().setText("");
        view.getNamaObat().setText("");
    }

    private void simpanPenjualan() {
       try {
         if (pembayaran < hargaTotal) {
            Notification.showInfo("Pembayaran kurang \n Pembayaran : " + pembayaran + "\n Total Harga :" + hargaTotal,view.getTable());
            return;
        }
       String kodeTrx =  Helper.KodeGenerator.generateKodeTransaksi();
       String[] fieldTrx ={"kode_transaksi","id_user","total_harga","pembayaran","kembalian"} ; 
       String[] valueTrx = {kodeTrx,"1",String.valueOf(hargaTotal),String.valueOf(pembayaran),String.valueOf(kembalian)}; 
       transaksiPenjualanModel.insert(fieldTrx, valueTrx);

       for (int i = 0; i < view.getTable().getRowCount(); i++) {
           String kodeObat = view.getTable().getValueAt(i, 0).toString();
           String harga = view.getTable().getValueAt(i, 3).toString();
           String qty = view.getTable().getValueAt(i, 4).toString();
           String subtotal = view.getTable().getValueAt(i, 5).toString();
           String[] field ={"kode_transaksi","kode_obat","harga","qty","subtotal"} ; 
           String[]  value = {kodeTrx,kodeObat,harga,qty,subtotal}; 
           detailPenjualanModel.insert(field, value);
       }

       Notification.showInfo("Transaksi Berhasil", view.getBaseLayer());
       } catch (Exception e) {
       
       }

    }

    @Override
    public Component getView() {
        return view;
    }
}
