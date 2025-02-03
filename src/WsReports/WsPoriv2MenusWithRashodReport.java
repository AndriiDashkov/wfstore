
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
import java.io.FileInputStream;
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
import WsControls.WsFileChooser;
import WsDataStruct.WsPartType;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsReportsSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsImport.WsImportExcelUtil;
import WsMain.WsCatalogKods;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 * 
 */
public class WsPoriv2MenusWithRashodReport  extends WSReportViewer {
	

	private static final long serialVersionUID = 1L;

	Vector<WsSkladMoveDataColumn> m_vec_all = null;
	
	JCheckBox m_check_box_all_kods = null;
	
	WsFileChooser[] m_fileChoosers = {new WsFileChooser(getGuiStrs("fileChooserNameMr1")),
			new WsFileChooser(getGuiStrs("fileChooserNameMr2"))
	
	};
	
	JPanel[] m_panels = {WsGuiTools.createHorizontalPanel(),
			WsGuiTools.createHorizontalPanel()
	};

	public WsPoriv2MenusWithRashodReport(JFrame f, String nameFrame) {
		super(f, nameFrame);

		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	 m_html_pages = generateReport();
            	 
            	 if( m_html_pages == null ||  m_html_pages.isEmpty()) { return; }
            	
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
		
		int index  = 0;
		
		for(WsFileChooser fc : m_fileChoosers){
			
			m_panels[index].add( fc); 
			
			
			m_panels[index++].add(Box.createHorizontalGlue());

		}
		
		for(JPanel p : m_panels){
			bottom_panel.add(p);
		}
		
		Dimension d =  m_fileChoosers[0].getMaximumSize();
		
		Dimension d1 =  m_fileChoosers[0].getMinimumSize();
		
		d.height = 50;
		
		d.width = 850;
		
		d1.width = 200;
		
		for(WsFileChooser fc : m_fileChoosers){
			
			fc.setMaximumSize(d);
			
			fc.setMinimumSize(d1);

		}
		
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
	
		StringBuilder sHeader_b = new StringBuilder();
		
		sHeader_b.append("<tr>");
		
		sHeader_b.append("<td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>" );
		
		sHeader_b.append(getGuiStrs("reportBookKodGoodColumn")); 
		
		sHeader_b.append("</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("nameNameInReport")); 
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("vibPorivNameReport")); 
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("vibRaskl1Name") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("vibRaskl2Name") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("diffRaskl1Name") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("diffRaskl2Name") );
		
		sHeader_b.append("&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" );
		
		sHeader_b.append(getGuiStrs("diffRAskl12Name") );
		
		sHeader_b.append("&nbsp;</font></td></tr>");

		StringBuilder row_s_b = new StringBuilder();
		
