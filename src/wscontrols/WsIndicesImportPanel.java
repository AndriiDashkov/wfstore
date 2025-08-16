
package wscontrols;

import static wsmain.WsUtils.getGuiStrs;
import static wsmain.WsUtils.getMessagesStrs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import wsimport.WsParseIndicies;
import wsimport.WsParseIndicies.TYPE;
import wsmain.WsGuiTools;
import wsmain.WsSettings;
import wsmain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsIndicesImportPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	JButton m_buttonUp = null;
	
	JButton m_buttonSt = null;
	
	JButton m_buttonDown = null;
	
	JButton m_buttonSave = null;
	
	JLabel m_mainLabel = new JLabel(getGuiStrs("importIndicesMainLabel"));
	
	protected static int[] RASKLADKA_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected static int[] NAKL_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected static int[] PRIHODNAKL_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected static int[] KARTZVIT_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected static int[] KARTZVITRASKLADKA_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected static int[] CATALOGRASKLADKA_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected static int[] PRODREST_static_indices = {-1,-1, -1, -1, -1, -1, -1,-1, -1, -1, -1, -1, -1, -1};
	
	protected  JLabel m_labelSheetColumn = new JLabel(getGuiStrs("labelSheetColumnCaption"));
	
	protected  JLabel m_labelKodColumn = new JLabel(getGuiStrs("labelKodColumnCaption"));
	
	protected  JLabel m_labelNameColumn = new JLabel(getGuiStrs("labelNameColumnCaption"));
	
	protected  JLabel m_labelQuantityColumn = new JLabel(getGuiStrs("labelQuantityColumnCaption"));
	
	protected  JLabel m_labeUnitsRow = new JLabel(getGuiStrs("labeUnitsRowCaption"));
	
	protected  JLabel m_labelMonday = new JLabel(getGuiStrs("labelMondayPeopleColumnCaption") + ":");
	
	protected  JLabel m_labelTuesday = new JLabel(getGuiStrs("labelTuesdayColumnCaption") + ":");
	
	protected  JLabel m_labelWednsday = new JLabel(getGuiStrs("labelWednesdayColumnCaption") + ":");
	
	protected  JLabel m_labelThursday = new JLabel(getGuiStrs("labelThursdayColumnCaption")+ ":");
	
	protected  JLabel m_labelFriday = new JLabel(getGuiStrs("labelFridayColumnCaption")+ ":");

	protected  JLabel m_labelSaturday = new JLabel(getGuiStrs("labelSaturdayCaption")+ ":");
	
	protected  JLabel m_labelSunday = new JLabel(getGuiStrs("labelSundayCaption")+ ":");
	
	protected  JLabel m_labelPeopleColumn = new JLabel(getGuiStrs("labelPeopleColumnCaption"));
	
	protected  JSpinner m_spinSheetColumn = null;
	
	protected  JSpinner m_spinKodColumn = null;
	
	protected  JSpinner m_spinNameColumn = null;
	
	protected  JSpinner m_spinQuantityColumn = null;
		
	protected  JSpinner m_spinUnitsRow = null;
	
	protected  JSpinner m_spinMondayRow = null;
	
	protected  JSpinner m_spinTuesdayRow = null;
	
	protected  JSpinner m_spinWednesdayRow = null;
	
	protected  JSpinner m_spinThursdayRow = null;
		
	protected  JSpinner m_spinFridayRow = null;
	
	protected  JSpinner m_spinSaturdayRow = null;
	
	protected  JSpinner m_spinSundayRow = null;
	
	protected  JSpinner m_spinPeopleColumn = null;
	
	protected  JSpinner m_spinQuantityColumn1 = null;
	
	WsParseIndicies.TYPE m_type = TYPE.NAKL;
	
	JPanel m_panel_bottom = null;
	 
	JDialog m_dialog_parent = null;
	
	public WsIndicesImportPanel(JDialog parent,WsParseIndicies.TYPE type) {
		super();
		
		m_type = type;
		
		m_dialog_parent = parent;
		
		createGui();
		
		Forwarder f = new  Forwarder();
		
		m_buttonUp.addActionListener(f);
		
		m_buttonSt.addActionListener(f);
		
		m_buttonSave.addActionListener(f);
		 
		m_panel_bottom.setVisible(false);
		
		setTooltips();
			
	}
	
	private void createGui() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		m_buttonUp = new JButton("v");
		
		m_buttonSt = new JButton("-");
		
		m_buttonSave = new JButton(getGuiStrs("saveButCap"));
		
		JPanel panel_top = WsGuiTools.createHorizontalPanel();
		
		panel_top.add(m_mainLabel);  
		
		panel_top.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
 
		panel_top.add(m_buttonUp);
		
		panel_top.add(m_buttonSt);
		
		panel_top.add(m_buttonSave);
		
		panel_top.add(Box.createHorizontalGlue());
		
		switch(m_type) {
		
			case RASKLADKA: { m_panel_bottom =  getGuiRaskladka(); break;}
			
			case NAKL: { m_panel_bottom = getGuinakladna(); break;}
			
			case CATALOGRASKLADKA: { m_panel_bottom = getGuiKatalog(); break;}
			
			case KARTZVIT: { m_panel_bottom = getKartkaZvit(); break;}
			
			case PRIHODNAKL: { m_panel_bottom = getGuinakladna(); break;}
			
			case PRODREST: { m_panel_bottom = getGuiProdRest(); break; } 
			
			default: { break; }
		};
		
		setInitialIndices();
		
		add(panel_top);
		
		add(Box.createVerticalStrut(WsUtils.VERT_STRUT/2));
		
		add(m_panel_bottom);
		
		add(Box.createVerticalGlue());
		
	}
	
	private JPanel getGuinakladna() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		SpinnerModel model1 = new SpinnerNumberModel(-1, -1, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(-1, -1, 20000, 1); 
		
		m_spinKodColumn = new  JSpinner(model2);
		
		SpinnerModel model3 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinNameColumn = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinQuantityColumn = new  JSpinner(model4);
			
		SpinnerModel model7 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinUnitsRow = new  JSpinner(model7);
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(8,2, 0, WsUtils.VERT_STRUT/4));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelNameColumn);     panel_grid.add(m_spinNameColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		panel_grid.add(m_labeUnitsRow);         panel_grid.add(m_spinUnitsRow);

		return panel_grid;
		
	}
	
	private JPanel getGuiProdRest() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		SpinnerModel model1 = new SpinnerNumberModel(-1, -1, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(-1, -1, 20000, 1); 
		
		m_spinKodColumn = new  JSpinner(model2);
		
		SpinnerModel model3 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinNameColumn = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinQuantityColumn = new  JSpinner(model4);
			
		SpinnerModel model5 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinQuantityColumn1 = new  JSpinner(model5);
		
		SpinnerModel model8 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		m_spinUnitsRow = new  JSpinner(model8);
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(8,2, 0, WsUtils.VERT_STRUT/4));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelNameColumn);     panel_grid.add(m_spinNameColumn);
		
		panel_grid.add(new JLabel("Index pochatok")); panel_grid.add(m_spinQuantityColumn);
		
		panel_grid.add(new JLabel("Index pribulo")); panel_grid.add(m_spinQuantityColumn1);
		
		panel_grid.add(m_labeUnitsRow);         panel_grid.add(m_spinUnitsRow);

		return panel_grid;
		
	}
	
	public void setType(WsParseIndicies.TYPE t) {
		
		m_type = t;
		
	}
	
	public void setInitialIndices() {
		
		if( isStaticUsed() ) {
			
			setIndicesStatic();
		}
		else {
			
			int[] ind  = WsUtils.get().getSettings().getImportIndices(m_type);
			
			boolean settFlag = false;
			
			for(int i : ind) {
				
				if(i != -1) {
					
					settFlag = true;
					
					break;
				}
			}
			
			if(settFlag) {
				
				WsParseIndicies sc = new WsParseIndicies(m_type);
				
				sc.setParseImportIndices(ind);
				
				setIndicesSchema(sc);
				
			}
			else {
				
				setIndicesSchema(new WsParseIndicies(m_type));
			}

		}
	}
	
	private int[] getStaticArray(WsParseIndicies.TYPE tp) {
		
		int[] ref = null;
		
		switch(tp) {
		
			case RASKLADKA:           {  ref = RASKLADKA_static_indices; break;}
			
			case NAKL:                {  ref = NAKL_static_indices; break;}
			
			case KARTZVIT:            {  ref = KARTZVIT_static_indices; break;}
			
			case KARTZVITRASKLADKA:   {  ref = KARTZVITRASKLADKA_static_indices; break;}
			
			case CATALOGRASKLADKA:    {  ref = CATALOGRASKLADKA_static_indices; break;}
			
			case PRODREST:            {  ref = PRODREST_static_indices; break;}
			
			case PRIHODNAKL:          {  ref = PRIHODNAKL_static_indices; break;}
			
			default: { break;}
		};
		
		return ref;
		
	}
	
	private boolean isStaticUsed() {
		
		int[] ref = getStaticArray(m_type);
				
		boolean res = false;
		
		for(int i = 0; i <  WsParseIndicies.indicesNumber; ++i) {
			
			if (ref[i] != -1) {
				
				res = true;
				
				break;
			}
		}
		
		return res;
	}
	
	
