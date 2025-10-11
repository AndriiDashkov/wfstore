package wsmain;


import java.util.Vector;

import wsdatabase.WsSignSqlStatements;
import wsdatabase.WsUtilSqlStatements;
import wsdatastruct.WsInfoData;
import wsdatastruct.WsSignsData;
import wsdatastruct.WsUnitData;


public class WsCommonDataUtil {
	
	private Vector<WsSignsData>  _signs_instance = null;
	
	private Vector<WsUnitData> _units_instance = null;
	
	private Vector<WsInfoData> _info_instance = null;
			
	private WsCommonDataUtil() {
		
	}
	
    private static class WsCommonDataUtilImpl {
	   
       public static final WsCommonDataUtil _instance = new WsCommonDataUtil();
       
    }

    public static WsCommonDataUtil get() {
	   
       return  WsCommonDataUtilImpl._instance;
       
    }
    
    public Vector<WsSignsData> getSignsList() {
    	
    	if(_signs_instance == null) {
    		
    		_signs_instance  = WsSignSqlStatements.getSignsList();

    	}
    	
    	return _signs_instance;
    	
    }
    
    public void resetSignsList() {
    	
    	if(_signs_instance != null) { _signs_instance.clear(); }
    	
    	_signs_instance = null;
    	
    }
    
    public Vector<WsUnitData> getUnitsList() {
    	
    	if(_units_instance == null) {
    		
    		_units_instance  = WsUtilSqlStatements.getUnitsList();

    	}
    	
    	return _units_instance;
    	
    }
    
    public void resetUnitsList() {
    	
    	if(_units_instance != null) { _units_instance.clear(); }
    	
    	_units_instance = null;
    	
    }
    
    
  public Vector<WsInfoData> getInfoDataList() {
    	
    	if(_info_instance == null) {
    		
    		_info_instance  = WsUtilSqlStatements.getInfoDataList();

    	}
    	
    	return _info_instance;
    	
    }
    
    public void resetInfoDataList() {
    	
    	if(_info_instance != null) { _info_instance.clear(); }
    	
    	_info_instance = null;
    	
    }
    
    

}
