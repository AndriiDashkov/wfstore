
package wsactions;

import static wsmain.WsUtils.*;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;

import wsdatabase.WsConnect;
import wsevents.WsEvent;
import wsevents.WsEventDispatcher;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
class WsActionExit extends WsAction {
	
	private static final long serialVersionUID = 1L;

	WsActionExit() {
		
		super("wfactionexit");
		
		putValue(AbstractAction.SMALL_ICON, 
				WsUtils.get().getIconFromResource("wsexit.png"));
		
		putValue(NAME, getMenusStrs("exitMenuName"));
		
	
	}
	public void actionPerformed(ActionEvent e) {
		
			//to give a last chance to save something
			WsEventDispatcher.get().fireCustomEvent( new WsEvent(WsEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT));
		
			WsUtils.get().getMainWindow ().dispose();
			
			try {
				
				WsConnect.get();
				
				if(WsConnect.getCurrentConnection() != null) {
				
					WsConnect.getCurrentConnection().close();
				}
				
			} catch (SQLException e1) {
		
				if( WsUtils.isDebug() ) {
					e1.printStackTrace();
				
				}
			}
			
			System.exit(0);				
		
	}
}
