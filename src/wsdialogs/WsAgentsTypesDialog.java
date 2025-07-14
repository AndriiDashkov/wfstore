
package wsdialogs;

import static wsmain.WsUtils.getGuiStrs;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import wsdatastruct.WsAgentTypeData;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsGuiTools;
import wsmain.WsUtils;
import wstables.WsAgentTypesTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentsTypesDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, 
				"refreshData");
	
			
	}
	
	protected WsAgentTypesTable m_table = new WsAgentTypesTable();
	
	public WsAgentsTypesDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		createGUI();
		
		pack();
		
		setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setCustomFont();
		
	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel toolbar_panel = WsGuiTools.createHorizontalPanel();
		
		toolbar_panel.add(new JLabel(getGuiStrs("tableAgentsTypesCap")));
		
		toolbar_panel.add(Box.createHorizontalGlue());
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		mainPanel.add(toolbar_panel);
		
		mainPanel.add(scroll);
		
		setLayout(new BorderLayout());
				
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
		
		WsEventDispatcher.get().disconnect(m_table);
		
		super.dispose();
	
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}
}