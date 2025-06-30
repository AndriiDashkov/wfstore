

package WsDialogs;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.WsAgentComboBox;
import WsEvents.WsEventDispatcher;
import WsLong.WsImportXmlPrihodLong;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsXmlImportPrihodDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	JLabel path_lab = new JLabel (getGuiStrs("pathXmlImportFileLabel"));
	
	//JLabel count_lab = new JLabel ("");
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path = new JTextField(25);
	
	protected WsAgentComboBox m_agents_combo = new WsAgentComboBox();
	
	JLabel m_agent_lable = new JLabel (getGuiStrs("agentAllXmlImpLabel"));
	
	JProgressBar m_progressBar = new JProgressBar(0, 100);
	
	JLabel m_info_label = new JLabel("");
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	JFrame m_parent = null;
	
	public WsXmlImportPrihodDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		m_parent = jf;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		setToolTips();
		
		setLocation(100,100);
		
		setCustomFont();
		
		pack();
		
	
			
	}
	

	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		panel_main.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(path_lab);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add(m_path);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		JPanel panel_agent = WsGuiTools.createHorizontalPanel();
		
		panel_agent.add(m_agent_lable);
		
		panel_agent.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_agent.add(m_agents_combo);
		
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		JPanel infoPanel = WsGuiTools.createHorizontalPanel();
		
		infoPanel.add(m_info_label);
		
		infoPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		infoPanel.add(m_progressBar);
		
		infoPanel.add(Box.createHorizontalGlue());
		
		panel_main.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		panel_main.add(panel_path);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_agent);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(infoPanel);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));

		panel_main.add(panel_button);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		WsGuiTools.setComponentFixedHeight(m_path, m_pathButton.getPreferredSize().height);
		
		WsGuiTools.setComponentFixedHeight(m_agents_combo, m_pathButton.getPreferredSize().height);
		
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
		m_pathButton.addActionListener(f);
		
		m_progressBar.setVisible(false);
		
		//WsGuiTools.setCustomFontSize(this, 18); 
		
	}
	
	public void setTextForCountLabel(String t) {
		
		m_info_label.setText(t);
	}

	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton )  {
				
				String path = getFilePath();
				
				if(path == null || path.isEmpty()) {
					
					WsUtils.showMessageDialog(  getMessagesStrs("noXmlFileMessage"));
					   
					return;
				}
					
				int res = WsUtils.showYesNoDialog( getMessagesStrs("importXMlNaklsCaption"));
		      	   
				if ( 1 == res) {
					
					WsImportXmlPrihodLong cusor = new  WsImportXmlPrihodLong(WsXmlImportPrihodDialog.this, m_progressBar);
							
					m_progressBar.setVisible(true);
						
					cusor.execute();

				}
			}
			
			if ( e.getSource() == m_pathButton )  {
				
				onPath(e);
			}
					
		}
	}
	
	public void onPath(ActionEvent e) {

		JFileChooser sourcePhotoFile = new JFileChooser();
		
		sourcePhotoFile.setCurrentDirectory(new File("."));
		
		sourcePhotoFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int result = sourcePhotoFile.showOpenDialog(m_path);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourcePhotoFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path.setText(name);	
			}
			
		}
	}
	
	public String getFilePath() {
		
		return m_path.getText();
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
	

	private void setToolTips() {
			
	}
	
	
	public JFrame getParentFrame() { return m_parent;}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f); 

			
	}
	
	public int getCurrentAgentSqlId() {
		
		
		return m_agents_combo.getCurrentSQLId() ;
		
	}
		 
}
