
package WsEditTables;

import static WsMain.WsUtils.getMessagesStrs;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventEnable;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */

public class WsQuantityControlCellEditor extends AbstractCellEditor implements TableCellEditor, FocusListener, ActionListener {
 
	private static final long serialVersionUID = 1L;
	
	private double m_part;
	
    public WsQuantityControlCellEditor() {
    	
    }
     
    @Override
    public Object getCellEditorValue() {
    	
        return this.m_part;
    }

    
    private class TextField extends JTextField {
    	
		private static final long serialVersionUID = 1L;
		
		int m_row = -1;
		
    	JTable m_parent_table = null;
    	
    	double m_old_value = -1.0;
    	
    	public TextField(int row, JTable t, double old_value) {
    		super();
    		
    		m_row = row;
    		
    		m_parent_table = t;
    		
    		m_old_value = old_value;
    	}
    	
    	@SuppressWarnings("unused")
		public int getRow() { return m_row; }
    	
    	@SuppressWarnings("unused")
		public void setOldValue() {
    		
    		String txt = getText();
    		
    		if(txt.isEmpty()) {
    			
    			m_old_value = 1.0;
    		}
    		else {
    		
    			m_old_value = Double.valueOf(txt);
    		}
    	}
    	
    	public double getRestValue() {
    		
    		return Double.parseDouble((String) m_parent_table.getModel().getValueAt(m_row, 4));
    	}
    	
    	public void setNewRestValue(double quantity, double rest) {
    		
    		m_parent_table.getModel().setValueAt(quantity, m_row, 3);
    		
    		m_parent_table.getModel().setValueAt(Double.toString(rest), m_row, 4);
    		
    		((WsPrihodPartEditTableModel)m_parent_table.getModel()).fireTableDataChanged();
    		
    	     WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
    	        
    		 WsEventDispatcher.get().fireCustomEvent(ev);
    		
    	}
    	
    	public boolean isRowNew() {
    		
    		return ((WsPrihodPartEditTableModel)m_parent_table.getModel()).isRowNew(m_row );
    	
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
		
		double rest = t.getRestValue();
		
		double used = t.m_old_value - rest;
		
		double new_value = 0.0;
		
		try {
			
			new_value =  Double.valueOf(t.getText());
			
		}catch(java.lang.NumberFormatException ex) {
			
			return;
		}
		
		if(t.isRowNew()) {
			
			t.setNewRestValue(new_value, new_value);
			
			m_part = new_value;	
			
			t.setText(Double.toString(m_part));
			
		}else {
			
			if(new_value < used) {
				
				t.setText(Double.toString(t.m_old_value));
				
				m_part = t.m_old_value;
				
				String operationMessage = getMessagesStrs("quantityRestPrihodMismatch");
				
				JOptionPane.showMessageDialog(
	       			    WsUtils.get().getMainWindow(),
	       			    operationMessage,
	       			    getMessagesStrs("messageInfoCaption"),
	       			    JOptionPane.CLOSED_OPTION);
			}
			else {
				
				t.setNewRestValue(new_value, new_value - used);
			}
		}
		
        WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
        
	 	WsEventDispatcher.get().fireCustomEvent(ev);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		TextField t = (TextField) e.getSource();
		
		m_part =  Double.valueOf(t.getText());
		
        WsEventEnable ev = new WsEventEnable(WsEventEnable.TYPE.REFRESH_PRIHOD_SUM);
        
	 	WsEventDispatcher.get().fireCustomEvent(ev);
		
	}
}