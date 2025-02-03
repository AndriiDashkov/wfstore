
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsPartType;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsTokenizer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPartTypesEditTableModel extends AbstractTableModel{

		private static final long serialVersionUID = 1L;
		
		private Vector<WsPartType> m_vec = new Vector<WsPartType>();
		
		private boolean m_notEditable = false;
		
		@SuppressWarnings("unused")
		private boolean m_dontUseSqlSource = false;
		
		
		String[] m_columnNames = {getGuiStrs("ptypesKodColumName"),getGuiStrs("ptypesColumName"), 
				getGuiStrs("ptypesKatalogCostWithNdsColumName"), getGuiStrs("ptypesSkladQuantityInfoColumName"),
				getGuiStrs("ptypesInfoColumName"), "id"};
		
		public WsPartTypesEditTableModel() {
			
		}
		
		public Vector<WsPartType> getCurrentVectorData() {
			
			 return m_vec;
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
		   
	        return 6; 
	   }

	   public int getRowCount() {
	    	
	        return m_vec.size();
	   }

	   public Object getValueAt(int row, int col) { 
	    	
	    	WsPartType dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
	    		case 0:{
	    			return dt.kod;
	    		}
	    		case 1:{
	    			return dt.name;
	    		}
	    		case 2:{
	    			return dt.costwithnds;
	    		}
	    		case 3:{
	    			return dt.quantity;
	    		}
	    		case 4:{
	    			return dt.info;
	    		}
	    		case 5: return dt.id;
	    	
	    	};
	    	
	        return null; 
	   }

    public boolean isCellEditable(int row, int col) {
    	
    			if(m_notEditable) return false;
    			
    			if(col < 5) return true;
    			
                return false;
     }
	    
	    
	 public void setEditable(boolean flag) {  m_notEditable  = !flag; }
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 	WsPartType dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
	        	case 0:
	        		dt.kod = Integer.valueOf((String) value);
	        		
	        		WsUtilSqlStatements.updatePartType(dt);
	        		
	        		break;
	        
		        case 1:
		        	
		        	dt.name = ((String) value);
		        	
		        	WsUtilSqlStatements.updatePartType(dt);
		        	
		            break;
		            
		        case 2:
		        	
		            dt.costwithnds = Double.valueOf((String) value);
		            
		            WsUtilSqlStatements.updatePartType(dt);
		            
		            break;
		            
		        case 3:
		        	
		            dt.quantity = Double.valueOf((String) value);
		            
		            WsUtilSqlStatements.updatePartType(dt);
		            
		            break;
		           
		        case 4:
		        	
		        	dt.info = ((String) value);
		        	
		        	WsUtilSqlStatements.updatePartType(dt);
		        	
		            break;
		            
		        case 5:
		        	
		            dt.id = (int) value;
		            
		            break;
	
	        } 
	        
	        this.fireTableDataChanged();
    }

	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public WsPartType getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsPartType dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	
	
	public void addUniqueIdRow(WsPartType dt) {
		
		for(int  i = 0 ; i < m_vec.size(); ++i) {
			
			WsPartType d = m_vec.elementAt(i);
			
			if(d.id == dt.id) { return; }
		}
		
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
			
			WsPartType d = m_vec.elementAt(i);
			
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
		 
		 m_vec =  WsUtilSqlStatements.getPartTypesList();
		 
		 this.fireTableDataChanged();
		
	}       
};
