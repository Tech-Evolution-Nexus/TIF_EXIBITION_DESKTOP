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

import javax.swing.JOptionPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import App.Model.JenisPenjualanModel;
import App.Model.KategoriModel;
import App.Model.ObatModel;
import App.Model.SatuanModel;
import Components.btnAction.obatAction.ActionEvent;
import Components.btnAction.obatAction.BtnAction;
import Components.btnAction.obatAction.BtnEditor;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class ObatController extends Controller {

    int status = 1;
    private int satuanIndexEdit = -1;
    private String idEdit = "";
    private ArrayList<Object[]> obatList = new ArrayList<Object[]>();
    private ObatView view = new ObatView();
    private ObatModel model = new ObatModel();
    private JenisPenjualanModel jenisPenjualanModel = new JenisPenjualanModel();
    private KategoriModel kategoriModel = new KategoriModel();
    private SatuanModel satuanModel = new SatuanModel();

    public ObatController() {
        view.getSearch().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                tampilData(true);
            }
        });
        tampilData(false);
        view.getBtnUbah().addActionListener(e -> editData());
        view.getBtnHapus().addActionListener(e -> hapusData());
        view.getBtnTambah().addActionListener(e -> tambahData());
        view.getBtnSimpan().addActionListener(e -> simpanData());
        view.getBtnSatuan().addActionListener(e -> listSatuan());
        view.getBtnDetail().addActionListener(e -> detail());
        view.getBtnExport().addActionListener(e -> exportbarcode());
        view.getMinStok().addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent evt) {
                char character = evt.getKeyChar();
                if (!Character.isDigit(character)||Character.isWhitespace(character)) {
                    evt.consume();
                }
            }
        });
        view.getTotal().addKeyListener(new KeyAdapter() {

            public void keyTyped(KeyEvent evt) {
                char character = evt.getKeyChar();
                if (!Character.isDigit(character)||Character.isWhitespace(character)) {
                    evt.consume();
                }
            }
        });
        view.getBaseLayer().addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                tampilData(false);
            }

            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }

            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        ActionEvent event = new ActionEvent() {
            @Override
            public void onEdit(int row) {
                satuanIndexEdit = row;
                String satuan = view.getListSatuan().getValueAt(row, 0).toString();
                String total = view.getListSatuan().getValueAt(row, 1).toString();
                String marginHarga = view.getListSatuan().getValueAt(row, 4).toString();
                String marginType = view.getListSatuan().getValueAt(row, 3).toString();
                view.getSatuan().setSelectedItem(satuan);
                view.getTotal().setText(total);
                view.getMargin().setText(marginHarga);
                view.getMarginStatus().setSelectedItem(marginType);
            }

            @Override
            public void onDelete(int row) {
                ((DefaultTableModel) view.getListSatuan().getModel()).removeRow(row);
            }
        };

        view.getListSatuan().getColumnModel().getColumn(5).setCellRenderer(new BtnAction(false));
        view.getListSatuan().getColumnModel().getColumn(5).setCellEditor(new BtnEditor(event, false));
        view.getListSatuan().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] selectedRows = view.getListSatuan().getSelectedRows();
                    int[] selectedColumns = view.getListSatuan().getSelectedColumns();
                    for (int row : selectedRows) {
                        for (int col : selectedColumns) {
                            view.getListSatuan().changeSelection(row, col, false, false);
                        }
                    }
                }
            }
        });

    }

    public void tampilData(boolean cari) {
        try {

            String kunci = view.getSearch().getText();
            ResultSet data = DB.query("SELECT * FROM data_obat   order by tanggal_dibuat desc,jumlah_obat desc");
            if (cari) {
                data = DB.query("SELECT * FROM data_obat where data_obat.nama_obat like '%" + kunci + "%'  order by tanggal_dibuat desc,jumlah_obat desc");

            }
            DefaultTableModel tabelData = (DefaultTableModel) view.getTable().getModel();

            tabelData.setRowCount(0);
            int no = 1;
            obatList.clear();
            while (data.next()) {
                Object[] dataArray = {no, data.getString("kode_obat"),
                    data.getString("nama_obat"),
                    data.getString("jumlah_obat"),
                    data.getString("satuan"),
                    data.getString("nama_kategori"),
                    data.getString("kandungan"),
                    Currency.format(data.getInt("harga")),
                    data.getInt("min_stok")
                };
                tabelData.addRow(dataArray);
                obatList.add(dataArray);
                no++;
            }

            ResultSet satuan = satuanModel.all();
            view.getSatuan().removeAllItems();
            while (satuan.next()) {
                view.getSatuan().addItem(satuan.getString("nama_satuan"));
            }
        } catch (Exception e) {
            System.out.println("error dari method tampil data " + e.getMessage());
        }
    }

    public void tambahData() {
        try {
            resetSatuanForm();
            clearDialog();
            view.getForm().setTitle("Tambah Obat");
            ResultSet kategoriData = DB.query("SELECT * from kategori");
//             addSatuan(null, 0, 1);
            view.getKategori().removeAllItems();
            while (kategoriData.next()) {
                view.getKategori().addItem(kategoriData.getString("nama_kategori"));
            }

            showForm();
        } catch (Exception e) {
            System.out.println(e);
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

            if (transaksiData != null) {
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
        String min_stok = view.getMinStok().getText();
        String kodeObat = idEdit.equals("") ? KodeGenerator.generateKodeObat() : idEdit;

        Set<String> uniqueBarangList = new HashSet<>();

        try {
            if (obatList.stream().anyMatch(satuan -> satuan[2].toString().trim().equalsIgnoreCase(namaObat.trim()) && !satuan[1].equals(idEdit))) {
                Notification.showInfo(Notification.DUPLICATE_DATA, view.getForm());
            } else if (namaObat.equals("") || kandungan.equals("") || kategori.equals("")) {
                Notification.showInfo(Notification.EMPTY_VALUE, view.getForm());
            } else {
                DefaultTableModel listSatuanModel = (DefaultTableModel) view.getListSatuan().getModel();
                if (listSatuanModel.getRowCount() == 0) {
                    Notification.showInfo("Silahkan pilih satuan obat terlebih dahulu", view.getForm());
                    return;
                }
                for (int i = 0; i < view.getListSatuan().getRowCount(); i++) {
                    String value = view.getListSatuan().getValueAt(i, 0).toString();
                    String value2 = view.getListSatuan().getValueAt(i, 1).toString();
                    if (!uniqueBarangList.add(value)) {
                        Notification.showInfo("Nilai duplikat dalam satuan: " + value, view.getForm());
                        return;
                    }
                    if (value2.equals("")) {
                        Notification.showInfo("Total Satuan Harap Diisi: " + value, view.getForm());
                        return;
                    }

                }
                String namaSatuan = view.getListSatuan().getValueAt(0, 0).toString();

                ResultSet dataKategori = kategoriModel.select("id").where("nama_kategori", "=", kategori).get();

                ResultSet dataSatuan = satuanModel.select("id").where("nama_satuan", "=", namaSatuan).get();
                dataKategori.next();
                dataSatuan.next();
                String idSatuanObat = dataSatuan.getString("id");
                String idKategori = dataKategori.getString("id");
                DB.query2("DELETE FROM jenis_penjualan where kode_obat = '" + kodeObat + "'");
                String[] fieldsObat = {"kode_obat", "nama_obat", "id_kategori", "id_satuan", "min_stok", "kandungan"};
                String[] valuesObat = {kodeObat, namaObat, idSatuanObat, idKategori, min_stok, kandungan};
                if (idEdit.equals("")) {
//                    System.out.println("sd");
                    model.insert(fieldsObat, valuesObat);

                } else {
                    model.update(fieldsObat, valuesObat, "kode_obat = '" + idEdit + "'");

                }
                for (int i = 0; i < view.getListSatuan().getRowCount(); i++) {
                    namaSatuan = view.getListSatuan().getValueAt(i, 0).toString();
                    String total = view.getListSatuan().getValueAt(i, 1).toString();
                    String marginType = view.getListSatuan().getValueAt(i, 3).toString();
                    String margin = view.getListSatuan().getValueAt(i, 4).toString();
                    String marginHarga = null;
                    String marginPersen = null;
                    if (marginType.equals("Margin Harga")) {
                        marginHarga = margin;
                    } else {
                        marginPersen = margin;
                    }
                    dataSatuan = satuanModel.select("id").where("nama_satuan", "=", namaSatuan).get();
                    dataSatuan.next();
                    String idSatuan = String.valueOf(dataSatuan.getInt("id"));
                    String[] column = {"kode_obat", "total", "margin_harga", "margin_persen", "id_satuan"};
                    String[] values = {kodeObat, total, marginHarga, marginPersen, idSatuan};
                    jenisPenjualanModel.insert(column, values);
                }
                // DB.query2("CALL simpanDataObat('" + kodeObat + "','" + namaObat + "','" + idSatuan + "','" + idKategori
                //         + "','" + kandungan + "')");

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
            resetSatuanForm();
            clearDialog();
            if (view.getTable().getSelectedRow() < 0) {
                Notification.showError(Notification.NO_DATA_SELECTED_INFO, view.getForm());
                return;
            }
            int row = view.getTable().getSelectedRow();
            String idObat = view.getTable().getValueAt(row, 1).toString();
            ResultSet obatData = DB.query("SELECT * from obat where kode_obat = '" + idObat + "'");
            if (obatData.next()) {

                String namaObat = obatData.getString("nama_obat");
                String kandungan = obatData.getString("kandungan");
                String minstok = obatData.getString("min_stok");

                view.getNamaObat().setText(namaObat);
                view.getKandungan().setText(kandungan);
                view.getMinStok().setText(minstok);

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
                ResultSet satuanData = DB.query("SELECT * from data_jenis_penjualan where kode_obat='" + idObat + "'");
                DefaultTableModel model = (DefaultTableModel) view.getListSatuan().getModel();
                model.setRowCount(0);
                String namaSatuanPertama = "";
                while (satuanData.next()) {
                    if (namaSatuanPertama.equals("")) {
                        namaSatuanPertama = satuanData.getString("satuan");
                    }
                    String margin = "";
                    String marginType = "";
                    if (satuanData.getString("margin_harga") == null) {
                        margin = satuanData.getString("margin_persen");
                        marginType = "Margin Persentase";
                    } else {
                        margin = satuanData.getString("margin_harga");
                        marginType = "Margin Harga";
                    }
                    Object[] data = {satuanData.getString("satuan"),
                        satuanData.getString("total"),
                        namaSatuanPertama,
                        marginType,
                        margin};
                    model.addRow(data);
                    // addSatuan(satuanData.getString("id_bentuk_sediaan"), satuanData.getInt("harga"), satuanData.getInt("total"));
                }

                showForm();
            } else {
                JOptionPane.showMessageDialog(null, "Tidak Ditemukan Kode Obat");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Sistem error " + e.getMessage());

        }
    }

    public void detail() {
        try {
            if (view.getTable().getSelectedRow() < 0) {
                return;
            }
            clearDialog();
            int row = view.getTable().getSelectedRow();
            String idObat = view.getTable().getValueAt(row, 1).toString();
            String namaObat = view.getTable().getValueAt(row, 2).toString();
            String kandungan = view.getTable().getValueAt(row, 6).toString();
            String kategori = view.getTable().getValueAt(row, 5).toString();
            String satuan = view.getTable().getValueAt(row, 4).toString();

            view.getDetailNama().setText(namaObat);
            view.getDetailKandungan().setText(kandungan);
            view.getDetailKategori().setText(kategori);
            view.getDetailSatuan().setText(satuan);
            view.getDetailKodeObat().setText(idObat);

            DefaultTableModel model = (DefaultTableModel) view.getSatuanTable().getModel();
            ResultSet satuanData = DB.query("SELECT * from data_jenis_penjualan where kode_obat='" + idObat + "'");
            model.setRowCount(0);
            while (satuanData.next()) {
                Object[] objectRow = {satuanData.getString("satuan"), satuanData.getInt("total"),
                    satuanData.getInt("harga")};
                model.addRow(objectRow);
            }

            model = (DefaultTableModel) view.getStokTable().getModel();
            ResultSet stokData = DB.query(
                    "SELECT * from detail_obat join supplier on detail_obat.kode_suplier = supplier.kode_suplier where kode_obat='"
                    + idObat + "' order by tanggal_masuk desc");
            model.setRowCount(0);
            while (stokData.next()) {
                Object[] objectRow = {stokData.getString("jumlah_obat"), stokData.getString("nama_suplier"),
                    FormatTanggal.formatDate(stokData.getDate("tanggal_kadaluarsa")),
                    FormatTanggal.formatDate(stokData.getDate("tanggal_masuk"))};
                model.addRow(objectRow);
            }

            view.getDetail().pack();
            view.getDetail().setLocationRelativeTo(null);
            view.getDetail().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view.getTable(), "Sistem error " + e.getMessage());

        }
    }

    private void listSatuan() {
        DefaultTableModel model = (DefaultTableModel) view.getListSatuan().getModel();
        if (model.getRowCount() == 4) {
            Notification.showInfo("Satuan Obat maksimal 4", view.getForm());
            return;
        }
        String namaSatuan = view.getSatuan().getSelectedItem().toString();
        String satuanTerkecil = model.getRowCount() == 0 ? namaSatuan
                : view.getListSatuan().getValueAt(0, 0).toString();
        String total = view.getTotal().getText();
        String margin = view.getMargin().getText();
        String marginType = view.getMarginStatus().getSelectedItem().toString();
        if (total.equals("")) {
            Notification.showInfo("Total wajib diisi ", view.getForm());
            return;
        }
        if (margin.equals("")) {
            Notification.showInfo("Margin pendapatan harus diisi ", view.getForm());
            return;
        }
        Object[] data = {namaSatuan, total, satuanTerkecil, marginType, margin};
        if (satuanIndexEdit == -1) {
            model.addRow(data);
        } else {
            for (int i = 0; i < data.length; i++) {
                model.setValueAt(data[i].toString(), satuanIndexEdit, i);
            }
            satuanIndexEdit = -1;
        }
        resetSatuanForm();

    }

    private void exportbarcode() {
        int row = view.getTable().getSelectedRow();

        if (row < 0) {
            try {
                // Compile JRXML file
                String sqlQuery = "Select * from obat";
                String pathi = "src/iReportdata/exportbarcode.jrxml";
                JasperDesign jasperDesign = JRXmlLoader.load(pathi);

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy_HH-mm-ss");
                String tanggalWaktu = dateFormat.format(new Date());

                // Nama file dengan format br-[tanggal dan waktu].pdf
                String namaFile = "br-" + tanggalWaktu + ".pdf";

                // Path untuk menyimpan file di document/exportbarcode/
                String path = System.getProperty("user.home") + "/Documents/exportbarcode/";
                // Pastikan direktori sudah ada, jika tidak, buat direktori baru
                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Ekspor ke PDF
                JasperExportManager.exportReportToPdfFile(jasperPrint, path + namaFile);
                JOptionPane.showMessageDialog(null, "Data berhasil D Simpan Di " + path);
            } catch (JRException ex) {
                Logger.getLogger(ObatController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String idObat = view.getTable().getValueAt(row, 1).toString();
                // Compile JRXML file
                String sqlQuery = "Select * from obat where kode_obat = '" + idObat + "'";
                String pathi = "src/iReportdata/exportbarcode.jrxml";
                JasperDesign jasperDesign = JRXmlLoader.load(pathi);

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy_HH-mm-ss");
                String tanggalWaktu = dateFormat.format(new Date());

                // Nama file dengan format br-[tanggal dan waktu].pdf
                String namaFile = "satu br-" + tanggalWaktu + ".pdf";

                // Path untuk menyimpan file di document/exportbarcode/
                String path = System.getProperty("user.home") + "/Documents/exportbarcode/";

                // Pastikan direktori sudah ada, jika tidak, buat direktori baru
                File directory = new File(path);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Ekspor ke PDF
                JasperExportManager.exportReportToPdfFile(jasperPrint, path + namaFile);
                JOptionPane.showMessageDialog(null, "Data berhasil D Simpan Di " + path);
            } catch (JRException ex) {
                Logger.getLogger(ObatController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void resetSatuanForm() {
        view.getSatuan().setSelectedIndex(0);
        view.getSatuan().setSelectedItem("");
        view.getTotal().setText("");
        view.getMargin().setText("");
    }

    public void clearDialog() {
        view.getKandungan().setText("");
        view.getNamaObat().setText("");
        view.getMinStok().setText("");
        DefaultTableModel listSatuan = (DefaultTableModel) view.getListSatuan().getModel();
        listSatuan.setRowCount(0);

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
