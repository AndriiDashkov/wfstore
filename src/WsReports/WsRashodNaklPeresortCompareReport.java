
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.Vector;
import javax.swing.JFrame;
import WsDataStruct.WsSkladMoveDataColumn;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashodNaklPeresortCompareReport  extends WSReportViewer {

	
	Vector<WsSkladMoveDataColumn>  m_vec_all = null;
	
	Date m_start_date = null;
	
	Date m_end_date = null;
	 
	public WsRashodNaklPeresortCompareReport (JFrame f, String nameFrame, Vector<WsSkladMoveDataColumn>  data,
			Date d1, Date d2) {
		super(f, nameFrame);
		
		 m_vec_all = data;
		 
		 m_start_date = d1;
		 
		 m_end_date = d2;
		
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
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
	
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int rows_number = m_vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		Vector<String> vec_pages = new Vector<String>();
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
		if( pages_number == 1 && rows_number < rows_per_page ) { end_row = rows_number -1; }
		
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
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}
	
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
	
		String date_s = WsUtils.dateToString(m_start_date, "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_end_date, "dd-MMMM-yyyy" );
		
		String sHeader = "<tr>"
				+ "<td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>"
				+ "<font size =4>" + getGuiStrs("reportBookKodGoodColumn") + "</font></td>"
				+ "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>"
				+ "<font size =4>&nbsp;" + getGuiStrs("nameColumnReportGoodName") + "&nbsp;</font></td>"
				+ "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>"
				+ "<font size =4>&nbsp;" + "Вибуло" + "&nbsp;</font></td>"
				+ "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>"
				+ "<font size =4>&nbsp;" + "Вибуло Excel" + "&nbsp;</font></td>"
				+ "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>"
				+ "<font size =4>&nbsp;" + "Різниця" + "&nbsp;</font></td></tr>";

		
		String row_s ="";
		
		for(int i = start; i <= end; ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			
		//	Vector<Double> vec_itogo =  d_row.pages_itogo_vec.elementAt( page_number);
			 String bottomBorder = "";
			 
			 if(i == end) {
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 
			 
			
			String s = "<tr><td style='border-left: 1px solid;border-top: 1px solid ; "
			+ bottomBorder + "'><font size =4>&nbsp;" 
			+ String.valueOf(i + 1) +  "&nbsp;</font></td>"
		    + "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " 
			+ bottomBorder + "'><font size =4> &nbsp;" + String.valueOf(d.kod) + "&nbsp;</font></td>"
			+ "<td nowrap style=' overflow-x: hidden; max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " 
				+ bottomBorder + "'><font size =4>&nbsp;" + d.name + "&nbsp;</font></td>"
			+ "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; " 
				+ bottomBorder + "'><font size =4>&nbsp;" + WsUtils.getDF(d.out_quantity_1) + "&nbsp;</font></td>"
			+ "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; " 
				+ bottomBorder + "'><font size =4>&nbsp;" + WsUtils.getDF(d.out_quantity) + "&nbsp;</font></td>"
			+ "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; " 
				+ bottomBorder + "'><font size =4>&nbsp;" + WsUtils.getDF(d.out_quantity - d.out_quantity_1) + "&nbsp;</font></td></tr>";
		
			
			row_s += s;
		}
		
		
		String tags = "<!DOCTYPE html>";
			
		
		String hS = tags + "<html> "
				+ "<style>    body {\r\n"
				+ "        height: 297mm;\r\n"
				+ "        width: 210mm;\r\n"
				+ "        /* to centre page on screen*/\r\n"
				+ "        margin-left: auto;\r\n"
				+ "        margin-right: auto;\r\n"
				+ "    }"
				+ "</style><body>"
		+ "<h2 align='center' ><font size =5>"+ getGuiStrs("bookSkladMovementCompareReportName") + " " + date_s + " "
		+ getGuiStrs("bookSkladPoReportName") + " "
		+ date_e  +"</font></h2>"
		+ "<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>"
	     + sHeader + row_s  + "</table></body></html>";// + sFooter;

		

		  return hS;
		 
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
			
			 File path = new File("C:\\report_page_" + String.valueOf(i) + ".html");

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
	
}
