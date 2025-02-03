package WsMain;

import java.awt.Dimension;
import javax.swing.JPanel;


/**
 * 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsMainPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;


	private WsCentralControlPanel m_viewPanel;
	
	
	public WsMainPanel () {
		
		m_viewPanel = new WsCentralControlPanel();

		m_viewPanel.setVisible(true);
		
	}

	public void setSplitMainOrientation(int flag) {
		

		Dimension dim2 = new Dimension(WsMainDesktop.getDefaultWidth()/6*5-4, WsMainDesktop.getDefaultHeight());

		m_viewPanel.setPreferredSize(dim2);
		
	}
	
}
