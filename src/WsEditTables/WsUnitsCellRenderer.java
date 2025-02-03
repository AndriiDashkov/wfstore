
package WsEditTables;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import WsDataStruct.WsUnitData;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsUnitsCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	
        if (value instanceof WsUnitData) {
        	
            WsUnitData d = (WsUnitData) value;
            
            setText(d.name);
        }
            
        return this;
    }
     
}
