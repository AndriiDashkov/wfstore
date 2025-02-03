
package WsTables;

import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import WsDataStruct.WsReturnedPartData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsReturnedRashodTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	WsReturnedRashodTableModel m_model = new WsReturnedRashodTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;

	public WsReturnedRashodTable() {
		     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	     
	     getColumnModel().getColumn(0).setMinWidth(50);
	     
	     getColumnModel().getColumn(0).setMaxWidth(50);
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );

	}
	

	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, 0);
		}
		
		return selected_id;
	}
	
	@SuppressWarnings("unused")
	private void setPopupMenu() {
		
		
	}
	
	public  Vector<WsReturnedPartData> getData() {
		
		return m_model.getCurrentVectorData();
		
	}
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {

		}
		
	}
	
	public void refresh() {
		
		m_model.refresh();
		
	}
	
	
	public void clear() {
		
		m_model.clear();
		
	}
	
}
