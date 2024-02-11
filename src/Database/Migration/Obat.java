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
public class Obat {
    
    public static void migration(){
        String sql = "CREATE TABLE `obat` (\n" +
                    "  `kode_obat` char(14) NOT NULL  PRIMARY KEY,\n" +
                    "  `id_kategori` int NOT NULL,\n" +
                    "  `nama_obat` varchar(50)  NOT NULL,\n" +
                    "  `id_bentuk_sediaan` int NOT NULL,\n" +
                    "  `jumlah_obat` int NOT NULL DEFAULT '0',\n" +
                    "  `aturan_pakai` varchar(50) DEFAULT NULL,\n" +
                    "  `tanggal_dibuat` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "`status` enum('0','1') NOT NULL default '1',\n" +
                    "FOREIGN KEY (id_kategori) REFERENCES kategori(id),"+
                    "FOREIGN KEY (id_bentuk_sediaan) REFERENCES bentuk_sediaan_obat(id)"+
                    ")";
        //run sql     
        drop();
        DB.query2(sql);
    } 
    
    public static void drop(){
        String sql = "DROP TABLE IF EXISTS `obat` CASCADE";
        //run sql
        DB.query2(sql);
    }
}
