
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import WsActions.WsKodFindAction;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentsWithTypesPanel;
import WsControls.WsContractsComboBox;
import WsControls.WsFindKodField;
import WsDataStruct.WsContractPriceData;
import WsDataStruct.WsNaklSums;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsDatabase.WsContractsSqlStatements;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsEditTables.WsKodEditTable;
import WsEditTables.WsPrihodPartEditTable;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventEnable.TYPE;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsTokenizer;
import WsMain.WsUtils;
import WsEvents.WsPrihodInvoiceChangedEvent;


/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewPrihodDialog extends JDialog {
	
	{
	
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshSums");
	
	}
	private static final long serialVersionUID = 1L;
	
	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_number_label;
	
	protected JLabel m_date_label;
	
	protected JLabel m_date_label2;
	
	protected JLabel m_agent_label;

	protected JLabel m_info_label;
	
	protected JLabel m_katalog_label = new JLabel(getGuiStrs("catalogProductivNameLabel"));
	
	protected JTextField m_number = new JTextField(25);
	
	DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	Ws2DatesControl  m_date_doc = new Ws2DatesControl(true);
	
	protected JTextField m_info = new JTextField(25);
	
	private WsAgentsWithTypesPanel m_agent = new WsAgentsWithTypesPanel();
	
	protected WsContractsComboBox m_contractsCombo =  new WsContractsComboBox();
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;
	
	WsKodEditTable m_kod_table = new WsKodEditTable();
	
	public JButton m_kod_add_button = new JButton(WsUtils.get().getIconFromResource("wsarrow.png"));
	
	public JButton m_part_add_button = new JButton(getGuiStrs("newPartPrihodNewDialogLabel"));
	
	WsFindKodField m_findKod = null;
	
	HashMap<Integer, WsPartType>  m_catalog = new HashMap<Integer, WsPartType>();
	
	protected WsPrihodPartEditTable m_table = new WsPrihodPartEditTable();
	
	WsPrihodData m_data = null;
	
	double m_nds_coeff = 1.2;
	
	protected JTextField m_sumwithnds = new JTextField(10);
	
	protected JTextField m_sumnds = new JTextField(10);
	
	protected JTextField m_sum = new JTextField(10);
	
	public WsNewPrihodDialog(JFrame jfrm, WsPrihodData dt, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		m_data = new WsPrihodData();
		
		m_data.date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		
		m_data.date_doc = m_data.date;
		//edit mode
		if(dt != null) {
			
			m_data = dt;
			
			m_number.setText(dt.number); m_number.setBorder(BorderFactory.createLoweredBevelBorder());
			
			m_info.setText(dt.info); m_info.setBorder(BorderFactory.createLoweredBevelBorder());	
			
			m_agent.setCurrentSQLId(dt.id_counterparty);
			
			m_kod_table.setEnabled(false);
			 
			m_kod_add_button.setEnabled(false);
				
			m_part_add_button.setEnabled(false);
			 
			m_katalog_label.setEnabled(false);
			
			m_contractsCombo.setCurrentSQLId(dt.id_contract);
		}
		
		m_date.setSqlStartDate(m_data.date);
		
		m_date_doc.setSqlStartDate(m_data.date_doc);
		
		m_table.refreshData(m_data.id);
		
		add(createGUI());
		
		setMinimumSize(new Dimension(800,600));
			
		m_kod_table.clearQuantityColumn();
		
		setBounds(100, 100, 1200, 620);
		
		init();
		
		refreshSums(null);
		
	}

	/**
	 * Listener for buttons reaction
	 * @author Andrii Dashkov license GNU GPL v3
	 *
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
			if ( e.getSource() == m_kod_add_button) { insertData(); }
			
			if ( e.getSource() == m_part_add_button) { 
				
				newPartOpenDialog();

				init();
			}
			
		}
	}
	
	private void insertData() {
		
		Vector<WsPartType>  vec = m_kod_table.getSelectedData();
		
		if(vec.isEmpty()) {
			
			WsUtils.showMessageDialog(getMessagesStrs("emptyCatalogSelectionSuccess"));
			
			return;
			
		}
		
		insertCostFromCatalog(vec, m_contractsCombo.getCurrentSQLId());
		
		Vector<WsPrihodPartData> vec_ins = new Vector<WsPrihodPartData>();
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsPartType d = vec.elementAt(i);
			
			WsPrihodPartData d_ = new WsPrihodPartData();
			
			d_.id_invoice = -1;
			
			d_.id_part_type = d.id;
			
			d_.quantity = d.quantity;
			
			d_.rest = d.quantity;
			
			d_.name = d.name;
			
			d_.kod = d.kod;
			
			d_.vendorcode2 = String.valueOf(d.kod);
			
			d_.id_units = 3;
			
			d_.cost = d.costwithnds/m_nds_coeff;
			
			d_.nds = d.costwithnds - d_.cost;
			
			d_.costwithnds = d.costwithnds;
			
			//eggs workaround
			if(WsUtils.isKodEqual(d_.kod, WsUtils.EGG_KOD_1) || WsUtils.isKodEqual(d_.kod, WsUtils.EGG_KOD_2)) {
				
				d_.id_units = 1;
			}
			
			d_.units_name =(WsUtilSqlStatements.getUnitForId(d_.id_units)).name;
			
			vec_ins.add(d_);
			
		}
		
		
		m_table.setDataVector(vec_ins);
		
		refreshSums(null);
		
	}
	
	private void newPartOpenDialog() {
		
		
		WFNewPartDialog dialog = new WFNewPartDialog(WsUtils.get().getMainWindow(), null, 
				getGuiStrs("newPartKodDialogWinCaption"));
		
		dialog.setVisible(true);
			
	}
	
	public void onOK(ActionEvent e) {

		flag = WsCloseFlag.OK;
		
		m_data.date = m_date.getSqlStartDate();
		
		m_data.date_doc = m_date_doc.getSqlStartDate();
		
		m_data.number = m_number.getText();
		
		m_data.info = m_info.getText();
		
		m_data.id_counterparty = m_agent.getCurrentSQLId();
		
		m_data.id_contract = m_contractsCombo.getCurrentSQLId();
		
		Vector<WsPrihodPartData> vec = m_table.getParts();
		
		if(containNegative(vec)) {
			
			WsUtils.showMessageDialog(getMessagesStrs("negativeQuantityWarning"));
			
			return;
	
		}
		
		boolean successFlag = false;
		
		if(m_data.id == -1) {
		
			String operationMessage =  getMessagesStrs("newPrihodCreationFail");
			
			if(vec.isEmpty()) {

				WsUtils.showMessageDialog( getMessagesStrs("newNomenclatureForPrihodCreationSuccess"));
				
				return;
				
			}
			
			WsTransactions.beginTransaction(null);
			
			int inserted_id = WsPrihodSqlStatements.createNewPrihod(m_data, vec);
			
			if (inserted_id != -1) {
				
				WsTransactions.commitTransaction(null);
				
				operationMessage =  getMessagesStrs("newPrihodCreationSuccess");
				
				WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();
				
				ev.setRowId(inserted_id);
				
				WsEventDispatcher.get().fireCustomEvent(ev);
				
				successFlag = true;
		       
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
			}
			 
			WsUtils.showMessageDialog(operationMessage);
		}
		else {
			
			String operationMessage = getMessagesStrs("updatePrihodFaultWoringDate");
			
			if ( WsPrihodSqlStatements.isNewDateForUsedPrihodValid(m_data.id, m_data.date)) {
				
				operationMessage = getMessagesStrs("updatePrihodFault");
				
				Vector<WsPrihodPartData> parts_vec = m_table.getParts();
				
				if(WsPrihodSqlStatements.updatePrihod(m_data, parts_vec)) {
					
					operationMessage = getMessagesStrs("updatePrihodSuccess");
					
					
					for(WsPrihodPartData d: parts_vec) {
						
						if(d.kod_changed_flag) {
							
							WsPrihodSqlStatements.changePrihodKodForRashod(d.id, d.kod, d.name);
						}
					}
					
					WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();
					
					ev.setRowId(m_data.id);
					
					WsEventDispatcher.get().fireCustomEvent(ev);
					
					successFlag = true;
				}
				
			}
			
			WsUtils.showMessageDialog(operationMessage);
		}
	
		if(successFlag == true) {
			
			WsEventDispatcher.get().disconnect(this);
			
			WsEventDispatcher.get().disconnect(m_table);
			
			WsEventDispatcher.get().disconnect(m_kod_table);
			
			dispose();
		}

	}
	

	private boolean containNegative( Vector<WsPrihodPartData> vec ) {
		
		for(WsPrihodPartData d: vec) {
			
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
		
		WsEventDispatcher.get().disconnect(m_kod_table);
		
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
		
		m_date_label2 = new JLabel(getGuiStrs("prihodDateNewDialogLabel2"));
		
		m_info_label = new JLabel(getGuiStrs("infoPrihodNewDialogLabel"));
		
		m_agent_label = new JLabel(getGuiStrs("agentPrihodNewDialogLabel"));
		
		m_findKod = new WsFindKodField(new WsKodFindAction(m_kod_table));
		
		JPanel panel_table = WsGuiTools.createVerticalPanel();
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		south_right.add(m_infoLabel);
		
		south_right.add(panel_Ok_Cancel);
		
	    JScrollPane scroll = new JScrollPane(m_table);
	        
	    scroll.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	    scroll.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        
		panel_table.add(scroll);
		panel_table.add(getSumGuiPanel());
		
		JScrollPane scroll_kod = new JScrollPane(m_kod_table);
	        
	    scroll_kod.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	    scroll_kod.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        
	    JPanel panel_kod_table = WsGuiTools.createVerticalPanel();
	        
		panel_kod_table.add(scroll_kod);
		
		panel_MAIN.add(createGrid());
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_kod_label = WsGuiTools.createHorizontalPanel();
		
		panel_kod_label.add( m_katalog_label);
		
		panel_kod_label.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_kod_label.add(new JLabel(getGuiStrs("findKodLabelName")));
		
		panel_kod_label.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_kod_label.add(m_findKod);
		
		panel_kod_label.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_kod_label.add(m_kod_add_button);
		
		panel_kod_label.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_kod_label.add(m_part_add_button);
		
		panel_kod_label.add(Box.createHorizontalGlue());
		
		panel_kod_label.add(south_right);
		
		JPanel panel_kod = WsGuiTools.createVerticalPanel();
		
		panel_kod.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_kod.add(panel_kod_label);
		
		panel_kod.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_kod.add( scroll_kod);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                panel_table, panel_kod);
		
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setDividerLocation(150);
		
		JPanel panel_split = WsGuiTools.createHorizontalPanel();
		
		panel_split.add(splitPane);
		
		panel_MAIN.add(panel_split);
		
		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	
	private JPanel createGrid() {
		
		JPanel panel_date2 = WsGuiTools.createHorizontalPanel();
		
		panel_date2.add(m_date_label2); panel_date2.add(m_date_doc);
		
		JPanel panel_date = WsGuiTools.createHorizontalPanel();
		
		panel_date.add(m_date); 
		
		panel_date.add(Box.createHorizontalGlue());
		
		panel_date.add( panel_date2);
		
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
        
        c.gridx = 0;  c.gridy = 3;
        
        panel.add(new JLabel(getGuiStrs("contractsComboLabel")), c);
        
        c.gridx = 0;  c.gridy = 4;
        
        panel.add(m_info_label, c);

        c.weightx = 1;
        
        c.gridx = 1;  c.gridy = 0;
        
        panel.add(m_number, c);
		
        c.gridx = 1;  c.gridy = 1;
		
        panel.add(panel_date, c);
			
		c.gridx = 1;  c.gridy = 2;
		
		panel.add(m_agent, c);
		    
		c.gridx = 1;  c.gridy = 3;
		
		panel.add(m_contractsCombo, c);
		    
		c.gridx = 1;  c.gridy = 4;
		
		panel.add(m_info, c);

		return panel;
		
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_kod_add_button.addActionListener(forwarder);
		
		m_part_add_button.addActionListener(forwarder);

		PaFocusCustomListener fListener = new PaFocusCustomListener();
		
		m_number.addFocusListener(fListener);
		
		m_date.addFocusListener(fListener);
		
		m_info.addFocusListener(fListener);
		
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
	     });
	     
	     m_table.addFocusListener(new FocusAdapter() {
	         @Override
	         public void focusLost(FocusEvent e) {
	             verifyInfo();
	         }           
	     });
	     
	     
	     m_contractsCombo.addActionListener(new ActionListener() {
	   
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id_contract = m_contractsCombo.getCurrentSQLId();
				
				m_data.id_contract = id_contract;
				
				if(m_table.isEmpty()) { return; }
				
				int res = WsUtils.showYesNoDialog(getMessagesStrs("changePricesNewContr"));
				
				if(res == 1) {
				
					changePricesForContract(id_contract);
				
				}
				
			}           
	     });
	}
	
	public void verifyInfo() {
		
		boolean flag = true;
		
		if (m_date.getStartDate() == null || m_date_doc.getStartDate() == null) {
			
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
		public void focusLost(FocusEvent arg0) {
		
			 verifyInfo();
			
		}
	    
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
 
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		
		m_kod_add_button.setToolTipText(getGuiStrs("prihodKodAddButtonToolTip"));
		
		m_part_add_button.setToolTipText(getGuiStrs("prihodPartAddButtonToolTip"));
		
		m_table.setToolTipText(getGuiStrs("prihodPartTablePositionsToolTip"));
		
		m_agent.setToolTipText(getGuiStrs("prihodPartComboAgentToolTip"));
		 
	 }
	
	/**
	 * <p>Initiation function.Don't remove it! It must be, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
		
	    Vector<WsPartType> vec = WsUtilSqlStatements.getPartTypesList();
	    
	    for(WsPartType d: vec) {
	    	
	    	 m_catalog.put(d.kod, d);
	    }
	    	    
	    m_nds_coeff = WsUtils.getNdsCoeff();
	    
	    m_table.setNdsCoeff(m_nds_coeff);
	    
	    m_table.setCatalog(m_catalog);
		
	}
	
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		WsEventDispatcher.get().disconnect(m_table);
		
		WsEventDispatcher.get().disconnect(m_kod_table);
		
		WsEventDispatcher.get().disconnect(m_contractsCombo);
		
		WsEventDispatcher.get().disconnect(m_agent);
		
		super.dispose();
		
	}
	
	
	private void insertCostFromCatalog(Vector<WsPartType> vec, int id_contract) {
		
		if(id_contract == -1) { return; }
		
		HashMap<Integer, WsContractPriceData> prices_map = 
				WsContractsSqlStatements.getContractPriceListMap(id_contract);
		
		if(prices_map.isEmpty()) { return; }
		
		for(WsPartType d : vec ) {
			
			WsContractPriceData data = prices_map.get(d.kod);
			
			if(data != null) {
			
				d.costwithnds = data.costwnds;
			
			}
			
		}
	}
	
	private void changePricesForContract( int id_contract) {
		
		if(id_contract == -1) { return; }
		
		HashMap<Integer, WsContractPriceData> prices_map = 
				WsContractsSqlStatements.getContractPriceListMap(id_contract);
		
		if(prices_map.isEmpty()) { return; }

		Vector<WsPrihodPartData> vec = m_table.getParts();
		
		for(WsPrihodPartData d : vec ) {
			
			WsContractPriceData data = prices_map.get(d.kod);
			
			if(data != null) {
			
				d.costwithnds = data.costwnds;
				
				d.cost = d.costwithnds/m_nds_coeff;
				
				d.nds = d.costwithnds - d.cost;
			
			}
			
		}
		
		m_table.setDataVector(vec);
	}
	
	private JPanel getSumGuiPanel() {
		
		JPanel v = WsGuiTools.createHorizontalPanel();
		
		v.add(Box.createHorizontalGlue());
		
		v.add(new JLabel(getGuiStrs("sumWithoutNdsLabel") + " : "));
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(m_sum);
		
		v.add(Box.createHorizontalStrut(HOR_STRUT));
		
		v.add(new JLabel(getGuiStrs("sumNdsLabel") + " : "));
		
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
			
		}
		
	}
}
