
package WsDataStruct;

import java.sql.Date;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPrihodRashodMoveData {
	
	public int id_invoice_part = -1; 
	
	public int id_invoice = -1;
	
	public int id_part_type;
	
    public double quantity_in = 0.0;
    
    public double rest = 0.0;
    
    public double quantity_out = 0.0;
    
    public Date date_in;
    
    public Date date_out;
    
    public int kod = -1;
    
    public String in_number = "";
    
    public String out_number = "";

}

