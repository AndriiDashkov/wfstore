
package WsControls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import WsDataStruct.WsPartType;
import WsEditTables.WsPartTypesEditTable;
import WsMain.WsGuiTools;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsKodChooserPanel extends JPanel {
	

	private static final long serialVersionUID = 1L;
	
	protected WsPartTypesEditTable m_kods_table = new  WsPartTypesEditTable();
	
	protected WsPartTypesEditTable m_kods_choosen_table = new  WsPartTypesEditTable();
	
	protected JButton m_in_but = new JButton("->");
	
	protected JButton m_out_but = new JButton("<-");
	
	public WsKodChooserPanel() {
		
		createGui();
		
		addListeners();
		
		m_kods_table.setEditable(false);
		
		m_kods_choosen_table.setEditable(false);
	}
	
	public void refresh() {
		
		m_kods_table.refreshData();
	}
	
	private void createGui() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel buttonPanel = WsGuiTools.createVerticalPanel();
		
		buttonPanel.add(m_in_but);
		
		buttonPanel.add(m_out_but);
		
		buttonPanel.add(Box.createVerticalGlue());
		
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
		
		add(scroll1);
		
		add(buttonPanel);
		
		add(scroll2);

	}
	
	
	public Vector<WsPartType> getCurrentVectorData() {
		
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
	
	}
	
	private void insertIntoChoosenTable(int row_id) {
		
		WsPartType d  = m_kods_table.getCurrentVectorData().elementAt(row_id);
		
		m_kods_choosen_table.addUniqueIdRow(d);
		
	}
	
	private void removeFromChoosenTable( int row_id) {
		
		
		m_kods_choosen_table.deleteRow(row_id);
		
	}

}
