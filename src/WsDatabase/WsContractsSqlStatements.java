/**
 * 
 */
package WsDatabase;

import static WsMain.WsUtils.getMessagesStrs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JOptionPane;
import WsDataStruct.WsContractData;
import WsDataStruct.WsContractPriceData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsUnitData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsContractsSqlStatements {
	
	
	public static int createNewContract(WsContractData data) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return  createNewContract(conn, data);
		
	}
	
	//returns inserted id
	public static int createNewContract(Connection conn, WsContractData  data) {
		
			if(data.info == null) { data.info = "";}
		
	        PreparedStatement ps = null;
	       
			try {
				
	        	String insertion_order = "INSERT INTO contracts (name, date, info) VALUES(?,?,?);";
	       	  	           
				ps = conn.prepareStatement(insertion_order, Statement.RETURN_GENERATED_KEYS);
						
	            ps.setString(1, data.number);
	            
	            ps.setDate(2, data.date);
	            
	            ps.setString(3, data.info);
	            
	            @SuppressWarnings("unused")
				int procesedRows = ps.executeUpdate();
	            
	            int inserted_id = -1;
	            
	            ResultSet rs = ps.getGeneratedKeys();
	            
	            if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	            }
	            
	            rs.close();
	            
	            ps.close();
	            
	            if(inserted_id == -1) { return -1; }
	            
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return -1;
		
	}
	
	public static boolean updateContract(WsContractData data) {
		
	       PreparedStatement ps = null;
	       
			try {

	        	String update = "UPDATE contracts SET name = ?,   date = ?, info = ? WHERE id = ?;";
	        	         
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(update);
				
	            ps.setString(1, data.number);
	            
	            ps.setDate(2, data.date);
	            
	            ps.setString(3, data.info);
	            
	            ps.setInt(4, data.id);
	            
	            
	            if(ps.executeUpdate() != 1) { 
	            	
	            	return false; 
	            }
	            
	            ps.close();
	            
	            return true;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return false;
		
	}
	
	//int 0 - sort for id, 1 - sort for date
	public static Vector<WsContractData> getContractsList(int sort_type) {
		
		try {
			
			WSConnect.get();

			Vector<WsContractData> vec = new  Vector<WsContractData>();
		
        	StringBuilder s_b = new StringBuilder(); 
        	
        	if(sort_type == 0) {
        		
        		s_b.append("SELECT id, name, date, info FROM contracts ORDER BY id;");
        		
        	}
        	else {
        		
        		s_b.append("SELECT id, name, date, info FROM contracts ORDER BY date;");
        		
        	}
        	
        	PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(s_b.toString());

        	ResultSet rs =  ps.executeQuery();
        	
        	while(rs.next()) {
        		
        		WsContractData d = new WsContractData();
        		
        		d.id = rs.getInt(1);
        		
        		d.number = rs.getString(2);
        		
        		d.date = rs.getDate(3);
        			
        		d.info = rs.getString(4);

        		vec.add(d);
        		
        	}
        	
        	rs.close();
        	
        	ps.close();
        	
        	return vec;
	         
		} catch (SQLException e) {

			e.printStackTrace();
		}
       
		return null;
	}
	
	
	public static int deleteContractForId(int id) {
		
		if (id  == -1) return 0;
       
		try {
		
			//validation
			
			if( checkContractIsUsed(id)) {
				
		        JOptionPane.showMessageDialog(
        			    WsUtils.get().getMainWindow(),
        			    getMessagesStrs("deleteContractFailedUsedMessage"),
        			    getMessagesStrs("messageInfoCaption"),
        			    JOptionPane.CLOSED_OPTION);
				
				return 0;
			}
			
        	String delete_st = "DELETE FROM contracts WHERE id = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
                    
            delete_st = "DELETE FROM contract_prices WHERE id_contract = ?;";
            		    
            ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
            
            ps.setInt(1, id);
            
            numRows += ps.executeUpdate();
            
        	return  numRows;
	          
		} catch (SQLException e) {
			
			e.printStackTrace();
		};
       
		return 0;
		
	}	
	
	
	public static boolean checkContractIsUsed(int id) {
		
		if (id  == -1) return false;
	       
		try {
			
        	String st = "SELECT invoices.id  FROM invoices "
        			+ "INNER JOIN contracts ON contracts.id = invoices.id_contract WHERE contracts.id = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ps.setInt(1, id);
     
            ResultSet rs = ps.executeQuery();
           
            boolean flag = rs.next() != false;
        	
        	rs.close();
           
        	return  flag;
	        
	        
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			return true;
		}
		
	}
	
	public static WsContractData getContractForId(int id) {
		
        Statement st;
        
        if(id == -1) return null;
       
		try {
			
			WSConnect.get();
			
			st = WSConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        		        	
		        String s = "SELECT id, name, date, info  FROM contracts "
		        			+ "WHERE id = " + Integer.toString(id) + ";";
		        	
		        ResultSet rs = st.executeQuery(s);
		        	
		        if (rs.next()){
		        		
		        		WsContractData dt = new WsContractData();
		            	
		                dt.id = rs.getInt(1);
		                
		                dt.number = rs.getString(2);
		                
		                dt.date = rs.getDate(3);
		                
		                dt.info = rs.getString(4);
		                
		                rs.close();
		                
		                return dt;
		              
		        }
		        else { 
		        	return null; 
		        }
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		return null;	
	}
	
	

	
	
	public static HashMap<Integer, WsContractPriceData> getContractPriceListMap(int id_contract) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		Vector<WsContractPriceData> vec = getContractPriceList(conn, id_contract, 0);
		
		HashMap<Integer, WsContractPriceData> map = new HashMap<Integer, WsContractPriceData>();
		
		for(WsContractPriceData d: vec) {
			
			map.put(d.kod, d);
			
		}
		
		return map;
		
	}
	
	
	public static Vector<WsContractPriceData> getContractPriceList(int id_contract, int sort_type) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getContractPriceList(conn, id_contract, sort_type);
		
	}
	

	
	
	public static Vector<WsContractPriceData> getContractPriceList(Connection conn, int id_contract, int sort_type) {
		
        Statement st;
       
		try {
			
			st = conn.createStatement();
			
			Vector<WsContractPriceData> vec = new Vector<WsContractPriceData>();
			
	        if (st != null) {
	        	
	        	String s_order = " part_types.kod;";
	        	
	        	switch(sort_type) {
	        	
	        		case 1: {
	        			
	        			s_order = " part_types.name;";
	        			break;
	        		}
	      
	        		case 0:
	        			
	        		default: {
	        			
	        			s_order = " part_types.kod;";
	        			
	        			break;
	        		}
	        	}

	        	StringBuilder s_b =  new StringBuilder();
	        	
	        	s_b.append("SELECT contract_prices.id, part_types.kod, part_types.name, units.name, "
	        			+ " contract_prices.cost, contract_prices.nds, contract_prices.costwnds, "
	        			+ "id_contract, "
	        			+ "id_part_type, id_units  FROM contract_prices"
	        			+ " INNER JOIN contracts ON contracts.id = contract_prices.id_contract"
	        			+ " INNER JOIN part_types ON part_types.id = contract_prices.id_part_type"
	        			+ " INNER JOIN units ON units.id = contract_prices.id_units"
	        			+ " WHERE id_contract = ");
	        	
	        	s_b.append( Integer.toString(id_contract));
	        	
	        	s_b.append( " ORDER BY " );
	        	
	        	s_b.append( s_order);
	        	
	        	ResultSet rs =   st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsContractPriceData d = new WsContractPriceData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.kod = rs.getInt(2);
	        		
	        		d.name = rs.getString(3);
	        		
	        		d.units_name = rs.getString(4);

	        		d.cost = rs.getDouble(5);
	        		
	        		d.nds = rs.getDouble(6);
	        		
	        		d.costwnds = rs.getDouble(7);
	        		
	        		d.id_contract = rs.getInt(8);
	        		
	        		d.id_part_type = rs.getInt(9);
	        		
	        		d.id_units = rs.getInt(10);
	
	        		vec.add(d);
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return vec;
	        
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		return null;

	}
	
	
	public static int deleteContractPriceForId(int id) {
		
		if (id  == -1) return 0;
       
		try {
		
			int id_contract = getContractDataForPriceId(id ).id;
			
			if( checkContractIsUsed(id_contract)) {
				
				 JOptionPane.showMessageDialog(
    	       			    WsUtils.get().getMainWindow(),
    	       			 getMessagesStrs("cantDeleteConPriceUsed"),
    	       			    getMessagesStrs("messageInfoCaption"),
    	       			    JOptionPane.CLOSED_OPTION);
				
				return 0;
			}
			

        	String delete_st = "DELETE FROM contract_prices WHERE id = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
                   
            ps.close();
            
        	return  numRows;
	          
		} catch (SQLException e) {
			
			e.printStackTrace();
		};
       
		return 0;
		
	}
	
	public static WsContractData getContractDataForPriceId(int id ) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getContractDataForPriceId(conn, id);
		
	}
	
	
	public static WsContractData getContractDataForPriceId(Connection conn, int id ) {
		
        Statement st;
       
		try {
			
			st = conn.createStatement();
			
	
	        if (st != null) {
	        	
	        	StringBuilder s_b =  new StringBuilder();
	        	
	        	s_b.append("SELECT contracts.name, contracts.date, contracts.info, contracts.id "
	        			+ " FROM contracts"
	        			+ " INNER JOIN contract_prices ON contracts.id = contract_prices.id_contract"
	        			+ " WHERE contract_prices.id = ");
	        	
	        	s_b.append(String.valueOf(id));
	        	
	        	s_b.append(";");

	        	ResultSet rs =   st.executeQuery(s_b.toString());
	        	
	        	WsContractData d = null;
	        	
	        	if(rs.next()) {
	        		
	        		d = new WsContractData();
	        		
	        		d.number = rs.getString(1);
	        		
	        		d.date = rs.getDate(2);

	        		d.info = rs.getString(3);
	        		
	        		d.id = rs.getInt(4);
	        		
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return d;
	        
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    
		return null;
		
		
	}
	
	
	public static int deleteAllPricesForContract(int id) {
		
		if (id  == -1) return 0;
       
		try {
		
			if( checkContractIsUsed(id)) {
				
				 JOptionPane.showMessageDialog(
    	       			    WsUtils.get().getMainWindow(),
    	       			 getMessagesStrs("cantDeleteConPriceUsed"),
    	       			    getMessagesStrs("messageInfoCaption"),
    	       			    JOptionPane.CLOSED_OPTION);
				
				return 0;
			}
			

        	String delete_st = "DELETE FROM contract_prices WHERE id_contract = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
                   
            ps.close();
            
        	return  numRows;
	          
		} catch (SQLException e) {
			
			e.printStackTrace();
		};
       
		return 0;
		
	}
	
	
	public static int createNewPrice(WsContractPriceData data) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return  createNewPrice(conn, data);
		
	}
	
	//returns inserted id
	public static int createNewPrice(Connection conn, WsContractPriceData  data) {
		
	        PreparedStatement ps = null;
	       
			try {
	
				
	        	String insertion_order = "INSERT INTO contract_prices (id_contract, id_part_type, id_units, "
	        			+ "cost, nds, costwnds) VALUES(?, ?, ?, ?, ?, ?);";
	       	  	           
				ps = conn.prepareStatement(insertion_order, Statement.RETURN_GENERATED_KEYS);
						
	            ps.setInt(1, data.id_contract);
	            
	            ps.setInt(2, data.id_part_type);
	            
	            ps.setInt(3, data.id_units);
	            
	            ps.setDouble(4, data.cost);
	            
	            ps.setDouble(5, data.nds);
	            
	            ps.setDouble(6, data.costwnds);
	            
	            @SuppressWarnings("unused")
				int procesedRows = ps.executeUpdate();
	            
	            int inserted_id = -1;
	            
	            ResultSet rs = ps.getGeneratedKeys();
	            
	            if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	            }
	            
	            rs.close();
	            
	            ps.close();
	            
	            if(inserted_id == -1) { return -1; }
	            
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return -1;
		
	}
	
	
	public static boolean updateContractPrice(WsContractPriceData data) {

	       PreparedStatement ps = null;
	       
			try {

	        	String update = "UPDATE contract_prices SET id_contract = ?,  id_part_type = ?, "
	        			+ "id_units = ?, cost = ?, nds = ?, costwnds = ? WHERE id = ?;";
	        	         
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(update);
				
	            ps.setInt(1, data.id_contract);
	            
	            ps.setInt(2, data.id_part_type);
	            
	            ps.setInt(3, data.id_units);
	            
	            ps.setDouble(4, data.cost);
	            
	            ps.setDouble(5, data.nds);
	            
	            ps.setDouble(6, data.costwnds);
	            
	            ps.setInt(7, data.id);
	            
	            
	            if(ps.executeUpdate() != 1) { 
	            	
	            	return false; 
	            }
	            
	            ps.close();
	            
	            return true;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return false;
		
	}
	
	
	public static boolean updateContractPriceCost(int id, double cost, double nds, double costwnds) {

		 
	       PreparedStatement ps = null;
	       
			try {

	        	String update = "UPDATE contract_prices SET cost = ?, nds = ?, costwnds = ? WHERE id = ?;";
	        	         
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(update);

	            ps.setDouble(1, cost);
	            
	            ps.setDouble(2, nds);
	            
	            ps.setDouble(3, costwnds);
	            
	            ps.setInt(4, id);

	            if(ps.executeUpdate() != 1) { 
	            	
	            	return false; 
	            }
	            
	            ps.close();
	            
	            return true;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return false;
		
	}
	
	public static void addPricesToContract(Vector<WsPartType> data_import, int id_contract, boolean merge) {
		

		Vector<WsContractPriceData> vec = getContractPriceList(id_contract, 0);
		
		if( !merge && !vec.isEmpty() ) {  
			
			deleteAllPricesForContract(id_contract); 
			
		}
		
		for(int i = 0; i < data_import.size(); ++i) {
			
			WsPartType p = data_import.elementAt(i);
			
			int kod = p.kod;
			
			boolean found = false;
			
			WsContractPriceData d = null;
			
			for(int j = 0; j < vec.size(); ++j) {
				
				d = vec.elementAt(j);
				
				if(kod == d.kod) { found = true ; break; }
				
			}
			
			if(found && d != null) {
				
				d.costwnds = p.costwithnds;
				
				d.cost = d.costwnds/p.nds_coeff;
				
				d.nds = d.costwnds - d.cost;
				
				updateContractPrice(d);
			}
			else {
				
				d = new WsContractPriceData();
				
				d.id_contract = id_contract;
				
				d.costwnds = p.costwithnds;
				
				d.cost = d.costwnds/p.nds_coeff;
				
				d.nds = d.costwnds - d.cost;
				
				d.kod = p.kod;
				
				WsPartType pt_d = WsUtilSqlStatements.getPartTypeForKod(p.kod);
				
				d.id_part_type = pt_d.id;
						
				d.name = pt_d.name;
				
				WsUnitData ud = null;
				
				if(p.kod == WsUtils.EGG_KOD_1 || p.kod == WsUtils.EGG_KOD_2 ) {
					
					ud = WsUtilSqlStatements.getShtUnit();
				}
				else {
					
					ud = WsUtilSqlStatements.getKgUnit();
				}
			
				d.id_units = ud.id;
				
				d.units_name = ud.name;
				
				createNewPrice(d);
				
			}		
		}

	}

}
