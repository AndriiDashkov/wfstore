
package WsEvents;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsContractPriceChangeEvent extends WsEvent {
	
	
	private int row_id = -1;

	public  WsContractPriceChangeEvent() {
		
		m_eventType = WsEventDispatcher.CONTRACT_PRICE_HAS_BEEN_CHANGED; 
		
	} 

	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
