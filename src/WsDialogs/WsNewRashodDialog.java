package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import WsActions.WsKodFindSkladAction;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentsWithTypesPanel;
import WsControls.WsContractsComboBox;
import WsControls.WsFindKodField;
import WsDataStruct.WsContractPriceData;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsContractsSqlStatements;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsEditTables.WsRashodPartEditTable;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsEvents.WsEventEnable.TYPE;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsTokenizer;
import WsMain.WsUtils;
import WsTables.WsSkladTable;
import WsEvents.WsEventNewRashodDate;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewRashodDialog extends JDialog  {
	
	private static final long serialVersionUID = 1L;
	
	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.NEW_DATA_RASHOD_EVENT, this,
			"refreshSkladTableAfterDateChange");
	
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshSums");
	}
	
	private JRadioButton m_radioDateSort = new JRadioButton(getGuiStrs("skladDateSortRadioButtonLabel"));
	
	private JRadioButton m_radioTypeNameSort = new JRadioButton(getGuiStrs("skladTypeNameSortRadioButtonLabel"));

	private JRadioButton m_radioNameSort = new JRadioButton(getGuiStrs("skladNameRadioButtonLabel"));
	
	private ButtonGroup m_butGroup = new ButtonGroup();
	
	JButton m_button_ins = new JButton(WsUtils.get().getIconFromResource("wsarrow.png"));
	
	JButton m_button_import = new JButton(getGuiStrs("importButtonRashodLabel"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	JButton m_button_clear = new JButton("Clear");
	
	WsSkladTable m_sklad_table = new WsSkladTable();
	
	protected WsContractsComboBox m_contractsCombo =  new WsContractsComboBox();
	
	ActionListener m_radio_but_listener = new ActionListener() {
		  

			@Override
			public void actionPerformed(ActionEvent e) {
				
				refreshSkladTable();
				
			}
	 };
	
	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_number_label;
	
	protected JLabel m_date_label;
	
	protected JLabel m_agent_label;
	
	protected JLabel m_people_label;

	protected JLabel m_info_label;
	
	protected JTextField m_number = new JTextField(25);
	
	DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	SpinnerModel m_people_model = new SpinnerNumberModel(0, 0, 1000000, 1);
	
	JSpinner m_people = new  JSpinner(m_people_model);

	protected JTextField m_info = new JTextField(25);
	
	//protected WsAgentComboBox m_agent =  new WsAgentComboBox();
	
	private WsAgentsWithTypesPanel m_agent = new WsAgentsWithTypesPanel();
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;
	
	protected JTextField m_sumwithnds = new JTextField(10);
	
	protected JTextField m_sumnds = new JTextField(10);
	
	protected JTextField m_sum = new JTextField(10);
	
	protected JTextField m_number_pos = new JTextField(5);
	
	WsFindKodField m_findKod = null ;
	
	JFrame m_owner = null;

	protected WsRashodPartEditTable m_table = new WsRashodPartEditTable();
	
	WsRashodData m_data = null;
	
	double m_nds_coeff = WsUtils.getNdsCoeff();
	
	public WsNewRashodDialog(JFrame jfrm, WsRashodData dt, String nameFrame) {
		super (jfrm, nameFrame, true);
		
		m_owner = jfrm;
		
		m_data = new WsRashodData();
		
		m_data.date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		
		//edit mode
		if(dt != null) {
			
			m_data = dt;
			
			m_number.setText(dt.number); m_number.setBorder(BorderFactory.createLoweredBevelBorder());
			
			m_info.setText(dt.info); m_info.setBorder(BorderFactory.createLoweredBevelBorder());	
			
			m_agent.setCurrentSQLId(dt.id_counterparty);
			
			m_people.setValue(dt.people);
			
			m_table.setOldValuesVector(m_data);
			
		}
		
		m_date.setSqlStartDate(m_data.date);
		
		m_table.refreshData(m_data.id);
		
		add(createGUI());
		
		init();
		
		refreshSkladTable();
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		refreshSums(null);
		
		pack();
		
		setBounds(100, 100, 1200, 650);
		
	}
	
	void clearTable(){
		
		 m_table.deleteAllRows();
		
	}
	

	/**
	 * Listener for buttons reaction
	 * @author Andrii Dashkov license GNU GPL v3
	 *
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_OkButton ) {
				
				onOK(e);
			}
			else
			if ( e.getSource() == m_CancelButton ) {
				
				onCancel(e);
			}
			else
			if ( e.getSource() == m_button_clear ) {
				
				clearTable();
			}
			else 
			if ( e.getSource() ==  m_contractsCombo) {
				
				int id_contract = m_contractsCombo.getCurrentSQLId();
				
				changePricesForContract( id_contract); 
			}
			else
			if ( e.getSource() == m_button_import ) {
				
				 WFExcelImportRashodDialog dialog = new  WFExcelImportRashodDialog(WsNewRashodDialog.this, 
						 WsNewRashodDialog.this,
							getGuiStrs("excelImportRashodDialogWinCaption"));
					
					dialog.setVisible(true);
				
			};
			
		}
	}
	
	public void onOK(ActionEvent e) {

		flag = WsCloseFlag.OK;
		
		m_data.date = m_date.getSqlStartDate();
		
		m_data.number = m_number.getText();
		
		m_data.info = m_info.getText();
		
		m_data.id_counterparty = m_agent.getCurrentSQLId();
		
		m_data.people = (int) m_people.getValue();
		
		m_owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsRashodPartData> vec = m_table.getParts();
		
		if(containNegative(vec)) {
			
			WsUtils.showMessageDialog(getMessagesStrs("negativeQuantityWarning"));
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			return;
	
		}
		
		String restNotEnough = WsRashodSqlStatements.checkRashodForRest(vec);
		
		boolean successFlag = false;
		
		if(!restNotEnough.isEmpty()) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			String operationMessage =  getMessagesStrs("newRashodCreationFailRestProblem") + " " +
					restNotEnough;
			
			WsUtils.showMessageDialog(operationMessage);
			
			return;
			
		}
		
		String dateStr =  m_table.checkForPrihodDate(m_date.getSqlStartDate());
		
		if( dateStr != null) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			WsUtils.showMessageDialog(getMessagesStrs("messageWrongDateRashodPrihodCaption") + " " + dateStr);
			
			return;
			
		}
		
		if(m_data.id == -1) {
		
			String operationMessage =  getMessagesStrs("newRashodCreationFail");
			
			WsTransactions.beginTransaction(null);
			
			int inserted_id = WsRashodSqlStatements.createNewRashod(m_data, vec);
			
			if (inserted_id != -1) {
				
				WsTransactions.commitTransaction(null);
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				operationMessage =  getMessagesStrs("newRashodCreationSuccess");
				
				WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
				
				ev.setRowId(inserted_id);
				
				WsEventDispatcher.get().fireCustomEvent(ev);
				
				successFlag = true;
		       
			}
			else {
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				WsTransactions.rollbackTransaction(null); 
			}
			 
			WsUtils.showMessageDialog(operationMessage);
			
		}
		else {
			
			String operationMessage = getMessagesStrs("updateRashodFaultWoringDate");
			
			if(WsRashodSqlStatements.isNewDateForUsedRashodValid(m_data.id, m_data.date)) {
			
				operationMessage = getMessagesStrs("updateRashodFault");
				
				WsTransactions.beginTransaction(null);
				
				if(WsRashodSqlStatements.updateRashod(m_data, m_table.getParts())) {
					
					WsTransactions.commitTransaction(null);
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					
					operationMessage = getMessagesStrs("updateRashodSuccess");
					
					WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
					
					ev.setRowId(m_data.id);
					
					WsEventDispatcher.get().fireCustomEvent(ev);
					
					successFlag = true;
				}
				else {
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					
					WsTransactions.rollbackTransaction(null); 
				}
			
			}
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			WsUtils.showMessageDialog(operationMessage);
					
		}
		
		if( successFlag ) {
			
			WsEventDispatcher.get().disconnect(this);
			
			WsEventDispatcher.get().disconnect(m_table);
			
			WsEventDispatcher.get().disconnect( m_sklad_table);
			
			WsEventDispatcher.get().disconnect( m_contractsCombo);
			
			m_owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			dispose();
		
		}

	}
	
	

	private boolean containNegative( Vector<WsRashodPartData> vec ) {
		
		for(WsRashodPartData d: vec) {
			
			if(d.quantity < 0.0) { return true; }
			
		}
		
		return false;
	}
	
	
	
	/**
	 * Cancel button reaction
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		flag = WsCloseFlag.CANCEL;
		
		WsEventDispatcher.get().disconnect(this);
		
		WsEventDispatcher.get().disconnect(m_table);
		
		WsEventDispatcher.get().disconnect( m_sklad_table);
		
		WsEventDispatcher.get().disconnect( m_contractsCombo);
		
		dispose();
	}	
	
	
	/**
	 * 	
	 * @return close flag to determine what operation should be done after the dialog close
	 */
	public WsCloseFlag getClosedFlagValue () {
		
		return flag;
	}
	
	
	/**
	 * 
	 * @return main UI panel with all components
	 */
	private JPanel createGUI() {
		
		m_number_label = new JLabel(getGuiStrs("prihodNumberNewDialogLabel"));
		
		m_date_label = new JLabel(getGuiStrs("prihodDateNewDialogLabel"));
		
		m_info_label = new JLabel(getGuiStrs("infoPrihodNewDialogLabel"));
		
		m_agent_label = new JLabel(getGuiStrs("agentRashodNewDialogLabel"));
		
		m_people_label = new JLabel(getGuiStrs("peopleRashodNewDialogLabel"));
		
		m_findKod = new WsFindKodField(new WsKodFindSkladAction(m_sklad_table));
		
		JPanel panelMAIN = WsGuiTools.createVerticalPanel();
		
		JPanel panelSklad = createSkladPanel();

		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

	    JScrollPane scroll = new JScrollPane(m_table);
	        
	    scroll.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	    scroll.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        
	    Dimension dim = scroll.getMinimumSize();
	        
	    dim.height = 200;
	    
	    scroll.setMinimumSize(dim);
	    
	    JPanel panel_sum = WsGuiTools.createVerticalPanel();
	    
	    panel_sum.add(scroll);
	    
	    panel_sum.add(getSumGuiPanel());
	        
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				 panel_sum, panelSklad);
		
		splitPane.setOneTouchExpandable(true);
		
		JPanel panel_split = WsGuiTools.createHorizontalPanel();
		
		panel_split.add(splitPane);
		
		panelMAIN.add(createGrid());
		
		panelMAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));

		panelMAIN.add(panel_split);

		m_radioTypeNameSort.setSelected(true);

		setAllListeners();
		
		setToolTips();
		
		return panelMAIN;
	}
	
	
