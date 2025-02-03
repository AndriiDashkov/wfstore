
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsInfoData;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsTokenizer;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsInfoEditTableModel extends AbstractTableModel{
	

	    private static final long serialVersionUID = 1L;

		private Vector<WsInfoData> m_vec = new Vector<WsInfoData>();
		
		String[] m_columnNames = {getGuiStrs("infoNameColumName"), 
				getGuiStrs("infoAdressColumName"),
				getGuiStrs("infoPhoneColumName"), 
				getGuiStrs("infoPersonColumName"),
				getGuiStrs("infoRahunokColumName"),
				getGuiStrs("infoMFOColumName"),
				getGuiStrs("infocalcNDsColumName"),
				getGuiStrs("infoMoneyColumName"),
				getGuiStrs("infoDodatkovoColumName"),
				"id"};
		
		public WsInfoEditTableModel() {
			
		}
		
		public Vector<WsInfoData> getCurrentVectorData() {
			
			 return m_vec;
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
		   
	        return 10; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsInfoData dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
	    		case 0:{
	    			
	    			return dt.name;
	    		}
	    		case 1:{
	    			
	    			return dt.adress;
	    		}
	    		case 2:{
	    			
	    			return dt.phone;
	    		}
	    		case 3:{
	    			
	    			return dt.person;
	    		}
	    		case 4:{
	    			
	    			return dt.rahunok;
	    		}
	    		case 5:{
	    			
	    			return dt.MFO;
	    		}
	    		case 6:{
	    			
	    			return dt.nds;
	    		}
	    		case 7:{
	    			
	    			return dt.money;
	    		}
	    		case 8:{
	    			
	    			return dt.comments;
	    		}
	    		case 9:  {
	    			
	    			return dt.id; 
	    		}
 	    	
	    	};
	    	
	        return null; 
	    }

	    public boolean isCellEditable(int row, int col) {
	    	
    			if(col < 8) { 
    				
    				return true; 
    			}
                { 
                	return false; 
                }
	     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 	WsInfoData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
		        case 0: {
		        	
		        	dt.name = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		            break;
		        }
		        case 1:
		        {
		        	dt.adress = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        	
		        }
		        	
		        case 2:
		        {
		        	dt.phone = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }
		        case 3:
		        {
		        	dt.person = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }
		        case 4:
		        {
		        	dt.rahunok = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }
		        case 5:
		        {
		        	dt.MFO = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }
		        case 6: 
		        {
		        	
		        	dt.nds = Double.valueOf((String)value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }
		        case 7:
		        {
		        	dt.money = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }
		        	
		        case 8:
		        {
		        	dt.comments = ((String) value);
		        	
		        	WsUtilSqlStatements.updateInfo(dt);
		        	
		        	break;
		        }

		        default:
	
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
	
	public WsInfoData getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsInfoData dt) {
		
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
			
			WsInfoData d = m_vec.elementAt(i);
			
			if( d.name == null || d.name.isEmpty() ||  WsTokenizer.isValidate(d.name) == false 
					|| d.adress == null || WsTokenizer.isValidate(d.adress) == false
					|| d.phone == null || WsTokenizer.isValidate(d.name) == false
					|| d.person == null || WsTokenizer.isValidate(d.person) == false
					|| d.rahunok == null || WsTokenizer.isValidate(d.rahunok) == false
					|| d.MFO == null || WsTokenizer.isValidate(d.MFO) == false
					|| d.comments == null || WsTokenizer.isValidate(d.comments) == false) {
				
				message =  getMessagesStrs("wrongNameInTheRowOrder");
				
				break;
			}
	
		}
		
		return message;
		
		
	}
	
	public void refresh() {
		
		m_vec.clear();
		
		m_vec = null;
		 
		m_vec =  WsUtilSqlStatements.getInfoDataList();
		
		this.fireTableDataChanged();
		
	}       
};
