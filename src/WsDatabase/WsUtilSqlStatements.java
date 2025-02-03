
package WsDatabase;

import static WsMain.WsUtils.getGuiStrs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsUnitData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsUtilSqlStatements {
	
	
	public static HashMap<Integer, WsPartType> getPartTypesMap() {
		
		WSConnect.get();
	
		return getPartTypesMap(WSConnect.getCurrentConnection());
			
	}
	
	public static HashMap<Integer, WsPartType> getPartTypesMap(Connection conn) {
		
		try {
		
			HashMap<Integer, WsPartType> map = new HashMap<Integer, WsPartType>();
			
        	final String st = "SELECT id, name, info, kod, sklad_quantity, costwithnds FROM part_types ORDER BY kod;";
       	  	          	
        	if(conn == null) { return map; }
        	
			PreparedStatement ps = conn.prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	
            	WsPartType d = new WsPartType();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.info = rs.getString(3);
            	
            	d.kod = rs.getInt(4);
            	
            	d.quantity = rs.getDouble(5);
            	
            	d.costwithnds = rs.getDouble(6);
            	
            	map.put(d.kod, d);
            	
            }
            
            rs.close();
            
            ps.close();
           
         	return  map;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}

		}
		
		return null;
		
	}
	
	public static boolean isCatalog5digits() {
		
		WSConnect.get();
	
		return isCatalog5digits(WSConnect.getCurrentConnection());
			
	}
	
	public static boolean isCatalog5digits(Connection conn) {
		
		
		final String st = "SELECT id, kod FROM part_types LIMIT 1;";
		
        try {
        	
    		PreparedStatement ps = conn.prepareStatement(st);
        	
			ResultSet rs = ps.executeQuery();
			
			 if(rs.next()) {
				 
				 return rs.getInt(2) > 9999;
				 
			 }
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
  	       
		
		return false;
	
	}
	
	public static Vector<WsPartType> getPartTypesList() {
		
		WSConnect.get();
	
		return getPartTypesList(WSConnect.getCurrentConnection());
			
	}
	
	public static Vector<WsPartType> getPartTypesList(Connection conn) {
		
		try {
		
			Vector<WsPartType> vec = new Vector<WsPartType>();
			
        	final String st = "SELECT id, name, info, kod, sklad_quantity, costwithnds FROM part_types ORDER BY kod;";
       	  	          	
        	if(conn == null) { return vec; }
        	
			PreparedStatement ps = conn.prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	
            	WsPartType d = new WsPartType();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.info = rs.getString(3);
            	
            	d.kod = rs.getInt(4);
            	
            	d.quantity = rs.getDouble(5);
            	
            	d.costwithnds = rs.getDouble(6);
            	
            	vec.add(d);
            	
            }
            
            rs.close();
            
            ps.close();
           
         	return  vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}

		}
		
		return null;
		
	}
	
	public static Vector<Integer> getKodsList() {
		
		Vector<Integer> vec = new Vector<Integer>();
		
		try {

        	final String st = "SELECT  kod FROM part_types ORDER BY kod;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	
            	int kod = rs.getInt(1);
 
            	vec.add(kod);
            	
            }
            
            rs.close();
            
            ps.close();
           
         	return  vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}

		}
		
		return vec;
		
	}
	
	public static Vector<WsInfoData> getInfoDataList() {
		
		try {
			
        	final String st = "SELECT id, name, adress, phone, person, rahunok, MFO, comments, nds, money FROM info;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
           
            Vector<WsInfoData> vec = new Vector<WsInfoData>();
            
            while(rs.next()) {
            	
            	WsInfoData d = new WsInfoData();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.adress = rs.getString(3);
            	
            	d.phone = rs.getString(4);
            	
            	d.person = rs.getString(5);
            	
            	d.rahunok = rs.getString(6);
            	
            	d.MFO = rs.getString(7);
            	
            	d.comments = rs.getString(8);
            	
            	d.nds = rs.getDouble(9);
            	
            	if(d.nds == 0.0) {
            		
            		d.nds = 1.2;
            	}
            	
            	d.money = rs.getString(10);
            	
            	vec.add(d);

            }
        
            rs.close();
            
        	return vec;
  
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	public static WsUnitData getUnitIdForName(String name) {
		
		Vector<WsUnitData> list = getUnitsList();
		
		for(int i =0; i < list.size(); ++i) {
			
			WsUnitData d = list.elementAt(i);
			
			if(d.name.equals(name)) {
				
				WsUnitData d_ =new WsUnitData();
				
				d_.id = d.id;
				
				d_.name = d.name;
				
				return d_;
			}
			
		}
		
		return null;
	}
	
	public static Vector<WsUnitData> getUnitsList() {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getUnitsList(conn);
		
	}
	
	public static Vector<WsUnitData> getUnitsList(Connection conn) {
		

		try {
			
			Vector<WsUnitData> vec = new Vector<WsUnitData>();
			
        	final String st = "SELECT id, name FROM units;";
       	  	          
			PreparedStatement ps = conn.prepareStatement(st);
     
            ResultSet rs = ps.executeQuery();
           
            while(rs.next()) {
            	
            	WsUnitData d = new WsUnitData();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	vec.add(d);
            	
            }
            
            rs.close();
            
            ps.close();
           
        	return  vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}

		}
		
		return null;
		
	}
	
	public static WsUnitData getUnitForId(int id) {
		
		WSConnect.get();
		
		Connection conn =  WSConnect.getCurrentConnection();
		
		return getUnitForId(conn, id);
	
	}

	public static WsUnitData getUnitForId(Connection conn, int id) {
		

		try {
			
        	final String st = "SELECT id, name FROM units WHERE id = ?;";
       	  	          
			PreparedStatement ps = conn.prepareStatement(st);
        
			ps.setInt(1, id);
     
            ResultSet rs = ps.executeQuery();
           
            while(rs.next()) {
            	
            	WsUnitData d = new WsUnitData();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
                rs.close();
                
                ps.close();
            	
            	return d;
            	
            }
            
            rs.close();
            
            ps.close();
           
        	return  null;
	          
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
	
	public static  WsUnitData getFirstUnit() {
		
		Vector<WsUnitData> vec = getUnitsList();

		if(!vec.isEmpty()) {
			
			return vec.elementAt(0);
			
		}
		 
		return null;
		
	}

	public static  WsUnitData getKgUnit() {
		
		Vector<WsUnitData> vec = getUnitsList();

		final String s = getGuiStrs("units_kg_DatabaseName");
		
		for(int i = 0; i < vec.size(); ++i) {
			 
			WsUnitData d = vec.elementAt(i);
			
			if(d.name.equals(s)) { return d; }
		}
		
		return null;
		
	}
	
	public static  WsUnitData getShtUnit() {
		
		Vector<WsUnitData> vec = getUnitsList();
		
		final String s = getGuiStrs("units_sht_DatabaseName");
		
		for(int i = 0; i < vec.size(); ++i) {
			 
			WsUnitData d = vec.elementAt(i);
			
			if(d.name.equals(s)) { return d; }
		}
	
		return null;
		
	}
	
	public static void updateUnitName(WsUnitData dt) {
		
	 	final String update = "UPDATE units SET name = ? WHERE id = ?;";
    	
	 	PreparedStatement ps;
	 	
		try {
			
			ps = WSConnect.getCurrentConnection().prepareStatement(update);
		
	        ps.setInt(2, dt.id);
	        
	        ps.setString(1, dt.name);
	        
	        @SuppressWarnings("unused")
			int rV = ps.executeUpdate();
	        
	        ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	}

	public static int insertUnit(WsUnitData dt) {
		
	        PreparedStatement ps = null;
	       
			try {
				
	        	final String insertion = "INSERT INTO units (name) VALUES(?);";
	       	  	           
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(insertion, Statement.RETURN_GENERATED_KEYS);
				
	            ps.setString(1, dt.name);
	            
	            @SuppressWarnings("unused")
				boolean flag = ps.executeUpdate() == 1;
	            
	            int inserted_id = -1;
	            
	            ResultSet rs = ps.getGeneratedKeys();
	            
	            if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	            }
	            
	            ps.close();
	            
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return -1;
		
	}
	
	public static void deleteUnit(WsUnitData dt) {
		
	 	final String update = "DELETE FROM units WHERE id = ?;";
    	
	 	PreparedStatement ps;
	 	
		try {
				ps = WSConnect.getCurrentConnection().prepareStatement(update);
			
		        ps.setInt(1, dt.id);
	
		        @SuppressWarnings("unused")
				int rV = ps.executeUpdate();
		        
		        ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static boolean checkUnitIsUsed(int id) {
		
		if (id  == -1) return false;
	       
		try {
			
			WSConnect.get();
			  	
        	final String st = "SELECT id FROM invoice_parts WHERE id_units = ?;";
	  	          
        	PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ps.setInt(1, id);
     
            ResultSet rs = ps.executeQuery();
           
            boolean flag = rs.next() != false;
            
            if(flag) return true;
           
            rs.close();
        	
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return true;
		}
		
		return  false;
	}
	
	public static int updatePartType(WsPartType dt) {
		
	 	final String update = "UPDATE part_types SET name = ?, info = ? , kod = ?, sklad_quantity = ?,"
	 			+ " costwithnds = ? WHERE id = ?;";
    	
	 	PreparedStatement ps;
	 	
		try {
			   ps = WSConnect.getCurrentConnection().prepareStatement(update);
			
			   ps.setString(1, dt.name);
				  
			   ps.setString(2, dt.info);
				  
		       ps.setInt(3, dt.kod);
		        
		       ps.setDouble(4, dt.quantity);
		       
		       ps.setDouble(5, dt.costwithnds);
		       
		       ps.setInt(6, dt.id);
		      
		        @SuppressWarnings("unused")
		       int rV = ps.executeUpdate();
		        
		       ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return 1;
		}
		
		return 0;
	}
	
	public static void updateInfo(WsInfoData dt) {
		
	 	final String update = "UPDATE info SET name = ?, adress = ?, phone = ?, person = ?,"
	 			+ " rahunok = ?, MFO = ?, comments = ?, nds = ?, money = ? WHERE id = ?;";
    	
	 	PreparedStatement ps;
	 	
		try {
			   ps = WSConnect.getCurrentConnection().prepareStatement(update);
			
		       ps.setString(1, dt.name);
		        
		       ps.setString(2, dt.adress);
		       
		       ps.setString(3, dt.phone);
		       
		       ps.setString(4, dt.person);
		       
		       ps.setString(5, dt.rahunok);
		       
		       ps.setString(6, dt.MFO);
		       
		       ps.setString(7, dt.comments);
		       
		       ps.setDouble(8, dt.nds);
		       
		       ps.setString(9, dt.money);
		       
		       ps.setInt(10, dt.id);
		        
		        @SuppressWarnings("unused")
				int rV = ps.executeUpdate();
		        
		        ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static int insertPartType(WsPartType dt) {
		
	       PreparedStatement ps = null;
	       
			try {
				
	        	final String insertion = "INSERT INTO part_types (name, info, kod, costwithnds) "
	        			+ "VALUES(?, ?, ?, ?);";
	       	  	           
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(insertion, Statement.RETURN_GENERATED_KEYS);
				
	            ps.setString(1, dt.name);
	            
	            ps.setString(2, dt.info);
	            
	            ps.setInt(3, dt.kod);
	            
	            ps.setDouble(4, dt.costwithnds);
	            
	            @SuppressWarnings("unused")
				boolean flag = ps.executeUpdate() == 1;
	            
	            int inserted_id = -1;
	            
	            ResultSet rs = ps.getGeneratedKeys();
	            
	            if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	            }
	            
	            ps.close();
	            
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return -1;
		
	}
	
	public static boolean checkPartTypeIsUsed(int id) {
		
		if (id  == -1) return false;
	       
		try {
			
			WSConnect.get();
			
        	final String st = "SELECT id FROM invoice_parts WHERE id_part_type = ?;";
	  	          
        	PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ps.setInt(1, id);
     
            ResultSet rs = ps.executeQuery();
           
            boolean flag = rs.next() != false;
            
            if(flag) return true;
           
            rs.close();
        	
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return true;
		}
		
		return  false;
	}

	public static void deletePartType(WsPartType dt) {
		
	 	final String update = "DELETE FROM part_types WHERE id = ?;";
    	
	 	PreparedStatement ps;
	 	
		try {
			
			ps = WSConnect.getCurrentConnection().prepareStatement(update);
		
	        ps.setInt(1, dt.id);

	        @SuppressWarnings("unused")
			int rV = ps.executeUpdate();
	        
	        ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	}

	public static void deleteAllCatalog() {
		
	 	final String update = "DELETE FROM part_types;";
    	
	 	PreparedStatement ps;
	 	
		try {
			
			ps = WSConnect.getCurrentConnection().prepareStatement(update);
		

	        @SuppressWarnings("unused")
			int rV = ps.executeUpdate();
	        
	        ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
 
	}
	
	public static void deleteInfoData(WsInfoData dt) {
		
	 	final String update = "DELETE FROM info WHERE id = ?;";
    	
	 	PreparedStatement ps;
	 	
		try {
			
			ps = WSConnect.getCurrentConnection().prepareStatement(update);
		
	        ps.setInt(1, dt.id);

	        @SuppressWarnings("unused")
			int rV = ps.executeUpdate();
	        
	        ps.close();
		        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	}
	
	public static int insertInfoData(WsInfoData dt) {
		
	       PreparedStatement ps = null;
	       
			try {
				
	        	final String insertion = "INSERT INTO info (name, adress, phone, person, "
	        			+ "rahunok, MFO, comments, nds, money) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
	       	  	           
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(insertion, Statement.RETURN_GENERATED_KEYS);
				
	            ps.setString(1, dt.name);
	            
	            ps.setString(2, dt.adress);
	            
	            ps.setString(3, dt.phone);
	            
	            ps.setString(4, dt.person);
	            
	            ps.setString(5, dt.rahunok);
	            
	            ps.setString(6, dt.MFO);
	            
	            ps.setString(7, dt.comments);
	            
	            ps.setDouble(8, dt.nds);
	            
	            ps.setString(9, dt.money);
	            
	            @SuppressWarnings("unused")
				boolean flag = ps.executeUpdate() == 1;
	            
	            int inserted_id = -1;
	            
	            ResultSet rs = ps.getGeneratedKeys();
	            
	            if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	            }
	            
	            ps.close();
	            
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return -1;
		
	}
	
	public static WsPartType getPartTypeForKod(int kod) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		 
		 return  getPartTypeForKod(conn, kod);
	
	}
	
	public static WsPartType getPartTypeForKod(Connection conn, int kod) {
		

		try {
			
        	final String st = "SELECT id, name, info, kod, costwithnds FROM part_types WHERE kod = ?;";
       	  	          
			PreparedStatement ps = conn.prepareStatement(st);
			
			ps.setInt(1, kod);
        
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	
            	WsPartType d = new WsPartType();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.info = rs.getString(3);
            	
            	d.kod = rs.getInt(4);
            	
            	d.costwithnds = rs.getDouble(5);
            	
            	return d;
            	
            }
            
            rs.close();
            
            ps.close();
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
			e.printStackTrace();
			}

		}
		
		return null;
		
	}
	
	public static WsPartType getPartTypeForId(int id) {
		

		try {
			
        	final String st = "SELECT id, name, info, kod, costwithnds FROM part_types WHERE id = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
			
			ps.setInt(1, id);
        
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	
            	WsPartType d = new WsPartType();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.info = rs.getString(3);
            	
            	d.kod = rs.getInt(4);
            	
            	d.costwithnds = rs.getDouble(5);
            	
            	return d;
            	
            }
            
            rs.close();
            
            ps.close();
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}

		}
		
		return null;
		
	}
	
	
	public static boolean importCatalog(Connection conn) {
		
		 boolean flag0 = WsUtilSqlStatements.isCatalog5digits();
			
		 boolean flag1 = WsUtilSqlStatements.isCatalog5digits(conn);
		
		Vector<WsPartType> vec  = getPartTypesList(conn);
		
		if(vec.isEmpty()) {
			
			deleteAllCatalog();
		
			if(flag0 == flag1) {
				
				for(WsPartType p: vec) {
					
					insertPartType(p);
					
				}
			}
			//all this code is just for compatibility between 4 digits and 5 digits catalogs
			else if(flag0 && !flag1) {
				
				for(WsPartType p: vec) {
					
					p.kod += 10000;

					insertPartType(p);
					
				}
			}
			else if(!flag0 && flag1) {
				
				for(WsPartType p: vec) {
					
					p.kod -= 10000;

					insertPartType(p);
					
				}
				
			}
		
		}
		
		return true;

	}
	
	public static boolean isCatalogInDatabaseNew() {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		 
		 return isCatalogInDatabaseNew(conn);
	
	}
	
	
	public static boolean isCatalogInDatabaseNew(Connection conn) {
		

		try {
		
        	final String st = "SELECT id FROM part_types WHERE kod = 1421 OR kod = 1632 OR kod = 4023;";
       	  	          	
        	if(conn == null) { return false; }
        	
			PreparedStatement ps = conn.prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            boolean flag = true;
            
            if(rs.next()) {
            	
            	flag =  false;
            	
            }
            
            rs.close();
            
            ps.close();
           
         	return  flag;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}

		}
		
		return false;
		
	}
	

	

}
