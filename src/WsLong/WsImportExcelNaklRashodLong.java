/**
 * 
 */
package WsLong;

import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsDialogs.WsGroupImportRashodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsRashodInvoiceChangedEvent;
import WsImport.WFParseIndicies;
import WsImport.WFRowData;
import WsImport.*;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsImportExcelNaklRashodLong extends SwingWorker<Integer, Object> implements PropertyChangeListener {
	
	 WsGroupImportRashodDialog m_d = null;
	 
	 JProgressBar m_progressBar = null;
	
	boolean is_working = true;
	
	public  WsImportExcelNaklRashodLong( WsGroupImportRashodDialog dialog, 
			JProgressBar progressBar) {
		
		m_d = dialog;
		
		m_progressBar = progressBar;
		
		addPropertyChangeListener(this);	
		
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
		
		WsImport.WsImportData  data_import = null;
	
		boolean lackFlag = false;
		
		int createdNakls = 0;
		
		double percentForNakl = 100.0/vec.size();
		
		setProgress(1);
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsAgentData d_agent =  vec.elementAt(i);
			
			String excel_file_name = d_agent.contact ; 
			
			if(excel_file_name.isEmpty()) {
				
				continue;
			}
			
			if(! prev_excel_file_name.equals(excel_file_name) ) {
				
				data_import = WSExcelImport.getData( excel_file_name,  schema);
				
				if(null == data_import) {
				
					continue;
					
				}
			}
		

			WsRashodData data = new WsRashodData(); 
			
			data.id_counterparty = d_agent.id;
			
			if(d_agent.id == -1) { continue; }
			
			data.date =   m_d.getDate();
			
			data.number = d_agent.info;
			
			data.people = d_agent.quantity[0];
		
			Vector<WsRashodPartData> vec_r = new Vector<WsRashodPartData>() ;
			
			for(int i1 = 0; i1 <  data_import.m_data.size(); ++i1) {
				
				WFRowData d = data_import.m_data.elementAt(i1);
				
				WsRashodPartData d_ = new WsRashodPartData();
				
				d_.quantity = d.quantity;
				
				if(d_.quantity > 0.00049 && d_.quantity < 0.001) {
					
					d_.quantity = 0.001;
				}
				
				if(d_.quantity <= 0.00049) {
					
					continue;
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
				
				setProgress((int)(percentForNakl*createdNakls));
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
			}
			
		}
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		setProgress(100);
		
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		 if ("progress" == evt.getPropertyName()) {
			 
			 	int p = (Integer) evt.getNewValue();
			 	
	            m_progressBar.setValue(p);
	               
	        }
		
	}
}
