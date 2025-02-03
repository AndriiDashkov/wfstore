package WsEvents;

import static WsMain.WsLog.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;


/** 
 * 
 *  Events dispatcher - works in synchronous way
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsEventDispatcher {
	
	public static final short SAVE_EVENT = 1;
	
	public static final short ENABLE_EVENT = 2;
	
	public static final short AGENT_IN_COMBO_SELECTED_EVENT = 4;
	
	public static final short INVOICE_HAS_BEEN_CHANGED = 5;
	
	public static final short ORDER_HAS_BEEN_CHANGED_EVENT = 6;
	
	public static final short SALE_INVOICE_HAS_BEEN_CHANGED =7;
	
	public static final short REFRESH_EVENT = 13;
	
	public static final short COMPLETE_DATA_CHANGED_EVENT =30;
	
	public static final short DO_CLICK_NEW_RASHOD_EVENT = 31; 
	
	public static final short AGENT_TYPE_CHAMGED_EVENT = 32;
	
	public static final short NEW_DATA_RASHOD_EVENT = 33;
	
	public static final short CONTRACT_HAS_BEEN_CHANGED = 34;
	
	public static final short SELECT_ALL_EVENT = 70;
	
	public static final short SELECT_EVENT  = 71;
	
	public static final short REFRECH_RECENT_MENU_EVENT = 72;
	
	public static final short CONTRACT_PRICE_HAS_BEEN_CHANGED = 80;
	
	public static final short NEW_CONTRACT_HAS_BEEN_CREATED = 81;

	public static final short SET_SLIDER_SHOW_EVENT = 123;
	
	public static final short SET_SLIDER_HIDE_EVENT = 124;
	
	public static final short BEFORE_APPLICATION_EXIT_EVENT = 125;
	
	public static final short TABLE_ROWS_NEW_COUNT_EVENT = 125;
	
	private static WsEventDispatcher _instance;

	private ArrayList<WsEventListeners> m_listeners = new ArrayList<WsEventListeners>();

	/**
	 * empty constructor
	 */
	private WsEventDispatcher() {}
	
	public  static WsEventDispatcher get() {
		if (_instance == null) {
			
			_instance = new WsEventDispatcher();
			
			_instance.initDispatcher();

		}
		return _instance;
	}
	/**
	 * 	Connects the receiver with event
	 * @param eventType - type of event for connection
	 * @param receiver - the object which will receive the event and react to it with method methodName
	 * @param methodName - method name which will be connect to the event eventType
	 */
	public void addConnect(int eventType, Object receiver, String methodName) {
		
		WsEventObjectMethod listener = new WsEventObjectMethod(receiver, methodName);
		
		for (WsEventListeners x : m_listeners) {
			
			if (x.get_eventType() == eventType) {
				
				x.addListener(listener);
			}
		}
	}
	/**
	 * Disconnects all events from the object receiver
	 * @param receiver - object to disconnect
	 */
	public void disconnect (Object receiver) {
		
		for(Iterator<WsEventListeners> it = m_listeners.iterator(); it.hasNext();) {
			
			for(Iterator<WsEventObjectMethod> it1 = it.next().getListeners().iterator(); it1.hasNext();){
				
				if (it1.next().getListener() == receiver) {
					
					it1.remove();
				}
			}
		}
	}
 	
	/**
	 * Starts the propagation of the event ev
	 * @param ev - event to start
	 */
	public void fireCustomEvent (WsEventInt ev) {

		String methodName = null;
		
		for (WsEventListeners x : m_listeners) {
			
			if (x.get_eventType() == ev.get_EventType()) {
				
				for (WsEventObjectMethod _objectMethod : x.getListeners()) {
					
					try {
					
						Class<?> cls = _objectMethod.getListener().getClass();

						methodName = _objectMethod.get_method();
						
						Method meth_1 = cls.getMethod(methodName, ev.getClass());	
						
						meth_1.invoke(_objectMethod.getListener(), ev);

					} 
					catch (IllegalAccessException  e) {
				
						writeLog("Dispatcher IllegalAccessException :" + methodName,e,true,false,true);
					}
					catch (NoSuchMethodException  e) {
						
						writeLog("Dispatcher NoSuchMethodException:" + methodName,e,true,false,true);
						
					}	catch (InvocationTargetException  e) {
						
						writeLog("Dispatcher InvocationTargetException:" + methodName,e,true,false,true);
						
						System.out.println("Dispatcher InvocationTargetException:" + methodName);
					}									
				}
			}
		}
	}
	/**
	 * Event dispatcher initialization
	 */
	public void initDispatcher () {
		
		m_listeners.add(new WsEventListeners(SAVE_EVENT));		

		m_listeners.add(new WsEventListeners(ENABLE_EVENT));
		
		m_listeners.add( new WsEventListeners(COMPLETE_DATA_CHANGED_EVENT));

		m_listeners.add(new WsEventListeners( DO_CLICK_NEW_RASHOD_EVENT ));
		
		m_listeners.add(new WsEventListeners(AGENT_TYPE_CHAMGED_EVENT));
		
		m_listeners.add(new WsEventListeners(NEW_DATA_RASHOD_EVENT));	
		
		m_listeners.add(new WsEventListeners( CONTRACT_HAS_BEEN_CHANGED));
		
		m_listeners.add(new WsEventListeners(AGENT_IN_COMBO_SELECTED_EVENT));	
	
		m_listeners.add(new WsEventListeners(INVOICE_HAS_BEEN_CHANGED));		
	
		m_listeners.add( new WsEventListeners( ORDER_HAS_BEEN_CHANGED_EVENT ));
		
		m_listeners.add( new WsEventListeners(SALE_INVOICE_HAS_BEEN_CHANGED));		

		m_listeners.add(new WsEventListeners(SELECT_ALL_EVENT));	
	
		m_listeners.add(new WsEventListeners(SELECT_EVENT));
		
		m_listeners.add( new WsEventListeners(REFRECH_RECENT_MENU_EVENT));

		m_listeners.add(new WsEventListeners(CONTRACT_PRICE_HAS_BEEN_CHANGED));
		
		m_listeners.add(new WsEventListeners(NEW_CONTRACT_HAS_BEEN_CREATED));

		m_listeners.add(new WsEventListeners(SET_SLIDER_HIDE_EVENT));
		
		m_listeners.add(new WsEventListeners(SET_SLIDER_SHOW_EVENT));
		
		m_listeners.add(new WsEventListeners(BEFORE_APPLICATION_EXIT_EVENT));//throwing from the only one place - when the application is going to be closed

		m_listeners.add(new WsEventListeners(TABLE_ROWS_NEW_COUNT_EVENT));
	}
	
}
