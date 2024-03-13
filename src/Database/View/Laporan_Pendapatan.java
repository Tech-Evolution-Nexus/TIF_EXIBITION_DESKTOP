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
public class Laporan_Pendapatan {
      public static void create() throws SQLException{
        String sql = "CREATE  VIEW `laporan_pendapatan`  AS SELECT sum(`transaksi_penjualan`.`total_harga`) AS `pendapatan`"
                + ", date_format(`transaksi_penjualan`.`tanggal_transaksi`,'%Y-%m') AS `bulan_tahun` "
                + "FROM `transaksi_penjualan` GROUP BY date_format(`transaksi_penjualan`.`tanggal_transaksi`,'%Y-%m')";
        //run sql
        DB.query2(sql);
    } 

}
