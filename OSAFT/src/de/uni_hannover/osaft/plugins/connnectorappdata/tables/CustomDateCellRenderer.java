package de.uni_hannover.osaft.plugins.connnectorappdata.tables;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomDateCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected,
			boolean hasFocus, int row, int column) {

		Date date = (Date) obj;
		DateFormat df = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

		return super.getTableCellRendererComponent(table, df.format(date), isSelected, hasFocus,
				row, column);
	}

}
