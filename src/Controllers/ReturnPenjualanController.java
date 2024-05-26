/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Core.Controller;
import Helper.Auth;
import Helper.Notification;
import View.ReturPenjualanView;

import java.awt.Component;
import java.awt.Insets;
import java.sql.ResultSet;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import App.Model.DetailPenjualanModel;
import App.Model.DetailPenjualanViewModel;
import App.Model.DetailReturnPenjualanModel;
import App.Model.LaporanPenjualanView;
import App.Model.ReturnPenjualanModel;
import App.Model.TransaksiPenjualanModel;
import Config.DB;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTable;


/**
 *
 * @author Muhammad Nor Kholit
 */
public class ReturnPenjualanController  extends Controller {
    private  ReturPenjualanView view = new ReturPenjualanView();  
    private  LaporanPenjualanView laporanPenjualanView = new LaporanPenjualanView();
    private  DetailPenjualanViewModel detailPenjualanViewModel = new DetailPenjualanViewModel();
    private DetailPenjualanModel detailPenjualanModel= new DetailPenjualanModel();
    private TransaksiPenjualanModel transaksiPenjualanModel= new TransaksiPenjualanModel();
    private ReturnPenjualanModel returnPenjualanModel = new ReturnPenjualanModel();
    private DetailReturnPenjualanModel detailReturnPenjualanModel = new DetailReturnPenjualanModel();

    
    public ReturnPenjualanController() {
        event();
    }


