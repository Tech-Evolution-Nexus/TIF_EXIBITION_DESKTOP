/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Pixel
 */
public class DB {

    public static Connection con;
    public static Statement stm;
    static String dbname = "apotek_6"; 
    static String user = "root";
    static String pass = "";
    static String url = "jdbc:mysql://localhost:3306/" + dbname;

    public static void DBSetup() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the database connection
            con = DriverManager.getConnection(url, user, pass);

            stm = con.createStatement();
//            System.out.println("Koneksi Berhasil");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            // Establish the database connection
            return con = DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static ResultSet query(String queString) {
        DBSetup();
        try {
            ResultSet resultSet = stm.executeQuery(queString);
            return resultSet;
        } catch (Exception e) {
            System.err.println("query error: " + e.getMessage());
            return null;
        }
    }

    public static int query2(String queString) {
        DBSetup();
        try {
            int res = stm.executeUpdate(queString);
            return res;
        } catch (Exception e) {
            System.err.print("query error: " + e.getMessage());
            return 0;
        }
    }
}
