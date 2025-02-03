
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import WsDataStruct.WsAgentData;
import WsDialogs.WsNewAgentDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsForms.WSContrAgentsForm;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsEditAgentAction extends WsAction {
	
	WSContrAgentsForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsEditAgentAction(WSContrAgentsForm  f) {
		
		super("wseditagentaction");
		
		putValue(AbstractAction.SMALL_ICON,
				WsUtils.get().getIconFromResource("wsagentedit.png"));
		
		parent = f;
		
		putValue(NAME, getMenusStrs("wsEditAgentMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		

	}
	
	public void actionPerformed(ActionEvent e) {
		
		 WsAgentData dt = parent.getAgentDataForEdit();
		
		if (dt.id != -1)  {
		
			WsNewAgentDialog dialog = new WsNewAgentDialog(WsUtils.get().getMainWindow(), 
					dt, getGuiStrs("newAgentDialogWinCaption") );
			
			dialog.setVisible(true);
		}
		else {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("editAgentFailNoSelectionMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
		}
	
	}		
}
