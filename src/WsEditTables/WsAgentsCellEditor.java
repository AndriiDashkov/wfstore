

package WsEditTables;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import WsControls.WsAgentComboBox;
import WsDataStruct.WsAgentData;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

/**
 * A custom editor for cells in the Country column.
 * @author www.codejava.net
 *
 */
public class WsAgentsCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
 
 
	private static final long serialVersionUID = 1L;
	
	private WsAgentData m_agent;
    
    public WsAgentsCellEditor() {
    	
    }
     
    @Override
    public Object getCellEditorValue() {
    	
        return this.m_agent;
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
        if (value instanceof WsAgentData) {
        	
            this.m_agent = (WsAgentData) value;
        }
         
        WsAgentComboBox combo = new WsAgentComboBox();
        
        combo.setRowIndex(row);
         
        if(m_agent == null) {
        	
	    	  if(combo.getItemCount()!= 0) {
	    		  
	        	combo.setSelectedIndex(0);
	        	
	        	m_agent = combo.getSelectedAgentData();
	    	  }
	    	  else {
	    		  
	    		  m_agent =null;
	    	  }
        }
        else {
        	combo.setSelectedItem(m_agent.name);
        }
         
        combo.addActionListener(this);
         
        return combo;
    }
 
    @Override
    public void actionPerformed(ActionEvent event) {
    	
    	WsAgentComboBox combo = (WsAgentComboBox) event.getSource();
        
        m_agent =  combo.getSelectedAgentData();
        
    }
}
