package WsDialogs;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
import static WsMain.WsUtils.*;
import java.awt.Cursor;
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
import WsControls.WsContractsComboBox;
import WsEvents.WsEventDispatcher;
import WsForms.WsPrihodForm;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


public class  WsContractGroupChangeDialog extends JDialog  {
	
	private static final long serialVersionUID = 1L;

	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private WsContractsComboBox m_contractCombo = new WsContractsComboBox();
	
	public JButton m_startButton;

	JCheckBox m_checkRashod = new JCheckBox(getGuiStrs("changeRashodCheckBox"));
	
	JLabel m_combo_label = new JLabel(getGuiStrs("newContractLabel"));
	
	WsPrihodForm parent = null;
	
	public  WsContractGroupChangeDialog(JFrame jfrm, WsPrihodForm p, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		parent = p;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(createGUI());
		
		setBounds(250, 150, 225, 150);

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
			
			if ( e.getSource() == m_startButton ) { onOK(e); }
			
					
		}
	}
	
	public void onOK(ActionEvent e) {

		flag = WsCloseFlag.OK;
			
		int res = WsUtils.showYesNoDialog( getMessagesStrs("startContrChangeCaption"));
	      	   
		if ( 1 == res) {
	
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			int processed = parent.changeContract( m_contractCombo.getCurrentSQLId(), m_checkRashod.isSelected() );

			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
 		    JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("endCnCganeMessage") + " " + String.valueOf(processed),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
					
			dispose();
		
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
		
		m_startButton = new JButton(getGuiStrs("buttonStartConrChCaption"));
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel1 = WsGuiTools.createHorizontalPanel();
		
		panel1.add( m_combo_label );   panel1.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel1.add(m_contractCombo );   
		
		JPanel panel2 = WsGuiTools.createHorizontalPanel();
		
		panel2.add( m_checkRashod);   panel2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		JPanel panel_start = WsGuiTools.createHorizontalPanel();
		
		panel_start.add(m_startButton);   panel_start.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panel1);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel2);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_start);
		
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
		 	 	  
		m_startButton.setToolTipText(getGuiStrs("importDBButtonToolTip"));
		
		m_checkRashod.setToolTipText(getGuiStrs("changeContrCheckToolTip"));
		 
	 }
	 

	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
				
		super.dispose();
			
	}
}

