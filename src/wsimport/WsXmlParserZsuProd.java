/**
 * 
 */
package wsimport;

import static wsmain.WsLog.writeLog;
import static wsmain.WsUtils.NEXT_ROW;
import static wsmain.WsUtils.getMessagesStrs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import wsdatastruct.WsPrihodData;
import wsdatastruct.WsPrihodPartData;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsXmlParserZsuProd {

	private final String DOC_ELEMENT = "Документ_";
	
	private final String DATE_ELEMENT = "ДатаДок";
	
	private final String NOMER_ELEMENT = "НомерДок";
	
	private final String DATE2_ELEMENT = "ДатаПоставщика";
	
	private final String ROWS_DOC_ELEMENT = "рядки_документу";
	
	private final String ROW_ELEMENT = "Рядок_";
	
	private final String KOD_ELEMENT = "Продукт";
	
	private final String QUANTITY_ELEMENT = "Количество";
	
	private final String SUM_ELEMENT = "Сумма";
	
	private final String DATE_RASKL_ELEMENT = "ДатаРаскладки"; 
	
	private final String PID_RASHOD_ELEMENT = "ПодразделениеВЧ";
	
	private String m_errorState  = null;
	
	private WsParserResult m_result = WsParserResult.NONVALID_IOERROR;
	
	public enum WsParserResult {

		   VALID_PRIHOD,
		   VALID_RASHOD,
		   NONVALID_IOERROR,
		   NONVALID_FILE_ACCESS_ERROR,
		   NONVALID_PARSE_ERROR
		}
	
	public WsXmlParserZsuProd() {
		
		
	}
	
	public WsParserResult getResult() {
		
		return m_result;
	}
	
	public String getErrorMessage() {
		
		return m_errorState;
	}
	
	private WsPrihodPartData loadPrihodRow(XMLStreamReader reader) throws XMLStreamException {
		
		WsPrihodPartData d = new WsPrihodPartData();
		
		while (reader.hasNext()) {
			
			int event = reader.next();
			
			if (event == XMLStreamConstants.START_ELEMENT) { 
					
				if(reader.getLocalName().equals( KOD_ELEMENT)) {
				
					String s  = reader.getElementText();
					
					d.kod = Integer.valueOf(s);
				}
				else if(reader.getLocalName().equals( QUANTITY_ELEMENT) ) {
					
					String s  = reader.getElementText();
					
					s = s.replace(',', '.');
					
					d.quantity = Double.valueOf(s);
					
				}
				else if(reader.getLocalName().equals( SUM_ELEMENT) ) {
					
					String s  = reader.getElementText();
					
					s = s.replace(',', '.');
					
					d.costwithnds = Double.valueOf(s);
					
				}
				
			}
			else if (event == XMLStreamConstants.END_ELEMENT && 
					reader.getLocalName().contains(ROW_ELEMENT)) {
				
				break;
			}	
		}
		
		return d;
		
	}
	
	private Vector<WsPrihodPartData> loadPrihodRows(XMLStreamReader reader) throws XMLStreamException {
		
		Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>();
		
		while (reader.hasNext()) {
			
			int event = reader.next();
			
			if (event == XMLStreamConstants.START_ELEMENT && 
					reader.getLocalName().contains(ROW_ELEMENT)) {
				
				WsPrihodPartData d = loadPrihodRow(reader);
				
				vec.add(d);
			}
			else if (event == XMLStreamConstants.END_ELEMENT && 
					reader.getLocalName().contains(ROWS_DOC_ELEMENT)) {
				
				break;
			}
			
		}
		
		return vec;
		
	}
	
	private WsPrihodData loadPrihodNakl(XMLStreamReader reader) throws XMLStreamException {
		
		WsPrihodData d = new WsPrihodData();
		
		while (reader.hasNext()) {
		
			int event = reader.next();
			
			if (event == XMLStreamConstants.START_ELEMENT) {
				
				if (reader.getLocalName().equals(DATE_ELEMENT)) {
					
					String s  = reader.getElementText().substring(0, 10);
					
					d.date = WsUtils.stringToSqlDate(s, "dd.MM.yyyy");
					
				}
				else 
				if (reader.getLocalName().equals(NOMER_ELEMENT)) {
					
					d.number  = reader.getElementText();
					
				}
				else 
				if (reader.getLocalName().equals(DATE2_ELEMENT)) {
					
					String s  = reader.getElementText().substring(0, 10);
					
					d.date_doc = WsUtils.stringToSqlDate(s, "dd.MM.yyyy");
					
				}
				else 
				if (reader.getLocalName().contains(ROWS_DOC_ELEMENT)) {
					
					d.rows = loadPrihodRows(reader);
					
				}
			}
			else if (event == XMLStreamConstants.END_ELEMENT && 
					reader.getLocalName().contains(DOC_ELEMENT)) {
				
				break;
			}	
		}	
		
		return d;
	}
	
	public Vector<WsPrihodData> loadPrihod (String xmlFilePath) {

		InputStream	in = null;
		
		Vector<WsPrihodData> nakls = new Vector<WsPrihodData>();
		
		try 
		{
		
			in = new FileInputStream(xmlFilePath);	
		
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			//String xmlContent = new String(Files.readAllBytes(Paths.get("yourfile.xml")), StandardCharsets.UTF_8).trim();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
		
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT && 
						reader.getLocalName().contains(DOC_ELEMENT)) {
						
					WsPrihodData d = loadPrihodNakl(reader);
					
					nakls.add(d);
																	
				}
			}	
			
			reader.close(); 
			
			m_result = WsParserResult.VALID_PRIHOD;
		
		}
		catch (XMLStreamException e) {
		 
			writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
			
			m_errorState = getMessagesStrs("xmlCantReadFile");
			
			m_result = WsParserResult.NONVALID_PARSE_ERROR;

		}
		catch (IOException  e) {
			
			writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
			
			m_errorState = getMessagesStrs("xmlCantAccessFile");
			
			m_result = WsParserResult.NONVALID_FILE_ACCESS_ERROR;
		}
		finally {
			
			try {
				
				in.close();
				
			} 
			catch (IOException e) {
				
				m_errorState = "Unknown error";
				
				m_result = WsParserResult.NONVALID_IOERROR;
			}
		}
		
		return nakls;
	}
	
	public void checkXml (String xmlFilePath) {

		InputStream	in = null;
		
		try 
		{
		
			in = new FileInputStream(xmlFilePath);	
		
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			//String xmlContent = new String(Files.readAllBytes(Paths.get("yourfile.xml")), StandardCharsets.UTF_8).trim();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
		
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT && 
						reader.getLocalName().contains(DATE2_ELEMENT)) {
						
					m_result = WsParserResult.VALID_PRIHOD; //prihod
					
					break;
																	
				}
				if (event == XMLStreamConstants.START_ELEMENT && 
						reader.getLocalName().contains(DATE_RASKL_ELEMENT)) {
						
					m_result = WsParserResult.VALID_RASHOD; //rashod
					
					break;
																	
				}
			}	
			
			reader.close(); 
			
		}
		catch (XMLStreamException e) {
		 
			writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
			
			m_errorState = getMessagesStrs("xmlCantReadFile");
			
			m_result = WsParserResult.NONVALID_PARSE_ERROR;
		}
		catch (IOException  e) {
			
			writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
			
			m_errorState = getMessagesStrs("xmlCantAccessFile");
			
			m_result = WsParserResult.NONVALID_FILE_ACCESS_ERROR;
		}
		finally {
			
			try {
				
				in.close();
				
			} 
			catch (IOException e) {
				
				m_errorState = "Unknown error";
				
				m_result = WsParserResult.NONVALID_IOERROR;
			}
		}
	}
	
	public Vector<WsRashodData> loadRashod (String xmlFilePath) {

		InputStream	in = null;
		
		Vector<WsRashodData> nakls = new Vector<WsRashodData>();
		
		try 
		{
		
			in = new FileInputStream(xmlFilePath);	
		
			XMLInputFactory factory = XMLInputFactory.newInstance();
			
			//String xmlContent = new String(Files.readAllBytes(Paths.get("yourfile.xml")), StandardCharsets.UTF_8).trim();
			
			XMLStreamReader reader = factory.createXMLStreamReader(in);
		
			while (reader.hasNext()) {
				
				int event = reader.next();
				
				if (event == XMLStreamConstants.START_ELEMENT && 
						reader.getLocalName().contains(DOC_ELEMENT)) {
						
					WsRashodData d = loadRashodNakl(reader);
					
					nakls.add(d);
																	
				}
			}	
			
			reader.close(); 
			
			m_result = WsParserResult.VALID_RASHOD;
		
		}
		catch (XMLStreamException e) {
		 
			writeLog("XMLStreamException  : " + NEXT_ROW, e, true, false, true);
			
			m_errorState = getMessagesStrs("xmlCantReadFile");
			
			m_result = WsParserResult.NONVALID_PARSE_ERROR;

		}
		catch (IOException  e) {
			
			writeLog("IOException  : " + NEXT_ROW, e, true, false, true);
			
			m_errorState = getMessagesStrs("xmlCantAccessFile");
			
			m_result = WsParserResult.NONVALID_FILE_ACCESS_ERROR;
		}
		finally {
			
			try {
				
				in.close();
				
			} 
			catch (IOException e) {
				
				m_errorState = "Unknown error";
				
				m_result = WsParserResult.NONVALID_IOERROR;
			}
		}
		
		return nakls;
	}
	
	private WsRashodData loadRashodNakl(XMLStreamReader reader) throws XMLStreamException {
		
		WsRashodData d = new WsRashodData();
		
		while (reader.hasNext()) {
		
			int event = reader.next();
			
			if (event == XMLStreamConstants.START_ELEMENT) {
				
				if (reader.getLocalName().equals(DATE_ELEMENT)) {
					
					String s  = reader.getElementText().substring(0, 10);
					
					d.date = WsUtils.stringToSqlDate(s, "dd.MM.yyyy");
					
				}
				else 
				if (reader.getLocalName().equals(NOMER_ELEMENT)) {
					
					d.number  = reader.getElementText();
					
				}
				else 
				if (reader.getLocalName().equals(PID_RASHOD_ELEMENT)) {
					
					d.agentName  = reader.getElementText();
					
				}
				else 
				if (reader.getLocalName().contains(ROWS_DOC_ELEMENT)) {
					
					d.rows = loadRashodRows(reader);
					
				}
			}
			else if (event == XMLStreamConstants.END_ELEMENT && 
					reader.getLocalName().contains(DOC_ELEMENT)) {
				
				break;
			}	
		}	
		
		return d;
	}
	
	private Vector<WsRashodPartData> loadRashodRows(XMLStreamReader reader) throws XMLStreamException {
		
		Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>();
		
		while (reader.hasNext()) {
			
			int event = reader.next();
			
			if (event == XMLStreamConstants.START_ELEMENT && 
					reader.getLocalName().contains(ROW_ELEMENT)) {
				
				WsRashodPartData d = loadRashodRow(reader);
				
				vec.add(d);
			}
			else if (event == XMLStreamConstants.END_ELEMENT && 
					reader.getLocalName().contains(ROWS_DOC_ELEMENT)) {
				
				break;
			}
			
		}
		
		return vec;
		
	}
	private WsRashodPartData loadRashodRow(XMLStreamReader reader) throws XMLStreamException {
		
		WsRashodPartData d = new WsRashodPartData();
		
		while (reader.hasNext()) {
			
			int event = reader.next();
			
			if (event == XMLStreamConstants.START_ELEMENT) { 
					
				if(reader.getLocalName().equals( KOD_ELEMENT)) {
				
					String s  = reader.getElementText();
					
					d.kod = Integer.valueOf(s);
				}
				else if(reader.getLocalName().equals( QUANTITY_ELEMENT) ) {
					
					String s  = reader.getElementText();
					
					s = s.replace(',', '.');
					
					d.quantity = Double.valueOf(s);
					
				}
				else if(reader.getLocalName().equals( SUM_ELEMENT) ) {
					
					String s  = reader.getElementText();
					
					s = s.replace(',', '.');
					
					d.costwithnds = Double.valueOf(s);
					
				}
				
			}
			else if (event == XMLStreamConstants.END_ELEMENT && 
					reader.getLocalName().contains(ROW_ELEMENT)) {
				
				break;
			}	
		}
		
		return d;
		
	}
	
}




