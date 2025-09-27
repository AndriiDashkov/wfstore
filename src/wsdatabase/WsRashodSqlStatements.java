
package wsdatabase;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import wsdatastruct.WsContractPriceData;
import wsdatastruct.WsPair;
import wsdatastruct.WsPartType;
import wsdatastruct.WsPrihodData;
import wsdatastruct.WsPrihodPartData;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsdatastruct.WsReturnedPartData;
import wsdatastruct.WsUnitData;
import wsmain.WsTokenizer;
import wsmain.WsUtils;

/**
 * The collections of SQL statements for 'rashod' tables in the database.
 * 'Rashod' operations means all output(sales) invoices.
 * There are 2 basic 'rashod' table in the database: 'sale_invoices' and 'sale_parts'.
 * The relation between them is one to many.
 * The table 'sale_parts' is also connected to the table 'invoice_parts' with
 * relation many to one.
 * 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsRashodSqlStatements {
	
	public static Vector<WsRashodData> getRashodList(Date start_d, Date end_d) {
	
		WsConnect.get();
		
		Connection conn = WsConnect.getCurrentConnection();
		
		return getRashodList(conn, start_d,  end_d);
	
	}


	public static Vector<WsRashodData> getRashodList(Connection conn, Date start_d, Date end_d) {
		
	    PreparedStatement ps;
	   
		try {
			
			Vector<WsRashodData> vec = new Vector<WsRashodData>();

	        	
	        String s = "SELECT sale_invoices.id, sale_invoices.number, sale_invoices.date,"
	        			+ " counterparties.name,   sale_invoices.info, id_counterparty, people   FROM sale_invoices "
	        			+ " INNER JOIN counterparties ON counterparties.id = sale_invoices.id_counterparty "
	        			+ " WHERE  sale_invoices.date BETWEEN ? AND ?;";
	        	
	        ps = conn.prepareStatement(s);
	        	
	        ps.setDate(1, start_d);
	        
	        ps.setDate(2, end_d);
	
	        ResultSet rs =  ps.executeQuery();
	        	
	        while(rs.next()) {
	        		
	        		WsRashodData d = new WsRashodData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.number = rs.getString(2);
	        		
	        		d.date = rs.getDate(3);
	        		
	        		d.agentName = rs.getString(4);
	        		
	        		d.info = rs.getString(5);
	        		
	        		d.id_counterparty = rs.getInt(6);
	        		
	        		d.people = rs.getInt(7);
	        		
	        		
	        		vec.add(d);
	        	
	       }
	        	
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
	
	
	public static Vector<WsRashodData> getRashodList(int id_agent, int id_contract, java.sql.Date start, 
			java.sql.Date end) {

	    boolean data_is_valid = start != null && end != null && (start.before(end) || start.equals(end));
	       
	   
		try {
			
			WsConnect.get();

			Vector<WsRashodData> vec = new Vector<WsRashodData>();
			
			StringBuilder s_b = new StringBuilder();
			
			s_b.append("SELECT sale_invoices.id, sale_invoices.number, sale_invoices.date,"
        			+ " counterparties.name,  sale_invoices.info, sale_invoices.id_counterparty, people  FROM sale_invoices "
        			+ " INNER JOIN counterparties ON counterparties.id = sale_invoices.id_counterparty  ");
        	
        	if(id_contract != -1) {
        		
        		s_b.append("INNER JOIN  sale_parts ON  sale_parts.id_sale_invoice = sale_invoices.id "
		        	+ " INNER JOIN  invoice_parts ON  sale_parts.id_invoice_parts = invoice_parts.id  "
					+ " INNER JOIN  invoices ON  invoices.id = invoice_parts.id_invoice "
					+ " INNER JOIN contracts ON contracts.id = invoices.id_contract");
        	}
        	
        	boolean flag = false;

        	if(id_agent != -1) {
        	
        		s_b.append(" WHERE sale_invoices.id_counterparty = ");
        		
        		s_b.append(Integer.toString(id_agent));
        		
        		s_b.append(" ");
        		
        		flag = true;
        	}
        	
        	if(id_contract != -1) {
        		
        		if(flag) { s_b.append(" AND "); } else { s_b.append(" WHERE "); }
	        	
        		s_b.append("contracts.id =  "); 
        		
        		s_b.append(Integer.toString(id_contract) );
        		
        		s_b.append(" ");
        		
        		flag = true;
        	}
        	
        	if(data_is_valid) {
        		
        		if(flag) { s_b.append(" AND "); } else { s_b.append(" WHERE "); }
	        	
        		s_b.append("sale_invoices.date BETWEEN ? AND ? ");
        		

        		flag = true;
        	}
        	
        	if(flag) { s_b.append(" GROUP BY sale_invoices.id"); } 
        	
        	s_b.append(" ORDER BY sale_invoices.date;");
        	
        	PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(s_b.toString());
        	
        	if(data_is_valid) {
        		
        		ps.setDate(1, start);
	        	
	        	ps.setDate(2, end);
        	}

        	ResultSet rs =  ps.executeQuery();
        	
        	while(rs.next()) {
        		
        		WsRashodData d = new WsRashodData();
        		
        		d.id = rs.getInt(1);
        		
        		d.number = rs.getString(2);
        		
        		d.date = rs.getDate(3);
        		
        		d.agentName = rs.getString(4);
        		
        		d.info = rs.getString(5);
        		
        		d.id_counterparty = rs.getInt(6);
        		
        		d.people = rs.getInt(7);
        		
        		vec.add(d);
        		
        	}
        	
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
	
	public static Vector<WsRashodData> getRashodList(int id_agent, int id_contract,  java.sql.Date start, 
			java.sql.Date end, int kod_id, boolean kod_inverse) {
	   
	    boolean data_is_valid = start != null && end != null && (start.before(end) || start.equals(end));
	     
	    if(kod_id == -1) {
	    	
	    	return getRashodList(id_agent, id_contract, start, end);
	    }
	    
		Vector<WsRashodData> vec = new Vector<WsRashodData>();
	   
		try {
			
			WsConnect.get();
			
	        StringBuilder s_b = new StringBuilder();
	        
	        s_b.append("SELECT sale_invoices.id, sale_invoices.number, sale_invoices.date,");
	        
	        s_b.append(" counterparties.name, sale_invoices.info, sale_invoices.id_counterparty, people" );
	        
			if( kod_inverse) {
				
				s_b.append(", SUM(CASE WHEN invoice_parts.id_part_type <> "); 
				
				s_b.append( Integer.toString(kod_id) ); 
				
				s_b.append(" THEN 0 ELSE 1 END) AS sum_inv ");
        	}
	       
	        s_b.append( " FROM sale_invoices ");
	        
	        s_b.append( "INNER JOIN  sale_parts ON  sale_parts.id_sale_invoice = sale_invoices.id  "
	        			+ " INNER JOIN  invoice_parts ON  sale_parts.id_invoice_parts = invoice_parts.id  "
	        			+ " INNER JOIN counterparties ON counterparties.id = sale_invoices.id_counterparty  ");
	        
	        if(id_contract != -1) {
	        	
	        	 s_b.append( " INNER JOIN  invoices ON  invoices.id = invoice_parts.invoice_id  "
		        			+ "INNER JOIN contracts ON contracts.id = invoices.id_contract");
	        	
	        }
	        	
        	if( kod_inverse) {
        		
        		s_b.append(" WHERE invoice_parts.id_part_type <> " );
        		
        		s_b.append( Integer.toString(kod_id) );
        		
        		s_b.append( " ");
        	}
        	else {
        		
        		s_b.append( " WHERE invoice_parts.id_part_type = " );
        		
        		s_b.append( Integer.toString(kod_id) );
        		
        		s_b.append( " ");
        	}
        	
        	if(id_agent != -1) {
        	
        		s_b.append( " AND id_counterparty = " );
        		
        		s_b.append( Integer.toString(id_agent) );
        		
        		s_b.append( " ");
        		
        	}
        	if(id_contract != -1) {
            	
        		s_b.append( " AND contracts.id = " );
        		
        		s_b.append( Integer.toString(id_contract) );
        		
        		s_b.append( " ");
        		
        	}
        	
        	if(data_is_valid) {
        		
        		s_b.append( " AND sale_invoices.date BETWEEN ? AND ? ");
        
        	}
        	
        	
        	if( kod_inverse) {
        		
        		s_b.append( " GROUP BY sale_invoices.id HAVING sum_inv == 0 ORDER BY sale_invoices.date, sale_invoices.id;"); 
        	}
        	else {
        		
        		s_b.append( " GROUP BY sale_invoices.id ORDER BY sale_invoices.date, sale_invoices.id;"); 
        		
        	}
        	
        	PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(s_b.toString());
        	
        	if(data_is_valid) {
        		
        		ps.setDate(1, start);
	        	
	        	ps.setDate(2, end);
        	}

        	ResultSet rs =  ps.executeQuery();
        	
        	while(rs.next()) {
        		
        		WsRashodData d = new WsRashodData();
        		
        		d.id = rs.getInt(1);
        		
        		d.number = rs.getString(2);
        		
        		d.date = rs.getDate(3);
        		
        		d.agentName = rs.getString(4);
        		
        		d.info = rs.getString(5);
        		
        		d.id_counterparty = rs.getInt(6);
        		
        		d.people = rs.getInt(7);
        		
        		vec.add(d);
        		
        	}
        	
        	rs.close();
        	
        	ps.close();
        	
        	return vec;

		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	   
		return vec;
	}
	
	public static Vector<WsRashodPartData> getRashodPartsList(int id_sale_invoices, int sort_type) {
	
		WsConnect.get();
		
		Connection conn = WsConnect.getCurrentConnection();
		
		return getRashodPartsList(conn, id_sale_invoices, sort_type);
	
	}
	
	
	
	public static Vector<WsRashodPartData> getRashodPartsList(Connection conn, int id_sale_invoices, int sort_type) {
		
        Statement st;
       
		try {
			
			st = conn.createStatement();
			
			Vector<WsRashodPartData> vec = new  Vector<WsRashodPartData>();
			
	        if (st != null) {
	        	

	        	StringBuilder s_b =  new StringBuilder();
	        	
	        	s_b.append("SELECT sale_parts.id,  sale_parts.name, sale_parts.quantity, "
	        			+ " units.name, sale_parts.vendor_code_2, sale_parts.info, id_sale_invoice, "
	        			+ " id_invoice_parts, sale_parts.id_units,  invoice_parts.rest, "
	        			+ " sale_parts.cost, sale_parts.nds,part_types.kod, sale_parts.req_quantity,"
	        			+ " units.name, sale_parts.costnds FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON sale_parts.id_invoice_parts =  invoice_parts.id"
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = sale_parts.id_units"
	        			+ " WHERE  sale_parts.id_sale_invoice = ");
	        	 
	        	s_b.append( Integer.toString(id_sale_invoices));
	        	
	        	switch(sort_type) {
	        	
		        	case 0: {
		        		
		        		s_b.append(" ORDER BY sale_parts.vendor_code_2;") ; 
		        		
		        		break;
		        	}
		        	case 1: {
		        		
		        		s_b.append(" ORDER BY sale_parts.name;") ; 
		        		
		        		break;
		        	}
		        	case 2: {
		        		
		        		s_b.append(" ORDER BY sale_parts.quantity;") ; 
		        		
		        		break;
		        	}
		        	
		        	default : {
		        		
		        		s_b.append(";");
		        	}
	        	
	        	};
	        	
	        	ResultSet rs = st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsRashodPartData d = new WsRashodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.name = rs.getString(2);
	        		
	        		d.quantity = rs.getDouble(3);
	        		
	        		d.units_name = rs.getString(4);
	        		
	        		d.vendor_code_2 = rs.getString(5);
	        		
	        		d.info = rs.getString(6);
	        		
	        		d.id_sale_invoice = rs.getInt(7);
	        		
	        		d.id_invoice_parts = rs.getInt(8);
	        		
	        		d.id_units =  rs.getInt(9);
	        		
	        		d.rest = rs.getDouble(10);
	        		
	        		d.cost = rs.getDouble(11);
	        		
	        		d.nds = rs.getDouble(12);
	        		
	        		d.kod = rs.getInt(13);
	        		
	        		d.req_quantity = rs.getDouble(14);
	        		
	        		d.units_name = rs.getString(15);
	        		
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
	
	public static Vector<WsRashodPartData> getRashodPartsListForPrihodId(int id_invoice_part, int sort_type) {
		
        Statement st;
        
        WsConnect.get();
		
		Connection conn = WsConnect.getCurrentConnection();
       
		try {
			
			st = conn.createStatement();
			
			Vector<WsRashodPartData> vec = new  Vector<WsRashodPartData>();
			
	        if (st != null) {
	        	

	        	StringBuilder s_b =  new StringBuilder();
	        	
	        	s_b.append("SELECT id, id_invoice_parts, name,  quantity, req_quantity, rest, id_units, "
	        			+ "vendor_code_2,info, cost, nds, costnds, id_sale_invoice   FROM sale_parts WHERE  id_invoice_parts = ");
	        	
	        	s_b.append( Integer.toString(id_invoice_part));
	        	
	        	switch(sort_type) {
	        	
		        	case 0: {
		        		
		        		s_b.append(" ORDER BY sale_parts.vendor_code_2;") ; 
		        		
		        		break;
		        	}
		        	case 1: {
		        		
		        		s_b.append(" ORDER BY sale_parts.name;") ; 
		        		
		        		break;
		        	}
		        	case 2: {
		        		
		        		s_b.append(" ORDER BY sale_parts.quantity;") ; 
		        		
		        		break;
		        	}
		        	
		        	default : {
		        		
		        		s_b.append(";");
		        	}
	        	
	        	};
	        	
	        	ResultSet rs = st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsRashodPartData d = new WsRashodPartData();

	        		d.id = rs.getInt(1);
	        		
	        		d.id_invoice_parts = rs.getInt(2);
	        		
	        		d.name = rs.getString(3);
	        		
	        		d.quantity = rs.getDouble(4);
	        		
	        		d.req_quantity = rs.getDouble(5);
	        		
	        		d.rest = rs.getDouble(6);
	        		
	        		d.id_units =  rs.getInt(7);
	        		
	        		d.vendor_code_2 = rs.getString(8);
	        		
	        		d.info = rs.getString(9);
	        		
	        		d.cost = rs.getDouble(10);
	        		
	        		d.nds = rs.getDouble(11);
	        		
	        		d.costwithnds = rs.getDouble(12);
	        		
	        		d.id_sale_invoice = rs.getInt(13);
	        		
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

	public static Vector<WsRashodPartData> getRashodPartsListKodGroup(Connection conn, int id_sale_invoices, 
			int sort_type) {
		
        Statement st;
       
		try {
			
			st = conn.createStatement();
			
			Vector<WsRashodPartData> vec = new  Vector<WsRashodPartData>();
			
	        if (st != null) {
	        	
	        	StringBuilder s_b =  new StringBuilder();
	        			
	        	s_b.append("SELECT sale_parts.id,  sale_parts.name, SUM(sale_parts.quantity), "
	        			+ " units.name, sale_parts.vendor_code_2, sale_parts.info, id_sale_invoice, "
	        			+ " id_invoice_parts, sale_parts.id_units,  SUM(invoice_parts.rest), "
	        			+ " sale_parts.cost, sale_parts.nds,part_types.kod, SUM(sale_parts.req_quantity),"
	        			+ " units.name FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON sale_parts.id_invoice_parts =  invoice_parts.id"
	        			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = sale_parts.id_units"
	        			+ " WHERE  sale_parts.id_sale_invoice = ");
	        	 
	        	s_b.append(Integer.toString(id_sale_invoices));
	        	 
	        	s_b.append(" GROUP BY part_types.kod ");
	        	
	        	switch(sort_type) {
	        	
	        	case 0: {
	        		
	        		s_b.append(" ORDER BY sale_parts.vendor_code_2;") ; 
	        		
	        		break;
	        	}
	        	case 1: {
	        		
	        		s_b.append(" ORDER BY sale_parts.name;") ; 
	        		
	        		break;
	        	}
	        	case 2: {
	        		
	        		s_b.append(" ORDER BY sale_parts.quantity;") ; 
	        		
	        		break;
	        	}
	        	
	        	default : {
	        		
	        		s_b.append(";");
	        	}
	        	
	        	};
	        	
	        	ResultSet rs = st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsRashodPartData d = new WsRashodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.name = rs.getString(2);
	        		
	        		d.quantity = rs.getDouble(3);
	        		
	        		d.units_name = rs.getString(4);
	        		
	        		d.vendor_code_2 = rs.getString(5);
	        		
	        		d.info = rs.getString(6);
	        		
	        		d.id_sale_invoice = rs.getInt(7);
	        		
	        		d.id_invoice_parts = rs.getInt(8);
	        		
	        		d.id_units =  rs.getInt(9);
	        		
	        		d.rest = rs.getDouble(10);
	        		
	        		d.cost = rs.getDouble(11);
	        		
	        		d.nds = rs.getDouble(12);
	        		
	        		d.kod = rs.getInt(13);
	        		
	        		d.req_quantity = rs.getDouble(14);
	        		
	        		d.units_name = rs.getString(15);
	        		
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
	
	public static Vector<WsRashodPartData> getRashodPartsVector(int id_sale_invoices) {
		
        Statement st;
        
        Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>();
       
		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	

	        	StringBuilder s_b = new StringBuilder();
	        	
	        	s_b.append("SELECT sale_parts.id,  sale_parts.name, sale_parts.quantity, "
	        			+ " units.name, sale_parts.vendor_code_2, sale_parts.info, id_sale_invoice, "
	        			+ "id_invoice_parts, sale_parts.id_units,  invoice_parts.rest, "
	        			+ " sale_parts.cost, sale_parts.nds FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON sale_parts.id_invoice_parts =  invoice_parts.id"
	        			+ " INNER JOIN units ON units.id = sale_parts.id_units"
	        			+ " WHERE  sale_parts.id_sale_invoice = ");
	        	
	        	s_b.append( Integer.toString(id_sale_invoices));
	        	
	        	s_b.append( ";");
	        	
	        	ResultSet rs =  st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsRashodPartData d = new WsRashodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.name = rs.getString(2);
	        		
	        		d.quantity =  rs.getDouble(3);
	        		
	        		d.units_name = rs.getString(4);
	        		
	        		d.vendor_code_2 = rs.getString(5);
	        		
	        		d.info = rs.getString(6);
	        		
	        		d.id_sale_invoice = rs.getInt(7);
	        		
	        		d.id_invoice_parts = rs.getInt(8);
	        		
	        		d.id_units = rs.getInt(9);
	        		
	        		d.rest =  rs.getDouble(10);
	        		
	        		d.cost =  rs.getDouble(11);
	        		
	        		d.nds =  rs.getDouble(12);
	        		
	        		d.kod = Integer.parseInt(d.vendor_code_2);
	        		
	        		vec.add(d);
	        		
	        	}
	        	rs.close();
	        
	        }
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
    
		return vec;

	}
	
	public static Vector<WsRashodPartData> getRashodPartsVector(Vector<Integer> vec_in) {
		
        Statement st;
        
        Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>();
        
        HashMap<Integer, List<WsRashodPartData>> map = new HashMap<Integer,List<WsRashodPartData>>();
       
		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	

	        	StringBuilder s_b = new StringBuilder();
	        	
	        	s_b.append("SELECT sale_parts.id,  sale_parts.name, sale_parts.quantity, "
	        			+ " units.name, sale_parts.vendor_code_2, sale_parts.info, id_sale_invoice, "
	        			+ "id_invoice_parts, sale_parts.id_units,  invoice_parts.rest, "
	        			+ " sale_parts.cost, sale_parts.nds FROM sale_parts"
	        			+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
	        			+ " INNER JOIN invoice_parts ON sale_parts.id_invoice_parts =  invoice_parts.id"
	        			+ " INNER JOIN units ON units.id = sale_parts.id_units"
	        			//+ " WHERE  sale_parts.id_sale_invoice = ");
	        	
	        			+ " WHERE  sale_parts.id_sale_invoice  IN (");
	        	
	        	//s_b.append(vec_in.elementAt(0).toString());
	        	
	        	
	        	//for(int i = 1; i < vec_in.size(); ++i) {
	        		
	        	//	s_b.append(" OR sale_parts.id_sale_invoice = " + vec_in.elementAt(i).toString());
	        		
	        	//}
	        	
	        	for(int i = 0; i < vec_in.size() - 1; ++i) {
	        		
		        		s_b.append( vec_in.elementAt(i).toString() + ",");
		        		
		        }
	        	
	        	s_b.append( vec_in.elementAt(vec_in.size() - 1).toString() + ")");
	        	
	        	
	        	
	        	s_b.append( ";");
	        	
	        	System.out.println(s_b.toString());
	        	
	        	ResultSet rs =  st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsRashodPartData d = new WsRashodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.name = rs.getString(2);
	        		
	        		d.quantity =  rs.getDouble(3);
	        		
	        		d.units_name = rs.getString(4);
	        		
	        		d.vendor_code_2 = rs.getString(5);
	        		
	        		d.info = rs.getString(6);
	        		
	        		d.id_sale_invoice = rs.getInt(7);
	        		
	        		d.id_invoice_parts = rs.getInt(8);
	        		
	        		d.id_units = rs.getInt(9);
	        		
	        		d.rest =  rs.getDouble(10);
	        		
	        		d.cost =  rs.getDouble(11);
	        		
	        		d.nds =  rs.getDouble(12);
	        		
	        		d.kod = Integer.parseInt(d.vendor_code_2);
	        		
	        		if(d.kod == 1004) {
	        			
	        			System.out.println("");
	        		}
	        		
	        		if(map.containsKey(d.kod)) {
	        			
	        			List<WsRashodPartData> d_map_list = map.get(d.kod);
	        			
	        			boolean found_flag = false;
	        			
	        			for(WsRashodPartData d_map: d_map_list) {
	        			
		        			if(Math.abs(d.cost - d_map.cost) < WsUtils.getRZL()) {
		        				
		        				d_map.quantity += d.quantity;
		        				
		        				d_map.rest += d.rest;
		        				
		        				found_flag = true;
		        				
		        				break;
		        			}
	        			
	        			}
	        			if(!found_flag) {
	        				
	        				d_map_list.add(d);
	        				
	        			}
	        		}
	        		else {
	        			
	        			List<WsRashodPartData> lt = new ArrayList<WsRashodPartData>();
	        			
	        			lt.add(d);
	        			
	        			map.put(d.kod , lt);
	        			
	        		}
	        		
	        	}
	        	
	        	rs.close();
	        	
	        	ArrayList<Integer> t_l =  new ArrayList<Integer>(map.keySet());
	        	
	        	Collections.sort(t_l);
	        	
	        	for(Integer kod: t_l) {
	        		
	        		List<WsRashodPartData> l = map.get(kod);
	        		
	        		for(WsRashodPartData d : l) {
	        			
	        			vec.add(d);
	        		}
	        	}
	        }
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
       
    
		return vec;

	}
	
	public static String checkRashodForRest(Vector<WsRashodPartData> vec)
	{
		StringBuilder s_b = new StringBuilder("");
		
		for(int  i = 0; i < vec.size(); ++i) {
			
			WsRashodPartData d = vec.elementAt(i);
			
			double rest = WsPrihodSqlStatements.getRestForInvoicePartId(d.id_invoice_parts);
			
			double old_quantity = 0.0;
			
			if(d.id != -1) {
				
				StringBuilder old_sql = new StringBuilder();
				
				old_sql.append("SELECT quantity FROM sale_parts WHERE id = ");
				
				old_sql.append( Integer.toString( d.id));
				
				old_sql.append( ";");
	    			 
	    		Statement st;
	    		
				try {
					
					st = WsConnect.getCurrentConnection().createStatement();
				
		    		ResultSet rs_old = st.executeQuery(old_sql.toString());
		    		
		    		rs_old.next();
		    		
		    		old_quantity = rs_old.getDouble(1);
		    		
		    		rs_old.close();
		    		
		    		st.close();
		    		
				} catch (SQLException e) {
				
					e.printStackTrace();
				}
			}
			
			if((rest + old_quantity) < d.quantity) {
				
				s_b.append(d.name);
				
				s_b.append( ";<br>");
				
			}
		}
		
		String s = s_b.toString();
		
		if(s != null &&  !s.isEmpty()) {
			
			s = "<html>" + s + "</html>";
		}
		
		return  s;		
	}
	
	//checks for forbidden symbols in the names
	private static String checkName(String name, int kod) {
		
		if(WsTokenizer.isValidate(name) == false) {
			
			WsPartType d = WsUtilSqlStatements.getPartTypeForKod(kod);
			
			if(d == null) { return name; }
			
			return d.name;
			
		}
		
		return name;
		
	}
	
	//must use 'шт' for these codes 
	private static int checkUnitsEggs(int kod, int old_id_units) {
		
		boolean flag = (WsUtils.isKodEqual(kod, WsUtils.EGG_KOD_1)) || (WsUtils.isKodEqual(kod, WsUtils.EGG_KOD_2));
		
		
		if(flag) {
						
			return 1;
			
		}
		
		return  old_id_units;
		
	}
	
	
	//returns inserted id
	public static int createNewRashod(WsRashodData data, Vector<WsRashodPartData> vec) {
		
	       PreparedStatement ps = null;
	       
	       Connection cn = null;
	       
			try {
				
	        	final String insertion_order = "INSERT INTO sale_invoices "
	        			+ "(number, date, info,  id_counterparty, people) VALUES(?,?,?,?,?);";
	       	  	           
	        	WsConnect.get();
	        	
	        	cn = WsConnect.getCurrentConnection();
	        	
				ps = cn.prepareStatement(insertion_order, Statement.RETURN_GENERATED_KEYS);
				
	            ps.setString(1, data.number);
	      	     
	            ps.setDate(2, data.date);
	            
	            ps.setString(3, data.info);
	            
	            ps.setInt(4, data.id_counterparty);
	            
	            ps.setInt(5, data.people);
	            
	            @SuppressWarnings("unused")
				int procesedRows = ps.executeUpdate();
	            
	            if(procesedRows != 1) {  return -1; }
	            
	            int inserted_id = -1;
	            
	            ResultSet rs = ps.getGeneratedKeys();
	            
	            if (rs.next()){
	            	
	                inserted_id = rs.getInt(1);
	            }
	            
	            rs.close();
	            
	            ps.close();
	            
	            if(inserted_id == -1) {  return -1; }
	            
	            if(vec.isEmpty()) { return inserted_id ;}
	            
	            Iterator<WsRashodPartData> value = vec.iterator();
	            
	            StringBuilder s_b = new StringBuilder("");
	            
	            s_b.append("INSERT INTO sale_parts ( id_sale_invoice, "
	            		+ "id_invoice_parts, \n"
		                + "name, quantity, rest, \n"
		                + " id_units, vendor_code_2, info, cost, nds, req_quantity, costnds)\n"
		                + "VALUES ");
	            
	            while(value.hasNext()) {
	            	
	            	WsRashodPartData d = (WsRashodPartData) value.next();
	            	
	            	d.name = checkName(d.name, d.kod);
	            	
	            	d.id_units =  checkUnitsEggs(d.kod, d.id_units);
	            	
	            	s_b.append( "(" + Integer.toString(inserted_id) );
	            	
	            	s_b.append( ",\n");
	            	
	            	s_b.append( Integer.toString(d.id_invoice_parts) );
	            	
	            	s_b.append( ",\n");
	            	
	            	s_b.append( "'" );
	            	
	            	s_b.append( d.name );
	            	
	            	s_b.append( "',\n");
	            	
	            	s_b.append( Double.toString(d.quantity) );
	            	
	            	s_b.append( ",\n");
	            	
	            	s_b.append( Double.toString(d.rest) );
	            	
	            	s_b.append( ",\n");
	            	
	            	s_b.append(  Integer.toString(d.id_units));
	            	
	            	s_b.append( ",\n");
	            	
	            	s_b.append( "'" + d.vendor_code_2 );
	            	
	            	s_b.append( "',\n");
	            	
	            	s_b.append( "'" );
	            	
	            	s_b.append( d.info );
	            	
	            	s_b.append( "',");
	            	
	            	s_b.append( Double.toString(d.cost) );
	            	
	            	s_b.append( ",");
	            	
	            	s_b.append( Double.toString(d.nds) );
	            	
	            	s_b.append( ",");
	            	
	            	s_b.append( Double.toString(d.req_quantity) );
	            	
	            	s_b.append( ",");
	            	
	            	s_b.append( Double.toString(d.costwithnds) );
	            	
	            	s_b.append( ")");
	            			
	            	if(value.hasNext()) {
	            		
	            		s_b.append( ",");
	            	}
	            	
	            }
	            
	            s_b.append(";");
	                     
	            ps = cn.prepareStatement( s_b.toString());
	            
	            @SuppressWarnings("unused")
				boolean flag = ps.executeUpdate() == vec.size();
	            
	            if(!flag) {  return -1;  }
	            
	            ps.close();
	            
		        int prows = updateSkladPositionsForRashod( inserted_id);
		        
		        if(prows == -1) {  return -1;  }
		        
	            return inserted_id;
	            
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			
			}
			
			return -1;
		
	}
	
	
	public static boolean updateRashod(WsRashodData data, 
			Vector<WsRashodPartData> vec ) {
			
	       PreparedStatement ps = null;
	       
	       Connection cn = null;
	       
		   try {
				
	        	final String update = "UPDATE sale_invoices SET number = ?,   date = ?, info = ?, "
	        			+ "id_counterparty = ?, people = ? WHERE id = ?;";
	        	            
	        	WsConnect.get();
	        	
	        	cn = WsConnect.getCurrentConnection();
	        	
				ps = cn.prepareStatement(update);
				
	            ps.setString(1, data.number);
	            
	            ps.setDate(2, data.date);
	            
	            ps.setString(3, data.info);
	            
	            ps.setInt(4, data.id_counterparty);
	            
	            ps.setInt(5, data.people);
	            
	            ps.setInt(6, data.id);
	            
	            if(ps.executeUpdate() != 1) { return false; }
	            
	            ps.close();
	            
	            //check and delete rows which are in the DB , but was deleted in the table
	            int delRows =  updateDeletePartsRashod(data, vec);
	            
	            if(delRows == -1) { return false; }
	            
	      		Iterator<WsRashodPartData> value = vec.iterator();
	          
	      		int numRowsAffected = 0;
		            
		            while(value.hasNext()) {
		            	
		            	WsRashodPartData d = (WsRashodPartData) value.next();
		            	
		             	WsConnect.get();
		             	
		             	if (checkIsPartIdExists(d.id)) {
		             		
		             		//get old quantity
		             		StringBuilder old_sql_b =  new StringBuilder();
		             		
		             		old_sql_b.append("SELECT quantity FROM sale_parts  WHERE id = ");
		             		 
		             		old_sql_b.append( Integer.toString( d.id));
		             		
		             		old_sql_b.append( ";");
		            			 
		            		Statement st = WsConnect.getCurrentConnection().createStatement();
		            			
		            		ResultSet rs_old = st.executeQuery(old_sql_b.toString());
		            		
		            		rs_old.next();
		            		
		            		double old_quantity = rs_old.getDouble(1);
		            		
		            		rs_old.close();
		            		
		            		st.close();
		            		
		            		final String update_parts = "UPDATE sale_parts SET id_sale_invoice = ?, id_invoice_parts = ?,\n"
		                  			+ "name = ?, quantity = ?,\n"
		                  			+ " rest = ?, id_units = ?, \n "
		                  			+ " vendor_code_2 = ?,\n"
		                  			+ " info = ?, cost = ?, nds =?,\n"
		                  			+ " req_quantity = ?, costnds = ? "
		                  			+ " WHERE id = ?;";
			        	
							ps = WsConnect.getCurrentConnection().prepareStatement(update_parts);
							
				            ps.setInt(1, d.id_sale_invoice); 
				            
				            ps.setInt(2, d.id_invoice_parts);
				            
				            ps.setString(3, d.name); 
				            
				            ps.setDouble(4, d.quantity);
				            
				            ps.setDouble(5, d.rest); 
				            
				            ps.setInt(6, d.id_units);
				            
				            ps.setString(7, d.vendor_code_2);  
				            
				            ps.setString(8, d.info);
				            
				            ps.setDouble(9, d.cost); 
				            
				            ps.setDouble(10, d.nds);
				            
				            ps.setDouble(11, d.req_quantity);
				            
				            ps.setDouble(12, d.costwithnds); 
				            
				            ps.setInt(13, d.id);
			            	
				            numRowsAffected = ps.executeUpdate();
				            
				            if(numRowsAffected != 1) { ps.close(); return false; }
				            
				            final String update_prihod = "UPDATE invoice_parts SET rest = (rest  + ? - SaleTable.quantity) "
							        + " FROM (SELECT id_invoice_parts, quantity "
							        + "FROM sale_parts WHERE id = ? ) AS SaleTable "
							        + " WHERE SaleTable.id_invoice_parts = invoice_parts.id;";
							        
							 ps = WsConnect.getCurrentConnection().prepareStatement(update_prihod);
							        
							 ps.setDouble(1,  old_quantity);
							 
							 ps.setInt(2, d.id);
							            
							 @SuppressWarnings("unused")
							 int procesedRows = ps.executeUpdate();
							 
							 ps.close();
				            
		             	}
		             	else {
		             		
		             		int inserted_id = data.id;
		             		             		
		                    StringBuilder insertion_parts_b = new StringBuilder();
		                    		
		                    insertion_parts_b.append("INSERT INTO  sale_parts ( id_sale_invoice, \n");
		                    
		                    insertion_parts_b.append( " id_invoice_parts, name, quantity, rest, \n");
		                    
		                    insertion_parts_b.append( " id_units, vendor_code_2, info, cost, nds, req_quantity, costnds)\n");
		                    
		                    insertion_parts_b.append( "VALUES(" );
		                    
		                    insertion_parts_b.append( Integer.toString(inserted_id) );
		                    
		                    insertion_parts_b.append( ",\n");
		                    
		                    insertion_parts_b.append( Integer.toString(d.id_invoice_parts) );
		                    
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
		                    
		                    insertion_parts_b.append( d.vendor_code_2 );
		                    
		                    insertion_parts_b.append( "',\n");
		                    
		                    insertion_parts_b.append( "'" );
		                    
		                    insertion_parts_b.append(d.info );
		                    
		                    insertion_parts_b.append( "',");
		                    
		                    insertion_parts_b.append( Double.toString(d.cost) );
		                    
		                    insertion_parts_b.append( ",");
		                    
		                    insertion_parts_b.append( Double.toString(d.nds) );
		                    
		                    insertion_parts_b.append( ",");
		                    
		                    insertion_parts_b.append( Double.toString(d.req_quantity) );
		                    
		                    insertion_parts_b.append( ",");
		                    
		                    insertion_parts_b.append( Double.toString(d.costwithnds) );
		                    
		                    insertion_parts_b.append(  ");");
		    	            			
		    	            
		         	       PreparedStatement ps1 = WsConnect.getCurrentConnection().prepareStatement(insertion_parts_b.toString(),
		         	    		   Statement.RETURN_GENERATED_KEYS);
		    	            
		         	      if(ps1.executeUpdate() != 1) { 
		    	        	   
		    	        	   ps.close();
		    	        	   
		    	        	   ps1.close();
		    	        	   
		    	        	   return false; 
		    	        	 }
		         	      
		         	       int inserted_part_id = -1;
		  	            
		  	            ResultSet rs = ps1.getGeneratedKeys();
		  	            
		  	            if (rs.next()){
		  	            	
		  	            	inserted_part_id = rs.getInt(1);
		  	            }
		  	            
		    	       rs.close();
		    	          
		    	            
		    	           ps1.close();
		    	           
		    	           if(inserted_part_id != -1) {
		    	           
			    	            //change prihod data
			    	            final String update_prihod = "UPDATE invoice_parts SET rest = (rest  - SaleTable.quantity) "
								        + " FROM (SELECT id_invoice_parts, quantity "
								        + "FROM sale_parts WHERE id = ? ) AS SaleTable "
								        + " WHERE SaleTable.id_invoice_parts = invoice_parts.id;";
								        
								 ps = WsConnect.getCurrentConnection().prepareStatement(update_prihod);
					
								 ps.setInt(1, inserted_part_id);
								            
								 @SuppressWarnings("unused")
								int procesedRows = ps.executeUpdate();
								 
								 ps.close();
		    	           }
		             		
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
	
	public static int updateDeletePartsRashod(WsRashodData data, Vector<WsRashodPartData> vec) {
		
		 String exist_rows = "SELECT id FROM sale_parts WHERE id_sale_invoice = " + Integer.toString(data.id) + ";";
		   
		 WsConnect.get();
		 
		 int numDeleteRows = 0;
			
		 try {
			 
			Statement st = WsConnect.getCurrentConnection().createStatement();
			
			ResultSet rs = st.executeQuery(exist_rows);
			
			
			while(rs.next()) {
				
				boolean found = false;
				
				int id = rs.getInt(1);
				
				for(int i = 0; i < vec.size(); ++i) {
					
					WsRashodPartData d = vec.elementAt(i);
					
					if( d.id == id) { found = true; break; }
					
				}
				
				if(!found) {
					//return the rest
			        final String update_prihod = "UPDATE invoice_parts SET rest = (rest + SaleTable.quantity) "
					        + " FROM (SELECT id_invoice_parts, quantity "
					        + "FROM sale_parts WHERE id = ? ) AS SaleTable "
					        + " WHERE SaleTable.id_invoice_parts = invoice_parts.id;";
					        
					 PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(update_prihod);
					        
					 ps.setInt(1, id);
					            
					 @SuppressWarnings("unused")
					int procesedRows = ps.executeUpdate();
					
				    String delete_st = "DELETE FROM sale_parts WHERE id = ?;";
		  	  	     
		            ps = WsConnect.getCurrentConnection().prepareStatement(delete_st);
		            
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
			
			return -1;
		}
		   
		   
		   
		 return numDeleteRows;
		
		
	}
	
	public static boolean checkIsPartIdExists(int id) {
		
		 if (id == -1) { return false; }
		
		 String exist_rows = "SELECT id FROM sale_parts WHERE id = " + Integer.toString(id) + ";";
		   
		 WsConnect.get();
			
		 try {
			 
			Statement st = WsConnect.getCurrentConnection().createStatement();
			
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
	
	
	public static WsRashodData getRashodForId(int id) {
		
        Statement st;
        
        if(id == -1) return null;
       
		try {
			
			WsConnect.get();
			
			st = WsConnect.getCurrentConnection().createStatement();
			
	        if (st != null) {
	        	
	        	StringBuilder s_b = new StringBuilder();
	        			
	        	s_b.append("SELECT sale_invoices.id, sale_invoices.number, sale_invoices.date, sale_invoices.info,"
	        			+ " counterparties.name,  sale_invoices.id_counterparty, sale_invoices.people   FROM sale_invoices "
	        			+ "INNER JOIN counterparties ON counterparties.id = sale_invoices.id_counterparty  "
	        			+ "WHERE sale_invoices.id = ");
	        	
	        	s_b.append( Integer.toString(id));
	        	
	        	s_b.append( ";");
	        	
	        	ResultSet rs = st.executeQuery(s_b.toString());
	        	
	        	if (rs.next()){
	        		
	        		WsRashodData dt = new WsRashodData();
	            	
	                dt.id = rs.getInt(1);
	                
	                dt.number = rs.getString(2);
	                
	                dt.date = rs.getDate(3);
	                
	                dt.info = rs.getString(4);
	                
	                dt.agentName = rs.getString(5);
	                
	                dt.id_counterparty = rs.getInt(6);
	                
	                dt.people = rs.getInt(7);
	                
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
	
	public static int deleteRashod(int sale_invoice_id) {
		
		 String exist_rows = "SELECT id FROM sale_parts WHERE id_sale_invoice = " + Integer.toString(sale_invoice_id) + ";";
		   
		 WsConnect.get();
		 
		 int numDeleteRows = 0;
		 
		 int procesedRows = 0;
			
		 try {
			 
			Statement st = WsConnect.getCurrentConnection().createStatement();
			
			ResultSet rs = st.executeQuery(exist_rows);
			
			while(rs.next()) {
				
					int id = rs.getInt(1);
					
					//return the rest to the store
				     final String update_prihod = "UPDATE invoice_parts SET rest = (rest + SaleTable.quantity) "
						        + " FROM (SELECT id_invoice_parts, quantity "
						        + "FROM sale_parts WHERE id = ? ) AS SaleTable "
						        + " WHERE SaleTable.id_invoice_parts = invoice_parts.id;";
						        
				     PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(update_prihod);
						        
					 ps.setInt(1, id);
						            
					 procesedRows += ps.executeUpdate();
						
					 final String delete_st = "DELETE FROM sale_parts WHERE id = ?;";
			  	  	     
			         ps = WsConnect.getCurrentConnection().prepareStatement(delete_st);
			            
			         ps.setInt(1, id);
			            
			         numDeleteRows += ps.executeUpdate();
			            
			         ps.close();
		         
				}
				
				 rs.close();
				
				 final String delete_invoice = "DELETE FROM sale_invoices WHERE id = ?;";
	  	  	     
				 PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(delete_invoice );
			        
				 ps.setInt(1, sale_invoice_id);
					            
				 numDeleteRows += ps.executeUpdate();
				 
				 ps.close();
			
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		     
		return numDeleteRows + procesedRows;
			
	}
	
	
	public static int updateSkladPositionsForRashod(int id_sale_invoce) {
		
		PreparedStatement ps;
		
		int procesedRows = 0;
		 
		try {
			
			ps = WsConnect.getCurrentConnection().prepareStatement("SELECT id_sale_invoice, "
			 		+ "id_invoice_parts, quantity  FROM sale_parts WHERE id_sale_invoice = ?");
			
			ps.setInt(1, id_sale_invoce);
			
			ResultSet r = ps.executeQuery();
			
			while(r.next()) {
				
				int id_invoice_parts = r.getInt(2); 
				
				double quantity_rashod = r.getDouble(3);

				PreparedStatement ps1 = WsConnect.getCurrentConnection().prepareStatement("UPDATE invoice_parts "
						+ "SET rest = (rest - ?)  WHERE id = ?;");
				
				ps1.setDouble(1, quantity_rashod);
				
				ps1.setInt(2, id_invoice_parts);
				
				procesedRows += ps1.executeUpdate();
				
				ps1.close();
					
			}
	            
			ps.close();
			
			return procesedRows ;
			
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return -1;
		}	
	}
	
	
	private static  int findFirstKodIndex(Vector<WsPrihodPartData> sklad_rows, int kod) {
		
		 for(int j = 0; j < sklad_rows.size(); ++j) {
			 
			  WsPrihodPartData d_s = sklad_rows.elementAt(j); 
			  		  
			  if( WsUtils.isKodEqual(d_s.kod, kod)) { return j; }
			  
		 }
		 
		 return -1;
		
	}
	//returns inserted id
	public static Vector<WsRashodPartData> findSkladPositionsForRashod(Date dt, 
			Vector<WsRashodPartData> vec, boolean take_latest_positions, 
			Vector<String> not_enough_quantity_kods,
			boolean showNotEnoughMessage) {
		
		   int sort_type = 6;
			
		   if(take_latest_positions) { sort_type = 5; }
		
		   Vector<WsPrihodPartData> sklad_rows = 
				   WsSkladSqlStatements.getSkladListAvailableForDate(dt, sort_type, false);
		   
		   Vector<WsRashodPartData> vec_out = new Vector<WsRashodPartData>();
		   
		   StringBuilder s_message_b = new StringBuilder("");
		  
		   for(int i = 0; i < vec.size(); ++i) {
			   
			   WsRashodPartData d_r =  vec.elementAt(i);
			   
			   int sklad_kod_first_index = findFirstKodIndex(sklad_rows, d_r.kod);
			   
			   if(sklad_kod_first_index == -1) {
				   
				   s_message_b.append( getGuiStrs("Nopositionforthekod") );
				   
				   s_message_b.append(  String.valueOf(d_r.kod));
				   
				   s_message_b.append(  ":");
				   
				   s_message_b.append(  String.valueOf(d_r.quantity));
				   
				   s_message_b.append(  "  \n");
			   
				   not_enough_quantity_kods.add( String.valueOf(d_r.kod) +":" + 
						   String.format("%.4f",d_r.quantity) + "; ");
				   
				   continue; 
			   }
			   
			   int j = sklad_kod_first_index;
			   
			   WsPrihodPartData d_s = sklad_rows.elementAt(j);
			   
			   double rest = d_r.quantity;
			   
			   double req_quantity = -1.0;
		
			   while(rest > 0.0 &&  WsUtils.isKodEqual(d_s.kod, d_r.kod)) {

				   WsRashodPartData d_r_1 = new WsRashodPartData( d_r );
				   
				   if(req_quantity < 0.0) { //to call only once in this loop
					   
					   d_r_1.req_quantity = rest;
					   
					   req_quantity = rest;
				   }
				   
				   if(d_s.rest >= rest) {
					   
					   d_r_1.id_invoice_parts = d_s.id;
					   
					   d_r_1.quantity = rest;
					   
					   d_r_1.rest = d_s.rest;
					   
					   d_r_1.cost = d_s.cost;
					   
					   d_r_1.costwithnds = d_s.costwithnds;
					   
					   d_r_1.nds = d_s.nds;
					   
					   vec_out.add(d_r_1);
					   
					   rest = 0.0;
				   }
				   else {
					   
					   d_r_1.id_invoice_parts = d_s.id;
					   
					   d_r_1.quantity = d_s.rest;
					   
					   d_r_1.rest = d_s.rest;
					   
					   d_r_1.cost = d_s.cost;
					   
					   d_r_1.costwithnds = d_s.costwithnds;
					   
					   d_r_1.nds = d_s.nds;
					   
					   vec_out.add(d_r_1);
					   
					   rest -= d_s.rest;
					   
					   if((j + 1) < sklad_rows.size() ) {
					   
						   d_s = sklad_rows.elementAt(++j);
					   }
					   else {
						   //the sklad list is over
						   break;
					   } 
				   }
			   }
			   
			   if(rest > 0) { 
				   
				   s_message_b.append(  getGuiStrs("Notenoughquantitykod") );
				   
				   s_message_b.append( " ");
				   
				   s_message_b.append(  String.valueOf(d_r.kod) );
				   
				   s_message_b.append(  " : " );
				   
				   s_message_b.append(  String.valueOf(rest) );
				   
				   s_message_b.append( "<br/>");
				   
				   not_enough_quantity_kods.add( String.valueOf(d_r.kod) + ":" + String.format("%.4f",rest) + "; ");
			   }
		   }
		   
		   String s_message = s_message_b.toString();
		   
		   if(showNotEnoughMessage && !s_message.isEmpty()) {
			   
			  WsUtils.showMessageDialog("<html>" + getGuiStrs("NotenoughquantitykodM") + "</html>");
			  
		   }
		   
		   return vec_out;
		
	}
	
	
	
	
	public static boolean updateOneRowExistingRashod( WsRashodPartData d) {
		
		
	   PreparedStatement ps = null;
	   
	   try { 		             		
           StringBuilder insertion_parts_b = new StringBuilder();
        		   
           insertion_parts_b.append(  "INSERT INTO  sale_parts ( id_sale_invoice, \n");
           
           insertion_parts_b.append(  " id_invoice_parts, name, quantity, rest, \n");
           
           insertion_parts_b.append(  " id_units, vendor_code_2, info, cost, nds)\n");
           
           insertion_parts_b.append(  "VALUES(" );
           
           insertion_parts_b.append( Integer.toString(d.id_sale_invoice) );
           
           insertion_parts_b.append(  ",\n");
           
           insertion_parts_b.append( Integer.toString(d.id_invoice_parts) );
           
           insertion_parts_b.append( ",\n");
           
           insertion_parts_b.append( "'" );
           
           insertion_parts_b.append( d.name );
           
           insertion_parts_b.append( "',\n");
           
           insertion_parts_b.append( Double.toString(d.quantity) );
           
           insertion_parts_b.append( ",\n");
           
           insertion_parts_b.append( Double.toString(d.rest) );
        	
           insertion_parts_b.append( ",\n");
        	 
           insertion_parts_b.append( Integer.toString(d.id_units) );
           
           insertion_parts_b.append( ",\n");
           
           insertion_parts_b.append( "'" );
           
           insertion_parts_b.append( d.vendor_code_2 );
           
           insertion_parts_b.append( "',\n");
           
           insertion_parts_b.append( "'" );
           
           insertion_parts_b.append(d.info );
           
           insertion_parts_b.append( "',");
           
           insertion_parts_b.append( Double.toString(d.cost) );
           
           insertion_parts_b.append( ",");
           
           insertion_parts_b.append( Double.toString(d.nds) );
           
           insertion_parts_b.append( ");");
	    	            			
	    	            
           PreparedStatement ps1 = WsConnect.getCurrentConnection().prepareStatement(insertion_parts_b.toString(),
        		   Statement.RETURN_GENERATED_KEYS);
	    	            
 	      if(ps1.executeUpdate() != 1) { 
        	   
        	   ps1.close();
        	   
        	   return false; 
           }
 	      
 	       int inserted_part_id = -1;
	  	            
            ResultSet rs = ps1.getGeneratedKeys();
            
            if (rs.next()){
            	
            	inserted_part_id = rs.getInt(1);
            }
            
	       rs.close();
	             
           ps1.close();
           
           if(inserted_part_id != -1) {
           
	           //change prihod data
	           final String update_prihod = "UPDATE invoice_parts SET rest = (rest  - SaleTable.quantity) "
				        + " FROM (SELECT id_invoice_parts, quantity "
				        + "FROM sale_parts WHERE id = ? ) AS SaleTable "
				        + " WHERE SaleTable.id_invoice_parts = invoice_parts.id;";
				        
				 ps = WsConnect.getCurrentConnection().prepareStatement(update_prihod);
	
				 ps.setInt(1, inserted_part_id);
				            
				 @SuppressWarnings("unused")
				 int procesedRows = ps.executeUpdate();
				 
				 ps.close();
           }
	             		
	       return true;
	            
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		
		}
		
		return false;
		
	}
	
	
	
	public static Vector<WsRashodPartData> getRashodPartSumList(java.sql.Date start, 
			java.sql.Date end, int kod_id) {
		
		Vector<WsRashodPartData> vec = new Vector<WsRashodPartData>();
	   
	    boolean data_is_valid = start != null && end != null && (start.before(end) || start.equals(end));
	     
	    
	    if(kod_id == -1 || !data_is_valid) {
	    	
	    	return vec;
	    }
	   
		try {
			
			WsConnect.get();
			
	        StringBuilder s_b = new StringBuilder(); 
	        		
	        s_b.append("SELECT  part_types.id, part_types.kod, part_types.name, SUM(sale_parts.quantity) FROM sale_parts  "
	        			+ "INNER JOIN   sale_invoices ON  sale_parts.id_sale_invoice = sale_invoices.id  "
	        			+ "INNER JOIN  invoice_parts ON  sale_parts.id_invoice_parts = invoice_parts.id  "
	        			+ "INNER JOIN  part_types ON part_types.id = invoice_parts.id_part_type  "
	        			+ " WHERE part_types.id = ");
	        
	        s_b.append( Integer.toString(kod_id) );
	        
	        s_b.append(" ");
	        
	         s_b.append( " AND sale_invoices.date BETWEEN ? AND ?  GROUP BY part_types.id;"); 
	        		
	        PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(s_b.toString());
	        		
	        ps.setDate(1, start);
		        	
		    ps.setDate(2, end);
	        	
	        ResultSet rs =  ps.executeQuery();
	        	
        	while(rs.next()) {
        		
        		WsRashodPartData d = new WsRashodPartData();
        		
        		d.id = rs.getInt(1);
        		
        		d.kod = rs.getInt(2);
        		
        		d.name = rs.getString(3);
        		
        		d.quantity = rs.getDouble(4);
        		      		
        		vec.add(d);
        		      		
        	}
        	
        	rs.close();
        	
        	ps.close();
        	
        	return vec;
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
	   
	
		return vec;
	}
	
	
	public static boolean isNewDateForUsedRashodValid(int sale_invoice_id, Date dt) {
		
		if (sale_invoice_id  == -1 || dt == null) return false;
	       
		try {
			
        	final String st = "SELECT invoices.date  FROM sale_parts "
        			+ "INNER JOIN invoice_parts ON sale_parts.id_invoice_parts  = invoice_parts.id "
        			+ "INNER JOIN invoices ON invoices.id  = invoice_parts.id_invoice "
        			+ "WHERE sale_parts.id_sale_invoice = ?;";
       	  	          
        	WsConnect.get();
        	
			PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(st);
        
            ps.setInt(1, sale_invoice_id);
     
            ResultSet rs = ps.executeQuery();
           
            while( rs.next() ) {
            	
            	if( dt.compareTo(rs.getDate(1)) < 0 ) { 
            		
            		ps.close();
            		
            		rs.close();
            		
            		return false;
            		
            	}
            	
            } 
            
            ps.close();
        	
        	rs.close();
           
        	return  true;
	          
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return false;
		}
		
	}
	
	public static WsPair getPeopleFromRashod(Vector<WsRashodData> rashod_vec, boolean usePeople) {
		
		 WsPair res = new WsPair();
		 
		 res.flag = usePeople ? 1 : 0;
		
	    double sum_people = 0.0;
	    
	    if(usePeople) {
	    	
		    for(WsRashodData r: rashod_vec) {
		    	
		    	sum_people += r.people;
		    }
		    
		    if(sum_people < 0.0001) { res.flag = 0; }
	    }
	   
	    res.value = sum_people;
	    
	    return res;
		
	}
	
	public static String processAllRestPrihodIntoRashod(int kod, Date end_d , 
			boolean usePeople,  double sum_people_for_rest,
			double rest, Vector<WsRashodData> rashod_vec, Vector<WsPrihodData> prihod_vec,
			 WsPartType dType, WsUnitData kgUnit) {
		
	     //1st all the rest before the satrt date into rashod
		
	    double quantity = 0.0;
	    
	    if(usePeople) {
	    	
	    	quantity = rest/sum_people_for_rest;
	    }
	    else {
	    	
	    	quantity = rest/rashod_vec.size();
	    }
	      
	    addAdditionalRashod(kod, rashod_vec,quantity,
				dType, kgUnit, usePeople);
	    
	    //2nd round - all prihod during the period into rashod

	    for(WsPrihodData d: prihod_vec) {
	    	
	    	//get rashods which start from the day of the prihod
	    	 Vector<WsRashodData> rashod_vec2 = getRashodList(d.date, end_d);
	    	
	    	 if(rashod_vec2.isEmpty()) { return null; }
	    	 
	    	 WsPair resp2 = getPeopleFromRashod(rashod_vec2, usePeople);
	 		
	 		 usePeople = resp2.flag == 1 ? true : false ;
	 		
	 	     double sum_people = resp2.value;
	    	
	 	     if(usePeople) {
		    	
		    	quantity = d.quantity/sum_people;
		     }
		     else {
		    	
		    	quantity = d.quantity/rashod_vec2.size();
		     }
	 	    
	 	     addAdditionalRashod(kod, rashod_vec2,quantity,
					dType, kgUnit, usePeople);
	    	
	    }

		return null;
	
	}
	
	public static String processRequiredRestPrihodIntoRashod(int kod, Date end_d , 
			boolean usePeople,  double sum_people_for_all_period,
			double rest, double prihod_sum, Vector<WsRashodData> rashod_vec, 
			Vector<WsPrihodData> prihod_vec,
			 WsPartType dType, WsUnitData kgUnit, double req_quantity) {
		
		
	    //1st  process the rest only
		
		double quantity_to_process = 0.0;
		
		if(req_quantity < rest)  {   quantity_to_process = req_quantity;    }
		else { quantity_to_process = rest;  }

	    double quantity;
	    
	    if(usePeople) {
	    	
	    	quantity = quantity_to_process/sum_people_for_all_period;
	    }
	    else {
	    	
	    	quantity = quantity_to_process/rashod_vec.size();
	    }
	      
	    addAdditionalRashod(kod, rashod_vec,quantity,
				dType,kgUnit, usePeople);
	    
	    double rest_to_process = req_quantity - quantity_to_process;
	    
	    if(rest_to_process <= 0.001) { return null; }
	    
	    //2nd round - working with prihod in the period
	    //the rashod can be performed starting with the date of prihod;
	    //that is why we do it separately for every prihod invoice
	 
	    for(WsPrihodData d: prihod_vec) {
	    	
	    	if(rest_to_process < d.quantity) { quantity_to_process = rest_to_process; }
	    	else { quantity_to_process =  d.quantity;   } 
	    	
	    	//get rashods which start from the day of the prihod
	    	 Vector<WsRashodData> rashod_vec2 = getRashodList(d.date, end_d);
	    	
	    	 if(rashod_vec2.isEmpty()) { return null; }
	    	 
	    	 WsPair resp2 = getPeopleFromRashod(rashod_vec2, usePeople);
	 		
	 		 usePeople = resp2.flag == 1 ? true : false ;
	 		
	 	     double sum_people = resp2.value;
	    	
	 	     if(usePeople) {
		    	
		    	quantity = quantity_to_process/sum_people;
		    }
		    else {
		    	
		    	quantity = quantity_to_process/rashod_vec2.size();
		    }
	 	    
	 	    addAdditionalRashod(kod, rashod_vec2, quantity,
					dType, kgUnit, usePeople);
	 	   
	 	    rest_to_process -= quantity_to_process;
	 	   
	 	    if(rest_to_process < 0.001) { return null; }
	    	
	    }
		
	    return null;
	}
	
	public static String transferAllPrihodIntoRashod(int kod,  Date start_d, Date end_d , 
			boolean usPl, double req_quantity) {
		
		WsPartType dType = WsUtilSqlStatements.getPartTypeForKod(kod);
		
		WsUnitData kgUnit = WsUtilSqlStatements.getKgUnit();
		 
	    Vector<WsRashodData> rashod_vec = getRashodList(start_d, end_d);
	    
		if(rashod_vec.isEmpty()) {
				
			return "<html>" + getMessagesStrs("noRashodForAutomaticPrihodMessage0") + "<br/>"
					+ getMessagesStrs("noRashodForAutomaticPrihodMessage1") + "<html>";
			
		}
		
		WsPair resp = getPeopleFromRashod(rashod_vec, usPl);
		
		boolean usePeople = resp.flag == 1 ? true : false ;
		
	    double sum_people = resp.value;
       
	    Vector<WsPrihodData> vec =  WsPrihodSqlStatements.getAvailableSumPrihodForKod(kod,  start_d, end_d );
	      
	    double prihod_sum = 0.0;
	    
	    for(WsPrihodData d: vec) {   prihod_sum += d.quantity; }
	    
	    java.sql.Date date_before_start = WsUtils.sqlDatePlusDays(start_d, -1);
	    
	    double rest = WsReportsSqlStatements.getAvailableRestForPartForDate(kod,  date_before_start);
	      
	    if((prihod_sum + rest) < 0.0) { return getMessagesStrs("noPrihodForAutomaticPrihodMessage"); }
	      
		if(req_quantity > (prihod_sum + rest)) {
			
			return getMessagesStrs("autoRashExceeds1") + " " + String.valueOf(req_quantity) +
					getMessagesStrs("autoRashExceeds2") + " " + String.valueOf(prihod_sum + rest);
		}
		
		
		if( req_quantity < 0) {//process all the rest and prihod
			
			return processAllRestPrihodIntoRashod(kod, end_d , 
					usePeople,  sum_people, rest, rashod_vec, vec,
					dType, kgUnit);
			
		}
		else {
			
			return processRequiredRestPrihodIntoRashod(kod, end_d, 
					usePeople,  sum_people,
					rest, prihod_sum, rashod_vec, vec,
					dType, kgUnit, req_quantity);
		}

	}
	
	//return for every position of the good with kod in vector, according to quantity
	static private void addAdditionalRashod(int kod, Vector<WsRashodData> rashod_vec, double quantity,
			WsPartType dType, WsUnitData kgUnit, boolean usePeople) {
		
		Vector<WsReturnedPartData> back_vec = new Vector<WsReturnedPartData>();
	      
	    for(WsRashodData r: rashod_vec) {
	    	              
	    	  WsReturnedPartData d = new WsReturnedPartData();
	    	  
	    	  d.id_part_type = dType.id;
	    			  
	    	  if(usePeople) {
	    		  
	  	    	d.quantity = quantity*r.people;
	  	    	
	    	  }
	    	  else {
	    		  
	    		  d.quantity = quantity;  
	    	  }
	    	  
	    	  if( d.quantity > 0.0001) {
	    	  
		    	  d.name = dType.name;
		    	  
		    	  d.id_units = kgUnit.id;
		    			  
		    	  d.kod = kod;
		    	  
		    	  d.id_sale_invoice = r.id;
		    	  
		    	  d.sale_invoice_date = r.date;
		    	  
		    	  d.units_name = kgUnit.name;
		    	  
		    	  back_vec.add(d);
	    	  }
	    	  
	      }
	      
	      WsReturnRashodSqlStatements.getRashodBack(back_vec);
		
	}
	
	//the output vector contains only 2 elements - min and max date
	public static Vector<java.sql.Date> getRashodMinMaxDate() {
		

		try {
			
			WsConnect.get();

	        final String s = "SELECT MIN(sale_invoices.date), MAX(sale_invoices.date)  FROM sale_invoices ";
	        	     	
	        PreparedStatement ps = WsConnect.getCurrentConnection().prepareStatement(s);
	        	
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
	
	public static void updateRashodPricesForIdPart( WsContractPriceData price_data, int id_invoice_part) {
		
		if(id_invoice_part == -1 || price_data == null) { return; }
		
		WsConnect.get();
		
		Connection conn = WsConnect.getCurrentConnection();
		
 		try {

			Vector<WsRashodPartData> vec = getRashodPartsListForPrihodId(id_invoice_part, 0);
			
			PreparedStatement ps = null;
			
			for(WsRashodPartData d : vec ) {
				
				d.costwithnds = price_data.costwnds;
				
				d.cost = price_data.cost;
				
				d.nds = price_data.nds;
				
	     		String update_parts = "UPDATE sale_parts SET cost = ?, nds = ?, costnds = ? WHERE id = ?;";
	     		
				ps = conn.prepareStatement(update_parts);
			
	            ps.setDouble(1, d.cost );
	            
	            ps.setDouble(2, d.nds );
	            
	            ps.setDouble(3, d.costwithnds);
	            
	            ps.setInt(4, d.id);
	            
	            if(ps.executeUpdate() != 1) { 

	            }
	            
	            ps.close();
					
			}
		
 		} catch (SQLException e) {

			e.printStackTrace();
		}
	
	}
	
	
	public static Vector<WsRashodPartData> findSkladPositionsForRashodThreshold(Date dt, 
			int id_contract, int id_part, double threshold) {
		
		   Vector<WsPrihodPartData> sklad_rows = 
				   WsSkladSqlStatements.getSkladListAvailableForDateThreshold(dt, 
					id_contract, id_part, threshold);
		   
		   Vector<WsRashodPartData> vec_out = new Vector<WsRashodPartData>();
		   

		   for(int i = 0; i < sklad_rows.size(); ++i) {
			   
			   WsRashodPartData d_r =  new WsRashodPartData();
			   
			   WsPrihodPartData d_s = sklad_rows.elementAt(i);
			   
			   d_r.kod = d_s.kod;
			   
			   d_r.vendor_code_2 = d_s.vendorcode2;
			   
			   d_r.id_units = d_s.id_units;
			   	   
			   d_r.id_invoice_parts = d_s.id;
					   
			   d_r.quantity = d_s.rest;
					   
			   d_r.rest = d_s.rest;
					   
			   d_r.cost = d_s.cost;
					   
			   d_r.costwithnds = d_s.costwithnds;
					   
			   d_r.nds = d_s.nds;
			   
			   d_r.name = d_s.name;
			   
			   d_r.units_name = d_s.units_name;
					   
			   vec_out.add(d_r);
   
		   }
			   	   
		   return vec_out;
		
	}
	

	public static int updateRestToZero(int id_part) {
		
		PreparedStatement ps;
		
		int procesedRows = 0;
		 
		try {
			
			WsTransactions.beginTransaction(null);
			
			ps = WsConnect.getCurrentConnection().prepareStatement("SELECT rest  FROM invoice_parts WHERE id = ?");
			
			ps.setInt(1, id_part);
			
			ResultSet r = ps.executeQuery();
			
			double rest = 0.0;
			
			if(r.next()) {
				
				rest = r.getDouble(1); 
				
				r.close();  ps.close();
					
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
				r.close();  ps.close();
				
				return -1;
			}
			
			ps = WsConnect.getCurrentConnection().prepareStatement("SELECT id, quantity  FROM sale_parts WHERE id_invoice_parts = ? ORDER BY id DESC");
			
			ps.setInt(1, id_part);
			
			r = ps.executeQuery();
			
			int id_sale_last = -1;
			
			double quantity = 0.0;
			
			if(r.next()) {
				
				id_sale_last = r.getInt(1); 
				
				quantity = r.getDouble(2);
				
				r.close();  ps.close();
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
				r.close();  ps.close();
				
				return -1; 
			}

			PreparedStatement ps1 = WsConnect.getCurrentConnection().prepareStatement("UPDATE invoice_parts "
					+ "SET rest = 0.0  WHERE id = ?;");
			
			ps1.setInt(1, id_part);
			
			procesedRows += ps1.executeUpdate();
			
			ps1.close();
			
			ps1 = WsConnect.getCurrentConnection().prepareStatement("UPDATE sale_parts "
					+ "SET quantity = ?  WHERE id = ?;");
			
			ps1.setDouble(1, quantity + rest);
			
			ps1.setInt(2, id_sale_last);
			
			procesedRows += ps1.executeUpdate();
	            
			WsTransactions.commitTransaction(null);
			
			return procesedRows ;
			
		} catch (SQLException e) {
			
			WsTransactions.rollbackTransaction(null);
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return -1;
		}	
	}
	
	
	public static int updateRestToZeroForAllRashod(int id_part) {
		
		PreparedStatement ps;
		
		int procesedRows = 0;
		 
		try {
			
			WsTransactions.beginTransaction(null);
			
			ps = WsConnect.getCurrentConnection().prepareStatement("SELECT rest  FROM invoice_parts WHERE id = ?");
			
			ps.setInt(1, id_part);
			
			ResultSet r = ps.executeQuery();
			
			double rest = 0.0;
			
			if(r.next()) {
				
				rest = r.getDouble(1); 
				
				r.close();  ps.close();
					
			}
			else {
				
				WsTransactions.rollbackTransaction(null);
				
				r.close();  ps.close();
				
				return -1;
			}
			
			ps = WsConnect.getCurrentConnection().prepareStatement("SELECT id, quantity  FROM sale_parts WHERE id_invoice_parts = ? ORDER BY id DESC");
			
			ps.setInt(1, id_part);
			
			r = ps.executeQuery();
						
			Vector<WsPair> vec_rash =  new Vector<WsPair>();
			
			while(r.next()) {
				
				vec_rash.add(new WsPair( null, r.getDouble(2), r.getInt(1)));
				
				r.close();  ps.close();
			}
			
			r.close();  ps.close();
			
			if(vec_rash.isEmpty()) {
				
				WsTransactions.rollbackTransaction(null);
				
				return -1;
				
			}
	
			double delta = rest/vec_rash.size();
			
			PreparedStatement ps1 = null;
			
			ps1 = WsConnect.getCurrentConnection().prepareStatement("UPDATE invoice_parts "
					+ "SET rest = 0.0  WHERE id = ?;");
			
			ps1.setInt(1, id_part);
			
			procesedRows += ps1.executeUpdate();
			
			ps1.close();
			
			if(procesedRows == 0) {
				
				WsTransactions.rollbackTransaction(null);
				
				return -1;
			}
			
			for(WsPair p : vec_rash) {

				ps1 = WsConnect.getCurrentConnection().prepareStatement("UPDATE sale_parts "
						+ "SET quantity = ?  WHERE id = ?;");
				
				ps1.setDouble(1, p.value + delta);
				
				ps1.setInt(2, p.flag);
				
				procesedRows += ps1.executeUpdate();
			
			}
	            
			WsTransactions.commitTransaction(null);
			
			return procesedRows ;
			
		} catch (SQLException e) {
			
			WsTransactions.rollbackTransaction(null);
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return -1;
		}	
	}
}
