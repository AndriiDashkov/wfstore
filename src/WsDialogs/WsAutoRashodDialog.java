
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsControls.WsPartTypesComboBox;
import WsDatabase.WsRashodSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAutoRashodDialog  extends JDialog  {
	
	private static final long serialVersionUID = 1L;

	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	public JButton m_startButton;

	protected JLabel m_comboPartTypeLabel = new JLabel(getGuiStrs("partTypeFilterComboBoxLabel"));
	
	protected WsPartTypesComboBox m_partTypesCombo = new  WsPartTypesComboBox();
	
	Ws2DatesControl  m_date = new Ws2DatesControl(false);
	
	JCheckBox m_checkPeople = new JCheckBox(getGuiStrs("usePeopleForAutoRashodCheckBox"));
	
	private static java.sql.Date m_start_date = null;
	
	private static java.sql.Date m_end_date = null;
	
	public WsAutoRashodDialog (JFrame jfrm, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(createGUI());
		
		setBounds(250, 150, 225, 150);
		
		init();

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
		
		int res = WsUtils.showYesNoDialog( getMessagesStrs("startAutoPrihodmessageCaption"));
	      	   
		if ( 1 == res) {
		
			int kod = m_partTypesCombo.getSelectedPartData().kod;
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			m_start_date =  m_date.getSqlStartDate();
			
			m_end_date = m_date.getSqlEndDate();
			
			String operationMessage = WsRashodSqlStatements.transferAllPrihodIntoRashod(kod,  
					 m_date.getSqlStartDate(), m_date.getSqlEndDate() , 
					m_checkPeople.isSelected());
			
			if(operationMessage == null) {
				
				WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();

				WsEventDispatcher.get().fireCustomEvent(ev);
				
				WsRashodInvoiceChangedEvent ev1 = new WsRashodInvoiceChangedEvent();
					
				WsEventDispatcher.get().fireCustomEvent(ev1);
				
				operationMessage =  getMessagesStrs("autoRashodSuccessMessage");
			}
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	
				
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    operationMessage,
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			//dispose();
		
		}
		
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
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		JPanel panel_1 = WsGuiTools.createHorizontalPanel();
		
		JPanel panel_2 = WsGuiTools.createHorizontalPanel();
		
		JPanel panel_3 = WsGuiTools.createHorizontalPanel();
		
		panel_1.add(m_comboPartTypeLabel);  panel_1.add(m_partTypesCombo); panel_1.add(Box.createHorizontalGlue());
		
		panel_2.add(m_date);  panel_2.add(Box.createHorizontalGlue());
		
		panel_3.add(m_checkPeople);  panel_3.add(Box.createHorizontalGlue());
	
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_startButton = new JButton(getGuiStrs("buttonStartAutoRashodCaption"));
		
		panel_Ok_Cancel.add(m_startButton);
		
		south_right.add(panel_Ok_Cancel);
					
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_1);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));	
		
		panel_MAIN.add(panel_2);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_3);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(south_right);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		setAllListeners();
		
		setToolTips();
		
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
		 	 	  
		m_startButton.setToolTipText(getGuiStrs("autoRashodButtonToolTip"));
		 
	 }
	 

	 
	/**
	 * <p>Initiation function.Don't remove it!  
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
		
		if(m_end_date == null) {
			
			m_date.setCurrentEndDate();
		}
		else {
			
			m_date.setSqlEndDate(m_end_date);
			
		}
		
		if(m_start_date == null) {
			
			m_date.setCurrentStartDate();
		}
		else {
			
			m_date.setSqlStartDate(m_start_date);
		}
		
		
	}

	
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		WsEventDispatcher.get().disconnect(m_partTypesCombo);
		
		super.dispose();
		
	}
}
