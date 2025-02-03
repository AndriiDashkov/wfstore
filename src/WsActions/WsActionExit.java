
package WsActions;

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import WsDatabase.WSConnect;
import WsEvents.WsEvent;
import WsEvents.WsEventDispatcher;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
class WsActionExit extends WsAction {
	
	private static final long serialVersionUID = 1L;

	WsActionExit() {
		
		super("wfactionexit");
		
		putValue(AbstractAction.SMALL_ICON, 
				WsUtils.get().getIconFromResource("wfexit.png"));
		
		putValue(NAME, getMenusStrs("exitMenuName"));
		
	}
	public void actionPerformed(ActionEvent e) {
		
			//to give a last chance to save something
			WsEventDispatcher.get().fireCustomEvent( new WsEvent(WsEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT));
		
			WsUtils.get().getMainWindow ().dispose();
			
			try {
				
				WSConnect.get();
				
				if(WSConnect.getCurrentConnection() != null) {
				
					WSConnect.getCurrentConnection().close();
				}
				
			} catch (SQLException e1) {
		
				if( WsUtils.isDebug() ) {
					e1.printStackTrace();
				
				}
			}
			
			System.exit(0);				
		
	}
}
