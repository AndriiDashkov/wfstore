
package WsControls;

import static WsMain.WsUtils.getGuiStrs;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import WsEvents.WsEventDispatcher;
import WsEvents.WsEventNewRashodDate;
import WsMain.WsUtils;

/**
 * @author Andrii Dashkov license GNU GPL v3
 *
 */
public class Ws2DatesControl extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JLabel m_l_start = new JLabel(getGuiStrs("sDate2Label"));
	
	private JLabel m_l_end = new JLabel(getGuiStrs("eDate2Label"));
	
	WsNumericField m_days_1 = null;
	
	WsNumericField m_month_1 = null;
	
	WsNumericField m_year_1 = null;

	WsNumericField m_days_2 = null;
	
	WsNumericField m_month_2 = null;

	WsNumericField m_year_2 = null;
	
	private SimpleDateFormat m_df = new SimpleDateFormat("dd.MM.yy");

	boolean m_singleDate = false;
	
	public Ws2DatesControl(boolean singleDate) {
		
			m_singleDate = singleDate;
		
			m_days_1 = new WsNumericField(this, 1, 2);
			
			m_days_2 =  new WsNumericField(this, 1, 2);
			
			m_days_1.setMaxValue(31);
			
			m_days_2.setMaxValue(31);
			
			m_month_1 = new WsNumericField(this, 1, 2);
			
			m_month_1.setMaxValue(12);
			
			m_month_2 =  new WsNumericField(this, 1, 2);
			
			m_month_2.setMaxValue(12);
			
			m_year_1 = new WsNumericField(this, 23, 2);
			
			m_year_1.setMaxValue(30);
			
			m_year_2 =  new WsNumericField(this, 1, 2);
			
			m_year_2.setMaxValue(30);
			
			createGUI();
		
	}

	private void createGUI() {
		
		 setLinearLayout();
		 
		 setTooltips();
		 	
	}
	
	
	@SuppressWarnings("unused")
	private void setLinearLayout() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		if(m_singleDate) {
			
			add(m_days_1);
			
			add(m_month_1);
			
			add(m_year_1);
			
			hideSecondDate();
			
			Dimension size = m_days_1.getMinimumSize();
			
			size.width = 35;
			
			m_days_1.setMinimumSize(size);
			
			Dimension size1 = m_days_1.getMaximumSize();
			
			size1.width = 35;
			
			m_days_1.setMaximumSize(size);
			
			size = m_month_1.getMinimumSize();
			
			size.width = 35;
			
			m_month_1.setMinimumSize(size);
			
			size1 = m_month_1.getMaximumSize();
			
			size1.width = 35;
			
			m_month_1.setMaximumSize(size);
			
			size = m_year_1.getMinimumSize();
			
			size.width = 35;
			
			m_year_1.setMinimumSize(size);
			
			size1 = m_year_1.getMaximumSize();
			
			size1.width = 35;
			
			m_year_1.setMaximumSize(size);
			
	
		}
		else {
			
			add(m_l_start);
			
			add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
			
			add(m_days_1);
			
			add(m_month_1);
			
			add(m_year_1);
		
			add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
			
			add(m_l_end);
			
			add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
			
			add(m_days_2);
			
			add(m_month_2);
			
			add(m_year_2);
			
			add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
			
			Dimension size = m_days_1.getMinimumSize();
			
			size.width = 35;
			
			m_days_1.setMinimumSize(size);
			
			Dimension size1 = m_days_1.getMaximumSize();
			
			size1.width = 35;
			
			m_days_1.setMaximumSize(size);
			
			size = m_month_1.getMinimumSize();
			
			size.width = 35;
			
			m_month_1.setMinimumSize(size);
			
			size1 = m_month_1.getMaximumSize();
			
			size1.width = 35;
			
			m_month_1.setMaximumSize(size);
			
			size = m_year_1.getMinimumSize();
			
			size.width = 35;
			
			m_year_1.setMinimumSize(size);
			
			size1 = m_year_1.getMaximumSize();
			
			size1.width = 35;
			
			m_year_1.setMaximumSize(size);
			
			size = m_days_2.getMinimumSize();
			
			size.width = 35;
			
			m_days_2.setMinimumSize(size);
			
			size1 = m_days_2.getMaximumSize();
			
			size1.width = 35;
			
			m_days_2.setMaximumSize(size);
			
			size = m_month_2.getMinimumSize();
			
			size.width = 35;
			
			m_month_2.setMinimumSize(size);
			
			size1 = m_month_2.getMaximumSize();
			
			size1.width = 35;
			
			m_month_2.setMaximumSize(size);
			
			size = m_year_2.getMinimumSize();
			
			size.width = 35;
			
			m_year_2.setMinimumSize(size);
			
			size1 = m_year_2.getMaximumSize();
			
			size1.width = 35;
			
			m_year_2.setMaximumSize(size);
	
			add(Box.createHorizontalGlue());
		
		}
	}
	
	@SuppressWarnings("unused")
	private void setGridLayout() {
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		add(m_l_start);

		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		JPanel panel1 = new JPanel();
		
		GridLayout l1 = new GridLayout(0,3);
		
		l1.setVgap(0);
		
		panel1.setLayout(l1);
		
		panel1.add(m_days_1); panel1.add(m_month_1); panel1.add(m_year_1);
		
		add(panel1);
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		add(m_l_end);
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
		JPanel panel2 = new JPanel();
		
		GridLayout l2 = new GridLayout(0,3);
		
		l2.setVgap(0);
		
		panel2.setLayout(l2);
		
		panel2.add(m_days_2); panel2.add(m_month_2); panel2.add(m_year_2);
		
		add(panel2);
		
		add(Box.createHorizontalStrut(WsUtils.HOR_STRUT));
		
	}
	
	
	public java.util.Date getStartDate() {
	
		int day = (int)m_days_1.getValue();
			
		int month = (int)m_month_1.getValue();
		
		int year = (int)m_year_1.getValue() + 2000;
		
		String t = String.valueOf(day) +"." + String.valueOf(month)  + "." +String.valueOf(year);
		
		java.util.Date d;
		
		try {
			
			d = m_df.parse(t);
			
		} catch (ParseException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			
			}
			
			return null;
		}
		
		return d;
	}
	
	public java.util.Date getEndDate() {
		
		int day = (int)m_days_2.getValue();
		
		int month = (int)m_month_2.getValue();
		
		int year = (int)m_year_2.getValue() + 2000;
		
		String t = String.valueOf(day) +"." + String.valueOf(month)  + "." +String.valueOf(year);
		
		java.util.Date d;
		
		try {
			
			d = m_df.parse(t);
			
		} catch (ParseException e) {
			
			if( WsUtils.isDebug() ) {
				
				e.printStackTrace();
			
			}
			
			return null;
		}
		
		return d;
		
	}
	
	public java.sql.Date getSqlStartDate() {
		
		java.util.Date dt = getStartDate();
		
		if(null == dt) { 
			
			return null; 
			
		}
		else {
			
		return new java.sql.Date(dt.getTime());
		
		}
		
	}
	
	public java.sql.Date getSqlEndDate() {
		
		
		return new java.sql.Date(getEndDate().getTime());
		
	}
	
	public void setStartDate(java.util.Date d) {
		
		Calendar c =  Calendar.getInstance();
		
		c.setTime(d);
		
		int year       = c.get(Calendar.YEAR) - 2000;
		
		int month      = c.get(Calendar.MONTH) + 1; // Jan = 0, dec = 11
		
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		
		m_days_1.setText(String.valueOf(dayOfMonth));
		
		m_month_1.setValue(month);
		
		m_year_1.setValue(year);
			
	}
	
	public void setEndDate(java.util.Date d) {
		
		Calendar c =  Calendar.getInstance();
		
		c.setTime(d);
		
		int year       = c.get(Calendar.YEAR) - 2000;
		
		int month      = c.get(Calendar.MONTH) + 1; // Jan = 0, dec = 11
		
		int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
		
		m_days_2.setValue(dayOfMonth);
		
		m_month_2.setValue(month);
		
		m_year_2.setValue(year);
		
	}
	
	public void setStartDate(int day, int month, int year) {
		
		Calendar c =  Calendar.getInstance();
		
		c.set(year, month, day);
	
		setStartDate(c.getTime());
		
	}
	
	public void setEndDate(int day, int month, int year) {
		
		Calendar c =  Calendar.getInstance();
		
		c.set(year, month, day);
	
		setEndDate(c.getTime());
		
	}
	
	
	public void setCurrentStartDate() {
		
		java.util.Date s = Calendar.getInstance().getTime();
	
		setStartDate(s);
		
	}
	
	public void setCurrentEndDate() {
		
		java.util.Date s = Calendar.getInstance().getTime();
		
		setEndDate(s);
		
	}
	
	public void setSqlStartDate(java.sql.Date d) {
		
		java.util.Date d_ = new java.util.Date(d.getTime());
		
		setStartDate(d_);
		
	}
	
	public void setSqlEndDate(java.sql.Date d) {
		
		java.util.Date d_ = new java.util.Date(d.getTime());
		
		setEndDate(d_);
		
	}
	
	public void valueChanged() {
		
		int m1 = m_month_1.getValue();
		
		int y1 = m_year_1.getValue();
				
		int m2 = m_month_2.getValue();
		
		int y2 = m_year_2.getValue();
		
		Calendar c = Calendar.getInstance();
		
		c.set(y2, m2 - 1, 1); 
		
		int days2 = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		c.set(y1, m1 - 1, 1); 
		
		int days1 = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		m_days_1.setMaxValue(days1);
		
		m_days_2.setMaxValue(days2);
		
		WsEventNewRashodDate ev = new WsEventNewRashodDate();
		
		WsEventDispatcher.get().fireCustomEvent(ev);
		
	}
	
	
	private void hideSecondDate() {
		
		
		m_days_2.setVisible(false);
		
		m_month_2.setVisible(false);
		
		m_year_2.setVisible(false);
		
		m_l_end.setVisible(false);
		
		m_l_start.setVisible(false);
		
		
	}
	
	private void setTooltips() {
		
		m_days_1.setToolTipText(getGuiStrs("dayFieldToolTipName"));
		
		m_days_2.setToolTipText(getGuiStrs("dayFieldToolTipName"));
		
		m_month_1.setToolTipText(getGuiStrs("monthFieldToolTipName"));
		
		m_month_2.setToolTipText(getGuiStrs("monthFieldToolTipName"));
		
		m_year_1.setToolTipText(getGuiStrs("yearFieldToolTipName"));
		
		m_year_2.setToolTipText(getGuiStrs("yearFieldToolTipName"));
		
	}
	
	
	public void setEnabled(boolean flag) {
		
		m_days_1.setEnabled(flag);
		
		m_days_2.setEnabled(flag);
		
		m_month_1.setEnabled(flag);
		
		m_month_2.setEnabled(flag);
		
		m_year_1.setEnabled(flag);
		
		m_year_2.setEnabled(flag);
		
	}

}
