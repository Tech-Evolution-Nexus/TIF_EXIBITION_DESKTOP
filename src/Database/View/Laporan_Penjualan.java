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
public class Laporan_Penjualan {
      public static void create() throws SQLException{
        String sql = "CREATE  VIEW `laporan_penjualan`  AS SELECT `users`.`nama` AS `nama_pengguna`"
                + ", `transaksi_penjualan`.`kode_transaksi` AS `kode_transaksi`, sum(`detail_penjualan`.`qty`) AS `total_obat`"
                + ", `transaksi_penjualan`.`total_harga` AS `total_harga`, `transaksi_penjualan`.`pembayaran` AS `pembayaran`"
                + ", `transaksi_penjualan`.`kembalian` AS `kembalian`, `transaksi_penjualan`.`tanggal_transaksi` AS `tanggal_transaksi`"
                + " FROM ((`transaksi_penjualan` join `users` on((`transaksi_penjualan`.`id_user` = `users`.`id`))) join `detail_penjualan`"
                + " on((`transaksi_penjualan`.`kode_transaksi` = `detail_penjualan`.`kode_transaksi`))) GROUP BY `detail_penjualan`.`kode_transaksi`";
        //run sql
        DB.query2(sql);
    } 
    
 
}
