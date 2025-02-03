
package WsReports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsSkladMoveDataColumn;
import WsMain.WsCatalogKods;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSReportUtil {
	
	
	public static Vector<WsSkladMoveDataColumn> generatesAllKodsForMap(HashMap<Integer, 
			WsSkladMoveDataColumn> map) {
		
		Vector<WsSkladMoveDataColumn> vec_all = new Vector<WsSkladMoveDataColumn>();
		
	    ArrayList<Integer> list = new ArrayList<Integer>(map.keySet()); 
		
		Collections.sort(list);
		
		for(Integer kod: list) {
				
				 WsSkladMoveDataColumn d = map.get(kod);
				 
				 d.kod = kod;
				 
				 vec_all.add(d);
				 
		}
	    
		
		return vec_all;
	}
	
	
	public static Vector<WsSkladMoveDataColumn> fillVectorWithAllKods( 
			Vector<WsSkladMoveDataColumn> vec) {
		

		WsCatalogKods c = new WsCatalogKods();
		
		HashMap<Integer, WsSkladMoveDataColumn> map = c.getKodsTable();
		
		
		for(int i = 0; i < vec.size(); ++i)  {
			
			WsSkladMoveDataColumn d = vec.elementAt(i);
			
			map.put(d.kod, d);
		}
		
		//in the sort order
		Vector<WsSkladMoveDataColumn> vec_all = generatesAllKodsForMap( map);
		
		return vec_all;
	}
	
	
	static public Vector<WsPrihodPartData> fillVectorWithAllKods2(Vector<WsPrihodPartData> vec) {
		
		WsCatalogKods c = new WsCatalogKods();
		
		HashMap<Integer, WsPrihodPartData> map = c.getKodsTable2();
		
		
		for(int i = 0; i < vec.size(); ++i)  {
			
			WsPrihodPartData d = vec.elementAt(i);
			
			map.put(d.kod, d);
		}
		
		Vector<WsPrihodPartData> vec_all = new Vector<WsPrihodPartData>();
		
	    ArrayList<Integer> list = new ArrayList<Integer>(map.keySet()); 
		
		Collections.sort(list);
		
		for(Integer kod: list) {
				
				WsPrihodPartData d = map.get(kod);
				 
				 d.kod = kod;
				 
				 vec_all.add(d);
				 
		}
		
		return vec_all;
	}
	
	static public Vector<WsRashodPartData> fillVectorWithAllKods3(Vector<WsRashodPartData> vec) {
		
		WsCatalogKods c = new WsCatalogKods();
		
		HashMap<Integer, WsRashodPartData> map = c.getKodsTable3();
		
		
		for(int i = 0; i < vec.size(); ++i)  {
			
			WsRashodPartData d = vec.elementAt(i);
			
			map.put(d.kod, d);
		}
		
		Vector<WsRashodPartData> vec_all = new Vector<WsRashodPartData>();
		
	    ArrayList<Integer> list = new ArrayList<Integer>(map.keySet()); 
		
		Collections.sort(list);
		
		for(Integer kod: list) {
				
				WsRashodPartData d = map.get(kod);
				 
				 d.kod = kod;
				 
				 vec_all.add(d);
				 
		}
		
		return vec_all;
	}
	
}
