package WsTables;

import static WsMain.WsUtils.getGuiStrs;

import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import WsDataStruct.WsPrihodData;
import WsDatabase.WsPrihodSqlStatements;
import WsEditTables.WsDateRenderer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsPrihodTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	ListSelectionListener m_selection_listener = null;


	DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	String[] m_columnNames = {getGuiStrs("prihodColumnNumberNameName"),
			getGuiStrs("prihodColumnDateName"), getGuiStrs("prihodColumnDateDocName"), 
			getGuiStrs("prihodColumnAgentName"), getGuiStrs("prihodColumnContractName"), 
			getGuiStrs("prihodColumnInfoName"), "id" };
	
	int m_id_index = 6; //this must be changed if m_columnNames is changed
	
	public WsPrihodTable() {
		
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     getColumnModel().getColumn(0).setMinWidth(150);
	     
	     getColumnModel().getColumn(0).setMaxWidth(250);
	     
	     getColumnModel().getColumn(1).setMaxWidth(100);
	     
	     getColumnModel().getColumn(2).setMaxWidth(100);
	     
	     getColumnModel().getColumn(3).setMinWidth(250);
	     
	     getColumnModel().getColumn(3).setMaxWidth(250);
	     
	     getColumnModel().getColumn(4).setMinWidth(250);
	     
	     getColumnModel().getColumn(4).setMaxWidth(250);
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     getColumnModel().getColumn(1).setCellRenderer(new WsDateRenderer());
	     
	     getColumnModel().getColumn(2).setCellRenderer(new WsDateRenderer());
	    
	}
	
	public void setSelectionListener(ListSelectionListener l) {
		
		m_selection_listener = l;
		
		ListSelectionModel cellSelectionModel = getSelectionModel();

	    cellSelectionModel.addListSelectionListener(m_selection_listener);
	}
	
	
	public void refreshData(int id_part_type, int id_agent, int id_contract, java.sql.Date start, java.sql.Date end ) {
		
        	 
        	Vector<WsPrihodData> vec  = WsPrihodSqlStatements.getPrihodList( id_part_type, id_agent, id_contract,  start, end);
    	    
    	    this.getSelectionModel().removeListSelectionListener(m_selection_listener);
    	    
    	    
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	    
    	    for(int i = 0; i < vec.size(); ++i) {
    	    	
    	    	WsPrihodData d = vec.elementAt(i);
    	    	
			     m_model.addRow(new Object[]{ d.number,d.date, d.date_doc, d.agentName, d.contractNumber,
			    		 d.info, d.id});
    	    	
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
	
	public int selectFind(String s, int flag) {
		
		int index  = 0 ;
		
		if(flag == 0) { 
			
			index = 0; 
		} 
		else if(flag == 1) {
			
			index =  5;

		}
		
		ListSelectionModel model = getSelectionModel();
		
		model.clearSelection();
		
		int counter = 0;
		
		for(int i = 0 ; i < m_model.getRowCount(); i++) {
			
			String num  =  (String) m_model.getValueAt(i, index );
			
			if(num.contains(s)) {
				
				model.addSelectionInterval(i, i);
				
				++counter;
		
			}	
		}	
		
		if(counter > 0) {
				
			Rectangle cellRectangle = getCellRect(getSelectedRow(), 0, true);
			
			scrollRectToVisible(cellRectangle);
		}

		return counter;
		
	}
	
}
