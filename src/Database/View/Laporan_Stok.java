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
public class Laporan_Stok {
      public static void create(){
        String sql = "CREATE  VIEW `laporan_stok`  AS SELECT `detail_penjualan`.`kode_obat` AS `kode_obat`"
                + ", `obat`.`nama_obat` AS `nama_obat`, `detail_penjualan`.`qty` AS `obat_terjual`"
                + ", `transaksi_penjualan`.`tanggal_transaksi` AS `tanggal_transaksi` FROM ((`detail_penjualan` "
                + "join `transaksi_penjualan` on((`detail_penjualan`.`kode_transaksi` = `transaksi_penjualan`.`kode_transaksi`)))"
                + " join `obat` on((`detail_penjualan`.`kode_obat` = `obat`.`kode_obat`)))";
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
