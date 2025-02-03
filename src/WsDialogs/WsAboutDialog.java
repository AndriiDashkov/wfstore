package WsDialogs;

import static WsMain.WsUtils.*;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import WsEvents.WsEventDispatcher;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * 
 * @author Andrii Dashkov  license GNU GPL v3
 * 
 * <p>"About" dialog</p>
 */
public class WsAboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public  WsAboutDialog  (JFrame jfrm) {
		
		super (jfrm, getGuiStrs("aboutDialogCaption"), true); 
				
		getContentPane().add(createGUI(),BorderLayout.CENTER);
		
		setBounds(250, 150, 225, 220);

		pack();
		
		setResizable(false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * @return main Gui panel
	 */
	private JPanel createGUI () {
		
		JPanel panelMain = new JPanel();

		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

		panelMain.setBorder( BorderFactory.createEmptyBorder(WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT,WsUtils.VERT_STRUT));
				
		JLabel pNameLabel = new JLabel("WFStore " + getGuiStrs("versionAbout") + " " + WsUtils.VERSION);
		
		JLabel yearLabel = new JLabel(getGuiStrs("placeAbout"));
		
		JLabel design1Label = new JLabel(getGuiStrs("codingAbout") + " " + WsGuiTools.getM1());
		
		JLabel UIdesignLabel = new JLabel(getGuiStrs("uiDesignAbout") + " " + WsGuiTools.getM2());
		
		JLabel licLabel = new JLabel(getGuiStrs("licAbout") + " , GNU GPL v3 license");
		
		panelMain.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panelMain.add(pNameLabel);
		
		panelMain.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panelMain.add(yearLabel);
		
		panelMain.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
			
		panelMain.add(design1Label);
		
		panelMain.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panelMain.add(UIdesignLabel);
		
		panelMain.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		panelMain.add(licLabel);
	
		return panelMain;
	}
	
	public void dispose() {
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
	}
}
