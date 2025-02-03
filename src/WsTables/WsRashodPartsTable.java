
package WsTables;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsRashodSqlStatements;
import WsEditTables.WsQuantityCellRenderer;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsRashodPartsTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
   JMenuItem m_itemKodSort = null;
   
   JMenuItem m_itemNameSort = null;
   
   JMenuItem m_itemQuantitySort = null;

   int m_id_invoice = -1;
   
   int sql_id_column_index = 11;
   
   double m_sumcost = 0.0;
   
   double m_sumnds = 0.0;
   
   boolean 	m_sumKodesFlag = false;
   
   int m_sort_type = 0;

   DefaultTableModel m_model = new DefaultTableModel() {

		private static final long serialVersionUID = 1L;

		@Override
		   public boolean isCellEditable(int row, int column) {
		     
		       return false;
		   }
	};
	
    
	String[] m_columnNames = { 
			getGuiStrs("skladColumnVenCode2KodName"),
			getGuiStrs("rashodPartsColumnPartName"),
			getGuiStrs("prihodPartsColumnQuantityName"), 
			getGuiStrs("rashodPartsColumnReqQuantityName"),
			getGuiStrs("rashodPartsColumnUnitsNameName"), 
			getGuiStrs("costNameInReportNoNDS"),
			getGuiStrs("prihodPartsColumnNdsName"), 
			getGuiStrs("prWithPdv"),
			getGuiStrs("rashodPartsColumnInfoName"),
			"id_sale_invoice", "id_units", "id_invoice_parts","id"};
	
	
	public WsRashodPartsTable() {
		
		
	     m_model.setColumnIdentifiers(m_columnNames);
	     
	     this.setModel(m_model);
	     
	     setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	     
	     setFillsViewportHeight(true);
	     
	     getColumnModel().getColumn(0).setMaxWidth(100);
	     
	     getColumnModel().getColumn(0).setMinWidth(100);
	     
	     getColumnModel().getColumn(1).setMinWidth(150);
	     
	     getColumnModel().getColumn(2).setMinWidth(150);
	     
	     getColumnModel().getColumn(2).setMaxWidth(200);
	     
	     getColumnModel().getColumn(3).setMinWidth(150);
	     
	     getColumnModel().getColumn(3).setMaxWidth(200);
	     
	     getColumnModel().getColumn(4).setMinWidth(80);
	     
	     getColumnModel().getColumn(4).setMaxWidth(80);
	     
	     getColumnModel().getColumn(5).setMaxWidth(100);
	     
	     getColumnModel().getColumn(5).setMinWidth(100);
	     
	     getColumnModel().getColumn(6).setMaxWidth(80);
	     
	     getColumnModel().getColumn(6).setMinWidth(80);
	     
	     getColumnModel().getColumn(7).setMaxWidth(100);
	     
	     getColumnModel().getColumn(7).setMinWidth(100);
	     
	     TableColumn quantityColumn = getColumnModel().getColumn(2);

	     quantityColumn.setCellRenderer(new WsQuantityCellRenderer());
	     
	     quantityColumn = getColumnModel().getColumn(3);

	     quantityColumn.setCellRenderer(new WsQuantityCellRenderer());
	     
	     hideColumns();
	     
	     getTableHeader().setReorderingAllowed( false );
	     
	     setPopupMenu();
	}
	
	
	public void refreshData(int id_invoice_sales) {
		
		refreshData(id_invoice_sales, m_sort_type);
		
	}
	
	public void refreshData(int id_invoice_sales, int sort_type) {
		
			m_sort_type = sort_type;
 
        	Vector<WsRashodPartData> vec = null;
        			
        	Vector<WsRashodPartData>  vec1 = WsRashodSqlStatements.getRashodPartsList(id_invoice_sales, sort_type);
        	
        	if (m_sumKodesFlag) {
        		
        		vec =  mergeSameCodesSortStable(vec1); 
        	}
        	else { 
        		
        		vec= vec1; 
        	}
        	
    	    
    	    while (m_model.getRowCount() > 0) {
    	    	
    	        m_model.removeRow(0);
    	        
    	    }
    	    
    	    for(int i =0; i < vec.size(); ++i) {
    	    	
    	    	WsRashodPartData d = vec.elementAt(i);
    	    	
    	    	m_model.addRow(new Object[]{ 
    	    			 d.vendor_code_2,
    	    			 d.name,
    	    			 WsUtils.getDF_fix(d.quantity,4),
    	    			 WsUtils.getDF_fix(d.req_quantity,4),
  			    		 d.units_name, 
  			    		 WsUtils.getDF_fix(d.cost, 3),
  			    		 WsUtils.getDF_fix(d.nds,3),
  			    		 WsUtils.getDF_fix(d.costwithnds,3),
  			    		 d.info, 
  			    		 d.id_sale_invoice, d.id_units,
  			    		 d.id_invoice_parts, d.id});
    	    	  	    	
    	    }
    	    
    	    //the sums are calculated for all not merged positions (different price can be for the same kods)
    	    m_sumcost = 0.0;
    	    
    	    m_sumnds = 0.0;
    	    
    	    for(int i = 0; i < vec1.size(); ++i) {
    	    	
    	    	WsRashodPartData d = vec1.elementAt(i);
    	    	
    	    	m_sumcost += d.quantity*d.cost;
        	    
        	    m_sumnds += d.quantity*d.nds;
    	    	
    	    	
    	    }
    	    
			m_model.fireTableDataChanged();
			
			m_id_invoice = id_invoice_sales;
	}
	
	
	public int getSelectedId() {
		
		int selected_id = getSelectedRow();
		
		if (selected_id != - 1) {
		
			selected_id  = (int) m_model.getValueAt(selected_id, sql_id_column_index );
		}
		
		return selected_id;
	}
	
	public Vector<Integer> getSelectedIds() {
		
		Vector<Integer> vec = new Vector<Integer>();
		
		int[] selected_ids = getSelectedRows();
		
		for(int id : selected_ids) {
			
			if (id != - 1) {
			
				vec.add( (int) m_model.getValueAt(id, sql_id_column_index ));
			}
			
		}
		
		return vec;
	}
	

	
	public WsRashodPartData getSelectedDataPrihodPart() {
		
		int selected_id = getSelectedRow();
		
		WsRashodPartData dt = new WsRashodPartData();
		
		if (selected_id != - 1) {		
			
			dt.vendor_code_2 = (String) m_model.getValueAt(selected_id, 0);
			
			dt.name = (String) m_model.getValueAt(selected_id, 1);
			
			dt.quantity = (double) m_model.getValueAt(selected_id, 2);
			
			dt.req_quantity = (double) m_model.getValueAt(selected_id, 3);
			
			dt.units_name = (String) m_model.getValueAt(selected_id, 4);
			
			dt.cost = (double) m_model.getValueAt(selected_id, 5); 
			
			dt.nds = (double) m_model.getValueAt(selected_id, 6);
			
			dt.costwithnds = (double) m_model.getValueAt(selected_id, 7);
			
			dt.info = (String) m_model.getValueAt(selected_id, 8);
			
			dt.id_sale_invoice = (int) m_model.getValueAt(selected_id, 9);
			
			dt.id_units = (int) m_model.getValueAt(selected_id, 10);
			
			dt.id_invoice_parts= (int) m_model.getValueAt(selected_id, 11);
			
			dt.id = (int) m_model.getValueAt(selected_id, 12);

			
		}
		
		return dt;
	}
	
	
	private void hideColumns() {
		
		if(WsUtils.HIDE_ID_COLUMNS) {
			
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
	
	public WsNaklSums getSums() {
		
		WsNaklSums s = new WsNaklSums();
		
		s.sum = m_sumcost;
		
		s.sumnds = m_sumnds;
		
		s.sumwithnds = m_sumnds + m_sumcost;
		
		return s;
	}
	
	
	public void setSumKodFlag(boolean f) {
		
		
		m_sumKodesFlag = f;
	}
	
	
	private Vector<WsRashodPartData> mergeSameCodesSortStable(Vector<WsRashodPartData> vec) {
		
		Vector<WsRashodPartData> v_r = new Vector<WsRashodPartData>();
		
		for(int i = 0; i < vec.size(); ++i) {
					
			WsRashodPartData d1 = vec.elementAt(i);
			
			int kod = d1.kod;
			
			boolean flag = false;
			
			//we need to keep the sort order, so the linear find
			for(int j = 0; j < v_r.size(); ++j) {
				
				WsRashodPartData d2 = v_r.elementAt(j);
				
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