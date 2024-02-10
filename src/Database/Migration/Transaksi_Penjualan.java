/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Migration;


import Config.DB;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Transaksi_Penjualan {
    
    public static void migration(){
        String sql = "CREATE TABLE `transaksi_penjualan` (\n" +
                    "  `kode_transaksi` char(17)  NOT NULL PRIMARY KEY,\n" +
                    "  `id_user` int NOT NULL,\n" +
                    "  `total_harga` int NOT NULL DEFAULT '0',\n" +
                    "  `pembayaran` int NOT NULL DEFAULT '0',\n" +
                    "  `kembalian` int NOT NULL DEFAULT '0',\n" +
                    "  `tanggal_transaksi` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,\n" +
                    "FOREIGN KEY (id_user) REFERENCES users(id)"+
                    ")";
        //run sql        
        drop();
        DB.query2(sql);
    } 
    
    public static void drop(){
        String sql = "DROP TABLE IF EXISTS`transaksi_penjualan` CASCADE";
        //run sql
        DB.query2("SET FOREIGN_KEY_CHECKS = 0");
        DB.query2(sql);
        DB.query2("SET FOREIGN_KEY_CHECKS = 1");
    }
}
