
package WsDataStruct;


import java.util.Comparator;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class WsKodComparator implements Comparator<WsSkladMoveDataColumn>
{

	public WsKodComparator() {}
 
    @Override public int compare(WsSkladMoveDataColumn o1, WsSkladMoveDataColumn o2)
    {
     
        return o1.kod > o2.kod ? 1 : (o1.kod == o2.kod ? 0 : -1) ;
    }
}
