
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Component;
import java.awt.Rectangle;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import WsDataStruct.WsPrihodPartData;
import WsDatabase.WsSkladSqlStatements;
import WsEditTables.WsQuantityControlCellRenderer;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsSkladTable extends JTable {
	
	private static final long serialVersionUID = 1L;

	Vector<WsPrihodPartData> m_vec  = null;
	
	DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	
	TableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {

		private static final long serialVersionUID = 1L;
		
		SimpleDateFormat f = new SimpleDateFormat(WsUtils.DATE_FORMAT);

	    public Component getTableCellRendererComponent(JTable table,
	            Object value, boolean isSelected, boolean hasFocus,
	            int row, int column) {
	    	
	        if( value instanceof Date) {
	        	
	            value = f.format(value);
	        }
	        
	        return super.getTableCellRendererComponent(table, value, isSelected,
	                hasFocus, row, column);
	    }
	};
	

	String[] m_columnNames = {getGuiStrs("skladDateColumnName"),
			getGuiStrs("skladColumnVenCode2KodName"), 
			getGuiStrs("skladNameColumnName"),
			getGuiStrs("skaldRestColumnName2"), 
			getGuiStrs("skladColumnUnitsName"),
			getGuiStrs("costNameInReportNoNDS"), 
			getGuiStrs("prihodPartsColumnNdsName"),
			getGuiStrs("contractName"),
			getGuiStrs("skladInfoColumnName"),
			"id_part_type", "id_units", "id_invoice","id", 			
			getGuiStrs("skladPartTypeColumnNameName") };
	
	
	public WsSkladTable() {
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     getColumnModel().getColumn(0).setMaxWidth(85);
	     
	     getColumnModel().getColumn(0).setMinWidth(85);
	     
	     getColumnModel().getColumn(1).setMinWidth(100);
	     
	     getColumnModel().getColumn(1).setMaxWidth(100);																		
	     
	     getColumnModel().getColumn(2).setMinWidth(300);
	     
	    // getColumnModel().getColumn(2).setMaxWidth(300);
	     
	     getColumnModel().getColumn(3).setMinWidth(140);
	     
	     getColumnModel().getColumn(3).setMaxWidth(140); 
	     
	     getColumnModel().getColumn(4).setMaxWidth(60);
	     
	     getColumnModel().getColumn(4).setMinWidth(60);
	     
	     getColumnModel().getColumn(5).setMaxWidth(180);
	     
	     getColumnModel().getColumn(5).setMinWidth(180);
	     
	     getColumnModel().getColumn(6).setMinWidth(140);
	     
	     getColumnModel().getColumn(6).setMaxWidth(140);
	     
	     getColumnModel().getColumn(7).setMinWidth(250);
	     
	     getColumnModel().getColumn(7).setMaxWidth(250);
	     
	     TableColumn zalColumn = getColumnModel().getColumn(3);

	     zalColumn.setCellRenderer(new WsQuantityControlCellRenderer());

	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	}

	public void refreshData(Date dt, int sort_flag, boolean groupByKod) {
		 
			m_vec  = null;
			
			if(dt == null) {
				
				if(groupByKod) {
					
					m_vec  = WsSkladSqlStatements.getSkladListGroupKod(sort_flag);
					
				}
				else {
					
					m_vec  = WsSkladSqlStatements.getSkladList(sort_flag);
				}
				
			}
			else {
				
				m_vec  = WsSkladSqlStatements.getSkladListAvailableForDate(dt, sort_flag, groupByKod);
			}
		
    	    
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	    
    	    for(int i = 0; i < m_vec.size(); ++i) {
    	    	
    	    	WsPrihodPartData d = m_vec.elementAt(i);
    	    	
    	    	if(!groupByKod) {
    	    		
    	    		d.contract_name = d.contract_name + " " + getGuiStrs("vidNaklName2") + " " +  WsUtils.dateToString(d.contract_date, "dd-MMMM-yyyy" );
    	    	}
    	    	else {
    	    		
    	    		d.contract_name = "";
    	    	}

    	        m_model.addRow(new Object[]{ d.date, 
    	        		 d.vendorcode2, 
			    		 d.name,
			    		 WsUtils.getDF_fix(d.rest, 5), 
			    		 d.units_name,
			    		 WsUtils.getDF_fix(d.cost,3),
			    		 WsUtils.getDF_fix(d.nds,3),
			    		 d.contract_name,
			    		 d.info,
			    		 d.id_part_type, 
			    		 d.id_units, 
			    		 d.id_invoice, 
			    		 d.id,
			    		 d.part_type_name, 
			    		 });
    	    	
    	    }
		
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = m_vec.elementAt(selected_id).id;
		}
		
		return selected_id;
	}
	
	public WsPrihodPartData getSelectedDataOrderPart() {

			int selected_id = getSelectedRow();
			
			if (selected_id == - 1) { return null; }
			
			return m_vec.elementAt(selected_id);
			
	}
	
	public Vector<WsPrihodPartData> getSelectedDataOrderParts() {

			int[] selected_id = getSelectedRows();
			
			if (selected_id.length == 0) { return null; }
			
			Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>();
			
			for(int i =0; i < selected_id.length; ++i) {
				
				vec.add( m_vec.elementAt(selected_id[i]));
			}
			
			return vec;
			
	}
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(13));
			
			removeColumn(getColumnModel().getColumn(12));
			
			removeColumn(getColumnModel().getColumn(11));
			
			removeColumn(getColumnModel().getColumn(10));
			
			removeColumn(getColumnModel().getColumn(9));

		}
		
	}
	
	
	public Vector<WsPrihodPartData> getDataVector() { return m_vec; }
	
	public void findKod(int kod) {
		
		if(kod > 0) {
		
			int index = findKodin_vec(kod);
			
			if(index != -1) {
				
				setRowSelectionInterval(index, index);
				
				Rectangle cellRect = getCellRect(index, 0, true);
				 
				scrollRectToVisible(cellRect);
			}
		
		}
	}
	
	
	//return the index of the found row
	public int findKodin_vec(int kod) {
		
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
		
		for(int i = 0; i < m_model.getRowCount(); ++i) {
			
			String d =  ((String) m_model.getValueAt(i, 1));
			
			if( ((int)(Integer.valueOf(d)/del) ) == kod) {
				
				return i;
			}
			
		}
		
		return -1;
	}
}