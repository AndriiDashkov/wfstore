
package WsForms;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.Ws2DatesControl;
import WsControls.WsKodChooserPanel;
import WsControls.WsNaklImportPanelControl;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsRashodSqlStatements;
import WsImport.WsImportExcelUtil;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsReports.WsRashodNaklPeresortCompareReport;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsFindPeresortForm extends JPanel {
	

	private static final long serialVersionUID = 1L;

	Ws2DatesControl m_date = new Ws2DatesControl(false);
	
	protected  JButton m_importButtonR = new JButton("Створити звіт");
	
	WsKodChooserPanel m_panelLeft = null;
	
	WsNaklImportPanelControl m_panelRight =  null;
	
	JSplitPane m_splitPane = null;
	
	public WsFindPeresortForm() {
		
		createGUI();
		
		 m_date.setCurrentStartDate();
		 
		 m_date.setCurrentEndDate();
		 
		 addListeners();
	}
	
	public void refresh() {
		
		m_panelLeft.refresh();
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel panel1 = WsGuiTools.createHorizontalPanel();
		
		panel1.add(m_date);
		
		Dimension sizeD = m_date.getPreferredSize();
		
		sizeD.width = 350;
		
		m_date.setMaximumSize(sizeD);
		
		panel1.add(Box.createHorizontalStrut( WsUtils.HOR_STRUT));
		
		panel1.add(m_importButtonR);
		
		panel1.add(Box.createHorizontalGlue());
		
	    m_panelLeft = createLeftPanel();
		
		m_panelRight =  createRightPanel();
		
		m_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				m_panelLeft, m_panelRight);
		
		Dimension d = m_panelLeft.getMaximumSize();
		
		d.width = 600;
		
		d.height = 400;
		
		d.width = 300;

		m_panelLeft.setMinimumSize(d);

		add(panel1);
		
		add(m_splitPane);
		
		d.width *=2;
		
		add(Box.createVerticalGlue());
		

	}
	
	private WsKodChooserPanel createLeftPanel( ) {
		
		
		return new WsKodChooserPanel();
	
	}
	
	private WsNaklImportPanelControl createRightPanel( ) {
		
		
		return new WsNaklImportPanelControl();

	}
	
	private HashMap<Integer, WsSkladMoveDataColumn> getRashod() {
		
		HashMap<Integer, WsSkladMoveDataColumn> map = null;
		
		map = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		Vector<WsPartType> vec = m_panelLeft.getCurrentVectorData();
		
		for(WsPartType d : vec) {
			
			
			 Vector<WsRashodPartData> vd = WsRashodSqlStatements.getRashodPartSumList(m_date.getSqlStartDate(), 
					 m_date.getSqlEndDate(), d.id);
			 
			 WsSkladMoveDataColumn d_in =  new WsSkladMoveDataColumn();
			 
			 if(!vd.isEmpty()) {
			 
				 WsRashodPartData d_out = vd.elementAt(0);
				 
				 d_in.out_quantity_1 = d_out.quantity;
				 
				 d_in.out_quantity = 0.0;
				 
				 d_in.kod = d_out.kod;
				 
				 d_in.id = d_out.id;
				 
				 d_in.name = d_out.name;
				 
				 map.put(d_in.kod, d_in);
			 }
			 else {
				 
				 d_in.out_quantity_1 = 0.0;
				 
				 d_in.out_quantity = 0.0;
				 
				 d_in.kod = d.kod;
				 
				 d_in.id = d.id;
				 
				 d_in.name = d.name;
				 
				 map.put(d_in.kod, d_in);
				 
			 }
		}
		
		return map;
		
	}
	
	private HashMap<Integer, WsSkladMoveDataColumn> importExcelNakl() {
		
		HashMap<String, Integer> map1 = m_panelRight.getSpinnerMap();
		
		int kod_column = map1.get("kod_column");
		
		int name_column = map1.get("name_column");
		
		int out_quantity_column = map1.get("out_quantity_column");
		
		int sheet_index = map1.get("sheet_index");
		
		map1.clear(); map1 = null;
		
		HashMap<Integer, WsSkladMoveDataColumn> map = null;
		
		map = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		String excel_file_name = null;
		
		Vector<WsAgentData> files =  m_panelRight.getData();
		 
	    for(WsAgentData dfc : files){
				
	    	String fc = dfc.contact;
	    	
			try {
				
				excel_file_name = fc;
				
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
		        		
		        		d.out_quantity_1 = 0.0;
		        		
		        		d.out_quantity = WsImportExcelUtil.getDoubleCell(row, out_quantity_column);
		        		
		        		d.name = WsImportExcelUtil.getStringCell(row,  name_column);
		        		
		        		WsSkladMoveDataColumn d_f = map.get(d.kod);
		        		
		        		if(d_f == null) {
		        			
		        			map.put(d.kod, d);
		        		}
		        		else {

		        			d_f.out_quantity += d.out_quantity;
		        			
		        		}  
			    }
					    
			    wb.close();
					    
			} catch(Exception ioe) {
				
				if( WsUtils.isDebug() ) {	
					
					ioe.printStackTrace();
				}
						    
			}
	    }
	    		
		return map;
		
	}
	
	private Vector<WsSkladMoveDataColumn> merge() {
		
		 HashMap<Integer, WsSkladMoveDataColumn> map1 = importExcelNakl();
		 
		 HashMap<Integer, WsSkladMoveDataColumn> map2 = getRashod() ;
		 
		 for (Map.Entry<Integer, WsSkladMoveDataColumn> entry : map2.entrySet()) {
			   
	            Integer key = entry.getKey();
	            
	            WsSkladMoveDataColumn value = entry.getValue();
	            
	            WsSkladMoveDataColumn d_f = map1.get(key);
	            
	        	if(d_f != null) {
        			
        			value.out_quantity = d_f.out_quantity;
        		}
	           
	      }
		
		  ArrayList<Integer> list = new ArrayList<Integer>(map2.keySet()); 
			
		  Collections.sort(list);
			
		  Vector<WsSkladMoveDataColumn> vec_all = new Vector<WsSkladMoveDataColumn>();
			 
		  for(Integer kod: list) {
				
				 WsSkladMoveDataColumn d = map2.get(kod);
				 
				 vec_all.add(d);
				
		  }
			
		  return vec_all;	
	}
	
	
	
	private void addListeners() {
		
		
		m_importButtonR.addActionListener(new ActionListener() {
	        
			@Override
			public void actionPerformed(ActionEvent e) {
				
			
				WsRashodNaklPeresortCompareReport dialog = new WsRashodNaklPeresortCompareReport(WsUtils.get().getMainWindow(), 
    					getGuiStrs("rashodNaklPeresortCompareReportCaption"), merge(), m_date.getSqlStartDate(), m_date.getSqlEndDate());
    			
    			dialog.setVisible(true);
				
			}
		});
		
		
	}
}