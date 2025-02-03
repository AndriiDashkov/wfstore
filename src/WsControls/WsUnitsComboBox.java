
package WsControls;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsUnitData;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

public class WsUnitsComboBox extends JComboBox<String> {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
		
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();

	public WsUnitsComboBox() {
		
		refreshModel(null);
			
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				 ev.getType() == WsEventEnable.TYPE.AGENTS_DATA_CHANGED) { 
		
			Vector<String> list = new Vector<String>();
			
			m_indices_map.clear();
			
			Vector<WsUnitData> vec = WsUtilSqlStatements.getUnitsList();
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsUnitData d = vec.elementAt(i);
				
				list.add(d.name);
				
				m_indices_map.add(i, d.id);
				
			}
			
			m_model = new DefaultComboBoxModel<String>(list);
				
			this.setModel(m_model);
		
		}
		
	}
	
	public int getCurrentSQLId() {
		
		return m_indices_map.elementAt(getSelectedIndex());
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
	
	public WsUnitData getSelectedUnitData() {
		
		WsUnitData d = new WsUnitData();
		
		d.id = getCurrentSQLId();
		
		d.name = (String) getSelectedItem();
		
		return d;
		
	}
}
