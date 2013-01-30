package de.uni_hannover.osaft.plugins.sqlreader.controller;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.swing.JTable;

import de.uni_hannover.osaft.plugins.connnectorappdata.tables.LiveSearchTableModel;
import de.uni_hannover.osaft.plugins.sqlreader.view.SQLReaderView;

public class SQLReaderController {
	
	//TODO: ich raffe nicht, warum das date render ding nicht funzt in den tabellen

	public static String WEBVIEW_FILENAME = "webview.db";
	public static String BROWSER_FILENAME = "browser2.db";
	public static String COOKIES_FILENAME = "webviewCookiesChromium.db";
	public static String BROWSER_CACHED_GEOLOCATION = "CachedGeoposition.db";
	public static String MMSSMS_FILENAME = "mmssms.db";
	public static String CALENDAR_FILENAME = "calendar.db";
	public static String CONTACTS_FILENAME = "contacts.db";
	public static String MAPS_SEARCH_HISTORY_FILENAME = "search_history.db";
	public static String MAPS_DESTINATION_HISTORY_FILENAME = "da_destination_history.db";
	// TODO: wie macht man das, dass da der useraccount drinsteht
	public static String GMAIL_FILENAME = "mailstore...db";
	public static String FACEBOOK_FILENAME = "fb.db";
	public static String WHATSAPP_FILENAME = "msgstore.db";
	// TODO: wie macht man das, dass da der useraccount drinsteht
	public static String TWITTER_FILENAME = "<userid>.db";

	private SQLReaderView view;
	private LiveSearchTableModel calendarTableModel, callsTableModel, browserHistoryTableModel,
			browserBookmarksTableModel, browserSearchTableModel, contactsTableModel, mmsTableModel,
			smsTableModel;

