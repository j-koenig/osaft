package de.uni_hannover.osaft.plugins.connnectorappdata.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.plugins.connnectorappdata.ConnectorAppDataController;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.CustomDateCellRenderer;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.CustomDefaultTableModel;

@PluginImplementation
public class ConnectorAppDataView implements ViewPlugin, ActionListener, ListSelectionListener {

	// TODO:
	// panels nur adden, wenn dateien dazu gefunden wurden

	private JTabbedPane tabs;
	private JPanel calendarPanel, smsPanel, browserHPanel, browserSPanel, callPanel, contactPanel,
			mmsPanel, preferencesPanel;
	private MMSInfoPanel mmsInfo;
	// TODO: wech
	JFrame frame;
	private ConnectorAppDataController controller;
	private JButton openCSVButton, pushAppButton, pullCSVButton;
	private JFileChooser fc;
	private JScrollPane calendarScrollPane, callScrollPane, browserHScrollPane, browserSScrollPane,
			contactScrollPane, mmsScrollPane, smsScrollPane;
	private JTable calendarTable, callTable, browserHTable, browserSTable, contactTable, mmsTable,
			smsTable;
	private Vector<JPanel> tabVector;
	private JTextArea smsTextArea;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Init
	public void init() {

		tabVector = new Vector<JPanel>();
		initGUI();
		fc = new JFileChooser();

		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		controller = new ConnectorAppDataController(this);
	}

	public void initGUI() {
		// TODO: jframe wieder entfernen, nur für window builder genutzt
		frame = new JFrame();
		tabs = new JTabbedPane();

		calendarPanel = new JPanel(new BorderLayout(0, 0));
		calendarPanel.setName("Calendar");

		callPanel = new JPanel(new BorderLayout(0, 0));
		callPanel.setName("CallLogs");

		browserHPanel = new JPanel(new BorderLayout(0, 0));
		browserHPanel.setName("Browser History");

		browserSPanel = new JPanel(new BorderLayout(0, 0));
		browserSPanel.setName("Browser Search History");

		contactPanel = new JPanel(new BorderLayout(0, 0));
		contactPanel.setName("Contacts");

		mmsPanel = new JPanel(new BorderLayout(0, 0));
		mmsPanel.setName("MMS");

		smsPanel = new JPanel(new BorderLayout(0, 0));
		smsPanel.setName("SMS");

		calendarTable = new JTable();
		callTable = new JTable();
		browserHTable = new JTable();
		browserSTable = new JTable();
		contactTable = new JTable();
		mmsTable = new JTable();
		mmsTable.getSelectionModel().addListSelectionListener(this);
		smsTable = new JTable();
		smsTable.getSelectionModel().addListSelectionListener(this);

		// columns are sortable:
		calendarTable.setAutoCreateRowSorter(true);
		callTable.setAutoCreateRowSorter(true);
		browserHTable.setAutoCreateRowSorter(true);
		browserSTable.setAutoCreateRowSorter(true);
		contactTable.setAutoCreateRowSorter(true);
		mmsTable.setAutoCreateRowSorter(true);
		smsTable.setAutoCreateRowSorter(true);

		// Date objects will be rendered differently
		CustomDateCellRenderer cdcr = new CustomDateCellRenderer();
		calendarTable.setDefaultRenderer(Date.class, cdcr);
		callTable.setDefaultRenderer(Date.class, cdcr);
		browserHTable.setDefaultRenderer(Date.class, cdcr);
		browserSTable.setDefaultRenderer(Date.class, cdcr);
		contactTable.setDefaultRenderer(Date.class, cdcr);
		mmsTable.setDefaultRenderer(Date.class, cdcr);
		smsTable.setDefaultRenderer(Date.class, cdcr);

		// put scrollpane-wrapped tables into panels
		calendarScrollPane = new JScrollPane(calendarTable);
		calendarPanel.add(calendarScrollPane, BorderLayout.CENTER);

		callScrollPane = new JScrollPane(callTable);
		callPanel.add(callScrollPane, BorderLayout.CENTER);

		browserHScrollPane = new JScrollPane(browserHTable);
		browserHPanel.add(browserHScrollPane, BorderLayout.CENTER);

		browserSScrollPane = new JScrollPane(browserSTable);
		browserSPanel.add(browserSScrollPane, BorderLayout.CENTER);

		contactScrollPane = new JScrollPane(contactTable);
		contactPanel.add(contactScrollPane, BorderLayout.CENTER);

		mmsScrollPane = new JScrollPane(mmsTable);
		mmsPanel.add(mmsScrollPane, BorderLayout.CENTER);
		mmsInfo = new MMSInfoPanel();
		mmsInfo.setPreferredSize(new Dimension(300, 0));
		mmsPanel.add(mmsInfo, BorderLayout.EAST);
		mmsPanel.add(new JTextField("Search"), BorderLayout.NORTH);

		smsScrollPane = new JScrollPane(smsTable);
		smsPanel.add(smsScrollPane, BorderLayout.CENTER);
		JPanel smsTextPanel = new JPanel(new BorderLayout());
		smsTextArea = new JTextArea();
		smsTextArea.setEditable(false);
		smsTextArea.setLineWrap(true);
		smsTextArea.setWrapStyleWord(true);
		smsTextPanel.add(new JScrollPane(smsTextArea));
		smsTextPanel.add(new JLabel("Text:"), BorderLayout.NORTH);
		smsTextPanel.setPreferredSize(new Dimension(300, 100));
		
		smsPanel.add(smsTextPanel, BorderLayout.EAST);

		openCSVButton = new JButton("Open CSV Files");
		openCSVButton.addActionListener(this);

		pushAppButton = new JButton("Push App to Phone");
		pushAppButton.addActionListener(this);

		pullCSVButton = new JButton("Pull CSV files from Phone");
		pullCSVButton.addActionListener(this);

		preferencesPanel = new JPanel();
		preferencesPanel.setName("Preferences");
		preferencesPanel.add(openCSVButton);
		preferencesPanel.add(pushAppButton);
		tabs.add(preferencesPanel);

		frame.getContentPane().add(tabs);

	}

