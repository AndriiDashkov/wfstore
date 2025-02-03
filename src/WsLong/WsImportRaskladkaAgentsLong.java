
package WsLong;


import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsDialogs.WsExcelRaskladkaRashodImport2Dialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class  WsImportRaskladkaAgentsLong extends SwingWorker<Integer, Object> {
	
	WsExcelRaskladkaRashodImport2Dialog m_d = null;
	
	boolean is_working = true;
	
	public  WsImportRaskladkaAgentsLong(WsExcelRaskladkaRashodImport2Dialog dialog) {
		
		m_d = dialog;
		
	}
	
	public void setFinished() { is_working = false;}
	
    @Override
    public Integer doInBackground() {
        
    	importData();
    	
    	return 0;
    }

    @Override
    protected void done() {
        
    	m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    	
		WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
		
		ev.setRowId(-1);
		
		WsEventDispatcher.get().fireCustomEvent(ev);

    }
    
    
	private void importData() {
		
		Vector<WsAgentData> vec = m_d.getTableData();
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		WFParseIndicies schema = m_d.getIndicesSchema();
		
		String prev_excel_file_name = "";
		
		Vector<WFRowData>  data_import = null;
		
		boolean lackFlag = false;
		
		int createdNakls = 0;
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsAgentData d_agent =  vec.elementAt(i);
			
			String excel_file_name = d_agent.contact ; 
			
			if(excel_file_name.isEmpty()) {
				
				   JOptionPane.showMessageDialog(
			   			    WsUtils.get().getMainWindow(),
			   			    getMessagesStrs("raskladkaFilePathIsEmptyMessage"),
			   			    getMessagesStrs("messageInfoCaption"),
			   			    JOptionPane.CLOSED_OPTION);
				
				continue;
			}
			
			if(! prev_excel_file_name.equals(excel_file_name) ) {
				
				data_import = WSExcelImport.getDataFromRaskladkaSum( excel_file_name,  schema);
				
				if(null == data_import) {
					
					m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					JOptionPane.showMessageDialog(
				   			    WsUtils.get().getMainWindow(),
				   			    getMessagesStrs("raskladkaFileImportWrongMessage"),
				   			    getMessagesStrs("messageInfoCaption"),
				   			    JOptionPane.CLOSED_OPTION);
				
					return;
					
				}
			}
		

			WsRashodData data = new WsRashodData(); 
			
			data.id_counterparty = d_agent.id;
			
			data.date =   m_d.getSqlStartDate();
			
			data.number = String.valueOf(m_d.getInitialNumber() + i);
			
			data.people = d_agent.quantity[0];
		
			
			Vector<WsRashodPartData> vec_r = new Vector<WsRashodPartData>() ;
			
			for(int i1 = 0; i1 <  data_import.size(); ++i1) {
				
				WFRowData d = data_import.elementAt(i1);
				
				WsRashodPartData d_ = new WsRashodPartData();
				
				d_.quantity = d.quantity*data.people;
				
				if(d_.quantity > 0.00049 && d_.quantity < 0.001) {
					
					d_.quantity = 0.001;
				}
				
				if(d_.quantity <= 0.00049) {
					
					continue;
				}
				
				//eggs
				if(WsUtils.isKodEqual(d_.kod, WsUtils.EGG_KOD_1) || WsUtils.isKodEqual(d_.kod, WsUtils.EGG_KOD_2)) {
					
					d_.quantity *= 1000;
				}
				
				d_.name = d.name;
				
				d_.kod = d.kod;
				
				d_.vendor_code_2 = String.valueOf(d.kod);
				
				WsUnitData ud = WsUtilSqlStatements.getUnitIdForName(d.units);
				
				if(ud == null) {
					
					ud = WsUtilSqlStatements.getKgUnit();
				}
				if(ud == null) {
					
					ud = WsUtilSqlStatements.getFirstUnit();
			
				}
			
				d_.id_units = ud.id;
			
				vec_r.add(d_); 
			}
			
			Vector<String> vec_not_enough_quantity = new Vector<String>();
			
			Vector<WsRashodPartData> vec_ins =
					WsRashodSqlStatements.findSkladPositionsForRashod(data.date, vec_r, 
							false, vec_not_enough_quantity, false);
			
			for(int i2 = 0; i2 < vec_not_enough_quantity.size(); ++i2) {
				
				if(i2 == 0) { data.info = "-"; }
				
				data.info += vec_not_enough_quantity.elementAt(i2) + " ";
				
				lackFlag = true;
			}
			
			WsTransactions.beginTransaction(null);
			
			if( WsRashodSqlStatements.createNewRashod(data, vec_ins) != -1) {
				
				WsTransactions.commitTransaction(null);
				
				createdNakls++;
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
			}
			
		}
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		if(lackFlag) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("raskladkaLackPositionsDetectedMessage") ,
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
		}
		
		JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    String.valueOf(createdNakls) + " " +
   			    getMessagesStrs("raskladkaNaklsNumberCreatedMessage") ,
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		

	}
}
