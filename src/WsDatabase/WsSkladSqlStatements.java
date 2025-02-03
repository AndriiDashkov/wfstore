
package WsDatabase;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsPrihodRashodMoveData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsSkladSqlStatements {
	
	public static Vector<WsPrihodPartData>  getSkladList(int sort_flag) {
		
		WSConnect.get();
		
		return getSkladList(WSConnect.getCurrentConnection(), sort_flag);
	
	}
	
	//0 - sort by date 1 - sort by part_type_name, 2- sort by name, 3- sort by kod
	public static Vector<WsPrihodPartData>  getSkladList(Connection conn, int sort_flag) {
		
        Statement st;
       
		try {
			
			Vector<WsPrihodPartData>  vec = new Vector<WsPrihodPartData> ();
	
			st = conn.createStatement();
			
	        if (st != null) {
	        	
	        	StringBuilder s_b =  new StringBuilder();
	        			
	        	s_b.append("SELECT invoice_parts.id, invoices.date, part_types.name, "
	        			+ "invoice_parts.name, invoice_parts.rest, "
	        			+ "units.name, invoice_parts.vendor_code_2, invoice_parts.info,"
	        			+ "id_part_type, id_units, id_invoice, invoice_parts.cost,"
	        			+ " invoice_parts.nds, part_types.kod, contracts.name, contracts.date, invoice_parts.costnds FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN contracts ON invoices.id_contract = contracts.id"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE invoice_parts.rest > 0.0001 ORDER BY ");
	        	
	        	switch(sort_flag) {
	        	
	        	
	        		case 0: s_b.append(" invoices.date;"); break;
	        		
	        		case 1: s_b.append(" part_types.kod;"); break;
	        		
	        		case 2: s_b.append(" invoice_parts.name;"); break;
	        		
	        		case 3: s_b.append(" part_types.kod;"); break;
	        		
	        		case 4: s_b.append(" contracts.date;"); break;
	        		
	        		default : s_b.append(" invoices.date;");
	        	};
	        	
	       
	        	ResultSet rs =  st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsPrihodPartData d = new WsPrihodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.date = rs.getDate(2);
	        		
	        		d.part_type_name = rs.getString(3);
	        		
	        		d.name = rs.getString(4);
	        		
	        		d.rest = rs.getDouble(5);
	        		
	        		d.units_name = rs.getString(6);
	        		
	        		d.vendorcode2 = rs.getString(7);
	        		
	        		d.info = rs.getString(8);
	        		
	        		d.id_part_type = rs.getInt(9);
	        		
	        		d.id_units = rs.getInt(10);
	        		
	        		d.id_invoice =  rs.getInt(11);
	        		
	        		d.cost = rs.getDouble(12);
	        		
	        		d.nds = rs.getDouble(13);
	        		
	        		d.kod = rs.getInt(14);
	        		
	        		d.contract_name = rs.getString(15);
	        		
	        		d.contract_date = rs.getDate(16);
	        		
	        		d.costwithnds = rs.getDouble(17);
	        		
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
	
	public static Vector<WsPrihodPartData>  getSkladListGroupKod(int sort_flag) {
		
		WSConnect.get();
					
		return getSkladListGroupKod(WSConnect.getCurrentConnection(), sort_flag);
		
	}
	public static Vector<WsPrihodPartData>  getSkladListGroupKod(Connection conn, int sort_flag) {
		
        Statement st;
       
		try {
			
			Vector<WsPrihodPartData>  vec = new Vector<WsPrihodPartData> ();

			st = conn.createStatement();
			
	        if (st != null) {
	        	
	        	StringBuilder s_b =  new StringBuilder();
	        	
	        	s_b.append("SELECT invoice_parts.id, invoices.date, part_types.name, "
	        			+ "invoice_parts.name, SUM(invoice_parts.rest), "
	        			+ "units.name, invoice_parts.vendor_code_2, invoice_parts.info,"
	        			+ "id_part_type, id_units, id_invoice, invoice_parts.cost,"
	        			+ " invoice_parts.nds, part_types.kod, invoice_parts.costnds FROM invoice_parts"
	        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
	        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
	        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
	        			+ " WHERE invoice_parts.rest > 0.00001 GROUP BY part_types.kod ORDER BY ");
	        	
	        	
	        	switch(sort_flag) {
	        	
	        		case 0: s_b.append(" invoices.date;"); break;
	        		
	        		case 1: s_b.append(" part_types.kod;"); break;
	        		
	        		case 2: s_b.append(" invoice_parts.name;"); break;
	        		
	        		case 3: s_b.append(" part_types.kod;"); break;
	        		
	        		default : s_b.append(" invoices.date;");
	        	};

	       
	        	ResultSet rs =  st.executeQuery(s_b.toString());
	        	
	        	while(rs.next()) {
	        		
	        		WsPrihodPartData d = new WsPrihodPartData();
	        		
	        		d.id = rs.getInt(1);
	        		
	        		d.date = rs.getDate(2);
	        		
	        		d.part_type_name = rs.getString(3);
	        		
	        		d.name = rs.getString(4);
	        		
	        		d.rest = rs.getDouble(5);
	        		
	        		d.units_name = rs.getString(6);
	        		
	        		d.vendorcode2 = rs.getString(7);
	        		
	        		d.info = rs.getString(8);
	        		
	        		d.id_part_type = rs.getInt(9);
	        		
	        		d.id_units = rs.getInt(10);
	        		
	        		d.id_invoice =  rs.getInt(11);
	        		
	        		d.cost = rs.getDouble(12);
	        		
	        		d.nds = rs.getDouble(13);
	        		
	        		d.kod = rs.getInt(14);
	        		
	        		d.contract_name = "--";
	        		
	        		d.costwithnds = rs.getDouble(15);
	        		
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

	public static  Vector<WsPrihodPartData> getSkladListAvailableForOrders(String vendorCode2) {
		
        PreparedStatement ps;
        
        Vector<WsPrihodPartData> vp = new Vector<WsPrihodPartData>();
       
		try {
			
			WSConnect.get();
			
        	final String s = "SELECT invoice_parts.id, invoice_parts.quantity,"
        			+ " COALESCE(SUM(completed_orders.quantity),0.0) AS completed_sum FROM invoice_parts"
        			+ " LEFT JOIN completed_orders ON completed_orders.id_invoice_parts = invoice_parts.id "
        			+ "WHERE  invoice_parts.vendor_code_2 = ? GROUP BY invoice_parts.id "
        			+ "HAVING abs(invoice_parts.quantity - completed_sum) > 0.0001;";
        	
        	
        	ps = WSConnect.getCurrentConnection().prepareStatement(s);
			
        	ps.setString(1, vendorCode2);
	       
	        ResultSet  r = ps.executeQuery();
	        
	        while(r.next()) {
	        	
	        	WsPrihodPartData dt = new WsPrihodPartData();
	        	
	        	dt.id = r.getInt(1);

	        	dt.rest = r.getDouble(2) - r.getDouble(3);
	        	
	        	if(dt.rest < 0.0) { dt.rest = 0.0;}
	        	
	        	vp.add(dt);
	        	
	        }
	        
	        r.close();
	        
	        ps.close();
	        
	        return vp;
	        
	        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}

		return null;

	}
	
	//0 - sort by date 1 - sort by part_type_name, 2- sort by name, 3- sort by kod
	public static Vector<WsPrihodPartData>  getSkladListAvailableForDate(Date dt, int sort_flag, boolean groupBykod) {
		
		Vector<WsPrihodPartData>  vec = new Vector<WsPrihodPartData> ();
		
		try {
			
			WSConnect.get();

        	StringBuilder s_b = new StringBuilder(); 
        			
        	 s_b.append("SELECT invoice_parts.id, invoices.date, part_types.name, ");
        			 s_b.append("invoice_parts.name, " );

			if(groupBykod) {
				
				s_b.append(" SUM(invoice_parts.rest), ");
			}
			else {
				
				s_b.append(" invoice_parts.rest, ");
			}
        			 	 
			s_b.append("units.name, invoice_parts.vendor_code_2, invoice_parts.info,"
        			+ "invoice_parts.id_part_type,invoice_parts.id_units, id_invoice, invoice_parts.cost,"
        			+ " invoice_parts.nds, part_types.kod, contracts.name, contracts.date, invoice_parts.costnds "
        			+ "  FROM invoice_parts"
        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
        			+ " INNER JOIN contracts ON invoices.id_contract = contracts.id"
        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
        			+ " WHERE invoices.date <= ? AND invoice_parts.rest > 0.0001 ");
        	
        	if(groupBykod) {
        		
        		s_b.append(" GROUP BY part_types.kod ");
        	}
        	
        	switch(sort_flag) {
        	
        		case 0: s_b.append(" ORDER BY invoices.date;"); break;
        		
        		case 1: s_b.append("  ORDER BY part_types.kod;"); break;
        		
        		case 2: s_b.append("  ORDER BY invoice_parts.name;"); break;
        		
        		case 3: s_b.append("   ORDER BY part_types.kod;"); break;
        		
        		case 4: s_b.append("   ORDER BY part_types.kod,invoice_parts.id ;"); break;
        		
        		case 5: s_b.append("   ORDER BY part_types.kod, invoices.date DESC, invoice_parts.id DESC;"); break;
        		
        		case 6: s_b.append("   ORDER BY part_types.kod, invoices.date, invoice_parts.id;"); break;
        		
        		default : s_b.append(" ORDER BY  invoices.date;");
        	};
        	
        	PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(s_b.toString());
        	
        	ps.setDate(1, dt);
        	
        	ResultSet rs =  ps.executeQuery();
        	
        	while(rs.next()) {
        		
        		WsPrihodPartData d = new WsPrihodPartData();
        		
        		d.id = rs.getInt(1);
        		
        		d.date = rs.getDate(2);
        		
        		d.part_type_name = rs.getString(3);
        		
        		d.name = rs.getString(4);
        		
        		d.rest =  rs.getDouble(5);
        		
        		d.units_name = rs.getString(6);
        		
        		d.vendorcode2 = rs.getString(7);
        		
        		d.info = rs.getString(8);
        		
        		d.id_part_type = rs.getInt(9);
        		
        		d.id_units = rs.getInt(10);
        		
        		d.id_invoice =  rs.getInt(11);
        		
        		d.cost = rs.getDouble(12);
        		
        		d.nds = rs.getDouble(13);
        		
        		d.kod = rs.getInt(14);
        		
        		d.contract_name = rs.getString(15);
        		
        		d.contract_date =  rs.getDate(16);
        		
        		d.costwithnds = rs.getDouble(17);
        		
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
	
	
	//0 - sort by date 1 - sort by part_type_name, 2- sort by name, 3- sort by kod
	public static Vector<WsPrihodRashodMoveData>  getSkladCheckMove(java.sql.Date startDate, java.sql.Date endDate, int kod) {
		
		Vector<WsPrihodRashodMoveData>  vec = new Vector<WsPrihodRashodMoveData> ();
		
		try {
			
			WSConnect.get();
		
        	final String s = "SELECT invoice_parts.id, invoices.date, part_types.kod,  invoice_parts.quantity,"
        			+ " invoice_parts.rest, sale_invoices.date, "
        			+ " sale_parts.quantity, invoices.number, sale_invoices.number"
        			+ " FROM invoice_parts"
        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type  "
        			+ " LEFT JOIN  sale_parts ON sale_parts.id_invoice_parts = invoice_parts.id"
        			+ " LEFT JOIN  sale_invoices ON  sale_parts.id_sale_invoice = sale_invoices.id"
        			+ " WHERE invoices.date BETWEEN ? AND ? AND part_types.id = ?"
        			+ " ORDER BY  invoices.date, invoice_parts.id, part_types.kod;";

        	 PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(s);
        	 
        	 ps.setDate(1, startDate);
        	 
        	 ps.setDate(2, endDate);
        	 
        	 ps.setInt(3, kod);
        	
        	 ResultSet rs =  ps.executeQuery();
        	
        	 while(rs.next()) {
        		
        		WsPrihodRashodMoveData d = new WsPrihodRashodMoveData();
        		
        		d.id_invoice_part = rs.getInt(1);
        		
        		d.date_in = rs.getDate(2);
        		
        		d.kod = rs.getInt(3);
        		
        		d.quantity_in = rs.getDouble(4);
        		
        		d.rest =  rs.getDouble(5);
        		
        		d.date_out = rs.getDate(6);
        		
        		d.quantity_out = rs.getDouble(7);
        		
        		d.in_number = rs.getString(8);
        		
        		d.out_number = rs.getString(9);
        			
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
	
	
	
	//return all available positions for the kod ->  part_type_id, not zero rest and the date of income less or equal dt
	public static Vector<WsPrihodPartData>  getSkladListKodAvailableForDate(Date dt, int part_type_id) {
		
		Vector<WsPrihodPartData>  vec = new Vector<WsPrihodPartData> ();
		
		try {
			
			WSConnect.get();

        	final String s = "SELECT invoice_parts.id, invoices.date, part_types.name, "
        			+ " invoice_parts.name, invoice_parts.rest," 
        			+ " units.name, invoice_parts.vendor_code_2, invoice_parts.info,"
        			+ " invoice_parts.id_part_type,invoice_parts.id_units, id_invoice, invoice_parts.cost,"
        			+ " invoice_parts.nds, part_types.kod, invoice_parts.costnds "
        			+ " FROM invoice_parts"
        			+ " INNER JOIN invoices ON invoices.id = invoice_parts.id_invoice"
        			+ " INNER JOIN part_types ON part_types.id = invoice_parts.id_part_type"
        			+ " INNER JOIN units ON units.id = invoice_parts.id_units"
        			+ " WHERE invoice_parts.id_part_type = ? AND invoices.date <= ? "
        			+ " AND invoice_parts.rest > 0.0001 ORDER BY  invoices.date,invoice_parts.id ;";
        	
            PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(s);
        	
        	ps.setInt(1, part_type_id);
        	 
        	ps.setDate(2, dt);
        	
        	ResultSet rs =  ps.executeQuery();
        	
        	while(rs.next()) {
        		
        		WsPrihodPartData d = new WsPrihodPartData();
        		
        		d.id = rs.getInt(1);
        		
        		d.date = rs.getDate(2);
        		
        		d.part_type_name = rs.getString(3);
        		
        		d.name = rs.getString(4);
        		
        		d.rest =  rs.getDouble(5);
        		
        		d.units_name = rs.getString(6);
        		
        		d.vendorcode2 = rs.getString(7);
        		
        		d.info = rs.getString(8);
        		
        		d.id_part_type = rs.getInt(9);
        		
        		d.id_units = rs.getInt(10);
        		
        		d.id_invoice =  rs.getInt(11);
        		
        		d.cost = rs.getDouble(12);
        		
        		d.nds = rs.getDouble(13);
        		
        		d.kod = rs.getInt(14);
        		
        		d.costwithnds = rs.getDouble(15);
        		
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
}
