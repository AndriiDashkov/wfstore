
package WsImport;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsImportExcelUtil {
	
	

	public static double getDoubleCell( XSSFRow row, int column_index) {
		
		XSSFCell cell = row.getCell((short)column_index);
		
		double quantity = 0.0;
		
		if(cell != null) {
		
			try {
				
				quantity = cell.getNumericCellValue();
				
			}catch(Exception e) {}
		
		}
		
		return quantity;
	
	}
	
	public static int getKodCell( XSSFRow row, int column_index) {
		
		XSSFCell cell = row.getCell((short)column_index);
		
		int kd  = -1;
		
		if(cell != null) {
		
			try {
				
				double quantity = cell.getNumericCellValue();
								
				kd = (int)quantity;
			
			}catch(Exception e) {
				
				 kd = -1;
			}
			
			if(kd == -1) {
				
				try {
					
					String str_kod = cell.getStringCellValue();
					
					double quantity = Double.valueOf(str_kod);
					
					kd = (int)quantity;
				
				}catch(Exception e) {
				
					kd = -1;
				}
					
			}
			
			if( kd == 0 || kd < 900  ) {
				
				kd = -1;
			}
		
		}
		
		return kd;
	
	}
	
	public static String getStringCell( XSSFRow row, int column_index) {
		
		XSSFCell cell = row.getCell((short)column_index);
		
		String name = "";
		
		if(cell != null) {
		
			try {
				
				name = cell.getStringCellValue();
				
			}catch(Exception e) {}
		
		}
		
		return name;
	
	}

}
