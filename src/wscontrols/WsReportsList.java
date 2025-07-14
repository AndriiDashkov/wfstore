
package wscontrols;

import java.awt.event.MouseListener;

import static wsmain.WsUtils.getGuiStrs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import wsmain.WsUtils;
import wsreports.WsForeignSklads46Report;
import wsreports.WsJoinZsuProdDogsReport;
import wsreports.WsKartkaZvitReport;
import wsreports.WsMovementCompareExcelReport;
import wsreports.WsOrderJsonGeneratorReport;
import wsreports.WsPeopleCountReport;
import wsreports.WsPorivSkladWithFutureRashod2;
import wsreports.WsSkadBookPrihodReport;
import wsreports.WsSklaMovementCompareFBase;
import wsreports.WsSkladBookRashodReport;
import wsreports.WsSkladMoveCompareZsuProd;
import wsreports.WsSkladMovementForAgentAndKod;
import wsreports.WsSkladMovementNakl13Report;
import wsreports.WsSkladMovementReport;
import wsreports.WsSkladMovementReport2;
import wsreports.WsSkladMovementReport3;
import wsreports.WsSkladMovementReport4;
import wsreports.WsSkladMovementReportZved;
import wsreports.WsWaterMovementReport;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsReportsList extends JPanel  {
	
	private static final long serialVersionUID = 1L;

	JTree m_tree = null;
    
	Vector<DefaultMutableTreeNode> m_move_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
	Vector<DefaultMutableTreeNode> m_compare_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
	Vector<DefaultMutableTreeNode> m_zvedeni_reports_nodes = new Vector<DefaultMutableTreeNode>();
	
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
	    
	    DefaultMutableTreeNode zvRepNode = new DefaultMutableTreeNode(getGuiStrs("comboPrihodReportsFormtion"));
	    
	    top.add(zvRepNode);
	    
	    createZvedReportsNodes(zvRepNode);
	    
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
	    
	    DefaultMutableTreeNode moveRepNode9 = new DefaultMutableTreeNode(getGuiStrs("reportRuhWithCostContractsVed"));
	    
	    root.add(moveRepNode9);
	    
	    m_move_reports_nodes.add(moveRepNode9);
	
	}
	
	private void createRashodReportsNodes(DefaultMutableTreeNode root) {
		
	    DefaultMutableTreeNode rashodRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportRuhCompareZsuProdClassicVed"));
	    
		root.add(rashodRepNode0);
		
		m_compare_reports_nodes.add(rashodRepNode0);
		
	    DefaultMutableTreeNode rashodRepNode2 = new DefaultMutableTreeNode(getGuiStrs("porivSkladFutureRasklMenuCompareReportName2"));
	    
		root.add(rashodRepNode2);
		
		m_compare_reports_nodes.add(rashodRepNode2);
		
		DefaultMutableTreeNode rashodRepNode3 = new DefaultMutableTreeNode(getGuiStrs("movement2BasesCompareReportName"));
		    
		root.add(rashodRepNode3);
			
		m_compare_reports_nodes.add(rashodRepNode3);
		
		DefaultMutableTreeNode rashodRepNode5 = new DefaultMutableTreeNode(getGuiStrs("movCompareExcelDialog3WinCaption"));
	    
		root.add(rashodRepNode5);
			
		m_compare_reports_nodes.add(rashodRepNode5);
		
	}
	
	
	private void createZvedReportsNodes(DefaultMutableTreeNode root) {
		
		 DefaultMutableTreeNode moveRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportZvedVedomRuhuRashod"));
		    
		root.add(moveRepNode0);
			
		m_zvedeni_reports_nodes.add(moveRepNode0);
		
	    DefaultMutableTreeNode prihodRepNode0 = new DefaultMutableTreeNode(getGuiStrs("reportZvedVedomPrihod"));
	    
		root.add(prihodRepNode0);
		
		m_zvedeni_reports_nodes.add(prihodRepNode0);
		
	    DefaultMutableTreeNode prihodRepNode1 = new DefaultMutableTreeNode(getGuiStrs("reportZvedVedomRashod"));
	    
		root.add(prihodRepNode1);
		
		m_zvedeni_reports_nodes.add(prihodRepNode1);
		
		DefaultMutableTreeNode repNode2 = new DefaultMutableTreeNode(getGuiStrs("reportZvedMoveF"));
	    
		root.add(repNode2);
		
		m_zvedeni_reports_nodes.add(repNode2);
				
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
		
		if(node ==m_move_reports_nodes.elementAt(0)) {
			
        	WsSkladMovementReport2 dialog = new WsSkladMovementReport2(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(1)) {
			
        	WsSkladMovementNakl13Report dialog = new WsSkladMovementNakl13Report(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitNaklDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(2)) {
			
        	WsSkladMovementForAgentAndKod dialog = new WsSkladMovementForAgentAndKod(WsUtils.get().getMainWindow(), 
					getGuiStrs("moveAgentCodeDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(3)) {
			
       	 	WsWaterMovementReport dialog = new  WsWaterMovementReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("moveWaterReportDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(4)) {
			
        	WsKartkaZvitReport dialog = new  WsKartkaZvitReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("kartkaZvitReportDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(5)) {
			
        	WsForeignSklads46Report dialog = new WsForeignSklads46Report(WsUtils.get().getMainWindow(), 
					getGuiStrs("dodatok46ReportDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(6)) {
			
        	WsPeopleCountReport dialog = new WsPeopleCountReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("sumRuhPeopleDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(7)) {
			
        	WsSkladMovementReport3 dialog = new WsSkladMovementReport3(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);
		}
		else
		if(node == m_move_reports_nodes.elementAt(8)) {
			
        	WsSkladMovementReport4 dialog = new WsSkladMovementReport4(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);
		}
		else

		if(node == 	m_compare_reports_nodes.elementAt(0)) {
				
	    	WsSkladMoveCompareZsuProd dialog = new WsSkladMoveCompareZsuProd(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitCompareDialogWinCaption"));
			
			dialog.setVisible(true);
			 	
        }
		else
		if(node == 	m_compare_reports_nodes.elementAt(1)) {
					
			 WsPorivSkladWithFutureRashod2 dialog = new   WsPorivSkladWithFutureRashod2(WsUtils.get().getMainWindow(), 
						getGuiStrs("porivSkladFutureRasklMenuCompareReportName2"));
				
			 dialog.setVisible(true);
		}
		else
		if(node == 	m_compare_reports_nodes.elementAt(2)) {
			
			WsSklaMovementCompareFBase dialog = new   WsSklaMovementCompareFBase(WsUtils.get().getMainWindow(), 
					getGuiStrs("movementZvitDialogWinCaption2"));
			
			dialog.setVisible(true);	
		}
		else
		if(node == 	m_compare_reports_nodes.elementAt(3)) {
			
			WsMovementCompareExcelReport dialog = new   WsMovementCompareExcelReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("movCompareExcelDialog3WinCaption"));
			
			dialog.setVisible(true);	
		}
		else
		if(node ==  m_zvedeni_reports_nodes.elementAt(0)) {
				
	        	WsSkladMovementReport dialog = new WsSkladMovementReport(WsUtils.get().getMainWindow(), 
						getGuiStrs("movementZvitDialogWinCaption"));
				
				dialog.setVisible(true);
				
		}
		else
		if(node == 	m_zvedeni_reports_nodes.elementAt(1)) {
			
			WsSkadBookPrihodReport dialog = new WsSkadBookPrihodReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("prihodZvitDialogWinCaption"));
			
			dialog.setVisible(true);

		}
		else
		if(node == 	m_zvedeni_reports_nodes.elementAt(2)) {
			            
        	WsSkladBookRashodReport dialog = new WsSkladBookRashodReport(WsUtils.get().getMainWindow(), 
					getGuiStrs("rashodZvitDialogWinCaption"));
			
			dialog.setVisible(true);
		}
		else
		if(node == 	m_zvedeni_reports_nodes.elementAt(3)) {
			            
			 WsSkladMovementReportZved dialog = new  WsSkladMovementReportZved(WsUtils.get().getMainWindow(), 
					getGuiStrs("zvZvitDialogWinCaption"));
			
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
