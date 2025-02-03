
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsAgentTypeData;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsMain.WsTokenizer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentTypeEditTableModel extends AbstractTableModel{
	

	private static final long serialVersionUID = 1L;

	private Vector<WsAgentTypeData> m_vec = new Vector<WsAgentTypeData>();
	
	String[] m_columnNames = {getGuiStrs("agenttypesColumName"), getGuiStrs("ptypesInfoColumName"), "id"};

	public WsAgentTypeEditTableModel() {
		
	}

	public Vector<WsAgentTypeData> getCurrentVectorData() {
		
		 return m_vec;
	}
	
    public String getColumnName(int column) {
    	
        return m_columnNames[column];
    }

   public int getColumnCount() { 
	   
        return 3; 
    }

    public int getRowCount() {
    	
        return m_vec.size();
    }

    public Object getValueAt(int row, int col) { 
    	
    	WsAgentTypeData dt = m_vec.elementAt(row);
    	
    	switch(col) {
    	
    		case 0:{
    			
    			return dt.name;
    		}
    		case 1:{
    			
    			return dt.info;
    		}
    		case 2:  { 
    			
    			return dt.id;
    		}
    	
    	};
    	
        return null; 
    }

    public boolean isCellEditable(int row, int col) {
    	
    	return col < 2;

     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 WsAgentTypeData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
		        case 0: {
		        	
		        	dt.name = ((String) value);
		        	
		        	WSAgentSqlStatements.updateAgentType(dt);
		        	
		        	WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED);
		   				
	   				WsEventDispatcher.get().fireCustomEvent(event);
		        	
		            break;
		        }
		           
		        case 1: {
		        	
		        	dt.info = ((String) value);
		        	
		        	WSAgentSqlStatements.updateAgentType(dt);
		        	
		        	WsEventInt ev = new WsEventEnable(WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED);
		   				
	   				WsEventDispatcher.get().fireCustomEvent(ev);
		        	
		            break; 
		        }
		        case 2: {
		            dt.id = (int) value;
		            break;
		        }
	
	        } 
	        
	        this.fireTableDataChanged();
	  }



	/**
	 * @param i
	 */
	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public WsAgentTypeData getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsAgentTypeData dt) {
		
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
			
			WsAgentTypeData d = m_vec.elementAt(i);
			
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
		 
		 m_vec = WSAgentSqlStatements.getAgentsTypes();
		 
		 this.fireTableDataChanged();
		
	}       
};
