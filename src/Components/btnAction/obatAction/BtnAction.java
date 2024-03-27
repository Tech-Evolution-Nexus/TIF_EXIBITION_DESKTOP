package Components.btnAction.obatAction;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BtnAction  extends DefaultTableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ActionPanel satuanActionPanel = new ActionPanel();
       if (isSelected) {
            satuanActionPanel.setBackground(com.getBackground());
       }
        return satuanActionPanel;
    }
}
