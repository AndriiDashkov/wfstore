
package WsDialogs;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import WsActions.WsDeleteAgentTypeAction;
import WsActions.WsEditAgentTypeAction;
import WsActions.WsNewAgentTypeAction;
import WsDataStruct.WsAgentTypeData;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsGuiTools;
import WsTables.WsAgentTypesTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentsTypesDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
	
			
	}
	
	protected  JButton m_editButton = new JButton(getGuiStrs("buttonEditCaption"));
	
	protected  JButton m_newButton = new JButton(getGuiStrs("buttonNewCaption"));
	
	protected  JButton m_deleteButton = new JButton(getGuiStrs("buttonDeleteCaption"));
	
	protected WsAgentTypesTable m_table = new WsAgentTypesTable();
	
	public WsAgentsTypesDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		createGUI();
		
		pack();
		
		setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel toolbar_panel = WsGuiTools.createHorizontalPanel();
	
		toolbar_panel.add(Box.createHorizontalGlue());
		
		toolbar_panel.add(m_editButton);
		
		toolbar_panel.add(m_newButton);
		
		toolbar_panel.add(m_deleteButton);
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		mainPanel.add(toolbar_panel);
		mainPanel.add(scroll);
		
		setLayout(new BorderLayout());
		
		m_newButton.setAction(new WsNewAgentTypeAction());
		
		m_editButton.setAction(new WsEditAgentTypeAction(this));
		
		m_deleteButton.setAction(new WsDeleteAgentTypeAction(this));
		
		
		add(mainPanel);
		
		refreshData(null);
		
	}
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED || 
						e.getType() == WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED) {
			
			m_table.refreshData();
			
		}
		
	}
	
	public int getSelectedId() {
		
		int id = m_table.getSelectedId();
		
		return id;
		
	}
	
	public WsAgentTypeData getAgentTypeDataForEdit() {
		
		return m_table.getSelectedDataAgentType();
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
	
	}
}