
package WsActions;

import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsDatabase.WsConnect;
import WsDialogs.WsDatabaseLoadDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsLoadDatabaseAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsLoadDatabaseAction() {
		
		super("wsdatabaseload");
		
		putValue(AbstractAction.SMALL_ICON, 
				WsUtils.get().getIconFromResource( 
				"wsloadbase.png"));
		
		putValue(NAME, getMenusStrs("wsloadDatabaseMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsConnect.get();
		
		if(WsConnect.isDatabaseLoaded()) {
			
			 WsUtils.showMessageDialogLong(getMessagesStrs("databaseHasBeenLoadedMessage0"),
					 getMessagesStrs("databaseHasBeenLoadedMessage1"));
			
		}
		else {
		
			WsDatabaseLoadDialog dialog = new WsDatabaseLoadDialog(WsUtils.get().getMainWindow());
			
			dialog.setVisible(true);
		}
	}		
}


