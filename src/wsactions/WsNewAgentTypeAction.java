
package wsactions;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;

import java.awt.event.ActionEvent;

import wsdialogs.WsNewAgentTypeDialog;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsUtils;

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
		
		putValue(NAME, getMenusStrs("addTableItemTypeMenu"));

	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsNewAgentTypeDialog dialog = new  WsNewAgentTypeDialog(WsUtils.get().getMainWindow(), null,
				getGuiStrs("newAgentTypeDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}
