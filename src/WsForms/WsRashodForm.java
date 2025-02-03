package WsForms;

import static WsMain.WsUtils.HOR_STRUT;
import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMenusStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import WsActions.WsClearFilterRashodAction;
import WsActions.WsPrintRashodAction;
import WsActions.WsShowRashodAction;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentsFilterComboBox;
import WsControls.WsContractsFilterComboBox;
import WsControls.WsPartTypesFilterComboBox;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsRashodData;
import WsDatabase.WSAgentSqlStatements;
import WsDatabase.WsRashodSqlStatements;
import WsDialogs.WsAutoRashodDialog;
import WsDialogs.WsExcelKartkaImportDialog;
import WsDialogs.WsExcelRaskladkaRashodImport2Dialog;
import WsDialogs.WsExcelRaskladkaRashodImportDialog;
import WsDialogs.WsExportRashodAsPrihodDialog;
import WsDialogs.WsFindDialog;
import WsDialogs.WsGroupImportRashodDialog;
import WsDialogs.WsNewRashodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsReports.WsRashNaklMilitary2Report;
import WsReports.WsRashNaklMilitaryReport;
import WsReports.WsRashNaklReport;
import WsTables.WsRashodPartsTable;
import WsTables.WsRashodTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsRashodForm  extends JPanel {
		
		private static final long serialVersionUID = 1L;

		{
			WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
			
			WsEventDispatcher.get().addConnect(WsEventDispatcher.SALE_INVOICE_HAS_BEEN_CHANGED, this, "refreshDataAndSelect");
			
		}
		
		Ws2DatesControl m_dates = new Ws2DatesControl(false);
		
		protected  JButton m_showButton = new JButton(getGuiStrs("buttonShowCaption"));
		
		protected  JButton m_clearFilterButton = new JButton(getGuiStrs("buttonClearFilterCaption"));
		
		protected  JButton m_printButton = new JButton(getGuiStrs("buttonPrintCaption"));
		
		protected WsRashodTable m_table = new WsRashodTable();
		
		protected WsRashodPartsTable m_parts_table = new WsRashodPartsTable();
		
		protected JLabel m_comboPartTypeLabel = new JLabel(getGuiStrs("partTypeFilterComboBoxLabel"));
		
		protected WsPartTypesFilterComboBox m_partTypesCombo = new  WsPartTypesFilterComboBox();
		
		protected JLabel m_comboAgentsLabel = new JLabel(getGuiStrs("agentsFilterComboBoxLabel"));
		
		protected WsAgentsFilterComboBox m_agentComboBox = new WsAgentsFilterComboBox();
		
		protected WsContractsFilterComboBox m_contractsCombo = new WsContractsFilterComboBox();
		
		protected JCheckBox m_sum_checkbox = new JCheckBox(getGuiStrs("sumSamePositionsKod"));
		
		JSplitPane splitPane = null; 
		
		private int m_selected_order_row = -1;
		
	    JMenuItem m_itemClearFilter  = null;
		
		JMenuItem m_itemEdit = null;
		   
		JMenuItem m_itemDelete = null;
		   
	    JMenuItem m_itemAdd = null;
	    
	    JMenuItem m_itemAuto = null;
	    
	    JMenuItem m_itemFind = null;
	    
	    JMenuItem m_itemRaskladka = null;
	    
	    JMenuItem m_itemRaskladkaAgents = null;
	    
	    JMenuItem m_itemPrihodExport = null;
	    
	    JMenuItem m_itemKartkaZvit = null;
	    
	    JMenuItem m_itemGroupImport = null;
	    
	    JMenuItem m_itemPrint = null;
	    
	    JMenuItem m_itemNakl = null;
	    
	    JMenuItem m_itemNakl2 = null;
	    
	    JMenuItem m_itemNakl3 = null;
	    
		protected JTextField m_sumwithnds = new JTextField(10);
		
		protected JTextField m_sumnds = new JTextField(10);
		
		protected JTextField m_sum = new JTextField(10);
		
		protected JTextField m_number_positions = new JTextField(10);
	    
		public  WsRashodForm() {
			
			createGUI();
			
			m_dates.setSqlStartDate( WsUtils.sqlDatePlusMonth(Calendar.getInstance().getTime(), -1)  );
			
			m_dates.setSqlEndDate( WsUtils.sqlDatePlusDays(Calendar.getInstance().getTime(), 6)  );
			
			setPopupMenu();
			
			setListeners();
			
		}
		
		private JPanel createToolbarPanel() {
			
			JPanel toolbar_panel = WsGuiTools.createHorizontalPanel();
			
			WsUtils.get().setFixedSizeBehavior(m_agentComboBox);
			
			WsGuiTools.setComponentFixedWidth(m_agentComboBox, 200);
			
			WsUtils.get().setFixedSizeBehavior(m_partTypesCombo);
			
			WsGuiTools.setComponentFixedWidth(m_partTypesCombo, 250);

			JPanel filterPanel = WsGuiTools.createHorizontalPanel();
			
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
			
			Dimension sizeD = m_dates.getPreferredSize();
			
			sizeD.width = 350;
			
			m_dates.setMaximumSize(sizeD);
			
			m_contractsCombo.setMaximumSize(sizeD);
			
			filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			filterPanel.add(m_showButton); 
			
			filterPanel.add(m_clearFilterButton);
			
			toolbar_panel.add(filterPanel);
		
			toolbar_panel.add(m_printButton);

			toolbar_panel.add(Box.createHorizontalGlue());
			
			return toolbar_panel;
			
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
			
			mainPanel.add(createToolbarPanel());
			
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
			
			m_showButton.setAction(new WsShowRashodAction(this));
			
			m_clearFilterButton.setAction(new WsClearFilterRashodAction(this));
			
			m_printButton.setAction(new WsPrintRashodAction(this));
			
		    ListSelectionModel cellSelectionModel = m_table.getSelectionModel();
	
		    cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
		 
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
			
			m_printButton.setVisible(false);
			
		}
		
		public void refreshData(WsEventEnable e) {
			
			if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED 
					|| e.getType() == WsEventEnable.TYPE.INVOICE_HAS_BEEN_CHANGED) {
				
					if(m_dates.getStartDate().compareTo(m_dates.getEndDate()) > 0) {
						
						   JOptionPane.showMessageDialog(
					   			    WsUtils.get().getMainWindow(),
					   			    getMessagesStrs("dateRangeIsInvalidMessage"),
					   			    getMessagesStrs("messageInfoCaption"),
					   			    JOptionPane.CLOSED_OPTION);
						
						
					}
					else {
						
				   		int id_agent = m_agentComboBox.getCurrentSQLId();
				   		
				   		int kod_id = -1;
				   		
				   		kod_id = m_partTypesCombo.getCurrentSQLId();
				   		
				   		int id_contract =  m_contractsCombo.getCurrentSQLId();
				   			
					    m_table.refreshData(id_agent, id_contract, m_dates.getSqlStartDate(),
					    		m_dates.getSqlEndDate(), kod_id, false);
					    
					    m_table.setToolTipText(getGuiStrs("topRashodTableToolTip") +
					    		" : " + m_table.getRowCount());
				    
					}
	   				
	   				
			}
		
			
		}
		
		public void refreshDataAndSelect(WsRashodInvoiceChangedEvent e) {
			
			if(e != null && e.getEventType() == WsEventDispatcher.SALE_INVOICE_HAS_BEEN_CHANGED) {
				
		
				if(m_dates.getStartDate().compareTo(m_dates.getEndDate()) > 0) {
					
					   JOptionPane.showMessageDialog(
				   			    WsUtils.get().getMainWindow(),
				   			    getMessagesStrs("dateRangeIsInvalidMessage"),
				   			    getMessagesStrs("messageInfoCaption"),
				   			    JOptionPane.CLOSED_OPTION);
					
					
				}
				else {
	  		   
	  		   		int id_agent = m_agentComboBox.getCurrentSQLId();
	  		   		
	  		   		int kod_id = -1;
			   		
			   		kod_id = m_partTypesCombo.getCurrentSQLId();
			   		
			   		int id_contract =  m_contractsCombo.getCurrentSQLId();
			   			  	
	 			    m_table.refreshData(id_agent, id_contract, m_dates.getSqlStartDate(),
				    		m_dates.getSqlEndDate(),  kod_id, false);
	   				
	   				int row = e.getRowId();
	   				
	   				if(row != -1) {
	   				
	   					m_table.setSelectedSqlId(row) ;
	   				
	   				}
	   				
				}
	   					
			}
		}
		
		
		public int getSelectedCarId() {
			
			int id = m_table.getSelectedId();
			
			return id;
			
		}
		
		
		class ItemChangeListener implements ItemListener{
			
		    @Override
		    public void itemStateChanged(ItemEvent event) {
		    	
		       if (event.getStateChange() == ItemEvent.SELECTED) {
		    	   
		    	   if (event.getSource() ==  m_agentComboBox) {

		    		   int id_agent = m_agentComboBox.getCurrentSQLId();
		    		   
		    		   int kod_id = -1;
			   		
			   		   kod_id = m_partTypesCombo.getCurrentSQLId();
			   		   
			   		   int id_contract =  m_contractsCombo.getCurrentSQLId();
			   			
		   			   m_table.refreshData(id_agent, id_contract, m_dates.getSqlStartDate(),
					    		m_dates.getSqlEndDate(), kod_id, false);
		    		   
		    	   } 
		  
		       }
		    }

		      
		}
		
		public int getSelectedRashodId() {
			
			return m_table.getSelectedId();
		}
		
		public Vector<Integer> getSelectedRashodIds() {
			
			return m_table.getSelectedIds();
		}
		
		public WsRashodData getRashodDataForEdit() {
			
			return WsRashodSqlStatements.getRashodForId(getSelectedRashodId()) ;
	
		}
		
		private void setPopupMenu() {
			
			
		   JPopupMenu m_popupMenu = new JPopupMenu();

		   m_itemEdit = new JMenuItem(getGuiStrs("editTableNameMenu"), 
				   WsUtils.get().getIconFromResource("wseditnakl.png"));
		   
		   m_itemDelete = new JMenuItem(getGuiStrs("deleteTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsdelrow.png"));
		   
		   m_itemAdd = new JMenuItem(getGuiStrs("newTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsnewnakl.png"));
		   
		   m_itemAuto = new JMenuItem(getGuiStrs("autoRashodTableNameMenu"));
		   
		   m_itemFind = new JMenuItem(getMenusStrs("findTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsfind_16.png"));
		   
		   m_itemPrint = new JMenuItem(getGuiStrs("printNameMenu"),
				   WsUtils.get().getIconFromResource("wsprint.png"));
		   
		   JMenu m_printMenu = new JMenu(getGuiStrs("printNameMenu"));
		   
		   m_printMenu.setIcon( WsUtils.get().getIconFromResource("wsprint16.png"));
		   
		   m_itemNakl = new JMenuItem(getGuiStrs("nakl1NameMenu"));
		   
		   m_itemNakl2 = new JMenuItem(getGuiStrs("nakl2NameMenu"));
		   
		   m_itemNakl3 = new JMenuItem(getGuiStrs("nakl3NameMenu"));
		   
		   m_printMenu.add(m_itemNakl);
		   
		   m_printMenu.add(m_itemNakl2);
		   
		   m_printMenu.add(m_itemNakl3);
		   
		   m_itemRaskladka = new JMenuItem(getGuiStrs("raskladkaRashodTableNameMenu"));
		   
		   m_itemRaskladkaAgents = new JMenuItem(getGuiStrs("raskladkaRashodTableNameMenu2"));
		   
		   JMenu rasklMenu = new JMenu(getGuiStrs("rasklNameMenu"));
		   
		   rasklMenu.add(m_itemRaskladka);
		   
		   rasklMenu.add(m_itemRaskladkaAgents);
		   
		   JMenu exportImportMenu = new JMenu(getMenusStrs("exImpNameMenu"));
		   
		   m_itemKartkaZvit = new JMenuItem(getGuiStrs("kartkaZvitRashodTableNameMenu"),
				   WsUtils.get().getIconFromResource("importExcel_16.png"));
		   
		   m_itemPrihodExport = new JMenuItem(getGuiStrs("exportRAshodAsPrihodTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsexportDB_16.png"));
		   
		   m_itemGroupImport = new JMenuItem(getGuiStrs("improtGroupNaklMenu"));
		   
		   exportImportMenu.add(m_itemKartkaZvit);
		   
		   exportImportMenu.add(m_itemGroupImport);
	        
	       exportImportMenu.add(m_itemPrihodExport);
		   
	       m_itemClearFilter  = new JMenuItem(getMenusStrs("clearFilPrTableNameMenu"),
				   WsUtils.get().getIconFromResource("wsfilterclear_16.png"));
		  
		   CustomPopupListener listener = new CustomPopupListener();
		   
	       m_itemAdd.addActionListener( listener);
	       
	       m_itemDelete.addActionListener( listener);
	        
	       m_itemEdit.addActionListener( listener);
	       
	       m_itemAuto.addActionListener( listener);
	       
	       m_itemRaskladka.addActionListener( listener);
	       
	       m_itemRaskladkaAgents.addActionListener( listener);
	       
	       m_itemKartkaZvit.addActionListener( listener);
	       
	       m_itemGroupImport.addActionListener( listener);
	       
	       m_itemPrihodExport.addActionListener( listener);
	       
	       m_itemFind.addActionListener( listener);
	       
	       m_itemNakl.addActionListener( listener);
		   
		   m_itemNakl2.addActionListener( listener);
		   
		   m_itemNakl3.addActionListener( listener);
		   
		   m_itemClearFilter.addActionListener( listener);

	        m_popupMenu.add(m_itemAdd);
	        
	        m_popupMenu.add(m_itemEdit);
	        
	        m_popupMenu.add(m_itemDelete);
	        
	        m_popupMenu.addSeparator();
	        
	        m_popupMenu.add( m_itemAuto);
	        
	        m_popupMenu.add( rasklMenu);
	        
	        m_popupMenu.add(exportImportMenu);
	        
	        m_popupMenu.addSeparator();
	        
	        m_popupMenu.add(m_itemClearFilter);
    
	        m_popupMenu.addSeparator();
	        
	        m_popupMenu.add(m_printMenu);
	        
	        m_popupMenu.add(m_itemFind);
	      
	        m_table.setComponentPopupMenu(m_popupMenu);
		
	}
	
	
	private class CustomPopupListener implements ActionListener {

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
            	   
	           		WsNewRashodDialog dialog = new WsNewRashodDialog(WsUtils.get().getMainWindow(), null, 
	        				getGuiStrs("newRahodDialogWinCaption"));
	        		
	        		dialog.setVisible(true);
                   
               } else if (menu == m_itemDelete) {
            	   
            	   if(getSelectedRashodIds().size() > 1) {
            		   
            		   deleteSelectedInvoices();
            	   }
            	   else {
            		   
            		   deleteCurrentInvoice();
            	   } 
                   
               } else if (menu ==  m_itemEdit) {
            	   
            	   editCurrentInvoice();
               }
               else if (menu ==  m_itemAuto) {
            	   
            	   WsAutoRashodDialog dialog = new  WsAutoRashodDialog(WsUtils.get().getMainWindow(),
            			   getGuiStrs("autoRashoDialogWinCaption"));
             		
              	   dialog.setVisible(true);
               }
               else if (menu ==  m_itemRaskladka) {
            	   
            	   WsExcelRaskladkaRashodImportDialog dialog = new WsExcelRaskladkaRashodImportDialog(WsUtils.get().getMainWindow(),
            			   getGuiStrs("raskladkaRashoDialogWinCaption"));
             		
              	   dialog.setVisible(true);
               }
               else if (menu ==  m_itemRaskladkaAgents) {
            	   
            	   WsExcelRaskladkaRashodImport2Dialog dialog = new WsExcelRaskladkaRashodImport2Dialog(WsUtils.get().getMainWindow(),
            			   getGuiStrs("raskladkaRashoDialog2WinCaption"));
             		
              	   dialog.setVisible(true);
               }
               else if (menu == m_itemKartkaZvit) {
            	   
            	   WsExcelKartkaImportDialog dialog = new WsExcelKartkaImportDialog(WsUtils.get().getMainWindow(),
            			   getGuiStrs("KZRashoDialogWinCaption"));
             		
              	   dialog.setVisible(true);
               }
               else if (menu == m_itemNakl) {
            	   
            	   showNaklReport1();
               }
               else if (menu == m_itemNakl2) {
            	   
            	   showNaklReport2();
               }
               else if (menu == m_itemNakl3) {
            	   
            	   showNaklReport3();
               }
               else if (menu == m_itemClearFilter) {
            	   
            	   clearFilter();
               }
               else if (menu == m_itemPrihodExport) {
            	   
            	   Vector<Integer> vec =  m_table.getSelectedIds();
            	   
            	   if(!vec.isEmpty()) {
            		   
            		   Vector<WsRashodData> dt = new  Vector<WsRashodData>();
            		   
            		   for(Integer id : vec) {
            			   
            			   WsRashodData d = WsRashodSqlStatements.getRashodForId(id);
            			   
            			   if(null != d) {
            				   
            				   dt.add(d);
            				   
            			   }
            			   
            		   }
            
	            	   WsExportRashodAsPrihodDialog dialog = 
	            			   new WsExportRashodAsPrihodDialog(WsUtils.get().getMainWindow(),
	            			   getGuiStrs("expRashodDialogWinCaption"), dt);
	             		
	              	   dialog.setVisible(true);
            	   }
               }
               else if (menu == m_itemFind) {
              	   
              	 	WsFindDialog<WsRashodForm> dialog = new  WsFindDialog<WsRashodForm>(WsUtils.get().getMainWindow(), 
              			 WsRashodForm.this, getGuiStrs("rashodFindDialogWinCaption"));
       			
              	 	dialog.setVisible(true);
            	   
               }  
               else if (menu ==  m_itemGroupImport) {
              	   
            	   	 WsGroupImportRashodDialog dialog = new WsGroupImportRashodDialog(WsUtils.get().getMainWindow(), 
                			  getGuiStrs("groupImportDialogWinCaption"));
         			
         			 dialog.setVisible(true);
              	   
                } 
		}

	};
	
	
	void deleteSelectedInvoices() {
		
		Vector<Integer> ids_vec = getSelectedRashodIds();
		
		if (ids_vec == null || ids_vec.isEmpty()) {
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("deleteRashodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
		
		String operation = getMessagesStrs("messageDeleteRashodSApproveMessage");
			
		int res = WsUtils.showYesNoDialog(operation);
		
		if ( 1 == res) {  
			
			res = WsUtils.showYesNoDialog(getMessagesStrs("numNaklForDelMes") + " " + String.valueOf(ids_vec.size())
			+ ". " + getMessagesStrs("addApproveForDelMes"));
		}
			
		if ( 1 == res) {
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			int num = 0 ;
			
			for(int i = 0; i < ids_vec.size(); ++i) {
				
				int id = ids_vec.elementAt(i);
				
				num += WsRashodSqlStatements.deleteRashod(id);
				
			}
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			if(num > 0) {
				
				refreshData(null);
				
				WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();

				WsEventDispatcher.get().fireCustomEvent(ev);
				
		          JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteRashodsSuccessMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
			}
			else {
				
				   JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteRashodFailMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
				
			}
		}
	}
		

	void deleteCurrentInvoice() {
		
		int id = getSelectedRashodId();
		
		if (id  == -1) {
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("deleteRashodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
		
		String operation = getMessagesStrs("messageDeleteRashodApproveMessage");
			
		int res = WsUtils.showYesNoDialog(operation);
			
		if ( 1 == res) {
			
			int num = WsRashodSqlStatements.deleteRashod(id);
			
			if(num > 0) {
				
				refreshData(null);
				
				WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();

				WsEventDispatcher.get().fireCustomEvent(ev);
				
		          JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteRashodSuccessMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
			}
			else {
				
				   JOptionPane.showMessageDialog(
	        			    WsUtils.get().getMainWindow(),
	        			    getMessagesStrs("deleteRashodFailMessage"),
	        			    getMessagesStrs("messageInfoCaption"),
	        			    JOptionPane.CLOSED_OPTION);
				
			}
		}
	}
	
	void editCurrentInvoice() {
		
		
		WsRashodData dt = getRashodDataForEdit();
		
		if (dt != null && dt.id != -1)  {
		
			WsNewRashodDialog dialog = new WsNewRashodDialog(WsUtils.get().getMainWindow(), 
					dt, getGuiStrs("wsEditRashodDialogCaption") );
			
			dialog.setVisible(true);
		}
		else {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("editRashodFailNoSelectionMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
		}
	}
	
	private void setToolTips() {
		
		m_showButton.setToolTipText(getGuiStrs("filterButtonToolTip"));
		
		m_clearFilterButton.setToolTipText(getGuiStrs("clearFilterButtonToolTip"));
		
		m_table.setToolTipText(getGuiStrs("topRashodTableToolTip"));
		
		m_parts_table.setToolTipText(getGuiStrs("bottomPrihodTableToolTip"));
		
		m_agentComboBox.setToolTipText(getGuiStrs("agentComboBoxToolTip"));
		
		m_partTypesCombo.setToolTipText(getGuiStrs("partTypesComboFilterToolTip"));
	}
		
	public void clearFilter() {
		
		
		 Vector<java.sql.Date> date_vec = WsRashodSqlStatements.getRashodMinMaxDate();
		 
		 if(null != date_vec && date_vec.elementAt(0) != null && date_vec.elementAt(1) != null) {
			 
				m_dates.setSqlStartDate(date_vec.elementAt(0));
				
				m_dates.setSqlEndDate(date_vec.elementAt(1));
				
				m_agentComboBox.setSelectedIndex(0);
				
				m_partTypesCombo.setSelectedIndex(0);
				
				refreshData(null);
			 
		 }
	}
	
	
	private void showNaklReport1() {
		
		int id = getSelectedRashodId();
		
		if (id  == -1) {
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("printRashodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
			
		WsRashodData dt = getRashodDataForEdit();
		
		if(dt != null) {
				
			WsRashNaklReport dialog = new WsRashNaklReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("newPrintDialogWinCaption"), dt);
			
			
			dialog.setVisible(true);
		}
			
	}
	
	private void showNaklReport2() {
		
		
		int id = getSelectedRashodId();
		
		if (id  == -1) {
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("printRashodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
		
		WsRashodData dt = getRashodDataForEdit();
		
		if(dt != null) {
				
			WsRashNaklMilitaryReport dialog = new WsRashNaklMilitaryReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("newPrintDialogWinCaption"), dt);
			
			
			dialog.setVisible(true);
		}
			
	}
	
	private void showNaklReport3() {
		
		int id = getSelectedRashodId();
		
		if (id  == -1) {
			
		   JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("printRashodFailNoSelectionMessage"),
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		   
		   return;
		   
		}
		
		WsRashodData dt = getRashodDataForEdit();
		
		if(dt != null) {
				
			WsRashNaklMilitary2Report dialog = new WsRashNaklMilitary2Report(WsUtils.get().getMainWindow(), 
					getGuiStrs("newPrintDialogWinCaption"), dt);
			
			
			dialog.setVisible(true);
		}
			
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
		
		v.add(new JLabel(getGuiStrs("sumNdsLabel") + " : "));
		
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
	
	private void setListeners() {
		
		  ActionListener actionListener = new ActionListener() {
			  
		      public void actionPerformed(ActionEvent actionEvent) {
		
		    	  m_parts_table.setSumKodFlag( m_sum_checkbox.isSelected());
		    	  
		    	  m_parts_table.refreshData(m_table.getSelectedId());
		 
		      }
		    };


		    m_sum_checkbox.addActionListener(actionListener);
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

