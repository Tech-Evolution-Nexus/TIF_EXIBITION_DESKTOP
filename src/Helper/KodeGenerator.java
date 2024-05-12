package Helper;

import java.util.Date;
import java.text.SimpleDateFormat;

public class KodeGenerator {

    public static String generateKodeSuplier() {
        return generateKode("SUP");
    }

    public static String generateKodeObat() {
        return generateKode("OBT");
    }

    public static String generateKodeTransaksi() {
        return generateKode("TRX");
    }
    public static String generateKodeReturnPenjualan() {
        return generateKode("RPJ");
    }
    public static String generateKodeReturnPembelian() {
        return generateKode("RPM");
    }

    private static String generateKode(String prefix) {
          Date currentDate = new Date();

    // Membuat objek SimpleDateFormat untuk format tanggal dan waktu
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");

    // Mengubah tanggal dan waktu menjadi string dengan format yang diinginkan
    String dateTimePart = dateFormat.format(currentDate);

    // Menggabungkan string tanggal dan waktu dengan prefix
    return prefix + dateTimePart;
    }

}
