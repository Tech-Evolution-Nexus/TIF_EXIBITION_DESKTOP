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
public class SimpanDataObat {
    public void create(){
          String sql =
                    "CREATE PROCEDURE `simpanDataObat` (\n" +
                    "    IN `p_kode_obat` VARCHAR(14),\n" +
                    "    IN `p_nama_obat` VARCHAR(50),\n" +
                    "    IN `p_id_bentuk_sediaan` INT,\n" +
                    "    IN `p_id_kategori` INT,\n" +
                    "    IN `p_aturan_pakai` VARCHAR(70)\n" +
                    ")\n" +
                    "BEGIN\n" +
                    "    DECLARE existing_obat INT;\n" +
                    "    \n" +
                    "    SELECT COUNT(*) INTO existing_obat FROM obat WHERE kode_obat = p_kode_obat;\n" +
                    "\n" +
                    "    IF existing_obat > 0 THEN\n" +
                    "        UPDATE obat\n" +
                    "        SET\n" +
                    "            nama_obat = p_nama_obat,\n" +
                    "            id_bentuk_sediaan = p_id_bentuk_sediaan,\n" +
                    "            id_kategori = p_id_kategori,\n" +
                    "            aturan_pakai = p_aturan_pakai\n" +
                    "        WHERE kode_obat = p_kode_obat;\n" +
                    "    ELSE\n" +
                    "        INSERT INTO obat (kode_obat, nama_obat, id_bentuk_sediaan, id_kategori, aturan_pakai)\n" +
                    "        VALUES (p_kode_obat, p_nama_obat, p_id_bentuk_sediaan, p_id_kategori, p_aturan_pakai);\n" +
                    "    END IF;\n" +
                    "END \n" ;
        //run sql
        DB.query2(sql);
    }
}
