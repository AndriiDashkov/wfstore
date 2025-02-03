
package WsMain;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *<p>Common button class which has Enter press support</p>
 */
public class WsButtonEnter extends JButton {

	private static final long serialVersionUID = 1L;

	public WsButtonEnter() {
		
		setListener();
	}


	public WsButtonEnter(Icon arg0) {
		super(arg0);
		
		setListener();
	}


	public WsButtonEnter(String arg0) {
		super(arg0);
		
		setListener();
	}


	public WsButtonEnter(Action arg0) {
		super(arg0);
		
		setListener();
	}


	public WsButtonEnter(String arg0, Icon arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	private void setListener(){
		
		this.addKeyListener(new ButtonEnterListener());
	}
	

	private class ButtonEnterListener extends KeyAdapter {
		
	    public void keyPressed(KeyEvent e) {
	    	
	         if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	        	 
	        	 if(hasFocus()) {		    
	        		 
		        	doClick();
	        	 }
			}
	    } 
	}
}
