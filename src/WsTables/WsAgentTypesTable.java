
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import WsDataStruct.WsAgentTypeData;
import WsDatabase.WSAgentSqlStatements;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class  WsAgentTypesTable extends JTable {
	
	private static final long serialVersionUID = 1L;

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
	}
	
	
	public void refreshData() {
		
		 while (m_model.getRowCount() > 0) {
 	    	
 	        m_model.removeRow(0);
 	        
 	    }

		Vector<WsAgentTypeData> vec = WSAgentSqlStatements.getAgentsTypes();
		
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
}
