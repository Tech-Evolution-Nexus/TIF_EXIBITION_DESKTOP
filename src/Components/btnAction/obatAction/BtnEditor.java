package Components.btnAction.obatAction;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;


public class BtnEditor extends DefaultCellEditor {

    private ActionEvent  event;
    public BtnEditor(ActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component com = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        ActionPanel satuanActionPanel = new ActionPanel();
        satuanActionPanel.initEvent(event, row);
       if (isSelected) {
            satuanActionPanel.setBackground(table.getSelectionBackground());
       }
        return satuanActionPanel;
    }
    
}
