
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentComboBox;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsEvents.WsEventDispatcher;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPrihodInRashodDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_number_label;
	
	protected JLabel m_date_label;
	
	protected JLabel m_agent_label;

	protected JLabel m_info_label;
	
	protected JTextField m_number = new JTextField(25);
	
	DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	protected JTextField m_info = new JTextField(25);
	
	protected WsAgentComboBox m_agent =  new WsAgentComboBox();
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;
	
	Vector<Integer> m_data = null;
	
	public WsPrihodInRashodDialog(JFrame jfrm, Vector<Integer> dt, String nameFrame) {
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	    m_data = dt;
			
		m_number.setText(" "); 
		
		m_number.setBorder(BorderFactory.createLoweredBevelBorder());
				
		m_date.setSqlStartDate(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
		
		add(createGUI());

		pack();
		
		
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
						
		}
	}


	
	public void onOK(ActionEvent e) {
		
		if( !verifyInfo()) { return; }

		flag = WsCloseFlag.OK;
		
		WsRashodData d_r = new WsRashodData();
		
		d_r.date = m_date.getSqlStartDate();

		d_r.number = m_number.getText();
		
		d_r.id_counterparty = m_agent.getSelectedAgentData().id;
		
		Vector<WsRashodPartData> r_data = new 	Vector<WsRashodPartData>();
		
		Vector<WsPrihodPartData> l_data = WsPrihodSqlStatements.getPrihodPartsListForIds(
				m_data, 0);
		
		for(int i = 0; i <  l_data.size(); ++i) {
			
			WsPrihodPartData d_p =  l_data.elementAt(i);
			
			if(d_p.rest > WsUtils.getRZL()) {
			
				WsRashodPartData d_ins = new WsRashodPartData();
				
				d_ins.id_invoice_parts = d_p.id;
				
				d_ins.id_units = d_p.id_units;
				
				d_ins.kod = d_p.kod;
				
				d_ins.name = d_p.name;
				
				d_ins.quantity = d_p.rest;
				
				d_ins.req_quantity = d_p.rest;
				
				d_ins.vendor_code_2 = d_p.vendorcode2;
				
				d_ins.cost = d_p.cost;
				
				d_ins.nds = d_p.nds;
				
				d_ins.costwithnds = d_p.costwithnds;
				
				r_data.add(d_ins);
			}
			
		}
			
		int res = WsUtils.showYesNoDialog( getMessagesStrs("makeAllPrihodIntoRashodMessage"));
		      	   
		if ( 1 == res) {
   			
   			setCursor(new Cursor(Cursor.WAIT_CURSOR));
   			
			WsTransactions.beginTransaction(null);
			
			int inserted_id = WsRashodSqlStatements.createNewRashod(d_r, r_data);
			
			String operationMessage =  "";
			
			boolean successFlag = false;
			
			if (inserted_id != -1) {
				
				WsTransactions.commitTransaction(null);
				
				operationMessage =  getMessagesStrs("newRashodCreationSuccess");
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				operationMessage =  getMessagesStrs("newRashodCreationSuccess");
				
				WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
				
				ev.setRowId(inserted_id);
				
				WsEventDispatcher.get().fireCustomEvent(ev);
				
				successFlag = true;
		       
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				operationMessage =  getMessagesStrs("newRashodCreationFail");
	
			}
			
			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			    operationMessage,
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
			
			if(successFlag == true) {
				
				WsEventDispatcher.get().disconnect(this);
						
				dispose();
			}
   		
   		}
	


	}
	/**
	 * Cancel button reaction
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		flag = WsCloseFlag.CANCEL;
	
		WsEventDispatcher.get().disconnect(this);
		
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
		
		m_number_label = new JLabel(getGuiStrs("rashodNumberNewDialogLabel"));
		
		m_date_label = new JLabel(getGuiStrs("prihodDateNewDialogLabel"));
		
		m_agent_label = new JLabel(getGuiStrs("agentRashodNewDialogLabel"));
		
		JPanel panel_1 = new JPanel();
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		JPanel panel_date = WsGuiTools.createHorizontalPanel();
		
		panel_date.add(m_date); panel_date.add(Box.createHorizontalGlue());
	
		GridLayout gridLayout = new GridLayout(0,2);

		panel_1.setLayout(gridLayout);

		panel_1.add(m_number_label);    panel_1.add(m_number);
		
		panel_1.add(m_date_label);    panel_1.add(panel_date);
		
		panel_1.add(m_agent_label);    panel_1.add(m_agent);
	
		WsUtils.get().setFixedSizeBehavior(m_agent);
		
		WsUtils.get().setFixedSizeBehavior(m_number);
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		south_right.add(m_infoLabel);
		
		south_right.add(panel_Ok_Cancel);
				
		panel_MAIN.add(panel_1);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
			
		panel_MAIN.add(south_right);
		
		panel_MAIN.add(Box.createVerticalGlue());
		
		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);

		PaFocusCustomListener fListener = new PaFocusCustomListener();
		
		m_number.addFocusListener(fListener);
		
		m_date.addFocusListener(fListener);
		
		m_info.addFocusListener(fListener);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	

	}
	
	public boolean verifyInfo() {
		
		boolean flag = true;
		
		if (m_number.getText().equals("")) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("noEmptyNumberOrderNameMessage")); 
			
		}
		
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
		
		return flag;
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
		
		m_agent.setToolTipText(getGuiStrs("prihodPartComboAgentToolTip"));

		 
	 }
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
			
		super.dispose();
		
	}
}
