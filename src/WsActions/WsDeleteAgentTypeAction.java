
package WsActions;

import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import WsDataStruct.WsAgentTypeData;
import WsDatabase.WSAgentSqlStatements;
import WsDialogs.WsAgentsTypesDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class  WsDeleteAgentTypeAction extends WsAction {
	
	private WsAgentsTypesDialog parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  WsDeleteAgentTypeAction(WsAgentsTypesDialog p) {
		
		super("wsdeleteagenttypeaction");
		
		parent = p;
		
		putValue(NAME, getMenusStrs("wsDeleteAgentTypeMenuName"));

	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		
		int id = parent.getSelectedId();
		
		if (id  == -1) {
			
			   JOptionPane.showMessageDialog(
    			    WsUtils.get().getMainWindow(),
    			    getMessagesStrs("deleteAgentTypeFailNoSelectionMessage"),
    			    getMessagesStrs("messageInfoCaption"),
    			    JOptionPane.CLOSED_OPTION);
		
			   return;
		}
		
		String operation = getMessagesStrs("messageDeleteAgentTypeApproveMessage");
			
		int res = WsUtils.showYesNoDialog(operation);
	      	   
		if ( 1 == res) {
			
				WsAgentTypeData dt = parent.getAgentTypeDataForEdit();
				
				int num = WSAgentSqlStatements.deleteAgentTypeForId(dt.id);
				
				if(num == 1) {
					
					parent.refreshData(null);
					
			          JOptionPane.showMessageDialog(
		        			    WsUtils.get().getMainWindow(),
		        			    getMessagesStrs("deleteAgentTypeSuccessMessage"),
		        			    getMessagesStrs("messageInfoCaption"),
		        			    JOptionPane.CLOSED_OPTION);
			          
			          WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED);
						
					WsEventDispatcher.get().fireCustomEvent(event);
				}
				else {
					
					   JOptionPane.showMessageDialog(
		        			    WsUtils.get().getMainWindow(),
		        			    getMessagesStrs("deleteAgentTypeFailMessage"),
		        			    getMessagesStrs("messageInfoCaption"),
		        			    JOptionPane.CLOSED_OPTION);
					
				}
			}
			
		}	
}
