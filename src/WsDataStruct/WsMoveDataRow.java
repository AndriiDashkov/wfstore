/**
 * 
 */
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsMoveDataRow {
	
	
	public int kod = -1;
	
	public int marker = -1;
	
	public String name = "";
	
	public double initial_rest = 0.0;
	
	public double initial_cost = 0.0;
	
	public double in_quantity = 0.0;
	
	public double in_cost_sum = 0.0;
	
	public double out_quantity = 0.0;
	
	public double out_cost_sum = 0.0;
	
	public double out_cost = 0.0;
	
	public double rest = 0.0;
	
	public double rest_cost_sum = 0.0;
	
	public WsMoveDataRow() { }
	
	public WsMoveDataRow(WsMoveDataRow rhs) {
		
		kod = rhs.kod;
		
		marker = rhs.marker;
		
		name = rhs.name;
		
		initial_rest = rhs.initial_rest;
		
		initial_cost = rhs.initial_cost;
		
		in_quantity = rhs.in_quantity;
		
		in_cost_sum = rhs.in_cost_sum;
		
		out_quantity = rhs.out_quantity;
		
		out_cost_sum = rhs.out_cost_sum;
		
		out_cost = rhs.out_cost;
		
		rest = rhs.rest;
		
		rest_cost_sum = rhs.rest_cost_sum;
		
	}

}
