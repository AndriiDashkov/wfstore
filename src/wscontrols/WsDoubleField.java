package wscontrols;



import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

public class WsDoubleField extends  JFormattedTextField {

	private static final long serialVersionUID = 1L;

	public WsDoubleField() {
		
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		
		numberFormat.setParseIntegerOnly(false);
		
		numberFormat.setMinimumFractionDigits(1); 
		
		numberFormat.setMaximumFractionDigits(6);
		
		numberFormat.setGroupingUsed(false);
		
		NumberFormatter formatter = new NumberFormatter(numberFormat);
		
		formatter.setValueClass(Double.class); 
		
		formatter.setAllowsInvalid(false); 
		
		formatter.setCommitsOnValidEdit(true); 
		
		setFormatter(formatter);
		
		setValue(0.0);
		
		
	}


}
