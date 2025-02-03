
package WsEvents;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsCompleteOrderDataChanged  extends WsEvent {
	
	
	private int row_id = -1;

	public   WsCompleteOrderDataChanged  () {
		
		m_eventType = WsEventDispatcher.COMPLETE_DATA_CHANGED_EVENT; 
		
	} 

	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
