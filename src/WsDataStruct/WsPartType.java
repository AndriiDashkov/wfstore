
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPartType {
	
	public int id = -1;
	
	public int kod = -1;
	
	public String name = "";
	
	public String info = "";
	
	//auxiliary
	public double quantity;
	
	public double costwithnds = 0.0;
	
	public boolean use = false;
	
	public double nds_coeff = 1.2;
	
	public WsPartType() {}
	
	public WsPartType(WsPartType d) {
		
		id = d.id;
		
		kod = d.kod;
		
		name = d.name;
		
		info = d.info;
		
		quantity = d.quantity;
		
		costwithnds = d.costwithnds;
		
		use = d.use;
		
		nds_coeff = d.nds_coeff;
		
	}

}