	public SQLReaderController(SQLReaderView view) {
		this.view = view;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendarTableModel = new LiveSearchTableModel(new Object[] { "Calendar", "Title",
				"Description", "Start", "End", "Duration", "Location", "Repeat", "Allday",
				"Deleted?" });
		callsTableModel = new LiveSearchTableModel(new Object[] { "Name", "Number", "Date",
				"Duration (in s)", "New Call", "Type", "Number Type" });
		browserHistoryTableModel = new LiveSearchTableModel(new Object[] { "Title", "URL",
				"Visits", "Last Visit" });
		browserBookmarksTableModel = new LiveSearchTableModel(new Object[] { "Title", "URL",
				"Created", "Deleted?" });
		browserSearchTableModel = new LiveSearchTableModel(new Object[] { "Date", "Search" });
		contactsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Name", "Numbers",
				"Organisation", "Email", "Address", "Website", "IM", "Skype", "Notes" });
		smsTableModel = new LiveSearchTableModel(new Object[] { "Number", "Name", "Date", "Text",
				"Read", "Seen", "Status" });
		mmsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Number", "Date", "Text",
				"Read", "Attachment" });
	}

	public boolean iterateChosenFolder(File folder) {
		boolean processedSomething = false;
		// progressbar?
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			String fName = curFile.getName();
			// only given csv files should be processed
			if (fName.equals(BROWSER_FILENAME) || fName.equals(WEBVIEW_FILENAME)
					|| fName.equals(CALENDAR_FILENAME) || fName.equals(CONTACTS_FILENAME)
					|| fName.equals(COOKIES_FILENAME) || fName.equals(BROWSER_CACHED_GEOLOCATION)
					|| fName.equals(MMSSMS_FILENAME) || fName.equals(MAPS_SEARCH_HISTORY_FILENAME)
					|| fName.equals(MAPS_DESTINATION_HISTORY_FILENAME)
					|| fName.equals(GMAIL_FILENAME) || fName.equals(FACEBOOK_FILENAME)
					|| fName.equals(WHATSAPP_FILENAME) || fName.equals(TWITTER_FILENAME)) {
				processDB(curFile);
				processedSomething = true;
			}
		}
		view.showTabs();
		return processedSomething;
	}

	private void processDB(File file) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:sqlite:"
					+ file.getAbsolutePath());
			if (file.getName().equals(BROWSER_FILENAME)) {
				processBrowser(connection);
			} else if (file.getName().equals(CALENDAR_FILENAME)) {
				processCalendar(connection);
			}

		} catch (SQLException e) {
			// TODO
			e.printStackTrace();
		}
	}

	private void processBrowser(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement
				.executeQuery("SELECT title, url, folder, deleted, created FROM bookmarks");
		while (rs.next()) {
			// check, if bookmark is folder:
			if (rs.getInt(3) != 1) {
				String title = rs.getString(1);
				String url = rs.getString(2);
				Date created = new Date(rs.getLong(5));
				boolean deleted = Boolean.parseBoolean(rs.getString(4));

				browserBookmarksTableModel.addRow(new Object[] { title, url, created, deleted });
			}
		}
		rs = statement.executeQuery("SELECT title, url, date, visits FROM history");
		while (rs.next()) {
			String title = rs.getString(1);
			String url = rs.getString(2);
			Date lastVisit = new Date(rs.getLong(3));
			int visits = rs.getInt(4);

			browserHistoryTableModel.addRow(new Object[] { title, url, visits, lastVisit });
		}
		rs = statement.executeQuery("SELECT search, date FROM searches");
		while (rs.next()) {
			String search = rs.getString(1);
			Date date = new Date(rs.getLong(2));
			browserSearchTableModel.addRow(new Object[] { date, search });
		}
		view.getBrowserHTable().setModel(browserHistoryTableModel);
		view.getBrowserBTable().setModel(browserBookmarksTableModel);
		view.getBrowserSTable().setModel(browserSearchTableModel);
		view.addTab(BROWSER_FILENAME);
	}

	private void processCalendar(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet rs = statement
				.executeQuery("SELECT calendar_displayName, title, description, dtstart, dtend, eventlocation, rrule, allDay, Events.deleted, duration FROM Events JOIN Calendars ON Events.calendar_id=Calendars._id ");
		while (rs.next()) {
			String calName = rs.getString(1);
			String title = rs.getString(2);
			String desc = (rs.getString(3) == null) ? "" : rs.getString(3);
			Date start = new Date(rs.getLong(4));
			String location = rs.getString(6);
			String repRule = (rs.getString(7) == null) ? "" : rs.getString(7);
			boolean allDay = rs.getInt(8)==1;
			boolean deleted = rs.getInt(9)==1;
			String duration = (rs.getString(10) == null) ? "" : rs.getString(10).substring(1);
			Date end = (rs.getString(5) == null)? null : new Date(rs.getLong(5));
			calendarTableModel.addRow(new Object[] { calName, title, desc, start, end, duration,
					location, repRule, allDay, deleted });
		}
		view.getCalendarTable().setModel(calendarTableModel);
		view.addTab(CALENDAR_FILENAME);
	}

	public void copySelectionToClipboard(int currentX, int currentY, JTable currentTable,
			boolean copyCell) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// find out which row or which cell is selected (variables
		// current... have been set in mouseClicked())
		Point p = new Point(currentX, currentY);
		int rowNumber = currentTable.rowAtPoint(p);
		int columnNumber = currentTable.columnAtPoint(p);
		String dataToClipboard = "";

		if (copyCell) {
			// just get the current cell
			dataToClipboard = currentTable.getModel().getValueAt(rowNumber, columnNumber)
					.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			// iterate over all columns of selected row
			for (int i = 0; i < currentTable.getColumnCount(); i++) {
				sb.append(currentTable.getColumnName(i));
				sb.append(": ");
				sb.append(currentTable.getModel().getValueAt(rowNumber, i));
				sb.append(", ");
			}
			dataToClipboard = sb.substring(0, sb.length() - 2).toString();
		}
		// save string to clipboard
		StringSelection strSel = new StringSelection(dataToClipboard);
		clipboard.setContents(strSel, null);
	}

}
