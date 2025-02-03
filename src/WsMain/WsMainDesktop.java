package WsMain;


import static WsMain.WsUtils.getMenusStrs;
import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.WindowConstants;
import WsActions.WsActionsMngr;
import WsActions.WsRecentDatabaseOpenAction;
import WsDatabase.WSConnect;
import WsEvents.WsEvent;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;


/**
 *  The main class to start the application.
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsMainDesktop extends JFrame {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshView");
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.REFRECH_RECENT_MENU_EVENT, this, "refreshRecentMenu");
	}
	
	private static final long serialVersionUID = 1L;

	static final int DEFAULT_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	
	static final int DEFAULT_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	private JDesktopPane desktopPane;

	private WsCentralControlPanel m_viewPanel;

	JMenuItem m_itemNewDatabase = null;

	JMenuItem m_itemLoadDatabase = null;
	
	JMenuItem m_itemImportDatabase = null;
	
	JMenu m_recentMenu = null;

	public WsMainDesktop () {
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	
		WsUtils.get().setMainWindow(this);

		initUser();

		try {
			
			WsUtils.get().initSettings ();
			
		} catch (IOException e) {
			
			System.out.print("IOException : can't init application " + WsUtils.NEXT_ROW + e.getMessage());
			
			System.exit(1);
		}

		WsEventDispatcher.get(); //event dispatcher
		
		WsActionsMngr.get().initActions(); 

		WsLog.get();
		
		setTitle("WFStore " + WsUtils.VERSION + " ");
		
		
		setIconImage(WsUtils.get().getIconFromResource("wsapp.png").getImage());
	
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		desktopPane = new JDesktopPane();
		
		desktopPane.setLayout(new BorderLayout()); 
		
		m_viewPanel = new WsCentralControlPanel();

		desktopPane.add(m_viewPanel);
		
		add(desktopPane, BorderLayout.CENTER);
			
		JMenuBar menuBar = new JMenuBar(); 	
		
		setJMenuBar(menuBar);	
		
		menuBar.add(createMenuDatabase());
		
		menuBar.add(createMenuAbout());

		menuBar.add(Box.createHorizontalGlue());
		
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				//to give a last chance to save something
				WsEventDispatcher.get().fireCustomEvent( new WsEvent(WsEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT));
				
				dispose();
				
				try {
					
					WSConnect.get();
					
					if(WSConnect.getCurrentConnection() != null) {
						
							WSConnect.getCurrentConnection().close();
					}
					
				} catch (SQLException e1) {
			
					e1.printStackTrace();
				}
				
				System.exit(0);	
					
			}
		});		
	}
	
	
	public void initUser() {
		
		WsUtils.get().setWorkDir(".");
		
		WsUtils.get().setHomeDir(".");
		
	}
	
	public static int getDefaultWidth() {
		
		return DEFAULT_WIDTH;
	}

	public static int getDefaultHeight() {
		
		return DEFAULT_HEIGHT;
	}
	
	@SuppressWarnings("unused")
	public void refreshRecentMenu(WsEvent e) {
		
		if(e.get_EventType() != WsEventDispatcher.REFRECH_RECENT_MENU_EVENT) { return; }
		
		 refreshRecentMenuBase();
		
	}
	
	private void refreshRecentMenuBase() {
		

		m_recentMenu.removeAll();
		
		ArrayList<String> rList = WsUtils.get().getSettings().getRecentData();
		
		for(String s : rList) {
			
			JMenuItem item = new JMenuItem(new WsRecentDatabaseOpenAction(s));
			
			m_recentMenu.add(item);
		}
		
	}
	
	public JMenu createMenuDatabase () {
		
		JMenu database = new JMenu(WsUtils.getMenusStrs("databaseMainMenuCaption")); 

		m_itemNewDatabase = new JMenuItem (WsActionsMngr.get().getAction("wsdatabasenew"));

		m_itemLoadDatabase = new JMenuItem(WsActionsMngr.get().getAction("wsdatabaseload"));
		
		m_itemImportDatabase = new JMenuItem(WsActionsMngr.get().getAction("wsdatabaseimport"));
		
		m_recentMenu = new JMenu(getMenusStrs("recentNameMenu") + " ");
		
		refreshRecentMenuBase();

		database.add(m_itemLoadDatabase);
		
		database.add(m_itemNewDatabase);
		
		database.add(m_itemImportDatabase);
		
		m_itemImportDatabase.setEnabled(false);
		
		database.addSeparator();
		
		database.add(m_recentMenu);
		
		database.addSeparator();
		
		JMenuItem exit = new JMenuItem(WsActionsMngr.get().getAction("wfactionexit"));

		database.add(exit);
				
		return database;
	}
	
	
	public JMenu createMenuAbout() {
		
		JMenu about = new JMenu(WsUtils.getMenusStrs("abouteMainMenuCaption")); 

		JMenuItem itemAbout = new JMenuItem (WsActionsMngr.get().getAction("wsactionabout"));
		
		JMenuItem itemManual = new JMenuItem (WsActionsMngr.get().getAction("wsactionmanual"));

		about.add(itemManual);
		
		about.add(itemAbout);
		
		return about;
	}
	
	public JMenu createMenuPrihod () {
		
		JMenu prihodMenu = new JMenu(getMenusStrs("wsPrihodMainMenuName")); 

		JMenuItem itemNewPrihod = new JMenuItem (WsActionsMngr.get().getAction("wsnewprihodaction"));

		JMenuItem itemPrihodsList = new JMenuItem (WsActionsMngr.get().getAction( "wsprihodlistaction"));

		prihodMenu.add(itemPrihodsList);
		
		prihodMenu.add(itemNewPrihod);
		
		prihodMenu.addSeparator();
		
		return prihodMenu;
	}
	

	public JMenu createMenuRashod () {
		
		JMenu rashodMenu = new JMenu(WsUtils.getMenusStrs("wsrashodMainMenuName")); 

		JMenuItem itemNewRashod = new JMenuItem (WsActionsMngr.get().getAction("wsrashodnewaction"));

		JMenuItem itemRAshodList = new JMenuItem (WsActionsMngr.get().getAction("wsrashodlistaction"));
		
		itemRAshodList.setMnemonic(KeyEvent.getExtendedKeyCodeForChar(getMenusStrs("altKeyEditSubjectMenu").charAt(0)));
		
		rashodMenu.add(itemNewRashod);
		
		rashodMenu.add(itemRAshodList);
	
		rashodMenu.addSeparator();
	
		return rashodMenu;
	}
	

	public JMenu createMenuContragents() {
		
		JMenu contragentsMenu = new JMenu(WsUtils.getMenusStrs("wsContrAgentsMainMenuName")); 

		JMenuItem itemNewAgent = new JMenuItem (WsActionsMngr.get().getAction("wsnewagentaction"));

		JMenuItem itemAgentsList = new JMenuItem (WsActionsMngr.get().getAction("wsagentslistaction"));
	
		contragentsMenu.add(itemNewAgent);
		
		contragentsMenu.add(itemAgentsList);
		
		return contragentsMenu;
	}
	

	public JMenu createMenuNomenclatura () {
		
		JMenu nomenclaturaMenu = new JMenu( WsUtils.getMenusStrs("wsSkladMainMenuName") ); 

		JMenuItem itemNewStoreItem = new JMenuItem (WsActionsMngr.get().getAction("wsnewstoreitemtaction"));

		JMenuItem itemListGoodsItem = new JMenuItem (WsActionsMngr.get().getAction("wsaliststoreitemsaction"));

		JMenuItem itemStoкeStateItem = new JMenuItem (WsActionsMngr.get().getAction("wsskladaction"));

		nomenclaturaMenu.add(itemNewStoreItem);
		
		nomenclaturaMenu.add(itemListGoodsItem);
		
		nomenclaturaMenu.add(itemStoкeStateItem);
			
		return nomenclaturaMenu;
	}
	
	

	public static void main(String[] args) {

		JFrame.setDefaultLookAndFeelDecorated(true);
		
		//UIManager.put("activeCaption", new javax.swing.plaf.ColorUIResource(Color.GREEN));

		JDialog.setDefaultLookAndFeelDecorated(true);
		
		EventQueue.invokeLater(new Runnable() {		
			
			@Override
			public void run() {			
				
				new WsMainDesktop();
				
			}
		});
	}
	
	
	public void refreshView(WsEventEnable e) {
		
		if(e != null && e.getType() == WsEventEnable.TYPE.DATABASE_LOADED
				) {
			
			
			m_itemImportDatabase.setEnabled(true);
			
		}
		
	}

}
