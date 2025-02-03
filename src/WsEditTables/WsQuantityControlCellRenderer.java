
package WsEditTables;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import WsMain.WsUtils;



/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsQuantityControlCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	
        if (value instanceof Double) {
     
            double d = (double) value;

            setText(WsUtils.getDF_for_tables(d));
        }

        return this;
    }
     
}
