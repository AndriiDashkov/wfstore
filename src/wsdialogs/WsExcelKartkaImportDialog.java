
package wsdialogs;

import static wsmain.WsUtils.*;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import wscontrols.Ws2DatesControl;
import wscontrols.WsAgentComboBox;
import wscontrols.WsIndicesImportPanel;
import wsdatabase.WsRashodSqlStatements;
import wsdatabase.WsTransactions;
import wsdatabase.WsUtilSqlStatements;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsdatastruct.WsUnitData;
import wsevents.WsEventDispatcher;
import wsevents.WsRashodInvoiceChangedEvent;
import wsimport.WsExcelImport;
import wsimport.WsParseIndicies;
import wsimport.WsRowData;
import wsimport.WsParseIndicies.TYPE;
import wsmain.WsGuiTools;
import wsmain.WsUtils;



/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsExcelKartkaImportDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private static String m_last_path = "";
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected WsAgentComboBox m_agentCombo = new WsAgentComboBox();
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path_file = new JTextField(25);
	
	protected JTextField m_number = new JTextField(25);
	
	//protected JSpinner m_people_spinner = null;
	
	protected JSpinner m_nakl_spinner = null;
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WsParseIndicies m_ind_schema = new WsParseIndicies();
		
	WsIndicesImportPanel m_indices_panel = null;
	
	public WsExcelKartkaImportDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		m_date.setCurrentStartDate();
		
		setToolTips();
		
		m_path_file.setText(m_last_path);
		
		m_number.setText("auto");
		
		setCustomFont();
		
		pack();
		
		setLocation(100,100);
		
	}
	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel top_grid = new JPanel();	
		
		top_grid.setLayout(new GridLayout(5,2, 0, WsUtils.VERT_STRUT/4));
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(m_path_file);
		
		WsGuiTools.setComponentFixedHeight(m_path_file, m_pathButton.getPreferredSize().height);
		
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
		
		top_grid.add( new JLabel (getGuiStrs("prihodColumnDateName") + ":"));   
		
		top_grid.add(m_date);
		
		top_grid.add( new JLabel(getGuiStrs("labeAgentOtrComboCaption")));    
		
		top_grid.add(m_agentCombo);
		
		top_grid.add(new JLabel (getGuiStrs("importExcelNaklNumberLabel")));    
		
		top_grid.add(m_number);
		
		SpinnerModel model11 = new SpinnerNumberModel(1, 0, 1000, 1);  
		
		m_nakl_spinner = new  JSpinner(model11);

		top_grid.add(new JLabel (getGuiStrs("importExcelNaklNumberLabel2"))); top_grid.add(m_nakl_spinner);
		
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
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
				
	
				int res = WsUtils.showYesNoDialogLong( 
						getMessagesStrs("confirmImportKZForRashodCaption0"),
						getMessagesStrs("confirmImportKZForRashodCaption1")
					    );
			      	   
				if ( 1 == res) {
				
					importDataMulti();
				
				}
			}
			else
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
	
	public WsParseIndicies getIndicesSchema() { return m_indices_panel.getIndicesSchema(); }
	
	public int getAgentSqlId() { return m_agentCombo.getCurrentSQLId(); }
	
	public Date getSqlStartDate() { return m_date.getSqlStartDate(); }
	
	public void importDataMulti() {
		
		String excel_file_name = getExcelFilePath() ; //m_path_file.getText();
		
		WsParseIndicies schema = getIndicesSchema();
		
		if(excel_file_name.isEmpty()) {
			
			WsUtils.showMessageDialog( getMessagesStrs("kzFilePathIsEmptyMessage"));
				
			return;
		}
		
		getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		int rowsNumber = (int) m_nakl_spinner.getValue();
		
		Vector<Vector<WsRowData>> data_import = WsExcelImport.getDataFromKartkaMultiRow( excel_file_name, 
			schema, rowsNumber);
		
		
		if(null == data_import) {
			
			getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			WsUtils.showMessageDialog( getMessagesStrs("raskladkaFileImportWrongMessage"));
			
			return;
			
		}
		
		int id_counterparty = getAgentSqlId();
			
		java.sql.Date date =   getSqlStartDate();
			
		String number = m_number.getText();
		
		//int people = (int) m_people_spinner.getValue();
		
		for(int i =  0; i < data_import.size(); ++i) {
			
			createNakl(data_import.elementAt(i), id_counterparty, 
				date, number, data_import.elementAt(i).elementAt(0).people);
			
			try {
				
				int num = Integer.valueOf(number);
				
				number = String.valueOf(num + 1);
				
			}catch(NumberFormatException  ex) {
				
				number = m_number.getText() + String.valueOf(i + 1); 
			}
			
			date = WsUtils.sqlDatePlusDays(date, 1);
		
		}
			
	}
	
	public void createNakl(Vector<WsRowData> data_import, int id_counterparty, 
			java.sql.Date date, String number, int people) {
		
		WsRashodData data = new WsRashodData(); 
		
		data.id_counterparty = id_counterparty;
		
		data.date =  date;
			
		data.number = number;
		
		data.people = people;
			
		Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>() ;
			
		for(int i = 0; i <  data_import.size(); ++i) {
			
			WsRowData d = data_import.elementAt(i);
			
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
			
			WsUtils.showMessageDialogLong3(getMessagesStrs("raskladkaLackPositionsDetectedMessage0"),
					 getMessagesStrs("raskladkaLackPositionsDetectedMessage1"),
					 getMessagesStrs("raskladkaLackPositionsDetectedMessage2"));
						
		}
		
		WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
		
		ev.setRowId(-1);
		
		WsEventDispatcher.get().fireCustomEvent(ev);
		
		WsUtils.showMessageDialog(String.valueOf(createdNakls) + " " +
   			    getMessagesStrs("raskladkaNaklsNumberCreatedMessage"));
			
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
	
	}
	
	private void setToolTips() {

		m_path_file.setToolTipText(getMessagesStrs("kzTextEditImp"));
		
		m_number.setToolTipText(getMessagesStrs("numImpKZ"));
		
		m_nakl_spinner.setToolTipText(getMessagesStrs("klNaklKZImp"));
			
	}
	
	@SuppressWarnings("unused")
	private void setIndicesSchema() {
	
	}
	
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}
}
