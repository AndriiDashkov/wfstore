
package WsEditTables;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsCheckBoxCellRenderer extends DefaultTableCellRenderer {
    
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	
        if (value instanceof Boolean) {
        	
        	 JCheckBox box = new JCheckBox();
        	 
        	 box.setSelected((Boolean) value);
            
            return box;
        }
        
        return this;
    }
     
}