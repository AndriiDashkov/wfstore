
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import WsDialogs.WsNewRashodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsNewRashodAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsNewRashodAction() {
		
		super("wsnewrashodaction");
		
		putValue(NAME, getMenusStrs("newRashodMenuNameCaption"));
		
	}
	
	public void enableAction(WsEventEnable event) {
			
	}
	
	public void actionPerformed(ActionEvent e) {
		
	
		WsNewRashodDialog dialog = new WsNewRashodDialog(WsUtils.get().getMainWindow(), null, 
				getGuiStrs("newRahodDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}
