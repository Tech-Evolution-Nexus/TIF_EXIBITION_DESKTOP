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
import Components.btnAction.obatAction.BtnAction;
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
    private boolean isEdit = false;
    private ArrayList<Object[]> listData = new ArrayList<>();

    public TransaksiPenjualanController() {

        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                reset();
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

                if (Integer.parseInt(value) > 0)
                    view.getAddList().setEnabled(true);
                else
                    view.getAddList().setEnabled(true);

            }
        });
        view.getBayar().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String bayar = view.getBayar().getText();
                if (bayar.equals("")) {
                    bayar = "0";
                }
                kembalian = Integer.parseInt(bayar) - hargaTotal;
                pembayaran = Integer.parseInt(bayar);
                view.getKembalian().setText(Helper.Currency.format(kembalian));
                if (Integer.parseInt(bayar) >= hargaTotal)
                    view.getBtnBayar().setEnabled(true);
                else
                    view.getBtnBayar().setEnabled(false);

            }
        });
        view.getTableCari().addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
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
                editTable(row);
            }

            @Override
            public void onDelete(int row) {
                listData.remove(row);
                removeData(row);
            }
        };

        view.getTable().getColumnModel().getColumn(6).setCellRenderer(new BtnAction());
        view.getTable().getColumnModel().getColumn(6).setCellEditor(new BtnEditor(event));

    }
    

    
    private void cariData(String kunci) {
        try {
            DefaultTableModel model = (DefaultTableModel) view.getTableCari().getModel();
           
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


    private void removeData(int row) {
        sumHarga();
        ((DefaultTableModel) view.getTable().getModel()).removeRow(row);
    }
    private void selectObat(int row) {
         String kodeObat = view.getTableCari().getValueAt(row, 0).toString();
         String namaObat = view.getTableCari().getValueAt(row, 1).toString();
         String harga = view.getTableCari().getValueAt(row, 2).toString();
         String satuan = view.getTableCari().getValueAt(row, 3).toString();
         String stok = view.getTableCari().getValueAt(row, 4).toString();

         for (int i = 0; i < listData.size(); i++) {
            if (kodeObat.equals(listData.get(i)[0]) && satuan.equals(listData.get(i)[2])) {
                int stokTerbaru = Integer.parseInt(stok)  - Integer.parseInt(listData.get(i)[4].toString());
                stok = String.valueOf(stokTerbaru);
            }
        }
         view.getJenisPenjualan().setText(satuan);
         view.getQty().setText("1");
         view.getStok().setText(stok);
         view.getHarga().setText(harga);
         view.getKodeObat().setText(kodeObat);
         view.getNamaObat().setText(namaObat);
         view.getAddList().setEnabled(true);
         view.getCariDialog().dispose();
         view.getBatal().setEnabled(true);

    }

    private void showForm() {
        view.getCariDialog().pack();
        view.getCariDialog().setLocationRelativeTo(null);
        view.getCariDialog().setVisible(true);
    }
    
    private void addList() {
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        String satuan = view.getJenisPenjualan().getText();
        int qty = Integer.parseInt(view.getQty().getText());
        int stok = Integer.parseInt(view.getStok().getText());
        int harga = Integer.parseInt(view.getHarga().getText());
        String kodeObat = view.getKodeObat().getText();
        String namaObat = view.getNamaObat().getText();
        int subTotal = harga * qty;
        
        Object[] data = { kodeObat, namaObat, satuan, Helper.Currency.format(harga), qty, Helper.Currency.format(subTotal) };
        for (Object object : data) {
            if (object.equals("")) {
                Notification.showInfo("Semua inputan wajib diisi"+stok, view.getBaseLayer());
                return;
            }
        }
        boolean isExist = false;
        int indexExist = -1;
        for (int i = 0; i < listData.size(); i++) {
            if (kodeObat.equals(listData.get(i)[0]) && satuan.equals(listData.get(i)[2]) ) {
                isExist = true;
                indexExist = i;
            }
        }

        if (qty == 0) {
            Notification.showInfo("Qty tidak tidak boleh 0 ", view.getBaseLayer());
            return;
        }
        if (qty > stok) {
            Notification.showInfo("Qty tidak boleh melebihi " + stok, view.getBaseLayer());
            return;
        }
        
        if (isExist) {
            if (!isEdit) {
                qty += Integer.parseInt(listData.get(indexExist)[4].toString());
            }
            //Menambahkan ke column kode obat
            model.setValueAt(kodeObat, indexExist, 0);
            //Menambahkan ke column nama obat
            model.setValueAt(namaObat, indexExist, 1);
            //Menambahkan ke column Satuan
            model.setValueAt(satuan, indexExist, 2);
            //Menambahkan ke column harga
            model.setValueAt(Helper.Currency.format(harga), indexExist, 3);
            int qtyTotal = qty;
            subTotal = qtyTotal * harga;
            //Menambahkan ke column qty
            model.setValueAt(qtyTotal, indexExist, 4);
            //Menambahkan ke column subtotal
            model.setValueAt(Helper.Currency.format(subTotal), indexExist, 5);
            Object[] dataEdit = { kodeObat, namaObat, satuan, harga, qty, subTotal };
            listData.set(indexExist, dataEdit);
        } else {
            model.addRow(data);
            listData.add(data);
        }
        view.getBatal().setEnabled(false);
        isEdit = false;
        sumHarga();
        resetForm();
    }

    private void sumHarga() {
        try {
            int rowTable = view.getTable().getRowCount();
            int harga = 0;
            for (int i = 0; i < rowTable; i++) {
            harga +=(int) Helper.Currency.deformat(view.getTable().getValueAt(i, 5).toString());
            }
            hargaTotal = harga;
            view.getTotalHarga().setText(Helper.Currency.format(hargaTotal));
        } catch (Exception e) {
            // TODO: handle exception
        }
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
                Notification.showInfo(
                        "Pembayaran kurang \n Pembayaran : " + pembayaran + "\n Total Harga :" + hargaTotal,
                        view.getTable());
                return;
            }
            String kodeTrx = Helper.KodeGenerator.generateKodeTransaksi();
            String[] fieldTrx = { "kode_transaksi", "id_user", "total_harga", "pembayaran", "kembalian" };
            String[] valueTrx = { kodeTrx, "1", String.valueOf(hargaTotal), String.valueOf(pembayaran),String.valueOf(kembalian) };
            transaksiPenjualanModel.insert(fieldTrx, valueTrx);

            for (int i = 0; i < view.getTable().getRowCount(); i++) {
                String kodeObat = view.getTable().getValueAt(i, 0).toString();
                String harga = view.getTable().getValueAt(i, 3).toString();
                String qty = view.getTable().getValueAt(i, 4).toString();
                String subtotal = view.getTable().getValueAt(i, 5).toString();
                String[] field = { "kode_transaksi", "kode_obat", "harga", "qty", "subtotal" };
                String[] value = { kodeTrx, kodeObat, harga, qty, subtotal };
                detailPenjualanModel.insert(field, value);
            }

            Notification.showInfo("Transaksi Berhasil", view.getBaseLayer());
        } catch (Exception e) {

        }

    }

    private void reset() {
        ((DefaultTableModel) view.getTable().getModel()).setRowCount(0);
        hargaTotal = 0;
        pembayaran = 0;
        kembalian = 0;
        resetForm();
        view.getBayar().setText("0");
        view.getKembalian().setText("0");
        view.getTotalHarga().setText("Rp. 0");
        listData.clear();
        
    }

    private void editTable(int row) {
        try {
        String kodeObat = view.getTable().getValueAt(row, 0).toString();
        String namaObat = view.getTable().getValueAt(row, 1).toString();
        String satuan = view.getTable().getValueAt(row, 2).toString();
        String harga = String.valueOf(Helper.Currency.deformat(view.getTable().getValueAt(row, 3).toString()));
        String qty = view.getTable().getValueAt(row, 4).toString();
        view.getJenisPenjualan().setText(satuan);
        view.getQty().setText(qty);
        view.getHarga().setText(harga);
        view.getKodeObat().setText(kodeObat);
        view.getNamaObat().setText(namaObat);

        ResultSet datas = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE satuan =  '" + satuan
                    + "' AND kode_obat  = '" + kodeObat + "'");
        while (datas.next()) {
            ResultSet obat = obatModel.where("kode_obat", "=", datas.getString("kode_obat")).get();
            obat.next();
            int stok = obat.getInt("jumlah_obat") / datas.getInt("total");
            if (stok > 0) {
                view.getStok().setText(String.valueOf(stok));
            }
        }
        isEdit = true;
        view.getBatal().setEnabled(true);
        } catch (Exception e) {
            Notification.showInfo("Terjadi kesalahan dengan sistem", view.getBaseLayer());
        }
    }
    @Override
    public Component getView() {
        return view;
    }
}