private JPanel createGrid() {
	
		JPanel panel = new JPanel(new GridBagLayout());
		
        GridBagConstraints c = new GridBagConstraints();
        
        c.insets = new Insets(3, 0, 3, 0);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.weightx = 0.2;

        c.gridx = 0;  c.gridy = 0;
        
        panel.add(m_number_label, c);
        
        c.gridx = 0;  c.gridy = 1;
        
        panel.add(m_date_label, c);
        
        c.gridx = 0;  c.gridy = 2;
        
        panel.add(m_agent_label, c);
        
        //c.gridx = 0;  c.gridy = 3;
        
        //panel.add(new JLabel(getGuiStrs("contractsComboLabel")), c);
        
        c.gridx = 0;  c.gridy = 3;
        
        panel.add(m_info_label, c);
        
        c.gridx = 0;  c.gridy = 4;
        
        panel.add(m_people_label, c);
        
        c.gridx = 0;  c.gridy = 5;
        
        panel.add(new JLabel(getGuiStrs("insPrRashod")), c);

        c.weightx = 1;
        
        c.gridx = 1;  c.gridy = 0;
        
        panel.add(m_number, c);
		
        c.gridx = 1;  c.gridy = 1;
		
        panel.add(m_date, c);
			
		c.gridx = 1;  c.gridy = 2;
		
		panel.add(m_agent, c);
		    
		c.gridx = 1;  c.gridy = 3;
		
		panel.add(m_info, c);
		
		c.gridx = 1;  c.gridy = 4;
		
		panel.add(m_people, c);
		
		c.gridx = 1;  c.gridy = 5;
		
		panel.add(m_contractsCombo, c);

		return panel;
		
	}
	
	
	private JPanel createSkladPanel() {
		
		JPanel panelLeft = WsGuiTools.createVerticalPanel();
				
		JPanel panelTop = WsGuiTools.createHorizontalPanel();
		
		JPanel buttons_panel = WsGuiTools.createHorizontalPanel();

		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("filterSkladBorderTitle"));
		
		buttons_panel.setBorder(title);
		
		buttons_panel.add( m_radioDateSort);
		
		buttons_panel.add( m_radioTypeNameSort);
		
		buttons_panel.add(m_radioNameSort);

		m_butGroup.add(m_radioDateSort);
		
		m_butGroup.add(m_radioTypeNameSort);
		
		m_butGroup.add(m_radioNameSort);
		
		m_radioDateSort.setSelected(true);
	
		JScrollPane scroll_left = new JScrollPane(m_sklad_table);
	        
	    scroll_left.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	    scroll_left.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    
	    panelTop.add(buttons_panel);
	    
	    panelTop.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panelTop.add(new JLabel(getGuiStrs("findKodLabelName")));
		
		panelTop.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
	    
	    panelTop.add(m_findKod);
	    
	    panelTop.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
	    
	    panelTop.add(m_button_ins);
	    
	    panelTop.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));

	    panelTop.add(m_button_import);
	    
	    panelTop.add(Box.createHorizontalGlue());
		
		m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panelTop.add(m_infoLabel);
		
		panelTop.add(m_OkButton);
		
		panelTop.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
		panelTop.add(m_CancelButton);
	    
	    panelLeft.add(panelTop);
	    
	    panelLeft.add(scroll_left);
			
		return panelLeft;
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_button_import.addActionListener(forwarder);
		
		m_contractsCombo.addActionListener(forwarder);

		PaFocusCustomListener fListener = new PaFocusCustomListener();
		
		m_number.addFocusListener(fListener);

		m_info.addFocusListener(fListener);
		
		m_button_ins.addActionListener(new ButCustomListener());
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
		
	     ListSelectionModel cellSelectionModel = m_table.getSelectionModel();

	     cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
	 
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				verifyInfo();
				
			}

			@SuppressWarnings("unused")
			public void insertImportData(Vector<WsRashodPartData> vec_ins) {
				
				for(int i = 0; i < vec_ins.size(); ++i) {
					
					WsRashodPartData insert_data = vec_ins.elementAt(i);
					
					if (!m_table.isRowWithIdInvoicePartsInserted(insert_data.id_invoice_parts ) ) {
						
						m_table.insertDataRow(insert_data);
					}
					
				}
			}
	     });
	     
	     m_table.addFocusListener(new FocusAdapter() {
	    	 
	         @Override
	         public void focusLost(FocusEvent e) {
	             verifyInfo();
	         }
	         
	     });

	     m_radioDateSort.addActionListener(m_radio_but_listener);
	 	
	 	 m_radioTypeNameSort.addActionListener(m_radio_but_listener);

	     m_radioNameSort.addActionListener(m_radio_but_listener);
	
		
	}
	
	public void verifyInfo() {
		
		boolean flag = true;
		
		if (m_date.getStartDate() == null) {
			
			flag = false;
			m_infoLabel.setText(getMessagesStrs("wrongDateValueMessage")); 
			
		}
		
		if ( WsTokenizer.isValidate(m_number.getText()) == false ||
				WsTokenizer.isValidate( m_info.getText()) == false ) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("notallowedSymbolsMessage")); 
			
		}
		
		m_OkButton.setEnabled(flag);
	   
		if (flag) { m_infoLabel.setText(""); }
		
		
	}
	
	
	class PaFocusCustomListener implements FocusListener
	{
		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent ef) {
		
			 verifyInfo();
			 
		}    
	}
	
	class ButCustomListener implements ActionListener
	{

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == m_button_ins) {
				
				multiInsertRowInPartsTable();
			}
			
		}

	    
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		  
		m_agent.setToolTipText(getGuiStrs("agentNewRashodComboBoxToolTip"));
		 
		m_button_import.setToolTipText(getGuiStrs("importExcelNewRashodButtonToolTip"));

		m_button_ins.setToolTipText(getGuiStrs("insertSkladPositionInNewRashodButtonToolTip")); 	
		
		m_sklad_table.setToolTipText(getGuiStrs("newRashodSkladTableToolTip")); 	
		
		m_table.setToolTipText(getGuiStrs("onewRashodDialodTableButtonToolTip")); 
		
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 

	 
	/**
	 * <p>Initiation function.Don't remove it! It must be empty, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {}
	
	public void refreshSkladTableAfterDateChange(WsEventNewRashodDate ev) {
		
		 refreshSkladTable();
	}
	
	private void refreshSkladTable() {
		
		int flag = 0;
		
		if(m_radioDateSort.isSelected()) {
			
			flag = 0;
		}
		if(m_radioTypeNameSort.isSelected()) {
			
			flag = 1;
		} 
		if(m_radioNameSort.isSelected()) {
			
			flag = 2;
		} 
		
		m_sklad_table.refreshData(m_date.getSqlStartDate(), flag, false);
				
	}
	
		
	private void multiInsertRowInPartsTable() {
				
		Vector<WsPrihodPartData>  pData_vec = m_sklad_table.getSelectedDataOrderParts();
				
		if(pData_vec == null || pData_vec.isEmpty()) { 
			
			   WsUtils.showMessageDialog( getMessagesStrs("noSkladSelectionMessage"));
			
			return; 
			
		}
		
		for(WsPrihodPartData pData : pData_vec) {
				
				WsRashodPartData insert_data = new WsRashodPartData();
				
				insert_data.id_sale_invoice = pData.id;
				
				insert_data.id_units = pData.id_units;
				
				insert_data.units_name = pData.units_name;
				
				insert_data.vendor_code_2 = pData.vendorcode2;
				
				insert_data.name = pData.name;
				
				insert_data.id_invoice_parts = pData.id;
				
				insert_data.quantity = pData.rest;
				
				insert_data.rest = pData.rest;
				
				insert_data.cost = pData.cost;
				
				insert_data.costwithnds = pData.costwithnds;
				
				insert_data.nds = pData.nds;
				
				insert_data.kod  = pData.kod;
				
				WsRashodPartData d_exist = m_table.setSelectedRowForId(insert_data.id_invoice_parts );
				
				if (d_exist == null ) {
					
					if(pData.date.compareTo(getDate()) > 0) {
						
						   WsUtils.showMessageDialog(getMessagesStrs("wrongDatePartsInsertedMessage"));
						
					}
					else {
						
						m_table.insertDataRow(insert_data);
					
						m_table.setLastRowSelected();
					}
				}
				else {
					
					String value = null;
					
					if(Math.abs(insert_data.rest - d_exist.quantity) < 0.001) {
						
					   value = String.valueOf(insert_data.rest );
					}
					else {
						
						 value = String.valueOf(insert_data.rest + d_exist.quantity );
					}
 
					WsUtils.showMessageDialog(getMessagesStrs("RowWithIdInvoicePartsInsertedMessage") + " " + value);
				}
		}
				
		WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
		        
		WsEventDispatcher.get().fireCustomEvent(ev);
				
	}
	
	@SuppressWarnings("unused")
	private void insertRowInPartsTable() {
		
		WsPrihodPartData pData = m_sklad_table.getSelectedDataOrderPart();
		
		if(pData == null) { 
			
			   WsUtils.showMessageDialog(getMessagesStrs("noSkladSelectionMessage"));
			
			return; 
			
		}
		
		WsRashodPartData insert_data = new WsRashodPartData();
		
		insert_data.id_sale_invoice = pData.id;
		
		insert_data.id_units = pData.id_units;
		
		insert_data.units_name = pData.units_name;
		
		insert_data.vendor_code_2 = pData.vendorcode2;
		
		insert_data.name = pData.name;
		
		insert_data.id_invoice_parts = pData.id;
		
		insert_data.quantity = pData.rest;
		
		insert_data.rest = pData.rest;
		
		insert_data.cost = pData.cost;
		
		insert_data.costwithnds = pData.costwithnds;
		
		insert_data.nds = pData.nds;
		
		insert_data.kod  = pData.kod;
		
		WsRashodPartData d_exist = m_table.setSelectedRowForId(insert_data.id_invoice_parts );
		
		if (d_exist == null ) {
			
			if(pData.date.compareTo(getDate()) > 0) {
				
				 WsUtils.showMessageDialog( getMessagesStrs("wrongDatePartsInsertedMessage"));
				
			}
			else {
				
				m_table.insertDataRow(insert_data);
			
				m_table.setLastRowSelected();
			}
		}
		else {
			
			String value = null;
			
			if(Math.abs(insert_data.rest - d_exist.quantity) < 0.001) {
				
			   value = String.valueOf(insert_data.rest );
			}
			else {
				
				 value = String.valueOf(insert_data.rest + d_exist.quantity );
			}
			  
			WsUtils.showMessageDialog( getMessagesStrs("RowWithIdInvoicePartsInsertedMessage") + " " + value);
		}
		
		WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
        
	 	WsEventDispatcher.get().fireCustomEvent(ev);
		
	}
	
	public void insertImportData(Vector<WsRashodPartData> vec_ins) {
		
		m_table.deleteAllRows();
		
		for(int i = 0; i < vec_ins.size(); ++i) {
			
			WsRashodPartData insert_data = vec_ins.elementAt(i);
			
			if (!m_table.isRowWithIdInvoicePartsInserted(insert_data.id_invoice_parts ) ) {
				
				m_table.insertDataRow(insert_data);
			}
			
		}
	}
	
	public void insertNotEnoughData(Vector<String> vec) {
		
		if (vec != null && !vec.isEmpty()) {
		
			StringBuilder s = new StringBuilder("- ");
			
			for(int i = 0; i < vec.size(); ++i) {
				
				String insert_data = vec.elementAt(i);
				
				s.append(insert_data);
				
				s.append(" ");
				
			}
			
			m_info.setText(s.toString());
		}
	}
	
	public Date getDate() { return m_date.getSqlStartDate(); }

	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		WsEventDispatcher.get().disconnect(m_table);
		
		WsEventDispatcher.get().disconnect(m_sklad_table);
		
		WsEventDispatcher.get().disconnect(m_agent);
		
		WsEventDispatcher.get().disconnect(m_agent);
		
		super.dispose();
		
	}
	
	private JPanel getSumGuiPanel() {
		
		JPanel v = WsGuiTools.createHorizontalPanel();
		
		v.add(new JLabel(getGuiStrs("numberPositionsPrihodLabel")));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_number_pos);
		
		v.add(Box.createHorizontalGlue());
		
		v.add(new JLabel(getGuiStrs("sumWithoutNdsLabel") + " : "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sum);
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(new JLabel(getGuiStrs("sumNdsLabel")+ " : "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sumnds);
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(new JLabel(getGuiStrs("sumWithNdsLabel") + " "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sumwithnds);
		
		Dimension d = m_sumwithnds.getPreferredSize();
		
		d.width = 100;
		
		m_sumwithnds.setMaximumSize(d);
		
		m_sumnds.setMaximumSize(d);
		
		m_sum.setMaximumSize(d);
		
		d.width = 50;
		 
		m_number_pos.setMaximumSize(d);
		 
		m_sumwithnds.setEditable(false);
			
		m_sumnds.setEditable(false);
			
		m_sum.setEditable(false);
		
		return v;
		
		
	}
	
	public void refreshSums(WsEventEnable ev) {
		
		if(ev == null || ev.getType() == TYPE.REFRESH_PRIHOD_SUM) {
			
			WsNaklSums s = m_table.getSums();
			
			double v = ((int)(s.sumwithnds*1000))/1000.0;
			 
			m_sumwithnds.setText(String.valueOf(v));
			
			v = ((int)(s.sumnds*1000))/1000.0;
				
			m_sumnds.setText(String.valueOf(v));
			
			v = ((int)(s.sum*1000))/1000.0;
					
			m_sum.setText(String.valueOf(v));
			
			m_number_pos.setText(String.valueOf(m_table.getRowCount()));
			
		}
		
	}
	
	private void changePricesForContract( int id_contract) {
		
		if(id_contract == -1) { return; }
		
		HashMap<Integer, WsContractPriceData> prices_map = 
				WsContractsSqlStatements.getContractPriceListMap(id_contract);
		
		if(prices_map.isEmpty()) { return; }

		Vector<WsRashodPartData> vec = m_table.getParts();
		
		m_table.deleteAllRows();
		
		for(WsRashodPartData d : vec ) {
			
			WsContractPriceData data = prices_map.get(d.kod);
			
			if(data != null) {
			
				d.costwithnds = data.costwnds;
				
				d.cost = d.costwithnds/m_nds_coeff;
				
				d.nds = d.costwithnds - d.cost;
				
			}
			
			m_table.insertDataRow(d);
			
		}
	
	}
	
}
