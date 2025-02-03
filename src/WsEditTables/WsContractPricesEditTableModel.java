
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsContractPriceData;
import WsDatabase.WsContractsSqlStatements;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsContractPricesEditTableModel extends AbstractTableModel{
	

		private static final long serialVersionUID = 1L;

		private Vector<WsContractPriceData> m_vec = new Vector<WsContractPriceData>();
		
		String[] m_columnNames = {
				getGuiStrs("skladColumnVenCode2KodName"), 
				getGuiStrs("nameColumnReportGoodName"),
				getGuiStrs("prihodPartsColumnUnitsNameName"), 
				getGuiStrs("costNameInReportNoNDS"), 
				getGuiStrs("prihodPartsColumnNdsName"), 
				 getGuiStrs("prWithPdv"), "id_inits", "id_part_type", "id"};
		
		private int m_id_column_index = 8; //must be changed if m_columnNames  has been changed
		
		private double m_nds_coeff = 0.0;
		
		private boolean m_notEditable = false;
	
		public WsContractPricesEditTableModel() {
				
		}
		
		public int getIdColumnIndex() { return m_id_column_index; }
		
		public Vector<WsContractPriceData> getModelVectorCopy() {
			
			Vector<WsContractPriceData> vec = new Vector<WsContractPriceData>();
			
			for(WsContractPriceData  d: m_vec) {
				
				vec.add(new WsContractPriceData(d));
			}
			
			return vec;
			
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
	        
		   return 9; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }
	    
	    
		 public WsContractPriceData getValueAt(int row) {
			 
			 	if(row >= m_vec.size()) { return null;}
			    	
			    	return m_vec.elementAt(row);
			    	
		 }

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsContractPriceData dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
    			case 0: { return String.valueOf(dt.kod); }

	    		case 1:  { return dt.name; }
	    		
	    		case 2: { return dt.units_name; }
	    		
	    		case 3:  { return dt.cost; }
	    		
	    		case 4:  { return dt.nds; }
	    		
	    		case 5:  { return dt.costwnds; }
	    		
	    		case 6: { return dt.id_units; }
	    		
	    		case 7: { return dt.id_part_type; }
	    		
	    		case 8: { return dt.id; }
	    	
	    	};
	    	
	        return null; 
	    }

	  public boolean isCellEditable(int row, int col) {
	    	
	    	   if(m_notEditable) return false;
	    		
	           return  col > 2	 && col < 6;
	  }
	    
	 public void setEditable(boolean flag) {  m_notEditable  = !flag; }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 	if(row > (m_vec.size() - 1) ) { return; } 
		 
		 	WsContractPriceData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
		        case 0: {
		        	
			        dt.kod = Integer.parseInt((String) value);
		    	
		            break;
		            
		        }
		            
		        case 1: {
		        	
		        	dt.name = ((String) value);
		        	
		            break;
		            
		        }
		        case 2: {

		        	dt.units_name = ((String) value);
		        	
		            break;
		            
		        }
   
		        case 3: { 
		        	
		        	m_nds_coeff = WsUtils.getNdsCoeff();
		        	
		        	dt.cost = (double) value; 
		        
		        	dt.costwnds = m_nds_coeff*dt.cost;
		        	
		        	dt.nds = dt.costwnds - dt.cost;
		        	
		        	WsContractsSqlStatements.updateContractPriceCost(dt.id, dt.cost, dt.nds, dt.costwnds);

		        	break; 
		        
		        }
		            
		        case 4: { 
		        	
		        	dt.nds = (double) value; 
		        	
		        	WsContractsSqlStatements.updateContractPriceCost(dt.id, dt.cost, dt.nds, dt.costwnds);
		        	
		        	break; 
		        	
		        }
		            
		        case 5: { 
		        	
		        	dt.costwnds = (double) value;  
		        	
		        	WsContractsSqlStatements.updateContractPriceCost(dt.id, dt.cost, dt.nds, dt.costwnds);
		        	
		        	break; 
		        	
		        }
		            
		 		case 6:  { dt.id_units= ((int) value);break; }
	    		
	    		case 7:  { dt.id_part_type= ((int) value); break; }

	    		case 8:  { dt.id = ((int) value); break; }
	        } 
	        
	        this.fireTableDataChanged();
	  }


	 public boolean isRowNew(int row ) { return ((int)getValueAt(row, 14)) == -1; }

	/**
	 * @param i
	 */
	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public void addRow(WsContractPriceData dt) {
		
		m_vec.add(dt);
		
		this.fireTableDataChanged();
		
	}	
	
	public void deleteRow(int index) {
		
		if(index != -1) {
			
			m_vec.remove(index);
			
			this.fireTableDataChanged();
		}
		
	}
	
	public String isDataValid() {
		
		
		String message = null;
		
		for(int i = 0; i < m_vec.size(); ++i) {
			
			WsContractPriceData d = m_vec.elementAt(i);
			
			if( d.name == null || d.name.isEmpty() ||  WsTokenizer.isValidate(d.name) == false ) {
				
				message =  getMessagesStrs("wrongNameInTheRowPrihod");
				
				break;
			}
			
			if( d.id_part_type == -1 ) {
				
				message =  getMessagesStrs("noPartTypeInTheRowPrihod");
				
				break;
			}
			
			if( d.id_units == -1 ) {
				
				message =  getMessagesStrs("noUnitsInTheRowPrihod");
				
				break;
			}
		}
		
		return message;
		
	}
	       
};
