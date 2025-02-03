
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import WsDialogs.WsImportOldBase;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsImportRestAction  extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  WsImportRestAction() {
		
		super("wsdatabaseimport");
		
		putValue(NAME, getMenusStrs("wsimportRestDatabaseMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsImportOldBase  dialog = new WsImportOldBase (WsUtils.get().getMainWindow(),
				getGuiStrs("importRestDialogCaption"));
		
		dialog.setVisible(true);
	}		
}