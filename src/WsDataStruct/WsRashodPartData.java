
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashodPartData {
	
	public int id = -1;
	
	public int  id_sale_invoice = -1;
	
	public int  id_invoice_parts = -1;
	
    public String name ="";
    
    public double quantity = 0.0;
    
    public double req_quantity = 0.0;
    
    public double rest = 0.0;
    
    public int  id_units =-1;
    
    public String units_name = "";
    
    public String vendor_code_2 = "";
    
    public String info = "";
    
    public double cost = 0.0;
    
    public double costwithnds = 0.0;
    
    public double nds = 0.0;
    
    public int kod;
    
    public int row_index = -1;
    
    public String number = ""; 
    
    public java.sql.Date date = null;
    
    public WsRashodPartData() {}
    
    public WsRashodPartData(WsRashodPartData rhs) {
    	
    	id = rhs.id;
    	
    	id_sale_invoice = rhs.id_sale_invoice;
    	
    	id_invoice_parts = rhs.id_invoice_parts;
    	
        name = rhs.name;
        
        quantity = rhs.quantity;
        
        req_quantity = rhs.req_quantity;
        
        rest = rhs.rest;
        
        id_units = rhs.id_units;
        
        units_name = rhs.units_name;
        
        vendor_code_2 = rhs.vendor_code_2;
        
        info = rhs.info;
        
        cost = rhs.cost;
        
        costwithnds = rhs.costwithnds;
        
        nds = rhs.nds;
        
        kod = rhs.kod;
        
        number = rhs.number;
        
        date = rhs.date;
        
        row_index = rhs.row_index;
    	
    }
    
 
}
