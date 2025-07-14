/**
 * 
 */
package wscontrols;

import static wsmain.WsUtils.getGuiStrs;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import wsdatastruct.WsSignsData;
import wsevents.WsEventDispatcher;
import wsmain.WsGuiTools;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSignsControlPanel extends JPanel {
	

	private static final long serialVersionUID = 1L;

	private JCheckBox m_approve_checkbox = new JCheckBox();
	
	private JCheckBox m_p1_checkbox = new JCheckBox();
	
	private JCheckBox m_p2_checkbox = new JCheckBox();
	
	private  WsSignsComboBox m_approve_combo =  new WsSignsComboBox();
	
	private  WsSignsComboBox m_p1_combo =  new WsSignsComboBox();
	
	private  WsSignsComboBox m_p2_combo =  new WsSignsComboBox();
	
	private static int id_app_combo_static = -1;
	
	private static int id_p1_combo_static = -1;
	
	private static int id_p2_combo_static = -1;
	
	private static boolean id_app_check_static = false;
	
	private static boolean id_p1_check_static = false;
	
	private static boolean id_p2_check_static = false;
	
	TitledBorder m_title = null;
	
	public WsSignsControlPanel() {
		
		createGUI();
		
		setCombosToStaticInit();
		
		setCustomFont();
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(m_approve_checkbox);
		
		add(new JLabel(getGuiStrs("pidp0Lb")));
		
		add(m_approve_combo);
		
		add(m_p1_checkbox);
		
		add(new JLabel(getGuiStrs("pidp1Lb")));
		
		add(m_p1_combo);
		
		add(m_p2_checkbox);
		
		add(new JLabel(getGuiStrs("pidp2Lb")));
		
		add(m_p2_combo);
		
		m_title = BorderFactory.createTitledBorder(getGuiStrs("signPeopleCaption"));
		
		setBorder(m_title);
		
		WsGuiTools.setComponentMaximumWidth(m_approve_combo,250);
		
		WsGuiTools.setFixedSizeBehavior(m_p1_combo);
		
		WsGuiTools.setFixedSizeBehavior(m_p2_combo);
		
		WsGuiTools.setFixedSizeBehavior(m_approve_combo);
		
		WsGuiTools.setFixedSizeBehavior(m_p1_combo);
		
		WsGuiTools.setFixedSizeBehavior(m_p2_combo);

	}
	
	public WsSignsData getApprovePerson() {
		
		if(!m_approve_checkbox.isSelected()) { return null; }
	
		return m_approve_combo.getSelectedSignData();
	
	}
	
	public WsSignsData getP1Person() {
		
		if(!m_p1_checkbox.isSelected()) { return null; }
	
		return m_p1_combo.getSelectedSignData();
	
	}
	
	public WsSignsData getP2Person() {
		
		if(!m_p2_checkbox.isSelected()) { return null; }
	
		return m_p2_combo.getSelectedSignData();
	
	}
	
	public void closeAllEventConnections() {
		
		WsEventDispatcher.get().disconnect(m_p1_combo);
		
		WsEventDispatcher.get().disconnect(m_p2_combo);
		
		WsEventDispatcher.get().disconnect(m_approve_combo);
		
	}
	
	private void setCombosToStaticInit() {
		
		m_approve_combo.setCurrentSQLId(id_app_combo_static);
		
		m_p1_combo.setCurrentSQLId(id_p1_combo_static);
		
	    m_p2_combo.setCurrentSQLId(id_p2_combo_static);
	    
	    m_approve_checkbox.setSelected(id_app_check_static);
		
		m_p1_checkbox.setSelected(id_p1_check_static);
		
	    m_p2_checkbox.setSelected(id_p2_check_static);
			
	}
	
	public void setComboStatic() {
		
		id_app_combo_static = m_approve_combo.getCurrentSQLId();
		
		id_p1_combo_static = m_p1_combo.getCurrentSQLId();
		
		id_p2_combo_static = m_p2_combo.getCurrentSQLId();
		
	    id_app_check_static = m_approve_checkbox.isSelected();
		
		id_p1_check_static = m_p1_checkbox.isSelected();
		
		id_p2_check_static = m_p2_checkbox.isSelected();
		
			
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
		
		m_title.setTitleFont(f);
			
	}

}
