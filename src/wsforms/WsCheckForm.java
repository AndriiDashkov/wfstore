
package wsforms;

import static wsmain.WsUtils.getGuiStrs;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import wscontrols.Ws2DatesControl;
import wscontrols.WsPartTypesComboBox;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsevents.WsPrihodInvoiceChangedEvent;
import wsevents.WsRashodInvoiceChangedEvent;
import wsmain.WsGuiTools;
import wsmain.WsUtils;
import wstables.WsCheckTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsCheckForm extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{
	
		WsEventDispatcher.get().addConnect(WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED, this, "refreshData2");

		WsEventDispatcher.get().addConnect(WsEventDispatcher.SALE_INVOICE_HAS_BEEN_CHANGED, this, "refreshData3");
		
		
	}
	
	TitledBorder m_title = null;
	
	WsPartTypesComboBox m_kods_combo = new WsPartTypesComboBox();
	
	Ws2DatesControl m_date2 = new Ws2DatesControl(false);
	
	JButton m_show_but = new JButton(getGuiStrs("showCapButton"),  WsUtils.get().getIconFromResource( 
			"wsfilter.png"));

	WsCheckTable m_table = new WsCheckTable();
	
    ActionListener m__listener = new ActionListener() {
		  

			@Override
			public void actionPerformed(ActionEvent e) {
				
				refresh();
				
			}
	   };
	
	public  WsCheckForm() {
		
		createGUI();
		
		m_date2.setCurrentStartDate();
		
		m_date2.setCurrentEndDate();
		
		m_show_but.addActionListener(m__listener);
		
		setCustomFont();

	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel buttons_panel = WsGuiTools.createHorizontalPanel();
		
		m_title = BorderFactory.createTitledBorder(getGuiStrs("filterCheckFormBorderTitle"));
		
		buttons_panel.setBorder(m_title);
			
		buttons_panel.add(m_kods_combo);
		
		buttons_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		buttons_panel.add(m_date2);
		
		buttons_panel.add(m_show_but);
		
		buttons_panel.add(Box.createHorizontalGlue());
		
		JScrollPane scroll = new JScrollPane(m_table);
	        
	    scroll.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
	    scroll.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        
	    add(buttons_panel);
	    
	    add(scroll);
	    
	    add(Box.createVerticalGlue());
	
	    WsGuiTools.setFixedSizeBehavior(m_kods_combo);
		
		WsGuiTools.setFixedSizeBehavior(m_date2);
		
		WsGuiTools.setFixedSizeBehavior(buttons_panel);
			
	}
	
	private void refresh() {
		
		int kod_id = m_kods_combo.getCurrentSQLId();
		
		if(kod_id != -1) {
			
			m_table.refreshData(m_date2.getSqlStartDate(), m_date2.getSqlEndDate(), m_kods_combo.getCurrentSQLId());
		}
					
	}
	
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED) {
			
			refresh();
			
		}
		
	}
	
	public void refreshData2(WsPrihodInvoiceChangedEvent e) {
		
		if(e == null || e.getEventType() == WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED) {
			
			refresh();
			
		}
		
	}
	
	public void refreshData3(WsRashodInvoiceChangedEvent e) {
		
		if(e == null || e.getEventType() == WsEventDispatcher.SALE_INVOICE_HAS_BEEN_CHANGED) {
			
	
			refresh();
			
		}
		
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 
		
		WsGuiTools.changeFont(m_table, f);
		
		m_table.getTableHeader().setFont(f);
		
		int margin_table = WsUtils.get().getTableMarginShiftForFont();
		
		m_table.setRowHeight(m_table.getRowHeight() + margin_table);
		
		m_title.setTitleFont(f);
	
	}
}
