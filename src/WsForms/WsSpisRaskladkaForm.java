
package WsForms;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.Ws2DatesControl;
import WsControls.WsFileTableControl2;
import WsControls.WsMutableString;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsKodComparator;
import WsDataStruct.WsPair;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsReportsSqlStatements;
import WsDialogs.WsImportRestExcelDialog;
import WsEditTables.WsMovePartsEditTable;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsImport.WFParseIndicies;
import WsImport.WFParseIndicies.TYPE;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


public class WsSpisRaskladkaForm extends JPanel {
	
	private static final long serialVersionUID = 1L;

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.ENABLE_EVENT, this, "refreshData");
		
		WsEventDispatcher.get().addConnect(WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED, this, "refreshDataAndSelect");
		
	}
	
	Ws2DatesControl m_dates = new Ws2DatesControl(false);
	
	protected  JButton m_restLoadButton = new JButton(getGuiStrs("buttonLoadRestProdCaption"));
	
	protected  JButton m_restSaveButton = new JButton(getGuiStrs("buttonSaveRestProdCaption"));
	
	String[] columnNames = { getGuiStrs("importRaskladkaFileName") 
			/*,getGuiStrs("importQuantityRasklakaName")*/};
	
	WsFileTableControl2 m_raskl_control_table = new WsFileTableControl2(columnNames, getGuiStrs("chooserRaskladkaFileLabelName"), true);
	
	protected JTextField m_path_raskl = new JTextField(25);
	
	protected JLabel m_infoLabel = new JLabel(getGuiStrs("rasklFoMOdLabel"));
	
	private JButton m_buttonLoadOut = new JButton(getGuiStrs("ldOutRasklButton"));
	
	private JButton m_buttonGenerate = new JButton(getGuiStrs("generateNewRasklButton"));

	private JButton m_saveRasklButton = new JButton(getGuiStrs("saveNewRasklFileChooseButton"));
	
	protected WsMovePartsEditTable m_table = new WsMovePartsEditTable();
	
	XSSFWorkbook m_wb = null;
	
	XSSFSheet m_sheet = null;
	
	private Vector<WsImportData> m_vec_raskl_data = null;
	
	protected static  WsMutableString m_excel_save_folder = new  WsMutableString(".");
   
	public WsSpisRaskladkaForm() {
		
		createGUI();
		
		m_dates.setSqlStartDate( WsUtils.sqlDatePlusMonth(Calendar.getInstance().getTime(), -1)  );
		
		m_dates.setSqlEndDate( WsUtils.sqlDatePlusDays(Calendar.getInstance().getTime(), 6)  );
		
		setPopupMenu();
		
		setListeners();
		
		setGuiEnabled(false);
		
	}
	
	private void setGuiEnabled(boolean flag) {
	

	}
	
	private void createGUI() {
		

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel date_panel = WsGuiTools.createHorizontalPanel();
		
		date_panel.add(m_dates);
		
		date_panel.add(Box.createHorizontalGlue());
		
		date_panel.add(m_restLoadButton);
		
		date_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		date_panel.add(m_buttonLoadOut);
		
		date_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		date_panel.add(m_buttonGenerate);
		
		date_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		date_panel.add(m_saveRasklButton);
		
		date_panel.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		date_panel.add(m_restSaveButton);
			
		Dimension sizeD = m_dates.getPreferredSize();
		
		sizeD.width = 350;
		
		m_dates.setMaximumSize(sizeD);
		
        JScrollPane scroll = new JScrollPane(m_table);
        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
    	JPanel top_panel = WsGuiTools.createVerticalPanel();
		
    	 top_panel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
    	
		 top_panel.add(date_panel);
		 
		 top_panel.add(m_raskl_control_table);
		 
		 JPanel scroll_panel = WsGuiTools.createVerticalPanel();
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				 top_panel, scroll_panel);
		
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setDividerLocation(250);
		
		scroll_panel.add(scroll);

		add(splitPane);

		setToolTips();
	}
	
	public void refreshData(WsEventEnable e) {
		
		if(e == null || e.getType() == WsEventEnable.TYPE.DATABASE_LOADED 
				|| e.getType() == WsEventEnable.TYPE.INVOICE_HAS_BEEN_CHANGED) {
			
	
			if(m_dates.getStartDate().compareTo(m_dates.getEndDate()) > 0) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("dateRangeIsInvalidMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				
			}
	
		}	
	}
	
	public void refreshDataAndSelect(WsPrihodInvoiceChangedEvent e) {
		
		if(e != null && e.getEventType() == WsEventDispatcher.INVOICE_HAS_BEEN_CHANGED) {
			
			if(m_dates.getStartDate().compareTo(m_dates.getEndDate()) > 0) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("dateRangeIsInvalidMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				
			}
	
		}
	}
	
	class ItemChangeListener implements ItemListener{
		
	    @Override
	    public void itemStateChanged(ItemEvent event) {
	    	
	       if (event.getStateChange() == ItemEvent.SELECTED) {
	    	   
	 
	  
	       }
	    }   
	}
	
	private void setPopupMenu() {
		
	        
	}
	
	private void setToolTips() {
		
		m_restLoadButton.setToolTipText(getMessagesStrs("loadZalSpisToolTip"));
		
		m_restSaveButton.setToolTipText(getMessagesStrs("saveZalSpisToolTip")); 
		
		m_buttonLoadOut.setToolTipText(getMessagesStrs("loadOutToolTip"));
		
		m_buttonGenerate.setToolTipText(getMessagesStrs("genRozSpisToolTip"));
		
		m_saveRasklButton.setToolTipText(getMessagesStrs("saveRozSpisToolTip"));
				
	}
	
	private void setListeners() {
		
		  m_restLoadButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	importRestFromExcel();
            }
		  });
		  
		  
		  m_buttonLoadOut.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	importOutFromRasklAndSklad();
	            }
		});

		m_buttonGenerate.addActionListener(new ActionListener() {
				
	            public void actionPerformed(ActionEvent e) {
	            	
	            	Vector<WsAgentData> rakl_vec = m_raskl_control_table.getData();
	            	
	            	createModifiedRaskladka( rakl_vec.elementAt( rakl_vec.size() -1).contact);
	            	
	            	
	            }
		});
		
		m_saveRasklButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	saveModifiedRaskladka(m_wb, m_sheet,m_vec_raskl_data );
            }
		}); 
		
		m_restSaveButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	saveNewRest();
            }
		});
		
		
		  
	}
	
	private void importRestFromExcel() {
		
		WsImportRestExcelDialog d = new WsImportRestExcelDialog(WsUtils.get().getMainWindow(),
				this, getMessagesStrs("loadInitRestProd"));
		
		d.setVisible(true);

	}
	
	public void setTableData(Vector< WsSkladMoveDataColumn> vec) {
		
		m_table.setDataVector(vec);
		
	}
	
	public void fillTableWithZeros() {
	
		Vector<WsSkladMoveDataColumn> table_vec = m_table.getData();
		
		for(WsSkladMoveDataColumn d: table_vec) {
			
			
			d.q_array[0].in_quantity = 0.0;
			
			d.q_array[0].out_quantity = 0.0;
			
			d.q_array[0].rest = 0.0;
			
			d.q_array[0].initial_rest = 0.0;
			
			d.q_array[1].in_quantity = 0.0;
			
			d.q_array[1].out_quantity = 0.0;
			
			d.q_array[1].rest = 0.0;
			
			d.correction = 0.0;
			
			d.correctionToDo = 0.0;
			
		}
	
	}
	
	public void loadImportRestFromSklad () {
		
		Vector<WsSkladMoveDataColumn> vec_sklad = 
				WsReportsSqlStatements.getPrihodRashodBookForDate2(m_dates.getSqlStartDate(), m_dates.getSqlEndDate(), -1);
		
		Vector<WsSkladMoveDataColumn> vec_tmp = new Vector<WsSkladMoveDataColumn>(); 
		
		for(WsSkladMoveDataColumn d: vec_sklad) {
			
			WsSkladMoveDataColumn d_ins = new WsSkladMoveDataColumn();
			
			if(d.initial_rest > WsUtils.getRZL() ) {
				
				d_ins.q_array[1].initial_rest = d.initial_rest;
				
				d_ins.kod = d.kod;
				
				d_ins.name = d.name;
				
				vec_tmp.add(d_ins);
			
			}
			
		}
		
		
		setTableData(vec_tmp);
		
	}
	
	
	
	
	public void importOutFromRasklAndSklad() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		fillTableWithZeros();
		
		WsPair p = WSExcelImport.getDataFromRaskladkaSet(m_raskl_control_table.getData(), false, true);

		@SuppressWarnings("unchecked")
		HashMap<Integer, WsSkladMoveDataColumn> foreign_data = (HashMap<Integer, WsSkladMoveDataColumn>)p.complex;

		Vector<WsSkladMoveDataColumn> vec_tmp = 
				WsReportsSqlStatements.getPrihodRashodBookForDate2(m_dates.getSqlStartDate(), m_dates.getSqlEndDate(), -1);
		
		Vector<WsSkladMoveDataColumn> vec_sklad = new Vector<WsSkladMoveDataColumn>();
		
		double thr = WsUtils.getRZL();
		
		for(int i = 0; i < vec_tmp.size(); ++i) {
			
			WsSkladMoveDataColumn d = vec_tmp.elementAt(i);
			
			if(d.in_quantity > thr || d.out_quantity > thr || d.rest > thr
					|| d.initial_rest > thr) {
				
				vec_sklad.add(d);
				
			}
		}
		
		vec_tmp.clear();
	
		Vector<WsSkladMoveDataColumn> table_vec = m_table.getData();
		
		//merge
		for(WsSkladMoveDataColumn d : vec_sklad) {
			
			boolean found = false;
			
			WsSkladMoveDataColumn d_found  = null;
			
			for(WsSkladMoveDataColumn df : table_vec) {
				
				if(d.kod == df.kod) {
					
					found = true;
					
					d_found = df;
					
					break;
				}
			}
			if(found) {
				
				d_found.q_array[0].initial_rest  += d.initial_rest;
				
				d_found.q_array[0].in_quantity  += d.in_quantity;
				
				d_found.q_array[0].out_quantity  += d.out_quantity;
				
				d_found.q_array[0].rest  += d.rest;
				
				d_found.q_array[1].rest  = d_found.q_array[1].initial_rest + d_found.q_array[0].in_quantity - d_found.q_array[1].out_quantity;
				
			}
			else {
				
				WsSkladMoveDataColumn d_new = new WsSkladMoveDataColumn(d);
				
				d_new.q_array[1].rest  = d_new.q_array[1].initial_rest + d_new.q_array[0].in_quantity - d_new.q_array[1].out_quantity;
				
				table_vec.add(d_new);
				
			}
		}
		
		for (Map.Entry<Integer, WsSkladMoveDataColumn> set : foreign_data.entrySet()) {
			
			int kod = set.getKey();
			
			WsSkladMoveDataColumn d = set.getValue();
			
			boolean found = false;
			
			WsSkladMoveDataColumn d_found  = null;
			
			for(WsSkladMoveDataColumn df : table_vec) {
				
				if(kod == df.kod) {
					
					found = true;
					
					d_found = df;
					
					break;
				}
			}
			if(found) {
				
				d_found.q_array[1].out_quantity  += d.q_array[1].out_quantity;
				
				d_found.q_array[1].rest  = d_found.q_array[1].initial_rest + d_found.q_array[0].in_quantity - d_found.q_array[1].out_quantity;
				
				
			}
			else {
				
				WsSkladMoveDataColumn d_new = new WsSkladMoveDataColumn(d);
				
				d_new.kod = kod;
				
				d_new.q_array[1].out_quantity  = d.q_array[1].out_quantity;
				
				d_new.q_array[1].rest  = d_new.q_array[1].initial_rest + d_new.q_array[0].in_quantity - d_new.q_array[1].out_quantity;
				
							
				table_vec.add(d_new);
				
			}
			
		}
	
		Collections.sort(table_vec, new WsKodComparator());
	
		m_table.refresh();
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	
	public void createModifiedRaskladka(String file_base_rakladka)  {
		
		if(file_base_rakladka.isEmpty()) {
			
		   JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("cantRaskFileForModMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		WFParseIndicies schema = new WFParseIndicies(TYPE.RASKLADKA);
		
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
						
			Vector<WsSkladMoveDataColumn> vec_all = m_table.getData();
			
			for(int i = 0; i < vec_all.size(); ++i) {
				
				WsSkladMoveDataColumn d = vec_all.elementAt(i);
							
				if(  WsUtils.isKodEqual(d.kod,  WsUtils.WATER_KOD) ) { continue; }
				
				double diff = d.q_array[1].rest - d.q_array[0].rest;
				
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
			 
			 fillCorrectionData(m_vec_raskl_data);
			 
			 setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			 
		} 
		catch (java.io.FileNotFoundException e1) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("cantLoadRaskFileForModMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			e1.printStackTrace();
			
			return;
		}
		catch (IOException e1) {
		
			e1.printStackTrace();
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			return;
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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
	
	
	private void fillCorrectionData(Vector<WsImportData> vec_raskl_data) {
		
		Vector<WsSkladMoveDataColumn> vec_all = m_table.getData();
		
		for(int i = 0; i < vec_all.size(); ++i) {
			
			WsSkladMoveDataColumn d = vec_all.elementAt(i);
						
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
			
			d.correctionToDo = d.correction;
			
		}
		
		m_table.refresh();
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
		
		String target_rakladka = WsUtils.get().excelRasklSaveFileChoose(this,  m_excel_save_folder);
		
		if(null == target_rakladka) return;
		
		changeDeltas(  vec_raskl_data, m_table.getData());
		
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
	
	
	private void changeDeltas( Vector<WsImportData> vec_raskl_data, Vector<WsSkladMoveDataColumn> vec_data) {
		
		Vector<Integer> vec_ch = new Vector<Integer>();
		
		Vector<Double> vec_diff = new Vector<Double>();
		
		for(int i = 0; i < vec_data.size(); ++i) {
		
			WsSkladMoveDataColumn d = vec_data.elementAt(i);
			
			if(Math.abs(d.correction - d.correctionToDo) > 0.00001) {
				
				vec_ch.add(d.kod);
				
				vec_diff.add(d.correctionToDo);
			}
				
		}
		
		if(vec_ch.isEmpty()) { return; }
		
		for(int i = 0; i < vec_ch.size(); ++i) {
			
			int kod = vec_ch.elementAt(i).intValue();
			
			double diff = vec_diff.elementAt(i);
			
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
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}
	
	private void saveNewRest( ) {
		
		Vector<WsSkladMoveDataColumn> vec = m_table.getData();
		
		if(vec.isEmpty()) {
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("noDataForSaveMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			   
			   return;
		}
		
		int res = WsUtils.showYesNoDialog( getMessagesStrs("saveNewResSpisMessage"));
		
		if(res == 0) { return; }
		
		String file_to_save = WsUtils.get().excelRasklSaveFileChoose(this,  m_excel_save_folder);
		
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
			
		    XSSFSheet sheet = (XSSFSheet) wb.createSheet();
		    
		    XSSFRow row = sheet.createRow(0);
		    
		    XSSFCell cell0 = row.createCell(0);
		    
		    cell0.setCellValue(getGuiStrs("kodColumnName"));
		    
		    cell0 = row.createCell(1);
		    
		    cell0.setCellValue(getGuiStrs("nameColumnReportGoodName"));
		    
		    cell0 = row.createCell(2); cell0 = row.createCell(3); cell0 = row.createCell(4);
		    
		    cell0 = row.createCell(5);
		    
		    cell0.setCellValue(getGuiStrs("prihodPartsColumnRestName"));
		    
		    int row_index = 1;
		
	    	for (WsSkladMoveDataColumn dt: vec) {
	
	            row = sheet.createRow(row_index++);
	            
	            int cell_index = 0;
	            
	            XSSFCell cell01 = row.createCell(cell_index++);
	            
	            cell01.setCellValue(dt.kod);
	        	
                XSSFCell cell02 = row.createCell(cell_index++);
                
                cell02.setCellValue(dt.name);
                
                cell02 = row.createCell(cell_index++);

                cell02 = row.createCell(cell_index++);
                
                cell02 = row.createCell(cell_index++);
                
                XSSFCell cell5 = row.createCell(cell_index++);
                
                cell5.setCellValue(dt.q_array[1].rest);
   
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

}