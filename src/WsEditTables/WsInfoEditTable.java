
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import WsDataStruct.WsInfoData;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsInfoEditTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	WsInfoEditTableModel m_model = new WsInfoEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;
	   
	public  WsInfoEditTable() {
		
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	     
	     setPopupMenu();
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	}
	

	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, 9);
		}
		
		return selected_id;
	}
	
	private class CustomPopupListener implements ActionListener {


		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
               if (menu == m_itemAdd) {
               	
            	   WsInfoData data = new WsInfoData();
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	   data.id = WsUtilSqlStatements.insertInfoData(data);
            	   
                   m_model.addRow(data);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   int index = WsInfoEditTable.this.getSelectedRow();
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
           	       }
            	   
            	   WsInfoData dt = ((WsInfoEditTableModel) getModel()).getDataAt(index);
            	   
            	   if(dt.id < 2) {
            		   
            		   String operationMessage = getMessagesStrs("deleteStandardInfoFault");
           			
           				JOptionPane.showMessageDialog(
                  			    WsUtils.get().getMainWindow(),
                  			    operationMessage,
                  			    getMessagesStrs("messageInfoCaption"),
                  			    JOptionPane.CLOSED_OPTION);
            		   return;
            	   }
            	   
            	   WsUtilSqlStatements.deleteInfoData(dt);
            	   
                   m_model.refresh();
                   
               } 
		}

	};  
	

	private void setPopupMenu() {
			
		   m_popupMenu = new JPopupMenu();
		   
		   m_itemAdd = new JMenuItem(getGuiStrs("addTableItemMenu"));
		   
		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableItemMenu"));
		   
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
			
			removeColumn(getColumnModel().getColumn(9));	
		}
		
	}
	
}