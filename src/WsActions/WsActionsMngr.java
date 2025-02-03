package WsActions;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

import java.util.HashMap;
import javax.swing.AbstractAction;

public class WsActionsMngr {
	
	private static WsActionsMngr _instance;
	
	HashMap<String,AbstractAction> m_map = new HashMap<String,AbstractAction>();	
	
	private WsActionsMngr() {};
	
	public static WsActionsMngr get() {
		
		if (_instance == null) {
			
			_instance = new WsActionsMngr();
		}
		return _instance;
	}
	
	public void initActions () {
		
		register(new WsActionExit());

		register(new WsNewAgentAction());
		
		register(new WsLoadDatabaseAction());
		
		register(new WsNewDatabaseAction());
		
		register(new WsAboutMenuAction());
		
		register(new WsImportRestAction());
		
		register(new WsActionManual());
		
	}
	
	/**
	 * Registers action
	 * @param action - action to register
	 */
	public void register(WsAction action){
		
		m_map.put(action.getActName(), action);
	}
	/**
	 * First this function invokes the disconnect method for action
	 * @param action - action to unregister
	 */
	public void unregister(WsAction action){
		
		action.disconnect();
		
		m_map.remove(action.getActName());
	}
	
	public AbstractAction getAction(String name){
		
		return m_map.get(name);
	}
}
