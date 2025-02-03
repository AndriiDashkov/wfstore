
package WsActions;

import java.awt.event.ActionEvent;
import WsEditTables.WsKodEditTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsKodFindAction extends WsAction {
	
	
	private static final long serialVersionUID = 1L;

	WsKodEditTable m_list = null;
	
	int m_kod = -1;
	
	public WsKodFindAction(WsKodEditTable list) {
		
		super("wskodfindaction");
		
		m_list = list;
			
	}
	
	public void setKod(int kod) {
		
		m_kod = kod;
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		setKod(Integer.parseInt(e.getActionCommand()));
		
		m_list.findKod(m_kod);
	
	}
}