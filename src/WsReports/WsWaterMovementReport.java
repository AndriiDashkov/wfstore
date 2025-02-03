
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.Ws2DatesControl;
import WsDataStruct.WsWaterDataRow;
import WsDataStruct.WsWaterPeriodsData;
import WsDatabase.WsReportsSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsWaterMovementReport extends WSReportViewer {

	private static final long serialVersionUID = 1L;
	
	Vector<WsWaterDataRow> m_vec_all_parts = null;
	
	String m_agentName = "";
	
	Ws2DatesControl  m_date_1 = new Ws2DatesControl(false);
	
	Ws2DatesControl  m_date_2 = new Ws2DatesControl(false);
	
	Ws2DatesControl  m_date_3 = new Ws2DatesControl(false);
		
	JTextField m_val_1 = new JTextField(5);
	
	JTextField m_val_2 = new JTextField(5);
	
	JTextField m_val_3 = new JTextField(5);
	
	public WsWaterMovementReport(JFrame f, String nameFrame) {
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
		 
		 Calendar c =  Calendar.getInstance();
		  
		 int current_year = c.get(Calendar.YEAR);
		 
		 m_date_1.setStartDate(1, 0, current_year);
		 
		 m_date_1.setEndDate(31, 4, current_year);
		 
		 m_date_2.setStartDate(1, 5, current_year);
		 
		 m_date_2.setEndDate(31, 7, current_year);
		 
		 m_date_3.setStartDate(1, 8, current_year);
		 
		 m_date_3.setEndDate(31, 11, current_year);
			  
		 m_val_1.setText("1.5");
		 
		 m_val_2.setText("3");
		 
		 m_val_3.setText("1.5");
		 
	}


	private void createGui() {
		
		JPanel hor_panel1 = WsGuiTools.createHorizontalPanel();
		
		JPanel hor_panel2 = WsGuiTools.createHorizontalPanel();
		
		JPanel hor_panel3 = WsGuiTools.createHorizontalPanel();
		
		JPanel ver_panel = WsGuiTools.createVerticalPanel();
		
		ver_panel.add(hor_panel1);
		
		ver_panel.add(hor_panel2);
		
		ver_panel.add(hor_panel3);
		
		m_control_panel2.add(ver_panel);
		
		hor_panel1.add(new JLabel(getGuiStrs("kLnaCh")));
		
		hor_panel1.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		hor_panel1.add(m_val_1);
		
		hor_panel1.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		hor_panel1.add(m_date_1); 
		
		hor_panel1.add(Box.createHorizontalGlue());
		
		hor_panel2.add(new JLabel(getGuiStrs("kLnaCh")));
		
		hor_panel2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		hor_panel2.add(m_val_2); 
		
		hor_panel2.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		hor_panel2.add(m_date_2); 
		
		hor_panel2.add(Box.createHorizontalGlue());
		
		hor_panel3.add(new JLabel(getGuiStrs("kLnaCh")));
		
		hor_panel3.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		hor_panel3.add(m_val_3); 
		
		hor_panel3.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		hor_panel3.add(m_date_3); 
		
		hor_panel3.add(Box.createHorizontalGlue());
		
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		WsGuiTools.fixComponentHeightToMin(m_control_panel2);
		
		Dimension d = m_val_2.getPreferredSize();
		
		d.width = 50;
		
		m_val_2.setMaximumSize(d);
		
		m_val_1.setMaximumSize(d);
		
		m_val_3.setMaximumSize(d);
				
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<String> vec_pages = new Vector<String>();
		
		int part_sql_id =  WsUtilSqlStatements.getPartTypeForKod(WsUtils.WATER_KOD).id;
		
		WsWaterPeriodsData periods = new  WsWaterPeriodsData();
		
		periods.dateStart[0] = m_date_1.getSqlStartDate();
		
		periods.dateEnd[0] = m_date_1.getSqlEndDate();
		
		periods.dateStart[1] = m_date_2.getSqlStartDate();
		
		periods.dateEnd[1] = m_date_2.getSqlEndDate();
		
		periods.dateStart[2] = m_date_3.getSqlStartDate();
		
		periods.dateEnd[2] = m_date_3.getSqlEndDate();
		

		try {

			periods.value[0] = Double.valueOf(m_val_1.getText());
			
			periods.value[1] = Double.valueOf(m_val_2.getText());
			
			periods.value[2] = Double.valueOf(m_val_3.getText());
		}
		catch(NumberFormatException ex) {
			
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("notRightWaterValueMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			   
			   return vec_pages;
			
		}
		

		
		Vector<WsWaterDataRow> vec_all_parts = WsReportsSqlStatements.getRashodWaterForDate(
				m_date.getSqlStartDate(), m_date.getSqlEndDate(), part_sql_id, periods);
		
		

		
		String page = getPrintHtml(vec_all_parts);
			
		vec_pages.add(page);
		
		m_vec_all_parts = vec_all_parts;
		 
		current_font_size = 4;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}
	
	
	public String getPrintHtml(Vector<WsWaterDataRow> vec_all_parts) {
		
	
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader3_b = new StringBuilder();
		
		sHeader3_b.append("<tr><td   style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>");
				
		sHeader3_b.append(getGuiStrs("nmbStr"));
		
		sHeader3_b.append(" </td>");
		
		sHeader3_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader3_b.append(getGuiStrs("nameColumnReportGoodName") );
		
		sHeader3_b.append( "</td>" );
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> " );
		
		sHeader3_b.append( getGuiStrs("periodColumnReportName") );
		
		sHeader3_b.append( "</td>");
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> " );
		
		sHeader3_b.append( getGuiStrs("otrimanoColumnReportName") );
		
		sHeader3_b.append( "</td>");
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> " );
		
		sHeader3_b.append( getGuiStrs("vidanoColumnReportName") );
		
		sHeader3_b.append( "</td>");
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> " );
		
		sHeader3_b.append( getGuiStrs("zalishokColumnReportName") );
		
		sHeader3_b.append( "</td>" );
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> " );
		
		sHeader3_b.append( getGuiStrs("peopleColumnReportName") );
		
		sHeader3_b.append( "</td>");
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> " );
		
		sHeader3_b.append( getGuiStrs("consumedColumnReportName") );
		
		sHeader3_b.append( "</td>" );
		
		sHeader3_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid ; text-align: center;'> "); 
		
		sHeader3_b.append( getGuiStrs("diffColumnReportName") );
		
		sHeader3_b.append( "</td></tr>" );
		
		StringBuilder row_s_b = new StringBuilder();
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
			
			 WsWaterDataRow d_row = vec_all_parts.elementAt(i);
			
			 String bottomBorder = "";
			 
			 if(i == (vec_all_parts.size() -1)) {
				 
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 
			String endDate = WsUtils.dateToString(d_row.date_end, "dd.MM.yy" );
			
			String startDate = WsUtils.dateToString(d_row.date_start, "dd.MM.yy" );

			String period = startDate + " - " + endDate;
			
			row_s_b.append("<tr><td style=' padding : 2px; border-left: 1px solid;border-top: 1px solid ; " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(String.valueOf(i + 1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' padding : 2px; max-width: 300px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>" );
			
			row_s_b.append(d_row.part_name );
			
			row_s_b.append("</font></td>");
			
			row_s_b.append("<td nowrap style=' padding : 2px; min-width: 250px; border-left: 1px solid; border-top: 1px solid ;  ");
			
			row_s_b.append( bottomBorder );
			
			row_s_b.append("'><font size =4>" );
			
			row_s_b.append(period );
			
			row_s_b.append("</font></td>");
				
			String rightBorder = "border-right: 1px solid ;";
			
			row_s_b.append("<td style=' padding : 2px; border-left: 1px solid;border-top: 1px solid ; " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append(" text-align: center; '><font size =4>" );
			
			row_s_b.append(WsUtils.getDF(d_row.in_quantity) );
			
			row_s_b.append("</font></td>");
				 
			row_s_b.append("<td style=' padding : 2px; border-left: 1px solid;border-top: 1px solid ; " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append(" text-align: center; '><font size =4>" );
			
			row_s_b.append(WsUtils.getDF(d_row.out_quantity) );
			
			row_s_b.append("</font></td>");
				
			row_s_b.append("<td style=' padding : 2px; border-left: 1px solid;border-top: 1px solid ; " );
			
			row_s_b.append( bottomBorder );
			
			row_s_b.append(" text-align: center; '><font size =4>" );
			
			row_s_b.append( WsUtils.getDF(d_row.in_rest) );
			
			row_s_b.append( "</font></td>");
					 
			row_s_b.append("<td style='padding : 2px; border-left: 1px solid;border-top: 1px solid ; "); 
			
			row_s_b.append( bottomBorder );
			
			row_s_b.append( "text-align: center; '><font size =4>"); 
			
			row_s_b.append( WsUtils.getDF(d_row.people) );
			
			row_s_b.append(  "</font></td>");
				
			row_s_b.append("<td style='padding : 2px; border-left: 1px solid;border-top: 1px solid ; " );
			
			row_s_b.append( bottomBorder );
			
			row_s_b.append( "text-align: center; '><font size =4>" );
			
			row_s_b.append(WsUtils.getDF(d_row.consumed) );
			
			row_s_b.append(  "</font></td>");
			
			row_s_b.append("<td style='padding : 2px; border-left: 1px solid;border-top: 1px solid ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append( rightBorder );
			
			row_s_b.append( "text-align: center; '><font size =4>" );
			
			row_s_b.append( WsUtils.getDF(d_row.out_rest) );
			
			row_s_b.append(  "</font></td>");
			
			row_s_b.append("</tr>");
			

		}
		
		StringBuilder hS_b =  new StringBuilder();

		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append( "<style>    body {\r\n");
		
		hS_b.append( "        height: 210mm;\r\n");
		
		hS_b.append( "        width: 297mm;\r\n");
		
		hS_b.append( "        /* to centre page on screen*/\r\n");
		
		hS_b.append( "        margin-left: 20;\r\n");
		
		hS_b.append( "        margin-right: auto;\r\n");
		
		hS_b.append( "    }");
		
		hS_b.append( "</style><body>");
		
		hS_b.append( "<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("waterReportName") );
		
		hS_b.append( " " );
		
		hS_b.append( date_s );
		
		hS_b.append( " ");
		
		hS_b.append( getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append( " ");
		
		hS_b.append( date_e  );
		
		hS_b.append( "   " );
		
		hS_b.append( "   "  );
		
		hS_b.append( " </font></h2>");
		
		hS_b.append( "<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append( sHeader3_b.toString() );
		
		hS_b.append( row_s_b.toString());// + sFooter;
		
		hS_b.append("</table><br><br><table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>"
				+ "<tr><td></td><td></td></tr><tr><td></td><td></td></tr></table></body></html>");
		  
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
	
	
	
	public void exportToExcelFile(Vector<WsWaterDataRow> vec_all_parts) {
		
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
		    
		    int row_index = 2;
		
	
	    	for (WsWaterDataRow dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
		            cell01.setCellValue(dt.part_name);
		            
		        	String endDate = WsUtils.dateToString(dt.date_end, "dd.MM.yy" );
		        	
					String startDate = WsUtils.dateToString(dt.date_start, "dd.MM.yy" );

					String period = startDate + " - " + endDate;
					
					XSSFCell cell02 = row.createCell(cell_index++);
			            
			        cell02.setCellValue(period);
			        
			        XSSFCell cell031 = row.createCell(cell_index++);
		            
			        cell031.setCellValue(dt.in_quantity);
			        
			        XSSFCell cell03 = row.createCell(cell_index++);
		            
			        cell03.setCellValue(dt.out_quantity);
			        
			        XSSFCell cell04 = row.createCell(cell_index++);
		            
			        cell04.setCellValue(dt.in_rest);
			        
			        XSSFCell cell05 = row.createCell(cell_index++);
		            
			        cell05.setCellValue(dt.people);
			        
			        XSSFCell cell06 = row.createCell(cell_index++);
		            
			        cell06.setCellValue(dt.consumed);
			        
			        XSSFCell cell07 = row.createCell(cell_index++);
		            
			        cell07.setCellValue(dt.out_rest);
		        
	              
	          }
	
	   
			wb.write(out);
	
			out.close();
	    
			wb.close(); 
    
		} catch (IOException  e) {
		
			e.printStackTrace();
		}

	}
	
	
	private void createExcelHeader( XSSFSheet sheet, Vector<WsWaterDataRow> vec_all_parts, XSSFCreationHelper creationHelper) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);
		   
		   createCell(rowHeader0, 0, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 1, getGuiStrs("periodReportName"), creationHelper);
		   
		   createCell(rowHeader0, 2,  getGuiStrs("otrimanoColumnReportName"), creationHelper);
		   
		   createCell(rowHeader0, 3,  getGuiStrs("vidanoColumnReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4,  getGuiStrs("zalishokPochatokReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5,  getGuiStrs("kolvoPeopleReportName"), creationHelper);
		   
		   createCell(rowHeader0, 6,  getGuiStrs("spozivanoNormReportName"), creationHelper);
		   
		   createCell(rowHeader0, 7,  getGuiStrs("diffColumnReportName"), creationHelper);
		   
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
         cell3.setCellValue(richString3);
		
	}
	
}