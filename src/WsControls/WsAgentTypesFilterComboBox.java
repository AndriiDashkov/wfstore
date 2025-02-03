
package WsControls;

import static WsMain.WsUtils.*;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsAgentTypeData;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentTypesFilterComboBox extends JComboBox<String> {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();

	public WsAgentTypesFilterComboBox() {
		
			
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				ev.getType() == WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED) { 
		
			Vector<String> list = new Vector<String>();

			list.add(getGuiStrs("allComboItemName"));
			
			m_indices_map.add(0, -1);
			
			Vector<WsAgentTypeData> vec = WSAgentSqlStatements.getAgentsTypes();
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsAgentTypeData d = vec.elementAt(i);
				
				list.add(d.name);
				
				m_indices_map.add(i + 1, d.id);

			}
			
			m_model = new DefaultComboBoxModel<String>(list);
			
			this.setModel(m_model);
			
			setSelectedIndex(0);
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
}
