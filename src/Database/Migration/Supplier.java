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
public class Supplier {
    
    public static void migration() throws SQLException{
        String sql = "CREATE TABLE `supplier` (\n" +
                    "  `kode_suplier` char(16) NOT NULL  PRIMARY KEY,\n" +
                    "  `nama_suplier` varchar(50)  NOT NULL,\n" +
                    "  `alamat` varchar(70)  NOT NULL,\n" +
                    "  `nomor_telepon` char(13)  NOT NULL\n" +
                    ") ";
        //run sql
//        drop();
        DB.query2(sql);
    } 
    
    public static void drop() throws SQLException{
        String sql = "DROP TABLE IF EXISTS `supplier` CASCADE";
        //run sql
        DB.query2(sql);
    }
    
}
