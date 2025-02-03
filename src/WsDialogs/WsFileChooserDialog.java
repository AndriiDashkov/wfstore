
package WsDialogs;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Component;
import java.awt.Container;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsFileChooserDialog extends JFileChooser {
	
	private static final long serialVersionUID = 1L;

	public  WsFileChooserDialog(String dialogTitle, String currentFolder, boolean filesOnly, boolean multiSelection) {
		super();
		
		setDialogTitle(dialogTitle);
		
		if(filesOnly) {
		
			setApproveButtonText(getGuiStrs("chooseFileChooserName"));
		
			setCurrentDirectory(new File(currentFolder));
			
			setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			setMultiSelectionEnabled(multiSelection);
			
			//workaround for ukrainian labels in JFileChooser
			for (Component c : getComponents()) {
				
				changeButtonText(c, "Cancel", getGuiStrs("cancelChooserButton"));
				
				changeLabelText(c, "Look In:", getGuiStrs("lookInChooserCaption"));
				
				changeLabelText(c, "File Name", getGuiStrs("fileNameChooserCaption"));
				
				changeLabelText(c, "Files of Type:", getGuiStrs("filterFilesChooser"));
				
			}
		}
		else {
			
			setApproveButtonText(getGuiStrs("chooseFileChooserName"));
			
			setCurrentDirectory(new File(currentFolder));
			
			setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			//workaround for ukrainian labels in JFileChooser
			for (Component c : getComponents()) {
				
				changeButtonText(c, "Cancel", getGuiStrs("cancelChooserButton"));
				
				changeLabelText(c, "Look In:", getGuiStrs("lookInChooserCaption"));
				
				changeLabelText(c, "Folder name", getGuiStrs("folderNameChooserCaption"));
				
				changeLabelText(c, "Files of Type:", getGuiStrs("filterFilesChooser"));
				
			}

		}
	
	}
	

	public void setButtonApproveText(String caption) {
		
		setApproveButtonText(caption);
	}
	
	
	private void changeButtonText (Component c, String original, String change) {

		   if (c instanceof JButton) {
			   
		       JButton b = (JButton) c;
		       
		       if (b.getText() != null && b.getText().equalsIgnoreCase(original))
		    	   
		           b.setText(change);
		       
		   } else if (c instanceof Container) {
			   
		        Container cont = (Container) c;
		        
		        for (int i = 0; i < cont.getComponents().length; i++) {
		        	
		           changeButtonText (cont.getComponent(i), original, change);
		        }
		   }
	}
	
	private void changeLabelText (Component c, String original, String change) {

		   if (c instanceof JLabel) {
			   
		       JLabel b = (JLabel) c;
		       
		       if (b.getText() != null && ( b.getText().equalsIgnoreCase(original) ||
		    		   b.getText().contains(original)) )
		    	   
		           b.setText(change);
		       
		   } else if (c instanceof Container) {
			   
		        Container cont = (Container) c;
		        
		        for (int i = 0; i < cont.getComponents().length; i++) {
		        	
		           changeLabelText (cont.getComponent(i), original, change);
		        }
		   }
	}

}
