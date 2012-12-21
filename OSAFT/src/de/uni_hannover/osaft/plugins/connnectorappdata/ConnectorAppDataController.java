package de.uni_hannover.osaft.plugins.connnectorappdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import de.uni_hannover.osaft.plugins.connnectorappdata.tables.CustomDefaultTableModel;

//TODO: suche implementieren
//TODO: extra dialog, der sich öffnet bei klick auf mms eintrag oder kontakt eintrag

public class ConnectorAppDataController {
	public static String BROWSER_HISTORY_FILENAME = "BrowserHistory.csv";
	public static String BROWSER_SEARCH_FILENAME = "BrowserSearchHistory.csv";
	public static String CALENDAR_FILENAME = "CalendarEvents.csv";
	public static String CALLS_FILENAME = "CallLogs.csv";
	public static String CONTACTS_FILENAME = "Contacts.csv";
	public static String MMS_FILENAME = "MMS.csv";
	public static String SMS_FILENAME = "SMS.csv";

	private ConnectorAppDataView view;
	private CustomDefaultTableModel calendarTableModel, callsTableModel, browserHistoryTableModel,
			browserSearchTableModel, contactsTableModel, mmsTableModel, smsTableModel;

	public ConnectorAppDataController(ConnectorAppDataView view) {
		this.view = view;
		calendarTableModel = new CustomDefaultTableModel(new Object[] { "Calendar", "Title",
				"Description", "Start", "End", "Location", "Allday" });
		callsTableModel = new CustomDefaultTableModel(new Object[] { "Name", "Number", "Date",
				"Duration (in s)", "New Call", "Type", "Number Label", "Number Type" });
		browserHistoryTableModel = new CustomDefaultTableModel(new Object[] { "Title", "URL",
				"Visits", "Created", "Bookmark?" });
		browserSearchTableModel = new CustomDefaultTableModel(new Object[] { "Date", "Search" });
		// contactsTableModel = new CustomDefaultTableModel(new Object[] { "",
		// 0)
		smsTableModel = new CustomDefaultTableModel(new Object[] { "Number", "Date", "Text",
				"Read", "Seen", "Status" });
		mmsTableModel = new CustomDefaultTableModel(new Object[] { "ID", "Number", "Date", "Text",
				"Read", "Attachment" });
	}

	public void iterateChosenFolder(File folder) {
		// progressbar?
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			String fName = curFile.getName();
			if (fName.equals(BROWSER_HISTORY_FILENAME) || fName.equals(BROWSER_SEARCH_FILENAME)
					|| fName.equals(CALENDAR_FILENAME) || fName.equals(CONTACTS_FILENAME)
					|| fName.equals(CALLS_FILENAME) || fName.equals(MMS_FILENAME)
					|| fName.equals(SMS_FILENAME)) {
				processCSV(curFile);
			}
		}
		view.addTabs();
	}

	private void processCSV(File f) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			if (f.getName().equals(SMS_FILENAME)) {
				processSMS(br);
			} else if (f.getName().equals(MMS_FILENAME)) {
				processMMS(br);
			} else if (f.getName().equals(CONTACTS_FILENAME)) {
				processContacts(br);
			} else if (f.getName().equals(CALLS_FILENAME)) {
				processCalls(br);
			} else if (f.getName().equals(CALENDAR_FILENAME)) {
				processCalendar(br);
			} else if (f.getName().equals(BROWSER_SEARCH_FILENAME)) {
				processBrowserSearch(br);
			} else if (f.getName().equals(BROWSER_HISTORY_FILENAME)) {
				processBrowserHistory(br);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// TODO: könnte auch ne indexoutofbounds werfen (wenn datei kaputt
			// is und array nicht 8 einträge hat)!
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void processBrowserHistory(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String title = values[0];
			String url = values[1];
			int visits = Integer.parseInt(values[2]);
			Date created = new Date(Long.parseLong(values[3]));
			boolean isBookmark = Boolean.parseBoolean(values[4]);
			browserHistoryTableModel
					.addRow(new Object[] { title, url, visits, created, isBookmark });
		}
		view.addBrowserHistoryTab(browserHistoryTableModel);
	}

	private void processBrowserSearch(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			Date date = new Date(Long.parseLong(values[0]));
			String search = values[1];
			browserSearchTableModel.addRow(new Object[] { date, search });
		}
		view.addBrowserSearchTab(browserSearchTableModel);
	}

	private void processContacts(BufferedReader br) {
		// TODO:
	}

	private void processMMS(BufferedReader br) throws IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String id = values[0];
			String number = values[1];
			Date date = new Date(Long.parseLong(values[2]));
			String text = values[3];
			boolean read = Boolean.parseBoolean(values[4]);
			// TODO:
			boolean hasAttachment = false;
			mmsTableModel.addRow(new Object[] { id, number, date, text, read, hasAttachment });
		}
		view.addMMSTab(mmsTableModel);
	}

	private void processSMS(BufferedReader br) throws IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String number = values[0];
			Date date = new Date(Long.parseLong(values[1]));
			String text = values[2];
			boolean read = Boolean.parseBoolean(values[3]);
			boolean seen = Boolean.parseBoolean(values[4]);
			int status = Integer.parseInt(values[5]);
			String statusAsText = "unknown";
			switch (status) {
			case 0:
				statusAsText = "Inbox";
				break;
			case 1:
				statusAsText = "Sent";
				break;
			case 2:
				statusAsText = "Draft";
				break;
			case 3:
				statusAsText = "Failed";
				break;
			case 4:
				statusAsText = "Queued";
				break;
			case 5:
				statusAsText = "Outbox";
				break;
			case 6:
				statusAsText = "Undelivered";
				break;
			default:
				break;
			}
			smsTableModel.addRow(new Object[] { number, date, text, read, seen, statusAsText });
		}
		view.addSMSTab(smsTableModel);
	}

	private void processCalls(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String name = (values[0].equals(" ")) ? "unknown" : values[0];
			String number = values[1];
			Date date = new Date(Long.parseLong(values[2]));
			int duration = Integer.parseInt(values[3]);
			boolean newCall = Boolean.parseBoolean(values[4]);
			String type = null;
			switch (Integer.parseInt(values[5])) {
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
			// TODO: dunno was damit zu machen
			String numberLabel = values[6];
			String numberType = values[7];
			callsTableModel.addRow(new Object[] { name, number, date, duration, newCall, type,
					numberLabel, numberType });
		}
		view.addCallTab(callsTableModel);
	}

	private void processCalendar(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String calendarName = values[0];
			String title = values[1];
			String description = values[2];
			Date start = new Date(Long.parseLong(values[3]));
			Date end = new Date(Long.parseLong(values[4]));
			String location = values[5];
			boolean allDay = Boolean.parseBoolean(values[6]);

			calendarTableModel.addRow(new Object[] { calendarName, title, description, start, end,
					location, allDay });
		}
		view.addCalendarTab(calendarTableModel);
	}

	public void pushApp() {
		// TODO
	}

}
