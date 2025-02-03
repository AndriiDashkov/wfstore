

package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.sql.Date;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsRashodSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashodPartEditTableModel extends AbstractTableModel{
	

		private static final long serialVersionUID = 1L;

		private Vector<WsRashodPartData> m_vec = new Vector<WsRashodPartData>();
		
		Vector<WsRashodPartData> m_old_values_vec = new Vector<WsRashodPartData>();
		
		String[] m_columnNames = { 
				getGuiStrs("rashodPartsColumnVenCode2Name"),
				getGuiStrs("rashodPartsColumnPartName"),
				getGuiStrs("prihodPartsColumnQuantityName"),
				getGuiStrs("rashodPartsColumnReqQuantityName"),
				getGuiStrs("rashodPartsColumnUnitsNameName"), 
				getGuiStrs("costNameInReportNoNDS"), 
				getGuiStrs("prihodPartsColumnNdsName"),	
				getGuiStrs("prWithPdv"),
				getGuiStrs("rashodPartsColumnInfoName"),
				"id_sale_invoice", "id_units", "id_invoice_parts", "id"};
	
		public WsRashodPartEditTableModel() {
			
		}
		
	    public String getColumnName(int column) {
	    	
	        return m_columnNames[column];
	    }
	
	   public int getColumnCount() { 
		   
	        return 13; 
	    }

	    public int getRowCount() {
	    	
	        return m_vec.size();
	    }

	    public Object getValueAt(int row, int col) { 
	    	
	    	WsRashodPartData dt = m_vec.elementAt(row);
	    	
	    	switch(col) {
	    	
	    		case 0: return dt.vendor_code_2;
	    		
	    		case 1: return dt.name;
	    		
	    		case 2: return dt.quantity;
	    		
	    		case 3: return dt.req_quantity;
	    		
	    		case 4: {
	    			
	    			WsUnitData d = new WsUnitData();
	    			
	    			d.name = dt.units_name;
	    			
	    			d.id = dt.id_units;
	    			
	    			return d;
	    		}
	    		
	    		case 5: return dt.cost;
	    		
	    		case 6: return dt.nds;
	    		
	    		case 7: return dt.costwithnds;
	 
	    		case 8: {
	    			
	    			return dt.info;
	    		}
	    		
	    		case 9: return dt.id_sale_invoice;
	    		
	    		case 10: return dt.id_units;
	    		
	    		case 11: return dt.id_invoice_parts;
	    		
	    		case 12: return dt.id;
	    	
	    	};
	    	
	        return null; 
	    }

	    public boolean isCellEditable(int row, int col) {
	    		
	           return  col < 9	&& col > 1;
	     }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 	WsRashodPartData dt = m_vec.elementAt(row);
	         
	        switch (col) {
		            
	        	case 0: {
	        		
	        		dt.vendor_code_2 = ((String) value);
	        		
	        		break;
	        	}
	        	
		        case 1: {
		        	
		            dt.name = (String) value;
		            
		            break;
		        }
		        
		        case 2: {
		        	
		        	try {
		        		
		        		dt.quantity = Double.parseDouble((String) value);
		        		
		        		WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
		                
		        	 	WsEventDispatcher.get().fireCustomEvent(ev);
		        		
		        		
		        	} catch(java.lang.NumberFormatException e) {
		        		
		        		
		        	}
		   
		            break;
		        }
		        
		        case 3: {
		        	
		        	try {
		        		
		        		dt.req_quantity = Double.parseDouble((String) value);
		        		
		        	} catch(java.lang.NumberFormatException e) {}
		       
		            break;
		        }
		        
		        case 4: {
		        	
		            dt.units_name = ((WsUnitData) value).name;
		            
		            dt.id_units = ((WsUnitData) value).id;
		            
		            break;
		        }
		        
		        case 5: {
		        	
		            dt.cost = (double) value;

		            break;
		        }
		        case 6: {
		        	
		            dt.nds = (double) value;
		            
		            break;
		        }  
		        case 7: {
		        	
		            dt.costwithnds = (double) value;
		            
		            break;
		        } 
		        case 8: {
		        	
		            dt.info = (String) value;
		            
		            break;
		        } 
	    		case 9: { dt.id_sale_invoice = ((int) value);   break; }
	    		
	    		case 10: { dt.id_units = ((int) value);          break; }
	    		
	    		case 11: { dt.id_invoice_parts = ((int) value); break; }
	        } 
	        
	        this.fireTableDataChanged();
	  }


	 public boolean isRowNew(int row ) { return ((int)getValueAt(row, 12)) == -1; }
	 
	 
	 public boolean isRowWithIdInvoicePartsInserted(int id_invoice_parts ) { 
		 
		 for(int i = 0 ; i < m_vec.size(); ++i) {
			 
			 if(m_vec.elementAt(i).id_invoice_parts == id_invoice_parts) return true;
			 
		 }
		 
		 return false;
	 }
	 
	 public WsRashodPartData getRowDataForId(int id_invoice_parts ) { 
		 
		 for(int i = 0 ; i < m_vec.size(); ++i) {
			 
			 WsRashodPartData d = m_vec.elementAt(i);
			 
			 if(d.id_invoice_parts == id_invoice_parts)  { 
				 
				 d.row_index = i;
				 
				 return d; 
			 }
			 
		 }
		 
		 return null;
	 }


	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public void addRow(WsRashodPartData dt) {
		
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
			
			WsRashodPartData d = m_vec.elementAt(i);
			
			if( d.name == null || d.name.isEmpty() ||  WsTokenizer.isValidate(d.name) == false ) {
				
				message =  getMessagesStrs("wrongNameInTheRowPrihod");
				
				break;
			}
			
			
			if( d.id_units == -1 ) {
				
				message =  getMessagesStrs("noUnitsInTheRowPrihod");
				
				break;
			}
			
			WsRashodPartData oldD = findOldValueForInvoicePartId(d.id_invoice_parts);
			
			double oldValue = 0.0;
			
			if(oldD != null) {
				
				oldValue = oldD.quantity;
			}
			
			if( d.quantity > (d.rest + oldValue) ) {
				
				message =  getMessagesStrs("quantityIsLessThanRetsInTheRowRashod") + " " + Double.toString(d.quantity) +
						" > " + Double.toString(d.rest + oldValue);
				
				break;
			}
		}
		
		return message;
		
		
	}
	
	public void setOldValuesVector(WsRashodData dt) {
		
		m_old_values_vec =  WsRashodSqlStatements.getRashodPartsVector(dt.id);
	}
	
	public WsRashodPartData findOldValueForInvoicePartId(int id_invoice_parts) { 
		
		for(int i =0; i < m_old_values_vec.size(); ++i) {
			
			 WsRashodPartData v = m_old_values_vec.elementAt(i);
			 
			 if(v.id_invoice_parts ==  id_invoice_parts) { return v; }
		}
		
		 return null;
	
	}
	
	public String checkForPrihodDate(Date prihod_date) {
		
		if(prihod_date == null) { return null; }
		
		for(int i = 0; i < m_vec.size(); ++i) {
			
			WsRashodPartData d = m_vec.elementAt(i);
			
			WsPrihodData dp =  WsPrihodSqlStatements.getPrihodForPartId(d.id_invoice_parts);
			
			if(dp.date.compareTo(prihod_date) > 0) {
				
				return
				new String(String.valueOf(d.kod) + ": "+
				getGuiStrs("dataPrihodDateControl")+" " + WsUtils.dateToString(dp.date, "dd-MM-yy" ));
			}			
		}
		return null;
		

		
	}
	
	public Vector<WsRashodPartData> getVectorCopy() {
		
		Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>();
		
		for(int i = 0; i < m_vec.size(); ++i) {
			
			WsRashodPartData d = new WsRashodPartData (m_vec.elementAt(i));
			
			vec.add(d);
			
		}
		
		return vec;
		
		
	}
	
	
	public WsNaklSums getSums() {
		
		WsNaklSums s = new WsNaklSums();
		
		for(WsRashodPartData d : m_vec ) {
			
			s.sum += d.cost*d.quantity ;
			
			s.sumnds += d.nds*d.quantity;
			
			s.sumwithnds += (d.cost + d.nds)*d.quantity;
			
		}
		
		return s;
	
	}
	       
};
