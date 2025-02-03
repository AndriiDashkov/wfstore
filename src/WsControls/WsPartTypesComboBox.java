
package WsControls;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsPartType;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

public class WsPartTypesComboBox extends JComboBox<String> {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
		
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	int m_row = -1;
	
	public WsPartTypesComboBox() {
		
		refreshModel(null);
			
	}
	
	public void setRow(int row) { m_row = row; }
	
	public int getRow() { return m_row; }
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				 ev.getType() == WsEventEnable.TYPE.AGENTS_DATA_CHANGED) { 
		
			Vector<String> list = new Vector<String>();
			
			m_indices_map.clear();

			Vector<WsPartType> vec =  WsUtilSqlStatements.getPartTypesList();
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsPartType d = vec.elementAt(i);
				
				list.add(String.valueOf(d.kod) + " | " +d.name);
				
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
	
	public WsPartType getSelectedPartData() {
		
		int id = getCurrentSQLId();
		
		WsPartType d = WsUtilSqlStatements.getPartTypeForId(id);
		
		return d;
	}
}
