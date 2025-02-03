
package WsLong;

import static WsMain.WsUtils.getMessagesStrs;
import static WsMain.WsUtils.sqlDatePlusDays;
import java.awt.Cursor;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsDialogs.WsExcelRaskladkaRashodImportDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.WSExcelImport;
import WsImport.WsImportData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsImportRaskladka7daysLong extends SwingWorker<Integer, Object> {
	
	WsExcelRaskladkaRashodImportDialog m_d = null;
	
	boolean is_working = true;
	
	public WsImportRaskladka7daysLong(WsExcelRaskladkaRashodImportDialog dialog) {
		
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
		
		String excel_file_name = m_d.getExcelFilePath() ; 
		
		if(excel_file_name.isEmpty()) {
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("raskladkaFilePathIsEmptyMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		WFParseIndicies schema = m_d.getIndicesSchema();
	
		Vector<WsImportData>  data_import = WSExcelImport.getDataFromRaskladka( excel_file_name,  schema);
		
		if(null == data_import) {
			
			m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("raskladkaFileImportWrongMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
		
			return;
			
		}
		
		int non_valid_data  = 0;
		
		for(int i = 0; i < 7; ++i) {
			
			if(data_import.elementAt(i).m_data == null || 	data_import.elementAt(i).m_data.isEmpty()) {
				
				non_valid_data++;
			}
		}
		
		if(non_valid_data == 7) {
			
			m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("raskladkaFileImportWrongMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
		
			return;
		}
		
		int createdNakls = 0;
		
		boolean lackFlag = false;
		
		for(int j = 0; j < 7; ++j) {
			
			if( ((int)m_d.getSpinners()[j].getValue()) == 0 ) { continue; }
			
			WsRashodData data = new WsRashodData(); 
			
			data.id_counterparty = m_d.getAgentSqlId();
			
			data.date =   sqlDatePlusDays(m_d.getSqlStartDate(), j);
			
			data.number = "auto";
			
			data.people = (int)m_d.getSpinners()[j].getValue();
			
			Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>() ;
			
			for(int i = 0; i <  data_import.get(j).m_data.size(); ++i) {
				
				WFRowData d = data_import.get(j).m_data.elementAt(i);
				
				WsRashodPartData d_ = new WsRashodPartData();
				
				d_.quantity = d.quantity*data.people;
				
				if(d_.quantity > 0.00049 && d_.quantity < 0.001) {
					
					d_.quantity = 0.001;
				}
				
				if(d_.quantity <= 0.00049) {
					
					continue;
				}
				
				//eggs
				if( WsUtils.isKodEqual(d_.kod , WsUtils.EGG_KOD_1) ||  WsUtils.isKodEqual(d_.kod , WsUtils.EGG_KOD_2)) {
					
					
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
			
				vec.add(d_); 
			}
			
			Vector<String> vec_not_enough_quantity = new Vector<String>();
			
			Vector<WsRashodPartData> vec_ins =
					WsRashodSqlStatements.findSkladPositionsForRashod(data.date, vec, 
							false, vec_not_enough_quantity, false);
			
			for(int i = 0; i < vec_not_enough_quantity.size(); ++i) {
				
				if(i == 0) { data.info = "-"; }
				
				data.info += vec_not_enough_quantity.elementAt(i) + " ";
				
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