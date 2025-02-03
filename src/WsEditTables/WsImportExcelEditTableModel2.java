/**
 * 
 */
package WsEditTables;

import WsDataStruct.WsAgentData;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsImportExcelEditTableModel2 extends WsImportExcelEditTableModel {


	private static final long serialVersionUID = 1L;
	
	int m_columns = 0;

	/**
	 * @param columnNames
	 */
	public WsImportExcelEditTableModel2(String[] columnNames) {
		super(columnNames);

		m_columns = columnNames.length;
	}
	
	
	  public Object getValueAt(int row, int col) { 
	    	
	    	WsAgentData dt = m_vec.elementAt(row);
	    	
	    	if(col == 0)  { 
	    		
	    		return dt.contact;
	    	}
	    	else
	    	if(col < m_columns)  { 
	    		
	    		return String.valueOf(dt.quantity[col - 1]);
	    	}
	    	
	    	
	        return null; 
	   }
	  
	   public void setValueAt(Object value, int row, int col) {
			 
	    	if(col == 0)  { 
	    		
	    		m_vec.get(row).contact = (String) value;
	    	}
	    	else
	    	if(col < m_columns)  { 
	    		
	    		m_vec.get(row).quantity[col - 1] = Integer.valueOf((String)value);
	    		
	    	}
			 	   
		    this.fireTableDataChanged();
		 }

}