	@Override
	public String getName() {
		return "Connector App Data";
	}

	@Override
	public JComponent getView() {
		return tabs;
	}

	@Override
	public void triggered() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openCSVButton) {
			int returnVal = fc.showOpenDialog(tabs);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File folder = fc.getSelectedFile();

				if (controller.iterateChosenFolder(folder)) {
					fc.setCurrentDirectory(folder);
				} else {
					JOptionPane.showMessageDialog(tabs, "No csv files found in this folder",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				calendarScrollPane.validate();
			}
		}

		if (e.getSource() == pushAppButton) {
			JOptionPane.showMessageDialog(tabs,
					"Connector App will be pushed to phone. Please confirm install on the phone",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			controller.pushApp();
		}
	}

	public void addTab(String sourceFile, CustomDefaultTableModel model) {
		if (sourceFile.equals(ConnectorAppDataController.BROWSER_HISTORY_FILENAME)) {
			browserHTable.setModel(model);
			tabVector.add(browserHPanel);
		} else if (sourceFile.equals(ConnectorAppDataController.BROWSER_SEARCH_FILENAME)) {
			browserSTable.setModel(model);
			tabVector.add(browserSPanel);
		} else if (sourceFile.equals(ConnectorAppDataController.CALENDAR_FILENAME)) {
			calendarTable.setModel(model);
			tabVector.add(calendarPanel);
		} else if (sourceFile.equals(ConnectorAppDataController.CALLS_FILENAME)) {
			callTable.setModel(model);
			tabVector.add(callPanel);
		} else if (sourceFile.equals(ConnectorAppDataController.CONTACTS_FILENAME)) {
			contactTable.setModel(model);
			tabVector.add(contactPanel);
		} else if (sourceFile.equals(ConnectorAppDataController.MMS_FILENAME)) {
			mmsTable.setModel(model);
			tabVector.add(mmsPanel);
		} else if (sourceFile.equals(ConnectorAppDataController.SMS_FILENAME)) {
			smsTable.setModel(model);
			tabVector.add(smsPanel);
		}
	}

	// shows tabs in alphabetic order
	public void showTabs() {
		tabs.removeAll();
		tabs.add(preferencesPanel);
		Collections.sort(tabVector, new Comparator<JPanel>() {
			public int compare(JPanel p1, JPanel p2) {
				return p1.getName().compareTo(p2.getName());
			}
		});

		for (int i = tabVector.size() - 1; i >= 0; i--) {
			tabs.add(tabVector.get(i), 0);
		}
		tabVector.clear();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(smsTable.getSelectionModel())) {
			int selectedRow = smsTable.getSelectedRow();
			if (selectedRow != -1) {
				smsTextArea.setText("" + smsTable.getValueAt(selectedRow, 2));
			}
		} else if (e.getSource().equals(mmsTable.getSelectionModel())) {
			int selectedRow = mmsTable.getSelectedRow();
			if (selectedRow != -1) {
				String id = (String) mmsTable.getValueAt(selectedRow, 0);
				String text = (String) mmsTable.getValueAt(selectedRow, 3);
				boolean hasAttachment = (Boolean) mmsTable.getValueAt(selectedRow, 5);
				mmsInfo.setInfo(id, text, hasAttachment, fc.getCurrentDirectory());
			}
		}
	}

}
