
package WsActions;

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsDialogs.WsAboutDialog;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
class  WsAboutMenuAction extends WsAction {
	
	private static final long serialVersionUID = 1L;

	 WsAboutMenuAction() {
		
		super("wsactionabout");
		
		putValue(NAME, getMenusStrs("aboutMenuName"));
		
		
		putValue(AbstractAction.SMALL_ICON, WsUtils.get().getIconFromResource("wsabout.png"));
		
	}
	public void actionPerformed(ActionEvent e) {
		

		WsAboutDialog dialog = new WsAboutDialog(WsUtils.get().getMainWindow());
		
		dialog.setVisible(true);			
		
	}
}
