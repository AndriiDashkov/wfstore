
package WsLong;

import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Cursor;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import WsDataStruct.WsPartType;
import WsDatabase.WsContractsSqlStatements;
import WsDialogs.WsExcelImportCatalogPricesDialog;
import WsEvents.WsContractPriceChangeEvent;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsImport.WFParseIndicies;
import WsImport.WSExcelImport;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsImportExcelRaskladkaToKatalogLong extends SwingWorker<Integer, Object> {
	
	 WsExcelImportCatalogPricesDialog  m_d = null;
	
	boolean is_working = true;
	
	public WsImportExcelRaskladkaToKatalogLong( WsExcelImportCatalogPricesDialog  dialog) {
		
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
    	
        WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.CATALOG_LOADED);
        
 		WsEventDispatcher.get().fireCustomEvent(event);
    	
    }
    
    
	private void importData() {
		
		int id_contract = m_d.getIdContract();
		
		if(id_contract == -1) { return; }
		
		String excel_file_name = m_d.getExcelFilePath() ; //m_path_file.getText();
		
		if(excel_file_name.isEmpty()) {
			
			   JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("raskladkaFilePathIsEmptyMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		m_d.getParentFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		WFParseIndicies schema = m_d.getIndicesSchema();
	
	    Vector<WsPartType> data_import = WSExcelImport.getDataFromRaskladkaKatalogCost( excel_file_name, schema);
			 
	
		if( data_import == null ||  data_import.isEmpty()) {
			
			 JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("raskladkaFileImportWrongMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			m_d.getParentFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
			return;
		}
		
		WsContractsSqlStatements.addPricesToContract(data_import, id_contract, m_d.isMergeSelected());
		
		m_d.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		m_d.getParentFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		
		WsContractPriceChangeEvent ev = new  WsContractPriceChangeEvent();
		
		ev.setRowId(id_contract);
		
		WsEventDispatcher.get().fireCustomEvent(ev);
	
		JOptionPane.showMessageDialog(
   			    WsUtils.get().getMainWindow(),
   			    getMessagesStrs("raskladkaKatalogImportSuccessMessage") ,
   			    getMessagesStrs("messageInfoCaption"),
   			    JOptionPane.CLOSED_OPTION);
		

	} 
    
}
