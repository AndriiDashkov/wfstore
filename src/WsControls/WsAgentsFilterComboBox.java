

package WsControls;

import static WsMain.WsUtils.*;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsAgentData;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentsFilterComboBox extends JComboBox<String> {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
		
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	private int m_id_type = -1;
	
	public WsAgentsFilterComboBox() {
		
	
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				 ev.getType() == WsEventEnable.TYPE.AGENTS_DATA_CHANGED) { 
		
			Vector<String> list = new Vector<String>();
			
			list.add(getGuiStrs("allComboItemName"));
			
			m_indices_map.add(0, -1);
			
			Vector<WsAgentData>  vec = WSAgentSqlStatements.getAgentsList(m_id_type);
				
		    for(int i = 0; i < vec.size(); ++i) {
					
					WsAgentData d = vec.elementAt(i);
					
					list.add(d.name);
					
					m_indices_map.add(i + 1,d.id);
					
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
	
	public void setTypeFilterId(int id) {
		
		m_id_type = id;
	}

}
