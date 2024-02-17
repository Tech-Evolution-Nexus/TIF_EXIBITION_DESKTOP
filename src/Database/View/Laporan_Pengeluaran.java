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
public class Laporan_Pengeluaran {
      public static void create(){
        String sql = "CREATE  VIEW `laporan_pengeluaran`  AS SELECT `pengeluaran`.`id` AS `id`, `users`.`nama` AS `nama`"
                + ", `pengeluaran`.`keterangan` AS `keterangan`, `pengeluaran`.`total_pengeluaran` AS `jumlah_pengeluaran`"
                + ", `pengeluaran`.`tanggal_pengeluaran` AS `tanggal_pengeluaran` FROM (`pengeluaran` join `users`"
                + " on((`pengeluaran`.`id_user` = `users`.`id`)))";
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
