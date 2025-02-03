/**
 * 
 */
package WsDialogs;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import WsEvents.WsEventDispatcher;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


public class WsFindDialog<T> extends JDialog {
	
	private static final long serialVersionUID = 1L;

	T m_parent = null;
	
	protected  JButton m_findButton = new JButton(getGuiStrs("buttonFindCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	
	JLabel label_number = new JLabel (getGuiStrs("textToFindNumberLabel"));
	
	protected JTextField m_number = new JTextField(25);
	

	JRadioButton m_radio_info = new JRadioButton(getGuiStrs("radioInfoCaption"));
	
	JRadioButton m_radio_nomer =  new JRadioButton(getGuiStrs("radioNomerCaption"));
	
	ButtonGroup m_radio_group = new ButtonGroup();

	public WsFindDialog(JFrame jf, T parent, String caption) {
		
		super (jf, caption, true);
		
		m_parent = parent;
		
		init();
		
	}
	
	public void init() {
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		m_radio_nomer.setSelected(true);
		
		setToolTips();
		
		pack();
		
		setLocation(100,100);
		
	}
	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("wFBorderTitle"));
		
		JPanel find_panel = WsGuiTools.createHorizontalPanel();
		
		find_panel.setBorder(title);
		
		find_panel.add(m_radio_nomer);
		
		find_panel.add(m_radio_info);
		
		find_panel.add(Box.createHorizontalGlue());
		
		JPanel number_panel = WsGuiTools.createHorizontalPanel();
		
		number_panel.add(label_number);
		
		number_panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		number_panel.add(m_number);
				
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_findButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.add(find_panel);
		
		panel_main.add(number_panel);
		
		panel_main.add(panel_button);
		
		add(panel_main);
		

		m_findButton.addActionListener( new  Forwarder());
		
		m_radio_group.add(this.m_radio_nomer); 
		
		m_radio_group.add(this.m_radio_info); 
		

		
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_findButton )  {
				
				String s  = m_number.getText();
				
				if(null != s && !s.isEmpty() ) {
					
					int flag = 0;
					
					if(m_radio_info.isSelected()) {
						
						flag = 1;
					}
					
					Method m;
					
					try {
						
						m = m_parent.getClass().getMethod("findNumberOrInfo", String.class, int.class);
						
						m.invoke(m_parent, s, flag);
					
						 
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
						
						e1.printStackTrace();
					} 
	 
				}
				else {
					
					 JOptionPane.showMessageDialog(
				   			    WsUtils.get().getMainWindow(),
				   			    getMessagesStrs("cantFindEmptyTextMessage"),
				   			    getMessagesStrs("messageInfoCaption"),
				   			    JOptionPane.CLOSED_OPTION);
					
				}
			}

			
		}
	}
	
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();

	}
	
	private void setToolTips() {
		

		m_findButton.setToolTipText(getGuiStrs("startFindToolTip"));
		
		m_radio_nomer.setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_radio_info.setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
			
	}
	
}
