
package WsDataStruct;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPair {
	
	public Object complex = null;
	
	public double value;
	
	public int flag = 0;
	
	public WsPair() {
		
	
	}
	
	public WsPair(Object c, double v, int f) {
		
		complex = c;
		
		value = v;
		
		flag = f;
	}

}
