
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsUnitData;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsTokenizer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsUnitsEditTableModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;
	
	private Vector<WsUnitData> m_vec = new Vector<WsUnitData>();
	
	String[] m_columnNames = {getGuiStrs("unitsColumName"), "id"};
	
	public WsUnitsEditTableModel() {
		
	}
	
	public Vector<WsUnitData> getCurrentVectorData() {
		
		 return m_vec;
	}
	
    public String getColumnName(int column) {
    	
        return m_columnNames[column];
    }

   public int getColumnCount() { 
	   
        return 2; 
    }

    public int getRowCount() {
    	
        return m_vec.size();
    }

    public Object getValueAt(int row, int col) { 
    	
    	WsUnitData dt = m_vec.elementAt(row);
    	
    	switch(col) {
    	
    		case 0:{
    			
    			return dt.name;
    		}
    		case 1:  { 
    			
    			return dt.id;
    		}
    		
    		default :
    	
    	};
    	
        return null; 
    }

    public boolean isCellEditable(int row, int col) {
    	
    	return col == 0;
  
     }
	      
	 public void setValueAt(Object value, int row, int col) {
		 
		 	WsUnitData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
		        case 0: {
		        	
		        	dt.name = ((String) value);
		        	
		        	WsUtilSqlStatements.updateUnitName(dt);
		        	
		            break;
		        }
		            
		        case 1: {
		        	
		            dt.id = (int) value;
		            
		            break;
		        }
	
	        } 
	        
	        this.fireTableDataChanged();
	  }

	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public WsUnitData getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsUnitData dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	
	public void deleteRow(int index) {
		
		m_vec.remove(index);
		
		this.fireTableDataChanged();
		
	}
	
	
	public String isDataValid() {
		
		
		String message = null;
		
		for(int i = 0; i < m_vec.size(); ++i) {
			
			WsUnitData d = m_vec.elementAt(i);
			
			if( d.name == null || d.name.isEmpty() ||  WsTokenizer.isValidate(d.name) == false ) {
				
				message =  getMessagesStrs("wrongNameInTheRowOrder");
				
				break;
			}
	
		}
		
		return message;
	
	}
	
	public void refresh() {
		
		 m_vec.clear();
		 
		 m_vec = null;
		 
		 m_vec = WsUtilSqlStatements.getUnitsList();
				 
		this.fireTableDataChanged();
		
	}       
};
