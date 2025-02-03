package WsMain;

import static WsMain.WsLog.writeLog;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import WsControls.WsMutableString;
import WsControls.WsSignsComboBox;
import WsDataStruct.WsInfoData;
import WsDataStruct.WsPartType;
import WsDataStruct.WsRashodPartData;
import WsDataStruct.WsReturnResult;
import WsDataStruct.WsSignsData;
import WsDatabase.WsUtilSqlStatements;
import WsDialogs.WsYesNoDialog;
import java.util.Arrays;
import java.util.Calendar;

/**
 * 
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsUtils {
	
	private static WsUtils _instance;
	
	public static String VERSION = "1.1.0";
	
	private static String DIR_SEP;
	
	public static final int VERT_STRUT = 12;
	
	public static final int HOR_STRUT = 6;
	
	public static final boolean HIDE_ID_COLUMNS = true;
	
	public static int BREAD_KOD_1 = 5017;
	
	public static int BREAD_KOD_2 = 5018;
	
	public static int BREAD_KOD_3 = 5020;
	
	public static int WATER_KOD = 4012;
	
	public static int EGG_KOD_1 = 2057;
	
	public static int EGG_KOD_2 = 2056;
	
	public static int CATALOG_MAX_KOD = 5023;
	
	public static int CATALOG_MIN_KOD = 1001;
	
	public static boolean CATALOG_5_DIGIT = false;
	
	public static String CATALOG_PREFIX = "k";
	
	public static boolean NEW_CATALOG = true;

	private JComboBox<String>  m_sortCombo = null;

	public static enum OSType {
		
		 WIN,
		 
		 LINUX
	}
	
	public static final OSType OS_TYPE = OSType.WIN;
	
	/**
	 * Sets differences between OS implementations
	 */
	private void initOSDependedConsts() {
		
		switch( OS_TYPE ) {
		
			default: {}
			
			case WIN: {		
				
					DIR_SEP = "\\";
					
			    	break;
			}
		
			case LINUX : {
				
					DIR_SEP = "/";
					
					break;
			}
		};
	}
	
    //DATE_FORMAT for inner keeping of dates 
	public static final String DATE_FORMAT = "dd.MM.yyyy";
	
	//GUI_DATE_FORMAT - for outer UI presentation of dates
	public static String GUI_DATE_FORMAT = "dd.MM.yyyy";
	
	public final static String NEXT_ROW = "\n";
	
	public final static String ADD_TAB = "\t";
	
	public final static String NAME_EXT_SEP = ".";
	
	public final static String NEW_FILE_EXT = "+1";
	
	public final static  int UNKNOWN_KOD  = 9999;

	private WsCentralControlPanel m_viewPanel;						
	
	private WsSettings _settings;						
	
	private JTextField m_mainLabel;						
					
	private JFrame _mainWindow;							

	private WsTokenizer _tokenizer;
	
	private String _workDir;
	
	private String _homeDir;
	
	private static ResourceBundle _menusStringSource;
	
	private static ResourceBundle _guiStringSource;
	
	private static ResourceBundle _messagesStringSource;
	
	static boolean m_debug = true;
	
	/**
	 * 
	 * @return the system depended separator for path
	 */
    public static String getSeparator() {
    	
    	return  DIR_SEP;
    }
    
    public static boolean isDebug() {
    	
    	return  m_debug;
    }
	
	
	public void setWorkDir(String dirName) {
		
		_workDir = dirName;
	}
	
	
	public String getWorkDir() {
		
		return  _workDir;
	}
	
	public void setHomeDir(String dirName) {
		
		_homeDir = dirName;
	}

	public WsCentralControlPanel getViewPanel () {
		
		return m_viewPanel;
	}
	

	private WsUtils() {
				
		initOSDependedConsts();
		
		_settings = new WsSettings();
		
		_menusStringSource = _settings.getResourceBundle("MenusStringSource");
		
		_guiStringSource = _settings.getResourceBundle("GuiStringSource");
		
		_messagesStringSource = _settings.getResourceBundle("MessagesSource");
	};
	
	public static WsUtils get() {
		
		if (_instance == null) {
			
			_instance = new WsUtils();	
		}
		return _instance;
	}
	
	public JTextField getMainLabel() {
		
		return m_mainLabel;
	}
	public void setMainLabel(String str) {
		
		m_mainLabel.setText(str);
	}
	
	public WsSettings getSettings () {
		
		return _settings;
	}
		
	/**
	 * 
	 * @return full path to the icons folder
	 */
    public String getIconsPath() {
    	
    	return new String (_workDir + DIR_SEP + "Icons" + DIR_SEP + "actions" + DIR_SEP );
    }
    /**
     * 
     * @param jfrm - new main window object
     */
    public void setMainWindow (JFrame jfrm) {
    	
    	_mainWindow = jfrm;
    }
    
    
    public void setMainWindowCaption (String s) {
    	
    	_mainWindow.setTitle(s);
    }
    
    /**
     * 
     * @return the main window
     */
    public JFrame getMainWindow () {
    	
    	return _mainWindow;
    }
    

    
    public WsTokenizer get_tokenizer () {
    	
    	return _tokenizer;
    }
        
    public String getAppPath () {
    	
    	return new String (_homeDir + DIR_SEP);
    }
    
    public String getXMLPath () {
    	
    	return new String ( getAppPath ());
    }
    
    
    public String getSettingsXMLName () {
    	
    	return new String ("_settings.xml");
    }
    
	public void initForms () {

		_tokenizer = new WsTokenizer();
		
		m_viewPanel = new WsCentralControlPanel();	
			

	}
    
	/**
	 * Initiates the all main containers of the application
	 * @throws IOException
	 */


    public static String getFileNameFromString(String fullPath ) {
    	
    	String fileName =  new String();
    	
    	if (fullPath.lastIndexOf(DIR_SEP) != -1 )  {
    		
    		fileName = fullPath.substring(fullPath.lastIndexOf(DIR_SEP)+1) ;
    	}
	
    	return fileName;
    
    }
    
    
    
    public String getPathFromString(String fullPath ) 
    {
    	
    	String path = new String();
    	
    	if (fullPath.lastIndexOf(DIR_SEP) != -1 )  {
    		
    		path = fullPath.substring(0,fullPath.lastIndexOf(DIR_SEP)+1) ;
    	}
 
    	return path;
    
    }

    public static boolean isSamePathes(String p1, String p2 ) 
    {
    	
    	Path path1 = Paths.get(p1);
    	
    	Path path2 = Paths.get(p2);
    	
    	if(path1 == null || path2 == null) {
    	//	return false;
    	}
    	   	
    	int result = path1.compareTo(path2);
    	
    	if ( result == 0 ) return true;
    	
    	return false;
   
    
    }
        
    public static String  checkFilePermisions(String checkPath, boolean checkReadable, boolean checkWritable) {
    	
    	Path p;
    	
    	try {
    		
			p = FileSystems.getDefault().getPath( checkPath );
		}
		catch( InvalidPathException exp) {
			
			 return getMessagesStrs("notValidPathMessage");  
		
		}
    	

    	Path parentPath = p;
    
    	do {
    		File f = new File(checkPath);
    		
    		if ( f.exists() ) {
    			
    			if (  checkReadable && !Files.isReadable(parentPath) ) {
    				
    				 return getMessagesStrs("notReadableOperationMessage"); 
    			}
    			
    			if (  checkWritable && !Files.isWritable(parentPath)  ) {
    				
   				 	return getMessagesStrs("notWritableOperationMessage");
    			}

    			break;
    		}
    		
    		if (parentPath == p.getRoot() ) { break;}
    		
    		parentPath = parentPath.getParent();
    		
    		if ( parentPath == null ) {
    			
    			return getMessagesStrs("notValidPathMessage");
    		}
    		
    	} while(true);

		return new String();
    	
    }
    
	public static void copyFile(String sourceFullPath, String targetFullPath) throws IOException, FileNotFoundException {
		
		File f= new File( sourceFullPath );
		
		if ( f.exists() ) {
			
			String newFullPath = concatPathName(targetFullPath, checkUniqueFileName(targetFullPath, f.getName()) );
	
			FileChannel source = null;
			
			FileChannel destination = null;
			
			FileInputStream fileInputStream = null;
			
			FileOutputStream fileOutputStream = null;
			
			try {
				
				fileInputStream = new FileInputStream(sourceFullPath);
				
				source = fileInputStream.getChannel();
				
				fileOutputStream = new FileOutputStream(newFullPath);
				
				destination = fileOutputStream.getChannel();
				
				destination.transferFrom(source, 0, source.size());
				
			} 
			finally {
	
				if(source != null) { source.close(); }
				
				if(destination != null) { destination.close(); }
				
				if(fileInputStream != null) {  fileInputStream.close(); }
				
				if(fileOutputStream != null) {  fileOutputStream.close(); } 
								
			}
						
		}
	}
    /**
     * Creates path + name; the func controls the end of the path variable
     * @param path
     * @param name
     * @return concatenated path
     */
    public static String concatPathName(String path, String name) 
    {
    	
     	if ( path.endsWith(DIR_SEP) /*|| path.endsWith(DIR_SEP2) */ ) {
    		
    		return path + name;
    	}

    	
     	return path + DIR_SEP + name;
     
    }
    /**
     * 
     * @param fullFileName - full name of the file
     * @return two strings first [0] - file name without extension, second [1] = extension of the file
     */
    public static String[] getNameAndExtension(String fullFileName)
    {
    	String[] sA = new String[2];
    	
    	 int index =fullFileName.lastIndexOf(NAME_EXT_SEP);
    	 
    	 if ( index == -1 ) {
    		 
    		 sA[0] = fullFileName;
    		 
    		 sA[1] = new String();
    		 
    	 }
    	 else {
    		 
    		sA[0] =  fullFileName.substring(0, index);
    		
    		sA[1] = fullFileName.substring(index+1);
    	 }
    	
    	 return sA;
    }

    
  
    
   
	public static String checkUniqueFileName(String checkPath, String fileNameToCheck )
	{
		String 	path = concatPathName(checkPath, fileNameToCheck ); 
		
		File f = new File(path);
		
		if ( f != null && f.exists() ) {
			
			String[] sArray = getNameAndExtension(fileNameToCheck);
			
			String newName = new String();
			
			if ( sArray[1].isEmpty() ) {
				
				newName = sArray[0] + NEW_FILE_EXT;
			}
			else {
				newName  = sArray[0] + NEW_FILE_EXT+NAME_EXT_SEP+sArray[1];
			}
			fileNameToCheck = checkUniqueFileName(checkPath, newName);
		}
		
		return  fileNameToCheck;
	}
	
	//gets international strings
   public static String getMenusStrs(String name) {
	   	   
	  return  _menusStringSource.getString(name);
   }
   
	//gets international strings
   public static String getGuiStrs(String name) {
	   	   
	  return  _guiStringSource.getString(name);
   }
   
   
   public static String getMessagesStrs(String name) {
	   	   
	  return  _messagesStringSource.getString(name);
   }
	
  public static String dateToString(Date dat, String format ) {
	  
	if ( dat == null || format.isEmpty() ) { return "00-00-00"; }
	
	SimpleDateFormat dateFormat = new SimpleDateFormat(format, WsUtils.get().getSettings().getCurrentLocale() );
	
	dateFormat.setLenient(false);
	
	return dateFormat.format( dat );
	
  }
  
  
  public static String dateSqlToString(java.sql.Date dat, String format ) {
	  
	if ( dat == null || format.isEmpty() ) { return "00-00-00"; }
	
	SimpleDateFormat dateFormat = new SimpleDateFormat(format,  WsUtils.get().getSettings().getCurrentLocale());
	
	dateFormat.setLenient(false);
	
	String s = dateFormat.format( dat );
	
	return s;
	
  }
  
  public static Date stringToDate(String dat,String format) {
	  
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		
		dateFormat.setLenient(false);
		
		try {
			
			Date d = dateFormat.parse( dat );
			
			return  d;
			
		} catch (ParseException e) {
			
			writeLog("ParseException :  " + NEXT_ROW, e, true, false, true);
			
			return null;
		}
  }
  
   
	/**
	 * @return full path to the log file
	 */
	public String getLogFullPath() {
			
		return getWorkDir() + DIR_SEP + "log_pa.txt";
	}


	/**
	 * @return  combo box - this is the single main combo box, the direct link to it is useful
	 */
	public JComboBox<String> getSortCombo(){  return m_sortCombo; }
	/**
	 * <p>Sets the link to the main sort combo box</p>
	 * @param combo - main sortin combo box. The direct link to it is very convinient 
	 */
	public void setSortCombo(JComboBox<String> combo) { m_sortCombo = combo;	}


	/**
	 * @return true if the application is in debug view 
	 */
	public boolean isDebugView() {
		
		return false;
	}
	
	/**
	 * <p>Sets the wait cursor globaly, controls previous value of cursor</p>
	 */
	public void setCursor(int currentCursorType, int typeToSet) {
		
		 if(currentCursorType != typeToSet) {
			 
			 getMainWindow().setCursor(Cursor.getPredefinedCursor(typeToSet));
		 }	
	}
	/**
	 * <p>Resets the wait cursor to default cursor globaly, controls previous value of the cursor</p>
	 */
	public void resetCursor(int previousCursorType)
	{			 
		getMainWindow().setCursor(Cursor.getPredefinedCursor(previousCursorType));
	}
	
	public int getCurrentCursor() {
		 
		 return getMainWindow().getCursor().getType();

	}
	/**
	 * 
	 * @param c - component to set fixed size
	 */
	public void setFixedSizeBehavior(JComponent c)
	{
		Dimension size = c.getPreferredSize();
		
		size.width = c.getMaximumSize().width;
		
		c.setMaximumSize(size);
	}
	/**
	 * @return the base font which is used in all GUI forms and windows
	 */
	public Font getBaseFont()
	{		
		return new Font(Font.DIALOG, Font.PLAIN , 12);
	}
	public Font getLargeFont()
	{		
		return new Font(Font.SANS_SERIF, Font.PLAIN , 14);
	}
	

	/**
	 * @return the full path to the folder with help files
	 */
	public String getPathToHelpFiles(){ 
		
		String p = getWorkDir() + DIR_SEP + "man" + DIR_SEP;
		
		return  p;
	}
		
		/**
		 * Sets the font for all components inside the component "comp"
		 * @param comp - top of the tree
		 * @param f - font to set
		 */
		public static void setComponentsFont ( Component comp, Font f )
		{
		    comp.setFont(f);
		    
		    if (comp instanceof Container)
		    {
		        for (Component child : ( (Container)comp ).getComponents () )
		        {
		        	setComponentsFont( child, f );
		        }
		    }
		}
	
		public static boolean isFileExists(String filePathString) {
			
			if(filePathString == null) return false; 
			
			File f = new File(filePathString);

			return f.exists();
	
		}
		
		
		public static boolean isFolderExists(String filePathString) {
			
			if(filePathString == null || filePathString.isEmpty()) return false; 
			
			File f = new File(filePathString);

			return f.exists() && f.isDirectory();
	
		}
		
		
		public static String getStringFromDoubleReport(double d) {
			
			if (d == 0.0) return "";
			
			return String.valueOf(d);
		}
		
		
		public static java.sql.Date sqlDatePlusDays(java.sql.Date date, int days) {
	
			if(days == 0) { return date; }
			
		    Calendar c = Calendar.getInstance();
		    
		    c.setTime(new java.util.Date(date.getTime()));
		    
		    c.add(Calendar.DATE, days);
		    
		    Date d = c.getTime();
		    
		    return new java.sql.Date(d.getTime());
		    
		}
		
		public static java.sql.Date sqlDatePlusDays(java.util.Date date, int days) {
			
		    Calendar c = Calendar.getInstance();
		   
		    c.setTime(date);
		    
		    c.add(Calendar.DATE, days);
		    
		    Date d = c.getTime();
		    
		    return new java.sql.Date(d.getTime());
		    
		}
		
		public static java.sql.Date sqlDatePlusMonth(java.util.Date date, int month) {
			
		    Calendar c = Calendar.getInstance();
		    
		    c.setTime(date);
		    
		    c.add(Calendar.MONTH, month);
		    
		    Date d = c.getTime();
		    
		    return new java.sql.Date(d.getTime());
		    
		}
		
		public static String getDF(double number) {
			
			if(Math.abs(number) < getRZL()) return "";
			
			DecimalFormat numberFormat = new DecimalFormat("#.000");
			
			if(number < 0.001) {
				
				numberFormat = new DecimalFormat("#.00000");
			}
	
			return numberFormat.format(number);
			
			
		}
		
		public static String getDF_0(double number) {
			
			if(Math.abs(number) < getRZL()) return "0,0";
	
			DecimalFormat numberFormat = new DecimalFormat("#.000");
			
			return numberFormat.format(number);
			
			
		}
		
		public static String getDF_for_tables(double number) {
			
			if(Math.abs(number) < getRZL()) return "0.0";
	
			DecimalFormat numberFormat = new DecimalFormat("#0.0000");
			
			//numberFormat.getDecimalFormatSymbols().setDecimalSeparator('.');
			
			return numberFormat.format(number).replace(",", ".");
			
			
		}
		
		//signs - round to number of signs after the point
		public static double getDF_fix(double number, int signs) {
			
			if(Math.abs(number) < getRZL()) return 0.0;
			
			BigDecimal result = new BigDecimal(number);
			
			result = result.setScale(signs, RoundingMode.CEILING);
			

			return result.doubleValue();
			
		}
		
		public static String getDF_fix_str(double number, int signs) {
			
			double d = getDF_fix(number, signs);
			
			
			DecimalFormat numberFormat = new DecimalFormat("#.000");
			
			return numberFormat.format(d);
			
			
		}
		
		
		public static String getDF_fix_to_String(double number, int signs) {
			
			double d = getDF_fix(number, signs);
			
			return String.valueOf(d);
			
			
		}
		
		
		public static int getUnsignedIntegerFromString(String s, String message) {
			
			int i = -1;
			
			try {
				
				i = Integer.valueOf(s);
			
			}
			catch(NumberFormatException  ex) {
					
			}
			
			if(message != null && i < 0) {
				
				JOptionPane.showMessageDialog(
	       			    WsUtils.get().getMainWindow(),
	       			    message,
	       			    getMessagesStrs("messageInfoCaption"),
	       			    JOptionPane.CLOSED_OPTION);
			}
			
			return i;
		}
		
	
		
		
		public static java.util.Date getFirstDayOfTheWeek(java.util.Date  dt) {
			
			Calendar calendar = Calendar.getInstance();
			
			calendar.clear();
			
			calendar.setTimeInMillis(dt.getTime());
			
			//compensation of the fact that the start of a week in Java is SUNDAY
			if(calendar.get(Calendar.DAY_OF_WEEK) == 1) {
				
				calendar.add(Calendar.DATE, -1);
				
			}
			
			while (calendar.get(Calendar.DAY_OF_WEEK) > calendar.getFirstDayOfWeek()) {
				
			    calendar.add(Calendar.DATE, -1); // Substract 1 day until first day of week.
			    
			}

			return new java.util.Date(calendar.getTimeInMillis());
			
			
		}
		
		public static java.sql.Date getFirstDayOfTheWeekSqlDate(java.sql.Date  dt) {
		
			java.util.Date dt_ = getFirstDayOfTheWeek(new java.util.Date(dt.getTime()));
			
			return new java.sql.Date(dt_.getTime());

		}
		
		
		public static java.sql.Date getDate(int day, int month, int year) {
			
			Calendar calendar = Calendar.getInstance();
			
			calendar.clear();
			
			calendar.set(year, month -1, day);
			
			return new java.sql.Date(calendar.getTimeInMillis());

		}
		
		public  ImageIcon getIconFromResource(String filename) {
			
			java.io.InputStream stream;
			
			try {
				
				stream = getClass().getResourceAsStream("/resources/icons/" + filename);
				
				if (stream == null) return new ImageIcon();
				
				return new ImageIcon(ImageIO.read(stream));
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
				return new ImageIcon();
			}
		}
		
		public static int compareDates(Date d1, Date d2) {
			
			Calendar c1 = Calendar.getInstance();
			
			c1.setTime(d1);
			
			int year1 = c1.get(Calendar.YEAR);
			
			int month1 = c1.get(Calendar.MONTH);
			
			int day1 = c1.get(Calendar.DAY_OF_MONTH);
			
			Calendar c2 = Calendar.getInstance();
			
			c2.setTime(d2);
			
			int year2 = c2.get(Calendar.YEAR);
			
			int month2 = c2.get(Calendar.MONTH);
			
			int day2 = c2.get(Calendar.DAY_OF_MONTH);
			
			if(year1 < year2) { 
				return -1;
			}
			else if(year1 > year2) {
				
				return 1;
			}
			else {
				
				if(month1 < month2) { 
					return -1;
				}
				else if(month1 > month2) {
					
					return 1;
				}
				else {
					
					if(day1 < day2) { 
						return -1;
					}
					else if(day1 > day2) {
						
						return 1;
					}
					else {
						
						return 0;
					}
				}
			}
		}
		
		public String ifTheDaySpecific(Date d) {
			
			Calendar c = Calendar.getInstance();
			
			int year = c.get(Calendar.YEAR); 
			
			c.clear();
			
			c.set(year, 9, 1);
			
			Date d1 = c.getTime();
			
			if(compareDates(d, d1) == 0) { return getMessagesStrs("specMessage1"); }
			
			c.clear();
			
			c.set(year, 11, 25);
			
			d1 = c.getTime();
			
			if(compareDates(d, d1) == 0) { return getMessagesStrs("specMessage2"); }
			
			c.clear();
			
			c.set(year, 7, 24);
			
			d1 = c.getTime();
			
			if(compareDates(d, d1) == 0) { return getMessagesStrs("specMessage3"); }
			
			return null;
		}
		
		//relative zero level for the store
		//all numbers below this level are treated as 0
		public static double getRZL() {
			
			return 0.00001;
		}
		//sort
		public static String getFirmName() {
			
		  String firm_name = "-----";
			  
			  Vector<WsInfoData>  v_info = WsUtilSqlStatements.getInfoDataList();
				
			  if(v_info.size() != 0) {
				
					firm_name = v_info.elementAt(0).name;
			   }
			  
			  return firm_name;
			
		}
		
		public static double getNdsCoeff() {
			
			  Vector<WsInfoData> vec_info = WsUtilSqlStatements.getInfoDataList();
			  
			  double nds_coeff = 1.2;
			    
			  if(vec_info != null && !vec_info.isEmpty()) {
			    	
			    	nds_coeff = vec_info.elementAt(0).nds;
			    	
			    	//can't be zero
			    	if(nds_coeff == 0.0) { nds_coeff = 1.2;}
			  }
			  
			  return nds_coeff;
			
		}
		
		
		public static int showYesNoDialog(String text) {
			
			   WsReturnResult res = new WsReturnResult();
         	  
        	   WsYesNoDialog d = new WsYesNoDialog (WsUtils.get().getMainWindow(),text, res);
        	   
        	   d.setVisible(true);
			
			  return res.result;
			
		}
		
		
		public static void showMessageDialog(String text) {
			
			JOptionPane.showMessageDialog(WsUtils.get().getMainWindow(),
       			 text, getMessagesStrs("messageInfoCaption"), JOptionPane.CLOSED_OPTION);
			
		}
		
		
		 public void initSettings () throws IOException  {
		    	
				_settings.load_Parameters(getXMLPath().concat(getSettingsXMLName()));
				
		  }
		 
		 
		 public static boolean isMonday(java.sql.Date dt) {
			 
				Calendar calendar = Calendar.getInstance();
				
				calendar.clear();
				
				calendar.setTimeInMillis(dt.getTime());
				
				return calendar.get(Calendar.DAY_OF_WEEK) == 2;
					
		 }
		 
		 
		 public static int getUnknownKatalogKod() {
			 
			 WsPartType d = WsUtilSqlStatements.getPartTypeForKod(UNKNOWN_KOD);
			 
			 if(null == d) {
				 
				 d = new WsPartType();
				 
				 d.kod = UNKNOWN_KOD;
				 
				 d.name = getGuiStrs("knNotFound");
				 
				 WsUtilSqlStatements.insertPartType(d);
				 
			 }
			 
			return UNKNOWN_KOD;
					
		 }
		 
		 
		public static Vector<WsRashodPartData> mergeSameCodes(Vector<WsRashodPartData> vec) {
				
				HashMap<Integer, WsRashodPartData> map = new HashMap<Integer, WsRashodPartData>();
				
				for(int i = 0; i < vec.size(); ++i) {
							
					WsRashodPartData d1 = vec.elementAt(i);
					
					int kod = d1.kod;
					
					if(map.containsKey(kod)) {
						
						WsRashodPartData d = map.get(kod);
						
		        		d.quantity +=  d1.quantity;
		        		
					}
					else {
						
						map.put(kod, d1);
					}
							
				}
				
				Vector<WsRashodPartData> v= new Vector<WsRashodPartData>();
				
				Integer[] keys = map.keySet().toArray(new Integer[0]);
				
				Arrays.sort(keys);
			
				for (Integer k : keys) {
					
				   v.add( map.get(k));
				                  
				}

				map.clear();
				
				return v;
		}
	
		
		public static String getNaymenuvannya(int i) {
			
			int m = 1;
			
			while(true) {
				
				int k = (int)(i/m);
		
				if(k < 10) {
					
					if(i > 10) { k = i - k*m;  }
					
					if(k == 0 || k > 4) {
						
						return getGuiStrs("naymen1");
					}
					else {
						
						return getGuiStrs("naymen2");
						
					}
					
				}
				
				m *= 10;
				
				if(m > 10000000) { break; }
			}
			
			return getGuiStrs("naymen2");
			
		}
		
		
		public static Vector<WsSignsData> getInfoPidp(WsSignsComboBox c0, WsSignsComboBox c1 ) {
			
			Vector<WsSignsData> d0_vec =  new  Vector<WsSignsData>();
			
			if(c0.listSize() == 0) {
				
				  d0_vec.add(new WsSignsData());
				    
				  d0_vec.add(new WsSignsData());

			}
			else {
				
				WsSignsData d0 = c0.getSelectedSignData();
				
				WsSignsData d1 = c1.getSelectedSignData();

				d0_vec.add(d0);
				 
				d0_vec.add(d1);
				
			}
			
			return d0_vec;
			 
		}
		
		
		public static int getDaysBetweenDates(java.sql.Date start_date, java.sql.Date end_date) {
			
			
			Period p = Period.between(start_date.toLocalDate(), end_date.toLocalDate());
			
			return p.getDays();
			
			
		}
		
		public static int[] getYearMonthDay(java.sql.Date dt) {
			
			Calendar calendar = Calendar.getInstance();
			
			calendar.clear();
			
			calendar.setTimeInMillis(dt.getTime());
			
			int[] ymd = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) };
		
		    return ymd;
		}
		
		
		
		public String showFileChooser(String extension, Component parent, WsMutableString excel_save_folder) {
			
			JFileChooser sourceFile = null;
			
			sourceFile = new JFileChooser();
			
			sourceFile.setDialogTitle(getGuiStrs("excelSaveFileDialogTitle"));
			
			sourceFile.setApproveButtonText(getGuiStrs("saveFileChooserName"));
		
			sourceFile.setCurrentDirectory(new File(excel_save_folder.toString()));
			
			sourceFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			//workaround for ukrainian labels in JFileChooser
			for (Component c : sourceFile.getComponents()) {
				
				changeButtonText(c, "Cancel", getGuiStrs("cancelChooserButton"));
				
				changeLabelText(c, "Look In:", getGuiStrs("lookInChooserCaption"));
				
				changeLabelText(c, "File Name", getGuiStrs("fileNameChooserCaption"));
				
				changeLabelText(c, "Files of Type:", getGuiStrs("filterFilesChooser"));
				
			}

			int result = sourceFile.showOpenDialog(parent);
		
			if (result == JFileChooser.APPROVE_OPTION) {
				
				String nm = sourceFile.getSelectedFile().getPath();
				
				excel_save_folder.set(sourceFile.getSelectedFile().getParentFile().getPath());
			
				if(nm.isEmpty()) { nm = "import_report";  excel_save_folder.set(".");   }
				
				if (!nm.endsWith("." + extension)) {
					
					nm += "." + extension;
				}
			
				return nm;
			}
			
			return null;	
		}
		
		public String showFolderChooser(String extension, Component parent, WsMutableString excel_save_folder) {
			
			JFileChooser sourceFile = null;
			
			sourceFile = new JFileChooser();
			
			sourceFile.setDialogTitle(getGuiStrs("excelSaveFolderDialogTitle"));
			
			sourceFile.setApproveButtonText(getGuiStrs("saveFileChooserName"));
		
			sourceFile.setCurrentDirectory(new File(excel_save_folder.toString()));
			
			sourceFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			//workaround for ukrainian labels in JFileChooser
			for (Component c : sourceFile.getComponents()) {
				
				changeButtonText(c, "Cancel", getGuiStrs("cancelChooserButton"));
				
				changeLabelText(c, "Look In:", getGuiStrs("lookInChooserCaption"));
				
				changeLabelText(c, "Folder name", getGuiStrs("folderNameChooserCaption"));
				
				changeLabelText(c, "Files of Type:", getGuiStrs("filterFilesChooser"));
				
			}

			int result = sourceFile.showOpenDialog(parent);
		
			if (result == JFileChooser.APPROVE_OPTION) {
				
				String nm = sourceFile.getSelectedFile().getPath();
				
				excel_save_folder.set(sourceFile.getSelectedFile().getPath());
			
				if(nm.isEmpty()) { nm = ".";  excel_save_folder.set(".");   }
			
				return nm;
			}
			
			return null;	
		}
		
		
		public String excelSaveFileChoose( Component parent, WsMutableString  excel_save_folder) {
			
			return showFileChooser("xlsx", parent, excel_save_folder);	
		}
		
		public String excelRasklSaveFileChoose( Component parent, WsMutableString excel_save_folder) {
			
			return showFileChooser("xlsm", parent, excel_save_folder);	
		}
		
		public String excelSaveFolderChoose(Component parent, WsMutableString  excel_save_folder) {
			
			return showFolderChooser("xlsx", parent, excel_save_folder);	
		}
		
		public String jsonSaveFileChoose(Component parent, WsMutableString  excel_save_folder) {
			
			return showFileChooser("json", parent, excel_save_folder);	
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
		
		//workaround for new catalog kod ==
		public static boolean isKodEqual(int kod1, int kod2) {
			
			int tmp1 = kod1;
			
			if(kod1 > 9999) {
				
				tmp1 = kod1 - 10000;
				
			}
			
			int tmp2 = kod2;
			
			if(kod2 > 9999) {
				
				tmp2 = kod2 - 10000;
				
			}
			
			return tmp1 == tmp2;
			
		}
		
		public static void changePredefinedKods() {
			
			if(CATALOG_5_DIGIT) {
			
				 BREAD_KOD_1 = 15017;
				
				 BREAD_KOD_2 = 15018;
				
				 BREAD_KOD_3 = 15020;
				
				 WATER_KOD = 14012;
				
				 EGG_KOD_1 = 12057;
				
				 EGG_KOD_2 = 12056;
				
				 CATALOG_MAX_KOD = 15023;
				
				 CATALOG_MIN_KOD = 11001;
			
			}
		}
}
