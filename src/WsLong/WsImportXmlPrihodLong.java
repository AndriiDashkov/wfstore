
package WsLong;

import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsTransactions;
import WsDatabase.WsUtilSqlStatements;
import WsDialogs.WsXmlImportPrihodDialog;
import WsEvents.WsEventDispatcher;
import WsEvents.WsPrihodInvoiceChangedEvent;
import WsImport.*;
import WsImport.WsXmlParserZsuProd.WsParserResult;
import WsMain.WsCatalogKods;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsImportXmlPrihodLong extends SwingWorker<Integer, Object> implements PropertyChangeListener {
	
	WsXmlImportPrihodDialog m_d = null;
	 
	 JProgressBar m_progressBar = null;
	
	boolean is_working = true;
	
	public  WsImportXmlPrihodLong(WsXmlImportPrihodDialog dialog, 
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
    	 
	    	 case VALID_PRIHOD: {
	    		 
	    		 resFlag = true;
	    		 
	    		 break;
	    	 }
	    	 case VALID_RASHOD: {
	    		 
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
    
    private boolean isXmlParsingResultValid(Vector<WsPrihodData> vec, WsXmlParserZsuProd parser) {
    	
		   	 WsParserResult res = parser.getResult();
		   	 
		   	 if(res == WsParserResult.VALID_PRIHOD) {
		   	 
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
			
			return; 
		}
		
		Vector<WsPrihodData> vec = parser.loadPrihod(m_d.getFilePath());
		
		setProgress(50);
		
		if(!isXmlParsingResultValid(vec, parser)) {  
			
			setProgress(100);
			
			return;
			
		}
		
		m_d.setTextForCountLabel(getGuiStrs("klNaklImport") + " " + String.valueOf(vec.size()) + ":");
		
		HashMap<Integer, WsPartType> db_catalog = WsUtilSqlStatements.getPartTypesMap();
		
		double nds_coeff = WsUtils.getNdsCoeff() - 1.0;
		
		int inserted_id = -1;
		
		StringBuilder errorMessage = new StringBuilder();
		
		int createdNakls = 0;
		
		double percentForNakl = 50.0/vec.size();
		
		for(WsPrihodData d : vec) {
			
			d.info = "import PAS";
			
			//test contract
			d.id_contract = 1;
			
			d.id_counterparty = m_d.getCurrentAgentSqlId();
			
			
			Vector<WsPrihodPartData> candidates_for_removing = new Vector<WsPrihodPartData>();
			
			for( WsPrihodPartData dr : d.rows) {
				
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
				
				dr.rest = dr.quantity;
				
				dr.id_part_type = tp.id;
				
				dr.vendorcode2 = String.valueOf(dr.kod);
				
				dr.costwithnds = dr.costwithnds/dr.quantity; //the parser saves here the total sum
				
				dr.nds = dr.costwithnds*nds_coeff;
				
				dr.cost = dr.costwithnds - dr.nds;
			}
			
			//remove rows with non-valid kods
			for(WsPrihodPartData d1: candidates_for_removing) {
				
				d.rows.remove(d1);
				
			}
						
			WsTransactions.beginTransaction(null);
			
			inserted_id = WsPrihodSqlStatements.createNewPrihod(d, d.rows);
			
			if (inserted_id != -1) {
				
				WsTransactions.commitTransaction(null);
				
				createdNakls++;
				
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
				errorMessage.append(d.number + ",");
				
			}
			
			setProgress(50 + (int)(percentForNakl*createdNakls));
			
		}
		
		setProgress(100);
		
		String errorStr = errorMessage.toString();
		
		if(errorStr != null && !errorStr.isEmpty()) {
		
			WsUtils.showMessageDialog(getMessagesStrs("xmlLoadInDBMessage") +
					" " + errorStr);
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
	

}
