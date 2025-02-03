package WsEvents;

import java.util.ArrayList;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsEventListeners {
	
	private int _eventType;	
	
	private ArrayList<WsEventObjectMethod> _listeners; 

	public WsEventListeners (int eventType) {
		
		_eventType = eventType;
		
		_listeners = new ArrayList<WsEventObjectMethod>();
	}


	public int get_eventType() {
		
		return _eventType;
	}

	public ArrayList<WsEventObjectMethod> getListeners() {
		
		return _listeners;
	}
	
	public boolean addListener (WsEventObjectMethod listener) {
		
		_listeners.add(listener);
		
		return true;
	}
	public void remove_listener (WsEventObjectMethod listener) {
		
		_listeners.remove(listener);
	}
}
