package WsMain;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Some useful tools to work with appearance of components
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsGuiTools {

	private static String m1 = "anddkov"; 
	
	private static String m2 = "nddkharkov"; 
	
	/**
	 * Makes the same left and right margins for buttons in array
	 * @param buttons - array of buttons
	 */
	public static void createRecommendedMargin(JButton[] buttons) {
		
		for (int i=0; i < buttons.length; i++) {
			
			Insets margin = buttons[i].getMargin();
			
			margin.left = WsUtils.VERT_STRUT;
			
			margin.right = WsUtils.VERT_STRUT;
			
			buttons[i].setMargin(margin);
		}
	}

	/**
	 * Makes components in array to be the same size , the size is determined as maximum
	 * size among all components in the array
	 * @param comps - array of components
	 */
	public static void makeSameSize(JComponent[] comps) {
		
		int[] sizes = new int[comps.length];
		
		for (int i=0; i<sizes.length; i++) {
			
			sizes[i] = comps[i].getPreferredSize().width;
		}

		int maxSizePos = maxPosition(sizes);
		
		Dimension maxSize = comps[maxSizePos].getPreferredSize();

		for (int i=0; i<comps.length; i++) {
			
			comps[i].setPreferredSize(maxSize);
			
			comps[i].setMinimumSize(maxSize);
			
			comps[i].setMaximumSize(maxSize);
		}
	}

	/**
	 * Fixes the endless height of some components
	 * @param f
	 */
	public static void fixTextFieldSize(JTextField f) {
		
		Dimension size = f.getPreferredSize();

		size.width = f.getMaximumSize().width;

		f.setMaximumSize(size);
	}
	
	/**
	 * makes the component to be normal, not stretched
	 * @param c - component to fix height for it
	 */
	public static void fixComponentSize(JComponent c) {
		
		Dimension size = c.getPreferredSize();
		
		size.width = c.getMaximumSize().width;
		
		c.setMaximumSize(size);
	}
	
	/**
	 * makes the component to be normal, not stretched
	 * @param c - component to fix height for it
	 */
	public static void fixComponentSize(JComponent c, int width) {
		
		Dimension size = c.getPreferredSize();
		
		size.width = width;
		
		c.setMaximumSize(size);
	}
	
	public static void setFixedSize(JComponent c, int s) {
		
		Dimension size = new Dimension(s,s);
		
		c.setMaximumSize(size);
		
		c.setMinimumSize(size);
		
		c.setPreferredSize(size);
	}
	
	/**
	 * 
	 * @param c - component to set a new preferred width
	 * @param width - new value of preferred width
	 */
	public static void setComponentPreferredWidth(JComponent c, int width) {
		
		Dimension size = c.getPreferredSize();
		
		size.width = width;
		
		c.setPreferredSize(size);
	}
	/**
	 * 
	 * @param c - component to set a new minimum width
	 * @param width - new value of minimum width
	 */
	public static void setComponentMinimumWidth(JComponent c, int width) {
		
		Dimension size = c.getMinimumSize();
		
		size.width = width;
		
		c.setMinimumSize(size);
	}
	
	/**
	 * 
	 * @param c - component to set a new fixed width
	 * @param width - new value of fixed width
	 */
	public static void setComponentFixedWidth(JComponent c, int width) {
		
		Dimension size = c.getMinimumSize();
		
		size.width = width;
		
		c.setMinimumSize(size);
		
		size = c.getMaximumSize();
		
		size.width = width;
		
		c.setMaximumSize(size);
		
		c.setPreferredSize(size);
	}

	public static void setComponentFixedHeight(JComponent c, int height) {
		
		Dimension size = c.getMinimumSize();
		
		size.height = height;
		
		c.setMinimumSize(size);
		
		size = c.getMaximumSize();
		
		size.height = height;
		
		c.setMaximumSize(size);
		
		Dimension psize = c.getPreferredSize();
		
		psize.height = height;
		
		c.setPreferredSize(psize);
	}
	
	public static void fixComponentHeightToMin(JComponent c) {
		
		Dimension size1 = c.getMinimumSize();
		
		Dimension size2 = c.getMaximumSize();
		
		size2.height = size1.height;
		
		c.setMaximumSize(size2);
		
		Dimension psize = c.getPreferredSize();
		
		psize.height = size1.height;
		
		c.setPreferredSize(psize);
	}
	
	
	
	/**
	 * 
	 * @param array
	 * @return the position of max element
	 */
	private static int maxPosition(int[] array) {
		
		int max = 0;
		
		for (int k = 1; k < array.length; k++) {
			
			if (array[k] > array[max]) { 
				
				max = k; 
			}
		}
		return max;
	}
	
	/**
	 * 
	 * @return the new vertical panel
	 */
	public static JPanel createVerticalPanel() {
		
		JPanel pl = new JPanel();
		
		pl.setLayout(new BoxLayout(pl, BoxLayout.Y_AXIS));
		
		return pl;
	}
	
	/**
	 * Creates horizontal panel
	 * @return new horizontal panel
	 */
	public static JPanel createHorizontalPanel() {
		
		JPanel pl = new JPanel();
		
		pl.setLayout(new BoxLayout(pl, BoxLayout.X_AXIS));
		
		return pl;
	}
	/**
	 * 
	 * @param comps
	 * @param align
	 */
	public static void setGroupAlignmentX(JComponent[] comps, float align) {
		
		for (int i=0; i<comps.length; i++) {
			
		comps[i].setAlignmentX(align);
		}
	}
	
	/**
	 * 
	 * @param comps
	 * @param align
	 */
	public static void setGroupAlignmentY(JComponent[] comps, float align) {
		
		for (int i=0; i<comps.length; i++) {
			
		comps[i].setAlignmentY(align);
		}
	}
	
	public static String getM1() {
		
		return m1 + "@gmail.com";
	}
	
	public static String getM2() {
		
		return m2 + "@gmail.com";
	}
	
	public static void enableComponents(Container container, boolean enable) {
		
		Component[] components = container.getComponents();
		
		for (Component component : components) {
			
			component.setEnabled(enable);
			
			if (component instanceof Container) {

			enableComponents((Container)component, enable);
			
			}
		}
	}
	
}