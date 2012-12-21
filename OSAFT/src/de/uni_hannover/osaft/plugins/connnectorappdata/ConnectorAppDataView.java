package de.uni_hannover.osaft.plugins.connnectorappdata;

import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.CustomDateCellRenderer;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.CustomDefaultTableModel;

@PluginImplementation
public class ConnectorAppDataView implements ViewPlugin, ActionListener {

	// TODO: filechooser für überordner von csv dateien, filechooser in jedem
	// tab wäre bisschen übertrieben
	// panels nur adden, wenn dateien dazu gefunden wurden

	private JTabbedPane tabs;
	private JPanel calendarPanel, smsPanel, browserHPanel, browserSPanel, callPanel, contactPanel,
			mmsPanel, preferencesPanel;
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

	/**
	 * @wbp.parser.entryPoint
	 */
	@Init
	public void init() {

		tabVector = new Vector<JPanel>();
		initGUI();
		fc = new JFileChooser();

		// tun, wenn erstes mal dir gewählt wurde
		// fc.setCurrentDirectory(dir)

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
		smsTable = new JTable();

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

		smsScrollPane = new JScrollPane(smsTable);
		smsPanel.add(smsScrollPane, BorderLayout.CENTER);

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
		System.out.println("app wurde gewählt");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openCSVButton) {
			int returnVal = fc.showOpenDialog(tabs);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File folder = fc.getSelectedFile();
				controller.iterateChosenFolder(folder);
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

	// unschön:

	public void addBrowserHistoryTab(CustomDefaultTableModel model) {
		browserHTable.setModel(model);
		// tabs.add(browserHPanel, 0);
		tabVector.add(browserHPanel);
	}

	public void addBrowserSearchTab(CustomDefaultTableModel model) {
		browserSTable.setModel(model);
		// tabs.add(browserSPanel, 0);
		tabVector.add(browserSPanel);
	}

	public void addCalendarTab(CustomDefaultTableModel model) {
		calendarTable.setModel(model);
		// tabs.add(calendarPanel, 0);
		tabVector.add(calendarPanel);
	}

	public void addCallTab(CustomDefaultTableModel model) {
		callTable.setModel(model);
		// tabs.add(callPanel, 0);
		tabVector.add(callPanel);
	}

	public void addContactTab(CustomDefaultTableModel model) {
		contactTable.setModel(model);
		// tabs.add(contactPanel, 0);
		tabVector.add(contactPanel);
	}

	public void addMMSTab(CustomDefaultTableModel model) {
		mmsTable.setModel(model);
		// tabs.add(mmsPanel, 0);
		tabVector.add(mmsPanel);
	}

	public void addSMSTab(CustomDefaultTableModel model) {
		smsTable.setModel(model);
		// tabs.add(smsPanel, 0);
		tabVector.add(smsPanel);
	}

	public void addTabs() {
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

}
