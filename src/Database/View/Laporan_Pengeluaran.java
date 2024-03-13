/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.View;

import Config.DB;
import java.sql.SQLException;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Laporan_Pengeluaran {
      public static void create() throws SQLException{
        String sql = "CREATE  VIEW `laporan_pengeluaran`  AS SELECT `pengeluaran`.`id` AS `id`, `users`.`nama` AS `nama`"
                + ", `pengeluaran`.`keterangan` AS `keterangan`, `pengeluaran`.`total_pengeluaran` AS `jumlah_pengeluaran`"
                + ", `pengeluaran`.`tanggal_pengeluaran` AS `tanggal_pengeluaran` FROM (`pengeluaran` join `users`"
                + " on((`pengeluaran`.`id_user` = `users`.`id`)))";
        //run sql
        DB.query2(sql);
    } 
 
}
