
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentComboBox;
import WsControls.WsIndicesImportPanel;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsGuiTools;
import WsMain.WsUtils;



/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsExcelKartkaImportDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private static String m_last_path = "";
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected WsAgentComboBox m_AgentCombo = new WsAgentComboBox();
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path_file = new JTextField(25);
	
	protected JTextField m_number = new JTextField(25);
	
	protected JSpinner m_people_spinner = null;
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WFParseIndicies m_ind_schema = new WFParseIndicies();
		
	WsIndicesImportPanel m_indices_panel = null;
	
	public WsExcelKartkaImportDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		m_date.setCurrentStartDate();
		
		setToolTips();
		
		pack();
		
		setLocation(100,100);
		
		
		m_path_file.setText(m_last_path);
		
		m_number.setText("auto");
		
	}
	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel top_grid = new JPanel();	
		
		top_grid.setLayout(new GridLayout(5,2, 0, WsUtils.VERT_STRUT/2));
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(m_path_file);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		JPanel raskl_panel = WsGuiTools.createVerticalPanel();
		
		JPanel raskl_panel_0 = WsGuiTools.createHorizontalPanel();
		
		raskl_panel_0.add(new JLabel (getGuiStrs("pathRaskladkaExcelFileLabel")));   
		
		raskl_panel_0.add(Box.createHorizontalStrut(HOR_STRUT));
		
		raskl_panel_0.add( panel_path);
		
		raskl_panel.add(raskl_panel_0);
		
		JPanel rasklsource__panel = WsGuiTools.createHorizontalPanel();
		
		rasklsource__panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		rasklsource__panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		rasklsource__panel.add(Box.createHorizontalStrut(HOR_STRUT));
		
		rasklsource__panel.add(Box.createHorizontalGlue());
		
		raskl_panel.add(rasklsource__panel);
		
		top_grid.add( new JLabel (getGuiStrs("prihodColumnDateName") + ":"));      top_grid.add(m_date);
		
		top_grid.add( new JLabel(getGuiStrs("labeAgentOtrComboCaption")));         top_grid.add(m_AgentCombo);
		
		top_grid.add(new JLabel (getGuiStrs("importExcelNaklNumberLabel")));       top_grid.add(m_number);
		
		SpinnerModel model1 = new SpinnerNumberModel(0, 0, 200000, 1);  
		
		m_people_spinner = new  JSpinner(model1);
		
		top_grid.add(new JLabel (getGuiStrs("importExcelNaklPeopleNumberLabel"))); top_grid.add(m_people_spinner);
			
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.add(raskl_panel);
		
		panel_main.add(top_grid);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		m_indices_panel = new WsIndicesImportPanel(this, TYPE.KARTZVIT);
		
		panel_main.add(panel_button);
		
		panel_main.add(m_indices_panel);
		 
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
		m_pathButton.addActionListener(f);
			
	}
	

	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton )  {
				
	
				int res = WsUtils.showYesNoDialog( 
						getMessagesStrs("confirmImportKZForRashodCaption")
					    );
			      	   
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
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), ".", true, false);


		int result = sourceFile.showOpenDialog(m_path_file);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path_file.setText(name);	
				 
				 m_last_path = name;
			}
			
		}
	}
	

	public String getExcelFilePath() {
		
		return m_path_file.getText();
	}
	
	public WFParseIndicies getIndicesSchema() { return m_indices_panel.getIndicesSchema(); }
	
	public int getAgentSqlId() { return m_AgentCombo.getCurrentSQLId(); }
	
	public Date getSqlStartDate() { return m_date.getSqlStartDate(); }
	
	public void importData() {
		
		String excel_file_name = getExcelFilePath() ; //m_path_file.getText();
		
		WFParseIndicies schema = getIndicesSchema();
		
		if(excel_file_name.isEmpty()) {
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("kzFilePathIsEmptyMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		Vector<WFRowData> data_import = WSExcelImport.getDataFromKartkaZvit(  excel_file_name,  schema) ;
		
		if(null == data_import) {
			
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("raskladkaFileImportWrongMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		
			return;
			
		}
		
		WsRashodData data = new WsRashodData(); 
	
		data.id_counterparty = getAgentSqlId();
			
		data.date =   getSqlStartDate();
			
		data.number = m_number.getText();
		
		data.people = (int) m_people_spinner.getValue();
			
		Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>() ;
			
		for(int i = 0; i <  data_import.size(); ++i) {
			
			WFRowData d = data_import.elementAt(i);
			
			WsRashodPartData d_ = new WsRashodPartData();
			
			d_.cost = d.cost;
			
			d_.nds = d.nds;
			
			d_.quantity = d.quantity;
			
			if(d_.quantity > 0.00049 && d_.quantity < 0.001) {
				
				d_.quantity = 0.001;
			}
			
			if(d_.quantity <= 0.00049) {
				
				continue;
			}
			
			d_.name = WsUtilSqlStatements.getPartTypeForKod(d.kod).name;
			
			d_.kod = d.kod;
			
			d_.vendor_code_2 = String.valueOf(d.kod);
			
			WsUnitData ud = WsUtilSqlStatements.getUnitIdForName(d.units);
			
			if(ud == null) {
				
				ud = WsUtilSqlStatements.getKgUnit();
			}
			if(ud == null) {
				
				ud = WsUtilSqlStatements.getFirstUnit();
		
			}
		
			d_.id_units = ud.id;
			
			
		
			vec.add(d_); 
		}
		
		boolean lackFlag = false;
		
		int createdNakls = 0;
		
		Vector<String> vec_not_enough_quantity = new Vector<String>();
		
		Vector<WsRashodPartData> vec_ins =
				WsRashodSqlStatements.findSkladPositionsForRashod(data.date, vec, 
						false, vec_not_enough_quantity, false);
		
		for(int i = 0; i < vec_not_enough_quantity.size(); ++i) {
			
			if(i == 0) { data.info = "-"; }
			
			data.info += vec_not_enough_quantity.elementAt(i) + " ";
			
			lackFlag = true;
		}
		
		WsTransactions.beginTransaction(null);
		
		if( WsRashodSqlStatements.createNewRashod(data, vec_ins) != -1) {
			
			WsTransactions.commitTransaction(null);
			
			createdNakls++;
		}
		else {
			
			WsTransactions.rollbackTransaction(null);
			
		}
			
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		if(lackFlag) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("raskladkaLackPositionsDetectedMessage") ,
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
		}
		
		WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
		
		ev.setRowId(-1);
		
		WsEventDispatcher.get().fireCustomEvent(ev);
		
		JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    String.valueOf(createdNakls) + " " +
   			    getMessagesStrs("raskladkaNaklsNumberCreatedMessage") ,
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
	
	}
	
	private void setToolTips() {

			
	}
	
	@SuppressWarnings("unused")
	private void setIndicesSchema() {
	
	}
	
}
