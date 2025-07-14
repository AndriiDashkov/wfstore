
package wsdialogs;


import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import wsmain.WsGuiTools;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsMessageDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	JButton m_ok = new JButton(getGuiStrs("closeButCap"));
	
	JLabel m_text = new JLabel("");
	
	Component m_parent = null;
	
	public WsMessageDialog(JFrame parent, String text) {
		
		super(parent, getMessagesStrs("messageInfoCaption"), true);
		
		m_parent = parent;
		
		m_text.setText(text);
		
		 
		setActionListeners();
		
		JPanel main = WsGuiTools.createVerticalPanel();
		
		JPanel p_label = WsGuiTools.createHorizontalPanel();
		
		JPanel p_buttons = WsGuiTools.createHorizontalPanel();
		
		p_buttons.add(Box.createHorizontalGlue());
		
		p_buttons.add(m_ok);
		
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
		
		setCustomFont();
		
		pack();
		
		setLocationRelativeTo( m_parent);
			
	}
	
	private void setActionListeners() {
		
		m_ok.addActionListener(new ActionListener() {
			
	        public void actionPerformed(ActionEvent e) {
	         
	        	dispose();
	         
	        }
	        
		});
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}
}