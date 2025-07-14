
package wslong;


import static wsmain.WsUtils.getMessagesStrs;

import java.awt.Cursor;
import java.util.Vector;
import javax.swing.SwingWorker;

import wsdatabase.WsRashodSqlStatements;
import wsdatabase.WsTransactions;
import wsdatabase.WsUtilSqlStatements;
import wsdatastruct.WsAgentData;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsdatastruct.WsUnitData;
import wsdialogs.WsExcelRaskladkaRashodImport2Dialog;
import wsevents.WsEventDispatcher;
import wsevents.WsRashodInvoiceChangedEvent;
import wsimport.WsExcelImport;
import wsimport.WsParseIndicies;
import wsimport.WsRowData;
import wsmain.WsUtils;

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
		
		WsParseIndicies schema = m_d.getIndicesSchema();
		
		String prev_excel_file_name = "";
		
		Vector<WsRowData>  data_import = null;
		
		boolean lackFlag = false;
		
		int createdNakls = 0;
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsAgentData d_agent =  vec.elementAt(i);
			
			String excel_file_name = d_agent.contact ; 
			
			if(excel_file_name.isEmpty()) {
				
				WsUtils.showMessageDialog(getMessagesStrs("raskladkaFilePathIsEmptyMessage"));
				
				continue;
			}
			
			if(! prev_excel_file_name.equals(excel_file_name) ) {
				
				data_import = WsExcelImport.getDataFromRaskladkaSum( excel_file_name,  schema);
				
				if(null == data_import) {
					
					m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
					WsUtils.showMessageDialog(getMessagesStrs("raskladkaFileImportWrongMessage"));
				
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
				
				WsRowData d = data_import.elementAt(i1);
				
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
			
			WsUtils.showMessageDialog(getMessagesStrs("raskladkaLackPositionsDetectedMessage0") +
					System.lineSeparator() + getMessagesStrs("raskladkaLackPositionsDetectedMessage1") );
			
		}
		
		WsUtils.showMessageDialog( String.valueOf(createdNakls) + " " +
   			    getMessagesStrs("raskladkaNaklsNumberCreatedMessage"));
		
	}
}
