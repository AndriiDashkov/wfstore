
package WsEvents;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsContractChangeEvent extends WsEvent {
	
	
	private int row_id = -1;

	public   WsContractChangeEvent () {
		
		m_eventType = WsEventDispatcher.CONTRACT_HAS_BEEN_CHANGED; 
		
	} 

	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
