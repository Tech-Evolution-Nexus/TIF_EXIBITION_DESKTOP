/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author admin
 */
public class convertbulantoangka {
    
    private static final Map<String, Integer> MONTH_MAP = new HashMap<>();

    static {
        // Inisialisasi peta pemetaan nama bulan ke angka bulan
        MONTH_MAP.put("Januari", 1);
        MONTH_MAP.put("Februari", 2);
        MONTH_MAP.put("Maret", 3);
        MONTH_MAP.put("April", 4);
        MONTH_MAP.put("Mei", 5);
        MONTH_MAP.put("Juni", 6);
        MONTH_MAP.put("Juli", 7);
        MONTH_MAP.put("Agustus", 8);
        MONTH_MAP.put("September", 9);
        MONTH_MAP.put("Oktober", 10);
        MONTH_MAP.put("November", 11);
        MONTH_MAP.put("Desember", 12);
    }

    // Metode untuk mengonversi nama bulan Indonesia menjadi angka bulan
    public static int convertToMonthNumber(String monthName) {
        Integer monthNumber = MONTH_MAP.get(monthName);
        if (monthNumber != null) {
            return monthNumber;
        } else {
            // Jika nama bulan tidak valid, kembalikan -1
            return -1;
        }
    }
}
