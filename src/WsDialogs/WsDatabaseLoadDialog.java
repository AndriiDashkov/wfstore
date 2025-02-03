
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import WsControls.WsPasswordField;
import WsDatabase.WSConnect;
import WsDatabase.WsUtilSqlStatements;
import WsEvents.WsEventDispatcher;
import WsMain.*;

/**
* @author Andrii Dashkov license GNU GPL v3
*
*/
public class WsDatabaseLoadDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	protected JTextField m_path_src = new JTextField(25);
	
	private JButton m_OkButton = new WsButtonEnter(getGuiStrs("buttonOkCaption"));
	
	private JButton m_CancelButton = new WsButtonEnter(getGuiStrs("buttonCancelCaption"));
	
	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected JLabel m_infoLabel = new JLabel(" ");
	
	protected String folderName;
	
	private Forwarder forwarder = new Forwarder();
	
	protected int flag = 0;
	
	private Font m_font = WsUtils.get().getBaseFont();
	
	WsPasswordField m_pass = new WsPasswordField(true);
	
	 public  WsDatabaseLoadDialog(JFrame jf) {
		 
		super (jf, getGuiStrs("loadDatabaseDialogCaption"), true);
		
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
		
		JPanel panel_path = WsGuiTools.createHorizontalPanel();		
		
		JLabel path_lab = new JLabel (getGuiStrs("pathForLoadDatabaseLabel"));
		
		path_lab.setFont(m_font);

		panel_path.add(path_lab);
		
		panel_path.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		panel_path.add(m_path_src);
		
		panel_path.add(Box.createHorizontalStrut(6));
		
		panel_path.add(m_pathButton);
		
		panel_path.add(Box.createHorizontalGlue());
		
		JPanel panel_p = WsGuiTools.createHorizontalPanel();	
		
		panel_p.add(m_pass); panel_p.add(Box.createHorizontalGlue());
			
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
		
		m_OkButton.setEnabled(false);
		
		WsGuiTools.fixTextFieldSize(m_path_src);

		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));

		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_MAIN.add(panel_path);
		
		panel_MAIN.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panel_p.setVisible(false);
		
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
		
		//catch focus exit event
		m_path_src.addFocusListener(new PaNewPathCustomListener());
		
		//catch focus enter event
		m_path_src.addKeyListener(new  PaKeyListener());
		
		m_OkButton.addActionListener(forwarder);
		
		m_CancelButton.addActionListener(forwarder);
		
		m_pathButton.addActionListener(forwarder);
		
	
	}
	
	
	public void onOK(ActionEvent e) 
	{
		
		flag  =   verifyInfo();
		
		if ( flag == 1 ) { 	
			
			WSConnect.connect(getFullNewDatabasePath());
			
			WsUtils.NEW_CATALOG = WsUtilSqlStatements.isCatalogInDatabaseNew();
						
			dispose(); 
			
		}
	}
	/**
	 * Validation of all information entered by user
	 * @return 1 if all information in the fields of the dialog is valid and can be saved
	 */
	protected int  verifyInfo() 
	{
		int flag =1;
		


		
		//path existence and access rights validation
		String sS= WsUtils.checkFilePermisions(m_path_src.getText(),true,true);
		
		if (! sS.isEmpty() ) {
			
			m_infoLabel.setText(sS);

			flag =0;
		}
				
		if ( flag == 0 ) {
			
			m_OkButton.setEnabled(false);
			
			return 0;
		}
		else {
			
			m_OkButton.setEnabled(true);
			
			m_infoLabel.setText("");
			
			return 1;
			
		}
	}

	/**
	 * Reaction on Cancel button
	 * @param e
	 */
	public void onCancel(ActionEvent e) {

		flag = 0;
		
		dispose();
	}
	
	/**
	 * Reaction on button ...
	 * @param e
	 */
	public void onPath(ActionEvent e) {
		

	
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(getGuiStrs("dialogFileChooserTitleDataLoad"), 
				".", true, false);
		
		int result = sourceFile.showOpenDialog(m_path_src);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			if(sourceFile.getSelectedFile() != null) {
				
				String name = sourceFile.getSelectedFile().getPath();
			
				m_path_src.setText(name);	
			
				verifyInfo();
			
			}
		}
		
		flag = 2;
	}
	
	protected String getFullNewDatabasePath () {
		
		 return m_path_src.getText();
	
	}
		
	class PaNewPathCustomListener implements FocusListener
	{


		@Override
		public void focusGained(FocusEvent arg0) {}

		@Override
		public void focusLost(FocusEvent arg0) {
		
			 verifyInfo();
			
		}
	    
	}
	
	class PaKeyListener implements KeyListener
	{

		@Override
		public void keyPressed(KeyEvent arg0) {
			
			if (arg0.getExtendedKeyCode() ==  KeyEvent.VK_ENTER ) {
				
				verifyInfo();
			}
			
		}

		@Override
		public void keyReleased(KeyEvent arg0) {}

		@Override
		public void keyTyped(KeyEvent arg0) {}
	    
	}
	

	
	 /**
	  * <p>Sets tooltips for all elements</p>
	  */
	 private void setToolTips() {
		 
		//m_name.setToolTipText(getGuiStrs("nameNewDatabaseToolTip"));
		
		m_path_src.setToolTipText(getGuiStrs("pathLoadDataBaseToolTip"));
			
		m_pathButton.setToolTipText(getGuiStrs("pathfileChooserToolTip"));
							 	  
		m_OkButton.setToolTipText(getGuiStrs("okButtonToolTip"));
		
		m_CancelButton.setToolTipText(getGuiStrs("cancelButtonToolTip"));
		 
	 }
	 
	 /**
	  * Key listener to verify the text input for bad symbols &*%${@link #changedUpdate(DocumentEvent)}etc
	  * @author Andrii Dashkov license GNU GPL v3
	  *
	  */
	 class KeyEnterListener implements DocumentListener {


		@Override
		public void changedUpdate(DocumentEvent arg0) {
			verifyInfo();
		}


		@Override
		public void insertUpdate(DocumentEvent arg0) {
			verifyInfo();
		}

	
		@Override
		public void removeUpdate(DocumentEvent arg0) {
			verifyInfo();
		}
		 
		 
	 }
	 
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
		
	}
}