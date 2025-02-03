
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsFileTableControl;
import WsControls.WsPartTypesFilterComboBox;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsMoveKodPage;
import WsDataStruct.WsPartType;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDataStruct.WsSkladMoveDataRow;
import WsDatabase.WsReportsSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsMain.WsGuiTools;
import WsMain.WsUtils;



/**
 * The class for '46 dodatok' generation.
 * The generation of this report uses foreign databases for other store units.
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsForeignSklads46Report  extends WSReportViewer {

	HashMap<Integer, WsMoveKodPage> m_vec_data = null; 
	
	//key - index, value - kod ; this map is just for saving
	HashMap<Integer, Integer> m_map_codes = null; 
	
	//key - kod, value - page number ; this map is just for saving
	HashMap<Integer, Integer> m_map_pages = null; 
			
	WsFileTableControl m_table_control = null;
	
	protected WsPartTypesFilterComboBox m_partTypesCombo = new  WsPartTypesFilterComboBox();
	 
	/**
	 * @param f
	 * @param nameFrame
	 */
	public  WsForeignSklads46Report(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		String[] columnNames = { getGuiStrs("importBazaSourceFileName"), 
				getGuiStrs("importBazaPidrozdilFileName")};
		
		m_table_control = new WsFileTableControl(columnNames, getGuiStrs("chooserBazaFileLabelName"), true);
		
		createGui();
		
		m_partTypesCombo.refreshModel(null);
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	 m_html_pages = generateReport();
            	
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
            		
            		exportToExcelFile();
            	
            	}
            }
	    });
		
		
		ItemChangeListener boxListener = new ItemChangeListener();
		
		m_partTypesCombo.addItemListener(boxListener);
		
		m_table_control.setButtonToolTip(getMessagesStrs("d46ButtonToolTip"));
		
		m_table_control.setTableToolTips(getMessagesStrs("d46TableToolTip") );
		
		setSize(1100, 650);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private void createGui() {
		
		WsUtils.get().setFixedSizeBehavior(m_table_control);
		
		WsGuiTools.setComponentFixedHeight(m_table_control, 120);
		
		JPanel panel_part = WsGuiTools.createVerticalPanel();
		
		JPanel panel_l = WsGuiTools.createHorizontalPanel();
		
		panel_l.add(new JLabel(getGuiStrs("partForReport46ComoLabel"))); 
		
		panel_l.add(m_partTypesCombo); 
		
		panel_l.add(Box.createHorizontalGlue());
		
		panel_part.add( panel_l);
		
		panel_part.add(Box.createVerticalGlue());
		
		JPanel panel_t = WsGuiTools.createVerticalPanel();
		
		panel_t.add(m_table_control);
		
		panel_t.add(Box.createVerticalGlue());
		
		m_control_panel2.add(panel_t);
		
		m_control_panel2.add(panel_part);
			
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		m_table_control.setTableToolTips(getGuiStrs("porivzVitZsuProdTableTooltip") );
		
		WsGuiTools.fixComponentHeightToMin(m_control_panel2);
		
		WsGuiTools.fixComponentHeightToMin(m_partTypesCombo);
		
		WsGuiTools. setComponentFixedWidth(m_partTypesCombo,350);
		
	}
	
	/**
	 * Returns the vector of generated report pages
	 */
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsAgentData> a_vec = m_table_control.getData();
		
		Vector<WsAgentData> agents_vec = new Vector<WsAgentData>();
		
		WsAgentData this_agent = new WsAgentData();
		
		this_agent.id = 1111111;
		
		this_agent.name = getGuiStrs("skladNAme");
		
		agents_vec.add(this_agent);
		
		agents_vec.addAll(a_vec);
				
		HashMap<Integer, WsMoveKodPage> vec_all = WsReportsSqlStatements.getDodatok46Movement(m_date.getSqlStartDate(),
				 m_date.getSqlEndDate(), agents_vec);
		
		ArrayList<Integer> keys = new ArrayList<Integer>(vec_all.keySet());
		
		Collections.sort(keys);
		
		int pages_number =  0;
		
		Vector<String> vec_pages = new Vector<String>();
		
		m_map_codes = new HashMap<Integer, Integer>(); 
		
		m_map_pages = new HashMap<Integer, Integer>(); 
		
		for(int k = 0; k < keys.size(); ++k) { 
			
			 WsMoveKodPage p = vec_all.get(keys.get(k));
			 
			 if(! p.isEmpty()) {
				 
				 sumRest( p);
				 
				 calcRunningSum(p);
				 
				 String page = getPrintHtml(p,  ++pages_number);
				 
				 m_map_codes.put(pages_number, p.kod);
				 
				 m_map_pages.put( p.kod, pages_number);
			
				 vec_pages.add(page);
			 }
			
		}
		
		current_font_size = 4;
		
		m_vec_data = vec_all;
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
		
	}
	
	/**
	 * Returns the specific page of the report in html form.
	 */
	public String getPrintHtml( WsMoveKodPage p, int page_number) {
	
	
		Vector<WsSkladMoveDataRow> vec_all_parts = p.rows_vec;
		
		int col_number = vec_all_parts.elementAt(0).row_vec.size();
		
		Vector<WsInfoData>  v_info = WsUtilSqlStatements.getInfoDataList();
		
		String firm_name = "-----";
		
		if(v_info.size() != 0) {
		
			firm_name = v_info.elementAt(0).name;
		}
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder  sHeader02_b = new StringBuilder(500);
		
		sHeader02_b.append("<tr>");
		
		StringBuilder sHeader03_b = new StringBuilder(500);
		
		sHeader03_b.append("<tr><td rowspan ='1' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'></td>");
		
		sHeader03_b.append("<td colspan='5' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>");
		
		sHeader03_b.append(getGuiStrs("d46KatSortName") );
		
		sHeader03_b.append( "</td>");
		
		StringBuilder sHeader04_b = new StringBuilder(1000);
				
		sHeader04_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>");
		
		sHeader04_b.append(getGuiStrs("d46Itogo"));
		
		sHeader04_b.append(" </td>");
		
		sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>1 </td>");
		
		sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>2 </td>");
		
		sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>3 </td>");
		
		sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>4 </td>");
		
		sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>5 </td>");
	
		StringBuilder sHeader01_b =  new StringBuilder(1000);
		
		sHeader01_b.append("<tr><td  rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> "); 
		
		sHeader01_b.append(getGuiStrs("nmbStr"));
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td rowspan ='4'  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("reportBookInDateNameColumn"));
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>");
		
		sHeader01_b.append(getGuiStrs("docNameReportName"));
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("d46NomerDoc"));
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("d46DataDoc")); 
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'>");
		
		sHeader01_b.append(getGuiStrs("reportBookNaklPostOder"));
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td  rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("quantityNameReportColumn"));
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td  rowspan ='4' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("quantityNameVibuloReportColumn")); 
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td  colspan ='6' rowspan='2' style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("d46PrebDoc")); 
		
		sHeader01_b.append("</td>");
		
		sHeader01_b.append("<td  colspan ='");
		
		sHeader01_b.append(String.valueOf(6*col_number)); 
		
		sHeader01_b.append("'  style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid ; text-align: center;'> ");
		
		sHeader01_b.append(getGuiStrs("d46VCh") );
		
		sHeader01_b.append("</td>");
		
	
		StringBuilder rowB = new StringBuilder(1000);
		
		Vector<WsSkladMoveDataColumn> row_vec_prev = null;
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
			
			//we need the previous row values to detect the step the 'rest' value over 0
			if(i > 0) { row_vec_prev =  vec_all_parts.elementAt(i -1).row_vec; }
			
			 String bottomBorder = "";
			 
			 if(i == (vec_all_parts.size() -1)) {
				 
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 
			 String nameRow ="";
			 
			 String dateIn ="";
			 
			 String agentName ="";
			 
			 switch(d_row.indexData) {
			 
				 case 0:{ 
					 
					 nameRow = "&nbsp; " + getGuiStrs("zalishokNaReportName");
					 
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 agentName = "";
					 break;
				 }
				 case 1:  {
					 
					 nameRow = "&nbsp; " + getGuiStrs("naklReportName2");
					 
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 if(d_row.sklad_row) {
						 
						 agentName = d_row.agent_name2;
						 
					 }
					 else {
						 
						 agentName = d_row.agent_name; 
						 
					 }
					 break;
				 }
				 case 2 : {
					 
		
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 nameRow = "&nbsp; " + getGuiStrs("naklReportName2");
					 
					 agentName = d_row.agent_name2;
					 
					 break;
				 }
			 };
			 
			 String naklad_number = "";
			 
			 if(null !=  d_row.nakl_number) {
				 
				 naklad_number = d_row.nakl_number;
			 }
			

			rowB.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4>&nbsp;"); 
			
			rowB.append(String.valueOf(i + 1)); 
			
			rowB.append("&nbsp;</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder);
			
			rowB.append("'><font size =4>");
			
			rowB.append(dateIn);
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' min-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4>");
			
			rowB.append(nameRow);
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' min-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder);
			
			rowB.append("'><font size =4>");
			
			rowB.append(naklad_number); 
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			rowB.append(bottomBorder );
		    
			rowB.append("'><font size =4>" );
			
			rowB.append(dateIn );
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4>"); 
			
			rowB.append(agentName); 
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4>"); 
			
			rowB.append(WsUtils.getDF(d_row.in_quantity)); 
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4>"); 
			
			rowB.append(WsUtils.getDF(d_row.out_quantity)); 
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4>"); 
			
			rowB.append(WsUtils.getDF(d_row.doc_quantity));
			
			rowB.append("</font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4></font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			rowB.append(bottomBorder);
			
			rowB.append("'><font size =4></font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4></font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4></font></td>");
			
			rowB.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  "); 
			
			rowB.append(bottomBorder); 
			
			rowB.append("'><font size =4></font></td>");
	
			double prevRest = 0.0;
			
			for(int j = 0; j < row_vec.size(); ++j) {
			
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 String rightBorder = "";
				
				 
				 if(j == (row_vec.size() -1)) {
					 
					 rightBorder = "border-right: 1px solid ;";
					 
				 }
				 
				 
				rowB.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
				rowB.append(bottomBorder); 
				
				rowB.append(" text-align: center; '><font size =4>"); 
				
				 // 'the first 0.0 must be shown' code
				 if( row_vec_prev != null) {
					 
					 prevRest = row_vec_prev.elementAt(j).rest;
					 
					 if(Math.abs(dc.rest - prevRest) <   WsUtils.getRZL()) {
						 
						 rowB.append("");
					 }
					 else {
						 rowB.append(WsUtils.getDF_0(dc.rest) );
						 
					 }
				 }
				 else {
					 
					 rowB.append(WsUtils.getDF(dc.rest) );
				 }
				//end 'the first 0.0 must be shown' code
				
				
				rowB.append("</font></td>");
					
				rowB.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
				rowB.append(bottomBorder); 
				
				rowB.append(" text-align: center; '><font size =4></font></td>");
				
				rowB.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
				rowB.append(bottomBorder); 
				
				rowB.append(" text-align: center; '><font size =4></font></td>");
				 
				rowB.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
				rowB.append(bottomBorder); 
				
				rowB.append(" text-align: center; '><font size =4></font></td>");
				 
				rowB.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
				rowB.append(bottomBorder); 
				
				rowB.append(" text-align: center; '><font size =4></font></td>");
				 
				rowB.append("<td style='border-left: 1px solid;border-top: 1px solid ; "); 
				
				rowB.append(bottomBorder); 
				
				rowB.append(rightBorder);  
				
				rowB.append("text-align: center; '><font size =4></font></td>");
					
				if( i == 0) {
					 
					sHeader02_b.append("<td colspan='6' style='border-left: 1px solid;border-top: 1px solid ;");
					 
					sHeader02_b.append(rightBorder);
					 
					sHeader02_b.append(" text-align: center;'><font size =4>" + dc.name +  "</font></td>" + "");
	 
					sHeader03_b.append("<td  style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>" );
					
					sHeader03_b.append("" +  "</font></td>");
					
					sHeader03_b.append("<td colspan='5' style='border-left: 1px solid;border-top: 1px solid ;" + rightBorder + " text-align: center;'><font size =4>");
					
					sHeader03_b.append(getGuiStrs("d46KatSortName"));
					
					sHeader03_b.append("</font></td>");
						 			 
					sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>"); 
					
					sHeader04_b.append(getGuiStrs("d46Itogo"));
					
					sHeader04_b.append("</font></td>");
					
					sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; text-align: center;'><font size =4>"); 
					
					sHeader04_b.append("1</font></td>");
					
					sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;  text-align: center;'><font size =4>");
					
					sHeader04_b.append("2</font></td>");
					
					sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;  text-align: center;'><font size =4>");
					
					sHeader04_b.append("3</font></td>"); 
					
					sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ;  text-align: center;'><font size =4>");
					
					sHeader04_b.append("4</font></td>");
					
					sHeader04_b.append("<td style='border-left: 1px solid;border-top: 1px solid ; ");
					
					sHeader04_b.append(rightBorder);
					
					sHeader04_b.append("text-align: center;'><font size =4>");
					
					sHeader04_b.append("5</font></td>");
					 
				 }
			}
			
			rowB.append("</tr>");
		}
		
		sHeader02_b.append("</tr>");
		
		sHeader01_b.append("</tr>");

		sHeader04_b.append("</tr>");
		
		sHeader03_b.append("</tr>");
		
		StringBuilder hs_b = new StringBuilder(3000);
		
		hs_b.append("<!DOCTYPE html><html> ");
		
		hs_b.append("<style>    body {\r\n");
		
		hs_b.append("        height: 210mm;\r\n");
		
		hs_b.append("        width: 297mm;\r\n");
		
		hs_b.append("        /* to centre page on screen*/\r\n");
		
		hs_b.append("        margin-left: 20;\r\n");
		
		hs_b.append("        margin-right: auto;\r\n");
		
		hs_b.append("    }");
		
		hs_b.append("</style><body>");
		
		hs_b.append("<h2 align='center' ><font size =5>"); 
		
		hs_b.append(String.valueOf(p.kod));
		
		hs_b.append(" ");
		
		hs_b.append(p.name_kod);
		
		hs_b.append(" : ");
		
		hs_b.append(getGuiStrs("bookSkladMovement46ReportName") );
		
		hs_b.append( " " );
		
		hs_b.append( date_s );
		
		hs_b.append( " ");
		
		hs_b.append(getGuiStrs("bookSkladPoReportName"));
		
		hs_b.append( " ");
		
		hs_b.append(date_e  );
		
		hs_b.append("  " );
		
		hs_b.append( firm_name );
		
		hs_b.append( "   " );
		
		hs_b.append( getGuiStrs("prodSkladNameReport") );
		
		hs_b.append("</font></h2>");
		
		hs_b.append("<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>");
		
		hs_b.append(sHeader01_b.toString() );
		
		hs_b.append(sHeader02_b.toString() );
		
		hs_b.append(sHeader03_b.toString() );
		
		hs_b.append(sHeader04_b.toString() );
		
		hs_b.append(rowB.toString());
		
		hs_b.append("</table>");
		
		hs_b.append("<br><br><table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0><tr><td></td><td></td></tr><tr><td></td><td></td></tr>"
				+ "</table>");
		
		hs_b.append("</body></html>");
		
		return hs_b.toString();
		 
	}

	public boolean saveToFile(String pathFolder) {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		if(m_partTypesCombo.getSelectedIndex() == 0) {
		
			for(int i = 0; i <  m_html_pages.size(); ++i) {
				
				String kod = String.valueOf(m_map_codes.get(i + 1));
				
				
				 File path = new File(WsUtils.concatPathName(pathFolder,  kod + "_46d.html"));
	
			        FileWriter wr;
			        
					try {
						wr = new FileWriter(path);
		
				        wr.write(m_html_pages.elementAt(i));
		
				        wr.flush();
				         
				        wr.close();
				        
					} catch (IOException e) {
						
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					
						e.printStackTrace();
						
						return false;
					}
	
			}
		}
		else {
				
				String kod = String.valueOf(m_map_codes.get(currentPage));
				
				File path = new File(WsUtils.concatPathName(pathFolder,  kod + "_46d.html"));
	
			    FileWriter wr;
			        
				try {
						wr = new FileWriter(path);
		
				        wr.write(m_html_pages.elementAt(currentPage - 1));
		
				        wr.flush();
				         
				        wr.close();
				        
				} catch (IOException e) {
					
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					
						e.printStackTrace();
						
						return false;
				}
				
				
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return true;
	}
	
	
	
	public void exportToExcelFile() {
		
		int startIndex = 1;
		
		int endIndex =  m_html_pages.size() + 1;
		
		if(m_partTypesCombo.getSelectedIndex() != 0) {
			
			 startIndex = currentPage;
			 
			 endIndex = startIndex + 1;
			
		}
		
		String path_to_save =  excelSaveFolderChoose(this) ;
		
		requestFocus();
		
		if(null == path_to_save) return;
	
		OutputStream out;
		
		getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			
			for(int i = startIndex; i < endIndex; ++i) {
				
				String kod = String.valueOf(m_map_codes.get(i));
				
				String file_to_save = WsUtils.concatPathName(path_to_save , kod + "_d46.xlsx");
			
				try {
					
					out = new FileOutputStream(file_to_save);
					
				} catch(java.io.FileNotFoundException exf) {
					
					getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
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
			    
			    int page_num = i;
			    
			    exportExcelPage(sheet,   m_vec_data.get( m_map_codes.get(page_num)), 1, creationHelper);
		   
				wb.write(out);
		
				out.close();
		    
				wb.close(); 
			
			}
			
			getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("saveExcelReportSuccessMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
    
		} catch (IOException  e) {
			
			getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("saveExcelReportFailedMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		}
		
		getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	}
	
	//return last row index
	private int createExcelHeader( int initialIndex, XSSFSheet sheet,  WsMoveKodPage p, XSSFCreationHelper creationHelper) {
		
		
		   int indexRow = initialIndex;
		   
		   int firstRowIndex = indexRow;
			
		   XSSFRow rowHeader0 = sheet.createRow(indexRow);
		   
		   
		   createCell(rowHeader0, 0, getGuiStrs("nmbStr"), creationHelper); 
		   
		   createCell(rowHeader0, 1, getGuiStrs("reportBookInDateNameColumn"), creationHelper); 
		   
		   createCell(rowHeader0, 2, getGuiStrs("docNameReportName"), creationHelper); 
		   
		   createCell(rowHeader0, 3, getGuiStrs("d46NomerDoc"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("d46DataDoc"), creationHelper); 
		   
		   createCell(rowHeader0, 5, getGuiStrs("reportBookNaklPostOder"), creationHelper); 
		   
		   createCell(rowHeader0, 6, getGuiStrs("quantityNameReportColumn"), creationHelper); 
		   
		   createCell(rowHeader0, 7, getGuiStrs("quantityNameVibuloReportColumn"), creationHelper); 
		   
		   createCell(rowHeader0, 8,  getGuiStrs("d46PrebDoc"), creationHelper); 
		   
		   createCell(rowHeader0, 9, "", creationHelper); 
		   
		   createCell(rowHeader0, 10, "", creationHelper); 
		   
		   createCell(rowHeader0, 11, "", creationHelper); 
		   
		   createCell(rowHeader0, 12, "", creationHelper); 
		   
		   createCell(rowHeader0, 13, "", creationHelper); 
		   
		   createCell(rowHeader0, 14, getGuiStrs("d46VCh"), creationHelper); 

		   indexRow++;
		   
		   WsSkladMoveDataRow firstRow = p.rows_vec.elementAt(0);
		   
		   Vector<WsSkladMoveDataColumn> vec = firstRow.row_vec;
		   
		   XSSFRow rowHeader1 = sheet.createRow(indexRow);
		   
		   for(int i1 = 0; i1 < 14; ++i1) {
		   
			   createCell(rowHeader1, i1, "", creationHelper); 
		   }
		   		   
		   int columnIndex = 14;
		   
		   for(int i =0; i < vec.size(); ++i) {
			   
			   WsSkladMoveDataColumn d = vec.elementAt(i);
			   
			   createCell(rowHeader1, columnIndex, d.name, creationHelper); 
			   
			   createCell(rowHeader1, ++columnIndex, "", creationHelper);
			   
			   createCell(rowHeader1, ++columnIndex, "", creationHelper);
			   
			   createCell(rowHeader1, ++columnIndex, "", creationHelper);
			   
			   createCell(rowHeader1, ++columnIndex, "", creationHelper);
			   
			   createCell(rowHeader1, ++columnIndex, "", creationHelper);
			   
			   sheet.addMergedRegion(new CellRangeAddress(indexRow,indexRow, columnIndex - 5, columnIndex));
			   
			   ++columnIndex;
			   
		   }
		   
		   
		   sheet.addMergedRegion(new CellRangeAddress(indexRow - 1,indexRow, 8, 13));
		   
		   sheet.addMergedRegion(new CellRangeAddress(indexRow - 1,indexRow - 1, 14, 14 + vec.size()*6 - 1));
		   
		   ++indexRow;
		   
		   XSSFRow rowHeader2 = sheet.createRow(indexRow);
		   
		   createCell(rowHeader2, 0, "", creationHelper); 
		   
		   createCell(rowHeader2, 1, "", creationHelper); 
		   
		   createCell(rowHeader2, 2, "", creationHelper); 
		   
		   createCell(rowHeader2, 3, "", creationHelper); 
		   
		   createCell(rowHeader2, 4, "", creationHelper); 
		   
		   createCell(rowHeader2, 5, "", creationHelper); 
		   
		   createCell(rowHeader2, 6, "", creationHelper); 
		   
		   createCell(rowHeader2, 7, "", creationHelper); 
		   
		   createCell(rowHeader2, 8, "", creationHelper); 
		   
		   createCell(rowHeader2, 9, getGuiStrs("d46KatSortName"), creationHelper); 
		   
		   createCell(rowHeader2, 10, "", creationHelper); 
		   
		   createCell(rowHeader2, 11, "", creationHelper); 
		   
		   createCell(rowHeader2, 12, "", creationHelper); 
		   
		   createCell(rowHeader2, 13, "", creationHelper); 
		   
		   sheet.addMergedRegion(new CellRangeAddress(indexRow,indexRow, 9, 13));
		   
		   columnIndex = 14;
		   
		   for(int i = 0; i < vec.size(); ++i) {
			   
			   createCell(rowHeader2, columnIndex, "", creationHelper); 
			   
			   createCell(rowHeader2, ++columnIndex, getGuiStrs("d46KatSortName"), creationHelper); 
			   
			   createCell(rowHeader2, ++columnIndex, "", creationHelper); 
			   
			   createCell(rowHeader2, ++columnIndex, "", creationHelper); 
			   
			   createCell(rowHeader2, ++columnIndex, "", creationHelper); 
			   
			   createCell(rowHeader2, ++columnIndex, "", creationHelper); 
			   
			   sheet.addMergedRegion(new CellRangeAddress(indexRow,indexRow, columnIndex - 4, columnIndex));
			   
			   ++columnIndex;
			   
		   }
		   
		   ++indexRow;
		   
		   XSSFRow rowHeader3 = sheet.createRow(indexRow);
		   
		   createCell(rowHeader3, 0, "", creationHelper); 
		   
		   createCell(rowHeader3, 1, "", creationHelper); 
		   
		   createCell(rowHeader3, 2, "", creationHelper); 
		   
		   createCell(rowHeader3, 3, "", creationHelper); 
		   
		   createCell(rowHeader3, 4, "", creationHelper); 
		   
		   createCell(rowHeader3, 5, "", creationHelper); 
		   
		   createCell(rowHeader3, 6, "", creationHelper); 
		   
		   createCell(rowHeader3, 7, "", creationHelper); 
		   
		   createCell(rowHeader3, 8, getGuiStrs("d46Itogo"), creationHelper); 
		   
		   createCell(rowHeader3, 9, "1", creationHelper); 
		   
		   createCell(rowHeader3, 10, "2", creationHelper); 
		   
		   createCell(rowHeader3, 11, "3", creationHelper); 
		   
		   createCell(rowHeader3, 12, "4", creationHelper); 
		   
		   createCell(rowHeader3, 13, "5", creationHelper); 
		   
		   columnIndex = 14;
		   
		   for(int i = 0; i < vec.size(); ++i) {
		   
			   createCell(rowHeader3, columnIndex, getGuiStrs("d46Itogo"), creationHelper); 
			   
			   createCell(rowHeader3, ++columnIndex, "1", creationHelper); 
			   
			   createCell(rowHeader3, ++columnIndex, "2", creationHelper); 
			   
			   createCell(rowHeader3, ++columnIndex, "3", creationHelper); 
			   
			   createCell(rowHeader3, ++columnIndex, "4", creationHelper); 
			   
			   createCell(rowHeader3, ++columnIndex, "5", creationHelper); 
			   
			   ++columnIndex;
			   
		   }
		   
		   for(int i =0 ; i < 8; ++i) {
			   sheet.addMergedRegion(new CellRangeAddress(firstRowIndex,firstRowIndex + 3, i, i));
		   }
		   
		   return indexRow;
		   
		  	
	}
	
	private void createCell( XSSFRow rowHeader, int index, String s, XSSFCreationHelper creationHelper) {
		
		  XSSFCell cell3 = rowHeader.createCell(index);
	        
	      XSSFRichTextString richString3 = creationHelper
	                .createRichTextString(s);
	
	      cell3.setCellValue(richString3);
		
	}
	
	
	public Vector<WsSkladMoveDataColumn> removeZerosRows(Vector<WsSkladMoveDataColumn>  vec) {
		
		Vector<WsSkladMoveDataColumn>  new_vec = new Vector<WsSkladMoveDataColumn>();
		
		double zL = WsUtils.getRZL();
		
		for(WsSkladMoveDataColumn d: vec) {
			
			boolean flag = d.initial_rest >zL || d.initial_rest_1 > zL || d.in_quantity > zL || 
					d.in_quantity_1 > zL || d.out_quantity > zL ||
					d.out_quantity_1 > zL || d.rest > zL || d.rest_1 > zL;
					
			if(flag) {
				
				new_vec.add(d);
			}
			
			
		}
		
		return new_vec;
	
	}
	
	private void calcRunningSum(WsMoveKodPage p) {
	
		 Vector<WsSkladMoveDataRow> v = p.rows_vec;
		
		 WsSkladMoveDataRow row = v.elementAt(0);
		 
		 Vector<WsSkladMoveDataColumn> c_v = row.row_vec;
		
		 if(row.indexData == 1) {
			 
			 for(int j = 0; j < c_v.size(); ++j) {
				 
				 c_v.elementAt(j).rest =  c_v.elementAt(j).in_quantity;
			 }
			 
			 row.in_quantity = c_v.elementAt(0).in_quantity;
		 }
		 
		 
		 
		 for(int j = 0; j < c_v.size(); ++j) {
			 
			 row.doc_quantity += c_v.elementAt(j).rest;
		 }
		 
	 
		 for(int i = 1; i < v.size(); ++i) {
			 
			 WsSkladMoveDataRow prev_row = v.elementAt(i - 1);
			 
			 Vector<WsSkladMoveDataColumn> prev_c_v = prev_row.row_vec;
				 
			 row = v.elementAt(i);
				 
			 c_v = row.row_vec;
			  	 
			 switch(row.indexData) {
				 
					 case 0: {
						 
						break; 
					 }
					 case 1: { //prihod
						 
						 if(row.sklad_row) {//prihod na sklad
							 
							 double value = c_v.elementAt(0).in_quantity;
							 
							 c_v.elementAt(0).rest = prev_c_v.elementAt(0).rest + value;
							 
							 for(int j = 1; j < c_v.size(); ++j) {
								 
								 c_v.elementAt(j).rest = prev_c_v.elementAt(j).rest;
								 
							 }
							 
							 row.in_quantity = value;
							// row.doc_quantity += value;
						 }
						 else {//prihod na bat
							 
							 double non_zero_value = 0.0;
							 
							 for(int j = 1; j < c_v.size(); ++j) {
								 
								 double value = c_v.elementAt(j).in_quantity;
								 
								 c_v.elementAt(j).rest = prev_c_v.elementAt(j).rest + value;
								 
								 if(value != 0.0) {  non_zero_value = value; }
							 }
							 
							 //prihod to bat  means  minus sklad if this is not external prihod 
							 if(!row.external_prihod)  {
								 
								 c_v.elementAt(0).rest = prev_c_v.elementAt(0).rest - non_zero_value;

								 row.out_quantity =  non_zero_value;
								 
							 }
							 else {
								 //prihod not from the main store;  that means that sklad rest must be same as in previous row
								 
								 c_v.elementAt(0).rest = prev_c_v.elementAt(0).rest;
								 
								 row.in_quantity =  non_zero_value;
								 
								 row.out_quantity =  0.0;
								 
							 }
							 
							 //row.out_quantity =  non_zero_value;
							
						 }
						 
						 break; 
					 }
					 case 2: { //rashod
						 
						 if(row.sklad_row) {//rashod sklad no bat
							 
							 double value = c_v.elementAt(0).out_quantity;
							 
							 c_v.elementAt(0).rest = prev_c_v.elementAt(0).rest - value;
							 
							 for(int j = 1; j < c_v.size(); ++j) {
								 
								 c_v.elementAt(j).rest = prev_c_v.elementAt(j).rest;
							 }
							 
							 row.out_quantity =  value;
						 }
						 else {//rashod  bat
							 
							 double non_zero_value = 0.0;
							 
							 c_v.elementAt(0).rest = prev_c_v.elementAt(0).rest;
							 
							 for(int j = 1; j < c_v.size(); ++j) {
								 
								 double value = c_v.elementAt(j).out_quantity;
								 
								 c_v.elementAt(j).rest = prev_c_v.elementAt(j).rest - value;
								 
								 if(value != 0.0) {  non_zero_value = value; }
								 
							 }
							 
							 row.out_quantity =  non_zero_value;
							 
						 }
						 
						 break; 
					 }
				 };
				 
				 for(int j = 0; j < c_v.size(); ++j) {
					 
					 row.doc_quantity += c_v.elementAt(j).rest;
				 }
		 
			}
		 
	 
	}
	
	
	private void sumRest(WsMoveKodPage p) {
		
		 WsSkladMoveDataRow r_sklad = p.rows_vec.elementAt(0);
		
		 for(int j1 = 1; j1 < p.rows_vec.size(); ++j1) {
			 
			 WsSkladMoveDataRow r1 = p.rows_vec.elementAt(j1);
			 
			 if(r1.indexData == 0 && !r1.sklad_row)  {
				 
				 Vector<WsSkladMoveDataColumn> v1 =  r_sklad.row_vec;
				 
				 Vector<WsSkladMoveDataColumn> v2 =  r1.row_vec;
				 
				 for(int i1 = 1; i1 < v1.size(); ++i1) {
					 
					 v1.elementAt(i1).rest +=  v2.elementAt(i1).rest;
					 
				 }
				 
			 }
			 else { break; }
			 
		 }
		 //we need omly 1 rest row
		 for(int j1 = 1; j1 < p.rows_vec.size(); ++j1) {
			 
			 WsSkladMoveDataRow r1 = p.rows_vec.elementAt(j1);
			  
			 if(r1.indexData == 0) { 
				 
				 p.rows_vec.remove(j1--);
			 }
			 else {
				 break;
			 }
		 
		 }
	 
	}
	
	
	class ItemChangeListener implements ItemListener{
		
	    @Override
	    public void itemStateChanged(ItemEvent event) {
	    	
	       if (event.getStateChange() == ItemEvent.SELECTED) {
	    	   
	    	   if (event.getSource() ==  m_partTypesCombo ) {
	    		   
	    		   if(m_map_pages == null || m_html_pages == null || m_html_pages.isEmpty()) { return; }
	    		   
	    		   WsPartType d = m_partTypesCombo.getSelectedPartData();
	    		   
	    		   if(null != d &&  m_map_pages != null) {
	    		   
		    		   Integer page_number = m_map_pages.get(d.kod);
		    		   
		    		   if(null == page_number) {
		    			   
		    			   JOptionPane.showMessageDialog(
					   			    WsUtils.get().getMainWindow(),
					   			    getMessagesStrs("noKodMove46ReportMessage"),
					   			    getMessagesStrs("messageInfoCaption"),
					   			    JOptionPane.CLOSED_OPTION);
		    			   
		    			   return;
		    		   }
		    		   
		    		   currentPage = page_number;
		            	
		            	if(currentPage > m_html_pages.size()) { 
		            		
		            		currentPage = m_html_pages.size();
		            	}
	    		   }
	    		   else {
	    			   currentPage = 1;
	    		   }
	            	
	            	setDialogCaption();
	            	
	            	setText(m_html_pages.elementAt(currentPage - 1));
	            	
	            	m_viewer.setSelectionStart(0);
	            	
	            	m_viewer.setSelectionEnd(0);
	    	   } 	  
	       }
	    }   
	}
	
	
	public void exportExcelPage(XSSFSheet sheet,  WsMoveKodPage p, int page_num, XSSFCreationHelper creationHelper) {
		
		
		Vector<WsSkladMoveDataRow> vec_all_parts = p.rows_vec;
		
		Vector<WsInfoData>  v_info = WsUtilSqlStatements.getInfoDataList();
		
		String firm_name = "-----";
		
		if(v_info.size() != 0) {
		
			firm_name = v_info.elementAt(0).name;
		}
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		String caption = String.valueOf(p.kod) + " " + p.name_kod + " : " +
				getGuiStrs("bookSkladMovement46ReportName") + " " + date_s + " "
				+ getGuiStrs("bookSkladPoReportName") + " "
				+ date_e  + "  " + firm_name + "   " + getGuiStrs("prodSkladNameReport") ;

		
		XSSFRow rowHeader0 = sheet.createRow(0);
		
		createCell(rowHeader0, 0, caption, creationHelper);
		
		for(int i = 1; i < 14; ++i) {
			
			createCell(rowHeader0, i, "", creationHelper); 
		}
		
		sheet.addMergedRegion(new CellRangeAddress(0,0,0, 13));
		
		int rowIndex = createExcelHeader(1, sheet,  m_vec_data.get( m_map_codes.get(page_num)), creationHelper);
		
		++rowIndex;
		
		Vector<WsSkladMoveDataColumn> row_vec_prev = null;
		
		for(int i = 0; i < vec_all_parts.size(); ++i) {
			
			WsSkladMoveDataRow d_row = vec_all_parts.elementAt(i);
			
			Vector<WsSkladMoveDataColumn> row_vec = d_row.row_vec;
			
			//we need the previous row values to detect the step the 'rest' value over 0
			if(i > 0) { row_vec_prev =  vec_all_parts.elementAt(i -1).row_vec; }
			
			 String nameRow ="";
			 
			 String dateIn ="";
			 
			 String agentName ="";
			 
			 switch(d_row.indexData) {
			 
				 case 0:{ 
					 
					 nameRow = "  " + getGuiStrs("zalishokNaReportName");
				
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 agentName = "";
					 
					 break;
				 }
				 case 1:  {
					 
					 nameRow = " " + getGuiStrs("naklReportName2");
					 
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 if(d_row.sklad_row) {
						 
						 agentName = d_row.agent_name2;
					 }
					 else {
						 
						 agentName = d_row.agent_name; 
					 }
					 break;
				 }
				 case 2 : {
					 
					 dateIn = WsUtils.dateToString(d_row.date, "dd.MM.yy" );
					 
					 nameRow = "  " + getGuiStrs("naklReportName2");// + " " + d_row.nakl_number + " "+ getGuiStrs("vidNaklName2") + " " + dateIn;
					 
					 agentName = d_row.agent_name2;
					 
					 break;
				 }
			 };
			 
			 String naklad_number = "";
			 
			 if(null !=  d_row.nakl_number) {
				 
				 naklad_number = d_row.nakl_number;
			 }
			 
			 XSSFRow rowHeader1 = sheet.createRow(rowIndex++);
			 
			 createCell(rowHeader1, 0,  String.valueOf(i + 1), creationHelper);
			 
			 createCell(rowHeader1, 1,  dateIn, creationHelper);
			 
			 createCell(rowHeader1, 2,  nameRow, creationHelper);
			 
			 createCell(rowHeader1, 3,  naklad_number, creationHelper);
			 
			 createCell(rowHeader1, 4,  dateIn, creationHelper);
			 
			 createCell(rowHeader1, 5,  agentName, creationHelper);
			 
		     createCell(rowHeader1, 6,  WsUtils.getDF(d_row.in_quantity), creationHelper);
		     
			 createCell(rowHeader1, 7,  WsUtils.getDF(d_row.out_quantity), creationHelper);
			 
			 createCell(rowHeader1, 8,  WsUtils.getDF(d_row.doc_quantity), creationHelper);
			 
			 createCell(rowHeader1, 9,  "", creationHelper);
			 
			 createCell(rowHeader1, 10, "", creationHelper);
			 
			 createCell(rowHeader1, 11, "", creationHelper);
			 
			 createCell(rowHeader1, 12, "", creationHelper);
			 
			 createCell(rowHeader1, 13, "", creationHelper);
			
			int cI = 13;
			
			for(int j = 0; j < row_vec.size(); ++j) {
			
				 WsSkladMoveDataColumn dc = row_vec.elementAt(j);
				 
				 // 'the first 0.0 must be shown' code
				 if( row_vec_prev != null) {
					 
					 double prevRest = row_vec_prev.elementAt(j).rest;
					 
					 if(Math.abs(dc.rest - prevRest) <   WsUtils.getRZL()) {
						 
						 createCell(rowHeader1, ++cI, "", creationHelper);
					 }
					 else {
						 createCell(rowHeader1, ++cI, WsUtils.getDF_0(dc.rest), creationHelper);
						 
					 }
				 }
				 else {
					 
					 createCell(rowHeader1, ++cI, WsUtils.getDF(dc.rest), creationHelper);
				 }
				//end 'the first 0.0 must be shown' code
				
				 createCell(rowHeader1, ++cI, "", creationHelper);
				 
				 createCell(rowHeader1, ++cI, "", creationHelper);
				 
				 createCell(rowHeader1, ++cI, "", creationHelper);
				 
				 createCell(rowHeader1, ++cI, "", creationHelper);
				 
				 createCell(rowHeader1, ++cI, "", creationHelper);
					 
			}
		}		 
	}
}

