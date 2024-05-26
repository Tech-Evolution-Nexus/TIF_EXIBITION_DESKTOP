package Helper;

import java.sql.ResultSet;
import java.util.prefs.Preferences;

import View.Auth.login;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import org.json.JSONArray;

public class Auth {

    private String id;
    private String rfid;
    private String noHp;
    private String nama;
    private String alamat;
    private String username;
    private String password;
    private String role;
    private String ukurankertas;

    private boolean status;

    public Auth() {
        try {
            Preferences userPreferences = Preferences.userNodeForPackage(login.class);
            String datalogin = userPreferences.get("localLogin", null);
            String s = userPreferences.get("sesionsetingg", null);
            if (datalogin != null) {
                JSONArray user = new JSONArray(datalogin);
                // Mengambil nilai dari objek jsonArray dan mengatur nilai properti
                id = user.getString(0);
                nama = user.getString(1);
                username = user.getString(2);
                role = user.getString(3);
                ukurankertas = s;
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
        }

    }

    public String getUkurankertas() {
        return ukurankertas;
    }

    public void setUkurankertas(String ukurankertas) {
        Preferences userPreferences = Preferences.userNodeForPackage(login.class);
        userPreferences.put("sesionsetingg", ukurankertas);
        this.ukurankertas = ukurankertas;
    }

    public boolean check() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getRfid() {
        return rfid;
    }

    public String getNoHp() {
        return noHp;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void Clear() {
        try {
            Preferences userPreferences = Preferences.userNodeForPackage(login.class);
            userPreferences.clear();
        } catch (BackingStoreException ex) {
            Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
