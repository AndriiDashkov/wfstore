
package WsDataStruct;

import java.sql.Date;
import java.util.Vector;



/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladMoveDataRow {
	

	public Date date;
	
	public Date date_doc;
	
	public Date date_start;
	
	public Date date_end;
	
	public String nakl_number;
	
	public String agent_name;
	
	public String agent_name2;
	
	public int agent_id;
	
	public Vector<WsSkladMoveDataColumn> row_vec = null;
	
	public Vector< Vector<WsSkladMoveDataColumn> > pages_row_vec = null;
	
	public int indexData = 0;
	
	public int people = 0;
	
	//0 -  rest, 1- prihod, 2 -rashod
	
	public int pages_number;
	
	public double doc_quantity = 0.0;;
	
	public double in_quantity = 0.0;
	
	public double out_quantity = 0.0;
	
	public boolean sklad_row = false;
	
	public boolean prihod = false;
	
	public boolean external_prihod = false;
	
	public double prihod_cost = 0.0;
	
	public WsSkladMoveDataRow() {}
	
	public WsSkladMoveDataRow(WsSkladMoveDataRow rhs) {
		
		date = rhs.date;
		
		date_doc = rhs.date_doc;
		
		date_start = rhs.date_start ;
		
		date_end = rhs.date_end;
		
		nakl_number = rhs.nakl_number;
		
		agent_name = rhs.agent_name;
		
		agent_id = rhs.agent_id;
		
		agent_name2 = rhs.agent_name2;
		
		doc_quantity = rhs.doc_quantity;
		
		sklad_row = rhs.sklad_row;
		
		in_quantity = rhs.in_quantity;
		
		out_quantity = rhs.out_quantity;
		
		prihod = rhs.prihod;
		
		external_prihod = rhs.external_prihod;
		
		 prihod_cost = rhs.prihod_cost;
		
		if(rhs.row_vec != null) {
			
			row_vec = new Vector<WsSkladMoveDataColumn>();
			
			for(WsSkladMoveDataColumn c: rhs.row_vec) {
				
				row_vec.add(new WsSkladMoveDataColumn(c));
			}
		
		}
		
		if(rhs.pages_row_vec != null) {
			
			pages_row_vec = new Vector< Vector<WsSkladMoveDataColumn> >();
			
			for(Vector<WsSkladMoveDataColumn> v: rhs.pages_row_vec) {
				
				Vector<WsSkladMoveDataColumn> r_vec = new  Vector<WsSkladMoveDataColumn>();
				
				for(WsSkladMoveDataColumn c: v) {
				
					r_vec.add(new WsSkladMoveDataColumn(c));
				
				}
				
				pages_row_vec.add(r_vec);
			}
		
		}
		
		
		indexData = rhs.indexData;
		
		people = rhs.people;
		
		//0 -  rest, 1- prihod, 2 -rashod
		
		pages_number = rhs.pages_number;
		
		
	}
	
	public void fillZeroQuantity() {
		
		in_quantity = 0.0;
		
		out_quantity = 0.0;
		
		for(WsSkladMoveDataColumn c: row_vec) {
			
			c.in_quantity = 0.0;
			
			c.in_quantity_1 = 0.0;
			
			c.out_quantity = 0.0;
			
			c.out_quantity_1 = 0.0;
			
			c.rest = 0.0;
			
			c.initial_rest_1 = 0.0;
			
			c.initial_rest = 0.0;
			
			for(int i = 0; i < c.q_array.length; ++i) {
			
					c.q_array[i].in_quantity = 0.0;
					
					c.q_array[i].out_quantity = 0.0;
					
					c.q_array[i].rest = 0.0;
					
					c.q_array[i].initial_rest = 0.0;
			
			}

		}
	}

}
