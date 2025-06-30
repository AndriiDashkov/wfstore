
package WsDatabase;

import java.sql.ResultSet;
import static WsMain.WsUtils.getMessagesStrs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsAgentTypeData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsAgentSqlStatements {
	
	public static Vector<WsAgentTypeData> getAgentsTypes() {
		
        Statement st;
        
        Vector<WsAgentTypeData> v = new Vector<WsAgentTypeData>();
        
		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	
	        	ResultSet rs = st.executeQuery("SELECT id, name, info FROM counterparties_types");
	        	
	        	while(rs.next()) {
	        		
	        		WsAgentTypeData d = new WsAgentTypeData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.name = rs.getString(2);
	        		
	        		d.info = rs.getString(3);
	        		
	        		v.add(d);
	        		
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return v;
	        
	        }
		} catch (SQLException e) {
		
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
        
		return null;
		
	}
	
	public static boolean createNewAgent(WsAgentData dt) {
		
       Statement st;
       
       PreparedStatement ps = null;
       
		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        
	        	 final String insertion = "INSERT INTO counterparties (name,contact,info,id_type) VALUES(?,?,?,?);";
	       	  	          
	        	 WsConnect.get();
	        	 
				 ps = WsConnect.getCurrentConnection().prepareStatement(insertion);
				 
	             ps.setString(1, dt.name);
	             
	             ps.setString(2, dt.contact);
	             
	             ps.setString(3, dt.info);
	             
	             ps.setInt(4, dt.id_type);
	     
	             @SuppressWarnings("unused")
				int numRowsInserted = ps.executeUpdate();
	            
	        	
	        	return true;
	        
	        }
		} catch (SQLException e) {
		
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
		return false;
    	
	}
	
	public static boolean updateAgent(WsAgentData dt) {
		
        PreparedStatement ps = null;
      
		try {
			     	
        	 final String update = "UPDATE counterparties SET name = ?, contact= ?, info= ?,  id_type = ? WHERE id = ?;";
        	 
        	 WsConnect.get();
        	 
			 ps = WsConnect.getCurrentConnection().prepareStatement(update);
			
             ps.setString(1, dt.name);
             
             ps.setString(2, dt.contact);
             
             ps.setString(3, dt.info);
             
             ps.setInt(4, dt.id_type);
             
             ps.setInt(5, dt.id);
              
             int numRowsUpdated = ps.executeUpdate();
        	
        	 return numRowsUpdated == 1;
	         
		} catch (SQLException e) {

			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
		return false;
   	
	}
	
	public static Vector<WsAgentData> getAgentsList(int id_types_index) {
		
	   WsConnect.get();
		
	   return getAgentsList(WsConnect.getCurrentConnection(), id_types_index);
	}
	
	public static Vector<WsAgentData> getAgentsList(Connection conn, int id_types_index) {
		
        Statement st;
        
        Vector<WsAgentData> vec = new  Vector<WsAgentData>();
        
		try {
			
			st = conn.createStatement();
			
	        if (st != null) {
	        	
	        	String s = null;
	        	
	        	if(id_types_index > -1) {
	        		
	        		s = "SELECT counterparties.id, counterparties.name, counterparties_types.name, contact, counterparties.info, counterparties_types.id "
		        			+ "FROM counterparties INNER JOIN counterparties_types ON counterparties_types.id = counterparties.id_type "
	        				+ "WHERE  counterparties.id_type =" + Integer.toString(id_types_index) + " ORDER BY counterparties.name;";
	        		
	        	}
	        	else {
	        		
	            	s = "SELECT counterparties.id, counterparties.name, counterparties_types.name, contact, counterparties.info, counterparties_types.id "
		        			+ "FROM counterparties INNER JOIN counterparties_types ON counterparties_types.id = counterparties.id_type ORDER BY counterparties.name;";
		      
	        	}
	        	
	        	ResultSet rs =  st.executeQuery(s);
	        	
	        	while(rs.next()) {
	        		
	        		WsAgentData d = new WsAgentData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.name = rs.getString(2);
	        		
	        		d.type_name = rs.getString(3);
	        		
	        		d.contact = rs.getString(4);
	        		
	        		d.info = rs.getString(5);
	        		
	        		d.id_type = rs.getInt(6);
	        		
	        		vec.add(d);
	        		
	        	}
	        	
	        	return vec;
	        
	        }
		} catch (SQLException e) {

			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
    
		return null;
		
		
	}
	
	
	public static int deleteAgentForId(int id) {
		
		if (id  == -1) return 0;
       
		try {
			
			if(checkAgentIdIsUsed(id)) {
				
				WsUtils.showMessageDialogLong(getMessagesStrs("deleteAgentFailedUsedMessage0"),
						getMessagesStrs("deleteAgentFailedUsedMessage1"));
				
				return 0;
				
			}
        	
        	final String delete_st = "DELETE FROM counterparties WHERE id= ?;";
       	  	           
        	WsConnect.get();
        	
			PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
        	
        	return  numRows;
	        
	        
		} catch (SQLException e) {

			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
    
		return 0;
		
		
	}
	
	
	public static int deleteAgentTypeForId(int id) {
		
		if (id  == -1) return 0;
		
		if(id == 1 || id == 2) {
			
			WsUtils.showMessageDialog(getMessagesStrs("deleteAgentTypeFailedStMessage"));
			
			return 0;
			
		}
       
		try {
			
			if( checkAgentTypeIdIsUsed(id) ) {

				WsUtils.showMessageDialogLong(getMessagesStrs("deleteAgentTypeFailedUsedMessage0"),
						getMessagesStrs("deleteAgentTypeFailedUsedMessage1"));
				
				return 0;
			}
			
        	final String delete_st = "DELETE FROM counterparties_types WHERE id= ?;";
       	  	           
        	WsConnect.get();
        	
			PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
        	
        	return  numRows;
	        
	        
		} catch (SQLException e) {

			e.printStackTrace();
		}
       
		return 0;
	}
	
	
	public static boolean checkAgentTypeIdIsUsed(int id) {
		
		if (id  == -1) return false;
       
		try {
		
        	final String st = "SELECT name FROM counterparties WHERE id_type = ?;";
       	  	          
        	WsConnect.get();
        	
			PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(st);
        
            ps.setInt(1, id);
     
            ResultSet rs = ps.executeQuery();
           
            boolean flag = rs.next() != false;
        	
        	rs.close();
           
        	return  flag;
	        
	        
		} catch (SQLException e) {
	
			if( WsUtils.isDebug() ) {
				e.printStackTrace();
			}
			
			return true;
		}
		
	}
	
	public static boolean checkAgentIdIsUsed(int id) {
		
		if (id  == -1) return false;
       
		try {
			
			String[] sqlNames = {
        	
        	" SELECT id FROM invoices WHERE id_counterparty = ?;",
        	
        	" SELECT id FROM sale_invoices WHERE  id_counterparty = ?;"};
       	  	          
        	WsConnect.get();
        	
        	for(int i = 0; i < 2; ++i) {
        	
        	
				PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(sqlNames[i]);
	        
	            ps.setInt(1, id);
	     
	            ResultSet rs = ps.executeQuery();
	           
	            boolean flag = rs.next() != false;
	            
	            rs.close();
	            
	            if(flag) { return true;}
        	}
           
        	return  false;
	        
	        
		} catch (SQLException e) {
	
			if( WsUtils.isDebug() ) {
				e.printStackTrace();
			}
			
			return true;
		}
		
	}
	
	public static int createNewAgentType(WsAgentTypeData dt) {
		
      
      PreparedStatement ps = null;
      
		try {
	        	
        	 final String insertion = "INSERT INTO counterparties_types (name,info) VALUES(?,?);";
       	  	          
        	 WsConnect.get();
        	 
			 ps = WsConnect.getCurrentConnection().prepareStatement(insertion, Statement.RETURN_GENERATED_KEYS);
        	 
             ps.setString(1, dt.name);
           
             ps.setString(2, dt.info);
        	
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
	
	public static boolean updateAgentType(WsAgentTypeData dt) {
		
	      
	      	PreparedStatement ps = null;
	      	
	      	if(dt == null) return false;
	      
			try {
		        	
	        	 final String update = "UPDATE counterparties_types SET name = ?, info= ? WHERE id = ?;";
	        	          
	        	 WsConnect.get();
	        	 
				 ps = WsConnect.getCurrentConnection().prepareStatement(update);
	        	 
	             ps.setString(1, dt.name);
	           
	             ps.setString(2, dt.info);
	             
	             ps.setInt(3, dt.id);
	        	
	        	 return ps.executeUpdate() == 1;
		      
			} catch (SQLException e) {

				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			}
			
			return false;
	   	
	}
	

	
	public static String getAgentNameForId(int id) {
		
        Statement st;

		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	
	        	String s = "SELECT counterparties.name FROM counterparties WHERE id = " + String.valueOf(id)+";";
	        	
	        	ResultSet rs =  st.executeQuery(s);
	        	
	        	String name = "";
	        	
	        	while(rs.next()) {
	        		
	        		name  = rs.getString(1);
	        		
	        	}
	        	
	        	return name;
	        
	        }
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return "";
		
	}
	
	
	public static Vector<Integer> getAgentIdsForName(String name) {
		
        Statement st;

		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	
	        	String s = "SELECT counterparties.id FROM counterparties WHERE name LIKE '%" + name +"%';";
	        	
	        	ResultSet rs =  st.executeQuery(s);
	        	
	        	Vector<Integer> res = new Vector<Integer>();
	        	
	        	while(rs.next()) {
	        		
	        		res.add(rs.getInt(1));
	        		
	        	}
	        	
	        	return res;
	        
	        }
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;
		
	}
	
	public static boolean importAgents(Connection conn) {
		
		Vector<WsAgentData> vec = getAgentsList(conn, -1);
		
		for(WsAgentData a: vec) {
			 
			 createNewAgent(a);
		}
	
		return true;
	}

	
	public static WsAgentData getAgentForId(int id) {
		
        Statement st;
        
		WsAgentData d = new WsAgentData();
        
		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
			if (st != null) {
	        	
		        	StringBuilder s = new StringBuilder();
		        			s.append("SELECT counterparties.id, counterparties.name, counterparties_types.name, contact, counterparties.info, counterparties_types.id "
		        			+ "FROM counterparties INNER JOIN counterparties_types ON counterparties_types.id = counterparties.id_type "
		        			+ "WHERE counterparties.id = ");
		        			
		        	s.append( String.valueOf(id) );
		        	
		        	s.append(";");
		        	
		        	ResultSet rs =  st.executeQuery(s.toString());
		        	
		        	while(rs.next()) {
		        		
		        		d.id = rs.getInt(1);
		        		
		        		d.name = rs.getString(2);
		        		
		        		d.type_name = rs.getString(3);
		        		
		        		d.contact = rs.getString(4);
		        		
		        		d.info = rs.getString(5);
		        		
		        		d.id_type = rs.getInt(6);
		        		
		        	}
		        	
		        	return d;
	        
	        }
		} catch (SQLException e) {
		
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;
	}
	
	
	public static boolean isAgentsTableEmpty() {
		
		 Vector<WsAgentData> vec = getAgentsList(-1);
		 
		 return vec == null || vec.isEmpty();
		
	}
	
	
	public static int createNewAgentByName(String name) {
		
		Vector<Integer> vec = getAgentIdsForName(name);
		
		if(vec.isEmpty()) {
			
			WsAgentData dt = new  WsAgentData();
			
			dt.name = name;
			
			dt.id_type = 1;
			
			createNewAgent(dt);
			
			vec = getAgentIdsForName(name);
		}

			
		return vec.elementAt(0);
		
		
	}
}

