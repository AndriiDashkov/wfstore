
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
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
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsIndicesImportPanel;
import WsDataStruct.WsKodComparator;
import WsDataStruct.WsSkladMoveDataColumn;
import WsEvents.WsEventDispatcher;
import WsForms.WsSpisRaskladkaForm;
import WsImport.WFParseIndicies;
import WsImport.WsImportExcelUtil;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsCatalogKods;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsImportRestExcelDialog extends JDialog {
	
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
	
	protected  JButton m_loadButton = new JButton(getGuiStrs("buttonSlRestLoadCaption"));
	
	JLabel path_lab = new JLabel (getGuiStrs("pathExcelFileLabel"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path = new JTextField(25);
	
	protected static String m_path_static = "";
	
	JLabel label_number = new JLabel (getGuiStrs("importExcelNaklNumberLabel"));
	
	protected JTextField m_number = new JTextField(25);
	
	JRadioButton m_importBut = new JRadioButton(getGuiStrs("importZalSpisLabel"));
	
	JRadioButton m_skladBut = new JRadioButton(getGuiStrs("lZalFsklad"));
	
	WsIndicesImportPanel m_panel_ind =null;

	WFParseIndicies m_ind_schema = new WFParseIndicies();
	
	JPanel m_panel_import = null;
	
	JPanel m_panel_b = null;
	
	WsSpisRaskladkaForm m_parent;

	public WsImportRestExcelDialog(JFrame jf, WsSpisRaskladkaForm parent, String caption) {
		
		super (jf, caption, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		setResizable(false);
		
		m_panel_ind.setIndicesSchema(new WFParseIndicies(TYPE.PRODREST));
		
		m_parent = parent;
		
		pack();
		
		setLocation(100,100);
		
	}
	
	private JPanel createImportPanel( ) {
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("importButtonRashodLabel"));
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		panel_main.setBorder(title);
		
		m_panel_ind = new WsIndicesImportPanel(this, TYPE.NAKL);
				
		JPanel but_panel = WsGuiTools.createHorizontalPanel();
		
		but_panel.add(m_importButton); 
	
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

		return panel_main;
		
	}
	
	private void createGUI() {
		
		JPanel panel = new JPanel(new GridBagLayout());
		
        GridBagConstraints c = new GridBagConstraints();
        
        c.insets = new Insets(3, 0, 3, 0);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.weightx = 1;

        c.gridx = 0;  c.gridy = 0;
        
        panel.add(m_importBut, c);
		
        c.gridx = 1;  c.gridy = 0;
		
        panel.add(m_skladBut, c);
		
        c.gridx = 0;  c.gridy = 1;
		
		 m_panel_import = createImportPanel( );
		
		 panel.add( m_panel_import, c);
		 
		 c.gridx = 1;  c.gridy = 1;
		 
		 TitledBorder title;
			
		 title = BorderFactory.createTitledBorder(getGuiStrs("lFsklad"));
			
		 m_panel_b = WsGuiTools.createVerticalPanel();
		 
		 m_panel_b.setBorder(title);
		 
		 m_panel_b.add(m_loadButton);
		 
		 panel.add(m_panel_b, c);
		
		 add(panel);
		 
		 Dimension d = m_panel_import.getPreferredSize();
		 
		 d.width = m_panel_b.getPreferredSize().width;
		 
		 m_panel_b.setPreferredSize(d);
		
		 Forwarder f = new  Forwarder();
		
		 m_importButton.addActionListener(f);
		 
		 m_pathButton.addActionListener(f);
		 
		 ButtonGroup gr = new ButtonGroup();
		 	
		 gr.add(m_importBut);
		 
		 gr.add(m_skladBut);
		 
		 m_importBut.addActionListener(f);
			
	     m_skladBut.addActionListener(f);
	     
	     m_loadButton.addActionListener(f);
	     
	     m_importBut.setSelected(true);
	     
	 	 WsGuiTools.enableComponents(m_panel_import, true);
		
		 WsGuiTools.enableComponents(m_panel_b, false);

	}
	
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton ) { 
				
				Vector< WsSkladMoveDataColumn> vec  = importData(); 
				
				if(vec != null) {
					
					m_parent.setTableData( vec) ;
				
				}		
			}
			else
			if ( e.getSource() == m_loadButton ) { 

				if ( 1 == WsUtils.showYesNoDialog(getMessagesStrs("loadZalFromSklApproveMessage")) ) {
			
					m_parent.loadImportRestFromSklad ();
				
				}		
			}
			else
				if ( e.getSource() == m_pathButton ) { 

					onPath();		
				}
			else
			if ( e.getSource() ==  m_importBut || e.getSource() == m_skladBut) { 
				
				if(m_importBut.isSelected()) {
					
					WsGuiTools.enableComponents(m_panel_import, true);
					
					WsGuiTools.enableComponents(m_panel_b, false);

				}
				else {
					
					WsGuiTools.enableComponents(m_panel_import, false);
					
					WsGuiTools.enableComponents(m_panel_b, true);
					
				}
				
			}	
		}
	}
	
	public void onPath() {
		
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
	
	public Vector< WsSkladMoveDataColumn>  importData() {
		
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
			
			return null;
		}
		
		Vector< WsSkladMoveDataColumn> vec_new = new Vector< WsSkladMoveDataColumn>();
		
		try {		
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
							    
			XSSFWorkbook wb = null;
						
			try {
				
				wb = new XSSFWorkbook( fStream );
				
			} catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
				
				if(wb != null) { wb.close(); }
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				return null;
			}
						
			fStream.close();
							    
			XSSFSheet sheet = null;
					    
			try {
			    
				sheet = wb.getSheetAt( m_ind_schema.sheetIndex);
		    
			}  catch(java.lang.IllegalArgumentException ex) { 
				
				wb.close();
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				return null; 
				
			}
			    
		    XSSFRow row = null;
				 
		    int rows; 
		    
		    rows = sheet.getPhysicalNumberOfRows();
			
		    WsCatalogKods ct = new  WsCatalogKods();
		    
		    for(int r = 0; r < rows; r++) {
							      
		    	row = sheet.getRow(r);
						    
				if(row == null) { continue; }
						        	
				WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
				
				int kod_imp = WsImportExcelUtil.getKodCell(row, m_ind_schema.kodColumnIndex);
				        	   	
				d.kod = ct.getKodFromCatalog(kod_imp);
				
				if(d.kod != WsUtils.UNKNOWN_KOD) {
								
				    d.name = WsImportExcelUtil.getStringCell(row, m_ind_schema.nameColumnIndex);

				}
				else {
					
					d.kod = kod_imp;
					
					d.name =  WsImportExcelUtil.getStringCell(row, m_ind_schema.nameColumnIndex) + " ! not found kod";
					
				}
				 
				d.q_array[1].initial_rest = WsImportExcelUtil.getDoubleCell(row, m_ind_schema.quantityColumnIndex);
 				   
				if(d.kod != -1) {
					
					vec_new.add(d); 
				}
				        		
			}
						    
			wb.close();
			
		} catch (IOException e) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
			e.printStackTrace();
		}
		
		
		Collections.sort(vec_new, new WsKodComparator());
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						    				
		return vec_new;
		
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
	
}
