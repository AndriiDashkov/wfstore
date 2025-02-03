
package WsDataStruct;

import java.sql.Date;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsWaterDataRow {
	

	public Date date;
	
	public Date date_start;
	
	public Date date_end;

	public String agent_name;
	
	public int id_agent = -1;
	
	public int kod = -1;
	
	public double in_quantity = 0.0;
	
	public double out_quantity = 0.0;
	
	public double in_rest = 0.0;
	
	public double out_rest = 0.0;
	
	public int people = 0;
	
	public double consumed = 0.0;
	
	public double difference = 0.0;
	
	public String part_name = "";
}
