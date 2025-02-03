
package WsActions;

import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsForms.WSContrAgentsForm;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDeleteAgentAction extends WsAction {
	

	
	WSContrAgentsForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsDeleteAgentAction(WSContrAgentsForm  f) {
		
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
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("deleteAgentFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
		
		String operation = getMessagesStrs("messageDeleteAgehApproveMessage");
		
		int res = WsUtils.showYesNoDialog(operation);
      	   
		if ( 1 == res) {
	
			int num = WSAgentSqlStatements.deleteAgentForId(id);
			
			if(num == 1) {
				
				parent.refreshData(null);
				
		          JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteAgentSuccessMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
			}
			else {
				
				   JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteAgentFailMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
				
			}
		}	
	}		
}
