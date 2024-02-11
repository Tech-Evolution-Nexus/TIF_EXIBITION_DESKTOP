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
public class Detail_Pembelian {
    
    public static void migration(){
        String sql = "CREATE TABLE `detail_pembelian` (\n" +
                    "  `id` int NOT NULL PRIMARY KEY,\n" +
                    "  `kode_transaksi` char(17)  NOT NULL,\n" +
                    "  `kode_obat` char(14)  NOT NULL,\n" +
                    "  `harga` int NOT NULL DEFAULT '0',\n" +
                    "  `qty` int NOT NULL DEFAULT '0',\n" +
                    "  `subtotal` int NOT NULL DEFAULT '0',\n" +
                    "FOREIGN KEY (kode_transaksi) REFERENCES transaksi_pembelian(kode_transaksi),"+
                    "FOREIGN KEY (kode_obat) REFERENCES obat(kode_obat)"+
                    ")";
        //run sql
        drop();
        DB.query2(sql);
    } 
    
    public static void drop(){
        String sql = "DROP TABLE IF EXISTS`detail_pembelian` CASCADE";
        //run sql
        DB.query2(sql);
    }}
