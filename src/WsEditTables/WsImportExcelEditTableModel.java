
package WsEditTables;


import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsAgentData;
import WsDatabase.WSAgentSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsImportExcelEditTableModel extends AbstractTableModel{
	
		private static final long serialVersionUID = 1L;


		//the type WsAgentData is used for convinience, because it just has suitable fields
		protected Vector< WsAgentData > m_vec = new Vector<WsAgentData>();
		

		String[] m_columnNames = new String[2];
	
		public  WsImportExcelEditTableModel(String columnName) {
			
			m_columnNames[0] = columnName;
		}
		
		//type == 1 -> only 1 column
		//type == 2 ->  2 columns
		public  WsImportExcelEditTableModel(String[] columnNames) {
			
			m_columnNames = new String[columnNames.length];
			
			int i = 0 ;
			
			for(String name : columnNames) {
				
				m_columnNames[i++] = name; 
			}
			
		}


		
		public Vector<WsAgentData> getCurrentVectorData() {
			
			 return m_vec;
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
		   
	        return m_columnNames.length; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }
	    
	

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsAgentData dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
	    		case 0: { return dt.contact;}
	    		
	    		case 1: {
	    			
	    			WsAgentData da = WSAgentSqlStatements.getAgentForId(dt.id);
	    			
	    			return da; 
	    			
	    		}
	    		case 2: {
	    			
	    			return String.valueOf(dt.quantity[0]);
	    			
	    		}
	    		case 3: {
	    			
	    			return dt.info;
	    			
	    		}
	    
	    	};
	    	
	        return null; 
	    }

	    public boolean isCellEditable(int row, int col) {
	    		
	          return col > 0;
	     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
	    	switch(col) {
	    	
	    		case 0: {
	    			
	    			m_vec.get(row).contact = (String) value;
	    			
	    			break;
	    		}
	    		
	    		case 1: {
	    			
	    			WsAgentData dt = (WsAgentData)value;
	    			
	    			m_vec.get(row).id = (int) dt.id;
	    			
	    			m_vec.get(row).name =  dt.name;
	    			
	    			break;
	    			
	    		}
	    		case 2: {
	    			
	    			try {
	    				
	    				int v = Integer.valueOf((String)value);
	    				
	    				m_vec.get(row).quantity[0] = v;
	    				
	    			}catch(NumberFormatException ex) {
	    				
	    				
	    			}
  			
	    			break;
	    			
	    		}
	    		case 3: {
	    			
	    			WsAgentData dt = (WsAgentData)value;
	    			
	    			m_vec.get(row).info =  dt.info;
	    			
	    			break;
	    			
	    		}
    
	    	};
	        
	        this.fireTableDataChanged();
	  }



	/**
	 * @param i
	 */
	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	

	
	public void addRow(WsAgentData dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	

	
	public void deleteRow(int index) {
		
		m_vec.remove(index);
		
		this.fireTableDataChanged();
		
	}
	
	public void clear() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	
	public void setVector(Vector<WsAgentData> v) {
		
		m_vec.clear();
		
		m_vec = null;
		
		m_vec = v;
		
		this.fireTableDataChanged();
		
	}
	
	public String isDataValid() {
		
		
		String message = null;
		
		for(int i = 0; i < m_vec.size(); ++i) {
			
			String d = m_vec.elementAt(i).name;
			
			if( d == null || d.isEmpty() ||  WsUtils.isFileExists(d) == false ) {
				
				message =  getMessagesStrs("wrongNameInTheRowOrder");
				
				break;
			}

		}
		
		return message;
		
		
	}
         
};
