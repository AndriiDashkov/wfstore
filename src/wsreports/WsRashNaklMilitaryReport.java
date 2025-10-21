
package wsreports;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;
import java.awt.Font;
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
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wscontrols.Ws2DatesControl;
import wscontrols.WsCompanyComboBox;
import wscontrols.WsSignsControlPanel;
import wsdatabase.WsRashodSqlStatements;
import wsdatastruct.WsInfoData;
import wsdatastruct.WsPair;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsmain.WsCommonDataUtil;
import wsmain.WsGuiTools;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashNaklMilitaryReport extends WSReportViewer {

	Vector<WsRashodData> m_data = null;
	
	WsSignsControlPanel m_p_panel = null;
	
	TitledBorder m_title = null;
	
	private WsCompanyComboBox m_combo = new WsCompanyComboBox(true);
	
	JLabel m_name_label = new JLabel(getGuiStrs("companyComboNameNewDialogLabel"));
	
	Ws2DatesControl  m_date_2 = null;
	 
	JSpinner m_spinSnid = null;
	
	JSpinner m_spinObid = null;
	
	JSpinner m_spinVech = null;
	
	//WsSignsComboBox m_combo_nps = new WsSignsComboBox();
	
	//WsSignsComboBox m_combo_ns = new WsSignsComboBox();
	
	public WsRashNaklMilitaryReport(JFrame f, String nameFrame, Vector<WsRashodData> data) {
		super(f, nameFrame);
		
		m_data = data;
		
		createGui();
		
		//if(m_combo_ns.listSize() > 1) {
			
		//	m_combo_ns.setSelectedIndex(1);
		//}
		
		if(m_data != null && !m_data.isEmpty()) {
		
			m_date_2.setSqlStartDate( WsUtils.sqlDatePlusDays(m_data.elementAt(0).date, -6));
			 
			m_date_2.setSqlEndDate(m_data.elementAt(0).date);
			
			int people  = 0;
			
			for(WsRashodData d : m_data) {
				
				people += d.people;
			}
			
			m_spinSnid.setValue(people);
			
			m_spinObid.setValue(people);
			
			m_spinVech.setValue(people);
		
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

		setCustomFont();
	}

	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		m_control_panel.add(m_name_label);
		
		m_control_panel.add(m_combo);
		
		m_control_panel.add(Box.createHorizontalGlue());
		
		WsGuiTools.setFixedSizeBehavior(m_combo);
		
		WsGuiTools.setComponentFixedWidth( m_combo, 150);
		
		m_date.setVisible(false);
		 
		m_date_2 = new Ws2DatesControl(false);
		 
		JPanel panel2 = WsGuiTools.createVerticalPanel();
		 
		JPanel panel2_ = WsGuiTools.createHorizontalPanel();
		 
		JPanel panel2_0 = WsGuiTools.createHorizontalPanel();
		 
		m_title = BorderFactory.createTitledBorder(getGuiStrs("peopleNaklMilRportLabel"));
			
		panel2_0.setBorder(m_title);
		 
		panel2_.add(m_date_2);   panel2_.add(panel2_0);
		 
		panel2.add(panel2_);
		 
		JLabel  label1 = new JLabel(getGuiStrs("snidanokMilRportLabel"));
		 
		JLabel  label2 = new JLabel(getGuiStrs("obidMilRportLabel"));
		 
		JLabel  label3 = new JLabel(getGuiStrs("vecherMilRportLabel"));
		 
		SpinnerModel model1 = new SpinnerNumberModel(0, 0, 20000, 1);  
			
		m_spinSnid = new  JSpinner(model1);
		 
		SpinnerModel model2 = new SpinnerNumberModel(0, 0, 20000, 1);  
			
		m_spinObid = new  JSpinner(model2);
		 
		SpinnerModel model3 = new SpinnerNumberModel(0, 0, 20000, 1);  
			
		m_spinVech = new  JSpinner(model3);
		
		panel2_0.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
		panel2_0.add(label1);
		
		panel2_0.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
		panel2_0.add(m_spinSnid);
		
		panel2_0.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
		panel2_0.add(label2);
		
		panel2_0.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
		panel2_0.add(m_spinObid);
		
		panel2_0.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
		panel2_0.add(label3);
		
		panel2_0.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		 
	    panel2_0.add(m_spinVech);
		 
		panel2_0.add(Box.createHorizontalGlue());
		
		m_p_panel = new WsSignsControlPanel();
			
		panel2.add(m_p_panel);

		m_control_panel2.add(panel2);

		WsGuiTools.setFixedSizeBehavior(m_control_panel2);
		
	}
	
	@SuppressWarnings("unused")
	private WsInfoData getInfo() {
	
		int id = m_combo.getCurrentSQLId();
		
		 Vector<WsInfoData> v_info = WsCommonDataUtil.get().getInfoDataList();
		 
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
		
		m_p_panel.setComboStatic();
		
		Vector<Integer> data_int = new Vector<Integer>();
		
		for(WsRashodData d : m_data) {
			
			data_int.add(d.id);
		}
		
		Vector<WsRashodPartData>  vec_parts = WsUtils.mergeSameCodes(WsRashodSqlStatements.getRashodPartsVector(data_int));
		
		StringBuilder rows_b = new StringBuilder();
		
		double sum =  (int)m_spinSnid.getValue() + (int)m_spinObid.getValue() + (int)m_spinVech.getValue();
		
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsRashodPartData d1 = vec_parts.elementAt(i);
			
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
		
		hS_b.append(formApproveHeader(m_p_panel.getApprovePerson()));
		
		hS_b.append( "<table style='width:100%;' cellspacing='0' cellpadding='1'  >");
		
		hS_b.append( "<tr><td align='center' colspan ='8' ><font size =5>");
		
		hS_b.append(getGuiStrs("naklMilt12"));
		
		hS_b.append( " " );
		
		hS_b.append( getGuiStrs("nmbStr") );
		
		hS_b.append(" " );
		
		if(m_data.size() == 1) {
		
			hS_b.append( m_data.elementAt(0).number );
		
		}
		else {
			
			hS_b.append( "_");
		}
		
		hS_b.append( "</font></td></tr>");
		
		hS_b.append( "<tr ><td align='center' colspan ='8' nowrap><font size =5>" );
		
		hS_b.append( getGuiStrs("naklMilt1") );
		
		hS_b.append( " " );
		
		if(m_data.size() == 1) {
			
			hS_b.append( m_data.elementAt(0).agentName );
		
		}
		else {
			
			hS_b.append( "____");
		}
			
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
		
		hS_b.append("<br>");
		
		hS_b.append(formPidpFooter(m_p_panel.getP1Person()));
		
		hS_b.append("<br>");
		
		
		hS_b.append("<table style='width:100%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		 
		hS_b.append("<tr><td style='text-align:left;'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt10"));
		 
		hS_b.append("</font></td<td>    </td>");
		
		hS_b.append("<td style='text-align:left;'><font size =4>");
		
		hS_b.append(getGuiStrs("naklMilt11"));
			 
		hS_b.append("</font></td></tr>");
		 
		hS_b.append("<tr>");
				
		hS_b.append("<td style='text-align: left;'><font size =4>");
			
		if(m_p_panel.getP2Person() != null) { hS_b.append(m_p_panel.getP2Person().rank); }
		 
		hS_b.append(" __________________");
		
		if(m_p_panel.getP2Person() != null) { hS_b.append(m_p_panel.getP2Person().name); }
			
		hS_b.append("</font></td><td> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   </td>");
		 
		hS_b.append("<td style='text-align: left;'><font size =4>");
		
		hS_b.append(" __________________</font></td>");
		 		
		hS_b.append("</tr>");
		 
		hS_b.append("</table>"); 
		
		
		hS_b.append( "</body></html>");
		  
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
		
		//Vector<WsSignsData> info2_vec =  WsUtils.getInfoPidp(m_combo_nps, m_combo_ns );
		
		
		OutputStream out;
		
		try {
			
			out = new FileOutputStream(file_to_save);
	
			XSSFWorkbook wb = new XSSFWorkbook();
		
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    XSSFCreationHelper creationHelper = wb.getCreationHelper();
		    
		    int rowCount = createExcelHeader( wb, sheet, creationHelper);
		    
			XSSFCellStyle  st1 = getExcelCellStyle(wb, 1, 1, 
					   1, 1, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		    
			Vector<Integer> data_int = new Vector<Integer>();
			
			for(WsRashodData d : m_data) {
				
				data_int.add(d.id);
			}
			
			
			Vector<WsRashodPartData> vec_parts =  WsUtils.mergeSameCodes(WsRashodSqlStatements.getRashodPartsVector(data_int));
			
			XSSFCellStyle st00 = getExcelCellStyle(wb, 1, 1, 1, 1, 
					   false, HorizontalAlignment.LEFT, VerticalAlignment.CENTER, false);
	
	    	for (WsRashodPartData dt : vec_parts) {
	
	            XSSFRow row = sheet.createRow(++rowCount);
	            
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
	                
	                //rowCount++;
	         }
	    	
	
	    	 
	    	XSSFRow  row1 = sheet.createRow(++rowCount);
	    	
	    	XSSFCell cell0 = row1.createCell(0);
	    	
	    	cell0.setCellValue(getGuiStrs("naklMilt4"));
	    	  
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	if(m_p_panel.getP1Person() != null) {
		    	
	    		cell0.setCellValue( m_p_panel.getP1Person().position);
	    	
	    	}
	    	else {  cell0.setCellValue(""); }
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	if(m_p_panel.getP1Person() != null) {
	    	
	    		cell0.setCellValue( m_p_panel.getP1Person().rank + " ___________________");
	    	
	    	}
	    	else {  cell0.setCellValue(""); }
	    	
	    	cell0 = row1.createCell(2);
	    	
	    	if(m_p_panel.getP1Person() != null) {
		    	
	    		cell0.setCellValue( m_p_panel.getP1Person().name);
	    	
	    	}
	    	else {  cell0.setCellValue(""); }
	    	
	    	sheet.createRow(++rowCount);
	    	
	    	row1 = sheet.createRow(++rowCount);
	    	
	    	cell0 = row1.createCell(0);
	    	
	    	if(m_p_panel.getP2Person() != null) {
	    	
	    		cell0.setCellValue(getGuiStrs("naklMilt10")+": " + m_p_panel.getP2Person().rank + " ___________  " + m_p_panel.getP2Person().name);
	    	}
	    	else {
	    		
	    		cell0.setCellValue(getGuiStrs("naklMilt10")+":   ___________  " );
	    	}
	    	
	    	cell0 = row1.createCell(2);
	    	
	    	cell0.setCellValue(getGuiStrs("naklMilt11")+": __________________________________");
	    	 
			sheet.autoSizeColumn(1);

			wb.write(out);

			out.close();
	    
			wb.close(); 
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportSuccessMessage"));
    
		} catch (IOException  e) {

			e.printStackTrace();
			
			WsUtils.showMessageDialog(getMessagesStrs("saveExcelReportFailedMessage"));
		}

	}
	
	
	private int createExcelHeader( XSSFWorkbook wb, XSSFSheet sheet,  XSSFCreationHelper creationHelper) {
		
		   String date_s = WsUtils.dateToString( m_date_2.getSqlStartDate(), "dd.MM.yyyy" );
		
		   String date_e = WsUtils.dateToString( m_date_2.getSqlEndDate(), "dd.MM.yyyy" );
		   
		   String agent_name = "";
		   
		   String number = "";
		   
		   if(m_data.size() == 1) {
			   
			   agent_name = m_data.elementAt(0).agentName; 
			   
			   number = m_data.elementAt(0).number; 
			   
		   }
		   else {  
			   
			   agent_name = "___";
			   
			   number = " __";
		   }
		   
		
		   String[] cap_names = {getGuiStrs("naklMilt12")+" " + getGuiStrs("nmbStr") + " " +number,
				   "",  getGuiStrs("naklMilt1") + " " + agent_name,
				   getGuiStrs("naklMilt2"),"",
				   date_s + " " + getGuiStrs("bookSkladPoReportName") + " " + date_e,""};
		   
		   XSSFCellStyle st0 = getExcelCellStyle(wb, 0, 0, 0, 0, 
				   false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		  // XSSFCellStyle st0_right = getExcelCellStyle(wb, 0, 0, 0, 0, 
			//	   false, HorizontalAlignment.RIGHT, VerticalAlignment.CENTER, false);
		   
		
		   WsPair p  = createExcelApprovePersonHeader( wb, sheet, 
					creationHelper, m_p_panel.getApprovePerson(), 0, 7, HorizontalAlignment.RIGHT);
		   
		
		   int rows_count  = p.flag;
		   
		   XSSFRow rowHeader = (XSSFRow) p.complex;
		   
		   for(int i = 0; i < 7; ++i) {
			   
			   XSSFCell cl0 = createCell(rowHeader, 0, cap_names[i], creationHelper); 
			   
			   sheet.addMergedRegion(new CellRangeAddress(rows_count, rows_count, 0, 7));
			   
			   cl0.setCellStyle(st0);
			   
			   rowHeader = sheet.createRow(++rows_count);
			    
		   }
		   
		   XSSFCellStyle  st1 = getExcelCellStyle(wb, 1, 1, 
				   1, 1, false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   XSSFCell cl = createCell(rowHeader, 0, getGuiStrs("nmbStr"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count + 2,0,0));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 1, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count + 2,1,1));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 2, getGuiStrs("unitsNameInReport"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count + 2,2,2));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 3, getGuiStrs("naklMilt3"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count,3,6));
		   
		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 4, "", creationHelper); 

		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 5, "", creationHelper); 

		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 6, "", creationHelper); 

		   cl.setCellStyle(st1);
		   
		   cl = createCell(rowHeader, 7, getGuiStrs("naklMilt4"), creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(rows_count,rows_count + 2,7,7));
		   
		   XSSFCellStyle  st = getExcelCellStyle(wb, 1, 1, 
				   1, 1,
					false, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, false);
		   
		   cl.setCellStyle(st);
		   
		   XSSFRow rowHeader1 = sheet.createRow(++rows_count);
		   
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
		   
		   XSSFRow rowHeader2 = sheet.createRow(++rows_count);
		   
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

		   sheet.addMergedRegion(new CellRangeAddress(rows_count, rows_count + 1, 6,6));
		   
		   cl.setCellStyle(st1);
		 
		   cl = createCell(rowHeader2, 7, "", creationHelper);

		   cl.setCellStyle(st1);
		   
		   return rows_count;
		
	}
	
	protected void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
		
		if(m_title != null) { m_title.setTitleFont(f); }
			
	}
}

