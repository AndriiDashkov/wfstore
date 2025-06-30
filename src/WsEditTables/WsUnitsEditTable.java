package WsEditTables;

import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import WsDataStruct.WsUnitData;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsUnitsEditTable extends JTable {

	private static final long serialVersionUID = 1L;
	
	WsUnitsEditTableModel m_model = new WsUnitsEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;
	   
	public  WsUnitsEditTable() {
		
	    setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	    setFillsViewportHeight(true);
	     
	    setModel(m_model);
	     
	    setPopupMenu();
	     
	    hideColumns();
	     
	    getTableHeader().setReorderingAllowed( false );
	     
	    TableColumn col = getColumnModel().getColumn(0);
			    
		col.setCellEditor( new DefaultCellEditor(new JTextField()));
	     
	    setCustomFont();
	     
	}
	

	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, 0);
		}
		
		return selected_id;
	}
	
	

	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
			   
               if (menu == m_itemAdd) {
               	
            	   WsUnitData data = new WsUnitData();
            	   
            	   if (isEditing()) {
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	 
            	   data.id = WsUtilSqlStatements.insertUnit(data);
            	   
                   m_model.addRow(data);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   int index = WsUnitsEditTable.this.getSelectedRow();
            	   
            	   if (isEditing()) {
           	        	getCellEditor().cancelCellEditing();
           	       }
            	   
            	   WsUnitData dt = ((WsUnitsEditTableModel) getModel()).getDataAt(index);
            	   
            	   if(dt == null) { return; }
            	   
            	   if(dt.id < 6) {
            		   
            		   String operationMessage = getMessagesStrs("deleteStandardUnitFault");
           			
           			   WsUtils.showMessageDialog(operationMessage);
           			   
            		   return;
            	   }
            	   
            	   if(WsUtilSqlStatements.checkUnitIsUsed(dt.id)) {
            		   
          			   WsUtils.showMessageDialogLong(getMessagesStrs("deleteUsedUnitFault0"),
          					 getMessagesStrs("deleteUsedUnitFault1"));
            		   
            		   return;
            	   }
            	   
            	   WsUtilSqlStatements.deleteUnit(dt);
            	   
                   m_model.refresh();
                   
               } 
		}

	};  
	
	private void setPopupMenu() {
		
		   m_popupMenu = new JPopupMenu();
		   
		   m_itemAdd = new JMenuItem(getMenusStrs("addTableItemMenu"));
		   
		   m_itemDelete = new JMenuItem(getMenusStrs("deleteTableItemMenu"));
		   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemAdd.addActionListener( listener);
	       
	       m_itemDelete.addActionListener( listener);
	       
	       m_popupMenu.add(m_itemAdd);
	        
	       m_popupMenu.add(m_itemDelete);
	        
	       setComponentPopupMenu(m_popupMenu);
		
	}
	
	public String isDataValid() {
		
		return m_model.isDataValid();
	}
	
	public void refreshData() {
		
		m_model.refresh();
	}
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(1));	
		}	
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(m_popupMenu, f); 
		
		DefaultCellEditor ed = (DefaultCellEditor)getColumnModel().getColumn(0).getCellEditor();
	 		
	 	ed.getComponent().setFont(f);
	
			
	}
}
