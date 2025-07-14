
package wsactions;

import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import wsdatabase.WsAgentSqlStatements;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsforms.WsContrAgentsForm;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDeleteAgentAction extends WsAction {
	

	
	WsContrAgentsForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsDeleteAgentAction(WsContrAgentsForm  f) {
		
		super("wsdeleteagentaction");
		
		parent = f;
		
		putValue(AbstractAction.SMALL_ICON, WsUtils.get().getIconFromResource("wsagentdel.png"));
		
		putValue(NAME, getMenusStrs("wsDeleteAgentMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
	
	}
	
	public void actionPerformed(ActionEvent e) {
		
		int id = parent.getSelectedAgentId();
		
		if (id  == -1) {
			 
		   WsUtils.showMessageDialog(getMessagesStrs("deleteAgentFailNoSelectionMessage"));
		   
		   return;
		   
		}
		
		String operation = getMessagesStrs("messageDeleteAgehApproveMessage");
		
		int res = WsUtils.showYesNoDialog(operation);
      	   
		if ( 1 == res) {
	
			int num = WsAgentSqlStatements.deleteAgentForId(id);
			
			if(num == 1) {
				
				parent.refreshData(null);
		          
		        WsUtils.showMessageDialog(getMessagesStrs("deleteAgentSuccessMessage"));
			}
			else {
				
				WsUtils.showMessageDialog(getMessagesStrs("deleteAgentFailMessage")); 
				
			}
		}	
	}		
}
