
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import App.Model.DetailObatModel;
import Config.DB;
import Core.Controller;
import Helper.Notification;
import View.PenjualanView;
import View.Auth.login;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import App.Model.DetailPenjualanModel;
import App.Model.ObatModel;
import App.Model.TransaksiPenjualanModel;
import Components.btnAction.obatAction.ActionEvent;
import Components.btnAction.obatAction.BtnAction;
import Components.btnAction.obatAction.BtnEditor;
import Helper.Auth;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
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
    private ArrayList<String[]> tmpData = new ArrayList<>();

    public TransaksiPenjualanController() {
        event();
    }
    

    
    private void cariData() {
        try {
            String kunci = view.getCariObat().getText();
            DefaultTableModel model = (DefaultTableModel) view.getTableCari().getModel();
           
            ResultSet datas = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE nama_obat like '%" + kunci
                    + "%' OR kode_obat  like '%" + kunci + "%'");
            model.setRowCount(0);
            while (datas.next()) {
                ResultSet obat = obatModel.where("kode_obat", "=", datas.getString("kode_obat")).get();
                obat.next();
                int stok = (int) Math.floor(obat.getInt("jumlah_obat") / datas.getInt("total"));
                String namaObat  = datas.getString("nama_obat");
                String satuan  = datas.getString("satuan");
                
                 int rowCount = view.getTable().getRowCount();
                //cek tabel data
                for (int row = 0; row < rowCount; row++) {
                        String kdObat = view.getTable().getValueAt(row, 0).toString();
                        String namaObatTable = view.getTable().getValueAt(row, 1).toString();
                        String satuanTable = view.getTable().getValueAt(row, 2).toString();
                        int stokDiTable = Integer.parseInt(view.getTable().getValueAt(row, 4).toString());
                        if (namaObatTable.equals(namaObat)) {
                            ResultSet dataJenis = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat ='" + kdObat
                                                + "' AND satuan  = '" + satuanTable + "'");
                            dataJenis.next();
                            stokDiTable =  stokDiTable*dataJenis.getInt("total");
                            stok = (obat.getInt("jumlah_obat")- stokDiTable)/ datas.getInt("total");
                            break;
                        }
                }
                
                if (stok > 0) {
                    Object[] data = { datas.getString("kode_obat"), datas.getString("nama_obat"),
                            datas.getString("harga"), datas.getString("satuan"), stok };
                    model.addRow(data);
                }
            }
            showForm();
        } catch (Exception e) {
            Notification.showError("Terjadi kesalahan dengan sistem", view.getTable());
        }
    }


   
    
    private void selectObat(){
        //ambil data obat
       int rowSelect = view.getTableCari().getSelectedRow();
       int columnCount = view.getTableCari().getColumnCount();
       String[] arrData = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String value = view.getTableCari().getValueAt(rowSelect, i).toString();
            arrData[i] = value;
        }
        
        //arrData index info
        //0 kode obat | 1 nama obat | 2 harga | 3 satuan | 4 stok tersedia
        //memasukkan ke array list sementara
        tmpData.add(arrData);
        view.getCariDialog().dispose();
        String namaObat = arrData[1];
        view.getNamaObat().setText(namaObat);
        view.getQty().requestFocus();
    }

    private void tambahDataTable(){
        try {
        if (tmpData.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Silahkan pilih obat terlebih dahulu");
            return;
        }
        DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
        String kdObat = tmpData.get(0)[0].toString();
        String namaObat = tmpData.get(0)[1].toString();
        int harga = Integer.parseInt(tmpData.get(0)[2].toString());
        String satuan = (tmpData.get(0)[3].toString());
        int stokTersedia = Integer.parseInt(tmpData.get(0)[4].toString());
        int qty = Integer.parseInt(view.getQty().getText());
        int totalHarga = harga * qty;
        
            if (qty <= 0) {
                JOptionPane.showMessageDialog(view, "Silahkan masukkan jumlah yang dibeli");
                return;
            }
        //tambahkan ke table
        int rowCount = view.getTable().getRowCount();
        boolean dataDitemukan = false;
        //cek tabel data
        for (int row = 0; row < rowCount; row++) {
                String namaObatTable = view.getTable().getValueAt(row, 1).toString();
                String satuanTable = view.getTable().getValueAt(row, 2).toString();
                //jika ditemukan kode obat dan satuan yg sama maka tambahkan stok dan update total harga
                if (namaObatTable.equals(namaObat) && satuanTable.equals(satuan) && !isEdit) {
                    qty += Integer.parseInt(view.getTable().getValueAt(row, 4).toString()); 
                    totalHarga = harga * qty;
                    
                    view.getTable().setValueAt(qty,row, 4);
                    view.getTable().setValueAt(Helper.Currency.format(totalHarga),row, 5);
                    break;
                }else if (namaObatTable.equals(namaObat) && satuanTable.equals(satuan) && isEdit) {
                    dataDitemukan=true;
                    view.getTable().setValueAt(qty,row, 4);
                    view.getTable().setValueAt(Helper.Currency.format(totalHarga),row, 5);
                }
        }
        //3 harga | 4 qty | 5 total harga
        if (!dataDitemukan) {
            String hargaFormat = Helper.Currency.format(harga);
            Object[] data= {kdObat,namaObat,satuan,hargaFormat,qty,Helper.Currency.format(totalHarga)};
            model.addRow(data);
        }
        resetForm();
        tmpData.clear();
        isEdit=false;
        hitungTotal();
        } catch (Exception e) {
            System.out.println("error di select data | error "+e.getMessage());
        }    
    }
    
  

    // simpan ke database
    private void simpanPenjualan() {
        try {
            if (pembayaran < hargaTotal) {
                Notification.showInfo(
                        "Pembayaran kurang \n Pembayaran : " + pembayaran + "\n Total Harga :" + hargaTotal,
                        view.getTable());
                return;
            }
            if (view.getTable().getRowCount() == 0) {
                Notification.showInfo("Silahkan masukkan barang yang akan di beli", view.getTable());
                return;
            }
            Auth user = new Auth();
            String kodeTrx = Helper.KodeGenerator.generateKodeTransaksi();
            
            String[] fieldTrx = { "kode_transaksi", "id_user", "total_harga", "pembayaran", "kembalian" };
            String[] valueTrx = { kodeTrx, user.getId(), String.valueOf(hargaTotal), String.valueOf(pembayaran),String.valueOf(kembalian) };
            transaksiPenjualanModel.insert(fieldTrx, valueTrx);
            for (int i = 0; i < view.getTable().getRowCount(); i++) {
                String kodeObat = view.getTable().getValueAt(i, 0).toString();
                String satuan = view.getTable().getValueAt(i, 2).toString();
                String harga = String.valueOf(Helper.Currency.deformat(view.getTable().getValueAt(i, 3).toString()));
                String qty = view.getTable().getValueAt(i, 4).toString();
                String subtotal = String.valueOf(Helper.Currency.deformat(view.getTable().getValueAt(i, 5).toString()));
                
                ResultSet jenisObat = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat = '" + kodeObat
                    + "' AND satuan  = '" + satuan + "'");
                jenisObat.next();
                String qtyTotal = String.valueOf(Integer.parseInt(qty) * jenisObat.getInt("total"));
                String idSatuan = jenisObat.getString("id_satuan");
                String[] field = { "kode_transaksi", "kode_obat", "harga", "qty", "subtotal","id_satuan" };
                String[] value = { kodeTrx, kodeObat, harga, qtyTotal, subtotal ,idSatuan};
                detailPenjualanModel.insert(field, value);
                ResultSet detailObat = DB.query("SELECT * from data_stok_obat where kode_obat = '" + kodeObat + "' AND status_kadaluarsa = 0 order by tanggal_masuk asc");
                int qtySisa = Integer.parseInt(qtyTotal);

                //mengurangi qty dengan metode fifo 
                while (detailObat.next()) {

                    if (detailObat.getInt("jumlah_obat") >= qtySisa) {
                        DB.query2("UPDATE detail_obat set jumlah_obat = jumlah_obat - " + String.valueOf(qtySisa) + " where no_batch  = '" + detailObat.getString("id") + "'");
                        break;
                    } else {
                        qtySisa = qtySisa - detailObat.getInt("jumlah_obat");
                        DB.query2("UPDATE detail_obat set jumlah_obat = jumlah_obat-jumlah_obat   where no_batch  = '" + detailObat.getString("id") + "'");
                    };
                }
            }
            
             try {
                String sqlQuery = "SELECT * FROM printerview where kode_transaksi = '"+kodeTrx+"'";
                String path = "src/iReportdata/printpenjualan.jrxml";
                 JasperDesign jasperDesign = JRXmlLoader.load(path);

                // Membuat objek JRDesignQuery
                 JRDesignQuery newQuery = new JRDesignQuery();
                newQuery.setText(sqlQuery);

                // Mengaitkan JRDesignQuery dengan JasperDesign
                jasperDesign.setQuery(newQuery);

                // Langkah 3: Mengisi data ke laporan JasperReports
                 JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                 Map<String, Object> parameters = new HashMap<>();
                // Mengisi laporan dengan data dari database
                 JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, DB.getConnection());

                // Menampilkan laporan (opsional)
                 JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setVisible(true);
            } catch (JRException ex) {
                 Logger.getLogger(PenjualanView.class.getName()).log(Level.SEVERE, null, ex);
            }
