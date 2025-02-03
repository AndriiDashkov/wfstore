
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsPartType;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsKodEditTableModel extends AbstractTableModel{
	

		private static final long serialVersionUID = 1L;

		private Vector<WsPartType> m_vec = new Vector<WsPartType>();

		String[] m_columnNames = {"_", getGuiStrs("skladColumnVenCode2KodName"), 
				 getGuiStrs("nameKodColumnName"),
				 getGuiStrs("quantityKodColumnName"), "id"};
	
		public WsKodEditTableModel() {	
		
		}

		public Vector<WsPartType> getCurrentVectorData() {
			
			 return m_vec;
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
	        return 5; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsPartType dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
	    		case 0: { 
	    			
	    			return dt.use;
	    		}
	    		case 1: { 
	    			
	    			return dt.kod;
	    		}
	    		
	    		case 2: {

	    			return dt.name;
	    		}
	    		case  3: { 
	    			
	    			return String.valueOf(dt.quantity);
	    		}
	    		case 4: {
	    			
	    			return dt.id;
	    		}
	    	
	    	};
	    	
	        return null; 
	    }

	    public boolean isCellEditable(int row, int col) {
	    	
	    			return col == 0 || col == 3;
	     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 WsPartType dt = m_vec.elementAt(row);
		 
		try {
	         
	        switch (col) {
	        
	        	case 0: {
	        		
	        		dt.use = (boolean) value; 
	        		
	        		break;
	        	}
	        	case 1: {
	        		
	        		dt.kod = (int) value;
	        		
	        		break;
	        	}
		        case 2: {
		        	
		            dt.name = ((String) value);
		            
		            break;
		        }
		        case 3 :{
		        	
		        	dt.quantity = Double.valueOf((String)value);
		        	
		            break;
		        }
		        case 4 :{
		        	
		        	dt.id = (int)value;
		        	
		            break;
		            
		        }
		            
	        } 
	        
	        this.fireTableDataChanged();
	        
		}
		catch(java.lang.NumberFormatException e) {
			
			String operationMessage = getMessagesStrs("enteredNumberIsWrongMessage");
   			
				JOptionPane.showMessageDialog(
      			    WsUtils.get().getMainWindow(),
      			    operationMessage,
      			    getMessagesStrs("messageInfoCaption"),
      			    JOptionPane.CLOSED_OPTION);
			
		}
		
	 }

	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public void addRow( WsPartType dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	
	public void deleteRow(int index) {
		
		m_vec.remove(index);
		
		this.fireTableDataChanged();
		
	}
	
	public void setVector(Vector< WsPartType> v) {
		
		m_vec.clear();
		
		m_vec = null;
		
		m_vec = v;
		
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

	public int findKod(int kod) {
		
		int del = 1;
		
		if(kod < 10) {
			
			del = 1000;
			
		}
		else if (kod < 100){
			
			del = 100;
			
		}
		else if(kod < 1000) {
			
			del = 10;
		}
		
		for(int i = 0; i < m_vec.size(); ++i) {
			
			WsPartType d =   m_vec.elementAt(i);
			
			if( ((int)(d.kod/del) ) == kod) {
				
				return i;
			}
			
		}
		
		return -1;
	}
};
