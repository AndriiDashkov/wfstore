
package WsImport;

import static WsMain.WsUtils.*;
import static WsMain.WsUtils.getMessagesStrs;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsPair;
import WsDataStruct.WsPartType;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDatabase.WsUtilSqlStatements;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsCatalogKods;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSExcelImport {
	

	 static public WsImportData getData( String excel_file_name, WFParseIndicies ind_schema) {
		 
		WsImportData dt = new WsImportData();
	    
	    HashMap<Integer, WsPartType> catalog = WsUtilSqlStatements.getPartTypesMap();
	    
	    double nds_coeff = WsUtils.getNdsCoeff();
		  
		try {
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
			
			 XSSFWorkbook wb = null;
			
			try {
			
				wb = new XSSFWorkbook( fStream );
		    
			}catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException ex) {
				
				wb.close();
				
				return null;
			}
			catch(java.lang.OutOfMemoryError ex) {
				
				wb.close();
				
				WsUtils.showMessageDialog(getMessagesStrs("outMemoryExcelImport"));
				
				return null;
			}
		    
		    XSSFSheet sheet = null;
		    
			try {
			    
				sheet = wb.getSheetAt(ind_schema.sheetIndex);
		    
			}  catch(java.lang.IllegalArgumentException ex) { 
				
				wb.close();
				
				return null; 
				
			}
		    
		    XSSFRow row;
		    
		    XSSFCell cell;
	
		    int rows; // No of rows
		    
		    rows = sheet.getPhysicalNumberOfRows();

		    
		    if(ind_schema.dateRowIndex != -1 && ind_schema.dateColumnIndex != -1) {
		    	
			    XSSFCell cell_date = sheet.getRow(ind_schema.dateRowIndex).getCell((short)ind_schema.dateColumnIndex);
		
			    dt.m_nakl_date = new java.sql.Date(cell_date.getDateCellValue().getTime());
		    }
		   
		    
		    for(int r = 0; r < rows; r++) {
		      
		        row = sheet.getRow(r);
		        
		        if(/*r > 4 &&*/ row != null) {
		        	
		        	if(row.getZeroHeight()) { continue; }
		        	
		        	cell = row.getCell((short)ind_schema.kodColumnIndex);
		        	
		        	if(cell == null) {continue;}
		        	
		        	double kod_d = -1.0;
		        	
		        	try {
		        		kod_d = cell.getNumericCellValue();
		        		
		        	} catch(Exception e) { continue; }
		        	
		        	int kod = (int)kod_d;
		        	
		        	kod   = WsCatalogKods.getKodFromDatabaseCatalog(kod, catalog);
        		
		        	if(kod == WsUtils.UNKNOWN_KOD) { continue; }
		        	
		        
	        		WFRowData d = new WFRowData();
	        		
	        		cell = row.getCell((short)ind_schema.quantityColumnIndex);
	        		
	        		if(cell == null) {continue;}
	        		
	        		double quantity = cell.getNumericCellValue();
	        		
	        		if(quantity == 0.0) { continue; }
	        		
	        		cell = row.getCell((short)ind_schema.nameColumnIndex);
	        		
	        		String name = "";
	        		
	        		if(cell != null) {
	        		
		        		try {
		        			
		        			name = cell.getStringCellValue();
		        			
		        			int index_fob_symbol = name.indexOf('\'');
		        			
		        			//workaround for sql forbidden symbols
		        			if(index_fob_symbol != -1) {
		        					
		        				name = name.replace('\'', ' ');
		        			}
		        			
		        		} catch(Exception e) { name =""; };
	        		
	        		}
	        		
	        		if(name.isEmpty()) { 
	        			
	        			name =  WsUtilSqlStatements.getPartTypeForKod(kod).name;
	        		}
	        		
	        		cell = row.getCell((short)ind_schema.unitsColumnIndex);
	        		
	        		String nameUnit = getGuiStrs("units_kg_DatabaseName");
	        		      		
	        		try {
		        		if(cell != null) {
		        			
		        			nameUnit = cell.getStringCellValue();
		        		}
	        		} catch(Exception e) { 
	        			
	        			nameUnit = getGuiStrs("units_kg_DatabaseName");;
	        		};
	        		
	        		if(WsUtils.isKodEqual(kod , WsUtils.EGG_KOD_1) || WsUtils.isKodEqual(kod , WsUtils.EGG_KOD_2)) {
	        			

	        			nameUnit = getGuiStrs("units_sht_DatabaseName");;
	        		}
	        		
	        		
	        		d.kod = kod;
	        		
	        		d.name = name;
	        		
	        		d.quantity = quantity;
	        		
	        		d.units = nameUnit;
	        		
	        		WsPartType c_data = catalog.get(d.kod);
	        		
	        		if(c_data != null) {
	        			
	        			d.cost = WsUtils.getDF_fix(c_data.costwithnds/nds_coeff, 4);
	        			
	        			d.nds = c_data.costwithnds - d.cost;
	        		
	        		}
	        		
	        		dt.m_data.add(d);
		        		
		        
		        }
		    }
		    
		    wb.close();
		    
		} catch(Exception ioe) {
			
		    ioe.printStackTrace();
		    
			return null;
		}
		
		return dt;
	
	}
	 
	 static public Vector<WFRowData>  getDataFromRaskladkaSum( String excel_file_name, WFParseIndicies schema) {
		 
		 Vector<WsImportData> data = getDataFromRaskladka( excel_file_name, schema);
	 		 
		 HashMap<Integer,  WFRowData > map = new HashMap<Integer, WFRowData >();
		 
		 for(int i = 0; i < 7; ++i) {
			 
			 Vector<WFRowData> dV = data.elementAt(i).m_data;
			 
			 for(int j = 0; j < dV.size(); ++j) {
				 
				 WFRowData d = dV.elementAt(j);
				 
				 if(map.containsKey(d.kod)) {
					 
					 map.get(d.kod).quantity += d.quantity;
				 }
				 else {
					 
					 map.put(d.kod, d);
				 }
				 
			 }
		 }
		 
		 ArrayList<Integer> list = new ArrayList<Integer>(map.keySet());
		 
		 Collections.sort(list);
		 
		 Vector<WFRowData> out_vec = new Vector<WFRowData>();
		 
		 for(Integer k : list) {
			 
			 out_vec.add(map.get(k));
		 }
		 
		 map.clear();
		 
		 data.clear();
		 
		 return out_vec;
	 }
	 
	static public  Vector<WsImportData> getDataFromRaskladka( String excel_file_name, WFParseIndicies schema) {
		 
	    HashMap<Integer, WsPartType> catalog = WsUtilSqlStatements.getPartTypesMap();
	    
		WsCatalogKods kt = new WsCatalogKods();
		 
		Vector<WsImportData> vec = null;
	
		double nds_coeff = WsUtils.getNdsCoeff();
		
		try {
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
			
			XSSFWorkbook wb = null;
			
		    try {
			
		    	wb = new XSSFWorkbook( fStream );
		    
		    } catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
		    	
		    	wb.close();
		    	
		    	return null;
		    }
			catch(java.lang.OutOfMemoryError ex) {
				
				wb.close();
				
				WsUtils.showMessageDialog(getMessagesStrs("outMemoryExcelImport"));
				
				return null;
			}
		    
		    vec = getPeopleFromRaskladka(wb, schema);
		    
		    if(vec == null || vec.isEmpty()) { return null; }
		    
		    XSSFSheet sheet = null;
	
			int sheetIndex = schema.sheetIndex; //3
			   
			sheet = wb.getSheetAt(sheetIndex);
			    
		    int kods_row_index = schema.kodRowIndex; //10;
		    
		    Integer[] sum_rows_indices = {schema.mondaySumIndex, schema.tuesdaySumIndex,
		    		schema.wednesdaySumIndex, schema.thursdaySumIndex, schema.fridaySumIndex,
		    		schema.sartudaySumIndex, schema.sundaySumIndex};
		    
		    XSSFRow[] rows_week = {null, null, null, null, null, null, null};
		    
		    XSSFRow row_kods = sheet.getRow(kods_row_index);
		    
		    for(int iw = 0; iw < 7; ++iw) {
		    	
		    	rows_week[iw] = sheet.getRow(sum_rows_indices[iw]);
		    }
		   
		    int kods_start_column_index = schema.quantityColumnIndex; //7;
				 
		    do {
					      
			     XSSFCell cell_kod = row_kods.getCell( kods_start_column_index);
			     	   
				 int kod = -1;
			        	
	        	 try {
	        		
	        		int kod0 = (int)cell_kod.getNumericCellValue();
	        		
	        		kod = kt.getKodFromCatalog(kod0);
	        		
	        		if(kod ==  WsUtils.UNKNOWN_KOD ) { 
	        			
	        			break ;
	        		}
	        		
	        	 } catch(Exception e) { break; }
			        	
			     for(int j = 0; j < 7;  ++j) {
			        		
			        	WsImportData dt =   vec.get(j);
			        		
			        	XSSFCell  cell = rows_week[j].getCell(kods_start_column_index);
			        		
			        	double value = cell.getNumericCellValue();
			        		
			        	if(value != 0.0) {
			        		
			        		
			        			WFRowData dt_row = new WFRowData();
			        			
				        		WsPartType c_data = catalog.get(kod);
				        		
				        		if(c_data != null) {
				        			
				        			dt_row.kod = kod;
				        			
				        			dt_row.quantity = value/1000.0;
				        			
				        			dt_row.cost = WsUtils.getDF_fix(c_data.costwithnds/nds_coeff, 4);
				        			
				        			dt_row.nds = c_data.costwithnds - dt_row.cost;
				        			
				        			dt_row.name = c_data.name;
				        		
				        		}
				        		else {
				        			
				        			dt_row.quantity = value/1000.0;
				        			
				        			dt_row.name = "?? not found in the catalog : " + String.valueOf(kod);
				        			
				        			dt_row.kod =  WsUtils.getUnknownKatalogKod();
				        			
				        		}
			        			
			        			dt.m_data.add(dt_row);
			        			
			        	}
			        		
			        		
			        }
					  
			      ++kods_start_column_index;
					   
				}while(true);
				 
				 		      
		    wb.close();
		    
		} catch( Exception ioe ) {
			
		   // ioe.printStackTrace();
		    
		    return null;
		    
		}
		
		return vec;
	
	}
	
	
	static public  Vector<WsImportData> getDataFromRaskladkaNoKodControl( String excel_file_name, WFParseIndicies schema) {
		 
	    HashMap<Integer, WsPartType> catalog = WsUtilSqlStatements.getPartTypesMap();
	    
		Vector<WsImportData> vec = null;
	
		double nds_coeff = WsUtils.getNdsCoeff();
		
		try {
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
			
			XSSFWorkbook wb = null;
			
		    try {
			
		    	wb = new XSSFWorkbook( fStream );
		    
		    } catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
		    	
		    	wb.close();
		    	
		    	return null;
		    }
			catch(java.lang.OutOfMemoryError ex) {
				
				wb.close();
				
				WsUtils.showMessageDialog(getMessagesStrs("outMemoryExcelImport"));
				
				return null;
			}
		    
		    vec = getPeopleFromRaskladka(wb, schema);
		    
		    if(vec == null || vec.isEmpty()) { return null; }
		    
		    XSSFSheet sheet = null;
	
			int sheetIndex = schema.sheetIndex; //3
			   
			sheet = wb.getSheetAt(sheetIndex);
			    
		    int kods_row_index = schema.kodRowIndex; //10;
		    
		    Integer[] sum_rows_indices = {schema.mondaySumIndex, schema.tuesdaySumIndex,
		    		schema.wednesdaySumIndex, schema.thursdaySumIndex, schema.fridaySumIndex,
		    		schema.sartudaySumIndex, schema.sundaySumIndex};
		    
		    XSSFRow[] rows_week = {null, null, null, null, null, null, null};
		    
		    XSSFRow row_kods = sheet.getRow(kods_row_index);
		    
		    for(int iw = 0; iw < 7; ++iw) {
		    	
		    	rows_week[iw] = sheet.getRow(sum_rows_indices[iw]);
		    }
		   
		    int kods_start_column_index = schema.quantityColumnIndex; //7;
				 
		    do {
					      
			     XSSFCell cell_kod = row_kods.getCell( kods_start_column_index);
			     	   
				 int kod = -1;
			        	
	        	 try {
	        		
	        		kod = (int)cell_kod.getNumericCellValue();
	        		
	        		if(kod > WsUtils.CATALOG_MAX_KOD || kod < WsUtils.CATALOG_MIN_KOD ) { 
	        			
	        			break ;
	        		}
	        		
	        	 } catch(Exception e) { break; }
			        	
			     for(int j = 0; j < 7;  ++j) {
			        		
			        	WsImportData dt =   vec.get(j);
			        		
			        	XSSFCell  cell = rows_week[j].getCell(kods_start_column_index);
			        		
			        	double value = cell.getNumericCellValue();
			        		
			        	if(value != 0.0) {
			        		
			        		
			        			WFRowData dt_row = new WFRowData();
			        			
			        			dt_row.kod = kod;
			        			
			        			dt_row.quantity = value/1000.0;
			        			
				        		WsPartType c_data = catalog.get(kod);
				        		
				        		if(c_data != null) {
				        			
				        			dt_row.cost = WsUtils.getDF_fix(c_data.costwithnds/nds_coeff, 4);
				        			
				        			dt_row.nds = c_data.costwithnds - dt_row.cost;
				        			
				        			dt_row.name = c_data.name;
				        		
				        		}
				        		else {
				        			
				        			dt_row.quantity = value/1000.0;
				        			
				        			dt_row.name = "?? not found in the catalog : " + String.valueOf(kod);
				        				
				        		}
			        			
			        			dt.m_data.add(dt_row);
			        			
			        	}
			        		
			        		
			        }
					  
			      ++kods_start_column_index;
					   
				}while(true);
				 
				 		      
		    wb.close();
		    
		} catch( Exception ioe ) {
			
		   // ioe.printStackTrace();
		    
		    return null;
		    
		}
		
		return vec;
	
	}
	
	
	static public Vector<WsImportData> getFullDataFromRaskladka( XSSFWorkbook wb , WFParseIndicies schema) {
		 

	    Vector<WsImportData> vec_data = new Vector<WsImportData >();
		
		WsCatalogKods kt = new WsCatalogKods();
		 	
		try {
			
			if(wb == null) { return null; }
		    
		    Vector<WsImportData> vec_people = getPeopleFromRaskladka(wb, schema);
		    
		    if(vec_people == null || vec_people.isEmpty()) { return null; }
		    	
			int sheetIndex = schema.sheetIndex; //3
			   
			XSSFSheet sheet = wb.getSheetAt(sheetIndex);
			    
		    int kods_row_index = schema.kodRowIndex; //10;
		    
		   Vector<Integer> sum_rows_indices = new Vector<Integer>();
		    	
		   sum_rows_indices.add(schema.mondaySumIndex);
		   
		   sum_rows_indices.add(schema.tuesdaySumIndex);
		   
		   sum_rows_indices.add(schema.wednesdaySumIndex); 
		   
		   sum_rows_indices.add(schema.thursdaySumIndex); 
		   
		   sum_rows_indices.add(schema.fridaySumIndex);
		   
		   sum_rows_indices.add(schema.sartudaySumIndex); 
		   
		   sum_rows_indices.add(schema.sundaySumIndex);
		    

		    XSSFRow row_kods = sheet.getRow(kods_row_index);
		   
		    int kods_start_column_index = schema.quantityColumnIndex; //7;
		    
		    do {
					      
			     XSSFCell cell_kod = row_kods.getCell( kods_start_column_index);
			     	   
				 int kod = -1;
			        	
	        	 try {
	        		
	        		kod = (int)cell_kod.getNumericCellValue();
	        		
	        		kod = kt.getKodFromCatalog(kod);
	        		
	        		if( kod == WsUtils.UNKNOWN_KOD ) { 
	        			
	        			break ;
	        		}
	        		
	        	 } catch(Exception e) { break; }
	        	 
	        	 WsImportData data = new WsImportData();
	        	 
	        	 data.kod = kod;
	        	 
	        	 data.column_index = kods_start_column_index;
	        	 
	        	 vec_data.add(data);
			     
	        	 Vector<WFRowData > vec_rows =  data.m_data; 
	        	 
	        	 int people_index = 0 ;
	        	 
	        	 int current_people = vec_people.elementAt(people_index).people[0];
	        	 
	        	 for(int j = 12; j < 223;  ++j) {
	        		 
	        		 if(sum_rows_indices.contains(j)) { 
	        			 
	        			 current_people = vec_people.elementAt(++people_index).people[0];
	        			 
	        			 continue; 
	        		 }
	        		 
	        		 XSSFRow row_current = sheet.getRow(j);
	        		 
	        		 XSSFCell cell_current = row_current.getCell( kods_start_column_index);
	        		 
	        		 double value = cell_current.getNumericCellValue();
	        		 
	        		 if(value != 0.0) {
	        			 
	        			 WFRowData d = new WFRowData();
	        			 
	        			 d.quantity = value;
	        			 
	        			 d.people = current_people;
	        			 
	        			 d.row_index = j;
	        			 
	        			 vec_rows.add(d);
	        			 
	        		 }
	        	 
	        	 }
					  
			     ++kods_start_column_index;
					   
			}while(true);
					    
		} catch( Exception ioe ) {
			
		   // ioe.printStackTrace();
		    
		    return null;
		    
		}
		
		return vec_data;
	
	}
	
	
	//reads the catalog prices form raskladka
	static public Vector<WsPartType> getDataFromRaskladkaKatalogCost( String excel_file_name, WFParseIndicies schema) {
		 
		Vector<WsPartType>  vec = new  Vector<WsPartType>();
		 
		WsCatalogKods kt = new WsCatalogKods();
		
		try {
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
			
			XSSFWorkbook wb = null;
			
		    try {
			
		    	wb = new XSSFWorkbook( fStream );
		    	
		    
		    } catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
		    	
		    	wb.close();
		    	
		    	return null;
		    }
			catch(java.lang.OutOfMemoryError ex) {
				
				wb.close();
				
				WsUtils.showMessageDialog(getMessagesStrs("outMemoryExcelImport"));
				
				return null;
			}
	
			int sheetIndex = schema.sheetIndex; //3
			
		    XSSFSheet sheet = null;
		    
			try {
			    
				sheet = wb.getSheetAt(sheetIndex);
		    
			}  catch(java.lang.IllegalArgumentException ex) { 
				
				wb.close();
				
				return null; 
				
			}
	
		    int kods_row_index = schema.kodRowIndex; //10;
		    
		    int kods_column_index = schema.kodColumnIndex; //7;
		    
		    int cost_column_index = schema.costColumnIndex; //7;
				 
		    do {
					
		    	XSSFRow row_kod = sheet.getRow(kods_row_index++);
		    	
		    	if(null == row_kod) { break; }
		  
			    XSSFCell cell_kod = row_kod.getCell( kods_column_index, MissingCellPolicy.RETURN_NULL_AND_BLANK );
			    
			    if(null == cell_kod) { break; }
	   
				int kod = -1;
			        	
	        	try {
	        		
	        		kod = (int)cell_kod.getNumericCellValue();
	        		
	        		kod = kt.getKodFromCatalog(kod);
	        		
	        		if( kod == WsUtils.UNKNOWN_KOD ) { 

	        			continue ;
	        		}
	        		
	        	 } catch(Exception e) { continue; }
	        	
	        	
	        	XSSFCell cell_cost = row_kod.getCell(cost_column_index);
	        	
	        	double cost = 0.0;
	        	
	        	try {
	        		
	        		cost = cell_cost.getNumericCellValue();
	        		
	        	 } catch(Exception e) {  }
	        	
	        	WsPartType dt_row = new WsPartType();
    			
    			dt_row.kod = kod;
    			
    			dt_row.costwithnds = cost;
   
				vec.add(dt_row);	
					
				if(kods_row_index > 10000) { break; }
    			
			}while(true);
				 
				 		      
		    wb.close();
		    
		} catch( Exception ioe ) {
			
		    //ioe.printStackTrace();
		    
		    return null;
		    
		}
		
		return vec;
	
	}
	
	

	static public Vector<WFRowData> getDataFromKartkaZvit( String excel_file_name, WFParseIndicies schema) {
		 
		
		Vector<WFRowData> vec = new Vector<WFRowData>();
		
		HashMap<Integer, WsPartType> catalog = WsUtilSqlStatements.getPartTypesMap();

		try {
			
			FileInputStream fStream = new FileInputStream(excel_file_name);
			
			XSSFWorkbook wb = null;
			
		    try {
			
		    	wb = new XSSFWorkbook( fStream );

		    
		    } catch(org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException e) {
		    	
		    	wb.close();
		    	
		    	return null;
		    }
			catch(java.lang.OutOfMemoryError ex) {
				
				wb.close();
				
				WsUtils.showMessageDialog(getMessagesStrs("outMemoryExcelImport"));
				
				return null;
			}
		    				    
		    XSSFSheet sheet = null;
		    
			int sheetIndex = schema.sheetIndex; //3
			   
			sheet = wb.getSheetAt(sheetIndex);
			    
		    int kods_row_index = schema.kodRowIndex; //10;
		    
		    int data_row_index = schema.quantityColumnIndex;
		    
		    int kods_start_column_index = schema.kodColumnIndex; //7;
		    
		    XSSFRow data_row = sheet.getRow(data_row_index);
		    
		    XSSFRow row_kods = sheet.getRow(kods_row_index);
		  				 
		    do {

				     XSSFCell cell_kod = row_kods.getCell( kods_start_column_index);
				     
				     XSSFCell cell_data = data_row.getCell( kods_start_column_index);
				     
				     if(cell_kod == null || cell_data == null) { break; }
				       
					 int kod = -1;
				        	
		        	 try {
		        		
		        		kod = (int)cell_kod.getNumericCellValue();
		        		
		        		//the transformation of kod value into value from database; 
		        		//kods 11203 and 1203 are treated as same in order to work with new 5-digit codes
		        		kod   = WsCatalogKods.getKodFromDatabaseCatalog(kod, catalog);
		        			
		        		if( kod == WsUtils.UNKNOWN_KOD ) {
		        			
		        			++kods_start_column_index;
		        			
		        			if(kods_start_column_index > 10000) { break; }
		        			
		        			continue ;
		        		}
		        		
		        		double data = cell_data.getNumericCellValue();
		        		
		        		
		        		WFRowData dt_row = new WFRowData();
	        			
	        			dt_row.kod = kod;
	        			
	        			dt_row.quantity = data;
	        			
	        			dt_row.costwithnds = catalog.get(kod).costwithnds;
	        			
	        			dt_row.cost = dt_row.costwithnds / WsUtils.getNdsCoeff();
	        			
	        			dt_row.nds = dt_row.costwithnds - dt_row.cost;
	        					
	        			vec.add(dt_row);
		        		
		        	 } catch(Exception e) { break; }
						  
				      ++kods_start_column_index;
				      
				      //the last line of defence
				      if(kods_start_column_index > 10000) { break; }
					   
				}while(true);
				 
				 		      
		    wb.close();
		    
		} catch( Exception ioe ) {
			
		   // ioe.printStackTrace();
		    
		    return null;
		    
		}
		
		return vec;
	
	}

	public static Vector<WsImportData> getPeopleFromRaskladka(XSSFWorkbook wb, WFParseIndicies schema) {
		
	    Vector<WsImportData> vec_people = new Vector<WsImportData >();
	    
	    for(int i = 0; i < 7; ++i) {
			 
			 vec_people.add(new WsImportData());
		 }
	    
	    XSSFSheet sheet = null;
	    
		try {
			
			int sheet_people_index = schema.peopleSheetIndex;
		    
			sheet = wb.getSheetAt(sheet_people_index);
			
			for(int i =0; i < 7; i++) {
				
				  XSSFRow row = sheet.getRow(i + schema.peopleStartRowIndex );
				 
				  XSSFCell cell0 = row.getCell( schema.peopleStartColumnIndex);
				  
				  XSSFCell cell1 = row.getCell( schema.peopleStartColumnIndex + 2);
				  
				  XSSFCell cell2 = row.getCell( schema.peopleStartColumnIndex + 4);
				  
				  double value0 = cell0.getNumericCellValue();
				  
				  double value1 = cell1.getNumericCellValue();
				  
				  double value2 = cell2.getNumericCellValue();
				  
				  WsImportData dt  =  vec_people.elementAt(i);
				  
				  dt.people[0] = (int)value0;
				  
				  dt.people[1] = (int)value1;
				  
				  dt.people[2] = (int)value2;
				
			}
	    
		}  catch(java.lang.IllegalArgumentException ex) { 
			
			return null; 
			
		}
		
		return vec_people;
		
	}
	
	
	public static WsPair getDataFromRaskladkaSet(Vector<WsAgentData> data_vec, boolean loadDataFromTable,
			boolean noKodControl) {
		
		WsPair return_pair = new WsPair();
		
		int out_people_sum = 0;
		
		HashMap<Integer, WsPartType> catalog = WsUtilSqlStatements.getPartTypesMap();
		
		HashMap<Integer, WsSkladMoveDataColumn> map = new HashMap<Integer, WsSkladMoveDataColumn>();
		
		return_pair.complex = map;
		
		String excel_file_name = null;
		
		int map_index = 1;
		
		WFParseIndicies schema =  new WFParseIndicies(TYPE.RASKLADKA);

	    for(WsAgentData fc : data_vec){
				  
			try {
				
				//out_people_sum += fc.quantity[0];
				
				excel_file_name = fc.contact;
				
				if(excel_file_name == null || excel_file_name.isEmpty()) {   continue; }
				
				Vector<WsImportData> vec_data = null;
				
				if(noKodControl) {
					
					 vec_data =  WSExcelImport.getDataFromRaskladkaNoKodControl(excel_file_name, schema);
				
				}
				else {
					
					 vec_data =  WSExcelImport.getDataFromRaskladka(excel_file_name, schema);
				}
				
				int out_people_sum_week_avr = 0;
				 
				for(int i = 0; i < 7; ++i) {
					 
					WsImportData d = vec_data.elementAt(i);
					
					int people = fc.quantity[0];
					
					if(!loadDataFromTable) {
						
						people = (d.people[0] + d.people[1] + d.people[2])/3;
						
						out_people_sum_week_avr += people;
					}

					for(WFRowData dt : d.m_data) {
						
						int kod = dt.kod;
												
						double kf = 1.0;
						
						kod  = WsCatalogKods.getKodFromDatabaseCatalog(kod, catalog);
						
						if(noKodControl && kod == WsUtils.UNKNOWN_KOD) {
							
							kod = dt.kod;
						}
						
						if( WsUtils.isKodEqual(kod, WsUtils.EGG_KOD_1) ||  WsUtils.isKodEqual(kod, WsUtils.EGG_KOD_2)) {
							
							kf = 1000.0;
						}
						

						if(map.containsKey(kod)) {
							
							map.get(kod).q_array[map_index].out_quantity += dt.quantity*kf*people;
						}
						else {
							
							WsSkladMoveDataColumn dc = new WsSkladMoveDataColumn();
							
							dc.kod = kod;
							
							dc.name = dt.name;
							
							dc.q_array[map_index].out_quantity = dt.quantity*kf*people;
							
							map.put(kod, dc);
						}
					} 
				 }
				
				if(loadDataFromTable) {
					
					out_people_sum += fc.quantity[0];
				}
				else {
					
					out_people_sum += (int)(out_people_sum_week_avr/7.0);
				}
					    
			} catch(Exception ioe) {
							
				ioe.printStackTrace();
						    
			}
	    }
		
	    return_pair.value = out_people_sum ;
				
		return return_pair;
		
	}

}
