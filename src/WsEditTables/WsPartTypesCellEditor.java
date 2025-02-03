
package WsEditTables;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import WsControls.WsPartTypesComboBox;
import WsDataStruct.WsPartType;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsPartTypesCellEditor extends AbstractCellEditor implements TableCellEditor, FocusListener, ActionListener {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WsPartType m_part;
    
    public WsPartTypesCellEditor() {
    	
    }
     
    @Override
    public Object getCellEditorValue() {
    	
        return this.m_part;
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
        if (value instanceof WsPartType) {
        	
            this.m_part = (WsPartType) value;
        }
         
        WsPartTypesComboBox combo = new WsPartTypesComboBox();
        
        combo.setRow(row);
         
        if(m_part == null) {
        	
        	combo.setSelectedIndex(0);
        	
        	m_part = combo.getSelectedPartData();
        }
        else {
        	
        	combo.setSelectedItem(m_part.name);
        }
         
        combo.addActionListener(this);
         
        
        return combo;
    }
 
    @Override
    public void actionPerformed(ActionEvent event) {
    	
    	WsPartTypesComboBox combo = (WsPartTypesComboBox) event.getSource();
        
        m_part =  combo.getSelectedPartData();
        
        WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.NEW_PART_TYPE_PRIHOD_SELECTED);
        
        ev.setId(combo.getRow());
        
        ev.setDataObject(m_part);
        
 		WsEventDispatcher.get().fireCustomEvent(ev);
 		
        WsEventEnable ev1 = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
        
	 	WsEventDispatcher.get().fireCustomEvent(ev1);
    }

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		
		System.out.println("111");
		
	}
}