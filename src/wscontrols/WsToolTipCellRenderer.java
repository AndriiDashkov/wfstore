
package wscontrols;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsToolTipCellRenderer extends DefaultTableCellRenderer {
	
	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent( JTable table,
            Object value, boolean isSelected, boolean hasFocus,
            int row, int col ) {
    	
        JComponent cp = (JComponent)super.getTableCellRendererComponent( table,
                value, isSelected, hasFocus, row, col );
        
        cp.setToolTipText( value.toString() );
        
        return cp;
    }
}