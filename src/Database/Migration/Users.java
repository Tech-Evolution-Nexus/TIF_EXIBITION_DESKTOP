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
public class Users {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `users` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `no_telepon` char(16) UNIQUE  NOT NULL,\n" +
                    "  `nama` varchar(40)  NOT NULL,\n" +
                    "  `alamat` varchar(70) NOT NULL,\n" +
                    "  `username` varchar(50) UNIQUE  NOT NULL,\n" +
                    "  `password` varchar(255)  NOT NULL,\n" +
                    "  `role` enum('owner','kasir')  NOT NULL\n" +
                    ") ";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS`users` CASCADE";
        //run sql
        DB.query2(sql);
    }
}
