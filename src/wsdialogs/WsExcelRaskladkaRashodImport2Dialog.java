
package wsdialogs;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import wscontrols.Ws2DatesControl;
import wscontrols.WsFileTableControl;
import wscontrols.WsIndicesImportPanel;
import wsdatastruct.WsAgentData;
import wsevents.WsEventDispatcher;
import wsimport.WsParseIndicies;
import wsimport.WsParseIndicies.TYPE;
import wslong.WsImportRaskladkaAgentsLong;
import wsmain.WsGuiTools;
import wsmain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsExcelRaskladkaRashodImport2Dialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected  JButton m_importButton = new JButton(getGuiStrs("buttonSformExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	WsFileTableControl m_table_control = null;
	
	protected  JSpinner m_spin_nakl = null;
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WsParseIndicies m_ind_schema = new WsParseIndicies();
	
	WsIndicesImportPanel m_indices_panel = null;
	
	public WsExcelRaskladkaRashodImport2Dialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		String[] columnNames = { getGuiStrs("importRasklSourceFileName"), 
				getGuiStrs("importBazaPidrozdilFileName"), 
				getGuiStrs("rashodColumnPeopleName")
				
		};
		
		m_table_control = new WsFileTableControl(columnNames, getGuiStrs("chooserRasklAgentsLabelName"), true);
		
		createGUI();
		
		m_date.setCurrentStartDate();

		setToolTips();
		
		setLocation(50, 50);
		
		setCustomFont();
		
		pack();
		
	
		
	}
	
	private void createGUI() {
		
		SpinnerModel model1 = new SpinnerNumberModel(0, 0, 20000, 1);  
		
		 m_spin_nakl = new  JSpinner(model1);
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel top = WsGuiTools.createHorizontalPanel();	

		top.add(new JLabel (getGuiStrs("importRaskladkaExcelDate2Label") )); 
		
		top.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		top.add(m_date);
		
		top.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		top.add(new JLabel (getGuiStrs("importRaskladkaExcelSpin2Label"))); 
		
		top.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		top.add(m_spin_nakl);
		
		top.add(Box.createHorizontalGlue());
				
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		//panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(top);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(m_table_control);
		
		WsGuiTools.setComponentPreferredHeight(m_table_control, 250);
		
		m_indices_panel = new WsIndicesImportPanel(this, TYPE.RASKLADKA);
		
		panel_main.add(panel_button);
		
		panel_main.add(m_indices_panel);
		 
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton )  {
				
				if(m_table_control.getData().isEmpty()) {
					  
					   WsUtils.showMessageDialogLong(  getMessagesStrs("raskladkaTableIsEmpty2Message0"),
							   getMessagesStrs("raskladkaTableIsEmpty2Message1"));
					   
					   return;
					
				}
				
				int res = WsUtils.showYesNoDialog(  getMessagesStrs("confirmImportRaskladkaForRashodCaption1")
					    + " " + String.valueOf(m_table_control.getData().size()) +
					    " " +getMessagesStrs("confirmImportRaskladkaForRashodCaption2"));
			      	   
				if ( 1 == res) {
				
					importData();
				
				}
			}
		}
	}
	
	public WsParseIndicies getIndicesSchema() { return m_indices_panel.getIndicesSchema(); }
	
	public Date getSqlStartDate() { return m_date.getSqlStartDate(); }
	
	public void importData() {
		
		 WsImportRaskladkaAgentsLong cusor = new  WsImportRaskladkaAgentsLong(this);
		
		 cusor.execute();
		
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
	
	private void setToolTips() {
				
	}

	
	public Vector<WsAgentData> getTableData() {
		
		return m_table_control.getData();
	}
	
	public int getInitialNumber() {
		
		return (int)m_spin_nakl.getValue();
		
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}
}
