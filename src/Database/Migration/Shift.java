/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Migration;

import Config.DB;
import java.sql.SQLException;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Shift {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `shift` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `id_user` int NOT NULL,\n" +
                    "  `saldo_awal_kas` int NOT NULL,\n" +
                    "  `saldo_akhir_kas` int NOT NULL DEFAULT '0',\n" +
                    "  `waktu_buka` time NOT NULL,\n" +
                    "  `waktu_tutup` time DEFAULT NULL,\n" +
                    "  `total_penjualan` int NOT NULL DEFAULT '0',\n" +
                    "  `total_pembayaran` int NOT NULL DEFAULT '0',\n" +
                    "  `tanggal_dibuat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "FOREIGN KEY (id_user) REFERENCES users(id)"+
                    ") ";
        //run sql
        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS `shift` ";
        //run sql
        DB.query2(sql);
    }
    
}
