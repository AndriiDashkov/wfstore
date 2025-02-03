
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsSignsData;
import WsDatabase.WsSignSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsMain.WsTokenizer;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsSignsEditTableModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;

	private Vector<WsSignsData> m_vec = new Vector<WsSignsData>();
	
	String[] m_columnNames = {getGuiStrs("signsRankColumName"), getGuiStrs("signsNameColumName"), getGuiStrs("signsPosColumName"), "id"};
	
	public WsSignsEditTableModel() {

	}
	
	public Vector<WsSignsData> getCurrentVectorData() {
		
		 return m_vec;
	}
	
    public String getColumnName(int column) {
    	
        return m_columnNames[column];
    }

   public int getColumnCount() { 
	   
        return 4; 
    }

    public int getRowCount() {
    	
        return m_vec.size();
    }

    public Object getValueAt(int row, int col) { 
    	
    	WsSignsData dt = m_vec.elementAt(row);
    	
    	switch(col) {
    	
    		case 0:{
    			
    			return dt.rank;
    		}
    		case 1:{
    			
    			return dt.name;
    		}
    		case 2:{
    			
    			return dt.position;
    		}
    		case 3: {
    			
    			return dt.id;
    		}
    		
    		default : {}
    	
    	};
    	
        return null; 
    }

    public boolean isCellEditable(int row, int col) {
    	
    	return col < 3;
     
    }
	    
	    
	public void setValueAt(Object value, int row, int col) {
		 
		 	WsSignsData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
		        case 0: {
		        	
		        	dt.rank = ((String) value);
		        	
		        	WsSignSqlStatements.updateSign(dt);
		        	
		        	WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.SIGNS_DATA_CHANGED);
		   				
	   				WsEventDispatcher.get().fireCustomEvent(event);
		        	
		            break;
		        }
		           
		        case 1: {
		        	
		        	dt.name = ((String) value);
		        	
		        	WsSignSqlStatements.updateSign(dt);
		        	
		        	WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.SIGNS_DATA_CHANGED);
	   				
	   				WsEventDispatcher.get().fireCustomEvent(event);
		        	
		            break; 
		        }
		        case 2: {
		        	
		        	dt.position = ((String) value);
		        	
		        	WsSignSqlStatements.updateSign(dt);
		        	
		        	WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.SIGNS_DATA_CHANGED);
	   				
	   				WsEventDispatcher.get().fireCustomEvent(event);
		        	
		            break; 
		        }
		        case 3: {
		        	
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
	
	public WsSignsData getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsSignsData dt) {
		
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
			
			WsSignsData d = m_vec.elementAt(i);
			
			if( d.name == null || d.name.isEmpty() ||  WsTokenizer.isValidate(d.name) == false ) {
				
				message =  getMessagesStrs("wrongNameInTheRowOrder");
				
				break;
			}
	
		}
		
		return message;
	}
	
	public void refresh() {
		
		 m_vec.clear();
		 
		 m_vec= null;
		 
		 m_vec = WsSignSqlStatements.getSignsList();
		 
		 this.fireTableDataChanged();
		
	}       
};