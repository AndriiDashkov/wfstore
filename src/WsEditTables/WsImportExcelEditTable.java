
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import WsDataStruct.WsAgentData;
import WsEvents.WsEvent;
import WsEvents.WsEventDispatcher;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsImportExcelEditTable extends JTable {
	
	private static final long serialVersionUID = 1L;

	WsImportExcelEditTableModel  m_model = null;

	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemDelete = null;
	
	JMenuItem m_itemDeleteAll = null;

	public  WsImportExcelEditTable(String[] columnNames , boolean enablePopupMenu) {

		init(columnNames, enablePopupMenu);
	
	}
	
	protected void init(String[] columnNames, boolean enablePopupMenu) {
		
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		setFillsViewportHeight(true);
		
		m_model = new  WsImportExcelEditTableModel(columnNames);

		setModel(m_model);
		
		if(columnNames.length > 1 ) {
		
		     TableColumn agColumn = getColumnModel().getColumn(1);
		     
		     WsAgentsCellEditor ed_part = new WsAgentsCellEditor();
		     
		     agColumn.setCellEditor(ed_part);
		     
		     agColumn.setCellRenderer(new WsAgentsCellRenderer());
		
		}
	     
		hideColumns();
		
		getTableHeader().setReorderingAllowed( false );
		
		if(enablePopupMenu) {
		
			setPopupMenu();
		
		}
		
	}

	public void clearTable() {
		
		Vector<WsAgentData> vec_empty = new Vector<WsAgentData>();
		
		m_model.setVector( vec_empty );
		
		WsEventDispatcher.get().fireCustomEvent (new WsEvent(WsEventDispatcher.TABLE_ROWS_NEW_COUNT_EVENT, 
      				this));

	}
	
	public String isDataValid() {

		return m_model.isDataValid();
	}
	
	protected void hideColumns( ) {

		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			//removeColumn(getColumnModel().getColumn(3));
			
		}
		
	}
	
	public Vector<WsAgentData> getCurrentVectorData() {
		
		return m_model.getCurrentVectorData();
		
	}
	
	protected void setPopupMenu() {
		
		   m_popupMenu = new JPopupMenu();
		   
		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableItemMenu"));
		   
		   m_itemDeleteAll = new JMenuItem(getGuiStrs("deleteAllTableItemMenu"));
		 	   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemDelete.addActionListener( listener);
	       
	       m_itemDeleteAll.addActionListener( listener);
  
	       m_popupMenu.add(m_itemDelete);
	       
	       m_popupMenu.add(m_itemDeleteAll);
	        
	       setComponentPopupMenu(m_popupMenu);
		
	}
	
	private class CustomPopupListener implements ActionListener {


		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
               if (menu == m_itemDelete) {
            	   
             	   if (isEditing()) {
             		   
          	        	getCellEditor().cancelCellEditing();
          	       }
            	   
            	   int index = WsImportExcelEditTable.this.getSelectedRow();
               	
            	   if(index != -1) {
            		   
	            	   m_model.deleteRow(index);
	            	   
	                   m_model.fireTableDataChanged();

	           		   WsEventDispatcher.get().fireCustomEvent (new WsEvent(WsEventDispatcher.TABLE_ROWS_NEW_COUNT_EVENT, 
	           				this));
            	   }
               } 
               
               if (menu == m_itemDeleteAll) {
            	   
             	   if (isEditing()) {
          	        	getCellEditor().cancelCellEditing();
          	       }
            	   
        
            	   m_model.clear();
            	   
                   m_model.fireTableDataChanged();

           		   WsEventDispatcher.get().fireCustomEvent (new WsEvent(WsEventDispatcher.TABLE_ROWS_NEW_COUNT_EVENT, 
           				this));
               } 
			
		}

	}; 
	
	public Vector<WsAgentData> getData() { 
	
		return m_model.getCurrentVectorData(); 
	}
	
	public void addRow(WsAgentData s) { 
	
		m_model.addRow(s);
		
		WsEventDispatcher.get().fireCustomEvent (new WsEvent(WsEventDispatcher.TABLE_ROWS_NEW_COUNT_EVENT, 
				this));
	}
	
	public void refresh() {
		
		m_model.fireTableDataChanged();
		
	}
	
}