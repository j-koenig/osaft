package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.CustomDateCellRenderer;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.LiveSearchTableModel;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.TableColumnAdjuster;
import de.uni_hannover.osaft.plugins.connnectorappdata.view.ContactInfoPanel;
import de.uni_hannover.osaft.plugins.sqlreader.controller.SQLReaderController;

@PluginImplementation
public class SQLReaderView implements ViewPlugin, ActionListener, ListSelectionListener, MouseListener, FocusListener, KeyListener {

	private JTabbedPane tabs;
	private JScrollPane calendarScrollPane, callScrollPane, browserHScrollPane, browserBScrollPane, browserSScrollPane, contactScrollPane,
			mmsScrollPane, smsScrollPane, contactInfoScroll, whatsappScrollPane, facebookScrollPane, webviewScrollPane;
	private JTable calendarTable, callTable, browserHTable, browserBTable, browserSTable, contactTable, mmsTable, smsTable, whatsappTable,
			facebookTable, webviewTable;
	private JPanel calendarPanel, smsPanel, browserHPanel, browserBPanel, browserSPanel, callPanel, contactPanel, mmsPanel,
			preferencesPanel, whatsappPanel, facebookPanel, webviewPanel;
	private JTextField contactSearch, calendarSearch, browserHSearch, browserBSearch, browserSSearch, smsSearch, mmsSearch, callsSearch,
			whatsappSearch, facebookSearch, webviewSearch;
	private JPopupMenu contextMenu;
	private JMenuItem copyCell, copyRow;
	private JTextArea smsTextArea;
	private JButton openSQLButton;
	private JFileChooser fc;
	private Vector<JPanel> tabVector;

	private SQLReaderController controller;
	private File caseFolder;
	private ADBThread adb;
	private JComboBox whatsAppCombo, webviewCombo;

	// used for contextmenu
	private int currentX, currentY;
	private JTable currentTable;

	private DBMMSInfoPanel mmsInfo;
	private ContactInfoPanel contactInfo;
	private DBWhatsAppInfoPanel whatsappInfo;

