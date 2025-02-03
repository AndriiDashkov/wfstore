
package WsControls;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import WsActions.WsAction;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsFindKodField extends JTextField implements KeyListener {
	

	private static final long serialVersionUID = 1L;
	
	WsAction m_action = null;
	
	int m_kod = -1;
	

	public WsFindKodField(WsAction action) {
		super(6);

		m_action = action;
		
		addKeyListener(this);
		
		Dimension d = getPreferredSize();
		
		d.width = 80;
		
		setMinimumSize(d);
		
		setMaximumSize(d);
		
	}

	private void check() {

		  int l = getText().length();
		  
		  if(l > 4) {
		    	  
			     setText(getText().substring(0,3));
		  }
		  
		  try {
			
			  if(!getText().isEmpty()) {
				  
				  m_kod = Integer.parseInt(getText());
			  }
			  
		  }
		  catch(NumberFormatException ex) {
	
			  m_kod = -1;
		  }
		
	}


	@Override
	public void keyTyped(KeyEvent e) {
	
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		check();
		
		m_action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
				String.valueOf(m_kod)));
		
	}
}

