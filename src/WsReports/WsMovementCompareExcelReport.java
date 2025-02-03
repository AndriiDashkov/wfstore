/**
 * 
 */
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsContractsFilterComboBox;
import WsDataStruct.WsContractData;
import WsDataStruct.WsKodComparator;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsReportsSqlStatements;
import WsDialogs.WsFileChooserDialog;
import WsEvents.WsEventDispatcher;
import WsImport.WsImportExcelUtil;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsCatalogKods;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsMovementCompareExcelReport  extends WSReportViewer {

	Vector<WsSkladMoveDataColumn>  m_vec_all = null;
	
	JCheckBox m_checkAllKodes = null;
	
	WsContractsFilterComboBox m_contractsCombo = null;
	
	int m_id_contract = -1;
	
	String m_name_contract = "";
	
	public static java.sql.Date m_date_start_static = null;
	
	public static java.sql.Date m_date_end_static = null;
	
	JLabel path_lab = new JLabel (getGuiStrs("pathExcelFileLabel"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path = new JTextField(25);
	
	protected  JLabel m_labelSheetColumn = new JLabel(getGuiStrs("labelSheetColumnCaption"));
	
	protected  JLabel m_labelKodColumn = new JLabel(getGuiStrs("labelKodColumnCaption"));
	
	protected  JLabel m_labelQuantityColumn = new JLabel(getGuiStrs("labelQuantityColumnCaption"));
	
	JSpinner m_spinSheetColumn = null;
	
	JSpinner m_spinKodColumn = null;

	JSpinner m_spinQuantityColumn = null;
	 
	public   WsMovementCompareExcelReport(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	 m_html_pages = generateReport();
            	 
            	 if(m_html_pages == null || m_html_pages.isEmpty()) { return; }
            	
            	setText(m_html_pages.elementAt(0));
            	
            	pagesNum = m_html_pages.size();
            	
            	currentPage = 1;
            	
            	setDialogCaption();
            	
            	m_viewer.setSelectionStart(0);
            	
            	m_viewer.setSelectionEnd(0);
            	
            	m_date_start_static = m_date.getSqlStartDate();
            	
            	m_date_end_static = m_date.getSqlEndDate();
             
            }
            
		});
		
		 m_saveExcelButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	if( !isReportEmpty() ) {
	            	
	            		exportToExcelFile(m_vec_all);
	            	
	            	}
	             
	            }
		 });	
		 
		 m_pathButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	if ( e.getSource() == m_pathButton ) { 

	    				onPath();		
	    			}
	             
	            }
		 });
		 	 
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		m_checkAllKodes = new JCheckBox(getGuiStrs("useAllKodsForReports"));
		
		m_contractsCombo = new WsContractsFilterComboBox();
		
		JPanel topPanel = WsGuiTools.createHorizontalPanel();
		
		topPanel.add(new JLabel(getGuiStrs("dogReportCombo")));
		
		topPanel.add(m_contractsCombo);
		
		WsUtils.get().setFixedSizeBehavior(m_contractsCombo);
		
		topPanel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		topPanel.add(m_checkAllKodes);
		
		topPanel.add(Box.createHorizontalGlue());
		
		JPanel inPanel = WsGuiTools.createVerticalPanel();
		
		inPanel.add(topPanel);
		
		inPanel.add(createImportPanel( ));
		
		m_control_panel2.add(inPanel);
		
		WsUtils.get().setFixedSizeBehavior(m_control_panel2);
		
		if(m_date_start_static == null || m_date_end_static == null) {
		
			m_date.setCurrentStartDate();
		
			m_date.setCurrentEndDate();
		}
		else {
			
			m_date.setStartDate(m_date_start_static);
			
			m_date.setEndDate(m_date_end_static);
		}
		
		m_contractsCombo.refreshModel(null);
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector< WsSkladMoveDataColumn> vec_import =   importExcelData();
		
		m_id_contract = -1;
		
		m_name_contract = "";

		WsContractData contData = m_contractsCombo.getSelectedContract();
		
		if(contData != null) {
			
			m_id_contract = contData.id;
			
			m_name_contract = contData.number;
			
		}
		
		Vector<WsSkladMoveDataColumn> vec_all =
				WsReportsSqlStatements.getPrihodRashodBookForDate2(m_date.getSqlStartDate(),
						m_date.getSqlEndDate(), m_id_contract);
		
		//fill all kods from the catalog
		if(m_checkAllKodes.isSelected()) {
			
			vec_all = WSReportUtil.fillVectorWithAllKods( vec_all) ;
			
		}
		else {
			Vector<WsSkladMoveDataColumn> vec_all_1 = new Vector<WsSkladMoveDataColumn>();
			
			for(int i = 0; i < vec_all.size(); ++i) {
				
				WsSkladMoveDataColumn d = vec_all.elementAt(i);
				
				if(d.in_quantity > 0.0001 || d.out_quantity > 0.0001 || d.rest > 0.0001
						|| d.initial_rest > 0.0001) {
					
					vec_all_1.add(d);
					
				}
			}
			
			vec_all.clear();
			
			vec_all = vec_all_1;
			
		}
		//merge with import
		for(WsSkladMoveDataColumn im_d : vec_import) {
			
			boolean found = false;
			
			for(int i = 0; i < vec_all.size(); ++i) {
				
				WsSkladMoveDataColumn d = vec_all.elementAt(i);
				
				if(d.kod == im_d.kod) {
					
					d.out_quantity_1 = im_d.out_quantity_1;
					
					found = true;
					
					break;
				}
			}
			
			if(!found) {
				
				im_d.name = getGuiStrs("impKodEx");
				
				vec_all.add(im_d);
			}
			
		}
		
		Collections.sort(vec_all, new WsKodComparator());
		

		int rows_number = vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		Vector<String> vec_pages = new Vector<String>();
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
		if(pages_number == 1) {
			
			end_row = vec_all.size() - 1;
		}
		
		for(int k = 0; k < pages_number; ++k) { 
			
			String page = getPrintHtml(vec_all, start_row, end_row, k);
			
			vec_pages.add(page);
			
			start_row = end_row + 1;
			
			end_row = start_row + rows_per_page - 1;
			
			if(end_row > (vec_all.size() -1)) {
				
				end_row = vec_all.size() -1;
			}
			
			
		}
		
		m_vec_all = vec_all;
		
		current_font_size = 4;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
		
	}
	
	
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader_b = new StringBuilder(); 
		
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>"); 
		
		sHeader_b.append(getGuiStrs("reportBookKodGoodColumn") );
		
		sHeader_b.append("</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;"); 
		
		sHeader_b.append(getGuiStrs("nameColumnReportGoodName") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("naPochatReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("pribuloReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;"); 
		
		sHeader_b.append(getGuiStrs("quantityNameVibuloReportColumn") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("prihodPartsColumnRestName"));
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("impDataRestName"));
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("diffZalName"));
		
		sHeader_b.append("&nbsp;</font></td></tr>");

		StringBuilder row_s_b = new StringBuilder();
		
		for(int i = start; i <= end; ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			String bottomBorder = "";
			 
			if(i == end) {
				
				 bottomBorder = "border-bottom: 1px solid ;"; 
			}
			 
			 row_s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(String.valueOf(i + 1) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			 
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4> &nbsp;"); 
			 
			 row_s_b.append(String.valueOf(d.kod) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  ");  
			
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(d.name ); 
			 
		     row_s_b.append("&nbsp;</font></td>");  
		     
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.initial_rest) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
		     row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
		     
			 row_s_b.append(bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(WsUtils.getDF(d.in_quantity) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
		     row_s_b.append(bottomBorder ); 
		     
		     row_s_b.append("'><font size =4>&nbsp;" ); 
		     
		     row_s_b.append(WsUtils.getDF(d.out_quantity)); 
		     
		     row_s_b.append("&nbsp;</font></td>"); 
		     
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; ");  
			
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(WsUtils.getDF(d.rest) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; ");  
				
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(WsUtils.getDF(d.out_quantity_1) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ;  ");  
				
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(WsUtils.getDF(d.rest - d.out_quantity_1) ); 
			 
			 row_s_b.append("&nbsp;</font></td></tr>");
		
		}
		
		
		StringBuilder hS_b =  new StringBuilder();
		
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append("        height: 297mm;\r\n");
		
		hS_b.append("        width: 210mm;\r\n");
		
		hS_b.append("        /* to centre page on screen*/\r\n");
		
		hS_b.append("        margin-left: auto;\r\n");
		
		hS_b.append("        margin-right: auto;\r\n");
		
		hS_b.append("    }");
		
		hS_b.append("</style><body>");
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append(getGuiStrs("bookSkladMovementReportName2") );
		
		hS_b.append(" " );
		
		hS_b.append(date_s); 
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName"));
		
		hS_b.append(" ");
		
		hS_b.append(date_e); 
		
		if(! m_name_contract.isEmpty()) {
			
			hS_b.append(" "); 
			
			hS_b.append(getGuiStrs("dogReport"));
			
			hS_b.append(" ");
			
			hS_b.append(m_name_contract);
		}
		
		
		hS_b.append("</font></h2>");
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader_b.toString()); 
		
		hS_b.append(row_s_b.toString()); 
		
		hS_b.append("</table></body></html>");

		return hS_b.toString();
		 
	}
	
	class ItemChangeListener implements ItemListener{


		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
		         
				//setText(getPrintHtml());
		    }
		}       
	}
	
	
	
	public boolean saveToFile() {
		
		if(m_html_pages == null || m_html_pages.isEmpty()) {return false;}
		
		for(int i =0; i <  m_html_pages.size(); ++i) {
			
			 File path = new File("C:\\sys\\report_page_" + String.valueOf(i) + ".html");

		        FileWriter wr;
		        
				try {
					wr = new FileWriter(path);
	
			        wr.write(m_html_pages.elementAt(i));
	
			        wr.flush();
			         
			        wr.close();
			        
				} catch (IOException e) {
				
					e.printStackTrace();
					
					return false;
				}

		}
		
		return true;
	}
	
	
	
	public void exportToExcelFile(Vector<WsSkladMoveDataColumn>  vec_all_parts) {
		
		String file_to_save = 	excelSaveFileChoose(this);
		
		if (null == file_to_save)  { return; }
	
		OutputStream out;
		
		try {
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			try {
				
				out = new FileOutputStream(file_to_save);
				
			} catch(java.io.FileNotFoundException exf) {
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("cantOpenFileForExportMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				return;
			}
			
			XSSFWorkbook wb = new XSSFWorkbook();
			
			XSSFCreationHelper creationHelper = wb.getCreationHelper();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    createExcelHeader(sheet, creationHelper);
		    
		    int row_index = 1;

	    	for (WsSkladMoveDataColumn dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            XSSFCell cell00 = row.createCell(cell_index++);
		            
		            cell00.setCellValue(row_index - 1);
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
		            cell01.setCellValue(dt.kod);
		        	
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellValue(dt.name);
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(WsUtils. getDF_fix(dt.initial_rest, 3));
	                
	                XSSFCell cell04 = row.createCell(cell_index++);
	                
	                cell04.setCellValue(WsUtils. getDF_fix(dt.in_quantity, 3));
	                
	                XSSFCell cell05 = row.createCell(cell_index++);
	                
	                cell05.setCellValue(WsUtils. getDF_fix(dt.out_quantity, 3));
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                cell06.setCellValue(WsUtils. getDF_fix(dt.rest, 3));
	                
	                XSSFCell cell07 = row.createCell(cell_index++);
	                
	                cell07.setCellValue(WsUtils. getDF_fix(dt.out_quantity_1, 3));
	                
	                XSSFCell cell08 = row.createCell(cell_index++);
	                
	                cell08.setCellValue(WsUtils. getDF_fix(dt.rest - dt.out_quantity_1, 3));	                
	
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

	}
	
	
	private void createExcelHeader( XSSFSheet sheet, XSSFCreationHelper creationHelper) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);
		   
		   createCell(rowHeader0, 0, "", creationHelper);
		   
		   createCell(rowHeader0, 1, getGuiStrs("reportBookKodGoodColumn"), creationHelper);
	
		   createCell(rowHeader0, 2, getGuiStrs("nameColumnReportGoodName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("naPochatReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("pribuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("prihodPartsColumnRestName") , creationHelper);
		   
		   createCell(rowHeader0, 7, getGuiStrs("impDataRestName"), creationHelper);
		   
		   createCell(rowHeader0, 8, getGuiStrs("diffZalName") , creationHelper);
		   		  	
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
		
	}
	

	protected void closeAllEventConnections() {
		
		WsEventDispatcher.get().disconnect(m_contractsCombo);
		
	}
	
	private JPanel createImportPanel( ) {
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(getGuiStrs("importButtonRashodLabel"));
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		panel_main.setBorder(title);
		
		SpinnerModel model1 = new SpinnerNumberModel(0, -1, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(0, -1, 20000, 1); 
		
		m_spinKodColumn = new  JSpinner(model2);
		
		SpinnerModel model4 = new SpinnerNumberModel(1, -1, 20000, 1);
		
		m_spinQuantityColumn = new  JSpinner(model4);
		

		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(path_lab);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add(m_path);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		panel_main.add(panel_path);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(3,2));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		panel_main.add( panel_grid);

		return panel_main;
		
	}
	
	public void onPath() {
		
		String currentFolder = ".";
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), currentFolder, true, false);
		
		int result = sourceFile.showOpenDialog(m_path);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			if(null == sourceFile.getSelectedFile()) { return; }
			
			String name = sourceFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path.setText(name);	
				 
			}
		}
	}
	
	public Vector< WsSkladMoveDataColumn>  importExcelData() {
		
		String excel_file_name = m_path.getText();
		
		int sheetIndex = (int) m_spinSheetColumn.getValue();
		
		int kodIndex = (int) m_spinKodColumn.getValue();
		
		int qnIndex = (int) m_spinQuantityColumn.getValue();
		
		Vector< WsSkladMoveDataColumn> vec_new = new Vector< WsSkladMoveDataColumn>();
		
		if(excel_file_name.isEmpty()) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			  JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			 getMessagesStrs("excelFileNotFoundCaption"), 
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return vec_new;
		}
		
		try {		
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
							    
			XSSFWorkbook wb = null;
						
			try {
				
				wb = new XSSFWorkbook( fStream );
				
			} catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
				
				if(wb != null) { wb.close(); }
			
				return vec_new;
			}
						
			fStream.close();
							    
			XSSFSheet sheet = null;
					    
			try {
			    
				sheet = wb.getSheetAt( sheetIndex);
		    
			}  catch(java.lang.IllegalArgumentException ex) { 
				
				wb.close();
					
				return vec_new; 
				
			}
			    
		    XSSFRow row = null;
				 
		    int rows; 
		    
		    rows = sheet.getPhysicalNumberOfRows();
			
		    WsCatalogKods ct = new  WsCatalogKods();
		    
		    for(int r = 0; r < rows; r++) {
							      
		    	row = sheet.getRow(r);
						    
				if(row == null) { continue; }
						        	
				WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
				
				int kod_imp = WsImportExcelUtil.getKodCell(row, kodIndex);
				        	   	
				d.kod = ct.getKodFromCatalog(kod_imp);
				 
				d.out_quantity_1 = WsImportExcelUtil.getDoubleCell(row, qnIndex);
 				   
				if(d.kod != -1) {
					
					vec_new.add(d); 
				}
				        		
			}
						    
			wb.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
						    				
		return vec_new;
		
	}

}
