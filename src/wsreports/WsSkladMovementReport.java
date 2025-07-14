
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

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wscontrols.WsSignsControlPanel;
import wsdatabase.WsReportsSqlStatements;
import wsdatabase.WsUtilSqlStatements;
import wsdatastruct.WsInfoData;
import wsdatastruct.WsSkladMoveDataColumn;
import wsdatastruct.WsSkladMoveDataRow;
import wsmain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladMovementReport extends WSReportViewer {

	Vector<WsSkladMoveDataRow> m_vec_all_parts = null;
	
	WsSignsControlPanel m_p_panel = null;

	public WsSkladMovementReport(JFrame f, String nameFrame) {
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
             
            }
            
		});
		
		 m_saveExcelButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	if( !isReportEmpty() ) {
	            	
	            		exportToExcelFile(m_vec_all_parts);
	            	
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
		
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		m_p_panel = new WsSignsControlPanel();
		
		m_control_panel2.add(m_p_panel);
		
		m_control_panel2.add(Box.createHorizontalGlue());
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		Vector<String> vec_pages = new Vector<String>();
		
		if( !WsUtils.isMonday(m_date.getSqlStartDate()) ) {
			
			WsUtils.showMessageDialog(getMessagesStrs("dateNotMondayMessage"));
			
			return  vec_pages;
		}
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsSkladMoveDataRow> vec_all_parts = 
			WsReportsSqlStatements.getPrihodRashodBookForDate(m_date.getSqlStartDate(), m_date.getSqlEndDate(), -1);
		
		int columns_number = vec_all_parts.elementAt(0).row_vec.size();
		
		//divide into pages
		int columns_per_page = 8;
		
		int pages_number =  (int)(columns_number /columns_per_page);
		
		if((pages_number *columns_per_page) < columns_number ) { pages_number++;}
		
		for(int i = 0; i <  vec_all_parts.size(); ++i) {
			
			WsSkladMoveDataRow row = vec_all_parts.elementAt(i);
			
			row.pages_row_vec = new Vector< Vector<WsSkladMoveDataColumn> >();
			
			row.pages_number = pages_number;
			
			Vector<WsSkladMoveDataColumn> all_columns = row.row_vec;
			 
			int column_index = 0;
			
			for(int k = 0; k < pages_number; ++k) {   
				
				Vector<WsSkladMoveDataColumn> p = new Vector<WsSkladMoveDataColumn>();
				
				row.pages_row_vec.add(p);   
				
				int cols = columns_per_page;
				
				if (k == ( pages_number - 1)) {
					
					cols =  columns_number - ((pages_number -1) * columns_per_page);
				}
				
				for(int j1 = 0; j1 < cols; ++j1) {
					
	
					p.add(all_columns.elementAt(column_index++));
					
				}
	
			}
			
		}

		for(int k = 0; k < pages_number; ++k) { 
			
			String page = getPrintHtml(vec_all_parts, k);
			
			vec_pages.add(page);
		}
		
		m_vec_all_parts = vec_all_parts;
		 
		current_font_size = 4;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}
	
	
	public String getPrintHtml(Vector<WsSkladMoveDataRow> vec_all_parts, int page_number) {
		
		StringBuilder sHeader01_b = new StringBuilder();	
				
		sHeader01_b.append("<td  colspan='3' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("reportBookNameGoodColumn") );
		
		sHeader01_b.append("</td>");
		
		StringBuilder sHeader02_b = new StringBuilder(); 
				
		sHeader02_b.append("<td  colspan='3' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>"); 
		
		sHeader02_b.append(getGuiStrs("reportBookKodGoodColumn") );
		
		sHeader02_b.append("</font></td>");

		StringBuilder sHeader3_b = new StringBuilder();
		
		sHeader3_b.append("<tr><td   style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>"); 
		
		sHeader3_b.append(getGuiStrs("nmbStr") );
		
		sHeader3_b.append("</td>");
		
		sHeader3_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader3_b.append(getGuiStrs("reportBookInDateNameColumn") );
		
		sHeader3_b.append("</td>");
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader3_b.append(getGuiStrs("reportBookNameColumn") );
		
		sHeader3_b.append("</td>");
		
		StringBuilder row_s_b = new StringBuilder();
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.pages_row_vec.elementAt( page_number);

			String bottomBorder = "";
			 
			if(i == (vec_all_parts.size() -1)) {
				
				 bottomBorder = "border-bottom: 1px solid ;"; 
			}
			  
			StringBuilder nameRow = new StringBuilder();
			
			String inDate ="";
			 
			switch(d_row.indexData) {
			 
				 case 0:{ 
					 
					 nameRow.append("&nbsp; " );
					 
					 nameRow.append(getGuiStrs("zalishokNaReportName")); 
					 
					 nameRow.append(" ");  
					 
					 nameRow.append(WsUtils.dateToString(d_row.date_end, "dd.MM.yy" ));
					 
					 inDate = WsUtils.dateToString(d_row.date_end, "dd.MM.yy" );
					 
					 break;
				 }
				 case 1:{
					 nameRow.append("&nbsp; " );
					 
					 nameRow.append(getGuiStrs("zvPribReportBookName"));
					 
					 nameRow.append(" ");
					 
					 nameRow.append(WsUtils.dateToString(d_row.date_start, "dd.MM.yy" ));
					 
					 nameRow.append(" "); 
					 
					 nameRow.append(getGuiStrs("bookSkladPoReportName") );
					 
					 nameRow.append(" ");
					 
					 nameRow.append(WsUtils.dateToString(d_row.date_end, "dd.MM.yy" ));
					 
					 inDate = WsUtils.dateToString(d_row.date_end, "dd.MM.yy" );
					 
					 break;
				 }
				 case 2 : {
					 
					 nameRow.append("&nbsp; " );
					 
					 nameRow.append(getGuiStrs("zvVitratBookReportName") );
					 
					 nameRow.append(" " );
					 
					 nameRow.append(WsUtils.dateToString(d_row.date_start, "dd.MM.yy" )); 
					 
					 nameRow.append(" " );
					 
					 nameRow.append(getGuiStrs("bookSkladPoReportName") );
					 
					 nameRow.append(" " );
					 
					 nameRow.append( WsUtils.dateToString(d_row.date_end, "dd.MM.yy" ));
					 
					 inDate = WsUtils.dateToString(d_row.date_end, "dd.MM.yy" );
					 
					 break;
				 }
			 };
			
			 row_s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; " );
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;" );
		    
			 row_s_b.append(String.valueOf(i + 1) );
			 
			 row_s_b.append("&nbsp;</font></td>");
			
			 row_s_b.append("<td nowrap style=' max-width: 120px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			 row_s_b.append(bottomBorder );
			
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(inDate );
			
			 row_s_b.append("&nbsp;</font></td>");
			
			 row_s_b.append("<td nowrap style=' min-width: 300px; border-left: 1px solid; border-top: 1px solid ;  " );
			 
			 row_s_b.append(bottomBorder );
			
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(nameRow.toString() );
			 
			 row_s_b.append("&nbsp;</font></td>");
			
			for(int j = 0; j < row_vec.size(); ++j) {
			
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 String rightBorder = "";
				
				 if(j == (row_vec.size() -1)) {
					 
					 rightBorder = "border-right: 1px solid ;";
				 }
				 
				 
				row_s_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; " );
				
				row_s_b.append(bottomBorder );
				
				row_s_b.append(" text-align: center; '><font size =4>"); 
				
				row_s_b.append( WsUtils.getDF(dc.in_quantity) );
				
				row_s_b.append("</font></td>");
				
			    row_s_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
			    row_s_b.append(bottomBorder );
				
			    row_s_b.append(" text-align: center; '><font size =4>"); 
				
			    row_s_b.append(WsUtils.getDF(dc.out_quantity) +  "</font></td>");
					 
				row_s_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; " );
				
				row_s_b.append(bottomBorder );
				
				row_s_b.append(rightBorder );
				
				row_s_b.append("text-align: center; '><font size =4>" );
				
				row_s_b.append(WsUtils.getDF(dc.rest) );
				
				row_s_b.append("</font></td>");
		
				if( i == 0) {
					 
					 sHeader02_b.append("<td colspan='3' style='border-left: 1px solid;border-top: 1px solid ;"); 
					 
					 sHeader02_b.append(rightBorder );
					 
					 sHeader02_b.append(" text-align: center;'><font size =4>" );
					
					 sHeader02_b.append(String.valueOf(dc.kod) );
					 
					 sHeader02_b.append("</font></td>");
			
					 sHeader01_b.append( "<td colspan='3' style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 sHeader01_b.append(rightBorder );
					 
					 sHeader01_b.append(" text-align: center;'><font size =4>" );
					
					 sHeader01_b.append(dc.name); 
					 
					 sHeader01_b.append("</font></td>");
					
					 sHeader3_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" );
					
					 sHeader3_b.append(getGuiStrs("quantityNameReportColumn") );
					
					 sHeader3_b.append("</font></td>");
					 
					 sHeader3_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" );
					 
					 sHeader3_b.append(getGuiStrs("rashodNameReportColumn") );
					
					 sHeader3_b.append("</font></td>");
					 
					 sHeader3_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; " );
					
					 sHeader3_b.append(rightBorder );
					
					 sHeader3_b.append(" text-align: center;'><font size =4>");
					
					 sHeader3_b.append(getGuiStrs("restNameReportColumn") );
					
					 sHeader3_b.append("</font></td>");
					 
				 }
			}
			
			row_s_b.append("</tr>");
		}
		
		sHeader02_b.append("</tr>");
		
		sHeader01_b.append("</tr>");
		
		sHeader3_b.append("</tr>");
		
		StringBuilder hS_b = new StringBuilder(); 
		
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append("        height: 210mm;\r\n");
		
		hS_b.append("        width: 297mm;\r\n");
		
		hS_b.append( "        /* to centre page on screen*/\r\n");
		
		hS_b.append( "        margin-left: 20;\r\n");
		
		hS_b.append( "        margin-right: auto;\r\n");
		
		hS_b.append( "    }");
		
		hS_b.append("</style><body>");
		
		hS_b.append(formApproveHeader(m_p_panel.getApprovePerson()));
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append(getReportCaption());
		
		hS_b.append(" </font></h2>");
		
		hS_b.append( "<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader01_b.toString() );
		
		hS_b.append(sHeader02_b.toString()); 
		
		hS_b.append(sHeader3_b.toString()); 
		
		hS_b.append(row_s_b.toString());// + sFooter;
		
		hS_b.append("</table><br>");
				
		hS_b.append(formPidpFooter(m_p_panel.getP1Person()));
				
		hS_b.append("<br>");
				
		hS_b.append(formPidpFooter(m_p_panel.getP2Person()));
		
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
	
	public void exportToExcelFile(Vector<WsSkladMoveDataRow> vec_all_parts) {
		
		String file_to_save = 	excelSaveFileChoose(this);
		
		if (null == file_to_save)  { return; }
	
		OutputStream out;
		
		try {
			try {
				
				out = new FileOutputStream(file_to_save);
				
			} catch(java.io.FileNotFoundException exf) {
				
				WsUtils.showMessageDialog(getMessagesStrs("cantOpenFileForExportMessage"));
				
				return;
			}
			
			XSSFWorkbook wb = new XSSFWorkbook();
			
			XSSFCreationHelper creationHelper = wb.getCreationHelper();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
			XSSFCellStyle st01 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
		    
		    int row_index = createExcelHeader(wb, sheet, vec_all_parts, creationHelper);
		    
		    ++row_index;
		
	    	for (WsSkladMoveDataRow dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            Vector<WsSkladMoveDataColumn> vec_columns = dt.row_vec;
		            
		            int cell_index = 0;
		            
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
		            cell01.setCellStyle(st01);
		        	
	                WsUtils.get();
					
	                
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellStyle(st01);
	                
	                String nameRow = null;
	                
	                String inDate = null;
	               
		   		    switch(dt.indexData) {
					 
						 case 0:{ 
							 
							 nameRow = getGuiStrs("zalishokNaReportName") + " " +  WsUtils.dateToString(dt.date_end, "dd.MM.yy" );
							 
							 inDate = WsUtils.dateToString(dt.date_end, "dd.MM.yy" );
							 
							 break;
						 }
						 case 1:{
							 
							 nameRow = getGuiStrs("zvPribReportBookName")+ " " +  WsUtils.dateToString(dt.date_start, "dd.MM.yy" ) + " " + getGuiStrs("poNameReport") + " "
									 + WsUtils.dateToString(dt.date_end, "dd.MM.yy" );
							 
							 inDate = WsUtils.dateToString(dt.date_start, "dd.MM.yy" );
							 
							 break;
						 }
						 case 2 : {
							 
							 nameRow = getGuiStrs("zvVitratBookReportName") + " " +  WsUtils.dateToString(dt.date_start, "dd.MM.yy" ) + " " + getGuiStrs("poNameReport") + " "
									 + WsUtils.dateToString(dt.date_end, "dd.MM.yy" );
							 
							 inDate = WsUtils.dateToString(dt.date_end, "dd.MM.yy" );
							 
							 break;
						 }
				  };
				  
				cell01.setCellValue(inDate);
				
			    cell01.setCellStyle(st01);
		 
		 		cell02.setCellValue(nameRow);
		 		
		 	    cell02.setCellStyle(st01);

	            for (int j = 0; j < vec_columns.size(); j++) {
	            	
	            	WsSkladMoveDataColumn col_data =  vec_columns.elementAt(j);
	            	
	                XSSFCell cell0 = row.createCell(cell_index++);
	                
	                cell0.setCellStyle(st01);
	                
	                if(col_data.in_quantity < 0.00001) {
	                	
	                	 cell0.setCellValue("");
	                }
	                else {
	                	
	                	 cell0.setCellValue(col_data.in_quantity);
	                }
	                
	                XSSFCell cell1 = row.createCell(cell_index++);
	                
	                cell1.setCellStyle(st01);
	                
	                if(col_data.out_quantity < 0.00001) {
	                	
	                	 cell1.setCellValue("");
	                }
	                else {
	                	
	                	 cell1.setCellValue(col_data.out_quantity);
	                }
	

	                XSSFCell cell2 = row.createCell(cell_index++);
	                
	                cell2.setCellStyle(st01);
	                
	                if(col_data.rest < 0.00001) {
	                	
	                	 cell2.setCellValue("");
	                }
	                else {
	                	
	                	 cell2.setCellValue(col_data.rest);
	                }
 
	            }
	                
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
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportSuccessMessage"));
    
		} catch (IOException  e) {

			e.printStackTrace();
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportFailedMessage"));
		}

	}
	
	
	private int createExcelHeader(  XSSFWorkbook wb, XSSFSheet sheet, Vector<WsSkladMoveDataRow> vec_all_parts, XSSFCreationHelper creationHelper) {
		
			XSSFCellStyle st0_right = getExcelCellStyle(wb, 0, 0, 0, 0, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
			
			//XSSFCellStyle st0 = getExcelCellStyle(wb, 0, 0, 0, 0, 
			//		   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st01 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
			int rows_count = 0;
			
			XSSFRow rowHeader = sheet.createRow( rows_count);
			
			if(m_p_panel.getApprovePerson() != null) {
				
				XSSFCell cl1 = createCell(rowHeader, 0, getGuiStrs("apprLabelRep"), creationHelper); 
				   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,6));
				
				cl1.setCellStyle(st0_right);
			   
			    rowHeader = sheet.createRow(++rows_count);
			    
			    cl1 = createCell(rowHeader, 0, m_p_panel.getApprovePerson().position, creationHelper); 
				   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,6));
				
				cl1.setCellStyle(st0_right);
				
				rowHeader = sheet.createRow(++rows_count);
				    
				cl1 = createCell(rowHeader, 0, m_p_panel.getApprovePerson().rank + "____________" +
						m_p_panel.getApprovePerson().name, creationHelper); 
					   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,6));
					
				cl1.setCellStyle(st0_right);
				
				rowHeader = sheet.createRow(++rows_count);
			    
				cl1 = createCell(rowHeader, 0," '____' ________ _______ ", creationHelper); 
					   
				sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,6));
					
				cl1.setCellStyle(st0_right);
				
				rowHeader = sheet.createRow(++rows_count);
		    
			}
			
		   XSSFRow rowHeader0 = sheet.createRow(++rows_count);
		   
		   XSSFCell cl1 = createCell(rowHeader0, 0, getReportCaption(), creationHelper);
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count ,rows_count, 
				   0, 11));
		
		   rowHeader0 = sheet.createRow(++rows_count);
		   
		   rowHeader0 = sheet.createRow(++rows_count);
		   
		   int row0_index = rows_count;
		   
		   cl1 = createCell(rowHeader0, 0, "", creationHelper); 
		   
		   cl1.setCellStyle(st01);
	
		   cl1 = createCell(rowHeader0, 1, getGuiStrs("reportBookNameGoodColumn") + ":", creationHelper);
		   
		   cl1.setCellStyle(st01);
		   
		   XSSFRow rowHeader1 = sheet.createRow(++rows_count);
		   
		   int row1_index = rows_count;
		   
		   cl1 = createCell(rowHeader1, 0, "", creationHelper); 
		   
		   cl1.setCellStyle(st01);
	
		   cl1 = createCell(rowHeader1, 1, getGuiStrs("colNameKod") + ":", creationHelper);
		   
		   cl1.setCellStyle(st01);
		   
		   XSSFRow rowHeader2 = sheet.createRow(++rows_count);
		   
		   cl1 = createCell(rowHeader2, 0, getGuiStrs("reportBookInDateNameColumn"), creationHelper); 
		   
		   cl1.setCellStyle(st01);
		   
		   cl1 = createCell(rowHeader2, 1, getGuiStrs("docNameReportName"), creationHelper);
		   
		   cl1.setCellStyle(st01);
		   
		   int cell_index = 2;
		   
		   WsSkladMoveDataRow data = vec_all_parts.elementAt(0);
		   
		   Vector<WsSkladMoveDataColumn> vec_columns = data.row_vec;
		   
		   for(int i = 0; i < vec_columns.size(); ++i) {
			   
			   WsSkladMoveDataColumn col = vec_columns.elementAt(i);
			   
			   int cell_index_merge = cell_index;
			   
			   cl1 = createCell(rowHeader0, cell_index, col.name, creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 = createCell(rowHeader1, cell_index, String.valueOf(col.kod), creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 = createCell(rowHeader2, cell_index++, getGuiStrs("pribuloReportName"), creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 = createCell(rowHeader0, cell_index, "", creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 = createCell(rowHeader1, cell_index, "", creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 = createCell(rowHeader2, cell_index++, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 =createCell(rowHeader0, cell_index, "", creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 =createCell(rowHeader1, cell_index, "", creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   cl1 = createCell(rowHeader2, cell_index++, getGuiStrs("zalishokColumnReportName"), creationHelper);
			   
			   cl1.setCellStyle(st01);
			   
			   sheet.addMergedRegion(new CellRangeAddress(row0_index, row0_index,cell_index_merge,cell_index_merge + 2));
			   
			   sheet.addMergedRegion(new CellRangeAddress(row1_index , row1_index, 
					   cell_index_merge,cell_index_merge + 2));
			   
		   }
		   
		   return rows_count;
	}	
	
	
	private String getReportCaption() {
		
		Vector<WsInfoData>  v_info = WsUtilSqlStatements.getInfoDataList();
		
		String firm_name = "-----";
		
		if(v_info.size() != 0) {
		
			firm_name = v_info.elementAt(0).name;
		}
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder hS_b = new StringBuilder();
		
		hS_b.append(getGuiStrs("bookSkladMovementReportName") );
		
		hS_b.append(" " );
		
		hS_b.append(date_s );
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append(" ");
		
		hS_b.append( date_e ); 
		
		hS_b.append("   " );
		
		hS_b.append(firm_name );
		
		hS_b.append("   " );
		
		hS_b.append(getGuiStrs("prodSkladNameReport") );
		
		return hS_b.toString();
		
	}
}
