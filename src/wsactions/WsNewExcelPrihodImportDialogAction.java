
/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

package wsactions;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import wsdialogs.WsExcelImportPrihodDialog;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewExcelPrihodImportDialogAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsNewExcelPrihodImportDialogAction() {
		
		super("wsimportPrihodaction");
		
		putValue(AbstractAction.SMALL_ICON, 
				 WsUtils.get().getIconFromResource( 
							"wsimportExcel.png"));
		
		putValue(NAME, getMenusStrs("newPrihodImportMenuNameCaption"));
		
	}
	
	public void enableAction(WsEventEnable event) {
			
	}
	
	public void actionPerformed(ActionEvent e) {
		
		 WsExcelImportPrihodDialog dialog = new  WsExcelImportPrihodDialog(WsUtils.get().getMainWindow(), 
				getGuiStrs("excelImportPrihodDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}
