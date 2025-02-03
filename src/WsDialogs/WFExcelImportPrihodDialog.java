
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsControls.WsAgentComboBox;
import WsControls.WsIndicesImportPanel;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsImport.WFParseIndicies;
import WsImport.WFParseIndicies.TYPE;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WFExcelImportPrihodDialog extends JDialog {
	

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
	
	protected JTextField m_path_alb = new JTextField(25);
	
	JLabel label_number = new JLabel (getGuiStrs("importExcelNaklNumberLabel"));
	
	protected JTextField m_number = new JTextField(25);
	
	JLabel label_date = new JLabel (getGuiStrs("importExcelDateLabel"));
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	WFParseIndicies m_ind_schema = new WFParseIndicies();
	
	WsIndicesImportPanel m_panel_ind = null;

	public WFExcelImportPrihodDialog(JFrame jf, String caption) {
		
		super (jf, caption, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		setResizable(false);
		
		m_date.setCurrentStartDate();
		
		pack();
		
		setLocation(100,100);
		
	}
	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(4,2));
		
		panel_grid.add(path_lab);

		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(m_path_alb);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		panel_grid.add(panel_path);
		
		panel_grid.add(label_number);
		
		panel_grid.add(m_number);
		
		panel_grid.add(label_date);
		
		panel_grid.add(m_date);
		
		panel_grid.add(m_labeAgentCombo);
		
		panel_grid.add(m_AgentCombo );
		
		m_panel_ind = new WsIndicesImportPanel(this, TYPE.NAKL);
			
		JPanel but_panel = WsGuiTools.createHorizontalPanel();
		
		but_panel.add(m_importButton); 
		
		but_panel.add(Box.createHorizontalGlue());
		
		panel_main.add(panel_grid);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(	but_panel);
		
		panel_main.add(m_panel_ind);
		
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
		m_pathButton.addActionListener(f);
		
			
	}
	
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton ) {
				
				importData();
				
			}
			
			if ( e.getSource() == m_pathButton ) onPath(e);
			
		}
	}
	
	public void onPath(ActionEvent e) {
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), ".", true, false);
	
		int result = sourceFile.showOpenDialog(m_path_alb);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			m_path_alb.setText(name);	
			
		}
	}
	
	private void fillIndicesSchema() {
		
		 
		 m_ind_schema= m_panel_ind.getIndicesSchema();
		
	}
	
	public void importData() {
		
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		String excel_file_name = m_path_alb.getText();
		
		fillIndicesSchema();
		
		if(excel_file_name == null || excel_file_name.isEmpty()) {
			
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
		
		WsPrihodData data = new WsPrihodData(); 
		
		data.id_counterparty = m_AgentCombo.getCurrentSQLId();
		
		data.date = m_date.getSqlStartDate();
		
		data.date_doc = data.date;
		
		data.number = m_number.getText();
		
		String warnMessage = null;
		
		Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>() ;
		
		for(int i =0; i <  data_import.m_data.size(); ++i) {
			
			WFRowData d = data_import.m_data.elementAt(i);
			
			WsPrihodPartData d_ = new WsPrihodPartData();
			
			WsPartType pt = WsUtilSqlStatements.getPartTypeForKod(d.kod);
			
			if(pt == null) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			 getMessagesStrs("cantDetectPartTypeCaption") + " " + String.valueOf(d.kod),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				   
				   setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				   return;
			}
			
			d_.quantity = d.quantity;
			
			d_.rest = d.quantity;
			
			d_.name = d.name;
			
			d_.id_part_type = pt.id;
			
			d_.kod = d.kod;
			
			d_.vendorcode2 = String.valueOf(d.kod);
			
			d_.cost = d.cost;
			
			d_.nds = d.nds;

			WsUnitData ud = WsUtilSqlStatements.getUnitIdForName(d.units);
			
			if(ud != null) {
				
				d_.id_units = ud.id;
				
			
			}
			else {
				
				WsUnitData ud1 = WsUtilSqlStatements.getKgUnit();
				
				d_.id_units = ud1.id;
				
				warnMessage = getMessagesStrs("noUnitWhileImportMessage");
				
			}
			
			//eggs workaround
			if(WsUtils.isKodEqual(d.kod, WsUtils.EGG_KOD_1) || WsUtils.isKodEqual(d.kod, WsUtils.EGG_KOD_2)) {

				d_.id_units = 1;
			}
			
			vec.add(d_);
		}
		
		
		if( warnMessage != null) {
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			 warnMessage ,
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		}
		
		WsTransactions.beginTransaction(null);
		
		int inserted_id = WsPrihodSqlStatements.createNewPrihod(data, vec);
		
		if(inserted_id != -1) {
		
			WsTransactions.commitTransaction(null);
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();
			
			ev.setRowId(inserted_id);
			
			WsEventDispatcher.get().fireCustomEvent(ev);
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			 getMessagesStrs("messageProhNaklSuccess") ,
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
		}
		else {
			
			WsTransactions.rollbackTransaction(null);
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			 getMessagesStrs("messageProhNaklFailed") ,
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
}
