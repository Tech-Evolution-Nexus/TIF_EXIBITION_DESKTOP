package Helper;

import java.util.Date;

import App.Model.ObatModel;
import App.Model.ReturnPembelianModel;
import App.Model.ReturnPenjualanModel;
import App.Model.SuplierModel;
import App.Model.TransaksiPembelianModel;
import App.Model.TransaksiPenjualanModel;

public class KodeGenerator {

    public static String generateKodeSuplier() {
        try {
            SuplierModel model = new SuplierModel();
            int count = model.count();
            return generateKode("SUP",count+1);
        } catch (Exception e) {
            return generateKode("SUP",1);
        }
    }

    public static String generateKodeObat() {
        try {
            ObatModel model = new ObatModel();
            int count = model.count();
            return generateKode("OBT",count+1);
        } catch (Exception e) {
            return generateKode("OBT",1);
        }
    }

    public static String generateKodeTransaksiPenjualan() {
        try {
            TransaksiPenjualanModel model = new TransaksiPenjualanModel();
            int count = model.count();
            return generateKode("TRJ",count+1);
        } catch (Exception e) {
            return generateKode("TRJ",1);
        }
    }

    public static String generateKodeTransaksiPembelian() {
         try {
            TransaksiPembelianModel model = new TransaksiPembelianModel();
            int count = model.count();
            return generateKode("TRB",count+1);
        } catch (Exception e) {
            return generateKode("TRB",1);

        }
    }

    public static String generateKodeReturnPenjualan() {
          try {
            ReturnPenjualanModel model = new ReturnPenjualanModel();
            int count = model.count();
            return generateKode("RPJ",count+1);
        } catch (Exception e) {
            return generateKode("RPJ",1);

        }
    }

    public static String generateKodeReturnPembelian() {
           try {
            ReturnPembelianModel model = new ReturnPembelianModel();
            int count = model.count();
            return generateKode("RPM",count+1);
        } catch (Exception e) {
            return generateKode("RPM",1);

        }
    }

  private static String generateKode(String prefix,  int count) {
    int availableLength = 10 - prefix.length();
    int countLength = String.valueOf(count).length();
    int maxCountLength = 7;
    if (availableLength < maxCountLength || countLength > maxCountLength) {
        // throw new IllegalArgumentException("Prefix and count exceed maximum length");
    }

    String countStr = String.format("%0" + (availableLength) + "d", count);
    return prefix + countStr;
}

}
