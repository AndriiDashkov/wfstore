
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodPartData;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsSkladStateReport extends WSReportViewer {

	
	Vector<WsPrihodPartData> m_data = null;
	
	java.sql.Date m_date1 = null;
	
	private boolean m_use_all_kods = false;
	

	public  WsSkladStateReport(JFrame f, String nameFrame, Vector<WsPrihodPartData> dataVec, 
			boolean useAllKods) {
		super(f, nameFrame);
		
		m_name = nameFrame;
		
		m_use_all_kods = useAllKods;
		
		m_data = new Vector<WsPrihodPartData>();
		
		for(int i = 0; i < dataVec.size(); ++i) {
			
			m_data.add(new WsPrihodPartData(dataVec.elementAt(i)));
			
		}
		
		createGui();
		
		m_genButton.setVisible(false);
	
		m_date.setVisible(false);
		
		m_date1_label.setVisible(false);
		
		m_date2_label.setVisible(false);
   
		generateReport();
		
		setText(m_html_pages.elementAt(0));
		
		currentPage = 1;
		
		setDialogCaption();
		
    	m_viewer.setSelectionStart(0);
    	
    	m_viewer.setSelectionEnd(0);
		
		m_saveExcelButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	if( !isReportEmpty() ) {
	            	
	            		exportToExcelFile();
	            	}
	             
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
		
		if(m_use_all_kods) {
			
			m_data =  WSReportUtil.fillVectorWithAllKods2(m_data);
		}
		
		int rows_number = m_data.size();
		
		//divide into pages
		int rows_per_page = 50;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number  ) { pages_number++;}
		
		pagesNum = pages_number;
    	
		if(m_html_pages != null) {
			
			m_html_pages.clear();
		}
		else {
			
			m_html_pages = new Vector<String>();
			
		}
		
		int start_index = 0;
		
		for(int k = 0; k < pages_number; ++k) { 
			
			int end_index = start_index + rows_per_page;
			
			if(end_index > m_data.size()) { end_index = m_data.size();}
			
			String page = getPrintHtml(m_data, start_index, end_index, k);
			
			m_html_pages.add(page);
			
			start_index = end_index;
		}
		
		current_font_size = 4;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return m_html_pages;
		
	}
	
	
	public String getPrintHtml(Vector<WsPrihodPartData> vec, int start_index, int end_index, int page_number) {
		
		String kodNameInReport = getGuiStrs("kodNameInReport");

		String nameNameInReport =  getGuiStrs("nameNameInReport");

		String restNameInReport = getGuiStrs("restNameInReport");

		String	costNameInReportNoNDS =  getGuiStrs("sumWithoutNdsLabel");

		String	unitsNameInReport =  getGuiStrs("unitsNameInReport");
		
		String	sumPdvName =  getGuiStrs("sumPDvName");

		StringBuilder sHeader_b = new StringBuilder();
		
		
		
		sHeader_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>");
		
		sHeader_b.append(getGuiStrs("nmbStr"));
		
		sHeader_b.append( "</font></td>");
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( kodNameInReport );
		
		sHeader_b.append( "</font></td>");
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( nameNameInReport );
		
		sHeader_b.append( "</font></td>" );
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( restNameInReport );
		
		sHeader_b.append( "</font></td>" );
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( unitsNameInReport  );
		
		sHeader_b.append( "</font></td>" );
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( costNameInReportNoNDS  );
		
		sHeader_b.append( "</font></td>" );
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'><font size =4>" );
		
		sHeader_b.append( sumPdvName );
		
		sHeader_b.append( "</font></td>" );
		
		sHeader_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid; text-align: center;'><font size =4>" );
		
		sHeader_b.append( getGuiStrs("contractName"));
		
		sHeader_b.append( "</font></td></tr>");

		StringBuilder row_s_b = new StringBuilder();
		
		for(int i = start_index; i < end_index; ++i) {
			
			WsPrihodPartData d_row = vec.elementAt(i);
			
			if((i + 1) == end_index) {
				
				row_s_b.append( "<tr><td style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;'><font size =4>" );
				
				row_s_b.append( "&nbsp;"  );
				
				row_s_b.append( String.valueOf(i + 1)  );
				
				row_s_b.append(  "&nbsp;</font></td>" );
				
				row_s_b.append( "<td nowrap style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(" "  );
				
				row_s_b.append( d_row.vendorcode2  );
				
				row_s_b.append(   "&nbsp;</font></td>" );
				
				row_s_b.append("<td nowrap style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(" "  );
				
				row_s_b.append( d_row.name  );
				
				row_s_b.append(   "&nbsp;</font></td>" );
				
				row_s_b.append( "<td nowrap style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(" "  );
				
				row_s_b.append( WsUtils.getDF(d_row.rest)  );
				
				row_s_b.append(   "&nbsp;</font></td>" );
				
				row_s_b.append( "<td nowrap style='border-left: 1px solid;border-top: 1px solid ; border-bottom: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(" "  );
				
				row_s_b.append( d_row.units_name   );
				
				row_s_b.append(   "&nbsp;</font></td>" );
				
				row_s_b.append(  "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append( WsUtils.getDF(d_row.cost*d_row.rest) );
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append(  "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append( WsUtils.getDF(d_row.nds*d_row.rest) );
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append( "<td nowrap style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid; border-bottom: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(" "  );
				
				row_s_b.append( d_row.contract_name );
				
				row_s_b.append(   "&nbsp;</font></td></tr>" );
				
			}
			else {
				
				row_s_b.append(  "<tr><td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>&nbsp;" );
				
				row_s_b.append(   String.valueOf(i + 1) );
				
				row_s_b.append(   "&nbsp;</font></td>");
				
				row_s_b.append(   "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append(   d_row.vendorcode2 );
				
				row_s_b.append(    "&nbsp;</font></td>");
				
				row_s_b.append(   "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append(   d_row.name );
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append(   "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				row_s_b.append(   WsUtils.getDF(d_row.rest));
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append(  "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append(  d_row.units_name );
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append(  "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append( WsUtils.getDF(d_row.cost*d_row.rest) );
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append(  "<td nowrap style='border-left: 1px solid;border-top: 1px solid ;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append(  WsUtils.getDF(d_row.nds*d_row.rest) );
				
				row_s_b.append(     "&nbsp;</font></td>");
				
				row_s_b.append(   "<td nowrap style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid;'><font size =4> &nbsp;" );
				
				row_s_b.append(  " " );
				
				row_s_b.append(  d_row.contract_name  );
				
				row_s_b.append(    "&nbsp;</font></td></tr>");
					
			}
			
		}
		
		Vector<WsInfoData>  v_info = WsUtilSqlStatements.getInfoDataList();
		
		String firm_name = "-----";
		
		if(v_info.size() != 0) {
			
			firm_name = v_info.elementAt(0).name;
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
		
		hS_b.append( "<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("bookSkladStateReportName")); 
		
		hS_b.append( " "); 
		
		hS_b.append( firm_name); 
		
		hS_b.append( " ");
		
		hS_b.append("</font></h2>");
		
		hS_b.append( "<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(  sHeader_b.toString()  );
		
		hS_b.append( row_s_b.toString() );
		
		hS_b.append( "</table></body></html>");
		
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
	
	
	
	public void exportToExcelFile() {
		
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
	
			XSSFSheet sheet = (XSSFSheet) wb.createSheet();
	    
			int rowCount = 1;
	    
		    int columnCount = 8;
		    
		    ArrayList<String> columnNames = new ArrayList<String>();
		
		    columnNames.add("№");
		    
		    columnNames.add(getGuiStrs("ptypesKodColumName"));
		    
		    columnNames.add(getGuiStrs("nameColumnReportGoodName"));
		    
		    columnNames.add(getGuiStrs("prihodPartsColumnRestName"));
		    
		    columnNames.add(getGuiStrs("unitsNameReportColumn"));
		    
		    columnNames.add(getGuiStrs("sumWithoutNdsLabel"));
		    
		    columnNames.add(getGuiStrs("sumPDvName"));
		    
		    columnNames.add(getGuiStrs("contractName"));

		    XSSFCreationHelper creationHelper = wb.getCreationHelper();
		    
		    // Create
		    XSSFRow rowHeader = sheet.createRow(0);
		    
		    for (int j = 0; j < columnCount; j++) {
		        // create first row
		        XSSFCell cell = rowHeader.createCell(j);
		        
		        XSSFRichTextString richString = creationHelper
		                .createRichTextString(columnNames.get(j));
		
		
	           cell.setCellValue(richString);
		        
	
		    }

	    	for (WsPrihodPartData dt : m_data) {
	
	            XSSFRow row = sheet.createRow(rowCount);
	            
	            for (int j = 0; j < columnCount; j++) {
	            	
	                XSSFCell cell = row.createCell(j);
	
	                    switch (j) {
	                    
	                    	case 0: {
	                    		
	                    		cell.setCellValue(rowCount);
	                    		
	                    		break;
	                    	}
		                    case 1: {
		                    	
		                        cell.setCellValue(dt.kod);
		                        
		                        break;
		                    }
		                    case 2:{
		                    	
		                        cell.setCellValue(dt.name);
		                        
		                        break;
		                    }
		                    case 3:{
		                    	
		                        cell.setCellValue(dt.rest);
		                        
		                        break;
		                    }
		                    case 4:{
		                    	
		                        cell.setCellValue(dt.units_name);
		                        
		                        break;
		                    }
		                    case 5:{
		                    	
		                        cell.setCellValue(dt.cost*dt.rest);
		                        
		                        break;
		                    }
		                    case 6:{
		                    	
		                        cell.setCellValue(dt.nds*dt.rest);
		                        
		                        break;
		                    }
		                    case 7:{
		                    	
		                        cell.setCellValue(dt.contract_name);
		                        
		                        break;
		                    }
		                 
		                    default:
		                        cell.setCellValue("Missing");
		                        break;
	                    }
	                }
	                rowCount++;
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
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		 JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("saveExcelReportFailedMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
	}

}
	
	
	@SuppressWarnings("unused")
	private  Vector<WsPrihodPartData> combine(Vector<WsPartType> real_sklad, Vector<WsPrihodPartData> data) {
		
		HashMap<Integer, WsPrihodPartData> map = new HashMap<Integer, WsPrihodPartData>();
		
		for(WsPrihodPartData d : data) {
			
			
			WsPrihodPartData dt = map.get(d.kod);
			
			if(dt == null) {
				
				map.put(d.kod, d);
				
			}
			else {
				
				dt.rest += d.rest;
			}
			
		}
		
		for(WsPartType d : real_sklad) {
			
			WsPrihodPartData dt = map.get(d.kod);
			
			if(dt == null  ) {
				
				if(d.quantity > 0.0001) {
					
					dt = new WsPrihodPartData();
					
					dt.real_quantity = d.quantity;
					
					dt.kod = d.kod;
					
					dt.vendorcode2 = String.valueOf(d.kod);
					
					dt.name = d.name;
					
					map.put(d.kod, dt);
				}
				
			}
			else {
				
				dt.real_quantity += d.quantity;
			}
			
		}
		
		
		ArrayList<Integer> list = new ArrayList<Integer>(map.keySet()); 
		
		Collections.sort(list);
		
		Vector<WsPrihodPartData> data_r = new  Vector<WsPrihodPartData>();
		
	
		for(Integer kod: list) {
				
			WsPrihodPartData d = map.get(kod);
				 
			data_r.add(d);
				 	
		}
		
		return data_r;
		
	}
	
}

