/**
 * 
 */
package WsEditTables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsDateRenderer extends DefaultTableCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	DateFormat format;

    public WsDateRenderer() {
    	
      super();
      
      format= new SimpleDateFormat("dd.MM.yyyy");
      
    }

    public void setValue(Object value) {

      setText((value == null) ? "" : format.format(value));
    }
    
  }