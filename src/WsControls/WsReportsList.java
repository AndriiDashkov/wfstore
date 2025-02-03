
package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import WsMain.WsUtils;
import WsReports.WsForeignSklads46Report;
import WsReports.WsJoinZsuProdDogsReport;
import WsReports.WsKartkaZvitReport;
import WsReports.WsMovementCompareExcelReport;
import WsReports.WsOrderJsonGeneratorReport;
import WsReports.WsPeopleCountReport;
import WsReports.WsPorivSkladWithFutureRashod2;
import WsReports.WsSkadBookPrihodReport;
import WsReports.WsSklaMovementCompareFBase;
import WsReports.WsSkladBookRashodReport;
import WsReports.WsSkladMoveCompareZsuProd;
import WsReports.WsSkladMoveCompareZsuProdRaskl;
import WsReports.WsSkladMovementForAgentAndKod;
import WsReports.WsSkladMovementNakl13Report;
import WsReports.WsSkladMovementReport;
import WsReports.WsSkladMovementReport2;
import WsReports.WsSkladMovementReport3;
import WsReports.WsWaterMovementReport;
import WsReports.WsZsuProdSkladRasklMoveCompareReport;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsReportsList extends JPanel  {
	
	private static final long serialVersionUID = 1L;

	JTree m_tree = null;
    
	Vector<DefaultMutableTreeNode> m_move_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
	Vector<DefaultMutableTreeNode> m_rashod_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
	Vector<DefaultMutableTreeNode> m_prihod_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
	Vector<DefaultMutableTreeNode> m_gen_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
	public WsReportsList() {
		
		super();
		
		createGUI();	

	    setListeners();
	    
	    expandAll(m_tree);
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add( new JScrollPane(createTree()));
		
	}
	
	private void expandAll(JTree tree){
		
	    for(int i= 0; i< tree.getRowCount();++i){
	    	
	        tree.expandRow(i);
	    }
	}
	
	private JScrollPane createTree() {
	    
	    DefaultMutableTreeNode top = new DefaultMutableTreeNode(getGuiStrs("reportsListCap"));
	    
	    DefaultMutableTreeNode moveRepNode = new DefaultMutableTreeNode(getGuiStrs("comboMoveReportsFormtion"));
	    
	    top.add(moveRepNode);
	    
	    createMoveReportsNodes(moveRepNode);
	    
	    DefaultMutableTreeNode prihodRepNode = new DefaultMutableTreeNode(getGuiStrs("comboPrihodReportsFormtion"));
	    
	    top.add(prihodRepNode);
	    
	    createPrihodReportsNodes(prihodRepNode);
	    
	    DefaultMutableTreeNode compRepNode = new DefaultMutableTreeNode(getGuiStrs("comboCompareReportsFormtion"));
	    
	    top.add(compRepNode);
	    
	    createRashodReportsNodes(compRepNode);

	    DefaultMutableTreeNode genRepNode = new DefaultMutableTreeNode(getGuiStrs("comboGeneratorReportsFormtion"));
	    
	    top.add(genRepNode);
	    
	    createGenReportsNodes(genRepNode);
	    
	    m_tree = new JTree(top);

	    return  new JScrollPane(m_tree);
	  
	}
	
	private void createMoveReportsNodes(DefaultMutableTreeNode root) {
		
	    DefaultMutableTreeNode moveRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportZvedVedomRuhuRashod"));
	    
		root.add(moveRepNode0);
		
		m_move_reports_nodes.add(moveRepNode0);
	    
	    DefaultMutableTreeNode moveRepNode1 = new DefaultMutableTreeNode(getGuiStrs("reportRuhClassicVed"));
	    
	    root.add(moveRepNode1);
	    
	    m_move_reports_nodes.add(moveRepNode1);
	    
	    DefaultMutableTreeNode moveRepNode2 = new DefaultMutableTreeNode(getGuiStrs("movementZvitNaklDialogWinCaption"));
	    
	    root.add(moveRepNode2);
	    
	    m_move_reports_nodes.add(moveRepNode2);
	    
	    DefaultMutableTreeNode moveRepNode3 = new DefaultMutableTreeNode(getGuiStrs("moveAgentCodeDialogWinCaption"));
	    
	    root.add(moveRepNode3);
	    
	    m_move_reports_nodes.add(moveRepNode3);
	    
	    DefaultMutableTreeNode moveRepNode4 = new DefaultMutableTreeNode(getGuiStrs("moveWaterForAgentDialogWinCaption"));
	    
	    root.add(moveRepNode4);
	    
	    m_move_reports_nodes.add(moveRepNode4);
	    
	    DefaultMutableTreeNode moveRepNode5 = new DefaultMutableTreeNode(getGuiStrs("kartkaZvitReportDialogWinCaption"));
	    
	    root.add(moveRepNode5);
	    
	    m_move_reports_nodes.add(moveRepNode5);
	    
	    DefaultMutableTreeNode moveRepNode6 = new DefaultMutableTreeNode(getGuiStrs("dodatok46ReportDialogWinCaption"));
	    
	    root.add(moveRepNode6);
	    
	    m_move_reports_nodes.add(moveRepNode6);
	    
	    DefaultMutableTreeNode moveRepNode7 = new DefaultMutableTreeNode(getGuiStrs("sumRuhPeopleDialogWinCaption"));
	    
	    root.add(moveRepNode7);
	    
	    m_move_reports_nodes.add(moveRepNode7);
	    
	    DefaultMutableTreeNode moveRepNode8 = new DefaultMutableTreeNode(getGuiStrs("reportRuhWithCostClassicVed"));
	    
	    root.add(moveRepNode8);
	    
	    m_move_reports_nodes.add(moveRepNode8);
	
	}
	
	private void createRashodReportsNodes(DefaultMutableTreeNode root) {
		
	    DefaultMutableTreeNode rashodRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportRuhCompareZsuProdClassicVed"));
	    
		root.add(rashodRepNode0);
		
		m_rashod_reports_nodes.add(rashodRepNode0);
		
	    DefaultMutableTreeNode rashodRepNode1 = new DefaultMutableTreeNode(getGuiStrs("spysanyaCompareZsuProdClassicVedRaskladka"));
	    
		root.add(rashodRepNode1);
		
		m_rashod_reports_nodes.add(rashodRepNode1);
		
	    DefaultMutableTreeNode rashodRepNode2 = new DefaultMutableTreeNode(getGuiStrs("porivSkladFutureRasklMenuCompareReportName2"));
	    
		root.add(rashodRepNode2);
		
		m_rashod_reports_nodes.add(rashodRepNode2);
		
		DefaultMutableTreeNode rashodRepNode3 = new DefaultMutableTreeNode(getGuiStrs("movement2BasesCompareReportName"));
		    
		root.add(rashodRepNode3);
			
		m_rashod_reports_nodes.add(rashodRepNode3);
		
		DefaultMutableTreeNode rashodRepNode4 = new DefaultMutableTreeNode(getGuiStrs("movementZvitCompareRasklDialog3WinCaption"));
	    
		root.add(rashodRepNode4);
			
		m_rashod_reports_nodes.add(rashodRepNode4);
		
		
		DefaultMutableTreeNode rashodRepNode5 = new DefaultMutableTreeNode(getGuiStrs("movCompareExcelDialog3WinCaption"));
	    
		root.add(rashodRepNode5);
			
		m_rashod_reports_nodes.add(rashodRepNode5);
		
		
		//WsMovementCompareExcelReport
		
	}
	
	
	private void createPrihodReportsNodes(DefaultMutableTreeNode root) {
		
	    DefaultMutableTreeNode prihodRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportZvedVedomPrihod"));
	    
		root.add(prihodRepNode0);
		
		m_prihod_reports_nodes.add(prihodRepNode0);
		
	    DefaultMutableTreeNode prihodRepNode1 = new DefaultMutableTreeNode(getGuiStrs("reportZvedVedomRashod"));
	    
		root.add(prihodRepNode1);
		
		m_prihod_reports_nodes.add(prihodRepNode1);
				
	}
	
	private void createGenReportsNodes(DefaultMutableTreeNode root) {
			
		    DefaultMutableTreeNode genRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportRaskladkaJsonOrder"));
		    
			root.add(genRepNode0);
			
			m_gen_reports_nodes.add(genRepNode0);
			
			DefaultMutableTreeNode genRepNode1 = new DefaultMutableTreeNode(getGuiStrs("reportJoinDogsOrder"));
			    
			root.add(genRepNode1);
				
			m_gen_reports_nodes.add(genRepNode1);  
}
	
	private void setListeners() {
		
	    MouseListener ml = new MouseAdapter() {
	    	
	        public void mousePressed(MouseEvent e) {
	        	
	            int row = m_tree.getRowForLocation(e.getX(), e.getY());
	            
	            TreePath path = m_tree.getPathForLocation(e.getX(), e.getY());
	            
	            if(null!= path) {
	            
		            DefaultMutableTreeNode node =(DefaultMutableTreeNode)path.getLastPathComponent();
		            
		            if(row != -1) {
		            	
		            	if(e.getClickCount() == 2) {
		                	
		                    openReport(node);
		                }
		            }
	            }
	        }
	    };
	    
	    
	    m_tree.addMouseListener(ml);

	}
	
	private void openReport( DefaultMutableTreeNode node) {
		
		if(node ==  m_move_reports_nodes.elementAt(0)) {
			
        	WsSkladMovementReport dialog = new WsSkladMovementReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption"));
			
			dialog.setVisible(true);
			
		}
		else
		if(node ==m_move_reports_nodes.elementAt(1)) {
			
        	WsSkladMovementReport2 dialog = new WsSkladMovementReport2(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(2)) {
			
        	WsSkladMovementNakl13Report dialog = new WsSkladMovementNakl13Report(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitNaklDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(3)) {
			
        	WsSkladMovementForAgentAndKod dialog = new WsSkladMovementForAgentAndKod(WsUtils.get().getMainWindow(), 
					getGuiStrs("moveAgentCodeDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(4)) {
			
       	 	WsWaterMovementReport dialog = new  WsWaterMovementReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("moveWaterReportDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(5)) {
			
        	WsKartkaZvitReport dialog = new  WsKartkaZvitReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("kartkaZvitReportDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(6)) {
			
        	WsForeignSklads46Report dialog = new WsForeignSklads46Report(WsUtils.get().getMainWindow(), 
					getGuiStrs("dodatok46ReportDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(7)) {
			
        	WsPeopleCountReport dialog = new WsPeopleCountReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("sumRuhPeopleDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(8)) {
			
        	WsSkladMovementReport3 dialog = new WsSkladMovementReport3(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);
		}
		else
		if(node == 	m_rashod_reports_nodes.elementAt(0)) {
				
	    	WsSkladMoveCompareZsuProd dialog = new WsSkladMoveCompareZsuProd(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitCompareDialogWinCaption"));
			
			dialog.setVisible(true);
			 	
        }
		else
		if(node == 	m_rashod_reports_nodes.elementAt(1)) {
				
			WsSkladMoveCompareZsuProdRaskl dialog = new WsSkladMoveCompareZsuProdRaskl(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitCompareRasklDialog2WinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == 	m_rashod_reports_nodes.elementAt(2)) {
					
			 WsPorivSkladWithFutureRashod2 dialog = new   WsPorivSkladWithFutureRashod2(WsUtils.get().getMainWindow(), 
						getGuiStrs("porivSkladFutureRasklMenuCompareReportName2"));
				
			 dialog.setVisible(true);
		}
		else
		if(node == 	m_rashod_reports_nodes.elementAt(3)) {
			
			WsSklaMovementCompareFBase dialog = new   WsSklaMovementCompareFBase(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);	
		}
		else
		if(node == 	m_rashod_reports_nodes.elementAt(5)) {
			
			WsMovementCompareExcelReport dialog = new   WsMovementCompareExcelReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("movCompareExcelDialog3WinCaption"));
			
			dialog.setVisible(true);	
		}
		else
		if(node == 	m_rashod_reports_nodes.elementAt(4)) {
		     
       	 	WsZsuProdSkladRasklMoveCompareReport dialog = new  WsZsuProdSkladRasklMoveCompareReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitCompareRasklDialog3WinCaption"));
			
			dialog.setVisible(true);	
		}
		else
		if(node == 	m_prihod_reports_nodes.elementAt(0)) {
			
			WsSkadBookPrihodReport dialog = new WsSkadBookPrihodReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("prihodZvitDialogWinCaption"));
			
			dialog.setVisible(true);

		}
		else
		if(node == 	m_prihod_reports_nodes.elementAt(1)) {
			            
        	WsSkladBookRashodReport dialog = new WsSkladBookRashodReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("rashodZvitDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == 	m_gen_reports_nodes.elementAt(0)) {
			
			WsOrderJsonGeneratorReport dialog = new  WsOrderJsonGeneratorReport(WsUtils.get().getMainWindow(), 
						getGuiStrs("raskladkaJsonDialogWinCaption"));
				
			dialog.setVisible(true);
		
		}
		else
		if(node == m_gen_reports_nodes.elementAt(1)) {
				
        	WsJoinZsuProdDogsReport dialog = new  WsJoinZsuProdDogsReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("joinZsuRpodDOgsDialogWinCaption"));
			
			dialog.setVisible(true);
			
		}	
	}
}
