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
public class Jenis_Penjualan {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `jenis_penjualan` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `kode_obat` char(14) NOT NULL,\n" +
                    "  `total` int NOT NULL DEFAULT '0',\n" +
                    "  `harga` int NOT NULL DEFAULT '0',\n" +
                    "  `id_bentuk_sediaan` int NOT NULL,\n" +
                    "FOREIGN KEY (id_bentuk_sediaan) REFERENCES bentuk_sediaan_obat(id),"+
                    "FOREIGN KEY (kode_obat) REFERENCES obat(kode_obat)"+
                    ")";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS `jenis_penjualan` CASCADE";
        //run sql
        DB.query2(sql);
    }
}
