
package WsControls;


import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsInfoData;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsCompanyComboBox  extends JComboBox<String> {
	

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");

	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	public WsCompanyComboBox(boolean delayInit) {
	
		refreshModel();
	
	}
	
	public void refreshModel() {
		
		Vector<String> list = new Vector<String>();
		
		Vector<WsInfoData> vec = WsUtilSqlStatements.getInfoDataList();
		
		m_indices_map.clear();
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsInfoData d = vec.elementAt(i);
			
			list.add(d.name);
			
			m_indices_map.add(i, d.id);
			
		}
		
		m_model = new DefaultComboBoxModel<String>(list);
		
		this.setModel(m_model);
		
		if(!list.isEmpty()) {
			
			setSelectedIndex(0);
		
		}
		
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev != null && (ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED)) { 
		
			refreshModel();
		
		}
		
	}

	
	public int getCurrentSQLId() {
		
		if(m_indices_map.isEmpty()) { return -1; }
		
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
}
