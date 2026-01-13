package wsdialogs;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import wscontrols.WsFileChooser;
import wsmain.WsGuiTools;
import wsmain.WsUtils;
import wsreports.WsSkladMovementReport;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsReport14ExcelImportDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	JRadioButton m_horRadio = new JRadioButton(getGuiStrs("horRep"));
	
	JRadioButton m_vertRadio = new JRadioButton(getGuiStrs("verRep"));
	
	JButton m_expButton = new JButton(getGuiStrs("startExp"));
	
	JSpinner m_spin = null;
	
	WsSkladMovementReport m_parent =  null;
	
	TitledBorder m_title = null;
	
	WsFileChooser m_file = new  WsFileChooser(getGuiStrs("fileExpCh"));
	
	public  WsReport14ExcelImportDialog(JFrame jfrm, String nameFrame,
			WsSkladMovementReport parent) {
		
		super (jfrm, nameFrame, true);
		
		m_parent = parent;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		m_horRadio.setSelected(true);
		
		addListeners();

		setLocation(250, 150);
	
		setCustomFont();

		pack();
		
		setResizable(false);
		
		m_spin.setEnabled(false);
		
		m_file.setExcelType(true);
		
	}
	
	void addListeners() {
		
		
		 m_expButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	String f  = m_file.getFullFilePath();
	            	
	            	if (f == null || f.isEmpty())  { 
	            		
	            		WsUtils.showMessageDialog(getMessagesStrs("noexpfile"));
         		
	            		return; 
	            		
	            	}
	            	
	            	
	            	if( m_horRadio.isSelected() ) {
	            	
	            		m_parent.exportToExcelFile(f);
	            		
	            		WsReport14ExcelImportDialog.this.dispose();
	            	
	            	}
	            	else {
	            		
	            		m_parent.exportToExcelFile2(f, (int)m_spin.getValue());
	            		
	            		WsReport14ExcelImportDialog.this.dispose();
	            		
	            	}
	             
	            }
		 });
		 
		 m_vertRadio.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            
	            	m_spin.setEnabled( m_vertRadio.isSelected());
	             
	            }
		 });
		
	}
	
	private void createGUI() {
		
		ButtonGroup bg = new ButtonGroup();
		
		 bg.add(m_horRadio);
		 
		 bg.add(m_vertRadio);
		
		JPanel main = WsGuiTools.createVerticalPanel(); 
		
		add(main);
		
		SpinnerModel model1 = new SpinnerNumberModel(3, 1, 100, 1);  
		
		 m_spin = new  JSpinner(model1);
		
		JPanel main1 = WsGuiTools.createVerticalPanel(); 

		m_title = BorderFactory.createTitledBorder(getGuiStrs("thrRashBorderTitle"));
		
		main1.setBorder(m_title);
	
		JPanel panel1 = WsGuiTools.createHorizontalPanel(); 
		
		JPanel panel2 = WsGuiTools.createHorizontalPanel(); 

		panel1.add(m_horRadio);
		
		panel1.add(Box.createHorizontalGlue());
		
		panel2.add(m_vertRadio);
		
		panel2.add(Box.createHorizontalGlue());
		
		main1.add(panel1);
		
		main1.add( panel2);
		
		JPanel sp_panel = WsGuiTools.createHorizontalPanel(); 
		
		sp_panel.add(new JLabel(getGuiStrs("colQ")));
		
		 sp_panel.add(m_spin);
		 
		 main1.add(sp_panel);
		 
		 JPanel bt_panel = WsGuiTools.createHorizontalPanel(); 
		 
		 bt_panel.add(m_expButton);
		 
		 bt_panel.add(Box.createHorizontalGlue());
		
		 main.add(main1);
		
		 main.add(m_file);
		
		 main.add(bt_panel);
		
		 main.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		
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
