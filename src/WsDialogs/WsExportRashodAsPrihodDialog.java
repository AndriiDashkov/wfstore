
package WsDialogs;

import static WsMain.WsUtils.HOR_STRUT;
import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsDataStruct.WsAgentData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsPrihodData;
import WsDataStruct.WsPrihodPartData;
import WsDataStruct.WsRashodData;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsUnitData;
import WsDatabase.WSAgentSqlStatements;
import WsDatabase.WSConnect;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsRashodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsExportRashodAsPrihodDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected  JButton m_importButton = new JButton(getGuiStrs("buttonExportStartCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	private static String m_last_path = "";
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path_alb = new JTextField(25);
	
	JLabel label_path = new JLabel (getGuiStrs("exportPathLabel"));
	
	Vector<WsRashodData> m_data = null;
	 
	JRadioButton m_radio_multi = new JRadioButton(getGuiStrs("radioMultiSelectSourceCaption"));
		
	JRadioButton m_radio_single =  new JRadioButton(getGuiStrs("radioSingleSelectCaption"));
		
	ButtonGroup m_radio_group = new ButtonGroup();
	
	Ws2DatesControl  m_date = new Ws2DatesControl(true);
	
	protected JCheckBox m_sum_checkbox = new JCheckBox(getGuiStrs("sumSamePositionsKod"));
		
	public  WsExportRashodAsPrihodDialog(JFrame jf, String caption,  Vector<WsRashodData> dt) {
		
		super (jf, caption, true);
		
		m_data = dt; 
		
		m_path_alb.setText(m_last_path);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		if(!dt.isEmpty()) {
			
			m_date.setSqlStartDate(dt.elementAt(0).date);
		}
		else {
			
			m_date.setCurrentStartDate(); 
		}
		
		setToolTips();
		
		pack();
		
		setLocation(100,100);
	
	}
	
	private void createGUI() {
		
		JPanel panel_main = WsGuiTools.createVerticalPanel();	
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();	
		
		panel_path.add(label_path);
		
		panel_path.add(m_path_alb);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_pathButton );
		
		JPanel  panel_22 = WsGuiTools.createHorizontalPanel();
		
		panel_22.add(m_sum_checkbox);
		
		panel_22.add(Box.createHorizontalGlue());
		
		JPanel  panel_21 = WsGuiTools.createHorizontalPanel();
		
		panel_21.add(new JLabel(getGuiStrs("dateExportExtDB")));
		
		panel_21.add(Box.createHorizontalStrut(HOR_STRUT)); 
		
		panel_21.add(m_date); 
		
		panel_21.add(Box.createHorizontalGlue());
		
		JPanel  panel_2 = WsGuiTools.createHorizontalPanel();
		
		panel_2.setBorder(BorderFactory.createTitledBorder(getGuiStrs("multiSelectionBorderTitle")));
		
		panel_2.add(Box.createHorizontalStrut(HOR_STRUT));
		
		panel_2.add(m_radio_single);
		
		panel_2.add(m_radio_multi);
		
		panel_2.add(Box.createHorizontalGlue());
		 
		m_radio_single.setSelected(true);
		
		m_radio_group.add(this.m_radio_single); 
		
		m_radio_group.add(this.m_radio_multi); 
			
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_path);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_21);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_2);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_22);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_main.add(panel_button);
		
		panel_main.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		add(panel_main);
		
		Forwarder f = new  Forwarder();
		
		m_importButton.addActionListener(f);
		 
		m_pathButton.addActionListener(f);
		
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_importButton )  {
				
				String baza_path = m_path_alb.getText();
				
				if(baza_path == null || baza_path.isEmpty()) {
					
					JOptionPane.showMessageDialog(
		       			    WsUtils.get().getMainWindow(),
		       			 getMessagesStrs("noDataBaseForExport"),
		       			    getMessagesStrs("messageInfoCaption"),
		       			    JOptionPane.CLOSED_OPTION);
					
					return;
				}
				
				WsUtils.get();
				
				int res = -1;
				
				if(m_data.size() == 1) {
				
					res = WsUtils.showYesNoDialog(  getMessagesStrs("exportRAshodMesCaption1") + " "
					    +  m_data.elementAt(0).number + " " +
						getMessagesStrs("exportRAshodMesCaptionVid") +
					    " " + WsUtils.dateToString(m_data.elementAt(0).date, "dd.MM.yy") +
					    " " + getMessagesStrs("exportRAshodMesCaption2"));
				}
				else {
					res = WsUtils.showYesNoDialog(  getMessagesStrs("exportRAshodMesCaption22"));
					
				}
			      	   
				if ( 1 == res) {
				
					exportDataMulti();
				
				}
			}
			
			if ( e.getSource() == m_pathButton ) 	 {
				
				onPath(e);
			}
				
		}
	}
	
	public void onPath(ActionEvent e) {
		
		String path = ".";
		
		if( m_last_path != null) { path =  WsUtils.get().getPathFromString( m_last_path); }
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), path, true, false);
		
		int result = sourceFile.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			if(name != null) {
			
				 m_path_alb.setText(name);	
				 
				 m_last_path = name;
			}
			
		}
	}
	

	public String getFilePath() {
		
		return m_path_alb.getText();
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
	
	}
	
	private void setToolTips() {
	
	}
		
	private Connection	getExternalConnect(String f) {
		
		Connection conn = null;
		
		try {
			
			conn = WSConnect.connectImport(f);

		}catch(Exception e) {
			
			conn = null;
		}
		
		if(null == conn) { 
			
			JOptionPane.showMessageDialog(
       			    WsUtils.get().getMainWindow(),
       			 getMessagesStrs("cantFindDatabaseExportFailed"),
       			    getMessagesStrs("messageInfoCaption"),
       			    JOptionPane.CLOSED_OPTION);
			
			return null; 
			
		}
		
		return conn;
	
	}
	
	private void 	exportDataMulti() {
		
		Connection conn = null;
		
		try {
			
			conn = getExternalConnect(getFilePath());
			
			if(null == conn) { return; }
			
			//1 - postachalnik
			Vector<WsAgentData> a_vec = WSAgentSqlStatements.getAgentsList(conn, 1);
			
			if(a_vec.isEmpty()) {
				
				JOptionPane.showMessageDialog(
	       			    WsUtils.get().getMainWindow(),
	       			 getMessagesStrs("noAgentsExportFailed"),
	       			    getMessagesStrs("messageInfoCaption"),
	       			    JOptionPane.CLOSED_OPTION);
				
				return;
			}
			
			Vector<WsPrihodData> out_vec = new Vector<WsPrihodData>();
			
			Vector<Vector<WsPrihodPartData>> out_vec_parts = new Vector<Vector<WsPrihodPartData>>();
			
			String notFindKods = new  String();
			
			if(m_radio_single.isSelected()) {
				
				notFindKods += formPrihodData(conn, a_vec.elementAt(0).id, out_vec , out_vec_parts);
				
				if(m_sum_checkbox.isSelected()) {
					
					joinSameKods(out_vec_parts);
				}
			}
			else {
				
				notFindKods += formPrihodDataMerge(conn, a_vec.elementAt(0).id, out_vec , out_vec_parts);
				
				joinSameKods(out_vec_parts);
			}
			
			int f = 0;
			
			for(int i = 0; i < out_vec.size(); ++i) {
				
				out_vec.elementAt(i);
			
				f +=  WsPrihodSqlStatements.createNewPrihod(conn, out_vec.elementAt(i),  
					 out_vec_parts.elementAt(i));
			}
			 
			 if(f != -1 ) {
				 
				 if(notFindKods.isEmpty()) {
					JOptionPane.showMessageDialog(
		       			    WsUtils.get().getMainWindow(),
		       			 getMessagesStrs("ExportNaklPrihSuccess"),
		       			    getMessagesStrs("messageInfoCaption"),
		       			    JOptionPane.CLOSED_OPTION);
				 }
				 else {
					
					 JOptionPane.showMessageDialog(
			       			    WsUtils.get().getMainWindow(),
			       			 getMessagesStrs("ExportNaklPrihSuccessKods") + notFindKods,
			       			    getMessagesStrs("messageInfoCaption"),
			       			    JOptionPane.CLOSED_OPTION);
					 
				 }
				 
			 }
			 else {
				 
					JOptionPane.showMessageDialog(
		       			    WsUtils.get().getMainWindow(),
		       			 getMessagesStrs("ExportNaklPrihFailed"),
		       			    getMessagesStrs("messageInfoCaption"),
		       			    JOptionPane.CLOSED_OPTION);
				 
			 }
			 
			 conn.close();
		 
		}catch(Exception e) {
			
			try {
				if(null != conn) {
					conn.close();
				}
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
		}
	}
		
	
	private Vector<WsPrihodPartData> transformRashodIntoPrihodVector(Connection conn, 
			Vector<WsRashodPartData> parts_vec, String notFindKods) {
		
		
		Vector<WsPrihodPartData> vec = new Vector<WsPrihodPartData>();
		
		for(WsRashodPartData d: parts_vec) {
			
				String notFindKods_ = new String();
			
				WsPrihodPartData pr_dt = transformRashodIntoPrihod(conn, d, notFindKods_);
			
				vec.add(pr_dt);
		
				notFindKods += notFindKods_;
			
		}
		
		return vec;
		
	}
	
	private WsPrihodPartData transformRashodIntoPrihod(Connection conn, 
			WsRashodPartData d, String notFindKods) {
		
			WsPrihodPartData pr_dt = new WsPrihodPartData();
			
			pr_dt.kod = d.kod;
			
			pr_dt.vendorcode2 = String.valueOf(d.kod);
			
			WsPartType d_type = WsUtilSqlStatements.getPartTypeForKod(conn, d.kod);
			
			if(d_type != null) {
				
				WsUnitData d_unit = WsUtilSqlStatements.getUnitForId(conn, d.id_units);
				
				if(d_unit == null) {
					
					 Vector<WsUnitData> vec_unit = WsUtilSqlStatements.getUnitsList(conn);
					 
					 boolean flag = false;
					 
					 for(WsUnitData ud : vec_unit ) {
						 
						 if(ud.name.equals(d.units_name)) {
							 
							 pr_dt.id_units = ud.id;
							 
							 flag = true;
							 
							 break;
						 }
						 
					 }
					 
					 if(!flag) {
						 
						 pr_dt.id_units = vec_unit.elementAt(0).id;
					 }
					
				}
				else {
					
					pr_dt.id_units = d.id_units;
					
				}
				
				pr_dt.id_part_type = d_type.id;
				
				pr_dt.name = d.name;
				
				pr_dt.cost = d.cost;
				
				pr_dt.costwithnds = d.nds + d.cost;
				
				pr_dt.nds = d.nds;
				
				pr_dt.rest = d.quantity;
				
				pr_dt.quantity = d.quantity;
				
			}
			else {
				
				notFindKods += "\n" + d.kod + " ";
			}

			return pr_dt;
		
	}
	
	private String formPrihodData(Connection conn, int agent_id,  Vector<WsPrihodData> out_vec , 
			Vector<Vector<WsPrihodPartData>> out_vec_parts) {
		
		
		String notFindKods = new  String();
		
		for(WsRashodData rd: m_data ) {
			
			Vector<WsRashodPartData> parts_vec = WsRashodSqlStatements.getRashodPartsVector(rd.id);
			
			WsPrihodData data = new WsPrihodData();
			
			data.number = rd.number;
			
			data.date = m_date.getSqlStartDate();
			
			data.date_doc = rd.date;
			
			data.id_counterparty = agent_id;
			
			data.info = getGuiStrs("sumPeopleLab") + String.valueOf(rd.people);
			
			data.id_external = rd.id;
			
			out_vec.add(data);
			
			String notFindKods_ = new  String();
			 
			Vector<WsPrihodPartData> pr_parts = transformRashodIntoPrihodVector(conn, 
					parts_vec, notFindKods_);
			
			out_vec_parts.add(pr_parts);
			
			 if(!notFindKods_.isEmpty()) {
				 
				 notFindKods += notFindKods_;
			 }
		}
		
		return notFindKods ;
	}
		
	private String formPrihodDataMerge(Connection conn, int agent_id,  Vector<WsPrihodData> out_vec , 
			Vector<Vector<WsPrihodPartData>> out_vec_parts) {
		

		String notFindKods = new  String();
		
		WsPrihodData data = new WsPrihodData();
		
		data.number = "merged" ;
		
		data.date = m_date.getSqlStartDate();
		
		data.date_doc = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		
		data.id_counterparty = agent_id;
		
		data.id_external = 0; //sale invoces have been merged, no definite id to set
		
		out_vec.add(data);
		
		HashMap<Integer, WsPrihodPartData> parts_map_all = new HashMap<Integer, WsPrihodPartData>();
		
		int sum_people = 0;
		
		for(WsRashodData rd: m_data ) {
			
			Vector<WsRashodPartData> parts_vec = WsRashodSqlStatements.getRashodPartsVector(rd.id);
			
			sum_people += rd.people;
			
			for(int i = 0; i < parts_vec.size(); ++i) {
				
				WsRashodPartData d = parts_vec.elementAt(i);
				
				if(parts_map_all.containsKey(d.kod)) {
					
					 WsPrihodPartData dp = parts_map_all.get(d.kod);
					 
					 dp.quantity += d.quantity;
					 
					 dp.rest = dp.quantity;
				}
				else {
					
					String notFindKods_ = new String();
					
					WsPrihodPartData pr_dt = transformRashodIntoPrihod(conn, 
							d, notFindKods_);
					
					if(!notFindKods_.isEmpty()) {
						
						notFindKods += notFindKods_;
					}
					
					parts_map_all.put(pr_dt.kod, pr_dt);
					
				}
				
			}
		}
		
		data.info = getGuiStrs("sumPeopleLab") + String.valueOf(sum_people);
		
		Vector<WsPrihodPartData> vd = new Vector<WsPrihodPartData>();
		
	   for (HashMap.Entry<Integer, WsPrihodPartData> entry : parts_map_all.entrySet()) {

		  	WsPrihodPartData d = entry.getValue();
		  	
		  	vd.add(d);
		  	
	   }
	   
	   out_vec_parts.add(vd);
		
	   return notFindKods ;
	}
	
	public void joinSameKods(Vector<Vector<WsPrihodPartData>> vec) {
		
		HashMap<Integer, WsPrihodPartData> map = new HashMap<Integer, WsPrihodPartData>();
	
		for(int i = 0; i < vec.size(); ++i) {
			
			Vector<WsPrihodPartData> v = vec.elementAt(i);
			
			map.clear();
			
			for(int j = 0; j < v.size(); ++j) {
				
				WsPrihodPartData d = v.elementAt(j);
				
				int kod = d.kod;
				
				if(map.containsKey(kod)) {
					
					map.get(kod).quantity += d.quantity;
					
					map.get(kod).rest += d.quantity;
				}
				else {
					
					map.put(kod, d);
				}

			}
			
			ArrayList<Integer> list = new ArrayList<Integer>(map.keySet());
			
			Collections.sort(list);
			
			v.clear();
			
			for(Integer k : list) {
				
				v.add(map.get(k));
			}
			
		}
		
	}
	
}
