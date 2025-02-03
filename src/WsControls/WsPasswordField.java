
package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPasswordField  extends JPanel{

	private static final long serialVersionUID = 1L;

	protected WsPassTextField m_password = new WsPassTextField(false, 25);
	
	protected WsPassTextField m_password2 = new WsPassTextField(false, 25);
	
	protected JCheckBox m_check = new JCheckBox(getGuiStrs("checkBoxShowPass"));
	
	private Font m_font = WsUtils.get().getBaseFont();
	
	boolean m_onlyOneField = false;
	
	ActionListener m_listener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			m_password.setShowPass(m_check.isSelected());
			
			if(!m_onlyOneField) {
				
				m_password2.setShowPass(m_check.isSelected());
			}
			
		}
	};
	
	public WsPasswordField(boolean onlyOneField) {
		
		 m_onlyOneField =  onlyOneField;
		
		createGui();
		
		m_check.addActionListener(m_listener);
		
	}
	
	private void createGui() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel p1 = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = 0;
		
		c.gridy = 0;
		
		JLabel lb1 = new JLabel(getGuiStrs("pass1Enter"));
		
		lb1.setFont(m_font);
		
		p1.add(lb1, c);
		
		c.gridx = 1;
		
		p1.add(m_password, c);
		
		c.gridx = 2;
		
		p1.add( m_check, c);
		
		if( !m_onlyOneField) {
			
			JLabel lb2 = new JLabel(getGuiStrs("pass2Enter"));
			
			lb2.setFont(m_font);
			
			c.gridx = 0;
			
			c.gridy = 1;
		
			p1.add(lb2, c);
			
			c.gridx = 1;
			
			c.gridy = 1;
		
			p1.add(m_password2, c);

		
		}
		
		m_check.setFont(m_font);
		
		add(p1);
		
		add(Box.createHorizontalGlue());
		
	}
	
	
	public String getPass() {
		
		String p1 =  m_password.getValue();
		
		String p2 =  m_password2.getValue();
		
		if(!m_onlyOneField) {
			
			if(p1.equals(p2)) {
				
				return p1;
			}
			else { 
				return null; 
			}
			
		}
		else {
			
			return p1;
		}
	}
	
}
