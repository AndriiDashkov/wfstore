/**
 * 
 */
package WsEditTables;

import javax.swing.JTable;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsImportExcelEditTable2 extends WsImportExcelEditTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * @param columnNames
	 */
	public WsImportExcelEditTable2(String[] columnNames, boolean enablePopupMenu ) {
		super(columnNames, enablePopupMenu);

	}
	
	
	protected void init(String[] columnNames, boolean enablePopupMenu) {
		
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		setFillsViewportHeight(true);
		
		m_model = new  WsImportExcelEditTableModel2(columnNames);

		setModel(m_model);
	     
		hideColumns();
		
		getTableHeader().setReorderingAllowed( false );
		
		if(enablePopupMenu) {
			
			setPopupMenu();
		
		}
		
		
	}
	
	public void setFirstColumnMinWidth(int w) {
		
		
		getColumnModel().getColumn(0).setMinWidth(w);
	}

}
