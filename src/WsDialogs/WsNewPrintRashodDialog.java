
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.WsCompanyComboBox;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import WsReports.WSReportViewer;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNewPrintRashodDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected JLabel m_infoLabel = new JLabel(" ");	//info label in the bottom of the dialog
	
	protected JLabel m_name_label;
	
	protected JLabel m_info_label;
	
	protected JTextField m_name = new JTextField(25);
	
	protected JTextField m_info = new JTextField(25);
	
	protected JPanel panel_R = new JPanel();	
	
	protected JPanel panel_0 = WsGuiTools.createVerticalPanel();
	
	protected JPanel panelButtons = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	private Font m_font = WsUtils.get().getBaseFont(); 
	
	public JButton m_OkButton, m_CancelButton;

	WsRashodData m_data = null;
	
	private WsCompanyComboBox m_combo = new WsCompanyComboBox(true);
	
	public WsNewPrintRashodDialog(JFrame jfrm, WsRashodData dt, String nameFrame) {
		super (jfrm, nameFrame, true);
		
		m_data = dt;
		
		add(createGUI());
		
		setBounds(250, 150, 225, 220);
		
		init();
	
		pack();
		
		setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
	}
	

	/**
	 * Listener for buttons reaction
	 * @author Andrii Dashkov license GNU GPL v3
	 *
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
		}
	}
	
	public void onOK(ActionEvent e) {

		flag = WsCloseFlag.OK;
		
		WSReportViewer dialog = new WSReportViewer(WsUtils.get().getMainWindow(), 
				getGuiStrs("newPrintDialogWinCaption"));
		
		dialog.setText(getPrintHtml());
		
		dialog.setVisible(true);

		dispose();

	}
	/**
	 * Cancel button reaction
	 * @param e
	 */
	public void onCancel(ActionEvent e) {
		
		flag = WsCloseFlag.CANCEL;
		
		dispose();
	}	
	
	
	/**
	 * 	
	 * @return close flag to determine what operation should be done after the dialog close
	 */
	public WsCloseFlag getClosedFlagValue () {
		
		return flag;
	}
	
	
	/**
	 * 
	 * @return main UI panel with all components
	 */
	private JPanel createGUI() {
		
		m_name_label = new JLabel(getGuiStrs("companyComboNameNewDialogLabel"));

		JPanel panel_1 = new JPanel();
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);

		panel_MAIN.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		GridLayout gridLayout = new GridLayout(0,2);

		panel_1.setLayout(gridLayout);

		panel_1.add(m_name_label);    panel_1.add(m_combo);
		
		JPanel south_right = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_OkButton = new JButton(getGuiStrs("buttonOkCaption"));
		
		m_CancelButton = new JButton(getGuiStrs("buttonCancelCaption"));
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);
	
		south_right.add(m_infoLabel);
		
		south_right.add(panel_Ok_Cancel);

		panel_MAIN.add(panel_1);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));	
		
		panel_MAIN.add(south_right);

		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);

		PaFocusCustomListener fListener = new PaFocusCustomListener();
		
		m_combo.addFocusListener(fListener);

		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				dispose();
			}
		});
	
		
	}
	
	public void verifyInfo() {
		
		boolean flag = true;
			
	   m_OkButton.setEnabled(flag);
	   
	   if (flag) { m_infoLabel.setText(""); }
		
		
	}
	
	
	class PaFocusCustomListener implements FocusListener
	{
		@Override
		public void focusGained(FocusEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void focusLost(FocusEvent arg0) {
		
			 verifyInfo();
			
		}
	    
	}
	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 

	 
	/**
	 * <p>Initiation function.Don't remove it! It must be empty, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
		
		m_combo.refreshModel();
	}
	
	
	public String getPrintHtml() {
		
	
		int id = m_combo.getCurrentSQLId();
		
		Vector<WsInfoData> v_info = WsUtilSqlStatements.getInfoDataList();
		 
		WsInfoData d = null; 
		 
		for(int j =0; j < v_info.size(); ++j) {
			 
			 d = v_info.elementAt(j);
			 
			 if(d.id == id) { break; }
		}
	
		if (d == null) {
			 
			 d = new  WsInfoData();
		}
		
		Vector<WsRashodPartData> vec_parts = WsRashodSqlStatements.getRashodPartsVector(m_data.id);
		
		double sum_cost = 0.0;
		
		double sum_nds = 0.0;
		
		String rows = "";
		
		for(int i = 0; i < vec_parts.size(); ++i) {
			
			WsRashodPartData d1 =vec_parts.elementAt(i);
			
			  String s = "<tr>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" + String.valueOf(i + 1) +  "</font></td>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" + d1.name + "</td>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" + String.valueOf(d1.quantity) + "</font></td>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" + d1.units_name + "</td>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" + String.valueOf(d1.cost) + "</font></td>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>" + String.valueOf(d1.nds) + "</font></td>"
			  + "<td style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid;'><font size =4>" + String.valueOf(d1.cost * d1.quantity) + "</font></td>"
			  + "</tr>";
			  
			  sum_cost += d1.cost;
			  sum_nds += d1.nds;
			
			  rows += s;
		}
		
		String hS = "<html> "
				+ "<style>"
				+ "</style><body>"
		+ "<h2 align='center' ><font size =5>Видаткова накладна № от "+ m_data.date +"</font></h2>"
		+ "<table style='width:60%;'  BORDER=0 CELLPADDING=0 CELLSPACING=0>"
		 + "<tr ><td><font size =5>Постачальник :</font></td><td><font size =4>"+ d.name +"</font></td></tr>"
		 + "<tr><td><font size =5>р/р :</font></td><td><font size =4>" + d.rahunok + "</font></td></tr>"
		 + "<tr><td ><font size =5>МФО :</font></td><td><font size =4>" + d.MFO + "</font></td></tr>"
		 + "<tr><td><font size =5>Адреса :</font></td><td><font size =4>" + d.adress + "</font></td></tr>"
		 + "<tr ><td><font size =5>Покупець :</font></td><td><font size =4>" + m_data.agentName + "</font></td></tr>"
		 + "<tr><td><font size =5>Підстава :</font></td><td><font size =4>"+ d.comments + "</font></td></tr>"
		+ "</table><table style='width:100%;' cellspacing='0' cellpadding='2'  >"
		+ " <tr>"
		+ " <td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>№</font></td>"
		+ " <td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>Найменування товару</font></td>"
		+ " <td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>Кількість</font></td>"
		+ "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>од. вим.</font></td>"
		+ "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>Ціна без ПДВ</font></td>"
		+ "<td style='border-left: 1px solid;border-top: 1px solid ;'><font size =4>ПДВ</font></td>"
		+ "<td style='border-left: 1px solid;border-top: 1px solid ; border-right: 1px solid; '><font size =4>Сумма без ПДВ</font></td>"
		+ "</tr>" + rows;

		String s =  " <tr style='border:hidden;'>"
		    + "<td style='border-top: 1px solid ;' colspan='2'><font size =4>Всього найменувань : " + String.valueOf(vec_parts.size())+ "</font></td>"
		    + "<td style='border-top: 1px solid ;'></td>"
		    + "<td style='border-top: 1px solid ;'></td>"
		    + "<td style='border-top: 1px solid ;'></td>"
		    + "<td style='border-top: 1px solid ;'><font size =4>Всього:</font></td>"
		    + "<td style='border-top: 1px solid ;'><font size =4>" + String.valueOf( sum_cost) + "</font></td>"
		    + "</tr><tr ><td  colspan='2'></td><td ></td>"
		    + "<td></td><td ></td><td ><font size =4>Сумма без ПДВ:</font></td><td><font size =4>" 
		    + String.valueOf( sum_nds) + "</font></td></tr></tr><tr>"
		    +  "<td  colspan='2'></td><td ></td><td ></td><td ></td><td ><font size =4>Всього с ПДВ:</font></td><td ><font size =4>"
		    + String.valueOf( sum_cost + sum_nds) +"</font></td></tr>"
		    + "<tr><td  colspan='2'></td><td ></td><td ></td><td ></td><td ></td><td ></td>"
		    + "</tr><tr><td  colspan='2'><font size =4>Від постачальника: _________</font></td>"
		    + "<td ></td><td ></td><td ></td><td ><font size =4>Отримав:________</font></td>"
		    + "<td ></td></tr></table></body></html>";
		  
		  return hS + s;
		 
	}
	
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
}
