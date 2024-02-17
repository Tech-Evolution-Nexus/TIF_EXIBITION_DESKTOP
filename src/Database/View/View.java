/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.View;

import Core.Classes;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class View {
    private static String[] viewName = {"Data_Jenis_Penjualan","Data_Obat","Data_Stok_Obat","Laporan_Pembelian","Laporan_Pendapatan","Laporan_Pengeluaran","Laporan_Penjualan","Laporan_Stok","PrinterView"}; 
    public static void main(String[] args) {
               for (String name : viewName) {
            Classes.runMethod("Database.View."+name, "create");
             System.out.println("view "+name+" successfully create -------------------------------");
        }
    }
}
