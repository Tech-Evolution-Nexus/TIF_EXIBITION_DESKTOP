/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Routine;

import Config.DB;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class SimpanJenisPenjualan {
    public void create(){
          String sql ="CREATE  PROCEDURE `simpanJenisPenjualan` (IN `kodeObat` CHAR(14), IN `total` INT, IN `harga` INT, IN `idBentukSatuan` INT)   BEGIN\n" +
"DECLARE idSatuanDefault int ;\n" +
"\n" +
"insert into jenis_penjualan VALUES(null,kodeObat,total,harga,idBentukSatuan);\n" +
"END";
        //run sql
        DB.query2(sql);
    }
}
