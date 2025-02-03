
package WsForms;

import static WsMain.WsUtils.HOR_STRUT;
import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsActions.WFNewExcelPrihodImportDialogAction;
import WsActions.WsClearFilterPrihodAction;
import WsActions.WsPrihodFilterShowAction;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentsFilterComboBox;
import WsControls.WsContractsFilterComboBox;
import WsControls.WsMutableString;
import WsControls.WsPartTypesFilterComboBox;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsDatabase.WSAgentSqlStatements;
import WsDatabase.WsPrihodSqlStatements;
import WsDialogs.WFExcelImportPrihodDialog;
import WsDialogs.WsContractGroupChangeDialog;
import WsDialogs.WsFindDialog;
import WsDialogs.WsNewPrihodDialog;
import WsDialogs.WsPrihodInRashodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsTables.WsPrihodPartsTable;
import WsTables.WsPrihodTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPrihodForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED, this, "refreshDataAndSelect");
		
	}
	
	Ws2DatesControl m_dates = new Ws2DatesControl(false);
	
	protected  JButton m_showButton = new JButton(WsUtils.get().getIconFromResource("wsfilter.png"));
	
	protected  JButton m_clearFilterButton = new JButton(getGuiStrs("buttonClearFilterCaption"));
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"));
	
	protected WsPrihodTable m_table = new WsPrihodTable();
	
	protected WsPrihodPartsTable m_parts_table = new WsPrihodPartsTable();
	
	protected JLabel m_comboPartTypeLabel = new JLabel(getGuiStrs("partTypeFilterComboBoxLabel"));
	
	protected WsPartTypesFilterComboBox m_partTypesCombo = new  WsPartTypesFilterComboBox();
	
	protected JLabel m_comboAgentsLabel = new JLabel(getGuiStrs("agentsFilterComboBoxLabel"));
	
	protected WsAgentsFilterComboBox m_agentComboBox = new WsAgentsFilterComboBox();
	
	//private WsAgentsWithTypesPanel m_agentComboBox = new WsAgentsWithTypesPanel();
	
	protected WsContractsFilterComboBox m_contractsCombo = new WsContractsFilterComboBox();
	
	protected JCheckBox m_sum_checkbox = new JCheckBox(getGuiStrs("sumSamePositionsKod"));
	
	JSplitPane splitPane = null; 
	
	private int m_selected_order_row = -1;
	
	JMenuItem m_itemEdit = null;
	   
	JMenuItem m_itemDelete = null;
	   
    JMenuItem m_itemAdd = null;
    
    JMenuItem m_itemImportExcel = null;
    
    JMenuItem m_itemRashodAll = null;
    
    JMenuItem m_itemExportExcel = null;
    
    JMenuItem m_itemClearFilter = null;
    
    JMenuItem m_itemChangeContract = null;
    
    JMenuItem m_itemFind = null;
    
	protected JTextField m_sumwithnds = new JTextField(10);
	
	protected JTextField m_sumnds = new JTextField(10);
	
	protected JTextField m_sum = new JTextField(10);
	
	protected JTextField m_number_positions = new JTextField(10);
	
	protected static WsMutableString m_excel_save_folder = new WsMutableString(".");
   
	public WsPrihodForm() {
		
		createGUI();
		
		m_dates.setSqlStartDate( WsUtils.sqlDatePlusMonth(Calendar.getInstance().getTime(), -1)  );
		
		m_dates.setSqlEndDate( WsUtils.sqlDatePlusDays(Calendar.getInstance().getTime(), 6)  );
		
		setPopupMenu();
		
		setListeners();
		
		setGuiEnabled(false);
		
	}
	
	private void setGuiEnabled(boolean flag) {
	
		m_importButton.setEnabled(flag);
		
		m_dates.setEnabled(flag);
		
		m_agentComboBox.setEnabled(flag);
		 
		m_showButton.setEnabled(flag);
		
		m_clearFilterButton.setEnabled(flag);
		 
		m_table.setEnabled(flag);
			
		m_parts_table.setEnabled(flag);
		
		m_partTypesCombo.setEnabled(flag);
	}
	
	private JPanel createFilterPanel() {
		
		JPanel filterPanel = WsGuiTools.createHorizontalPanel();
		
		WsUtils.get().setFixedSizeBehavior(m_agentComboBox);
		
		WsGuiTools.setComponentFixedWidth(m_agentComboBox, 200);
		
		WsUtils.get().setFixedSizeBehavior(m_contractsCombo);
		
		WsGuiTools.setComponentFixedWidth(m_contractsCombo, 200);
		
		WsUtils.get().setFixedSizeBehavior(m_partTypesCombo);
		
		WsGuiTools.setComponentFixedWidth(m_partTypesCombo, 250);
		
		Dimension size = m_agentComboBox.getPreferredSize();
		
		size.width = 250;

		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("filterBorderTitle"));
		
		filterPanel.setBorder(title);
		
		filterPanel.add(m_comboPartTypeLabel);
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
	
		filterPanel.add(m_partTypesCombo);
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		filterPanel.add(m_comboAgentsLabel); 
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		filterPanel.add(m_agentComboBox);
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		filterPanel.add(new JLabel(getGuiStrs("contractsComboLabel")));
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));

		filterPanel.add( m_contractsCombo);
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		filterPanel.add(m_dates);
		
		filterPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		filterPanel.add(m_showButton);
		
		filterPanel.add(m_clearFilterButton);
		
		Dimension sizeD = m_dates.getPreferredSize();
		
		sizeD.width = 300;
		
		m_dates.setMaximumSize(sizeD);
		
		filterPanel.add(Box.createHorizontalGlue());
		
		return filterPanel;
		
	}
	
	private void createGUI() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        JScrollPane scroll2 = new JScrollPane(m_parts_table);
        
        scroll2.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll2.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		mainPanel.add(createFilterPanel());
		
		JPanel bottomPanel = WsGuiTools.createVerticalPanel();
		
		bottomPanel.add(scroll2);
		
		bottomPanel.add(getSumGuiPanel());
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scroll, bottomPanel);
		
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setDividerLocation(250);

		JPanel splitPanel = WsGuiTools.createHorizontalPanel();

		splitPanel.add(splitPane);
		
		mainPanel.add(splitPanel);

		setLayout(new BorderLayout());
		
		m_showButton.setAction(new WsPrihodFilterShowAction(this));
		
		m_clearFilterButton.setAction(new WsClearFilterPrihodAction(this));
		
		m_importButton.setAction(new  WFNewExcelPrihodImportDialogAction());
		
		m_table.setSelectionListener(new ListSelectionListener() {
			 
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				int viewRow = m_table.getSelectedRow();
				
				if(m_selected_order_row != viewRow) {
					
					m_selected_order_row = viewRow;
					
					m_parts_table.refreshData(m_table.getSelectedId());
					
					WsNaklSums sm = m_parts_table.getSums();
					
					m_sumwithnds.setText(WsUtils.getDF_fix_str(sm.sumwithnds, 4));
					
					m_sumnds.setText(WsUtils.getDF_fix_str(sm.sumnds, 4));
					
					m_sum.setText(WsUtils.getDF_fix_str(sm.sum, 4));
					
					m_number_positions.setText(String.valueOf(m_parts_table.getRowCount()));
					
				}
				
			}
	     });
		
		add(mainPanel);
		
		setToolTips();
	}
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED 
				|| e.getType() == WsEventEnable.TYPE.INVOICE_HAS_BEEN_CHANGED) {
			
			
			if(m_importButton.isEnabled() == false) {  setGuiEnabled(true);  }
			
			if(m_dates.getStartDate().compareTo(m_dates.getEndDate()) > 0) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("dateRangeIsInvalidMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				
			}
			else {
			
				int id_part_type = m_partTypesCombo.getCurrentSQLId();
	  		   
		   		int id_agent = m_agentComboBox.getCurrentSQLId();
		   		
		   		int id_contract =  m_contractsCombo.getCurrentSQLId();
	
			    m_table.refreshData(id_part_type, id_agent, id_contract, m_dates.getSqlStartDate(),
			    		m_dates.getSqlEndDate());
			    
			    m_table.setToolTipText(getGuiStrs("topPrihodTableToolTip") +
			    		" : " + m_table.getRowCount());
			    
			}	
		}	
	}
	
	public void refreshDataAndSelect(WsPrihodInvoiceChangedEvent e) {
		
		if(e != null && e.getEventType() == WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED) {
			
			if(m_dates.getStartDate().compareTo(m_dates.getEndDate()) > 0) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("dateRangeIsInvalidMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				
			}
			else {
	
			    int id_part_type = m_partTypesCombo.getCurrentSQLId();
  		   
  		   		int id_agent = m_agentComboBox.getCurrentSQLId();
  		   		
  		   		int id_contract =  m_contractsCombo.getCurrentSQLId();
  	
 			    m_table.refreshData(id_part_type, id_agent,  id_contract, m_dates.getSqlStartDate(),
			    		m_dates.getSqlEndDate() );
   				
   				int row = e.getRowId();
   				
   				m_table.setSelectedSqlId(row) ;
   				
			}		
		}
	}
	
	
	public int getSelectedCarId() {
		
		int id = m_table.getSelectedId();
		
		return id;
		
	}
	
	
	public int getSelectedPrihodId() {
		
		return m_table.getSelectedId();
	}
	
	public Vector<Integer> getSelectedPrihodIds() {
		
		return m_table.getSelectedIds();
	}
	
	public WsPrihodData getPrihodDataForEdit() {
		
		return WsPrihodSqlStatements.getPrihodForId(getSelectedPrihodId()) ;
			
	}
	
	private void setPopupMenu() {
		
		   JPopupMenu m_popupMenu = new JPopupMenu();

		   m_itemEdit = new JMenuItem(getGuiStrs("editTableNameMenu"), 
				   WsUtils.get().getIconFromResource("wseditnakl.png"));
		   
		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsdelrow.png"));
		   
		   m_itemAdd = new JMenuItem(getGuiStrs("newTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsnewnakl.png"));
		   
		   m_itemImportExcel = new JMenuItem(getMenusStrs("importExcelPrihodTableNameMenu"),
				   WsUtils.get().getIconFromResource("importExcel_16.png"));
		   
		   m_itemRashodAll = new JMenuItem(getMenusStrs("allPrihodInRashodTableNameMenu"));
		   
		   m_itemExportExcel = new JMenuItem(getMenusStrs("exportExcelPrihodTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsexportexcel_16.png"));
		   
		   m_itemClearFilter  = new JMenuItem(getMenusStrs("clearFilPrTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsfilterclear_16.png"));
		   
		   m_itemChangeContract = new JMenuItem(getMenusStrs("chConPrTableNameMenu"));
		   
		   m_itemFind = new JMenuItem(getMenusStrs("findTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsfind_16.png"));
		   
		   JMenu m_exportImportMenu = new JMenu(getMenusStrs("exImpNameMenu"));
		   
		   JMenu m_groupOperationMenu = new JMenu(getMenusStrs("groupOperationsNameMenu"));
		   
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemAdd.addActionListener( listener);
	       
	       m_itemDelete.addActionListener( listener);
	        
	       m_itemEdit.addActionListener( listener);
	       
	       m_itemImportExcel.addActionListener( listener);
	       
	       m_itemRashodAll.addActionListener( listener);
	       
	       m_itemExportExcel.addActionListener( listener);
	       
	       m_itemClearFilter.addActionListener( listener);
	       
	       m_itemChangeContract.addActionListener( listener);
	       
	       m_itemFind.addActionListener( listener);
	       
	       m_popupMenu.add(m_itemAdd);
	        
	       m_popupMenu.add(m_itemEdit);
	        
	       m_popupMenu.add(m_itemDelete);
	        
	       m_popupMenu.addSeparator();
	       
	       m_popupMenu.add(m_itemClearFilter);
	       
	       m_popupMenu.addSeparator();
	        
	       m_exportImportMenu.add( m_itemImportExcel);
	        
	       m_exportImportMenu.add( m_itemExportExcel);
	       
	       m_groupOperationMenu.add( m_itemRashodAll);
	       
	       m_groupOperationMenu.add(m_itemChangeContract);
	       
	       m_popupMenu.add(m_groupOperationMenu);
	       
	       m_popupMenu.add(m_exportImportMenu);
	       
	       m_popupMenu.add(m_itemFind);
	       
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
        	   
	      	   if (WSAgentSqlStatements.isAgentsTableEmpty()) {
	      		   
	          		   JOptionPane.showMessageDialog(
	         	   			    WsUtils.get().getMainWindow(),
	         	   			    getMessagesStrs("agentsEmtyMessage"),
	         	   			    getMessagesStrs("messageInfoCaption"),
	         	   			    JOptionPane.CLOSED_OPTION);
	          		   
	          		   return;
	      	   }
			   
               if (menu == m_itemAdd) {
               	
            	   WsNewPrihodDialog dialog = new WsNewPrihodDialog(WsUtils.get().getMainWindow(), null, 
           				getGuiStrs("newOrderDialogWinCaption"));
            	   
            	   Dimension minSize = dialog.getMinimumSize();
            	   
            	   minSize.height = 600;
            	   
            	   dialog.setMinimumSize(minSize);
           		
           		   dialog.setVisible(true);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   deleteCurrentInvoice();
            	   
   				   WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();

   				   WsEventDispatcher.get().fireCustomEvent(ev);
                   
               } else if (menu ==  m_itemEdit) {
            	   
            	   editCurrentInvoice();
               }
               else if (menu == m_itemImportExcel) {
            	   

            	   
            		 WFExcelImportPrihodDialog dialog = new  WFExcelImportPrihodDialog(WsUtils.get().getMainWindow(), 
            					getGuiStrs("excelImportPrihodDialogWinCaption"));
            			
            		 dialog.setVisible(true);
               }
               else if (menu == m_itemRashodAll) {
            	   
            	   Vector<Integer> selected_vec = getSelectedPrihodIds();
            	   
            	   if( selected_vec.isEmpty()) {
            		   
            		   JOptionPane.showMessageDialog(
            	   			    WsUtils.get().getMainWindow(),
            	   			    getMessagesStrs("prihodInRashodNoSelectionMessage"),
            	   			    getMessagesStrs("messageInfoCaption"),
            	   			    JOptionPane.CLOSED_OPTION);
            			   
            			   return;
            		   
            	   }
            	   
            	   WsPrihodInRashodDialog dialog = new  WsPrihodInRashodDialog(WsUtils.get().getMainWindow(), 
            			   selected_vec, getGuiStrs("prihodInRashodDialogWinCaption"));
       			
       			   dialog.setVisible(true);
            	   
             }  
             else if (menu == m_itemClearFilter) {
            	 
            	 clearFilter();
             } 
             else if (menu == m_itemExportExcel) {
            	   
          		 exportInvoiceToExcel();
             }
             else if (menu ==  m_itemChangeContract) {
            	 
         		int id = getSelectedPrihodId();
        		
        		if (id  == -1) {
        			
        		   JOptionPane.showMessageDialog(
           			    WsUtils.get().getMainWindow(),
           			    getMessagesStrs("ctChNoSelectionMessage"),
           			    getMessagesStrs("messageInfoCaption"),
           			    JOptionPane.CLOSED_OPTION);
        		   
        		   return;
        		   
        		}
          	   
            	 WsContractGroupChangeDialog dialog = new  WsContractGroupChangeDialog(WsUtils.get().getMainWindow(), 
            			 WsPrihodForm.this, getGuiStrs("contrChangeDialogWinCaption"));
     			
     			 dialog.setVisible(true);
             }
             else if (menu == m_itemFind) {
          	   
            	 WsFindDialog<WsPrihodForm> dialog = new  WsFindDialog<WsPrihodForm>(WsUtils.get().getMainWindow(), 
            			 WsPrihodForm.this, getGuiStrs("prihodFindDialogWinCaption"));
     			
     			 dialog.setVisible(true);
          	   
           } 
          
		}
	};
	
	private void deleteCurrentInvoice() {
		
		int id = getSelectedPrihodId();
		
		if (id  == -1) {
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("deletePrihodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
		
		String operation = getMessagesStrs("messageDeletePrihodApproveMessage");
			
		int res = WsUtils.showYesNoDialog(operation);
		
		if ( 1 == res) {
			
			int num = WsPrihodSqlStatements.deletePrihodForId(id);
			
			if(num > 0) {
				
				  refreshData(null);
				
		          JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deletePrihodSuccessMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
			}
			else {
				
				   JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deletePrihodFailMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
				
			}
		}
	}
	
	private void editCurrentInvoice() {
		
		WsPrihodData dt = getPrihodDataForEdit();
		
		if (dt != null && dt.id != -1)  {
		
			WsNewPrihodDialog dialog = new WsNewPrihodDialog(WsUtils.get().getMainWindow(), 
					dt, getGuiStrs("wsEditPrihodDialogCaption") );
			
			dialog.setVisible(true);
		}
		else {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("editPrihodFailNoSelectionMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
		}
	
	}
	
	
	private void setToolTips() {
		
		m_table.setToolTipText(getGuiStrs("topPrihodTableToolTip"));
		
		m_parts_table.setToolTipText(getGuiStrs("bottomPrihodTableToolTip"));
		
		m_agentComboBox.setToolTipText(getGuiStrs("agentComboBoxToolTip"));
		
		m_showButton.setToolTipText(getGuiStrs("filterButtonToolTip"));
		
		m_clearFilterButton.setToolTipText(getGuiStrs("clearFilterButtonToolTip"));
		
		m_importButton.setToolTipText(getGuiStrs("importPrihodExcelButtonToolTip"));
		
		m_partTypesCombo.setToolTipText(getGuiStrs("partTypesComboFilterToolTip"));
		
	}
	
	
	private JPanel getSumGuiPanel() {
		
		JPanel v = WsGuiTools.createHorizontalPanel();
		
		v.add(new JLabel(getGuiStrs("numberPositionsPrihodLabel")));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_number_positions);
		
		v.add(m_sum_checkbox);
		
		v.add(Box.createHorizontalGlue());
		
		v.add(new JLabel(getGuiStrs("sumWithoutNdsLabel") + " : "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sum);
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(new JLabel(getGuiStrs("sumNdsLabel")  + " : "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sumnds);
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(new JLabel(getGuiStrs("sumWithNdsLabel") + " : "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sumwithnds);
		
		Dimension d = m_sumwithnds.getPreferredSize();
		
		d.width = 100;
		
		m_sumwithnds.setMaximumSize(d);
		
		m_sumnds.setMaximumSize(d);
		
		m_sum.setMaximumSize(d);
		
		d.width = 50;
		
		m_number_positions.setMaximumSize(d);
		
		m_sumwithnds.setEditable(false);
			
		m_sumnds.setEditable(false);
			
		m_sum.setEditable(false);
		
		return v;
		
	}
	
	public void clearFilter() {
		
		
		 Vector<java.sql.Date> date_vec = WsPrihodSqlStatements.getPrihodMinMaxDate();
		 
		 if(null != date_vec && date_vec.elementAt(0) != null && date_vec.elementAt(1) != null) {
			 
				m_dates.setSqlStartDate(date_vec.elementAt(0));
				
				m_dates.setSqlEndDate(date_vec.elementAt(1));
				
				m_agentComboBox.setSelectedIndex(0);
				
				m_partTypesCombo.setSelectedIndex(0);
				
				m_contractsCombo.setSelectedIndex(0);
				
				refreshData(null);
			 
		 }
		
	}
	
	private void setListeners() {
		
		
		  ActionListener actionListener = new ActionListener() {
			  
		  public void actionPerformed(ActionEvent actionEvent) {
		
		    	  m_parts_table.setSumKodFlag( m_sum_checkbox.isSelected());
		    	  
		    	  m_parts_table.refreshData(m_table.getSelectedId());
		 
		      }
		  };


		  m_sum_checkbox.addActionListener(actionListener);
		
	}
	
	private void exportInvoiceToExcel() {
		
		if(-1 ==  getSelectedPrihodId()) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("noSelectionPrMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		
		String file_to_save = WsUtils.get().showFileChooser("xlsx", this, m_excel_save_folder);
			
		if (null == file_to_save)  { return; }
		
		WsPrihodData d = getPrihodDataForEdit();
		
		Vector<WsPrihodPartData> parts_vec = WsPrihodSqlStatements.getPrihodPartsList(d.id, 0);
		
		OutputStream out;
		
		try {
			
			out = new FileOutputStream(file_to_save);
	
			XSSFWorkbook wb = new XSSFWorkbook();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    int rowCount = 0;
		    
		    XSSFRow row = sheet.createRow(rowCount);
		    
		    XSSFCell cell = row.createCell(0);
		    
		    cell.setCellValue(getGuiStrs("naklReportName2") + " №"); 
		    
		    cell = row.createCell(1);
		    
		    cell.setCellValue(d.number); 
		    
		    cell = row.createCell(2);
		    
		    cell.setCellValue(getGuiStrs("vidNaklName2"));
		    
		    cell = row.createCell(3);
		    
		    cell.setCellValue(WsUtils.dateSqlToString(d.date, "dd-MM-yyyy"));
		    
		    cell = row.createCell(4);
		    
		    cell.setCellValue( getGuiStrs("d46DataDoc") + " : ");
		    
		    cell = row.createCell(5);
		    
		    cell.setCellValue(WsUtils.dateSqlToString(d.date_doc, "dd-MM-yyyy"));
		    
		    row = sheet.createRow(++rowCount);
		    
		    row = sheet.createRow(++rowCount);
		    
		    cell = row.createCell(0);
		    
		    cell.setCellValue(getGuiStrs("postachDatabaseName") + " : ");
		    
		    cell = row.createCell(1);
		    
		    cell.setCellValue(d.agentName);
		    
		    row = sheet.createRow(++rowCount);
		    
		    row = sheet.createRow(++rowCount);
		    
        	cell = row.createCell(0);
        	
            cell.setCellValue("№"); 
            
            cell = row.createCell(1);
            
            cell.setCellValue(getGuiStrs("kodColumnName"));
            
        	cell = row.createCell(2);
        	
            cell.setCellValue(getGuiStrs("nameKodColumnName"));
            
            cell = row.createCell(3);
        	
            cell.setCellValue(getGuiStrs("unitsNameColumName"));
            
            cell = row.createCell(4);
        	
            cell.setCellValue(getGuiStrs("quantityKodColumnName"));
            
            cell = row.createCell(5);
        	
            cell.setCellValue(getGuiStrs("costNameInReportNoNDS"));
            
            cell = row.createCell(6);
        	
            cell.setCellValue(getGuiStrs("prihodPartsColumnNdsName"));
            
            cell = row.createCell(7);
        	
            cell.setCellValue(getGuiStrs("prWithPdv"));
            
            cell = row.createCell(8);
        	
            cell.setCellValue(getGuiStrs("primitka"));
            
            cell = row.createCell(9);
        	
            cell.setCellValue(getGuiStrs("prihodPartsColumnRestName"));
		    		
			for(int i = 0; i <  parts_vec.size(); ++i) {
				
				WsPrihodPartData dt =  parts_vec.elementAt(i);
	
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
            	
                cell.setCellValue(WsUtils.getDF_fix(dt.quantity, 4));
                
                cell = row.createCell(5);
            	
                cell.setCellValue(WsUtils.getDF_fix(dt.cost, 3));
                
                cell = row.createCell(6);
            	
                cell.setCellValue(WsUtils.getDF_fix(dt.nds, 3));
                
                cell = row.createCell(7);
            	
                cell.setCellValue(WsUtils.getDF_fix(dt.costwithnds, 3));
                
                cell = row.createCell(8);
            	
                cell.setCellValue(dt.info);
                
                cell = row.createCell(9);
            	
                cell.setCellValue(WsUtils.getDF_fix(dt.rest, 4));
                
	         }
	    	
			sheet.autoSizeColumn(1);

			wb.write(out);

			out.close();
	    
			wb.close(); 
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("saveExcelReportSuccessMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
    
		} catch (IOException  e) {

			e.printStackTrace();
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("saveExcelReportFailedMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		}	
	}
	
	public int changeContract( int id_new_contract, boolean rashodChangeFlag  ) {
		
		if(id_new_contract == -1) {
			
			return 0;
		}
		
		Vector<Integer> vec = getSelectedPrihodIds();
		
		if(vec.isEmpty()) {
			
			return 0;
		}
		
		int processed = 0;
		
		for(int i = 0; i < vec.size(); ++i) {
			
			int id = vec.elementAt(i);
			
			processed += WsPrihodSqlStatements.updatePricesForInvoice( id_new_contract,  id, rashodChangeFlag);
			
		}		
		
		WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();
		
		ev.setRowId( vec.elementAt(0));
		
		WsEventDispatcher.get().fireCustomEvent(ev);
		
		return processed;
			
	}
	
	public void findNumberOrInfo(String s, int flag) {
		
		int found  = m_table.selectFind(s, flag);
		
		if(found == 0) {
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("nothingFoundMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
		}
		else {
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("nFoundMessage") + " " + String.valueOf(found),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
		}
	
	}	
}



