
package wsactions;

import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.event.ActionEvent;

import wsdatabase.WsRashodSqlStatements;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsevents.WsPrihodInvoiceChangedEvent;
import wsforms.WsRashodForm;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDeleteRashodAction extends WsAction {
	
	WsRashodForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsDeleteRashodAction(WsRashodForm  f) {
		
		super("wsdeleterashodaction");
		
		parent = f;
		
		putValue(NAME, getMenusStrs("wsDeleteRashodMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
			
	}
	
	public void actionPerformed(ActionEvent e) {
		
		int id = parent.getSelectedRashodId();
		
		if (id  == -1) {
			
		   WsUtils.showMessageDialog(getMessagesStrs("deleteRashodFailNoSelectionMessage"));
		   
		   return;
		   
		}
		
		String operation = getMessagesStrs("messageDeleteRashodApproveMessage");
				
		int res = WsUtils.showYesNoDialog(operation);
	      	   
		if ( 1 == res) {
	
			int num = WsRashodSqlStatements.deleteRashod(id);
			
			if(num > 0) {
				
				parent.refreshData(null);
				
				WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();

				WsEventDispatcher.get().fireCustomEvent(ev);
				
		        WsUtils.showMessageDialog(  getMessagesStrs("deleteRashodSuccessMessage"));
			}
			else {

				WsUtils.showMessageDialog(getMessagesStrs("deleteRashodFailMessage"));
				
			}
		}
	}		
}
