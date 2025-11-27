package wstables;

import static wsmain.WsUtils.getGuiStrs;
import java.awt.Font;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import wsdatastruct.WsSkladMoveDataColumn;
import wsedittables.WsQuantityCellRenderer;
import wsmain.WsGuiTools;
import wsmain.WsUtils;

/**
 * The table for the list of an invoice. This is non editable table.
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsMovementExtDbTable extends JTable {
	
	 private static final long serialVersionUID = 1L;
	

	 DefaultTableModel m_model = new DefaultTableModel() {

	 private static final long serialVersionUID = 1L;

	 @Override
	 public boolean isCellEditable(int row, int column) {
	     
	       return false;
	   }
	 };
	
	String[] m_columnNames = { getGuiStrs("databaseListId"), 
			getGuiStrs("naPochatReportName"),
			getGuiStrs("pribuloReportName"), 
			getGuiStrs("quantityNameVibuloReportColumn"),
			getGuiStrs("skaldRestColumnName")};
	
	
	public WsMovementExtDbTable() {
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	   
	     getColumnModel().getColumn(0).setMinWidth(200);
	     
	     getColumnModel().getColumn(0).setMaxWidth(200);
	     
	     for(int i = 1; i < 5; ++i) {
	    	 
		     getColumnModel().getColumn(i).setMinWidth(150);
		     
		     getColumnModel().getColumn(i).setMaxWidth(150);
		     
		     getColumnModel().getColumn(i).setCellRenderer(new WsQuantityCellRenderer());
	    	 
	     }
	     
	     getTableHeader().setReorderingAllowed( false );
	          
	     setCustomFont();
	}
	

	public void refreshData(Vector<WsSkladMoveDataColumn> vec) {
		
	    while (m_model.getRowCount() > 0) {
	    	
	        m_model.removeRow(0);
	        
	    }
	       	    
	    for(int i = 0; i < vec.size(); ++i) {
	    	
	    	WsSkladMoveDataColumn d = vec.elementAt(i);
	
	        m_model.addRow(new Object[]{  
	        		 d.contract_name, 
	        		 WsUtils.getDF_fix(d.initial_rest, 4), 
	        		 WsUtils.getDF_fix(d.in_quantity,4),   
	        		 WsUtils.getDF_fix(d.out_quantity,4),
	        		 WsUtils.getDF_fix(d.rest,4)
		     });
	    }
	    
	    m_model.fireTableDataChanged();
    	  		
	}

	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 
		
	}
	
}