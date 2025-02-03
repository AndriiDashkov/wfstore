
package WsActions;

import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsDialogs.WsDatabaseNewDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsNewDatabaseAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public   WsNewDatabaseAction() {
		
		super("wsdatabasenew");
		
		putValue(AbstractAction.SMALL_ICON, 
				WsUtils.get().getIconFromResource(
				"wfnewdatabase.png"));
		
		putValue(NAME, getMenusStrs("wsnewDatabaseMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		

		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsDatabaseNewDialog dialog = new WsDatabaseNewDialog(WsUtils.get().getMainWindow());
		
		dialog.setVisible(true);
	}		
}

