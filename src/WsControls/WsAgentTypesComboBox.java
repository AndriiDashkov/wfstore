/**
 * 
 */
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
public class WsAgentTypesComboBox extends JComboBox<String> {
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");

	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();

	public WsAgentTypesComboBox(boolean delayInit) {
		
		if( !delayInit ) {
			
			refreshModel();
			
		}
		
	}
	
	private void refreshModel() {
		
		Vector<String> list = new Vector<String>();
		
		Vector<WsAgentTypeData> vec = WSAgentSqlStatements.getAgentsTypes();
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsAgentTypeData d = vec.elementAt(i);
			
			list.add(d.name);
			
			m_indices_map.add(i, d.id);
			
		}
		
		m_model = new DefaultComboBoxModel<String>(list);
		
		this.setModel(m_model);
		
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev != null && (ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ||
				ev.getType() == WsEventEnable.TYPE.TYPE_AGENT_DATA_CHANGED)) { 
		
			refreshModel();
		
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
	
	public void addAllItem() {
		
		m_model.addElement(getGuiStrs("allComboItemName"));

	}
	
	public void setLastItemSelected() {
		
		this.setLastItemSelected();
	}
}
