package Components.btnAction.obatAction;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BtnAction  extends DefaultTableCellRenderer{
private boolean withInfo;
    public BtnAction(boolean withInfo) {
        this.withInfo = withInfo; 
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ActionPanel1 satuanActionPanel = new ActionPanel1();
        if (isSelected) {
            satuanActionPanel.setBackground(com.getBackground());
        }      
        else if (row % 2 == 0) {
            satuanActionPanel.setBackground(Color.WHITE);
        } else {
            satuanActionPanel.setBackground(new Color(248, 248, 251));
        }
        return satuanActionPanel;
    }
}
