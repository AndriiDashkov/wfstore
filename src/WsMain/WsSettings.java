package WsMain;


import static WsMain.WsLog.writeLog;
import static WsMain.WsUtils.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import WsEvents.WsEvent;
import WsEvents.WsEventDispatcher;


/**
 * @author Andrii Dashkov license GNU GPL v3
 * Container for saving and loading settings of the application
 */
public class WsSettings {

	{
		WsEventDispatcher.get().addConnect(WsEventDispatcher.SAVE_EVENT, this, "saveSettings");
		
		//to give a last chance to save something
		WsEventDispatcher.get().addConnect(WsEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT, this, "saveSettingsIfNeed");
	}
	
	public static String LOCALE_DATE_DEFAULT_CAPTION ="localeDefault";
	
	private int _importPrihodSheetIndex = 10;
	
	private int _importPrihodKodColumnIndex = 0;
	
	private int _importPrihodNameColumnIndex = 1;
	
	private int _importPrihodQuantityColumnIndex = 6;
	
	private int _importPrihodUnitColumnIndex = 2;
	
	private boolean m_logEnabled;
	
	private boolean m_saving_has_been_required = true;

	int m_minTimerDelay = 1;
	
	int m_maxTimerDelay = 10;
	
	int m_currentTimerDelay = 3;

	private String language = new String("uk");
	
	private String country = new String("UA");
	
	private Locale m_locale = null;
	
	//furather the fomat will be loaded from the settings file
	private String guiDateFormat = new String(DATE_FORMAT);
	
	private int m_maxLogFileLength = 10;//in bytes

	private ArrayList< String > rowRecentData = new ArrayList<String>();
	
	//container for ids of info messages, which have been marked as "don't show any more"
	private ArrayList<Integer> m_info_not_shown = new ArrayList<Integer>();
	
	HashMap<Integer,Integer> m_hiddenDialogsChoice = new HashMap<Integer,Integer>();	
	
	private final String PRIHOD1_SHEET_INDEX_ATTR = "prihod_import_sheet";
	
	private final String PRIHOD1_KOD_COLUMN_INDEX_ATTR = "prihod_import_kod_column";
	
	private final String PRIHOD1_NAME_COLUMN_INDEX_ATTR = "prihod_import_name_column";
	
	private final String PRIHOD1_QAUNTITY_COLUMN_INDEX_ATTR = "prihod_quantity_column";
	
	private final String PRIHOD1_UNIT_COLUMN_INDEX_ATTR = "prihod_unit_column";
	
	private final String SETT_ELEMENT = "Settings";
	
	private final String RECENT_BASES_ELEMENT = "RecentBases";
	
	private final String RECENT_BASE_ELEMENT = "RecentBase";
	
	private final String RECENT_BASE_PATH_ATTR = "pathName";
		
	private final String LOG_ENABLED_ATTR = "logEnabled";
	

	public WsSettings () {
		
		setCurrentLocale();
	}
	
	
	public int[] getPrihodImportNakl() {
		
		int[] ar = {
			_importPrihodSheetIndex,
			
		    _importPrihodKodColumnIndex,
		    
			_importPrihodNameColumnIndex,
			
			_importPrihodQuantityColumnIndex,
			
			_importPrihodUnitColumnIndex 
		};
		
		return ar;
		
		
	}
	
	public String getLanguage() {
		
		return language ;
	}
	
	public void setLanguage(String l) {
		
		language = l;
	}
	
	public String getCountry() {
		
		return country ;
	}
	
	public void setCountry(String l) {
		country = l;
	}
	
	public void setGuiDateFormat(String format) {
		
		if ( format != null ) {
			guiDateFormat = format;
		}
		
	}
	
	
	public String getGuiDateFormat() {
		
		return guiDateFormat;
	}
	
	public boolean firstInit() {
		
		return true;
	}
	
	public void load_Parameters (String configFile) {
	
		rowRecentData.clear();
		
		InputStream	in = null;
		
		try 
		{
			environmentCheck(configFile); 
	
			in = new FileInputStream(configFile);	
		
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
		
			
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT) {
					
					if (reader.getLocalName().equals(SETT_ELEMENT)) {
						
						try{
							_importPrihodSheetIndex = Integer.parseInt(reader.getAttributeValue(null, PRIHOD1_SHEET_INDEX_ATTR));
						   
							_importPrihodKodColumnIndex = Integer.parseInt(reader.getAttributeValue(null, PRIHOD1_KOD_COLUMN_INDEX_ATTR));
							
							_importPrihodNameColumnIndex = Integer.parseInt(reader.getAttributeValue(null, PRIHOD1_NAME_COLUMN_INDEX_ATTR));
							
							_importPrihodQuantityColumnIndex = Integer.parseInt(reader.getAttributeValue(null, PRIHOD1_QAUNTITY_COLUMN_INDEX_ATTR));
							
							_importPrihodUnitColumnIndex = Integer.parseInt(reader.getAttributeValue(null, PRIHOD1_UNIT_COLUMN_INDEX_ATTR));
						}
						catch( NumberFormatException e) {
							
							_importPrihodSheetIndex = 10;
							
						    _importPrihodKodColumnIndex = 0;
							
							_importPrihodNameColumnIndex = 1;
							
							_importPrihodQuantityColumnIndex = 6;
							
							_importPrihodUnitColumnIndex = 2;
							
						}
																	
					}
					
					if (reader.getLocalName().equals(RECENT_BASE_ELEMENT)) {
					    
						rowRecentData.add(reader.getAttributeValue(null, RECENT_BASE_PATH_ATTR));
					
					}
										
				}
			}	
			
