
package WsEvents;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentComboSelected extends WsEvent {
	
	private int agent_id = -1;
	
	private int row_id = -1;

	public WsAgentComboSelected () {
		
		m_eventType = WsEventDispatcher.AGENT_IN_COMBO_SELECTED_EVENT; 
		
	} 

	public int getAgentId() {
		
		return  agent_id;
	}
	
	public void setAgentId(int id) {
		
		agent_id = id;
	}
	
	public int getRowId() {
		
		return  row_id;
	}
	
	public void setRowId(int id) {
		
		row_id = id;
	}
}
