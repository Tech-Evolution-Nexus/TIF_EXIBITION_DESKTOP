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
public class Data_Jenis_Penjualan {
      public static void create(){
        String sql = "CREATE VIEW `data_jenis_penjualan`  AS SELECT `obat`.`kode_obat` AS `kode_obat`"
                + ", `obat`.`nama_obat` AS `nama_obat`, `bentuk_sediaan_obat`.`nama_bentuk_sediaan` AS `satuan`"
                + ", `jenis_penjualan`.`total` AS `total`, `jenis_penjualan`.`harga` AS `harga` "
                + "FROM ((`jenis_penjualan` join `bentuk_sediaan_obat` on((`jenis_penjualan`.`id_bentuk_sediaan` = `bentuk_sediaan_obat`.`id`))) "
                + "join `obat` on((`jenis_penjualan`.`kode_obat` = `obat`.`kode_obat`)))";
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
