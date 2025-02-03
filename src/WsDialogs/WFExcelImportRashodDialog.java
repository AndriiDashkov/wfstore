package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentComboBox;
import WsControls.WsIndicesImportPanel;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WFExcelImportRashodDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected  JLabel m_labelSheetColumn = new JLabel(getGuiStrs("labelSheetColumnCaption"));
	
	protected  JLabel m_labelKodColumn = new JLabel(getGuiStrs("labelKodColumnCaption"));
	
	protected  JLabel m_labelNameColumn = new JLabel(getGuiStrs("labelNameColumnCaption"));
	
	protected  JLabel m_labelQuantityColumn = new JLabel(getGuiStrs("labelQuantityColumnCaption"));
	
	protected  JLabel m_labelDateColumn = new JLabel(getGuiStrs("labelDateColumnCaption"));
	
	protected  JLabel m_labeDateRow = new JLabel(getGuiStrs("labeDateRowCaption"));
	
	protected  JLabel m_labeUnitsRow = new JLabel(getGuiStrs("labeUnitsRowCaption"));
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected  JLabel m_labeAgentCombo = new JLabel(getGuiStrs("labeAgentComboCaption"));
	
	protected WsAgentComboBox m_AgentCombo = new WsAgentComboBox();
	
	JLabel path_lab = new JLabel (getGuiStrs("pathExcelFileLabel"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path = new JTextField(25);
	
	protected static String m_path_static = "";
	
	JLabel label_number = new JLabel (getGuiStrs("importExcelNaklNumberLabel"));
	
	protected JTextField m_number = new JTextField(25);
	
	WsIndicesImportPanel m_panel_ind =null;
	
	JLabel label_date = new JLabel (getGuiStrs("importExcelDateLabel"));

	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WFParseIndicies m_ind_schema = new WFParseIndicies();
	
	JCheckBox m_take_latest_first = new JCheckBox(getGuiStrs("useLatestPositionsFirst"));
	
	WsNewRashodDialog m_parent_dialog;

	public WFExcelImportRashodDialog(Window jf, WsNewRashodDialog parent_dialog, String caption) {
		
		super (jf, caption, DEFAULT_MODALITY_TYPE);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		setResizable(false);
		
		m_date.setCurrentStartDate();
				
		m_parent_dialog = parent_dialog;
		
		m_panel_ind.setIndicesSchema(new WFParseIndicies(TYPE.NAKL));
		
		pack();
		
		setLocation(100,100);
		
	}
	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		m_panel_ind = new WsIndicesImportPanel(this, TYPE.NAKL);
		
		JPanel but_panel = WsGuiTools.createHorizontalPanel();
		
		but_panel.add(m_importButton); 
		
		but_panel.add( m_take_latest_first);
		
		but_panel.add(Box.createHorizontalGlue());
	
		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
	
		panel_path.add(path_lab);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add(m_path);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		panel_main.add(panel_path);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(but_panel);
		
		panel_main.add(m_panel_ind);
		
		add(panel_main);
		
		 Forwarder f = new  Forwarder();
		
		 m_importButton.addActionListener(f);
		 
		 m_pathButton.addActionListener(f);
		
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton ) importData();
			
			if ( e.getSource() == m_pathButton ) onPath(e);
			
		}
	}
	
	public void onPath(ActionEvent e) {
		
		String currentFolder = ".";
		
		if( !m_path_static.isEmpty() ) { currentFolder = m_path_static;  } 
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), currentFolder, true, false);
		
		int result = sourceFile.showOpenDialog(m_path);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			if(null == sourceFile.getSelectedFile()) { return; }
			
			String name = sourceFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path.setText(name);	
				 
				 m_path_static = WsUtils.get().getPathFromString(name) ;
			}

		}
	}
	
	private void fillIndicesSchema() {
	
		 m_ind_schema = m_panel_ind.getIndicesSchema();
		
	}
	
	public void importData() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		String excel_file_name = m_path.getText();
		
		fillIndicesSchema();
		
		if(excel_file_name.isEmpty()) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			  JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			 getMessagesStrs("excelFileNotFoundCaption"), 
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		WsImportData  data_import = WSExcelImport.getData( excel_file_name, m_ind_schema);
		
		if(data_import == null) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			 getMessagesStrs("wrongExcelFileFormatInfoCaption"), 
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			   
			return;
		}
		
		
		Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>() ;
		
		for(int i =0; i <  data_import.m_data.size(); ++i) {
			
			WFRowData d = data_import.m_data.elementAt(i);
			
			WsRashodPartData d_ = new WsRashodPartData();
			
			d_.quantity = d.quantity;
			
			d_.name = d.name;
			
			d_.kod = d.kod;
			
			d_.vendor_code_2 = String.valueOf(d.kod);
			
			d_.cost = d.cost;
			
			d_.nds = d.nds;
			
			WsUnitData ud = WsUtilSqlStatements.getUnitIdForName(d.units);
			
			if(ud == null) {
				
				ud = WsUtilSqlStatements.getKgUnit();
			}
			if(ud == null) {
				
				ud = WsUtilSqlStatements.getFirstUnit();
		
			}
			//workaround for eggs
			if(WsUtils.isKodEqual(d_.kod, WsUtils.EGG_KOD_1 ) || WsUtils.isKodEqual(d_.kod, WsUtils.EGG_KOD_2 )) {
				

				ud = WsUtilSqlStatements.getShtUnit();
				
				d_.id_units =  ud.id;
				
				d_.units_name = ud.name;
			}
			else {
				
				d_.id_units = ud.id;
				
				d_.units_name = d.units;
			}
		
			//the kod repetition control
			boolean notfound = true;
			
			for(int j2 = 0; j2 < vec.size(); ++j2) {
				
				WsRashodPartData d_f = vec.elementAt(j2);
				
				if(WsUtils.isKodEqual(d_f.kod, d_.kod)) {
					
					d_f.quantity += d_.quantity;
					
					notfound = false;
					
					break;
				}
				
			}
			
			if(notfound)  { vec.add(d_); }
		}
		
		Vector<String> vec_not_enough_quantity = new Vector<String>();
		
		Vector<WsRashodPartData> vec_ins =
				WsRashodSqlStatements.findSkladPositionsForRashod(m_parent_dialog.getDate(), vec, 
						m_take_latest_first.isSelected(), vec_not_enough_quantity, true);
		
		m_parent_dialog.insertImportData(vec_ins);
		
		m_parent_dialog.insertNotEnoughData(vec_not_enough_quantity);
		
		WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
	        
	 	WsEventDispatcher.get().fireCustomEvent(ev);
		
	 	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}
	
	public void dispose() {
		
		m_parent_dialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		WsEventDispatcher.get().disconnect(this);
			
		super.dispose();
		
	}
	
}