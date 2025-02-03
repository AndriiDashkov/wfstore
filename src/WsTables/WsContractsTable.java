
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import WsDataStruct.WsContractData;
import WsDatabase.WsContractsSqlStatements;
import WsEditTables.WsDateRenderer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsContractsTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ListSelectionListener m_selection_listener = null;


	DefaultTableModel m_model = new DefaultTableModel() {

		   /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	String[] m_columnNames = {
			getGuiStrs("prihodColumnNumberNameName"),
			getGuiStrs("prihodColumnDateName"),  
			getGuiStrs("prihodColumnInfoName"), "id" };
	
	int m_id_index = 3; //this must be changed if m_columnNames is changed
	
	public WsContractsTable() {
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     getColumnModel().getColumn(0).setMinWidth(150);
	     
	     getColumnModel().getColumn(0).setMaxWidth(150);
	     
	     getColumnModel().getColumn(1).setMaxWidth(100);
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     getColumnModel().getColumn(1).setCellRenderer(new WsDateRenderer());
	    
	}
	
	public void setSelectionListener(ListSelectionListener l) {
		
		 m_selection_listener = l;
		
		 ListSelectionModel cellSelectionModel = getSelectionModel();
		
	     cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	     cellSelectionModel.addListSelectionListener(m_selection_listener);
	}
	
	
	public void refreshData( ) {
		
        	 
			Vector<WsContractData> vec  = WsContractsSqlStatements.getContractsList(1);
    	    
    	    this.getSelectionModel().removeListSelectionListener(m_selection_listener);
    	    
    	    
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	    
    	    for(int i = 0; i < vec.size(); ++i) {
    	    	
    	    	 WsContractData d = vec.elementAt(i);
    	    	
			     m_model.addRow(new Object[]{ d.number, d.date, d.info, d.id});
    	    	
    	    }
			
			if(m_selection_listener != null) {
				
				this.getSelectionModel().addListSelectionListener(m_selection_listener);
			}
			
			m_model.fireTableDataChanged();
					
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  =  (int) m_model.getValueAt(selected_id, m_id_index );
		}
		
		return selected_id;
	}
	
	
	public Vector<Integer> getSelectedIds() {
		
		int[] selected_ids = getSelectedRows();
		
		Vector<Integer> v = new Vector<Integer>();
		
		for(int i = 0; i < selected_ids.length; ++i) {
		
			int selected_id  =  (int) m_model.getValueAt(selected_ids[i], m_id_index );
			
			v.add(selected_id );
		}
		
		return v;
	}
	
	
	public void setSelectedSqlId(int id) {
		
		
		for(int i =0 ; i < m_model.getRowCount(); i++) {
			
			int id_  =  (int) m_model.getValueAt(i, m_id_index );
			
			if(id_ == id) {
				
				setRowSelectionInterval(i, i);
				
				return;
			}
			
		}
	
	}
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(m_id_index));

		}
		
	}
	
	
	public Vector< WsContractData> getCurrentVectorData() {
		
		Vector< WsContractData> vec = new Vector< WsContractData>();
		
		for(int i =0 ; i < m_model.getRowCount(); i++) {
			
			WsContractData d = new WsContractData();
			
			d.id =  (int) m_model.getValueAt(i, m_id_index );
			
			d.number = (String ) m_model.getValueAt(i, 0 );
			
			d.date = ( java.sql.Date) m_model.getValueAt(i, 1 );
			
			d.info = (String ) m_model.getValueAt(i, 2 );
			
			vec.add(d);
			
		}
		
		return vec;
		
	}
	
	
	public void addUniqueIdRow(WsContractData d) {
		
		int id = d.id;
		
		for(int i = 0 ; i < m_model.getRowCount(); i++) {
			
			if(id == (int) m_model.getValueAt(i, m_id_index ) ) {
				
				return;
				
			}
					
		}
	    	
	     m_model.addRow(new Object[]{ d.number, d.date, d.info, d.id});
	     
	     m_model.fireTableDataChanged();
		
	}
	
	public void deleteRowForId(int id) {
		
		int index = -1;
		
		for(int i = 0 ; i < m_model.getRowCount(); i++) {
			
			if(id == (int) m_model.getValueAt(i, m_id_index ) ) {
				
				index = i;
				
			}
					
		}
		
		if(index != -1) {
			
			m_model.removeRow(index);
			
			m_model.fireTableDataChanged();
		}
		
	}
	
	public void deleteRowForIndex(int index) {
		
		if(index != -1) {
			
			m_model.removeRow(index);
			
			m_model.fireTableDataChanged();
		}
		
	}
	
	public void deleteAll() {
		
		while(m_model.getRowCount() != 0) {
			
			m_model.removeRow(0);
					
		}
			
	}
	
}
