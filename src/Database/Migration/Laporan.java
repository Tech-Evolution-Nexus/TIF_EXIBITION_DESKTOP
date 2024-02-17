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
public class Laporan {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `laporan` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `pendapatan` int NOT NULL DEFAULT '0',\n" +
                    "  `pengeluaran` int NOT NULL DEFAULT '0',\n" +
                    "  `laba_bersih` int NOT NULL DEFAULT '0',\n" +
                    "  `rugi_bersih` int NOT NULL DEFAULT '0',\n" +
                    "  `bulan_tahun` char(7) NOT NULL,\n" +
                    "  `status` varchar(11) NOT NULL\n" +
                    ")";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS `laporan` ";
        //run sql
        DB.query2(sql);
    }
    
}
