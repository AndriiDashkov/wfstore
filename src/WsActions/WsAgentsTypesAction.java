
package WsActions;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsDialogs.WsAgentsTypesDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsAgentsTypesAction extends WsAction { //arrow.png
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public  WsAgentsTypesAction() {
		
		super("wsnewagentaction");
		
		putValue(AbstractAction.SMALL_ICON, 
				 WsUtils.get().getIconFromResource( 
							"wsagenttype.png"));

		putValue(NAME, getMenusStrs("wsnAgentTypesMenuName"));
		

	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		WsAgentsTypesDialog dialog = new WsAgentsTypesDialog(WsUtils.get().getMainWindow(), 
				getGuiStrs("agentTypesDialogWinCaption"));
		
		dialog.setVisible(true);
	
	}		
}