    private void cariTransaksi() {
       try {
         String kodeTransaksi = view.getKodeTransaksi().getText();
           System.out.println(kodeTransaksi);
        if (kodeTransaksi.equals("")) {
            Notification.showInfo("Silahkan masukkan kode transaksi terlebih dahulu", view);
            return;
        }
        ResultSet dataTransaksi = laporanPenjualanView.where("kode_transaksi", "=", kodeTransaksi).get();
       
        if (!dataTransaksi.next()) {
            Notification.showInfo("Transaksi tidak ditemukan", view);
            return;
        }

        //informasi transaksi
        view.getKode_transaksi().setText(dataTransaksi.getString("kode_transaksi"));
        view.getNamaKasir().setText(dataTransaksi.getString("nama_pengguna"));
        view.getPembayaran().setText(Helper.Currency.format(dataTransaksi.getInt("pembayaran")));
        view.getKembalian().setText(Helper.Currency.format(dataTransaksi.getInt("kembalian")));
        view.getTotalHarga().setText(Helper.Currency.format(dataTransaksi.getInt("total_harga")));
        view.getTanggal().setText(Helper.FormatTanggal.format(dataTransaksi.getTimestamp("tanggal_transaksi")));


        ResultSet dataDetail = detailPenjualanViewModel
        .where("kode_transaksi", "=", kodeTransaksi)
        .get();
        DefaultTableModel modelTable = (DefaultTableModel) view.getTable().getModel();
        modelTable.setRowCount(0);
        //memasukkan ke table
        while (dataDetail.next()) {
            
            String kdObat = dataDetail.getString("kode_obat");
            String noBatch = dataDetail.getString("no_batch");
            String namaObat = dataDetail.getString("nama_obat");
            String harga = Helper.Currency.format(dataDetail.getInt("harga"));
            String namaSatuan = dataDetail.getString("nama_satuan");
            String subTotal = Helper.Currency.format(dataDetail.getInt("subtotal"));
            String qty = dataDetail.getString("qty");
            ResultSet jenisObat = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat = '" + kdObat
                    + "' AND satuan  = '" + namaSatuan + "'");
            jenisObat.next();
            qty = String.valueOf(Integer.parseInt(qty) / jenisObat.getInt("total"));
            Object[] data = { kdObat,noBatch, namaObat, namaSatuan,harga, qty,0, subTotal };
            modelTable.addRow(data);
        }



    } catch (Exception e) {
        System.out.println(e.getMessage());
        Notification.showError("Terjadi Kesalahan pada sistem", view);
       }
    }

   
    private void simpanReturn() {
      try {
        int rowCount = view.getTable().getRowCount();
        String kodeTransaksi = view.getKode_transaksi().getText();
        int totalHarga  = 0;
        String pembayaran  = String.valueOf(Helper.Currency.deformat(view.getPembayaran().getText()));
        String kembalian  =String.valueOf(Helper.Currency.deformat( view.getKembalian().getText()));
        String alasan  =view.getAlasan().getText();
        Auth user = new Auth();

        String kodeReturn = Helper.KodeGenerator.generateKodeReturnPenjualan();
        //update transaksi penjualan
        String[] fieldReturn = { "kode_retur_penjualan", "kode_transaksi_penjualan", "alasan_retur","id_user" };
        String[] valueReturn = { kodeReturn, kodeTransaksi, alasan,user.getId()};
        returnPenjualanModel.insert(fieldReturn, valueReturn);

        //update detail penjualan
        for (int i = 0; i < rowCount; i++) {
            Object kondisiObat = view.getTable().getValueAt(i, 8);
            String kodeObat = view.getTable().getValueAt(i, 0).toString();
            String noBatch = view.getTable().getValueAt(i, 1).toString();
            String satuan = view.getTable().getValueAt(i, 3).toString();
            String harga = String.valueOf(Helper.Currency.deformat(view.getTable().getValueAt(i, 4).toString()));
            String qty = view.getTable().getValueAt(i, 5).toString();
            String qtyKembali = view.getTable().getValueAt(i, 6).toString();
            String subtotal = String.valueOf(Helper.Currency.deformat(view.getTable().getValueAt(i, 7).toString()));
            totalHarga += Integer.parseInt(subtotal);
            String selisih = String.valueOf(Integer.parseInt(qty)*Integer.parseInt(harga) - Integer.parseInt(subtotal));
            ResultSet jenisObat = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat = '" + kodeObat
                    + "' AND satuan  = '" + satuan + "'");
            jenisObat.next();
            if (qtyKembali.equals("0")) {
                continue;
            }
            int qtyFix = Integer.parseInt(qty) - Integer.parseInt(qtyKembali);
            String qtyTotal = String.valueOf(qtyFix * jenisObat.getInt("total"));
            String idSatuan = jenisObat.getString("id_satuan");
            String[] field = { "harga", "qty", "subtotal" };
            String[] value = { harga, qtyTotal, subtotal };
            String kondisi = "kode_obat = '" + kodeObat + "' AND kode_transaksi = '" + kodeTransaksi
                    + "' AND id_satuan = " + idSatuan + " AND no_batch = '" + noBatch + "'";
            System.out.println("qty total "+qtyTotal+" qty fix "+qty + "qty kmbali"+qtyKembali+"query"+kondisi);

            // kembalikan stok ketika kondisi baik
             String[] fieldReturnDetail = { "kode_return_penjualan","kode_obat","qty_sebelum_retur","qty_retur","selisih","harga","subtotal"	
 };
             String[] valueReturnDetail = { kodeReturn, kodeObat, qty,qtyKembali,selisih,harga,subtotal};
        detailReturnPenjualanModel.insert(fieldReturnDetail, valueReturnDetail);
            //update qty detail penjualan
                            detailPenjualanModel.update(field, value, kondisi);

            if (kondisiObat.equals("Baik")) {
                continue;
            }
        }

        
        //update transaksi penjualan
        String[] field = { "kembalian", "total_harga", "pembayaran" };
        String[] value = { kembalian, String.valueOf(totalHarga), pembayaran };
        String kondisi = "kode_transaksi = '" + kodeTransaksi + "'";
        transaksiPenjualanModel.update(field, value, kondisi);



        



        reset();
        Notification.showInfo("Berhasil melakukan pengembalian", view);
    } catch (Exception e) {
        System.out.println(e.getMessage());
       Notification.showInfo( "Gagal melakukan return",view);
      }
    }
    
    
    private void reset() {
        DefaultTableModel tablemodel = (DefaultTableModel) view.getTable().getModel();
        tablemodel.setRowCount(0);
        view.getKode_transaksi().setText("-");
        view.getNamaKasir().setText("-");
        view.getPembayaran().setText("-");
        view.getKembalian().setText("-");
        view.getTotalHarga().setText("-");
        view.getTanggal().setText("-");
        view.getAlasan().setText("-");
    }

   @Override
    public Component getView() {
        return view;
    }


    private void event() {
        view.getKodeTransaksi().addActionListener(e->cariTransaksi());
                view.getBtnCari().addActionListener(e -> cariTransaksi());
        view.getBtnSimpan().addActionListener(e -> simpanReturn());
        
        KondisiEditor kondisiEditor = new KondisiEditor();
        KondisiRenderer kondisiRenderer = new KondisiRenderer();
        view.getTable().getColumnModel().getColumn(8).setCellEditor(kondisiEditor);
        view.getTable().getColumnModel().getColumn(8).setCellRenderer(kondisiRenderer);
        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                reset();
            }
        public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
        }
        public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
        }});
    }
}




class KondisiEditor extends DefaultCellEditor {
    private JComboBox input;
    public KondisiEditor() {
       super(new JCheckBox());
       input = new JComboBox(new Object[] { "Baik", "Rusak", "Kadaluarsa" });
        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped(); 
            }
        });
         ((JComponent)input.getEditor().getEditorComponent()).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
         super.getTableCellEditorComponent(table, value, isSelected, row, column); 
         return input;
    }

    @Override
    public Object getCellEditorValue() {
        return input.getSelectedItem();
    }   
}

class KondisiRenderer extends DefaultTableCellRenderer {
     private JComboBox input;

     public KondisiRenderer() {
         input = new JComboBox(new Object[] { "Baik", "Rusak", "Kadaluarsa" });
         ((JComponent)input.getEditor().getEditorComponent()).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
     }
    
      @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
            input.setSelectedItem(value);
        } else {
             input.setSelectedItem("Baik");
        }

        return input;
    }
    
  
}