//            Notification.showInfo("Transaksi Berhasil", view.getBaseLayer());
            resetForm();
            reset();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
    
    
    private void edit(int rowSelected){
        try {
        String kodeObat = view.getTable().getValueAt(rowSelected, 0).toString();
        String satuan = view.getTable().getValueAt(rowSelected, 2).toString();
        String stokTable = view.getTable().getValueAt(rowSelected, 4).toString();
        
        ResultSet dataJenis = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat ='" + kodeObat
                                                + "' AND satuan  = '" + satuan + "'");
        dataJenis.next();
        
        ResultSet obat = obatModel.where("kode_obat", "=", kodeObat).get();
        obat.next();
        int stok = (int) Math.floor(obat.getInt("jumlah_obat") / dataJenis.getInt("total"));
            int rowCount = view.getTable().getRowCount();
            //cek tabel data
            for (int row = 0; row < rowCount; row++) {
                if (row == rowSelected) {
                    continue;
                }
                    String kdObat = view.getTable().getValueAt(row, 0).toString();
                    String satuanTable = view.getTable().getValueAt(row, 2).toString();
                    int stokDiTable = Integer.parseInt(view.getTable().getValueAt(row, 4).toString());
                    if (kdObat.equals(kodeObat)) {
                        ResultSet dj = DB.query("SELECT * FROM `data_jenis_penjualan` WHERE kode_obat ='" + kdObat
                                            + "' AND satuan  = '" + satuanTable + "'");
                        dj.next();
                        stokDiTable =  stokDiTable*dj.getInt("total");
                        stok = (obat.getInt("jumlah_obat")- stokDiTable)/ dataJenis.getInt("total");
                        System.out.println("stok sekarang "+stok + " "+obat.getString("jumlah_obat")+" "+ stokDiTable+" "+ dataJenis.getInt("total"));
                        break;
                    }
            }
       String[]  data = { dataJenis.getString("kode_obat"), dataJenis.getString("nama_obat"),
                            dataJenis.getString("harga"), dataJenis.getString("satuan"), String.valueOf(stok) };
       tmpData.clear();
       view.getNamaObat().setText(dataJenis.getString("nama_obat"));
       view.getQty().setText(stokTable);
       tmpData.add(data);
       
       isEdit = true;
        } catch (Exception e) {
            System.out.println("terjadi error dibagian edit penjualan | error "+e.getMessage());
        }
    }
    
    
    private void delete(int row){
        DefaultTableModel modal = (DefaultTableModel) view.getTable().getModel();
        modal.removeRow(row);
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
        tmpData.clear();
    }
      //reset form
    private void resetForm() {
        view.getNamaObat().setText("");
        view.getQty().setText("0");
        tmpData.clear();

    }
     private void showForm() {
        view.getCariDialog().pack();
        view.getCariDialog().setLocationRelativeTo(null);
        view.getCariDialog().setVisible(true);
    }
     
     private void hitungTotal(){
          int rowCount = view.getTable().getRowCount();
        //cek tabel data
        hargaTotal = 0;
        for (int row = 0; row < rowCount; row++) {
             hargaTotal += Helper.Currency.deformat(view.getTable().getValueAt(row, 5).toString());
        }
        view.getTotalHarga().setText(Helper.Currency.format(hargaTotal));
     }

    @Override
    public Component getView() {
        return view;
    }
    
    
    
    private void event(){
        
        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                reset();
            }

            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }

            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        view.getBtnCari().addActionListener(e ->cariData());
        
        view.getCariObat().addActionListener(e -> cariData());
        view.getQty().addActionListener(e -> tambahDataTable());
        view.getBtnBayar().addActionListener(e -> simpanPenjualan());
        view.getBayar().addActionListener(e -> simpanPenjualan());
       
        view.getQty().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String value = view.getQty().getText();
                value =   String.valueOf(Helper.Validasi.getNumeric(value));

                if (value.equals("") || Integer.parseInt(value) < 0) {
                    value = "0";
                }
                
                if (!tmpData.isEmpty()) {
                    int stokMaksimal = Integer.parseInt(tmpData.get(0)[4]);
                    if (Integer.parseInt(value) > stokMaksimal) {
                        JOptionPane.showMessageDialog(view, "Qty melebihi stok yang tersedia");
                        value = String.valueOf(stokMaksimal);
                    }
                }
                
                view.getQty().setText(value);

            }
        });
        view.getBayar().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                int bayar = (int)( Helper.Validasi.getNumeric(view.getBayar().getText()));
                if ( (bayar)<0) {
                    bayar = 0;
                }
                
                kembalian = (bayar) - hargaTotal;
                pembayaran = (bayar);
                view.getKembalian().setText(Helper.Currency.format(kembalian));
                view.getBayar().setText(Helper.Currency.format(bayar));
               

            }
        });
        view.getTableCari().addMouseListener(new MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    selectObat();
                }
            }
        });
        view.getAddList().addActionListener(e ->tambahDataTable());
        view.getBatal().addActionListener(e ->resetForm());
        ActionEvent event = new ActionEvent() {

            @Override
            public void onEdit(int row) {
                edit(row);
            }

            @Override
            public void onDelete(int row) {
//                listData.remove(row);
                delete(row);
            }
        };

        view.getTable().getColumnModel().getColumn(6).setCellRenderer(new BtnAction(false));
        view.getTable().getColumnModel().getColumn(6).setCellEditor(new BtnEditor(event,false));

    }
}
