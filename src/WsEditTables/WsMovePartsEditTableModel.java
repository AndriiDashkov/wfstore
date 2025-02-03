
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsSkladMoveDataColumn;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsMovePartsEditTableModel extends AbstractTableModel{
	
	
		private static final long serialVersionUID = 1L;

		private Vector< WsSkladMoveDataColumn> m_vec = new Vector< WsSkladMoveDataColumn>();
		
		String[] m_columnNames = {getGuiStrs("ptypesKodColumName"),getGuiStrs("ptypesColumName"), 
				getGuiStrs("impZali"), getGuiStrs("prihodPartsColumnRestName"), getGuiStrs("quantityNameReportColumn"),
				getGuiStrs("rashodNameReportColumn"), getGuiStrs("vubRazkl"), getGuiStrs("prihodPartsColumnRestName"),
				getGuiStrs("zalRazkl"),  getGuiStrs("diffReportNAme"), getGuiStrs("correctioPossibleReportName2"),
				getGuiStrs("corrToDo")};
			
		public WsMovePartsEditTableModel() {
			
			
		}

		public Vector< WsSkladMoveDataColumn> getCurrentVectorData() {
			
			 return m_vec;
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
		   
	        return 12; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsSkladMoveDataColumn dt = m_vec.elementAt(row);
	    	 
	    	
	    	switch(col) {
	    	
	    		case 0:{
	    			
	    			return dt.kod;
	    		}
	    		case 1:{
	    			
	    			return dt.name;
	    		}
	    		case 2:{
	    			
	    			return dt.q_array[1].initial_rest;
	    		}
	    		case 3:{
	    			
	    			return dt.q_array[0].initial_rest;
	    		}
	    		case 4:{
	    			
	    			return dt.q_array[0].in_quantity;
	    		}
	    		case 5:{
	    			
	    			return dt.q_array[0].out_quantity;
	    		}
	    		case 6:{
	    			
	    			return dt.q_array[1].out_quantity;
	    		}
	    		case 7:{
	    			
	    			return dt.q_array[0].rest;
	    		}
	    		case 8:{
	    			
	    			return dt.q_array[1].rest;
	    		}
	    		case 9:{
	    			
	    			return dt.q_array[1].rest  - dt.q_array[0].rest;
	    		}
	    		case 10:{
	    			
	    			return dt.correction;
	    		}
	    		case 11:{
	    			
	    			return dt.correctionToDo;
	    		}
	    	
	    	};
	    	
	        return null; 
	    }

	    public boolean isCellEditable(int row, int col) {
	    	
	    	return col == 11;
	    			
	     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 WsSkladMoveDataColumn dt = m_vec.elementAt(row);
	         
	        switch (col) {
	          
		        case 11: {
		        	
		            dt.correctionToDo =Double.valueOf((String) value);
		          
		            break;
		        }

	        } 
	        
	        this.fireTableDataChanged();
	  }


	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public WsSkladMoveDataColumn getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsSkladMoveDataColumn dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	
	public void deleteRow(int index) {
		
		m_vec.remove(index);
		
		this.fireTableDataChanged();
		
	}
	

	public void refresh() {
				 
		 this.fireTableDataChanged();
		
	}       
};
