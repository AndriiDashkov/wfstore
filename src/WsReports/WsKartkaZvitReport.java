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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsItogoData;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDataStruct.WsSkladMoveDataRow;
import WsDatabase.WsReportsSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsUtils;

//JSpinner
/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsKartkaZvitReport extends WSReportViewer {

	Vector<WsSkladMoveDataRow> m_vec_all_parts = null;
	
	int global_index = 0;
	
	public WsKartkaZvitReport(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
        		setCursor(new Cursor(Cursor.WAIT_CURSOR));
            	
            	 m_html_pages = generateReport();
            	 
            	 if(m_html_pages == null || m_html_pages.isEmpty()) {
            		 
            		 setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            		 
            		 return;
            	}
            	
            	setText(m_html_pages.elementAt(0));
            	
            	pagesNum = m_html_pages.size();
            	
            	currentPage = 1;
            	
            	setDialogCaption();
            	
            	m_viewer.setSelectionStart(0);
            	
            	m_viewer.setSelectionEnd(0);
            	
        		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
             
             
            }
            
		});
		
		
		 m_saveExcelButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	if( !isReportEmpty() ) {
	            	
	            		exportToExcelFile(m_vec_all_parts);
	            	
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
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		global_index = 0;
		
		Vector<WsSkladMoveDataRow> vec_all_parts = 
				WsReportsSqlStatements.getPrihodRashodBookNaklForDate(m_date.getSqlStartDate(), 
						m_date.getSqlEndDate(), false, -1);
		
		removeZeroColumns(vec_all_parts);
		
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

		Vector<String> vec_pages = new Vector<String>();
		
		for(int k = 0; k < pages_number; ++k) { 
			
			String page = getPrintHtml(vec_all_parts, k);
			
			vec_pages.add(page);
		}
		
		m_vec_all_parts = vec_all_parts;
		
		current_font_size = 4;
		
		return vec_pages;
		
		
	}
	
	
	public String getPrintHtml(Vector<WsSkladMoveDataRow> vec_all_parts, int page_number) {
		
		Vector<WsInfoData>  v_info = WsUtilSqlStatements.getInfoDataList();
		
		String firm_name = "-----";
		
		if(v_info.size() != 0) {
		
			firm_name = v_info.elementAt(0).name;
		}
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		
		StringBuilder h_top_1_1_b = new StringBuilder(500);
		
		h_top_1_1_b.append("<td rowspan='5' max-width: 50px; style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4> " + getGuiStrs("dataNadhodgReport") + "</font></td>"); 
		
		h_top_1_1_b.append("<td colspan='2' rowspan='5' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4> " + getGuiStrs("zvidNadihlpReportName") + "</font></td>"); 
		
		h_top_1_1_b.append("<td colspan='4' rowspan='5' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> <font size =4>" + getGuiStrs("docNameReportName") + "</font></td>");
		
		h_top_1_1_b.append("<td rowspan='5' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> <font size =4>" + getGuiStrs("numberDocReportName") + "</font></td>");
			
		StringBuilder h_top_1_2_b = new StringBuilder(500);
		
		StringBuilder h_top_1_3_b = new StringBuilder(500);
		
		StringBuilder h_top_1_4_b = new StringBuilder(500);
		
		StringBuilder h_top_1_5_b = new StringBuilder(500);
	
		StringBuilder h_top_2_b = new StringBuilder();
		
		h_top_2_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>1</font></td>"); 
		
		h_top_2_b.append("<td colspan='2' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>2</font></td>"); 
		
		h_top_2_b.append("<td colspan='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>3</font></td>");
		
		h_top_2_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>4</font></td>");
		
		StringBuilder h_top_3_b = new StringBuilder();
		
		h_top_3_b.append("<td colspan='8' style='border-left: 1px solid;border-top: 1px solid ; '>");
		
		h_top_3_b.append(getGuiStrs("vitrataReportName")); 
		
		h_top_3_b.append( " </td>");
		
		StringBuilder h_bot_1_1_b = new StringBuilder(500);
		
		h_bot_1_1_b.append("<td rowspan='4' style='  border-left: 1px solid;border-top: 1px solid ; text-align: center;'> <font size =4>" );
		
		h_bot_1_1_b.append(getGuiStrs("dateOfMonthReportName") );
		
		h_bot_1_1_b.append("</font></td>" );
		
		h_bot_1_1_b.append("<td  colspan='5' rowspan='2' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4> " );
		
		h_bot_1_1_b.append(getGuiStrs("normZabezpReportName") );
		
		h_bot_1_1_b.append("</font></td>" );
		
		h_bot_1_1_b.append("<td rowspan='3' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" );
		
		h_bot_1_1_b.append(getGuiStrs("docNameReportName2") );
		
		h_bot_1_1_b.append("</font></td>");
		
		h_bot_1_1_b.append("<td rowspan='3' style='border-left: 1px solid;  border-top: 1px solid ; text-align: center;'><font size =4>" );
		
		h_bot_1_1_b.append(getGuiStrs("numberDocReportName") );
		
		h_bot_1_1_b.append("</font></td>");
		
		StringBuilder  h_bot_1_2_b = new StringBuilder(500);
		
		StringBuilder h_bot_1_3_b = new StringBuilder();
		
		h_bot_1_3_b.append("<td  rowspan='2' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4> " + getGuiStrs("polPunktHarchuvReportName") + "</font></td>"); 
		
		h_bot_1_3_b.append("<td rowspan='2'style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" + getGuiStrs("zagalviiskovaReportNAme") + "</font></td>");
		
		h_bot_1_3_b.append("<td colspan='3' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4> " + getGuiStrs("priiomIgiReportName") + "</font></td>");

		StringBuilder h_bot_1_4_b = new StringBuilder();
		
		h_bot_1_4_b.append(
				"<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" + getGuiStrs("snidanokReportName") + "</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4> " + getGuiStrs("obidReportName") + "</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" + getGuiStrs("vecheryaReportName") + "</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'></td>");
		
		StringBuilder h_bot_2_b = new StringBuilder();
		
		h_bot_2_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>1</font></td>" 
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>2</font></td>" 
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>3</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>4</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>5</font></td>" 
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>6</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>7</font></td>"
				+ "<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>8</font></td>");
		
		StringBuilder s_prihod_sum_b = new StringBuilder();
		
		s_prihod_sum_b.append("<td colspan='7' nowrap style='  border-left: 1px solid; border-top: 1px solid ;  " );
		
		s_prihod_sum_b.append( "'><font size =4>"); 
		
		s_prihod_sum_b.append(getGuiStrs("allWithTheRest") );
		
		s_prihod_sum_b.append("</font></td>");
		
		s_prihod_sum_b.append( "<td  nowrap style=' border-left: 1px solid; border-top: 1px solid ;  "); 
		
		s_prihod_sum_b.append( "'><font size =4></font></td>");
		
		StringBuilder s_rashod_sum_b = new StringBuilder();
		
		s_rashod_sum_b.append("<td colspan='8' nowrap style=' border-left: 1px solid; border-top: 1px solid;  "); 
		
		s_rashod_sum_b.append("'><font size =4>"); 
		
		s_rashod_sum_b.append(getGuiStrs("allRashodNaReportName") );
		
		s_rashod_sum_b.append("</font></td>");
		
		StringBuilder s_global_rest_b = new StringBuilder();
		
		s_global_rest_b.append("<td colspan='8' nowrap style='  border-left: 1px solid; border-top: 1px solid ; ");
		
		s_global_rest_b.append(" border-bottom: 1px solid ; " );
		
		s_global_rest_b.append("'><font size =4>" );
		
		s_global_rest_b.append(getGuiStrs("zalishokNaReportName") );
		
		s_global_rest_b.append( " " );
		
		s_global_rest_b.append(WsUtils.dateToString(m_date.getEndDate(), "dd.MM.yy" ) );
		
		s_global_rest_b.append("</font></td>");
		
		StringBuilder row_s_prihod_b = new StringBuilder(1000);
		
		StringBuilder row_s_rashod_b = new StringBuilder(1000);
		
		Vector<WsItogoData> v_itogo = getItogoForPage(vec_all_parts, page_number);
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.pages_row_vec.elementAt( page_number);
				
			 String bottomBorder = "";
			 		 
			 String dateIn ="";
			 
			 String agentName ="";
			 
			 StringBuilder s_prihod_b = new  StringBuilder(500);
			 
			 StringBuilder s_rashod_b = new  StringBuilder(500);
			 
			 switch(d_row.indexData) {
			 
				 case 0:{ 
	
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 agentName = "";
					 
					 s_prihod_b.append("<td colspan='7' nowrap style='  border-left: 1px solid; border-top: 1px solid ;  "); 
					 
					 s_prihod_b.append(bottomBorder );
					 
					 s_prihod_b.append("'><font size =4>"); 
					 
					 s_prihod_b.append("&nbsp;  " );
					 
					 s_prihod_b.append(getGuiStrs("zalishokNaReportName") );
					 
					 s_prihod_b.append(" ");
					 
					 s_prihod_b.append(WsUtils.dateToString(m_date.getStartDate(), "dd.MM.yy" ));
					 
					 s_prihod_b.append("</font></td>");
					 
					 s_prihod_b.append("<td  nowrap style='  border-left: 1px solid; border-top: 1px solid ;  "); 
					 
					 s_prihod_b.append(bottomBorder );
					 
					 s_prihod_b.append("'><font size =4></font></td>");
						
					 break;
				 }
				 case 1:  {

					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 agentName = d_row.agent_name;

					s_prihod_b.append("<td nowrap style=' max-width: 50px; border-left: 1px solid; border-top: 1px solid ;  text-align: center; "); 
					
					s_prihod_b.append(bottomBorder); 
					
					s_prihod_b.append("'><font size =4>" );
					
					s_prihod_b.append(dateIn );
					
					s_prihod_b.append("</font></td>");
					
					s_prihod_b.append("<td colspan='2' style='  border-left: 1px solid; border-top: 1px solid ;  text-align: center;  "); 
					
					s_prihod_b.append(bottomBorder); 
					
					s_prihod_b.append( "'><font size =4>"); 
					
					s_prihod_b.append( agentName );
					
					s_prihod_b.append( "</font></td>");
					
					s_prihod_b.append("<td colspan='4' style='  border-left: 1px solid; border-top: 1px solid ;  text-align: center; "); 
					
					s_prihod_b.append(bottomBorder );
					
					s_prihod_b.append( "'><font size =4>" );
					
					s_prihod_b.append(getGuiStrs("naklReportName2")); 
					
					s_prihod_b.append("</font></td>");
					
					s_prihod_b.append("<td nowrap style=' border-left: 1px solid; border-top: 1px solid ;  " );
					
					s_prihod_b.append(bottomBorder );
					
					s_prihod_b.append("'><font size =4>" );
					
					s_prihod_b.append( d_row.nakl_number );
					
					s_prihod_b.append( "</font></td>");
					 
					break;
				 }
				 case 2 : {
					 
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 agentName = d_row.agent_name;
					 
					 s_rashod_b.append("<td nowrap style=' min-width: 50px; border-left: 1px solid; border-top: 1px solid ; text-align: center; "); 
					
					 s_rashod_b.append(bottomBorder );
				 	
					 s_rashod_b.append("'><font size =4>" );
					 
				 	 s_rashod_b.append(dateIn );
					 
					 s_rashod_b.append("</font></td>");
					
					 s_rashod_b.append("<td nowrap style=' border-left: 1px solid; border-top: 1px solid ;  "); 
					
					 s_rashod_b.append(bottomBorder );
					
					 s_rashod_b.append("'><font size =4></font></td>"); 
					
					 s_rashod_b.append("<td nowrap style='  border-left: 1px solid; border-top: 1px solid ;  "); 
					 
					 s_rashod_b.append(bottomBorder );
					
					 s_rashod_b.append("'><font size =4></font></td>"); 
					 
					 s_rashod_b.append("<td nowrap style=' border-left: 1px solid; border-top: 1px solid ; text-align: center; ");  
					
					 s_rashod_b.append(bottomBorder );
					 
					 s_rashod_b.append("'><font size =4>" );
					
					 s_rashod_b.append(String.valueOf(d_row.people) );
					
					 s_rashod_b.append("</font></td>"); 
					 
					 s_rashod_b.append("<td nowrap style='  border-left: 1px solid; border-top: 1px solid ; text-align: center; ");  
					 
					 s_rashod_b.append(bottomBorder );
					 
					 s_rashod_b.append("'><font size =4>");
					 
					 s_rashod_b.append(String.valueOf(d_row.people) );
					 
					 s_rashod_b.append("</font></td>"); 
					 
					 s_rashod_b.append("<td nowrap style='  border-left: 1px solid; border-top: 1px solid ; text-align: center; ");  
					
					 s_rashod_b.append(bottomBorder );
					 
					 s_rashod_b.append("'><font size =4>" );
					 
					 s_rashod_b.append(String.valueOf(d_row.people) );
					 
					 s_rashod_b.append("</font></td>"); 
					 
					 s_rashod_b.append("<td nowrap style='  border-left: 1px solid; border-top: 1px solid ; text-align: center; ");  
					
					 s_rashod_b.append(bottomBorder );
					 
					 s_rashod_b.append("'><font size =4>"); 
				     
					 s_rashod_b.append(getGuiStrs("naklReportName2") );
					 
				     s_rashod_b.append("</font></td>"); 
					 
					 s_rashod_b.append("<td nowrap style='  border-left: 1px solid; border-top: 1px solid ; text-align: center; ");  
					
					 s_rashod_b.append(bottomBorder); 
					 
					 s_rashod_b.append("'><font size =4>");
					 
					 s_rashod_b.append(d_row.nakl_number );
					 
					 s_rashod_b.append("</font></td>");
					 
					 break;
				 }
			 };
			
			for(int j = 0; j < row_vec.size(); ++j) {
			
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 String rightBorder = "";
				
				 
				 if(j == (row_vec.size() -1)) {
					 
					 rightBorder = "border-right: 1px solid ;";
				 }
				 
				 switch(d_row.indexData) {
					 
					 case 0:{
						 
						 s_prihod_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
						
						 s_prihod_b.append(rightBorder); 
						
						 s_prihod_b.append(bottomBorder); 
						 
						 s_prihod_b.append(" text-align: center; '><font size =4>"); 
						 
						 s_prihod_b.append(WsUtils.getDF(dc.rest) );
						
						 s_prihod_b.append("</font></td>");
						 
						 break;
					 }
					 case 1:{
						 
						 s_prihod_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
						
						 s_prihod_b.append(rightBorder );
						 
						 s_prihod_b.append(bottomBorder );
						
						 s_prihod_b.append(" text-align: center; '><font size =4>" );
						
						 s_prihod_b.append(WsUtils.getDF(dc.in_quantity) );
						
						 s_prihod_b.append("</font></td>");
						 
						 break;
					 }
					 case 2:{
						 
						 s_rashod_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; " );
						
						 s_rashod_b.append(rightBorder );
						 
						 s_rashod_b.append(bottomBorder );
					     
						 s_rashod_b.append(" text-align: center; '><font size =4>" );
						
						 s_rashod_b.append(WsUtils.getDF(dc.out_quantity) );
					    
						 s_rashod_b.append("</font></td>");
							
						 break;
					 }
			
				 };
			
				 if( i == 0) {
					 
					 if(j == 0) {
					 
						h_top_1_1_b.append("<td colspan='3' nowrap style=' border-left: 1px solid; border-top: 1px solid ;");
						
						h_top_1_1_b.append(rightBorder );
						
						h_top_1_1_b.append(" text-align: center;'><font size =4>" );
						
						h_top_1_1_b.append( getGuiStrs("nameProductsReportName") );
						
						h_top_1_1_b.append("</font></td>");
						 
					 }
					 
					 if(j > 2) {
						 
						h_top_1_1_b.append("<td nowrap style='border-top: 1px solid ;" );
						
						h_top_1_1_b.append(rightBorder );
						
						h_top_1_1_b.append( " text-align: center;'><font size =4></font></td>");
						 
					 }

					 h_top_1_2_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 h_top_1_2_b.append(rightBorder );
					 
					 h_top_1_2_b.append(" text-align: center;'><font size =4>" );
					 
					 h_top_1_2_b.append( String.valueOf(dc.kod) );
					 
					 h_top_1_2_b.append(  "</font></td>");
					 
					 h_top_1_3_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;" );
				     
					 h_top_1_3_b.append(rightBorder );
					 
					 h_top_1_3_b.append( " text-align: center;'><font size =4>" );
					 
					 h_top_1_3_b.append( dc.name );
					 
					 h_top_1_3_b.append(  "</font></td>");
						
					 h_top_1_5_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 h_top_1_5_b.append(rightBorder );
					 
					 h_top_1_5_b.append(" text-align: center;'><font size =4>" );
					 
					 h_top_1_5_b.append(v_itogo.elementAt(j).units );
					 
					 h_top_1_5_b.append("</font></td>");
					
					 h_top_2_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 h_top_2_b.append(rightBorder );
					 
					 h_top_2_b.append(" text-align: center;'><font size =4>");
					 
					 h_top_2_b.append(String.valueOf(global_index + 5));
				     
					 h_top_2_b.append("</font></td>");
					  
					 h_top_3_b.append("<td style='border-top: 1px solid ;" );
					 
					 h_top_3_b.append(rightBorder );
					 
					 h_top_3_b.append(" text-align: center;'><font size =4></font></td>");
					
					 if(j == 0) {
						 
						 h_bot_1_1_b.append("<td style='border-left: 1px solid; border-top: 1px solid ;"); 
						 
						 h_bot_1_1_b.append(rightBorder); 
						 
						 h_bot_1_1_b.append(" text-align: center;'><font size =4>&nbsp</font></td>");
						 
						 h_bot_1_3_b.append("<td style='border-left: 1px solid; border-top: 1px solid ;" );
						 
						 h_bot_1_3_b.append(rightBorder );
						 
						 h_bot_1_3_b.append(" text-align: center;'><font size =4>&nbsp</font></td>");
						 
						 h_top_1_4_b.append("<td  style=' border-left: 1px solid; border-top: 1px solid ;" );
						 
						 h_top_1_4_b.append(rightBorder );
						 
						 h_top_1_4_b.append(" text-align: center;'><font size =4></font>&nbsp</td>");
							
					 }
					 else {
						 
						 h_bot_1_1_b.append("<td style='border-top: 1px solid ;"); 
						 
						 h_bot_1_1_b.append(rightBorder); 
						 
						 h_bot_1_1_b.append(" text-align: center;'><font size =4></font></td>");
						 
						 h_bot_1_3_b.append("<td style='border-top: 1px solid ;" );
						 
						 h_bot_1_3_b.append( rightBorder );
						 
						 h_bot_1_3_b.append( " text-align: center;'><font size =4></font></td>");

						 h_top_1_4_b.append("<td  style='border-top: 1px solid ;" );
						 
						 h_top_1_4_b.append( rightBorder);
						 
						 h_top_1_4_b.append( " text-align: center;'><font size =4></font>&nbsp</td>");
					 }
					 
					 h_bot_1_2_b.append("<td  class='verticalTableHeader' style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 h_bot_1_2_b.append(rightBorder );
					 
					 h_bot_1_2_b.append(" text-align: center;'><font size =4>" );
					 
					 h_bot_1_2_b.append(dc.name );
					 
					 h_bot_1_2_b.append( "</font></td>");

					 h_bot_2_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;"); 
					 
					 h_bot_2_b.append(rightBorder );
					 
					 h_bot_2_b.append(" text-align: center;'><font size =4>");
					 
					 h_bot_2_b.append(String.valueOf(global_index + 9));
					 
					 h_bot_2_b.append("</font></td>");
					  
					 h_bot_1_4_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;" );
					 
					 h_bot_1_4_b.append(rightBorder); 
					 
					 h_bot_1_4_b.append(" text-align: center;'><font size =4>"); 
					 
					 h_bot_1_4_b.append(v_itogo.elementAt(j).units); 
					 
					 h_bot_1_4_b.append("</font></td>");
					
					 s_prihod_sum_b.append("<td style='border-left: 1px solid;border-top: 1px solid;"); 
					 
					 s_prihod_sum_b.append(rightBorder); 
					 
					 s_prihod_sum_b.append(" text-align: center;'><font size =4>"); 
					 
					 s_prihod_sum_b.append(WsUtils.getDF(v_itogo.elementAt(j).sum1) );
					 
					 s_prihod_sum_b.append("</font></td>");
						
					 s_rashod_sum_b.append("<td style='border-left: 1px solid; border-top: 1px solid; "); 
					 
					 s_rashod_sum_b.append(rightBorder); 
					 
					 s_rashod_sum_b.append(" text-align: center;'><font size =4>"); 
					 
					 s_rashod_sum_b.append(WsUtils.getDF(v_itogo.elementAt(j).sum2)); 
					 
					 s_rashod_sum_b.append("</font></td>");
					
					 s_global_rest_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;" );
					 
					 s_global_rest_b.append(rightBorder );
					 
					 s_global_rest_b.append(" text-align: center;'><font size =4>"); 
					 
					 s_global_rest_b.append(WsUtils.getDF(v_itogo.elementAt(j).sum3) );
					 
					 s_global_rest_b.append("</font></td>");
					 
					 global_index++;
				 }
			}
			
			s_prihod_b.append("</tr>");
			
			s_rashod_b.append("</tr>");
			
			row_s_prihod_b.append( s_prihod_b.toString());
			
			s_prihod_b = null;

			row_s_rashod_b.append(s_rashod_b.toString()) ;
			
			s_rashod_b = null;
		}
		
		 h_top_1_1_b.append("</tr>");
		
		 h_top_1_2_b.append("</tr>");
		 
	     h_top_1_3_b.append("</tr>");
	     
	     h_top_1_4_b.append("</tr>");
					 
		 h_top_1_5_b.append("</tr>");

		 h_top_2_b.append("</tr>");
		 
		 s_prihod_sum_b.append("</tr>");
		 
		 h_top_3_b.append("</tr>");
		 
		 s_rashod_sum_b.append("</tr>");
		 
		 h_bot_1_1_b.append("</tr>");

		 h_bot_1_2_b.append("</tr>");
			
		 h_bot_1_3_b.append("</tr>");
				
	     h_bot_1_4_b.append("</tr>");
	     
		 h_bot_2_b.append("</tr>");
			
		StringBuilder hS_b = new  StringBuilder();
		
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append("        height: 210mm;\r\n");
		
		hS_b.append("        width: 297mm;\r\n");
		
		hS_b.append("        /* to centre page on screen*/\r\n");
		
		hS_b.append("        margin-left: 20;\r\n");
		
		hS_b.append("        margin-right: auto;\r\n");
		
		hS_b.append("    }");
		
		hS_b.append("</style><body>");
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append(getGuiStrs("kartkaZvitMovementReportName") );
		
		hS_b.append(" " );
		
		hS_b.append(date_s );
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append( " ");
		
		hS_b.append(date_e  );
		
		hS_b.append("  " );
		
		hS_b.append( firm_name );
		
		hS_b.append("   " );
		
		hS_b.append(getGuiStrs("prodSkladNameReport")  );
		
		hS_b.append("</font></h2>");
		
		hS_b.append("<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(h_top_1_1_b.toString()); 
		
		hS_b.append(h_top_1_2_b.toString());
		
		hS_b.append(h_top_1_3_b.toString());
		
		hS_b.append(h_top_1_4_b.toString()); 
		
		hS_b.append(h_top_1_5_b.toString());
		
		hS_b.append(h_top_2_b.toString()); 
		
		hS_b.append(row_s_prihod_b.toString());
		
		hS_b.append(s_prihod_sum_b.toString());
		
		hS_b.append(h_top_3_b.toString() );
		
		hS_b.append(h_bot_1_1_b.toString() );
		
		hS_b.append(h_bot_1_2_b.toString()); 	
		
		hS_b.append(h_bot_1_3_b.toString()); 	
		
		hS_b.append(h_bot_1_4_b.toString() );
		
		hS_b.append(h_bot_2_b.toString() );
		
		hS_b.append(row_s_rashod_b.toString());
		
		hS_b.append(s_rashod_sum_b.toString());
		
		hS_b.append(s_global_rest_b.toString());
		
		hS_b.append("</table><br><br><table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append("<tr><td><font size =4>" );
		
		hS_b.append(getGuiStrs("commanderReportName")); 
		
		hS_b.append(" " );
		
		hS_b.append(firm_name );
		
		hS_b.append("</font></td>___________________________________<td></td></tr><tr><td></td><td></td></tr>");
		
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
	
	public void exportToExcelFile(Vector<WsSkladMoveDataRow> vec_all_parts) {
		
		int signs = 4;
		
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
		    
		    createExcelHeader(sheet, vec_all_parts, creationHelper, wb);
		    
			XSSFCellStyle cs12 = getExcelCellStyle(wb, 1, 1, 1, 1,
						false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
			   
		    int row_index = 6;
		    
		    Vector<WsItogoData> v_itogo = getItogoAllColumns(vec_all_parts);
		    
		    
		    XSSFRow row = sheet.createRow(row_index);
		    
		    WsSkladMoveDataRow dt0 =  vec_all_parts.elementAt(0);
		    
		    row_index = createZalishokRow(sheet , row_index,  dt0,creationHelper, cs12, signs);
		    
		    ++row_index;
		    
		    row_index = createPrihodRows( sheet , row_index, vec_all_parts,
					creationHelper,  cs12, signs);
		    
		    ++row_index;
		    
		    row_index = createAllWithRestRow(wb, sheet ,row_index, 
					 vec_all_parts,  creationHelper,  
					 cs12, v_itogo, signs);
		    
		    ++row_index;
		    
	    	for (WsSkladMoveDataRow dt: vec_all_parts) {
	    		
	    		if(dt.indexData != 2) { continue; }
	    		
	            row = sheet.createRow(row_index);

    			createCell(row, 0, WsUtils.dateToString(dt.date, "dd-MM-yy" ), creationHelper); 
    			   
    			createCell(row, 1, "", creationHelper); 
    			   
    			createCell(row, 2, "", creationHelper); 
    			   
    			createCell(row, 3, String.valueOf(dt.people), creationHelper); 
    			   
    			createCell(row, 4, String.valueOf(dt.people), creationHelper);
    			   
    			createCell(row, 5, String.valueOf(dt.people), creationHelper); 
    			   
    			createCell(row, 6, getGuiStrs("naklReportName2"), creationHelper);
    			   
    			createCell(row, 7, dt.nakl_number, creationHelper);
    			   
    			   
    			for(int k =0; k < 8; ++k) {
    				   
    				   row.getCell(k).setCellStyle(cs12);
    				   
    			}
    			   
	    		int cell_index = 8;
	     
	    		row_index++;
	    		
	            Vector<WsSkladMoveDataColumn> vec_columns = dt.row_vec;
	            
	            for (int j = 0; j < vec_columns.size(); j++) {
	            	
	            	WsSkladMoveDataColumn col_data =  vec_columns.elementAt(j);
	            	
	            	XSSFCell cell0 = row.createCell(cell_index++);
	            		
	            	cell0.setCellValue(WsUtils.getDF_fix_str(col_data.out_quantity, signs));
	            		 
	            	cell0.setCellStyle(cs12);
	                	            
	            }
	                             
	          }
	    	
	    	  XSSFRow row_last = sheet.createRow(row_index++);
	    	  
	    	  createCell(row_last, 0, getGuiStrs("allRashodNaReportName") + " ", creationHelper); 
	    	  
	    	  row_last.getCell(0).setCellStyle(cs12);
			   
			  for(int i = 1; i < 8; ++i) {   
				  
				  createCell(row_last, i, "", creationHelper); 
				  
				  row_last.getCell(i).setCellStyle(cs12);
			  
			  }
			  
			  sheet.addMergedRegion(new CellRangeAddress(row_last.getRowNum(),row_last.getRowNum(),0, 7));
			   
	    	  XSSFRow row_last1 = sheet.createRow(row_index++);
	    		
	    	  createCell(row_last1, 0, getGuiStrs("zalishokNaReportName") + " " + WsUtils.dateToString(this.m_date.getEndDate(), "dd-MM-yy" ), creationHelper); 
			   
	    	  row_last1.getCell(0).setCellStyle(cs12);
	    	  
			  for(int i = 1; i < 8; ++i) {   
				  
				  createCell(row_last1, i, "", creationHelper);   
				  
				  row_last1.getCell(i).setCellStyle(cs12);
			  
			  }
			  
			  sheet.addMergedRegion(new CellRangeAddress(row_last1.getRowNum(),row_last1.getRowNum(),0, 7));
			  
			  int index_cell = 8;
			 
			  for(WsItogoData d: v_itogo) {
				  
				  createCell(row_last, index_cell, WsUtils.getDF_fix_str(d.sum2, signs), creationHelper); 
				  
				  createCell(row_last1, index_cell, WsUtils.getDF_fix_str(d.sum3, signs), creationHelper); 
				  
				  row_last1.getCell(index_cell).setCellStyle(cs12);
				  
				  row_last.getCell(index_cell).setCellStyle(cs12);
				  
				  index_cell++;
			  }

			  XSSFRow row_footer = sheet.createRow(row_index);
			  
			  createCell( row_footer, 0, getGuiStrs("commanderReportName") + " " + WsUtils.getFirmName(), creationHelper); 
			  
			  createCell( row_footer, 1, "", creationHelper); 
			  
			  createCell( row_footer, 2, "", creationHelper); 
			  
			  createCell( row_footer, 3, "____________________________", creationHelper); 
			  
			  createCell( row_footer, 4, "", creationHelper); 
			  
			  createCell( row_footer, 5, "", creationHelper); 
			  
			  sheet.addMergedRegion(new CellRangeAddress(row_footer.getRowNum(),row_footer.getRowNum(),0, 2));
			 
			  sheet.addMergedRegion(new CellRangeAddress(row_footer.getRowNum(),row_footer.getRowNum(),3, 5));

			  wb.write(out);
	
			  out.close();
	    
			  wb.close(); 
			
			  setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			  JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("excelExportSuccessMessage"),
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
	
	
	private void createExcelHeader( XSSFSheet sheet, Vector<WsSkladMoveDataRow> vec_all_parts, XSSFCreationHelper creationHelper,
			XSSFWorkbook wb) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);
		   
		   createCell(rowHeader0, 0, getGuiStrs("dataNadhodgReport"), creationHelper); 
		   
		   createCell(rowHeader0, 1, getGuiStrs("zvidNadihlpReportName"), creationHelper); 
		   
		   createCell(rowHeader0, 2, "", creationHelper); 
		   
		   createCell(rowHeader0, 3, getGuiStrs("docNameReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, "", creationHelper); 
		   
		   createCell(rowHeader0, 5, "", creationHelper);
		   
		   createCell(rowHeader0, 6, "", creationHelper); 
		   
		   createCell(rowHeader0, 7, getGuiStrs("numberDocReportName"), creationHelper);
		   
		   
		   XSSFCellStyle cs00 = getExcelCellStyle(wb, 1, 1, 1, 1,
					false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   rowHeader0.getCell(1).setCellStyle(cs00);
		   
		   rowHeader0.getCell(3).setCellStyle(cs00);
		   
		   XSSFCellStyle cs = getExcelCellStyle(wb, 1, 1, 1, 1,
					true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   rowHeader0.getCell(7).setCellStyle(cs);

 
		   XSSFRow rowHeader1 = sheet.createRow(1);
		   
		   for(int i = 0; i < 8; ++i) { 
			   
			   createCell(rowHeader1, i, "", creationHelper);  
			   
			   rowHeader1.getCell(i).setCellStyle(cs00);
			   
		  }
		   
		   XSSFRow rowHeader2 = sheet.createRow(2);
		   
		   for(int i = 0; i < 8; ++i) {   
			   createCell(rowHeader2, i, "", creationHelper);   
			   
			   rowHeader2 .getCell(i).setCellStyle(cs00);
			   
		   }

		   XSSFRow rowHeader3 = sheet.createRow(3);
		   
		   for(int i =0; i < 8; ++i) {  
			   
			   createCell(rowHeader3, i, "", creationHelper); 
			   
			   rowHeader3.getCell(i).setCellStyle(cs00);
			   
		   }
		   
		   XSSFRow rowHeader4 = sheet.createRow(4);
		   
		   for(int i =0; i < 8; ++i) { 
			   
			   createCell(rowHeader4, i, "", creationHelper);  
			   
			   rowHeader4.getCell(i).setCellStyle(cs00);
		   }
		   
		   XSSFRow rowHeader5 = sheet.createRow(5);
		   
		   createCell(rowHeader5, 0, "1", creationHelper); 
		   
		   createCell(rowHeader5, 1, "2", creationHelper); 
		   
		   createCell(rowHeader5, 2, "", creationHelper); 
		   
		   createCell(rowHeader5, 3, "3", creationHelper);
		   
		   createCell(rowHeader5, 4, "", creationHelper); 
		   
		   createCell(rowHeader5, 5, "", creationHelper);
		   
		   createCell(rowHeader5, 6, "", creationHelper); 
		   
		   createCell(rowHeader5, 7, "4", creationHelper);
		   
		   for(int i = 0; i <8; ++i) {
			   
			   rowHeader5.getCell(i).setCellStyle(cs00);
		   }
		   
		   XSSFCellStyle cs0 = getExcelCellStyle(wb, 1, 1, 1, 1,
					true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
		   
		 
		   int cell_index = 8;
		   
		   WsSkladMoveDataRow data = vec_all_parts.elementAt(0);
		   
		   Vector<WsSkladMoveDataColumn> vec_columns = data.row_vec;
		   
		   for(int i = 0; i < vec_columns.size(); ++i) {
			   
			   WsSkladMoveDataColumn col = vec_columns.elementAt(i);
			   
			   createCell(rowHeader2, cell_index, col.name, creationHelper);
			   
			   rowHeader2.getCell(cell_index).setCellStyle(cs0);
			   
			   createCell(rowHeader1, cell_index, String.valueOf(col.kod), creationHelper);

			   rowHeader1.getCell(cell_index).setCellStyle(cs00);
			   
			   createCell(rowHeader4, cell_index, col.units, creationHelper);
			   
			   rowHeader4.getCell(cell_index).setCellStyle(cs00);
			
			   createCell(rowHeader5, cell_index, String.valueOf(5 + i), creationHelper);
			   
			   rowHeader5.getCell(cell_index).setCellStyle(cs00);

			   ++cell_index;
			   
			   
		   }
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 4,0,0));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 4,1,2));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 4,3,6));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 4,7,7));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader5.getRowNum(),rowHeader5.getRowNum(),1,2));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader5.getRowNum(),rowHeader5.getRowNum(),3,6));
		   
		   XSSFCellStyle cs1 = getExcelCellStyle(wb, 1, 1, 1, 1,
					false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
		   
		   rowHeader0.getCell(3).setCellStyle(cs1);
		   
		   rowHeader0.getCell(2).setCellStyle(cs1);
		   
		   rowHeader0.getCell(1).setCellStyle(cs1);
		   
		   rowHeader0.getCell(0).setCellStyle(cs1);
		
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
         cell3.setCellValue(richString3);
		
	}
	
	
	private Vector<WsItogoData> getItogoForPage(Vector<WsSkladMoveDataRow> vec_all_parts, int page_number) {
		
		Vector<WsItogoData> v = new Vector<WsItogoData>();
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
					
				WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
				
				Vector<WsSkladMoveDataColumn> row_vec = d_row.pages_row_vec.elementAt( page_number);
				
				for(int j = 0; j < row_vec.size(); ++j) {
					
					 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
					 
					 WsItogoData dt = null;
					 
					 if( i == 0) {
						 
						 dt = new WsItogoData();
						 
						 v.add(dt);
					 }
					 else {
						 dt = v.elementAt(j);
					 }
					 
					 dt.kod = dc.kod;
					 
					 if(dc.units != null && !dc.units.isEmpty()) {
						 
						 dt.units = dc.units;
						 
					 }
						 
					 switch(d_row.indexData) {
						 
						 case 0:{
					
							 dt.sum3 += dc.rest;
							 
							 dt.sum1 += dc.rest;
							 
							 break;
						 }
						 case 1:{
							 
							 dt.sum3 += dc.in_quantity;
							 
							 dt.sum1 += dc.in_quantity;
							 
							 
							 break;
						 }
						 case 2:{
							 
					
							 dt.sum3 -= dc.out_quantity;
							 
							 dt.sum2  += dc.out_quantity;
							
							 
							 break;
						 }
				
					 };
				}
					
					
		}
		
		return v;
		
	}
	
	private HashSet<Integer> getZeroColumns(Vector<WsSkladMoveDataRow> vec_all_parts) {
		
		HashSet<Integer> v = new HashSet<Integer>();
		
		Vector<WsItogoData> vec = new Vector<WsItogoData>();
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
					
				WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
						
				Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
				
				for(int j = 0; j < row_vec.size(); ++j) {
					
					 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
					 
					 WsItogoData dt = null;
					 
					 if( i == 0) {
						 
						 dt = new WsItogoData();
						 
						 vec.add(dt);
					 }
					 else {
						 dt = vec.elementAt(j);
					 }
					 
					 dt.kod = dc.kod;
					 
					 switch(d_row.indexData) {
						 
						 case 0:{
					
							 dt.sum3 += dc.rest;
							 dt.sum1 += dc.rest;
							 break;
						 }
						 case 1:{
							 
							 dt.sum3 += dc.in_quantity;
							 dt.sum1 += dc.in_quantity;
							 
							 break;
						 }
						 case 2:{
							 
					
							 dt.sum3 -= dc.out_quantity;
							 dt.sum2  += dc.out_quantity;
							
							 
							 break;
						 }
				
					 };
				}
					
					
		}
		
		for(WsItogoData d: vec) {
			
			if(d.sum1 < WsUtils.getRZL() && d.sum2 < WsUtils.getRZL() && d.sum3 < WsUtils.getRZL() ) {
				
				v.add(d.kod);
				
			}
		}
		
		return v;
		
	}
	

	private void removeZeroColumns(Vector<WsSkladMoveDataRow> vec_all_parts) {
		
		HashSet<Integer> v = getZeroColumns(vec_all_parts);
			
		for(int i = 0; i < vec_all_parts.size(); ++i) {
					
				WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
				
				Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
				
				Vector<WsSkladMoveDataColumn> new_row_vec = new Vector<WsSkladMoveDataColumn>();
				
				for(int j = 0; j < row_vec.size(); ++j) {
					
					 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
					 
					 if( !v.contains(dc.kod) ) {
						 
						 new_row_vec.add(dc);
					 }
					 
				}	
				
				d_row.row_vec = new_row_vec;					
		}
	}
	
	
	private int createExcelHeader2( XSSFSheet sheet, Vector<WsSkladMoveDataRow> vec_all_parts, 
			XSSFCreationHelper creationHelper, XSSFWorkbook wb,
			int current_row_index) {
		
		   XSSFCellStyle cs02 = getExcelCellStyle(wb, 1, 1, 1, 1,
					false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, true);
		
			current_row_index++;
		
		   XSSFRow rowHeader00 = sheet.createRow(current_row_index);
		   
		   createCell(rowHeader00, 0, getGuiStrs("vitrataReportName"), creationHelper); 
		   
		   rowHeader00.getCell(0).setCellStyle(cs02);
		   
		   for(int i = 1; i < 8; ++i) {   
			   
			   createCell(rowHeader00, i, "", creationHelper); 
			   
			   rowHeader00.getCell(i).setCellStyle(cs02);
			   
		   }

		   current_row_index++;
		
		   XSSFRow rowHeader0 = sheet.createRow(current_row_index);
		   
		   createCell(rowHeader0, 0, getGuiStrs("dateOfMonthReportName"), creationHelper); 
		   
		   createCell(rowHeader0, 1, getGuiStrs("normZabezpReportName"), creationHelper); 
		   
		   createCell(rowHeader0, 2, "", creationHelper); 
		   
		   createCell(rowHeader0, 3, "", creationHelper);
		   
		   createCell(rowHeader0, 4, "", creationHelper); 
		   
		   createCell(rowHeader0, 5, "", creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("docNameReportName2"), creationHelper); 
		   
		   createCell(rowHeader0, 7, getGuiStrs("numberDocReportName"), creationHelper);
		   
		   current_row_index++;
		   
		   XSSFRow rowHeader1 = sheet.createRow(current_row_index);
		   
		   for(int i = 0; i < 8; ++i) {  
			   
			   createCell(rowHeader1, i, "", creationHelper);   
			   
			   rowHeader1.getCell(i).setCellStyle(cs02);
			   
		   }
		   
		   current_row_index++;
		   
		   XSSFRow rowHeader2 = sheet.createRow( current_row_index);
		   
		   createCell(rowHeader2, 0, "", creationHelper); 
		   
		   createCell(rowHeader2, 1, getGuiStrs("polPunktHarchuvReportName"), creationHelper); 
		   
		   createCell(rowHeader2, 2, getGuiStrs("zagalviiskovaReportNAme"), creationHelper);
		   
		   createCell(rowHeader2, 3, getGuiStrs("priiomIgiReportName"), creationHelper);
		   
		   createCell(rowHeader2, 4, "", creationHelper); 
		   
		   createCell(rowHeader2, 5, "", creationHelper);
		   
		   createCell(rowHeader2, 6, "", creationHelper); 
		   
		   createCell(rowHeader2, 7, "", creationHelper);
		   
		   current_row_index++;
		   
		   XSSFRow rowHeader3 = sheet.createRow( current_row_index);
		   
		   createCell(rowHeader3, 0, "", creationHelper); 
		   
		   createCell(rowHeader3, 1, "", creationHelper); 
		   
		   createCell(rowHeader3, 2, "", creationHelper); 
		   
		   createCell(rowHeader3, 3, getGuiStrs("snidanokReportName"), creationHelper);
		   
		   createCell(rowHeader3, 4, getGuiStrs("obidReportName"), creationHelper); 
		   
		   createCell(rowHeader3, 5, getGuiStrs("vecheryaReportName"), creationHelper);
		   
		   createCell(rowHeader3, 6, "", creationHelper); 
		   
		   createCell(rowHeader3, 7, "", creationHelper);
		      
		   current_row_index++;
		   
		   XSSFRow rowHeader4 = sheet.createRow( current_row_index);
		   
		   createCell(rowHeader4, 0, "1", creationHelper); 
		   
		   createCell(rowHeader4, 1, "2", creationHelper); 
		   
		   createCell(rowHeader4, 2, "3", creationHelper); 
		   
		   createCell(rowHeader4, 3, "4", creationHelper);
		   
		   createCell(rowHeader4, 4, "5", creationHelper); 
		   
		   createCell(rowHeader4, 5, "6", creationHelper);
		   
		   createCell(rowHeader4, 6, "7", creationHelper); 
		   
		   createCell(rowHeader4, 7, "8", creationHelper);  
		   
		   for(int l =0; l < 8; ++l) {
			   
			   rowHeader4.getCell(l).setCellStyle(cs02);
		   }
	
		   XSSFCellStyle cs0 = getExcelCellStyle(wb, 1, 1, 1, 1,
					true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);

		   int cell_index = 8;
		   
		   WsSkladMoveDataRow data = vec_all_parts.elementAt(0);
		   
		   Vector<WsSkladMoveDataColumn> vec_columns = data.row_vec;
		   
		   for(int i = 0; i < vec_columns.size(); ++i) {
			   
			   WsSkladMoveDataColumn col = vec_columns.elementAt(i);
			   
			   createCell(rowHeader1, cell_index, col.name, creationHelper);
			   
			   rowHeader1.getCell(cell_index).setCellStyle(cs0);
		
			   createCell(rowHeader00, cell_index, "", creationHelper);
			   
			   rowHeader00.getCell(cell_index).setCellStyle(cs02);
			   
			   createCell(rowHeader3, cell_index, col.units, creationHelper);
			   
			   rowHeader3.getCell(cell_index).setCellStyle(cs02);
			
			   createCell(rowHeader4, cell_index, String.valueOf(9 + i), creationHelper);
			   
			   rowHeader4.getCell(cell_index).setCellStyle(cs02);

			   ++cell_index;
 
		   }
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader00.getRowNum(),rowHeader00.getRowNum(),0,8 + vec_columns.size()));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 3,
				   0, 0));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 1,1, 5));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader2.getRowNum(),rowHeader2.getRowNum() + 1,1, 1));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader2.getRowNum(),rowHeader2.getRowNum() + 1,2, 2));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader2.getRowNum(),rowHeader2.getRowNum(),3, 5));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 3,6, 6));
		   
		   sheet.addMergedRegion(new CellRangeAddress(rowHeader0.getRowNum(),rowHeader0.getRowNum() + 3,7, 7));
		   
		   
		   XSSFCellStyle cs01 = getExcelCellStyle(wb, 1, 1, 1, 1,
					true, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
		   
		   rowHeader0.getCell(0).setCellStyle(cs01);
		   
		   rowHeader0.getCell(6).setCellStyle(cs01);
		   
		   rowHeader0.getCell(7).setCellStyle(cs01);
		
		   rowHeader0.getCell(2).setCellStyle(cs01);
		
		   rowHeader0.getCell(1).setCellStyle(cs02);
		   
		   rowHeader3.getCell(1).setCellStyle(cs02);
		   
		   rowHeader3.getCell(2).setCellStyle(cs02);
		   
		   rowHeader3.getCell(3).setCellStyle(cs02);
		   
		   rowHeader3.getCell(4).setCellStyle(cs02);
		   
		   rowHeader3.getCell(5).setCellStyle(cs02);
		   
		   rowHeader3.getCell(6).setCellStyle(cs02);
		   
		   XSSFCellStyle cs021 = getExcelCellStyle(wb, 1, 1, 1, 1,
					false, HorizontalAlignment.CENTER, VerticalAlignment.TOP, true);
		   

		   rowHeader2.getCell(1).setCellStyle(cs021);
		   
		   rowHeader2.getCell(2).setCellStyle(cs021);
		   
		   rowHeader2.getCell(3).setCellStyle(cs021);
		   
		   rowHeader2.getCell(4).setCellStyle(cs021);
		   
		   rowHeader2.getCell(5).setCellStyle(cs021);
		   
		   rowHeader2.getCell(7).setCellStyle(cs021);
		   
		   
		return current_row_index;
	}
	
	
	private Vector<WsItogoData> getItogoAllColumns(Vector<WsSkladMoveDataRow> vec_all_parts) {
		
		Vector<WsItogoData> vec = new Vector<WsItogoData>();
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
					
			WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
					
			Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
			
			for(int j = 0; j < row_vec.size(); ++j) {
				
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 WsItogoData dt = null;
				 
				 if( i == 0) {
					 
					 dt = new WsItogoData();
					 
					 vec.add(dt);
				 }
				 else {
					 
					 dt = vec.elementAt(j);
				 }
				 
				 dt.kod = dc.kod;
				 
				 switch(d_row.indexData) {
					 
					 case 0:{
				
						 dt.sum3 += dc.rest;
						 
						 dt.sum1 += dc.rest;
						 
						 break;
					 }
					 case 1:{
						 
						 dt.sum3 += dc.in_quantity;
						 
						 dt.sum1 += dc.in_quantity;
						 
						 break;
					 }
					 case 2:{
						 
						 dt.sum3 -= dc.out_quantity;
						 
						 dt.sum2  += dc.out_quantity;
						
						 break;
					 }
			
				 };
			}
					
					
		}
		
		return vec;
		
	}
	
	
	private int createZalishokRow(   XSSFSheet sheet , int row_index,  WsSkladMoveDataRow dt0, XSSFCreationHelper creationHelper,
		  XSSFCellStyle cs12, int signs) {
	  
	  
	   XSSFRow row = sheet.createRow(row_index);
		  
	   createCell(row, 0, getGuiStrs("zalishokNaReportName") + " " + WsUtils.dateToString(this.m_date.getStartDate(), "dd-MM-yy" ), creationHelper); 
	   
	   row.getCell(0).setCellStyle(cs12);
	   
	   for(int i = 1; i < 8; ++i) {   
		   
		   createCell(row , i, "", creationHelper); 
	   
		   row.getCell(i).setCellStyle(cs12);
	   }
	   
	   sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(),0,6));
	   
	   Vector<WsSkladMoveDataColumn> vec_columns = dt0.row_vec;
	  
	   int cell_index = 8;
	   
	    for (int j = 0; j < vec_columns.size(); j++) {
        	
        		WsSkladMoveDataColumn col_data =  vec_columns.elementAt(j);
        	
        		XSSFCell cell0 = row.createCell(cell_index++);
        		
        		cell0.setCellValue(WsUtils.getDF_fix_str(col_data.rest, signs));
        		
        		cell0.setCellStyle(cs12);
            
        }
	  
	    return row_index;
	  
	}

	
	private int createPrihodRows(  XSSFSheet sheet , int row_index, Vector<WsSkladMoveDataRow> vec_all_parts,
			XSSFCreationHelper creationHelper,  XSSFCellStyle cs12, int signs) {
		
		
    	for (WsSkladMoveDataRow dt: vec_all_parts) {
    		
			if(dt.indexData != 1) { continue; }
			
		    XSSFRow row = sheet.createRow(row_index);
	        
		   createCell(row, 0, WsUtils.dateToString(dt.date, "dd-MM-yy" ), creationHelper); 
				  
		   createCell(row, 1, dt.agent_name, creationHelper); 
				   
		   createCell(row, 2, "", creationHelper); 
    			   
		   createCell(row, 3, getGuiStrs("naklReportName2"), creationHelper);
		   
		   createCell(row, 4, "", creationHelper); 
		   
		   createCell(row, 5, "", creationHelper);
		   
		   createCell(row, 6, "", creationHelper); 
		   
		   createCell(row, 7, dt.nakl_number, creationHelper);
		   
		   sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(),1,2));
		   
		   sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(),3,6));
		   
		   row.getCell(0).setCellStyle(cs12);
		   
		   row.getCell(1).setCellStyle(cs12);
		   
		   row.getCell(2).setCellStyle(cs12);
		   
		   row.getCell(3).setCellStyle(cs12);
		   
		   row.getCell(4).setCellStyle(cs12);
		   
		   row.getCell(5).setCellStyle(cs12);
		   
		   row.getCell(6).setCellStyle(cs12);
		   
		   row.getCell(7).setCellStyle(cs12);

		   int cell_index = 8;
     
		   row_index++;
    		
           Vector<WsSkladMoveDataColumn> vec_columns = dt.row_vec;
            
      
           for (int j = 0; j < vec_columns.size(); j++) {
            	
            	WsSkladMoveDataColumn col_data =  vec_columns.elementAt(j);
            	
            	XSSFCell cell0 = row.createCell(cell_index++);
            		
            	cell0.setCellValue(WsUtils.getDF_fix_str(col_data.in_quantity, signs));
            		 
            	cell0.setCellStyle(cs12);
                        
            }                 
        }
    	
    	return --row_index;
	}
	
	
	public int createAllWithRestRow(XSSFWorkbook wb, XSSFSheet sheet , int row_index, 
			 Vector<WsSkladMoveDataRow> vec_all_parts, XSSFCreationHelper creationHelper,  
			 XSSFCellStyle cs12, Vector<WsItogoData> v_itogo, int signs) {
		
		XSSFRow row = sheet.createRow(row_index);
		
		createCell(row, 0, getGuiStrs("allWithTheRest"), creationHelper); 
		
		for(int i = 1; i < 8; ++i) {   
			
			createCell(row, i, "", creationHelper);   
		
			row.getCell(i).setCellStyle(cs12);
		}
		
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(),row.getRowNum(),0,7));
		
		for(int i1 = 0; i1 < v_itogo.size(); ++i1) {
			
			WsItogoData d =  v_itogo.elementAt(i1);
			
			createCell(row, i1 + 8,  WsUtils.getDF_fix_str(d.sum1, signs), creationHelper); 
			
			row.getCell(i1 + 8).setCellStyle(cs12);
		}
		
		row_index = createExcelHeader2(sheet, vec_all_parts, creationHelper, wb, row_index);
		
		return row_index;
		
	}
	 
}
	

