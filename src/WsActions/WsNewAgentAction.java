
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsDialogs.WsNewAgentDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsNewAgentAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsNewAgentAction() {
		
		super("wsnewagentaction");
		
		putValue(AbstractAction.SMALL_ICON, 
				WsUtils.get().getIconFromResource("wsnewagent.png"));
		
		putValue(NAME, getMenusStrs("wsnewAgentMenuName"));


	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsNewAgentDialog dialog = new WsNewAgentDialog(WsUtils.get().getMainWindow(), null, getGuiStrs("newAgentDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}

