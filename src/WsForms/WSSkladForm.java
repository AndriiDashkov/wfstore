
package WsForms;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsReports.WsSkladStateReport;
import WsTables.WsSkladTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSSkladForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED, this, "refreshData2");

		WsEventDispatcher.get().addConnect(WsEventDispatcher.SALE_INVOICE_HAS_BEEN_CHANGED, this, "refreshData3");
		
		
	}
	
	private JRadioButton m_radioDateSort = new JRadioButton(getGuiStrs("skladDateSortRadioButtonLabel"));
	
	private JRadioButton m_radioTypeNameSort = new JRadioButton(getGuiStrs("skladTypeNameSortRadioButtonLabel"));

	private JRadioButton m_radioNameSort = new JRadioButton(getGuiStrs("skladNameRadioButtonLabel"));
	
	private JRadioButton m_contrNameSort = new JRadioButton(getGuiStrs("skladContrRadioButtonLabel"));
	
	private JRadioButton m_radioJoinKodSort = new JRadioButton(getGuiStrs("skladCombineKodRadioButtonLabel"));

	private ButtonGroup m_butGroup = new ButtonGroup();

	JCheckBox m_checkBoxAllKods = new JCheckBox(getGuiStrs("useAllKodsForReports"));
	
	protected JButton m_genButton = new JButton(getGuiStrs("createSkladReportButton"), 
			WsUtils.get().getIconFromResource("wsreportgeneration.png"));
	
	WsSkladTable m_table = new WsSkladTable();
	
	  ActionListener m_radio_but_listener = new ActionListener() {
		  

			@Override
			public void actionPerformed(ActionEvent e) {
				
						
				refresh();
				
			}
	   };
	
	
	public WSSkladForm() {
		
		
		createGUI();
				
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	
            	 WsSkladStateReport dialog = new WsSkladStateReport(WsUtils.get().getMainWindow(), 
    					getGuiStrs("stateSkladZvitDialogWinCaption"), m_table.getDataVector(),
    					m_checkBoxAllKods.isSelected());
    			
    			dialog.setVisible(true);
             
            }
            
		});
		
	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel bottom_panel = WsGuiTools.createHorizontalPanel();
		
		JPanel buttons_panel = WsGuiTools.createHorizontalPanel();
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("filterSkladBorderTitle"));
		
		buttons_panel.setBorder(title);
		
		buttons_panel.add( m_radioDateSort);
		
		buttons_panel.add( m_radioTypeNameSort);
		
		buttons_panel.add(m_radioNameSort);
		
		buttons_panel.add(m_contrNameSort);
		
		JPanel buttons_panel2 = WsGuiTools.createHorizontalPanel();
		
		TitledBorder title2  = BorderFactory.createTitledBorder(getGuiStrs("joinSkladBorderTitle"));
		
		buttons_panel2.setBorder(title2);
		
		buttons_panel2.add( m_radioJoinKodSort);
		
		bottom_panel.add(buttons_panel);
		
		bottom_panel.add(buttons_panel2);
	
		 bottom_panel.add(m_genButton);
		 
		 bottom_panel.add(m_checkBoxAllKods);
		 
		 bottom_panel.add(Box.createHorizontalGlue());
	    
		m_butGroup.add(m_radioDateSort);
		
		m_butGroup.add(m_radioTypeNameSort);
	    
		m_butGroup.add(m_radioDateSort);
		
		m_butGroup.add(m_radioTypeNameSort);
		
		m_butGroup.add(m_radioNameSort);
		
		m_butGroup.add(m_contrNameSort);
		
		JScrollPane scroll = new JScrollPane(m_table);
	        
	    scroll.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	    scroll.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	           
	    mainPanel.add(bottom_panel);
	    
	    mainPanel.add(scroll);
	    
	    mainPanel.add(Box.createVerticalGlue());
	
	    setLayout(new BorderLayout());
		
		add(mainPanel);
		
		m_radioDateSort.addActionListener(m_radio_but_listener);
		
		m_radioTypeNameSort.addActionListener(m_radio_but_listener);
		
		m_radioNameSort.addActionListener(m_radio_but_listener);
		
		m_contrNameSort.addActionListener(m_radio_but_listener);
		
		m_radioJoinKodSort.addActionListener(m_radio_but_listener);
		
		m_radioTypeNameSort.setSelected(true);
		
		setToolTips();
		
		
	}
	
	private void refresh() {
		
			int flag = 0;
			
			boolean groupByKod = m_radioJoinKodSort.isSelected();
			
			if(m_radioDateSort.isSelected()) {
				
				flag = 0;
			}
			else
			if(m_radioTypeNameSort.isSelected()) {
				
				flag = 1;
			} 
			else
			if(m_radioNameSort.isSelected()) {
				
				flag = 2;
			}
			else
			if(m_contrNameSort.isSelected()) {
				
				flag = 4;
			}


			m_table.refreshData(null, flag, groupByKod);

					
	}
	
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED) {
			
			refresh();
			
		}
		
	}
	
	public void refreshData2(WsPrihodInvoiceChangedEvent e) {
		
		if(e == null || e.getEventType() == WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED) {
			
			refresh();
			
		}
		
	}
	
	public void refreshData3(WsRashodInvoiceChangedEvent e) {
		
		if(e == null || e.getEventType() == WsEventDispatcher.SALE_INVOICE_HAS_BEEN_CHANGED) {
			
			refresh();
			
		}
		
	}
	
	public void setToolTips() {
		
		m_checkBoxAllKods.setToolTipText(getGuiStrs("useAllKodesForReportToolTip"));
		
		m_genButton.setToolTipText(getGuiStrs("skladButtonReportGenerationToolTip"));
		
	}
	
}
