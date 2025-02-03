
package WsControls;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

import static WsMain.WsUtils.getGuiStrs;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsContractData;
import WsDatabase.WsContractsSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;


public class WsContractsFilterComboBox extends JComboBox<String> {
	
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshModel");
		
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	public WsContractsFilterComboBox() {
				
	}
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null || ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED ) { 
		
			Vector<String> list = new Vector<String>();
			
			m_indices_map.clear();
			
			list.add(getGuiStrs("allComboItemName"));
			
			m_indices_map.add(0, -1);
			
			Vector<WsContractData> vec =  WsContractsSqlStatements.getContractsList(0);
			
			for(int i = 0; i < vec.size(); ++i) {
				
				WsContractData d = vec.elementAt(i);
				
				list.add(String.valueOf(d.number) + " | " +  WsUtils.dateSqlToString(d.date, "dd-MM-yy"));

				
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
	
	public WsContractData getSelectedContract() {
		
		WsContractData d = new WsContractData();
		
		d.id = getCurrentSQLId();
		
		if(d.id == -1) { return null; }
			
		WsContractData dt = WsContractsSqlStatements.getContractForId(d.id);
				
		return dt;
	}
}