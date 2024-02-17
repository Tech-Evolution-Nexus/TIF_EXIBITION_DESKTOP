/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.View;

import Config.DB;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Laporan_Pembelian {
      public static void create(){
        String sql = "CREATE  VIEW `laporan_pembelian`  AS SELECT `transaksi_pembelian`.`kode_transaksi` AS `kode_transaksi`"
                + ", `supplier`.`nama_suplier` AS `nama_suplier`, sum(`detail_pembelian`.`qty`) AS `jumlah_obat`, `transaksi_pembelian`.`total_harga` AS `total_harga`"
                + ", `transaksi_pembelian`.`tanggal_transaksi` AS `tanggal_transaksi` FROM ((`transaksi_pembelian` "
                + "join `supplier` on((`transaksi_pembelian`.`kode_suplier` = `supplier`.`kode_suplier`)))"
                + " join `detail_pembelian` on((`transaksi_pembelian`.`kode_transaksi` = `detail_pembelian`.`kode_transaksi`)))"
                + " GROUP BY `transaksi_pembelian`.`kode_transaksi`  ";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop(){
        String sql = "DROP TABLE IF EXISTS`users` CASCADE";
        //run sql
        DB.query2(sql);
    }
}
