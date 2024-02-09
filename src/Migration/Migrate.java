/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Migration;

import java.util.Scanner;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Migrate {
    public static void drop(){
        Users.drop();
    }
    public static void migrate(){
        Users.migration();
    }
    public static void main(String[] args) {
        try {
        Scanner sc = new Scanner(System.in);
        System.out.println("Pilih Opsi");
        System.out.println("1. Migrate Database");
        System.out.println("2. Drop Database");
        
        int opsi = sc.nextInt();
        
        if (opsi==1) {
            migrate();
        }else if(opsi==2){
            drop();
        }else{
            System.out.println("opsi tidak ada");
        }
        } catch (Exception e) {
            System.out.println("Anda Salah Memasukkan Opsi");
        }
    }
}
