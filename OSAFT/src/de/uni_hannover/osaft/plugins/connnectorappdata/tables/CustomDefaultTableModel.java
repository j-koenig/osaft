package de.uni_hannover.osaft.plugins.connnectorappdata.tables;

import javax.swing.table.DefaultTableModel;

public class CustomDefaultTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public CustomDefaultTableModel(Object[] columnNames) {
		super(columnNames, 0);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public Class<? extends Object> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

}
