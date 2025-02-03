/**
 * 
 */
package WsControls;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import WsDataStruct.WsAgentData;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

public class WsAgentComboBox extends JComboBox<String> {
	

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
		
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	private int row_index_in_table = -1;
	
	public WsAgentComboBox() {
		
		refreshModel(null);
			
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		refreshModel(ev, -1);
	}
	
	public void refreshModel(WsEventEnable ev, int id_type) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				 ev.getType() == WsEventEnable.TYPE.AGENTS_DATA_CHANGED) { 
		
			Vector<String> list = new Vector<String>();
			
			Vector<WsAgentData>  vec = WSAgentSqlStatements.getAgentsList(id_type);
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsAgentData d = vec.elementAt(i);
				
				list.add(d.name);
				
				m_indices_map.add(i, d.id);
			}
			
			m_model = new DefaultComboBoxModel<String>(list);
			
			this.setModel(m_model);
		
		}
		
	}
	
	public int getCurrentSQLId() {
		
		int selected_index = getSelectedIndex();
		
		if(selected_index == -1 || ( selected_index > (m_indices_map.size() - 1) ) ) {  return -1; }
		
		return m_indices_map.elementAt(selected_index);
	}
	
	public void setCurrentSQLId(int index) {
		
		int i = m_indices_map.indexOf(index);
		
		if ( i != -1) {
			
			setSelectedIndex(i);
		}
		
	}
	
	public void setLastItemSelected() {
		
		this.setLastItemSelected();
	}
	
	public WsAgentData getSelectedAgentData() {
		
		WsAgentData d = new WsAgentData();
		
		d.id =  getCurrentSQLId();
		
		if(d.id == -1) { return null; }
		
		d.name = (String) getSelectedItem();
		
		return d;
		
	}
	
	public int getRowIndex() {
		
		return row_index_in_table;
	}
	
	public void setRowIndex(int index ) { row_index_in_table = index;}
}

