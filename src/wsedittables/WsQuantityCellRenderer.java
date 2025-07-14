
package wsedittables;

import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import wsmain.WsGuiTools;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsQuantityCellRenderer extends DefaultTableCellRenderer {
    

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
    	
        if (value instanceof Double) {
     
            double d = (double) value;
            
            if(d < WsUtils.getRZL() && d > 0.0) {
            	
            	DecimalFormat formatter = new DecimalFormat("0.0000000");
            	
            	setText(formatter .format(d));
            	
            }
            else {
            	
            	setText(WsUtils.getDF_for_tables(d));
            
            }
            
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