		for(int i = start; i <= end; ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			String bottomBorder = "";
			 
			if(i == end) {
				
				 bottomBorder = "border-bottom: 1px solid ;"; 
			}
			 
			row_s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; "); 
			
			row_s_b.append(bottomBorder ); 
			
			row_s_b.append("'><font size =4>&nbsp;" ); 
			
			row_s_b.append(String.valueOf(i + 1) ); 
			
			row_s_b.append("&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append(bottomBorder ); 
			
			row_s_b.append("'><font size =4> &nbsp;" ); 
			
			row_s_b.append(String.valueOf(d.kod) ); 
			
			row_s_b.append("&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' overflow-x: hidden; max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append(bottomBorder ); 
			
			row_s_b.append("'><font size =4>&nbsp;" ); 
			
			row_s_b.append(d.name ); 
			
			row_s_b.append("&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append(bottomBorder ); 
			
			row_s_b.append("'><font size =4>&nbsp;" ); 
			
			row_s_b.append(WsUtils.getDF(d.q_array[0].out_quantity) ); 
			
			row_s_b.append("&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append(bottomBorder ); 
			
			row_s_b.append( "'><font size =4>&nbsp;" ); 
			
			row_s_b.append( WsUtils.getDF(d.q_array[1].out_quantity) ); 
			
			row_s_b.append("&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append( bottomBorder ); 
			
			row_s_b.append("'><font size =4>&nbsp;" ); 
			
			row_s_b.append( WsUtils.getDF(d.q_array[2].out_quantity) ); 
			
			row_s_b.append( "&nbsp;</font></td>"); 
			
			row_s_b.append( "<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append( bottomBorder ); 
			
			row_s_b.append( "'><font size =4>&nbsp;" ); 
			
			row_s_b.append( WsUtils.getDF(d.q_array[0].out_quantity - d.q_array[1].out_quantity) ); 
			
			row_s_b.append( "&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " ); 
			
			row_s_b.append( bottomBorder ); 
			
			row_s_b.append("'><font size =4>&nbsp;" ); 
			
			row_s_b.append( WsUtils.getDF(d.q_array[0].out_quantity - d.q_array[2].out_quantity) ); 
			
			row_s_b.append( "&nbsp;</font></td>"); 
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; ");  
			
			row_s_b.append(bottomBorder); 
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.q_array[1].out_quantity - d.q_array[2].out_quantity) );
			
			row_s_b.append( "&nbsp;</font></td></tr>");
		
		}
		
		StringBuilder hS_b =  new StringBuilder();
				
		hS_b.append("<!DOCTYPE html><html> ");
		
		hS_b.append("<style>    body {\r\n");
		
		hS_b.append("        height: 297mm;\r\n");
		
		hS_b.append("        width: 210mm;\r\n");
		
		hS_b.append("        /* to centre page on screen*/\r\n");
		
		hS_b.append("        margin-left: auto;\r\n");
		
		hS_b.append("        margin-right: auto;\r\n");
		
		hS_b.append("    }");
	    
		hS_b.append("</style><body>");
	    
		hS_b.append("<h2 align='center' ><font size =5>");
	    
		hS_b.append(getGuiStrs("poriv2RasklMenuCompareReportName")); 
	    
		hS_b.append(" " );
	    
		hS_b.append(date_s); 
	    
		hS_b.append(" ");
	    
		hS_b.append(getGuiStrs("bookSkladPoReportName")); 
	    
		hS_b.append(" ");
	    
		hS_b.append(date_e  );
	    
		hS_b.append("</font></h2>");
	    
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
	    
		hS_b.append(sHeader_b.toString() );
	    
		hS_b.append(row_s_b.toString()  );
	    
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
	
	
	private HashMap<Integer, WsSkladMoveDataColumn> importExcel() {
		
		int kod_column = 0;
		
		int name_column = 1;
		
		int out_quantity_column = 6;
				
		int sheet_index = 10;
		
		HashMap<Integer, WsPartType> catalog = WsUtilSqlStatements.getPartTypesMap();
		
		HashMap<Integer, WsSkladMoveDataColumn> map = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		String excel_file_name = null;
		
		int map_index = 1;
		
	    for(WsFileChooser fc : m_fileChoosers){
				  
			try {
				
				excel_file_name = fc.getFullFilePath();
				
				if(excel_file_name == null || excel_file_name.isEmpty()) {   continue; }
						
				FileInputStream fStream = new FileInputStream(excel_file_name);
					    
				XSSFWorkbook wb = new XSSFWorkbook( fStream );
				
				fStream.close();
					    
			    XSSFSheet sheet = wb.getSheetAt(sheet_index);
					    
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
		        		
		        		int kod   = WsCatalogKods.getKodFromDatabaseCatalog(d.kod, catalog);
		        		
		        		d.q_array[map_index].out_quantity = WsImportExcelUtil.getDoubleCell(row, out_quantity_column);
		        		
		        		d.name = WsImportExcelUtil.getStringCell(row,  name_column);
		        		
		        		if((d.q_array[map_index].out_quantity) > 0.0001) {
		        		
			        		WsSkladMoveDataColumn d_f = map.get(kod);
			        		
			        		if(d_f == null) {
			        			
			        			map.put(kod, d);
			        		}
			        		else {
			        			

			        			d_f.q_array[map_index].out_quantity += d.q_array[map_index].out_quantity;

			        		}  
		        		}
			    }
					    
			    wb.close();
			    
			    ++map_index;
					    
			} catch(Exception ioe) {
							
				ioe.printStackTrace();
						    
			}
	    }
					
		return map;
		
	}
	
	
	public void exportToExcelFile(Vector<WsSkladMoveDataColumn> vec_all_parts) {
		
		String file_to_save = 	excelSaveFileChoose(this);
		
		if (null == file_to_save)  { return; }
	
		OutputStream out;
		
		try {
			out = new FileOutputStream(file_to_save);
			
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
	                
	                XSSFCell cell03 = row.createCell(cell_index++);
	                
	                cell03.setCellValue(dt.q_array[0].out_quantity);
	                
	                XSSFCell cell04 = row.createCell(cell_index++);
	                
	                cell04.setCellValue(dt.q_array[1].out_quantity);
	                
	                XSSFCell cell05 = row.createCell(cell_index++);
	                
	                cell05.setCellValue(dt.q_array[2].out_quantity);
	                
	                XSSFCell cell06 = row.createCell(cell_index++);
	                
	                cell06.setCellValue(dt.q_array[0].out_quantity - dt.q_array[1].out_quantity);
	                
	                XSSFCell cell07 = row.createCell(cell_index++);
	                
	                cell07.setCellValue(dt.q_array[0].out_quantity - dt.q_array[2].out_quantity);
	                
	                XSSFCell cell08 = row.createCell(cell_index++);
	                
	                cell08.setCellValue(dt.q_array[1].out_quantity - dt.q_array[2].out_quantity);

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
		   
		   createCell(rowHeader0, 0, getGuiStrs("menuRaskl2Poriv0"), creationHelper);
	
		   createCell(rowHeader0, 1, getGuiStrs("menuRaskl2Poriv1"), creationHelper);
		   
		   createCell(rowHeader0, 2, getGuiStrs("menuRaskl2Poriv2"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("menuRaskl2Poriv3"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("menuRaskl2Poriv4"), creationHelper);
		   
		   createCell(rowHeader0, 5,getGuiStrs("menuRaskl2Poriv5"), creationHelper);
		   
		   createCell(rowHeader0, 6,getGuiStrs("menuRaskl2Poriv6"), creationHelper);
		   
		   createCell(rowHeader0, 7, getGuiStrs("menuRaskl2Poriv7"), creationHelper);
		  	
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
		
	}

}
