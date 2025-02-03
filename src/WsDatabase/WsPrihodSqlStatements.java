
package WsDatabase;

import static WsMain.WsUtils.getMessagesStrs;
import static WsMain.WsUtils.getGuiStrs;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JOptionPane;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsAgentTypeData;
import WsDataStruct.WsContractPriceData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsMain.WsCatalogKods;
import WsMain.WsUtils;


/**
 * The collections of SQL statements for 'prihod' tables in the database.
 * 'Prihod' operations means all input invoices.
 * There are 2 basic 'prihod' table in the database: 'invoices' and 'invoice_parts'.
 * The relation between them is one to many.
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsPrihodSqlStatements {
	
	
	public static WsPrihodData getPrihodForId(int id) {
		
        Statement st;
        
        if(id == -1) return null;
       
		try {
			
			WSConnect.get();
			
			st = WSConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        		        	
		        String s = "SELECT invoices.id, invoices.number, invoices.date, invoices.info,"
		        			+ " counterparties.name,  id_counterparty, date_doc, contracts.name,  id_contract   FROM invoices "
		        			+ "INNER JOIN counterparties ON counterparties.id = invoices.id_counterparty  "
		        			+ "INNER JOIN contracts ON contracts.id = invoices.id_contract  "
		        			+ "WHERE invoices.id = " + Integer.toString(id) + ";";
		        	
		        ResultSet rs = st.executeQuery(s);
		        	
		        if (rs.next()){
		        		
		        		WsPrihodData dt = new WsPrihodData();
		            	
		                dt.id = rs.getInt(1);
		                
		                dt.number = rs.getString(2);
		                
		                dt.date = rs.getDate(3);
		                
		                dt.info = rs.getString(4);
		                
		                dt.agentName = rs.getString(5);
		                
		                dt.id_counterparty = rs.getInt(6);
		                
		                dt.date_doc = rs.getDate(7);
		                
		                dt.contractNumber = rs.getString(8);
		                
		                dt.id_contract = rs.getInt(9);
		                
		                rs.close();
		                
		                return dt;
		              
		        }
		        else { 
		        	return null; 
		        }
	        }
		} catch (SQLException e) {


			e.printStackTrace();
		}
       
		return null;	
	}
	

	public static boolean checkPrihodIsUsed(int id) {
		
		if (id  == -1) return false;
	       
		try {
			
        	String st = "SELECT invoices.id   FROM invoices "
        			+ "INNER JOIN invoice_parts ON invoices.id = invoice_parts.id_invoice  "
        			+ "WHERE invoices.id = ? AND abs(quantity - rest) > 0.001;";
       	  	          
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
	
	
	public static int deletePrihodForId(int id) {
		
		if (id  == -1) return 0;
       
		try {
		
			//validation
			
			if( checkPrihodIsUsed(id)) {
				
		        JOptionPane.showMessageDialog(
        			    WsUtils.get().getMainWindow(),
        			    getMessagesStrs("deletePrihodFailedUsedMessage"),
        			    getMessagesStrs("messageInfoCaption"),
        			    JOptionPane.CLOSED_OPTION);
				
				return 0;
			}
			
        	String delete_st = "DELETE FROM invoice_parts WHERE id_invoice = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
        
            ps.setInt(1, id);
     
            int numRows = ps.executeUpdate();
                    
            delete_st = "DELETE FROM invoices WHERE id = ?;";
            		    
            ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
            
            ps.setInt(1, id);
            
            numRows += ps.executeUpdate();
            
        	return  numRows;
	          
		} catch (SQLException e) {
			
			e.printStackTrace();
		};
       
		return 0;
		
	}	
	
	
	public static Vector<WsPrihodData> getPrihodList(int id_part_type, int id_agent) {
		
        Statement st;
       
		try {
			
			WSConnect.get();
			
			st = WSConnect.getCurrentConnection().createStatement();
			
			 Vector<WsPrihodData> vec = new  Vector<WsPrihodData>();
			
	        if (st != null) {
	        	
	        	StringBuilder s_b = new StringBuilder(); 
	        			
	        	s_b.append("SELECT invoices.id, invoices.number, invoices.date, invoices.info,"
	        			+ " counterparties.name,  id_counterparty, date_doc   FROM invoices "
	        			+ "INNER JOIN counterparties ON counterparties.id = invoices.id_counterparty  ");
	        	
	        	boolean flag = false;
	        	
	        	if(id_part_type != -1) {
	        		
	        		s_b.append(" INNER JOIN invoice_parts ON invoice_parts.id_invoice = invoices.id "
	        				+ "WHERE id_part_type = " );
	        		
	        		s_b.append( Integer.toString(id_part_type) );
	        		
	        		s_b.append( " ");
	        		
	        		flag = true;
	        	}

	        	if(id_agent != -1) {
	        		
	        		if(flag) { 
	        			
	        			s_b.append(" AND "); 
	        		}
	        		else {
	        			s_b.append(" WHERE ");
	        		}
	        		
	        		s_b.append(" id_counterparty = ");
	        		
	        		s_b.append( Integer.toString(id_agent));
	        		
	        		s_b.append( " ");
	        	}
	        	
	        	s_b.append(" GROUP BY invoices.id ORDER BY invoices.date;");

	        	ResultSet rs =  st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsPrihodData d = new WsPrihodData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.number = rs.getString(2);
	        		
	        		d.date = rs.getDate(3);
	        		
	        		d.info = rs.getString(4);
	        		
	        		d.agentName = rs.getString(5);
	        		
	        		d.id_counterparty = rs.getInt(6);
	        		
	        		d.date_doc = rs.getDate(7);
	        		
	        		vec.add(d);
	        		
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return vec;
	        
	        }
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
       
    
		return null;
		
		
	}
	
	public static Vector<WsPrihodData> getPrihodList(int id_part_type, int id_agent, int id_contract,
			Date start_d, Date end_d) {
		

        boolean data_is_valid = start_d != null && end_d != null && (start_d.before(end_d) || start_d.equals(end_d));
       
		try {
			
			WSConnect.get();

			Vector<WsPrihodData> vec = new  Vector<WsPrihodData>();
		
        	StringBuilder s_b = new StringBuilder(); 
        			
        	s_b.append("SELECT invoices.id, invoices.number, invoices.date, invoices.info,"
        			+ " counterparties.name,  id_counterparty, invoices.date_doc, invoices.id_contract,"
        			+ " contracts.name  FROM invoices "
        			+ "INNER JOIN counterparties ON counterparties.id = invoices.id_counterparty  "
        			+ "INNER JOIN contracts ON contracts.id = invoices.id_contract");
        	
        	boolean flag = false;
        	
        	if(id_part_type != -1) {
        		
        		s_b.append(" INNER JOIN invoice_parts ON invoice_parts.id_invoice = invoices.id "
        				+ "WHERE id_part_type = " );
        		
        		s_b.append( Integer.toString(id_part_type));
        		
        		s_b.append( " ");
        		
        		flag = true;
        	}

        	if(id_agent != -1) {
        		
        		if(flag) { s_b.append(" AND "); } else {  s_b.append(" WHERE "); }
        		
        		s_b.append(" id_counterparty = " );
        		
        		s_b.append( Integer.toString(id_agent) );
        		
        		s_b.append( " ");
        		
        		flag = true;
        	}
        	
        	if(id_contract != -1) {
        		
        		if(flag) { s_b.append(" AND "); } else {  s_b.append(" WHERE "); }
        		
        		s_b.append(" invoices.id_contract = " );
        		
        		s_b.append( Integer.toString(id_contract) );
        		
        		s_b.append( " ");
        		
        		flag = true;
        	}
        	
        	if(data_is_valid) {
        		
        		if(flag) { s_b.append(" AND "); }  else { s_b.append(" WHERE "); }
        		
        		s_b.append(" invoices.date BETWEEN ? AND ? ");
        		
        	}
        	
        	s_b.append(" GROUP BY invoices.id ORDER BY invoices.date;");
        	
        	PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(s_b.toString());
        	
        	if(data_is_valid) {
        		
        		ps.setDate(1, start_d);
	        	
	        	ps.setDate(2, end_d);
        	}

        	ResultSet rs =  ps.executeQuery();
        	
        	while(rs.next()) {
        		
        		WsPrihodData d = new WsPrihodData();
        		
        		d.id = rs.getInt(1);
        		
        		d.number = rs.getString(2);
        		
        		d.date = rs.getDate(3);
        		
        		d.info = rs.getString(4);
        		
        		d.agentName = rs.getString(5);
        		
        		d.id_counterparty = rs.getInt(6);
        		
        		d.date_doc = rs.getDate(7);
        		
        		d.id_contract = rs.getInt(8);
        		
        		d.contractNumber = rs.getString(9);
        		
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
	
	public static Vector<WsPrihodData> getPrihodList(Date start_d, Date end_d) {
		
		WSConnect.get();
	 
		Connection conn = WSConnect.getCurrentConnection();
		
		return getPrihodList(conn, start_d, end_d);
	
	}
	
	
	public static Vector<WsPrihodData> getPrihodList(Connection conn, Date start_d, Date end_d) {
		
        Statement st;
       
		try {
			
			st = conn.createStatement();
			
			Vector<WsPrihodData> vec = new  Vector<WsPrihodData>();
			
	        if (st != null) {
	        	
	        	String s = "SELECT invoices.id, invoices.number, invoices.date, invoices.info,"
	        			+ " counterparties.name,  id_counterparty,  invoices.date_doc, invoices.id_external  FROM invoices "
	        			+ "INNER JOIN counterparties ON counterparties.id = invoices.id_counterparty "
	        			+ " WHERE invoices.date BETWEEN ? AND ?;";
	        	
	        	PreparedStatement ps = conn.prepareStatement(s);
	        	
	        	ps.setDate(1, start_d);
	        	
	        	ps.setDate(2, end_d);

	        	ResultSet rs =  ps.executeQuery();
	        	
	        	while(rs.next()) {
	        		
	        		WsPrihodData d = new WsPrihodData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.number = rs.getString(2);
	        		
	        		d.date = rs.getDate(3);
	        		
	        		d.info = rs.getString(4);
	        		
	        		d.agentName = rs.getString(5);
	        		
	        		d.id_counterparty = rs.getInt(6);
	        		
	        		d.date_doc = rs.getDate(7);
	        		
	        		d.id_external = rs.getInt(8);
	        		
	        		vec.add(d);
	        		
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return vec;
	        
	        }
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
       
		return null;
	}
	
	public static Vector<WsPrihodPartData> getPrihodPartsList(int id_invoice, int sort_type) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getPrihodPartsList(conn, id_invoice, sort_type);
		
	}
	
	
	public static Vector<WsPrihodPartData> getPrihodPartsList(Connection conn, int id_invoice, int sort_type) {
		
        Statement st;
       
		try {
			
			st = conn.createStatement();
			
			Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>();
			
	        if (st != null) {
	        	
	        	String s_order = " part_types.kod;";
	        	
	        	switch(sort_type) {
	        	
	        		case 1: {
	        			s_order = " part_types.name;";
	        			break;
	        		}
	        		case 2: {
	        			s_order = " quantity;";
	        			break;
	        		}
	        		case 0:
	        		default: {
	        			s_order = " part_types.kod;";
	        			break;
	        		}
	        	}

	        	StringBuilder s_b =  new StringBuilder();
	        	
	        	s_b.append("SELECT invoice_parts.id, part_types.name, invoice_parts.name, quantity, "
	        			+ " rest, units.name, cost, nds, vendor_code_2, closed, invoice_parts.info, "
	        			+ " id_invoice, "
	        			+ " id_part_type, id_units, kod, costnds  FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE id_invoice = ");
	        	
	        	s_b.append( Integer.toString(id_invoice));
	        	
	        	s_b.append( " ORDER BY " );
	        	
	        	s_b.append( s_order);
	        	
	        	ResultSet rs =   st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsPrihodPartData d = new WsPrihodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.part_type_name = rs.getString(2);
	        		
	        		d.name = rs.getString(3);
	        		
	        		d.quantity = rs.getDouble(4);
	        		
	        		d.rest = rs.getDouble(5);
	        		
	        		d.units_name = rs.getString(6);
	        		
	        		d.cost = rs.getDouble(7);
	        		
	        		d.nds = rs.getDouble(8);
	        		
	        		d.vendorcode2 = rs.getString(9);
	        		
	        		d.closed = rs.getBoolean(10);
	        		
	        		d.info = rs.getString(11);
	        		
	        		d.id_invoice = rs.getInt(12);
	        		
	        		d.id_part_type = rs.getInt(13);
	        		
	        		d.id_units = rs.getInt(14);
	        		
	        		d.kod = rs.getInt(15);
	        		
	        		d.costwithnds = rs.getDouble(16);
	        		
	        		vec.add(d);
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return vec;
	        
	        }
		} catch (SQLException e) {

			e.printStackTrace();
		}
       
    
		return null;
		
		
	}
	
	public static int createNewPrihod(WsPrihodData data, Vector<WsPrihodPartData> vec) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return  createNewPrihod(conn, data, vec);
		
	}
	
	//returns inserted id
	public static int createNewPrihod(Connection conn, WsPrihodData data, Vector<WsPrihodPartData> vec) {
		
			if(data.info == null) { data.info = "";}
		
	       PreparedStatement ps = null;
	       
			try {
				
	        	String insertion_order = "INSERT INTO invoices (number, date, info,  id_counterparty, date_doc,"
	        			+ "id_contract)"
	        			+ " VALUES(?,?,?,?,?,?);";
	       	  	           
				ps = conn.prepareStatement(insertion_order, Statement.RETURN_GENERATED_KEYS);
						
	            ps.setString(1, data.number);
	            
	            ps.setDate(2, data.date);
	            
	            ps.setString(3, data.info);
	            
	            ps.setInt(4, data.id_counterparty);
	            
	            if(data.date_doc == null) { data.date_doc =  data.date; }
	            
	            ps.setDate(5, data.date_doc);
	            
	            ps.setInt(6, data.id_contract);
	            
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
	            
	            boolean flag = false;
	            
	            if(!vec.isEmpty()) {
	            
	            	Iterator<WsPrihodPartData> value = vec.iterator();
	            
		            StringBuilder insertion_parts_b = new StringBuilder();
		            
		            insertion_parts_b.append("INSERT INTO  invoice_parts ( id_invoice, \n"
			                + " id_part_type, name, quantity, rest, \n"
			                + " id_units, vendor_code_2, closed, info, cost, nds, costnds)\n"
			                + " VALUES ");
		            
		            while(value.hasNext()) {
		            	
		            	WsPrihodPartData d = (WsPrihodPartData) value.next();
		            	
		            	if(d.kod == 4117) {
		            		
		            		insertion_parts_b.append("");
		            	}
		            	
		            	insertion_parts_b.append("(" );
		            	
		            	insertion_parts_b.append( Integer.toString(inserted_id) );
		            	
		            	insertion_parts_b.append( ",\n");
		            	
		            	insertion_parts_b.append( Integer.toString(d.id_part_type) );
		            	
		            	insertion_parts_b.append( ",\n");
		            	
		            	insertion_parts_b.append( "'" );
		            	
		            	insertion_parts_b.append( d.name );
		            	
		            	insertion_parts_b.append( "',\n");
		            	
		            	insertion_parts_b.append( Double.toString(d.quantity) );
		            	
		            	insertion_parts_b.append( ",\n");
		            	
		            	insertion_parts_b.append( Double.toString(d.rest) );
		            	
		            	insertion_parts_b.append( ",\n");
		            	
		            	insertion_parts_b.append(  Integer.toString(d.id_units));
		            	
		            	insertion_parts_b.append( ",\n");
		            	
		            	insertion_parts_b.append( "'" );
		            	
		            	insertion_parts_b.append( d.vendorcode2 );
		            	
		            	insertion_parts_b.append( "',\n");
		            	
		            	insertion_parts_b.append( Boolean.toString(d.closed) );
		            	
		                insertion_parts_b.append( "," );
		                
		            	insertion_parts_b.append( "'" );
		            	
		            	insertion_parts_b.append(d.info );
		            	
		            	insertion_parts_b.append( "',");
		            	
		            	insertion_parts_b.append( Double.toString(d.cost) );
		            	
		            	insertion_parts_b.append( ",");
		            	
		            	insertion_parts_b.append( Double.toString(d.nds) );
		            	
		            	insertion_parts_b.append( ",");
		            	
		            	insertion_parts_b.append( Double.toString(d.costwithnds) );
		            	
		            	insertion_parts_b.append( ")");
		            			
		            	if(value.hasNext()) {
		            		
		            		insertion_parts_b.append( ",");
		            	}
		            	
		            }
		            
		            insertion_parts_b.append(";");
		            
		            ps = conn.prepareStatement(insertion_parts_b.toString());
		            
		            flag = ps.executeUpdate() == vec.size();
	            
	            }
	            
	            ps.close();
	            
	            if(!flag) { return -1; }
	            
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if(e instanceof org.sqlite.SQLiteException) {
					
					   JOptionPane.showMessageDialog(
				   			    WsUtils.get().getMainWindow(),
				   			    getMessagesStrs("databaseLockedForExportMessage"),
				   			    getMessagesStrs("messageInfoCaption"),
				   			    JOptionPane.CLOSED_OPTION);
					
				}
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
		
			
			return -1;
		
	}
	
	
	public static boolean checkIsPartIdExists(int id) {
		
		 if (id == -1) { return false; }
		
		 String exist_rows = "SELECT id FROM invoice_parts WHERE id = " + Integer.toString(id) + ";";
		   
		 WSConnect.get();
			
		 try {
			 
			Statement st = WSConnect.getCurrentConnection().createStatement();
			
			ResultSet rs = st.executeQuery(exist_rows);
			
			if(rs != null && rs.next( )) {
				
				return true;
			}
			
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
			e.printStackTrace();
			}
		}
		      
		return false;
		    
	}
	
	
	public static int updateDeletePartsPrihod(WsPrihodData data, Vector<WsPrihodPartData> vec) {
		
		 String exist_rows = "SELECT id FROM invoice_parts WHERE id_invoice = " + Integer.toString(data.id) + ";";
		   
		 WSConnect.get();
		 
		 int numDeleteRows = 0;
			
		 try {
			 
			Statement st = WSConnect.getCurrentConnection().createStatement();
			
			ResultSet rs = st.executeQuery(exist_rows);
			
			while(rs.next()) {
				
				boolean found = false;
				
				int id = rs.getInt(1);
				
				for(int i = 0; i < vec.size(); ++i) {
					
					WsPrihodPartData d = vec.elementAt(i);
					
					if( d.id == id) { found = true; break; }
					
				}
				
				if(!found) {
					
				    String delete_st = "DELETE FROM invoice_parts WHERE id = ?;";
		  	  	     
		            PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
		            
		            ps.setInt(1, id);
		            
		            numDeleteRows += ps.executeUpdate();
		            
		        	ps.close();
				}
				
				
			}
			
			rs.close();

			
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
			e.printStackTrace();
			}
		}
		   
		return numDeleteRows;

	}
	
	
	public static boolean updatePrihod(WsPrihodData data, Vector<WsPrihodPartData> vec) {
		
	       PreparedStatement ps = null;
	       
			try {

	        	String update = "UPDATE invoices SET number = ?,   date = ?, info = ?, id_counterparty = ?, "
	        			+ "date_doc = ?, id_contract = ? WHERE id = ?;";
	        	         
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(update);
				
	            ps.setString(1, data.number);
	            
	            ps.setDate(2, data.date);
	            
	            ps.setString(3, data.info);
	            
	            ps.setInt(4, data.id_counterparty);
	            
	            if(null == data.date_doc) { data.date_doc = data.date;}
	            
	            ps.setDate(5, data.date_doc);
	            
	            ps.setInt(6, data.id_contract);
	            
	            ps.setInt(7, data.id);
	            
	            if(ps.executeUpdate() != 1) { 
	            	return false; 
	            }
	            
	            ps.close();
	            
	            //check and delete rows which are in the DB , but was deleted in the table
	            updateDeletePartsPrihod(data, vec);
	     
         		String update_parts = "UPDATE invoice_parts SET id_invoice = ?, id_part_type = ?,\n"
         			+ "name = ?, quantity = ?,\n"
         			+ " rest = ?, id_units = ?, \n "
         			+ " vendor_code_2 = ?,\n"
         			+ " closed = ?, info = ?, cost = ?, nds = ?, costnds = ?\n"
         			+ " WHERE id = ?;";
         	
         		Iterator<WsPrihodPartData> value = vec.iterator();
             
         		int numRowsAffected = 0;
	            
	            while(value.hasNext()) {
	            	
	            	WsPrihodPartData d = (WsPrihodPartData) value.next();
	            	
	             	WSConnect.get();
	             	
	             	if (checkIsPartIdExists(d.id)) {
	             		
						ps = WSConnect.getCurrentConnection().prepareStatement(update_parts);
						
			            ps.setInt(1, d.id_invoice); 
			            
			            ps.setInt(2, d.id_part_type);
			            
			            ps.setString(3, d.name); 
			            
			            ps.setDouble(4, d.quantity);
			            
			            ps.setDouble(5, d.rest); 
			            
			            ps.setInt(6, d.id_units);
			            
			            ps.setString(7, d.vendorcode2); 
			            
			            ps.setBoolean(8, d.closed); 
			            
			            ps.setString(9, d.info);
	
			            ps.setDouble(10, d.cost); 
			            
			            ps.setDouble(11, d.nds);
			            
			            ps.setDouble(12, d.costwithnds);
			            
			            ps.setInt(13, d.id);
		            	
			            numRowsAffected = ps.executeUpdate();
			            
			            if(numRowsAffected != 1) { ps.close(); return false; }
	             	}
	             	else {
	             		
	             		int inserted_id = data.id;
	             		
	                    StringBuilder insertion_parts_b = new StringBuilder();
	                    		
	                    insertion_parts_b.append("INSERT INTO  invoice_parts ( id_invoice, \n");
	                    
	                    insertion_parts_b.append( " id_part_type, name, quantity, rest, \n");
	                    
	                    insertion_parts_b.append( " id_units, vendor_code_2, closed, info, cost, nds)\n");
	                    
	                    insertion_parts_b.append( "VALUES(" );
	                    
	                    insertion_parts_b.append( Integer.toString(inserted_id) );
	                    
	                    insertion_parts_b.append( ",\n");
	                    
	                    insertion_parts_b.append( Integer.toString(d.id_part_type) );
	                    
	                    insertion_parts_b.append( ",\n");
	                    
	                    insertion_parts_b.append( "'" );
	                    
	                    insertion_parts_b.append( d.name );
	                    
	                    insertion_parts_b.append( "',\n");
	                    
	                    insertion_parts_b.append( Double.toString(d.quantity) );
	                    
	                    insertion_parts_b.append( ",\n");
	                    
	                    insertion_parts_b.append( Double.toString(d.rest) );
	                    
	                    insertion_parts_b.append( ",\n");
	                    
	                    insertion_parts_b.append(  Integer.toString(d.id_units) );
	                    
	                    insertion_parts_b.append( ",\n");
	                    
	                    insertion_parts_b.append( "'" );
	                    
	                    insertion_parts_b.append( d.vendorcode2 );
	                    
	                    insertion_parts_b.append( "',\n");
	                    
	                    insertion_parts_b.append( Boolean.toString(d.closed) );
	                    
	                    insertion_parts_b.append( "," );
	                    
	                    insertion_parts_b.append( "'" );
	                    
	                    insertion_parts_b.append( d.info );
	                    
	                    insertion_parts_b.append( "', " );
	                    
	                    insertion_parts_b.append( Double.toString(d.cost) );
	                    
	                    insertion_parts_b.append( ",");
	                    
	                    insertion_parts_b.append( Double.toString(d.nds) );
	                    
	                    insertion_parts_b.append( ");");
	    	            			
	         	       PreparedStatement ps1 = WSConnect.getCurrentConnection().prepareStatement(insertion_parts_b.toString());
	    	            
	    	           if(ps1.executeUpdate() != 1) { 
	    	        	   
	    	        	   ps.close();
	    	        	   
	    	        	   ps1.close();
	    	        	   
	    	        	   return false; 
	    	            }
	    	            
	    	           ps1.close();
	             	}
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
	
	public static double getRestForInvoicePartId(int id_invoice_part) {
		
		
		String s_st = "SELECT rest FROM invoice_parts WHERE id = ?;";
		
		PreparedStatement ps0;
		
		try {
			
			ps0 = WSConnect.getCurrentConnection().prepareStatement(s_st);
	
	        ps0.setInt(1, id_invoice_part);
	        
	        ResultSet rs1 = ps0.executeQuery();
	        
	        rs1.next();
	        
	        double v = rs1.getDouble(1);
	        
	        rs1.close();
	        
	        ps0.close();
	        
	        return v;
        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
		return -1.0;

	}
	
	public static double getQuantityForInvoicePartId(int id_invoice_part) {
		
		String s_st = "SELECT quantity FROM invoice_parts WHERE id = ?;";
		
		PreparedStatement ps0;
		
		try {
			
			ps0 = WSConnect.getCurrentConnection().prepareStatement(s_st);
	
	        ps0.setInt(1, id_invoice_part);
	        
	        ResultSet rs1 = ps0.executeQuery();
	        
	        rs1.next();
	        
	        double v = rs1.getDouble(1);
	        
	        rs1.close();
	        
	        ps0.close();
	        
	        return v;
        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
		return -1.0;
	
	}
	
	
	
	public static WsPrihodData getPrihodForPartId(int id) {
		
        Statement st;
        
        if(id == -1) return null;
       
		try {
			
			WSConnect.get();
			
			st = WSConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	
	        	StringBuilder s_b = new StringBuilder();
	        	
	        	s_b.append("SELECT invoices.id, invoices.number, invoices.date, invoices.info,"
	        			+ " counterparties.name,  id_counterparty, date_doc   FROM invoices "
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id_invoice = invoices.id "
	        			+ " INNER JOIN counterparties ON counterparties.id = invoices.id_counterparty  "
	        			+ " WHERE invoice_parts.id = ");
	        	
	        	s_b.append( Integer.toString(id));
	        	
	        	s_b.append(";");
	        	
	        	ResultSet rs = st.executeQuery(s_b.toString());
	        	
	        	if (rs.next()){
	        		
	        		WsPrihodData dt = new WsPrihodData();
	            	
	                dt.id = rs.getInt(1);
	                
	                dt.number = rs.getString(2);
	                
	                dt.date = rs.getDate(3);
	                
	                dt.info = rs.getString(4);
	                
	                dt.agentName = rs.getString(5);
	                
	                dt.id_counterparty = rs.getInt(6);
	                
	                dt.date_doc = rs.getDate(7);
	                
	                rs.close();
	                
	                return dt;
	              
	            }
	        	else { return null; }
	        	
	        
	        }
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;

	}
	
	public static boolean isNewDateForUsedPrihodValid(int invoice_id, Date dt) {
		
		if (invoice_id  == -1 || dt == null) return false;
	       
		try {
			
        	String st = "SELECT sale_invoices.date   FROM invoice_parts "
        			+ " INNER JOIN sale_parts ON sale_parts.id_invoice_parts  = invoice_parts.id "
        			+ " INNER JOIN sale_invoices ON sale_parts.id_sale_invoice  = sale_invoices.id "
        			+ " WHERE invoice_parts.id_invoice = ?;";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ps.setInt(1, invoice_id);
     
            ResultSet rs = ps.executeQuery();
           
            while( rs.next() ) {
            	
            	if( dt.compareTo(rs.getDate(1)) > 0 ) { return false; }
            	
            } 
        	
        	rs.close();
           
        	return  true;
	          
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return false;
		}
				
	}
	
	public static boolean changePrihodKodForRashod(int id_invoice_part, int new_id_part_kod, String newName) {
		
	       PreparedStatement ps = null;
	       
			try {

	        	String update = "UPDATE sale_parts SET name = ?, vendor_code_2 = ? WHERE id_invoice_parts = ?;";
	        	         
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(update);
				
				ps.setString(1, newName);
				
	            ps.setString(2, String.valueOf(new_id_part_kod));
	            
	            ps.setInt(3,  id_invoice_part);
	            
	            if(ps.executeUpdate() < 1) { return false; }
	            
	            ps.close();
	            
	            return true;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return false;
		
	}
	
	//collects all positions which are with kod == kod, between dates, and available to give out 
	public static double getSumPrihodForKod(int kod,  Date start_d, Date end_d ) {
		
		PreparedStatement ps;
        
		try {
			
			WSConnect.get();
			
	        String s = "SELECT SUM(invoice_parts.quantity)  FROM invoice_parts "
	        			+ " INNER JOIN invoices ON invoice_parts.id_invoice = invoices.id "
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " WHERE invoices.date BETWEEN ? AND ? AND  part_types.kod = ? AND invoice_parts.rest > ?" 
	        			+ " GROUP BY part_types.kod;";
	        	
	        	ps = WSConnect.getCurrentConnection().prepareStatement(s);
	        	
	        	ps.setDate(1, start_d);
	        	
	        	ps.setDate(2, end_d);
	        	
	        	ps.setInt(3, kod);
	        	
	        	ps.setDouble(4, WsUtils.getRZL());
	        	
	        	ResultSet rs = ps.executeQuery();
	            
	            if(!rs.next()) {return 0.0; };
	            		            	
	            double v = rs.getDouble(1);
	        	
	            return v;
	        	
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return -1.0;
	}
	
	public static Vector<WsPrihodPartData> getPrihodPartsListForIds(Vector<Integer> vec_ids, 
			int sort_type) {
		
        Statement st;
        
        StringBuilder condition_b = new StringBuilder();
        
        for(int i = 0; i < vec_ids.size(); ++i) {
        	
        	Integer id_vec = vec_ids.elementAt(i);
        	
        	if(i == (vec_ids.size() -1)) {
        		
        		condition_b.append(" id_invoice = " );
        		
        		condition_b.append(String.valueOf(id_vec));
        		
        	}
        	else {
        		condition_b.append(" id_invoice = " );
        		
        		condition_b.append(String.valueOf(id_vec) );
        		
        		condition_b.append( " OR ");
        	}
        	
        }
       
		try {
			
			WSConnect.get();
			
			st = WSConnect.getCurrentConnection().createStatement();
			
			Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>();
			
	        if (st != null) {
	        	
	        	String s_order = " part_types.kod;";
	        	
	        	switch(sort_type) {
	        	
	        		case 1: {
	        			s_order = " part_types.name;";
	        			break;
	        		}
	        		case 2: {
	        			s_order = " quantity;";
	        			break;
	        		}
	        		case 0:
	        		default: {
	        			s_order = " part_types.kod;";
	        			break;
	        		}
	        	}

	        	StringBuilder s_b = new StringBuilder();
	        	
	        	s_b.append("SELECT invoice_parts.id, part_types.name, invoice_parts.name, quantity, "
	        			+ " rest, units.name, cost, nds, vendor_code_2, closed, invoice_parts.info, "
	        			+ " id_invoice, "
	        			+ " id_part_type, id_units, kod, invoice_parts.costnds  FROM invoice_parts "
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE ");
	        	
	        	s_b.append( condition_b.toString() );
	        	
	        	s_b.append( " ORDER BY " );
	        	
	        	s_b.append( s_order);
	        	
	        	
	        	ResultSet rs =   st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsPrihodPartData d = new WsPrihodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.part_type_name = rs.getString(2);
	        		
	        		d.name = rs.getString(3);
	        		
	        		d.quantity = rs.getDouble(4);
	        		
	        		d.rest = rs.getDouble(5);
	        		
	        		d.units_name = rs.getString(6);
	        		
	        		d.cost = rs.getDouble(7);
	        		
	        		d.nds = rs.getDouble(8);
	        		
	        		d.vendorcode2 = rs.getString(9);
	        		
	        		d.closed = rs.getBoolean(10);
	        		
	        		d.info = rs.getString(11);
	        		
	        		d.id_invoice = rs.getInt(12);
	        		
	        		d.id_part_type = rs.getInt(13);
	        		
	        		d.id_units = rs.getInt(14);
	        		
	        		d.kod = rs.getInt(15);
	        		
	        		d.costwithnds = rs.getDouble(16);
	        		
	        		vec.add(d);
	        	}
	        	
	        	rs.close();
	        	
	        	st.close();
	        	
	        	return vec;
	        
	        }
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;
		
	}
	
	public static boolean importRest(Connection conn, java.sql.Date datePrihod, boolean toNewcCatalog) {
		
		 boolean flag0 = WsUtilSqlStatements.isCatalog5digits();
		
		 boolean flag1 = WsUtilSqlStatements.isCatalog5digits(conn);
		 
		 int shift1 = flag1 ? 10000: 0;
		
		 Vector<WsPrihodPartData>  vec  = WsSkladSqlStatements.getSkladListGroupKod(conn, 1);
		
		 Vector<WsPrihodPartData>  new_vec  = new Vector<WsPrihodPartData>();
		
		 HashMap<Integer, Integer> map_old_to_new = null;
		 
		 if(toNewcCatalog) {
			 
			 WsCatalogKods catalog = new WsCatalogKods();
			 
			 map_old_to_new = catalog.createOldNewCatalogMap();
		 }
		
		for(WsPrihodPartData d:  vec) {
			
			WsPartType dt =  WsUtilSqlStatements.getPartTypeForKod(conn, d.kod);
			
			if(dt == null) { continue; }
			
			WsPrihodPartData d_ins = new WsPrihodPartData(d);
			
			int kod = dt.kod - shift1;
			
			if(toNewcCatalog && map_old_to_new.containsKey(dt.kod)) {
				
				kod = map_old_to_new.get(dt.kod);
			}
			
	
			if(flag0 && kod < 9999) {   kod += 10000; }
				

			WsPartType new_dt =  WsUtilSqlStatements.getPartTypeForKod(kod);
			
			if(new_dt == null) {
				
				System.out.println(kod);
			}
			
			d_ins.id_part_type = new_dt.id;
			
			d_ins.kod = kod;
			
			d_ins.vendorcode2 = String.valueOf(kod);
			
			d_ins.quantity = d.rest;
			
			d_ins.name = new_dt.name;
			
			new_vec.add(d_ins);
			
		}
		
		WsPrihodData data = new WsPrihodData();
		
		data.date = datePrihod;
		
		data.date_doc = datePrihod;
		
		Vector<WsAgentData> a_vec = WSAgentSqlStatements.getAgentsList(-1);
		
		if(a_vec.isEmpty()) {
			
			WsAgentData dt = new WsAgentData();
			
			dt.name = "ForRestInsertion"; 
			
			Vector<WsAgentTypeData> t_vec = WSAgentSqlStatements.getAgentsTypes();
			
			dt.id_type = t_vec.elementAt(0).id;
			
			WSAgentSqlStatements.createNewAgent(dt);
			
			a_vec = WSAgentSqlStatements.getAgentsList(-1);
			
		}
		
		data.id_counterparty =  a_vec.elementAt(0).id;
		
		data.number = getGuiStrs("zali");
		
		createNewPrihod(data, new_vec);
		
		return true;
	}
	
	
	//the output vector contains only 2 elements - min and max date
	public static Vector<java.sql.Date> getPrihodMinMaxDate() {
		

		try {
			
			WSConnect.get();

	        String s = "SELECT MIN(invoices.date), MAX(invoices.date) FROM invoices ";
	        	     	
	        PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(s);
	        	
	        ResultSet rs =  ps.executeQuery();
	        	
	        Vector<java.sql.Date> vec = new Vector<java.sql.Date>();
	        
	        java.sql.Date min_date = null;
    		
    		java.sql.Date max_date = null;
	        	
	        if(rs.next()) {
		
        		min_date = rs.getDate(1);
        		
        		max_date = rs.getDate(2);
	        				
	        }
	        
    		vec.add(min_date);
    		
    		vec.add(max_date);
	        	
	        rs.close();
	        	
	        ps.close();
	        	
	        return vec;
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				e.printStackTrace();
			}
		}
	   
		return null;
	}
	
	public static boolean changeContractForId(int id, int id_new_contract, boolean rashodChangeFlag) {
		
	       PreparedStatement ps = null;
	       
			try {

	        	String update = "UPDATE invoices SET id_contract = ? WHERE id = ?;";
	        	         
	        	WSConnect.get();
	        	
				ps = WSConnect.getCurrentConnection().prepareStatement(update);
				
				ps.setInt(1, id_new_contract);
				
	            ps.setInt(2,id);
	            
	            if(ps.executeUpdate() < 1) { return false; }
	            
	            ps.close();
	            
	            return true;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return false;
		
	}
	
	
	public static int updatePricesForInvoice( int id_contract, int id_invoice, boolean rashodChangeFlag) {
		
		if(id_contract == -1) { return 0; }
		
		WSConnect.get();
		
		HashMap<Integer, WsContractPriceData> prices_map = 
				WsContractsSqlStatements.getContractPriceListMap(id_contract);
		
		if(prices_map.isEmpty()) { return 0; }
		
		int processed = 0;
		
		try {

			Vector<WsPrihodPartData> vec = getPrihodPartsList(id_invoice,0);
			
			PreparedStatement ps = null;
			
			for(WsPrihodPartData d : vec ) {
				
				WsContractPriceData data = prices_map.get(d.kod);
				
				if(data != null) {
				
					d.costwithnds = data.cost + data.nds;
					
					d.cost = data.cost;
					
					d.nds = data.nds;
					
	         		String update_parts = "UPDATE invoice_parts SET cost = ?, nds = ?, costnds = ? WHERE id = ?;";

					ps = WSConnect.getCurrentConnection().prepareStatement(update_parts);
					
		            ps.setDouble(1, d.cost );
		            
		            ps.setDouble(2, d.nds );
		            
		            ps.setDouble(3, d.costwithnds);
		            
		            ps.setInt(4, d.id);
		            
		            WsTransactions.beginTransaction(null);
		            
		            if(ps.executeUpdate() == 1) { 
		            	
		            	 String update = "UPDATE invoices SET id_contract = ? WHERE id = ?;";
		            	 
		            	 ps.close();

						 ps = WSConnect.getCurrentConnection().prepareStatement(update);
						
						 ps.setInt(1, id_contract);
						
						 ps.setInt(2, id_invoice);
						 
						 if(ps.executeUpdate() == 1) {
							 
			            	 ++processed;
			            	
			            	 if(rashodChangeFlag) { 
			            	
			            		 WsRashodSqlStatements.updateRashodPricesForIdPart( data, d.id);
			            	 }
			            	 
			            	 WsTransactions.commitTransaction(null);
		            	 
						 }
						 else {    WsTransactions.rollbackTransaction(null);  }
		            	
		            }
		            else {
		            	
		            	WsTransactions.rollbackTransaction(null);
		            }
		            
		            
		            ps.close();
				
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
		}
		
		return processed;
			
	}
	
}
