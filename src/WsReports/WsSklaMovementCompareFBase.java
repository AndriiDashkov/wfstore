
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsFileChooser;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WSConnect;
import WsDatabase.WsReportsSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSklaMovementCompareFBase  extends WSReportViewer {


	Vector< WsSkladMoveDataColumn>  m_vec_all = null;
	
	JCheckBox m_checkAllKodes = null;
	
	protected  WsFileChooser m_text = null;
	
	JSpinner m_spinner = null;
	
	private Font m_font = WsUtils.get().getBaseFont();
	 
	/**
	 * @param f
	 * @param nameFrame
	 */
	public WsSklaMovementCompareFBase(JFrame f, String nameFrame) {
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
	            	
	            		exportToExcelFile(m_vec_all);
	            	}
	             
	            }
		 });
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();
		
		JPanel panel_spinner = WsGuiTools.createHorizontalPanel();
		
		SpinnerModel model1 = new SpinnerNumberModel(1, 1, 20000, 1);  
		
		m_spinner = new  JSpinner(model1);
		
		JLabel label_spinner = new JLabel(getGuiStrs("kolPeopelReportCompareMov") + " ");
		
		label_spinner.setFont(m_font);
		
		panel_spinner.add(label_spinner);
		
		panel_spinner.add(m_spinner);
		
		panel_spinner.add(Box.createHorizontalGlue());
		
		m_text =  new WsFileChooser(getGuiStrs("labelfDataBAseChosserLabel"));
		
		m_checkAllKodes = new JCheckBox(getGuiStrs("useAllKodsForReports"));
		
		m_checkAllKodes.setFont(m_font);
		
		JPanel m_control_panel21 = WsGuiTools.createHorizontalPanel();
		
		m_control_panel21.add(m_text);
		
		m_control_panel21.add(m_checkAllKodes);
		
		m_control_panel21.add(Box.createHorizontalGlue());
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(m_control_panel21);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_spinner);
		
		m_control_panel2.add(panel_main);
		
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		WsUtils.get().setFixedSizeBehavior( m_text);
		
		Dimension d = m_spinner.getMaximumSize();
		
		d.width = 50;
		
		m_spinner.setMaximumSize(d);
		
		WsUtils.get().setFixedSizeBehavior(m_spinner);
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<String> vec_pages = new Vector<String>();
		
		Vector< WsSkladMoveDataColumn>  vec_all = getData( );
		
		if(vec_all == null) { return vec_pages; }
		
		int rows_number = vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
		if(pages_number == 1) {
			
			end_row = vec_all.size() - 1;
		}
		
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
	
	
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader_b = new StringBuilder(); 
				
		sHeader_b.append( "<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>" );
		
		sHeader_b.append( getGuiStrs("reportBookKodGoodColumn") + "</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("nameColumnReportGoodName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("naPochatReportName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("naPochatReportName1") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("pribuloReportName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("pribuloReportName1") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("quantityNameVibuloReportColumn") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("quantityNameVibuloReportColumn1") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("prihodPartsColumnRestName") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("prihodPartsColumnRestName1") );
		
		sHeader_b.append( "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append( "<font size =4>&nbsp;" );
		
		sHeader_b.append( getGuiStrs("restDiff2BasesName") );
		
		sHeader_b.append( "&nbsp;</font></td></tr>");
			
		StringBuilder s_b = new StringBuilder(); 
		
		for(int i = start; i <= end; ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			String bottomBorder = "";
			 
			if(i == end) {
				
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 	
			s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			
			s_b.append(bottomBorder); 
			
			s_b.append( "'><font size =4>&nbsp;"); 
			
			s_b.append( String.valueOf(i + 1) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4> &nbsp;" );
			
			s_b.append( String.valueOf(d.kod) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( d.name );
			
			s_b.append( "&nbsp;</font></td>");
					
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append(WsUtils.getDF(d.q_array[0].initial_rest) );
			
			s_b.append( "&nbsp;</font></td>");
				
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF(d.q_array[1].initial_rest) );
			
			s_b.append( "&nbsp;</font></td>"	);
				
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder);
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF(d.q_array[0].in_quantity) );
			
			s_b.append( "&nbsp;</font></td>");
			
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF(d.q_array[1].in_quantity) );
			
			s_b.append( "&nbsp;</font></td>");
			 
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder + "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF(d.q_array[0].out_quantity));
			
			s_b.append( "&nbsp;</font></td>");
				
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF(d.q_array[1].out_quantity) );
			
			s_b.append( "&nbsp;</font></td>"	);
				
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; " );
		   
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append(WsUtils.getDF(d.q_array[0].rest) );
			
			s_b.append( "&nbsp;</font></td>");
				
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; " );
		    
			s_b.append( bottomBorder );
			
			s_b.append( "'><font size =4>&nbsp;" );
			
			s_b.append(WsUtils.getDF(d.q_array[1].rest) );
			
			s_b.append("&nbsp;</font></td>");
				
			s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; " );
			
			s_b.append( bottomBorder );
			
			s_b.append("'><font size =4>&nbsp;" );
			
			s_b.append( WsUtils.getDF((d.q_array[1].rest - d.q_array[0].rest)*1000/( (int)m_spinner.getValue())) );
			
			s_b.append( "&nbsp;</font></td></tr>"	);
		}
		
		
		StringBuilder hS_b =  new StringBuilder();
					
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append( "<style>    body {\r\n");
		
		hS_b.append( "        height: 297mm;\r\n");
		
		hS_b.append("        width: 210mm;\r\n");
		
		hS_b.append( "        /* to centre page on screen*/\r\n");
		
		hS_b.append( "        margin-left: auto;\r\n");
		
		hS_b.append( "        margin-right: auto;\r\n");
		
		hS_b.append( "    }");
		
		hS_b.append( "</style><body>");
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("bookSkladMovementReportName2") );
		
		hS_b.append( " " );
		
		hS_b.append( date_s );
		
		hS_b.append( " ");
		
		hS_b.append( getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append( " ");
		
		hS_b.append( date_e  );
		
		hS_b.append("</font></h2>");
		
		hS_b.append( "<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append( sHeader_b.toString() );
		
		hS_b.append( s_b.toString()  );
		
		hS_b.append( "</table></body></html>");

		return hS_b.toString();
		 
	}
	
	class ItemChangeListener implements ItemListener{


		/* (non-Javadoc)
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
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

	public void exportToExcelFile(Vector< WsSkladMoveDataColumn>   vec_all_parts) {
		
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
		
	    	for (WsSkladMoveDataColumn dt: vec_all_parts) {
	
		            XSSFRow row = sheet.createRow(row_index++);
		            
		            int cell_index = 0;
		            
		            XSSFCell cell00 = row.createCell(cell_index++);
		            
		            cell00.setCellValue(row_index - 1);
		            
		            XSSFCell cell01 = row.createCell(cell_index++);
		            
		            cell01.setCellValue(dt.kod);
		        	
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellValue(dt.name);
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(WsUtils. getDF(dt.initial_rest));
	                
	                XSSFCell cell04 = row.createCell(cell_index++);
	                
	                cell04.setCellValue(WsUtils. getDF(dt.in_quantity));
	                
	                XSSFCell cell05 = row.createCell(cell_index++);
	                
	                cell05.setCellValue(WsUtils. getDF(dt.out_quantity));
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                cell06.setCellValue(WsUtils. getDF(dt.rest));
	                
	
	         }
	
	   
			wb.write(out);
	
			out.close();
	    
			wb.close(); 
    
		} catch (IOException  e) {
		
			e.printStackTrace();
		}

	}
	
	
	private void createExcelHeader( XSSFSheet sheet, XSSFCreationHelper creationHelper) {
		
		   XSSFRow rowHeader0 = sheet.createRow(0);
		   
		   createCell(rowHeader0, 0, "", creationHelper);
		   
		   createCell(rowHeader0, 1, getGuiStrs("reportBookKodGoodColumn"), creationHelper);
	
		   createCell(rowHeader0, 2, getGuiStrs("nameColumnReportGoodName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("naPochatReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("pribuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("prihodPartsColumnRestName") , creationHelper);
		   
		  	
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
		
	}
	
	
	
	private Vector< WsSkladMoveDataColumn>  getData( ) {
		
		
	HashMap<Integer, WsSkladMoveDataColumn> data = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		Vector<WsSkladMoveDataColumn> vec_all =
				WsReportsSqlStatements.getPrihodRashodBookForDate2(m_date.getSqlStartDate(),
						m_date.getSqlEndDate(), -1);
		
		//fill all kods from the catalog
		if(m_checkAllKodes.isSelected()) {
			
			vec_all = WSReportUtil.fillVectorWithAllKods( vec_all) ;
			
		}
		else {
			
			Vector<WsSkladMoveDataColumn> vec_all_1 = new Vector<WsSkladMoveDataColumn>();
			
			for(int i = 0; i < vec_all.size(); ++i) {
				
				WsSkladMoveDataColumn d = vec_all.elementAt(i);
				
				if(d.in_quantity > 0.0001 || d.out_quantity > 0.0001 || d.rest > 0.0001
						|| d.initial_rest > 0.0001) {
					
					vec_all_1.add(d);
					
				}
			}
			
			vec_all.clear();
			
			vec_all = vec_all_1;

		}
		
		
		
		for(WsSkladMoveDataColumn d : vec_all) {
			
			data.put(d.kod, d);
			
		}
		
		Connection conn = WSConnect.connectImport(m_text.getFullFilePath());
		
		if(null == conn) { return null; }
		
		Vector<WsSkladMoveDataColumn> vec_all_f =
				WsReportsSqlStatements.getPrihodRashodBookForDate2(conn, m_date.getSqlStartDate(),
						m_date.getSqlEndDate(), -1);
		
		
		if(!m_checkAllKodes.isSelected()) {
			

			Vector<WsSkladMoveDataColumn> vec_all_1 = new Vector<WsSkladMoveDataColumn>();
			
			for(int i = 0; i < vec_all_f.size(); ++i) {
				
				WsSkladMoveDataColumn d = vec_all_f.elementAt(i);
				
				if(d.in_quantity > 0.0001 || d.out_quantity > 0.0001 || d.rest > 0.0001
						|| d.initial_rest > 0.0001) {
					
					vec_all_1.add(d);
					
				}
			}
			
			vec_all_f.clear();
			
			vec_all_f = vec_all_1;

		}
		
		for(WsSkladMoveDataColumn d : vec_all_f) {
			
			int kod =  d.kod;
			
			if(data.containsKey(kod)) {
				
				WsSkladMoveDataColumn d1 = data.get(kod);
				
				d1.q_array[1].in_quantity = d.q_array[0].in_quantity;
				
				d1.q_array[1].out_quantity = d.q_array[0].out_quantity;
				
				d1.q_array[1].initial_rest = d.q_array[0].initial_rest;
				
				d1.q_array[1].rest = d.q_array[0].rest;
				
			}
			else {
				
				WsSkladMoveDataColumn d1 = new WsSkladMoveDataColumn();
				
				d1.q_array[1].in_quantity = d.q_array[0].in_quantity;
				
				d1.q_array[1].out_quantity = d.q_array[0].out_quantity;
				
				d1.q_array[1].initial_rest = d.q_array[0].initial_rest;
				
				d1.q_array[1].rest = d.q_array[0].rest;
				
				d1.kod = d.kod;
				
				d1.name = d.name;
				
				d1.units = d.units;
				
				data.put(d.kod, d1);
				
			}
		}
		
		Vector< WsSkladMoveDataColumn>  vec = new Vector< WsSkladMoveDataColumn>();
		
		ArrayList<Integer> list = new ArrayList<Integer>(data.keySet());
		
		list.sort(null);
		
		for(Integer kod: list) {
			
			vec.add(data.get(kod));
		}
	
		return vec;
		
	}

}
