
package wsactions;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import wsdatastruct.WsAgentData;
import wsdialogs.WsNewAgentDialog;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsforms.WsContrAgentsForm;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsEditAgentAction extends WsAction {
	
	WsContrAgentsForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsEditAgentAction(WsContrAgentsForm  f) {
		
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
			
			 WsUtils.showMessageDialog(getMessagesStrs("editAgentFailNoSelectionMessage"));
		}
	
	}		
}
