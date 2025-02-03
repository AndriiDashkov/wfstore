
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsRashodSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsRashodPartEditTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	WsRashodPartEditTableModel m_model = new WsRashodPartEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	   
	JMenuItem m_itemDelete = null;
	   
	JMenuItem m_itemDeleteAll = null;
	   
	public  WsRashodPartEditTable() {
		
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	     
	     TableColumn unitsColumn = getColumnModel().getColumn(4);
	     
	     unitsColumn.setCellEditor(new  WsUnitsCellEditor());
	     
	     unitsColumn.setCellRenderer(new  WsUnitsCellRenderer());
	     
	     TableColumn costColumn = getColumnModel().getColumn(5);
	     
	     costColumn.setCellEditor(new  WsCostRashodControlCellEditor(5));

	     costColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn ndsColumn = getColumnModel().getColumn(6);
	     
	     ndsColumn.setCellEditor(new  WsCostRashodControlCellEditor(6));

	     ndsColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn costndsColumn = getColumnModel().getColumn(7);
	     
	     costndsColumn.setCellEditor(new  WsCostRashodControlCellEditor(7));

	     costndsColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     setPopupMenu();
	     
	     getColumnModel().getColumn(0).setMaxWidth(80);
	     
	     getColumnModel().getColumn(0).setMinWidth(80);
	     
	     getColumnModel().getColumn(1).setMinWidth(200);
	     
	     getColumnModel().getColumn(2).setMaxWidth(150);
	     
	     getColumnModel().getColumn(2).setMinWidth(150);
	    
	     getColumnModel().getColumn(3).setMaxWidth(100);
	     
	     getColumnModel().getColumn(4).setMaxWidth(70);
	     
	     getColumnModel().getColumn(5).setMaxWidth(100);
	     
	     getColumnModel().getColumn(5).setMinWidth(100);
	     
	     getColumnModel().getColumn(6).setMaxWidth(70);
	     
	     getColumnModel().getColumn(7).setMaxWidth(100);
	     
	     getColumnModel().getColumn(7).setMinWidth(100);

	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	}
	
	
	public void refreshData(int id_sales_invoice) {
		
	    m_model.deleteAllRows();
	        
    	if (id_sales_invoice == -1) { return; }
    	 
    	Vector<WsRashodPartData> vec = WsRashodSqlStatements.getRashodPartsList(id_sales_invoice, 0);
    	
    	for(int i = 0; i < vec.size(); ++i) {
    		
    		WsRashodPartData d = vec.elementAt(i);
    		
    		m_model.addRow(d);
    			
    	}
	    
		m_model.fireTableDataChanged();
			
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, 11);
		}
		
		return selected_id;
	}
	
	public Vector<WsRashodPartData> getParts() {
		
		return   m_model.getVectorCopy();
					
	}
	

	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();

			   
               if (menu == m_itemDelete) {
            	   
            	   int index = WsRashodPartEditTable.this.getSelectedRow();
            	   
            	   if (isEditing()) {
            		   
            	        getCellEditor().cancelCellEditing();
            	    }
            	   
                   m_model.deleteRow(index);
                   
               } else if (menu ==  m_itemDeleteAll) {
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	   m_model.deleteAllRows();
               }
		}

	};  
	
	
	
	private void setPopupMenu() {
		
		   m_popupMenu = new JPopupMenu();
		  
		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsrowdelete.png") );
		   
		   m_itemDeleteAll = new JMenuItem(getGuiStrs("deleteAllTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsdeleteall.png"));
		   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemDelete.addActionListener( listener);
	        
	       m_itemDeleteAll.addActionListener( listener);
	       
	
	        m_popupMenu.add(m_itemDelete);
	        
	        m_popupMenu.add(m_itemDeleteAll);
	      
	      
	        setComponentPopupMenu(m_popupMenu);
		
	}
	
	public String isDataValid() {
		
		return m_model.isDataValid();
	}
	
	public void insertDataRow(WsRashodPartData insert_data) {
		
		((WsRashodPartEditTableModel) getModel()).addRow(insert_data);
		
		
	}
	
	public boolean isRowWithIdInvoicePartsInserted(int id_invoice_parts ) { 
		
		return ((WsRashodPartEditTableModel) getModel()).isRowWithIdInvoicePartsInserted( id_invoice_parts );
	}
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(12));
			
			removeColumn(getColumnModel().getColumn(11));
			
			removeColumn(getColumnModel().getColumn(10));
			
			removeColumn(getColumnModel().getColumn(9));
		
		}
	}

	public void setOldValuesVector(WsRashodData dt) {
		
		((WsRashodPartEditTableModel) getModel()).setOldValuesVector(dt);
		
	}
	
	public void deleteAllRows() {
		
		((WsRashodPartEditTableModel) getModel()).deleteAllRows();
	}
	
	public String checkForPrihodDate(Date prihod_date) {
		
		 return m_model.checkForPrihodDate(prihod_date);
		
	}
	
	public WsRashodPartData setSelectedRowForId(int id_invoice_parts) {
		
		WsRashodPartData d = m_model.getRowDataForId(id_invoice_parts );
		
		if(d != null) {
			
			int index  = d.row_index;
			
			setRowSelectionInterval(index, index);
			
			Rectangle cellRect = getCellRect(index, 0, true);
			 
			scrollRectToVisible(cellRect);
			
			return d;
		}
		
		return null;
		
	}
	
	public void setLastRowSelected() {
		
		int index = m_model.getRowCount() - 1;
		
		if(index > -1) {
			
			setRowSelectionInterval(index, index);
			
			Rectangle cellRect = getCellRect(index, 0, true);
			 
			scrollRectToVisible(cellRect);
			
		}
	}
	
	public WsNaklSums getSums() {
		
		return m_model.getSums();
		
	}

}
