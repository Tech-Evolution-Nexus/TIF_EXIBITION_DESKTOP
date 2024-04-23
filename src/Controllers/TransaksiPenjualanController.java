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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;

import javax.swing.table.DefaultTableModel;

import App.Model.KategoriModel;
import App.Model.ObatModel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class TransaksiPenjualanController  extends Controller{

   
    private PenjualanView view = new PenjualanView();
    private ObatModel obatModel= new ObatModel();
  

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
         view.getCariDialogForm().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                 String kunci = view.getCariDialogForm().getText();
                 cariData(kunci);
            }
        });
    }

    
    private void cariData(String kunci) {
       try {
         DefaultTableModel model = (DefaultTableModel) view.getTableCari().getModel();
         System.out.println("SELECT * FROM `data_jenis_penjualan` WHERE nama_obat like '%"+kunci+"%' OR kode_obat  = '%"+kunci+"%'");
        ResultSet datas = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE nama_obat like '%"+kunci+"%' OR kode_obat  = '%"+kunci+"%'");
        model.setRowCount(0);
        while (datas.next()) {
            ResultSet obat = obatModel.where("kode_obat","=",datas.getString("kode_obat")).get();
            obat.next();
            int stok = obat.getInt("jumlah_obat") / datas.getInt("total");
            System.out.println(obat.getInt("jumlah_obat") + " "+datas.getInt("total"));
            if (stok > 0) {
                Object[] data = {datas.getString("kode_obat"),datas.getString("nama_obat"),datas.getString("harga"),datas.getString("satuan"),stok};
                model.addRow(data);
            }
        }
       } catch (Exception e) {
            Notification.showError("Terjadi kesalahan dengan sistem", view.getTable());
       }
    }

    private void showForm() {
        view.getCariDialog().pack();
        view.getCariDialog().setLocationRelativeTo(null);
        view.getCariDialog().setVisible(true);
    }

    

    @Override
    public Component getView() {
        return view;
    }
}
