
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsFileTableControl2;
import WsDataStruct.WsPair;
import WsDataStruct.WsPartType;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsReportsSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsImport.WSExcelImport;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 * 
 */
public class WsPorivSkladWithFutureRashod2 extends WSReportViewer {
	
	private static final long serialVersionUID = 1L;
	
	Vector<WsSkladMoveDataColumn> m_vec_all = null;
	
	JCheckBox m_check_box_all_kods = null;
	
	JPanel[] m_panels = {WsGuiTools.createHorizontalPanel()};
	
	WsFileTableControl2 m_control_table = null;
	
	int m_people_sum = 0;
	 

	public WsPorivSkladWithFutureRashod2(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		String[] columnNames = { getGuiStrs("importRaskladkaFileName"), 
				getGuiStrs("importQuantityRasklakaName")};
		
		m_control_table = new WsFileTableControl2(columnNames, getGuiStrs("chooserRaskladkaFileLabelName"), true);

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

	private void createGui() {
		
		JPanel bottom_panel = WsGuiTools.createVerticalPanel();
		
		m_panels[0].add( m_control_table ); 
			
		m_panels[0].add(Box.createHorizontalGlue());

		for(JPanel p : m_panels){
			
			bottom_panel.add(p);
		}
		
		WsUtils.get().setFixedSizeBehavior(m_control_table);
		
		WsGuiTools.setComponentFixedHeight(m_control_table, 120);
		
		m_control_panel2.add(bottom_panel);
		
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
			
		m_check_box_all_kods = new JCheckBox(getGuiStrs("allKodsExistedUseCaptionCheckBox"));
			
		JPanel bottom_panel1 = WsGuiTools.createHorizontalPanel();
		
		bottom_panel1.add(m_check_box_all_kods);
		
		bottom_panel1.add(Box.createHorizontalGlue());
		
		 m_control_panel2.add(bottom_panel1);
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		m_control_table.finishEditing();
		
		Vector<WsSkladMoveDataColumn> vec_all = WsReportsSqlStatements.getPrihodRashodBookForDate2(m_date.getSqlStartDate(), 
				m_date.getSqlEndDate(), -1);
		
		HashMap<Integer, WsSkladMoveDataColumn> foreign_data = importExcel();
		
		//merge
		for(WsSkladMoveDataColumn d : vec_all) {
			
			WsSkladMoveDataColumn df = foreign_data.get(d.kod);
			
			if(df == null) {
				
				foreign_data.put(d.kod, d);
			}
			else {
				
				df.q_array[0].out_quantity += d.q_array[0].out_quantity;
				
				df.q_array[0].in_quantity += d.q_array[0].in_quantity;
				
				df.q_array[0].initial_rest += d.q_array[0].initial_rest;

				
			}
		}
		
		if (m_check_box_all_kods.isSelected()) {
			
			Vector<Integer> list_kods = WsUtilSqlStatements.getKodsList();
			
			for(Integer kod : list_kods) {
				
				WsSkladMoveDataColumn df = foreign_data.get(kod);
				
				if(df == null) {
					
					df = new WsSkladMoveDataColumn();
					
					df.kod = kod;
					
					WsPartType pt = WsUtilSqlStatements.getPartTypeForKod(kod);
					
					if(pt != null) {
						
						df.name = pt.name;
					}
					else {
						
						df.name = "????????";
					}
					
					foreign_data.put(kod, df);
				}

			}
		}
	
		Vector<WsPartType> vec_real = WsUtilSqlStatements.getPartTypesList();
		
		for(WsPartType f : vec_real) {
			
			int kod = f.kod;
			
			WsSkladMoveDataColumn df = foreign_data.get(kod);
			
			if(df == null) {
				
				if(f.quantity > 0.0001) {
					
					df = new WsSkladMoveDataColumn();
					
					df.kod = kod;
					
					df.q_array[3].out_quantity = f.quantity;
					
					df.name = f.name;
				
					foreign_data.put(kod, df);
				}
			}
			else {
				
				df.q_array[3].out_quantity += f.quantity;
			}
			
			
		}

		ArrayList<Integer> list = new ArrayList<Integer>(foreign_data.keySet()); 
		
		Collections.sort(list);
		
		vec_all.clear();
		
		if (m_check_box_all_kods.isSelected()) {
			
			for(Integer kod: list) {
				
				 WsSkladMoveDataColumn d = foreign_data.get(kod);
				 
				 vec_all.add(d);
				 	
			}
		}
		else {
		
			for(Integer kod: list) {
				
				 WsSkladMoveDataColumn d = foreign_data.get(kod);
				 
				 if(( d.q_array[0].out_quantity + 
						 d.q_array[1].out_quantity + d.q_array[2].out_quantity) > 0.00001 ) {
				 
					 vec_all.add(d);
				 }
				
			}
		}
		
		int rows_number = vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		Vector<String> vec_pages = new Vector<String>();
		
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
		
		m_vec_all = vec_all;
		
		current_font_size = 4;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
			
	}
		
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
	
		StringBuilder sHeader_b =  new StringBuilder();
				
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>" );
		
		sHeader_b.append(getGuiStrs("reportBookKodGoodColumn") );
		
		sHeader_b.append("</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("nameNameInReport") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("zalishokPochatokReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("pribuloReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
			
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("quantityNameVibuloReportColumn") );
		
		sHeader_b.append("&nbsp;</font></td>");
				
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("zalishokEndReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
				
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("planRashodReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
				
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("diffplanZalishokReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
				
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("correctioPossibleReportName2") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("correctioPossibleReportName") );
		
		sHeader_b.append("&nbsp;</font></td>");
				
		sHeader_b.append("<td   style='border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("planRashodReportName4") );
		
		sHeader_b.append("&nbsp;</font></td></tr>");

		StringBuilder  row_s_b = new StringBuilder();
		
		for(int i = start; i <= end; ++i) {
			
			 WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			 String bottomBorder = "";
			 
			 if(i == end) {
				 
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 
			 double sklad_rest = d.q_array[0].in_quantity + d.q_array[0].initial_rest - d.q_array[0].out_quantity;
			 
			 double plan = d.q_array[1].out_quantity + d.q_array[2].out_quantity;
			 
			 row_s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			 
			 row_s_b.append(bottomBorder + "'><font size =4>&nbsp;" );
			 
			 row_s_b.append(String.valueOf(i + 1) );
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4> &nbsp;"); 
			 
			 row_s_b.append(String.valueOf(d.kod)); 
			 
			 row_s_b.append("&nbsp;</font></td>");
		     
			 row_s_b.append("<td nowrap style=' overflow-x: hidden; max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(d.name); 
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			 
			 row_s_b.append(bottomBorder); 
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].initial_rest) );
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append(bottomBorder); 
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].in_quantity) );
			 
			 row_s_b.append("&nbsp;</font></td>");
			
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 		
			 
			 row_s_b.append(bottomBorder); 
		     
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(WsUtils.getDF(d.q_array[0].out_quantity) );
			 
			 row_s_b.append("&nbsp;</font></td>");
			 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(WsUtils.getDF(sklad_rest)); 
			 
			 row_s_b.append("&nbsp;</font></td>");
	 
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 	    
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(WsUtils.getDF(plan) );
			 
			 row_s_b.append("&nbsp;</font></td>");
				
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			 
			 row_s_b.append(bottomBorder); 
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(WsUtils.getDF(sklad_rest - plan) );
			 
			 row_s_b.append("&nbsp;</font></td>");
			
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(WsUtils.getDF((sklad_rest - plan)/m_people_sum) );
																							 
			 row_s_b.append("&nbsp;</font></td>");	
			
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append(bottomBorder );
			 
			 row_s_b.append("'><font size =4>&nbsp;" );
			 
			 row_s_b.append(WsUtils.getDF((sklad_rest - plan)*1000/m_people_sum) );
			 
			 row_s_b.append("&nbsp;</font></td>");	
									
			 row_s_b.append("<td nowrap style=' max-width: 250px; border-right: 1px solid; border-left: 1px solid; border-top: 1px solid ;  "); 
			 
			 row_s_b.append(bottomBorder); 
			 
			 row_s_b.append("'><font size =4>&nbsp;"); 
			 
			 row_s_b.append(" "); 
			 
			 row_s_b.append("&nbsp;</font></td></tr>");
		}
		
		
		StringBuilder hS_b = new StringBuilder();
		
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append("        height: 297mm;\r\n");
		
		hS_b.append("        width: 210mm;\r\n");
		
		hS_b.append("        /* to centre page on screen*/\r\n");
		
		hS_b.append("        margin-left: auto;\r\n");
		
		hS_b.append("        margin-right: auto;\r\n");
		
		hS_b.append("    }");
		
		hS_b.append("</style><body>");
		
		hS_b.append("<h2 align='center' ><font size =5>"+ getGuiStrs("porivSkaldFutureRashodCompareReportName") + " " + date_s + " ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName") + " ");
		
