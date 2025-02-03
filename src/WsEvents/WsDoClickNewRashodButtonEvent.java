
package WsEvents;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDoClickNewRashodButtonEvent  extends WsEvent {
	
	
	private int row_id = -1;

	public WsDoClickNewRashodButtonEvent  () {
		
		m_eventType = WsEventDispatcher.DO_CLICK_NEW_RASHOD_EVENT; 
		
	} 

	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
