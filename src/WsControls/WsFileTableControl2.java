
package WsControls;


import java.awt.Font;

import WsEditTables.WsImportExcelEditTable2;
import WsMain.WsGuiTools;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsFileTableControl2 extends WsFileTableControl {

	private static final long serialVersionUID = 1L;


	public WsFileTableControl2(String[] columnNames, String fileChooserLabel, boolean enablePopupMenu) {
		super(columnNames, fileChooserLabel, enablePopupMenu);

	}
	
	
	protected  void init(String[] columnNames, String fileChooserLabel) {
		
		m_table = new  WsImportExcelEditTable2(columnNames, m_enablePopupMenu);
		
		m_title = fileChooserLabel;
		
		createGUI();
		
		m_pathButton.addActionListener(forwarder);

		setCustomFont();
	}
	
	public void setFirstColumnMinWidth(int w) {
		
		
		((WsImportExcelEditTable2) m_table).setFirstColumnMinWidth(w);
	}
	
	public void finishEditing() {
		
		 if (m_table.isEditing()) {
  		   
	        	m_table.getCellEditor().cancelCellEditing();
	        	
	       }
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}

}
