
package WsReports;


import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import WsControls.WsFileTableControl3;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsSkladMoveDataColumn;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


public class  WsOrderJsonGeneratorReport  extends WSReportViewer {

	private static final long serialVersionUID = 1L;
	
	Vector<WsSkladMoveDataColumn> m_vec_all = null;
	
	WsFileTableControl3 m_rasklaka_control = null;
	
	/**
	 * @param f
	 * @param nameFrame
	 */
	public WsOrderJsonGeneratorReport(JFrame f, String nameFrame) {
		super(f, nameFrame);
		
		String[] columnNames2 = { getGuiStrs("excelImportPrEdaName"),		
			getGuiStrs("labelMondayPeopleColumnCaption"),	
			getGuiStrs("labelTuesdayColumnCaption"),			
			getGuiStrs("labelWednesdayColumnCaption"),			
			getGuiStrs("labelThursdayColumnCaption"),			
			getGuiStrs("labelFridayColumnCaption"),		
			getGuiStrs("labelSaturdayCaption"),			
			getGuiStrs("labelSundayCaption")  };
		
		m_rasklaka_control = new WsFileTableControl3(columnNames2, 
				getGuiStrs("movementZvitCompareRasklDialogWinCaption"), false);
		
		m_rasklaka_control.setParentDialog(this);
		
		createGui();
		
		m_genButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	m_html_pages = generateReport();
            	
            	if(null == m_html_pages) { return; }
            	
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
            	
            		saveToJsonFile();
            	
            	}          
            }
	    });

		m_viewer.setContentType("text/plain");
	}

	private void createGui() {
		
		m_saveExcelButton.setText("Json");
		
		m_saveButton.setVisible(false);
		
		m_date.setVisible(false);
		 
		m_rasklaka_control.setFirstColumnMinWidth(150);
		
		WsGuiTools.setComponentFixedHeight(m_rasklaka_control, 120);
		
		m_control_panel2.add(m_rasklaka_control);

		m_date.setCurrentStartDate();
		
		m_date.setCurrentEndDate();
		
		m_rasklaka_control.setTableToolTips(getMessagesStrs("jsonFileTableToolTip"));
		
	}
	
	//vector of report pages
	public Vector<String> generateReport() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		Vector<WsImportData>  data  = m_rasklaka_control.getImportData();
		
		if(data  == null) {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("noDataForMenuRasklForExportMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return null;
		}
				
		Vector<WsAgentData> p_data  = m_rasklaka_control.getData();
		
		Vector<String> vec_pages = new Vector<String>();
		
		HashMap<Integer, Double> main_map = new HashMap<Integer, Double>();
		
		//the size of data_import == 7
		for(int i = 0; i < 7; ++i) {
			
			WsImportData di = data.elementAt(i);
			
			Vector<WFRowData > v =  di.m_data;
			
			int people_quantity = p_data.elementAt(0).quantity[i];
			
			for(WFRowData dr : v) {
				
				int kod = dr.kod;
				
				double q = dr.quantity * people_quantity;
				
				if(main_map.keySet().contains(kod)) {
					
					double vl =  main_map.get(kod);
					
					main_map.put(kod, vl + q);
				}
				else {
		
					main_map.put(kod, q);
				}
			}
		}
		
		
		ArrayList<Integer> list = new ArrayList<Integer>( main_map.keySet()); 
			
		Collections.sort(list);
		  
		StringBuilder s = new StringBuilder(500);
		  
		s.append("{\n");
		  
		s.append("  ''version_programm'': ''1.0.0.1'',\n");
		  
		s.append("  ''version_catalog'': ''2024-07-01'',\n");
		  
		s.append("  ''type_id'': 1,\n");
		  
		s.append("  ''started_at'': ''2024-08-05'',\n");
		
		s.append("  ''ended_at'': ''2024-08-11'',\n");
		  
		s.append("  ''territorial_unit_code'': ''-'',\n");
		  
		s.append("  ''notes'': ''валідний'',\n");
		  
	    s.append("  ''transport_weight_id'': 5,\n");
		  
		s.append("  ''delivery_date_main'': '''',\n");
		  
		s.append("  ''delivery_date_milk'': '''',\n");
		  
		s.append("  ''delivery_date_fresh'': '''',\n");
		  
		s.append("  ''delivery_date_time'': ''09'',\n");
		  
		s.append("  ''Data'': [\n");
		  
		for(int i = 0; i < list.size(); ++i) {
				
			  	 int kod = list.get(i);
			  	
				 double d = main_map.get(kod);
				 	 
				 s.append("    {\n");
				 
				 s.append("      ''catalog_code'': 1" + String.valueOf(kod) + ",\n");
				 
				 s.append("      ''period_number'': 0,\n");
				 
				 if( WsUtils.isKodEqual(kod, WsUtils.EGG_KOD_1) ||  WsUtils.isKodEqual(kod, WsUtils.EGG_KOD_2)) {
					 

					 s.append("      ''quantity'': " + String.valueOf((int)(d*1000)));
				 
				 }
				 else {
					 s.append("      ''quantity'': " + WsUtils. getDF_fix_to_String(d, 3));
				 }
				 
				 if(i != (list.size() -1) ) {
					 
					 s.append("\n    },\n");
				 }
				 else {
					 
					 s.append("\n    }\n"); 
				 }

		}
		  
		s.append("\n ]\n}");
			
		vec_pages.add(s.toString());
		  
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		return vec_pages;
	}
	
	public String getPrintHtml(Vector<WsSkladMoveDataColumn> vec_all, int start, int end, int page_number) {

		return "";
		 
	}
	
	class ItemChangeListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if (e.getStateChange() == ItemEvent.SELECTED) {
		         
				//setText(getPrintHtml());
		    }
		}       
	}
	
	
	
	public boolean saveToJsonFile() {
		
		String file_to_save =  jsonSaveFileChoose(this);
		
		if(null == file_to_save)  { return false; }
		
		for(int i = 0; i <  m_html_pages.size(); ++i) {
			
			 	File path = new File(file_to_save);

		        FileWriter wr;
		        
				try {
					wr = new FileWriter(path);
					
					String s = m_html_pages.elementAt(i);
	
			        wr.write(s.replace("''","\""));
	
			        wr.flush();
			         
			        wr.close();
			        
				} catch (IOException e) {
				
					e.printStackTrace();
					
					return false;
				}

		}
		
		return true;
	}
	
	
	public HashMap<Integer, WsSkladMoveDataColumn> importDataRaskl() {
		
		Vector<WsAgentData> vec = m_rasklaka_control.getData();

		WFParseIndicies schema =  new WFParseIndicies(TYPE.RASKLADKA);

		HashMap<Integer, WsSkladMoveDataColumn> main_map = new 	HashMap<Integer, WsSkladMoveDataColumn>();
		
		for(WsAgentData dt : vec) {
		
			Vector<WsImportData>  data_import = WSExcelImport.getDataFromRaskladka( dt.contact,  schema);
			
			//the size of data_import == 7
			for(int i = 0; i < 7; ++i) {
				
				WsImportData di = data_import.elementAt(i);
				
				Vector<WFRowData > v =  di.m_data;
				
				int people_quantity = dt.quantity[i];
				
				for(WFRowData dr : v) {
					
					int kod = dr.kod;
					
					double q = dr.quantity * people_quantity;
					
					if(main_map.keySet().contains(kod)) {
						
						main_map.get(kod).out_quantity_1 += q;
					}
					else {
						
						WsSkladMoveDataColumn cl = new WsSkladMoveDataColumn();
						
						cl.out_quantity_1 = q;
						
						main_map.put(kod, cl);
					}
				}
			}
		}
		
		return main_map;
		
	}
	
}