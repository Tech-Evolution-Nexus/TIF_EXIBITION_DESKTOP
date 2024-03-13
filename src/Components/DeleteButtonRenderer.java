package Components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteButtonRenderer extends Components.ButtonIcon implements TableCellRenderer {


    public DeleteButtonRenderer() {

        // Set properties for the button
        setOpaque(true);
        setForeground(Color.WHITE);
        setBackground(Color.RED);
        setFocusPainted(false);
        setBorderPainted(false);
        setIcon("Assets/svg/deleteIcon.svg");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Return the button as the renderer component
        return this;
    }
}
