
package WsActions;

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import WsDialogs.WsHelpBrowser;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
class  WsActionManual extends WsAction {
	
	private static final long serialVersionUID = 1L;

	WsActionManual() {
		
		super("wsactionmanual");
		
		putValue(NAME, getMenusStrs("manualMenuName"));
		
	}
	public void actionPerformed(ActionEvent e) {
		
		WsHelpBrowser dialog = new WsHelpBrowser(WsUtils.get().getMainWindow(), getMenusStrs("manualMenuName"));
		
		dialog.setVisible(true);			
		
	}
}
