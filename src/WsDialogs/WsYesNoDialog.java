
package WsDialogs;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import WsDataStruct.WsReturnResult;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsYesNoDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	JButton m_yes = new JButton(getGuiStrs("takResponse"));
	
	JButton m_no = new JButton(getGuiStrs("niRespose"));
	
	JLabel m_text = new JLabel("");
	
	WsReturnResult m_result = null;
	
	Component m_parent = null;
	
	public WsYesNoDialog(JFrame parent, String text, WsReturnResult result) {
		
		super(parent, getMessagesStrs("messageAnswerCaption"), true);
		
		m_parent = parent;
		
		m_text.setText(text);
		
		m_result = result;
		
		m_result.result = 0;
		 
		setActionListeners();
		
		JPanel main = WsGuiTools.createVerticalPanel();
		
		JPanel p_label = WsGuiTools.createHorizontalPanel();
		
		JPanel p_buttons = WsGuiTools.createHorizontalPanel();
		
		p_buttons.add(Box.createHorizontalGlue());
		
		p_buttons.add(m_yes);
		
		p_buttons.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		p_buttons.add(m_no);
		
		p_buttons.add(Box.createHorizontalGlue());
		
		p_label.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT*2));
		
		p_label.add(Box.createHorizontalGlue());
		
		p_label.add(m_text);
		
		p_label.add(Box.createHorizontalGlue());
		
		p_label.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT*2));
		
		main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		main.add(p_label);
		
		main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT*2));
		
		main.add(p_buttons);
		
		main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		add(main);
		
		pack();
		
		Dimension d = m_no.getPreferredSize();
		
		m_yes.setMaximumSize(d);
		
		m_no.setMaximumSize(d);
		
		pack();
		
		setLocationRelativeTo( m_parent);
		
	}
	
	private void setActionListeners() {
		
		m_yes.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	m_result.result = 1;
	        	
	        	dispose();
	         
	        }
	        
		});
		
		m_no.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	m_result.result = 0;
	        	
	        	dispose();
	         
	        }
	        
		});
		
	}
}
