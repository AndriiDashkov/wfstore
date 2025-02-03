
package WsEditTables;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import WsControls.WsUnitsComboBox;
import WsDataStruct.WsUnitData;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsUnitsCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	private static final long serialVersionUID = 1L;
	
	private WsUnitData m_unit;

    public  WsUnitsCellEditor() {
    	
    }
     
    @Override
    public Object getCellEditorValue() {
    	
        return this.m_unit;
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
        if (value instanceof WsUnitData) {
        	
            this.m_unit = (WsUnitData) value;
        }
         
        WsUnitsComboBox combo = new WsUnitsComboBox();
         
        if(m_unit == null) {
        	
        	combo.setSelectedIndex(0);
        	
        	m_unit = combo.getSelectedUnitData();
        }
        else {
        	combo.setSelectedItem(m_unit.name);
        }
         
        combo.addActionListener(this);
               
        return combo;
    }
 
    @Override
    public void actionPerformed(ActionEvent event) {
    	
    	WsUnitsComboBox combo = (WsUnitsComboBox) event.getSource();
        
        m_unit =  combo.getSelectedUnitData();
    }
}
