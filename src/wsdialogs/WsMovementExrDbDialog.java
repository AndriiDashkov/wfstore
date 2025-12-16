package wsdialogs;

import static wsmain.WsUtils.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import wsdatastruct.WsSkladMoveDataColumn;
import wsevents.WsEventDispatcher;
import wsmain.WsGuiTools;
import wstables.WsMovementExtDbTable;

/**
 * 
 * @author Andrii Dashkov  license GNU GPL v3
 * 
 * <p>"About" dialog</p>
 */
public class WsMovementExrDbDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	JLabel m_label = new JLabel("");
	
	WsMovementExtDbTable m_table = new WsMovementExtDbTable();

	public  WsMovementExrDbDialog(JFrame jfrm, Vector<WsSkladMoveDataColumn> data,
			String kodName) {
		
		super (jfrm, getGuiStrs("detailedRuhDialogCaption"), true); 
				
		createGUI();
		
		m_table.refreshData(data);
		
		m_label.setText(kodName);
		
		setLocation(200, 200);
		
		this.setMinimumSize(new Dimension(500, 250));
		
		setCustomFont();

		pack();

		//setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}
	
	/**
	 * @return main Gui panel
	 */
	private void createGUI () {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel pLabel = WsGuiTools.createHorizontalPanel();
		
		pLabel.add(m_label);
		
		pLabel.add(Box.createHorizontalGlue());
		
		mainPanel.add(pLabel);
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
        mainPanel.add(scroll);
        
    	setLayout(new BorderLayout());
		
		add(mainPanel);

	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}
}
