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
public class PrinterView {
      public static void create(){
        String sql = "CREATE VIEW `printerview`  AS SELECT `transaksi_penjualan`.`kode_transaksi` AS `kode_transaksi`"
                + ", `users`.`username` AS `username`, `transaksi_penjualan`.`tanggal_transaksi` AS `tanggal_transaksi`"
                + ", `obat`.`nama_obat` AS `nama_obat`, `detail_penjualan`.`qty` AS `qty`, `detail_penjualan`.`harga` AS `harga`"
                + ", `detail_penjualan`.`subtotal` AS `subtotal`, `transaksi_penjualan`.`total_harga` AS `total_harga`"
                + ", `transaksi_penjualan`.`pembayaran` AS `pembayaran`, `transaksi_penjualan`.`kembalian` AS `kembalian`"
                + " FROM (((`transaksi_penjualan` join `detail_penjualan` on((`detail_penjualan`.`kode_transaksi` = `transaksi_penjualan`.`kode_transaksi`)))"
                + " join `users` on((`users`.`id` = `transaksi_penjualan`.`id_user`))) join `obat` on((`obat`.`kode_obat` = `detail_penjualan`.`kode_obat`)))";
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
