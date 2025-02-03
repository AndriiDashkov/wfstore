
package WsActions;

import java.awt.event.ActionEvent;
import WsTables.WsSkladTable;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsKodFindSkladAction extends WsAction {
	
	private static final long serialVersionUID = 1L;

	 WsSkladTable m_list = null;
	
	int m_kod = -1;
	
	public WsKodFindSkladAction( WsSkladTable list) {
		
		super("wskodskladfindaction");
		
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
