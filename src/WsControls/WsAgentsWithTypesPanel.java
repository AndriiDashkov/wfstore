/**
 * 
 */
package WsControls;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import WsMain.WsUtils;
import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsAgentsWithTypesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	WsAgentTypesFilterComboBox m_typesCombo = null;
	
	WsAgentComboBox m_agentsCombo = null;
	
	public WsAgentsWithTypesPanel() {
		
		createGUI();
	}
	
	private void createGUI() {
		
		m_typesCombo = new WsAgentTypesFilterComboBox();
		
		m_agentsCombo = new WsAgentComboBox();
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(m_agentsCombo);
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		add(new JLabel(getGuiStrs("fTypesAg")));
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		add(m_typesCombo);
		
		m_typesCombo.refreshModel(null);
		
		setListeners();
	
	}
	
	private void setListeners() {
		
			m_typesCombo.addActionListener(new ActionListener() {
	        
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int id_type = m_typesCombo.getCurrentSQLId();
				
				m_agentsCombo.refreshModel(null, id_type);
				
			}
		});
	}
	
	public int getCurrentSQLId() {
		
		return m_agentsCombo.getSelectedAgentData().id;
		
	}
	
	public void setCurrentSQLId(int id) {
		
		m_typesCombo.setSelectedIndex(0);
		
		m_agentsCombo.refreshModel(null, -1);
		
		m_agentsCombo.setCurrentSQLId(id);
		
	}
}
