/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author admin
 */
public class textfielddd extends DefaultCellEditor {

    private JPanel panel;
    private JSpinner spinner;

    public textfielddd() {
        super(new JCheckBox());
        panel = new JPanel(new BorderLayout());
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0L, 0L, Integer.MAX_VALUE, 1L);
        spinner = new JSpinner();

        // Attach a NumberFormatter to the JSpinner to allow only numbers
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
        editor.getTextField().setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new NumberFormatter(NumberFormat.getIntegerInstance())));
        spinner.setEditor(editor);
        panel.add(spinner);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        spinner.setValue(value);

        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        // Return the text of the text field as the cell editor value
        return spinner.getValue();
    }
}
