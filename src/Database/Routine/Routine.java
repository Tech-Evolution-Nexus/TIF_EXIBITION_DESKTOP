/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Routine;

import Core.Classes;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Routine {
        private static String[] MigrateClass = {"SimpanKategori","SimpanUser","SimpanDataObat","SimpanJenisPenjualan"};
    public static void main(String[] args) {
        try {
         for (String name : MigrateClass) {
            Classes.runMethod("Database.Routine."+name, "create");
             System.out.println("routine "+name+" successfully create -------------------------------");
        }
        } catch (Exception e) {
        }
    }
}
