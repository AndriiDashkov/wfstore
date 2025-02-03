

package WsDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsTransactions {
	
	
	public static boolean beginTransaction(Connection c) {
		       
		try {
			
			if(c == null) {
				
				WSConnect.getCurrentConnection().setAutoCommit(false);
				
			}
			else {
				
				c.setAutoCommit(false);
			}

		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return false;
		}
       
		return true;		
	}
	
	public static boolean commitTransaction(Connection c) {
	       
		try {
			
			if(c == null) {
				
				WSConnect.getCurrentConnection().commit();
				
				WSConnect.getCurrentConnection().setAutoCommit(true);
				
			}
			else {
				
				c.commit();
				
				c.setAutoCommit(true);
			}

		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				e.printStackTrace();
			}
			
			return false;
		}

		return true;
		
	}
	
	public static boolean rollbackTransaction(Connection c) {
	       
		try {
			
			if(c == null) {
				
				WSConnect.getCurrentConnection().rollback();
				
				WSConnect.getCurrentConnection().setAutoCommit(true);
				
			}
			else {
				
				c.rollback();
				
				c.setAutoCommit(true);
			
			}

		} catch (SQLException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			}
			
			return false;
		}

		return true;
		
	}
}
