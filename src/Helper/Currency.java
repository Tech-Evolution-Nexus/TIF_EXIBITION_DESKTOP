/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Currency {

    public static String format(int angka) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        kursIndonesia.setMaximumFractionDigits(0);
        kursIndonesia.setGroupingSize(3);

        return kursIndonesia.format(angka);
    }

    public static String format(Long angka) {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        kursIndonesia.setMaximumFractionDigits(0);
        kursIndonesia.setGroupingSize(3);

        return kursIndonesia.format(angka);
    }

    public static long deformat(String formattedString){
//        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
//        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
//
//        formatRp.setCurrencySymbol("Rp. ");
//        formatRp.setMonetaryDecimalSeparator(',');
//        formatRp.setGroupingSeparator('.');
//        kursIndonesia.setDecimalFormatSymbols(formatRp);
//
//        // Parsing string yang diformat menjadi nilai integer
//        Number number = kursIndonesia.parse(formattedString);
//        return number.intValue();
        String cleanNumberString = formattedString.replaceAll("[^\\d]", "");
        long number = Long.parseLong(cleanNumberString);
        if(String.valueOf(number).isEmpty()){
            return 0;
        }
        return number;
        
    }
}
