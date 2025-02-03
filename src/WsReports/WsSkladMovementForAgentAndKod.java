
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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsAgentComboBox;
import WsControls.WsPartTypesComboBox;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WSAgentSqlStatements;
import WsDatabase.WsReportsSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsSkladMovementForAgentAndKod  extends WSReportViewer {


	Vector<WsRashodPartData>  m_vec_all = null;
	
	JCheckBox m_checkAllKodes = null;
	
	WsAgentComboBox m_combo_agent = null;
	
	WsPartTypesComboBox m_part_type_combo = null;	 
	
	String m_agentName = "";

	public  WsSkladMovementForAgentAndKod(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	 m_html_pages = generateReport();
            	 
            	 if(m_html_pages == null) {return;}
            	 
            	if(!m_html_pages.isEmpty()) {
            	
	            	setText(m_html_pages.elementAt(0));
	            	
	            	pagesNum = m_html_pages.size();
	            	
	            	currentPage = 1;
	            	
	            	setDialogCaption();
	            	
	            	m_viewer.setSelectionStart(0);
	            	
	            	m_viewer.setSelectionEnd(0);
            	
            	}
             
             
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

	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		JLabel name_label = new JLabel(getGuiStrs("agentComboNewDialogLabel"));
		
		JLabel kod_label = new JLabel(getGuiStrs("nameColumnReportGoodName") + " : ");
		
		m_combo_agent = new WsAgentComboBox();
		
		m_part_type_combo = new WsPartTypesComboBox();
		
		m_control_panel2.add(kod_label);
		
		m_control_panel2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		m_control_panel2.add(m_part_type_combo);
		
		m_control_panel2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		m_control_panel2.add(name_label);
		
		m_control_panel2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		m_control_panel2.add(m_combo_agent);
		
		m_control_panel2.add(Box.createHorizontalGlue());
		
		WsUtils.get().setFixedSizeBehavior(m_combo_agent);
		
		WsGuiTools.setComponentFixedWidth(m_combo_agent, 300);
		
		WsUtils.get().setFixedSizeBehavior(m_part_type_combo);
		
		WsGuiTools.setComponentFixedWidth(m_part_type_combo, 300);
		
		WsGuiTools.fixComponentHeightToMin(m_control_panel2);
			
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int agentId =  m_combo_agent.getCurrentSQLId();

		Vector<WsRashodPartData> vec_all = WsReportsSqlStatements.getRashodPartsListForDateAndAgentKod(m_date.getSqlStartDate(), 
				m_date.getSqlEndDate(), agentId , m_part_type_combo.getCurrentSQLId());
		
		m_agentName  = WSAgentSqlStatements.getAgentNameForId(agentId);

		int rows_number = vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		Vector<String> vec_pages = new Vector<String>();
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
		if(pages_number == 1 && end_row >= vec_all.size()) {
			
			end_row = vec_all.size()  - 1;
		}
		
		if(pages_number == 0) { pages_number  = 1;}
		
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
	
	public String getPrintHtml(Vector<WsRashodPartData> vec_all, int start, int end, int page_number) {
		
	
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader_b = new StringBuilder();
		
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>" );
		
		sHeader_b.append(getGuiStrs("reportBookKodGoodColumn") );
		
		sHeader_b.append("</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("nameNameInReport") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;");
		
		sHeader_b.append( getGuiStrs("dataReportName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("periodReportName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("vibuloReportName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;  text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("unitsNameInReport") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;  text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("sumWithoutNdsLabel") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;  text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("sumNdsLabel") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid; text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("sumWithNdsLabel") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "</tr>");

		StringBuilder s_b = new StringBuilder(); 
		
		s_b.append("");
		
		if(vec_all.isEmpty()) { start = 0; end = -1;}
		
		for(int i = start; i <= end; ++i) {
			
			 WsRashodPartData d = vec_all.elementAt(i);
			
			 String bottomBorder = "";
			 
			 if(i == end) {
				 
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }

			 java.sql.Date fD = WsUtils.getFirstDayOfTheWeekSqlDate(d.date);
			 
			 java.sql.Date lD = WsUtils.sqlDatePlusDays(fD, 6);
			 
			 String period = WsUtils.dateToString(fD, "dd.MM.yyyy" ) + " - "  + WsUtils.dateToString(lD, "dd.MM.yyyy" );
			 		
			s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			
			s_b.append(bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;"); 
			
			s_b.append( String.valueOf(i + 1) );
			
			s_b.append(  "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4> &nbsp;" );
			
			s_b.append( String.valueOf(d.kod) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( d.name );
			
			s_b.append( "&nbsp;</font></td>");
					
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.dateToString(d.date, "dd.MM.yyyy" ) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( period );
			
			s_b.append("&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF(d.quantity) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			s_b.append( bottomBorder);
			
			s_b.append("'><font size =4>&nbsp;" );
			
			s_b.append( d.units_name );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			s_b.append( bottomBorder);
			
			s_b.append("'><font size =4>&nbsp;" );
			
			s_b.append(  WsUtils.getDF(d.quantity*d.cost) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;"); 
			
			s_b.append( bottomBorder);
			
			s_b.append("'><font size =4>&nbsp;" );
			
			s_b.append(  WsUtils.getDF(d.quantity*d.nds) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; "); 
			
			s_b.append( bottomBorder);
			
			s_b.append("'><font size =4>&nbsp;" );
			
			if(d.costwithnds > 0.0) {
			
				s_b.append(  WsUtils.getDF(d.quantity*d.costwithnds) );
			
			}
			else {
				
				s_b.append(  WsUtils.getDF(d.quantity*(d.cost + d.nds)) );
			}
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "</tr>");
		
		}
		
		double sum_quantity = 0.0;
		
		double sum_nds = 0.0;
		
		double sum_cost = 0.0;
		
		double sum_costnds = 0.0;
		
		for(WsRashodPartData f: vec_all) {
			
			sum_quantity += f.quantity;
			
			sum_nds += f.quantity*f.nds;
			
			sum_cost += f.quantity*f.cost;
			
			if(f.costwithnds > 0.0) {
			
				sum_costnds += f.quantity*f.costwithnds;
			
			}
			else {
				
				sum_costnds += f.quantity*(f.cost + f.nds);
			}
		}
		
		s_b.append("<tr><td colspan='5' style='border-left: 1px solid; border-bottom: 1px solid ;'>");
		
		s_b.append("<font size =4>" );
		
		s_b.append( getGuiStrs("zagalomReportName") );
		
		s_b.append( "</font></td>");
		
		s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid;  border-bottom: 1px solid ;" );
		
		s_b.append("'><font size =4>&nbsp;" );
		
		s_b.append( WsUtils.getDF(sum_quantity) );
		
		s_b.append( "&nbsp;</font></td>");
		
		s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid;  border-bottom: 1px solid ;" );
		
		s_b.append("'><font size =4>&nbsp;" );
		
		s_b.append( "&nbsp;</font></td><");
		
		s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid;  border-bottom: 1px solid ;" );
		
		s_b.append("'><font size =4>&nbsp;" );
		
		s_b.append( WsUtils.getDF(sum_cost) );
		
		s_b.append( "&nbsp;</font></td>");
		
		s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-bottom: 1px solid ;" );
		
		s_b.append("'><font size =4>&nbsp;" );
		
		s_b.append( WsUtils.getDF(sum_nds) );
		
		s_b.append( "&nbsp;</font></td>");
		
		s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-right: 1px solid ; border-bottom: 1px solid ;" );
		
		s_b.append("'><font size =4>&nbsp;" );
		
		s_b.append( WsUtils.getDF(sum_costnds) );
		
		s_b.append( "&nbsp;</font></td>");
		
		s_b.append( "</tr>");
		
		StringBuilder hS_b = new StringBuilder(); 
				
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append( "        height: 297mm;\r\n");
		
		hS_b.append( "        width: 210mm;\r\n");
		
		hS_b.append( "        /* to centre page on screen*/\r\n");
		
		hS_b.append("        margin-left: auto;\r\n");
		
		hS_b.append( "        margin-right: auto;\r\n");
		
		hS_b.append("    }");
		
		hS_b.append("</style><body>");
		
		hS_b.append( "<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("movementAgentReportName") );
		
		hS_b.append( " " );
		
		hS_b.append( date_s );
		
		hS_b.append( " ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append( " ");
		
		hS_b.append(date_e );
		
		hS_b.append("</font></h2>");
		
		hS_b.append("<h2 align='center' ><font size =5> ");
		
		hS_b.append(getGuiStrs("pidrozdilReportName")); 
		
		hS_b.append(m_agentName );
		
		hS_b.append("</font></h2>");
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader_b.toString() );
		
		hS_b.append(s_b.toString()  );
		
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
	
	
	
	public void exportToExcelFile(Vector<WsRashodPartData>  vec_all_parts) {
		
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
		    
		    createExcelHeader(sheet, creationHelper);
		    
		    int row_index = 1;
		
	    	for (WsRashodPartData dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
		            cell01.setCellValue(dt.kod);

	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellValue(dt.name);

	                XSSFCell cell021 = row.createCell(cell_index++);
	                
	                cell021.setCellValue(WsUtils.dateToString(dt.date, "dd.MM.yyyy" ));
	                
		   			java.sql.Date fD = WsUtils.getFirstDayOfTheWeekSqlDate(dt.date);
		   			
					java.sql.Date lD = WsUtils.sqlDatePlusDays(fD, 6);
					 
					String period = WsUtils.dateToString(fD, "dd.MM.yyyy" ) + " - " 
					 + WsUtils.dateToString(lD, "dd.MM.yyyy" );
	                
	                XSSFCell cell022 = row.createCell(cell_index++);
	                
	                cell022.setCellValue(period);
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(dt.quantity);
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                cell06.setCellValue(dt.units_name);
	                
	                XSSFCell cell07 = row.createCell(cell_index++);
	                
	                cell07.setCellValue(dt.cost*dt.quantity);
	                
	                XSSFCell cell08 = row.createCell(cell_index++);
	                
	                cell08.setCellValue(dt.nds*dt.quantity);
	                
	                XSSFCell cell09 = row.createCell(cell_index++);
	                
	                cell09.setCellValue(dt.costwithnds*dt.quantity);
	                
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
	
	
	private void createExcelHeader( XSSFSheet sheet, XSSFCreationHelper creationHelper) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);

		   createCell(rowHeader0, 0, getGuiStrs("kodNameInReport"), creationHelper);
	
		   createCell(rowHeader0, 1, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 2, getGuiStrs("dataReportName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("periodReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("vibuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("unitsNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("sumWithoutNdsLabel"), creationHelper);
		   
		   createCell(rowHeader0, 7, getGuiStrs("sumNdsLabel"), creationHelper);
		   
		   createCell(rowHeader0, 8, getGuiStrs("sumWithNdsLabel"), creationHelper);
		  	
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
		
	}
}
