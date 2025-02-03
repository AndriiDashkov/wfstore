
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import WsDataStruct.WsAgentData;
import WsDatabase.WSAgentSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsAgentListTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	String[] m_columnNames = {getGuiStrs("agentColumnNameName"), getGuiStrs("agentColumnTypeName"), 
			getGuiStrs("agentColumnContactName"), getGuiStrs("agentColumnInfoName"), "id_type", "id"};
	
	
	public WsAgentListTable() {
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );

	}
	
	
	public void refreshData(int id_types_index) {
		
		
	    while (m_model.getRowCount() > 0) {
	    	
	        m_model.removeRow(0);
	        
	    }
	    
	    Vector<WsAgentData>  vec = WSAgentSqlStatements.getAgentsList(id_types_index);
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsAgentData d = vec.elementAt(i);

			 m_model.addRow(new Object[]{ d.name, d.type_name,
		    		 d.contact, d.info, String.valueOf(d.id_type), String.valueOf(d.id)});
		}
	     
		
		m_model.fireTableDataChanged();
			
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = Integer.parseInt((String) m_model.getValueAt(selected_id, 5));
		}
		
		return selected_id;
	}
	
	
	public WsAgentData getSelectedDataAgent() {
		
		int selected_id = getSelectedRow();
		
		WsAgentData dt = new WsAgentData();
		
		if (selected_id != - 1) {
		
			dt.name  = (String) m_model.getValueAt(selected_id, 0);
			
			dt.id_type  = Integer.parseInt((String) m_model.getValueAt(selected_id, 4));
			
			dt.contact  = (String) m_model.getValueAt(selected_id, 2);
			
			dt.info  = (String) m_model.getValueAt(selected_id, 3);
			
			dt.id  = Integer.parseInt((String) m_model.getValueAt(selected_id, 5));
			
		}
		
		return dt;
	}
	
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(5));
			
			removeColumn(getColumnModel().getColumn(4));

		}
	}

}
