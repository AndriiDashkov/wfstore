
package wsedittables;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import wsdatastruct.WsUnitData;
import wsmain.WsGuiTools;

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
            
            setCustomFont();
        }
            
        return this;
    }
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		setFont(f);
			
	}
     
}
