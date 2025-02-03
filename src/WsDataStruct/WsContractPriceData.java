
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsContractPriceData {

	public int id = -1; 
	
	public int id_contract = -1;
	
	public int kod = -1;
	
	public String name = "";
	
	public int id_part_type = -1;
	
	public int id_units  = -1;
	
	public String units_name = "";
	
	public double cost  = 0.0;
	
	public double nds = 0.0; 
	
	public double costwnds= 0.0;
	 
    public  WsContractPriceData( WsContractPriceData rhs) {
    	
    	id = rhs.id; 
    	
    	id_contract = rhs.id_contract;
    	
    	name = rhs.name; 
    	
    	id_part_type = rhs.id_part_type;
    	
        id_units = rhs.id_units;
  
        cost = rhs.cost;
        
        nds = rhs.nds;
        
        costwnds = rhs.costwnds;
        
        kod = rhs.kod;
        
        units_name = rhs.units_name;
        
    	
    }
    
    public  WsContractPriceData( ) {
    	
    }
}


