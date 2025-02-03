
package WsReports;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import WsControls.Ws2DatesControl;
import WsControls.WsMutableString;
import WsDialogs.WsFileChooserDialog;
import WsEvents.WsEventDispatcher;
import WsMain.WsGuiTools;
import WsMain.WsUtils;
import static WsMain.WsUtils.getGuiStrs;
import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Vector;

/**
 * The base class for all reports in the application.
 * Contains the basic controls: increase|decrease buttons, export buttons, etc.
 * @author Andrii Dashkov license GNU GPL v3
 *  
 */
public class WSReportViewer extends JDialog {
	
	private static final long serialVersionUID = 1L;
	
	protected JEditorPane m_viewer = new JEditorPane();

	private JButton m_zoomIn = new JButton("+", WsUtils.get().getIconFromResource( 
			"wszoomout.png"));
	
	private JButton m_zoomOut = new JButton("-", WsUtils.get().getIconFromResource( 
			"wszoomin.png"));
	
	private JButton m_print = new JButton(WsUtils.get().getIconFromResource("wsprint.png"));
	
	Ws2DatesControl  m_date = new Ws2DatesControl(false);
	
	protected JLabel m_date1_label = new JLabel(getGuiStrs("dateRangeReportLabel"));
	
	protected JLabel m_date2_label = new JLabel(getGuiStrs("dateRange2ReportLabel"));
	
	protected JButton m_genButton = new JButton(getGuiStrs("startReportReportLabel"),
			WsUtils.get().getIconFromResource("wsreportgeneration.png"));
	
	protected JPanel m_control_panel = WsGuiTools.createHorizontalPanel();
	
	protected JPanel m_control_panel2 = WsGuiTools.createHorizontalPanel();
	
	protected JButton m_righPageButton = new JButton(WsUtils.get().getIconFromResource( 
			"wsrightpage.png"));
	
	protected JButton m_leftPageButton = new JButton(WsUtils.get().getIconFromResource( 
			"wsleftpage.png"));
	
	protected JButton m_saveButton = new JButton("HTML", WsUtils.get().getIconFromResource( 
			"wsexporthtml.png"));
	
	protected JButton m_saveExcelButton = new JButton("Excel", WsUtils.get().getIconFromResource( 
			"wsexportexcel.png"));
	
	protected int current_font_size = 4;
	
	String m_currentText = null;
	
	protected int currentPage = 0;
	
	protected int pagesNum = 0;
	 
	protected Vector<String> m_html_pages;
	
	String m_name = null;
	
	protected JScrollPane m_ScrollPane = null;
	
	protected static WsMutableString  m_excel_save_folder =  new  WsMutableString(".");
	
	public WSReportViewer(JFrame f, String nameFrame) {
		
		super (f, nameFrame, true);
		
		m_name = nameFrame;
		
		setDialogCaption();
	
		createGui();
		
		setBounds(100, 100, 1100, 850);
		
		m_viewer.setContentType("text/html");
		
		setListeners( );
	}
	
	
	private void setListeners( ) {
		
		m_zoomIn.addActionListener(new ActionListener() {
	        
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(m_html_pages == null || m_html_pages.isEmpty()) { return; }
				
				if((current_font_size + 1) == 8) {return;}
				
				current_font_size += 1;
				
				String r = "<font size ="+ String.valueOf(current_font_size - 1) + ">";
				
				String r_ = "<font size ="+ String.valueOf(current_font_size) + ">";
				
				String r1 = "<font size ="+ String.valueOf(current_font_size) + ">";
				
				String r1_ = "<font size ="+ String.valueOf(current_font_size + 1) + ">";
				
				for(int i = 0; i < m_html_pages.size(); ++i) {
					
					String t = m_html_pages.elementAt(i);
					
					String t_ = t.replaceAll(r1, r1_);
					
					t_ = t_.replaceAll(r, r_);
					
					m_html_pages.set(i, t_);
					
				}
				
				m_viewer.setText(m_html_pages.elementAt(currentPage -1));
				
				m_viewer.setSelectionStart(0);
            	
            	m_viewer.setSelectionEnd(0);
			}
    });
	
