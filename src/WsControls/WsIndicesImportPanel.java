
package WsControls;

import static WsMain.WsUtils.getGuiStrs;
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
import WsImport.WFParseIndicies;
import WsImport.WFParseIndicies.TYPE;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsIndicesImportPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	JButton m_buttonUp = null;
	
	JButton m_buttonSt = null;
	
	JButton m_buttonDown = null;
	
	JLabel m_mainLabel = new JLabel(getGuiStrs("importIndicesMainLabel"));
	
	protected  static int m_spinSheetColumn_v = -1;
	
	protected  static int m_spinKodColumn_v = -1;
	
	protected  static int m_spinNameColumn_v = -1;
	
	protected  static int m_spinQuantityColumn_v = -1;
		
	protected  static int m_spinUnitsRow_v = -1; 
	
	protected  static int m_spinMondayRow_v = -1;
	
	protected  static int m_spinTuesdayRow_v = -1;
	
	protected  static int m_spinWednesdayRow_v = -1;
	
	protected  static int m_spinThursdayRow_v = -1;
		
	protected  static int m_spinFridayRow_v = -1;
	
	protected  static int m_spinSaturdayRow_v = -1;
	
	protected  static int m_spinSundayRow_v = -1;
	
	protected  JLabel m_labelSheetColumn = new JLabel(getGuiStrs("labelSheetColumnCaption"));
	
	protected  JLabel m_labelKodColumn = new JLabel(getGuiStrs("labelKodColumnCaption"));
	
	protected  JLabel m_labelNameColumn = new JLabel(getGuiStrs("labelNameColumnCaption"));
	
	protected  JLabel m_labelQuantityColumn = new JLabel(getGuiStrs("labelQuantityColumnCaption"));
	
	protected  JLabel m_labeUnitsRow = new JLabel(getGuiStrs("labeUnitsRowCaption"));
	
	protected  JLabel m_labelMonday = new JLabel(getGuiStrs("labelMondayPeopleColumnCaption"));
	
	protected  JLabel m_labelTuesday = new JLabel(getGuiStrs("labelTuesdayColumnCaption"));
	
	protected  JLabel m_labelWednsday = new JLabel(getGuiStrs("labelWednesdayColumnCaption"));
	
	protected  JLabel m_labelThursday = new JLabel(getGuiStrs("labelThursdayColumnCaption"));
	
	protected  JLabel m_labelFriday = new JLabel(getGuiStrs("labelFridayColumnCaption"));

	protected  JLabel m_labelSaturday = new JLabel(getGuiStrs("labelSaturdayCaption"));
	
	protected  JLabel m_labelSunday = new JLabel(getGuiStrs("labelSundayCaption"));
	
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
	
	WFParseIndicies.TYPE m_type = TYPE.NAKL;
	
	JPanel m_panel_bottom = null;
	 
	JDialog m_dialog_parent = null;
	
    private static boolean[] m_calls_flags = { false, false, false, false};  
	
	public WsIndicesImportPanel(JDialog parent,WFParseIndicies.TYPE type) {
		super();
		
		m_type = type;
		
		m_dialog_parent = parent;
		
		createGui();
		
		Forwarder f = new  Forwarder();
		
		m_buttonUp.addActionListener(f);
		
		m_buttonSt.addActionListener(f);
		 
		m_panel_bottom.setVisible(false);
			
	}
	
	private void createGui() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		m_buttonUp = new JButton("v");
		
		m_buttonSt = new JButton("-");
		
		m_buttonUp.setToolTipText(getGuiStrs("showIndicesImportButtonToolTip"));
		
		m_buttonSt.setToolTipText(getGuiStrs("getStIndicesImportButtonToolTip"));
		
		JPanel panel_top = WsGuiTools.createHorizontalPanel();
		
		panel_top.add(m_mainLabel);  
		
		panel_top.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
 
		panel_top.add(m_buttonUp);
		
		panel_top.add(m_buttonSt);
		
		panel_top.add(Box.createHorizontalGlue());
		
		switch(m_type) {
		
			case RASKLADKA: { m_panel_bottom =  getGuiraskladka(); break;}
			
			case NAKL: { m_panel_bottom = getGuinakladna(); break;}
			
			case CATALOGRASKLADKA: { m_panel_bottom = getGuiKatalog(); break;}
			
			case KARTZVIT: { m_panel_bottom = getKartkaZvit(); break;}
			
			default: {
				break;
			}
		};
		
		add(panel_top);
		
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
		
		panel_grid.setLayout(new GridLayout(8,2));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelNameColumn);     panel_grid.add(m_spinNameColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		panel_grid.add(m_labeUnitsRow);         panel_grid.add(m_spinUnitsRow);

		if( !m_calls_flags[1]) {
			
			setIndicesSchema(new WFParseIndicies(TYPE.NAKL));
			
			m_calls_flags[1] = true;
		}
		else {
			
			setNakladnaIndicesStatic();
			
		}
		
		return panel_grid;
		
	}
	
	