		hS_b.append(date_e  +"</font></h2>");
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append(sHeader_b.toString() );
		
		hS_b.append(row_s_b.toString() ); 
		
		hS_b.append("</table></body></html>");// + sFooter;

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
		
		if(m_html_pages == null || m_html_pages.isEmpty()) { return false; }
		
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
	
	
	@SuppressWarnings("unchecked")
	private HashMap<Integer, WsSkladMoveDataColumn> importExcel() {
		
		WsPair p = WSExcelImport.getDataFromRaskladkaSet(m_control_table.getData(), true, false);
	
		m_people_sum = (int)p.value;
		
		return (HashMap<Integer, WsSkladMoveDataColumn>)p.complex;
			
	}
	
	
	public void exportToExcelFile(Vector<WsSkladMoveDataColumn> vec_all_parts) {
		
		String file_to_save = 	excelSaveFileChoose(this);
		
		if (null == file_to_save)  { return; }
	
		OutputStream out;
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		try {
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
		        	
	                WsUtils.get();
						                
	                XSSFCell cell02 = row.createCell(cell_index++);
	                
	                cell02.setCellValue(dt.name);
	                	    
	                double sklad_rest = dt.q_array[0].in_quantity + dt.q_array[0].initial_rest - dt.q_array[0].out_quantity;
				 
	                double plan = dt.q_array[1].out_quantity + dt.q_array[2].out_quantity;
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(dt.q_array[0].initial_rest);
	                
	                XSSFCell cell04 = row.createCell(cell_index++);
	                
	                cell04.setCellValue(dt.q_array[0].in_quantity);
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                cell06.setCellValue(dt.q_array[0].out_quantity);
	                
	                XSSFCell cell05 = row.createCell(cell_index++);
	                
	                cell05.setCellValue(sklad_rest);
	                
	                XSSFCell cell07 = row.createCell(cell_index++);
	                
	                cell07.setCellValue(plan);
	                
	                XSSFCell cell08 = row.createCell(cell_index++);
	                
	                cell08.setCellValue(sklad_rest - plan);
	                	                
	                XSSFCell cell09 = row.createCell(cell_index++);
	          
	                cell09.setCellValue((sklad_rest - plan)/m_people_sum);
	                	                
	                XSSFCell cell10 = row.createCell(cell_index++);
	  	          
	                cell10.setCellValue((sklad_rest - plan)*1000/m_people_sum);
	                

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
		   
		   createCell(rowHeader0, 0, getGuiStrs("ptypesKodColumName"), creationHelper);
	
		   createCell(rowHeader0, 1, getGuiStrs("reportBookNameGoodColumn"), creationHelper);
		   
		   createCell(rowHeader0, 2, getGuiStrs("zalishokPochatokReportName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("pribuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("zalishokEndReportName"), creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("planRashodReportName"), creationHelper);
		   
		   createCell(rowHeader0, 7, getGuiStrs("diffplanZalishokReportName"), creationHelper);
		   
		   createCell(rowHeader0, 8, getGuiStrs("correctioPossibleReportName2"), creationHelper);
		   
		   createCell(rowHeader0, 9, getGuiStrs("correctioPossibleReportName"), creationHelper);
		   
		   
		  	
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
      cell3.setCellValue(richString3);
		
	}	
}
