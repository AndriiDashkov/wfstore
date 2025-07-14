package wsmain;

import static wsmain.WsUtils.*;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsforms.WsBaseInfoForm;
import wsforms.WsCheckParentForm;
import wsforms.WsContrAgentsForm;
import wsforms.WsContractsForm;
import wsforms.WsPrihodForm;
import wsforms.WsRashodForm;
import wsforms.WsReportsForm;
import wsforms.WsReturnForm;
import wsforms.WsSkladForm;
import wsforms.WsSpisRaskladkaForm;

/**
 * <p>The main view panel of the application</p>
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsCentralControlPanel extends JPanel {	
	
	JTabbedPane m_tab = null;
	
	private static final long serialVersionUID = 1L;
	
	{
		WsEventDispatcher.get().addConnect(
				WsEventDispatcher.ENABLE_EVENT, this, "refreshView");
			
	}
	
	JScrollPane m_scrollPane;
    
    private WsPrihodForm m_prihod_form = null;
    
    private WsSkladForm m_sklad_form = null;
    
    private WsContrAgentsForm m_agents_form = null;
    
    private WsRashodForm m_rashod_form = null;
    
    private WsBaseInfoForm m_base_form =  null;
    
    private WsReportsForm m_report_form = null;
    
    private WsCheckParentForm m_check_form = null;
   
    private WsReturnForm m_return_form = null;
    
    private WsSpisRaskladkaForm m_sp_form = null;
    
    private WsContractsForm m_ct_form = null;
    

	public WsCentralControlPanel () {
		
		m_tab = new JTabbedPane(); 
	    
	    m_prihod_form = new WsPrihodForm();
	    
	    m_sklad_form = new WsSkladForm();
	    
	    m_agents_form = new WsContrAgentsForm();
	    
	    m_rashod_form = new WsRashodForm();

	    m_base_form =  new WsBaseInfoForm();
	    
	    m_report_form = new  WsReportsForm();
	    
	    m_check_form =  new WsCheckParentForm();
	    
	    m_return_form = new WsReturnForm();
	    
	    m_sp_form = new  WsSpisRaskladkaForm();
	    
	    m_ct_form = new  WsContractsForm();
	    
	    m_tab.add(getGuiStrs("prihodTabCaption"),m_prihod_form);
	    
	    m_tab.add(getGuiStrs("skladTabCaption"),m_sklad_form);
	     
	    m_tab.add(getGuiStrs("rashodTabCaption"),m_rashod_form);
	    
	    m_tab.add(getGuiStrs("agentsTabCaption"),m_agents_form);
	    
	    m_tab.add(getGuiStrs("reportsTabCaption"), m_report_form);
	    
	    m_tab.add(getGuiStrs("ctTabCaption"), m_ct_form);

	    m_tab.add(getGuiStrs("baseTabCaption"), m_base_form);
	    
	    m_tab.add(getGuiStrs("checkTabCaption"), m_check_form);
	    
	    m_tab.add(getGuiStrs("returnTabCaption"), m_return_form);
	    
	    m_tab.add(getGuiStrs("spTabCaption"), m_sp_form);
	    
		setLayout(new BorderLayout());
		
		setBorder( BorderFactory.createEmptyBorder(0, 0, 0, 0));

		m_tab.setVisible(true);

		add (m_tab, BorderLayout.CENTER);	
		
		m_tab.setEnabled(false);
		
		setCustomFont();

	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 
	
	}

	public void refreshView(WsEventEnable e) {
		
		if(e != null && e.getType() == WsEventEnable.TYPE.DATABASE_LOADED
				) {
			
			
			m_tab.setEnabled(true);
			
		}
		
	}

	/*public void switchCurrentTab(WsEventEnable ev) {
		
		if (ev.getType() == WsEventEnable.TYPE.NEW_LEFT_PANEL_ITEM_ACTIVATED) {
			
			switch(ev.getLeftPanelIndex()) {
			
				case 0: { m_tab.setSelectedIndex(0); break; }
			
				case 1: { m_tab.setSelectedIndex(1); break; }
				
				case 2: { m_tab.setSelectedIndex(2); break; }
				
				case 3: { m_tab.setSelectedIndex(3); break; }
				
				case 4: { m_tab.setSelectedIndex(4); break; }
				
				default: {m_tab.setSelectedIndex(0);}
			
			}
		}
		
	}*/

}
