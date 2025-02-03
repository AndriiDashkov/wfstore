
/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsDialogs.WFExcelImportPrihodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WFNewExcelPrihodImportDialogAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WFNewExcelPrihodImportDialogAction() {
		
		super("wsimportPrihodaction");
		
		putValue(AbstractAction.SMALL_ICON, 
				 WsUtils.get().getIconFromResource( 
							"wsimportExcel.png"));
		
		putValue(NAME, getMenusStrs("newPrihodImportMenuNameCaption"));
		
	}
	
	public void enableAction(WsEventEnable event) {
			
	}
	
	public void actionPerformed(ActionEvent e) {
		
		 WFExcelImportPrihodDialog dialog = new  WFExcelImportPrihodDialog(WsUtils.get().getMainWindow(), 
				getGuiStrs("excelImportPrihodDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}
