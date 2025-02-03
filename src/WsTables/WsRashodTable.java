
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Rectangle;
import java.sql.Date;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import WsDataStruct.WsRashodData;
import WsDatabase.WsRashodSqlStatements;
import WsEditTables.WsDateRenderer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsRashodTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	int sql_id_column_index = 6;

	DefaultTableModel m_model = new DefaultTableModel() {


		   private static final long serialVersionUID = 1L;

		   @Override
           public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	String[] m_columnNames = { getGuiStrs("prihodColumnNumberNameName"),
			getGuiStrs("prihodColumnDateName"), getGuiStrs("prihodColumnAgentName"), 
			getGuiStrs("rashodColumnPeopleName"), 
			getGuiStrs("prihodColumnInfoName"), 
			"id_counterparty","id", };
	
	
	public WsRashodTable() {
		
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     getColumnModel().getColumn(0).setMinWidth(150);
	     
	     getColumnModel().getColumn(0).setMaxWidth(200);
	     
	     getColumnModel().getColumn(1).setMaxWidth(120);
	     
	     getColumnModel().getColumn(2).setMinWidth(200);
	     
	     getColumnModel().getColumn(2).setMaxWidth(300);
	     
	     getColumnModel().getColumn(3).setMinWidth(100);
	     
	     getColumnModel().getColumn(3).setMaxWidth(100);
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     getColumnModel().getColumn(1).setCellRenderer(new WsDateRenderer());
	}
	
	
	public void refreshData(int id_agent, int id_contract, Date start, Date end, int kod_id, boolean kod_inverse) {
		
        	Vector<WsRashodData> vec  = WsRashodSqlStatements.getRashodList( id_agent, id_contract, start, end,  kod_id, kod_inverse);
        
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	    
    	    for(int i = 0; i < vec.size(); ++i) {
    	    	
    	    	WsRashodData d = vec.elementAt(i);
    	    	
    	    	m_model.addRow(new Object[]{ d.number,d.date, 
			    		 d.agentName, d.people, d.info, d.id_counterparty, d.id});
    	    
    	    }
    	    
			
			m_model.fireTableDataChanged();
			
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  =  (int) m_model.getValueAt(selected_id, sql_id_column_index );
		}
		
		return selected_id;
	}
	
	public Vector<Integer> getSelectedIds() {
		
		int[] selected_id = getSelectedRows();
		
		Vector<Integer> vec = new Vector<Integer>();
		
		for(int id : selected_id) {
		
			if (id != - 1) {
			
				vec.add((int) m_model.getValueAt(id, sql_id_column_index ));
			}
		}
		
		return vec;
		
	}
	
	
	public void setSelectedSqlId(int id) {
		
		if(id == -1) { return; }
		
		for(int i =0 ; i < m_model.getRowCount(); i++) {
			
			int id_  =  (int) m_model.getValueAt(i, 6);
			
			if(id_ == id) {
				
				setRowSelectionInterval(i, i);
				
				Rectangle cellRect = getCellRect(i, 0, true);
				 
				scrollRectToVisible(cellRect);
				
				return;
			}
			
		}
	
	}
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(6));
			
			removeColumn(getColumnModel().getColumn(5));


		}
		
	}
	
	public int selectFind(String s, int flag) {
		
		int index  = 0 ;
		
		if(flag == 0) { 
			
			index = 0; 
		} 
		else if(flag == 1) {
			
			index =  4;

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