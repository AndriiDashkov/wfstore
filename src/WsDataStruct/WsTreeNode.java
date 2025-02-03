
package WsDataStruct;


import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *<p>Tree node object for the help system mostly</p>
 */
public class WsTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 1L;
	
	private int m_id;
	
	private String m_name = null;
	
	private String m_link = null;


	public WsTreeNode() {
		
	}

	/**
	 * @param arg0
	 */
	public WsTreeNode( String name, String link) {	
		super(name);
		
		this.setName(name);
		
		m_name = name;
		
		m_link = link;
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public WsTreeNode(boolean arg1, String name, String link) {
		
		super(name, arg1);
		
		this.setName(name);
		
		m_name = name;
		
		m_link = link;
	}
	
	
	public int getId() { return m_id; }
	
	public void setId(int i) { m_id = i; }
	
	public String getName() { return m_name; }
	
	public void setName(String s) { m_name = s; }
	
	public String getLink() { return m_link; }
	
	public void setLink(String s) { m_link = s; }

}