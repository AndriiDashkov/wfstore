
package WsDialogs;

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
import WsControls.WsPartTypesComboBox;
import WsControls.WsUnitsComboBox;
import WsDataStruct.WsContractPriceData;
import WsDataStruct.WsPartType;
import WsDatabase.WsContractsSqlStatements;
import WsDatabase.WsTransactions;
import WsEvents.WsContractChangeEvent;
import WsEvents.WsEventDispatcher;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewContractPriceDialog extends JDialog {
	
	{
	
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshSums");
	
	}
	private static final long serialVersionUID = 1L;
	
	WsPartTypesComboBox m_combo_parts = new WsPartTypesComboBox();
	
	WsUnitsComboBox m_combo_units = new WsUnitsComboBox();
	
	protected JTextField m_cost = new JTextField(25);
	
	protected JTextField m_nds = new JTextField(25);
	
	protected JTextField m_costwnds = new JTextField(25);
	
	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_combo_label;
	
	protected JLabel m_cost_label;

	protected JLabel m_nds_label;
	
	protected JLabel m_costwnds_label;
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected ButtonGroup butGroup = new ButtonGroup(); 
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;
	
	private WsContractPriceData m_data = null;
	
	
	public WsNewContractPriceDialog(JFrame jfrm, int id_contract, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		m_data = new WsContractPriceData();
		
		add(createGUI());
		
		setBounds(250, 150, 500, 300);
		
		init();
		
		pack();
		
		m_data.id_contract = id_contract;
		
		WsPartType pt = m_combo_parts.getSelectedPartData();
		
		m_data.id_part_type = pt.id;
		
		m_data.kod = pt.kod;
		
		m_data.name = pt.name;
		
		m_data.id_units = m_combo_units.getCurrentSQLId();
		
		m_data.cost = 0.0;
		
		m_data.nds = 0.0;
		
		m_data.costwnds = 0.0;
		
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

		WsPartType pt = m_combo_parts.getSelectedPartData();
		
		m_data.id_part_type = pt.id;
		
		m_data.kod = pt.kod;
		
		m_data.name = pt.name;
		
		m_data.id_units = m_combo_units.getCurrentSQLId();
		
		try {
			
			m_data.cost = Double.valueOf(m_cost.getText());
			
			m_data.nds = Double.valueOf(m_nds.getText());
			
			m_data.costwnds = Double.valueOf(m_costwnds.getText());
		
		}
		catch(java.lang.NumberFormatException ex) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("wrongFromatNum"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
		}
	
		boolean successFlag = false;
		
		String operationMessage =  getMessagesStrs("newContractPriceCreationFail");
		
		WsTransactions.beginTransaction(null);
		
		int inserted_id = WsContractsSqlStatements.createNewPrice(m_data);
		
		if (inserted_id != -1) {
			
			WsTransactions.commitTransaction(null);
			
			operationMessage =  getMessagesStrs("newContractPriceCreationSuccess");
			
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
		
		JPanel panel_top = WsGuiTools.createHorizontalPanel();
		
		panel_top.add(new JLabel(getGuiStrs("catPosLabel")+ " "));
		
		panel_top.add(m_combo_parts);
		
		panel_top.add(new JLabel(getGuiStrs("unitsPosLabel")+ " "));
		
		panel_top.add( m_combo_units);
		
		panel_top.add(Box.createHorizontalGlue());
		
		JPanel panel_cost = WsGuiTools.createHorizontalPanel();
		
		panel_cost.add( new JLabel(getGuiStrs("varNoNdsLabel")+ " "));
		
		panel_cost.add( m_cost);
		
		panel_cost.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_cost.add( new JLabel(getGuiStrs("prihodPartsColumnNdsName")+ " "));
		
		panel_cost.add(m_nds);
		
		panel_cost.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_cost.add( new JLabel(getGuiStrs("varWithNdsLabel")+ " "));
		
		panel_cost.add(m_costwnds);
		
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
		
		panel_MAIN.add(panel_top);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_cost);
		
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
	
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	
	}
	
	public void verifyInfo() {
		
	   boolean flag = true;
				
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