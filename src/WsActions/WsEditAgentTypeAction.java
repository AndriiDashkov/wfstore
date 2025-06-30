
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import WsDataStruct.WsAgentTypeData;
import WsDialogs.WsNewAgentTypeDialog;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;
import WsTables.WsAgentTypesTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsEditAgentTypeAction extends WsAction {
	
	private WsAgentTypesTable m_parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		//WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsEditAgentTypeAction(WsAgentTypesTable parent) {
		
		super("wseditagenttypeaction");
		
		m_parent = parent;
		
		putValue(NAME, getMenusStrs("editTableItemTypeMenu"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsAgentTypeData data = m_parent.getSelectedDataAgentType();
		
		if (data != null && data.id != -1) {
		
			WsNewAgentTypeDialog dialog = new  WsNewAgentTypeDialog(WsUtils.get().getMainWindow(), 
					data,
					getGuiStrs("editAgentTypeDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else {
			
			String  operationMessage =  getMessagesStrs("noSelectedAgentTypeForEditMessage");
			
			WsUtils.showMessageDialog(operationMessage);
			
		}
	
	}		
}
