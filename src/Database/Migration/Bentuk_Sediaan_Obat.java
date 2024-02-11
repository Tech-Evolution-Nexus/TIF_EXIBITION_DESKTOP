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
public class Bentuk_Sediaan_Obat {
    
    public static void migration(){
        String sql = "CREATE TABLE `bentuk_sediaan_obat` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `nama_bentuk_sediaan` varchar(30) NOT NULL,\n" +
                    "  `deskripsi` varchar(255)  NOT NULL\n" +
                    ")";
        //run sql
        drop();
        DB.query2(sql);
    } 
    
    public static void drop(){
        String sql = "DROP TABLE IF EXISTS`bentuk_sediaan_obat` CASCADE";
        //run sql
        DB.query2(sql);

    }
}
