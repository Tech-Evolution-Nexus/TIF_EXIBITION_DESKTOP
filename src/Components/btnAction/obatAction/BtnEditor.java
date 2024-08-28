package Components.btnAction.obatAction;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;


public class BtnEditor extends DefaultCellEditor {

    private ActionEvent  event;
    private boolean  withInfo;
    public BtnEditor(ActionEvent event,boolean withInfo) {
        super(new JCheckBox());
        this.event = event;
        this.withInfo = withInfo;
    }
    

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component com = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        
        if (withInfo) {
        ActionPanel2 satuanActionPanel = new ActionPanel2();
          satuanActionPanel.initEvent(event, row);
        if (isSelected) {
        satuanActionPanel.setBackground(table.getSelectionBackground());
        }
        return satuanActionPanel;
        }else{
        ActionPanel1 satuanActionPanel = new ActionPanel1();
          satuanActionPanel.initEvent(event, row);
        if (isSelected) {
        satuanActionPanel.setBackground(table.getSelectionBackground());
        }
        return satuanActionPanel;
        }
      
    }
     @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) e;
            JTable table = (JTable) e.getSource();
            int row = table.rowAtPoint(mouseEvent.getPoint());
            if (row < 0 || row >= table.getRowCount()) {
                return false; // Jangan edit jika baris tidak valid
            }
        }
        return super.isCellEditable(e);
    }
}
