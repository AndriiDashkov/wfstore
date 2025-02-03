
package WsDataStruct;

import java.sql.Date;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsCompleteData {

	public int id = -1;
	
	public int id_order_parts = -1;
	
	public int id_invoice_parts = -1;
	
	public double quantity = 0.0;
	
	
	public String units_name ="";
	
	public String type_name ="";
	
	public String invoice_part_name ="";
	
	public double invoice_part_quantity = 0.0;
	
	public String vendor_code_2 ="";
	
	public String invoice_number ="";
	
	public Date invoice_date;
	
	public String agent_name ="";
	
	public int agent_id = -1;
	
	public int types_id = -1;
	
	public int units_id = -1;

}
