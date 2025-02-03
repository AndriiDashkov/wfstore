
package WsForms;

import static WsMain.WsUtils.getGuiStrs;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsCheckParentForm extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTabbedPane m_tab = null;
	
	WSCheckForm m_check_form =  new WSCheckForm();
	
	WsFindPeresortForm m_peresort_form = new WsFindPeresortForm();
	
	public  WsCheckParentForm() {
		
		m_tab = new JTabbedPane(); 

		m_tab.add(getGuiStrs("movementCheckCaption"), m_check_form);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(m_tab);
		
	    ChangeListener changeListener = new ChangeListener() {
	    	

			@Override
			public void stateChanged(ChangeEvent e) {
				
				int index = m_tab.getSelectedIndex();
				
				switch(index) {
				
					case 1: {
						
						m_peresort_form.refresh();
					}
				
				};
				
			}
			
	    };
	      
	    m_tab.addChangeListener(changeListener);
	
	}
}
