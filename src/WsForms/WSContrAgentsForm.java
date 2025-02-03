
package WsForms;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import WsActions.WsDeleteAgentAction;
import WsActions.WsAgentsTypesAction;
import WsActions.WsEditAgentAction;
import WsActions.WsNewAgentAction;
import WsControls.WsAgentTypesFilterComboBox;
import WsDataStruct.WsAgentData;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsTables.WsAgentListTable;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSContrAgentsForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
			
	}
	
	protected ButtonGroup butGroup = new ButtonGroup();
	
	protected JRadioButton m_rButton1 = new JRadioButton(getGuiStrs("radioButton1AllAgentsLabel")); //

	protected JRadioButton m_rButton2 = new JRadioButton(getGuiStrs("radioButton2PostAgentsLabel")); //

	protected JRadioButton m_rButton3 = new JRadioButton(getGuiStrs("radioButton3PidrozdilAgentsLabel")); //
	protected  JButton m_agentsTypesButton = new JButton(getGuiStrs("buttonAgentTypesCaption"));
	
	protected WsAgentListTable m_table = new WsAgentListTable();
	
	protected WsAgentTypesFilterComboBox m_typesCombo = new WsAgentTypesFilterComboBox();
	
	protected JLabel m_comboLabel = new JLabel(getGuiStrs("typesComboBoxLabel"));
	
	JMenuItem m_itemEdit = null;
	
	JMenuItem m_itemDelete = null;
	   
	JMenuItem m_itemAdd = null;

	public WSContrAgentsForm() {
		
		createGUI();
		
		setPopupMenu();
		
	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel toolbar_panel = WsGuiTools.createHorizontalPanel();
		
		WsUtils.get().setFixedSizeBehavior(m_typesCombo);
		
		WsGuiTools.setComponentFixedWidth(m_typesCombo, 150);
		
		JPanel filterPanel = WsGuiTools.createHorizontalPanel();
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("filterBorderTitle"));
		
		filterPanel.setBorder(title);
		
		filterPanel.add(m_comboLabel);
		
		filterPanel.add(m_typesCombo);
		
		toolbar_panel.add(filterPanel);
				
		toolbar_panel.add(m_agentsTypesButton);
		
		toolbar_panel.add(Box.createHorizontalGlue());
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		mainPanel.add(toolbar_panel);
		mainPanel.add(scroll);
		
		setLayout(new BorderLayout());
		
		m_agentsTypesButton.setAction(new WsAgentsTypesAction());
		
		m_typesCombo.addItemListener(new ItemChangeListener());
		
		add(mainPanel);
		
	}
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED || e.getType() == WsEventEnable.TYPE.AGENTS_DATA_CHANGED) {
			
			
			int index = m_typesCombo.getCurrentSQLId();
	 
			m_table.refreshData(index);
			
		}
		
	}
	
	public int getSelectedAgentId() {
		
		int id = m_table.getSelectedId();
		
		return id;
		
	}
	
	public WsAgentData getAgentDataForEdit() {
		
		return m_table.getSelectedDataAgent();
		
	}
	
	class ItemChangeListener implements ItemListener{
		
	    @Override
	    public void itemStateChanged(ItemEvent event) {
	    	
	       if (event.getStateChange() == ItemEvent.SELECTED) {
	    	   
	    	   int index = m_typesCombo.getCurrentSQLId();
	    		 
				
				m_table.refreshData(index);
	          
	       }
	    }

	      
	}
	
	
	private void setPopupMenu() {
		
	   JPopupMenu m_popupMenu = new JPopupMenu();

	   m_itemEdit = new JMenuItem(new WsEditAgentAction(this));
	   
	   m_itemDelete = new JMenuItem(new WsDeleteAgentAction(this));
	   
	   m_itemAdd = new JMenuItem( new WsNewAgentAction());
       
       m_popupMenu.add(m_itemAdd);
        
       m_popupMenu.add(m_itemEdit);
        
       m_popupMenu.add(m_itemDelete);
        
       m_table.setComponentPopupMenu(m_popupMenu);
        
	}
}