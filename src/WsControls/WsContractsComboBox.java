
package WsControls;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import WsDataStruct.WsContractData;
import WsDatabase.WsContractsSqlStatements;
import WsEvents.WsContractChangeEvent;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

public class WsContractsComboBox extends JComboBox<String> {
	

	{
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.NEW_CONTRACT_HAS_BEEN_CREATED, this, "refreshModel2");
		
			
	}
	
	private static final long serialVersionUID = 1L;

	private DefaultComboBoxModel<String> m_model = null;
	
	private Vector<Integer> m_indices_map = new Vector<Integer>();
	
	int m_row = -1;
	
	public WsContractsComboBox() {
		
		refreshModel(null);
			
	}
	
	public void setRow(int row) { m_row = row; }
	
	public int getRow() { return m_row; }
	
	public void refreshModel2(WsContractChangeEvent ev) {
		
		if ( ev == null || ev.getEventType() == WsEventDispatcher.NEW_CONTRACT_HAS_BEEN_CREATED ) { 
		
			refreshModel();
		
		}
		
	}
	
	
	public void refreshModel(WsEventEnable ev) {
		
		if (ev == null ||  ev.getType() == WsEventEnable.TYPE.DATABASE_LOADED) { 
		
			refreshModel();
		
		}
		
	}
	
	private void refreshModel() {
		
		Vector<String> list = new Vector<String>();
		
		m_indices_map.clear();

		Vector<WsContractData> vec =  WsContractsSqlStatements.getContractsList(0);
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsContractData d = vec.elementAt(i);
			
			list.add(String.valueOf(d.number) + " | " + WsUtils.dateSqlToString(d.date, "dd-MM-yy"));
			
			m_indices_map.add(i, d.id);
			
		}
		
		m_model = new DefaultComboBoxModel<String>(list);
			
		this.setModel(m_model);
		
		
		
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
	
	public 	WsContractData getSelectedPartData() {
		
		int id = getCurrentSQLId();
		
		WsContractData d =  WsContractsSqlStatements.getContractForId(id);
		
		return d;
	}
}