private JPanel getGuiraskladka() {
	
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
	    
		WsGuiTools.setComponentFixedHeight(m_spinSheetColumn, 15);
		
	    JPanel panel_left = WsGuiTools.createVerticalPanel();
		
		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(4,2));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelNameColumn);     panel_grid.add(m_spinNameColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		m_labelSheetColumn.setText(getGuiStrs("rasklLabelSheetExcelIndex"));
		
		m_labelKodColumn.setText(getGuiStrs("rasklLabelKodExcelIndex"));
		
		m_labelNameColumn.setText(getGuiStrs("rasklLabelNameExcelIndex"));
		
		m_labelQuantityColumn.setText(getGuiStrs("rasklLabelSumStartExcelIndex"));

		panel_left.add(panel_grid);         panel_left.add(Box.createVerticalGlue());
		
		JPanel panel_grid2 = new JPanel();	

		TitledBorder title = BorderFactory.createTitledBorder(getGuiStrs("labelSumWeekDaysCaption"));
		
		panel_grid2.setLayout(new GridLayout(7,2));
		
		panel_grid2.setBorder(title);
		
	    panel_grid2.add(m_labelMonday);  panel_grid2.add(m_spinMondayRow);
	    
	    panel_grid2.add( m_labelTuesday); panel_grid2.add(m_spinTuesdayRow);
		
		panel_grid2.add( m_labelWednsday);  panel_grid2.add(m_spinWednesdayRow);
		
		panel_grid2.add( m_labelThursday);  panel_grid2.add(m_spinThursdayRow);
		
		panel_grid2.add(m_labelFriday);  panel_grid2.add(m_spinFridayRow);
		
		panel_grid2.add(m_labelSaturday);    panel_grid2.add(m_spinSaturdayRow);
		
		panel_grid2.add(m_labelSunday); panel_grid2.add( m_spinSundayRow);
		
		main.add(panel_left);
		
		main.add(panel_grid2);
		
		TitledBorder title_main = BorderFactory.createTitledBorder(getGuiStrs("labelRaskladkaIndicesCaption"));
		
		main.setBorder(title_main);
		
		if( !m_calls_flags[0] ) {
			
			//setGuiraskladkaIndices();
			
			setIndicesSchema(new WFParseIndicies(TYPE.RASKLADKA));
			
			m_calls_flags[0] = true;
		}
		else {
		
			setGuiraskladkaIndicesStatic(); 
		}
			
		return main;
		
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
		
		panel_grid.setLayout(new GridLayout(4,2));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelNameColumn);     panel_grid.add(m_spinNameColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		if(!m_calls_flags[2]) {
			
			setIndicesSchema(new WFParseIndicies(TYPE.CATALOGRASKLADKA));
			
			m_calls_flags[2] = true;
		}
		else {
			
			setCatalogRaskladkaIndicesStatic();
		}
		
		return panel_grid;
	}
	
	
	private JPanel getKartkaZvit() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
		SpinnerModel model1 = new SpinnerNumberModel(-1, -1, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(-1, -1, 20000, 1); 
		
		m_spinKodColumn = new  JSpinner(model2);
	
		SpinnerModel model3 = new SpinnerNumberModel(-1, -1, 20000, 3); 
		
		m_spinNameColumn = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(-1, -1, 20000, 1);
		
		//this is start row 
		m_spinQuantityColumn = new  JSpinner(model4);
			
		m_labelKodColumn.setText(getGuiStrs("nameLabelCostColumnKodKZImportName"));
		
		m_labelQuantityColumn.setText(getGuiStrs("nameLabelstartRowKZImportName"));
		
		m_labelNameColumn.setText(getGuiStrs("KodRowKZImportNameLabel"));

		JPanel panel_grid = new JPanel();	
		
		panel_grid.setLayout(new GridLayout(4,2));
		
		panel_grid.add(m_labelSheetColumn); panel_grid.add(m_spinSheetColumn);
		
		panel_grid.add( m_labelNameColumn);	    panel_grid.add(	m_spinNameColumn);
		
		panel_grid.add(m_labelKodColumn);	    panel_grid.add(m_spinKodColumn);
		
		panel_grid.add(m_labelQuantityColumn); panel_grid.add(m_spinQuantityColumn);
		
		if( !m_calls_flags[3]) {
			
			setIndicesSchema(new WFParseIndicies(TYPE.KARTZVIT));
			
			m_calls_flags[3] = true;
		}
		else {
			
			setKZIndicesStatic();
		}
		
		return panel_grid;

	}
	
	public WFParseIndicies getIndicesSchema() {
		
		
		WFParseIndicies ind_schema = new WFParseIndicies();

		if(m_type == TYPE.RASKLADKA) {
	
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
		else if(m_type == TYPE.KARTZVIT) {
			
			 ind_schema.sheetIndex = (int)m_spinSheetColumn.getValue();
			 
			 ind_schema.kodRowIndex = (int)m_spinNameColumn.getValue();
				
			 ind_schema.kodColumnIndex = (int)m_spinKodColumn.getValue();
			
			 ind_schema.quantityColumnIndex = (int)m_spinQuantityColumn.getValue();
			
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
				}
				
			}
			if ( e.getSource() == m_buttonSt ) { 
				
				setIndicesSchema(new WFParseIndicies(m_type));
				
			}		
		}
	}
	
	public void setIndicesSchema(WFParseIndicies sc) {
	
		if(m_spinSheetColumn != null ) {
			
			m_spinSheetColumn.setValue( sc.sheetIndex);
		}
		
		if(sc.type == TYPE.RASKLADKA) {
			
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
	    
	    m_spinSheetColumn_v = sc.sheetIndex;
	    
	    if(sc.type == TYPE.NAKL) {
	    	
	    	m_spinKodColumn_v = sc.kodColumnIndex;
	    }
	    else {
	    	
	    	m_spinKodColumn_v = sc.kodRowIndex;
	    }

		m_spinNameColumn_v = sc.nameColumnIndex;

		m_spinQuantityColumn_v = sc.quantityColumnIndex;
		
		m_spinUnitsRow_v = sc.unitsColumnIndex;
			
		m_spinMondayRow_v = sc.mondaySumIndex;
		
		m_spinTuesdayRow_v = sc.tuesdaySumIndex;

		m_spinWednesdayRow_v = sc.wednesdaySumIndex;
		
		m_spinThursdayRow_v = sc.thursdaySumIndex;
		
		m_spinFridayRow_v = sc.fridaySumIndex;
		
	    m_spinSaturdayRow_v = sc.sartudaySumIndex;

	    m_spinSundayRow_v = sc.sundaySumIndex;
	
	}
	
	public void setGuiraskladkaIndices() {
		
		setIndicesSchema(new WFParseIndicies(TYPE.RASKLADKA));
			    
	}
	
	
	public void setkarkaZvitRaskladkaIndices() {
		
		
		setIndicesSchema(new WFParseIndicies(TYPE.KARTZVITRASKLADKA));
		    
	}
	
	public void setCatalogRaskladkaIndices() {
		
		setIndicesSchema(new WFParseIndicies(TYPE.CATALOGRASKLADKA));
			   
	}
	
	public void setNakladnaIndices() {
		
		setIndicesSchema(new WFParseIndicies(TYPE.NAKL));
			
	}
	
	
	public void setProdRestIndices() {
		
		setIndicesSchema(new WFParseIndicies(TYPE.PRODREST));
			
	}
	
	public void setKZIndices() {
		
		setIndicesSchema(new WFParseIndicies(TYPE.KARTZVIT));
				   
	}
	
	public void setNakladnaIndicesStatic() {
		
		m_spinSheetColumn.setValue(m_spinSheetColumn_v);
		
		m_spinKodColumn.setValue(m_spinKodColumn_v);
	
		m_spinNameColumn.setValue(m_spinNameColumn_v);
		
		m_spinQuantityColumn.setValue(	m_spinQuantityColumn_v);
		
		m_spinUnitsRow.setValue(m_spinUnitsRow_v);
	
	}
	
	
	public void setGuiraskladkaIndicesStatic() {
		
		m_spinSheetColumn.setValue(m_spinSheetColumn_v);

		m_spinKodColumn.setValue(m_spinKodColumn_v);

		m_spinNameColumn.setValue(m_spinNameColumn_v);

		m_spinQuantityColumn.setValue(m_spinQuantityColumn_v);
			
		m_spinMondayRow.setValue(m_spinMondayRow_v);
		
		m_spinTuesdayRow.setValue(m_spinTuesdayRow_v);

		m_spinWednesdayRow.setValue(m_spinWednesdayRow_v);
		
		m_spinThursdayRow.setValue(m_spinThursdayRow_v);
		
		m_spinFridayRow.setValue(m_spinFridayRow_v);
		
	    m_spinSaturdayRow.setValue(m_spinSaturdayRow_v);

	    m_spinSundayRow.setValue(m_spinSundayRow_v);
	    
	    
	}
	
	
	public void setCatalogRaskladkaIndicesStatic() {
		
		m_spinSheetColumn.setValue(m_spinSheetColumn_v);

		m_spinKodColumn.setValue(m_spinKodColumn_v);

		m_spinNameColumn.setValue(m_spinNameColumn_v);

		m_spinQuantityColumn.setValue(m_spinQuantityColumn_v);
		   
	}
	
	public void setKZIndicesStatic() {
		
		m_spinSheetColumn.setValue(m_spinSheetColumn_v);
		
		m_spinNameColumn.setValue(m_spinNameColumn_v);

		m_spinKodColumn.setValue(m_spinKodColumn_v);

		m_spinQuantityColumn.setValue(m_spinQuantityColumn_v);
	   
	}
	
	
	public void setkarkaZvitRaskladkaIndicesStatic() {
		
		m_spinSheetColumn.setValue(m_spinSheetColumn_v);

		m_spinKodColumn.setValue(m_spinKodColumn_v);

		m_spinNameColumn.setValue(m_spinNameColumn_v);

		m_spinQuantityColumn.setValue(m_spinQuantityColumn_v);
			
		m_spinMondayRow.setValue(m_spinMondayRow_v);
		
		m_spinTuesdayRow.setValue(m_spinTuesdayRow_v);

		m_spinWednesdayRow.setValue(m_spinWednesdayRow_v);
		
		m_spinThursdayRow.setValue(m_spinThursdayRow_v);
		
		m_spinFridayRow.setValue(m_spinFridayRow_v);
		
	    m_spinSaturdayRow.setValue( m_spinSaturdayRow_v);

	    m_spinSundayRow.setValue( m_spinSundayRow_v);
	    
	    
	}
	
	public void setAllStatic() {
		
		if(m_spinSheetColumn != null ) {
		
			m_spinSheetColumn_v = (int) m_spinSheetColumn.getValue();
		}
		
		if(m_spinKodColumn != null ) {
			
			m_spinKodColumn_v = (int) m_spinKodColumn.getValue();
		}
		
		if(m_spinNameColumn != null ) {
			
			m_spinNameColumn_v = (int) m_spinNameColumn.getValue();
		}
		
		if(m_spinQuantityColumn != null ) {
			
			m_spinQuantityColumn_v = (int) m_spinQuantityColumn.getValue();
		}
		
		if(m_spinUnitsRow != null ) {
			
			m_spinUnitsRow_v =  (int) m_spinUnitsRow.getValue();
		}
		
		if( m_spinMondayRow != null ) {	
			
			m_spinMondayRow_v = (int) m_spinMondayRow.getValue();
		}
		
		if(m_spinTuesdayRow != null ) {
			
			m_spinTuesdayRow_v = (int) m_spinTuesdayRow.getValue();
		}
		
		if(m_spinWednesdayRow != null ) {
			
			m_spinWednesdayRow_v = (int) m_spinWednesdayRow.getValue();
		}
		
		if(m_spinThursdayRow != null ) {
			
			m_spinThursdayRow_v = (int) m_spinThursdayRow.getValue();
		}
		
		if(m_spinFridayRow != null ) {
			
			m_spinFridayRow_v = (int) m_spinFridayRow.getValue();
		}
		
		if(m_spinSaturdayRow != null ) {
			
			m_spinSaturdayRow_v = (int) m_spinSaturdayRow.getValue();
		}
		
		if(m_spinSundayRow != null ) {
			
			m_spinSundayRow_v = (int) m_spinSundayRow.getValue();
		}	
	}
	
}
