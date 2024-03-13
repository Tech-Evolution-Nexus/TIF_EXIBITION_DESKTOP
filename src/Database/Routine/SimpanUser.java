/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Routine;

import Config.DB;
import java.sql.SQLException;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class SimpanUser {
    public void create() throws SQLException{
          String sql =
                        "CREATE PROCEDURE simpanUser(\n" +
                        "    IN p_ktp VARCHAR(255),\n" +
                        "    IN p_nama VARCHAR(255),\n" +
                        "    IN p_alamat VARCHAR(255),\n" +
                        "    IN p_username VARCHAR(255),\n" +
                        "    IN p_password VARCHAR(255),\n" +
                        "    IN p_role VARCHAR(255),\n" +
                        "    IN p_id INT\n" +
                        ")\n" +
                        "BEGIN\n" +
                        "    IF p_id = 0 THEN\n" +
                        "        INSERT INTO users (no_ktp, nama, alamat, username, password, role)\n" +
                        "        VALUES (p_ktp, p_nama, p_alamat, p_username, p_password, p_role);\n" +
                        "    ELSE\n" +
                        "        UPDATE users\n" +
                        "        SET no_ktp = p_ktp,\n" +
                        "            nama = p_nama,\n" +
                        "            alamat = p_alamat,\n" +
                        "            username = p_username,\n" +
                        "            password = p_password,\n" +
                        "            role = p_role\n" +
                        "        WHERE id = p_id;\n" +
                        "    END IF;\n" +
                        "END";
        //run sql
        DB.query2(sql);
    }
}
