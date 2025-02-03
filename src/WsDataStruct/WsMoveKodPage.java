
package WsDataStruct;

import java.util.Vector;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsMoveKodPage {
	
	
	public Vector<WsSkladMoveDataRow> rows_vec = null;
	
	public int kod =-1;
	
	public String name_kod = null;
	
	public boolean isEmpty() { return rows_vec.size() == 0; }
	
	public WsMoveKodPage() {
		
		 rows_vec = new Vector<WsSkladMoveDataRow>();
		
	}

}
