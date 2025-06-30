package WsComparators;

import java.util.Comparator;

import WsDataStruct.WsRashodData;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsDateComparator2 implements Comparator<WsRashodData>
{

	public WsDateComparator2() {}
 
    @Override public int compare(WsRashodData o1, WsRashodData o2)
    {
     
        int res = o2.date.compareTo(o1.date);
        
        return res == -1 ? 1 : ( res == 1 ? -1 : 0) ;
    }
}