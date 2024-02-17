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
public class Data_Stok_Obat {
      public static void create(){
        String sql = "CREATE VIEW `data_stok_obat`  AS SELECT `stok_obat`.`id` AS `id`, `obat`.`kode_obat` AS `kode_obat`"
                + ", `obat`.`nama_obat` AS `nama_obat`, `stok_obat`.`jumlah_obat` AS `jumlah_obat`"
                + ", `stok_obat`.`harga_beli` AS `harga_beli`, `stok_obat`.`tanggal_kadaluarsa` AS `tanggal_kadaluarsa`"
                + ", `stok_obat`.`tanggal_masuk` AS `tanggal_masuk`, (case when (cast(`stok_obat`.`tanggal_kadaluarsa` as date) > now())"
                + " then 0 else 1 end) AS `status_kadaluarsa` FROM ((`stok_obat` join `supplier` on((`stok_obat`.`kode_suplier` = `supplier`.`kode_suplier`)))"
                + " join `obat` on((`stok_obat`.`kode_obat` = `obat`.`kode_obat`))) ";
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
