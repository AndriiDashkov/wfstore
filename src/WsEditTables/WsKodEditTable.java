
package WsEditTables;

import java.awt.Rectangle;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import WsDataStruct.WsPartType;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsKodEditTable extends JTable {
	
	{
	
		WsEventDispatcher.get().addConnect( WsEventDispatcher.ENABLE_EVENT, this, "refreshData2");

	
	}

	private static final long serialVersionUID = 1L;
	
	WsKodEditTableModel m_model = new WsKodEditTableModel();

	JPopupMenu m_popupMenu = null;

	JMenuItem m_itemComplete = null;

	JMenuItem m_itemReturn = null;

	public WsKodEditTable() {

		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		setFillsViewportHeight(true);

		setModel(m_model);
		
		TableColumn checkColumn = getColumnModel().getColumn(0);
	     
	    checkColumn.setCellRenderer( new WsCheckBoxCellRenderer());
	     
	    WsCheckBoxCellEditor ed = new WsCheckBoxCellEditor();
	     
	    ed.setColumnIndex(0);
		  
	    checkColumn.setCellEditor(ed);
	     
	    getColumnModel().getColumn(0).setMaxWidth(60);
	     
	    getColumnModel().getColumn(0).setMinWidth(60);
	     
	    getColumnModel().getColumn(1).setMaxWidth(100);
	     
	    getColumnModel().getColumn(1).setMinWidth(100);
	     
		hideColumns();
		
		getTableHeader().setReorderingAllowed( false );
		
		refreshDataBase();
	}

	public void clearTable() {
		
		Vector<WsPartType> vec_empty = new Vector<WsPartType>();
		

		m_model.setVector( vec_empty );


	}
	
	private void refreshDataBase() {
		

		Vector<WsPartType> vec = WsUtilSqlStatements.getPartTypesList();
				
		if(WsUtils.isKodEqual(vec.lastElement().kod , WsUtils.UNKNOWN_KOD)) {
			
			
			vec.remove(vec.size() - 1);
		}

		m_model.setVector(vec);
		
	}
	
	private void refreshDataJoin() {
		

		Vector<WsPartType> vec = WsUtilSqlStatements.getPartTypesList();
		
		if(WsUtils.isKodEqual(vec.lastElement().kod , WsUtils.UNKNOWN_KOD)) {
			
			vec.remove(vec.size() - 1);
		}
		

		Vector<WsPartType> vec_ = m_model.getCurrentVectorData();
		
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsPartType d = vec.elementAt(i);
			
			for(int j = 0; j < vec_.size(); ++j) {
				
				WsPartType d1 = vec_.elementAt(j);
				
				if(WsUtils.isKodEqual(d1.kod, d.kod) && d1.use ) {
					
					d.use = true;
					
					d.quantity = d1.quantity;
					
					break;
					
				}
				
			}	
		}
		
		m_model.setVector(vec);
		
		vec_.clear();
		
		vec_ = null;
		
	}

	public void refreshData() {
		
		refreshDataBase();
		
	}
	
	public void refreshData2(WsEventEnable ev) {
		

		if(ev.getType() == WsEventEnable.TYPE.NEW_PART_TYPE_CREATED) {
			
			refreshDataJoin();
			
		}
		
	}
	
	
	public void clearQuantityColumn() {
		
		Vector<WsPartType> vec = m_model.getCurrentVectorData();
		
		for(WsPartType f : vec) {
			
			f.quantity = 0.0;
		}
		
		m_model.fireTableDataChanged();
		
	}


	public int getSelectedId() {

		int selected_id = getSelectedRow();

		if (selected_id != -1) {

			selected_id = (int) m_model.getValueAt(selected_id, 4);
		}

		return selected_id;
	}

	 

	public String isDataValid() {

		return m_model.isDataValid();
	}
	
	
	private void hideColumns( ) {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(4));
						
		}
		
	}
	
	public Vector<WsPartType> getCurrentVectorData() {
		
		return m_model.getCurrentVectorData();
		
		
	}
	
	
	public Vector<WsPartType> getSelectedData() {
		
		Vector<WsPartType> v = new Vector<WsPartType>();
		
		Vector<WsPartType> vec =  m_model.getCurrentVectorData();
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsPartType d = vec.elementAt(i);
			
			if(d.use) { 
				
				v.add(new WsPartType(d));
			} 
			
		}
		
		return v;
		
		
	}
	
	public void findKod(int kod) {
		
		int index = m_model.findKod(kod);
		
		if(index != -1) {
			
			setRowSelectionInterval(index, index);
			
			Rectangle cellRect = getCellRect(index, 0, true);
			 
			scrollRectToVisible(cellRect);
		}
	}
}