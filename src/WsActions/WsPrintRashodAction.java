
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import WsDataStruct.WsRashodData;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsForms.WsRashodForm;
import WsMain.WsUtils;
import WsReports.WsRashNaklReport;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsPrintRashodAction extends WsAction {
	

	
	WsRashodForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsPrintRashodAction(WsRashodForm  f) {
		
		super("wsprintrashodaction");
		
		parent = f;
		
		putValue(AbstractAction.SMALL_ICON, 
				WsUtils.get().getIconFromResource(
						"wsprint.png"));
		
		putValue(NAME, getMenusStrs("wsPrintRashodMenuName"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		int id = parent.getSelectedRashodId();
		
		if (id  == -1) {
			
		   WsUtils.showMessageDialog( getMessagesStrs("printRashodFailNoSelectionMessage"));
		   
		   return;
		   
		}

		Vector<WsRashodData> dt = parent.getSelectedRashodDatas();
		
		if(dt != null) {
				
			WsRashNaklReport dialog = new WsRashNaklReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("newPrintDialogWinCaption"), dt);
			
			
			dialog.setVisible(true);
		}
			
	}		
}
