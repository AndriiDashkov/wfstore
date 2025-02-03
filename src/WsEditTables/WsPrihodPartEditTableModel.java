
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsUnitData;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPrihodPartEditTableModel extends AbstractTableModel{
	

	private static final long serialVersionUID = 1L;

	private Vector<WsPrihodPartData> m_vec = new Vector<WsPrihodPartData>();
	


	String[] m_columnNames = {
			getGuiStrs("skladColumnVenCode2KodName"),
			getGuiStrs("prihodPartsColumnPartTypeName"), 
			getGuiStrs("prihodPartsColumnPartName"),
			getGuiStrs("prihodPartsColumnQuantityName"), 
			getGuiStrs("prihodPartsColumnRestName"),
			getGuiStrs("prihodPartsColumnUnitsNameName"), 
			getGuiStrs("prihodPartsColumnCostName"), 
			getGuiStrs("prihodPartsColumnNdsName"), 
			getGuiStrs("varWithNdsLabel"),
			getGuiStrs("prihodPartsColumnInfoName"),
			getGuiStrs("prihodPartsColumnClosedName"),
			"id_invoice","id_part_type", "id_units","rest", "id"};
	
	int m_id_index = 15;
	
	public int getIdColumnIndex() { return m_id_index; }

	public WsPrihodPartEditTableModel() {
		
		
	}
	
	public Vector<WsPrihodPartData> getModelVectorCopy() {
		
		Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>();
		
		for(WsPrihodPartData  d: m_vec) {
			
			vec.add(new WsPrihodPartData(d));
		}
		
		return vec;
		
	}
	
    public String getColumnName(int column) {
    	
        return m_columnNames[column];
    }

   public int getColumnCount() { 
	   
        return 16; 
    }

    public int getRowCount() {
    	
        return m_vec.size();
    }
    
    public boolean isEmpty() {
    	
    	return m_vec.isEmpty();
    }
    
    
	 public WsPrihodPartData getValueAt(int row) {
		 
		 	if(row >= m_vec.size()) { return null;}
		    	
		    	return m_vec.elementAt(row);
		    	
	 }

    public Object getValueAt(int row, int col) { 
    	
    	WsPrihodPartData dt = m_vec.elementAt(row);
    	
    	switch(col) {
    	
    		case 0: { 
    			return dt.vendorcode2;
    		}
    		case 1:{
    			
    			WsPartType d = new WsPartType(); 
    			
    			d.name = dt.part_type_name;
    			
    			d.id = dt.id_part_type;
    			
    			return d;
    		}
    		case 2: return dt.name;
    		
    		case 3: return dt.quantity;
    		
    		case 4: return WsUtils.getDF_for_tables(dt.rest); 
    		
    		case 5: {
    			
    			WsUnitData d = new WsUnitData();
    			
    			d.name = dt.units_name;
    			
    			d.id = dt.id_units;
    			
    			return d;
    		}
    		
    		
    		case 6: return dt.cost;
    		
    		case 7: return dt.nds;
    		
    		case 8: return dt.costwithnds;
    		
    		case 9: {
    			
    			return dt.info;
    		}
    		
    		case 10: {
    			
    			return dt.closed;
    		}
    	
    		case 11: return dt.id_invoice;
    		
    		case 12: return dt.id_part_type;
    		
    		case 13: return dt.id_units;
    		
    		case 14: return dt.rest;//current store rest
    		
    		case 15: return dt.id;
    	
    	};
    	
        return null; 
    }

	 public boolean isCellEditable(int row, int col) {
	    			
	             return  col < 9 && col != 5 && col != 4 && col != 2;
	 }
	    
	    
	 public void setValueAt(Object value, int row, int col) {
		 
		 	WsPrihodPartData dt = m_vec.elementAt(row);
	         
	        switch (col) {
	        
	         
		        case 0: {
		        	
		            dt.vendorcode2 = ((String) value);
		            
		            break;
		        }
	        
		        case 1: {
		        	
			        dt.part_type_name = ((WsPartType) value).name;
			        	
			        dt.id_part_type = ((WsPartType) value).id;
		    	
		            break;
		            
		        }
		            
		        case 2: {
		        	
		            dt.name = (String) value;
		            
		            break;
		        }
		        case 3: {
		  
		            dt.quantity = (double) value;
		            
		            break;
		        }
		        case 4: {
		        	
		            dt.rest = Double.parseDouble((String) value);
		            
		            break;
		        }
		        case 5: {
		        	
		            dt.units_name = ((WsUnitData) value).name;
		            
		            dt.id_units = ((WsUnitData) value).id;
		            
		            break;
		        }
		        case 6: {
		        	
		            dt.cost = (double) value;
		            
		            break;
		        }
		            
		        case 7: {
		        	
		            dt.nds = (double) value;
		            
		            break;
		            
		        }
		        case 8: {
		        	
		            dt.costwithnds = (double) value;
		            
		            break;
		            
		        }
		        case 9: {
		        	
		            dt.info = (String) value;
		            
		            break;
		        }
		        case 10: {
		        	
		        
		        	dt.closed = (boolean) value;
				            
		            break;
		        }
		            
	    		case 11:  { dt.id_invoice = ((int) value); break; }
	    		
	    		case 12:  { dt.id_part_type= ((int) value); break; }
	    		
	    		case 13: { dt.id_units= ((int) value); break; }
	    		
	    		case 14: { dt.rest = ((double) value); break; }
	    		
	    		case 15: { dt.id = ((int) value); break; }
	        } 
	        
	        this.fireTableDataChanged();
	  }


    public boolean isRowNew(int row ) { return ((int)getValueAt(row, m_id_index)) == -1; }

	public void deleteAllRows() {
		
		m_vec.clear();
		
		this.fireTableDataChanged();
		
	}
	
	public void addRow(WsPrihodPartData dt) {
		
		m_vec.add(dt);
		
		//this.fireTableDataChanged();
		
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
			
			WsPrihodPartData d = m_vec.elementAt(i);
			
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
	
	void insertKodAndName(WsPartType p, int row) {

		WsPrihodPartData dt = m_vec.elementAt(row);
		
		if(Math.abs(dt.quantity - dt.rest) < WsUtils.getRZL()) {
		

			dt.id_part_type = p.id;
			
			dt.name = p.name;
			
			dt.vendorcode2 = String.valueOf(p.kod);
			
			dt.kod = p.kod;
			
			dt.cost = p.costwithnds/p.nds_coeff;
			
			dt.nds = p.costwithnds - dt.cost;
			
			dt.costwithnds = p.costwithnds;
				
		}
		else {
			
			int res = WsUtils.showYesNoDialog(getMessagesStrs("quesitonChangeKodPrihodRowRashod"));
	      	   
			int input = (res  == 1) ? 0: 1;
			//0 - yes
			switch(input) {
			
				case 0: {
					
					dt.kod_changed_flag = true; 
					
					dt.id_part_type = p.id;
						
					dt.name = p.name;
						
					dt.vendorcode2 = String.valueOf(p.kod);
					
					dt.kod = p.kod;
					
					dt.cost = p.costwithnds/p.nds_coeff;
					
					dt.nds = p.costwithnds - dt.cost;
					
					dt.costwithnds = p.costwithnds;
					
					break;
				}
				
				default:
					
				case 1:
					
				case 2: {
					
					break;
				}
			
			};
		}
		
		this.fireTableDataChanged();
		
	}
	
	public boolean canDeleteAll() {
		
		for(int i =0; i < m_vec.size(); ++i) {
			
			WsPrihodPartData  d =  m_vec.get(i);
			
			if( Math.abs(d.rest - d.quantity) > WsUtils.getRZL()) {
				
				return false;
			}
		}
		
		return true;
		
	}
	
	public WsNaklSums getSums() {
		
		WsNaklSums s = new WsNaklSums();
		
		for(WsPrihodPartData d : m_vec ) {
			
			s.sum += d.cost*d.quantity ;
			
			s.sumnds += d.nds*d.quantity;
			
			s.sumwithnds += (d.costwithnds)*d.quantity;
			
		}
		
		return s;
	
	}       
};
