/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Components;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class CustomDialog extends JDialog {

    public CustomDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setModalExclusionType(JDialog.ModalExclusionType.APPLICATION_EXCLUDE);
        pack();
        setLocationRelativeTo(null);
        add(new javax.swing.JLabel("Das"));
       
          requestFocusInWindow();
        
        // Menambahkan event listener untuk tombol keyboard
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
    }

}
