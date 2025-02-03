
package WsControls;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsSignsData;
import WsDatabase.WsSignSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSignsComboBox extends JComboBox<String> {
	

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");	
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	private int row_index_in_table = -1;
	
	public WsSignsComboBox() {
		
		refreshModel(null);
			
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				 ev.getType() == WsEventEnable.TYPE.SIGNS_DATA_CHANGED) { 
		
			Vector<String> list = new Vector<String>();
			
			Vector<WsSignsData>  vec = WsSignSqlStatements.getSignsList();
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsSignsData d = vec.elementAt(i);
				
				list.add(d.rank + " " + d.name + " " + d.position);
				
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
	
	public WsSignsData getSelectedSignData() {
		
		int id =  getCurrentSQLId();
		
		if(id  == - 1) { return null; }
		
		WsSignsData d = WsSignSqlStatements.getSignsForId(id);
				
		return d;
		
	}
	
	public int getRowIndex() {
		
		return row_index_in_table;
	}
	
	public void setRowIndex(int index ) { row_index_in_table = index;}
	
	public int listSize() { return  m_indices_map.size(); }
}









