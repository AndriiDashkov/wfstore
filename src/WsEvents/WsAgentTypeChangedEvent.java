
package WsEvents;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsAgentTypeChangedEvent extends WsEvent {
	
	private int agent_id = -1;
	
	private int row_id = -1;

	public WsAgentTypeChangedEvent () {
		
		m_eventType = WsEventDispatcher.AGENT_TYPE_CHAMGED_EVENT; 
		
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
