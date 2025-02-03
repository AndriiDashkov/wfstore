
package WsActions;


import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsForms.WsPrihodForm;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsClearFilterPrihodAction extends WsAction {
	
	WsPrihodForm parent = null;
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsClearFilterPrihodAction(WsPrihodForm  f) {
		
		super("wsclearfilterprihodaction");
		
		parent = f;
		
		putValue(AbstractAction.SMALL_ICON, WsUtils.get().getIconFromResource( "wsfilterreset.png"));
		
	}
	
	public void enableAction(WsEventEnable event) {
		
	}
	
	public void actionPerformed(ActionEvent e) {

			parent.clearFilter();
				
	}
	
}
