

package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import WsDataStruct.WsAgentData;
import WsDialogs.WsFileChooserDialog;
import WsEditTables.WsImportExcelEditTable;
import WsMain.WsGuiTools;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class  WsFileTableControl extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected  WsImportExcelEditTable m_table = null;

	protected  JButton m_insertButton = new JButton(getGuiStrs("insertFileChoserName"));

	protected JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	protected static String m_current_path = ".";
	
	protected TitledBorder m_title_border = null;
	
	boolean m_enablePopupMenu = true;

	
	protected Forwarder forwarder = new Forwarder();
	
	String m_title = "";
	
	protected JDialog m_parent = null;
	
	public  WsFileTableControl(String[] columnNames, String fileChooserLabel, boolean enablePopupMenu) {
		super();
		
		m_enablePopupMenu = enablePopupMenu;
		
		init( columnNames, fileChooserLabel);
	}

	protected  void init(String[] columnNames, String fileChooserLabel) {
		
		m_table = new  WsImportExcelEditTable(columnNames, m_enablePopupMenu);
		
		m_title = fileChooserLabel;
		
		createGUI();
		
		m_pathButton.addActionListener(forwarder);

		
	}
	
	public void setParentDialog(JDialog parent) { m_parent = parent; }
	
	protected void createGUI() {
			
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		m_title_border = BorderFactory.createTitledBorder(m_title);
		
		setBorder(m_title_border);
		
		JPanel panel1 = WsGuiTools.createVerticalPanel();

		panel1.add(m_pathButton);

		panel1.add(Box.createVerticalGlue());
		
		JScrollPane scroll = new JScrollPane(m_table);
	        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
             
        add(panel1);
        
        add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
        
        add(scroll);
             
        m_insertButton.setToolTipText(getGuiStrs("chooserInsertButtonTooltip"));

	}
	
	public Vector<WsAgentData> getData() {
		
		return m_table.getData();
	}
	
	public WsImportExcelEditTable getTableRef() {
		
		return m_table;
	}
	
	public void setTableToolTips(String tableToolTip ) {
		
		m_table.setToolTipText(tableToolTip);
		
	}
	public void setButtonToolTip(String tip ) {
		
		m_pathButton.setToolTipText(tip);
		
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			
			if ( e.getSource() == m_pathButton ) { onPath(e); }
			
		}
	}
	
	protected void onPath(ActionEvent e) {
		
		boolean multiSelection = true;
		
		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
				getGuiStrs("chooseFileDialogCaption"), m_current_path, true, multiSelection);
		
		int result = sourceFile.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			File[] files = sourceFile.getSelectedFiles();
			
			m_current_path = files[0].getParent();
				
			for(File f : files) {
				
				WsAgentData d = new WsAgentData();
				
				d.contact = f.getPath();
				
				if(d.contact != null && !d.contact.isEmpty()) {
					
					m_table.addRow(d);
				}
			}
		}
	}
	
	public void refreshTable() {
		
		m_table.refresh();
		
	}
}