	m_zoomOut.addActionListener(new ActionListener() {
		
		@Override
        public void actionPerformed(ActionEvent e) {
			
			if(m_html_pages == null || m_html_pages.isEmpty()) { return; }
			
			if((current_font_size - 1) == 0 ) {return;}
			
			String r1 = "<font size ="+ String.valueOf(current_font_size + 1) + ">";
			
			String r1_ = "<font size ="+ String.valueOf(current_font_size) + ">";
			
			current_font_size -= 1;
			
			String r = "<font size ="+ String.valueOf(current_font_size + 1) + ">";
			
			String r_ = "<font size ="+ String.valueOf(current_font_size) + ">";
			
			if(m_html_pages == null) { return; }
	
			for(int i = 0; i < m_html_pages.size(); ++i) {
				
				String t = m_html_pages.elementAt(i);
				
				String t_ = t.replaceAll(r1, r1_);
				
				t_ = t_.replaceAll(r, r_);
				
				m_html_pages.set(i, t_);
				
			}
			
			m_viewer.setText(m_html_pages.elementAt(currentPage -1));
			
			m_viewer.setSelectionStart(0);
        	
        	m_viewer.setSelectionEnd(0);
        }
	});
	
	
	m_print.addActionListener(new ActionListener() {
        
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				if (!m_viewer.getText().isEmpty() ) {
					
					try {
						
						Printable p = m_viewer.getPrintable(new MessageFormat(""), new MessageFormat(""));
						
						PrinterJob job = PrinterJob.getPrinterJob();
						
						job.setPrintable(p);
						
						boolean doPrint = job.printDialog();
						
						if (doPrint) {
			
						        job.print();
						        
						}
						
					} catch (PrinterException e1) {
					
						
	            		 JOptionPane.showMessageDialog(
	     			   			    WsUtils.get().getMainWindow(),
	     			   			    getMessagesStrs("printJobFailedMessage"),
	     			   			    getMessagesStrs("messageInfoCaption"),
	     			   			    JOptionPane.CLOSED_OPTION);
						
	            			if( WsUtils.isDebug() ) {	
						
	            					e1.printStackTrace();
	            					
	            			}
					}
					
				}

			}
    });
	
	m_righPageButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	if(m_html_pages == null || m_html_pages.isEmpty()) { return; }
            	
            	currentPage++;
            	
            	if(currentPage > m_html_pages.size()) { 
            		
            		currentPage = m_html_pages.size();
            	}
            	
            	setDialogCaption();
            	
            	setText(m_html_pages.elementAt(currentPage - 1));
            	
            	m_viewer.setSelectionStart(0);
            	
            	m_viewer.setSelectionEnd(0);
            }
	});
	 
	m_leftPageButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	if(m_html_pages == null || m_html_pages.isEmpty()) { return; }
            	
            	currentPage--;
            	
            	if(currentPage < 1) { 
            		
            		currentPage = 1;
            	}
            
            	setDialogCaption();
            	
            	setText(m_html_pages.elementAt(currentPage - 1));
            	
            	m_viewer.setSelectionStart(0);
            	
            	m_viewer.setSelectionEnd(0);
             
            }
            
		});
	 
	 	m_saveButton.addActionListener(new ActionListener() {
			
            public void actionPerformed(ActionEvent e) {
            	
            	if(m_html_pages == null || m_html_pages.isEmpty()) { 
            		
            		 JOptionPane.showMessageDialog(
     			   			    WsUtils.get().getMainWindow(),
     			   			    getMessagesStrs("emptyReportFailedSaveMessage"),
     			   			    getMessagesStrs("messageInfoCaption"),
     			   			    JOptionPane.CLOSED_OPTION);
            		
            		return; 
            		
            	}
            	
        		WsFileChooserDialog sourceFile = new WsFileChooserDialog(
        				getGuiStrs("chooseFolderDialogCaption"), ".", false, false);
        		
        		sourceFile.setButtonApproveText(getGuiStrs("approveButtonHtmlReport"));
      
        		int result = sourceFile.showOpenDialog(null);
        	
        		if (result == JFileChooser.APPROVE_OPTION) {
        			
        			String nm = sourceFile.getSelectedFile().getPath();
        			
        			if( saveToFile(nm) ) {
        				
        				 JOptionPane.showMessageDialog(
     			   			    WsUtils.get().getMainWindow(),
     			   			    getMessagesStrs("saveHtmlReportSuccessMessage"),
     			   			    getMessagesStrs("messageInfoCaption"),
     			   			    JOptionPane.CLOSED_OPTION);
        				
        			}
        			else {
        				
        				 JOptionPane.showMessageDialog(
     			   			    WsUtils.get().getMainWindow(),
     			   			    getMessagesStrs("saveHtmlReportFailedMessage"),
     			   			    getMessagesStrs("messageInfoCaption"),
     			   			    JOptionPane.CLOSED_OPTION);
        				
        			}
        		}
            }
	 });
		
		
	}
	
	private void createGui() {
		
		JPanel mainPanel = WsGuiTools.createVerticalPanel();
		
		JPanel top_panel = WsGuiTools.createHorizontalPanel();
		
		JPanel toolbar_panel = WsGuiTools.createHorizontalPanel();
		
		toolbar_panel.add(Box.createHorizontalGlue());
		
		toolbar_panel.add(m_zoomIn);
		
		toolbar_panel.add(m_zoomOut);
		
		toolbar_panel.add(m_print);
		
		m_control_panel.add(m_date);
		
		m_control_panel.add(m_genButton);
		
		m_control_panel.add(Box.createHorizontalGlue());
		
		m_control_panel.add(m_leftPageButton);
		
		m_control_panel.add(m_righPageButton);
		
		m_control_panel.add(Box.createHorizontalGlue());
		
		m_control_panel.add(m_saveButton);
		
		m_control_panel.add(m_saveExcelButton);
		
		m_control_panel.add(Box.createHorizontalGlue());

		WsUtils.get().setFixedSizeBehavior(m_date);
		
		WsGuiTools.setComponentFixedWidth(m_date, 300);
		
		top_panel.add(m_control_panel); 
		
		top_panel.add(toolbar_panel);
		
		mainPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT)); 

		mainPanel.add(top_panel);
		
		mainPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		mainPanel.add(m_control_panel2);
		
		mainPanel.add(Box.createVerticalStrut(WsUtils.VERT_STRUT));
		
		m_ScrollPane = new JScrollPane(m_viewer);
		
		mainPanel.add(m_ScrollPane);
		
		add(mainPanel);
		
		setToolTips();
	}
	
	public void setText(String s) {
		
		m_currentText = s;
		
		m_viewer.getEditorKit().createDefaultDocument();
		
		m_viewer.setText(s);
		
	}
	
	
	public boolean saveToFile(String pathFolder) {
		
		
		for(int i = 0; i <  m_html_pages.size(); ++i) {
			
		
			 File path = new File(WsUtils.concatPathName(pathFolder,  "r_page_" + String.valueOf(i + 1) + ".html"));

		        FileWriter wr;
		        
				try {
					wr = new FileWriter(path);
	
			        wr.write(m_html_pages.elementAt(i));
	
			        wr.flush();
			         
			        wr.close();
			         
				} catch (IOException e) {
				
					e.printStackTrace();
					
					return false;
				}

		}
		
		return true;
	}
	
	protected void setDialogCaption() {
		
		this.setTitle(m_name + "   " + getPagesCaption());
	}
	
	String getPagesCaption() {
		
		return  getGuiStrs("numPagesReportLabel") + " " + String.valueOf(pagesNum) + "  " + 
				getGuiStrs("currentPageReportLabel") + " " 
            	+ String.valueOf(currentPage);
	}
	
	
	protected void setToolTips() {
		
		m_viewer.setToolTipText(getGuiStrs("viewerReportToolTip"));

		m_zoomIn.setToolTipText(getGuiStrs("zoomInButtonToolTip"));
		
		m_zoomOut.setToolTipText(getGuiStrs("zoomOutButtonToolTip"));
			
	    m_genButton.setToolTipText(getGuiStrs("genButtonReporttoolTip"));
		
		m_righPageButton.setToolTipText(getGuiStrs("nextPageReportToolTip"));
		
		m_leftPageButton.setToolTipText(getGuiStrs("prevPageReportToolTip"));
		
		m_saveButton.setToolTipText(getGuiStrs("saveToHtmlToolTip"));
		
		m_saveExcelButton.setToolTipText(getGuiStrs("saveExcelButToolTip"));
		
	}
	
	public XSSFCellStyle  getExcelCellStyle(XSSFWorkbook wb, int topBorder, int botBorder, int leftBorder, int rightBorder,
			boolean verticalText, HorizontalAlignment hAl, VerticalAlignment vAl, boolean wrapText) {
	
	
	   XSSFCellStyle cs12 = wb.createCellStyle();
	   
	   cs12.setAlignment(hAl);
	   
	   cs12.setVerticalAlignment(vAl);
	   
	   cs12.getStyleXf().setApplyAlignment(true); // <<< Important
	   
	   if(verticalText) {
		   
		   cs12.setRotation((short) 90);  
		   
	   }
	   
	   if(leftBorder == 1) {
		   
		   cs12.setBorderLeft(BorderStyle.THIN);
		   
	   }
	   
	   if(rightBorder == 1) {
		   
		   cs12.setBorderRight(BorderStyle.THIN);
		   
	   }
	   
	   if(topBorder == 1) {
		   
		   cs12.setBorderTop(BorderStyle.THIN);
		   
	   }
	   
	   if(botBorder == 1) {
		   
		   cs12.setBorderBottom(BorderStyle.THIN);
		   
	   }
	   
	   cs12.setWrapText(wrapText);

	   return cs12;
	}
	

	public String excelSaveFileChoose( Component parent) {
		
		return WsUtils.get().excelSaveFileChoose(parent,  m_excel_save_folder);	
	}
	
	public String excelRasklSaveFileChoose( Component parent) {
		
		return WsUtils.get().excelRasklSaveFileChoose(parent,  m_excel_save_folder);	
	}
	
	public String excelSaveFolderChoose(Component parent) {
		
		return WsUtils.get().excelSaveFolderChoose(parent,  m_excel_save_folder);	
	}
	
	public String jsonSaveFileChoose(Component parent) {
		
		return WsUtils.get().jsonSaveFileChoose(parent,  m_excel_save_folder);	
	}

	
	
	public boolean isReportEmpty() {
	
		if(m_html_pages == null || m_html_pages.isEmpty()) { 
			
			JOptionPane.showMessageDialog(
		   			    WsUtils.get().getMainWindow(),
		   			    getMessagesStrs("emptyReportFailedSaveMessage"),
		   			    getMessagesStrs("messageInfoCaption"),
		   			    JOptionPane.CLOSED_OPTION);
			
			return true; 
			
		}
		
		return false;

	}	
	
	//may be reloaded in subclasses
	protected void closeAllEventConnections() {
		
	}
	
	public void dispose() {
		
		closeAllEventConnections();
		
		WsEventDispatcher.get().disconnect(this);
		
		super.dispose();
		
		
	}
}
