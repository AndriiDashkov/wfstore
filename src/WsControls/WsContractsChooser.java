/**
 * 
 */
package WsControls;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import WsDataStruct.WsContractData;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsTables.WsContractsTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsContractsChooser extends JPanel {
	

	private static final long serialVersionUID = 1L;
	
	protected WsContractsTable m_kods_table = new  WsContractsTable();
	
	protected WsContractsTable m_kods_choosen_table = new  WsContractsTable();
	
	protected JButton m_in_but = new JButton(">");
	
	protected JButton m_out_but = new JButton("<");
	
	protected JButton m_in_all_but = new JButton(">>");
	
	protected JButton m_out_all_but = new JButton("<<");
	
	public WsContractsChooser() {
		
		createGui();
		
		addListeners();
		
		//m_kods_table.setEditable(false);
		
		//m_kods_choosen_table.setEditable(false);
	}
	
	public void refresh() {
		
		m_kods_table.refreshData();
	}
	
	private void createGui() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel buttonPanel = WsGuiTools.createVerticalPanel();
		
		buttonPanel.add(Box.createVerticalGlue());
		
		buttonPanel.add(m_in_but);
		
		buttonPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		buttonPanel.add(m_out_but);
		
		buttonPanel.add(Box.createVerticalGlue());
		

		buttonPanel.add(m_in_all_but);
		
		buttonPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		buttonPanel.add(m_out_all_but);
		
		
		JScrollPane scroll1 = new JScrollPane(m_kods_table);
	        
		scroll1.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scroll1.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		
		JScrollPane scroll2 = new JScrollPane(m_kods_choosen_table);
        
		scroll2.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scroll2.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		
		JPanel panel1  = WsGuiTools.createVerticalPanel();
		
		JPanel panel2  = WsGuiTools.createVerticalPanel();
		
		JPanel panel1_l  = WsGuiTools.createHorizontalPanel();
		
		JPanel panel2_l  = WsGuiTools.createHorizontalPanel();
		
		JLabel l1 = new JLabel(WsUtils.getGuiStrs("ctTabCaption"));
		
		JLabel l2 = new JLabel(WsUtils.getGuiStrs("dogReportCombo"));
		
		
		panel1_l.add(l1); panel1_l.add(Box.createHorizontalGlue());
		 
		panel2_l.add(l2); panel2_l.add(Box.createHorizontalGlue());
		
		panel1.add(panel1_l);
		 
		panel2.add(panel2_l);
		 
		panel1.add(scroll1);
		 
		panel2.add(scroll2);
		
		add(panel1);
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		add(buttonPanel);
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		add(panel2);

	}
	
	
	public Vector<WsContractData> getCurrentVectorData() {
		
		 return m_kods_choosen_table.getCurrentVectorData();
	}
	
	private void addListeners() {
	
		m_in_but.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	int row_id = m_kods_table.getSelectedRow();
	        	
	        	if( row_id != -1) {
	        		
	        		insertIntoChoosenTable( row_id);
	        	}
	         
	        }
	        
		});
		
		
		m_out_but.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	int row_id = m_kods_choosen_table.getSelectedRow();
	        	
	        	if( row_id != -1) {
	        		
	        		removeFromChoosenTable( row_id);
	        	}
	         
	        }
	        
		});
		
		m_in_all_but.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	Vector<WsContractData> vec = m_kods_table.getCurrentVectorData();
	        	
	        	for(int i = 0; i < vec.size(); ++i) {
	        		
	        		insertIntoChoosenTable( i);
	        		
	        	}         
	        }
		});
		
		
		m_out_all_but.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	 m_kods_choosen_table.deleteAll();
	        
	         
	        }
	        
		});
	
	}
	
	private void insertIntoChoosenTable(int row_index) {
		
		WsContractData d  = m_kods_table.getCurrentVectorData().elementAt(row_index);
		
		m_kods_choosen_table.addUniqueIdRow(d);
		
	}
	
	private void removeFromChoosenTable( int row_index) {
		
		m_kods_choosen_table.deleteRowForIndex(row_index);
		
	}
	
	public Vector<WsContractData> getChoosenData() {
		
		return m_kods_choosen_table.getCurrentVectorData();
		
	}

}
