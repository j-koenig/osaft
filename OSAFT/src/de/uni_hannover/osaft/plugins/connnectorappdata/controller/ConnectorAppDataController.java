package de.uni_hannover.osaft.plugins.connnectorappdata.controller;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.plugins.connnectorappdata.tables.LiveSearchTableModel;
import de.uni_hannover.osaft.plugins.connnectorappdata.view.ConnectorAppDataView;

public class ConnectorAppDataController {
	public static String BROWSER_HISTORY_FILENAME = "BrowserHistory.csv";
	public static String BROWSER_SEARCH_FILENAME = "BrowserSearchHistory.csv";
	public static String CALENDAR_FILENAME = "CalendarEvents.csv";
	public static String CALLS_FILENAME = "CallLogs.csv";
	public static String CONTACTS_FILENAME = "Contacts.csv";
	public static String MMS_FILENAME = "MMS.csv";
	public static String SMS_FILENAME = "SMS.csv";

	private ConnectorAppDataView view;
	private LiveSearchTableModel calendarTableModel, callsTableModel, browserHistoryTableModel,
			browserSearchTableModel, contactsTableModel, mmsTableModel, smsTableModel;
	private ADBThread adb;

	public ConnectorAppDataController(ConnectorAppDataView view, ADBThread adb) {
		this.view = view;
		this.adb = adb;
		calendarTableModel = new LiveSearchTableModel(new Object[] { "Calendar", "Title",
				"Description", "Start", "End", "Location", "Allday" });
		callsTableModel = new LiveSearchTableModel(new Object[] { "Name", "Number", "Date",
				"Duration (in s)", "New Call", "Type", "Number Type" });
		browserHistoryTableModel = new LiveSearchTableModel(new Object[] { "Title", "URL",
				"Visits", "Created", "Bookmark?" });
		browserSearchTableModel = new LiveSearchTableModel(new Object[] { "Date", "Search" });
		contactsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Name", "Numbers",
				"Organisation", "Email", "Address", "Website", "IM", "Skype", "Notes" });
		smsTableModel = new LiveSearchTableModel(new Object[] { "Number", "Name", "Date",
				"Text", "Read", "Seen", "Status" });
		mmsTableModel = new LiveSearchTableModel(new Object[] { "ID", "Number", "Date", "Text",
				"Read", "Attachment" });
	}

	public boolean iterateChosenFolder(File folder) {
		boolean processedSomething = false;
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			String fName = curFile.getName();
			// only given csv files should be processed
			if (fName.equals(BROWSER_HISTORY_FILENAME) || fName.equals(BROWSER_SEARCH_FILENAME)
					|| fName.equals(CALENDAR_FILENAME) || fName.equals(CONTACTS_FILENAME)
					|| fName.equals(CALLS_FILENAME) || fName.equals(MMS_FILENAME)
					|| fName.equals(SMS_FILENAME)) {
				processCSV(curFile);
				processedSomething = true;
			}
		}

		view.showTabs();
		return processedSomething;
	}

	// switch for csv files
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
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(view.getView(), "One or more csv files are corrupted", "Warning", JOptionPane.WARNING_MESSAGE);
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
			title = title.replace("ESCAPED_COMMA", ",");
			String url = values[1];
			int visits = Integer.parseInt(values[2]);
			Date created = new Date(Long.parseLong(values[3]));
			boolean isBookmark = Boolean.parseBoolean(values[4]);
			browserHistoryTableModel
					.addRow(new Object[] { title, url, visits, created, isBookmark });
		}
		view.addTab(BROWSER_HISTORY_FILENAME, browserHistoryTableModel);
	}

	private void processBrowserSearch(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			Date date = new Date(Long.parseLong(values[0]));
			String search = values[1];
			search = search.replace("ESCAPED_COMMA", ",");
			browserSearchTableModel.addRow(new Object[] { date, search });
		}
		view.addTab(BROWSER_SEARCH_FILENAME, browserSearchTableModel);
	}

	private void processContacts(BufferedReader br) throws IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			int id = Integer.parseInt(values[0]);
			String name = "";
			String organisation = "";
			String numbers = "";
			String email = "";
			String addresses = "";
			String ims = "";
			String notes = "";
			String skypename = "";
			String websites = "";
			// iterate all elements which were seperated by commas
			for (int i = 1; i < values.length; i++) {
				if (values[i].contains("Name:")) {
					name = values[i].substring("Name:".length());
					name = name.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("Organisation:")) {
					organisation = values[i].substring("Organisation:".length());
					organisation = organisation.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("Numbers:")) {
					numbers = values[i].substring("Numbers:".length());
					numbers = numbers.replace("ESCAPED_COMMA", ",").replace(";", ",");
				} else if (values[i].contains("Emailaddresses:")) {
					email = values[i].substring("Emailaddresses:".length());
					email = email.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("Addresses:")) {
					addresses = values[i].substring("Addresses:".length());
					addresses = addresses.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("Websites:")) {
					websites = values[i].substring("Websites:".length());
					websites = websites.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("IMs:")) {
					ims = values[i].substring("IMs:".length());
					ims = ims.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("Notes:")) {
					notes = values[i].substring("Notes:".length());
					notes = notes.replace("ESCAPED_COMMA", ",");
				} else if (values[i].contains("Skype Nickname:")) {
					skypename = values[i].substring("Skype Nickname:".length());
					skypename = skypename.replace("ESCAPED_COMMA", ",");
				}
			}
			contactsTableModel.addRow(new Object[] { id, name, numbers, organisation, email,
					addresses, websites, ims, skypename, notes });
		}
		view.addTab(CONTACTS_FILENAME, contactsTableModel);
	}

	private void processMMS(BufferedReader br) throws IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String id = values[0];
			String number = values[1];
			Date date = new Date(Long.parseLong(values[2]));
			String text = values[3];
			text = text.replace("ESCAPED_COMMA", ",");
			boolean read = Boolean.parseBoolean(values[4]);
			boolean hasAttachment = Boolean.parseBoolean(values[5]);
			mmsTableModel.addRow(new Object[] { id, number, date, text, read, hasAttachment });
		}
		view.addTab(MMS_FILENAME, mmsTableModel);
	}

	private void processSMS(BufferedReader br) throws IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String number = values[0];
			String contactName = values[1];
			Date date = new Date(Long.parseLong(values[2]));
			String text = values[3];
			text = text.replace("ESCAPED_COMMA", ",");
			boolean read = Boolean.parseBoolean(values[4]);
			boolean seen = Boolean.parseBoolean(values[5]);
			int status = Integer.parseInt(values[6]);
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
			smsTableModel.addRow(new Object[] { number, contactName, date, text, read, seen,
					statusAsText });
		}
		view.addTab(SMS_FILENAME, smsTableModel);
	}

	private void processCalls(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String name = (values[0].equals(" ")) ? "unknown" : values[0];
			name = name.replace("ESCAPED_COMMA", ",");
			String number = (values[1].equals("-2")) ? "private number" : values[1];
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

			String numberLabel = values[6];
			String numberTypeAsInt = values[7];
			String numberType = "";

			switch (Integer.parseInt(numberTypeAsInt)) {
			case 0:
				numberType = numberLabel;
				break;
			case 1:
				numberType = "Home";
				break;
			case 2:
				numberType = "Mobile";
				break;
			case 3:
				numberType = "Work";
				break;
			case 4:
				numberType = "Fax Work";
				break;
			case 5:
				numberType = "Fax Home";
				break;
			case 6:
				numberType = "Pager";
				break;
			case 7:
				numberType = "Other";
				break;
			case 8:
				numberType = "Callback";
				break;
			case 9:
				numberType = "Car";
				break;
			case 10:
				numberType = "Company Main";
				break;
			case 11:
				numberType = "ISDN";
				break;
			case 12:
				numberType = "Main";
				break;
			case 13:
				numberType = "Other Fax";
				break;
			case 14:
				numberType = "Radio";
				break;
			case 15:
				numberType = "Telex";
				break;
			case 16:
				numberType = "TTY TDD";
				break;
			case 17:
				numberType = "Work Mobile";
				break;
			case 18:
				numberType = "Work Pager";
				break;
			case 19:
				numberType = "Assistant";
				break;
			case 20:
				numberType = "MMS";
				break;

			default:
				break;
			}

			callsTableModel.addRow(new Object[] { name, number, date, duration, newCall, type,
					numberType });
		}
		view.addTab(CALLS_FILENAME, callsTableModel);
	}

	private void processCalendar(BufferedReader br) throws NumberFormatException, IOException {
		String curLine;
		while ((curLine = br.readLine()) != null) {
			String[] values = curLine.split(",");
			String calendarName = values[0];
			String title = values[1];
			title = title.replace("ESCAPED_COMMA", ",");
			String description = values[2];
			description = description.replace("ESCAPED_COMMA", ",");
			Date start = new Date(Long.parseLong(values[3]));
			Date end = new Date(Long.parseLong(values[4]));
			String location = values[5];
			location = location.replace("ESCAPED_COMMA", ",");
			boolean allDay = Boolean.parseBoolean(values[6]);

			calendarTableModel.addRow(new Object[] { calendarName, title, description, start, end,
					location, allDay });
		}
		view.addTab(CALENDAR_FILENAME, calendarTableModel);
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
