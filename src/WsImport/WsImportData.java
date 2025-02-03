
package WsImport;

import java.sql.Date;
import java.util.Vector;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsImportData {
	
	//container for the nomenclature number and quantity
	public Vector<WFRowData > m_data = new Vector<WFRowData >(); 
	
	public String m_nakl_number = new String();
	
	public Date m_nakl_date = new Date(0);
	
	public int[] people = {0, 0 ,0}; //snidanok, obid, vecherya
	
	public int kod = -1;
	
	public int column_index = -1;
	
	public WsImportData() {}
	
	

}
