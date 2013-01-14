package de.uni_hannover.osaft.plugins.connnectorappdata.tables;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class LiveSearchTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private Vector<Vector<Object>> filteredData;
	private Vector<Vector<Object>> originalData;
	private Vector<Object> columnNames;

	public LiveSearchTableModel(Object[] columnNames) {
		super(columnNames, 0);
		filteredData = new Vector<Vector<Object>>();
		originalData = new Vector<Vector<Object>>();
		this.columnNames = new Vector<Object>();
		for (int i = 0; i < columnNames.length; i++) {
			this.columnNames.add(columnNames[i]);
		}
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addRow(Object[] row) {
		super.addRow(row);
		originalData = getDataVector();
	}

	// needed for custom cell rendering (in this case just
	// CustomDateCellRenderer)
	public Class<? extends Object> getColumnClass(int c) {
		if (!getDataVector().isEmpty()) {
			return getValueAt(0, c).getClass();
		}
		return Object.class;
	}

	public void resetData() {
		setDataVector(originalData, columnNames);
		fireTableDataChanged();
	}

	// when the user types in the searchfield, the table will be filtered for
	// this input
	public void filterData(String filterString) {
		filteredData.clear();
		for (int i = 0; i < originalData.size(); i++) {
			for (int j = 0; j < originalData.get(i).size(); j++) {
				if (originalData.get(i).get(j).toString().toLowerCase().contains(filterString)) {
					filteredData.add(originalData.get(i));
					break;
				}
			}
		}
		setDataVector(filteredData, columnNames);
		fireTableDataChanged();
	}

}
