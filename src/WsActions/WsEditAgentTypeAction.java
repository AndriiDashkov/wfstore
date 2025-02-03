
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import WsDialogs.WsAgentsTypesDialog;
import WsDialogs.WsNewAgentTypeDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsEditAgentTypeAction extends WsAction {
	
	private WsAgentsTypesDialog parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsEditAgentTypeAction(WsAgentsTypesDialog p) {
		
		super("wseditagenttypeaction");
		
		parent = p;
		
		putValue(NAME, getMenusStrs("wsEditAgentTypeMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if (parent.getSelectedId() != -1) {
		
			WsNewAgentTypeDialog dialog = new  WsNewAgentTypeDialog(WsUtils.get().getMainWindow(), 
					parent. getAgentTypeDataForEdit(),
					getGuiStrs("editAgentTypeDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else {
			
			String  operationMessage =  getMessagesStrs("noSelectedAgentTypeForEditMessage");
			
			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			    operationMessage,
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
			
		}
	
	}		
}
