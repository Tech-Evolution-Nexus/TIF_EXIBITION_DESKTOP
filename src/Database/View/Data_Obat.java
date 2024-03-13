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
public class Data_Obat {
      public static void create() throws SQLException{
        String sql = "CREATE  VIEW `data_obat`  AS SELECT `obat`.`kode_obat` AS `kode_obat`"
                + ", `obat`.`nama_obat` AS `nama_obat`, `obat`.`jumlah_obat` AS `jumlah_obat`"
                + ", `kategori`.`nama_kategori` AS `nama_kategori`, `bentuk_sediaan_obat`.`nama_bentuk_sediaan` AS `satuan`"
                + ", `obat`.`tanggal_dibuat` AS `tanggal_dibuat`, `obat`.`aturan_pakai` AS `aturan_pakai`"
                + " FROM ((`obat` join `kategori` on((`obat`.`id_kategori` = `kategori`.`id`))) "
                + "join `bentuk_sediaan_obat` on((`obat`.`id_bentuk_sediaan` = `bentuk_sediaan_obat`.`id`))) ";
        //run sql
        DB.query2(sql);
    } 
    
   
}
