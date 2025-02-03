
package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JFileChooser;
import WsDataStruct.WsAgentData;
import WsDialogs.WsFileChooserDialog;
import WsEditTables.WsImportExcelEditTable2;
import WsImport.WFParseIndicies;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsImport.WFParseIndicies.TYPE;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsFileTableControl3 extends WsFileTableControl {

	private static final long serialVersionUID = 1L;
	
	private Vector<WsImportData> m_vec = null;
	
	WFParseIndicies m_schema = null;
	

	/**
	 * @param columnNames
	 * @param fileChooserLabel
	 */
	public WsFileTableControl3(String[] columnNames, String fileChooserLabel, boolean enablePopupMenu) {
		super(columnNames, fileChooserLabel, enablePopupMenu);

				
		m_schema =  new WFParseIndicies(TYPE.RASKLADKA);
		
	}
	
	protected  void init(String[] columnNames, String fileChooserLabel) {
		
		m_table = new  WsImportExcelEditTable2(columnNames, m_enablePopupMenu);
		
		m_title = fileChooserLabel;
		
		createGUI();
		
		m_pathButton.addActionListener(forwarder);
	}
	
	public void setFirstColumnMinWidth(int w) {
		
		
		((WsImportExcelEditTable2) m_table).setFirstColumnMinWidth(w);
	}
	
	protected void onPath(ActionEvent e) {
		

		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), m_current_path, true, false);
		
		int result = sourceFile.showOpenDialog(this);
		
		String fileName = "";
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			if(null == sourceFile.getSelectedFile()) { return; }
			
			clearData();
			
			m_current_path = sourceFile.getSelectedFile().getParent();
			
			fileName = sourceFile.getSelectedFile().getName();
			
			WsAgentData d = new WsAgentData();
			
			d.contact = sourceFile.getSelectedFile().getPath();
			
			sourceFile = null;
			
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			if(d.contact != null && !d.contact.isEmpty()) {
				
				WsAgentData[] d1 = new WsAgentData[3];
				
				for(int i = 0; i < 3; ++i) {
					
					d1[i] = new WsAgentData();
				}
				
				d1[0].contact = getGuiStrs("snidanokReportName");
				
				d1[1].contact = getGuiStrs("obidReportName");
				
				d1[2].contact = getGuiStrs("vecheryaReportName");
					
				m_vec = WSExcelImport.getDataFromRaskladka( d.contact,  m_schema);
				
				for(int i = 0; i < 7; ++i) {
					
					WsImportData di = m_vec.elementAt(i);
					
					d1[0].quantity[i] = di.people[0];
					
					d1[1].quantity[i] = di.people[1];
					
					d1[2].quantity[i] = di.people[2];
					
				}
				
				m_table.addRow(d1[0]);
				
				m_table.addRow(d1[1]);
				
				m_table.addRow(d1[2]);
			}
			
			
			m_title_border.setTitle(m_title + " : " + fileName);
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public Vector<WsImportData>  getImportData() {
		
		return m_vec;
	}

	public void clearData() {
		
		m_table.clearTable();
		
		if(m_vec != null) {
			
			m_vec.clear();
		}
		
		m_title_border.setTitle(m_title);
	}
}
