package WsControls;

import static WsMain.WsUtils.getGuiStrs;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import WsDataStruct.WsAgentData;
import WsEditTables.WsImportExcelEditTable;
import WsMain.WsGuiTools;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class WsNaklImportPanelControl extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected  WsFileChooser m_textR = new WsFileChooser("Файл накладної для імпорту :");

	protected  WsImportExcelEditTable m_tableR = null;

	protected  JButton m_insertButtonR = new JButton("Вставити в таблицю");
	
	JSpinner m_spinSheetColumn = null;
	
	JSpinner m_spinKodColumn = null;
	
	JSpinner m_spinNameColumn = null;
	
	JSpinner m_spinQuantityColumn = null;
	
	public WsNaklImportPanelControl() {
		
		String[] columnNames = { getGuiStrs("excelImportSourceFileName") };
		
		m_tableR = new  WsImportExcelEditTable(columnNames, true);
		
		createGui();
		
		addListeners();
		
	}
	

	private void createGui() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
		Dimension d = m_textR.getMaximumSize();
		
		d.width = 500 ;
		
		d.height = 50 ;
		
		m_textR.setMaximumSize(d);
		
		m_textR.setMinimumSize(d);
			
		JPanel panel1 = WsGuiTools.createHorizontalPanel();
			
		panel1.add(m_textR);
		
		panel1.add(m_insertButtonR);
		
		panel1.add(Box.createHorizontalStrut( WsUtils.HOR_STRUT));
			
		JScrollPane scroll = new JScrollPane(m_tableR);
	        
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
		Dimension d1 =  scroll.getMaximumSize();
		
		d1.height = 200 ;
		
		scroll.setMaximumSize(d1);
        
        add(panel1);
        
        add(scroll);
        
        add(createRightSpinnersPanel());
        
        add(Box.createVerticalGlue());
 		
	}

	private void addListeners() {

		m_insertButtonR.addActionListener(new ActionListener() {
	        
			@Override
			public void actionPerformed(ActionEvent e) {
				
				WsAgentData d = new WsAgentData();
				
				d.contact = m_textR.getFullFilePath();
				
				if(d.contact != null && !d.contact.isEmpty()) {
					
					m_tableR.addRow(d);
				}
				
			}
		});
		
	}
	
	
	private JPanel createRightSpinnersPanel() {
		
		SpinnerModel model1 = new SpinnerNumberModel(10, 0, 20000, 1);  
		
		m_spinSheetColumn = new  JSpinner(model1);
		
		SpinnerModel model2 = new SpinnerNumberModel(0, 0, 20000, 1);  
		
		m_spinKodColumn = new  JSpinner(model2);
		
		SpinnerModel model3 = new SpinnerNumberModel(1, 0, 20000, 1);  
		
		m_spinNameColumn = new  JSpinner(model3);
		
		SpinnerModel model4 = new SpinnerNumberModel(6, 0, 20000, 1);  
		
		m_spinQuantityColumn = new  JSpinner(model4);

		JLabel sheetLabel = new JLabel("Індекс листа в Excel :");
		
		JLabel kodLabel = new JLabel("Індекс колонки кода в Excel :");
		
		JLabel nameLabel = new JLabel("Індекс колонки найменування в Excel :");
		
		JLabel quantityLabel = new JLabel("Індекс колонки кількості в Excel :");
		
		JPanel p = new JPanel();
		
		GridLayout ly = new GridLayout(0,3);
		
		p.setLayout(ly);
		
		p.add(sheetLabel);    p.add(m_spinSheetColumn);    p.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT*2));
		
		p.add(kodLabel);      p.add(m_spinKodColumn);      p.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT*2));
		
		p.add(nameLabel);     p.add(m_spinNameColumn);     p.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT*2));
		
		p.add(quantityLabel); p.add(m_spinQuantityColumn); p.add(Box.createHorizontalStrut(WsUtils.HOR_STRUT*2));
		
		return p;
				
	}
	
	
	
	public HashMap<String, Integer> getSpinnerMap() {
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("kod_column" , (int)m_spinKodColumn.getValue());
		
		map.put("name_column" ,(int)m_spinNameColumn.getValue());
		
		map.put("out_quantity_column" ,(int)m_spinQuantityColumn.getValue());
		
		map.put("sheet_index" ,(int)m_spinSheetColumn.getValue());
		
		return map;
	}
	
	
	public Vector<WsAgentData> getData()
	{
		
		return  m_tableR.getData();
	}

}
