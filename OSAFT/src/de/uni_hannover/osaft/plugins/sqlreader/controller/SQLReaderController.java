package de.uni_hannover.osaft.plugins.sqlreader.controller;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JTable;

import de.uni_hannover.osaft.plugins.connnectorappdata.tables.LiveSearchTableModel;
import de.uni_hannover.osaft.plugins.sqlreader.view.SQLReaderView;

public class SQLReaderController {

	// TODO: ich raffe nicht, warum das date render ding nicht funzt in den
	// tabellen

	public final static String WEBVIEW_FILENAME = "webview.db";
	// könnte auch anders heißen (auf huawei heißt es browser.db und hat anderen
	// inhalt):
	public final static String BROWSER_FILENAME = "browser2.db";
	public final static String COOKIES_FILENAME = "webviewCookiesChromium.db";
	public final static String BROWSER_CACHED_GEOLOCATION = "CachedGeoposition.db";
	public final static String MMSSMS_FILENAME = "mmssms.db";
	public final static String CALENDAR_FILENAME = "calendar.db";
	public final static String CONTACTS_FILENAME = "contacts2.db";
	public final static String MAPS_SEARCH_HISTORY_FILENAME = "search_history.db";
	public final static String MAPS_DESTINATION_HISTORY_FILENAME = "da_destination_history.db";
	// TODO: wie macht man das, dass da der useraccount drinsteht
	public final static String GMAIL_FILENAME = "mailstore...db";
	public final static String FACEBOOK_FILENAME = "fb.db";
	public final static String WHATSAPP_FILENAME = "msgstore.db";
	// TODO: wie macht man das, dass da der useraccount drinsteht
	public final static String TWITTER_FILENAME = "<userid>.db";

	public final static int MIMETYPE_NAME = 7;
	public final static int MIMETYPE_NUMBER = 5;
	public final static int MIMETYPE_EMAIL = 1;
	public final static int MIMETYPE_IM = 2;
	public final static int MIMETYPE_SKYPE = 15;
	public final static int MIMETYPE_ADDRESS = 8;
	public final static int MIMETYPE_WEBSITE = 18;
	public final static int MIMETYPE_NOTES = 12;
	public final static int MIMETYPE_ORGANISATION = 4;
	public final static int MIMETYPE_PHOTO = 10;

	private SQLReaderView view;
	private LiveSearchTableModel calendarTableModel, callsTableModel, browserHistoryTableModel, browserBookmarksTableModel,
			browserSearchTableModel, contactsTableModel, mmsTableModel, smsTableModel;

