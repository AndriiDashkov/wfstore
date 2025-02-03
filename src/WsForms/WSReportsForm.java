
package WsForms;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import WsControls.WsReportsList;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSReportsForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public WSReportsForm() {
		
		createGUI();
			
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		 add(new WsReportsList());
		 
	}
}
