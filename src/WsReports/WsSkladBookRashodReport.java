
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDataStruct.WsSkladMoveDataRow;
import WsDatabase.WsReportsSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladBookRashodReport extends WSReportViewer {

	Vector<WsSkladMoveDataRow> m_vec_all_parts = null;

	public WsSkladBookRashodReport(JFrame f, String nameFrame) {
		super(f, nameFrame);

		createGui();
		
		 m_saveExcelButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	if( !isReportEmpty() ) {
	            	
	            		exportToExcelFile(m_vec_all_parts);
	            	
	            	}
	             
	            }
		 });
		
		
		 m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	 m_html_pages = generateReport();
            	 
            	 if(!m_html_pages.isEmpty()) {
            	
		            	setText(m_html_pages.elementAt(0));
		            	
		            	pagesNum = m_html_pages.size();
		            	
		            	currentPage = 1;
		            	
		            	setDialogCaption();
		            	
		            	m_viewer.setSelectionStart(0);
		            	
		            	m_viewer.setSelectionEnd(0);
            	 }
            	 else {
            		 
            		 	pagesNum = 0;
		            	
		            	currentPage = 0;
		            	
		            	setDialogCaption();
            		 
            	 }
            } 
		});
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
	}
	
	private Vector< Vector<Double> > getItogo(Vector<WsSkladMoveDataRow> vec_parts, int pages_number, int columns_page) {
		
		
		Vector< Vector<Double> > itogo_vec = new Vector< Vector<Double> >();
		
		for(int k = 0; k < pages_number; ++k) {
		
			Vector<Double> vec_itogo = new Vector<Double>();

			for(int j = 0; j < columns_page; ++j) {
				
				vec_itogo.add(0.0);
			}
			
			itogo_vec.add(vec_itogo);
		
		}
	
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_parts.elementAt(i);
			
		
			for(int k = 0; k < d_row.pages_number; ++k) {
				
				Vector<WsSkladMoveDataColumn> row_vec = d_row.pages_row_vec.elementAt(k);
				
			
				Vector<Double> vec_itogo = itogo_vec.elementAt(k);
			
				for(int j = 0; j < row_vec.size(); ++j) {
					
					 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
					 
					 vec_itogo.set(j, vec_itogo.elementAt(j) + dc.out_quantity);
				
				}
			}
	
		}
		
		return  itogo_vec;

	}
	
	//in the data the number of columns for goods can be different; this function makes empty columns for absent ones
	//returns columns number
	private int syncColumns(Vector<WsSkladMoveDataRow> vec_parts) {
		
		HashSet<Integer> kods_list = new HashSet<Integer>();
		
		HashMap<Integer, String> names_map = new HashMap<Integer, String>();
		
		//get list of all codes
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
			
			for(int j = 0; j < row_vec.size(); ++j) {
				
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 kods_list.add(dc.kod);
				 
				 names_map.put(dc.kod, dc.name);
			
			}
	
		}
		
		Vector<Integer> vec_kods = new Vector<Integer>();
		
		for(Integer i: kods_list){
			
			vec_kods.add(i);
	    }
		//Comparator
		Collections.sort(vec_kods);  
		
		int column_number = vec_kods.size();
		
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
			
			Vector<WsSkladMoveDataColumn>  new_vec = new Vector<WsSkladMoveDataColumn>();
			
			for(int j = 0; j < vec_kods.size(); ++j) {
				
				Integer kod = vec_kods.elementAt(j);
				
				boolean notfound = true;
				
				for(int k = 0; k < row_vec.size(); ++k) {
					
					WsSkladMoveDataColumn dc = row_vec.elementAt(k);
					
					if( WsUtils.isKodEqual(dc.kod, (int)kod)) {
						
						notfound = false;
						
						new_vec.add(dc);
						
						break;
					}
				
				}
				
				if(notfound) {
					
					WsSkladMoveDataColumn dc = new WsSkladMoveDataColumn();
					
					dc.kod = kod;
					
					dc.name = names_map.get(kod);
					
					dc.units = "";
					
					dc.in_quantity = 0.0;
					
					new_vec.add(dc);
					
				}
			}
			
			d_row.row_vec =  new_vec ;
		}
		
		return column_number;
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
	
		Vector<WsSkladMoveDataRow> vec_all_parts = WsReportsSqlStatements.getRashodMovement(m_date.getSqlStartDate(), m_date.getSqlEndDate());

		int columns_number = syncColumns(vec_all_parts);
		
		//divide into pages
		int columns_per_page = 12;
		
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
		
		//calculates itogo values
		Vector< Vector<Double> > itogo_vec = getItogo( vec_all_parts, pages_number, columns_per_page);
		
		Vector<String> vec_pages = new Vector<String>();
		
		for(int k = 0; k < pages_number; ++k) { 
			
			String page = getPrintHtml(vec_all_parts, itogo_vec.elementAt(k), k);
			
			vec_pages.add(page);
		}
		
		current_font_size = 4;
		
		m_vec_all_parts = vec_all_parts;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}
	
	
	public String getPrintHtml(Vector<WsSkladMoveDataRow> vec_all_parts, Vector<Double> vec_itogo, int page_number) {
		

		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader_b = new StringBuilder();
				
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( getGuiStrs("reportBookKodGoodColumn") );
		 
		sHeader_b.append("</font></td>");

		StringBuilder sHeader2_b = new StringBuilder();
				
		sHeader2_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>");
		
		sHeader2_b.append( getGuiStrs("reportBookNameGoodColumn"));
		
		sHeader2_b.append("</font></td>");

		StringBuilder sHeader3_b =  new StringBuilder();
				
		sHeader3_b.append("<tr><td colspan='2' style='border-left: 1px solid;border-top: 1px solid ; ext-align: center;'><font size =4>");
		
		sHeader3_b.append( getGuiStrs("reportBookNameColumn") );
		
		sHeader3_b.append("</font></td>");
		
		StringBuilder sFooter_b =  new StringBuilder();
				
		sFooter_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;'><font size =4></font></td>");
		
		sFooter_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;border-bottom: 1px solid ;'> &nbsp;");
		
		sFooter_b.append(getGuiStrs("reportBookNameItogoColumn"));
		
		sFooter_b.append( "</td>");
		
		StringBuilder row_s_b = new StringBuilder();
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.pages_row_vec.elementAt( page_number);
			
			row_s_b.append( "<tr><td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>&nbsp;");
			
			row_s_b.append( String.valueOf(i + 1) );
			
			row_s_b.append(  "&nbsp;</font></td>");
			
			row_s_b.append( "<td nowrap  style='border-left: 1px solid;border-top: 1px solid ;'>");
			
			row_s_b.append( "<font size =4>&nbsp;" );
			
			row_s_b.append( getGuiStrs("naklReportName2") );
			
			row_s_b.append( " " );
			
			row_s_b.append( d_row.nakl_number );
			
			row_s_b.append( " " );
			
			row_s_b.append( getGuiStrs("vidNaklName2") );
			
			row_s_b.append(" " );
			
			row_s_b.append(WsUtils.dateToString(d_row.date, "dd.MM.yy" ) );
			
			row_s_b.append( "</font></td>");
			
			for(int j = 0; j < row_vec.size(); ++j) {
			
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 String rightBorder = "";
				 
				 if(j == (row_vec.size() -1)) {
					 
					 rightBorder = "border-right: 1px solid ;";
				 }
				  
				 row_s_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center; '><font size =4>" );
				 
				 row_s_b.append( WsUtils.getDF(dc.out_quantity) );
				 
				 row_s_b.append( "</font></td>");
				 
				 row_s_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;" );
				 
				 row_s_b.append( rightBorder );
				 
				 row_s_b.append(" text-align: center; '><font size =4>" );
				 
				 row_s_b.append( dc.units );
				 
				 row_s_b.append( "</font></td>");
			
				 if( i == 0) {
					 
					 sHeader_b.append( "<td colspan='2' style='border-left: 1px solid;border-top: 1px solid ;"); 
					 
				     sHeader_b.append( rightBorder );
				     
					 sHeader_b.append( " text-align: center;'><font size =4>" );
					 
					 sHeader_b.append( String.valueOf(dc.kod) );
					 
					 sHeader_b.append( "</font></td>");
			
					 sHeader2_b.append("<td colspan='2' style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 sHeader2_b.append( rightBorder );
					 
					 sHeader2_b.append( " text-align: center;'><font size =4>" );
					 
					 sHeader2_b.append( String.valueOf(dc.name));
					 
					 sHeader2_b.append(  "</font></td>");
				
					 sHeader3_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" );
					 
					 sHeader3_b.append(getGuiStrs("quantityNameVibuloReportColumn") );
					 
					 sHeader3_b.append("</font></td>");
					 
					 sHeader3_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; " );
					 
					 sHeader3_b.append( rightBorder );
					 
					 sHeader3_b.append(" text-align: center;'><font size =4>");
					 
					 sHeader3_b.append(getGuiStrs("unitsNameReportColumn") );
					 
					 sHeader3_b.append( "</font></td>");
					 
					 double sum_itog = vec_itogo.elementAt(j);
					 
					 sFooter_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;border-bottom: 1px solid ;'><font size =4>"); 
					 
					 sFooter_b.append(WsUtils.getDF(sum_itog) );
					 
					 sFooter_b.append( "</font></td>");
					
					 sFooter_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;border-bottom: 1px solid ;" );
					
					 sFooter_b.append( rightBorder );
					 
					 sFooter_b.append("'><font size =4></font></td>");
				 }
			}
			
			row_s_b.append("</tr>");

		}
		
		sHeader_b.append( "</tr>");
		
		sHeader2_b.append("</tr>");
		
		sHeader3_b.append("</tr>");
	
		StringBuilder hS_b =  new StringBuilder();
		
		hS_b.append("<!DOCTYPE html><html> ");
				
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append("        height: 210mm;\r\n");
		
		hS_b.append("        width: 297mm;\r\n");
		
		hS_b.append("        /* to centre page on screen*/\r\n");
		
		hS_b.append("        margin-left: auto;\r\n");
		
		hS_b.append( "        margin-right: auto;\r\n");
		
		hS_b.append( "    }");
		
		hS_b.append( "</style><body>");
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("bookSkladRashodReportName") );
		
		hS_b.append(" " );
		
		hS_b.append( date_s );
		
		hS_b.append( " ");
		
		hS_b.append( getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append( " ");
		
		hS_b.append( date_e  );
		
		hS_b.append("</font></h2>");
		
		hS_b.append( "<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append( sHeader2_b.toString() );
		
		hS_b.append( sHeader_b.toString() );
		
		hS_b.append(  sHeader3_b.toString() );
		
		hS_b.append(row_s_b.toString() );
		
		hS_b.append( sFooter_b.toString());
		
		hS_b.append("</table><br><br><table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>"
				+ "<tr><td></td><td></td></tr><tr><td></td><td></td></tr>"
				+ "</table></body></html>");
	  
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
		    
		    createExcelHeader(sheet, vec_all_parts, creationHelper);
		    
		    int row_index = 3;
		
	
	    	for (WsSkladMoveDataRow dt: vec_all_parts) {
	
	            XSSFRow row = sheet.createRow(row_index++);
	            
	            Vector<WsSkladMoveDataColumn> vec_columns = dt.row_vec;
	            
	            int cell_index = 0;
	            
	            XSSFCell cell01 = row.createCell(cell_index++);
	        	
                WsUtils.get();
                
				cell01.setCellValue(WsUtils.dateSqlToString(dt.date, "dd.MM.yy"));
                
                XSSFCell cell02 = row.createCell(cell_index++);
                
                if(dt.indexData == 0) {
                	
                	cell02.setCellValue(getGuiStrs("naklReportName2") + " "+ dt.nakl_number + " " + getGuiStrs("vidNaklName2") +" " + WsUtils.dateSqlToString(dt.date, "dd.MM.yy"));
                }
                
                XSSFCell cell03 = row.createCell(cell_index++);
	        	
                cell03.setCellValue(dt.agent_name);
	            
	            for (int j = 0; j < vec_columns.size(); j++) {
	            	
	            	WsSkladMoveDataColumn col_data =  vec_columns.elementAt(j);
	            	
	                XSSFCell cell1 = row.createCell(cell_index++);
	                
	                if(col_data.out_quantity < 0.00001) {
	                	
	                	 cell1.setCellValue("");
	                }
	                else {
	                	 cell1.setCellValue(col_data.out_quantity);
	                }
	                
	                XSSFCell cell11 = row.createCell(cell_index++);
	                
	                cell11.setCellValue(col_data.units);
	                
	            }

	        }
	
			wb.write(out);
	
			out.close();
	    
			wb.close(); 
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("saveExcelReportSuccessMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
    
		} catch (IOException  e) {
			
			e.printStackTrace();
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("saveExcelReportFailedMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		}

	}
	
	
	private void createExcelHeader( XSSFSheet sheet, Vector<WsSkladMoveDataRow> vec_all_parts, XSSFCreationHelper creationHelper) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);
		   
		   createCell(rowHeader0, 0, "", creationHelper); 
		   
		   createCell(rowHeader0, 1, "", creationHelper); 
		   
		   createCell(rowHeader0, 2, getGuiStrs("reportBookNameGoodColumn") + ":", creationHelper);
		   
		   XSSFRow rowHeader1 = sheet.createRow(1);
		   
		   createCell(rowHeader1, 0, "", creationHelper); 
		   
		   createCell(rowHeader1, 1, "", creationHelper); 
		   
		   createCell(rowHeader1, 2, getGuiStrs("ptypesKodColumName") + ":", creationHelper);
		   
		   XSSFRow rowHeader2 = sheet.createRow(2);
		   
		   createCell(rowHeader2, 0, getGuiStrs("reportBookInDateNameColumn"), creationHelper); 
		   
		   createCell(rowHeader2, 1, getGuiStrs("docNameReportName"), creationHelper);
		   
		   createCell(rowHeader2, 2 , getGuiStrs("postOdergReportName"), creationHelper);
		   
		   int cell_index = 3;
		   
		   WsSkladMoveDataRow data = vec_all_parts.elementAt(0);
		   
		   Vector<WsSkladMoveDataColumn> vec_columns = data.row_vec;
		   
		   for(int i = 0; i < vec_columns.size(); ++i) {
			   
			   WsSkladMoveDataColumn col = vec_columns.elementAt(i);
			   
			   int cell_index_merge = cell_index;
			   
			   createCell(rowHeader0, cell_index, col.name, creationHelper);
			   
			   createCell(rowHeader1, cell_index, String.valueOf(col.kod), creationHelper);

			   createCell(rowHeader2, cell_index++, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);

			   createCell(rowHeader2, cell_index++, getGuiStrs("unitsNameInReport"), creationHelper);
			   
			   sheet.addMergedRegion(new CellRangeAddress(1,1,cell_index_merge,cell_index_merge + 1));
  
		   }
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
         cell3.setCellValue(richString3);
		
	}
}
