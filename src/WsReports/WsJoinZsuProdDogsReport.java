/**
 * 
 */
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsFileTableControl;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsSkladMoveDataColumn;
import WsImport.WsImportExcelUtil;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsJoinZsuProdDogsReport  extends WSReportViewer {

	private static final long serialVersionUID = 1L;
	
	Vector<WsSkladMoveDataColumn> m_vec_all = null;
	
	WsFileTableControl m_table_control = null; 
	
	public WsJoinZsuProdDogsReport(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		String[] columnNames = { getGuiStrs("excelImportSourceFileName") };
		
		m_table_control = new WsFileTableControl(columnNames, getGuiStrs("chooserExcelFileLabelName"), true);
		
		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	m_html_pages = generateReport();
            	
            	if(m_html_pages.isEmpty()) {
            		
            		JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("noDataForReportMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);

            		return;
            	}
            	
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
            	
            		exportToExcelFile(m_vec_all);
            	
            	}
            }
		});	
	}

	private void createGui() {
		
		WsUtils.get().setFixedSizeBehavior(m_table_control);
		
		WsGuiTools.setComponentFixedHeight(m_table_control, 120);
		
		m_control_panel2.add(m_table_control);
		 
		m_control_panel2.add(Box.createHorizontalGlue());
			
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		m_table_control.setTableToolTips(getGuiStrs("porivzVitZsuProdTableTooltip") );
		
		m_date.setVisible(false);
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<String> vec_pages = new Vector<String>();
		
		HashMap<Integer, WsSkladMoveDataColumn> foreign_data = importExcel();
		
		if(foreign_data.isEmpty()) { 
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			return vec_pages; 
			
		}

		ArrayList<Integer> list = new ArrayList<Integer>(foreign_data.keySet()); 
		
		Collections.sort(list);
		
		Vector<WsSkladMoveDataColumn> vec_all = new Vector<WsSkladMoveDataColumn> ();
		
		for(Integer kod: list) {
			
			 WsSkladMoveDataColumn d = foreign_data.get(kod);
			 
			 vec_all.add(d);
			
		}
		
		vec_all = removeZerosRows(vec_all);
		
		m_vec_all =  vec_all;
		
		int rows_number = vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
		for(int k = 0; k < pages_number; ++k) { 
			
			String page = getPrintHtml(vec_all, start_row, end_row, k);
			
			vec_pages.add(page);
			
			start_row = end_row + 1;
			
			end_row = start_row + rows_per_page - 1;
			
			if(end_row > (vec_all.size() -1)) {
				
				end_row = vec_all.size() -1;
			}
			
		}
		
		current_font_size = 4;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}
	
	
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
		StringBuilder sHeader_b = new StringBuilder();
		
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>" + getGuiStrs("reportBookKodGoodColumn") + "</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("nameNameInReport") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("naPochatProdReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("pribuloProdReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("vibuloReportName") + " Prod" + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restNameInReport") + " Prod" + "&nbsp;</font></td></tr>");

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
			
			row_s_b.append("'><font size =4>&nbsp;"); 
			
			row_s_b.append(WsUtils.getDF(d.initial_rest_1)); 
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.in_quantity_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.out_quantity_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest_1) );
			
			row_s_b.append("&nbsp;</font></td></tr>");;
		
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
		
		hS_b.append( getGuiStrs("joinZsuProdReportName") );
		
		hS_b.append(" " );
		
		hS_b.append(" ");
		
		hS_b.append(" ");
		
		hS_b.append("</font></h2>");
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader_b.toString() );
		
		hS_b.append(row_s_b.toString() ); 
		
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
					
					wb.close();
					
					return map;
				}
				
				fStream.close();
					    
			    XSSFSheet sheet = null;
			    
				try {
				    
					sheet = wb.getSheetAt(sheet_index);
			    
				}  catch(java.lang.IllegalArgumentException ex) { 
					
					wb.close();
					
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
		        	
		        	d.kod = WsImportExcelUtil.getKodCell(row, kod_column);
	        		
	        		if(d.kod == -1) { continue;}
	        		
	        		
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
	            
	            XSSFCell cell01 = row.createCell(cell_index++);
	            
	            cell01.setCellValue(dt.kod);
	        	
                XSSFCell cell02 = row.createCell(cell_index++);
                
                cell02.setCellValue(dt.name);
                
                XSSFCell cell03_1 = row.createCell(cell_index++);
                
                cell03_1.setCellValue(dt.initial_rest_1);
                
                XSSFCell cell04_1 = row.createCell(cell_index++);
                
                cell04_1.setCellValue(dt.in_quantity_1);
                
                XSSFCell cell05_1 = row.createCell(cell_index++);
                
                cell05_1.setCellValue(dt.out_quantity_1);
                
                XSSFCell cell06_1 = row.createCell(cell_index++);
                
                cell06_1.setCellValue(dt.rest_1);
	                
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
		   
		   createCell(rowHeader0, 0, getGuiStrs("kodNameInReport"), creationHelper);
	
		   createCell(rowHeader0, 1, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 2, getGuiStrs("naPochatReportName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("pribuloReportName"), creationHelper);

		   createCell(rowHeader0, 4, getGuiStrs("vibuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("restNameInReport"), creationHelper);
		   
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
		
	}
	
	
	public Vector<WsSkladMoveDataColumn> removeZerosRows(Vector<WsSkladMoveDataColumn>  vec) {
		
		Vector<WsSkladMoveDataColumn>  new_vec = new Vector<WsSkladMoveDataColumn>();
		
		double zL = WsUtils.getRZL();
		
		for(WsSkladMoveDataColumn d: vec) {
			
			boolean flag =  d.initial_rest_1 > zL || 
					d.in_quantity_1 > zL || 
					d.out_quantity_1 > zL || d.rest_1 > zL;
					
			if(flag) {
				
				new_vec.add(d);
			}
		
		}
		
		return new_vec;
	
	}
	
}
