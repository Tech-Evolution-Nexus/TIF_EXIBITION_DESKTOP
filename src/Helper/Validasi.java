/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helper;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class Validasi {

    public static boolean onlyNumber(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static long getNumeric(String input) {
        try {
            String numericString = input.replaceAll("[^0-9]", "");
            long maxValue = Long.MAX_VALUE;
            if (numericString.length() > String.valueOf(maxValue).length()) {
                throw new NumberFormatException("Input string is too large.");
            }
            System.out.println(numericString+"ini numerik");
            return Long.parseLong(numericString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    public static void inputOnlyNumber(JTextField input) {
         input.addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            char c = e.getKeyChar();
            if (!Character.isDigit(c)) {
                e.consume(); // Discard the character
            }
        }
    });
    }
}