			reader.close(); 
		
		}
		catch (XMLStreamException e) {
		 
		 writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
		}
		catch (IOException  e) {
			
			writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
		}
		finally {
			
			try {
				
				in.close();
				
			} 
			catch (IOException e) {}
		}
	}

	@SuppressWarnings("deprecation")
	private  void setCurrentLocale() {
		
	   if ( language.isEmpty() && country.isEmpty() )	{
		   
		   m_locale = Locale.getDefault(); 
	   }
	   else {
		   
		   try {
			   
			   m_locale =  Locale.of(language, country);
		   }
		   catch(java.lang.NoSuchMethodError ex) {
			   
			   m_locale =  new Locale(language, country);   
		   }

	   }
		
	}
	
	
	public Locale getCurrentLocale() { return m_locale; }

	//
	/**
	 *  gets object for localization
	 * @param resName - resource name
	 * @return the resource for language
	 */
	public ResourceBundle getResourceBundle( String resName)
	{

      //for normal work the file with specific locale should exist
       return ResourceBundle.getBundle( "WsTranslation."+ resName, m_locale);

	}

	public  void saveSettings (WsEvent eventSave)  {
		
		if ( eventSave.getEventType() != WsEventDispatcher.SAVE_EVENT ) { return; }
		
		try {		
			
			saveXMLdata();
			
		} catch (FileNotFoundException e) {
	
			 writeLog("FileNotFoundException  : " + NEXT_ROW, e, true, false, true);
			 
		} catch (XMLStreamException e) {
	
			 writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
		}
	}
	
	public  void saveSettingsIfNeed(WsEvent ev)  {
		
		if (m_saving_has_been_required) { 
			
			if ( ev.getEventType() != WsEventDispatcher.BEFORE_APPLICATION_EXIT_EVENT ) { return; }
			
			try {		
				
				saveXMLdata();
				
			} catch (FileNotFoundException e) {
		
				 writeLog("FileNotFoundException  : " + NEXT_ROW, e, true, false, true);
				 
			} catch (XMLStreamException e) {
		
				 writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
			}
		}
	}
	
	public void setReqSavingFlag() {
		
		m_saving_has_been_required = true;
	}

	// save xml data
	public  void saveXMLdata() throws FileNotFoundException, XMLStreamException {
		
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		
		//  "UTF-8" must be set here
		FileOutputStream fSt = new FileOutputStream(WsUtils.get().
				getXMLPath().concat(WsUtils.get().getSettingsXMLName()));
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(fSt, "UTF-8");
		
		try {
		
			writer.writeStartDocument("UTF-8", "1.0");
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeStartElement(SETT_ELEMENT);
			
			writer.writeAttribute(PRIHOD1_SHEET_INDEX_ATTR, Integer.toString(_importPrihodSheetIndex ));
	
			writer.writeAttribute(PRIHOD1_KOD_COLUMN_INDEX_ATTR, Integer.toString(_importPrihodKodColumnIndex ));
			
			writer.writeAttribute(PRIHOD1_NAME_COLUMN_INDEX_ATTR, Integer.toString(_importPrihodNameColumnIndex ));
			
			writer.writeAttribute(PRIHOD1_QAUNTITY_COLUMN_INDEX_ATTR , Integer.toString(_importPrihodQuantityColumnIndex ));
			
			writer.writeAttribute(PRIHOD1_UNIT_COLUMN_INDEX_ATTR, Integer.toString(_importPrihodUnitColumnIndex ));
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeDTD(ADD_TAB);
			
			writer.writeStartElement(RECENT_BASES_ELEMENT);
			
			for (int i= 0; i< rowRecentData.size(); i++) {
				
				if(null == rowRecentData.get(i)) { continue; }
				
				writer.writeDTD(NEXT_ROW);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeDTD(ADD_TAB);
				
				writer.writeStartElement(RECENT_BASE_ELEMENT);
				
				writer.writeAttribute(RECENT_BASE_PATH_ATTR , rowRecentData.get(i));
				
				writer.writeEndElement();		
				
			}
		
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeDTD(ADD_TAB);
			
			writer.writeDTD(NEXT_ROW);
			
			writer.writeEndElement();
			
			writer.writeEndDocument();
		}
		finally {
			
			writer.close();
			
			if(fSt != null) {
				
				try {
					
					fSt.close();
				} 
				catch (IOException e) {}
			}
		}
	}
	
	
	public void setRecentData(String fullPath) {
		
		for(int i = 0; i <  rowRecentData.size(); ++i) {
			
			String s = rowRecentData.get(i);
			
			if(s.equals(fullPath)) { 
				
				rowRecentData.remove(i);
				
				rowRecentData.add(0, fullPath);
				
				return; 
				
			}
		}
		
		rowRecentData.add(0, fullPath);
		
		if(rowRecentData.size() > 10) {
			
			rowRecentData.removeLast();
			
		}
		
	}
	
	public void removeRecentData(String fullPath) {
		
		for(int i = 0; i <  rowRecentData.size(); ++i) {
			
			String s = rowRecentData.get(i);
			
			if(s.equals(fullPath)) { 
				
				rowRecentData.remove(i);
				
				return; 
				
			}
		}
		
	}
	
	
	public ArrayList<String> getRecentData() {
		
		return rowRecentData;
			
		
	}

	@SuppressWarnings("unused")
	private boolean createConfigFile(String configFile) {

		Locale currentLocale = Locale.getDefault();

		String s = "<?xml version='1.0' encoding='UTF-8'?>" + "<"+SETT_ELEMENT+" " 
		
		+ PRIHOD1_SHEET_INDEX_ATTR + "= '" + Integer.toString( _importPrihodSheetIndex ) + "' " 
		
		+ PRIHOD1_KOD_COLUMN_INDEX_ATTR + "= '" + Integer.toString( _importPrihodKodColumnIndex ) + "' "
		
		+ PRIHOD1_NAME_COLUMN_INDEX_ATTR  + "= '" + Integer.toString( _importPrihodNameColumnIndex ) + "' "
		
		+ PRIHOD1_QAUNTITY_COLUMN_INDEX_ATTR  + "= '" + Integer.toString( _importPrihodQuantityColumnIndex ) + "' "
		
		+ PRIHOD1_UNIT_COLUMN_INDEX_ATTR  + "= '" + Integer.toString( _importPrihodUnitColumnIndex ) + "' "
		
		+ LOG_ENABLED_ATTR +"='false' "+" ><"
		
		+ RECENT_BASES_ELEMENT + ">< " + RECENT_BASE_ELEMENT + " "  + RECENT_BASE_PATH_ATTR + " =''  ></"
		
		+ RECENT_BASE_ELEMENT + "></" + RECENT_BASES_ELEMENT + ">"
	
		+ "</"+SETT_ELEMENT+">";
		
		byte[] b = s.getBytes();
		
		try {
			
			FileOutputStream out = new FileOutputStream(configFile);
			
			out.write(b);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			
			writeLog("FileNotFoundException  : can't create config file " + NEXT_ROW, e, true, false, true);
			// no possibility to start application
			return false;
		}
		catch (IOException e) {
			
			 writeLog("IOException  : can't create config file " + NEXT_ROW, e, true, false, true);
			// no possibility to start application
			 return false;
		}
		
		return true;
	} 

	public void setLogEnabled(boolean flag) { 
		m_logEnabled = flag;
	}
	
	public boolean isLogEnabled() { 
		return m_logEnabled; 
	}

	/**
     * <p>Sets the maximum size of log file in bytes</p>
     * @param sz - maximum size of the log file in bytes 
     */
	public void setMaxLogFileLength(int sz)
	{
		m_maxLogFileLength = sz;
	}
	/**
     * <p>Gets the maximum size of log file in bytes</p>
     * @return  the maximum size of log file in bytes
     */
	public int getMaxLogFileLength()
	{
		return m_maxLogFileLength;
	}

	public ArrayList<Integer> getNonVisibleInfoMessages(){
		
		return m_info_not_shown;
	}
	
	public HashMap<Integer,Integer> getNonVisibleInfoMessagesCh(){
		
		return  m_hiddenDialogsChoice;
	}
	
	public void clearHiddenDialogLists() {
		
		m_hiddenDialogsChoice.clear();
		
		m_info_not_shown.clear();
		
		setReqSavingFlag();
	}
	
	private void environmentCheck(String configFile) 
	{
		
		File f = new File(configFile);
		
		if ( ! f.exists() ) { 
			
			if (! createConfigFile(configFile) ) System.exit(0);
			
		}
		
	}
}
