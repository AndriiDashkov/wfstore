
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import WsDataStruct.WsRashodData;
import WsDialogs.WsNewRashodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsForms.WsRashodForm;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsEditRashodAction extends WsAction {
	
	WsRashodForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  WsEditRashodAction(WsRashodForm  f) {
		
		super("wseditrashodaction");
		
		parent = f;
		
		putValue(NAME, getMenusStrs("wsEditRashoddMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		
		WsRashodData dt = parent.getRashodDataForEdit();
		
		if (dt != null && dt.id != -1)  {
		
			WsNewRashodDialog dialog = new WsNewRashodDialog(WsUtils.get().getMainWindow(), 
					dt, getGuiStrs("wsEditRashodDialogCaption") );
			
			dialog.setVisible(true);
		}
		else {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("editRashodFailNoSelectionMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
		}
	}		
}
