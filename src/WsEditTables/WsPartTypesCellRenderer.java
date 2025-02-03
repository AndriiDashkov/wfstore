
package WsEditTables;


import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import WsDataStruct.WsPartType;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPartTypesCellRenderer extends DefaultTableCellRenderer {
    
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	
        if (value instanceof WsPartType) {

            WsPartType d = (WsPartType) value;
            
            setText(d.name);
        }        
        return this;
    }
     
}