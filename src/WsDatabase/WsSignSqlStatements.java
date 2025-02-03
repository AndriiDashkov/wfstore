
package WsDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import WsDataStruct.WsSignsData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSignSqlStatements {


	public WsSignSqlStatements() {

	}

	public static Vector<WsSignsData> getSignsList() {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getSignsList(conn);
		
	}
	
	public static Vector<WsSignsData> getSignsList(Connection conn) {
		

		try {
			
			Vector<WsSignsData> vec = new Vector<WsSignsData>();
			
        	final String st = "SELECT id, name, rank, position FROM signatories;";
       	  	          
			PreparedStatement ps = conn.prepareStatement(st);
     
            ResultSet rs = ps.executeQuery();
           
            while(rs.next()) {
            	
            	WsSignsData d = new WsSignsData();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.rank = rs.getString(3);
            	
            	d.position = rs.getString(4);
            	
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
	
	public static WsSignsData getSignsForId(int id) {
		

		try {
			
        	final String st = "SELECT id, name, rank, position FROM signatories WHERE id = ?;";
       	  	          
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
			
			ps.setInt(1, id);
     
            ResultSet rs = ps.executeQuery();
           
            while(rs.next()) {
            	
            	WsSignsData d = new WsSignsData();
            	
            	d.id = rs.getInt(1);
            	
            	d.name = rs.getString(2);
            	
            	d.rank = rs.getString(3);
            	
            	d.position = rs.getString(4);
            	
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
	
	public static boolean updateSign(WsSignsData dt) {
		
      	PreparedStatement ps = null;
      	
      	if(dt == null) return false;
      
		try {
	        	
        	 final String update = "UPDATE signatories SET name = ?, rank = ?, position = ? WHERE id = ?;";
        	          
        	 WSConnect.get();
        	 
			 ps = WSConnect.getCurrentConnection().prepareStatement(update);
        	 
             ps.setString(1, dt.name);
           
             ps.setString(2, dt.rank);
             
             ps.setString(3, dt.position);
             
             ps.setInt(4, dt.id);
        	
        	 return ps.executeUpdate() == 1;
	      
		} catch (SQLException e) {

			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
		return false;
   	
	}
	
	public static int createNewSign(WsSignsData dt) {
		
	      
	      PreparedStatement ps = null;
	      
			try {
		        	
	        	 final String insertion = "INSERT INTO signatories (name, rank, position) VALUES(?,?,?);";
	       	  	          
	        	 WSConnect.get();
	        	 
				 ps = WSConnect.getCurrentConnection().prepareStatement(insertion, Statement.RETURN_GENERATED_KEYS);
	        	 
	             ps.setString(1, dt.name);
	           
	             ps.setString(2, dt.rank);
	             
	             ps.setString(2, dt.position);
	        	
	        	 @SuppressWarnings("unused")
				 boolean flag =  ps.executeUpdate() == 1;
	        	 
	        	 int inserted_id = -1;
		            
	             ResultSet rs = ps.getGeneratedKeys();
	            
	             if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	             }
	            
	             rs.close();
	            
	             ps.close();
	            
	            return inserted_id;
		      
			} catch (SQLException e) {
			
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			}
			
			return -1;

	}
	
	public static int deleteSignForId(int id) {
		
		if (id  == -1) return 0;
       
		try {
			
        	final String delete_st = "DELETE FROM signatories WHERE id= ?;";
       	  	           
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
        	
        	return  numRows;
	        
		} catch (SQLException e) {

			e.printStackTrace();
		}
       
		return 0;
	}
}
