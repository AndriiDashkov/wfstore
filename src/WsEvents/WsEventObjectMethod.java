package WsEvents;
/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsEventObjectMethod {

	private Object _listener;	
	
	private String _method;	
	

	public WsEventObjectMethod (Object listener, String methodName) {
		
		_listener = listener;
		
		_method = methodName;
		
	}


	
	public Object getListener() {
		
		return _listener;
	}

	public void set_listeners(Object listeners) {
		
		_listener = listeners;
	}

	public String get_method() {
		
		return _method;
	}

	public void set_method(String methodName) {
		
		_method = methodName;
	}
	
}
