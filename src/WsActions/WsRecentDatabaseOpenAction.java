package WsActions;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import WsDatabase.WsConnect;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEvent;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRecentDatabaseOpenAction extends WsAction {

	String m_fullPath = "";
	
	private static final long serialVersionUID = 1L;
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "enableAction");
	}
	
	public WsRecentDatabaseOpenAction(String  fullPath) {
		
		super("wsdeleteagentaction");
		
		 m_fullPath =  fullPath;
		
		putValue(NAME,  m_fullPath);
		
	}
	
	public void enableAction(WsEventEnable event) {
	
	}
	
	public void actionPerformed(ActionEvent e) {
		
		
		WsConnect.get();
		
		if(WsConnect.isDatabaseLoaded()) {
			
			WsUtils.showMessageDialogLong(getMessagesStrs("databaseHasBeenLoadedMessage0"),
					 getMessagesStrs("databaseHasBeenLoadedMessage1"));
			
		}
		else {
			
        	if(! WsUtils.isFileExists(m_fullPath) ) {
        		
        		int res = WsUtils.showYesNoDialog(getMessagesStrs("noRecentDataBaseMessage"));
           	   
        		if ( 1 == res) {
        			
        			WsUtils.get().getSettings().removeRecentData(m_fullPath);
        			
        			WsEvent ev = new WsEvent( WsEventDispatcher.REFRECH_RECENT_MENU_EVENT);
        			
        			WsEventDispatcher.get().fireCustomEvent(ev);
        			
        		}
        		
        		return;
        		
        	}
		
			WsConnect.connect(m_fullPath);
			
			WsUtils.get().getSettings().setRecentData(m_fullPath);
			
			WsUtils.NEW_CATALOG = WsUtilSqlStatements.isCatalogInDatabaseNew();
		
		}
	}		
}
