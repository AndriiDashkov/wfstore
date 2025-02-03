
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPrihodPartData;
import WsDatabase.WsPrihodSqlStatements;
import WsEditTables.WsQuantityCellRenderer;
import WsMain.WsUtils;



/**
 * The table for the list of an invoice. This is non editable table.
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPrihodPartsTable extends JTable {
	
	 private static final long serialVersionUID = 1L;
	
	 ListSelectionListener m_selection_listener = null;
	
    JMenuItem m_itemKodSort = null;
   
    JMenuItem m_itemNameSort = null;
   
    JMenuItem m_itemQuantitySort = null;
   
    int  m_id_invoice = -1;
   
    double m_sumcost = 0.0;
   
    double m_sumnds = 0.0;
   
    boolean m_sumKodesFlag = false;

    int  m_sort_type = 0;

	DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
	String[] m_columnNames = { getGuiStrs("skladColumnVenCode2KodName"), 
			getGuiStrs("prihodPartsColumnPartName"),
			getGuiStrs("prihodPartsColumnQuantityName"), 
			getGuiStrs("prihodPartsColumnRestName"),
			getGuiStrs("prihodPartsColumnUnitsNameName"), 
			getGuiStrs("prihodPartsColumnCostName"), 
			getGuiStrs("prihodPartsColumnNdsName"),
			getGuiStrs("varWithNdsLabel"),
			getGuiStrs("prihodPartsColumnInfoName"),
			"id_invoice","id_part_type", "id_units","id", 
			getGuiStrs("prihodPartsColumnPartTypeName"),};
	
	
	public WsPrihodPartsTable() {
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	   
	     getColumnModel().getColumn(0).setMinWidth(100);
	     
	     getColumnModel().getColumn(0).setMaxWidth(100);
	     
	     getColumnModel().getColumn(1).setMinWidth(300);
	     
	     getColumnModel().getColumn(2).setMaxWidth(140);
	     
	     getColumnModel().getColumn(2).setMinWidth(140);
	     
	     getColumnModel().getColumn(3).setMaxWidth(140);

	     getColumnModel().getColumn(3).setMinWidth(140);
	     
	     int prwidth = 140;
	     
	     getColumnModel().getColumn(5).setMaxWidth(prwidth);

	     getColumnModel().getColumn(5).setMinWidth(prwidth);
	     
	     getColumnModel().getColumn(6).setMaxWidth(prwidth);

	     getColumnModel().getColumn(6).setMinWidth(prwidth);
	     
	     getColumnModel().getColumn(7).setMaxWidth(prwidth);

	     getColumnModel().getColumn(7).setMinWidth(prwidth);
	     
	     getColumnModel().getColumn(4).setMaxWidth(100);
	     
	     getColumnModel().getColumn(10).setMaxWidth(30);
	     
	     getColumnModel().getColumn(11).setMaxWidth(30);
	     
	     getColumnModel().getColumn(12).setMaxWidth(30);
	         
	     hideColumns( );
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     TableColumn quantityColumn = getColumnModel().getColumn(2);

	     quantityColumn.setCellRenderer(new WsQuantityCellRenderer());
	     
	     TableColumn restColumn = getColumnModel().getColumn(3);

	     restColumn.setCellRenderer(new WsQuantityCellRenderer());
	     
	     quantityColumn = getColumnModel().getColumn(3);

	     quantityColumn.setCellRenderer(new WsQuantityCellRenderer());
	     
	     setPopupMenu();
	}
	
	public void setSelectionListener(ListSelectionListener l) {
		
		m_selection_listener = l;
		
		ListSelectionModel cellSelectionModel = getSelectionModel();
		
	    cellSelectionModel.addListSelectionListener(m_selection_listener);
	}
	
	public void setSumKodFlag(boolean f) {
		
		
		m_sumKodesFlag = f;
	}
	
	
	public void refreshData(int id_invoice) {
		
		refreshData(id_invoice, m_sort_type);
	}
	
	/**
	 * Refreshes the data in the table.
	 * @author Andrii Dashkov license GNU GPL v3
	 *
	 */
	public void refreshData(int id_invoice, int sort_type) {
		
			m_sort_type = sort_type;
		
        	Vector<WsPrihodPartData> vec = null;
        			
        	Vector<WsPrihodPartData> vec1 =	WsPrihodSqlStatements.getPrihodPartsList(id_invoice, sort_type);
        	
        	if (m_sumKodesFlag) {
        		
        		//to merge the same kods positions in the invoice
        		vec =  mergeSameCodesSortStable(vec1); 
        	}
        	else { vec= vec1; }
        	
    	    this.getSelectionModel().removeListSelectionListener(m_selection_listener);
    	    
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	       	    
    	    for(int i = 0; i < vec.size(); ++i) {
    	    	
    	    	WsPrihodPartData d = vec.elementAt(i);
    	    	
    	    	double rest = d.rest < WsUtils.getRZL() ? 0.0 : d.rest;
    	    	
    	        m_model.addRow(new Object[]{  
    	        		 d.vendorcode2, 
    	        		 d.name, 
    	        		 WsUtils.getDF_fix(d.quantity,4),   
    	        		 WsUtils.getDF_fix(rest,4),
			    		 d.units_name, 
			    		 WsUtils.getDF_fix(d.cost,3),
			    		 WsUtils.getDF_fix(d.nds, 3),
			    		 WsUtils.getDF_fix(d.costwithnds,3),
			    		 d.info,       
			    		 d.id_invoice, 
			    		 d.id_part_type,
			    		 d.id_units,  
			    		 d.id,
			    		 d.part_type_name
			     });
    	    }
    	    
    	    //get sums
    	    m_sumcost = 0.0;
    	    
    	    m_sumnds = 0.0;
    	    
    	    for(int i = 0; i < vec1.size(); ++i) {
    	    	
    	    	WsPrihodPartData d = vec1.elementAt(i);
    	    	  	        
    	        m_sumcost += d.quantity*d.cost;
        	    
        	    m_sumnds += d.quantity*d.nds;
    	    	
    	    }
    	    
			if(m_selection_listener != null) {
				
				this.getSelectionModel().addListSelectionListener(m_selection_listener);
			}
			
			m_model.fireTableDataChanged();
			
			m_id_invoice = id_invoice;
			
	}
	
	public WsNaklSums getSums() {
		
		WsNaklSums s = new WsNaklSums();
		
		s.sum = m_sumcost;
		
		s.sumnds = m_sumnds;
		
		s.sumwithnds = m_sumnds + m_sumcost;
		
		return s;
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, 12);
		}
		
		return selected_id;
	}
	
	
	public WsPrihodPartData getSelectedDataPrihodPart() {
		
		int selected_id = getSelectedRow();
		
		WsPrihodPartData dt = new WsPrihodPartData();
		
		if (selected_id != - 1) {		
			
			dt.vendorcode2 = (String) m_model.getValueAt(selected_id, 0);
			
			dt.name = (String) m_model.getValueAt(selected_id, 1);
			
			dt.quantity = (double) m_model.getValueAt(selected_id, 2);
			
			dt.rest = (double) m_model.getValueAt(selected_id, 3);
			
			dt.units_name = (String) m_model.getValueAt(selected_id, 4);
			
			dt.cost = (double) m_model.getValueAt(selected_id, 5);
			
			dt.nds = (double) m_model.getValueAt(selected_id, 6);
			
			dt.costwithnds = (double) m_model.getValueAt(selected_id, 7);
			
			dt.info = (String) m_model.getValueAt(selected_id, 8);
			
			dt.id_invoice = (int) m_model.getValueAt(selected_id, 9);
			
			dt.id_part_type = (int) m_model.getValueAt(selected_id, 10);
			
			dt.id_units = (int) m_model.getValueAt(selected_id, 11);
			
			dt.id = (int) m_model.getValueAt(selected_id, 12);
			
			dt.part_type_name = (String) m_model.getValueAt(selected_id, 13);
			
		}
		
		return dt;
	}
	
	private void hideColumns( ) {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
			removeColumn(getColumnModel().getColumn(13));
			
			removeColumn(getColumnModel().getColumn(12));
			
			removeColumn(getColumnModel().getColumn(11));
			
			removeColumn(getColumnModel().getColumn(10));
			
			removeColumn(getColumnModel().getColumn(9));
			
		}
		
	}
	
	
	private void setPopupMenu() {
		
		
	   JPopupMenu m_popupMenu = new JPopupMenu();

	   m_itemKodSort = new JMenuItem(getGuiStrs("sortKodNameMenu"), 
			   WsUtils.get().getIconFromResource("wssortkod.png"));
	   
	   m_itemNameSort = new JMenuItem(getGuiStrs("sortNameNameMenu"), 
			   WsUtils.get().getIconFromResource("wssortname.png"));
	   
	   m_itemQuantitySort = new JMenuItem(getGuiStrs("sortQuantityNameMenu"));
	   
	   CustomPopupListener listener = new CustomPopupListener();
	   
	   m_itemKodSort.addActionListener( listener);
       
	   m_itemNameSort.addActionListener( listener);
        
	   m_itemQuantitySort.addActionListener( listener);
       
       m_popupMenu.add(m_itemKodSort);
        
       m_popupMenu.add(m_itemNameSort);
        
       m_popupMenu.add(m_itemQuantitySort);
      
       setComponentPopupMenu(m_popupMenu);
        
	}
	
	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
               if (menu == m_itemKodSort) {
               	
            	   refreshData(m_id_invoice, 0);
                   
               } else if (menu == m_itemNameSort) {
            	   
            	   refreshData(m_id_invoice, 1);
                   
               } else if (menu ==  m_itemQuantitySort) {
            	   
            	   refreshData(m_id_invoice, 2);
               }
			
		}

	};
	/**
	 * The function merges rows with the same kod. 
	 * The algorithm of merging is not effective, but stable - it preserves
	 * the sorting order of the input vector.
	 * @author Andrii Dashkov license GNU GPL v3
	 *
	 */
	private Vector<WsPrihodPartData> mergeSameCodesSortStable(Vector<WsPrihodPartData> vec) {
		
		Vector<WsPrihodPartData> v_r = new Vector<WsPrihodPartData>();
		
		for(int i = 0; i < vec.size(); ++i) {
					
			WsPrihodPartData d1 = vec.elementAt(i);
			
			int kod = d1.kod;
			
			boolean flag = false;
			
			//we need to keep the sort order, so the linear find
			for(int j = 0; j < v_r.size(); ++j) {
				
				WsPrihodPartData d2 = v_r.elementAt(j);
				
				if(	WsUtils.isKodEqual(d2.kod, kod)) {
					
					d2.quantity +=  d1.quantity;
					
					flag = true;
					
					break;
				}
				
			}
			
			if(!flag) {
				
				v_r.add(d1);
				
			}
					
		}
		
		return v_r;
	}
	
}
