package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
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
import WsControls.WsPasswordField;
import WsDatabase.WSConnect;
import WsEvents.WsEventDispatcher;
import WsMain.*;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsDatabaseNewDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	protected JTextField m_name = new JTextField(25);
	
	protected JTextField m_path_folder = new JTextField(25);
	
	protected WsPasswordField m_password = new  WsPasswordField(false);  
	
	private JButton m_OkButton = new WsButtonEnter(getGuiStrs("buttonOkCaption"));
	
	private JButton m_CancelButton = new WsButtonEnter(getGuiStrs("buttonCancelCaption"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JLabel m_infoLabel = new JLabel(" ");
	
	protected String folderName;
	
	private JCheckBox m_5kodsCheck = new JCheckBox(getGuiStrs("kods5CheckBox"));
	
	private Forwarder forwarder = new Forwarder();
	
	private Font m_font = WsUtils.get().getBaseFont();
	
	 public  WsDatabaseNewDialog(JFrame jf) {
		 
		super (jf, getGuiStrs("createNewDatabaseDialogCaption"), true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		add(createGUI());
		
		setBounds(250, 150, 400, 220);
		
		pack();
		
		setResizable(false);
		 
		setToolTips();
	 }
	 
	private JPanel createGUI () {
		
		//main panel
		JPanel panel_MAIN = WsGuiTools.createVerticalPanel();

		panel_MAIN.setBorder( BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,
				WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);
		
		JPanel panel_name = WsGuiTools.createHorizontalPanel();		
		
		JLabel name_lab = new JLabel (getGuiStrs("newDatabaseNameLabel"));  
		
		name_lab.setFont(m_font);
		
		panel_name.add(name_lab);
		
		panel_name.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_name.add(m_name);
		
		panel_name.add(Box.createHorizontalGlue());
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();		
		
		JLabel path_lab = new JLabel (getGuiStrs("folderForNewDatabaseLabel"));
		
		path_lab.setFont(m_font);

		panel_path.add(path_lab);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add(m_path_folder);
		
		panel_path.add(Box.createHorizontalStrut(6));
		
		panel_path.add(m_pathButton);
		
		panel_path.add(Box.createHorizontalGlue());
		
		JPanel panel_pass = WsGuiTools.createHorizontalPanel();	
		
		panel_pass.add(m_password);
		
		panel_pass.add(Box.createHorizontalGlue());
		
		JPanel panel_check = WsGuiTools.createHorizontalPanel();	
		
		panel_check.add(m_5kodsCheck);
		
		panel_check.add(Box.createHorizontalGlue());
		
		JPanel south = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0) );
		
		JPanel panel_Ok_Cancel = new JPanel( new GridLayout( 1,2,5,0) );
		
		m_infoLabel.setFont(m_font);
		
		m_infoLabel.setForeground(Color.RED);
		
		panel_Ok_Cancel.add(m_OkButton);
		
		panel_Ok_Cancel.add(m_CancelButton);

		south.add(panel_Ok_Cancel);
				
		//mnemonics
		m_OkButton.setMnemonic(KeyEvent.VK_O);
		
		m_CancelButton.setMnemonic(KeyEvent.VK_C);
		
		WsGuiTools.fixTextFieldSize(m_path_folder);

		panel_MAIN.add(panel_name);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));	
		
		panel_pass.setVisible(false);

		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_path);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_check);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(south);
			
		setAllListeners();
		
		return panel_MAIN;
		
	}

	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_OkButton ) onOK(e);
			
			if ( e.getSource() == m_CancelButton ) onCancel(e);
			
			if ( e.getSource() == m_pathButton ) onPath(e);
			
		}
	}
	

	
	private void setAllListeners() {
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_pathButton.addActionListener(forwarder);
		
		
	}
	
	
	public void onOK(ActionEvent e) 
	{
		
		if(m_path_folder.getText() == null || m_path_folder.getText().isEmpty() ) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("folderDataNewEmptyMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		if( m_name.getText() == null || m_name.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    getMessagesStrs("nameDataNewEmptyMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}

		
		String sS= WsUtils.checkFilePermisions(m_path_folder.getText(),true,true);
		
		if (! sS.isEmpty() ) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			    sS,
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
			
			return;
		}
		
		String pass = "";//checkPassword();
		
		if(null != pass) {
		
			WSConnect.createNewDatabase(getFullNewDatabasePath (), m_5kodsCheck.isSelected());
			
			dispose(); 
		}
			
	
	}


	/**
	 * Reaction on Cancel button
	 * @param e
	 */
	public void onCancel(ActionEvent e) {

		dispose();
	}
	
	/**
	 * Reaction on button ...
	 * @param e
	 */
	public void onPath(ActionEvent e) {
		
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("dialogFolderChooserTitleDataLoad"), ".", false, false);
		
		
		int result = sourceFile.showOpenDialog(m_path_folder);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			m_path_folder.setText(name);	
			
		}

	}
	
	protected String getFullNewDatabasePath () {
		
		 return WsUtils.concatPathName(m_path_folder.getText() , m_name.getText());
	
	}
	
	 /**
	  * <p>Sets tooltips )for all elements</p>
	  */
	 private void setToolTips() {
		 
		m_name.setToolTipText(getGuiStrs("nameNewDatabaseToolTip"));
		
		m_path_folder.setToolTipText(getGuiStrs("pathNewDataBaseToolTip"));
			
		m_pathButton.setToolTipText(getGuiStrs("pathChooserToolTip"));
							 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 

	 
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
		
	}
	
	@SuppressWarnings("unused")
	private String checkPassword() {
		
		String  p = m_password.getPass();
		
		if(null == p ) {
			
			JOptionPane.showMessageDialog(
	   			    WsUtils.get().getMainWindow(),
	   			 getMessagesStrs("passNotEqualMessage"),
	   			    getMessagesStrs("messageInfoCaption"),
	   			    JOptionPane.CLOSED_OPTION);
	      	   		
			return null;
		}
		
		if(p.isEmpty()) {
			
			int res = WsUtils.showYesNoDialog(getMessagesStrs("passisEmptyMessage"));
			
			if ( 1 == res) {
			
				return "";
			}
			
			return null;
		}
		
		return p;
		
	}
}
