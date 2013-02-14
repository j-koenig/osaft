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
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JTable;

import de.uni_hannover.osaft.plugins.connnectorappdata.tables.LiveSearchTableModel;
import de.uni_hannover.osaft.plugins.sqlreader.view.SQLReaderView;

public class SQLReaderController {

	// TODO: ich raffe nicht, warum das date render ding nicht funzt in den
	// tabellen

	// Passwörter und cached formdata:
	public final static String WEBVIEW_FILENAME = "webview.db"; // Done
	// könnte auch anders heißen (auf huawei heißt es browser.db und hat anderen
	// inhalt):
	public final static String BROWSER_FILENAME = "browser2.db"; // Done
	// relevant?
	public final static String COOKIES_FILENAME = "webviewCookiesChromium.db";
	public final static String BROWSER_CACHED_GEOPOSITION = "CachedGeoposition.db"; // Done
	public final static String MMSSMS_FILENAME = "mmssms.db"; // Done
	public final static String CALENDAR_FILENAME = "calendar.db"; // Done
	public final static String CONTACTS_FILENAME = "contacts2.db"; // Done
	public final static String MAPS_SEARCH_HISTORY_FILENAME = "search_history.db"; // Done
	public final static String MAPS_DESTINATION_HISTORY_FILENAME = "da_destination_history"; // Done
	// TODO: wie macht man das, dass da der useraccount drinsteht
	public final static String GMAIL_FILENAME = "mailstore...db";
	public final static String FACEBOOK_FILENAME = "fb.db";
	public final static String WHATSAPP_FILENAME = "msgstore.db"; // Done
	// TODO: wie macht man das, dass da der useraccount drinsteht
	public final static String TWITTER_FILENAME = "<userid>.db";
	public final static String GTALK_FILENAME = "talk.db";

	// found these by examining the database with a db-browser:
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
	private LiveSearchTableModel calendarTableModel, callsTableModel, contactsTableModel, mmsTableModel, smsTableModel;
	private HashMap<String, LiveSearchTableModel> whatsAppTableModels;
	private ArrayList<LiveSearchTableModel> browserTableModels, mapsTableModels;
	private File curFolder;
	private HashMap<Integer, String> contactNames;

