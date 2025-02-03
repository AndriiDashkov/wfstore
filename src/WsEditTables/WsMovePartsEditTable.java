/**
 * 
 */
package WsEditTables;

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import WsDataStruct.WsPartType;
import WsDataStruct.WsSkladMoveDataColumn;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsMovePartsEditTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	WsMovePartsEditTableModel m_model = new WsMovePartsEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	   
	JMenuItem m_itemDelete = null;
	   
	JMenuItem m_itemDeleteAll = null;
	
	HashMap<Integer, WsPartType> m_catalog = null;
	
	double m_nds_coeff = 1.2;
	   
	public  WsMovePartsEditTable() {
		
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	       
	     setPopupMenu();
	     
	     getColumnModel().getColumn(0).setMaxWidth(50);
	     
	     getColumnModel().getColumn(0).setMinWidth(50);

	     getColumnModel().getColumn(1).setMinWidth(150);
 
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     TableColumn diffColumn = getColumnModel().getColumn(9);

	     diffColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn zalColumn = getColumnModel().getColumn(3);

	     zalColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn imZalColumn = getColumnModel().getColumn(2);

	     imZalColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	}
	
	
	public void refresh() {
		
		m_model.fireTableDataChanged();	
	}
	
	
	public void setDataVector(Vector<WsSkladMoveDataColumn> vec) {
		
	    m_model.deleteAllRows();
	        
    	for(int i = 0; i <vec.size(); ++i) {
    		
    		WsSkladMoveDataColumn d = vec.elementAt(i);
    		
    		m_model.addRow(d);
    		
    	}

		m_model.fireTableDataChanged();		
	}
	
	public Vector<WsSkladMoveDataColumn> getData() {
		
		return m_model.getCurrentVectorData();
				
	}
	
	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
			   if (menu == m_itemDelete) {
            	   
				   int index = getSelectedRow();
				   
				   if(index == -1) { return; }
				  
            	   if (isEditing()) {
            		   
            	        getCellEditor().cancelCellEditing();
            	    }
            	   
            	   if(1 ==  WsUtils.showYesNoDialog(getMessagesStrs(" deleteTheRowMess")) ) {
            	  
            		   m_model.deleteRow(index);
            		
            	   }

               } else if (menu ==  m_itemDeleteAll) {
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	   if(1 ==  WsUtils.showYesNoDialog(getMessagesStrs("deleteAllRowsMess")) ) {
                 	  
            		   m_model.deleteAllRows();
            	   
            	   }
               }	
		}

	};  
	
	private void setPopupMenu() {
		
		
		   m_popupMenu = new JPopupMenu();

		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsrowdelete.png"));
		   
		   m_itemDeleteAll = new JMenuItem(getGuiStrs("deleteAllTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsdeleteall.png"));
		   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemDelete.addActionListener( listener);
	        
	       m_itemDeleteAll.addActionListener( listener);
	       
	       m_popupMenu.add(m_itemDelete);
	        
	       m_popupMenu.add(m_itemDeleteAll);

	       setComponentPopupMenu(m_popupMenu);
		
	}
	

	
	private void hideColumns( ) {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			//removeColumn(getColumnModel().getColumn(14));
		
		}
	}
}
	


