
package wsforms;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMenusStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wscontrols.WsMutableString;
import wsdatabase.WsContractsSqlStatements;
import wsdatabase.WsUtilSqlStatements;
import wsdatastruct.WsContractData;
import wsdatastruct.WsContractPriceData;
import wsdatastruct.WsPartType;
import wsdatastruct.WsUnitData;
import wsdialogs.WsExcelImportCatalogPricesDialog;
import wsdialogs.WsNewContractDialog;
import wsedittables.WsContractPricesEditTable;
import wsevents.WsContractChangeEvent;
import wsevents.WsContractPriceChangeEvent;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsmain.WsGuiTools;
import wsmain.WsUtils;
import wstables.WsContractsTable;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsContractsForm extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.CONTRACT_HAS_BEEN_CHANGED, this, "refreshDataAndSelect");
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.CONTRACT_PRICE_HAS_BEEN_CHANGED, this, "refreshPricesData");
		
	}
	
	protected  JButton m_loadFromCatalogButton = new JButton(getGuiStrs("loadCatalogItems"), 
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportCostExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected  JButton m_excelExportButton = new JButton(WsUtils.get().getIconFromResource("wsexportexcel.png"));
	
	protected WsContractsTable m_table = new WsContractsTable();
	
	protected WsContractPricesEditTable m_prices_table = new WsContractPricesEditTable();
	
	JSplitPane splitPane = null; 
	
	private int m_selected_order_row = -1;
	
	JMenuItem m_itemEdit = null;
	   
	JMenuItem m_itemDelete = null;
	   
    JMenuItem m_itemAdd = null;
    
    JPopupMenu m_popupMenu = null;
    
	protected static WsMutableString m_excel_save_folder =  new WsMutableString(".");
   
	public WsContractsForm() {
		
		createGUI();
			
		setPopupMenu();
		
		setListeners();
		
		setGuiEnabled(false);
		
		m_prices_table.setParentForm(this);
		
		setCustomFont();
	
	}
	
	private void setGuiEnabled(boolean flag) {
	
		m_table.setEnabled(flag);
		
		m_prices_table.setEnabled(flag);
			
	}
	
	private void setListeners() {
		
		m_importButton.addActionListener(new ActionListener() { 
			
			  public void actionPerformed(ActionEvent e) { 
				  
				  int id_contract =  getSelectedId();
				  
				  if(-1 == id_contract) {
					  	
					   WsUtils.showMessageDialog(getMessagesStrs("contactNoSelectionMessage"));
					   
					   return;
  
				  }
				  else {
					  
					  	WsExcelImportCatalogPricesDialog dialog = new   WsExcelImportCatalogPricesDialog(
						  WsUtils.get().getMainWindow(), getGuiStrs("excelImportKatalogDialogWinCaption"),
						  id_contract);
						
				  		dialog.setVisible(true);
				  }
			  } 
			  
		} );
		
		m_loadFromCatalogButton.addActionListener(new ActionListener() { 
			
			  public void actionPerformed(ActionEvent e) { 
				  
				  int id_contract =  getSelectedId();
				  
				  if(-1 == id_contract) {
					  	
					   WsUtils.showMessageDialog(getMessagesStrs("contactNoSelectionMessage"));
					   
					   return;

				  }
				  else {
					  
						int res = WsUtils.showYesNoDialog(getMessagesStrs("contactPriceLoadCatalogMessage"));
						
						if ( 1 == res) {
							
							setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
							Vector<WsContractPriceData> vec = getPricesFromCatalog();
							
							WsContractsSqlStatements.deleteAllPricesForContract(id_contract);
							
							for(WsContractPriceData d: vec) {
								
								WsContractsSqlStatements.createNewPrice(d);
							
							}
							
							m_prices_table.refreshData(id_contract);
							
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					  
						}
				  }
			  } 
			  
		} );
		
		
		 m_excelExportButton.addActionListener(new ActionListener() { 
			
			  public void actionPerformed(ActionEvent e) { 
				  
				  int id_contract =  getSelectedId();
				  
				  if(-1 == id_contract) {
					  	
					   WsUtils.showMessageDialog(getMessagesStrs("contactNoSelectionMessage"));
					   
					   return;

				  }
				  else {
					  
					  exportContractPriceToExcel();
				  }
			  } 
			  
		} );
		
		
		

	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel leftPanel = WsGuiTools.createVerticalPanel();
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JPanel topLeftPanel = WsGuiTools.createHorizontalPanel();
        
        topLeftPanel.add(new JLabel(getGuiStrs("ctTabCaption") + " :"));
        
        topLeftPanel.add(Box.createHorizontalGlue());
        
        leftPanel.add(topLeftPanel);
        
        leftPanel.add(scroll);
        
    	JPanel rightPanel = WsGuiTools.createVerticalPanel();
        
        JScrollPane scroll2 = new JScrollPane(m_prices_table);
        
        scroll2.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
       scroll2.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
       JPanel buttonPanel = WsGuiTools.createHorizontalPanel();
       
       JLabel lb = new JLabel(getGuiStrs("varPos"));
       
       buttonPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
       
       buttonPanel.add(lb);
       
       buttonPanel.add(Box.createHorizontalGlue());
       
       buttonPanel.add(m_loadFromCatalogButton);
       
       buttonPanel.add(m_importButton);
       
       WsGuiTools.fixComponentHeightToMin(  buttonPanel);
       
       buttonPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
       
       buttonPanel.add(m_excelExportButton);
       
       rightPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
       rightPanel.add(buttonPanel);
       
       rightPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
	
       rightPanel.add(scroll2);
       
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, rightPanel);
		
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setDividerLocation(400);

		JPanel splitPanel = WsGuiTools.createHorizontalPanel();

		splitPanel.add(splitPane);
		
		mainPanel.add(splitPanel);

		setLayout(new BorderLayout());
		
		m_table.setSelectionListener(new ListSelectionListener() {
			 
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int viewRow = m_table.getSelectedRow();
				
				if(m_selected_order_row != viewRow) {
					
					m_selected_order_row = viewRow;
					
					m_prices_table.refreshData(m_table.getSelectedId());
					

					
				}
				
			}
	     });
		
		add(mainPanel);
		
		setToolTips();
	}
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED) {
			
			  setGuiEnabled(true);
			
			  m_table.refreshData();

		}	
			
	}
	
	public void refreshDataAndSelect(WsContractChangeEvent e) {
		
		if(e != null && e.getEventType() == WsEventDispatcher.CONTRACT_HAS_BEEN_CHANGED) {
	
 			  m_table.refreshData( );
	
		}
	}

	public void refreshPricesData(WsContractPriceChangeEvent e) {
		
		if(e != null && e.getEventType() == WsEventDispatcher.CONTRACT_PRICE_HAS_BEEN_CHANGED) {
	
			m_prices_table.refreshData(m_table.getSelectedId());
	
		}
	}
	
	public int getSelectedId() {
		
		int id = m_table.getSelectedId();
		
		return id;
		
	}
	
	public Vector<Integer> getSelectedIds() {
		
		return m_table.getSelectedIds();
	}
	
	public WsContractData getDataForEdit() {
		
		return WsContractsSqlStatements.getContractForId(getSelectedId()) ;
			
	}
	
	private void setPopupMenu() {
		
		   m_popupMenu = new JPopupMenu();

		   m_itemEdit = new JMenuItem(getMenusStrs("editConItem"));
		   
		   m_itemDelete = new JMenuItem(getMenusStrs("delConItem"));
		   
		   m_itemAdd = new JMenuItem(getMenusStrs("newConItem"));
		   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemAdd.addActionListener( listener);
	       
	       m_itemDelete.addActionListener( listener);
	        
	       m_itemEdit.addActionListener( listener);
	           
	       m_popupMenu.add(m_itemAdd);
	        
	       m_popupMenu.add(m_itemEdit);
	        
	       m_popupMenu.add(m_itemDelete);

	       m_table.setComponentPopupMenu(m_popupMenu);
	        
	}
	
	
	private class CustomPopupListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			   JMenuItem menu = (JMenuItem) e.getSource();
			   
			   
        	   if (m_table.isEditing()) {
        		   
       	        	m_table.getCellEditor().cancelCellEditing();
       	        	
       	       }
			   
               if (menu == m_itemAdd) {
               	
            	   WsNewContractDialog dialog = new WsNewContractDialog(WsUtils.get().getMainWindow(), null, 
           				getGuiStrs("newConDialogCaption"));
            	             		
           		   dialog.setVisible(true);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   deleteCurrentContract();
            	   
            	   WsContractChangeEvent ev = new WsContractChangeEvent();

   				   WsEventDispatcher.get().fireCustomEvent(ev);
                   
               } else if (menu ==  m_itemEdit) {
            	   
            	   editCurrentContract();
               }
    
		}
	};
	
	
	private void deleteCurrentContract() {
		
		int id = getSelectedId();
		
		if (id  == -1) {
			
		   WsUtils.showMessageDialog(getMessagesStrs("contactNoSelectionMessage"));
		   
		   return;
		   
		}
		
		//the very first contract can't be deleted
		if (id  == 1) { 
			
			WsUtils.showMessageDialog(getMessagesStrs("contactTestdelMessage"));
			
			return; 
			
		}
		
		String operation = getMessagesStrs("messageDeleteContractApproveMessage");
			
		int res = WsUtils.showYesNoDialog(operation);
		
		if ( 1 == res) {
			
			int num = WsContractsSqlStatements.deleteContractForId(id);
			
			if(num > 0) {
				
				  refreshData(null);
				
		          WsUtils.showMessageDialog(getMessagesStrs("deleteContractSuccess"));
			}
			else {
				
				   WsUtils.showMessageDialog(getMessagesStrs("deleteContractFail"));
				
			}
		}
	}
	
	private void editCurrentContract() {
		
		WsContractData dt = getDataForEdit();
		
		if (dt != null && dt.id != -1)  {
		
			WsNewContractDialog dialog = new WsNewContractDialog(WsUtils.get().getMainWindow(), 
					dt, getGuiStrs("wsEditContractDialogCaption") );
			
			dialog.setVisible(true);
		}
		else {
			
			WsUtils.showMessageDialog(getMessagesStrs("editContractFailNoSelectionMessage"));
		}
	
	}
	
	
	private void setToolTips() {
		
		m_table.setToolTipText(getMessagesStrs("contractsTableToolTip"));
		
		m_prices_table.setToolTipText(getMessagesStrs("priceTableTooltip"));

		m_loadFromCatalogButton.setToolTipText(getMessagesStrs("priceLoadCatTooltip"));
		
		m_importButton.setToolTipText(getMessagesStrs("priceImportRasklTooltip"));
		
		m_excelExportButton.setToolTipText(getMessagesStrs("priceExportTooltip"));
	}
	

	
	private void exportContractPriceToExcel() {
		
		
		String file_to_save = WsUtils.get().showFileChooser("xlsx", this, m_excel_save_folder);
			
		if (null == file_to_save)  { return; }
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		WsContractData d = getDataForEdit();
		
		Vector<WsContractPriceData> parts_vec = m_prices_table.getPrices();
		
		OutputStream out;
		
		try {
			
			out = new FileOutputStream(file_to_save);
	
			XSSFWorkbook wb = new XSSFWorkbook();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    int rowCount = 0;
		    
		    XSSFRow row = sheet.createRow(rowCount);
		    
		    XSSFCell cell = row.createCell(0);
		    
		    cell.setCellValue(getGuiStrs("contractName") + " №"); 
		    
		    cell = row.createCell(1);
		    
		    cell.setCellValue(d.number); 
		    
		    cell = row.createCell(2);
		    
		    cell.setCellValue(getGuiStrs("vidNaklName2"));
		    
		    cell = row.createCell(3);
		    
		    cell.setCellValue(WsUtils.dateSqlToString(d.date, "dd-MM-yyyy"));
		    
		    row = sheet.createRow(++rowCount);
		    
		    cell = row.createCell(0);
		    
		    cell.setCellValue("№"); 
		    
		    cell = row.createCell(1);
		    
		    cell.setCellValue(getGuiStrs("skladColumnVenCode2KodName")); 
		    
		    cell = row.createCell(2);
		    
		    cell.setCellValue(getGuiStrs("nameColumnReportGoodName")); 
		    
		    cell = row.createCell(3);
		    
		    cell.setCellValue(getGuiStrs("unitsNameColumName"));
		    
		    cell = row.createCell(4);
		    
		    cell.setCellValue(getGuiStrs("costNameInReportNoNDS"));
		    
		    cell = row.createCell(5);
		    
		    cell.setCellValue(getGuiStrs("prihodPartsColumnNdsName"));
		    
		    cell = row.createCell(6);
		    
		    cell.setCellValue(getGuiStrs("prWithPdv"));
		    		
			for(int i = 0; i <  parts_vec.size(); ++i) {
				
				WsContractPriceData dt =  parts_vec.elementAt(i);
	
	            row = sheet.createRow(++rowCount);
	                     	
            	cell = row.createCell(0);
            	
                cell.setCellValue(i + 1); 
                
                cell = row.createCell(1);
            	
                cell.setCellValue(dt.kod);
                
            	cell = row.createCell(2);
            	
                cell.setCellValue(dt.name);
                
                cell = row.createCell(3);
            	
                cell.setCellValue(dt.units_name);
                
                cell = row.createCell(4);
            	
                cell.setCellValue(dt.cost);
                
                cell = row.createCell(5);
            	
                cell.setCellValue(dt.nds);
                
                cell = row.createCell(6);
            	
                cell.setCellValue(dt.costwnds);
                
                cell = row.createCell(7);
            	 
	         }
	    	
			sheet.autoSizeColumn(1);

			wb.write(out);

			out.close();
	    
			wb.close(); 
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportSuccessMessage"));
    
		} catch (IOException  e) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			e.printStackTrace();

			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportFailedMessage"));
		}
		
		
	}
	
	private Vector<WsContractPriceData> getPricesFromCatalog() {
		
		Vector<WsContractPriceData> vec = new Vector<WsContractPriceData>();
		
		Vector<WsPartType> parts_vec = WsUtilSqlStatements.getPartTypesList();
		
		int id_contract =  getSelectedId();
		
		if(id_contract == -1) { return vec; }
		
		WsUnitData ud_sht = WsUtilSqlStatements.getShtUnit();
		
		WsUnitData ud_kg = WsUtilSqlStatements.getKgUnit();
		
		for(WsPartType p: parts_vec) {
			
			if(p.kod == WsUtils.UNKNOWN_KOD) { continue; }
			
			WsContractPriceData d = new WsContractPriceData();

			d.kod = p.kod;
			
			d.id_contract = id_contract;
			
			d.id_part_type = p.id;
			
			d.name = p.name;
			
			if(p.kod == WsUtils.EGG_KOD_1 || p.kod == WsUtils.EGG_KOD_2 ) {
				
				d.id_units =  ud_sht.id;
				
				d.units_name =  ud_sht.name;
			}
			else {
				
				d.id_units =  ud_kg.id;
				
				d.units_name =  ud_kg.name;
			}
			
			vec.add(d);
			
		}
		
		return vec;
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 
		
		int margin_table = WsUtils.get().getTableMarginShiftForFont();
		
		m_table.setRowHeight(m_table.getRowHeight() + margin_table);
		
		WsGuiTools.changeFont(m_table, f);
		
		WsGuiTools.changeFont(m_popupMenu, f);

		m_table.getTableHeader().setFont(f);
		
		
	}
}
