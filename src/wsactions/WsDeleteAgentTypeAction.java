
package wsactions;

import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.event.ActionEvent;

import wsdatabase.WsAgentSqlStatements;
import wsdatastruct.WsAgentTypeData;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsevents.WsEventInt;
import wsmain.WsUtils;
import wstables.WsAgentTypesTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class  WsDeleteAgentTypeAction extends WsAction {
	
	private WsAgentTypesTable m_parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		//WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  WsDeleteAgentTypeAction(WsAgentTypesTable parent) {
		
		super("wsdeleteagenttypeaction");
		
		m_parent = parent;
		
		putValue(NAME, getMenusStrs("deleteTableItemTypeMenu"));

	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsAgentTypeData data = m_parent.getSelectedDataAgentType();

		if (data == null || data.id  == -1) {
			
			   WsUtils.showMessageDialog(getMessagesStrs("deleteAgentTypeFailNoSelectionMessage"));
		
			   return;
		}
		
		String operation = getMessagesStrs("messageDeleteAgentTypeApproveMessage");
			
		int res = WsUtils.showYesNoDialog(operation);
	      	   
		if ( 1 == res) {
			
				int num = WsAgentSqlStatements.deleteAgentTypeForId(data.id);
				
				if(num == 1) {
					
			        WsUtils.showMessageDialog(getMessagesStrs("deleteAgentTypeSuccessMessage"));
			          
			        WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED);
						
					WsEventDispatcher.get().fireCustomEvent(event);
				}
				else {
					
					WsUtils.showMessageDialog(getMessagesStrs("deleteAgentTypeFailMessage"));
					
				}
			}
			
		}	
}
