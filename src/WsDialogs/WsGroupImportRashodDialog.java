/**
 * 
 */
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsControls.WsFileTableControl;
import WsControls.WsIndicesImportPanel;
import WsDataStruct.WsAgentData;
import WsEvents.WsEvent;
import WsEvents.WsEventDispatcher;
import WsImport.WFParseIndicies;
import WsImport.WFParseIndicies.TYPE;
import WsLong.WsImportExcelNaklRashodLong;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsGroupImportRashodDialog  extends JDialog  {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.TABLE_ROWS_NEW_COUNT_EVENT, this, "refreshCounter");

	}

	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	public JButton m_startButton;

	Ws2DatesControl m_date = new Ws2DatesControl(true);
	
	WsFileTableControl m_table = null;
	
	JProgressBar m_progressBar = new JProgressBar(0, 100);
	
	WsIndicesImportPanel m_indices_panel = null; 
	
	JLabel m_countLabel = new JLabel("0");
	
	private static java.sql.Date m_start_date = null;
		
	public WsGroupImportRashodDialog (JFrame jfrm, String nameFrame) {
		
		super (jfrm, nameFrame, false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(createGUI());
		
		setBounds(100, 100, 600, 150);
		
		init();

		pack();
		
	}
		
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_startButton ) { 
				
				flag = WsCloseFlag.OK;
				
				int res = WsUtils.showYesNoDialog( getMessagesStrs("importGroupNaklsCaption"));
			      	   
				if ( 1 == res) {
					
					int noAgentsNum = checkTableData();
					
					if(noAgentsNum != 0) {
						
						res = WsUtils.showYesNoDialog( getMessagesStrs("noagentSelMessage") + " " 
								+ String.valueOf(noAgentsNum)  + "<>" + getMessagesStrs("continueOPMessage"));
						
						if(res != 1) { return; }
						
					}
					
					WsImportExcelNaklRashodLong cusor = new   WsImportExcelNaklRashodLong(WsGroupImportRashodDialog.this,
						     m_progressBar);
						
					m_progressBar.setVisible(true);
					
					cusor.execute();

				}
			}	
		}
	}
	
	/**
	 * 	
	 * @return close flag to determine what operation should be done after the dialog close
	 */
	public WsCloseFlag getClosedFlagValue () {
		
		return flag;
	}
	
	
	/**
	 * 
	 * @return main UI panel with all components
	 */
	private JPanel createGUI() {
		
		String[] columnNames = {getGuiStrs("flNaklTableCap"), 
				getGuiStrs("labeAgentOtrComboCaption"), 
				getGuiStrs("rashodColumnPeopleName") +" :",
				getGuiStrs("saleInvoiceNumberColumName")};
		
		m_table = new WsFileTableControl(columnNames, getGuiStrs("vbFlEcelNaklCap"), true);
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		JPanel panel_2 = WsGuiTools.createHorizontalPanel();
		
		panel_2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_2.add(new JLabel(getGuiStrs("dateNaklImportGroupRash")));
		
		panel_2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT)); 
		
		panel_2.add(m_date);  panel_2.add(Box.createHorizontalGlue());
		
		JPanel infoPanel = WsGuiTools.createHorizontalPanel();
		
		infoPanel.add(new JLabel(getGuiStrs("klNaklImport")));
		
		infoPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		infoPanel.add(m_countLabel);
		
		infoPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		infoPanel.add(m_progressBar);
		
		infoPanel.add(Box.createHorizontalGlue());
		
	    m_progressBar.setValue(0);
	    
	    m_progressBar.setStringPainted(true);
	    
		JPanel batPanel = WsGuiTools.createHorizontalPanel();
		
		m_startButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
				WsUtils.get().getIconFromResource("wsimportExcel.png"));
		
		batPanel.add(m_startButton);
		
		batPanel.add(Box.createHorizontalGlue());

		m_indices_panel = new WsIndicesImportPanel(this, TYPE.NAKL);
	
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));

		panel_MAIN.add(panel_2);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		WsGuiTools.setComponentFixedHeight(m_table, 250);
		
		panel_MAIN.add(m_table);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(infoPanel);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(batPanel);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(m_indices_panel);
		
		setAllListeners();
		
		setToolTips();
		
		m_progressBar.setVisible(false);
		
		return panel_MAIN;
	}
	
	private void fillTable(Vector<WsAgentData> vec ) {
		
		for(WsAgentData d: vec) {
			
			String s = WsUtils.getFileNameFromString(d.contact);
			
			String[] list = s.split("[^0-9]+");
			
			try {
				
				if(list.length > 2) {
					
					int n = Integer.valueOf(list[2]);
					
					d.quantity[0] = n;
				}
				
			} catch(NumberFormatException ex) {}
			
			if(list.length > 0) {
				
				d.info = list[1];
			}
		}
		
		m_table.refreshTable();
		
	}
	
	private void setAllListeners() {
		
		m_startButton.addActionListener(forwarder);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});

	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 	 	  
		m_startButton.setToolTipText(getGuiStrs("autoRashodButtonToolTip"));
		 
	 }
	 
	/**
	 * <p>Initiation function.Don't remove it!  
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
			
		if(m_start_date == null) {
			
			m_date.setCurrentStartDate();
		}
		else {
			
			m_date.setSqlStartDate(m_start_date);
		}	
	}
	
	public void refreshCounter(WsEvent ev) {
		
		if(ev.getEventType() == WsEventDispatcher.TABLE_ROWS_NEW_COUNT_EVENT && 
				 m_table.getTableRef() == ev.getSender()) {
			
			m_countLabel.setText(String.valueOf(m_table.getTableRef().getRowCount()));
			
			fillTable(m_table.getData());
			
		}
	}
	
	public Vector<WsAgentData> getTableData() {
		
		return m_table.getData();
		
	}
	
	public int checkTableData() {
		
		 Vector<WsAgentData> vec =  m_table.getData();
		 
		 int counter = 0;
		 
		 for(WsAgentData d: vec) {
			 
			 if(d.id == -1) { ++counter;}
			 
		 }
		 
		 return counter;
		
	}

	public WFParseIndicies getIndicesSchema() {
		
		return m_indices_panel.getIndicesSchema();
		
	}
	
	public Date getDate() {
		
		return m_date.getSqlStartDate();
	}

	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);

		super.dispose();
		
	}
}