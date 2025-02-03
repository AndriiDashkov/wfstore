package WsControls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDateTextField extends JFormattedTextField{
	

	private static final long serialVersionUID = 1L;

	private final MaskFormatter m_formatter = new TimeFormatter();
	
	private SimpleDateFormat m_df = new SimpleDateFormat("dd.MM.yy");
	
	public WsDateTextField(){
		super();
		
		setFormatter(m_formatter);
		
	}
	
	
	public  SimpleDateFormat getCustomDateFormatter() {
		
		return m_df;
	}
	
	public java.util.Date getDate() {
		

		String t = getText();
		
		try {
			
			java.util.Date d = m_df.parse(t);
			
			return d;
			
		} catch (ParseException e) {
	
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public java.sql.Date getSqlDate() {
		
	
		return new java.sql.Date(getDate().getTime());
		
	}
	
	public void setDate(java.util.Date d) {
		
		
		String s = m_df.format(d);

		setText(s);
		
	}
	
	public void setCurrentDate() {
		
		
		String s = m_df.format(new java.sql.Date(Calendar.getInstance().getTime().getTime()));

		setText(s);
		
	}
	
	
	
	public void setSqlDate(java.sql.Date d) {
		
		java.util.Date d_ = new java.util.Date(d.getTime());
		
		setDate(d_);
		
	}
	
	
	public class TimeFormatter extends MaskFormatter {

	    private static final long serialVersionUID = 1L;
	    
	    public TimeFormatter() { // set mask and placeholder
	        try {
	        	
	            setMask("##.##.##");
	            
	            setPlaceholderCharacter('0');
	            
	            setAllowsInvalid(false);
	            
	            setOverwriteMode(true);
	            
	        } catch (ParseException e) {
	        	
	        	if( WsUtils.isDebug() ) {
	        		
	        		e.printStackTrace();
	        	}
	        }
	    }
  
	}
}
