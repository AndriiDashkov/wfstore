
package WsControls;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsMutableString {
	
	
	private String m_str = null;
	
	WsMutableString() {
		
	}
	
	public WsMutableString(String s) {
		
		m_str = new String(s);
		
	}
	
	public void set(String s) {
		
		m_str = new String(s);
		
	}
	
	public String toString() {
		
		return m_str;
	}

}
