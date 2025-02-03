
package WsDialogs;

import static WsMain.WsUtils.*;
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
import WsControls.WsAgentTypesComboBox;
import WsDataStruct.WsAgentData;
import WsDatabase.WSAgentSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsTokenizer;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewAgentDialog extends JDialog  {
	
	private static final long serialVersionUID = 1L;

	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_name_label;
	
	protected JLabel m_type_combo_label;
	
	protected JLabel m_contact_label;
	
	protected JLabel m_info_label;
	
	protected WsAgentTypesComboBox m_type_combo = new WsAgentTypesComboBox(false);
	
	protected JTextField m_name = new JTextField(25);
	
	protected JTextField m_contact = new JTextField(25);
	
	protected JTextField m_info = new JTextField(25);
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;

	WsAgentData m_data = null;
	

	public WsNewAgentDialog(JFrame jfrm, WsAgentData dt, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		m_data = new WsAgentData();
		
		//edit mode
		if(dt != null) {
			
			m_data = dt;
			
			m_name.setText(dt.name); m_name.setBorder(BorderFactory.createLoweredBevelBorder());
			
			m_contact.setText(dt.contact); m_contact.setBorder(BorderFactory.createLoweredBevelBorder());
			
			m_info.setText(dt.info); m_info.setBorder(BorderFactory.createLoweredBevelBorder());
			
			m_type_combo.setCurrentSQLId(dt.id_type);
		}
		
		
		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		init();

		pack();
		
		setResizable(false);
		
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
		
		m_data.id_type = m_type_combo.getCurrentSQLId();
		
		m_data.name = m_name.getText();
		
		m_data.contact = m_contact.getText();
		
		m_data.info = m_info.getText();
		
		if (m_data.id == -1) {
			
			String operationMessage = getMessagesStrs("newAgentCreationFault");
			
			if (WSAgentSqlStatements.createNewAgent(m_data)) {
				
				operationMessage = getMessagesStrs("newAgentCreationSuccess");
	          
			}
			
			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			    operationMessage,
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
		}
		else {
			
			String operationMessage = getMessagesStrs("updateAgentFault");
			
			if(WSAgentSqlStatements.updateAgent(m_data)) {
				
				operationMessage = getMessagesStrs("updateAgentSuccess");
			}
			
			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			    operationMessage,
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
		}
		
		
		WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.AGENTS_DATA_CHANGED);
		
		WsEventDispatcher.get().fireCustomEvent(event);
		
		dispose();

	}
	/**
	 * Cancel button reaction
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		flag = WsCloseFlag.CANCEL;
		
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
		
		m_name_label = new JLabel(getGuiStrs("agentNameNewDialogLabel"));
		
		m_type_combo_label = new JLabel(getGuiStrs("typeAgentNewDialogLabel"));
		
		m_contact_label = new JLabel(getGuiStrs("contactAgentNewDialogLabel"));
		
		m_info_label = new JLabel(getGuiStrs("infoAgentNewDialogLabel"));
		
		JPanel panel_1 = new JPanel();
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		GridLayout gridLayout = new GridLayout(0,2);

		panel_1.setLayout(gridLayout);

		panel_1.add(m_name_label);    panel_1.add(m_name);
		
		panel_1.add(m_type_combo_label);    panel_1.add(m_type_combo);
		
		panel_1.add(m_contact_label);    panel_1.add(m_contact);
		
		panel_1.add(m_info_label);    panel_1.add(m_info);
		
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

		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);

		PaFocusCustomListener fListener = new PaFocusCustomListener();
		
		m_name.addFocusListener(fListener);
		
		m_contact.addFocusListener(fListener);
		
		m_info.addFocusListener(fListener);
		

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	
		
	}
	


	public void verifyInfo() {
		
		boolean flag = true;
		
		if (m_name.getText().equals("")) {
			
			flag = false;
			
			m_infoLabel.setText(getMessagesStrs("noEmptyNameMessage")); 
			
		}
		
		if ( WsTokenizer.isValidate(m_name.getText()) == false ||
				WsTokenizer.isValidate( m_info.getText()) == false || WsTokenizer.isValidate( m_contact.getText()) == false) {
			
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
		 
	 }
	 

	 
	/**
	 * <p>Initiation function.Don't remove it! It must be empty, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {}

	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		WsEventDispatcher.get().disconnect(m_type_combo);
		
		super.dispose();
	
	}
}
