/**
 * 
 */
package WsActions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private String m_name;
	
	protected WsAction(String name) {
		
		super();
		
		m_name = name;
	}

	public String getActName() { return m_name; }

	public void enableAction (WsEventEnable e) {}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}
	/**
	 * Disconnects the action from events framework
	 */
	public void disconnect() {
		
		WsEventDispatcher.get().disconnect(this);
	}

}
