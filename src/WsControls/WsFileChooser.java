
package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import WsMain.WsUtils;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsFileChooser extends JPanel {
	

	private static final long serialVersionUID = 1L;

	private JTextField m_path = new JTextField(50);

	private JButton m_pathButton = new JButton(getGuiStrs("captionForFileChooseButton"));
	
	private JLabel m_label = null;
	
	private Forwarder forwarder = new Forwarder();
	
	private static String m_current_path = null;
	
	private Font m_font = WsUtils.get().getBaseFont();
	
	public WsFileChooser(String label) {
		
		if(label != null) {
			
			m_label = new JLabel(label);
			
		}
		
		createGUI();
		
		m_pathButton.addActionListener(forwarder);
		
		m_pathButton.setToolTipText(getGuiStrs("osFileChooserButton"));
		
	}
	
	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {

			
			if ( e.getSource() == m_pathButton ) onPath(e);
			
		}
	}
	
	private void onPath(ActionEvent e) {
		
		JFileChooser sourceFile = null;
		
		if(m_current_path == null ) {

			sourceFile = new JFileChooser();
		}
		else {
			
			sourceFile = new JFileChooser(m_current_path);
		}
		
		sourceFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		int result = sourceFile.showOpenDialog(m_path);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			
			String name = sourceFile.getSelectedFile().getPath();
			
			m_path.setText(name);	
			
			m_current_path = name;
		}
	}
	
	private void createGUI() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		if(m_label != null) {
		
			m_label.setFont(m_font);
	
			add(m_label);
			
			add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		}
		
		add(m_path);
		
		add(Box.createHorizontalStrut(6));
		
		add(m_pathButton);
		
		add(Box.createHorizontalGlue());

		Dimension d = m_path.getMinimumSize();
		
		d.width = 100;
		
		m_path.setMinimumSize(d);
		
		m_path.setMaximumSize(d);
	
		
	}
	
	public String getFullFilePath() {
		
		return m_path.getText();
	}

}
