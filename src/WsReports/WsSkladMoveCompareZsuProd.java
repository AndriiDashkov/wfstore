
package WsReports;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.WsFileTableControl;
import WsControls.WsPartTypesComboBox;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsReportsSqlStatements;
import WsDialogs.WsFileChooserDialog;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsImport.WsImportExcelUtil;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsCatalogKods;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsSkladMoveCompareZsuProd  extends WSReportViewer {

	private static final long serialVersionUID = 1L;
	
	Vector<WsSkladMoveDataColumn> m_vec_all = null;
	
	WsFileTableControl m_table_control = null; 
	
	protected JTextField m_path_raskl = new JTextField(25);
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JLabel m_infoLabel = new JLabel(getGuiStrs("rasklFoMOdLabel"));
	
	private JButton m_buttonGenerate = new JButton(getGuiStrs("generateRasklButton"));
	
	private static String raskl_path_static = new String();
	
	private JButton m_saveRasklButton = new JButton(getGuiStrs("saveNewRasklFileChooseButton"));
	
	private JButton m_undoButton = new JButton(getGuiStrs("vidminaButton"));
	
	protected JLabel m_vidLabel = new JLabel(getGuiStrs("vidminaRasklLabel"));
	
	protected WsPartTypesComboBox m_partsCombo = new WsPartTypesComboBox();
	
	private Vector<WsImportData> m_vec_raskl_data = null;
	
	XSSFWorkbook m_wb = null;
	
	XSSFSheet m_sheet = null;
	

	public  WsSkladMoveCompareZsuProd (JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		String[] columnNames = { getGuiStrs("excelImportSourceFileName") };
		
		m_table_control = new WsFileTableControl(columnNames, getGuiStrs("chooserExcelFileLabelName"), true);
		
		createGui();
		
		m_path_raskl.setText(raskl_path_static);	
		
		m_buttonGenerate.setEnabled(false);
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	generateReport();
            	
            }
            
		});
		
		
		m_saveExcelButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	if( !isReportEmpty() ) {
            	
            		exportToExcelFile(m_vec_all);
            	
            	}
            }
		});	
		
		m_buttonGenerate.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	try {
            		
					createModifiedRaskladka();
					
				} catch (FileNotFoundException e1) {

					e1.printStackTrace();
				}
            }
		});
		
		m_pathButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	onPath(e);
            }
		});
		
		 m_saveRasklButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	saveModifiedRaskladka(m_wb, m_sheet, m_vec_raskl_data );
	            }
		 });
		 
		 m_undoButton.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	undoKod();
	            }
		 });
	}

	private void createGui() {
		
		WsUtils.get().setFixedSizeBehavior(m_table_control);
		
		WsGuiTools.setComponentFixedHeight(m_table_control, 120);
		
		JPanel panel0 = WsGuiTools.createVerticalPanel();
		
		JPanel panel1 = WsGuiTools.createHorizontalPanel();
		
		panel1 .add(m_table_control);
		 
		panel1.add(Box.createHorizontalGlue());
		
		JPanel rask_panel = WsGuiTools.createHorizontalPanel();
		
		rask_panel.add( m_infoLabel);
		
		rask_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		rask_panel.add(m_path_raskl);
		
		rask_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		rask_panel.add(m_pathButton);
		
		rask_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		rask_panel.add(m_buttonGenerate);
		
		JPanel undo_panel = WsGuiTools.createHorizontalPanel();

		undo_panel.add(m_vidLabel);
		
		undo_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		undo_panel.add(m_partsCombo);
		
		undo_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		undo_panel.add(m_undoButton);
		
		undo_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		undo_panel.add(m_saveRasklButton);
		
		panel0.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel0.add(rask_panel);
		
		panel0.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel0.add(undo_panel);
		
		panel0.setBorder(BorderFactory.createTitledBorder(getGuiStrs("modRasklTitleBorder")));
		
		JPanel panel10 = WsGuiTools.createVerticalPanel();
	
		panel10.add(panel1);
		
		panel10.add(panel0);
		
		m_control_panel2.add(panel10);
	
		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		WsGuiTools.fixTextFieldSize(m_path_raskl);
		
		WsGuiTools.fixComponentHeightToMin(m_partsCombo);
		
		WsGuiTools.fixComponentHeightToMin(panel10);
		
		m_table_control.setTableToolTips(getGuiStrs("porivzVitZsuProdTableTooltip") );
		
		m_buttonGenerate.setToolTipText(getGuiStrs("genModRaskTooltip") );
		
	}
	
	//vector of report pages
	private Vector<String> generatePages() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int rows_number = m_vec_all.size();
		
		//divide into pages
		int rows_per_page = 25;
		
		int pages_number =  (int)(rows_number /rows_per_page);
		
		if((pages_number *rows_per_page) < rows_number ) { pages_number++;}
		
		Vector<String> vec_pages = new Vector<String>();
		
		int start_row = 0;
		
		int end_row = rows_per_page - 1;
		
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
		
		m_html_pages = vec_pages;
		
		setText(m_html_pages.elementAt(0));
     	
     	pagesNum = m_html_pages.size();
     	
     	currentPage = 1;
     	
     	setDialogCaption();
     	
     	m_viewer.setSelectionStart(0);
     	
     	m_viewer.setSelectionEnd(0);
	
		return vec_pages;
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsSkladMoveDataColumn> vec_all = 
				WsReportsSqlStatements.getPrihodRashodBookForDate2(m_date.getSqlStartDate(), m_date.getSqlEndDate(), -1);
		
		HashMap<Integer, WsSkladMoveDataColumn> foreign_data = importExcel();
		
		//merge
		for(WsSkladMoveDataColumn d : vec_all) {
			
			WsSkladMoveDataColumn df = foreign_data.get(d.kod);
			
			if(df == null) {
				
				foreign_data.put(d.kod, d);
			}
			else {
				
				df.in_quantity += d.in_quantity;
				
				df.out_quantity += d.out_quantity;
				
				df.rest += d.rest;
				
				df.initial_rest += d.initial_rest;
				
			}
		}

		ArrayList<Integer> list = new ArrayList<Integer>(foreign_data.keySet()); 
		
		Collections.sort(list);
		
		vec_all.clear();
		
		for(Integer kod: list) {
			
			 WsSkladMoveDataColumn d = foreign_data.get(kod);
			 
			 vec_all.add(d);
			
		}
		
		vec_all = removeZerosRows(vec_all);
		
		m_vec_all =  vec_all;
		
		Vector<String> vec_pages = generatePages();
		
		m_buttonGenerate.setEnabled(true);
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
		
	}
	
	public void createModifiedRaskladka() throws FileNotFoundException {
		
		
		
		String file_base_rakladka = m_path_raskl.getText();
		
		if(file_base_rakladka.isEmpty()) {
			
		   JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("cantRaskFileForModMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		WFParseIndicies schema =  new WFParseIndicies(TYPE.RASKLADKA);
		
		FileInputStream fStream = null;
		
		try {
			
			fStream = new FileInputStream(file_base_rakladka);
	
		    try {
		    	
		    		if(m_wb != null) { m_wb.close(); }
			
					m_wb = new XSSFWorkbook( fStream );
				
					fStream.close();
		    
		    } catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
		    	
		    	m_wb.close();
		    	
		    	fStream.close();
		    	
		    	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		    	
		    	return ;
		    }
	
			Vector<WsImportData> vec_raskl_data =  WSExcelImport.getFullDataFromRaskladka( m_wb, schema);
			
			int sheetIndex = schema.sheetIndex; //3
			   
			m_sheet = m_wb.getSheetAt(sheetIndex);
			
			Vector<WsSkladMoveDataColumn> vec_all = m_vec_all;
			
			for(int i = 0; i < vec_all.size(); ++i) {
				
				WsSkladMoveDataColumn d = vec_all.elementAt(i);
							
				if(  WsUtils.isKodEqual(d.kod,  WsUtils.WATER_KOD) ) { continue; }
				
				double diff = d.rest_1 - d.rest;
				
				//small diff is no worse to process, the big diff means the 'peresort'
				if(Math.abs(diff) < 0.1 || (Math.abs(diff) > 300.0 && 
						!WsUtils.isKodEqual(d.kod, WsUtils.BREAD_KOD_1) && 
						!WsUtils.isKodEqual(d.kod, WsUtils.BREAD_KOD_2) && 
						!WsUtils.isKodEqual(d.kod, WsUtils.BREAD_KOD_3)) ) {  continue; }
				
				
					
				int kod = d.kod;
				
				WsImportData fd   = findData(kod, vec_raskl_data);
				
				if(fd == null || fd.m_data.isEmpty()) { continue;}
				
				Vector<Double> vec_coeffs =  getCoeffs(diff,  fd);
				
				double step = 1.0;
				
				if(WsUtils.isKodEqual(kod , WsUtils.EGG_KOD_1) || WsUtils.isKodEqual(kod , WsUtils.EGG_KOD_2)) {
					

					step =  0.25;
				}
				else if(fd.m_data.elementAt(0).quantity > 0.999) {
					
					 step = 1.0;
				}
				else { //values < 1 gr
					
					step = 0.1;
				}
				
				setDifference2(diff, vec_coeffs, fd, step);
			}
			
			 m_vec_raskl_data =  vec_raskl_data;
			 
			 changeReportData(m_vec_raskl_data);
			 
           	 m_html_pages = generatePages();
            	
			 setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			 
		} catch (IOException e1) {
			
			e1.printStackTrace();
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			return;
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	private void saveModifiedRaskladka(XSSFWorkbook wb, XSSFSheet sheet, 
			Vector<WsImportData> vec_raskl_data ) {
		
		if(null == vec_raskl_data) {
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("noModRasklForSaveMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			   
			   return;
			
			
		}
		
		String target_rakladka =  excelRasklSaveFileChoose(this);
		
		if(null == target_rakladka) return;
		
		for(int j = 0; j <  vec_raskl_data.size(); ++j) {
			
			WsImportData d = vec_raskl_data.elementAt(j);
			
			Vector<WFRowData >  kod_rows_data =  d.m_data;
			
			for(int i = 0; i <  kod_rows_data.size(); ++i) {
				
				WFRowData  dt = kod_rows_data.elementAt(i);
				
				if(Math.abs(dt.delta) > 0.0) {
				
					 XSSFRow r = sheet.getRow(dt.row_index);
		   		 
					 XSSFCell c = r.getCell( d.column_index);
		       	 
					 double new_value = dt.quantity + dt.delta;
					 
					 if(new_value < 0.0) { new_value = 0.0; }
		       		 
					 c.setCellValue(new_value);
				 
				}
				
			}
		
		}
		
		OutputStream out;
		 
		try {
			
			out = new FileOutputStream(target_rakladka);
		
			wb.write(out);
		
			out.close();
			 
		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	private Vector<Double> getCoeffs(double global_diff, WsImportData fd) {
		
		Vector<Double> coeffs = new Vector<Double>();
		
		double sum = 0.0;
		
		for(int l = 0; l < fd.m_data.size(); ++l) {
			
			double cf = Math.exp((fd.m_data.elementAt(l).quantity/1000.0));
			
			sum += cf;
			
			coeffs.add(cf);
			
		}
	
		for(int l = 0; l < fd.m_data.size(); ++l) {
			
			double cf = coeffs.elementAt(l)/sum;
			
			coeffs.set(l, cf);

		}
		
		return coeffs;
	
	}
	
	private void setDifference2(double diff, Vector<Double> vec_coeffs, 
			WsImportData fd,  double step) {
		
		double rest = 0.0;
		
		double units_mult = 1000.0;

		if(step == 0.25) { units_mult = 1.0; }
		
		for(int i = 0; i < vec_coeffs.size(); ++i) {
			
			double delta = vec_coeffs.elementAt(i)*diff*units_mult/fd.m_data.elementAt(i).people;
			
			double rounded_delta = ((int)( delta/step)) * step;
			
	
			fd.m_data.elementAt(i).delta = rounded_delta;
			
			rest += delta - rounded_delta;
			
			fd.m_data.elementAt(i).old_quantity = fd.m_data.elementAt(i).quantity;
			
		}
		
		double rest_abs = Math.abs(rest);
		//the last gramm
		if(rest_abs >= step) {
			
			double d = step;
			
			if(rest < 0.0) { d = - step; }
			
			for(int i1 = 0; i1 < Math.round(rest_abs/step); ++i1) {
		
				Random random = new Random();
				
				int index = random.nextInt(fd.m_data.size() - 0) + 0;
				 

				fd.m_data.elementAt(index).delta +=  d;
			
			}
						
		}
				
	}
	

	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {
		
		
		String date_s = WsUtils.dateToString(m_date.getStartDate(), "dd-MMMM-yyyy" );
		
		String date_e = WsUtils.dateToString(m_date.getEndDate(), "dd-MMMM-yyyy" );
		
		StringBuilder sHeader_b = new StringBuilder();
		
		sHeader_b.append("<tr><td  colspan='2' style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>" + getGuiStrs("reportBookKodGoodColumn") + "</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("nameNameInReport") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" +  getGuiStrs("naPochatReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("naPochatProdReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("pribuloReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("pribuloProdReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append( "<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" +  getGuiStrs("vibuloReportName") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("vibuloReportName") + " Prod" + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restNameInReport") + "&nbsp;</font></td>");
		
		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; '>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restNameInReport") + " Prod" + "&nbsp;</font></td>");

		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center;  ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("restDiffInReport")  + "&nbsp;</font></td>");
		

		sHeader_b.append("<td   style='border-left: 1px solid;border-top: 1px solid ;text-align: center; border-right: 1px solid ;'>");
		
		sHeader_b.append("<font size =4>&nbsp;" + getGuiStrs("correctionDiffInReport") + "&nbsp;</font></td></tr>");
		
		StringBuilder  row_s_b = new StringBuilder();
		
		for(int i = start; i <= end; ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
			
			 String bottomBorder = "";
			 
			 if(i == end) {
				 
				 bottomBorder = "border-bottom: 1px solid ;"; 
			 }
			 
			row_s_b.append("<tr><td style='border-left: 1px solid;border-top: 1px solid ; ");
			
			row_s_b.append(bottomBorder + "'><font size =4>&nbsp;" );
			
			row_s_b.append(String.valueOf(i + 1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4> &nbsp;" );
			
			row_s_b.append(String.valueOf(d.kod) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; text-overflow:ellipsis; overflow: hidden; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(d.name );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.initial_rest) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;"); 
			
			row_s_b.append(WsUtils.getDF(d.initial_rest_1)); 
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.in_quantity) + "&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.in_quantity_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.out_quantity) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ;  " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.out_quantity_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; " );
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest_1) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid  ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.rest_1 - d.rest) );
			
			row_s_b.append("&nbsp;</font></td>");
			
			row_s_b.append("<td nowrap style=' max-width: 250px; border-left: 1px solid; border-top: 1px solid ; border-right: 1px solid ; "); 
			
			row_s_b.append(bottomBorder );
			
			row_s_b.append("'><font size =4>&nbsp;" );
			
			row_s_b.append(WsUtils.getDF(d.correction) );
			
			row_s_b.append("&nbsp;</font></td></tr>");
		
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
		
		hS_b.append("<h2 align='center' ><font size =5>");
		
		hS_b.append( getGuiStrs("bookSkladMovementCompareReportName") );
		
		hS_b.append(" " );
		
		hS_b.append( date_s);
		
		hS_b.append(" ");
		
		hS_b.append(getGuiStrs("bookSkladPoReportName") );
		
		hS_b.append(" ");
		
		hS_b.append(date_e ); 
		
		hS_b.append("</font></h2>");
		
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
		
		int initial_rest_column = 4;
		
		int in_quantity_column = 6;
		
		int out_quantity_column = 8;
		
		int rest_column = 10;
		
		int sheet_index = 0;
		
		HashMap<Integer, WsSkladMoveDataColumn> map = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		String excel_file_name = null;
		
		Vector<WsAgentData> vec = m_table_control.getData();
		
		 WsCatalogKods ct = new  WsCatalogKods();
		 
	    for(WsAgentData dfc : vec){
	    	
	    	String fc = dfc.contact;
				  
			try {

				excel_file_name = fc;
				
				if(excel_file_name == null || excel_file_name.isEmpty()) {   continue; }
						
				FileInputStream fStream = new FileInputStream(excel_file_name);
					    
				XSSFWorkbook wb = null;
				
				try {
					
					wb = new XSSFWorkbook( fStream );
					
				} catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
					
					if(wb != null) { wb.close(); }
					
					return map;
				}
				
				fStream.close();
					    
			    XSSFSheet sheet = null;
			    
				try {
				    
					sheet = wb.getSheetAt(sheet_index);
			    
				}  catch(java.lang.IllegalArgumentException ex) { 
					
					wb.close();
					
					return map; 
					
				}
					    
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
		        	
		        	//d.kod = WsImportExcelUtil.getKodCell(row, kod_column);
		        	
		        	d.kod = ct.getKodFromCatalog(WsImportExcelUtil.getKodCell(row, kod_column));
	        		
	        		//if(d.kod == -1) { continue;}
	        		
	        		d.initial_rest_1 = WsImportExcelUtil.getDoubleCell(row, initial_rest_column);
	        		
	        		d.in_quantity_1 = WsImportExcelUtil.getDoubleCell(row, in_quantity_column);
	        		
	        		d.out_quantity_1 = WsImportExcelUtil.getDoubleCell(row, out_quantity_column);
	        		
	        		d.rest_1 = WsImportExcelUtil.getDoubleCell(row, rest_column);
	        		
	        		d.name = WsImportExcelUtil.getStringCell(row,  name_column);
	        		
	        		WsSkladMoveDataColumn d_f = map.get(d.kod);
	        		
	        		if(d_f == null) {
	        			
	        			map.put(d.kod, d);
	        		}
	        		else {
	        			
	        			d_f.in_quantity_1 += d.in_quantity_1;
	        			
	        			d_f.out_quantity_1 += d.out_quantity_1;
	        			
	        			d_f.initial_rest_1 += d.initial_rest_1;
	        			
	        			d_f.rest_1 += d.rest_1;
	        		}  
		        		
			    }
					    
			    wb.close();
					    
			} catch(Exception  ioe) {
							
				ioe.printStackTrace();
						    
			}
	    }
					
		return map;
		
	}
	
	public void exportToExcelFile(Vector<WsSkladMoveDataColumn>  vec) {
		
	
		Vector<WsSkladMoveDataColumn>  vec_all_parts = removeZerosRows(vec);
		
		String file_to_save =  excelSaveFileChoose(this);
		
		if(null == file_to_save) return;
	
		OutputStream out;
		
		try {
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			
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
	        	
                XSSFCell cell02 = row.createCell(cell_index++);
                
                cell02.setCellValue(dt.name);
                
                XSSFCell cell03 = row.createCell(cell_index++);
                
                cell03.setCellValue(WsUtils. getDF_0(dt.initial_rest));
                
                XSSFCell cell03_1 = row.createCell(cell_index++);
                
                cell03_1.setCellValue(WsUtils. getDF_0(dt.initial_rest_1));
                
                XSSFCell cell04 = row.createCell(cell_index++);
                
                cell04.setCellValue(WsUtils. getDF_0(dt.in_quantity));
                
                XSSFCell cell04_1 = row.createCell(cell_index++);
                
                cell04_1.setCellValue(WsUtils. getDF_0(dt.in_quantity_1));
                
                XSSFCell cell05 = row.createCell(cell_index++);
                
                cell05.setCellValue(WsUtils. getDF_0(dt.out_quantity));
                
                XSSFCell cell05_1 = row.createCell(cell_index++);
                
                cell05_1.setCellValue(WsUtils. getDF_0(dt.out_quantity_1));
                
                XSSFCell cell06 = row.createCell(cell_index++);
                
                cell06.setCellValue(WsUtils. getDF_0(dt.rest));
                
                XSSFCell cell06_1 = row.createCell(cell_index++);
                
                cell06_1.setCellValue(WsUtils. getDF_0(dt.rest_1));
	                
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
		   
		   createCell(rowHeader0, 0, getGuiStrs("kodNameInReport"), creationHelper);
	
		   createCell(rowHeader0, 1, getGuiStrs("nameNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 2, getGuiStrs("naPochatReportName"), creationHelper);
		   
		   createCell(rowHeader0, 3, getGuiStrs("naPochatProdReportName"), creationHelper);
		   
		   createCell(rowHeader0, 4, getGuiStrs("pribuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 5, getGuiStrs("pribuloProdReportName"), creationHelper);
		   
		   createCell(rowHeader0, 6, getGuiStrs("vibuloReportName"), creationHelper);
		   
		   createCell(rowHeader0, 7, getGuiStrs("vibuloReportName") + " Prod", creationHelper);
		   
		   createCell(rowHeader0, 8, getGuiStrs("restNameInReport"), creationHelper);
		   
		   createCell(rowHeader0, 9, getGuiStrs("restNameInReport") + " Prod", creationHelper);
		   
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
	
	public void onPath(ActionEvent e) {
		

		WsFileChooserDialog sourceFile = new WsFileChooserDialog(getGuiStrs("dialogFileChooserTitleDataLoad"), 
				".", true, false);

		int result = sourceFile.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			if(sourceFile.getSelectedFile() != null) {
				
				String name = sourceFile.getSelectedFile().getPath();
			
				m_path_raskl.setText(name);	
				
				raskl_path_static = name;

			}
		}
	}
	
	
	private WsImportData findData(int kod, Vector<WsImportData> vec_raskl_data) {
		
		WsImportData fd  = null;
		
		for(int k = 0; k < vec_raskl_data.size(); ++k) {
			
			if( WsUtils.isKodEqual(vec_raskl_data.elementAt(k).kod , kod)) {
				
				fd = vec_raskl_data.elementAt(k);
				
				break;
			}
		}
		
		return fd;
		
	}
	
	private void undoKod() {
		
		WsPartType d1 = m_partsCombo.getSelectedPartData();
    	
    	if(null == d1) { return; }
		
		int  kod =  d1.kod;
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		for(int i = 0; i < m_vec_raskl_data.size(); ++i) {
			
			WsImportData d = m_vec_raskl_data.elementAt(i);
			
			if(WsUtils.isKodEqual(d.kod, kod)) {
				
				Vector<WFRowData>  v = d.m_data;
				
				for(int j = 0; j < v.size(); ++j) {
					
					WFRowData dr = v.elementAt(j);
					
					dr.delta = 0;
					
				}

			}
		}
		
		changeReportData(m_vec_raskl_data);
		
		generatePages();
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}
	
	
	private void changeReportData(Vector<WsImportData> vec_raskl_data) {
		
		for(int i = 0; i < m_vec_all.size(); ++i) {
			
			WsSkladMoveDataColumn d = m_vec_all.elementAt(i);
						
			if( WsUtils.isKodEqual(d.kod, WsUtils.WATER_KOD) ) { continue; }
			
			
				
			int kod = d.kod;
			
			WsImportData fd  = findData(kod, vec_raskl_data);
			
			if(fd == null || fd.m_data.isEmpty()) { continue;}
			
			double sum = 0.0;
			
			for(int j1 = 0 ; j1 < fd.m_data.size(); ++j1) {
				
				WFRowData  dr = fd.m_data.elementAt(j1);
				
				sum += ( dr.delta)*dr.people;
			}
			
			d.correction = sum/1000.0;
			
		}
		
		
	}
	
	public void dispose() {
		
		try {
			
			if(m_wb != null)  { m_wb.close(); }
			
		} catch (IOException e) {
	
			e.printStackTrace();
		}
		
		super.dispose();

	}
}
