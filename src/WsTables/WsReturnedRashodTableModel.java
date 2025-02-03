
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.sql.Date;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsReturnedPartData;
import WsDatabase.WsReturnRashodSqlStatements;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsReturnedRashodTableModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 1L;


		private Vector<WsReturnedPartData> m_vec = new Vector<WsReturnedPartData>();
		
		
		String[] m_columnNames = { "id", getGuiStrs("saleInvoiceNumberColumName"),
				getGuiStrs("saleInvoiceDateColumName"),
				getGuiStrs("kodColumName"), getGuiStrs("goodNameReturnColumName"), 
				getGuiStrs("quantityReturnColumName"),
				getGuiStrs("quantityReturnedColumName"),
				getGuiStrs("unitsNameColumName")};
		
		
		public WsReturnedRashodTableModel() {
			
			
		}
		
		public Vector<WsReturnedPartData> getCurrentVectorData() {
			
			 return m_vec;
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
	        return 8; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsReturnedPartData dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
	    		case 0: { return dt.id; }
	    		
	    		case 1: { return dt.sale_invoice_number; }
	    		
	    		case 2: { return dt.sale_invoice_date; }
	    		
	    		case 3: { return dt.name; }
	    		
	    		case 4: { return dt.kod; }
	    		
	    		case 5: { return dt.quantity; }
	    		
	    		case 6:  { return dt.returned_quantity; }
	    		
	    		case 7:  { return dt.units_name; }
	    	
	    	};
	    	
	        return null; 
	    }

	    public boolean isCellEditable(int row, int col) {
	    		
	                return col == 5;
	     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 	WsReturnedPartData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
		        case 0:{
		        	
	    			dt.id = (int)value; break;
	    		}
	    		case 1: {  dt.sale_invoice_number =(String)value; break; }
	    		
	    		case 2: {  dt.sale_invoice_date = (Date)value; break; }
	    		
	    		case 3: {  dt.name = (String)value; break; }
	    		
	    		case 4: {  dt.kod = (int)value; break; }
	    		
	    		case 5: { 
	    			
	    			try {
	    				
	    				dt.quantity = Double.valueOf((String) value); 
	    			
	    				WsReturnRashodSqlStatements.updateQuantity(dt.quantity, dt.id );
	    			 
	    			} catch (java.lang.NumberFormatException ex) {}
	    			
	    			 break;
	    		}
	    		case 6: { dt.returned_quantity = (double)value; break;}
	    		
	    		case 7: { dt.units_name = (String)value; break; }
	
	        } 
	        
	        this.fireTableDataChanged();
	  }

	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public WsReturnedPartData getDataAt(int row) {
		
		return m_vec.elementAt(row);
		
	}
	
	public void addRow(WsReturnedPartData dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	
	public void deleteRow(int index) {
		
		m_vec.remove(index);
		
		this.fireTableDataChanged();
		
	}
	
	public void refresh() {
		
		m_vec.clear();
		
		m_vec = null;
		
		m_vec = WsReturnRashodSqlStatements.getReturndeTableContent( );
		
		this.fireTableDataChanged();
		
	}
	
	
	public void clear() {
		
		WsReturnRashodSqlStatements.clearReturnTableContent( );
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}

       
};