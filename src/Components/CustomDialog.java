/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Components;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 * @author Muhammad Nor Kholit
 */
public class CustomDialog extends JDialog {


   
     public CustomDialog() {
        setup();
    }
    
    
    
    private void setup(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setModalExclusionType(JDialog.ModalExclusionType.APPLICATION_EXCLUDE);
        pack();
        setFocusable(true);
        requestFocusInWindow();
        setLocationRelativeTo(null);
        add(new javax.swing.JLabel("Das"));
       // Menambahkan window listener untuk menutup dialog saat tombol Escape ditekan
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Mendapatkan panel yang menerima fokus
                JPanel contentPane = (JPanel) getContentPane();
                
                // Membuat pemetaan input untuk tombol Escape
                KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
                contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "closeDialog");
                
                // Membuat aksi untuk menutup dialog
                contentPane.getActionMap().put("closeDialog", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dispose(); // Menutup dialog saat tombol Escape ditekan
                    }
                });
            }
        });   
       
    }
    
    


}
