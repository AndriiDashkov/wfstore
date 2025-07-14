
package wsactions;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;

import wsdatastruct.WsRashodData;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsforms.WsRashodForm;
import wsmain.WsUtils;
import wsreports.WsRashNaklReport;

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
