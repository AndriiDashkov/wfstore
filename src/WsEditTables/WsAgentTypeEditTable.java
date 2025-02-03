
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import WsDataStruct.WsAgentTypeData;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsAgentTypeEditTable  extends JTable {
	

	private static final long serialVersionUID = 1L;
	
	 WsAgentTypeEditTableModel m_model = new  WsAgentTypeEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;
	   

	public  WsAgentTypeEditTable() {
		
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
               	
            	   WsAgentTypeData data = new WsAgentTypeData();
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	 
            	   data.id = WSAgentSqlStatements.createNewAgentType(data);
            	   
                   m_model.addRow(data);
                   
                    WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED);
   				
   					WsEventDispatcher.get().fireCustomEvent(event);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   int index = WsAgentTypeEditTable.this.getSelectedRow();
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
           	       }
            	   
            	   WsAgentTypeData dt = ((WsAgentTypeEditTableModel) getModel()).getDataAt(index);
            	   
            	   WSAgentSqlStatements.deleteAgentTypeForId(dt.id);
            	   
                   m_model.refresh();
                   
                   WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED);
   				
   				   WsEventDispatcher.get().fireCustomEvent(event);
                   
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
			
			removeColumn(getColumnModel().getColumn(2));	
		}
		
	}
	
}
