
package WsDataStruct;

import java.sql.Date;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPrihodPartData {
	
	public int id = -1; 
	
	public int id_invoice = -1;
	
	public int id_part_type;
	
	public String part_type_name = ""; 
	
    public String name = ""; 
    
    public double quantity = 0.0;
    
    public double real_quantity = 0.0;
    
    public double rest = 0.0;
    
    public double cost = 0.0;
    
    public double costwithnds = 0.0;
    
    public double nds = 0.0;
    
    public int id_units;
    
    public String units_name = ""; 
    
    public String vendorcode2 = "";
    
    public boolean closed = false;
    
    public String info = "";
    
    public Date date;
    
    public int kod = -1;
    
    public boolean kod_changed_flag = false;
    
    public String contract_name = "";
    
    public Date contract_date;
    
    public WsPrihodPartData( WsPrihodPartData rhs) {
    	
    	id = rhs.id; 
    	
    	id_invoice = rhs.id_invoice;
    	
    	id_part_type = rhs.id_part_type;
    	
    	part_type_name = rhs.part_type_name; 
    	
    	name = rhs.name; 
    	
        quantity = rhs.quantity;
        
        real_quantity = rhs.real_quantity;
        
        rest = rhs.rest;
        
        cost = rhs.cost;
        
        nds = rhs.nds;
        
        costwithnds = rhs.costwithnds;
        
        id_units = rhs.id_units;
        
        units_name = rhs.units_name; 
        
        vendorcode2 = rhs.vendorcode2;
        
        closed = rhs.closed;
        
        info = rhs.info;
        
        date = rhs.date;
        
        kod = rhs.kod;
        
        kod_changed_flag = rhs.kod_changed_flag;
        
        contract_name = rhs.contract_name;
        
        contract_date = rhs.contract_date;
    	
    }
    
    public WsPrihodPartData( ) {
    	
    }

}
