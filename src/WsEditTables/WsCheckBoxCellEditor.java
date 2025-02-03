
package WsEditTables;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */


public class  WsCheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	
	private static final long serialVersionUID = 1L;

	Boolean m_value = false;
	
	private int m_index = 13;
	
    public  WsCheckBoxCellEditor() {
    	
    	
    }
     
    @Override
    public Object getCellEditorValue() {
    	
        return this.m_value;
    }
    
    public void setColumnIndex(int i) {
    	
        this.m_index = i;
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
        if (value instanceof Boolean) {
        	
            this.m_value = (Boolean) value;
        }
        
       Boolean dt = (Boolean)table.getValueAt(row, this.m_index);
         
       JCheckBox box = new JCheckBox();
       
       box.setSelected(dt);
         
       box.addActionListener(this);
         
       return box;
    }
 
    @Override
    public void actionPerformed(ActionEvent event) {
    	
    	JCheckBox box = (JCheckBox) event.getSource();
        
        m_value =  box.isSelected();
    }
    
}
