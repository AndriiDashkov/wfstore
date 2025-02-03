

package WsForms;


import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import WsControls.Ws2DatesControl;
import WsControls.WsPartTypesComboBox;
import WsDataStruct.WsReturnedPartData;
import WsDatabase.WsReturnRashodSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsTables.WsReturnedRashodTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsReturnForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");

	}
	
	Ws2DatesControl m_dates = new Ws2DatesControl(true);
	
	protected JLabel m_date_label = new JLabel(getGuiStrs("dateLabeleReturnFormtion"));
	
	protected JLabel m_comboPartTypeLabel = new JLabel(getGuiStrs("partTypeFilterComboBoxLabel2"));
	
	WsPartTypesComboBox m_kods_combo = new WsPartTypesComboBox();
	
	protected  JButton m_returnButton = new JButton(getGuiStrs("buttonReturnFormCaption"),
			WsUtils.get().getIconFromResource("wsreturntable.png"));
	
	
	protected  JButton m_backButton = new JButton(getGuiStrs("buttonBackReturnFormCaption"),
			WsUtils.get().getIconFromResource("wsundoreturn.png"));
	
	protected  JButton m_clearButton = new JButton(getGuiStrs("buttonClearReturnFormCaption"),
			WsUtils.get().getIconFromResource("wscleartable.png"));
	
	
	protected WsReturnedRashodTable m_table = new WsReturnedRashodTable();
	
	public WsReturnForm() {
		
		createGUI();
		
		m_returnButton.addActionListener(new ActionListener() {
			
	            public void actionPerformed(ActionEvent e) {
	            	
	        		int res = WsUtils.showYesNoDialog(getMessagesStrs("returnTheReturnedTableCaption"));
	        			
	        		if ( 1 == res) {
	            
	        			int part_type_id = m_kods_combo.getCurrentSQLId();
	        			
	        			java.sql.Date dt = m_dates.getSqlStartDate();
	        		
	        			WsReturnRashodSqlStatements.deletePartForRashod(part_type_id, dt);
	        			
	        			m_table.refresh();
	        			
	        		}
	         
	            }
	    });
		
		m_backButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
	        	int res = WsUtils.showYesNoDialog(getMessagesStrs("backTheReturnedTableCaption"));
        			
	        	if ( 1 == res) {
            
        			Vector<WsReturnedPartData> vec = m_table.getData();
        			
        			WsReturnRashodSqlStatements.getRashodBack(vec);
        			
        			WsReturnRashodSqlStatements.insertIntoReturnTable(vec);
        			
        			m_table.refresh();
        		
        		}
            	

            }
		});
		

		
		m_clearButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            		
    	        int res = WsUtils.showYesNoDialog(getMessagesStrs("clearTheReturnedTableCaption"));
        			
    	        if ( 1 == res) {
            
        			m_table.clear();
        		}
            }
		});
		
		
		m_dates.setCurrentStartDate();
		
	}
	
	private void createGUI() {
		
		setLayout(new BorderLayout());
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel butPanel =  WsGuiTools.createHorizontalPanel();
		
		WsUtils.get().setFixedSizeBehavior(m_kods_combo);
		
		WsGuiTools.setComponentFixedWidth(m_kods_combo, 300);
		
		WsUtils.get().setFixedSizeBehavior(m_dates);
		
		WsGuiTools.setComponentFixedWidth(m_dates, 100);
	
		butPanel.add(m_date_label); 
		
		butPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT)); 
		
		butPanel.add(m_dates); 
		
		butPanel.add(m_comboPartTypeLabel);
		
		butPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));

		butPanel.add(m_kods_combo); 
		
		butPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		butPanel.add( m_returnButton); 
		
		butPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		butPanel.add(m_backButton);
		
		butPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		butPanel.add(m_clearButton);
		
		butPanel.add(Box.createHorizontalGlue());
		
		mainPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		mainPanel.add(butPanel);
		
		mainPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		 JScrollPane scroll = new JScrollPane(m_table);
	        
	        scroll.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	        scroll.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		mainPanel.add(scroll);
		
		mainPanel.add(Box.createVerticalGlue());
		
		Dimension d = m_table.getPreferredSize();
		
		d.height = 300;
		
		m_table.setMinimumSize(d);
		
		WsGuiTools.fixComponentHeightToMin(butPanel);
		
		add(mainPanel);
		
		setToolTips();

	}
	
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED) {
			
			    m_table.refresh();
   						
		}
	}
	
	
	private void setToolTips() {
		
		m_returnButton.setToolTipText(getGuiStrs("returnButtonToolTip"));
		
		m_backButton.setToolTipText(getGuiStrs("provodkaReturnButtonToolTip"));
		
		m_clearButton.setToolTipText(getGuiStrs("clearTableReturnToolTip"));
		
		
	}
}
