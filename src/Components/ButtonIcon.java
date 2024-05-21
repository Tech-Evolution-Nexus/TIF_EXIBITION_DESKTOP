/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class ButtonIcon extends JButton {

  

    public ButtonIcon() {
        setBorderPainted(false);
        setHorizontal(false);
            }

    public void setIcon(String path) {
        setIcon(new FlatSVGIcon(path));
    }

    public void setHorizontal(Boolean kondisi) {
        if (kondisi) {
            setVerticalTextPosition(JButton.CENTER);
            setHorizontalTextPosition(JButton.TRAILING);
        } else {
            setPreferredSize(new Dimension(getPreferredSize().width, 58));

            setVerticalTextPosition(JButton.BOTTOM);
            setHorizontalTextPosition(JButton.CENTER);
        }
    }
}