private JPanel getGuiRaskladka() {
	
		m_labelSheetColumn.setText(getGuiStrs("rasklLabelSheetExcelIndex"));
	
		m_labelKodColumn.setText(getGuiStrs("rasklLabelKodExcelIndex"));
	
		m_labelNameColumn.setText(getGuiStrs("rasklLabelNameExcelIndex"));
	
		m_labelQuantityColumn.setText(getGuiStrs("rasklLabelSumStartExcelIndex"));
	
	    JPanel main = new JPanel();
		
		main.setLayout(new BoxLayout(main, BoxLayout.X_AXIS));

		SpinnerModel model1 = new SpinnerNumberModel(3, -1, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(10, -1, 20000, 1); 
		
		m_spinKodColumn = new  JSpinner(model2);
		
		SpinnerModel model3 = new SpinnerNumberModel(12, -1, 20000, 1);
		
		m_spinNameColumn = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(7, -1, 20000, 1);
		
		m_spinQuantityColumn = new  JSpinner(model4);
			
		SpinnerModel model8 = new SpinnerNumberModel(42, -1, 20000, 1);
		
		m_spinMondayRow = new  JSpinner(model8);
		
		SpinnerModel model9 = new SpinnerNumberModel(73, -1, 20000, 1);
		
		m_spinTuesdayRow = new  JSpinner(model9);
		
		SpinnerModel model10 = new SpinnerNumberModel(104, -1, 20000, 1);
		
		m_spinWednesdayRow = new  JSpinner(model10);
		
		SpinnerModel model11 = new SpinnerNumberModel(135, -1, 20000, 1);
		
		m_spinThursdayRow = new  JSpinner(model11);
		
		SpinnerModel model12 = new SpinnerNumberModel(166, -1, 20000, 1);
			
		m_spinFridayRow = new  JSpinner(model12);
		
		SpinnerModel model13 = new SpinnerNumberModel(197, -1, 20000, 1);
		
	    m_spinSaturdayRow = new  JSpinner(model13);
	    
	    SpinnerModel model14 = new SpinnerNumberModel(228, -1, 20000, 1);
		
	    m_spinSundayRow = new  JSpinner(model14);
	    
		//WsGuiTools.setComponentFixedHeight(m_spinSheetColumn, 15);
		
	    JPanel panel_left = WsGuiTools.createVerticalPanel();
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(7,4, WsUtils.HOR_STRUT/4, WsUtils.VERT_STRUT/4));
		
		panel_grid.add(m_labelSheetColumn); 
		
		panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelMonday);  
	    
	    panel_grid.add(m_spinMondayRow);
		
		panel_grid.add(m_labelKodColumn);	    
		
		panel_grid.add(m_spinKodColumn);
		
	    panel_grid.add( m_labelTuesday); 
	    
	    panel_grid.add(m_spinTuesdayRow);
		
		panel_grid.add(m_labelNameColumn);     
		
		panel_grid.add(m_spinNameColumn);
		
		panel_grid.add( m_labelWednsday);  
		
		panel_grid.add(m_spinWednesdayRow);
		
		panel_grid.add(m_labelQuantityColumn); 
		
		panel_grid.add(m_spinQuantityColumn);
		
		panel_grid.add( m_labelThursday);  
		
		panel_grid.add(m_spinThursdayRow);
		
		panel_grid.add(new JLabel(""));  
		
		panel_grid.add(new JLabel(""));
		
		panel_grid.add(m_labelFriday);  
		
		panel_grid.add(m_spinFridayRow);
		
		panel_grid.add(new JLabel(""));  
		
		panel_grid.add(new JLabel(""));
		
		panel_grid.add(m_labelSaturday);    
		
		panel_grid.add(m_spinSaturdayRow);
		
		panel_grid.add(new JLabel(""));  
		
		panel_grid.add(new JLabel(""));
		
		panel_grid.add(m_labelSunday); 
		
		panel_grid.add( m_spinSundayRow);
		


		panel_left.add(panel_grid);         
		
		panel_left.add(Box.createVerticalGlue());
		
		//JPanel panel_grid2 = new JPanel();	

		//TitledBorder title = BorderFactory.createTitledBorder(getGuiStrs("labelSumWeekDaysCaption"));
		
		//panel_grid2.setLayout(new GridLayout(7,2));
		
		//panel_grid2.setBorder(title);
		
		main.add(panel_left);
		
		//main.add(panel_grid2);
		
		TitledBorder title_main = BorderFactory.createTitledBorder(getGuiStrs("labelRaskladkaIndicesCaption"));
		
		main.setBorder(title_main);
		
		return main;
		
	}

	public void setDynamicAlignment() {
		
		switch(m_type) {
		
			case KARTZVITRASKLADKA: {  }
			
			case RASKLADKA:         { 
	
				int h = m_spinSundayRow.getMinimumSize().height;
				
				WsGuiTools.setComponentFixedHeight(m_spinSheetColumn,h);
							
				WsGuiTools.setComponentFixedHeight(m_labelSheetColumn, h);
				
				WsGuiTools.setComponentFixedHeight(m_spinSheetColumn, h);
				
				WsGuiTools.setComponentFixedHeight(m_labelKodColumn, h);	    
				
				WsGuiTools.setComponentFixedHeight(m_spinKodColumn, h);
				
				WsGuiTools.setComponentFixedHeight(m_labelNameColumn, h);   
				
				WsGuiTools.setComponentFixedHeight(m_spinNameColumn, h);
				
				WsGuiTools.setComponentFixedHeight(m_labelQuantityColumn, h);
				
				WsGuiTools.setComponentFixedHeight(m_spinQuantityColumn, h);
				
				
				break;
				
			}
			
			case NAKL:              {   break;}
			
			case KARTZVIT:          {   break;}
			
			case CATALOGRASKLADKA:  {   break;}
			
			case PRODREST:          {   break;}
			
			case PRIHODNAKL:          {   break;}
			
			default: { }
		};	
	}


	private JPanel getGuiKatalog() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		SpinnerModel model1 = new SpinnerNumberModel(-1, -1, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(-1, -1, 20000, 1); 
		
		m_spinKodColumn = new  JSpinner(model2);
		
		SpinnerModel model3 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		//this is column for cost
		m_spinNameColumn = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		//this is start row 
		m_spinQuantityColumn = new  JSpinner(model4);
			
		m_labelNameColumn.setText(getGuiStrs("nameLabelCostColumnCatalaogImportName"));
		
		m_labelQuantityColumn.setText(getGuiStrs("nameLabelstartRowCatalaogImportName"));
	
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(4,2, 0, WsUtils.VERT_STRUT/4));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelNameColumn);     panel_grid.add(m_spinNameColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
				
		return panel_grid;
	}
	
	
	private JPanel getKartkaZvit() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
		m_spinSheetColumn = new  JSpinner(new SpinnerNumberModel(-1, -1, 20000, 1)); 
		
		m_spinKodColumn = new  JSpinner(new SpinnerNumberModel(-1, -1, 20000, 1));
	
		m_spinNameColumn = new  JSpinner(new SpinnerNumberModel(-1, -1, 20000, 3));
		
		//this is start row 
		m_spinQuantityColumn = new  JSpinner(new SpinnerNumberModel(-1, -1, 20000, 1));
		
		m_spinPeopleColumn = new  JSpinner(new SpinnerNumberModel(-1, -1, 20000, 1));
			
		m_labelKodColumn.setText(getGuiStrs("nameLabelCostColumnKodKZImportName"));
		
		m_labelQuantityColumn.setText(getGuiStrs("nameLabelstartRowKZImportName"));
		
		m_labelNameColumn.setText(getGuiStrs("KodRowKZImportNameLabel"));

		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(5,2, 0, WsUtils.VERT_STRUT/4));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add( m_labelNameColumn);	    panel_grid.add(	m_spinNameColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		panel_grid.add(m_labelPeopleColumn); panel_grid.add(m_spinPeopleColumn);
			
		return panel_grid;

	}
	
	public WsParseIndicies getIndicesSchema() {
		
		
		WsParseIndicies ind_schema = new WsParseIndicies();

		if(m_type == TYPE.RASKLADKA || m_type == TYPE.KARTZVITRASKLADKA) {
	
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
			
			 ind_schema.kodRowIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.nameRowIndex = (int)m_spinNameColumn.getValue();
			
			 ind_schema.quantityColumnIndex = (int)m_spinQuantityColumn.getValue();
			 
			 ind_schema.mondaySumIndex = (int) m_spinMondayRow.getValue();
	 
			 ind_schema.tuesdaySumIndex = (int)m_spinTuesdayRow.getValue();
	
			 ind_schema.wednesdaySumIndex = (int)m_spinWednesdayRow.getValue();
	
			 ind_schema.thursdaySumIndex = (int)m_spinThursdayRow.getValue();
	
			 ind_schema.fridaySumIndex = (int)m_spinFridayRow.getValue();
	
			 ind_schema.sartudaySumIndex = (int)m_spinSaturdayRow.getValue();
	
			 ind_schema.sundaySumIndex = (int)m_spinSundayRow.getValue();
			 
			 ind_schema.peopleStartRowIndex = 9;
				
			 ind_schema.peopleStartColumnIndex = 3;
				
			 ind_schema.peopleSheetIndex = 0;
		 
		}
		else if(m_type == TYPE.CATALOGRASKLADKA) {
			
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
			
			 ind_schema.kodColumnIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.costColumnIndex = (int)m_spinNameColumn.getValue();
			
			 ind_schema.kodRowIndex = (int)m_spinQuantityColumn.getValue();
	
		}
		else if(m_type == TYPE.NAKL) {
			
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
				
			 ind_schema.kodColumnIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.nameColumnIndex = (int)m_spinNameColumn.getValue();
			
			 ind_schema.quantityColumnIndex = (int)m_spinQuantityColumn.getValue();
			
			 ind_schema.unitsColumnIndex = (int)m_spinUnitsRow.getValue();
	
		}
		else if(m_type == TYPE.PRODREST) {
			
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
				
			 ind_schema.kodColumnIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.nameColumnIndex = (int)m_spinNameColumn.getValue();
			
			 ind_schema.quantityColumnIndex = (int)m_spinQuantityColumn.getValue();
			 
			 ind_schema.quantityColumnIndex1 = (int)m_spinQuantityColumn1.getValue();
			
			 ind_schema.unitsColumnIndex = (int)m_spinUnitsRow.getValue();
	
		}
		else if(m_type == TYPE.KARTZVIT) {
			
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
			 
			 ind_schema.kodRowIndex = (int)m_spinNameColumn.getValue();
				
			 ind_schema.kodColumnIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.quantityColumnIndex = (int)m_spinQuantityColumn.getValue();
			 
			 ind_schema.peopleStartColumnIndex = (int)m_spinPeopleColumn.getValue();
			
		}
		else if(m_type == TYPE.PRIHODNAKL) {
			
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
				
			 ind_schema.kodColumnIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.nameColumnIndex = (int)m_spinNameColumn.getValue();
			
			 ind_schema.quantityColumnIndex = (int)m_spinQuantityColumn.getValue();
			
			 ind_schema.unitsColumnIndex = (int)m_spinUnitsRow.getValue();
	
		}
		
		setAllStatic();
		 
		return ind_schema;
			
	}

	class Forwarder implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	
			if ( e.getSource() == m_buttonUp ) { 
				
				if(m_panel_bottom.isVisible()) {
				
					m_panel_bottom.setVisible(false);
					
					m_buttonUp.setText("v");
					
					m_buttonUp.setToolTipText(getGuiStrs("showIndicesImportButtonToolTip"));
					
					m_dialog_parent.pack();
				}
				else {
					
					m_panel_bottom.setVisible(true);
					
					m_buttonUp.setText("^");
					
					m_buttonUp.setToolTipText(getGuiStrs("hideIndicesImportButtonToolTip"));

					m_dialog_parent.pack();
					
					//setDynamicAlignment();
				}
				
			}
			if ( e.getSource() == m_buttonSt ) { 
				
				setIndicesSchema(new WsParseIndicies(m_type));
				
			}
			if ( e.getSource() == m_buttonSave ) { 
				
				saveIndicesToSettings();
				
				WsUtils.showMessageDialog( getMessagesStrs("indSaveSetMessage"));
					
			}
		}
	}
	
	
	private void saveIndicesToSettings() {
		
		WsSettings st = WsUtils.get().getSettings();
		
		int[] ar = new int[WsParseIndicies.indicesNumber];
		
		for(int i = 0; i < WsParseIndicies.indicesNumber; ++i ) {
			
			ar[i] = -1;
		}
		
		if(m_spinSheetColumn != null)  { ar[0] = (int) m_spinSheetColumn.getValue(); }
		
		if(m_spinKodColumn != null)  { ar[1] = (int) m_spinKodColumn.getValue(); }
		
		if(m_spinNameColumn != null)  { ar[2] = (int) m_spinNameColumn.getValue(); }
		
		if(m_spinQuantityColumn != null)  { ar[3] = (int) m_spinQuantityColumn.getValue(); }
			
		if(m_spinUnitsRow != null)  { ar[4] = (int) m_spinUnitsRow.getValue(); }
		
		if(m_spinMondayRow != null)  { ar[5] = (int) m_spinMondayRow.getValue(); }
		
		if(m_spinTuesdayRow != null)  { ar[6] = (int) m_spinTuesdayRow.getValue(); }
		
		if( m_spinWednesdayRow != null)  { ar[7] = (int) m_spinWednesdayRow.getValue(); }
		
		if(m_spinThursdayRow != null)  { ar[8] = (int) m_spinThursdayRow.getValue(); }
			
		if(m_spinFridayRow != null)  { ar[9] = (int) m_spinFridayRow.getValue(); }
		
		if(m_spinSaturdayRow != null)  { ar[10] = (int) m_spinSaturdayRow.getValue(); }
		
		if(m_spinSundayRow != null)  { ar[11] = (int) m_spinSundayRow.getValue(); }
		
		if(m_spinPeopleColumn != null)  { ar[12] = (int) m_spinPeopleColumn.getValue(); }
		
		if(m_spinQuantityColumn1 != null)  { ar[13] = (int) m_spinQuantityColumn1.getValue(); }
		
		st.setImportIndices(m_type, ar);
		
		st.setReqSavingFlag();
		
	}
	
	public void setIndicesSchema(WsParseIndicies sc) {
	
		if(m_spinSheetColumn != null ) {
			
			m_spinSheetColumn.setValue( sc.sheetIndex);
		}
		
		if(sc.type == TYPE.RASKLADKA || sc.type == TYPE.KARTZVITRASKLADKA) {
			
			if(m_spinKodColumn != null ) {
				
				m_spinKodColumn.setValue( sc.kodRowIndex);
			}

			if(m_spinNameColumn != null ) {
				
				m_spinNameColumn.setValue(sc.nameRowIndex);
			}
		}
		
		else {
		
			if(m_spinKodColumn != null ) {
				
				m_spinKodColumn.setValue( sc.kodColumnIndex);
			}
	
			if(m_spinNameColumn != null ) {
				
				m_spinNameColumn.setValue(sc.nameColumnIndex);
				
			}
		}

		if(m_spinQuantityColumn != null ) {
			
			m_spinQuantityColumn.setValue(sc.quantityColumnIndex);
		}
		
		if(m_spinUnitsRow != null ) {
			
			m_spinUnitsRow.setValue(sc.unitsColumnIndex);
		}
			
		if(m_spinMondayRow != null ) {
			
			m_spinMondayRow.setValue(sc.mondaySumIndex);
		}
		
		if(m_spinTuesdayRow != null ) {
			
			m_spinTuesdayRow.setValue(sc.tuesdaySumIndex);
		}

		if(m_spinWednesdayRow != null ) {
			
			m_spinWednesdayRow.setValue(sc.wednesdaySumIndex);
		}
		
		if(m_spinThursdayRow != null ) {
			
			m_spinThursdayRow.setValue(sc.thursdaySumIndex);
		}
		
		if(m_spinFridayRow != null ) {
			
			m_spinFridayRow.setValue(sc.fridaySumIndex);
		}
		
		if(m_spinSaturdayRow != null ) {
			
			m_spinSaturdayRow.setValue(sc.sartudaySumIndex);
		}

	    if(m_spinSundayRow != null ) {
	    	
	    	m_spinSundayRow.setValue(sc.sundaySumIndex);
	    }
	    
	    if(m_spinPeopleColumn != null ) {
	    	
	    	m_spinPeopleColumn.setValue(sc.peopleStartColumnIndex);
	    }
	    
		if(m_spinQuantityColumn1 != null ) {
			
			m_spinQuantityColumn1.setValue(sc.quantityColumnIndex1);
		}
	    
	    int[] ref = getStaticArray(sc.type);
	    
	    ref[0] = sc.sheetIndex;
	    
	    if(sc.type == TYPE.NAKL || sc.type == TYPE.KARTZVIT || sc.type == TYPE.PRIHODNAKL) {
	    	
	    	 ref[1] = sc.kodColumnIndex;
	    }
	    else {
	    	
	    	 ref[1] = sc.kodRowIndex;
	    }
	    
		if(sc.type == TYPE.RASKLADKA || sc.type == TYPE.KARTZVITRASKLADKA) {
			
			ref[2] = sc.nameRowIndex;
		}
		else {
			
			ref[2] = sc.nameColumnIndex;
	    
		}

	    ref[3] = sc.quantityColumnIndex;
		
	    ref[4] = sc.unitsColumnIndex;
			
	    ref[5] = sc.mondaySumIndex;
		
	    ref[6] = sc.tuesdaySumIndex;

	    ref[7] = sc.wednesdaySumIndex;
		
	    ref[8] = sc.thursdaySumIndex;
		
	    ref[9] = sc.fridaySumIndex;
		
	    ref[10] = sc.sartudaySumIndex;

	    ref[11] = sc.sundaySumIndex;
	    
	    ref[12] = sc.peopleStartColumnIndex;
	    
	    ref[13] = sc.quantityColumnIndex1;
	
	}
	
	public void setIndicesStatic() {
				
		int[] ref = getStaticArray(m_type);
		
		if(null != m_spinSheetColumn) { m_spinSheetColumn.setValue(ref[0]); }
			
		if(null !=  m_spinKodColumn) { m_spinKodColumn.setValue(ref[1]); }
			
		if(null !=  m_spinNameColumn) { m_spinNameColumn.setValue(ref[2]); }
			
		if(null != m_spinQuantityColumn) { m_spinQuantityColumn.setValue(ref[3]); }
				
		if(null != m_spinUnitsRow) { m_spinUnitsRow.setValue(ref[4]); }
			
		if(null != m_spinMondayRow) { m_spinMondayRow.setValue(ref[5]); }
			
		if(null !=  m_spinTuesdayRow) { m_spinTuesdayRow.setValue(ref[6]); }
			
		if(null != m_spinWednesdayRow) { m_spinWednesdayRow.setValue(ref[7]); }
			
		if(null != m_spinThursdayRow) { m_spinThursdayRow.setValue(ref[8]); }
				
		if(null != m_spinFridayRow) { m_spinFridayRow.setValue(ref[9]); }
			
		if(null != m_spinSaturdayRow) { m_spinSaturdayRow.setValue(ref[10]); }
			
		if(null != m_spinSundayRow) { m_spinSundayRow.setValue(ref[11]); }
		
		if(null != m_spinPeopleColumn) { m_spinPeopleColumn.setValue(ref[12]); }
		
		if(null != m_spinQuantityColumn1) { m_spinQuantityColumn1.setValue(ref[13]); }
				
	}
	
	public void setAllStatic() {
		
		int[] ref = getStaticArray(m_type);
		
		if(m_spinSheetColumn != null ) {
		
			ref[0] = (int) m_spinSheetColumn.getValue();
		}
		
		if(m_spinKodColumn != null ) {
			
			ref[1] = (int) m_spinKodColumn.getValue();
		}
		
		if(m_spinNameColumn != null ) {
			
			ref[2] = (int) m_spinNameColumn.getValue();
		}
		
		if(m_spinQuantityColumn != null ) {
			
			ref[3] = (int) m_spinQuantityColumn.getValue();
		}
		
		if(m_spinUnitsRow != null ) {
			
			ref[4] =  (int) m_spinUnitsRow.getValue();
		}
		
		if( m_spinMondayRow != null ) {	
			
			ref[5] = (int) m_spinMondayRow.getValue();
		}
		
		if(m_spinTuesdayRow != null ) {
			
			ref[6] = (int) m_spinTuesdayRow.getValue();
		}
		
		if(m_spinWednesdayRow != null ) {
			
			ref[7] = (int) m_spinWednesdayRow.getValue();
		}
		
		if(m_spinThursdayRow != null ) {
			
			ref[8] = (int) m_spinThursdayRow.getValue();
		}
		
		if(m_spinFridayRow != null ) {
			
			ref[9] = (int) m_spinFridayRow.getValue();
		}
		
		if(m_spinSaturdayRow != null ) {
			
			ref[10] = (int) m_spinSaturdayRow.getValue();
		}
		
		if(m_spinSundayRow != null ) {
			
			ref[11] = (int) m_spinSundayRow.getValue();
		}	
		if(m_spinPeopleColumn != null ) {
			
			ref[12] = (int) m_spinPeopleColumn.getValue();
		}
		if(m_spinQuantityColumn1 != null ) {
			
			ref[13] = (int) m_spinQuantityColumn1.getValue();
		}
	}
	
	private void setTooltips() {
		
		m_buttonUp.setToolTipText(getGuiStrs("showIndicesImportButtonToolTip"));
		
		m_buttonSt.setToolTipText(getGuiStrs("getStIndicesImportButtonToolTip"));
		
		m_buttonSave.setToolTipText(getGuiStrs("indicesImportSaveButtonToolTip"));
		
		if(m_spinSheetColumn != null)  { m_spinSheetColumn.setToolTipText(getGuiStrs("sheetColImToolTip")); }
		
		if(m_spinKodColumn != null)  {  m_spinKodColumn.setToolTipText(getGuiStrs("kodColImToolTip")); }
		
		if(m_spinNameColumn != null)  {  m_spinNameColumn.setToolTipText(getGuiStrs("nameColImToolTip")); }
		
		if(m_spinQuantityColumn != null)  {  m_spinQuantityColumn.setToolTipText(getGuiStrs("quanColImToolTip")); }
			
		if(m_spinUnitsRow != null)  {  m_spinUnitsRow.setToolTipText(getGuiStrs("unitsColImToolTip")); }
		
		if(m_spinMondayRow != null)  {  m_spinMondayRow.setToolTipText(getGuiStrs("mondayColImToolTip2")); }
		
		if(m_spinTuesdayRow != null)  {  m_spinTuesdayRow.setToolTipText(getGuiStrs("tuesdayColImToolTip2")); }
		
		if( m_spinWednesdayRow != null)  {  m_spinWednesdayRow.setToolTipText(getGuiStrs("wedColImToolTip2")); }
		
		if(m_spinThursdayRow != null)  {  m_spinThursdayRow.setToolTipText(getGuiStrs("thursDayColImToolTip2")); }
			
		if(m_spinFridayRow != null)  {  m_spinFridayRow.setToolTipText(getGuiStrs("fridayColImToolTip2")); }
		
		if(m_spinSaturdayRow != null)  {  m_spinSaturdayRow.setToolTipText(getGuiStrs("saturdayColImToolTip2")); }
		
		if(m_spinSundayRow != null)  {  m_spinSundayRow.setToolTipText(getGuiStrs("sundayColImToolTip2")); }
		
	}
	
}
