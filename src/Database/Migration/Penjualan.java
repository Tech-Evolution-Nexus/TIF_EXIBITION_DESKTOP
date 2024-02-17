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
public class Penjualan {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `penjualan` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `kode_obat` char(14) NOT NULL,\n" +
                    "  `kode_suplier` char(16) NOT NULL,\n" +
                    "FOREIGN KEY (kode_obat) REFERENCES obat(kode_obat),"+
                    "FOREIGN KEY (kode_suplier) REFERENCES supplier(kode_suplier)"+
                    ")";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS `penjualan` ";
        //run sql
        DB.query2(sql);
    }
    
}