	@Override
	@Init
	public void init() {

		controller = new SQLReaderController(this);
		tabVector = new Vector<JPanel>();
		initGUI();

		fc = new JFileChooser();

		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void initGUI() {
		tabs = new JTabbedPane();

		calendarPanel = new JPanel(new BorderLayout(0, 0));
		calendarPanel.setName("Calendar");

		callPanel = new JPanel(new BorderLayout(0, 0));
		callPanel.setName("CallLogs");

		browserHPanel = new JPanel(new BorderLayout(0, 0));
		browserHPanel.setName("Browser History");

		browserBPanel = new JPanel(new BorderLayout(0, 0));
		browserBPanel.setName("Browser Bookmarks");

		browserSPanel = new JPanel(new BorderLayout(0, 0));
		browserSPanel.setName("Browser Search History");

		contactPanel = new JPanel(new BorderLayout(0, 0));
		contactPanel.setName("Contacts");

		mmsPanel = new JPanel(new BorderLayout(0, 0));
		mmsPanel.setName("MMS");

		smsPanel = new JPanel(new BorderLayout(0, 0));
		smsPanel.setName("SMS");

		whatsappPanel = new JPanel(new BorderLayout(0, 0));
		whatsappPanel.setName("WhatsApp");

		facebookPanel = new JPanel(new BorderLayout(0, 0));
		facebookPanel.setName("Facebook");
		
		webviewPanel = new JPanel(new BorderLayout(0,0));
		webviewPanel.setName("Webview");

		preferencesPanel = new JPanel();
		preferencesPanel.setName("Preferences");
		tabs.add(preferencesPanel);

		ArrayList<JTable> tables = new ArrayList<JTable>();

		calendarTable = new JTable();
		callTable = new JTable();
		browserHTable = new JTable();
		browserBTable = new JTable();
		browserSTable = new JTable();
		contactTable = new JTable();
		webviewTable = new JTable();
		// selectionlistener listens for selection changes in the table
		contactTable.getSelectionModel().addListSelectionListener(this);
		mmsTable = new JTable();
		mmsTable.getSelectionModel().addListSelectionListener(this);
		smsTable = new JTable();
		smsTable.getSelectionModel().addListSelectionListener(this);
		whatsappTable = new JTable();
		whatsappTable.getSelectionModel().addListSelectionListener(this);
		facebookTable = new JTable();
		facebookTable.getSelectionModel().addListSelectionListener(this);
		tables.add(calendarTable);
		tables.add(callTable);
		tables.add(browserHTable);
		tables.add(browserBTable);
		tables.add(browserSTable);
		tables.add(contactTable);
		tables.add(smsTable);
		tables.add(mmsTable);
		tables.add(whatsappTable);
		tables.add(facebookTable);
		tables.add(webviewTable);

		// TODO scheint hier irgendwie nich zu funzen, zumindest bei browser
		// history nich...
		// Date objects will be rendered differently
		CustomDateCellRenderer cdcr = new CustomDateCellRenderer();

		for (int i = 0; i < tables.size(); i++) {
			tables.get(i).setDefaultRenderer(Date.class, cdcr);
			// columns are sortable:
			tables.get(i).setAutoCreateRowSorter(true);
			// reorder of columns is disabled
			tables.get(i).getTableHeader().setReorderingAllowed(false);
			tables.get(i).addMouseListener(this);
		}

		// put scrollpane-wrapped tables into panels, add listeners and
		// searchbars
		calendarScrollPane = new JScrollPane(calendarTable);
		calendarSearch = new JTextField("Search");
		calendarSearch.addKeyListener(this);
		calendarSearch.addFocusListener(this);
		calendarPanel.add(calendarSearch, BorderLayout.NORTH);
		calendarPanel.add(calendarScrollPane, BorderLayout.CENTER);

		callScrollPane = new JScrollPane(callTable);
		callsSearch = new JTextField("Search");
		callsSearch.addKeyListener(this);
		callsSearch.addFocusListener(this);
		callPanel.add(callsSearch, BorderLayout.NORTH);
		callPanel.add(callScrollPane, BorderLayout.CENTER);

		browserHScrollPane = new JScrollPane(browserHTable);
		browserHSearch = new JTextField("Search");
		browserHSearch.addFocusListener(this);
		browserHSearch.addKeyListener(this);
		browserHPanel.add(browserHSearch, BorderLayout.NORTH);
		browserHPanel.add(browserHScrollPane, BorderLayout.CENTER);

		browserBScrollPane = new JScrollPane(browserBTable);
		browserBSearch = new JTextField("Search");
		browserBSearch.addFocusListener(this);
		browserBSearch.addKeyListener(this);
		browserBPanel.add(browserBSearch, BorderLayout.NORTH);
		browserBPanel.add(browserBScrollPane, BorderLayout.CENTER);

		browserSScrollPane = new JScrollPane(browserSTable);
		browserSSearch = new JTextField("Search");
		browserSSearch.addFocusListener(this);
		browserSSearch.addKeyListener(this);
		browserSPanel.add(browserSSearch, BorderLayout.NORTH);
		browserSPanel.add(browserSScrollPane, BorderLayout.CENTER);

		// contacts, mms and sms also provide an "info"-panel
		contactScrollPane = new JScrollPane(contactTable);
		JPanel contactWrapper = new JPanel(new BorderLayout());
		contactWrapper.add(contactScrollPane, BorderLayout.CENTER);
		contactSearch = new JTextField("Search");
		contactSearch.addKeyListener(this);
		contactSearch.addFocusListener(this);
		contactWrapper.add(contactSearch, BorderLayout.NORTH);
		contactPanel.add(contactWrapper, BorderLayout.CENTER);
		contactInfo = new ContactInfoPanel();
		contactInfo.setPreferredSize(new Dimension(280, 740));
		contactInfoScroll = new JScrollPane(contactInfo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contactInfoScroll.setPreferredSize(new Dimension(300, 0));
		contactPanel.add(contactInfoScroll, BorderLayout.EAST);

		mmsScrollPane = new JScrollPane(mmsTable);
		JPanel mmsWrapper = new JPanel(new BorderLayout());
		mmsWrapper.add(mmsScrollPane, BorderLayout.CENTER);
		mmsSearch = new JTextField("Search");
		mmsSearch.addKeyListener(this);
		mmsSearch.addFocusListener(this);
		mmsWrapper.add(mmsSearch, BorderLayout.NORTH);
		mmsPanel.add(mmsWrapper, BorderLayout.CENTER);
		mmsInfo = new DBMMSInfoPanel();
		mmsInfo.setPreferredSize(new Dimension(300, 0));
		mmsPanel.add(mmsInfo, BorderLayout.EAST);

		smsScrollPane = new JScrollPane(smsTable);
		JPanel smsWrapper = new JPanel(new BorderLayout());
		smsWrapper.add(smsScrollPane, BorderLayout.CENTER);
		smsSearch = new JTextField("Search");
		smsSearch.addKeyListener(this);
		smsSearch.addFocusListener(this);
		smsWrapper.add(smsSearch, BorderLayout.NORTH);
		smsPanel.add(smsWrapper, BorderLayout.CENTER);
		JPanel smsTextPanel = new JPanel(new BorderLayout());
		smsTextArea = new JTextArea();
		smsTextArea.setEditable(false);
		smsTextArea.setLineWrap(true);
		smsTextArea.setWrapStyleWord(true);
		smsTextPanel.add(new JScrollPane(smsTextArea));
		smsTextPanel.add(new JLabel("Text:"), BorderLayout.NORTH);
		smsTextPanel.setPreferredSize(new Dimension(300, 100));
		smsPanel.add(smsTextPanel, BorderLayout.EAST);

		whatsappScrollPane = new JScrollPane(whatsappTable);
		whatsappSearch = new JTextField("Search");
		whatsappSearch.addFocusListener(this);
		whatsappSearch.addKeyListener(this);
		whatsAppCombo = new JComboBox();
		whatsAppCombo.addActionListener(this);
		whatsappInfo = new DBWhatsAppInfoPanel();
		whatsappInfo.setPreferredSize(new Dimension(300, 0));
		JPanel whatsappNorthPanel = new JPanel(new GridLayout(1, 2));
		whatsappNorthPanel.add(whatsAppCombo);
		whatsappNorthPanel.add(whatsappSearch);
		JPanel whatsappWrapper = new JPanel(new BorderLayout());
		whatsappWrapper.add(whatsappNorthPanel, BorderLayout.NORTH);
		whatsappWrapper.add(whatsappScrollPane, BorderLayout.CENTER);
		whatsappPanel.add(whatsappWrapper, BorderLayout.CENTER);
		whatsappPanel.add(whatsappInfo, BorderLayout.EAST);

		facebookScrollPane = new JScrollPane(facebookTable);
		facebookSearch = new JTextField("Search");
		facebookSearch.addFocusListener(this);
		facebookSearch.addKeyListener(this);
		facebookPanel.add(facebookSearch, BorderLayout.NORTH);
		facebookPanel.add(facebookScrollPane, BorderLayout.CENTER);
		
		webviewScrollPane = new JScrollPane(webviewTable);
		webviewSearch = new JTextField("Search");
		webviewSearch.addFocusListener(this);
		webviewSearch.addKeyListener(this);
		webviewCombo = new JComboBox(new Object[] { "Passwords", "Formdata" });
		webviewCombo.addActionListener(this);
		JPanel webviewNorthPanel = new JPanel(new GridLayout(1, 2));
		webviewNorthPanel.add(webviewCombo);
		webviewNorthPanel.add(webviewSearch);
		webviewPanel.add(webviewNorthPanel, BorderLayout.NORTH);
		webviewPanel.add(webviewScrollPane, BorderLayout.CENTER);

		contextMenu = new JPopupMenu();
		copyCell = new JMenuItem("Copy cell to clipboard");
		copyCell.addActionListener(this);
		copyRow = new JMenuItem("Copy row to clipboard");
		copyRow.addActionListener(this);

		contextMenu.add(copyCell);
		contextMenu.add(copyRow);

		openSQLButton = new JButton("Open SQL DBs");
		openSQLButton.addActionListener(this);
		preferencesPanel.add(openSQLButton);
	}

	public void addTab(String sourceFile) {
		if (sourceFile.equals(SQLReaderController.BROWSER_FILENAME)) {
			tabVector.add(browserHPanel);
			tabVector.add(browserBPanel);
			tabVector.add(browserSPanel);
		} else if (sourceFile.equals(SQLReaderController.CALENDAR_FILENAME)) {
			tabVector.add(calendarPanel);
		} else if (sourceFile.equals(SQLReaderController.CONTACTS_FILENAME)) {
			tabVector.add(contactPanel);
			tabVector.add(callPanel);
			contactTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(contactTable);
			tca.adjustColumns();
		} else if (sourceFile.equals(SQLReaderController.MMSSMS_FILENAME)) {
			tabVector.add(mmsPanel);
			tabVector.add(smsPanel);
			mmsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableColumnAdjuster tca = new TableColumnAdjuster(mmsTable);
			tca.adjustColumns();
		} else if (sourceFile.equals(SQLReaderController.WHATSAPP_FILENAME)) {
			tabVector.add(whatsappPanel);
		} else if (sourceFile.equals(SQLReaderController.FACEBOOK_FILENAME)) {
			tabVector.add(facebookPanel);
		} else if (sourceFile.equals(SQLReaderController.WEBVIEW_FILENAME)) {
			tabVector.add(webviewPanel);
		}
	}

	// shows tabs in alphabetic order
	public void showTabs() {
		if (tabVector.contains(smsPanel) && tabVector.contains(contactPanel)) {
			controller.addNamesToSMS();
		}
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
	public String getName() {
		return "SQLReader";
	}

	@Override
	public JComponent getView() {
		return tabs;
	}

	@Override
	public void triggered() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setADBThread(ADBThread adb) {
		this.adb = adb;
	}

	@Override
	public void setCaseFolder(File caseFolder) {
		this.caseFolder = caseFolder;
	}

	@Override
	public void reactToADBResult(String result, String executedCommand) {
		// TODO Auto-generated method stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(openSQLButton)) {
			int returnVal = fc.showOpenDialog(tabs);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File folder = fc.getSelectedFile();

				if (controller.iterateChosenFolder(folder)) {
					fc.setCurrentDirectory(folder);
				} else {
					JOptionPane.showMessageDialog(tabs, "No db files found in this folder", "Error", JOptionPane.ERROR_MESSAGE);
				}
				calendarScrollPane.validate();
			}
		} else if (e.getSource().equals(copyCell)) {
			controller.copySelectionToClipboard(currentX, currentY, currentTable, true);
		} else if (e.getSource().equals(copyRow)) {
			controller.copySelectionToClipboard(currentX, currentY, currentTable, false);
		} else if (e.getSource().equals(whatsAppCombo)) {
			controller.setWhatsappTableModel(whatsAppCombo.getSelectedItem().toString());
		} else if(e.getSource().equals(webviewCombo)) {
			controller.setWebviewTableModel(webviewCombo.getSelectedIndex());
		}

	}

	// update info-panels if selected row changed
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedRow;
		if (e.getSource().equals(smsTable.getSelectionModel())) {
			selectedRow = smsTable.getSelectedRow();
			if (selectedRow != -1) {
				smsTextArea.setText("" + smsTable.getValueAt(selectedRow, 3));
			}
		} else if (e.getSource().equals(mmsTable.getSelectionModel())) {
			selectedRow = mmsTable.getSelectedRow();
			if (selectedRow != -1) {
				String text = mmsTable.getValueAt(selectedRow, 3).toString();
				String filename = mmsTable.getValueAt(selectedRow, 4).toString();
				String mimetype = mmsTable.getValueAt(selectedRow, 5).toString();
				mmsInfo.setInfo(text, mimetype, caseFolder, filename);
			}
		} else if (e.getSource().equals(contactTable.getSelectionModel())) {
			selectedRow = contactTable.getSelectedRow();
			if (selectedRow != -1) {
				String id = contactTable.getValueAt(selectedRow, 0).toString();
				String name = contactTable.getValueAt(selectedRow, 1).toString();
				String numbers = contactTable.getValueAt(selectedRow, 2).toString();
				String organisation = contactTable.getValueAt(selectedRow, 5).toString();
				String emails = contactTable.getValueAt(selectedRow, 6).toString();
				String addresses = contactTable.getValueAt(selectedRow, 7).toString();
				String websites = contactTable.getValueAt(selectedRow, 8).toString();
				String im = contactTable.getValueAt(selectedRow, 9).toString();
				String skype = contactTable.getValueAt(selectedRow, 10).toString();
				String notes = contactTable.getValueAt(selectedRow, 11).toString();
				// TODO:hier casefolder rein:
				File f = new File(caseFolder + File.separator + "contact_photos" + File.separator + id + ".png");
				if (f.isFile()) {
					contactInfo.setInfo(name, numbers, organisation, emails, addresses, websites, im, skype, notes, f);
				} else {
					contactInfo.setInfo(name, numbers, organisation, emails, addresses, websites, im, skype, notes);
				}
			}
		} else if (e.getSource().equals(whatsappTable.getSelectionModel())) {
			selectedRow = whatsappTable.getSelectedRow();
			if (selectedRow != -1) {
				String text = (whatsappTable.getValueAt(selectedRow, 0).equals("")) ? whatsappTable.getValueAt(selectedRow, 1).toString()
						: whatsappTable.getValueAt(selectedRow, 0).toString();
				String filename = whatsappTable.getValueAt(selectedRow, 6).toString();
				whatsappInfo.setInfo(text, caseFolder, filename);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			// if (e.getSource().equals(browserHTable)) {
			// openContextMenu(e, browserHTable);
			// } else if (e.getSource().equals(browserBTable)) {
			// openContextMenu(e, browserBTable);
			// } else if (e.getSource().equals(browserSTable)) {
			// openContextMenu(e, browserSTable);
			// } else if (e.getSource().equals(calendarTable)) {
			// openContextMenu(e, calendarTable);
			// } else if (e.getSource().equals(callTable)) {
			// openContextMenu(e, callTable);
			// } else if (e.getSource().equals(contactTable)) {
			// openContextMenu(e, contactTable);
			// } else if (e.getSource().equals(mmsTable)) {
			// openContextMenu(e, mmsTable);
			// } else if (e.getSource().equals(smsTable)) {
			// openContextMenu(e, smsTable);
			// } else if (e.getSource().equals(whatsappTable)) {
			// openContextMenu(e, whatsappTable);
			// }
			openContextMenu(e, (JTable) e.getSource());
		}
	}

	private void openContextMenu(MouseEvent e, JTable table) {

		// selects the row which was right-clicked
		Point p = e.getPoint();
		int rowNumber = table.rowAtPoint(p);
		table.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);

		currentX = e.getX();
		currentY = e.getY();
		currentTable = table;

		contextMenu.show(table, currentX, currentY);

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		String search = "";
		LiveSearchTableModel model = null;
		if (e.getSource().equals(contactSearch)) {
			search = contactSearch.getText();
			model = (LiveSearchTableModel) contactTable.getModel();
		} else if (e.getSource().equals(browserBSearch)) {
			search = browserBSearch.getText();
			model = (LiveSearchTableModel) browserBTable.getModel();
		} else if (e.getSource().equals(browserHSearch)) {
			search = browserHSearch.getText();
			model = (LiveSearchTableModel) browserHTable.getModel();
		} else if (e.getSource().equals(browserSSearch)) {
			search = browserSSearch.getText();
			model = (LiveSearchTableModel) browserSTable.getModel();
		} else if (e.getSource().equals(callsSearch)) {
			search = callsSearch.getText();
			model = (LiveSearchTableModel) callTable.getModel();
		} else if (e.getSource().equals(calendarSearch)) {
			search = calendarSearch.getText();
			model = (LiveSearchTableModel) calendarTable.getModel();
		} else if (e.getSource().equals(smsSearch)) {
			search = smsSearch.getText();
			model = (LiveSearchTableModel) smsTable.getModel();
		} else if (e.getSource().equals(mmsSearch)) {
			search = mmsSearch.getText();
			model = (LiveSearchTableModel) mmsTable.getModel();
		} else if (e.getSource().equals(whatsappSearch)) {
			search = whatsappSearch.getText();
			model = (LiveSearchTableModel) whatsappTable.getModel();
		} else if (e.getSource().equals(facebookSearch)) {
			search = facebookSearch.getText();
			model = (LiveSearchTableModel) facebookTable.getModel();
		} else if (e.getSource().equals(webviewSearch)) {
			search = webviewSearch.getText();
			model = (LiveSearchTableModel) webviewTable.getModel();
		}
		if (!search.equals("")) {
			model.filterData(search);
		} else {
			model.resetData();
		}
		browserHScrollPane.getVerticalScrollBar().setValue(0);
		browserBScrollPane.getVerticalScrollBar().setValue(0);
		browserSScrollPane.getVerticalScrollBar().setValue(0);
		callScrollPane.getVerticalScrollBar().setValue(0);
		calendarScrollPane.getVerticalScrollBar().setValue(0);
		contactScrollPane.getVerticalScrollBar().setValue(0);
		mmsScrollPane.getVerticalScrollBar().setValue(0);
		smsScrollPane.getVerticalScrollBar().setValue(0);
		whatsappScrollPane.getVerticalScrollBar().setValue(0);
		facebookScrollPane.getVerticalScrollBar().setValue(0);
		webviewScrollPane.getVerticalScrollBar().setValue(0);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusGained(FocusEvent e) {
		// if (e.getSource().equals(contactSearch)) {
		// contactSearch.setText("");
		// } else if (e.getSource().equals(browserHSearch)) {
		// browserHSearch.setText("");
		// } else if (e.getSource().equals(browserBSearch)) {
		// browserBSearch.setText("");
		// } else if (e.getSource().equals(browserSSearch)) {
		// browserSSearch.setText("");
		// } else if (e.getSource().equals(calendarSearch)) {
		// calendarSearch.setText("");
		// } else if (e.getSource().equals(callsSearch)) {
		// callsSearch.setText("");
		// } else if (e.getSource().equals(mmsSearch)) {
		// mmsSearch.setText("");
		// } else if (e.getSource().equals(smsSearch)) {
		// smsSearch.setText("");
		// } else if (e.getSource().equals(whatsappSearch)) {
		// whatsappSearch.setText("");
		// }
		((JTextField) e.getSource()).setText("");
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
	}

	public JTable getCalendarTable() {
		return calendarTable;
	}

	public JTable getCallTable() {
		return callTable;
	}

	public JTable getBrowserHTable() {
		return browserHTable;
	}

	public JTable getBrowserBTable() {
		return browserBTable;
	}

	public JTable getBrowserSTable() {
		return browserSTable;
	}

	public JTable getContactTable() {
		return contactTable;
	}

	public JTable getMmsTable() {
		return mmsTable;
	}

	public JTable getSmsTable() {
		return smsTable;
	}

	public JTable getWhatsappTable() {
		return whatsappTable;
	}

	public File getCaseFolder() {
		return caseFolder;
	}

	public ADBThread getAdb() {
		return adb;
	}

	public JComboBox getWhatsappCombo() {
		return whatsAppCombo;
	}
	
	public JTable getWebviewTable() {
		return webviewTable;
	}

}
