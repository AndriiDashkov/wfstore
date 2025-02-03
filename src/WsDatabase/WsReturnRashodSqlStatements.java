
package WsDatabase;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsReturnedPartData;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsReturnRashodSqlStatements {
	
	
	//returns back every rashod after date dt and for the good with kod = part_type_id
	//saves the result into the special table
	public static int deletePartForRashod(int part_type_id, java.sql.Date dt) {
		
		 final String returned_rows = "SELECT sale_invoices.id, sale_invoices.date, sale_parts.quantity,"
		 		+ "part_types.id, sale_parts.id, sale_parts.id_units, sale_parts.name, part_types.kod,"
		 		+ " sale_invoices.number, units.name  FROM sale_parts "
		 		+ " INNER JOIN sale_invoices ON sale_invoices.id =  sale_parts.id_sale_invoice"
    			+ " INNER JOIN invoice_parts ON sale_parts.id_invoice_parts =  invoice_parts.id"
    			+ " INNER JOIN part_types ON part_types.id =  invoice_parts.id_part_type"
    			+ " INNER JOIN units ON units.id =  sale_parts.id_units"
		 		+ " WHERE sale_invoices.date >= ? AND  part_types.id  = ? ORDER BY sale_invoices.date;";
		   
		 
			//return the rest
	     final String update_prihod = "UPDATE invoice_parts SET rest = (rest + SaleTable.quantity) "
			        + " FROM (SELECT id_invoice_parts, quantity "
			        + "FROM sale_parts WHERE id = ? ) AS SaleTable "
			        + " WHERE SaleTable.id_invoice_parts = invoice_parts.id;";
	     
	     final String delete_st = "DELETE FROM sale_parts WHERE id = ?;";
	     

    
	     
		 final String sql_units_table_insertion = "INSERT INTO  returned_sale_parts (id_sale_invoice, id_sale_part,"
		   	+ " id_part_type, quantity,  sale_invoice_date,  name,  kod, sale_invoice_number, unit_name,"
		   	+ "id_units)\n"
		    + " VALUES(?,?,?,?,?,?,?,?,?,?);";
	        
		 WSConnect.get();
		 
		 int procesedRows  = 0;
			
		 try {
			 
			 PreparedStatement ps0 = WSConnect.getCurrentConnection().prepareStatement(returned_rows);
		      
			 ps0.setDate(1, dt);
			 
			 ps0.setInt(2, part_type_id);
			 
			 ResultSet rs = ps0.executeQuery();
			 
			 PreparedStatement ps = null;
			 
			 while(rs.next()) {
				 
				 int sale_invoice_id = rs.getInt(1);
				 
				 Date dat = rs.getDate(2);
				 
				 double quan = rs.getDouble(3);
				 
				 int part_types_id = rs.getInt(4);
				 
				 int id_sale_part = rs.getInt(5);
				 
				 int id_units = rs.getInt(6);
				 
				 String name = rs.getString(7);
				 
				 int kod = rs.getInt(8);
				 
				 String sale_invoce_number = rs.getString(9);
				 
				 String units_name = rs.getString(10);
				 
				 ps = WSConnect.getCurrentConnection().prepareStatement(update_prihod);
				 
				 ps.setInt(1, id_sale_part);
		            
				 procesedRows = ps.executeUpdate();
				 
				 ps.close();
				 
				 ps = WSConnect.getCurrentConnection().prepareStatement(delete_st);
		            
		         ps.setInt(1, id_sale_part);
		            
		         procesedRows += ps.executeUpdate();
		         
		         ps.close();
		         
		         ps = WSConnect.getCurrentConnection().prepareStatement(sql_units_table_insertion);
		            
		         ps.setInt(1, sale_invoice_id);
		         
		         ps.setInt(2, id_sale_part);
		         
		         ps.setInt(3, part_types_id);
		         
		         ps.setDouble(4, quan);
		         
		         ps.setDate(5, dat);
		         
		         ps.setString(6, name);
		         
		         ps.setInt(7, kod);
		         
		         ps.setString(8, sale_invoce_number);
		         
		         ps.setString(9, units_name);
		         
		         ps.setInt(10,id_units);
		               
		         procesedRows += ps.executeUpdate();
		         
		         ps.close();
				 
			 }

			
			rs.close();

			
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		   
		   
		   
		 return procesedRows ;
		
		
	}
	
	public static void getRashodBack(Vector<WsReturnedPartData> vec) {
		
		 //int sort_type = 4;
		
		for(int i = 0; i < vec.size(); ++i) {
			
			WsReturnedPartData dt = vec.elementAt(i);
			
			Vector<WsPrihodPartData> sklad_rows =  
					 WsSkladSqlStatements.getSkladListKodAvailableForDate(dt.sale_invoice_date,
							dt.id_part_type);
			
			double quantity_to_process = dt.quantity;
			
			dt.returned_quantity = 0.0;
			
			for(int j =0; j < sklad_rows.size(); ++j) {
				
				WsPrihodPartData p_dt = sklad_rows.elementAt(j);
				
				double rest = p_dt.rest;
				
				WsRashodPartData d = new WsRashodPartData();
				
				d.id_sale_invoice = dt.id_sale_invoice;
				
				d.id_invoice_parts = p_dt.id;
				
				d.id_units = p_dt.id_units;
				
				d.kod = p_dt.kod;
				
				d.name = p_dt.name;
				
				d.nds = p_dt.nds;
				
				d.units_name = p_dt.units_name;
				
				d.vendor_code_2 = p_dt.vendorcode2;
				
				d.cost = p_dt.cost;
				
				d.costwithnds = p_dt.costwithnds;
				
				if(rest >= quantity_to_process) {
					
					double q = quantity_to_process;
					
					d.quantity = q;
		
					d.rest = q;
					
					WsRashodSqlStatements.updateOneRowExistingRashod( d);
					
					dt.returned_quantity += q;
					
					break;
					
				}
				else {
					
					d.quantity = rest;
					
					d.rest = rest;
					
					WsRashodSqlStatements.updateOneRowExistingRashod( d);
					
					dt.returned_quantity += rest;
					
					quantity_to_process -= rest;
					
				}	
			}
		}
	
	}
	
	
	public static Vector<WsReturnedPartData> getReturndeTableContent( ) {
	
		Vector<WsReturnedPartData> vec = new Vector<WsReturnedPartData>();
	
		final String returned_rows = "SELECT id, id_sale_invoice, id_part_type, id_sale_part,"
		 		+ "quantity, sale_invoice_date, id_units,"
		 		+ "unit_name, name, kod, sale_invoice_number, returned_quantity  FROM returned_sale_parts "
		 		+ "  ORDER BY sale_invoice_date;";
		   
	        
		 WSConnect.get();
		 
		 try {
			 
			  PreparedStatement ps0 = WSConnect.getCurrentConnection().prepareStatement(returned_rows);
		      
			  ResultSet rs = ps0.executeQuery();
			  
			  while(rs.next()) {
				  
				  WsReturnedPartData d = new WsReturnedPartData();
				  
				  d.id = rs.getInt(1);  
				  
				  d.id_sale_invoice = rs.getInt(2);  
				  
				  d.id_part_type = rs.getInt(3); 
				  
				  d.id_sale_part = rs.getInt(4); 
				  
				  d.quantity = rs.getDouble(5);  
		
			      d.id_units = rs.getInt(7);  
			      
			      d.sale_invoice_date = rs.getDate(6);  
			      
			      d.sale_invoice_number = rs.getString(11);  
			      
			      d.name = rs.getString(9);  
			      
			      d.units_name = rs.getString(8);  
			      
			      d.kod =  rs.getInt(10);
			      
			      d.returned_quantity = rs.getDouble(12); 
			      
			      vec.add(d);
				  
			  }
			 
			  rs.close();
			  
			  ps0.close();
			 
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			}
		 
		 return vec;
	
	}
	
	
	public static  void clearReturnTableContent() {
		

		 final String returned_rows = "DELETE FROM returned_sale_parts;";
		     
		 WSConnect.get();
		 	
		 try {
			 
			  PreparedStatement ps0 = WSConnect.getCurrentConnection().prepareStatement(returned_rows);
		      
			  @SuppressWarnings("unused")
			int rows = ps0.executeUpdate();
			  
			  ps0.close();
			 
			} catch (SQLException e) {
				
				if( WsUtils.isDebug() ) {
					
					e.printStackTrace();
				}
			}
		
	}
	
	public static  void insertIntoReturnTable(Vector<WsReturnedPartData> vec) {
		
		 clearReturnTableContent();

		 final String sql_units_table_insertion = "INSERT INTO  returned_sale_parts (id_sale_invoice, id_sale_part,"
				   	+ " id_part_type, quantity,  sale_invoice_date,  name, kod, sale_invoice_number, unit_name,"
				   	+ "id_units, returned_quantity )\n"
				    + " VALUES(?,?,?,?,?,?,?,?,?,?,?);";
		   
	        
		 WSConnect.get();
		 	
		 PreparedStatement ps = null;
		 try {
			 
			 for(int i = 0; i < vec.size(); ++i) {
				 
				 WsReturnedPartData d = vec.elementAt(i);
				 
				 ps = WSConnect.getCurrentConnection().prepareStatement(sql_units_table_insertion);
				 
			     ps.setInt(1, d.id_sale_invoice);
			     
		         ps.setInt(2, d.id_sale_part);
		         
		         ps.setInt(3, d.id_part_type);
		         
		         ps.setDouble(4, d.quantity);
		         
		         ps.setDate(5, d.sale_invoice_date);
		         
		         ps.setString(6, d.name);
		         
		         ps.setInt(7, d.kod);
		         
		         ps.setString(8, d.sale_invoice_number);
		         
		         ps.setString(9, d.units_name);
		         
		         ps.setInt(10,d.id_units);
		         
		         ps.setDouble(11, d.returned_quantity);
				 
				 @SuppressWarnings("unused")
				int rows = ps.executeUpdate();
				  
				 ps.close();
				 
			 } 
		
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
		}
		
	}
	
	public static boolean updateQuantity(double new_q, int id ) {
		
    	String update = "UPDATE returned_sale_parts SET quantity = ?  WHERE id = ?;";
   	      
    	WSConnect.get();
    	
    	PreparedStatement ps;
    	
		try {
			
			ps = WSConnect.getCurrentConnection().prepareStatement(update);
	
			ps.setDouble(1,  new_q);
        
			ps.setInt(2,  id);
        
			if(ps.executeUpdate() != 1) { return false; }
        
			ps.close();
        
		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return false;
		}
	 
		return true;
	
	}
}
