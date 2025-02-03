
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import WsDataStruct.WsTreeNode;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsHelpBrowser extends JFrame 
{

	private static final long serialVersionUID = 1L;
	
	private JTree m_tree; //main tree for contents info
	
	private JEditorPane m_textArea; //field for manual texts
	
	private WsTreeNode m_root; //root node of the tree
	
	private Map<String, String> m_map = new HashMap<String,String>();
	
	JScrollPane m_scrollPanel = null;
		
	public WsHelpBrowser(JFrame jf, String caption) {
		
		super (caption); 
		
		createGui();
		
		init();
		
		m_textArea.setContentType("text/html; charset=UTF-8");
		
		setBounds(50, 50, 1200, 800);
		
		setResizable(true);

 		for (int i = 0; i < m_tree.getRowCount(); i++) {
 			
 		    m_tree.expandRow(i);
 		}
	}
	
	/**
	 * <p>Creates gui elements for user manual dialog</p>
	 */
	private void createGui(){
		
		JPanel panel_MAIN = WsGuiTools.createHorizontalPanel();
		
		JPanel panelTree = WsGuiTools.createVerticalPanel();
	
		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT, WsUtils.VERT_STRUT
				, WsUtils.VERT_STRUT, WsUtils.VERT_STRUT));
		
		add(panel_MAIN);
		
		m_root = new WsTreeNode( true, getMenusStrs("manualRootName"), "");
		
		m_tree = new JTree(m_root);
		
		m_scrollPanel = new JScrollPane(m_tree);
		
		panelTree.add(m_scrollPanel);
		
		m_textArea = new JEditorPane();
		
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN , 13);

		m_textArea.setFont(f);
		
		JScrollPane scrollPanel2 = new JScrollPane(m_textArea);
		
		m_textArea.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				panelTree, scrollPanel2);
		
		splitPane.setOneTouchExpandable(true);
		
		splitPane.setDividerLocation(450);
		
		panel_MAIN.add(splitPane);
		
		
	}
	/**
	 * <p>Initiates the tree nodes and sets the listener for selection operations</p>
	 */
	private void init(){
		
		m_map.clear();
		
		loadTreeItems();
		
		m_tree.addTreeSelectionListener(new TreeSelectionListener() {
			
		    public void valueChanged(TreeSelectionEvent e) {
		    	
		    	WsTreeNode node = (WsTreeNode) m_tree.getLastSelectedPathComponent();
	
		        if (node == null) return;
	
		        String link  = node.getLink();
		        
				if(link != null && !link.isEmpty()) {
					
					String sText = null;
					
					try {
						
						 byte[] b = Files.readAllBytes(Paths.get(WsUtils.get().getPathToHelpFiles() + link));
						 
						 sText = new String(b, "UTF-8");
	
					} catch(IOException  ex) {
										
					}
					if(sText != null) {
							
							m_textArea.setText(sText);
							
							m_textArea.setCaretPosition(0);
							
					} else {
							
					}
					
				}
				
		    }
		});
	}
	
	/**
	 * <p>Loads contents file and then parses it with creation of tree nodes</p>
	 */
	public void loadTreeItems() {
		
		WsTreeNode treeNode1 = new WsTreeNode(true, getMenusStrs("m1"), "m1_1.html");
		
		m_root.add(treeNode1);
		
		WsTreeNode treeNode11 = new WsTreeNode(false, getMenusStrs("m1_1"), "m1_1.html");
		
		treeNode1.add(treeNode11);
		
		WsTreeNode treeNode12 = new WsTreeNode(false, getMenusStrs("m1_2"), "m1_2.html");
		
		treeNode1.add(treeNode12);
		
		WsTreeNode treeNode13 = new WsTreeNode( false, getMenusStrs("m1_3"), "m1_3.html");
		
		treeNode1.add(treeNode13);
		
		WsTreeNode treeNode14 = new WsTreeNode( false, getMenusStrs("m1_4"), "m1_4.html");
		
		treeNode1.add(treeNode14);
		
		WsTreeNode treeNode2 = new WsTreeNode( true, getMenusStrs("m2"), "m2_1.html");
		
		m_root.add(treeNode2);
		
		WsTreeNode treeNode21 = new WsTreeNode( false, getMenusStrs("m2_1"), "m2_1.html");
		
		treeNode2.add(treeNode21);
		
		WsTreeNode treeNode22 = new WsTreeNode( false, getMenusStrs("m2_2"), "m2_2.html");
		
		treeNode2.add(treeNode22);
		
		WsTreeNode treeNode23 = new WsTreeNode( false, getMenusStrs("m2_3"), "m2_3.html");
		
		treeNode2.add(treeNode23);
		
		WsTreeNode treeNode24 = new WsTreeNode( false, getMenusStrs("m2_4"), "m2_4.html");
		
		treeNode2.add(treeNode24);
	
		WsTreeNode treeNode3 = new WsTreeNode(true, getMenusStrs("m3"), "m3_1.html");
		
		m_root.add(treeNode3);
		
		WsTreeNode treeNode31 = new WsTreeNode(false, getMenusStrs("m3_1"), "m3_1.html");
		
		treeNode3.add(treeNode31);
		
		WsTreeNode treeNode32 = new WsTreeNode( false, getMenusStrs("m3_2"), "m3_2.html");
		
		treeNode3.add(treeNode32);
	
		WsTreeNode treeNode33 = new WsTreeNode( false, getMenusStrs("m3_3"), "m3_3.html");
		
		treeNode3.add(treeNode33);
		
		WsTreeNode treeNode34 = new WsTreeNode( false,getMenusStrs("m3_4"), "m3_4.html");
		
		treeNode3.add(treeNode34);
		
		WsTreeNode treeNode35 = new WsTreeNode( false, getMenusStrs("m3_5"), "m3_5.html");
		
		treeNode3.add(treeNode35);
		
		WsTreeNode treeNode36 = new WsTreeNode( true, getMenusStrs("m3_6"), "m3_6_1.html");
		
		treeNode3.add(treeNode36);
		
		WsTreeNode treeNode361 = new WsTreeNode( false, getMenusStrs("m3_6_1"), "m3_6_1.html");

		treeNode36.add(treeNode361);
		
		WsTreeNode treeNode362 = new WsTreeNode( false, getMenusStrs("m3_6_2"), "m3_6_2.html");

		treeNode36.add(treeNode362);
		
		WsTreeNode treeNode37 = new WsTreeNode( false, getMenusStrs("m3_7"), "m3_7.html");
		
		treeNode3.add(treeNode37);
		
		WsTreeNode treeNode38 = new WsTreeNode(false,  getMenusStrs("m3_8"), "m3_8.html");
		
		treeNode3.add(treeNode38);
		
		WsTreeNode treeNode4 = new WsTreeNode( true, getMenusStrs("m4"), "m4.html");
		
		m_root.add(treeNode4);
		
		WsTreeNode treeNode41 = new WsTreeNode( false, getMenusStrs("m4_1"), "m4_1.html");
		
		treeNode4.add(treeNode41);
		
		WsTreeNode treeNode42 = new WsTreeNode( false,  getMenusStrs("m4_2"), "m4_2.html");
		
		treeNode4.add(treeNode42);
		
		WsTreeNode treeNode43 = new WsTreeNode( false, getMenusStrs("m4_3"), "m4_3.html");
		
		treeNode4.add(treeNode43);
		
		WsTreeNode treeNode44 = new WsTreeNode( false,  getMenusStrs("m4_4"), "m4_4.html");
		
		treeNode4.add(treeNode44);
		
		WsTreeNode treeNode45 = new WsTreeNode( false, getMenusStrs("m4_5"), "m4_5.html");
		
		treeNode4.add(treeNode45);
		
		WsTreeNode treeNode46 = new WsTreeNode( false, getMenusStrs("m4_6"), "m4_6.html");
		
		treeNode4.add(treeNode46);
		
		WsTreeNode treeNode47 = new WsTreeNode( false, getMenusStrs("m4_7"), "m4_7.html");
		
		treeNode4.add(treeNode47);
		
		WsTreeNode treeNode48 = new WsTreeNode( false, getMenusStrs("m4_8"), "m4_8.html");
		
		treeNode4.add(treeNode48);
		
		WsTreeNode treeNode49 = new WsTreeNode( false, getMenusStrs("m4_9"), "m4_9.html");

		treeNode4.add(treeNode49);
		
		WsTreeNode treeNode5 = new WsTreeNode( true, getMenusStrs("m5"), "m5_1.html");

		m_root.add(treeNode5);
	
		WsTreeNode treeNode51 = new WsTreeNode( false,  getMenusStrs("m5_1"), "m5_1.html");
		
		treeNode5.add(treeNode51);
		
		WsTreeNode treeNode52 = new WsTreeNode( false, getMenusStrs("m5_2"), "m5_2.html");
		
		treeNode5.add(treeNode52);
		
		WsTreeNode treeNode6 = new WsTreeNode( true, getMenusStrs("m6"), "m6.html");	
		
		m_root.add(treeNode6);

		WsTreeNode treeNode61 = new WsTreeNode( false, getMenusStrs("m6_1"), "m6_1.html");	
		
		treeNode6.add(treeNode61);
		
		WsTreeNode treeNode62 = new WsTreeNode( false, getMenusStrs("m6_2"), "m6_2.html");		
		
		treeNode6.add(treeNode62);
		
		WsTreeNode treeNode63 = new WsTreeNode( false, getMenusStrs("m6_3"), "m6_3.html");		
		
		treeNode6.add(treeNode63);
		
		WsTreeNode treeNode64 = new WsTreeNode( false, getMenusStrs("m6_4"), "m6_4.html");	
		
		treeNode6.add(treeNode64);
		
		WsTreeNode treeNode65 = new WsTreeNode( false,getMenusStrs("m6_5"), "m6_5.html");	
		
		treeNode6.add(treeNode65);
		
		WsTreeNode treeNode66 = new WsTreeNode( false, getMenusStrs("m6_6"), "m6_6.html");	
		
		treeNode6.add(treeNode66);
		
		WsTreeNode treeNode67 = new WsTreeNode( true, getMenusStrs("m6_7"), "m6_7.html");	
		
		treeNode6.add(treeNode67);
		
		WsTreeNode treeNode671 = new WsTreeNode( false, getMenusStrs("m6_7_1"), "m6_7_1.html");	
		
		treeNode67.add(treeNode671);
		
		WsTreeNode treeNode672 = new WsTreeNode( false, getMenusStrs("m6_7_2"), "m6_7_2.html");	
		
		treeNode67.add(treeNode672);
		
		WsTreeNode treeNode68 = new WsTreeNode( false,  getMenusStrs("m6_8"), "m6_8.html");	
		
		treeNode6.add(treeNode68);
		
		WsTreeNode treeNode69 = new WsTreeNode( false,  getMenusStrs("m6_9"), "m6_9.html");	
		
		treeNode6.add(treeNode69);
		
		WsTreeNode treeNode7 = new WsTreeNode( true,getMenusStrs("m7"), "m7.html");	

		m_root.add(treeNode7);
		
		WsTreeNode treeNode71 = new WsTreeNode( false, getMenusStrs("m7_1"), "m7.html");
		
		treeNode7.add(treeNode71);
		
		WsTreeNode treeNode72 = new WsTreeNode( false, getMenusStrs("m7_2"), "m7_2.html");
		
		treeNode7.add(treeNode72);
		
		WsTreeNode treeNode73 = new WsTreeNode( false, getMenusStrs("m7_3"), "m7_3.html");
		
		treeNode7.add(treeNode73);
		
		WsTreeNode treeNode9 = new WsTreeNode( true, getMenusStrs("m9"), "m9.html");
		
		m_root.add(treeNode9);
		
		WsTreeNode treeNode8 = new WsTreeNode( true, getMenusStrs("m8"), "m8.html");
		
		m_root.add(treeNode8);
	}
}
