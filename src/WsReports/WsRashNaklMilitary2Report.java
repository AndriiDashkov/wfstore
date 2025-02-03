
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsCompanyComboBox;
import WsControls.WsSignsComboBox;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsSignsData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashNaklMilitary2Report extends WSReportViewer {

	private static final long serialVersionUID = 1L;
	
	WsRashodData m_data = null;
	
	WsSignsComboBox m_combo_nps = new WsSignsComboBox();
	
	WsSignsComboBox m_combo_ns = new WsSignsComboBox();
	
	private WsCompanyComboBox m_combo = new WsCompanyComboBox(true);
	
	Vector<WsRashodPartData>  m_vec_parts = null;
	
	JLabel m_name_label = new JLabel(getGuiStrs("companyComboNameNewDialogLabel"));
	
	public WsRashNaklMilitary2Report(JFrame f, String nameFrame, WsRashodData data) {
		super(f, nameFrame);
		
		m_data = data;
		
		createGui();
		
		if(m_combo_ns.listSize() > 1) {
			
			m_combo_ns.setSelectedIndex(1);
		}
		
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
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
        		m_html_pages = new Vector<String>();
        		
        		m_html_pages.add(getPrintHtml());
        		
        		setText(m_html_pages.elementAt(0));
        		
        		pagesNum = m_html_pages.size();
            	
            	currentPage = 1;
            	
            	setDialogCaption();
            	
            	m_viewer.setSelectionStart(0);
            	
            	m_viewer.setSelectionEnd(0);
             
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

		JPanel panel2_ = WsGuiTools.createHorizontalPanel();
		 
		panel2_.add(new JLabel(getGuiStrs("npsMil2ReportLabel")));
		
		panel2_.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel2_.add(m_combo_nps);
		
		panel2_.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel2_.add( new JLabel(getGuiStrs("nsMil2ReportLabel")));
		
		panel2_.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel2_.add(m_combo_ns);
		 
		m_control_panel2.add(panel2_);
		
		WsUtils.get().setFixedSizeBehavior(m_combo_nps);
		
		WsUtils.get().setFixedSizeBehavior(m_combo_ns);
		
		WsGuiTools.fixComponentHeightToMin(m_control_panel2);
		
	}
	
	private WsInfoData getInfo() {
	
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
		 
		 return d;
	 
	}
	
	public String getPrintHtml() {
		
		WsInfoData d = getInfo();

		Vector<WsSignsData> info2_vec = WsUtils.getInfoPidp( m_combo_nps, m_combo_ns );
	
		 m_vec_parts = WsUtils.mergeSameCodes(WsRashodSqlStatements.getRashodPartsVector(m_data.id));
		
		StringBuilder rows_b = new StringBuilder();
		
		for(int i = 0; i <  m_vec_parts.size(); ++i) {
			
			WsRashodPartData d1 =  m_vec_parts.elementAt(i);
			
			rows_b.append("<tr><td  style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(String.valueOf(i + 1) );
			
			rows_b.append("</font></td>");

			rows_b.append( "<td colspan ='3' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>"); 
			
			rows_b.append(d1.name );
			
			rows_b.append("</td>");
			
			rows_b.append("<td align='center' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(String.valueOf(d1.kod) );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td colspan ='2' align='center'  style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(d1.units_name );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td align='center'  style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td align='center'colspan ='2' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(d1.quantity) );
			
			rows_b.append( "</font></td>");
			
			rows_b.append( "<td align='center' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(d1.quantity) );
			
			rows_b.append( "</font></td>");
			
			rows_b.append( "<td align='center'  style='border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append("</font></td>");
			
			rows_b.append("</tr>");
			  
		}
		
		String date_s = WsUtils.dateToString( m_data.date, "dd.MM.yyyy" );

		StringBuilder hS_b = new StringBuilder(); 
				
		hS_b.append("<html> ");
		
		hS_b.append( "<style>");
		
		hS_b.append( "</style><body>");
		
		hS_b.append( "<table style='width:100%;' cellspacing='0' cellpadding='1'  >");
		
		hS_b.append( "<tr><td align='center' colspan ='12' ><font size =5>");
		
		hS_b.append(getGuiStrs("naklMilt12"));
		
		hS_b.append( " " );
		
		hS_b.append( getGuiStrs("nmbStr") );
		
		hS_b.append(" " );
		
		hS_b.append( m_data.number );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr ><td align='center' colspan ='12' nowrap><font size =5>" );
		
		hS_b.append( getGuiStrs("naklMilt2") );
		
		hS_b.append( " " );
		
		hS_b.append( d.name );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr></tr>" );
		
		hS_b.append( "<tr>" );
		
		hS_b.append( "<td align='center' colspan ='2' style=' border-left: 1px solid; border-top: 1px solid ;' nowrap><font size =4>" + getGuiStrs("regNomerNaklM2") +"</font></td>" );
		
		hS_b.append( "<td align='center'  style=' border-left: 1px solid; border-top: 1px solid ;' nowrap><font size =4>" + getGuiStrs("nomerArkush") +"</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;'nowrap><font size =4>" + getGuiStrs("nomerDocM2") + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;'nowrap><font size =4>" + getGuiStrs("dataDocM2") + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;' nowrap><font size =4>" + getGuiStrs("pidMetaM2") + "</font></td>" );
		
		hS_b.append( "</tr>");
		
		hS_b.append( "<tr>" );
		
		hS_b.append( "<td align='center' colspan ='2' style=' border-left: 1px solid; border-top: 1px solid; border-bottom: 1px solid;' nowrap><font size =4>" + m_data.number  + "</font></td>" );
		
		hS_b.append( "<td align='center' style=' border-left: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4></font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4>" + m_data.number  + "</font></td>" );
		
		hS_b.append( "<td align='center'colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4>" + date_s + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4>" + getGuiStrs("potZabez") + "</font></td>" );
		
		hS_b.append( "</tr>");
		
		hS_b.append( " <tr></tr>");
		
		hS_b.append( "<tr>" );
 
		hS_b.append( "<td align='center' colspan ='2' style=' border-left: 1px solid; border-top: 1px solid ;' nowrap><font size =4>"+getGuiStrs("dataOpM2")+"</font></td>" );
		
		hS_b.append( "<td align='center'  style=' border-left: 1px solid; border-top: 1px solid ;' nowrap><font size =4>" + getGuiStrs("slZabezM2") + "</font></td>" );
		
		hS_b.append( "<td align='center'  colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;'nowrap><font size =4>" + getGuiStrs("vanVidM2") + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3'  style=' border-left: 1px solid; border-top: 1px solid ;'nowrap><font size =4>" + getGuiStrs("vanOdM2") + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3'  style=' border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;' nowrap><font size =4>" + getGuiStrs("vidOdM2") + "</font></td>" );
		
		hS_b.append( "</tr>");
		
		hS_b.append( "<tr>" );
		
		hS_b.append( "<td align='center' colspan ='2' style=' border-left: 1px solid; border-top: 1px solid; border-bottom: 1px solid;' nowrap><font size =4>" + date_s  + "</font></td>" );
		
		hS_b.append( "<td align='center' style=' border-left: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4>" + getGuiStrs("prodM2") + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4>" + d.name  + "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4>" + m_data.agentName+ "</font></td>" );
		
		hS_b.append( "<td align='center' colspan ='3' style=' border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;  border-bottom: 1px solid;' nowrap><font size =4></font></td>" );
		
		hS_b.append( "</tr>");
		
		hS_b.append( " <tr></tr></table>");
		
		hS_b.append( "<table style='width:100%;' cellspacing='0' cellpadding='1' ><tr> "
				+ "<td  align='center' style=' width: 15px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>");
		
		hS_b.append(" " );
		
		hS_b.append( getGuiStrs("nmbStr") );
		
		hS_b.append( " " );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( " <td  align='center' colspan ='3' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("vskMaino") + "<br />" + getGuiStrs("indexKresl") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td  align='center' style='  width: 20px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("kodNomenkl") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td  align='center' colspan ='2' style='  width: 20px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append(getGuiStrs("odVumir") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append("<td  align='center'style='border-left: 1px solid;  border-top: 1px solid ;'><font size =4>");
		
		hS_b.append(getGuiStrs("catSort") );
		
		hS_b.append("</font></td>");
		
		hS_b.append( "<td colspan ='2' style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>");
		

		hS_b.append(getGuiStrs("vudatu"));
		
		hS_b.append("</font></td>"	);
		
		hS_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>");
		
		hS_b.append(getGuiStrs("vidpush"));
		
		hS_b.append("</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;' align='center'><font size =4>");
		
		hS_b.append(getGuiStrs("prOdM2"));
		
		hS_b.append("</font></td> </tr>"	);
		
		hS_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>1</font></td>"	);
		
		hS_b.append("<td colspan='3' style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>2</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>3</font></td>"	);
		
		hS_b.append("<td colspan='2' style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>4</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>5</font></td>"	);
		
		hS_b.append("<td colspan='2' style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>6</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>7</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;' align='center'><font size =4>8</font></td></tr>"	);
		
		hS_b.append( rows_b.toString());
		
		hS_b.append("<tr><td  colspan = '4' style='border-left: 1px solid; border-top: 1px solid; border-bottom: 1px solid;' align='center'><font size =4>"
				+   getGuiStrs("vsgo") +"</font></td>"	);
		
		hS_b.append("<td  colspan = '8' style='border-left: 1px solid; border-right: 1px solid; border-top: 1px solid; border-bottom: 1px solid;' align='center'><font size =4>" );
		
		hS_b.append(m_vec_parts.size() );
		
		hS_b.append(" "); 
		
		hS_b.append(WsUtils.getNaymenuvannya(m_vec_parts.size()) );
		
		hS_b.append( "</font></td>"	);
		
		hS_b.append( "<tr style='border:hidden;'><td  colspan='3'><font size =4>");
		
		hS_b.append(info2_vec.elementAt(0).position) ;
		
		hS_b.append(getGuiStrs("vscchastM2") );
		
		hS_b.append( d.name );
		
		hS_b.append("</font></td><td colspan ='9'></td></tr>");
		
		hS_b.append( "<tr style='border:hidden;'><td  colspan='2'><font size =4>");
		
		hS_b.append( info2_vec.elementAt(0).rank);
		
		hS_b.append("</font></td>");
		
		hS_b.append( "<td align='right' colspan='10'><font size =4>");
		
		hS_b.append( info2_vec.elementAt(0).name);
		
		hS_b.append("</font></td></tr>");
		
		hS_b.append( "<tr style='border:hidden;'><td  colspan='3'><font size =4>");
		
		hS_b.append(getGuiStrs("vudav") + " : " );
		
		hS_b.append( info2_vec.elementAt(1).rank );
		
		hS_b.append("</font></td>");
		
		hS_b.append( "<td style='border-bottom: 1px solid;' colspan='5'><font size =4></font></td>");
		
		hS_b.append( "<td align='right' colspan='4' ><font size =4>" );
		
		hS_b.append( info2_vec.elementAt(1).name); 
		
		hS_b.append("</font></td></tr>");
		
		hS_b.append( "<tr align='right' style='border:hidden;'><td  colspan='5'><font size =4>" + getGuiStrs("pidps") + "</td>");

		hS_b.append( "<td  colspan='7'><font size =4></font></td> </tr>");
		
		hS_b.append( "<tr style='border:hidden;'><td  colspan='3'> <font size =4>Отримав (прийняв)</font></td>");
		
		hS_b.append( "<td style='border-bottom: 1px solid;' colspan='5'><font size =4></font></td><td colspan ='4'></td>");
		
		hS_b.append( "<tr align='right' style='border:hidden;'><td  colspan='5'><font size =4>(підпис)</td>");
		
		hS_b.append( "<td  colspan='7'><font size =4></td> </tr>");
		
		hS_b.append( "<tr style='border:hidden;'><td  colspan='4'><font size =4>\"___\"_________20___ року</td>");
		
		hS_b.append( "<td  colspan='4'><font size =4></td> </tr>");
		
		hS_b.append( "</table></body></html>");
		  
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
		
		WsInfoData d = getInfo();

		Vector<WsSignsData> info2_vec =  WsUtils.getInfoPidp(m_combo_nps, m_combo_ns );
		
		OutputStream out;
		
		try {
			
			out = new FileOutputStream(file_to_save);
	
			XSSFWorkbook wb = new XSSFWorkbook();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    XSSFCreationHelper creationHelper = wb.getCreationHelper();
		    
		    int rowCount = createExcelHeader( wb, sheet, creationHelper);
		    
			XSSFCellStyle st00 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st003 = getExcelCellStyle(wb, 1, 1, 1, 0, 
					   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st002 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st001 = getExcelCellStyle(wb, 0, 1, 0, 0, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
			
			XSSFCellStyle st0011 = getExcelCellStyle(wb, 0, 1, 0, 1, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
			
			for(int i = 0; i <  m_vec_parts.size(); ++i) {
				
				WsRashodPartData dt =  m_vec_parts.elementAt(i);
	
	            XSSFRow row = sheet.createRow(++rowCount);
	                     	
            	XSSFCell cell = row.createCell(0);
            	
                cell.setCellValue(i + 1); 
                
                cell.setCellStyle(st00);

            	cell = row.createCell(1);
            	
                cell.setCellValue(dt.name);
                
                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 1, 3));
                
                cell.setCellStyle(st002);
                
    			cell = row.createCell(2); 
   		     
    			cell.setCellStyle(st00);
    			
    			cell = row.createCell(3); 
      		     
    			cell.setCellStyle(st00);

            	cell = row.createCell(4);
            	
                cell.setCellValue(dt.kod); 
                
                cell.setCellStyle(st00);

            	cell = row.createCell(5);
            	
            	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 5, 6));
            	
                cell.setCellValue(dt.units_name);
                
                cell.setCellStyle(st00);
                
    			cell = row.createCell(6); 
      		     
    			cell.setCellStyle(st00);
                
            	cell = row.createCell(7);
            	
            	cell.setCellStyle(st00);
            	
            	cell = row.createCell(8);
            	
            	cell.setCellStyle(st00);

                sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 8, 9));
            	
            	cell.setCellValue(dt.quantity);
            	
    			cell = row.createCell(9); 
      		     
    			cell.setCellStyle(st00);

            	cell = row.createCell(10);
            	
                cell.setCellValue(dt.quantity);
                
                cell.setCellStyle(st00);

            	cell = row.createCell(11);
            	
                cell.setCellValue("");
                
                cell.setCellStyle(st00);

	         }
	    	
	
	    	 
	    	XSSFRow  row1 = sheet.createRow(++rowCount);
	    	
	    	XSSFCell cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 4));
	    	
	    	cell0.setCellValue( getGuiStrs("vsgo"));
	    	
	    	cell0.setCellStyle(st003);
	    	
	    	for(int k = 1; k < 5; ++k) {
	    		
	    		cell0 = row1.createCell(k);
	    	
	    		cell0.setCellStyle(st001);
	    	}
	    	
	    	cell0 = row1.createCell(5);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 5, 11));
	    	
	    	cell0.setCellValue( m_vec_parts.size() + " " + WsUtils.getNaymenuvannya(m_vec_parts.size()));
	    	
	    	cell0.setCellStyle(st003);
	    	
	    	for(int k = 6; k < 11; ++k) {
	    		
	    		cell0 = row1.createCell(k);
	    	
	    		cell0.setCellStyle(st001);
	    	}
	    	
	    	cell0 = row1.createCell(11);
	    	
    		cell0.setCellStyle(st0011);
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
	    	
	    	cell0.setCellValue( info2_vec.elementAt(0).position + " " + getGuiStrs("vscchastM2") + " " + d.name);
	    	
	    	
	      	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 1));
	    	
	    	cell0.setCellValue( info2_vec.elementAt(0).rank);
	    	
	    	cell0 = row1.createCell(2);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 2, 5));
	    	
	    	cell0 = row1.createCell(6);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 6, 11));
	    	
	    	cell0.setCellValue( info2_vec.elementAt(0).name);
		
	    	row1 = sheet.createRow(++rowCount);
			
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
	    	
	    	cell0.setCellValue(getGuiStrs("vudav") + " : " + info2_vec.elementAt(1).rank);

	    	cell0 = row1.createCell(4);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 4, 5));
	    	
			cell0 = row1.createCell(4); 
		     
			cell0.setCellStyle(st001);
			
			cell0 = row1.createCell(5); 
		     
			cell0.setCellStyle(st001);
			
	    	cell0 = row1.createCell(6);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 6, 11));
	    	
	    	cell0.setCellValue( info2_vec.elementAt(1).name);

	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
	    	
	    	cell0 = row1.createCell(4);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 4, 11));
	    	
	    	cell0.setCellValue( getGuiStrs("pidps"));
	    	
	    	cell0 = row1.createCell(6);
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 4));
	    	
	    	cell0.setCellValue( getGuiStrs("otrprinyav"));
	    	
			cell0 = row1.createCell(4); 
		     
			cell0.setCellStyle(st001);
			
			cell0 = row1.createCell(5); 
		     
			cell0.setCellStyle(st001);
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
	    	
	    	cell0 = row1.createCell(4);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 4, 11));
	    	
	    	cell0.setCellValue( getGuiStrs("pidps"));
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 3));
	    	
	    	cell0.setCellValue("\"___\"_________20___ "  + getGuiStrs("roku"));
	    
			sheet.autoSizeColumn(1);

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
	
	
	private int createExcelHeader( XSSFWorkbook wb, XSSFSheet sheet,  XSSFCreationHelper creationHelper) {
		
		   String date_s = WsUtils.dateToString( m_data.date, "dd.MM.yyyy" );
		
		   WsInfoData d = getInfo();
		   
		   XSSFCellStyle st0 = getExcelCellStyle(wb, 0, 0, 0, 0, 
				   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
			XSSFCellStyle  st1 = getExcelCellStyle(wb, 1, 1, 
					   1, 1, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
		   
			
			int rows_count  = 0;
		   
		    XSSFRow rowHeader = sheet.createRow(rows_count);
			   
		    String s  = getGuiStrs("naklMilt12") + " " + getGuiStrs("nmbStr") + " " + m_data.number;
		    
		    XSSFCell cl0 = createCell(rowHeader, 0, s, creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,11));
			   
			cl0.setCellStyle(st0);
			
			rowHeader = sheet.createRow(++rows_count);
			   
		    s  = getGuiStrs("naklMilt2") + " " + d.name;
		    
		    XSSFCell cl1 = createCell(rowHeader, 0, s, creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,11));
			   
			cl1.setCellStyle(st0);
			
			rowHeader = sheet.createRow(++rows_count);

			rowHeader = sheet.createRow(++rows_count);
			   
		    s  = getGuiStrs("regNomerNaklM2");
		    
		    cl1 = createCell(rowHeader, 0, s, creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,1));
			   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 1, "", creationHelper); 
			     
			cl1.setCellStyle(st1);
			
			s  = getGuiStrs("nomerArkush");
			
			cl1 = createCell(rowHeader, 2, s, creationHelper); 
			     
			cl1.setCellStyle(st1);
			
			s  = getGuiStrs("nomerDocM2");
			    
			cl1 = createCell(rowHeader, 3, s, creationHelper); 
				   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,3,5));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 4, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 5, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			s  = getGuiStrs("dataDocM2");
		    
			cl1 = createCell(rowHeader, 6, s, creationHelper); 
				   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,6,8));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 7, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 8, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			s  = getGuiStrs("pidMetaM2");
		    
			cl1 = createCell(rowHeader, 9, s, creationHelper); 
				   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,9,11));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 10, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 11, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
		
			rowHeader = sheet.createRow(++rows_count);

		    cl1 = createCell(rowHeader, 0,"" , creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,1));
			   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 1, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
		    cl1 = createCell(rowHeader, 2, "" , creationHelper); 
			   
			cl1.setCellStyle(st1);

			cl1 = createCell(rowHeader, 3, m_data.number , creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,3,5));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 4, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 5, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
  
			cl1 = createCell(rowHeader, 6, date_s, creationHelper); 
				   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,6,8));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 7, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 8, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
		    
			cl1 = createCell(rowHeader, 9, getGuiStrs("potZabez"), creationHelper); 
				   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,9,11));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 10, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 11, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			rowHeader = sheet.createRow(++rows_count);

			rowHeader = sheet.createRow(++rows_count);
			
			cl1 = createCell(rowHeader, 0, 	getGuiStrs("dataOpM2"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,1));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 1, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 2, 	getGuiStrs("slZabezM2"), creationHelper); 
			      
			cl1.setCellStyle(st1);
		
			cl1 = createCell(rowHeader, 3, 	getGuiStrs("vanVidM2"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,3,5));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 4, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 5, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 6, 	getGuiStrs("vanOdM2"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,6,8));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 7, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 8, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 9, 	getGuiStrs("vidOdM2"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,9,11));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 10, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 11, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			rowHeader = sheet.createRow(++rows_count);
			
			cl1 = createCell(rowHeader, 0, 	date_s, creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,0,1));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 1, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 2, 	getGuiStrs("prodM2"), creationHelper); 
			   	   
			cl1.setCellStyle(st1);

			cl1 = createCell(rowHeader, 3, 	d.name, creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,3,5));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 4, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 5, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 6, 	m_data.agentName, creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,6,8));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 7, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 8, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 9, 	"", creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,9,11));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 10, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 11, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
					
			rowHeader = sheet.createRow(++rows_count);

			rowHeader = sheet.createRow(++rows_count);
			
			cl1 = createCell(rowHeader, 0, getGuiStrs("nmbStr"), creationHelper); 
			      
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 1, getGuiStrs("vskMaino") + " " + getGuiStrs("indexKresl"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,1,3));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 2, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 3, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 4, getGuiStrs("kodNomenkl"), creationHelper); 
			      
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 5, getGuiStrs("odVumir"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,5,6));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 6, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 7, getGuiStrs("catSort"), creationHelper); 
	   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 8, getGuiStrs("vudatu"), creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,8,9));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 9, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 10, getGuiStrs("vidpush"), creationHelper); 
		   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 11, 	getGuiStrs("prOdM2"), creationHelper); 
			   
			cl1.setCellStyle(st1);
			
			rowHeader = sheet.createRow(++rows_count);
			
			cl1 = createCell(rowHeader, 0, "1", creationHelper); 
			   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 1, "2", creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,1,3));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 2, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 3, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 4, "3", creationHelper); 
			   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 5, "4", creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,5,6));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 6, "", creationHelper); 
		     
			cl1.setCellStyle(st1);

			cl1 = createCell(rowHeader, 7, "5", creationHelper); 
			   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 8, "6", creationHelper); 
			   
			sheet.addMergedRegion(new CellRangeAddress(rows_count, rows_count,8,9));
				   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 9, "", creationHelper); 
		     
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 10, "7", creationHelper); 
			   
			cl1.setCellStyle(st1);
			
			cl1 = createCell(rowHeader, 11, "8", creationHelper); 
			   
			cl1.setCellStyle(st1);

			return rows_count;
	}
	
	private XSSFCell createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
	      
	      return cell3;
		
	}
	
	protected void closeAllEventConnections() {
		
		WsEventDispatcher.get().disconnect(m_combo_nps); 
		
		WsEventDispatcher.get().disconnect(m_combo_ns); 
		
		WsEventDispatcher.get().disconnect(m_combo);
		
	}
}