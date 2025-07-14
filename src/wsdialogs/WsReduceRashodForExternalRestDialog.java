package wsdialogs;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import wscomparators.WsDateComparator2;
import wsdatabase.WsConnect;
import wsdatabase.WsRashodSqlStatements;
import wsdatabase.WsSkladSqlStatements;
import wsdatastruct.WsPrihodPartData;
import wsdatastruct.WsRashodData;
import wsdatastruct.WsRashodPartData;
import wsevents.WsEventDispatcher;
import wsmain.WsGuiTools;
import wsmain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsReduceRashodForExternalRestDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	protected  JButton m_importButton = new JButton(getGuiStrs("buttonExportStartCaption"),
			WsUtils.get().getIconFromResource("wsimportExcel.png"));
	
	private static String m_last_path = "";
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JTextField m_path_alb = new JTextField(25);
	
	JLabel label_path = new JLabel (getGuiStrs("exportPathLabel"));
	
	Vector<WsRashodData> m_data = null;
	
	java.sql.Date m_initial_date = null;
	
	public WsReduceRashodForExternalRestDialog(JFrame jf, String caption,  Vector<WsRashodData> dt) {
		
		super (jf, caption, true);
		
		m_data = dt; 
		
		m_path_alb.setText(m_last_path);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		createGUI();
		
		setToolTips();
		
		setCustomFont();
		
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
			
		JPanel panel_button = WsGuiTools.createHorizontalPanel();
		
		panel_button.add( m_importButton);
		
		panel_button.add( Box.createHorizontalGlue());
		
		panel_main.setBorder(BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		panel_main.add(panel_path);
		
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
						
					WsUtils.showMessageDialog(getMessagesStrs("noDataBaseForExport"));
					
					return;
				}
				
				WsUtils.get();
				
				int res = -1;
				
				if(m_data.size() == 1) {
				
					res = WsUtils.showYesNoDialogLong(  getMessagesStrs("exportRashodRedCaption1") + " "
					    +  m_data.elementAt(0).number + " " +
						getMessagesStrs("exportRAshodMesCaptionVid") +
					    " " + WsUtils.dateToString(m_data.elementAt(0).date, "dd.MM.yy"),
					    " " + getMessagesStrs("exportRashodRedCaption2"));
				}
				else {
					res = WsUtils.showYesNoDialogLong(  getMessagesStrs("exportRashodRedCaption220"),
							 getMessagesStrs("exportRashodRedCaption221"));
					
				}
			      	   
				if ( 1 == res) {
				
					reducePositionsInRashod();
				
				}
			}
			else
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
			
			conn = WsConnect.connectImport(f);

		}catch(Exception e) {
			
			conn = null;
		}
		
		if(null == conn) { 
			
			WsUtils.showMessageDialog(getMessagesStrs("cantFindDatabaseExportFailed"));
			
			return null; 
			
		}
		
		return conn;
	
	}
	
	private void reducePositionsInRashod() {
		
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		if(m_data != null && !m_data.isEmpty()) {
			
			Collections.sort(m_data, new WsDateComparator2());
		}
		else {
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			return;
		}
		
		
		Connection conn = null;
		
		try {
			
			conn = getExternalConnect(getFilePath());
			
			if(null == conn) { 
				
				//WsUtils.showMessageDialog(getMessagesStrs("connectionToExtDBFailed"));
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				return; 
				
			}
			
			HashMap<Integer, Double> used_map = new HashMap<Integer, Double>();
			
			for(WsRashodData d : m_data) {
			
				Vector<WsPrihodPartData>  vec = WsSkladSqlStatements.getSkladListAvailableForDate(d.date, 1, true);
			
				for(WsRashodPartData dr :  d.rows) {
					
					for(WsPrihodPartData df :  vec) {
						
						double used = 0.0;
						
						if(df.kod == dr.kod) {
							
							Double used_rest = used_map.get(dr.kod);
							
							double rest = df.rest - (used_rest == null ? 0.0 : used_rest);
							
							 dr.req_quantity = dr.quantity - rest;
							 
							 if(dr.req_quantity < 0) {
								 
								 df.rest -= dr.quantity;
								 
								 dr.req_quantity = 0.0;  
								 
								 used = dr.quantity;
							 }
							 else {
								 
								 used = rest;
								 
								 df.rest = 0.0;
							 }
							 
							double oldValue = 0.0;
							
							if(used_map.containsKey(dr.kod)) {
								
								oldValue = used_map.get(dr.kod);
							}
							
							used_map.put(dr.kod, used + oldValue);
							 
							dr.quantity = dr.req_quantity;
							
							break;
						}	
					} 
					
				}
				
			}
			
			for(WsRashodData d : m_data) {
				
				WsRashodSqlStatements.updateRashod(d, d.rows );
			
			}
			
			//TODO 
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			conn.close();
		 
		}catch(Exception e) {
			
			try {
				if(null != conn) {
					
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					
					conn.close();
				}
			} catch (SQLException e1) {
				
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				
				e1.printStackTrace();
			}
		}
	}
	
	private void setCustomFont() {
		
		Font f = WsGuiTools.getCustomFont( );
		
		if(null == f) {
			
			return;
		}
		
		WsGuiTools.changeFont(this, f);
			
	}	
}