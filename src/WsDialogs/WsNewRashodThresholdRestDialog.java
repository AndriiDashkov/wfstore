/**
 * 
 */
package WsDialogs;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentComboBox;
import WsControls.WsContractsFilterComboBox;
import WsControls.WsPartTypesFilterComboBox;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsEvents.WsEventDispatcher;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

public class WsNewRashodThresholdRestDialog  extends JDialog  {
	
	private static final long serialVersionUID = 1L;

	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	public JButton m_startButton;

	protected JLabel m_comboPartTypeLabel = new JLabel(getGuiStrs("partTypeFilterComboBoxLabel"));
	
	protected WsPartTypesFilterComboBox m_partTypesCombo = new WsPartTypesFilterComboBox();
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WsContractsFilterComboBox m_contractsCombo =  new WsContractsFilterComboBox();
	
	WsAgentComboBox m_agentsCombo = new WsAgentComboBox();
	
	JFormattedTextField m_threshold = null;
	
	JTextField m_number = new JTextField(30);
	
	private static java.sql.Date m_start_date = null;
	
	TitledBorder m_title = null;
	
	public WsNewRashodThresholdRestDialog (JFrame jfrm, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(createGUI());
		
		//setBounds(250, 150, 225, 150);
		
		setLocation(250, 150);
		
		init();
		
		setCustomFont();

		pack();
		
		setResizable(false);
		
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_startButton ) onOK(e);
			
		}
	}
	
	public void onOK(ActionEvent e) {

		flag = WsCloseFlag.OK;
		
		int res = WsUtils.showYesNoDialogLong(getMessagesStrs("thresRashodAtuQ0"),
				getMessagesStrs("thresRashodAtuQ1"));
	   	   
		if ( 1 != res) { return; }

		setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
		int createdNakls = createNakl();
			
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	
		WsUtils.showMessageDialog(String.valueOf(createdNakls) + " " +
   			    getMessagesStrs("raskladkaNaklsNumberCreatedMessage"));
			
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
		
		m_threshold = new JFormattedTextField(NumberFormat.getNumberInstance());

		m_threshold.setValue(Double.valueOf(0.001));
		
		m_threshold.setColumns(10);
		
		WsGuiTools.setComponentFixedWidth(m_partTypesCombo, 200);
		
		WsGuiTools.fixComponentHeightToMin(m_partTypesCombo);
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		JPanel p_0 = new JPanel(new GridLayout(0,2));
		
		JLabel l = new JLabel(getGuiStrs("newRashodOrderNumberNewDialogLabel"));
		
		p_0.add(l);
		
		p_0.add(m_number);
		
		p_0.add(new JLabel(getGuiStrs("prihodDateNewDialogLabel")));
		
		p_0.add(m_date);
		
		p_0.add(new JLabel(getGuiStrs("labeAgentOtrComboCaption")));
		
		p_0.add(m_agentsCombo);
		
		JPanel panel_1 =  new JPanel(new GridLayout(0,2));
		
		panel_1.add(new JLabel(getGuiStrs("contractsComboLabel")));  
		
		panel_1.add(m_contractsCombo);
		
		panel_1.add(m_comboPartTypeLabel);  
			
		panel_1.add(m_partTypesCombo); 
		
		panel_1.add(new JLabel(getGuiStrs("kilThreshold")));  
		
		panel_1.add(m_threshold); 

		m_title = BorderFactory.createTitledBorder(getGuiStrs("thrRashBorderTitle"));
		
		panel_1.setBorder(m_title);
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_startButton = new JButton(getGuiStrs("buttonStartAutoRashodCaption"));
		
		panel_Ok_Cancel.add(m_startButton);
		
		south_right.add(panel_Ok_Cancel);
		
		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		panel_MAIN.add(p_0);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_1);
							
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));	
		
		panel_MAIN.add(south_right);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		setAllListeners();
		
		setToolTips();
		
		setCustomFont();
		
		return panel_MAIN;
		
	}
	
	private void setAllListeners() {
		
		m_startButton.addActionListener(forwarder);
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});

	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 	 	  
		m_startButton.setToolTipText(getMessagesStrs("thresRashodButtonToolTip"));
		 
	 }

	/**
	 * <p>Initiation function.Don't remove it!  
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
		
		if(m_start_date == null) {
			
			m_date.setCurrentStartDate();
		}
		else {
			
			m_date.setSqlStartDate(m_start_date);
		}
		
		m_partTypesCombo.refreshModel(null);
		 
		m_contractsCombo.refreshModel(null);
		
		
	}

	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		WsEventDispatcher.get().disconnect(m_partTypesCombo);
		
		WsEventDispatcher.get().disconnect(m_contractsCombo);
		
		WsEventDispatcher.get().disconnect(m_agentsCombo);
		
		super.dispose();
		
	}
	
	public int createNakl() {
		

		WsRashodData data = new WsRashodData(); 
		
		data.id_counterparty = m_agentsCombo.getCurrentSQLId();
		
		data.date =  m_date.getSqlStartDate();
			
		data.number = m_number.getText();
		
		int createdNakls = 0;
		
		Vector<WsRashodPartData> vec_ins = WsRashodSqlStatements.findSkladPositionsForRashodThreshold(data.date, 
				 m_contractsCombo.getCurrentSQLId(), m_partTypesCombo.getCurrentSQLId(), 
				 (Double)m_threshold.getValue());
		
		
		WsTransactions.beginTransaction(null);
		
		if( WsRashodSqlStatements.createNewRashod(data, vec_ins) != -1) {
			
			WsTransactions.commitTransaction(null);
			
			createdNakls++;
		}
		else {
			
			WsTransactions.rollbackTransaction(null);
			
		}
			
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
		
		ev.setRowId(-1);
		
		WsEventDispatcher.get().fireCustomEvent(ev);
		
		return createdNakls;
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
		
		m_title.setTitleFont(f);
			
	}
}
