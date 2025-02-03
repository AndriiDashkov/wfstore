
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentComboBox;
import WsControls.WsIndicesImportPanel;
import WsEvents.WsEventDispatcher;
import WsImport.WFParseIndicies;
import WsImport.WFParseIndicies.TYPE;
import WsLong.WsImportRaskladka7daysLong;
import WsMain.WsGuiTools;
import WsMain.WsUtils;



/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsExcelRaskladkaRashodImportDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected  JLabel m_labelMondayColumn = new JLabel(getGuiStrs("labelMondayPeopleColumnCaption"));
	
	protected  JLabel m_labelTuesdayColumn = new JLabel(getGuiStrs("labelTuesdayColumnCaption"));
	
	protected  JLabel m_labelWednesdayColumn = new JLabel(getGuiStrs("labelWednesdayColumnCaption"));
	
	protected  JLabel m_labelThursdayColumn = new JLabel(getGuiStrs("labelThursdayColumnCaption"));
	
	protected  JLabel m_labelFridayColumn = new JLabel(getGuiStrs("labelFridayColumnCaption"));
	
	protected  JLabel m_labeSaturdayRow = new JLabel(getGuiStrs("labelSaturdayCaption"));
	
	protected  JLabel m_labelSundayRow = new JLabel(getGuiStrs("labelSundayCaption"));
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected  JLabel m_labeAgentCombo = new JLabel(getGuiStrs("labeAgentOtrComboCaption"));
	
	protected WsAgentComboBox m_AgentCombo = new WsAgentComboBox();
	
	JLabel path_lab = new JLabel (getGuiStrs("pathRaskladkaExcelFileLabel"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path_alb = new JTextField(25);
	
	JLabel label_number = new JLabel (getGuiStrs("importExcelNaklNumberLabel"));
	
	protected JTextField m_number = new JTextField(25);
	
	protected  JSpinner[] m_spinners = new JSpinner[7];
	
	JLabel label_date = new JLabel (getGuiStrs("importRaskladkaExcelDateLabel"));

	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WFParseIndicies m_ind_schema = new WFParseIndicies();
	
	WsIndicesImportPanel m_indices_panel = null;
	
	JRadioButton m_radio_raskladka = new JRadioButton(getGuiStrs("radioRaskladSourceCaption"));
	
	JRadioButton m_radio_kartka_zvit =  new JRadioButton(getGuiStrs("radioKartkaZvitCaption"));
	
	ButtonGroup m_radio_group = new ButtonGroup();

	public WsExcelRaskladkaRashodImportDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		m_date.setCurrentStartDate();
		
		m_radio_raskladka.setSelected(true);
		
		setToolTips();
		
		pack();
		
		setLocation(100,100);
		
	}
	
	private void createGUI() {
		
		SpinnerModel model1 = new SpinnerNumberModel(0, 0, 20000, 1);  
		
		 m_spinners[0] = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(0, 0, 20000, 1); 
		
		m_spinners[1] = new  JSpinner(model2);
		
		SpinnerModel model3 = new SpinnerNumberModel(0, 0, 20000, 1);
		
		m_spinners[2] = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(0, 0, 20000, 1);
		
		m_spinners[3] = new  JSpinner(model4);
		
		SpinnerModel model5 = new SpinnerNumberModel(0, 0, 20000, 1);
		
		m_spinners[4] = new  JSpinner(model5);
	
		SpinnerModel model6 = new SpinnerNumberModel(0, 0, 20000, 1);
		
		m_spinners[5] = new  JSpinner(model6);
		
		SpinnerModel model7 = new SpinnerNumberModel(0, 0, 20000, 1);
		
		m_spinners[6] = new  JSpinner(model7);
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel top_grid = new JPanel();	
		
		top_grid.setLayout(new GridLayout(3,2));
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(7,2));
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("peopleQuantityBorderTitle"));
		
		panel_grid.setBorder(title);
		
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
		
		JPanel rasklsource__panel = WsGuiTools.createHorizontalPanel();
		
		rasklsource__panel.add(new JLabel(getGuiStrs("rasklImportSourceLabelName")));
		
		rasklsource__panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		rasklsource__panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		rasklsource__panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		rasklsource__panel.add(m_radio_raskladka);
		
		rasklsource__panel.add(m_radio_kartka_zvit);
		
		rasklsource__panel.add(Box.createHorizontalGlue());
		
		raskl_panel.add(rasklsource__panel);
		
		top_grid.add(label_date); top_grid.add(m_date);
		
		top_grid.add(m_labeAgentCombo); top_grid.add(m_AgentCombo);
		
		panel_grid.add(m_labelMondayColumn); panel_grid.add(m_spinners[0]);
		
		panel_grid.add(m_labelTuesdayColumn);	    panel_grid.add(m_spinners[1]);
		
		panel_grid.add(m_labelWednesdayColumn);     panel_grid.add(m_spinners[2]);
		
		panel_grid.add(m_labelThursdayColumn); panel_grid.add(m_spinners[3]);
		
		panel_grid.add(m_labelFridayColumn);     panel_grid.add(m_spinners[4]);
		
		panel_grid.add(m_labeSaturdayRow);         panel_grid.add(m_spinners[5]);
		
		panel_grid.add(m_labelSundayRow);         panel_grid.add(m_spinners[6]);
				
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.add(raskl_panel);
		
		panel_main.add(top_grid);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
						
		panel_main.add(panel_grid);
		
		m_indices_panel = new WsIndicesImportPanel(this, TYPE.RASKLADKA);
		
		panel_main.add(panel_button);
		
		panel_main.add(m_indices_panel);
		 
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
		m_pathButton.addActionListener(f);
		
		m_radio_group.add(this.m_radio_kartka_zvit); 
		
		m_radio_group.add(this.m_radio_raskladka); 
		
		m_radio_kartka_zvit.addActionListener(f); 
		
		m_radio_raskladka.addActionListener(f);
		
	}
	
	private int getNaklNumber() {
		
		int res = 0;
		
		for(int i =0; i < 7; ++i) {
		
			res += ((int)m_spinners[i].getValue()) > 0 ? 1 : 0;
		}
		
		return res;
		
	}

	
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton )  {
				
				if(0 == getNaklNumber()) {
					
					 JOptionPane.showMessageDialog(
				   			    WsUtils.get().getMainWindow(),
				   			    getMessagesStrs("noPeopleForRaskladkaImportMessage"),
				   			    getMessagesStrs("messageInfoCaption"),
				   			    JOptionPane.CLOSED_OPTION);
					 
					 return;
				}
						
				int res = WsUtils.showYesNoDialog(  getMessagesStrs("confirmImportRaskladkaForRashodCaption1")
					    + " " + String.valueOf(getNaklNumber()) +
					    " " +getMessagesStrs("confirmImportRaskladkaForRashodCaption2"));
			      	   
				if ( 1 == res) {
				
					importData();
				
				}
			}
			
			if ( e.getSource() == m_pathButton )  {
				
				onPath(e);
			}
			

			if ( e.getSource() ==  m_radio_raskladka ) { 

				setIndicesSchema();
			}
			
			if ( e.getSource() == m_radio_kartka_zvit ) { 

				setIndicesSchema();
			}
			
		}
	}
	
	public void onPath(ActionEvent e) {
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), ".", true, false);
	
		int result = sourceFile.showOpenDialog(m_path_alb);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path_alb.setText(name);	
			}
			
		}
	}
	

	public String getExcelFilePath() {
		
		return m_path_alb.getText();
	}
	
	public WFParseIndicies getIndicesSchema() { return m_indices_panel.getIndicesSchema(); }
	
	public int getAgentSqlId() { return m_AgentCombo.getCurrentSQLId(); }
	
	public JSpinner[] getSpinners() { return m_spinners; }
	
	public Date getSqlStartDate() { return m_date.getSqlStartDate(); }
	
	public void importData() {
		
		WsImportRaskladka7daysLong cusor = new WsImportRaskladka7daysLong(this);
		
		cusor.execute();
		
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
		
	}
	
	private void setToolTips() {
		
		m_spinners[0].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_spinners[1].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_spinners[2].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_spinners[3].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_spinners[4].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_spinners[5].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
		
		m_spinners[6].setToolTipText(getGuiStrs("peopleRasklRashodSpinnerToolTip"));
			
	}
	
	private void setIndicesSchema() {
		
		if(m_radio_raskladka.isSelected()) {
			 
			 m_indices_panel.setIndicesSchema(new WFParseIndicies(TYPE.RASKLADKA));
			 
		}

		if (m_radio_kartka_zvit.isSelected()) {
			
			m_indices_panel.setkarkaZvitRaskladkaIndices();
			
		}	
	}	
}
