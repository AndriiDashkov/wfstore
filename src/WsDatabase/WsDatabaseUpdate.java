
package WsDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDatabaseUpdate {
	

	
	private static int update00() {
		

			String check = "SELECT date FROM contracts WHERE id = 1;";
			
			int flag = 0;
			
			try {
				
				PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(check);
				
				ResultSet rs = ps.executeQuery();
				
				if(rs.next()) {
				
					try {
						
						@SuppressWarnings("unused")
						java.sql.Date  d = rs.getDate(1);
					
					} catch (SQLException e0) {
					
						rs.close(); 
						
						ps.close(); 
						
						flag  = changeDate();
						
						return flag;
					
					}
	
				}
				
				rs.close();
				
				ps.close();
			
			} catch (SQLException e) {
					
				e.printStackTrace();
				
				return 1;
			}
		
		return flag;
		
	}

	
	private static int changeDate() {
		
		try {
			
			WsTransactions.beginTransaction(null);
			
	        String update = "UPDATE contracts SET date = datetime('now','localtime') WHERE id = 1;";
	        	 
	        PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(update);
	        	
	    	if( ps.executeUpdate() == 0) {
	    		
	    		WsTransactions.rollbackTransaction(null);
	               
	            ps.close();
	    		
	    		return 1;
	    	}
	    	
	    	WsTransactions.commitTransaction(null);
	    	
	        ps.close();
	    	   
	    	return 0;

		} catch (SQLException e1) {
			
			// TODO Auto-generated catch block
			e1.printStackTrace();
		
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}

	}
	
	
	@SuppressWarnings("unused")
	private static int update0_1() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('sale_parts') WHERE name='costnds';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
            ps.close();
            
     		rs.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE sale_parts ADD costnds real default 0.0;";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	@SuppressWarnings("unused")
	private static int update0_2() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('invoice_parts') WHERE name='costnds';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE invoice_parts ADD costnds real default 0.0;";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	@SuppressWarnings("unused")
	private static int update0_3() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('info') WHERE name='nds';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE info ADD nds real default 0.0;";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	
	@SuppressWarnings("unused")
	private static int update0_4() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('part_types') WHERE name='costwithnds';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE part_types ADD costwithnds real default 0.0;";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
		
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	

	@SuppressWarnings("unused")
	private static int update0_5() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('info') WHERE name='money';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE info ADD money text default ' ';";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	
	
	@SuppressWarnings("unused")
	private static int update0_6() {
	
	try {
		    Statement stmt = WSConnect.getCurrentConnection().createStatement();  
		     
		    final String st = "CREATE TABLE IF NOT EXISTS signatories (\n"  
		                + " id integer PRIMARY KEY,\n"  
		                + " name text default '',\n" 
		                + " rank text default '',\n"
		                + " position text default ''\n"
		                + ");";
	            
	        stmt.execute(st);
            
	        stmt.close();
     		
		} catch (SQLException e) {

			e.printStackTrace();
		
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	
	@SuppressWarnings("unused")
	private static int update0_7() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='contracts';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(WSCreateDatabase.sql_contracts_table_creation);
            	
            	stt.execute(WSCreateDatabase.sql_contracts_table_insertion);
            	
               	stt.execute(WSCreateDatabase.sql_contract_prices_table_creation);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	
	@SuppressWarnings("unused")
	private static int update0_8() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('invoices') WHERE name='id_contract';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE invoices ADD id_contract integer NOT NULL default 1;";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
		
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	
	
	private static int update0_9() {
		
		
	try {
		
			WsTransactions.beginTransaction(null);
		
        	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('invoices') WHERE name='id_external';";
       	  	          
        	WSConnect.get();
        	
			PreparedStatement ps = WSConnect.getCurrentConnection().prepareStatement(st);
        
            ResultSet rs = ps.executeQuery();
            
            rs.next();
           
            int r = rs.getInt(1);
            
     		rs.close();
     		
     		ps.close();
            	
            if(r == 0 ) {
            	
            	String s = "ALTER TABLE invoices ADD id_external integer NOT NULL default -1;";
            	
            	Statement stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s);
            	
            	stt.close();
            	
            	String s1 = "UPDATE invoices SET id_external = 0;";
            	
            	stt = WSConnect.getCurrentConnection().createStatement();
            	
            	stt.execute(s1);
            	
            	stt.close();
                 
                WsTransactions.commitTransaction(null);
                 
         		return 0;//has been done
            		
            }
            
            WsTransactions.rollbackTransaction(null);
   
		} catch (SQLException e) {
		
			e.printStackTrace();
			
			WsTransactions.rollbackTransaction(null);
			
			return 1;

		}
		
		
		return 2; //no need to do 
	}
	


	
	public static void main_update() {
		
		//@SuppressWarnings("unused")

		//changeDate();
		
		update0_1();
		
		update0_2();

		update0_3();
		
		update0_4();
		
		update0_5();
		
		update0_6();
		
		update0_7();
		
		update0_8();
		
		update00();
		
		update0_9();
		
		
		
	}

}
