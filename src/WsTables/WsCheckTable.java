
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Component;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import WsDataStruct.WsPrihodRashodMoveData;
import WsDatabase.WsSkladSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsCheckTable extends JTable {
	

	private static final long serialVersionUID = 1L;

	DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	
	TableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {

		private static final long serialVersionUID = 1L;
		
		SimpleDateFormat f = new SimpleDateFormat(WsUtils.DATE_FORMAT);

	    public Component getTableCellRendererComponent(JTable table,
	            Object value, boolean isSelected, boolean hasFocus,
	            int row, int column) {
	    	
	        if( value instanceof Date) {
	        	
	            value = f.format(value);
	        }
	        
	        return super.getTableCellRendererComponent(table, value, isSelected,
	                hasFocus, row, column);
	    }
	};
	

	String[] m_columnNames = {
	getGuiStrs("colNameInvoicePartId"),getGuiStrs("colNameKod"), getGuiStrs("colNameInPrihodNaklNumber"),
	getGuiStrs("colNameDateInPrihod"),getGuiStrs("colNameAuantityInPrihod"), getGuiStrs("colNameRest"),
	getGuiStrs("colNameQuantityOutRashod"), getGuiStrs("colNameDateOutRashod"), getGuiStrs("colNameOutRashodNaklNumber")};
	
	public WsCheckTable( ) {
		
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     getColumnModel().getColumn(0).setCellRenderer(tableCellRenderer);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	      getColumnModel().getColumn(0).setMaxWidth(60);
	      
	      getColumnModel().getColumn(0).setMinWidth(60);
	      
	      getColumnModel().getColumn(1).setMaxWidth(60);
	      
	      getColumnModel().getColumn(1).setMinWidth(60);
	      
	      getColumnModel().getColumn(2).setMaxWidth(150);
	      
	      getColumnModel().getColumn(2).setMinWidth(150);
	      
	      getColumnModel().getColumn(7).setMaxWidth(100);
	      
	      getColumnModel().getColumn(7).setMinWidth(100);
	      
	      getColumnModel().getColumn(3).setMaxWidth(100);
	      
	      getColumnModel().getColumn(3).setMinWidth(100);
	      
	      getColumnModel().getColumn(4).setMaxWidth(150);
	      
	      getColumnModel().getColumn(4).setMinWidth(150);
	      
	      getColumnModel().getColumn(5).setMaxWidth(150);
	      
	      getColumnModel().getColumn(5).setMinWidth(150);
	      
	      getColumnModel().getColumn(6).setMaxWidth(150);
	      
	      getColumnModel().getColumn(6).setMinWidth(150);
	      
	      getColumnModel().getColumn(8).setMaxWidth(150);
	      
	      getColumnModel().getColumn(8).setMinWidth(150);

	      getTableHeader().setReorderingAllowed( false );
	}
	
	
	public void refreshData(java.sql.Date startDate, java.sql.Date endDate, int kod_id) {
		 
			Vector<WsPrihodRashodMoveData> vec  = null;
			
			vec  = WsSkladSqlStatements.getSkladCheckMove(startDate, endDate,  kod_id);
			
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	    
    	    for(int i = 0; i < vec.size(); ++i) {
    	    	
    	    	WsPrihodRashodMoveData d = vec.elementAt(i);
    	    		
    	        m_model.addRow(new Object[]{ d.id_invoice_part, d.kod, d.in_number,
			    		 d.date_in, d.quantity_in,
			    		 d.rest, d.quantity_out, d.date_out, d.out_number,
			    		   });
    	    	
    	    }
		
	}
	
	@SuppressWarnings("unused")
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(12));
			
			removeColumn(getColumnModel().getColumn(11));
			
			removeColumn(getColumnModel().getColumn(10));
			
			removeColumn(getColumnModel().getColumn(9));

		}		
	}
}