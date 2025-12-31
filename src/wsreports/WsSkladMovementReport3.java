
package wsreports;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;
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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wscontrols.WsContractsChooser;
import wscontrols.WsSignsControlPanel;
import wsdatabase.WsReportsSqlStatements;
import wsdatastruct.WsContractData;
import wsdatastruct.WsSkladMoveDataColumn;
import wsmain.WsGuiTools;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladMovementReport3 extends WSReportViewer {

	Vector<WsSkladMoveDataColumn>  m_vec_all = null;
	
	WsContractsChooser m_contractsList = null;
	
	WsSignsControlPanel m_p_panel = null;
	
	int m_id_contract = -1;
	
	String m_name_contract = "";
	
	public static java.sql.Date m_date_start_static = null;
	
	public static java.sql.Date m_date_end_static = null;
	 
	public   WsSkladMovementReport3(JFrame f, String nameFrame) {
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
		
		m_contractsList =  new  WsContractsChooser();
		
		JPanel panelV = WsGuiTools.createVerticalPanel();
		
		JPanel panelH = WsGuiTools.createHorizontalPanel();
		
		panelH.add(m_contractsList);
		
		panelH.add(Box.createHorizontalGlue());
		
		panelV.add(panelH);
		
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

		WsGuiTools.setComponentFixedWidth(m_contractsList, 800);
		
		WsGuiTools.setComponentFixedHeight(m_contractsList, 150);
		
		m_contractsList.refresh();
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		Vector< WsContractData> vec_contracts =  m_contractsList.getChoosenData();
		
		if(vec_contracts.isEmpty()) {
			
			WsUtils.showMessageDialog(getMessagesStrs("noContrForReportMessage"));
			
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			return null;
			
		}
		
		Vector<WsSkladMoveDataColumn> vec_all = WsReportsSqlStatements.getMovePartsForContracts(m_date.getSqlStartDate(),
				m_date.getSqlEndDate(), vec_contracts);
		
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
			
			vec_pages.add(  getPrintHtml(vec_all, start_row, end_row, k, vec_contracts) );

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
	
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number, 
			Vector< WsContractData> vec_contracts) {
		
		StringBuffer sb_c = new StringBuffer();
	
		if(vec_contracts != null && !vec_contracts.isEmpty()) {
	
			for(int i = 0; i < vec_contracts.size(); ++i) {
    	   
				sb_c.append(String.valueOf(vec_contracts.elementAt(i).number));
    	   
				if(i !=  (vec_contracts.size() - 1)) {
    		   
					sb_c.append(","); 
				}
    	   
			}
			
	   }
		
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
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("sumColumnRestName"));
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("contractName"));
		
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
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].initial_rest) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].initial_rest_sum) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].in_quantity) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].in_quantity_sum) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].out_quantity) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append( bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;");  
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].out_quantity_sum) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
		     row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
		     
			 row_s_b.append(bottomBorder ); 
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].rest) ); 
			 
			 row_s_b.append("&nbsp;</font></td>"); 
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;");  
				
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].rest_sum) ); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; ");  
				
			 row_s_b.append(bottomBorder);  
			 
			 row_s_b.append("'><font size =4>&nbsp;" ); 
			 
			 row_s_b.append(d.contract_name ); 
			 
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
			
		hS_b.append(sb_c.toString());
			
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
		
			XSSFCreationHelper creationHelper = wb.getCreationHelper();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    int row_index = createExcelHeader(wb, sheet, creationHelper);
		    
		    ++row_index;

	    	for (WsSkladMoveDataColumn dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            XSSFCell cell00 = row.createCell(cell_index++);
		            
		            cell00.setCellValue(row_index - 1);
		            
		            cell00.setCellStyle(st01);
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
		            cell01.setCellValue(dt.kod);
		            
		            cell01.setCellStyle(st01);
		        	
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellValue(dt.name);
	                
	                cell02.setCellStyle(st01);
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(WsUtils. getDF_fix(dt.q_array[0].initial_rest, 3));
	                
	                cell03.setCellStyle(st01);
	                
	                XSSFCell cell04 = row.createCell(cell_index++);
	                
	                cell04.setCellStyle(st01);
	                
	                cell04.setCellValue(WsUtils. getDF_fix(dt.q_array[0].initial_rest_sum, 3));
	                
	                XSSFCell cell05 = row.createCell(cell_index++);
	                
	                cell05.setCellStyle(st01);
	                
	                cell05.setCellValue(WsUtils. getDF_fix(dt.q_array[0].in_quantity, 3));
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                cell06.setCellStyle(st01);
	                
	                cell06.setCellValue(WsUtils. getDF_fix(dt.q_array[0].in_quantity_sum, 3));
	                
	                XSSFCell cell07 = row.createCell(cell_index++);
	                
	                cell07.setCellStyle(st01);
	                
	                cell07.setCellValue(WsUtils. getDF_fix(dt.q_array[0].out_quantity, 3));
	          
	                XSSFCell cell08 = row.createCell(cell_index++);
	                
	                cell08.setCellStyle(st01);
	                
	                cell08.setCellValue(WsUtils. getDF_fix(dt.q_array[0].out_quantity_sum, 3));
				 
	                XSSFCell cell09 = row.createCell(cell_index++);
	                
	                cell09.setCellValue(WsUtils. getDF_fix(dt.q_array[0].rest, 3));
	                
	                cell09.setCellStyle(st01);

	                XSSFCell cell10 = row.createCell(cell_index++);
	                
	                cell10.setCellStyle(st01);
	                
	                cell10.setCellValue(WsUtils. getDF_fix(dt.q_array[0].rest_sum, 3));
	
	                XSSFCell cell11 = row.createCell(cell_index++);
	                
	                cell11.setCellValue(dt.contract_name);
	                
	                cell11.setCellStyle(st01);
				      
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
			
		   Vector< WsContractData> vec_contracts =  m_contractsList.getChoosenData();
		
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
		   
		   if(  vec_contracts != null && ! vec_contracts.isEmpty() ) {
				
				for(int i = 0; i < vec_contracts.size(); ++i) {
					
					 WsContractData dc = vec_contracts.elementAt(i);
					 
					 sb.append( dc.number);
					 
					 if( i != (vec_contracts.size() -1) ) {
						 
						 sb.append( ",  ");
					 }
					 
					
				}	
		   }
		   
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
		   
		   cl1 = createCell(rowHeader0, 11, getGuiStrs("contractName"), creationHelper);
			
		   cl1.setCellStyle(st01);
		   
		   return rows_count;
		   	  	
	}
	
	protected void closeAllEventConnections() {
		
		
	}

}
