package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsSignsControlPanel;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsReportsSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladMovementReport4 extends WSReportViewer {

	Vector<WsSkladMoveDataColumn>  m_vec_all = null;
	
	WsSignsControlPanel m_p_panel = null;
	
	int m_id_contract = -1;
	
	String m_name_contract = "";
	
	public static java.sql.Date m_date_start_static = null;
	
	public static java.sql.Date m_date_end_static = null;
	 
	public   WsSkladMovementReport4(JFrame f, String nameFrame) {
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
		 
		 setCustomFont();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		m_p_panel = new WsSignsControlPanel();
		
		JPanel panelV = WsGuiTools.createVerticalPanel();
		
		panelV.add(m_p_panel);
		
		m_control_panel2.add(panelV);
		
		m_control_panel2.add(Box.createHorizontalGlue());
		
		if(m_date_start_static == null || m_date_end_static == null) {
			
			m_date.setCurrentStartDate();
		
			m_date.setCurrentEndDate();
		}
		else {
			
			m_date.setStartDate(m_date_start_static);
			
			m_date.setEndDate(m_date_end_static);
		}
	
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsSkladMoveDataColumn> vec_all = WsReportsSqlStatements.getMovePartsForAllContracts(m_date.getSqlStartDate(),
				m_date.getSqlEndDate());
		
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
			
			vec_pages.add(  getPrintHtml(vec_all, start_row, end_row, k) );

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
	
	private String getBoldFont( boolean flag, String name) {
		
		if(flag) {
			
			return "<b>" + name + "</b>";
		}
		else {
			
			return name;
		}
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
		
		sHeader_b.append(getGuiStrs("sumZalReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("pribuloReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;"); 
		
		sHeader_b.append(getGuiStrs("sumInReport") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;"); 
		
		sHeader_b.append(getGuiStrs("quantityNameVibuloReportColumn") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;"); 
		
		sHeader_b.append(getGuiStrs("sumVibuloReportColumn") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("prihodPartsColumnRestName"));
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid ; text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("sumColumnRestName"));
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("</tr>");

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
			  
			 row_s_b.append(i + 1 ); 
			
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			 
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4> &nbsp;"); 
			 
			 if(d.id_contract != -1) {
				 
				 row_s_b.append(String.valueOf(d.kod) ); 
			 }
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  ");  
			
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			  
			 row_s_b.append(getBoldFont( d.id_contract == -1, d.name) ); 
				 
		     row_s_b.append("&nbsp;</font></td>");  
		     
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].initial_rest) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(getBoldFont( d.id_contract == -1,WsUtils.getDF(d.q_array[0].initial_rest_sum)) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].in_quantity) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(getBoldFont( d.id_contract == -1,WsUtils.getDF(d.q_array[0].in_quantity_sum)) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].out_quantity) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(getBoldFont( d.id_contract == -1,WsUtils.getDF(d.q_array[0].out_quantity_sum) )); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
		     row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
		     
			 row_s_b.append(bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append( WsUtils.getDF(d.q_array[0].rest) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ;");  
				
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(getBoldFont( d.id_contract == -1,WsUtils.getDF(d.q_array[0].rest_sum)) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("</tr>");
		
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
		
		hS_b.append(formApproveHeader(m_p_panel.getApprovePerson()));
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append(getGuiStrs("bookSkladMovementReportName2") );
		
		hS_b.append(" " );
		
		hS_b.append(date_s); 
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName"));
		
		hS_b.append(" ");
		
		hS_b.append(date_e); 
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("zdogReport"));
		
		hS_b.append(" \n");
			
		hS_b.append("</font></h2>");
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader_b.toString()); 
		
		hS_b.append(row_s_b.toString()); 
		
		hS_b.append("</table><br>");
		
		hS_b.append(formPidpFooter(m_p_panel.getP1Person()));
				
		hS_b.append("<br>");
				
		hS_b.append(formPidpFooter(m_p_panel.getP2Person()));
		
		hS_b.append("<br>");
		
		hS_b.append("</body></html>");

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
	
	

	
	public void exportToExcelFile(Vector<WsSkladMoveDataColumn> vec_all_parts) {
		

		String file_to_save = 	excelSaveFileChoose(this);
		
		if (null == file_to_save)  { return; }
		
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
			
			XSSFCellStyle st01 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st02 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
			
			
			XSSFCellStyle st03 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
			
			XSSFFont dFont = wb.createFont();
			
			dFont.setBold(true);
			
			st03.setFont(dFont);
		
			XSSFCreationHelper creationHelper = wb.getCreationHelper();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    int row_index = createExcelHeader(wb, sheet, creationHelper);
		    
		    ++row_index;

	    	for (WsSkladMoveDataColumn dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            XSSFCell cell00 = row.createCell(cell_index++);
		            
		            cell00.setCellValue(row_index - 4);
		            
		            cell00.setCellStyle(st01);
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
					if(dt.id_contract != -1) {
						 
						 cell01.setCellValue(dt.kod);
					}
					else {
						 
						 cell01.setCellValue(""); 
					}
		            
		            cell01.setCellStyle(st01);
		        	
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellValue( dt.name);
	                
		            setExcelCellStyle( cell02, dt.id_contract == -1,st03, st02);

	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(WsUtils. getDF_fix(dt.q_array[0].initial_rest, 3));
	                
	                cell03.setCellStyle(st01);
	                
	                XSSFCell cell04 = row.createCell(cell_index++);
	                
	                setExcelCellStyle( cell04, dt.id_contract == -1,st03, st01);

	                cell04.setCellValue(WsUtils. getDF_fix(dt.q_array[0].initial_rest_sum, 3));
	                
	                XSSFCell cell05 = row.createCell(cell_index++);
	                
	                cell05.setCellStyle(st01);
	                
	                cell05.setCellValue(WsUtils. getDF_fix(dt.q_array[0].in_quantity, 3));
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                setExcelCellStyle( cell06, dt.id_contract == -1,st03, st01);
	                
	                cell06.setCellValue(WsUtils. getDF_fix(dt.q_array[0].in_quantity_sum, 3));
	                
	                XSSFCell cell07 = row.createCell(cell_index++);
	                
	                cell07.setCellStyle(st01);
	                
	                cell07.setCellValue(WsUtils. getDF_fix(dt.q_array[0].out_quantity, 3));
	          
	                XSSFCell cell08 = row.createCell(cell_index++);
	                
	                setExcelCellStyle( cell08, dt.id_contract == -1,st03, st01);
	                
	                cell08.setCellValue(WsUtils. getDF_fix(dt.q_array[0].out_quantity_sum, 3));
				 
	                XSSFCell cell09 = row.createCell(cell_index++);
	                
	                cell09.setCellValue(WsUtils. getDF_fix(dt.q_array[0].rest, 3));
	                
	                cell09.setCellStyle(st01);

	                XSSFCell cell10 = row.createCell(cell_index++);
	                
	                setExcelCellStyle( cell10, dt.id_contract == -1,st03, st01);
	                
	                cell10.setCellValue(WsUtils. getDF_fix(dt.q_array[0].rest_sum, 3));
	      
	        }
	    	
        XSSFRow row = sheet.createRow(row_index++);
	        
	        row = sheet.createRow(row_index++);
	        
	        if(m_p_panel.getP1Person() != null) {
	        	
	        	XSSFCell c1 = row.createCell(0);
	        	
	        	c1.setCellValue(m_p_panel.getP1Person().position);
	        	
	        	sheet.addMergedRegion(new CellRangeAddress(row_index - 1,row_index - 1,0,4));
	        	
	        	row = sheet.createRow(row_index++);
	        	
	        	c1 = row.createCell(0);
	        	
	        	c1.setCellValue(m_p_panel.getP1Person().rank + " ____________ " + m_p_panel.getP1Person().name);
	        	
	        	sheet.addMergedRegion(new CellRangeAddress(row_index - 1,row_index - 1,0,6));
	        	
	        }
	        
	        row = sheet.createRow(row_index++);
	        
	        row = sheet.createRow(row_index++);
	        
	        if(m_p_panel.getP2Person() != null) {
	        	
	        	XSSFCell c1 = row.createCell(0);
	        	
	        	c1.setCellValue(m_p_panel.getP2Person().position);
	        	
	        	sheet.addMergedRegion(new CellRangeAddress(row_index - 1,row_index - 1,0,4));
	        	
	        	row = sheet.createRow(row_index++);
	        	
	        	c1 = row.createCell(0);
	        	
	        	c1.setCellValue(m_p_panel.getP2Person().rank + " ____________ " + m_p_panel.getP2Person().name);
	        	
	        	sheet.addMergedRegion(new CellRangeAddress(row_index - 1,row_index - 1,0,6));
	        	
	   
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
	
	
	private int createExcelHeader(  XSSFWorkbook wb, XSSFSheet sheet, XSSFCreationHelper creationHelper) {
		
		
			XSSFCellStyle st0_right = getExcelCellStyle(wb, 0, 0, 0, 0, 
				   false, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st0 = getExcelCellStyle(wb, 0, 0, 0, 0, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st01 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		
		   String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
			
		   String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
	
		   int rows_count = 0;
			
			XSSFRow rowHeader = sheet.createRow( rows_count);
			
			if(m_p_panel.getApprovePerson() != null) {
				
				XSSFCell cl1 = createCell(rowHeader, 0, getGuiStrs("apprLabelRep"), creationHelper); 
				   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,11));
				
				cl1.setCellStyle(st0_right);
			   
			    rowHeader = sheet.createRow(++rows_count);
			    
			    cl1 = createCell(rowHeader, 0, m_p_panel.getApprovePerson().position, creationHelper); 
				   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,11));
				
				cl1.setCellStyle(st0_right);
				
				rowHeader = sheet.createRow(++rows_count);
				    
				cl1 = createCell(rowHeader, 0, m_p_panel.getApprovePerson().rank + "____________" +
						m_p_panel.getApprovePerson().name, creationHelper); 
					   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,11));
					
				cl1.setCellStyle(st0_right);
				
				rowHeader = sheet.createRow(++rows_count);
			    
				cl1 = createCell(rowHeader, 0," '____' ________ _______ ", creationHelper); 
					   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,11));
					
				cl1.setCellStyle(st0_right);
				
				rowHeader = sheet.createRow(++rows_count);
		    
		   }
		   
		   XSSFRow rowHeader02 = sheet.createRow(++rows_count);
		   
		   StringBuilder sb = new StringBuilder();

		   sb.append( getGuiStrs("bookSkladMovementReportName2")); 
		   
		   sb.append( " "); 
		   
		   sb.append( date_s); 
		   
		   sb.append( " ");
		   
		   sb.append( getGuiStrs("bookSkladPoReportName"));
		   
		   sb.append( " ");
		   
		   sb.append( date_e);
		   
		   sb.append( " ");
		   
		   sb.append( getGuiStrs("zdogReport"));
		   
		   sb.append(" " );
		   
		   XSSFCell cl1 = createCell(rowHeader02, 0, sb.toString(), creationHelper);
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count, rows_count, 0, 11));
		   
		   cl1.setCellStyle(st0);
		  
		   sheet.createRow(++rows_count);
		
		   XSSFRow rowHeader0 = sheet.createRow(++rows_count);
		   
		   cl1 = createCell(rowHeader0, 0, "", creationHelper);
		   
		   cl1.setCellStyle(st01);
		   			
		   cl1 = createCell(rowHeader0, 1, getGuiStrs("reportBookKodGoodColumn"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 2, getGuiStrs("nameColumnReportGoodName"), creationHelper);
	
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 3, getGuiStrs("naPochatReportName"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 4, getGuiStrs("sumZalReportName"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 5, getGuiStrs("pribuloReportName"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 6, getGuiStrs("sumInReport"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 7, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 8, getGuiStrs("sumVibuloReportColumn"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 9, getGuiStrs("prihodPartsColumnRestName"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader0, 10, getGuiStrs("sumColumnRestName"), creationHelper);
			
		   cl1.setCellStyle(st01);
		     
		   return rows_count;
		   	  	
	}
	
	protected void closeAllEventConnections() {
		
		
	}

}
