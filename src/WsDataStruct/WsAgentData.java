
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentData {
	
	public int id = -1;
	
	public int id_type;
	
	public String name = "";
	
	public String contact = "";
	
	public String info = "";
	
	public String type_name = "";
	
	//this is auxiliary array it is requred for some reports, contains no information for agent
	public int[] quantity = new int[7];
}
