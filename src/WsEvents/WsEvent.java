package WsEvents;

/**
 *  Main event class
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsEvent implements WsEventInt {

	protected short m_eventType;
	
	protected Object m_sender = null; 

	protected String m_endMessage = null;
	
	/**
	 * This id can be used for transfer information this the event
	 */
	protected int m_id = -1;
	
	public WsEvent() {
		
		m_eventType = -1;
	}
	
	public WsEvent(short eType) {
		
		m_eventType = eType;
	}
	
	public WsEvent(short eType, Object sender) {
		
		m_eventType = eType;
		
		m_sender = sender;
	}

	public Object getSender() {
		
		return m_sender;
	}
	
	public short getEventType() {
		
		return m_eventType;
	}

	public void setEventType(short t) {
		
		m_eventType = t;
	}

	
	@Override
	public short get_EventType() {
		
		return m_eventType;
	}	
	
	public void setId(int i) { m_id =i; }
	
	public int getId() { return m_id; }
	
	/**
	 * 
	 * @param s - end message string
	 */
	public void setEndMessage(String s) { 
		
		m_endMessage = s;
	}
	/**
	 * 
	 * @return end message text
	 */
	public String getEndMessage() {
		
		return m_endMessage;
	}
}
