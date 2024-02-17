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
public class Detail_Penjualan {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `detail_penjualan` (\n" +
                    "  `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  `kode_transaksi` char(17)  NOT NULL,\n" +
                    "  `kode_obat` char(14)  NOT NULL,\n" +
                    "  `harga` int NOT NULL DEFAULT '0',\n" +
                    "  `qty` int NOT NULL DEFAULT '0',\n" +
                    "  `subtotal` int NOT NULL DEFAULT '0',\n" +
                    "FOREIGN KEY (kode_transaksi) REFERENCES transaksi_penjualan(kode_transaksi),"+
                    "FOREIGN KEY (kode_obat) REFERENCES obat(kode_obat)"+
                    ")";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS`detail_penjualan` CASCADE";
        //run sql
        DB.query2(sql);
    }
}
