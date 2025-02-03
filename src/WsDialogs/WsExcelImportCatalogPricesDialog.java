
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.WsIndicesImportPanel;
import WsEvents.WsEventDispatcher;
import WsImport.WFParseIndicies;
import WsImport.WFParseIndicies.TYPE;
import WsLong.WsImportExcelRaskladkaToKatalogLong;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsExcelImportCatalogPricesDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	JLabel path_lab = new JLabel (getGuiStrs("pathRaskladkaExcelFileLabel"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path_alb = new JTextField(25);
	
	protected JCheckBox m_check_merge = new JCheckBox(getGuiStrs("mergePricesCheckBox"));
	
	private int m_id_contract = -1;
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	 WsIndicesImportPanel m_ind_panel = null;
	 
	 JFrame m_parent = null;
	
	public WsExcelImportCatalogPricesDialog(JFrame jf, String caption, int id_contract) {
		
		super (jf, caption, true);
		
		m_parent = jf;
		
		m_id_contract = id_contract;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		setToolTips();
		
		m_check_merge.setSelected(true);
		
		pack();
		
		setLocation(100,100);
			
	}
	
	private void createGUI() {
		
		m_ind_panel = new  WsIndicesImportPanel(this, TYPE.CATALOGRASKLADKA);
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(m_path_alb);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		JPanel raskl_panel = WsGuiTools.createVerticalPanel();
		
		raskl_panel.setBorder(BorderFactory.createTitledBorder(getGuiStrs("raskladkaImportBorderTitle")));
		
		JPanel raskl_panel_0 = WsGuiTools.createHorizontalPanel();
		
		raskl_panel_0.add(path_lab);   
		
		raskl_panel_0.add(Box.createHorizontalStrut(HOR_STRUT));
		
		raskl_panel_0.add( panel_path);
		
		raskl_panel.add(raskl_panel_0);
		
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		JPanel panel_check = WsGuiTools.createHorizontalPanel();
		
		panel_check.add(m_check_merge);
		
		panel_check.add( Box.createHorizontalGlue());
		
		panel_main.add(raskl_panel);
		
		panel_main.add(panel_check);
		
		panel_main.add(panel_button);
		
		panel_main.add(m_ind_panel);
		 
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
		m_pathButton.addActionListener(f);
		
		
	}

	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton )  {
					
				int res = WsUtils.showYesNoDialog(getMessagesStrs("confirmImportRaskladkaForKatalogCaption1"));
			      	   
				if ( 1 == res) {	
				
					importData();
				
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
		
		int result = sourcePhotoFile.showOpenDialog(m_path_alb);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourcePhotoFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path_alb.setText(name);	
			}
			
		}
	}
	
	public String getExcelFilePath() {
		
		return m_path_alb.getText();
	}
	
	public void importData() {
		
		 WsImportExcelRaskladkaToKatalogLong cusor = new  WsImportExcelRaskladkaToKatalogLong(this);
		
		cusor.execute();
		
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
	
	public boolean isMergeSelected() {
		
		return !m_check_merge.isSelected();
	}
	
	private void setToolTips() {
			
	}
	
	public WFParseIndicies getIndicesSchema() { return  m_ind_panel.getIndicesSchema(); }
	
	public int getIdContract() { return m_id_contract; }
	
	public JFrame getParentFrame() { return m_parent;}
	 
}

