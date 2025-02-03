
package WsDialogs;

import static WsMain.WsUtils.DATE_FORMAT;
import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Color;
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
import WsDataStruct.WsContractData;
import WsDatabase.WsContractsSqlStatements;
import WsDatabase.WsTransactions;
import WsEvents.WsContractChangeEvent;
import WsEvents.WsEventDispatcher;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewContractDialog extends JDialog {
	
	{
	
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshSums");
	
	}
	private static final long serialVersionUID = 1L;
	
	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_number_label;
	
	protected JLabel m_date_label;

	protected JLabel m_info_label;
	
	protected JTextField m_number = new JTextField(25);
	
	DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	protected JTextField m_info = new JTextField(25);
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;
	
	WsContractData m_data = null;
	
	public WsNewContractDialog(JFrame jfrm, WsContractData dt, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		m_data = new WsContractData();
		
		m_data.date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		
		//edit mode
		if(dt != null) {
			
			m_data = dt;
			
			m_number.setText(dt.number); m_number.setBorder(BorderFactory.createLoweredBevelBorder());
			
			m_info.setText(dt.info); m_info.setBorder(BorderFactory.createLoweredBevelBorder());	
			
		}
		
		m_date.setSqlStartDate(m_data.date);
		
		add(createGUI());
		
		setBounds(250, 150, 500, 300);
		
		init();
		
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

		flag = WsCloseFlag.OK;
		
		m_data.date = m_date.getSqlStartDate();
		
		m_data.number = m_number.getText();
		
		m_data.info = m_info.getText();
		
		boolean successFlag = false;
		
		if(m_data.id == -1) {
		
			String operationMessage =  getMessagesStrs("newContractCreationFail");
			
			WsTransactions.beginTransaction(null);
			
			int inserted_id = WsContractsSqlStatements.createNewContract(m_data);
			
			if (inserted_id != -1) {
				
				WsTransactions.commitTransaction(null);
				
				operationMessage =  getMessagesStrs("newContractCreationSuccess");
				
				WsContractChangeEvent ev = new  WsContractChangeEvent();
				
				ev.setRowId(inserted_id);
				
				WsEventDispatcher.get().fireCustomEvent(ev);
				
				successFlag = true;
		       
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
			}
			 
			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			    operationMessage,
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
		}
		else {
			

		    String operationMessage = getMessagesStrs("updateContractFault");
				

			if( WsContractsSqlStatements.updateContract(m_data ) ) {
					
					operationMessage = getMessagesStrs("updateContractSuccess");
					
					WsContractChangeEvent ev = new WsContractChangeEvent();
					
					ev.setRowId(m_data.id);
					
					WsEventDispatcher.get().fireCustomEvent(ev);
					
					successFlag = true;
			}

			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			    operationMessage,
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
		}
	
		if(successFlag == true) {
			
			WsEventDispatcher.get().disconnect(this);
			
			dispose();
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
		
		m_number_label = new JLabel(getGuiStrs("prihodNumberNewDialogLabel")+ " ");
		
		m_date_label = new JLabel(getGuiStrs("prihodDateNewDialogLabel") + " ");
		
		m_info_label = new JLabel(getGuiStrs("infoPrihodNewDialogLabel") + " ");
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		JPanel panel_top = WsGuiTools.createHorizontalPanel();
		
		panel_top.add(m_number_label);    panel_top.add(m_number);
		
		panel_top.add(m_date_label);    panel_top.add(m_date);
		
		JPanel panel_mid = WsGuiTools.createHorizontalPanel();
		
		panel_mid.add(m_info_label);    panel_mid.add(m_info);
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		south_right.add(m_infoLabel);
		
		south_right.add(panel_Ok_Cancel);
		
		panel_MAIN.add(panel_top);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_mid);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(south_right);
		
		panel_MAIN.add(Box.createVerticalGlue());
		
		WsGuiTools.fixComponentHeightToMin(m_number);
		
		WsGuiTools.fixComponentHeightToMin(m_info);
		
		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		FocusCustomListener fListener = new FocusCustomListener();
		
		m_number.addFocusListener(fListener);
		
		m_date.addFocusListener(fListener);
		
		m_info.addFocusListener(fListener);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	
	}
	
	public void verifyInfo() {
		
		boolean flag = true;
		
		if (m_date.getStartDate() == null ) {
			
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
	
	
	class FocusCustomListener implements FocusListener
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
			 
	 }
	
	/**
	 * <p>Initiation function.Don't remove it! It must be, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
		
		
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
				
		super.dispose();
		
	}
	
}
