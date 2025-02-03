
package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import WsDataStruct.WsContractPriceData;
import WsDatabase.WsContractsSqlStatements;
import WsDialogs.WsNewContractPriceDialog;
import WsForms.WsContractsForm;
import WsMain.WsUtils;

public class WsContractPricesEditTable extends JTable {

	
	private static final long serialVersionUID = 1L;
	
	WsContractPricesEditTableModel m_model = new WsContractPricesEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;
	   
	JMenuItem m_itemDeleteAll = null;

	double m_nds_coeff = 1.2;
	
	WsContractsForm m_parent = null;
	   
	public  WsContractPricesEditTable() {
		
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	      
	     TableColumn costColumn = getColumnModel().getColumn(3);
	     
	     costColumn.setCellEditor(new  WsCostContractPriceCellEditor(3));

	     costColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn ndsColumn = getColumnModel().getColumn(4);
	     
	     ndsColumn.setCellEditor(new WsCostContractPriceCellEditor(4));

	     ndsColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn ncostwndsColumn = getColumnModel().getColumn(5);
	     
	     ncostwndsColumn.setCellEditor(new  WsCostContractPriceCellEditor(5));

	     ncostwndsColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	    
	     setPopupMenu();
	    
	     getColumnModel().getColumn(0).setMaxWidth(100);
	     
	     getColumnModel().getColumn(0).setMinWidth(100);
	     
	     getColumnModel().getColumn(1).setMinWidth(350);
	     
	     getColumnModel().getColumn(2).setMaxWidth(70);
	     
	     getColumnModel().getColumn(2).setMinWidth(70);
	     
	     getColumnModel().getColumn(3).setMaxWidth(150);
	     
	     getColumnModel().getColumn(4).setMaxWidth(100);
	     
	     getColumnModel().getColumn(5).setMaxWidth(150);
	     
	     //getColumnModel().getColumn(3).setMinWidth(200);
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	}
	
	public void setEditable(boolean flag) {  m_model.setEditable(flag); }
	
	public void setParentForm( WsContractsForm f) {
		
		m_parent = f;
		
	}
	
	
	public void refreshData(int id_contract) {
		
			if (id_contract == -1) { return; }
        	
    	    m_model.deleteAllRows();
    	        
        	Vector<WsContractPriceData> vec =  WsContractsSqlStatements.getContractPriceList(id_contract, 0);
        	
        	for(int i = 0; i <vec.size(); ++i) {
        		
        		WsContractPriceData d = vec.elementAt(i);
        		
        		m_model.addRow(d);
        		
        	}

			m_model.fireTableDataChanged();		
			
	}
	
	public void setDataVector(Vector<WsContractPriceData> vec) {
		
	    m_model.deleteAllRows();
	        
    	for(int i = 0; i <vec.size(); ++i) {
    		
    		WsContractPriceData d = vec.elementAt(i);
    		
    		m_model.addRow(d);
    		
    	}

		
		m_model.fireTableDataChanged();		
	}
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, m_model.getIdColumnIndex() );
		}
		
		return selected_id;
	}
	
	public Vector<WsContractPriceData> getPrices() {
		
		return m_model.getModelVectorCopy();
			
	}
	

	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
			   
               if (menu == m_itemAdd) {
               	
            
            	   int id_contract  = m_parent.getSelectedId();
            	   
            	   if(id_contract != -1) {
   
	            	   WsNewContractPriceDialog dialog = new WsNewContractPriceDialog(WsUtils.get().getMainWindow(), 
	            			   id_contract, getGuiStrs("wsEditContractPriceDialogCaption"));
	            	   

	       				dialog.setVisible(true);
	       				
	       				refreshData(id_contract);
       				
            	   }
            	   else {
            		   
            		   
            	   }
                   
               } else if (menu == m_itemDelete) {
            	   
            	   int index = WsContractPricesEditTable.this.getSelectedRow();
            	   
            	   if (isEditing()) {
            		   
            	        getCellEditor().cancelCellEditing();
            	   }
            	   
            	   WsContractPriceData d = m_model.getValueAt(index);
            	   
            	   int res = WsUtils.showYesNoDialog(getMessagesStrs("deleteSinglePriceConf"));
            	   
            	   if(d != null && res == 1) {
            		   
            		   WsContractsSqlStatements.deleteContractPriceForId(d.id);
            		   
            		   refreshData(d.id_contract);
            	   }
    
               } else if (menu ==  m_itemDeleteAll) {
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	   Vector<WsContractPriceData> vec = m_model.getModelVectorCopy();
            	   
            	   int res = WsUtils.showYesNoDialog(getMessagesStrs("deleteAllPreicesConf"));
            	   
            	   if(res == 1 &&  vec != null && !vec.isEmpty()) {
            		   
            		   int id_contract = vec.elementAt(0).id_contract;
            	   
            		   WsContractsSqlStatements.deleteAllPricesForContract(id_contract);
            		   
            		   refreshData(id_contract);
            	   
            	   }
               }
		}

	};  
	
	private void setPopupMenu() {
		
		   m_popupMenu = new JPopupMenu();
		   
		   m_itemAdd = new JMenuItem(getGuiStrs("addTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsrowadd.png"));
		   
		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsrowdelete.png"));
		   
		   m_itemDeleteAll = new JMenuItem(getGuiStrs("deleteAllTableItemMenu"),
				   WsUtils.get().getIconFromResource("wsdeleteall.png"));
		   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemAdd.addActionListener( listener);
	       
	       m_itemDelete.addActionListener( listener);
	        
	       m_itemDeleteAll.addActionListener( listener);
	       
	       m_popupMenu.add(m_itemAdd);
	        
	       m_popupMenu.add(m_itemDelete);
	        
	       m_popupMenu.add(m_itemDeleteAll);
	      
	       setComponentPopupMenu(m_popupMenu);
		
	}
	
	public String isDataValid() {
		
		return m_model.isDataValid();
	}
	
	private void hideColumns( ) {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(8));
			
			removeColumn(getColumnModel().getColumn(7));
			
			removeColumn(getColumnModel().getColumn(6));
		
		}
		
	}
	
	public void setLastRowSelected() {
		
		int index = m_model.getRowCount() - 1;
		
		if(index > -1) {
			
			setRowSelectionInterval(index, index);
			
			Rectangle cellRect = getCellRect(index, 0, true);
			 
			scrollRectToVisible(cellRect);
			
		}
	}
	
	public void setNdsCoeff(double v) {
		
		  m_nds_coeff = v;
	}	
}