
package WsDataStruct;

import java.util.Vector;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashodData {

    public int id = -1;  
    
    public String number = "";  
    
    public java.sql.Date date; 
    
    public String info = ""; 
    
    public int id_counterparty = -1;
    
    public String agentName = "";
    
    public int id_company = -1;
    
    public int people = 0;
    
    public Vector<WsRashodPartData> rows = null;
    
}
