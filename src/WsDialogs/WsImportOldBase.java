
package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import WsControls.Ws2DatesControl;
import WsDatabase.WSAgentSqlStatements;
import WsDatabase.WSConnect;
import WsDatabase.WsPrihodSqlStatements;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsEvents.WsEventInt;
import WsMain.WsCloseFlag;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/** 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsImportOldBase extends JDialog  {
	
	private static final long serialVersionUID = 1L;

	private Forwarder forwarder = new Forwarder();
	
	private WsCloseFlag flag = WsCloseFlag.CANCEL;
	
	public JButton m_startButton;

	JCheckBox m_checkAgents = new JCheckBox(getGuiStrs("importOldAgentsCheckBox"));
	
	JCheckBox m_checkCatalog = new JCheckBox(getGuiStrs("importOldCatalogCheckBox"));
	
	JCheckBox m_checkRest = new JCheckBox(getGuiStrs("importOldRestCheckBox"));
	
	JCheckBox m_checkOldCatalog = new JCheckBox(getGuiStrs("transfCatalogCheckBox"));
	
	Ws2DatesControl  m_date_rest_prihod = new Ws2DatesControl(true);
	
	JLabel m_date_label = new JLabel(getGuiStrs("importOldBaseDateLable"));
	
	protected JTextField m_path = new JTextField(25);
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	JLabel m_path_label = new JLabel(getGuiStrs("importOldBasePathLable"));
	
	public WsImportOldBase (JFrame jfrm, String nameFrame) {
		
		super (jfrm, nameFrame, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(createGUI());
		
		setBounds(250, 150, 225, 150);
		
		init();

		pack();
		
		setResizable(false);
		
	}
	

	/**
	 * Listener for buttons reaction
	 * @author Andrii Dashkov license GNU GPL v3
	 *
	 */
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			
			if ( e.getSource() == m_startButton ) { onOK(e); }
			
			if ( e.getSource() == m_pathButton ) { onPath(e); }
			

					
		}
	}
	
	class IListener implements ItemListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		@Override
		public void itemStateChanged(ItemEvent e) {
			
			if ( e.getItemSelectable() == m_checkRest ) { 
				
				boolean flag = m_checkRest.isSelected();
				
				 m_date_rest_prihod.setEnabled(flag);
			}
			else
			if ( e.getItemSelectable() == m_checkOldCatalog ) { 
				

				 m_checkCatalog.setSelected(false);
				 
				 m_checkCatalog.setEnabled(!m_checkOldCatalog.isSelected());
			}

		}
	
	}
	

	public void onPath(ActionEvent e) {
		
		
		WsFileChooserDialog sourcePhotoFile = new WsFileChooserDialog(
				getGuiStrs("dialogFileChooserTitleDataLoad"), ".", true, false);
		
		sourcePhotoFile.setApproveButtonText(getGuiStrs("loadFileChooserName"));
		
		int result = sourcePhotoFile.showOpenDialog(m_path);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourcePhotoFile.getSelectedFile().getPath();
			
			m_path.setText(name);	
			
		}
		
	}
	
	public void onOK(ActionEvent e) {

		flag = WsCloseFlag.OK;
			
		int res = WsUtils.showYesNoDialog( getMessagesStrs("startImportOldBaseCaption"));
	      	   
		if ( 1 == res) {
	
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			
			importFromOldDatabase( m_checkAgents.isSelected(), m_checkCatalog.isSelected(),
					m_checkRest.isSelected(), m_date_rest_prihod.getSqlStartDate(),
					m_path.getText());

			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			dispose();
		
		}
		
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
		
		m_startButton = new JButton(getGuiStrs("buttonStartImportCaption"));
		
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();
		
		panel_path.add( m_path_label );   panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add( m_path );   panel_path.add(m_pathButton);
		
		JPanel panel_start = WsGuiTools.createHorizontalPanel();
		
		panel_start.add(m_startButton);   panel_start.add(Box.createHorizontalGlue());
		
		JPanel panel_date = WsGuiTools.createHorizontalPanel();
		
		panel_date.add(m_date_label);
		
		panel_date.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_date.add(m_date_rest_prihod);
		
		panel_date.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panel_path);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_check4 = WsGuiTools.createHorizontalPanel();
		
		panel_check4.add(m_checkOldCatalog);
		
		panel_check4.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panel_check4);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_check1 = WsGuiTools.createHorizontalPanel();
		
		panel_check1.add(m_checkAgents);
		
		panel_check1.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panel_check1);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_check2 = WsGuiTools.createHorizontalPanel();
		
		panel_check2.add(m_checkCatalog);
		
		panel_check2.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panel_check2);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		JPanel panel_check3 = WsGuiTools.createHorizontalPanel();
		
		panel_check3.add(m_checkRest);
		
		panel_check3.add(Box.createHorizontalGlue());
		
		panel_MAIN.add(panel_check3);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_date);

		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_start);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		setAllListeners();
		
		setToolTips();
		
		return panel_MAIN;
	}
	
	private void setAllListeners() {
		
		m_startButton.addActionListener(forwarder);
		
		m_pathButton.addActionListener(forwarder);
		
		IListener l = new IListener();
		
		m_checkRest.addItemListener(l);
		
		m_checkOldCatalog.addItemListener(l);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});
	
		
	}
	

	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
			 	  
		m_startButton.setToolTipText(getGuiStrs("importDBButtonToolTip"));
		
		m_checkCatalog.setToolTipText(getGuiStrs("importDBCatalogCheckToolTip"));
		
		m_checkAgents.setToolTipText(getGuiStrs("importDBAgentsCheckToolTip"));
		
		m_checkRest.setToolTipText(getGuiStrs("importDBRestCheckToolTip"));
		
		m_checkOldCatalog.setToolTipText(getGuiStrs("importDBRestOldToolTip"));
		 
	 }
	 

	 
	/**
	 * <p>Initiation function.Don't remove it! It must be empty, 
	 * it 's reloaded in the Edit dialog</p>
	 */
	protected void init() {
		
		m_date_rest_prihod.setCurrentStartDate();
		
		m_checkRest.setSelected(true);
		
		m_checkAgents.setSelected(true);
		
	}

	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
				
		super.dispose();
			
	}
	
	private boolean importFromOldDatabase( boolean importAgents, boolean importCatalog,
			boolean importRest, java.sql.Date dat, String dataPath ) {
		
		if(dataPath.isEmpty()) { return false;}
		
		try {
		
			Connection conn = WSConnect.connectImport(dataPath);
			
			if(null == conn) { return false; }
			
			if(importAgents) {
				
				boolean flag = WSAgentSqlStatements.importAgents(conn);
				
				if(!flag) {
					JOptionPane.showMessageDialog(
		       			    WsUtils.get().getMainWindow(),
		       			    getMessagesStrs("errorAgentsImportCaption"),
		       			    getMessagesStrs("messageInfoCaption"),
		       			    JOptionPane.CLOSED_OPTION);
				
				}
				
			}
			
			if(importCatalog) {
				
				boolean flag  = WsUtilSqlStatements.importCatalog(conn);
				
				if(!flag) {
					
					JOptionPane.showMessageDialog(
		       			    WsUtils.get().getMainWindow(),
		       			    getMessagesStrs("errorCatalogImportCaption"),
		       			    getMessagesStrs("messageInfoCaption"),
		       			    JOptionPane.CLOSED_OPTION);
				}
			}
			
			if(importRest) {
				
				boolean flag  = WsPrihodSqlStatements.importRest(conn, dat, m_checkOldCatalog.isSelected());	
				
				if(!flag) {
					
					JOptionPane.showMessageDialog(
		       			    WsUtils.get().getMainWindow(),
		       			    getMessagesStrs("errorRestImportCaption"),
		       			    getMessagesStrs("messageInfoCaption"),
		       			    JOptionPane.CLOSED_OPTION);
				}
				
			}
		
			conn.close();
			
			//reload the database
			WsEventInt event = new WsEventEnable(WsEventEnable.TYPE.DATABASE_LOADED);
	            
			WsEventDispatcher.get().fireCustomEvent(event);
			
		} catch (SQLException e) {
		
			e.printStackTrace();
			
			return false;
		}

		return true;
		
	}
	
}

