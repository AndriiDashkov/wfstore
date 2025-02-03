

package WsEditTables;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsPrihodPartEditTable extends JTable {
	
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "insertPartDataForPartType");
		
	}
	
	private static final long serialVersionUID = 1L;
	
	WsPrihodPartEditTableModel m_model = new WsPrihodPartEditTableModel();
	
	JPopupMenu m_popupMenu = null;
	
	JMenuItem m_itemAdd = null;
	   
	JMenuItem m_itemDelete = null;
	   
	JMenuItem m_itemDeleteAll = null;
	
	HashMap<Integer, WsPartType> m_catalog = null;
	
	double m_nds_coeff = WsUtils.getNdsCoeff();
	   
	public  WsPrihodPartEditTable() {
		
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     setModel(m_model);
	    
	     TableColumn partTypesColumn = getColumnModel().getColumn(1);
		   
	     WsPartTypesCellEditor ed_part = new WsPartTypesCellEditor();
	     
	     partTypesColumn.setCellEditor(ed_part);
	     
	     partTypesColumn.setCellRenderer(new  WsPartTypesCellRenderer());
	     
	     TableColumn unitsColumn = getColumnModel().getColumn(5);
	     
	     unitsColumn.setCellEditor(new  WsUnitsCellEditor());
	     
	     unitsColumn.setCellRenderer(new  WsUnitsCellRenderer());
	
	     TableColumn quantityColumn = getColumnModel().getColumn(3);
	     
	     quantityColumn.setCellEditor(new  WsQuantityControlCellEditor());

	     quantityColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	  
	     TableColumn costColumn = getColumnModel().getColumn(6);
	     
	     costColumn.setCellEditor(new  WsCostPrihodControlCellEditor(6));

	     costColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn ndsColumn = getColumnModel().getColumn(7);
	     
	     ndsColumn.setCellEditor(new  WsCostPrihodControlCellEditor(7));

	     ndsColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	     
	     TableColumn costndsColumn = getColumnModel().getColumn(8);
	     
	     costndsColumn.setCellEditor(new  WsCostPrihodControlCellEditor(8));

	     costndsColumn.setCellRenderer(new WsQuantityControlCellRenderer());
	    
	     setPopupMenu();
	     
	     getColumnModel().getColumn(0).setMaxWidth(80);
	     
	     getColumnModel().getColumn(0).setMinWidth(80);
	     
	     getColumnModel().getColumn(1).setMaxWidth(120);
	     
	     getColumnModel().getColumn(1).setMinWidth(120);
	     
	     getColumnModel().getColumn(3).setMaxWidth(100);
	     
	     getColumnModel().getColumn(3).setMinWidth(100);
	     
	     getColumnModel().getColumn(4).setMaxWidth(100);

	     getColumnModel().getColumn(5).setMaxWidth(60);
	     
	     getColumnModel().getColumn(5).setMaxWidth(70);
	     
	     getColumnModel().getColumn(5).setMinWidth(70);
	     
	     getColumnModel().getColumn(6).setMinWidth(100);
	     
	     getColumnModel().getColumn(7).setMinWidth(100);
	     
	     getColumnModel().getColumn(8).setMinWidth(100);
	     
	     getColumnModel().getColumn(6).setMaxWidth(100);
	     
	     getColumnModel().getColumn(7).setMaxWidth(100);
	     
	     getColumnModel().getColumn(8).setMaxWidth(100);
	    
	    // getColumnModel().getColumn(9).setMaxWidth(30);
	    
	     //getColumnModel().getColumn(4).setMaxWidth(60);
	          
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	}
	
	
	public void refreshData(int id_prihod) {
		
			if (id_prihod == -1) { return; }
        	
    	    m_model.deleteAllRows();
    	         
        	Vector<WsPrihodPartData> vec = WsPrihodSqlStatements.getPrihodPartsList(id_prihod, 0);
        	
        	for(int i = 0; i <vec.size(); ++i) {
        		
        		WsPrihodPartData d = vec.elementAt(i);
        		
        		m_model.addRow(d);
        		
        	}

			m_model.fireTableDataChanged();		
			
	        WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
	        
		 	WsEventDispatcher.get().fireCustomEvent(ev);
	}
	
	
	public void setDataVector(Vector<WsPrihodPartData> vec) {
		

	    m_model.deleteAllRows();
	        
    	for(int i = 0; i <vec.size(); ++i) {
    		
    		WsPrihodPartData d = vec.elementAt(i);
    		
    		m_model.addRow(d);
    		
    	}

		m_model.fireTableDataChanged();	
		
		WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
        
	 	WsEventDispatcher.get().fireCustomEvent(ev);
}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, m_model.getIdColumnIndex());
		}
		
		return selected_id;
	}
	
	public Vector<WsPrihodPartData> getParts() {
		
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
               	
            	   WsPrihodPartData data = new WsPrihodPartData();
            	   
            	   if (isEditing()) {
           	        	getCellEditor().cancelCellEditing();
           	       }
            	   
            	   WsUnitData d = WsUtilSqlStatements.getKgUnit();
            	   
            	   data.id_units = d.id;
            	   
            	   data.units_name = d.name;
            	   
                   m_model.addRow(data);
                   
                   m_model.fireTableDataChanged();
                   
                   setLastRowSelected();
                   
       	        	WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
		        
       		 		WsEventDispatcher.get().fireCustomEvent(ev);
                   
                   
               } else if (menu == m_itemDelete) {
            	   
            	   int index = WsPrihodPartEditTable.this.getSelectedRow();
            	   
            	   if (isEditing()) {
            		   
            	        getCellEditor().cancelCellEditing();
            	    }
            	   
            	   WsPrihodPartData d = m_model.getValueAt(index);
            	   
            	   if(d != null &&  Math.abs(d.quantity - d.rest) < 0.0001) {
            		   
            		   m_model.deleteRow(index);
            		   
           	           WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
    		        
    		 		   WsEventDispatcher.get().fireCustomEvent(ev);
            		   
            	   }
            	   else {
            		   
            		   JOptionPane.showMessageDialog(
       	       			    WsUtils.get().getMainWindow(),
       	       			    getMessagesStrs("cantDeletePrihodRowRashod"),
       	       			    getMessagesStrs("messageInfoCaption"),
       	       			    JOptionPane.CLOSED_OPTION);
            	   }
                   
               } else if (menu ==  m_itemDeleteAll) {
            	   
            	   if (isEditing()) {
            		   
           	        	getCellEditor().cancelCellEditing();
            	   }
            	   
            	   if(m_model.canDeleteAll()) {
            	   
            		   m_model.deleteAllRows();
            		   
           	           WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
    		        
    		 		   WsEventDispatcher.get().fireCustomEvent(ev);
            	   }
            	   else {
            		   
            		   JOptionPane.showMessageDialog(
          	       			    WsUtils.get().getMainWindow(),
          	       			    getMessagesStrs("cantDeleteAllPrihodRowRashod"),
          	       			    getMessagesStrs("messageInfoCaption"),
          	       			    JOptionPane.CLOSED_OPTION);
            		   
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
			
			removeColumn(getColumnModel().getColumn(15));
			
			removeColumn(getColumnModel().getColumn(14));
			
			removeColumn(getColumnModel().getColumn(13));
			
			removeColumn(getColumnModel().getColumn(12));
			
			removeColumn(getColumnModel().getColumn(11));
			
			removeColumn(getColumnModel().getColumn(10));
	
		}
		
	}
	
	
	
	public void insertPartDataForPartType(WsEventEnable e) {
		
		if(e.getType() == WsEventEnable.TYPE.NEW_PART_TYPE_PRIHOD_SELECTED ) {
			
				int row = e.getId();
				
				WsPartType p = (WsPartType) e.getDataObject();
				
				WsPartType p_catalog = m_catalog.get(p.kod);
				
			    p.costwithnds = p_catalog.costwithnds;
			    
			    p.nds_coeff = m_nds_coeff;
				
				((WsPrihodPartEditTableModel)getModel()).insertKodAndName(p, row);
				
		        WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
		        
		 		WsEventDispatcher.get().fireCustomEvent(ev);
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
	
	public void setCatalog(HashMap<Integer, WsPartType> c) {
		
		  m_catalog  = c;
	}
	
	public void setNdsCoeff(double v) {
		
		  m_nds_coeff = v;
	}
	
	
	public WsNaklSums getSums() {
		
		return m_model.getSums();
		
	}
	
	public boolean isEmpty() {
		
		return m_model.isEmpty();
	}
	
}
