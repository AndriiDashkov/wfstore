
package wsforms;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import wscontrols.WsReportsList;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsReportsForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	public WsReportsForm() {
		
		createGUI();
			
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		 add(new WsReportsList());
		 
	}
}
