
package wsforms;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import wsactions.WsAgentsTypesAction;
import wsactions.WsDeleteAgentAction;
import wsactions.WsEditAgentAction;
import wsactions.WsNewAgentAction;
import wscontrols.WsAgentTypesFilterComboBox;
import wsdatastruct.WsAgentData;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsGuiTools;
import wsmain.WsUtils;
import wstables.WsAgentListTable;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsContrAgentsForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
			
	}
	
	protected ButtonGroup butGroup = new ButtonGroup();
	
	protected  JButton m_agentsTypesButton = new JButton(getGuiStrs("buttonAgentTypesCaption"));
	
	protected WsAgentListTable m_table = new WsAgentListTable();
	
	protected WsAgentTypesFilterComboBox m_typesCombo = new WsAgentTypesFilterComboBox();
	
	protected JLabel m_comboLabel = new JLabel(getGuiStrs("typesComboBoxLabel"));
	
	JMenuItem m_itemEdit = null;
	
	JMenuItem m_itemDelete = null;
	   
	JMenuItem m_itemAdd = null;
	
	JMenuItem m_itemNameSort = null;
	   
	JMenuItem m_itemTypeSort = null;
	
	JPopupMenu m_popupMenu = null;
	
	JMenu m_sortMenu = null;
	 
	TitledBorder m_title = null;

	public WsContrAgentsForm() {
		
		createGUI();
		
		setPopupMenu();
		
		setCustomFont();
		
	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel toolbar_panel = WsGuiTools.createHorizontalPanel();
		
		WsGuiTools.setFixedSizeBehavior(m_typesCombo);
		
		WsGuiTools.setComponentFixedWidth(m_typesCombo, 150);
		
		JPanel filterPanel = WsGuiTools.createHorizontalPanel();
		
		m_title = BorderFactory.createTitledBorder(getGuiStrs("filterBorderTitle"));
		
		filterPanel.setBorder(m_title);
		
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
		
		setToolTips();
		
	}
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED || e.getType() == WsEventEnable.TYPE.AGENTS_DATA_CHANGED) {
			
			
			int index = m_typesCombo.getCurrentSQLId();
	 
			m_table.refreshData(index, 0);
			
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
	    		 
				m_table.refreshData(index, 0);
	          
	       }
	    }

	      
	}
	
	
	private void setPopupMenu() {
		
	   m_popupMenu = new JPopupMenu();

	   m_itemEdit = new JMenuItem(new WsEditAgentAction(this));
	   
	   m_itemDelete = new JMenuItem(new WsDeleteAgentAction(this));
	   
	   m_itemAdd = new JMenuItem( new WsNewAgentAction());
	   
	   m_sortMenu = new JMenu(getMenusStrs("agSortMenu"));
       
       m_popupMenu.add(m_itemAdd);
        
       m_popupMenu.add(m_itemEdit);
        
       m_popupMenu.add(m_itemDelete);
       
       m_popupMenu.add(m_sortMenu);
       
       m_itemNameSort = new JMenuItem(getMenusStrs("agSortNameMenu"));
	   
   	   m_itemTypeSort = new JMenuItem(getMenusStrs("agSortTypeMenu"));
   	   
   	   m_sortMenu.add(m_itemNameSort);
   	   
   	   m_sortMenu.add(m_itemTypeSort);
   	   
	   CustomPopupListener listener = new CustomPopupListener();
	   
	   m_itemTypeSort.addActionListener( listener);
	   
	   m_itemNameSort.addActionListener( listener);
        
       m_table.setComponentPopupMenu(m_popupMenu);
        
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 
		
		int margin_table = WsUtils.get().getTableMarginShiftForFont();
		
		m_table.setRowHeight(m_table.getRowHeight() + margin_table);
		
		WsGuiTools.changeFont(m_table, f);
		
		WsGuiTools.changeFont(m_popupMenu,f);
		
		m_sortMenu.setFont(f);
		
		m_itemNameSort.setFont(f);
		   
		m_itemTypeSort.setFont(f);

		m_table.getTableHeader().setFont(f);
		
		m_title.setTitleFont(f);
		
	}
	
	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
               if (menu ==  m_itemNameSort) {
            	   
            	   int index = m_typesCombo.getCurrentSQLId();

            	   m_table.refreshData(index, 0);
                   
               } else if (menu == m_itemTypeSort) {
            	   
            	   int index = m_typesCombo.getCurrentSQLId();
            	   
            	   m_table.refreshData(index, 1);
                   
               } 
		}
	};
	
	
	private void setToolTips() {
		
		m_agentsTypesButton.setToolTipText(getMessagesStrs("agTypebUtTt"));
		
		m_typesCombo.setToolTipText(getMessagesStrs("typesAgComboTt"));
		
	}
	
}