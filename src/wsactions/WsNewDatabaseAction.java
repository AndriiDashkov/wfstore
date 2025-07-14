
package wsactions;

import static wsmain.WsUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import wsdialogs.WsDatabaseNewDialog;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsUtils;

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
				"wsnewdatabase.png"));
		
		putValue(NAME, getMenusStrs("wsnewDatabaseMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		

		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsDatabaseNewDialog dialog = new WsDatabaseNewDialog(WsUtils.get().getMainWindow());
		
		dialog.setVisible(true);
	}		
}

