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
public class Pengeluaran {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `pengeluaran` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `id_user` int NOT NULL,\n" +
                    "  `total_pengeluaran` int NOT NULL DEFAULT '0',\n" +
                    "  `keterangan` varchar(70) NOT NULL,\n" +
                    "  `tanggal_pengeluaran` date NOT NULL,\n" +
                    "FOREIGN KEY (id_user) REFERENCES users(id)"+
                    ") ";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS `pengeluaran` ";
        //run sql
        DB.query2(sql);
    }
    
}