	public SQLReaderController(SQLReaderView view) {
		this.view = view;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendarTableModel = new LiveSearchTableModel(new Object[] { "Calendar", "Title", "Description", "Start", "End", "Duration",
				"Location", "Repeat", "Allday", "Deleted?" });
		callsTableModel = new LiveSearchTableModel(new Object[] { "Name", "Number", "Date", "Duration (in s)", "New Call", "Type",
				"Number Type" });
		browserHistoryTableModel = new LiveSearchTableModel(new Object[] { "Title", "URL", "Visits", "Last Visit" });
		browserBookmarksTableModel = new LiveSearchTableModel(new Object[] { "Title", "URL", "Created", "Deleted?" });
		browserSearchTableModel = new LiveSearchTableModel(new Object[] { "Date", "Search" });
		contactsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Name", "Numbers", "Times Contacted", "Last Time Contacted",
				"Organisation", "Email", "Address", "Website", "IM", "Skype", "Notes", "Starred?", "Deleted?" });
		smsTableModel = new LiveSearchTableModel(new Object[] { "Number", "Name", "Date", "Text", "Read", "Seen", "Status" });
		mmsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Number", "Date", "Text", "Read", "Attachment" });
	}

	public boolean iterateChosenFolder(File folder) {
		boolean processedSomething = false;
		// progressbar?
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			String fName = curFile.getName();
			// only given csv files should be processed
			if (fName.equals(BROWSER_FILENAME) || fName.equals(WEBVIEW_FILENAME) || fName.equals(CALENDAR_FILENAME)
					|| fName.equals(CONTACTS_FILENAME) || fName.equals(COOKIES_FILENAME) || fName.equals(BROWSER_CACHED_GEOLOCATION)
					|| fName.equals(MMSSMS_FILENAME) || fName.equals(MAPS_SEARCH_HISTORY_FILENAME)
					|| fName.equals(MAPS_DESTINATION_HISTORY_FILENAME) || fName.equals(GMAIL_FILENAME) || fName.equals(FACEBOOK_FILENAME)
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
			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
			Statement statement = connection.createStatement();
			if (file.getName().equals(BROWSER_FILENAME)) {
				processBrowser(statement);
			} else if (file.getName().equals(CALENDAR_FILENAME)) {
				processCalendar(statement);
			} else if (file.getName().equals(CONTACTS_FILENAME)) {
				processContacts(statement);
			} else if (file.getName().equals(MMSSMS_FILENAME)) {
				processSMSMMS(statement);
			} else if (file.getName().equals(FACEBOOK_FILENAME)) {
				processFacebook(statement);
			} else if (file.getName().equals(WHATSAPP_FILENAME)) {
				processWhatsapp(statement);
			}

		} catch (SQLException e) {
			// TODO
			e.printStackTrace();
		}
	}

	private void processBrowser(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT title, url, folder, deleted, created FROM bookmarks");
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

	private void processCalendar(Statement statement) throws SQLException {
		ResultSet rs = statement
				.executeQuery("SELECT calendar_displayName, title, description, dtstart, dtend, eventlocation, rrule, allDay, Events.deleted, duration FROM Events JOIN Calendars ON Events.calendar_id=Calendars._id ");
		while (rs.next()) {
			String calName = rs.getString(1);
			String title = rs.getString(2);
			String desc = (rs.getString(3) == null) ? "" : rs.getString(3);
			Date start = new Date(rs.getLong(4));
			String location = rs.getString(6);
			String repRule = (rs.getString(7) == null) ? "" : rs.getString(7);
			boolean allDay = rs.getInt(8) == 1;
			boolean deleted = rs.getInt(9) == 1;
			String duration = (rs.getString(10) == null) ? "" : rs.getString(10).substring(1);
			Date end = (rs.getString(5) == null) ? null : new Date(rs.getLong(5));
			calendarTableModel.addRow(new Object[] { calName, title, desc, start, end, duration, location, repRule, allDay, deleted });
		}
		view.getCalendarTable().setModel(calendarTableModel);
		view.addTab(CALENDAR_FILENAME);
	}

	private void processContacts(Statement statement) throws SQLException {
		ArrayList<Integer> contactIDs = new ArrayList<Integer>();
		ResultSet rs = statement.executeQuery("SELECT _id FROM contacts");

		while (rs.next()) {
			contactIDs.add(rs.getInt(1));
		}

		for (int i = 0; i < contactIDs.size(); i++) {
			int id = contactIDs.get(i);
			String query = "SELECT raw_contacts._id, contact_id, mimetype_id, data1, data4, data5, data6, data15, last_time_contacted, times_contacted, starred, deleted  FROM raw_contacts JOIN data ON data.raw_contact_id=raw_contacts._id WHERE contact_id="
					+ id + " ORDER BY raw_contacts._id ASC";
			rs = statement.executeQuery(query);

			String name = "";
			String numbers = "";
			String organisation = "";
			String emails = "";
			String address = "";
			String website = "";
			String IM = "";
			String skype = "";
			String notes = "";
			Date lastTimeContacted = (rs.getString(9) == null) ? null : new Date(rs.getLong(9));
			boolean starred = rs.getInt(11) == 1;
			boolean deleted = rs.getInt(12) == 1;
			int timesContacted = 0;

			while (rs.next()) {
				switch (rs.getInt(3)) {
				case MIMETYPE_NAME:
					name = (rs.getString(4) == null) ? "" : rs.getString(4);
					break;
				case MIMETYPE_EMAIL:
					emails += rs.getString(4) + ", ";
					break;
				case MIMETYPE_NUMBER:
					numbers += ((rs.getString(5) == null) ? rs.getString(4) : rs.getString(5)) + ", ";
					break;
				case MIMETYPE_IM:
					if (rs.getInt(6) == -1) {
						IM += rs.getString(7) + ": " + rs.getString(4) + ", ";
					} else {
						IM += getIMType(rs.getInt(6)) + ": " + rs.getString(4) + ", ";
					}
					break;
				case MIMETYPE_SKYPE:
					if (rs.getString(7).contains("skype")) {
						skype = rs.getString(4);
					}
					break;
				case MIMETYPE_ADDRESS:
					address += rs.getString(4) + ", ";
					break;
				case MIMETYPE_NOTES:
					notes = rs.getString(4);
					break;
				case MIMETYPE_WEBSITE:
					website += rs.getString(4) + ", ";
					break;
				case MIMETYPE_ORGANISATION:
					organisation += rs.getString(4);
					break;
				case MIMETYPE_PHOTO:
					byte[] photoByteArray = rs.getBytes(8);
					if (photoByteArray != null) {
						try {
							InputStream in = new ByteArrayInputStream(photoByteArray);
							BufferedImage photo = ImageIO.read(in);
							ImageIO.write(photo, "png", new File("/home/jannis/" + id + ".png"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					break;
				default:
					break;
				}
			}
			emails = (emails.length() > 0) ? emails.substring(0, emails.length() - 2) : "";
			IM = (IM.length() > 0) ? IM.substring(0, IM.length() - 2) : "";
			numbers = (numbers.length() > 0) ? numbers.substring(0, numbers.length() - 2) : "";
			website = (website.length() > 0) ? website.substring(0, website.length() - 2) : "";
			address = (address.length() > 0) ? address.substring(0, address.length() - 2) : "";

			contactsTableModel.addRow(new Object[] { id, name, numbers, timesContacted, lastTimeContacted, organisation, emails, address,
					website, IM, skype, notes, starred, deleted });
		}

		view.getContactTable().setModel(contactsTableModel);
		view.addTab(CONTACTS_FILENAME);
	}

	// see:
	// https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Im.html#PROTOCOL
	private String getIMType(int i) {
		switch (i) {
		case 1:
			return "MSN";
		case 2:
			return "YAHOO";
		case 3:
			return "Skype";
		case 4:
			return "QQ";
		case 5:
			return "GTalk";
		case 6:
			return "ICQ";
		case 7:
			return "Jabber";
		case 8:
			return "Netmeeting";

		default:
			break;
		}
		return "";
	}

	private void processSMSMMS(Statement statement) throws SQLException {
		// MMS UND SMS
	}

	private void processFacebook(Statement statement) throws SQLException {
		// FATZBUK
	}

	private void processWhatsapp(Statement statement) throws SQLException {
		// WHATSAPP
	}

	public void copySelectionToClipboard(int currentX, int currentY, JTable currentTable, boolean copyCell) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// find out which row or which cell is selected (variables
		// current... have been set in mouseClicked())
		Point p = new Point(currentX, currentY);
		int rowNumber = currentTable.rowAtPoint(p);
		int columnNumber = currentTable.columnAtPoint(p);
		String dataToClipboard = "";

		if (copyCell) {
			// just get the current cell
			dataToClipboard = currentTable.getModel().getValueAt(rowNumber, columnNumber).toString();
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
		StringSelection strSel = new StringSelection(dataToClipboard.replace("null", ""));
		clipboard.setContents(strSel, null);
	}

}
