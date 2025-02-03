
package WsDatabase;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsContractData;
import WsDataStruct.WsDateComparator;
import WsDataStruct.WsMoveKodPage;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsSkladMoveDataColumn;
import WsDataStruct.WsSkladMoveDataRow;
import WsDataStruct.WsStaffData;
import WsDataStruct.WsWaterDataRow;
import WsDataStruct.WsWaterPeriodsData;
import WsMain.WsCatalogKods;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsReportsSqlStatements {
	
	public static Vector<WsSkladMoveDataRow> getSkladPrihodMovement(Date start_d, Date end_d) {
		
		WSConnect.get();
 
		Connection conn = WSConnect.getCurrentConnection();
		
		return getSkladPrihodMovement(conn, start_d, end_d);
	
	}
	
	
	public static Vector<WsSkladMoveDataRow> getSkladPrihodMovement(Connection conn, Date start_d, Date end_d) {
		
		if (start_d == null ||   end_d == null) return null;
	       
		try {
			
			Vector<WsPrihodData> nakl_vector = WsPrihodSqlStatements.getPrihodList(conn, start_d, end_d);
			
			Vector<WsSkladMoveDataRow> vec = new Vector<WsSkladMoveDataRow>();
			
			for(int i = 0; i < nakl_vector.size(); ++i) {
				
				WsPrihodData dn = nakl_vector.elementAt(i);
				
				WsSkladMoveDataRow row = new WsSkladMoveDataRow();
				
				row.date = dn.date;
				
				row.date_doc = dn.date_doc;
				
				row.nakl_number = dn.number;
				
				row.agent_name = dn.agentName; 
				
				row.agent_id = dn.id_counterparty;
				
				//sorted by code
				Vector<WsPrihodPartData> vec_parts = WsPrihodSqlStatements.getPrihodPartsList(conn, dn.id, 0);
				
				row.row_vec = new Vector<WsSkladMoveDataColumn>();
				
				for(int j = 0; j < vec_parts.size(); ++j) {
					
					WsPrihodPartData d_part =  vec_parts.elementAt(j); 
					
					WsSkladMoveDataColumn d_part_r = new WsSkladMoveDataColumn();
					
					d_part_r.in_quantity = d_part.quantity;
					
					d_part_r.kod = d_part.kod;
					
					d_part_r.name = d_part.name;
					
					d_part_r.units = d_part.units_name;
					
					//the case when there are 2 rows in one invoice with the same kod
					//we don't need two columns with the same kod, combine them in the single one
					boolean notfound = true;
					
					for(int i1 = 0; i1 < row.row_vec.size(); ++i1) {
						
						WsSkladMoveDataColumn d_ =  row.row_vec.elementAt(i1);
						
						if( WsUtils.isKodEqual(d_.kod ,d_part_r.kod)) {
							
							d_.in_quantity += d_part_r.in_quantity;
							
							notfound = false;
							
							break;
						}
					}
					
					if(notfound) {
						
						row.row_vec.add(d_part_r);
					}
					
				}
				
				vec.add(row);
			}
			
			return vec;
				        
	        
		} catch (Exception e) {

			return null;
		}
	}
	
	public static Vector<WsSkladMoveDataRow> getRashodMovement(Date start_d, Date end_d) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getRashodMovement(conn, start_d, end_d);
	}
	
	public static Vector<WsSkladMoveDataRow> getRashodMovement(Connection conn, Date start_d, Date end_d) {
		
		if (start_d == null ||   end_d == null) return null;
	       
		try {
			
			Vector<WsRashodData> nakl_vector = WsRashodSqlStatements.getRashodList(conn, start_d, end_d);
			
			Vector<WsSkladMoveDataRow> vec = new Vector<WsSkladMoveDataRow>();
			
			for(int i = 0; i < nakl_vector.size(); ++i) {
				
				WsRashodData dn = nakl_vector.elementAt(i);
				
				WsSkladMoveDataRow row = new WsSkladMoveDataRow();
				
				row.date = dn.date;

				row.nakl_number = dn.number;
				
				row.agent_name = dn.agentName; 
				
				row.agent_id = dn.id_counterparty;
				
				row.people = dn.people;
				
				//sorted by code
				Vector<WsRashodPartData> vec_parts = WsRashodSqlStatements.getRashodPartsList(conn, dn.id, 0);
				
				row.row_vec = new Vector<WsSkladMoveDataColumn>();
				
				for(int j = 0; j < vec_parts.size(); ++j) {
					
					WsRashodPartData d_part =  vec_parts.elementAt(j); 
					
					WsSkladMoveDataColumn d_part_r = new WsSkladMoveDataColumn();
					
					d_part_r.out_quantity = d_part.quantity;
					
					d_part_r.kod = d_part.kod;
					
					d_part_r.name = d_part.name;
					
					d_part_r.units = d_part.units_name;
					
					//the case when there are 2 rows in one invoice with the same kod
					//we don't need two columns with the same kod, combine them in the single one
					boolean notfound = true;
					
					for(int i1 = 0; i1 < row.row_vec.size(); ++i1) {
						
						WsSkladMoveDataColumn d_ =  row.row_vec.elementAt(i1);
						
						if( WsUtils.isKodEqual(d_.kod ,d_part_r.kod)) {

							d_.out_quantity += d_part_r.out_quantity;
							
							notfound = false;
							
							break;
						}
					}
					
					if(notfound) {
						
						row.row_vec.add(d_part_r);
					}
				}
				
				vec.add(row);
			}
			
			return vec;
				        
	        
		} catch (Exception e) {

			return null;
		}
		
		
	}
	
	public static Vector<WsSkladMoveDataColumn> getRestPartsListForDate(Date end_date, int id_contract) {
		
		WSConnect.get();
		
		Connection conn =  WSConnect.getCurrentConnection();
		
		return getRestPartsListForDate(conn, end_date, id_contract);
	
	}
	
	
	public static Vector<WsSkladMoveDataColumn> getRestPartsListForDate(Connection conn, Date end_date, int id_contract) {
	
       
		try {
			
				Vector<WsSkladMoveDataColumn> vec = new Vector<WsSkladMoveDataColumn>();
			
	         	String s1 = "SELECT  SUM(quantity), "
	        			+ " part_types.kod, part_types.name, units.name  FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE invoices.date <= ? ";
	         	
    	
	         	if(id_contract > 0) {
	         		
	         		s1 += " AND invoices.id_contract = ? ";
	         	}
	         	
	         	s1 += " GROUP BY part_types.kod ORDER BY part_types.kod;";
	         	
	        	PreparedStatement ps1 = conn.prepareStatement(s1);
				
	            ps1.setDate(1, end_date);
	            
	            if(id_contract > 0) {
	            	
	            	 ps1.setInt(2, id_contract);
	            	
	            }
	      	     
	        	ResultSet rs1 =   ps1.executeQuery();
	        	
	        	while(rs1.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs1.getInt(2);
	        		
	        		d.in_quantity = rs1.getDouble(1);
	        		
	        		d.q_array[0].in_quantity = rs1.getDouble(1);
	        		
	        		d.name = rs1.getString(3);
	    
	        		d.units = rs1.getString(4);
	        		
	        		vec.add(d);
	        	}
	        	
	        	rs1.close();
	        	
	        	ps1.close();
	        	
	        	String s2 = "SELECT SUM(sale_parts.quantity), part_types.kod, part_types.name, units.name FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id = sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN invoices ON invoice_parts.id_invoice = invoices.id"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE sale_invoices.date <= ? ";
	        	
	        	if(id_contract > 0) {
	         		
	         		s2 += " AND invoices.id_contract = ? ";
	         	}
	         	
	         	s2 += " GROUP BY part_types.kod ORDER BY part_types.kod;";
	        	
	        	PreparedStatement ps2 = conn.prepareStatement(s2);
				
	            ps2.setDate(1, end_date);
	            
	            if(id_contract > 0) {
	            	
	            	 ps2.setInt(2, id_contract);
	            	
	            }
	        	
	        	Vector<WsSkladMoveDataColumn> vec2 = new Vector<WsSkladMoveDataColumn>();
	        	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs2.getInt(2);
	        		
	        		d.out_quantity = rs2.getDouble(1);
	        		
	        		d.q_array[0].out_quantity = rs2.getDouble(1);
	        		
	        		d.name = rs2.getString(3);
	        		
	        		d.units = rs2.getString(4);
	    
	        		
	        		vec2.add(d);
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	        	
	        	for(int i =0; i < vec.size(); ++i) {
	        		
	        		WsSkladMoveDataColumn d = vec.elementAt(i);
	        		
	        		boolean foundInrashod = false;
	        		
	        		for(int j = 0; j < vec2.size(); ++j) {
	        			
	        			WsSkladMoveDataColumn d_ = vec2.elementAt(j);
	        			
	        			if( WsUtils.isKodEqual(d_.kod , d.kod)) {
	        				
	        				d.out_quantity = d_.out_quantity;
	        				
	        				d.rest = d.in_quantity - d.out_quantity;
	        				
	        				d.out_quantity = 0.0;
	        				
	        				d.in_quantity = 0.0;
	        				
	        				d.q_array[0].out_quantity = d_.q_array[0].out_quantity;
	        				
	        				d.q_array[0].rest = d.q_array[0].in_quantity - d.q_array[0].out_quantity;
	        				
	        				d.q_array[0].out_quantity = 0.0;
	        				
	        				d.q_array[0].in_quantity = 0.0;
	        				
	        				foundInrashod = true;
	        				
	        				break;
	        			}
	        			
	        		}
	        		
	        		if(!foundInrashod) {
	        			
	        			d.rest = d.in_quantity;
	        			
	        			d.out_quantity = 0.0;
        				
        				d.in_quantity = 0.0;
        				
        				d.q_array[0].rest = d.q_array[0].in_quantity;
	        			
	        			d.q_array[0].out_quantity = 0.0;
        				
        				d.q_array[0].in_quantity = 0.0;
	        			
	        		}
	        		
	        	}
	        	
	        	return vec;
	     
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;
	}
	
	
	public static double getRestPartsSumForDateAndKod(Date end_date, int kod_id) {
		
	       
		try {
			
				WSConnect.get();
			 
				Connection conn = WSConnect.getCurrentConnection();

	         	final String s1 = "SELECT  SUM(quantity) FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " WHERE invoices.date <= ? AND part_types.id = ? GROUP BY part_types.id;";
	         	
	        	PreparedStatement ps1 = conn.prepareStatement(s1);
				
	            ps1.setDate(1, end_date);
	            
	            ps1.setInt(2, kod_id);
	      	     

	        	final String s2 = "SELECT SUM(sale_parts.quantity) FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id = sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " WHERE  sale_invoices.date <= ? AND part_types.id = ? GROUP BY part_types.id;";
	        	
	        	PreparedStatement ps2 = conn.prepareStatement(s2);
				
	            ps2.setDate(1, end_date);
	            
	            ps2.setInt(2, kod_id);
	        	
	        	ResultSet rs1 =   ps1.executeQuery();
	        	
	        	double prihod = 0.0;
	        	
	        	if(rs1.next()) {

	        		prihod = rs1.getDouble(1);
	        		
	        	}
	        	
	        	rs1.close();
	        	
	        	ps1.close();
	        	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	double rashod = 0.0;
	        	
	        	if(rs2.next()) {

	        		rashod = rs2.getDouble(1);
	        		
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	        	
	        	return prihod - rashod;
	     
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return 0.0;
	}
	
	public static Vector<WsSkladMoveDataColumn> getPrihodPartsListForPeriod(Date start_date, Date end_date, int id_contract) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return getPrihodPartsListForPeriod(conn, start_date, end_date, id_contract);
	
	}
	
	public static Vector<WsSkladMoveDataColumn> getPrihodPartsListForPeriod(Connection conn, Date start_date, Date end_date,
			int id_contract) {
		
	       
		try {
			
			Vector<WsSkladMoveDataColumn> vec = new Vector<WsSkladMoveDataColumn>();
				
         	String s1 = "SELECT  SUM(quantity), "
        			+ " part_types.kod, part_types.name  FROM invoice_parts"
        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
        			+ " WHERE invoices.date BETWEEN ? AND ? ";
         	
         	if(id_contract > 0) {
         		
         		s1 += " AND invoices.id_contract = ? ";
         	}
         	
         	s1 += " GROUP BY part_types.kod ORDER BY part_types.kod; ";
         	
        	PreparedStatement ps1 = conn.prepareStatement(s1);
			
            ps1.setDate(1, start_date);
            
            ps1.setDate(2, end_date);
            
            if(id_contract > 0) {
            	
            	 ps1.setInt(3, id_contract);
            }
           
        		
        	ResultSet rs1 =   ps1.executeQuery();
        	
        	while(rs1.next()) {
        		
        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
        		
        		d.kod = rs1.getInt(2);
        		
        		d.in_quantity = rs1.getDouble(1);
        		
        		d.q_array[0].in_quantity = rs1.getDouble(1);
        		
        		d.name = rs1.getString(3);
    
        		vec.add(d);
        	}
        	
        	rs1.close();
        	
        	ps1.close();
        	
        	return vec;
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;

	}
	
	public static Vector<WsSkladMoveDataColumn> getRashodPartsListForDate(Date start_date, Date end_date, int id_contract) {
		
		WSConnect.get();
		
		Connection conn =  WSConnect.getCurrentConnection();
		
		return  getRashodPartsListForDate(conn, start_date, end_date,  id_contract);
	
	}
	
	
	public static Vector<WsSkladMoveDataColumn> getRashodPartsListForDate(Connection conn, Date start_date, Date end_date, int id_contract) {
	
       
		try {
			
				WSConnect.get();
        	
				Vector<WsSkladMoveDataColumn> vec = new Vector<WsSkladMoveDataColumn>();

	        	String s2 = "SELECT SUM(sale_parts.quantity), part_types.kod, part_types.name FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
	        			+ " WHERE  sale_invoices.date BETWEEN ? AND ? ";
	        	
	         	if(id_contract > 0) {
	         		
	         		s2 += " AND invoices.id_contract = ? ";
	         	}
	         	
	         	s2 += " GROUP BY part_types.kod ORDER BY part_types.kod;";
	        	
	        	PreparedStatement ps2 = conn.prepareStatement(s2);
				
	        	ps2.setDate(1, start_date);
	        	
	            ps2.setDate(2, end_date);
	            
	            if(id_contract > 0) {
	            	
	            	 ps2.setInt(3, id_contract);
	            }
	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs2.getInt(2);
	        		
	        		d.out_quantity = rs2.getDouble(1);
	        		
	        		d.q_array[0].out_quantity = rs2.getDouble(1);
	        		
	        		d.name = rs2.getString(3);
	        		
	        		vec.add(d);
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	          	
	        	return vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				e.printStackTrace();
			}
		}
       
		return null;

	}
	
	
	
	public static Vector<WsRashodPartData> getRashodPartsListForDateAndAgentKod(Date start_date, 
			Date end_date, int id_agent, int kod_id) {
		
		try {
			
				WSConnect.get();
        	
				Vector< WsRashodPartData> vec = new Vector< WsRashodPartData>();
			
	        	final String s2 = "SELECT SUM(sale_parts.quantity), part_types.kod, part_types.name, "
	        			+ "sale_invoices.date, sale_invoices.number,  units.name, SUM(sale_parts.cost), SUM(sale_parts.nds),"
	        			+ " SUM(sale_parts.costnds) FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN units ON invoice_parts.id_units = units.id"
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
	        			+ " WHERE  sale_invoices.date BETWEEN ? AND ? AND sale_invoices.id_counterparty = ? "
	        			+ " AND part_types.id = ?  GROUP BY sale_invoices.id;";
	        	
	        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
				
	        	ps2.setDate(1, start_date);
	        	
	            ps2.setDate(2, end_date);
	            
	            ps2.setInt(3, id_agent);
	            
	            ps2.setInt(4, kod_id);
	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		 WsRashodPartData d = new  WsRashodPartData();
	        		
	        		d.kod = rs2.getInt(2);
	        		
	        		d.quantity = rs2.getDouble(1);
	 
	        		d.name = rs2.getString(3);
	        		
	        		d.date = rs2.getDate(4);
	        		
	        		d.number = rs2.getString(5);
	        		
	        		d.units_name = rs2.getString(6);
	        		
	        		d.cost =  rs2.getDouble(7);
	        		
	        		d.nds = rs2.getDouble(8);
	        		 
	        		d.costwithnds = rs2.getDouble(9);
	        		
	        		vec.add(d);
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	          	
	        	return vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;

	}
	
	
	public static double getPrihodPartsSumForDateKod(Date start_date, 
			Date end_date, int kod_id) {
		
		try {
			
				WSConnect.get();
        	
	        	final String s2 = "SELECT SUM(invoice_parts.quantity) FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
	        			+ " WHERE invoices.date BETWEEN ? AND ? "
	        			+ " AND part_types.id = ?  GROUP BY part_types.id;";
	        	
	        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
				
	        	ps2.setDate(1, start_date);
	        	
	            ps2.setDate(2, end_date);
	             
	            ps2.setInt(3, kod_id);
	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	double v = 0.0;
	        	
	        	if(rs2.next()) {

	        		v = rs2.getDouble(1);
	 
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	          	
	        	return v;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return 0.0;

	}
	

	public static double getRashodPartsSumForDateKod(Date start_date, 
			Date end_date, int kod_id) {
		
		try {
			
				WSConnect.get();
        	
	        	final String s2 = "SELECT SUM(sale_parts.quantity) FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
	        			+ " WHERE  sale_invoices.date BETWEEN ? AND ? "
	        			+ " AND part_types.id = ?  GROUP BY part_types.id;";
	        	
	        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
				
	        	ps2.setDate(1, start_date);
	        	
	            ps2.setDate(2, end_date);
	             
	            ps2.setInt(3, kod_id);
	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	double v = 0.0;
	        	
	        	if(rs2.next()) {

	        		v = rs2.getDouble(1);
	 
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	          	
	        	return v;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return 0.0;

	}
	
	
	public static Vector<Integer> getPeoplePerDaysForPeriod(Date start_date, Date end_date) {
		
		try {
			
				WSConnect.get();
        	
	        	final String s2 = "SELECT SUM(people) FROM sale_invoices"
	        			+ " WHERE  sale_invoices.date BETWEEN ? AND ? "
	        			+ " GROUP BY sale_invoices.date;";
	        	
	        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
				
	        	ps2.setDate(1, start_date);
	        	
	            ps2.setDate(2, end_date);
	             
	            Vector<Integer> vec= new Vector<Integer>();
	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	double v = 0.0;
	        	
	        	while(rs2.next()) {

	        		v = rs2.getDouble(1);
	        		
	        		vec.add((int)v);
	 
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	          	
	        	return vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;

	}

	private static Vector<Integer>   getUsedKods(Vector<WsSkladMoveDataColumn> vec_rest, 
			Vector<WsSkladMoveDataColumn> vec_prihod  ) {
		
		 Vector<Integer> vec_kods = new  Vector<Integer>();
		 
		 HashSet<Integer> set = new  HashSet<Integer>();
		 
		 for(int i = 0; i < vec_rest.size(); ++i) {
			 
			 WsSkladMoveDataColumn d = vec_rest.elementAt(i);
			 
			 set.add(d.kod);
		 }
		 
		 
		 for(int i = 0; i < vec_prihod.size(); ++i) {
			 
			 WsSkladMoveDataColumn d = vec_prihod.elementAt(i);
			 
			 set.add(d.kod);
		 }
		 
		 for (Integer e : set) {
			 
			 vec_kods.add(e);
		 }
		 
		 Collections.sort(vec_kods);
		 
		 return vec_kods;
		
	}
	
	private static WsSkladMoveDataRow initKodVectorData(String name, Vector<Integer> vec_kods) {
		
		WSConnect.get();
		
		Connection conn = WSConnect.getCurrentConnection();
		
		return initKodVectorData(conn, name, vec_kods);
	
	}
	
	private static WsSkladMoveDataRow initKodVectorData(Connection conn, String name, Vector<Integer> vec_kods) {
		
		WsSkladMoveDataRow d = new WsSkladMoveDataRow();
		
		d.nakl_number = name;
		
		d.row_vec = new Vector<WsSkladMoveDataColumn>();
		
		for(int i = 0; i < vec_kods.size(); ++i) {
			
			WsSkladMoveDataColumn dc = new WsSkladMoveDataColumn();
			
			dc.kod = vec_kods.elementAt(i);
			
			WsPartType pt = WsUtilSqlStatements.getPartTypeForKod(conn, dc.kod);
			
			dc.name = pt.name;
			
			d.row_vec.add(dc);
		}
		
		return d;
		
	}
	
	private static void fillKodData( WsSkladMoveDataRow d, Vector<WsSkladMoveDataColumn> vec_data) {
		
		for(int i = 0; i < vec_data.size(); ++i) {
			
			WsSkladMoveDataColumn d_ins = vec_data.elementAt(i);
			
			for(int j = 0; j < d.row_vec.size(); ++j) {
				
				WsSkladMoveDataColumn d_f = d.row_vec.elementAt(j); 
				
				if( WsUtils.isKodEqual(d_f.kod , d_ins.kod)) {
					

					d_f.in_quantity = d_ins.in_quantity;
					
					d_f.out_quantity = d_ins.out_quantity;
					
					d_f.rest = d_ins.rest;
					
					d_f.units = d_ins.units;
					
					d_f.name = d_ins.name;
					
					//d_f.units = d_ins.units;
					
					break;
							
				}
			}
		}
	}
	
	public static Vector<WsSkladMoveDataRow> getPrihodRashodBookForDate(Date start_date, Date end_date, int id_contract) {
		
	       
			java.sql.Date start_date_1 = WsUtils.sqlDatePlusDays(start_date, -1) ;
			
			Vector<WsSkladMoveDataColumn> vec_rest = getRestPartsListForDate(start_date_1, id_contract);
			
			Vector<WsSkladMoveDataColumn> vec_prihod = getPrihodPartsListForPeriod(start_date, end_date, id_contract);
			
			Vector<Integer> vec_kods = getUsedKods(vec_rest, vec_prihod);
			
			Vector<WsSkladMoveDataRow> vec_res = new Vector<WsSkladMoveDataRow>();
			
			//initial rest row
			 WsSkladMoveDataRow row_initial_rest = initKodVectorData("Остаток", vec_kods);
			 
			 row_initial_rest.date_end = start_date_1;
			 
			 row_initial_rest.indexData = 0;
			 
			 fillKodData( row_initial_rest, vec_rest);
			 
			 vec_res.add(row_initial_rest);

			 Date startDate = null;
			 
			 Date endCurrentDate =  WsUtils.sqlDatePlusDays(start_date, -1) ;
			 
			 int status = 0;
			 
			 WsSkladMoveDataRow row_prihod_0 = initKodVectorData("Приход", vec_kods);
			 
			 WsSkladMoveDataRow row_rashod_0 = initKodVectorData("Расход", vec_kods);
			 
			 do {
				 
				 startDate = WsUtils.sqlDatePlusDays(endCurrentDate, 1) ;
				 
				 endCurrentDate =  WsUtils.sqlDatePlusDays(startDate, 6) ;
				 
				 status = endCurrentDate.compareTo(end_date);
				 
				 if(status > 0) { endCurrentDate = end_date; }
		
				 vec_prihod = getPrihodPartsListForPeriod(startDate, endCurrentDate, id_contract);
				 
				 WsSkladMoveDataRow row_prihod = new WsSkladMoveDataRow(row_prihod_0);
				 
				 row_prihod .indexData = 1;
				 
				 row_prihod.date_start = startDate;
				 
				 row_prihod.date_end = endCurrentDate;
						 
				 fillKodData( row_prihod, vec_prihod);
				 
				 vec_res.add(row_prihod);
				 //rashod
				 Vector<WsSkladMoveDataColumn> vec_rashod = getRashodPartsListForDate(startDate, endCurrentDate, id_contract);
				 
				 WsSkladMoveDataRow row_rashod = new  WsSkladMoveDataRow(row_rashod_0);
				 
				 row_rashod.indexData = 2;
				 
				 row_rashod.date_start = startDate;
				 
				 row_rashod.date_end = endCurrentDate;
				 
				 fillKodData( row_rashod, vec_rashod);
				 
				 vec_res.add(row_rashod);
				 
				 status = endCurrentDate.compareTo(end_date);
				 
			 }while(status < 0);
			
			 //calculates rest
			 for(int j = 1; j < vec_res.size(); j +=2) {
				 
				 Vector<WsSkladMoveDataColumn>  rest_row = vec_res.elementAt(j - 1).row_vec;
				 
				 Vector<WsSkladMoveDataColumn>  prihod_row = vec_res.elementAt(j).row_vec;
				 
				 Vector<WsSkladMoveDataColumn>  rashod_row = vec_res.elementAt(j + 1).row_vec;
				 
				 for(int i = 0; i < rest_row.size(); ++i) {
					 
					 double rest = rest_row.elementAt(i).rest;
					 
					 double prihod = prihod_row.elementAt(i).in_quantity;
					 
					 rest += prihod;
					 
					 prihod_row.elementAt(i).rest = rest;
					 
					 double rashod = rashod_row.elementAt(i).out_quantity;
					 
					 rashod_row.elementAt(i).rest = rest - rashod;
					 
				 }
			 }
			
			 return vec_res;  	
	}


	public static Vector<WsSkladMoveDataColumn> getPrihodRashodBookForDate2(Date start_date, Date end_date, int id_contract) {
		
		WSConnect.get();
		
		Connection conn =  WSConnect.getCurrentConnection();
		
		return  getPrihodRashodBookForDate2(conn, start_date, end_date, id_contract);
	
	}




	public static Vector<WsSkladMoveDataColumn> getPrihodRashodBookForDate2(Connection conn, Date start_date, Date end_date,
			int id_contract) {
		
	       
			java.sql.Date start_date_1 = WsUtils.sqlDatePlusDays(start_date, -1) ;
			
			Vector<WsSkladMoveDataColumn> vec_rest = getRestPartsListForDate(conn, start_date_1, id_contract);
			
			Vector<WsSkladMoveDataColumn> vec_prihod = getPrihodPartsListForPeriod(conn, start_date, end_date, id_contract);
			
			Vector<WsSkladMoveDataColumn> vec_rashod = getRashodPartsListForDate(conn, start_date, end_date, id_contract);
			 
			Vector<WsPartType>  vec_kods = WsUtilSqlStatements.getPartTypesList();
			
			Vector<WsSkladMoveDataColumn> vec_res = new Vector<WsSkladMoveDataColumn>();
			
			for(int i = 0; i < vec_kods.size(); ++i) {
				
				WsPartType kd = vec_kods.elementAt(i);
				
				WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
				
				d.name = kd.name;
				
				d.kod =  kd.kod;
				
				//initial rest
				for(int j = 0; j < vec_rest.size(); ++j) {
					
					WsSkladMoveDataColumn dc = vec_rest.elementAt(j);
					
					if( WsUtils.isKodEqual(dc.kod,d.kod)) {
						
						d.initial_rest = dc.rest;
						
						d.q_array[0].initial_rest = dc.q_array[0].rest;
						
						break;
					}
				}
				
				//prihod
				for(int j = 0; j < vec_prihod.size(); ++j) {
					
					WsSkladMoveDataColumn dc = vec_prihod.elementAt(j);
					
					if(	 WsUtils.isKodEqual(dc.kod, d.kod)) {
						
						d.in_quantity = dc.in_quantity;
						
						d.q_array[0].in_quantity  = dc.q_array[0].in_quantity ;
						
						break;
					}
				}
				
				//rashod
				for(int j = 0; j < vec_rashod.size(); ++j) {
					
					WsSkladMoveDataColumn dc = vec_rashod.elementAt(j);
					
					if( WsUtils.isKodEqual(dc.kod , d.kod)) {	
						
						d.out_quantity = dc.out_quantity;
						
						d.q_array[0].out_quantity  = dc.q_array[0].out_quantity ;
						
						break;
					}
				}
				
				d.rest = d.initial_rest + d.in_quantity - d.out_quantity;
				
				d.q_array[0].rest = d.q_array[0].initial_rest + d.q_array[0].in_quantity - d.q_array[0].out_quantity;
				
				vec_res.add(d);
				
			}
	
			return vec_res;
	        	
	}
	
	
	public static  Vector<WsStaffData> getPeopleVidatok(Date start_date, Date end_date) {
		
		WSConnect.get();
		
		Connection conn =  WSConnect.getCurrentConnection();
		
		return   getPeopleVidatok(conn, start_date, end_date);
	
	}
	
	public static Vector<WsStaffData> getPeopleVidatok(Connection conn, Date start_date, Date end_date) {
		
		  Vector<WsStaffData> vec= new Vector<WsStaffData>();
	       
		try {
			
			WSConnect.get();
    	
        	final String s2 = "SELECT sale_invoices.id_counterparty, counterparties.name, SUM(people) FROM sale_invoices"
        			+" INNER JOIN counterparties ON sale_invoices.id_counterparty =  counterparties.id "
        			+ " WHERE  sale_invoices.date BETWEEN ? AND ? "
        			+ " GROUP BY sale_invoices.id_counterparty;";
        	
        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
			
        	ps2.setDate(1, start_date);
        	
            ps2.setDate(2, end_date);
             
        	ResultSet rs2 =   ps2.executeQuery();
        	
        	//double v = 0.0;
        	
        	while(rs2.next()) {
        		
        		WsStaffData d = new WsStaffData();

        		d.id_counterparty = rs2.getInt(1);
        		
        		d.agentName = rs2.getString(2);
        			
        		d.quantity = rs2.getDouble(3);
        		
        		vec.add(d);
 
        	}
        	
        	rs2.close();
        	
        	ps2.close();
          	
        	return vec;
        
        
	} catch (SQLException e) {
		
		if( WsUtils.isDebug() ) {
			
			e.printStackTrace();
		}
	}

	return vec;
        	
}
	
	public static Vector<WsSkladMoveDataRow> getSkladBookNaklForDate(Date start_date, Date end_date, int id_contract) {
		
			java.sql.Date start_date_1 = WsUtils.sqlDatePlusDays(start_date, -1) ;
			
			Vector<WsSkladMoveDataColumn> vec_rest = getRestPartsListForDate(start_date_1, id_contract);
			
			Vector<WsSkladMoveDataColumn> vec_prihod = getPrihodPartsListForPeriod(start_date, end_date, id_contract);
			
			Vector<Integer> vec_kods = getUsedKods(vec_rest, vec_prihod);
			
			Vector<WsSkladMoveDataRow> vec_res = new Vector<WsSkladMoveDataRow>();
			
			//initial rest row
			 WsSkladMoveDataRow row_initial_rest = initKodVectorData("Остаток", vec_kods);
			 
			 row_initial_rest.date_end = start_date_1;
			 
			 row_initial_rest.indexData = 0;
			 
			 fillKodData( row_initial_rest, vec_rest);
			 
			 vec_res.add(row_initial_rest);
			////////
			 //rows prihod/rashod
			 Date startDate = null;
			 
			 Date endCurrentDate =  WsUtils.sqlDatePlusDays(start_date, -1) ;
			 
			 int status = 0;
			 
			 do {
				 
				 startDate = WsUtils.sqlDatePlusDays(endCurrentDate, 1) ;
				 
				 endCurrentDate =  WsUtils.sqlDatePlusDays(startDate, 6) ;
				 
				 status = endCurrentDate.compareTo(end_date);
				 
				 if(status > 0) { endCurrentDate = end_date; }
				 //prihof
				 vec_prihod = getPrihodPartsListForPeriod(startDate, endCurrentDate, id_contract);
				 
				 WsSkladMoveDataRow row_prihod = initKodVectorData("Приход", vec_kods);
				 
				 row_prihod .indexData = 1;
				 
				 row_prihod.date_start = startDate;
				 
				 row_prihod.date_end = endCurrentDate;
						 
				 fillKodData( row_prihod, vec_prihod);
				 
				 vec_res.add(row_prihod);
				 //rashod
				 Vector<WsSkladMoveDataColumn> vec_rashod = getRashodPartsListForDate(startDate, endCurrentDate, id_contract);
				 
				 WsSkladMoveDataRow row_rashod = initKodVectorData("Расход", vec_kods);
				 
				 row_rashod.indexData = 2;
				 
				 row_rashod.date_start = startDate;
				 
				 row_rashod.date_end = endCurrentDate;
				 
				 fillKodData( row_rashod, vec_rashod);
				 
				 vec_res.add(row_rashod);
				 
				 status = endCurrentDate.compareTo(end_date); 
				 
			 }while(status < 0);
			
			 //calculates rest
			 for(int j = 1; j < vec_res.size(); j +=2) {
				 
				 Vector<WsSkladMoveDataColumn>  rest_row = vec_res.elementAt(j - 1).row_vec;
				 
				 Vector<WsSkladMoveDataColumn>  prihod_row = vec_res.elementAt(j).row_vec;
				 
				 Vector<WsSkladMoveDataColumn>  rashod_row = vec_res.elementAt(j + 1).row_vec;
				 
				 for(int i = 0; i < rest_row.size(); ++i) {
					 
					 double rest = rest_row.elementAt(i).rest;
					 
					 double prihod = prihod_row.elementAt(i).in_quantity;
					 
					 rest += prihod;
					 
					 prihod_row.elementAt(i).rest = rest;
					 
					 double rashod = rashod_row.elementAt(i).out_quantity;
					 
					 rashod_row.elementAt(i).rest = rest - rashod;
					 
				 }
			 }
			
			 return vec_res;
	        	
	}
	
	
	public static Vector<WsSkladMoveDataRow> getPrihodRashodBookNaklForDate(Date start_date, Date end_date, 
			boolean alignDates, int id_contract){
		
	   WSConnect.get();
   	
       Connection conn = WSConnect.getCurrentConnection();
       
       return getPrihodRashodBookNaklForDate(conn, start_date, end_date, alignDates, id_contract);
		
		
	}
	
	public static Vector<WsSkladMoveDataRow> getPrihodRashodBookNaklForDate(Connection conn, Date start_date, Date end_date,
			 boolean alignDates, int id_contract) {
		
	       
		java.sql.Date start_date_1 = WsUtils.sqlDatePlusDays(start_date, -1) ;
		
		Vector<WsSkladMoveDataColumn> vec_rest = getRestPartsListForDate(conn, start_date_1, id_contract);
		
		Vector<WsSkladMoveDataColumn> vec_prihod_check = getPrihodPartsListForPeriod(conn, start_date, end_date, id_contract);
		
		Vector<Integer> vec_kods = getUsedKods(vec_rest, vec_prihod_check);
		
		vec_prihod_check.clear();
		
		vec_prihod_check = null;
		
		Vector<WsSkladMoveDataRow> vec_prihod = WsReportsSqlStatements.getSkladPrihodMovement(conn, start_date, end_date);
		
		Vector<WsSkladMoveDataRow> vec_rashod = WsReportsSqlStatements.getRashodMovement(conn, start_date, end_date);
		
		Vector<WsSkladMoveDataRow> vec_res = new Vector<WsSkladMoveDataRow>();
		
		//initial rest row
		WsSkladMoveDataRow row_initial_rest = initKodVectorData(conn, "Остаток", vec_kods);
		 
		row_initial_rest.date_end = start_date_1;
		 
		row_initial_rest.indexData = 0;
		
		row_initial_rest.date = start_date_1;
		 
		fillKodData( row_initial_rest, vec_rest);
		 
		vec_res.add(row_initial_rest);
		
		WsSkladMoveDataRow prihod_0 = initKodVectorData(conn, "Приход", vec_kods);
		
		Date moving_date = new Date(start_date_1.getTime());
		
		for(int i = 0; i < vec_prihod.size(); ++i) {
			
			WsSkladMoveDataRow sr = vec_prihod.elementAt(i);
			
			WsSkladMoveDataRow prihod = new WsSkladMoveDataRow(prihod_0);
			
			fillKodData( prihod, sr.row_vec);
			 
			prihod.indexData = 1;
			 
			prihod.agent_name = sr.agent_name;
			
			prihod.agent_id = sr.agent_id;
			 
			prihod.date_start = sr.date_start;
			 
			prihod.date_end = sr.date_end;
			
			prihod.date_doc = sr.date_doc;
			
			prihod.date = sr.date;
			 
			prihod.nakl_number = sr.nakl_number;
			 
			 vec_res.add(prihod);
			
		}
		
		WsSkladMoveDataRow rashod_0 = initKodVectorData(conn,"Расход", vec_kods);
		
		for(int i = 0; i < vec_rashod.size(); ++i) {
			
			 WsSkladMoveDataRow sr = vec_rashod.elementAt(i);
			 
			 WsSkladMoveDataRow rashod = new WsSkladMoveDataRow(rashod_0);
			 
			 fillKodData( rashod, sr.row_vec);
			 
			 rashod.indexData = 2;
			 
			 rashod.agent_name = sr.agent_name;
			 
			 rashod.agent_id = sr.agent_id;
			 
			 rashod.date_start = sr.date_start;
			 
			 rashod.date_end = sr.date_end;
			 
			 rashod.date = sr.date;
			 
			 rashod.date_doc = sr.date;
			 
			 rashod.people = sr.people;

			 rashod.nakl_number = sr.nakl_number;
			 
			 vec_res.add(rashod);
			 
		}
	
			
		Collections.sort(vec_res, new WsDateComparator());
			
	    vec_prihod.clear();
		
		vec_rashod.clear();
		
		vec_prihod = null;

		vec_rashod = null;
		
		 //calculates rest
		 for(int j = 1; j < vec_res.size(); ++j) {
			 
			 Vector<WsSkladMoveDataColumn>  prev_row = vec_res.elementAt(j - 1).row_vec;
			
			 WsSkladMoveDataRow row_d = vec_res.elementAt(j);
			 
			 Vector<WsSkladMoveDataColumn>  current_row = row_d.row_vec;
			 
			 if(alignDates) {
			 //the date of record aligning
				if(row_d.date_doc.compareTo(row_d.date) > 0) { row_d.date  = new Date( row_d.date_doc.getTime()); }
				
				if(moving_date.compareTo(row_d.date) > 0) { 
					
					row_d.date  = new Date( moving_date.getTime());
				}
				else {
					
					moving_date =  new Date( row_d.date.getTime());
				}
			 }
			 
			 if(row_d.indexData == 1) {
				 
				 for(int i = 0; i < current_row.size(); ++i) {
					 
					 double rest = prev_row.elementAt(i).rest;
					 
					 WsSkladMoveDataColumn  dc = current_row.elementAt(i);
					 
					 dc.rest = rest + dc.in_quantity;
				 
				 }
				 
			 }
			 if(row_d.indexData == 2) {
				 
				 for(int i = 0; i < current_row.size(); ++i) {
					 
					 double rest = prev_row.elementAt(i).rest;
					 
					 WsSkladMoveDataColumn  dc = current_row.elementAt(i);
					 
					 dc.rest = rest - dc.out_quantity;
				 
				 }
				 
			 }
		 }
		
		 return vec_res;
        	
	}
	
	

	public static Vector<WsWaterDataRow> getRashodWaterForDate(Date start_date, Date end_date, 
			int id_kod, WsWaterPeriodsData periods) {
		
			WsPartType pd =  WsUtilSqlStatements.getPartTypeForId(id_kod); 
			
			int kod  = pd.kod;
			
			Vector<WsWaterDataRow> vec_res = new Vector<WsWaterDataRow>();
			
			WsWaterDataRow row_initial_rest = new WsWaterDataRow();
			
			java.sql.Date start_date_1 = WsUtils.sqlDatePlusDays(start_date, -1) ;	

			row_initial_rest.out_rest = getRestPartsSumForDateAndKod(start_date_1, id_kod);
			 
			row_initial_rest.date_end = start_date_1;
			 
			row_initial_rest.kod = kod;
			 
			 vec_res.add(row_initial_rest);
		
			 Date startDate = null;
			 
			 Date endCurrentDate =  WsUtils.sqlDatePlusDays(start_date, -1) ;
			 
			 int status = 0;
			 
			 do {
				 
				 startDate = WsUtils.sqlDatePlusDays(endCurrentDate, 1) ;
				 
				 endCurrentDate =  WsUtils.sqlDatePlusDays(startDate, 6) ;
				 
				 status = endCurrentDate.compareTo(end_date);
				 
				 if(status > 0) { endCurrentDate = end_date; }

				 WsWaterDataRow row = new WsWaterDataRow();
				 
				 row.part_name = pd.name;
				 
				 row.date_start = startDate;
				 
				 row.date_end = endCurrentDate;
						 
				 row.kod = kod;
				 
				 row.out_quantity = getRashodPartsSumForDateKod(startDate, endCurrentDate, id_kod);
				 
			     row.in_quantity = getPrihodPartsSumForDateKod(startDate, endCurrentDate, id_kod);

				 Vector<Integer> vec_people = getRashodPeopleForAgent(startDate, endCurrentDate);
				 
				 int avg_people = 0;
				 
				 for(Integer p: vec_people) {
					 
					 avg_people += p; 
				 }
				 
				 row.people = (int) (avg_people/vec_people.size());
				 
				 row.consumed =  consumedWaterForPeriod(startDate,  vec_people, periods);

				 vec_res.add(row);

				 status = endCurrentDate.compareTo(end_date);

			 }while(status < 0);
			 
			 for(int i = 1; i < vec_res.size(); ++i) {
				 
				 WsWaterDataRow prev = vec_res.elementAt(i - 1);
				 
				 WsWaterDataRow current = vec_res.elementAt(i);
				 
				 current.in_rest = current.in_quantity + prev.out_rest;
				 
				 current.out_rest = current.in_rest - current.out_quantity;
				 
				 
			 }
			 
			 return vec_res;
	        	
	}
	
	
	private static double consumedWaterForPeriod(java.sql.Date startD, Vector<Integer> vec_people, WsWaterPeriodsData 
			periods) {
		
		double sum = 0.0;
		
		Date current = startD;
		
		for(int i = 0; i < vec_people.size(); ++i) {
			
			double coeff = 1.5;
			
			int year = WsUtils.getYearMonthDay(current)[0];
			
			int year_in_periods = WsUtils.getYearMonthDay(periods.dateStart[0])[0];
			
			if(year_in_periods != year) {
				
				Calendar c = Calendar.getInstance();
				
				c.setTime(current);
				
				c.set(year_in_periods, c.get(Calendar.MONTH), c.get(Calendar.DATE));
				
				java.util.Date current_d = c.getTime();
						
				for(int j = 0; j < 3; ++j) {
					
					if( (current_d.compareTo( periods.dateStart[j]) >= 0)  && (current_d.compareTo(periods.dateEnd[j]) <= 0) ) {
						
						coeff = periods.value[j];
	
					}
				}
				
			}
			else {
	
				for(int j = 0; j < 3; ++j) {
					
					if( (current.compareTo( periods.dateStart[j]) >= 0)  && (current.compareTo(periods.dateEnd[j]) <= 0) ) {
						
						coeff = periods.value[j];
	
					}
				}
			}


			sum += vec_people.elementAt(i)*coeff;
			
			current =  WsUtils.sqlDatePlusDays(current, 1);
		}
			
		return sum;
	}


	
	public static Vector<Integer> getRashodPeopleForAgent(Date start_date, 
			Date end_date) {
		
		Vector<Integer> vec= new Vector<Integer>();
		
		try {
			
				WSConnect.get();
        	
	        	final String s2 = "SELECT SUM(people) FROM sale_invoices"
	        			+ " WHERE  sale_invoices.date BETWEEN ? AND ?  "
	        			+ " GROUP BY sale_invoices.date;";
	        	
	        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
				
	        	ps2.setDate(1, start_date);
	        	
	            ps2.setDate(2, end_date);
	            
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		vec.add( rs2.getInt(1));
	        		
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	        	
	        	int days = WsUtils.getDaysBetweenDates(start_date, end_date) + 1;
	        	
	        	//vector must be filled by zeros for every day in the range without data
	        	int diff = days - vec.size();
	        	
	        	if(diff > 0) {
	        		
	        		for(int i = 0; i < diff; ++i ) {
	        			
	        			vec.add(0);
	        			
	        		}

	        	}
	          	
	        	return vec;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}

		return null;
	
	}
	
	
	
	public static double getRestForPartForDate(int kod, Date end_date) {
		
	       
		WsPartType dType = WsUtilSqlStatements.getPartTypeForKod(kod);
		
		try {
			
				WSConnect.get();
        	
				Vector<WsSkladMoveDataColumn> vec = new Vector<WsSkladMoveDataColumn>();
			
	         	final String s1 = "SELECT  SUM(quantity), "
	        			+ " part_types.kod, part_types.name  FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " WHERE invoices.date <= ? AND part_types.id == ? GROUP BY part_types.kod;";
	         	
	        	PreparedStatement ps1 = WSConnect.getCurrentConnection().prepareStatement(s1);
				
	            ps1.setDate(1, end_date);
	            
	            ps1.setInt(2, dType.id);
	      	     
	        	final String s2 = "SELECT SUM(sale_parts.quantity), part_types.kod, part_types.name FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id = sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " WHERE  sale_invoices.date <= ? AND part_types.id == ? GROUP BY part_types.kod;";
	        	
	        	PreparedStatement ps2 = WSConnect.getCurrentConnection().prepareStatement(s2);
				
	            ps2.setDate(1, end_date);
	        	
	            ps2.setInt(2, dType.id);
	        	
	        	ResultSet rs1 =   ps1.executeQuery();
	        	
	        	while(rs1.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs1.getInt(2);
	        		
	        		d.in_quantity = rs1.getDouble(1);
	        		
	        		d.q_array[0].in_quantity = rs1.getDouble(1);
	        		
	        		d.name = rs1.getString(3);
	    
	        		vec.add(d);
	        	}
	        	
	        	rs1.close();
	        	
	        	ps1.close();
	        	
	        	Vector<WsSkladMoveDataColumn> vec2 = new Vector<WsSkladMoveDataColumn>();
	        	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs2.getInt(2);
	        		
	        		d.out_quantity = rs2.getDouble(1);
	        		
	        		d.q_array[0].out_quantity = rs2.getDouble(1);
	        		
	        		d.name = rs2.getString(3);
	    
	        		
	        		vec2.add(d);
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	        	
	        	for(int i =0; i < vec.size(); ++i) {
	        		
	        		WsSkladMoveDataColumn d = vec.elementAt(i);
	        		
	        		boolean foundInrashod = false;
	        		for(int j = 0; j < vec2.size(); ++j) {
	        			
	        			WsSkladMoveDataColumn d_ = vec2.elementAt(j);
	        			
	        			if( WsUtils.isKodEqual(d_.kod , d.kod)) {
	        				
	        				
	        				d.out_quantity = d_.out_quantity;
	        				
	        				d.rest = d.in_quantity - d.out_quantity;
	        				
	        				d.out_quantity = 0.0;
	        				
	        				d.in_quantity = 0.0;
	        				
	        				///////////////////////////////
	        				
	        				d.q_array[0].out_quantity = d_.q_array[0].out_quantity;
	        				
	        				d.q_array[0].rest = d.q_array[0].in_quantity - d.q_array[0].out_quantity;
	        				
	        				d.q_array[0].out_quantity = 0.0;
	        				
	        				d.q_array[0].in_quantity = 0.0;
	        				
	        				foundInrashod = true;
	        				
	        				break;
	        			}
	        			
	        		}
	        		
	        		if(!foundInrashod) {
	        			
	        			d.rest = d.in_quantity;
	        			
	        			d.out_quantity = 0.0;
        				
        				d.in_quantity = 0.0;
        				
        				d.q_array[0].rest = d.q_array[0].in_quantity;
	        			
	        			d.q_array[0].out_quantity = 0.0;
        				
        				d.q_array[0].in_quantity = 0.0;
	        			
	        		}
	        		
	        	}
	        	
	        	if(vec.isEmpty()) { return 0;}
	        	
	        	return vec.elementAt(0).rest;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}

		return -1.0;

	}
	

	public static HashMap<Integer, WsMoveKodPage> transposePrihodRashodBookNaklForDate(
			Vector<WsSkladMoveDataRow> vec, Vector<WsAgentData> agents_vec) {
		
		WsCatalogKods catalog = new WsCatalogKods();
		
		HashMap<Integer, WsMoveKodPage>  res_map = catalog.getKodsTable4();
	
		
		HashMap<Integer, Integer> agents_ind__map = new HashMap<Integer, Integer>();
		
		WsSkladMoveDataRow row_mock = createNewRowWithAgentsColumns(agents_vec,
				 agents_ind__map);
			
		for(int j = 0; j < vec.size(); ++j) {
				
				WsSkladMoveDataRow row = vec.elementAt(j);
				
				Vector<WsSkladMoveDataColumn> col_vec = row.row_vec;
				
				int agent_index = agents_ind__map.get(row.agent_id);
				
				for(int k = 0; k < col_vec.size(); ++k) {
					
					WsSkladMoveDataColumn col = col_vec.elementAt(k);
					
					int indexData = -1;
					
					if(col.in_quantity != 0.0) {
						
						indexData = 1;
					} 
					if(col.out_quantity != 0.0) {
						
						indexData = 2;
					}
					if(col.rest != 0.0) {
						
						indexData = 0;
					}
					if(indexData > -1) {
						
						 WsMoveKodPage page =   res_map.get(col.kod);
						
						 WsSkladMoveDataRow new_row = new WsSkladMoveDataRow(row_mock);
						 
						 new_row.indexData = indexData;
						 
						 new_row.date = row.date;
						 
						 new_row.date_doc = row.date;
						 
						 new_row.nakl_number = row.nakl_number;
						 
						 new_row.agent_name = row.agent_name;
						 
						 new_row.agent_name2 = row.agent_name2;
						 
						 new_row.sklad_row = row.sklad_row;
						 
						 new_row.external_prihod = row.external_prihod;
						 
						 WsSkladMoveDataColumn cl = new_row.row_vec.elementAt(agent_index);
						
						 cl.rest = col.rest ;
						 
						 cl.in_quantity = col.in_quantity;
						 
						 cl.out_quantity = col.out_quantity;
						 
						 page.rows_vec.add(new_row);

					}

				}	
		}

		return res_map;

	}

	private static WsSkladMoveDataRow createNewRowWithAgentsColumns(Vector<WsAgentData> agents_vec,
			HashMap<Integer, Integer> ind_map) {
		
		WsSkladMoveDataRow row_new = new  WsSkladMoveDataRow();
		
		row_new.row_vec = new Vector<WsSkladMoveDataColumn>();
		
		for(int k = 0; k < agents_vec.size(); ++k) {
		
			WsAgentData ag_d =  agents_vec.elementAt(k);
			
			WsSkladMoveDataColumn ag_col = new WsSkladMoveDataColumn();
			
			ag_col.name = ag_d.name;
			
			ag_col.id = ag_d.id;
			
			row_new.row_vec.add(ag_col);
			
			ind_map.put(ag_d.id, k);
		}
		
		return row_new;

	}
	
	
	public static HashMap<Integer, WsMoveKodPage> getDodatok46Movement(Date start_date, Date end_date,
			Vector<WsAgentData> agents_vec) {
		
		WSConnect.get();

		Connection conn = 	WSConnect.getCurrentConnection();
		
		WsAgentData skl_agent = agents_vec.elementAt(0);
		
		Vector<WsSkladMoveDataRow> this_sklad = getPrihodRashodBookNakl46ForDate(conn, start_date, end_date, skl_agent,true);
				
		for(int i = 1; i < agents_vec.size(); ++i) {
			
			Vector<WsSkladMoveDataRow> sklad = getSkladBookFromForeignBase(start_date, end_date, agents_vec.elementAt(i));
			
			this_sklad.addAll(sklad);
			
		}
		
		Collections.sort(this_sklad, new WsDateComparator());
		
		HashMap<Integer, WsMoveKodPage> res_vec  = transposePrihodRashodBookNaklForDate(this_sklad, agents_vec);
		
		return res_vec;
		
	}
	
	
	private static  Vector<WsSkladMoveDataRow> getSkladBookFromForeignBase(Date start_date, Date end_date, 
			WsAgentData dt_agent) {
		
		Vector<WsSkladMoveDataRow> res_vec = new  Vector<WsSkladMoveDataRow>();
		
		try {
			
			Connection conn = WSConnect.connectImport(dt_agent.contact);
			
			if(null == conn) { return res_vec; }

			res_vec = getPrihodRashodBookNakl46ForDate(conn, start_date, end_date, dt_agent, false);
	
			conn.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			
		}
		
		return res_vec;
		
	}
	
	public static Vector<WsSkladMoveDataRow> getPrihodRashodBookNakl46ForDate(Connection conn, 
			Date start_date, Date end_date, WsAgentData agent, boolean ignoreRashod) {
		
	       
		java.sql.Date start_date_1 = WsUtils.sqlDatePlusDays(start_date, -1) ;
		
		Vector<WsSkladMoveDataRow> vec_rest = getRestPartsListForDate46(conn, start_date_1, agent);
		
		
		Vector<WsSkladMoveDataRow> vec_prihod = getSkladPrihodMovement46(conn, start_date, end_date,
				agent);
		
		Vector<WsSkladMoveDataRow> vec_rashod = new Vector<WsSkladMoveDataRow>();
		
		if(!ignoreRashod) {
			
			vec_rashod = getRashodMovement46(conn, start_date, end_date, agent);
			
			
		}
		
		vec_rest.addAll(vec_prihod);
		
		vec_rest.addAll(vec_rashod);
		
		return vec_rest;
		
	}
	
	
	//for dodatok 46
	public static Vector<WsSkladMoveDataRow> getSkladPrihodMovement46(Connection conn, Date start_d,
			Date end_d, WsAgentData agent) {
		
		if (start_d == null ||   end_d == null) return null;
	       
		try {
			
			Vector<WsPrihodData> nakl_vector = WsPrihodSqlStatements.getPrihodList(conn, start_d, end_d);
			
			Vector<WsSkladMoveDataRow> vec = new Vector<WsSkladMoveDataRow>();
			
			for(int i = 0; i < nakl_vector.size(); ++i) {
				
				WsPrihodData dn = nakl_vector.elementAt(i);
								
				//sorted by code
				Vector<WsPrihodPartData> vec_parts = WsPrihodSqlStatements.getPrihodPartsList(conn, dn.id, 0);
				
				for(int j = 0; j < vec_parts.size(); ++j) {
					
					WsSkladMoveDataRow row = new WsSkladMoveDataRow();
					
					row.prihod = true;
					
					row.date = dn.date;
					
					row.date_doc = dn.date_doc;
					
					row.nakl_number = dn.number;
					
					row.agent_name = dn.agentName; 
					
					row.agent_name2 = dn.agentName; 
					
					row.agent_id = dn.id_counterparty;
					
					row.indexData = 1; //prihod
					
					row.agent_id = agent.id;
					
					row.agent_name = agent.name;
					
					row.sklad_row = agent.id == 1111111;
					
					row.external_prihod = dn.id_external == -1;
					
					row.row_vec = new Vector<WsSkladMoveDataColumn>();
					
					WsPrihodPartData d_part =  vec_parts.elementAt(j); 
					
					WsSkladMoveDataColumn d_part_r = new WsSkladMoveDataColumn();
					
					d_part_r.in_quantity = d_part.quantity;
					
					d_part_r.kod = d_part.kod;
					
					d_part_r.name = d_part.name;
					
					d_part_r.units = d_part.units_name;
					
					row.row_vec.add(d_part_r);
					
					vec.add(row);
					
				}
			}
			
			return vec;
   
		} catch (Exception e) {

			return null;
		}
	
	}
	
	
public static Vector<WsSkladMoveDataRow> getRashodMovement46(Connection conn, Date start_d, 
		Date end_d, WsAgentData agent) {
		
		if (start_d == null ||   end_d == null) return null;
	       
		try {
			
			Vector<WsRashodData> nakl_vector = WsRashodSqlStatements.getRashodList(conn, start_d, end_d);
			
			Vector<WsSkladMoveDataRow> vec = new Vector<WsSkladMoveDataRow>();
			
			for(int i = 0; i < nakl_vector.size(); ++i) {
				
				WsRashodData dn = nakl_vector.elementAt(i);
				
				//sorted by code
				Vector<WsRashodPartData> vec_parts = WsRashodSqlStatements.getRashodPartsListKodGroup(conn, dn.id, 0);
				
				for(int j = 0; j < vec_parts.size(); ++j) {
					
					WsSkladMoveDataRow row = new WsSkladMoveDataRow();
					
					row.prihod = false;
					
					row.date = dn.date;
					
					row.date_doc = dn.date;
					
					row.nakl_number = dn.number;
					
					row.agent_name = dn.agentName;
					
					row.agent_name2 = dn.agentName;
					
					row.agent_id = dn.id_counterparty;
					
					row.people = dn.people;
					
					row.indexData = 2;//rashod
					
					row.agent_id = agent.id;
					
					row.agent_name = agent.name;
					
					row.sklad_row = agent.id == 1111111;
					
					row.row_vec = new Vector<WsSkladMoveDataColumn>();
					
					WsRashodPartData d_part =  vec_parts.elementAt(j); 
					
					WsSkladMoveDataColumn d_part_r = new WsSkladMoveDataColumn();
					
					d_part_r.out_quantity = d_part.quantity;
					
					d_part_r.kod = d_part.kod;
					
					d_part_r.name = d_part.name;
					
					d_part_r.units = d_part.units_name;
					
					row.row_vec.add(d_part_r);
					
					vec.add(row);
					
				}
				
				
			}
			
			return vec;

		} catch (Exception e) {

			return null;
		}
	}

	public static Vector<WsSkladMoveDataRow> getRestPartsListForDate46(Connection conn, 
			Date end_date, WsAgentData agent) {
		
		try {
			
				Vector<WsSkladMoveDataRow> vec_rows = new Vector<WsSkladMoveDataRow>();
			
				Vector<WsSkladMoveDataColumn> vec = new Vector<WsSkladMoveDataColumn>();
	        	
	        	final String s1 = "SELECT  SUM(quantity), "
	        			+ " part_types.kod, part_types.name, units.name  FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE invoices.date <= ? GROUP BY part_types.kod ORDER BY part_types.kod;";

	        	PreparedStatement ps1 = conn.prepareStatement(s1);
				
	            ps1.setDate(1, end_date);
	      	     
	         	
	        	final String s2 = "SELECT SUM(sale_parts.quantity), part_types.kod, part_types.name, units.name FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id = sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE  sale_invoices.date <= ? GROUP BY part_types.kod ORDER BY part_types.kod;";
	        	
	        	PreparedStatement ps2 = conn.prepareStatement(s2);
				
	            ps2.setDate(1, end_date);
	        	
	        	
	        	ResultSet rs1 =   ps1.executeQuery();
	        	
	        	while(rs1.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs1.getInt(2);
	        		
	        		d.in_quantity = rs1.getDouble(1);
	        		
	        		d.q_array[0].in_quantity = rs1.getDouble(1);
	        		
	        		d.name = rs1.getString(3);
	    
	        		d.units = rs1.getString(4);
	        		
	        		vec.add(d);
	        	}
	        	
	        	rs1.close();
	        	
	        	ps1.close();
	        	
	        	Vector<WsSkladMoveDataColumn> vec2 = new Vector<WsSkladMoveDataColumn>();
	        	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		WsSkladMoveDataColumn d = new WsSkladMoveDataColumn();
	        		
	        		d.kod = rs2.getInt(2);
	        		
	        		d.out_quantity = rs2.getDouble(1);
	        		
	        		d.q_array[0].out_quantity = rs2.getDouble(1);
	        		
	        		d.name = rs2.getString(3);
	        		
	        		d.units = rs2.getString(4);
	    
	        		vec2.add(d);
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	        	
	        	for(int i = 0; i < vec.size(); ++i) {
	        		
	        		WsSkladMoveDataColumn d = vec.elementAt(i);
	        		
	        		boolean foundInrashod = false;
	        		for(int j = 0; j < vec2.size(); ++j) {
	        			
	        			WsSkladMoveDataColumn d_ = vec2.elementAt(j);
	        			
	        			if( WsUtils.isKodEqual(d_.kod , d.kod)) {
	        				
	        				
	        				d.out_quantity = d_.out_quantity;
	        				
	        				d.rest = d.in_quantity - d.out_quantity;
	        				
	        				d.out_quantity = 0.0;
	        				
	        				d.in_quantity = 0.0;
	        				
	        				///////////////////////////////
	        				
	        				d.q_array[0].out_quantity = d_.q_array[0].out_quantity;
	        				
	        				d.q_array[0].rest = d.q_array[0].in_quantity - d.q_array[0].out_quantity;
	        				
	        				d.q_array[0].out_quantity = 0.0;
	        				
	        				d.q_array[0].in_quantity = 0.0;
	        				
	        				foundInrashod = true;
	        				
	        				break;
	        			}
	        			
	        		}
	        		
	        		if(!foundInrashod) {
	        			
	        			d.rest = d.in_quantity;
	        			
	        			d.out_quantity = 0.0;
	    				
	    				d.in_quantity = 0.0;
	    				
	    				d.q_array[0].rest = d.q_array[0].in_quantity;
	        			
	        			d.q_array[0].out_quantity = 0.0;
	    				
	    				d.q_array[0].in_quantity = 0.0;
	        			
	        		}
	        		
	        	}
	        	
        		//transfer into vector of rows
        		for(int i = 0; i < vec.size(); ++i) {
        			
        			WsSkladMoveDataRow row = new WsSkladMoveDataRow();
            		
            		row.row_vec = new Vector<WsSkladMoveDataColumn>();
        			
        			row.row_vec.add(vec.elementAt(i));
        			
        			row.indexData = 0; //rest
        			
        			row.agent_id = agent.id;
        			
        			row.agent_name = agent.name;
        			
        			row.date_end = end_date;
        			
        			row.date = end_date;
        			
        			row.date_doc = end_date;
        			
        			row.sklad_row = agent.id == 1111111;
        			
        			vec_rows.add(row);
        			
        		}
	        	
	        	return  vec_rows;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}

		return null;

	}
	
	
	public static HashMap<Integer, Vector<WsSkladMoveDataColumn>> getPrihodSumMoveForContract( Date start_date, Date end_date, String contactsIdsList) {
		
		try {
			
				WSConnect.get();
			 
				Connection conn = WSConnect.getCurrentConnection();
				//key -> kod, vector elements for different contract, but same kod
				HashMap<Integer, Vector<WsSkladMoveDataColumn>> res_map = new HashMap<Integer, Vector<WsSkladMoveDataColumn>>();
	        		
	        	String s2 = "SELECT part_types.kod, part_types.name, invoice_parts.id, invoices.id_contract, " 
	        			+ " (CASE WHEN invoices.date < ? THEN SUM(invoice_parts.quantity) ELSE 0 END), "
	        			+ " (CASE WHEN invoices.date < ? THEN SUM(invoice_parts.quantity*invoice_parts.cost) ELSE 0 END), "
	        			+ " (CASE WHEN invoices.date BETWEEN ? AND ? THEN SUM(invoice_parts.quantity) ELSE 0 END), "
	        			+ " (CASE WHEN invoices.date BETWEEN ? AND ? THEN SUM(invoice_parts.quantity*invoice_parts.cost) ELSE 0 END),"
	        			+ " contracts.name  "
	        			+ " FROM invoice_parts  "
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice "
	        			+ " INNER JOIN contracts ON contracts.id = invoices.id_contract "
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type " ;
	        	
	        	
	        	if(null == contactsIdsList) {
	        		
	        		s2 += " WHERE invoices.date <= ? GROUP BY part_types.kod, invoices.id_contract ORDER BY part_types.kod, invoice_parts.id;";
		        	
	        	}
	        	else {
	        		
	        		s2 += " WHERE invoices.date <= ? AND  invoices.id_contract IN ("
	        				 + contactsIdsList + ")   GROUP BY part_types.kod, invoices.id_contract ORDER BY part_types.kod, invoice_parts.id ;";
	        		
	        	}
	        	
	        	PreparedStatement ps2 = conn.prepareStatement(s2);
	        	
	        	ps2.setDate(1, start_date);
	        	 
	        	ps2.setDate(2, start_date);
	        	 
	        	ps2.setDate(3, start_date);
	        	 
	        	ps2.setDate(4, end_date);
	        	 
	        	ps2.setDate(5, start_date);
	        	 
	        	ps2.setDate(6, end_date);
	        	 
	        	ps2.setDate(7, end_date);
	        	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		int kod = rs2.getInt(1);
	        		
	        		int id_contract = rs2.getInt(4);
	        		
	        		Vector<WsSkladMoveDataColumn> vec = res_map.get(kod);
	        		
	        		if(vec == null) {
	        			
	        			vec = new Vector<WsSkladMoveDataColumn>();
	        			
	        			res_map.put(kod, vec);
	        			
	        		}
	        		
	        		WsSkladMoveDataColumn data = findSameContract(vec, id_contract);
	        		
	        		if(null == data) {
	        			
	        			 data = new WsSkladMoveDataColumn();
	        			 
	        			 vec.add(data);
	        		}
	        		
	        		data.q_array[0].initial_rest  += rs2.getDouble(5);
	        		
	        		data.q_array[0].initial_rest_sum += rs2.getDouble(6);
	        		
	        		data.q_array[0].in_quantity += rs2.getDouble(7);
	        		
	        		data.q_array[0].in_quantity_sum += rs2.getDouble(8);
	        		
	        		data.kod = rs2.getInt(1);
	        		
	        		data.name = rs2.getString(2);
	        		
	        		data.id_contract = id_contract;
	        		
	        		data.contract_name = rs2.getString(9);
	        				
				}				

	        	return res_map ;  
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;

	}
	
	public static Vector<WsSkladMoveDataColumn> getMovePartsForContracts( Date start_date, Date end_date, 
			Vector<WsContractData> vec_contracts) {
		
		
			StringBuffer sb_c = new StringBuffer();
		
			if(vec_contracts != null && !vec_contracts.isEmpty()) {
		
				for(int i = 0; i < vec_contracts.size(); ++i) {
	    	   
					sb_c.append(String.valueOf(vec_contracts.elementAt(i).id));
	    	   
					if(i !=  (vec_contracts.size() - 1)) {
	    		   
						sb_c.append(","); 
					}
	    	   
				}
				
		   }
	       
	       StringBuffer sb = new StringBuffer();
	       
	       sb.append("SELECT t1.kd, "
	       		+ "t1.nm, "
	       		+ "t1.idc, "
	       		+ "t1.cnn,");  
	       sb.append("t1.in_r, ");
	       sb.append("t1.in_q, ");
	       sb.append("IFNULL(t2.out_r, 0.0),"); 
	       sb.append("IFNULL(t2.out_q, 0.0), ");
	       sb.append("(t1.in_r - IFNULL(t2.out_r, 0.0)) AS init_rest, ");
	       sb.append("(t1.in_r - IFNULL(t2.out_r, 0.0) + t1.in_q -  IFNULL(t2.out_q, 0.0)) AS rest,");  
	       sb.append("(t1.in_r_sum - IFNULL(t2.out_r_sum, 0.0)) AS init_rest_sum,"); 
	       sb.append("t1.in_q_sum,"); 
	       sb.append("IFNULL(t2.out_q_sum, 0.0), ");
	       sb.append("( (t1.in_r - IFNULL(t2.out_r, 0.0) + t1.in_q -  IFNULL(t2.out_q, 0.0) )*( t1.in_r_sum - IFNULL(t2.out_r_sum, 0.0) )/( t1.in_r - IFNULL(t2.out_r, 0.0) ) ) AS rest_sum ");
	       sb.append("FROM ");
	       sb.append("(SELECT part_types.kod as kd, part_types.name as nm, invoices.id_contract as idc, contracts.name as cnn,");
	       sb.append("SUM(CASE WHEN invoices.date < ? THEN invoice_parts.quantity ELSE 0.0 END) as in_r, ");
	      // sb.append("SUM(CASE WHEN invoices.date < ? THEN invoice_parts.quantity*invoice_parts.costnds ELSE 0.0 END) as in_r_sum,  ");
	       sb.append("SUM(CASE WHEN invoices.date < ? THEN invoice_parts.quantity*(invoice_parts.cost + invoice_parts.nds) ELSE 0.0 END) as in_r_sum,  ");
	       sb.append("SUM(CASE WHEN invoices.date BETWEEN ? AND ? THEN invoice_parts.quantity ELSE 0.0 END) as in_q, ");
	       sb.append("SUM(CASE WHEN invoices.date BETWEEN ? AND ? THEN invoice_parts.quantity*(invoice_parts.cost + invoice_parts.nds) ELSE 0.0 END) as in_q_sum ");
	       sb.append("FROM invoice_parts  INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type ");
	       sb.append("INNER JOIN contracts ON invoices.id_contract =  contracts.id ");
	       sb.append("WHERE invoices.date <= ? ");
	       
	       if(vec_contracts != null && !vec_contracts.isEmpty()) {
		       sb.append(" AND invoices.id_contract IN (");
		       sb.append(sb_c.toString());  
		       sb.append(") ");
	       }
	       sb.append(" GROUP BY part_types.kod, invoices.id_contract ORDER BY part_types.kod, invoice_parts.id) AS t1 ");
	       sb.append("LEFT JOIN ");
	       sb.append("(SELECT part_types.kod as kd,  invoices.id_contract as idc,");
	       sb.append("SUM(CASE WHEN sale_invoices.date < ? THEN sale_parts.quantity ELSE 0.0 END) as out_r,");
	       sb.append("SUM(CASE WHEN sale_invoices.date < ? THEN sale_parts.quantity*(invoice_parts.cost + invoice_parts.nds) ELSE 0.0 END) as out_r_sum,  ");
	       sb.append("SUM(CASE WHEN sale_invoices.date BETWEEN ? AND ? THEN sale_parts.quantity ELSE 0.0 END) as out_q, ");
	       sb.append("SUM(CASE WHEN sale_invoices.date BETWEEN ? AND ? THEN sale_parts.quantity*(sale_parts.cost + sale_parts.nds)  ELSE 0.0 END) as out_q_sum ");
	       sb.append("FROM invoice_parts  INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type ");
	       sb.append("INNER JOIN sale_parts ON sale_parts.id_invoice_parts =  invoice_parts.id INNER JOIN sale_invoices ON sale_parts.id_sale_invoice =  sale_invoices.id ");
	       sb.append("WHERE sale_invoices.date <= ? ");
	       
	       if(vec_contracts != null && !vec_contracts.isEmpty()) {
		       sb.append(" AND invoices.id_contract IN (");
		       sb.append(sb_c.toString());  
		       sb.append(") ");
	       }
	   
	       sb.append(" GROUP BY part_types.kod, invoices.id_contract ORDER BY part_types.kod, invoice_parts.id) AS t2 ON t2.idc = t1.idc AND t1.kd = t2.kd ");
	       sb.append("WHERE init_rest > ? OR t1.in_q > ?;");
	       
	       
	       Vector<WsSkladMoveDataColumn> vec = new Vector<WsSkladMoveDataColumn>();
	       
		   try {
				
				WSConnect.get();
			 
				Connection conn = WSConnect.getCurrentConnection();
		
	        	PreparedStatement ps2 = conn.prepareStatement(sb.toString());
	        	
	        	ps2.setDate(1, start_date);
	        	 
	        	ps2.setDate(2, start_date);
	        	 
	        	ps2.setDate(3, start_date);
	        	 
	        	ps2.setDate(4, end_date);
	        	 
	        	ps2.setDate(5, start_date);
	        	 
	        	ps2.setDate(6, end_date);
	        	 
	        	ps2.setDate(7, end_date);
	        	
	          	ps2.setDate(8, start_date);
	        	 
	        	ps2.setDate(9, start_date);
	        	 
	        	ps2.setDate(10, start_date);
	        	 
	        	ps2.setDate(11, end_date);
	        	 
	        	ps2.setDate(12, start_date);
	        	 
	        	ps2.setDate(13, end_date);
	        	 
	        	ps2.setDate(14, end_date);
	        	
	        	ps2.setDouble(15, WsUtils.getRZL());
	        	
	        	ps2.setDouble(16, WsUtils.getRZL());
	        	
	        	ResultSet rs2 =  ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		WsSkladMoveDataColumn data = new WsSkladMoveDataColumn();
	        		
	        		data.q_array[0].initial_rest  = rs2.getDouble(9);
	        		
	        		data.q_array[0].initial_rest_sum = rs2.getDouble(11);
	        		
	        		data.q_array[0].in_quantity = rs2.getDouble(6);
	        		
	        		data.q_array[0].in_quantity_sum = rs2.getDouble(12);
	        		
	        		data.q_array[0].out_quantity = rs2.getDouble(8);
	        		
	        		data.q_array[0].out_quantity_sum = rs2.getDouble(13);
	        		
	        		data.q_array[0].rest = rs2.getDouble(10);
	        		
	        		//double rSum = rs2.getDouble(14);
	        		if(data.q_array[0].initial_rest < WsUtils.getRZL()) {
	        			
	        			if(data.q_array[0].in_quantity_sum < WsUtils.getRZL()) {
	        			
	        				data.q_array[0].rest_sum = 0.0;
	        			}
	        			else {
	        				
	        				data.q_array[0].rest_sum = data.q_array[0].rest*data.q_array[0].in_quantity_sum/data.q_array[0].in_quantity;
	        				
	        			}
	        		}
	        		else {
	        			
	        			data.q_array[0].rest_sum = data.q_array[0].rest*data.q_array[0].initial_rest_sum/data.q_array[0].initial_rest;
	        		}
	        		
	        		data.id_contract =  rs2.getInt(3);
	        		
	        		data.contract_name =  rs2.getString(4);
	        		
	        		data.kod = rs2.getInt(1);
	        		
	        		data.name =  rs2.getString(2);
	        		
	        		vec.add(data);
		
				}	
	        
	    		WsSkladMoveDataColumn dataSum = new WsSkladMoveDataColumn();
	    		
	    		for(WsSkladMoveDataColumn d : vec) {
	    			
	    			dataSum.q_array[0].initial_rest_sum += d.q_array[0].initial_rest_sum;
	    			
	    			dataSum.q_array[0].in_quantity_sum += d.q_array[0].in_quantity_sum;
	    			
	    			dataSum.q_array[0].out_quantity_sum += d.q_array[0].out_quantity_sum;
	    			
	    		}
	        	
	    		dataSum.q_array[0].rest_sum = dataSum.q_array[0].initial_rest_sum + dataSum.q_array[0].in_quantity_sum - dataSum.q_array[0].out_quantity_sum;
	    		
	    		
	    		dataSum.name = WsUtils.getGuiStrs("vsegoNaklName");
	    		
	    		dataSum.contract_name = "";
	        	
	    		vec.add(dataSum);

	        	return vec ;  
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	      
		   
		return vec;
		
		
	}
	
	
	public static HashMap<Integer, Vector<WsSkladMoveDataColumn>> getRashodSumMoveForContract( Date start_date, Date end_date, String contractsIdsList) {
		
		
		try {
			
				WSConnect.get();
			 
				Connection conn = WSConnect.getCurrentConnection();
				//key -> kod, vector elements for different contract, but same kod
				HashMap<Integer, Vector<WsSkladMoveDataColumn>> res_map = new HashMap<Integer, Vector<WsSkladMoveDataColumn>>();

	        	String s2 = "SELECT sale_parts.id, part_types.kod, part_types.name, contracts.id, contracts.name, "      
	        			+ " (CASE WHEN sale_invoices.date < ? THEN SUM( sale_parts.quantity) ELSE 0 END), "
	        			+ " (CASE WHEN sale_invoices.date < ? THEN SUM( sale_parts.quantity*sale_parts.cost) ELSE 0 END), "
	        			+ " (CASE WHEN sale_invoices.date BETWEEN ? AND ? THEN SUM(sale_parts.quantity) ELSE 0 END), "
	        			+ " (CASE WHEN sale_invoices.date BETWEEN ? AND ? THEN SUM(sale_parts.quantity*sale_parts.cost) ELSE 0 END) "
	        			+ " FROM sale_parts "
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id = sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON invoice_parts.id = sale_parts.id_invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN contracts ON invoices.id_contract = contracts.id"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type";

	        	if(null == contractsIdsList) {
	        		
	        		s2 += " WHERE sale_invoices.date <= ? GROUP BY part_types.kod, contracts.id ORDER BY part_types.kod, sale_parts.id;";
		        	
	        	}
	        	else {
	        		
	        		s2 += " WHERE sale_invoices.date <= ? AND  invoices.id_contract IN ("
	        				 + contractsIdsList + ")   GROUP BY part_types.kod, contracts.id ORDER BY part_types.kod, sale_parts.id ;";
	        		
	        	}
	        	
	        	PreparedStatement ps2 = conn.prepareStatement(s2);
	        	
	        	ps2.setDate(1, start_date);
	        	 
	        	ps2.setDate(2, start_date);
	        	 
	        	ps2.setDate(3, start_date);
	        	 
	        	ps2.setDate(4, end_date);
	        	 
	        	ps2.setDate(5, start_date);
	        	 
	        	ps2.setDate(6, end_date);
	        	 
	        	ps2.setDate(7, end_date);
	        	
	        	ResultSet rs2 =   ps2.executeQuery();
	        	
	        	while(rs2.next()) {
	        		
	        		int kod = rs2.getInt(2);
	        		
	        		int id_contract = rs2.getInt(4);
	        		
	        		String contract_name = rs2.getString(5);
	        		
	        		Vector<WsSkladMoveDataColumn> vec = res_map.get(kod);
	        		
	        		if(vec == null) {
	        			
	        			vec = new Vector<WsSkladMoveDataColumn>();
	        			
	        			res_map.put(kod, vec);
	        			
	        		}
	        		
	        		WsSkladMoveDataColumn data = findSameContract(vec, id_contract);
	        		
	        		if(null == data) {
	        			
	        			 data = new WsSkladMoveDataColumn();
	        			 
	        			 vec.add(data);
	        		}
	        		
	        		data.q_array[0].initial_rest  += rs2.getDouble(6);
	        		
	        		data.q_array[0].initial_rest_sum += rs2.getDouble(7);
	        		
	        		data.q_array[0].out_quantity += rs2.getDouble(8);
	        		
	        		data.q_array[0].out_quantity_sum += rs2.getDouble(9);
	        		
       			 	data.id_contract = id_contract;
    			 
       			 	data.contract_name = contract_name;
    			 
       			 	data.name =  rs2.getString(3);
    			 
       			 	data.kod = kod;
	        		
	        	}
	        	
	        	rs2.close();
	        	
	        	ps2.close();
	        	
	        	
	        	return res_map;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
		return null;

	}
	
	private static WsSkladMoveDataColumn findSameContract(Vector<WsSkladMoveDataColumn> vec, double id_contract) {
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsSkladMoveDataColumn res = vec.elementAt(i);
			
			if(res.id_contract == id_contract) { return res;}
			
		}
		
		return null;
		
	}
	
		
}

