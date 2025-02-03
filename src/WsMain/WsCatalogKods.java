
package WsMain;

import static WsMain.WsUtils.getGuiStrs;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import WsDataStruct.WsMoveKodPage;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsSkladMoveDataColumn;

/**
 * 
 * This class represents the catalog of food with appropriate 'kods'.
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsCatalogKods {
	
	HashMap<Integer, WsSkladMoveDataColumn> m_map = new HashMap<Integer, WsSkladMoveDataColumn>();
	
	public WsCatalogKods() {
		
		if(WsUtils.NEW_CATALOG) {
			
			WsUtils.CATALOG_PREFIX = "kn";
			
			kodInitNewCatalog();
		}
		else {
			
			WsUtils.CATALOG_PREFIX = "k";
			
			kodInitOldCatalog();
		}
		
	}
	
	public Integer[] getSortedKods() {
		

		Integer[] keys = m_map.keySet().toArray(new Integer[0]);
		
		Arrays.sort(keys);
		
		return keys;
		
		
	}
	
	public HashMap<Integer, WsSkladMoveDataColumn> getKodsTable() { return m_map; }
	
	public HashMap<Integer, WsPrihodPartData> getKodsTable2() { 
		
		HashMap<Integer, WsPrihodPartData> map2 = new HashMap<Integer, WsPrihodPartData>();
		

        for (Map.Entry<Integer, WsSkladMoveDataColumn> entry : m_map.entrySet()) {
        	
            Integer key = entry.getKey();
            
            WsSkladMoveDataColumn value = entry.getValue();
            
            WsPrihodPartData d = new WsPrihodPartData();
            
    		d.kod = key;
    		
    		d.vendorcode2 = String.valueOf(key);
    		
    		d.name = value.name;
    		
    		map2.put(key, d);
           
        }
        
		return map2; 
		
	}
	
	public HashMap<Integer, WsRashodPartData> getKodsTable3() { 
		
		HashMap<Integer, WsRashodPartData> map2 = new HashMap<Integer, WsRashodPartData>();
		

        for (Map.Entry<Integer, WsSkladMoveDataColumn> entry : m_map.entrySet()) {
        	
            Integer key = entry.getKey();
            
            WsSkladMoveDataColumn value = entry.getValue();
            
            WsRashodPartData d = new WsRashodPartData();
            
    		d.kod = key;
    		
    		d.vendor_code_2 = String.valueOf(key);
    		
    		d.name = value.name;
    		
    		map2.put(key, d);
           
        }
		
		return map2; 
		
	}
	
	public HashMap<Integer, WsMoveKodPage> getKodsTable4() { 
		
		HashMap<Integer, WsMoveKodPage> map2 = new HashMap<Integer, WsMoveKodPage>();

        for (Map.Entry<Integer, WsSkladMoveDataColumn> entry : m_map.entrySet()) {
        	
            Integer key = entry.getKey();
            
            WsSkladMoveDataColumn value = entry.getValue();
            
            WsMoveKodPage d = new WsMoveKodPage();
            
    		d.kod = key;
    		
    		d.name_kod = value.name;
    		
    		map2.put(key, d);
           
        }
		
		return map2; 
		
	}
	
	public String getName(int kod) { 
		
		WsSkladMoveDataColumn d =  m_map.get(kod);
		
		if(d == null) { return null; }
		
		return d.name; 
		
	}
	
	void putToMaps( int i) {
		
		WsSkladMoveDataColumn d1 = new  WsSkladMoveDataColumn();
		
		String name = getGuiStrs(WsUtils.CATALOG_PREFIX + String.valueOf(i));
		
		d1.kod = i;
		
		d1.name = name;
		
		m_map.put(i, d1);
			
	}
	
	private void kodInitOldCatalog() {
		
		for(int i = 1001; i <= 1065; ++i) {
			
			putToMaps(i);
			
		}

	
		for(int i = 1201; i <= 1213; ++i) {
			
			putToMaps(i);
			
		}
		

		for(int i = 1301; i <= 1308; ++i) {
			
			putToMaps(i);
			
		}
		
		for(int i = 1401; i <= 1421; ++i) {
			
			putToMaps(i);
			
		}
		
		for(int i = 1501; i <= 1505; ++i) {
			
			putToMaps(i);
			
		}
	
		for(int i = 1601; i <= 1635; ++i) {
			
			putToMaps(i);
			
		}
		
		for(int i = 2001; i <= 2074; ++i) {
			

			putToMaps(i);
			
		}

		for(int i = 2201; i <= 	2237; ++i) {
			

			putToMaps(i);
			
		}
		
		for(int i = 2301; i <= 	2308; ++i) {
			
			putToMaps(i);
			
		}
		
		for(int i = 3001; i <= 	3021; ++i) {
			
			putToMaps(i);
			
		}

		for(int i = 3101; i <= 	3124; ++i) {
			

			putToMaps(i);
			
		}


		for(int i = 4001; i <= 4034; ++i) {
			
			putToMaps(i);
			
		}
	
		for(int i = 4101; i <= 4143; ++i) {
			

			putToMaps(i);
			
		}
		
		for(int i = 5001; i <= 5021; ++i) {
			

			putToMaps(i);
			
		}

	}
	
	private void kodInitNewCatalog() {
		
		for(int i = 1001; i <= 1065; ++i) {
			
			 //1003 ->1004 1018->1019  1027->1026 1035->1033 1036->1034 1037->1033 1038->1034 1056->1057
			
			if(i != 1003 && i != 1018 && i != 1027 && i != 1035 && i != 1036 && i != 1037 && i != 1038 && i != 1056) {
				
				putToMaps(i);
			}
			
		}

		for(int i = 1201; i <= 1215; ++i) {
			
			//1207-> 1026
			
			if(i != 1207) {
				
				putToMaps(i);
			}
			
		}

		for(int i = 1301; i <= 1303; ++i) {
			
			//1303 ->1406    1304->1408 1305->1409 1306->1410 1307->1415 1308->1411

			putToMaps(i);

		}
		
		putToMaps(1309);
		
		putToMaps(1310);
		
		for(int i = 1401; i <= 1424; ++i) {
			
			//1418 -> 1419
			
			if(i != 1418 && i != 1421) {
				
				putToMaps(i);
			}
			
		}
		
		for(int i = 1501; i <= 1505; ++i) {
			
			putToMaps(i);
			
		}
	
		for(int i = 1602; i <= 1639; ++i) {
			
			//1601->1602
			
			if(i != 1632) {
					
				putToMaps(i);
			
			}
			
		}
		
		for(int i = 2001; i <= 2076; ++i) {
			
			//2002 ->2003  2006->2007 2015->2016  2017,2018,2019->2020 2021->2020 2027->2026 2045->2044
			//2048->2047 2050->2049 2053->2054 2059->2058  2063->2062 2067-> 2069 2068->2069
			//2070->2069 2071->2069 2072->2069 2073->2069
			
			if(i == 2002 || i == 2006 || i == 2015 || i == 2017 || i == 2018 || i == 2019  || i == 2021  || i == 2027
					|| i == 2045 || i == 2048 || i == 2050 || i == 2053 || i == 2059 || i == 2063 || i == 2067
							|| i == 2068 || i == 2070 || i == 2071|| i == 2072 || i == 2073) {
				
				continue;
			}

			putToMaps(i);

		}

		for(int i = 2201; i <= 	2237; ++i) {
			
			//2203 ->2202  2206->2205   2207->2208 2210->2209 2212->2211   2214->2213 2220->2221
			//2222->2224 2223->2224 2230->2231 2232->2231 2233->2231 2234->2231 2236->2235
			
			if(i == 2203 || i == 2206 || i == 2207 || i == 2210 || i == 2212 || i == 2214 || i == 2220
					|| i == 2222 || i == 2223 || i == 2230 || i == 2233 || i == 2232 || i == 2234 || i == 2236 
					|| i == 2227) {
				
				continue;
			}

			putToMaps(i);
			
		}
		
		for(int i = 2301; i <= 	2308; ++i) {
			
			putToMaps(i);
			
		}
		
		for(int i = 3001; i <= 	3021; ++i) {
			
			//3012                3014->3013
			
			if(i == 3012 || i == 3014) { 
				
				continue;
			}
			
			putToMaps(i);
			
		}

		for(int i = 3101; i <= 	3126; ++i) {
			
			//3103->3102
			
			if(i == 3103) { continue; }
			
			putToMaps(i);
			
		}


		for(int i = 4001; i <= 4032; ++i) {
			
			//4009 -> 4010 4014 ->4013   4019->4020 4021->4022 4033->  4034->
			
			if(i == 4009 || i == 4014 || i == 4019 || i == 4021 || i == 4023) { continue; }
			
			putToMaps(i);
			
		}
		
		putToMaps(4035);
		
		putToMaps(4036);
		
		putToMaps(4037);
	
		for(int i = 4101; i <= 4143; ++i) {
			
			//4019                 4130->4131              4140
			
			if(i == 4019 || i == 4130  || i == 4140 || i == 4119  ) { continue; }

			putToMaps(i);
			
		}
		
		for(int i = 5001; i <= 5023; ++i) {
			
			//5002->5004               5016           5019->5020
			
			if(i == 5002 || i == 5016 || i == 5019 ) { continue; }

			putToMaps(i);
			
		}
		
		
		WsSkladMoveDataColumn d1 = new  WsSkladMoveDataColumn();
		
		d1.kod = WsUtils.UNKNOWN_KOD;
		
		d1.name = getGuiStrs("knNotFound");
		
		m_map.put( WsUtils.UNKNOWN_KOD, d1);
	
	}
	
	public HashMap<Integer, Integer> createOldNewCatalogMap() {
		
		HashMap<Integer, Integer> map_old_to_new = new HashMap<Integer, Integer>();
		
		map_old_to_new.put(1003, 1004);
	
		map_old_to_new.put(1018, 1019);
		
		map_old_to_new.put(1027, 1026);
		
		map_old_to_new.put(1035, 1033); 
		
		map_old_to_new.put(1036, 1034); 
		
		map_old_to_new.put(1037, 1033); 
		
		map_old_to_new.put(1038, 1034); 
		
		map_old_to_new.put(1056, 1057);
	
		map_old_to_new.put(1207, 1026);
		
		map_old_to_new.put(1303, 1406);  
		
		map_old_to_new.put(1304, 1408); 
		
	    map_old_to_new.put(1305, 1409); 
	    
		map_old_to_new.put(1306, 1410); 
		
		map_old_to_new.put(1307, 2010); 
		
		map_old_to_new.put(1308, 1411);

		map_old_to_new.put(1418, 1419);
		
		map_old_to_new.put(1421, WsUtils.UNKNOWN_KOD);
		
		map_old_to_new.put(1601, 1602);
		
		map_old_to_new.put(1632, WsUtils.UNKNOWN_KOD);
		
		map_old_to_new.put(2002, 2003);  
		
		map_old_to_new.put(2006, 2007); 

		map_old_to_new.put(2011, 2010); 
		
		map_old_to_new.put(2015, 2016); 
		
	    map_old_to_new.put(2017, 2020);
	    
	    map_old_to_new.put(2018, 2020);
	    
	    map_old_to_new.put(2019, 2020);
	    
		map_old_to_new.put(2021, 2020);
		
		map_old_to_new.put(2027, 2026); 
		
		map_old_to_new.put(2045, 2044);
		
	    map_old_to_new.put(2048, 2047); 
	    
	    map_old_to_new.put(2050, 2049); 
	    
	    map_old_to_new.put(2053, 2054);
	    
	    map_old_to_new.put(2059, 2058); 
	    
	    map_old_to_new.put(2063, 2062);
	    
	    map_old_to_new.put(2067, 2069);
	    
	    map_old_to_new.put(2068, 2069);
	    
	    map_old_to_new.put(2070, 2069);
	    
	    map_old_to_new.put(2071, 2069); 
	    
	    map_old_to_new.put(2072, 2069); 
	    
	    map_old_to_new.put(2073, 2069);
		
	    map_old_to_new.put(2203, 2202);  
	    
	    map_old_to_new.put(2206, 2205);   
	    
	    map_old_to_new.put(2207, 2208); 
	    
	    map_old_to_new.put(2210, 2209); 
	    
	    map_old_to_new.put(2209, 2213); 
	    
	    map_old_to_new.put(2212, 2211); 
	    
	    map_old_to_new.put(2214, 2213);
	    
	    map_old_to_new.put(2216, 2215);
	    
	    map_old_to_new.put(2220, 2221);
	    
	    map_old_to_new.put(2222, 2224); 
	    
	    map_old_to_new.put(2223, 2224);
	    
	    map_old_to_new.put(2227, 2228);
	    
	    map_old_to_new.put(2230, 2231);
	    
	    map_old_to_new.put(2232, 2231);
	    
	    map_old_to_new.put(2233, 2231); 
	    
	    map_old_to_new.put(2234, 2231); 
	    
	    map_old_to_new.put(2236, 2235);
	    
	    map_old_to_new.put(3012, WsUtils.UNKNOWN_KOD);  
	    
	    map_old_to_new.put(3014, 3013);
		
	    map_old_to_new.put(3103,3102);
		
	    map_old_to_new.put(4009, 4010); 
	    
	    map_old_to_new.put(4014, 4013);  
	    
	    map_old_to_new.put(4019, 4020);
	    
	    map_old_to_new.put(4021, 4022); 
	   
	    map_old_to_new.put(4023, WsUtils.UNKNOWN_KOD); 
	    
	    map_old_to_new.put(4033, WsUtils.UNKNOWN_KOD); 
	    
	    map_old_to_new.put(4034, WsUtils.UNKNOWN_KOD);
		
	    map_old_to_new.put(4019, WsUtils.UNKNOWN_KOD); 
	    
	    map_old_to_new.put(4130, 4131);    
	    
	    map_old_to_new.put(4140, WsUtils.UNKNOWN_KOD);
		
	    map_old_to_new.put(5002, 5004);    
	    
	    map_old_to_new.put(5016, WsUtils.UNKNOWN_KOD);  
	    
	    map_old_to_new.put(5019, 5020);
	    
	    map_old_to_new.put(5021, 5021);
	
		return  map_old_to_new;
		
	}
	
	public  int getKodFromCatalog(int foreign_kod) {
		
		if( m_map.containsKey(foreign_kod)) { return foreign_kod; }
		
		int tmp1 = foreign_kod - 10000;
		
		int tmp2 = foreign_kod + 10000;
		
		if( m_map.containsKey(tmp1)) { return tmp1; }
		
		if( m_map.containsKey(tmp2)) { return tmp2; }
		
	
		return WsUtils.UNKNOWN_KOD;
		
		
	}
	
	public static  int getKodFromDatabaseCatalog(int foreign_kod, HashMap<Integer, WsPartType> db_catalog) {
		
		if( db_catalog.containsKey(foreign_kod)) { return foreign_kod; }
		
		int tmp1 = foreign_kod - 10000;
		
		int tmp2 = foreign_kod + 10000;
		
		if( db_catalog.containsKey(tmp1)) { return tmp1; }
		
		if( db_catalog.containsKey(tmp2)) { return tmp2; }
		
	
		return WsUtils.UNKNOWN_KOD;
		
		
	}

}