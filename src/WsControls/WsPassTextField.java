
package WsControls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPassTextField extends JTextField implements KeyListener {
	
	
	StringBuilder m_value = new StringBuilder();
	
	private static final long serialVersionUID = 1L;
	
	boolean m_showPass = false;
	
	public WsPassTextField(boolean showPass, int columns) {
		
		super("", columns);
		
		m_showPass = showPass;
		
		addKeyListener(this);
		
	}

	public String getValue() {
		
		return m_value.toString();
		
	}



	public void check() {

		  String text = getText();
		  
		  if(m_showPass) {
			  
			  m_value = new StringBuilder(text);
			  
			  return;
		  }
		  
		  if(text.length() == 0) {
			  
			  m_value = new StringBuilder();
		  }
		  else if (text.length() > m_value.length()) {
			  
			  String ch = text.substring(text.length() - 1, text.length());
			  
			  m_value.append(ch);
		  
		  }
		  else {
			  
			  m_value.deleteCharAt(m_value.length() - 1);
		  }
		  
		  StringBuilder b = new StringBuilder();
		  
		  for(int i = 0; i < m_value.length(); ++i) {
			  
			  b.append("*");
		  }
		  
		 setText(b.toString());
		  
	}




	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		
		check();
			
	}
	
	
	public void setShowPass(boolean showPass) {
		
		if(m_showPass && !showPass) {
			
			StringBuilder b = new StringBuilder();
			  
			for(int i = 0; i < m_value.length(); ++i) {
				  
				  b.append("*");
			}
			  
			setText(b.toString());
			
		}
		
		if(!m_showPass && showPass) {
			
			setText(m_value.toString());
		}
		
		m_showPass = showPass;
	
	}
}

