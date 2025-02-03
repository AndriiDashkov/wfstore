
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladMoveDataColumn {
	
	public double rest  = 0.0;
	
	public double in_quantity = 0.0;
	
	public double out_quantity= 0.0;
	
	public int kod;
	
	public int id_contract;
	
	public String name;
	
	public String contract_name;
	
	public String units;
	
	public double initial_rest = 0.0;
	
	public double rest_1  = 0.0;
	
	public double in_quantity_1 = 0.0;
	
	public double out_quantity_1 = 0.0;
	
	public double initial_rest_1 = 0.0;
	
	public double correction = 0.0;
	
	public double correctionToDo = 0.0;
	
	public double out_cost = 0.0;
	
	public double in_cost = 0.0;

	public WsQuantityData[] q_array= { new WsQuantityData(), new WsQuantityData(), 
			new WsQuantityData(), new WsQuantityData()};
	
	public int id = -1;
	
	public class WsQuantityData {
		
		public double rest  = 0.0;
		
		public double in_quantity = 0.0;
		
		public double out_quantity= 0.0;
		
		public double initial_rest = 0.0;
		
		public double rest_sum  = 0.0;
		
		public double in_quantity_sum = 0.0;
		
		public double out_quantity_sum = 0.0;
		
		public double initial_rest_sum = 0.0;
		
		public WsQuantityData() {}
		
		public WsQuantityData(WsQuantityData rhs) {
			
			rest  = rhs.rest ;
			
			in_quantity = rhs.in_quantity;
			
			out_quantity = rhs.out_quantity;
			
			initial_rest = rhs.initial_rest;
			
			rest_sum  = rhs.rest_sum ;
			
			in_quantity_sum = rhs.in_quantity_sum;
			
			out_quantity_sum = rhs.out_quantity_sum;
			
			initial_rest_sum = rhs.initial_rest_sum;
			
			
		}
		
	}
	
	public WsSkladMoveDataColumn() {}
	
	public WsSkladMoveDataColumn( WsSkladMoveDataColumn rhs) {
		
		rest  = rhs.rest;
		
		in_quantity = rhs.in_quantity;
		
		out_quantity = rhs.out_quantity;
		
		kod = rhs.kod;
		
		name = rhs.name;
		
		contract_name = rhs.contract_name;
		
		units = rhs.units;
		
		initial_rest = rhs.initial_rest;
		
		rest_1  = rhs.rest_1;
		
		in_quantity_1 = rhs.in_quantity_1;
		
		out_quantity_1 = rhs.out_quantity_1;
		
		initial_rest_1 = rhs.initial_rest_1;
		
		q_array[0] = new WsQuantityData(rhs.q_array[0]);
		
		q_array[1] = new WsQuantityData(rhs.q_array[1]);
		
		q_array[2] = new WsQuantityData(rhs.q_array[2]);
		
		q_array[3] = new WsQuantityData(rhs.q_array[3]);
		
		id = rhs.id;
		
		id_contract = rhs.id_contract;
		
		in_cost = rhs.in_cost;
		
		out_cost = rhs.out_cost;
		
	}
}
