
package WsEvents;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsPrihodInvoiceChangedEvent extends WsEvent {
	
	private int row_id = -1;

	public  WsPrihodInvoiceChangedEvent () {
		
		m_eventType = WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED; 
		
	} 

	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
