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
public class SimpanKategori {
    public void create(){
          String sql = 
                        "CREATE  PROCEDURE `simpanKategori`(IN `namakategori` VARCHAR(50), IN `idkategori` INT)\n" +
                        "IF(idkategori!=0)THEN\n" +
                        "UPDATE kategori SET nama_kategori = namakategori WHERE id = idkategori;\n" +
                        "ELSE\n" +
                        "INSERT INTO kategori (nama_kategori) VALUES (namakategori);\n" +
                        "end IF\n" ;
        //run sql
        DB.query2(sql);
    }
}
