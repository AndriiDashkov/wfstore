
package WsForms;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsMutableString;
import WsDataStruct.WsPartType;
import WsEditTables.WsAgentTypeEditTable;
import WsEditTables.WsInfoEditTable;
import WsEditTables.WsPartTypesEditTable;
import WsEditTables.WsSignsEditTable;
import WsEditTables.WsUnitsEditTable;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSBaseInfoForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
	}
	
	protected JLabel m_status_label = new JLabel(getGuiStrs("statusTableBaseInfoFormtion"));
	
	protected JLabel m_units_label = new JLabel(getGuiStrs("unitsTableBaseInfoFormtion"));
	
	protected JLabel m_part_types_label = new JLabel(getGuiStrs("partTypesTableBaseInfoFormtion"));
	
	protected JLabel m_agent_types_label = new JLabel(getGuiStrs("agentTypesTableBaseInfoFormtion"));
	
	protected JLabel m_signs_label = new JLabel(getGuiStrs("signsTableBaseInfoFormtion"));
	
	protected JLabel m_info_label = new JLabel(getGuiStrs("infoTableBaseInfoFormtion"));
	
	protected WsUnitsEditTable m_units_table = new WsUnitsEditTable();
	
	protected WsPartTypesEditTable m_part_types_table = new  WsPartTypesEditTable();

	protected WsAgentTypeEditTable m_agent_types_table = new WsAgentTypeEditTable();
	
	protected WsSignsEditTable m_signs_table = new WsSignsEditTable();

	protected WsInfoEditTable m_info_table = new WsInfoEditTable();
	
	protected  JButton m_importButton = new JButton(getGuiStrs("buttonImportCostExcelCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	protected  JButton m_excelExportButton = new JButton(WsUtils.get().getIconFromResource("wsexportexcel.png"));
	
	protected static WsMutableString m_export_file =   new WsMutableString("");

	public WSBaseInfoForm() {
		
		createGUI();

		m_excelExportButton.addActionListener(new ActionListener() { 
			
			  public void actionPerformed(ActionEvent e) { 
			    
				  exportCatalogToExcelFile();
			  } 
			  
		} );
		
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel panel0 = WsGuiTools.createVerticalPanel();
		
		JPanel panel1 = WsGuiTools.createHorizontalPanel();
		
		JPanel unitsPanel = WsGuiTools.createVerticalPanel();
		
		JPanel unitsLablePanel = WsGuiTools.createHorizontalPanel();
		
		JPanel partTypesPanel = WsGuiTools.createVerticalPanel();
		
		JPanel partTypesLablePanel = WsGuiTools.createHorizontalPanel();
		
		JPanel agentTypesPanel = WsGuiTools.createVerticalPanel();
		
		JPanel agentTypesLablePanel = WsGuiTools.createHorizontalPanel();
		
		JPanel signsPanel = WsGuiTools.createVerticalPanel();
		
		JPanel infoPanel  = WsGuiTools.createVerticalPanel();
		
		JPanel infoLablePanel = WsGuiTools.createHorizontalPanel();
		
		JScrollPane scrollUnits = new JScrollPane(m_units_table);
	        
		scrollUnits.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scrollUnits.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		  
		unitsLablePanel.add(m_units_label);
		
		unitsLablePanel.add(Box.createHorizontalGlue());
		
		unitsPanel.add(unitsLablePanel);
		
		unitsPanel.add(scrollUnits);
		
		JScrollPane scrollPartTypes = new JScrollPane(m_part_types_table);
        
		scrollPartTypes.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scrollPartTypes.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		  
		partTypesLablePanel.add(m_part_types_label);
		
		partTypesLablePanel.add(Box.createHorizontalGlue());
		
		partTypesLablePanel.add(m_excelExportButton);
		
		partTypesLablePanel.add(m_importButton);
		

		partTypesLablePanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		WsGuiTools.fixComponentHeightToMin(partTypesLablePanel);
		
		partTypesPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		partTypesPanel.add(partTypesLablePanel);
		
		partTypesPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		partTypesPanel.add(scrollPartTypes);
		
		JScrollPane scrollAgentTypes = new JScrollPane(m_agent_types_table);
        
		scrollAgentTypes.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scrollAgentTypes.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		  
		agentTypesLablePanel.add(m_agent_types_label);
		
		agentTypesLablePanel.add(Box.createHorizontalGlue());
		
		agentTypesPanel.add(agentTypesLablePanel);
		
		agentTypesPanel.add(scrollAgentTypes);
		
		JPanel signsLablePanel = WsGuiTools.createHorizontalPanel();
		
		JScrollPane scrollSigns = new JScrollPane(m_signs_table);
        
		scrollSigns.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scrollSigns.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		  
		signsLablePanel.add(m_signs_label);
		
		signsLablePanel.add(Box.createHorizontalGlue());
		
		signsPanel.add(signsLablePanel);
		
		signsPanel.add(scrollSigns);

		JScrollPane scrollInfo = new JScrollPane(m_info_table);
        
		scrollInfo.setHorizontalScrollBarPolicy(
	                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        
		scrollInfo.setVerticalScrollBarPolicy(
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		  
		infoLablePanel.add(m_info_label);
		
		infoLablePanel.add(Box.createHorizontalGlue());
		
		infoPanel.add(infoLablePanel);
		
		infoPanel.add(scrollInfo);
		
		panel1.add(unitsPanel);
		
		panel1.add(signsPanel);
		
		panel1.add(agentTypesPanel);
		
		panel0.add(panel1);
		
		panel0.add(infoPanel);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				partTypesPanel, panel0);

		splitPane.setOneTouchExpandable(true);
		
		splitPane.setDividerLocation(400);
		
		add(splitPane);
		
		m_importButton.setVisible(false);
		
		setToolTips();
			
	}
	
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED) {
			
			    m_units_table.refreshData();
			    
			    m_part_types_table.refreshData();
			    	    
			    m_agent_types_table.refreshData();
			    
			    m_info_table.refreshData();
			    
			    m_signs_table.refreshData();
   				
		}
		else
		if(e != null && e.getType() == WsEventEnable.TYPE.CATALOG_LOADED) {
			
		    m_part_types_table.refreshData();
		
		}
		else
		if(e.getType() == WsEventEnable.TYPE.NEW_PART_TYPE_CREATED) {
				
			  m_part_types_table.refreshData();
				
		}
			
	}
	
	private void setToolTips() {
		
		 m_importButton.setToolTipText(getMessagesStrs("costLoadCatalogtoolTip"));
			
		 m_excelExportButton.setToolTipText(getMessagesStrs("exportCatalogExcelToolTip"));
		
		
	}
	
	
	private void exportCatalogToExcelFile() {
		
		String file_to_save = 	WsUtils.get().excelSaveFileChoose(this, m_export_file);
		
		if (null == file_to_save)  { return; }
	
		OutputStream out;
		
		try {
			try {
				
				out = new FileOutputStream(file_to_save);
				
			} catch(java.io.FileNotFoundException exf) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("cantOpenFileForExportMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				return;
			}
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			XSSFWorkbook wb = new XSSFWorkbook();
			
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    int row_index = 0;
		    
		    XSSFRow row = sheet.createRow(row_index++);
		    
		    XSSFCell cell0 = row.createCell(0);
		    
		    XSSFCell cell01 = row.createCell(1);
		    
		    cell0.setCellValue(getGuiStrs("ptypesKodColumName"));
		    
		    cell01.setCellValue(getGuiStrs("nameColumnReportGoodName"));
		    
		    Vector<WsPartType> vec = m_part_types_table.getCurrentVectorData();
		
	    	for (WsPartType dt: vec) {
	
		            row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            cell0 = row.createCell(cell_index++);

	                cell01 = row.createCell(cell_index++);
	                
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	    		    cell0.setCellValue(dt.kod);
	    		    
	    		    cell01.setCellValue(dt.name);
	    		    
	    		    cell02.setCellValue(dt.costwithnds);
	    		    
	    		    cell03.setCellValue(dt.info);
	         
	        }
	
	   
			wb.write(out);
	
			out.close();
	    
			wb.close(); 
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("saveExcelReportSuccessMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
    
		} catch (IOException  e) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			e.printStackTrace();
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("saveExcelReportFailedMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
	}

}