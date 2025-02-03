/**
 * 
 */
package WsEditTables;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;


/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsCostContractPriceCellEditor extends AbstractCellEditor implements TableCellEditor, FocusListener, ActionListener {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double m_part;
	
	private int m_column_index = -1;

    public WsCostContractPriceCellEditor(int index) {
    	
    	 m_column_index = index;
    }
     
    @Override
    public Object getCellEditorValue() {
    	
        return this.m_part;
    }

    
    private class TextField extends JTextField {
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		int m_row = -1;
		
    	JTable m_parent_table = null;

    	
    	public TextField(int row, JTable t, double old_value) {
    		super();
    		
    		m_row = row;
    		
    		m_parent_table = t;
    	
    	}
    	
    	@SuppressWarnings("unused")
		public int getRow() { return m_row; }
    	

    	
    	public void setNewValue(double cost) {
    		
    		m_parent_table.getModel().setValueAt(cost, m_row, WsCostContractPriceCellEditor.this.m_column_index);
    		
    		((WsContractPricesEditTableModel)m_parent_table.getModel()).fireTableDataChanged();
 
    		
    	}	
    }
 
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
        if (value instanceof Double) {
        	
            this.m_part = (double) value;
        }
         
        TextField t = new TextField(row, table, m_part);
         
        t.setText(String.valueOf(this.m_part));
         
        t.addFocusListener(this);
        
        t.addActionListener(this);
         
        return t;
    }
 


	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {

	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {
		
		stopCellEditing();
		
		TextField t = (TextField) e.getSource();
		
		try {
			
			double new_value =  Double.valueOf(t.getText());
			
			t.setNewValue(new_value);
			
			m_part = new_value;	
				
			t.setText(Double.toString(m_part));
						
		}catch(java.lang.NumberFormatException ex) {
			
			
		}
  
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		TextField t = (TextField) e.getSource();
		
		m_part =  Double.valueOf(t.getText());
		
				
	}
}
