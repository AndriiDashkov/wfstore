
package wsreports;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Font;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wscontrols.WsFileTableControl;
import wsdatabase.WsReportsSqlStatements;
import wsdatastruct.WsAgentData;
import wsdatastruct.WsSkladMoveDataColumn;
import wsimport.WsImportExcelUtil;
import wsmain.WsCatalogKods;
import wsmain.WsGuiTools;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsSkladMoveCompareZsuProd  extends WSReportViewer {

	private static final long serialVersionUID = 1L;
	
	Vector<WsSkladMoveDataColumn> m_vec_all = null;
	
	WsFileTableControl m_table_control = null; 
	
	protected JLabel m_infoLabel = new JLabel(getGuiStrs("rasklFoMOdLabel"));
	
	XSSFWorkbook m_wb = null;
	
	XSSFSheet m_sheet = null;
	
	public  WsSkladMoveCompareZsuProd (JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		String[] columnNames = { getGuiStrs("excelImportSourceFileName") };
		
		m_table_control = new WsFileTableControl(columnNames, getGuiStrs("chooserExcelFileLabelName"), true);
		
		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	generateReport();
            	
            }
            
		});
		
		
		m_saveExcelButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	if( !isReportEmpty() ) {
            	
            		exportToExcelFile(m_vec_all);
            	
            	}
            }
		});	
		 
		 setCustomFont();
	}

	private void createGui() {
		
		WsGuiTools.setFixedSizeBehavior(m_table_control);
		
		WsGuiTools.setComponentFixedHeight(m_table_control, 120);

		JPanel panel1 = WsGuiTools.createHorizontalPanel();
		
		panel1 .add(m_table_control);
		 
		panel1.add(Box.createHorizontalGlue());
		
		JPanel panel10 = WsGuiTools.createVerticalPanel();
	
		panel10.add(panel1);
	
		m_control_panel2.add(panel10);
	
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		WsGuiTools.fixComponentHeightToMin(panel10);
		
		m_table_control.setTableToolTips(getGuiStrs("porivzVitZsuProdTableTooltip") );

	}
	
	//vector of report pages
	private Vector<String> generatePages() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int rows_number = m_vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		Vector<String> vec_pages = new Vector<String>();
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
		for(int k = 0; k < pages_number; ++k) { 
			
			String page = getPrintHtml(m_vec_all, start_row, end_row, k);
			
			vec_pages.add(page);
			
			start_row = end_row + 1;
			
			end_row = start_row + rows_per_page - 1;
			
			if(end_row > (m_vec_all.size() -1)) {
				
				end_row = m_vec_all.size() -1;
			}
			
		}
		
		current_font_size = 4;
		
		m_html_pages = vec_pages;
		
		setText(m_html_pages.elementAt(0));
     	
     	pagesNum = m_html_pages.size();
     	
     	currentPage = 1;
     	
     	setDialogCaption();
     	
     	m_viewer.setSelectionStart(0);
     	
     	m_viewer.setSelectionEnd(0);
	
		return vec_pages;
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsSkladMoveDataColumn> vec_all = 
				WsReportsSqlStatements.getPrihodRashodBookForDate2(m_date.getSqlStartDate(), m_date.getSqlEndDate(), -1);
		
		HashMap<Integer, WsSkladMoveDataColumn> foreign_data = importExcel();
		
		//merge
		for(WsSkladMoveDataColumn d : vec_all) {
			
			WsSkladMoveDataColumn df = foreign_data.get(d.kod);
			
			if(df == null) {
				
				foreign_data.put(d.kod, d);
			}
			else {
				
				df.in_quantity += d.in_quantity;
				
				df.out_quantity += d.out_quantity;
				
				df.rest += d.rest;
				
				df.initial_rest += d.initial_rest;
				
			}
		}

		ArrayList<Integer> list = new ArrayList<Integer>(foreign_data.keySet()); 
		
		Collections.sort(list);
		
		vec_all.clear();
		
		for(Integer kod: list) {
			
			 WsSkladMoveDataColumn d = foreign_data.get(kod);
			 
			 vec_all.add(d);
			
		}
		
		vec_all = removeZerosRows(vec_all);
		
		m_vec_all =  vec_all;
		
		Vector<String> vec_pages = generatePages();
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}

	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader_b = new StringBuilder();
		
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>" + getGuiStrs("reportBookKodGoodColumn") + "</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("nameNameInReport") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" +  getGuiStrs("naPochatReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("naPochatProdReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("pribuloReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("pribuloProdReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" +  getGuiStrs("quantityNameVibuloReportColumn") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("quantityNameVibuloReportColumn") + " Prod" + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restNameInReport") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restNameInReport") + " Prod" + "&nbsp;</font></td>");

		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;  ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restDiffInReport")  + "&nbsp;</font></td>");
		
		sHeader_b.append("</tr>");
		
		StringBuilder  row_s_b = new StringBuilder();
		
		for(int i = start; i <= end; ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			 String bottomBorder = "";
			 
			 if(i == end) {
				 
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 
			row_s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			
			row_s_b.append(bottomBorder + "'><font size =4>&nbsp;" );
			
			row_s_b.append(String.valueOf(i + 1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4> &nbsp;" );
			
			row_s_b.append(String.valueOf(d.kod) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(d.name );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.initial_rest) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;"); 
			
			row_s_b.append(WsUtils.getDF(d.initial_rest_1)); 
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.in_quantity) + "&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.in_quantity_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.out_quantity) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.out_quantity_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-right: 1px solid; border-top: 1px solid  ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest_1 - d.rest) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("</tr>");
		
		}
		
		StringBuilder hS_b = new StringBuilder();
		
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append( "        height: 297mm;\r\n");
		
		hS_b.append( "        width: 210mm;\r\n");
		
		hS_b.append( "        /* to centre page on screen*/\r\n");
		
		hS_b.append( "        margin-left: auto;\r\n");
		
		hS_b.append( "        margin-right: auto;\r\n");
		
		hS_b.append( "    }");
		
		hS_b.append( "</style><body>");
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("bookSkladMovementCompareReportName") );
		
		hS_b.append(" " );
		
		hS_b.append( date_s);
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append(" ");
		
		hS_b.append(date_e ); 
		
		hS_b.append("</font></h2>");
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader_b.toString() );
		
		hS_b.append(row_s_b.toString() ); 
		
		hS_b.append("</table></body></html>");// + sFooter;

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
	
	private HashMap<Integer, WsSkladMoveDataColumn> importExcel() {
		
		int kod_column = 0;
		
		int name_column = 1;
		
		int initial_rest_column = 4;
		
		int in_quantity_column = 6;
		
		int out_quantity_column = 8;
		
		int rest_column = 10;
		
		int sheet_index = 0;
		
		HashMap<Integer, WsSkladMoveDataColumn> map = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		String excel_file_name = null;
		
		Vector<WsAgentData> vec = m_table_control.getData();
		
		 WsCatalogKods ct = new  WsCatalogKods();
		 
	    for(WsAgentData dfc : vec){
	    	
	    	String fc = dfc.contact;
				  
			try {

				excel_file_name = fc;
				
				if(excel_file_name == null || excel_file_name.isEmpty()) {   continue; }
						
				FileInputStream fStream = new FileInputStream(excel_file_name);
					    
				XSSFWorkbook wb = null;
				
				try {
					
					wb = new XSSFWorkbook( fStream );
					
				} catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
					
					if(wb != null) { wb.close(); }
					
					return map;
				}
				
				fStream.close();
					    
			    XSSFSheet sheet = null;
			    
				try {
				    
					sheet = wb.getSheetAt(sheet_index);
			    
				}  catch(java.lang.IllegalArgumentException ex) { 
					
					if(wb != null)  { wb.close(); }
					
					return map; 
					
				}
					    
			    XSSFRow row;
					 
			    int rows; // No of rows
			    
			    rows = sheet.getPhysicalNumberOfRows();
				
				int cols = 0; // No of columns
					    
				int tmp = 0;
				
				// This trick ensures that we get the data properly even if it doesn't start from first few rows
				for(int i = 0; i < 10 || i < rows; i++) {
					    	
			        row = sheet.getRow(i);
			        
			        if(row != null) {
			        	
			            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
			            
			            if(tmp > cols) cols = tmp;
			        }
			    }	   
					    
			    for(int r = 0; r < rows; r++) {
					      
				    row = sheet.getRow(r);
				    
				    if(row == null) { continue; }
				        	
		        	WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
		        	
		        	d.kod = ct.getKodFromCatalog(WsImportExcelUtil.getKodCell(row, kod_column));
	        		
	        		if(d.kod == WsUtils.UNKNOWN_KOD) { continue;}
	        		
	        		d.initial_rest_1 = WsImportExcelUtil.getDoubleCell(row, initial_rest_column);
	        		
	        		d.in_quantity_1 = WsImportExcelUtil.getDoubleCell(row, in_quantity_column);
	        		
	        		d.out_quantity_1 = WsImportExcelUtil.getDoubleCell(row, out_quantity_column);
	        		
	        		d.rest_1 = WsImportExcelUtil.getDoubleCell(row, rest_column);
	        		
	        		d.name = WsImportExcelUtil.getStringCell(row,  name_column);
	        		
	        		WsSkladMoveDataColumn d_f = map.get(d.kod);
	        		
	        		if(d_f == null) {
	        			
	        			map.put(d.kod, d);
	        		}
	        		else {
	        			
	        			d_f.in_quantity_1 += d.in_quantity_1;
	        			
	        			d_f.out_quantity_1 += d.out_quantity_1;
	        			
	        			d_f.initial_rest_1 += d.initial_rest_1;
	        			
	        			d_f.rest_1 += d.rest_1;
	        		}  
		        		
			    }
					    
			    wb.close();
					    
			} catch(Exception  ioe) {
							
				ioe.printStackTrace();
						    
			}
	    }
					
		return map;
		
	}
	
	public void exportToExcelFile(Vector<WsSkladMoveDataColumn>  vec) {
		
	
		Vector<WsSkladMoveDataColumn>  vec_all_parts = removeZerosRows(vec);
		
		String file_to_save =  excelSaveFileChoose(this);
		
		if(null == file_to_save) return;
	
		OutputStream out;
		
		try {
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			
			try {
				
				out = new FileOutputStream(file_to_save);
				
			} catch(java.io.FileNotFoundException exf) {
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				WsUtils.showMessageDialog(getMessagesStrs("cantOpenFileForExportMessage"));
				
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
	            
	            XSSFCell cell01 = row.createCell(cell_index++);
	            
	            cell01.setCellValue(dt.kod);
	        	
                XSSFCell cell02 = row.createCell(cell_index++);
                
                cell02.setCellValue(dt.name);
                
                XSSFCell cell03 = row.createCell(cell_index++);
                
                cell03.setCellValue(WsUtils. getDF_0(dt.initial_rest));
                
                XSSFCell cell03_1 = row.createCell(cell_index++);
                
                cell03_1.setCellValue(WsUtils. getDF_0(dt.initial_rest_1));
                
                XSSFCell cell04 = row.createCell(cell_index++);
                
                cell04.setCellValue(WsUtils. getDF_0(dt.in_quantity));
                
                XSSFCell cell04_1 = row.createCell(cell_index++);
                
                cell04_1.setCellValue(WsUtils. getDF_0(dt.in_quantity_1));
                
                XSSFCell cell05 = row.createCell(cell_index++);
                
                cell05.setCellValue(WsUtils. getDF_0(dt.out_quantity));
                
                XSSFCell cell05_1 = row.createCell(cell_index++);
                
                cell05_1.setCellValue(WsUtils. getDF_0(dt.out_quantity_1));
                
                XSSFCell cell06 = row.createCell(cell_index++);
                
                cell06.setCellValue(WsUtils. getDF_0(dt.rest));
                
                XSSFCell cell06_1 = row.createCell(cell_index++);
                
                cell06_1.setCellValue(WsUtils. getDF_0(dt.rest_1));
	                
	         }
	
			wb.write(out);
	
			out.close();
	    
			wb.close(); 
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportSuccessMessage"));
    
		} catch (IOException  e) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	
			e.printStackTrace();
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportFailedMessage"));
		}

	}
	
	
	private void createExcelHeader( XSSFSheet sheet, XSSFCreationHelper creationHelper) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);
		   
		   createCell(rowHeader0, 0, getGuiStrs("kodNameInReport"), creationHelper);
	
		   createCell(rowHeader0, 1, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 2, getGuiStrs("naPochatReportName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("naPochatProdReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("pribuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("pribuloProdReportName"), creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);
		   
		   createCell(rowHeader0, 7, getGuiStrs("quantityNameVibuloReportColumn") + " Prod", creationHelper);
		   
		   createCell(rowHeader0, 8, getGuiStrs("restNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 9, getGuiStrs("restNameInReport") + " Prod", creationHelper);
		   
	}
	
	public Vector<WsSkladMoveDataColumn> removeZerosRows(Vector<WsSkladMoveDataColumn>  vec) {
		
		Vector<WsSkladMoveDataColumn>  new_vec = new Vector<WsSkladMoveDataColumn>();
		
		double zL = WsUtils.getRZL();
		
		for(WsSkladMoveDataColumn d: vec) {
			
			boolean flag = d.initial_rest >zL || d.initial_rest_1 > zL || d.in_quantity > zL || 
					d.in_quantity_1 > zL || d.out_quantity > zL ||
					d.out_quantity_1 > zL || d.rest > zL || d.rest_1 > zL;
					
			if(flag) {
				
				new_vec.add(d);
			}
		
		}
		
		return new_vec;
	
	}

	public void dispose() {
		
		try {
			
			if(m_wb != null)  { m_wb.close(); }
			
		} catch (IOException e) {
	
			e.printStackTrace();
		}
		
		super.dispose();

	}
	
	
	protected void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
		
	//	if(m_border != null) {
			
		//	m_border.setTitleFont(f);
			
		//}
	}
}
