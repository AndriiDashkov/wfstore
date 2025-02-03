
package WsEvents;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsOrderChangedEvent extends WsEvent {
	
	
	private int row_id = -1;

	public  WsOrderChangedEvent() {
		
		m_eventType = WsEventDispatcher.ORDER_HAS_BEEN_CHANGED_EVENT; 
		
	} 

	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
