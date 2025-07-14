
package wsactions;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;

import java.awt.event.ActionEvent;

import wsdialogs.WsImportOldBase;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsUtils;

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