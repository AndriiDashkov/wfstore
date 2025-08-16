
package wsdatabase;

import static wsmain.WsUtils.getMessagesStrs;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import wsevents.WsEventDispatcher;
import wsevents.WsEventEnable;
import wsevents.WsEventInt;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsConnect {
	
	private static Connection m_conn = null;
	
	private static WsConnect _instance;//image height
	
	private static boolean m_loaded = false;
	
	public static WsConnect get() {
		
		if (_instance == null) {
			
			_instance = new WsConnect();	
			

		}
		return _instance;
	}
	
	private WsConnect() {
	}
	
	public static boolean isDatabaseLoaded() { return m_loaded; }
	
	public static Connection getCurrentConnection() { return m_conn; }
	
	 public static boolean connect(String fileName) {  
	          
	        try {  
	        	
	        	if(! WsUtils.isFileExists(fileName) ) {
	        		
	        		throw new SQLException(getMessagesStrs("cantLoadDatabaseFile"));
	        		
	        	}
	        	
	            // db parameters  
	            String url = "jdbc:sqlite:" + fileName;  
	            
	            if(m_conn != null) {
	            	
	            	m_conn.close(); 
	            }
	            
	            m_conn = DriverManager.getConnection(url);  
	            
	            if(m_conn == null || !checkDataBase(m_conn)) {
	            	
	            	throw new SQLException(getMessagesStrs("cantLoadDatabaseFile")); 
	            }
	            
				WsDatabaseUpdate.main_update();
	            
	            WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.DATABASE_LOADED);
	            
	            WsUtils.get().getSettings().setRecentData(fileName);
	            
				WsEventDispatcher.get().fireCustomEvent(event);
				
				String specMessage =  WsUtils.get().ifTheDaySpecific(Calendar.getInstance().getTime());
				
				if( specMessage != null) {
						 
					 WsUtils.showMessageDialog(getMessagesStrs( "loadDatatbaseSuccessMessage") +
		        			    " " + specMessage);
					
				}
				else {
	       
	                WsUtils.showMessageDialog(getMessagesStrs("loadDatatbaseSuccessMessage"));
				}
	              
	            System.out.println("Connection to SQLite has been established.");
	            
	            m_loaded = true;
	            
	            WsUtils.CATALOG_5_DIGIT = WsUtilSqlStatements.isCatalog5digits();
	            
	            WsUtils.changePredefinedKods();
	            
	            WsUtils.get().setMainWindowCaption ("WFStore " + WsUtils.VERSION  +
	            		" " + getMessagesStrs("loadedBaseCaptionMain") + " : " + fileName);
	            
	            return true;
	              
	        } catch (SQLException e) {  
	        	
	            System.out.println(e.getMessage());  
	         
	            String errorMessage = getMessagesStrs("loadDatatbaseErrorMessage");
                
                WsUtils.showMessageDialog(errorMessage + "  " + e.getMessage());
	        }   
	        
	        return false;
	    }
	 
	    public static void createNewDatabase(String fileName, boolean kods5flag) {  
	    	   
	        String url = "jdbc:sqlite:" + fileName;  
	   
	        try {  
	        	
	            Connection conn = DriverManager.getConnection(url);  
	            
	            if (conn != null) {  
	            	
	                DatabaseMetaData meta = conn.getMetaData();  
	                
	                System.out.println("The driver name is " + meta.getDriverName());
	                
	                WsCreateDatabase.createDatabase(conn, kods5flag);
	                
	                System.out.println("A new database has been created.");  
	                
	                String operationMessage = getMessagesStrs("newDatatbaseCreatedMessage");
	                                
	                WsUtils.showMessageDialog(operationMessage);
	            }  
	            else {
	            	
	            	SQLException ex = new SQLException();
	            	    	
	            	throw ex;
	            }
	   
	        } catch (SQLException e) { 
	        	
	            System.out.println(e.getMessage());  
	            
	            String errorMessage = getMessagesStrs("newDatatbaseErrorMessage");
                
                WsUtils.showMessageDialog(errorMessage + "  " + e.getMessage());
	        }  
	    }
	    
	    private static boolean checkDataBase(Connection con) {
	    	
	    	PreparedStatement ps  = null;
	    	
	    	ResultSet rs = null;
	    	
    	    try {
    		
    	      	String st = "SELECT COUNT(*) AS CNTREC FROM pragma_table_info('invoices') WHERE name='date';";
 	  	           	
    			ps = con.prepareStatement(st);
            
                rs = ps.executeQuery();
	                
                ps.close();
                
                rs.close();
                
	            return true;

            
    		} catch (SQLException e) {
    			
    			return false;

    		}
	    	
	    }
	    
	    public static Connection connectImport(String fileName) {  
	          
	        try {  
	            
	            String url = "jdbc:sqlite:" + fileName;  
	            
	     
	            Connection conn = DriverManager.getConnection(url);  
	            
	            if(conn == null || !checkDataBase(conn)) {
	            	
	            	throw new SQLException(getMessagesStrs("cantLoadDatabaseFile")); 
	            }
	             
	            System.out.println("Connection to SQLite has been established.");
	            
	      
	            return conn;
	              
	        } catch (SQLException e) {  
	        	
	            System.out.println(e.getMessage());  
	         
	            String errorMessage = getMessagesStrs("loadDatatbaseErrorMessage");

                WsUtils.showMessageDialog(errorMessage + "  " + e.getMessage());
	        }   
	        
	        return null;
	    }
}




