
package WsDatabase;

import static WsMain.WsUtils.getGuiStrs;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import WsMain.WsCatalogKods;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WSCreateDatabase {
	
	
	final static String sql_counterparties_types_table_creation = "CREATE TABLE IF NOT EXISTS counterparties_types (\n"  
            + " id integer PRIMARY KEY,\n"  
            + " name text NOT NULL,\n"  
            + " info text \n"
            + ");"; 
	
	static String  sql_counterparties_types_table_insertion = "INSERT INTO counterparties_types (name)\n"
  	      + " VALUES('" + getGuiStrs("postachDatabaseName") + "'),( '" + getGuiStrs("pidrozdilDatabaseName") + "');";
  	          
	
    final static String sql_counterparties_table_creation = "CREATE TABLE IF NOT EXISTS counterparties (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text NOT NULL,\n"
                + " contact text NOT NULL,\n"
                + " info text,\n"
                + " id_type integer NOT NULL,\n" 
                + " FOREIGN KEY(id_type) REFERENCES counterparties_types(id) ON DELETE RESTRICT\n"
                + ");";  
      
  	final static String sql_store_table_creation = "CREATE TABLE IF NOT EXISTS store (\n"  
            + " id integer PRIMARY KEY,\n"  
            + " name text NOT NULL,\n"
            + " vendor_code_1 text,\n"
            + " vendor_code_2 text,\n"
            + " rest real default 0.0\n" 
            + ");"; 
  	 
     final static String sql_part_types_table_creation = "CREATE TABLE IF NOT EXISTS part_types (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text,\n"  
                + " kod integer,\n"
                + " sklad_quantity real default 0.0,\n"
                + " info text,\n" 
                + " costwithnds real default 0.0\n" 
                + ");"; 
      
      
     
	  	          

	  final static String sql_units_table_creation = "CREATE TABLE IF NOT EXISTS units (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text NOT NULL\n"  
                + ");";
	   
	  final static String sql_units_table_insertion = "INSERT INTO units (name)\n"
		  	      + " VALUES('" + getGuiStrs("units_sht_DatabaseName") + "'),( '"
			  + getGuiStrs("units_t_sht_DatabaseName") + "'),( '"
			  + getGuiStrs("units_kg_DatabaseName") + "'),( '"
			  + getGuiStrs("units_tonn_DatabaseName") + "'),( '"
			  + getGuiStrs("units_gr_DatabaseName") + "'), ('"
			  + getGuiStrs("units_litr_DatabaseName") +
			  "');";
	  
	  public final static String sql_contracts_table_creation = "CREATE TABLE IF NOT EXISTS contracts (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text NOT NULL,\n"  
                + " date date NOT NULL,\n"
                + " info text NOT NULL\n" 
                + ");";
	   
	   
	  public final static String sql_contracts_table_insertion = 
			   "INSERT INTO contracts (name, date, info)  VALUES('test', datetime('now','localtime'), '');";
	   
	  public final static String sql_contract_prices_table_creation = "CREATE TABLE IF NOT EXISTS contract_prices (\n"  
	                + " id integer PRIMARY KEY,\n"  
	                + " id_contract NOT NULL,\n"
	                + " id_part_type integer NOT NULL,\n"
	                + " id_units integer NOT NULL,\n"
	                + " cost real default 0.0,\n" 
	                + " nds real default 0.0,\n" 
	                + " costwnds real default 0.0,\n"
	                + " FOREIGN KEY(id_contract) REFERENCES contracts(id) ON DELETE RESTRICT,\n"
	                + " FOREIGN KEY(id_units) REFERENCES units(id) ON DELETE RESTRICT,\n"
	                + " FOREIGN KEY(id_part_type) REFERENCES part_types(id) ON DELETE RESTRICT\n"
	                + ");";
		  	          

      final static String sql_invoices_table_creation = "CREATE TABLE IF NOT EXISTS invoices (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " number text NOT NULL,\n"  
                + " date date NOT NULL,\n" 
                + " date_doc date NOT NULL,\n" 
                + " info text NOT NULL,\n" 
                + " id_counterparty integer NOT NULL,\n"
                + " id_contract integer NOT NULL default 1,\n"
                + " id_external integer NOT NULL default -1,\n"
                + " FOREIGN KEY(id_counterparty) REFERENCES counterparties(id) ON DELETE RESTRICT,\n"
                + " FOREIGN KEY(id_contract) REFERENCES contracts(id) ON DELETE RESTRICT\n"
                + ");";
      

      
      final static String sql_invoice_parts_table_creation = "CREATE TABLE IF NOT EXISTS invoice_parts (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " id_invoice integer NOT NULL,\n"
                + " id_part_type integer NOT NULL,\n"
                + " name text NOT NULL,\n"  
                + " quantity real default 0.0,\n" 
                + " rest real default 0.0,\n" 
                + " id_units integer NOT NULL,\n"
                + " vendor_code_2 text,\n"
                + " closed boolean,\n"
                + " info text NOT NULL,\n" 
                + " cost real default 0.0,\n" 
                + " nds real default 0.0,\n" 
                + " costnds real default 0.0,\n"
                + " FOREIGN KEY(id_invoice) REFERENCES invoices(id) ON DELETE RESTRICT,\n"
                + " FOREIGN KEY(id_units) REFERENCES units(id) ON DELETE RESTRICT,\n"
                + " FOREIGN KEY(id_part_type) REFERENCES part_types(id) ON DELETE RESTRICT\n"
                + ");";
      
      
      final static String sql_sale_invoices_table_creation = "CREATE TABLE IF NOT EXISTS sale_invoices (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " number text NOT NULL,\n"  
                + " date date NOT NULL,\n" 
                + " info text NOT NULL,\n" 
                + " id_counterparty integer NOT NULL,\n"
                + " people integer default 0,\n"
                + " FOREIGN KEY(id_counterparty) REFERENCES counterparties(id) ON DELETE RESTRICT\n"
                + ");";
      

      
      final static String sql_sale_parts_table_creation = "CREATE TABLE IF NOT EXISTS sale_parts (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " id_sale_invoice integer NOT NULL,\n"
                + " id_invoice_parts integer NOT NULL,\n"
                + " name text NOT NULL,\n"  
                + " quantity real default 0.0,\n" 
                + " req_quantity real default 0.0,\n"
                + " rest real default 0.0,\n" 
                + " id_units integer NOT NULL,\n"
                + " vendor_code_2 text,\n"
                + " info text NOT NULL,\n" 
                + " cost real default 0.0,\n" 
                + " nds real default 0.0,\n" 
                + " costnds real default 0.0,\n"
                + " FOREIGN KEY(id_sale_invoice) REFERENCES sale_invoices(id) ON DELETE RESTRICT,\n"
                + " FOREIGN KEY(id_units) REFERENCES units(id) ON DELETE RESTRICT,\n"
                + " FOREIGN KEY(id_invoice_parts) REFERENCES invoice_parts(id) ON DELETE RESTRICT\n"
                + ");";
      

  
      final static String sql_info_table_creation = "CREATE TABLE IF NOT EXISTS info (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text NOT NULL,\n"  
                + " adress text default '',\n"  
                + " phone text default '',\n"  
                + " person text default '',\n"  
                + " MFO text default '',\n"
                + " rahunok text default '',\n"  
                + " comments text default '',\n"
                + " money text default '',\n" 
                + " nds real default 0.0\n"
                + ");";
      
      final static String sql_returned_sale_parts_table_creation = "CREATE TABLE IF NOT EXISTS returned_sale_parts (\n"  
              + " id integer PRIMARY KEY,\n"  
              + " id_sale_invoice integer NOT NULL,\n"
              + " id_sale_part integer NOT NULL,\n"
              + " id_part_type integer NOT NULL,\n"
              + " quantity real default 0.0,\n"
              + " returned_quantity real default 0.0,\n"
              + " sale_invoice_date Date,\n"
              + " name text default '',\n"
              + " kod text default '',\n"
              + " sale_invoice_number text default '',\n"
              + " unit_name text default '',\n"
              + " id_units integer NOT NULL);";
      
      
	  final static String sql_sign_table_creation = "CREATE TABLE IF NOT EXISTS signatories (\n"  
                + " id integer PRIMARY KEY,\n"  
                + " name text default '',\n" 
                + " rank text default '',\n"
                + " position text default ''\n"
                + ");";
	
	public static void createDatabase(Connection conn, boolean kods5) {
		
		WsUtils.NEW_CATALOG = true;

	  
	        try{  
	             
	            Statement stmt = conn.createStatement();  
	            
	            stmt.execute(sql_counterparties_types_table_creation);
	            
	            stmt.execute(sql_counterparties_types_table_insertion);
	            
	            stmt.execute(sql_counterparties_table_creation);
	            
	            stmt.execute(sql_store_table_creation);
	         
	            stmt.execute(sql_units_table_creation);
	            
	            stmt.execute(sql_units_table_insertion);
	            
	            stmt.execute(sql_part_types_table_creation);
	            
	            stmt.execute(sql_contracts_table_creation);
	            
	            stmt.execute(sql_contracts_table_insertion);
	            
	            stmt.execute(sql_contract_prices_table_creation);
	            
	            stmt.execute(sql_invoices_table_creation);
	            
	            stmt.execute(sql_invoice_parts_table_creation);
	            
	            stmt.execute(sql_sale_invoices_table_creation);
	            
	            stmt.execute(sql_sale_parts_table_creation);
	            
	            String sql_part_types_table_insertion = getPartTypeCatalogInsertionSql(kods5);

	            stmt.execute(sql_part_types_table_insertion);
	            
	            stmt.execute(sql_info_table_creation);
	            
	            stmt.execute(sql_returned_sale_parts_table_creation); 
	            
	            stmt.execute(sql_sign_table_creation); 
	            
	        } catch (SQLException e) {  
	        	
	            System.out.println(e.getMessage());  
	        }  
		
	}

	
	private static String getPartTypeCatalogInsertionSql(boolean kods5flag) {
		
		WsCatalogKods  ct = new WsCatalogKods();
		
		Integer[] keys = ct.getSortedKods();
		
		Integer[] keys_ = new Integer[keys.length];
		
		int shift = 0;
		
		if(kods5flag) { shift = 10000; }
		
		for(int i = 0; i < keys.length; ++i)	 {
			
			keys_[i] = keys[i] + shift;
		}
		
	
		
		StringBuilder sb = new StringBuilder(30*100);
		
		String strEnd = "'),";
		
		sb.append("INSERT INTO part_types (kod, name)\n");
		
		sb.append(" VALUES(" + keys_[0].toString() + ",'");
		 
		sb.append(ct.getName(keys[0]));
		 
		sb.append(strEnd);
		
		for(int i = 1; i < (keys.length - 1); ++i) {
			
			
			sb.append("(" + keys_[i].toString() + ",'"); 
			
			sb.append(ct.getName(keys[i])); 
			
			sb.append(strEnd);
			
		}
		
		sb.append("(" + keys_[keys_.length - 1].toString() + ",'"); 
		
		sb.append(ct.getName(keys[keys.length - 1])); 
		
		sb.append("');");
		
		return sb.toString();
		
	}
	
}
