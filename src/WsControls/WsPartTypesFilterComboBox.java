
package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsPartType;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;


public class WsPartTypesFilterComboBox extends JComboBox<String> {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	public WsPartTypesFilterComboBox() {
				
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ) { 
		
			Vector<String> list = new Vector<String>();
			
			m_indices_map.clear();
			
			list.add(getGuiStrs("allComboItemName"));
			
			m_indices_map.add(0, -1);
			
			Vector<WsPartType> vec =  WsUtilSqlStatements.getPartTypesList();
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsPartType d = vec.elementAt(i);
				
				list.add(String.valueOf(d.kod) + " | " +d.name);

				
				m_indices_map.add(i + 1, d.id);
				
			}
			
			m_model = new DefaultComboBoxModel<String>(list);
			
			this.setModel(m_model);
		
		}
		
	}
	
	public int getCurrentSQLId() {
		
		int index = getSelectedIndex();
		
		if (index == -1) { return -1;}
		
		return m_indices_map.elementAt(index);
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
		
		WsPartType d = new WsPartType();
		
		d.id = getCurrentSQLId();
		
		if(d.id == -1) { return null; }
			
		WsPartType dt = WsUtilSqlStatements.getPartTypeForId(d.id);
				
		return dt;
	}
}
