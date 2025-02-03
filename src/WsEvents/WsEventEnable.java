package WsEvents;

/** 
 * Special event for enabling GUI controls
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsEventEnable extends WsEvent {

	private int m_flag = 0;
	
	private TYPE m_type;
	
	private int m_left_panel_index = -1;
	
	Object m_data_object = null;
	
	public enum TYPE{

		NEW_LEFT_PANEL_ITEM_ACTIVATED,
		
		DATABASE_LOADED, 
		
		AGENTS_DATA_CHANGED, 
		
		TYPE_AGENT_DATA_CHANGED,   
		
		SIGNS_DATA_CHANGED,          
		
		ORDER_STATUS_CHANGED,    
		
		INVOICE_HAS_BEEN_CHANGED,     
		
		CATALOG_LOADED,
		
		COMPLETE_ORDER_DATA_CHANGED,	
		
		NEW_PART_TYPE_PRIHOD_SELECTED,	
		
		NEW_PART_TYPE_CREATED,
		
		REFRESH_PRIHOD_SUM,
		
		REFRESH_RASHOD_SUM,
		
		UNDO_DISABLED,
		
		REDO_ENABLED,
		
		REDO_DISABLED,
		
		UNVALID_TYPE	
	}

	public WsEventEnable (int flag) {
		
		m_eventType = WsEventDispatcher.ENABLE_EVENT; //2;
		
		m_flag = flag;
		
		m_type = getTypeForFlag(flag);
		
	}
	
	public WsEventEnable (TYPE t) {
		
		m_eventType = WsEventDispatcher.ENABLE_EVENT; //2;
		
		m_type = t; 
		
		m_flag = getFlagForType(t);
		
	}
	
	public int get_flag() {
		
		return m_flag;
	}
	
	
	public TYPE getType() {

		return m_type;
	}
	
	public  void setType(TYPE t) {

		m_type =t;
	}
	
	public  int getFlagForType(TYPE t) {

		switch(t) {
		
			case NEW_LEFT_PANEL_ITEM_ACTIVATED: return 0; 
			
			case DATABASE_LOADED: return 1;
			
			case AGENTS_DATA_CHANGED: return 2;
			
			case TYPE_AGENT_DATA_CHANGED: return 4;   
			
			case SIGNS_DATA_CHANGED: return 5;   
			
			case ORDER_STATUS_CHANGED: return 6;  
			
			case INVOICE_HAS_BEEN_CHANGED:   return -1;  
			
			case CATALOG_LOADED:   return 103;  
			
			case COMPLETE_ORDER_DATA_CHANGED: return 101;	
			
			case NEW_PART_TYPE_PRIHOD_SELECTED: return 102;	
			
			case REFRESH_PRIHOD_SUM: return 104;
			
			case REFRESH_RASHOD_SUM: return 105;
			
			
			default: return -1;		
		}
	}
	
	public  TYPE getTypeForFlag(int f) {

		switch(f) {
		
			case 0: return TYPE.NEW_LEFT_PANEL_ITEM_ACTIVATED;
			
			case 1: return TYPE.DATABASE_LOADED;
			
			case 2: return TYPE.AGENTS_DATA_CHANGED;
			
			case 4: return TYPE.TYPE_AGENT_DATA_CHANGED;      
			
			case 5: return TYPE.SIGNS_DATA_CHANGED;  
			
			case 6: return TYPE.ORDER_STATUS_CHANGED;   
			
			case -1: return TYPE.INVOICE_HAS_BEEN_CHANGED;   
			
			case 101: return TYPE.COMPLETE_ORDER_DATA_CHANGED;		
			
			case 102: return TYPE.NEW_PART_TYPE_PRIHOD_SELECTED;	
			
			case 103: return TYPE.CATALOG_LOADED; 
			
			case 104: return TYPE.REFRESH_PRIHOD_SUM;
			
			case 105: return TYPE.REFRESH_RASHOD_SUM;
			
			default: return TYPE.UNVALID_TYPE;		
		}
	}
	
	public void setLeftPanelIndex(int index) {
		
		m_left_panel_index  = index;
	}
	
	public int getLeftPanelIndex() {
		
		return m_left_panel_index;
	}
	
	public void setDataObject(Object o) { m_data_object = o; }
	
	public Object getDataObject() { return m_data_object; }

}