	public SQLReaderController(SQLReaderView view) {
		this.view = view;
		contactNames = new HashMap<Integer, String>();
		whatsAppTableModels = new HashMap<String, LiveSearchTableModel>();
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendarTableModel = new LiveSearchTableModel(new Object[] { "Calendar", "Title", "Description", "Start", "End", "Duration",
				"Location", "Repeat", "Allday", "Deleted?" });
		callsTableModel = new LiveSearchTableModel(new Object[] { "Name", "Number", "Date", "Duration (in s)", "New Call", "Type",
				"Number Type", "Location" });
		contactsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Name", "Numbers", "Times Contacted", "Last Time Contacted",
				"Organisation", "Email", "Address", "Website", "IM", "Skype", "Notes", "Starred?", "Deleted?" });
		smsTableModel = new LiveSearchTableModel(new Object[] { "Number", "Person", "Date", "Text", "Read", "Seen", "Status" });
		mmsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Number", "Date", "Text", "Attachment", "Mimetype" });
		mapsTableModels = new ArrayList<LiveSearchTableModel>();
		// maps searches:
		mapsTableModels.add(new LiveSearchTableModel(new Object[] { "Search", "Suggestion", "Latitude", "Longitude", "Date" }));
		// destination history:
		mapsTableModels.add(new LiveSearchTableModel(new Object[] { "Date", "Destination Lat", "Destination Lon", "Destination Title",
				"Destination Address", "Source Lat", "Source Lon" }));
		browserTableModels = new ArrayList<LiveSearchTableModel>();
		// browser bookmarks
		browserTableModels.add(new LiveSearchTableModel(new Object[] { "Title", "URL", "Created", "Deleted?" }));
		// browser cached geopositions:
		browserTableModels.add(new LiveSearchTableModel(new Object[] { "Latitude", "Longitude", "Altitude", "Accuracy",
				"Altitude Accuracy", "Heading", "Speed", "Timestamp" }));
		// browser history:
		browserTableModels.add(new LiveSearchTableModel(new Object[] { "Title", "URL", "Visits", "Last Visit" }));
		// browser search history
		browserTableModels.add(new LiveSearchTableModel(new Object[] { "Date", "Search" }));
		// browser cached formdata
		browserTableModels.add(new LiveSearchTableModel(new Object[] { "Name", "Value" }));
		// browser saved passwords
		browserTableModels.add(new LiveSearchTableModel(new Object[] { "Host", "Username", "Password" }));
	}

	public boolean iterateChosenFolder(File folder) {
		curFolder = folder;
		boolean processedSomething = false;
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			String fName = curFile.getName();
			// only given db files should be processed
			if (fName.equals(BROWSER_FILENAME) || fName.equals(WEBVIEW_FILENAME) || fName.equals(CALENDAR_FILENAME)
					|| fName.equals(CONTACTS_FILENAME) || fName.equals(COOKIES_FILENAME) || fName.equals(BROWSER_CACHED_GEOPOSITION)
					|| fName.equals(MMSSMS_FILENAME) || fName.equals(MAPS_SEARCH_HISTORY_FILENAME)
					|| fName.equals(MAPS_DESTINATION_HISTORY_FILENAME) || fName.equals(GMAIL_FILENAME) || fName.equals(FACEBOOK_FILENAME)
					|| fName.equals(WHATSAPP_FILENAME) || fName.equals(TWITTER_FILENAME) || fName.equals(GTALK_FILENAME)) {
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
				processBrowser2(statement);
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
			} else if (file.getName().equals(WEBVIEW_FILENAME)) {
				processWebview(statement);
			} else if (file.getName().equals(BROWSER_CACHED_GEOPOSITION)) {
				processCachedGeoposition(statement);
			} else if (file.getName().equals(MAPS_SEARCH_HISTORY_FILENAME)) {
				processMapsSearchHistory(statement);
			} else if (file.getName().equals(MAPS_DESTINATION_HISTORY_FILENAME)) {
				processMapsDestinationHistory(statement);
			}

		} catch (SQLException e) {
			// TODO
			e.printStackTrace();
		}
	}

	private void processBrowser2(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT title, url, folder, deleted, created FROM bookmarks");
		while (rs.next()) {
			// check, if bookmark is folder:
			if (rs.getInt(3) != 1) {
				String title = rs.getString(1);
				String url = rs.getString(2);
				Date created = new Date(rs.getLong(5));
				boolean deleted = Boolean.parseBoolean(rs.getString(4));
				browserTableModels.get(0).addRow(new Object[] { title, url, created, deleted });
			}
		}
		rs = statement.executeQuery("SELECT title, url, date, visits FROM history");
		while (rs.next()) {
			String title = rs.getString(1);
			String url = rs.getString(2);
			Date lastVisit = new Date(rs.getLong(3));
			int visits = rs.getInt(4);
			browserTableModels.get(2).addRow(new Object[] { title, url, visits, lastVisit });
		}
		rs = statement.executeQuery("SELECT search, date FROM searches");
		while (rs.next()) {
			String search = rs.getString(1);
			Date date = new Date(rs.getLong(2));
			browserTableModels.get(3).addRow(new Object[] { date, search });
		}
		view.getBrowserTable().setModel(browserTableModels.get(0));
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
		// FILL HASHMAP FOR LATER USAGE IN addNamesToSMS():
		ResultSet rs = statement.executeQuery("SELECT _id, display_name FROM raw_contacts");
		while (rs.next()) {
			contactNames.put(rs.getInt(1), rs.getString(2));
		}

		// CONTACTS:
		ArrayList<Integer> contactIDs = new ArrayList<Integer>();
		rs = statement.executeQuery("SELECT _id FROM contacts");

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
							// TODO: ordner erstellen, wenn nich da
							InputStream in = new ByteArrayInputStream(photoByteArray);
							BufferedImage photo = ImageIO.read(in);
							ImageIO.write(photo, "png", new File(view.getCaseFolder() + File.separator + "contact_photos" + File.separator
									+ id + ".png"));
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
		// CALLS:
		rs = statement.executeQuery("SELECT name, number, date, duration, new, type, numbertype, geocoded_location FROM calls");

		while (rs.next()) {
			String name = (rs.getString(1) == null) ? "unknown" : rs.getString(1);
			String number = (rs.getString(2).equals("-2")) ? "private number" : rs.getString(2);
			Date date = new Date(rs.getLong(3));
			int duration = rs.getInt(4);
			boolean newCall = rs.getInt(5) == 1;
			String type = "";
			switch (rs.getInt(6)) {
			case 1:
				type = "incoming";
				break;
			case 2:
				type = "outgoing";
				break;
			case 3:
				type = "missed";
				break;
			default:
				break;
			}
			String numberType = decodeNumberType(rs.getInt(7));
			String geoLocation = rs.getString(8);
			callsTableModel.addRow(new Object[] { name, number, date, duration, newCall, type, numberType, geoLocation });
		}

		view.getCallTable().setModel(callsTableModel);
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

	private String decodeNumberType(int numberTypeAsInt) {
		switch (numberTypeAsInt) {
		case 1:
			return "Home";
		case 2:
			return "Mobile";
		case 3:
			return "Work";
		case 4:
			return "Fax Work";
		case 5:
			return "Fax Home";
		case 6:
			return "Pager";
		case 7:
			return "Other";
		case 8:
			return "Callback";
		case 9:
			return "Car";
		case 10:
			return "Company Main";
		case 11:
			return "ISDN";
		case 12:
			return "Main";
		case 13:
			return "Other Fax";
		case 14:
			return "Radio";
		case 15:
			return "Telex";
		case 16:
			return "TTY TDD";
		case 17:
			return "Work Mobile";
		case 18:
			return "Work Pager";
		case 19:
			return "Assistant";
		case 20:
			return "MMS";
		default:
			break;
		}
		return "";
	}

	private void processSMSMMS(Statement statement) throws SQLException {
		// SMS:
		ResultSet rs = statement.executeQuery("SELECT address, person, date, body, read, seen, type FROM sms");
		while (rs.next()) {
			String number = rs.getString(1);
			String person = (rs.getString(2) == null) ? "" : rs.getString(2);
			Date date = new Date(rs.getLong(3));
			String text = rs.getString(4);
			boolean read = rs.getInt(5) == 1;
			boolean seen = rs.getInt(6) == 1;
			String[] smsStatus = new String[] { "Inbox", "Sent", "Draft", "Failed", "Queued", "Outbox", "Undelivered" };
			String status = smsStatus[rs.getInt(7) - 1];
			smsTableModel.addRow(new Object[] { number, person, date, text, read, seen, status });
		}
		view.getSmsTable().setModel(smsTableModel);

		// MMS:
		// TODO: gucken wo der text für sms gespeichert wird

		rs = statement.executeQuery("SELECT mid FROM part");
		ArrayList<Integer> mids = new ArrayList<Integer>();

		while (rs.next()) {
			if (!mids.contains(rs.getInt(1))) {
				mids.add(rs.getInt(1));
			}
		}

		for (int i = 0; i < mids.size(); i++) {
			// TODO: fragwürdig: type=137 bedeutet das absender?
			rs = statement
					.executeQuery("SELECT mid, name, _data, date, address, text, ct FROM (part JOIN pdu ON part.mid=pdu._id) JOIN addr ON part.mid=addr.msg_id WHERE addr.type=137 AND part.mid = "
							+ mids.get(i));
			int id = 0;
			String name = "";
			String data = "";
			Date date = null;
			String number = "";
			String text = "";
			String mimetype = "";
			while (rs.next()) {
				if (rs.getString(3) != null) {
					id = rs.getInt(1);
					name = rs.getString(2);
					data = rs.getString(3).substring(rs.getString(3).lastIndexOf('/') + 1);
					date = new Date(rs.getLong(4) * 1000);
					number = rs.getString(5);
					mimetype = rs.getString(7);
				} else if (rs.getString(7).contains("text")) {
					text = (rs.getString(6) != null) ? rs.getString(6) : "";
				}
			}
			mmsTableModel.addRow(new Object[] { id, number, date, text, data, mimetype });
		}

		// for schleife über alle mms anhänge und die dann pullen (blödsinn,
		// einfach während pull der ganzen dbs auch die app parts pullen
		// [com.android.providers.telephony/app_parts])

		view.getMmsTable().setModel(mmsTableModel);
		view.addTab(MMSSMS_FILENAME);
	}

	public void addNamesToSMS() {
		for (int i = 0; i < smsTableModel.getRowCount(); i++) {
			if (!((String) smsTableModel.getValueAt(i, 1)).equals("")) {
				try {
					int rawContactId = Integer.parseInt((String) smsTableModel.getValueAt(i, 1));
					if (!(contactNames.get(rawContactId) == null)) {
						smsTableModel.setValueAt(contactNames.get(rawContactId), i, 1);
					}
				} catch (NumberFormatException e) {
					// already added names
				}
			}
		}
	}

	private void processFacebook(Statement statement) throws SQLException {
		// FATZBUK
	}

	// TODO: media ordner von sdcard pullen
	// TODO: gruppen anders behandeln? (zumindest received timestamp)
	private void processWhatsapp(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT key_remote_jid FROM chat_list");
		ArrayList<String> ids = new ArrayList<String>();
		while (rs.next()) {
			// TODO: was passiert bei gruppen?
			ids.add(rs.getString(1));
			view.getWhatsappCombo().addItem(rs.getString(1));
			whatsAppTableModels.put(rs.getString(1), new LiveSearchTableModel(new Object[] { "Sent", "Received", "Sent Time",
					"Received Timestamp", "Latitude", "Longitude", "Filename" }));
		}

		for (int i = 0; i < ids.size(); i++) {
			LiveSearchTableModel currentModel = whatsAppTableModels.get(ids.get(i));
			rs = statement
					.executeQuery("SELECT _id, key_remote_jid, key_from_me, data, timestamp, media_mime_type, media_size, latitude, longitude, receipt_device_timestamp, thumb_image FROM messages WHERE key_remote_jid = '"
							+ ids.get(i) + "' ORDER BY timestamp ASC");
			while (rs.next()) {
				String data = (rs.getString(4) == null) ? "" : rs.getString(4);
				Date date = new Date(rs.getLong(5));
				String latitude = rs.getString(8);
				String longitude = rs.getString(9);
				String mimetype = rs.getString(6);
				if (rs.getInt(3) == 0) {
					if (mimetype != null) {
						// received data
						String filename = decodeMetadata(rs.getBytes(11));
						currentModel.addRow(new Object[] { "", mimetype, null, date, latitude, longitude, filename });
					} else {
						// received a message
						currentModel.addRow(new Object[] { "", data, null, date, latitude, longitude, "" });
					}
				} else if (rs.getInt(3) == 1) {
					Date receiptDeviceTimestamp = new Date(rs.getLong(10));
					if (mimetype != null) {
						// sent data
						String filename = decodeMetadata(rs.getBytes(11));
						currentModel.addRow(new Object[] { mimetype, "", date, receiptDeviceTimestamp, latitude, longitude, filename });
					} else {
						// sent message
						currentModel.addRow(new Object[] { data, "", date, receiptDeviceTimestamp, latitude, longitude, "" });
					}
				}
			}
		}
		view.getWhatsappTable().setModel(whatsAppTableModels.get(ids.get(0)));
		view.addTab(WHATSAPP_FILENAME);
	}

	private String decodeMetadata(byte[] metadata) {
		String result = "";
		if (metadata != null) {
			for (int j = 0; j < metadata.length; j++) {
				int character = new Integer(metadata[j] & 0xFF);
				if (character > 32 && character < 127) {
					result += (char) character;
				}
			}
			result = result.substring(0, result.length() - 3);
			result = result.substring(result.lastIndexOf("/") + 1);
		}
		return result;
	}

	private void processWebview(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT host, username, password FROM password");
		while (rs.next()) {
			String host = (rs.getString(1) == null) ? "" : rs.getString(1);
			String username = (rs.getString(2) == null) ? "" : rs.getString(2);
			String password = (rs.getString(3) == null) ? "" : rs.getString(3);
			browserTableModels.get(5).addRow(new Object[] { host, username, password });
		}
		rs = statement.executeQuery("SELECT host, username, password FROM httpauth");
		while (rs.next()) {
			String host = (rs.getString(1) == null) ? "" : rs.getString(1);
			String username = (rs.getString(2) == null) ? "" : rs.getString(2);
			String password = (rs.getString(3) == null) ? "" : rs.getString(3);
			browserTableModels.get(5).addRow(new Object[] { host, username, password });
		}
		rs = statement.executeQuery("SELECT name, value FROM formdata");
		while (rs.next()) {
			String name = (rs.getString(1) == null) ? "" : rs.getString(1);
			String value = (rs.getString(2) == null) ? "" : rs.getString(2);
			browserTableModels.get(4).addRow(new Object[] { name, value });
		}
		view.getBrowserTable().setModel(browserTableModels.get(0));
		view.addTab(WEBVIEW_FILENAME);
	}

	private void processCachedGeoposition(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT * FROM CachedPosition");
		while (rs.next()) {
			String latitude = rs.getString(1);
			String longitude = rs.getString(2);
			String altitude = rs.getString(3);
			double accuracy = rs.getDouble(4);
			double altitudeAccuracy = rs.getDouble(5);
			double heading = rs.getDouble(6);
			double speed = rs.getDouble(7);
			Date timestamp = new Date(rs.getLong(8));

			browserTableModels.get(1).addRow(
					new Object[] { latitude, longitude, altitude, accuracy, altitudeAccuracy, heading, speed, timestamp });
		}
		view.getBrowserTable().setModel(browserTableModels.get(0));
		view.addTab(BROWSER_CACHED_GEOPOSITION);
	}

	private void processMapsSearchHistory(Statement statement) throws SQLException {
		ResultSet rs = statement.executeQuery("SELECT data1, displayQuery, latitude, longitude, timestamp FROM suggestions");
		while (rs.next()) {
			String search = rs.getString(1);
			String suggestion = rs.getString(2);
			String latitude = rs.getString(3);
			String longitude = rs.getString(4);
			Date timestamp = new Date(rs.getLong(5));
			mapsTableModels.get(0).addRow(new Object[] { search, suggestion, latitude, longitude, timestamp });
		}
		view.getMapsTable().setModel(mapsTableModels.get(0));
		view.addTab(MAPS_SEARCH_HISTORY_FILENAME);
	}

	// TODO: bei lat und lon noch sicherstellen, dass komma da steht wo es soll
	private void processMapsDestinationHistory(Statement statement) throws SQLException {
		ResultSet rs = statement
				.executeQuery("SELECT time, dest_lat, dest_lng, dest_title, dest_address, source_lat, source_lng FROM destination_history");
		while (rs.next()) {
			Date time = new Date(rs.getLong(1));
			String dest_lat = rs.getString(2);
			String dest_lng = rs.getString(3);
			String dest_title = rs.getString(4);
			String dest_addr = rs.getString(5);
			String source_lat = rs.getString(6);
			String source_lng = rs.getString(7);
			mapsTableModels.get(1).addRow(new Object[] { time, dest_lat, dest_lng, dest_title, dest_addr, source_lat, source_lng });
		}
		view.getMapsTable().setModel(mapsTableModels.get(0));
		view.addTab(MAPS_DESTINATION_HISTORY_FILENAME);
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

	public void setWhatsappTableModel(String contactId) {
		if (whatsAppTableModels.get(contactId) != null) {
			view.getWhatsappTable().setModel(whatsAppTableModels.get(contactId));
		}
	}

	public void setMapsTableModel(int index) {
		view.getMapsTable().setModel(mapsTableModels.get(index));
	}

	public void setBrowserTableModel(int index) {
		view.getBrowserTable().setModel(browserTableModels.get(index));
	}

}
