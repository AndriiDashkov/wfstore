
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import WsDialogs.WsNewAgentTypeDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewAgentTypeAction extends WsAction {
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsNewAgentTypeAction() {
		
		super("wsnewagenttypeaction");
		
		putValue(NAME, getMenusStrs("wsnewAgentTypeMenuName"));

	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsNewAgentTypeDialog dialog = new  WsNewAgentTypeDialog(WsUtils.get().getMainWindow(), null,
				getGuiStrs("newAgentTypeDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}
