
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;

import java.awt.Font;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import WsActions.WsDeleteAgentTypeAction;
import WsActions.WsEditAgentTypeAction;
import WsActions.WsNewAgentTypeAction;
import WsDataStruct.WsAgentTypeData;
import WsDatabase.WsAgentSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class  WsAgentTypesTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemNew = null;
	   
	JMenuItem m_itemEdit = null;
	   
	JMenuItem m_itemDelete = null;

	DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	String[] m_columnNames = {"id",getGuiStrs("agentTypeColumnNameName"), getGuiStrs("agentTypeInfoColumnName")};
	
	public   WsAgentTypesTable() {
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     getColumnModel().getColumn(0).setMaxWidth(30);
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     setPopupMenu();
	     
	     setCustomFont();
	}
	
	
	public void refreshData() {
		
		 while (m_model.getRowCount() > 0) {
 	    	
 	        m_model.removeRow(0);
 	        
 	    }

		Vector<WsAgentTypeData> vec = WsAgentSqlStatements.getAgentsTypes();
		
		for(int i =0; i < vec.size(); ++i) {
			
			WsAgentTypeData d = vec.elementAt(i);

			 m_model.addRow(new Object[]{String.valueOf(d.id), d.name, d.info});
			
		}
		
		m_model.fireTableDataChanged();
		
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = Integer.parseInt((String) m_model.getValueAt(selected_id, 0));
		}
		
		return selected_id;
	}
	
	
	public WsAgentTypeData getSelectedDataAgentType() {
		
		int selected_id = getSelectedRow();
		
		WsAgentTypeData dt = new WsAgentTypeData();
		
		if (selected_id != - 1) {
		
			dt.id  = Integer.parseInt((String) m_model.getValueAt(selected_id, 0));
			
			dt.name  = (String) m_model.getValueAt(selected_id, 1);
			
			dt.info  = (String) m_model.getValueAt(selected_id, 2);
			
		}
		
		return dt;
	}
	
	private void setPopupMenu() {
		
		   m_popupMenu = new JPopupMenu();
		  
		   m_itemNew = new JMenuItem(getMenusStrs("addTableItemTypeMenu"),
				   WsUtils.get().getIconFromResource("wsrowdelete.png") );
		   
		   m_itemEdit = new JMenuItem(getMenusStrs("editTableItemTypeMenu"),
				   WsUtils.get().getIconFromResource("wsdeleteall.png"));
		   
		   m_itemDelete = new JMenuItem(getMenusStrs("deleteTableItemTypeMenu"),
				   WsUtils.get().getIconFromResource("wsdeleteall.png"));
		   	   
		   m_itemDelete.setAction(new WsDeleteAgentTypeAction(this));
		
		   m_itemNew.setAction(new WsNewAgentTypeAction());
	       
	       m_itemEdit.setAction(new WsEditAgentTypeAction(this));
	       
	       m_popupMenu.add(m_itemNew);
	       
	       m_popupMenu.add(m_itemEdit);
	
	       m_popupMenu.add(m_itemDelete);
	        
	       setComponentPopupMenu(m_popupMenu);
		
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 
		
		int margin_table = WsUtils.get().getTableMarginShiftForFont();
		
		setRowHeight(getRowHeight() + margin_table);
		
		WsGuiTools.changeFont(this, f);
		
		WsGuiTools.changeFont(m_popupMenu, f);

		getTableHeader().setFont(f);
			
	}
	
}
