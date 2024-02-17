/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Database.Migration;

import Core.Classes;

public class Migrate {
    //pengurutan sesuaikan dari table parent terlebih dahulu
    private static String[] MigrateClass = {"Users","Supplier","Kategori","Bentuk_Sediaan_Obat","Obat","Jenis_Penjualan","Pengeluaran","Penjualan","Shift","Stok_Obat","Laporan","Transaksi_Pembelian","Detail_Pembelian","Transaksi_Penjualan","Detail_Penjualan"};
  
    public static void drop(){
        
        try {
              String[]reverse=reverse();
        for (String name : reverse) {
            System.out.println(name);
        Classes.runMethod("Database.Migration."+name, "drop");
                    

        }
        } catch (Exception e) {
        }
    }
    public static void migrate(){
        try {
            drop();
                       

         for (String name : MigrateClass) {
            Classes.runMethod("Database.Migration."+name, "migration");
             System.out.println("table "+name+" successfully migrate -------------------------------");
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {
        try {  
            migrate();
        }  catch(Exception e){
            
        }
    }
    
    public static String[] reverse(){
        String  t;
        int n=MigrateClass.length;
        String[] reverse = new String[n];
        for (int i = 0; i < n ; i++) { 
            t = MigrateClass[i]; 
            reverse[i] = MigrateClass[n - i - 1]; 
            reverse[n - i - 1] = t; 
        } 
        return reverse;
    }
    
}
