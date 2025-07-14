/**
 * 
 */
package wslong;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import wsdatabase.WsAgentSqlStatements;
import wsdatabase.WsRashodSqlStatements;
import wsdatabase.WsTransactions;
import wsdatabase.WsUtilSqlStatements;
import wsdatastruct.WsPartType;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsdialogs.WsXmlImportRashodDialog;
import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsevents.WsEventInt;
import wsevents.WsEventNewRashodDate;
import wsevents.WsPrihodInvoiceChangedEvent;
import wsevents.WsRashodInvoiceChangedEvent;
import wsimport.*;
import wsimport.WsXmlParserZsuProd.WsParserResult;
import wsmain.WsCatalogKods;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsImportXmlRashodLong extends SwingWorker<Integer, Object> implements PropertyChangeListener {
	
	WsXmlImportRashodDialog m_d = null;
	 
	 JProgressBar m_progressBar = null;
	
	boolean is_working = true;
	
	public  WsImportXmlRashodLong(WsXmlImportRashodDialog dialog, 
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
    	
		WsPrihodInvoiceChangedEvent ev = new WsPrihodInvoiceChangedEvent();
		
		ev.setRowId(-1);
		
		WsEventDispatcher.get().fireCustomEvent(ev);

    }
    
    
    private boolean isXmlFileValid(WsXmlParserZsuProd parser) {
    	
    	 parser.checkXml (m_d.getFilePath());
    	
    	 WsParserResult res = parser.getResult();
    	 
    	 boolean resFlag = false;
    	 
    	 switch(res) {
    	 
	    	 case VALID_RASHOD: {
	    		 
	    		 resFlag = true;
	    		 
	    		 break;
	    	 }
	    	 case VALID_PRIHOD: {
	    		 
	    		 resFlag = false;
	    		 
	    		 WsUtils.showMessageDialog(getMessagesStrs("xmlPRInstPMessage"));
	    		 
	    		 break;
	    		 
	    	 }
	    	 case NONVALID_IOERROR:
	    	 case NONVALID_FILE_ACCESS_ERROR:
	    	 case NONVALID_PARSE_ERROR: 
	    	 default : {
	    		 
	    		 resFlag = false;
	    		 
	        	 WsUtils.showMessageDialog(getMessagesStrs("xmlParserCriticalMessage") + " : " +
	 					parser.getErrorMessage());
	    	 }
    	 
    	 };
    	 
    	 return resFlag;
    	
    }
    
    private boolean isXmlParsingResultValid(Vector<WsRashodData> vec, WsXmlParserZsuProd parser) {
    	
		   	 WsParserResult res = parser.getResult();
		   	 
		   	 if(res == WsParserResult.VALID_RASHOD) {
		   	 
		   		 if(vec == null || vec.isEmpty()) {
		   			 
		   			WsUtils.showMessageDialog(getMessagesStrs("xmlParserEmptyMessage"));
		   			 
		   			setProgress(100);
		   			
		   			return false;
		   		 }
	
		   		 return true;
		   	 
		   	 }
		   	 else if(vec != null && !vec.isEmpty()) {
		   		 
		   		int rs = WsUtils.showYesNoDialogLong(  getMessagesStrs("confirmPartialXmlImport0"),
		   				getMessagesStrs("confirmPartialXmlImport1"));
			 	   
				return 1 == rs;
		 
		   	 }
		   	 else {
		   		 
		   		WsUtils.showMessageDialog(getMessagesStrs("xmlParserCriticalMessage") + " : " +
						parser.getErrorMessage());
		   		 
		   	 }
		   	 
		   	 return false;
   	 
    }
    
	private void importData() {
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		WsXmlParserZsuProd parser = new WsXmlParserZsuProd();
		
		m_d.setTextForCountLabel(getMessagesStrs("xmlParserMoveMessage"));
		
		setProgress(2);
		
		if( ! isXmlFileValid(parser) ) { 
			
			m_d.setTextForCountLabel("");
			
			setProgress(100); 
			
			m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			return; 
		}
		
		Vector<WsRashodData> vec = parser.loadRashod(m_d.getFilePath());
		
		setProgress(50);
		
		if(!isXmlParsingResultValid(vec, parser)) {  
			
			setProgress(100);
			
			m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			return;
			
		}
		
		m_d.setTextForCountLabel(getGuiStrs("klNaklImport") + " " + String.valueOf(vec.size()) + ":");
		
		HashMap<Integer, WsPartType> db_catalog = WsUtilSqlStatements.getPartTypesMap();
		
		double nds_coeff = WsUtils.getNdsCoeff() - 1.0;
		
		int createdNakls = 0;
		
		double percentForNakl = 50.0/vec.size();
		
		boolean lackFlag = false;
		
		Vector<Integer> v_inserted_id = new Vector<Integer>(); 
		
		for(WsRashodData d : vec) {
			
			d.info = "import PAS";
			
			if(m_d.isCreateAgentsCheckBoxSelected() && (d.agentName != null 
					&& !d.agentName.isEmpty()) ) {
				
				d.id_counterparty = createAgentByName(d.agentName);
			}
			else {
				
				d.id_counterparty = m_d.getCurrentAgentSqlId();
			
			}
		
			Vector<WsRashodPartData> candidates_for_removing = new Vector<WsRashodPartData>();
			
			for( WsRashodPartData dr : d.rows) {
				
				int kod = WsCatalogKods.getKodFromDatabaseCatalog(dr.kod, db_catalog);
				
				if(kod == WsUtils.UNKNOWN_KOD) {
					
					candidates_for_removing.add(dr);
					
					continue;
				}
				
				dr.kod = kod;
				
				WsPartType tp = WsUtilSqlStatements.getPartTypeForKod(dr.kod);
				
				if(tp == null) {
					
					candidates_for_removing.add(dr);
					
					continue;
				}
				
				dr.name = tp.name;
				
				dr.id_units = WsUtils.getUnitIdForName(dr.name);
				
				dr.vendor_code_2 = String.valueOf(dr.kod);
				
				dr.costwithnds = dr.costwithnds/dr.quantity; //the parser saves here the total sum
				
				dr.nds = dr.costwithnds*nds_coeff;
				
				dr.cost = dr.costwithnds - dr.nds;
			}
			
			//remove rows with non-valid kods
			for(WsRashodPartData d1: candidates_for_removing) {
				
				d.rows.remove(d1);
				
			}
			
			Vector<String> vec_not_enough_quantity = new Vector<String>();
			
			Vector<WsRashodPartData> vec_ins =
					WsRashodSqlStatements.findSkladPositionsForRashod(d.date, d.rows, 
							false, vec_not_enough_quantity, false);
			
			for(int i2 = 0; i2 < vec_not_enough_quantity.size(); ++i2) {
				
				if(i2 == 0) { d.info = "-"; }
				
				d.info += vec_not_enough_quantity.elementAt(i2) + " ";
				
				lackFlag = true;
			}
			
		
			
			WsTransactions.beginTransaction(null);
			
			try {
				
				int inserted_id =  WsRashodSqlStatements.createNewRashod(d, vec_ins);
				
				if( inserted_id != -1) {
					
					v_inserted_id.add(inserted_id);
					
					WsTransactions.commitTransaction(null);
					
					createdNakls++;
					
					setProgress(50 + (int)(percentForNakl*createdNakls));
				}
				else {
					
					WsTransactions.rollbackTransaction(null);
					
				}
			}
			catch(Exception e) {
				
				WsTransactions.rollbackTransaction(null);
			}
		}
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		WsRashodInvoiceChangedEvent ev = new WsRashodInvoiceChangedEvent();
		
		ev.setId(v_inserted_id.elementAt(0));
				
		WsEventDispatcher.get().fireCustomEvent(ev);
		
		WsEventNewRashodDate ev1 = new WsEventNewRashodDate();
		
		WsEventDispatcher.get().fireCustomEvent(ev1);
		
		WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.AGENTS_DATA_CHANGED);
		
		WsEventDispatcher.get().fireCustomEvent(event);
		
		setProgress(100);
		
		if(lackFlag) {
			
			WsUtils.showMessageDialog("<html>" + getMessagesStrs("raskladkaLackPositionsDetectedMessage0") +
					"<br/>" + getMessagesStrs("raskladkaLackPositionsDetectedMessage1")
							+ "</html>");
			
		}
		
		WsUtils.showMessageDialog(String.valueOf(createdNakls) + " " +
   			    getMessagesStrs("raskladkaNaklsNumberCreatedMessage"));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		 if ("progress" == evt.getPropertyName()) {
			 
			 	int p = (Integer) evt.getNewValue();
			 	
	            m_progressBar.setValue(p);
	               
		 }
	}
	
	private int createAgentByName(String name) {
		
		return  WsAgentSqlStatements.createNewAgentByName(name);
	}
}
