
package WsImport;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WFParseIndicies {
	
	public int sheetIndex = -1;
	
	public int kodColumnIndex = -1;
	
	public int quantityColumnIndex = -1;
	
	public int minKodValue = 1;
	
	public int maxKodValue = 20000000;
	
	public int costColumnIndex = -1;
	
	public int dateRowIndex = -1;
	
	public int dateColumnIndex = -1;
	
	public int nameColumnIndex = -1;
	
	public int unitsColumnIndex = -1;
	
	public int mondaySumIndex = -1;
	
	public int tuesdaySumIndex = -1;
	
	public int wednesdaySumIndex = -1;
	
	public int thursdaySumIndex = -1;
	
	public int fridaySumIndex = -1;
	
	public int sartudaySumIndex = -1;
	
	public int sundaySumIndex = -1;
	
	public int kodRowIndex = -1;
	
	public int nameRowIndex = -1;
	
	public int peopleStartRowIndex = -1;
	
	public int peopleStartColumnIndex = -1;
	
	public int peopleSheetIndex = -1;
	
	
	public enum TYPE{
	
		RASKLADKA,
		NAKL,
		KARTZVIT,
		KARTZVITRASKLADKA,
		CATALOGRASKLADKA,
		PRODREST,
		NOTYPE
		
		
	};
	
	public TYPE type = TYPE.NOTYPE;
	
	public WFParseIndicies() {
		
		
	}
	
	public WFParseIndicies(TYPE t) {
		
		type = t;
		
		switch(t) {
		
			case RASKLADKA: {
				
				sheetIndex = 3;
					
				kodRowIndex = 10;
				
				nameRowIndex = 12;
				
				quantityColumnIndex = 7;
				 
				mondaySumIndex = 42;
		 
				tuesdaySumIndex = 73;
		
				wednesdaySumIndex = 104;
		
				thursdaySumIndex = 135;
		
				fridaySumIndex = 166;
		
				sartudaySumIndex = 197;
		
				sundaySumIndex = 228;
				 
				peopleStartRowIndex = 9;
					
			    peopleStartColumnIndex = 3;
					
				peopleSheetIndex = 0;
				
				break;
				 	
			}
			case KARTZVITRASKLADKA: {
				
				sheetIndex = 3;
					
				kodRowIndex = 2;
				
				nameRowIndex = 3;
				
				quantityColumnIndex = 3;
				 
				mondaySumIndex = 34;
		 
				tuesdaySumIndex = 65;
		
				wednesdaySumIndex = 96;
		
				thursdaySumIndex = 127;
		
				fridaySumIndex = 158;
		
				sartudaySumIndex = 189;
		
				sundaySumIndex = 220;
				 
				break;
				 	
			}
			case NAKL : { 
				
				sheetIndex = 10;
				
				kodColumnIndex = 0;
			
				nameColumnIndex = 1;
				
				quantityColumnIndex = 6;
				
				unitsColumnIndex = 2;
				
				break; 
				
			}
			
			case KARTZVIT  : { 

				sheetIndex = 0;
				
				nameColumnIndex = 8;

				kodColumnIndex = 9;

				quantityColumnIndex = 22;
				
				break;
				
			
			}
			case CATALOGRASKLADKA  : { 

				sheetIndex = 4;
				
				nameColumnIndex = 7;

				kodColumnIndex = 0;

				quantityColumnIndex = 4;
				
				break;
				
			
			}
			
			case PRODREST : { 

				sheetIndex =0;
			
				kodColumnIndex = 0;
		
				nameColumnIndex = 1;
			
				quantityColumnIndex = 5;
			
				unitsColumnIndex =2;
				
				break;
			
			}
			
			default: {
				
				break;
			}
		
		};
	}

}
