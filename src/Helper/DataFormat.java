/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class DataFormat {
     private Map<String, Object> jsonObject;

    public DataFormat() {
        this.jsonObject = new HashMap<>();
    }

    public void put(String key, Object value) {
        jsonObject.put(key, value);
    }

    public Object get(String key) {
        return jsonObject.get(key);
    }

    public String getString(String key) {
        return (String) jsonObject.get(key);
    }

    public int getInt(String key) {
        return (int) jsonObject.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean) jsonObject.get(key);
    }
}
