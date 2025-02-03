
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsCompanyComboBox;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashNaklReport extends WSReportViewer {
	

	private static final long serialVersionUID = 1L;
	
	WsRashodData m_data = null;
	
	private WsCompanyComboBox m_combo = new WsCompanyComboBox(true);
	
	JLabel m_name_label = new JLabel(getGuiStrs("companyComboNameNewDialogLabel"));
	
	public WsRashNaklReport(JFrame f, String nameFrame, WsRashodData data) {
		super(f, nameFrame);
		
		m_data = data;
		
		createGui();
		
		m_html_pages = new Vector<String>();
		
		m_html_pages.add(getPrintHtml());
		
		setText(m_html_pages.elementAt(0));
		
		pagesNum = m_html_pages.size();
    	
    	currentPage = 1;
    	
    	setDialogCaption();
    	
    	m_viewer.setSelectionStart(0);
    	
    	m_viewer.setSelectionEnd(0);
		
		m_combo.addItemListener(new ItemChangeListener());
		
		m_saveExcelButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	if( !isReportEmpty() ) {
            	
            		exportToExcelFile();
            	
            	}
             
            }
	 });
		
	}

	private void createGui() {
		
		m_control_panel.add(m_name_label);
		
		m_control_panel.add(m_combo);
		
		m_control_panel.add(Box.createHorizontalGlue());
		
		WsUtils.get().setFixedSizeBehavior(m_combo);
		
		WsGuiTools.setComponentFixedWidth( m_combo, 150);
		
		m_date.setVisible(false);
		 
		m_genButton.setVisible(false);
		
	}
	
	public String getPrintHtml() {
		
		int id = m_combo.getCurrentSQLId();
		
		 Vector<WsInfoData> v_info = WsUtilSqlStatements.getInfoDataList();
		 
		 WsInfoData d = null; 
		 
		 for(int j = 0; j < v_info.size(); ++j) {
			 
			 d = v_info.elementAt(j);
			 
			 if(d.id == id) { break; }
		 }
		 
		 if (d == null) {
			 
			 d = new  WsInfoData();
		 }
		
		Vector<WsRashodPartData> vec_parts = WsRashodSqlStatements.getRashodPartsVector(m_data.id);
		
		double sum_cost = 0.0;
		
		double sum_nds = 0.0;
		
		StringBuilder rows_b = new StringBuilder();
		
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsRashodPartData d1 =vec_parts.elementAt(i);
			
			rows_b.append("<tr><td style='border-left: 1px solid; width: 15px; border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(String.valueOf(i + 1) );
			
			rows_b.append("</font></td>");
			
			rows_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append( d1.name );
			
			rows_b.append( "</td>");
			
			rows_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(d1.quantity) );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(d1.units_name );
			
			rows_b.append( "</td>");
		    
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" ); 
			
			rows_b.append(WsUtils.getDF_0(d1.cost) );
			
			rows_b.append( "</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF_0(d1.nds) );
			
			rows_b.append( "</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF_0(d1.cost * d1.quantity) );
			
			rows_b.append( "</font></td>");
			
			rows_b.append("</tr>");
			  
			  sum_cost += d1.cost*d1.quantity;
			  
			  sum_nds += d1.nds*d1.quantity;

		}
		
		String date_s = WsUtils.dateToString(m_data.date, "dd-MMMM-yyyy" );
		
		StringBuilder hS_b = new StringBuilder();
		
		hS_b.append("<html><style></style><body>");
		
		hS_b.append( "<h2 align='center' ><font size =5>"); 
		
		hS_b.append(getGuiStrs("vidNaklName1") );
		
		hS_b.append(" " );
		
		hS_b.append(getGuiStrs("nmbStr") );
		
		hS_b.append(" " );
		
		hS_b.append(m_data.number );
		
		hS_b.append(" " );
		
		hS_b.append(getGuiStrs("vidNaklName2") );
		
		hS_b.append("  ");
		
		hS_b.append(date_s );
		
		hS_b.append("</font></h2>");
		
		hS_b.append( "<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hS_b.append( "<tr ><td><font size =5>" );
		
		hS_b.append(getGuiStrs("postachDatabaseName") );
		
		hS_b.append(" :</font></td><td><font size =4>");
		
		hS_b.append( d.name );
		
		hS_b.append("</font></td></tr>");
		
		hS_b.append( "<tr><td><font size =5>" );
		
		hS_b.append(getGuiStrs("raschetSchetNaklName") );
		
		hS_b.append("</font></td><td><font size =4>" );
		
		hS_b.append(d.rahunok );
		
		hS_b.append("</font></td></tr>");
		
		hS_b.append( "<tr><td ><font size =5>" );
		
		hS_b.append(getGuiStrs("infoMFOColumName") );
		
		hS_b.append(" :</font></td><td><font size =4>" );
		
		hS_b.append( d.MFO );
		
		hS_b.append("</font></td></tr>");
		
		hS_b.append( "<tr><td><font size =5>" );
		
		hS_b.append( getGuiStrs("infoAdressColumName") );
		
		hS_b.append( " :</font></td><td><font size =4>" );
		
		hS_b.append( d.adress );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr ><td><font size =5>" );
		
		hS_b.append( getGuiStrs("pokupecNameNakl") );
		
		hS_b.append( " </font></td><td><font size =4>" );
		
		hS_b.append( m_data.agentName );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr><td><font size =5>" );
		
		hS_b.append( getGuiStrs("pidstavaNameNakl") );
		
		hS_b.append( "</font></td><td><font size =4>");
		
		hS_b.append( d.comments );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "</table><table style='width:100%;' cellspacing='0' cellpadding='1'  >");
		
		hS_b.append(" <tr>");
		
		hS_b.append(" <td align='center' style='border-left: 1px solid; width: 15px; border-top:  1px solid ;'><font size =4>");
		
		hS_b.append( getGuiStrs("nmbStr") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( " <td align='center' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("nameNameInReport") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append(" <td align='center' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("quantityReturnColumName") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td align='center'style='border-left: 1px solid;  width: 20px; border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("unitsNameInReport"));
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td align='center'style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("costNameInReportNoNDS") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td align='center'style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("prihodPartsColumnNdsName") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td align='center' style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid; '><font size =4>" );
		
		hS_b.append( getGuiStrs("sumWithoutPDvName"));
		
		hS_b.append("</font></td>");
		
		hS_b.append( "</tr>" );
		
		hS_b.append( rows_b.toString());
		
		hS_b.append(" <tr style='border:hidden;'>");
		
		hS_b.append( "<td style='border-top: 1px solid ;' colspan='2'><font size =4>" );
		
		hS_b.append( getGuiStrs("vsegoNamesNaklRsh") );
		
		hS_b.append(" " );
		
		hS_b.append( String.valueOf(vec_parts.size()));
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append("<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append("<td style='border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append(getGuiStrs("vsegoNaklName") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append(  WsUtils.getDF_0(sum_cost) );
		
		hS_b.append(" " + d.money + "</font></td>");
		
		hS_b.append("</tr><tr ><td  colspan='2'></td><td ></td>");
		
		hS_b.append("<td></td><td ></td><td ><font size =4>");
		
		hS_b.append(getGuiStrs("sumPDvName") + " : ");
		
		hS_b.append("</font></td><td><font size =4>"); 
	    
		hS_b.append(  WsUtils.getDF_0(sum_nds) );
		
		hS_b.append( " " + d.money + "</font></td></tr></tr><tr>");
		
		hS_b.append(  "<td  colspan='2'></td><td ></td><td ></td><td ></td><td ><font size =4>"); 
		
		hS_b.append( getGuiStrs("vsegoSPdvNaklName") );
		
		hS_b.append( "</font></td><td ><font size =4>");
		
		hS_b.append( WsUtils.getDF_0(sum_cost + sum_nds)); 
		
		hS_b.append(" " + d.money + "</font></td></tr>");
		
		hS_b.append("<tr><td  colspan='2'></td><td ></td><td ></td><td ></td><td ></td><td ></td>");
		
		hS_b.append( "</tr><tr><td  colspan='2'><font size =4>" );
		
		hS_b.append( getGuiStrs("vidPostachName"));
		
		hS_b.append( " _________</font></td>");
		
		hS_b.append( "<td ></td><td ></td><td ></td><td ><font size =4>"); 
		
		hS_b.append( getGuiStrs("otrimavNameNakl") );
		
		hS_b.append("________</font></td>");
		
		hS_b.append( "<td ></td></tr></table></body></html>");
		  
		return hS_b.toString();
		 
	}
	
	class ItemChangeListener implements ItemListener{


		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange() == ItemEvent.SELECTED) {
		         
				setText(getPrintHtml());
				
		    	m_viewer.setSelectionStart(0);
		    	
		    	m_viewer.setSelectionEnd(0);
					
		    }
		}       
	}
	
	
	public void exportToExcelFile() {
		
		String file_to_save = 	excelSaveFileChoose(this);
		
		if (null == file_to_save)  { return; }
	
		OutputStream out;
		
		try {
			
			out = new FileOutputStream(file_to_save);
	
			XSSFWorkbook wb = new XSSFWorkbook();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    int rowCount = 1;
		    
		    int columnCount = 8;
		    
		    ArrayList<String> columnNames = new ArrayList<String>();
		
		    columnNames.add("№");
		    
		    columnNames.add(getGuiStrs("kodNameInReport"));
		    
		    columnNames.add(getGuiStrs("nameNameInReport"));
		    
		    columnNames.add(getGuiStrs("quantityReturnColumName"));
		    
		    columnNames.add(getGuiStrs("unitsNameInReport"));
		    
		    columnNames.add(getGuiStrs("costNameInReportNoNDS"));
		    
		    columnNames.add(getGuiStrs("prihodPartsColumnNdsName"));
		    
		    columnNames.add(getGuiStrs("sumWithoutPDvName"));
		
		    XSSFCreationHelper creationHelper = wb.getCreationHelper();
		    
		    XSSFRow rowHeader = sheet.createRow(0);
		    
		    for (int j = 0; j < columnCount; j++) {
		  
		        XSSFCell cell = rowHeader.createCell(j);
		        
		        XSSFRichTextString richString = creationHelper
		                .createRichTextString(columnNames.get(j));
		
	           cell.setCellValue(richString);
		        
	
		    }
		    
			Vector<WsRashodPartData> vec_parts = WsRashodSqlStatements.getRashodPartsVector(m_data.id);

	    	for (WsRashodPartData dt : vec_parts) {
	
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
		                    case 2: {
		                    	
		                        cell.setCellValue(dt.name);
		                        
		                        break;
		                    }
		                    case 3: {
		                    	
		                        cell.setCellValue(dt.quantity);
		                        
		                        break;
		                    }
		                    case 4: {
		                    	
		                        cell.setCellValue(dt.units_name);
		                        
		                        break;
		                    }
		                    case 5: {
		                    	
		                        cell.setCellValue(dt.cost);
		                        
		                        break;
		                    }
		                    case 6: {
		                    	
		                        cell.setCellValue(dt.nds);
		                        
		                        break;
		                    }
		                    case 7: {
		                    	
		                        cell.setCellValue(dt.cost * dt.quantity);
		                        
		                        break;
		                    }
		                    default: {
		                    	
		                        cell.setCellValue("Missing");
		                        
		                        break;
		                    }
	                    }
	                }
	            
	                rowCount++;
	          }

   
			wb.write(out);
	
			out.close();
	    
			wb.close(); 
    
		} catch (IOException  e) {
			
			e.printStackTrace();
		}

	}

}
