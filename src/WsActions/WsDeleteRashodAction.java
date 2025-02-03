
package WsActions;

import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import WsDatabase.WsRashodSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsForms.WsRashodForm;
import WsMain.WsUtils;

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
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("deleteRashodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
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
				
		          JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteRashodSuccessMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
			}
			else {
				
				   JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteRashodFailMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
				
			}
		}
	}		
}
