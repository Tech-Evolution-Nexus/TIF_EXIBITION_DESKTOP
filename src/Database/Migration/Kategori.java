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
public class Kategori {
    
    public static void migration(){
        String sql = "CREATE TABLE `kategori` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `nama_kategori` varchar(50) NOT NULL\n" +
                    ")";
        //run sql
        drop();
        DB.query2(sql);
    } 
    
    public static void drop(){
        String sql = "DROP TABLE IF EXISTS`kategori` CASCADE";
        //run sql
        DB.query2(sql);
    }
}
