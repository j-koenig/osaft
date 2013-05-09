/*******************************************************************************
 * Copyright (c) 2013 Jannis Koenig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jannis Koenig - initial API and implementation
 ******************************************************************************/
package de.uni_hannover.osaft.plugins.connnectorappdata.tables;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.uni_hannover.osaft.plugins.connnectorappdata.view.ConnectorAppDataView;
import de.uni_hannover.osaft.plugins.sqlreader.view.SQLReaderView;

/**
 * {@link TableModel} that extends the {@link DefaultTableModel} and provides
 * the opportunity to filter the content for a given search string
 * 
 * @author Jannis Koenig
 * 
 */
public class LiveSearchTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	//two vectors to save original row data and filtered row data
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
			for (int i = 0; i < getRowCount(); i++) {
				if (!(getValueAt(i, c) == null)) {
					return getValueAt(i, c).getClass();
				}
			}
		}
		return Object.class;
	}

	public void resetData() {
		setDataVector(originalData, columnNames);
		fireTableDataChanged();
	}

	/**
	 * Called if user types in a searchfield from {@link SQLReaderView} or
	 * {@link ConnectorAppDataView}. Iterates over each cell and evaluates if
	 * the cell contains the search string. Matching rows will be added to the
	 * {@link Vector} filteredData. After finishing the iteration, the
	 * datavector of the table model will be set to the filteredData-
	 * {@link Vector}
	 * 
	 * @param filterString
	 */
	public void filterData(String filterString) {
		filteredData.clear();
		for (int i = 0; i < originalData.size(); i++) {
			for (int j = 0; j < originalData.get(i).size(); j++) {
				if (originalData.get(i).get(j) != null
						&& originalData.get(i).get(j).toString().toLowerCase().contains(filterString.toLowerCase())) {
					filteredData.add(originalData.get(i));
					break;
				}
			}
		}
		setDataVector(filteredData, columnNames);
		fireTableDataChanged();
	}

}
