
package WsControls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNumericField extends JTextField implements KeyListener {
	
	
	int m_old_value = 0;
	
	int m_max = 31;
	
	int m_min = 1;

	private static final long serialVersionUID = 1L;
	
	Ws2DatesControl m_parent = null;
	

	public WsNumericField(Ws2DatesControl parent, int v, int digits) {
		
		super("", 2);
		
		m_old_value = v;
		
		m_parent = parent;
		
		setText(String.valueOf(v));
		
		addKeyListener(this);
		
	}
	
	public void setMaxValue(int v) {
		
		m_max  = v;
		
		if(!getText().isEmpty()) {
		
			int value = Integer.parseInt(getText());
			
			if(value > m_max) {
				
				setText(String.valueOf(m_max));
			}
		}
	}
	
	public void setMinValue(int v) {
		
		m_min  = v;
		
		if(!getText().isEmpty()) {
			
			int value = Integer.parseInt(getText());
			
			if(value < m_min) {
				
				setText(String.valueOf(m_min));
			}
		}
	}
	
	public int getValue() {
			
		String s = getText();
		
		if(!s.isEmpty()) {
			
			return Integer.parseInt(getText());
		}
		return m_min;
	}
	
	public void setValue(int v) {
		
		m_old_value = v;
		
		setText(String.valueOf(v));
	}


	private void check() {

		  int l = getText().length();
		  
		  boolean parseFlag = true;
		  
		  int v = -1;
		  
		  try {
			
			  if(!getText().isEmpty()) {
				  
				  v = Integer.parseInt(getText());
			  }
			  
		  }
		  catch(NumberFormatException ex) {
			  		  
			  parseFlag = false;
		  }
	      
	      if((l > 2) || !parseFlag) {
	    	  
		      setText(String.valueOf(m_old_value));
	      }
	      else {
	    	  if(!getText().isEmpty()) {
		    		if(v > m_max) {
		    			
		    			setText(String.valueOf(m_max));
		    			
		    			m_old_value = m_max;
		    		}
		    		if(v < m_min) {
		    			
		    			setText(String.valueOf(m_old_value));
		    	
		    		}
		    		else {
		    			
		    			m_old_value = Integer.parseInt(getText());
		    		}
	    	  }
	      }
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		check();
		
		m_parent.valueChanged();
		
	}
}
