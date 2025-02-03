

package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import WsDialogs.WsNewPrihodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class  WsNewPrihodAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  WsNewPrihodAction() {
		
		super("wsnewprihodaction");
		
		putValue(NAME, getMenusStrs("newPrihodMenuNameCaption"));

	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
	
		WsNewPrihodDialog dialog = new WsNewPrihodDialog(WsUtils.get().getMainWindow(), null, 
				getGuiStrs("newOrderDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}
