
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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
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
import WsControls.Ws2DatesControl;
import WsControls.WsCompanyComboBox;
import WsControls.WsSignsComboBox;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsSignsData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashNaklMilitaryReport extends WSReportViewer {

	WsRashodData m_data = null;
	
	private WsCompanyComboBox m_combo = new WsCompanyComboBox(true);
	
	JLabel m_name_label = new JLabel(getGuiStrs("companyComboNameNewDialogLabel"));
	
	Ws2DatesControl  m_date_2 = null;
	 
	JSpinner m_spinSnid = null;
	
	JSpinner m_spinObid = null;
	
	JSpinner m_spinVech = null;
	
	WsSignsComboBox m_combo_nps = new WsSignsComboBox();
	
	WsSignsComboBox m_combo_ns = new WsSignsComboBox();
	
	public WsRashNaklMilitaryReport(JFrame f, String nameFrame, WsRashodData data) {
		super(f, nameFrame);
		
		m_data = data;
		
		createGui();
		
		if(m_combo_ns.listSize() > 1) {
			
			m_combo_ns.setSelectedIndex(1);
		}
		
		m_date_2.setSqlStartDate( WsUtils.sqlDatePlusDays(m_data.date, -6));
		 
		m_date_2.setSqlEndDate(m_data.date);
		
		m_spinSnid.setValue(m_data.people);
		
		m_spinObid.setValue(m_data.people);
		
		m_spinVech.setValue(m_data.people);
		
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		m_control_panel.add(m_name_label);
		
		m_control_panel.add(m_combo);
		
		m_control_panel.add(Box.createHorizontalGlue());
		
		WsUtils.get().setFixedSizeBehavior(m_combo);
		
		WsGuiTools.setComponentFixedWidth( m_combo, 150);
		
		m_date.setVisible(false);
		 
		m_date_2 = new Ws2DatesControl(false);
		 
		JPanel panel2 = WsGuiTools.createVerticalPanel();
		 
		JPanel panel2_ = WsGuiTools.createHorizontalPanel();
		 
		JPanel panel2_0 = WsGuiTools.createHorizontalPanel();
		 
		TitledBorder title;
			
		title = BorderFactory.createTitledBorder(getGuiStrs("peopleNaklMilRportLabel"));
			
		panel2_0.setBorder(title);
		 
		panel2_.add(m_date_2);   panel2_.add(panel2_0);
		 
		panel2.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		 
		panel2.add(panel2_);
		 
		panel2.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		 
		JLabel  label1 = new JLabel(getGuiStrs("snidanokMilRportLabel"));
		 
		JLabel  label2 = new JLabel(getGuiStrs("obidMilRportLabel"));
		 
		JLabel  label3 = new JLabel(getGuiStrs("vecherMilRportLabel"));
		 
		SpinnerModel model1 = new SpinnerNumberModel(0, 0, 20000, 1);  
			
		m_spinSnid = new  JSpinner(model1);
		 
		SpinnerModel model2 = new SpinnerNumberModel(0, 0, 20000, 1);  
			
		m_spinObid = new  JSpinner(model2);
		 
		SpinnerModel model3 = new SpinnerNumberModel(0, 0, 20000, 1);  
			
		m_spinVech = new  JSpinner(model3);
		 
		panel2_0.add(label1);
		 
		panel2_0.add(m_spinSnid);
		 
		panel2_0.add(label2);
		 
		panel2_0.add(m_spinObid);
		 
		panel2_0.add(label3);
		 
	    panel2_0.add(m_spinVech);
		 
		panel2_0.add(Box.createHorizontalGlue());
		
		JPanel panel2_1 = WsGuiTools.createHorizontalPanel();
		 
		panel2_1.add(new JLabel(getGuiStrs("npsMil2ReportLabel")));
		
		panel2_1.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel2_1.add(m_combo_nps);
		
		panel2_1.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel2_1.add( new JLabel(getGuiStrs("nsMil2ReportLabel")));
		
		panel2_1.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel2_1.add(m_combo_ns);
		
		panel2.add(panel2_1);

		WsUtils.get().setFixedSizeBehavior(m_spinSnid);
		
		WsUtils.get().setFixedSizeBehavior(m_spinObid);
		
		WsUtils.get().setFixedSizeBehavior(m_spinVech);
		
		WsUtils.get().setFixedSizeBehavior(panel2);
		 
		m_control_panel2.add(panel2);
		
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
		
		Vector<WsSignsData> info2_vec =  WsUtils.getInfoPidp(m_combo_nps, m_combo_ns );
		
		Vector<WsRashodPartData>  vec_parts = WsUtils.mergeSameCodes(WsRashodSqlStatements.getRashodPartsVector(m_data.id));
		
		StringBuilder rows_b = new StringBuilder();
		
		double sum =  (int)m_spinSnid.getValue() + (int)m_spinObid.getValue() + (int)m_spinVech.getValue();
		
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsRashodPartData d1 =vec_parts.elementAt(i);
			
			double v1 = d1.quantity/(sum)*((int)m_spinSnid.getValue());
			
			double v2 = d1.quantity/(sum)*((int)m_spinObid.getValue());
			
			double v3 = d1.quantity - v1 - v2;
			
			rows_b.append("<tr><td  style=' width: 20px; border-left: 1px solid; border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(String.valueOf(i + 1) );
			
			rows_b.append("</font></td>");
			
			rows_b.append("<td  style=' width: 20px; border-left: 1px solid; border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(String.valueOf(d1.kod) );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>"); 
			
			rows_b.append(d1.name );
			
			rows_b.append("</td>");
			
			rows_b.append( "<td align='center'  style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(d1.units_name );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(v1) );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(v2) );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(v3) );
			
			rows_b.append("</font></td>");
			
			rows_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
			
			rows_b.append(WsUtils.getDF(d1.quantity) );
			
			rows_b.append( "</font></td>");
			
			rows_b.append( "<td style='border-right: 1px solid; border-left: 1px solid;border-top: 1px solid ;'><font size =4></font></td>");	
			
			rows_b.append("</tr>");
			  
		}
		
		String date_s = WsUtils.dateToString( m_date_2.getSqlStartDate(), "dd.MM.yyyy" );
		
		String date_e = WsUtils.dateToString( m_date_2.getSqlEndDate(), "dd.MM.yyyy" );
		
		StringBuilder hS_b = new StringBuilder(); 
				
		hS_b.append("<html> ");
		
		hS_b.append( "<style>");
		
		hS_b.append( "</style><body>");
		
		hS_b.append( "<table style='width:100%;' cellspacing='0' cellpadding='1'  >");
		
		hS_b.append( "<tr><td align='center' colspan ='8' ><font size =5>");
		
		hS_b.append(getGuiStrs("naklMilt12"));
		
		hS_b.append( " " );
		
		hS_b.append( getGuiStrs("nmbStr") );
		
		hS_b.append(" " );
		
		hS_b.append( m_data.number );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr ><td align='center' colspan ='8' nowrap><font size =5>" );
		
		hS_b.append( getGuiStrs("naklMilt1") );
		
		hS_b.append( " " );
		
		hS_b.append( m_data.agentName );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr><td align='center' colspan ='8' nowrap><font size =4>" );
		
		hS_b.append( getGuiStrs("naklMilt2") );
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( " <tr></tr>");
		
		hS_b.append( "<tr><td align='center' colspan ='8'><font size =4>" );
		
		hS_b.append( getGuiStrs("cDateBeginLabel") );
		
		hS_b.append( " ");
		
		hS_b.append( date_s );
		
		hS_b.append( " " );
		
		hS_b.append( getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append( " " );
		
		hS_b.append(date_e );
		
		hS_b.append( " " );
		
		hS_b.append( getGuiStrs("roku") );
		
		hS_b.append( " </font></td></tr>");
		
		hS_b.append( " <tr></tr>");
		
		hS_b.append( "<tr> <td rowspan='3' align='center' style=' width: 15px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>");
		
		hS_b.append(" " );
		
		hS_b.append( getGuiStrs("nmbStr") );
		
		hS_b.append( " " );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td rowspan='3' align='center' style=' width: 20px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("nameKodInReport") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( " <td rowspan='3' align='center' style=' width: 20px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("nameNameInReport") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append( "<td rowspan='3' align='center' style='  width: 20px; border-left: 1px solid;border-top: 1px solid ;'><font size =4>" );
		
		hS_b.append( getGuiStrs("unitsNameInReport") );
		
		hS_b.append( "</font></td>");
		
		hS_b.append("<td colspan='4' align='center' style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt3"));
		
		hS_b.append("</font></td>");
		
		hS_b.append("<td rowspan='3' align='center'style='border-left: 1px solid; border-right: 1px solid; border-top: 1px solid ;'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt4") );
		
		hS_b.append("</font></td></tr>");
		
		hS_b.append( "<tr><td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt5"));
		
		hS_b.append("</font></td>"	);
		
		hS_b.append( "<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt6"));
		
		hS_b.append("</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt7"));
		
		hS_b.append("</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4></font></td></tr>"	);
		
		hS_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>" );
		
		hS_b.append( String.valueOf(((int)m_spinSnid.getValue())));
		
		hS_b.append( "</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>" );
		
		hS_b.append( String.valueOf(((int)m_spinObid.getValue())));
		
		hS_b.append( "</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>" );
		
		hS_b.append( String.valueOf(((int)m_spinVech.getValue())));
		
		hS_b.append( "</font></td>"	);
		
		hS_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;' align='center'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt8"));
		
		hS_b.append("</font></td></tr>"	);
		
		hS_b.append( rows_b.toString());
		
		hS_b.append(" <tr style='border:hidden;'>");
		
		hS_b.append( "<td style='border-top: 1px solid ;' colspan='2'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt4") );
		
		hS_b.append(":</font></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "<td style='border-top: 1px solid ;'></td>");
		
		hS_b.append( "</tr></table><table>");
		
		hS_b.append("<tr ><td colspan='5' ><font size =4>" );
		
		hS_b.append(getGuiStrs("naklMilt9") );
		
		hS_b.append(" " );
		
		hS_b.append(d.name);
		
		hS_b.append("</font></td><td ></td>");
		
		hS_b.append("<td></td><td ></td><td ><font size =4></font></td><td><font size =4></font></td></tr>");
		
		hS_b.append("<tr><td colspan='4'><font size =4>"); 
		
		hS_b.append(info2_vec.elementAt(0).rank );
		
		hS_b.append("</font></td><td colspan='4'><font size =4> " + info2_vec.elementAt(0).name + "</font></td></tr>");
		
		hS_b.append( "<tr><td colspan='2'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt10"));
		
		hS_b.append(": " +  info2_vec.elementAt(1).rank  + " _______________________" + info2_vec.elementAt(1).name + "</font></td>");
		
		hS_b.append( "<td ></td><td ></td><td colspan='3'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt11"));
		
		hS_b.append(": ________</font></td>");
		
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
		
		double sum =  (int)m_spinSnid.getValue() + (int)m_spinObid.getValue() + (int)m_spinVech.getValue();
		
		if(sum == 0.0) { sum = 1.0; } //just to prevent a divison by zero
		
		Vector<WsSignsData> info2_vec =  WsUtils.getInfoPidp(m_combo_nps, m_combo_ns );
		
		
		OutputStream out;
		
		try {
			
			out = new FileOutputStream(file_to_save);
	
			XSSFWorkbook wb = new XSSFWorkbook();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    XSSFCreationHelper creationHelper = wb.getCreationHelper();
		    
		    createExcelHeader( wb, sheet, creationHelper);
		    
		    int rowCount = 10;
		    
			XSSFCellStyle  st1 = getExcelCellStyle(wb, 1, 1, 
					   1, 1, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		    
			Vector<WsRashodPartData> vec_parts = WsRashodSqlStatements.getRashodPartsVector(m_data.id);
			
			XSSFCellStyle st00 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
	
	    	for (WsRashodPartData dt : vec_parts) {
	
	            XSSFRow row = sheet.createRow(rowCount);
	            
	            double v1 = dt.quantity/(sum)*((int)m_spinSnid.getValue());
	            
	            double v2 = dt.quantity/(sum)*((int)m_spinObid.getValue());
	            
	            double v3 = dt.quantity - v1 - v2;
	            
	            if((int)m_spinVech.getValue() == 0) { v3 = 0.0; }
	            
	            for (int j = 0; j < 8; j++) {
	            	
	                XSSFCell cell = row.createCell(j);
	                
	                cell.setCellStyle(st1);
	
	                switch (j) {

		                    case 0: {
		                    	
		                        cell.setCellValue(dt.kod); 
		                        
		                        cell.setCellStyle(st00);
		                        
		                        break;
		                    }
		                        
		                    case 1: {
		                    	
		                        cell.setCellValue(dt.name);
		                        
		                        cell.setCellStyle(st00);
		                        
		                        break;
		                        
		                    }
		                        
		                    case 2: {
		                    	
		                        cell.setCellValue(dt.units_name);
		                        
		                        break;
		                        
		                    }
		                        
		                    case 3: {
		                    	
		                        cell.setCellValue(v1);
		                        
		                        break;
		                        
		                    }
		                        
		                    case 4: {
		                    	
		                        cell.setCellValue(v2);
		                        
		                        break;
		                        
		                    }
		                    case 5: {
		                    	
		                        cell.setCellValue(v3);
		                        
		                        break;
		                        
		                    }
		                        
		                    case 6: {
		                    	
		                        cell.setCellValue(dt.quantity);
		                        
		                        break;
		                        
		                    }
		                    
		                    default: {
		                    	
		                        cell.setCellValue("");
		                        
		                        break;
		                        
		                    }
	                    }
	                };
	                
	                rowCount++;
	         }
	    	
	
	    	 
	    	XSSFRow  row1 = sheet.createRow(rowCount++);
	    	
	    	XSSFCell cell0 = row1.createCell(0);
	    	
	    	cell0.setCellValue(getGuiStrs("naklMilt4"));
	    	 
	    	WsInfoData d = getInfo();
	    	 
	    	row1 = sheet.createRow(rowCount++);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	cell0.setCellValue(getGuiStrs("naklMilt9") + " " + d.name);
	    	 
	    	row1 = sheet.createRow(rowCount++);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	cell0.setCellValue( info2_vec.elementAt(0).rank);
	    	
	    	cell0 = row1.createCell(2);
	    	
	    	cell0.setCellValue( info2_vec.elementAt(0).name);
	    	 
	    	row1 = sheet.createRow(rowCount++);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	cell0.setCellValue(getGuiStrs("naklMilt10")+": " + info2_vec.elementAt(1).rank + " ___________  " + info2_vec.elementAt(1).name);
	    	
	    	cell0 = row1.createCell(2);
	    	
	    	cell0.setCellValue(getGuiStrs("naklMilt11")+": __________________________________");
	    	 
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
	
	
	private void createExcelHeader( XSSFWorkbook wb, XSSFSheet sheet,  XSSFCreationHelper creationHelper) {
		
		   String date_s = WsUtils.dateToString( m_date_2.getSqlStartDate(), "dd.MM.yyyy" );
		
		   String date_e = WsUtils.dateToString( m_date_2.getSqlEndDate(), "dd.MM.yyyy" );
		
		   String[] cap_names = {getGuiStrs("naklMilt12")+" " + getGuiStrs("nmbStr") + " " + m_data.number,
				   "",  getGuiStrs("naklMilt1") + " " + m_data.agentName,
				   getGuiStrs("naklMilt2"),"",
				   date_s + " " + getGuiStrs("bookSkladPoReportName") + " " + date_e,""};
		   
		   XSSFCellStyle st0 = getExcelCellStyle(wb, 0, 0, 0, 0, 
				   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   for(int i = 0; i < 7; ++i) {
			   
			   XSSFRow rowHeader = sheet.createRow(i);
			   
			   XSSFCell cl0 = createCell(rowHeader, 0, cap_names[i], creationHelper); 
			   
			   sheet.addMergedRegion(new CellRangeAddress(i,i,0,7));
			   
			   cl0.setCellStyle(st0);
			    
		   }
		   
		   XSSFCellStyle  st1 = getExcelCellStyle(wb, 1, 1, 
				   1, 1, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   XSSFRow rowHeader = sheet.createRow(7);
		   
		   XSSFCell cl = createCell(rowHeader, 0, getGuiStrs("nmbStr"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(7,9,0,0));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 1, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   sheet.addMergedRegion(new CellRangeAddress(7,9,1,1));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 2, getGuiStrs("unitsNameInReport"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(7,9,2,2));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 3, getGuiStrs("naklMilt3"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(7,7,3,6));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 4, "", creationHelper); 

		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 5, "", creationHelper); 

		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 6, "", creationHelper); 

		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 7, getGuiStrs("naklMilt4"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(7,9,7,7));
		   
		   XSSFCellStyle  st = getExcelCellStyle(wb, 1, 1, 
				   1, 1,
					false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   cl.setCellStyle(st);
		   
		   XSSFRow rowHeader1 = sheet.createRow(8);
		   
		   cl = createCell(rowHeader1, 0, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader1, 1, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader1, 2, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader1, 3, getGuiStrs("naklMilt5"), creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader1, 4, getGuiStrs("naklMilt6"), creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader1, 5, getGuiStrs("naklMilt7"), creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader1, 6, getGuiStrs("naklMilt8"), creationHelper);
		   
		   XSSFCellStyle  st11 = getExcelCellStyle(wb, 1, 1, 
				   1, 1,
					false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, true);
		   
		   cl.setCellStyle(st11);
		   
		   cl = createCell(rowHeader1, 7, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   XSSFRow rowHeader2 = sheet.createRow(9);
		   
		   cl =createCell(rowHeader2, 0, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl =createCell(rowHeader2, 1, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl =createCell(rowHeader2, 2, "", creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader2, 3, String.valueOf(((int)m_spinSnid.getValue())), creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader2, 4, String.valueOf(((int)m_spinObid.getValue())), creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader2, 5, String.valueOf(((int)m_spinVech.getValue())), creationHelper);
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader2, 6, "", creationHelper);

		   sheet.addMergedRegion(new CellRangeAddress(8,9,6,6));
		   
		   cl.setCellStyle(st1);
		 
		   cl = createCell(rowHeader2, 7, "", creationHelper);

		   cl.setCellStyle(st1);
		
	}
	
	private XSSFCell createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
	      
	      return cell3;
		
	}
}

