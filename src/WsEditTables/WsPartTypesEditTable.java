
package WsEditTables;

import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import WsDataStruct.WsPartType;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPartTypesEditTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	WsPartTypesEditTableModel m_model = new WsPartTypesEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;
	   

	public  WsPartTypesEditTable() {
		
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	     
	     setPopupMenu();
	     
	     hideColumns();
	     
	     getColumnModel().getColumn(0).setMaxWidth(50);
	     
	     getColumnModel().getColumn(0).setMinWidth(50);
	     
	     getColumnModel().getColumn(1).setMinWidth(100);
	    
	     getTableHeader().setReorderingAllowed( false );
	     
	     for(int i = 0; i < 3; ++i) {
	    	 
	    	 TableColumn col = getColumnModel().getColumn(i);
			    
			 col.setCellEditor( new DefaultCellEditor(new JTextField()));
	     }
	     
	     
	     setCustomFont();
	     
	}
	
	
	 public void setEditable(boolean flag) {  m_model.setEditable(flag); }
	

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
               	
            	   WsPartType data = new WsPartType();
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	 
            	   data.id = WsUtilSqlStatements.insertPartType(data);
            	   
                   m_model.addRow(data);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   int index = WsPartTypesEditTable.this.getSelectedRow();
            	   
            	   if(index == -1) { return; }
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
           	       }
            	   
            	   WsPartType dt = ((WsPartTypesEditTableModel) getModel()).getDataAt(index);
            	   
            	   //this kod can't be deleted
            	  if(  WsUtils.isKodEqual(dt.kod, WsUtils.getUnknownKatalogKod()) ) { return; }
            	   
            	  int res = WsUtils.showYesNoDialog(getMessagesStrs("messageDeleteKodFromKatalogAnswerCaption"));
            	   
            	  if ( 1 == res) {
            			
	            	   if(WsUtilSqlStatements.checkPartTypeIsUsed(dt.id)) {
	            		   
	            		   String operationMessage = getMessagesStrs("deleteUsedPartTypeFault");

	          			   WsUtils.showMessageDialog(operationMessage);
	            		   
	            		   return;
	            	   }
	            	   
	            	   WsUtilSqlStatements.deletePartType(dt);
	            	   
	                   m_model.refresh();
                   
            		}
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
			
			removeColumn(getColumnModel().getColumn(5));	
			
			removeColumn(getColumnModel().getColumn(3));
			
			removeColumn(getColumnModel().getColumn(2));
		}
		
	}
	
	public Vector<WsPartType> getCurrentVectorData() {
		
		 return m_model.getCurrentVectorData();
	}
	
	public void addUniqueIdRow(WsPartType dt) {
		
		m_model.addUniqueIdRow(dt);
	}
	
	public void deleteRow(int row_id) {
		
		m_model.getCurrentVectorData().remove(row_id);
		
		m_model.fireTableDataChanged();

	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(m_popupMenu, f); 
		
	    for(int i = 0; i < 3; ++i) {
	    	 
	    	DefaultCellEditor ed = (DefaultCellEditor)getColumnModel().getColumn(i).getCellEditor();
	 		
	 		ed.getComponent().setFont(f);
	    }
	
	}

}
