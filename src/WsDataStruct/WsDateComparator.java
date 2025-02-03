
package WsDataStruct;

import java.util.Comparator;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsDateComparator implements Comparator<WsSkladMoveDataRow>
{

	public WsDateComparator() {}
 
    @Override public int compare(WsSkladMoveDataRow o1, WsSkladMoveDataRow o2)
    {
     
        int res = o2.date.compareTo(o1.date);
        
        return res == -1 ? 1 : ( res == 1 ? -1 : 0) ;
    }
}