package de.uni_hannover.android.artifactextract;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.util.SparseArray;
import de.uni_hannover.android.artifactextract.artifacts.Artifact;
import de.uni_hannover.android.artifactextract.artifacts.BrowserHistory;
import de.uni_hannover.android.artifactextract.artifacts.BrowserSearch;
import de.uni_hannover.android.artifactextract.artifacts.CalendarEvent;
import de.uni_hannover.android.artifactextract.artifacts.Call;
import de.uni_hannover.android.artifactextract.artifacts.Contact;
import de.uni_hannover.android.artifactextract.artifacts.MMS;
import de.uni_hannover.android.artifactextract.artifacts.SMS;
import de.uni_hannover.android.artifactextract.util.SDCardHandler;

public class Gatherer {
	// TODO: mms video?

	private String savePath, dataPath;
	// storages for all different artifacts:
	private ArrayList<Artifact> contacts, events, smss, calls, mmss, browserHistory,
			browserSearches;
	private ContentResolver cr;
	private ArtifactExtract view;

	public Gatherer(String path, ContentResolver cr, ArtifactExtract view) {
		contacts = new ArrayList<Artifact>();
		events = new ArrayList<Artifact>();
		smss = new ArrayList<Artifact>();
		calls = new ArrayList<Artifact>();
		mmss = new ArrayList<Artifact>();
		browserHistory = new ArrayList<Artifact>();
		browserSearches = new ArrayList<Artifact>();
		createCurrentDir(path);
		this.cr = cr;
		this.view = view;
	}

	// creates for each extraction a new directory in folder "artifacts" with
	// current time and date
	public void createCurrentDir(String path) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
		savePath = path + sdf.format(new Date()) + "/";
		SDCardHandler.mkDir(savePath);
		// contactphotos and mms attachments will be saved here:
		dataPath = savePath + "data/";
		SDCardHandler.mkDir(dataPath);
	}

	// basically the next get... methods work very similar: they query databases
	// with projections and selections and iterate through the returned cursor.
	// The results are written to arraylists which will be iterated by the
	// SDCardHandler to save the gathered information to the sdcard
	public void getBrowserHistory() {
		String[] projection = new String[] { Browser.BookmarkColumns.BOOKMARK,
				Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL,
				Browser.BookmarkColumns.VISITS, Browser.BookmarkColumns.CREATED };
		Cursor browserCursor = cr.query(Browser.BOOKMARKS_URI, projection, null, null, null);
		try {
			while (browserCursor.moveToNext()) {
				boolean is_bookmark = browserCursor.getString(0).equals("1");
				String title = browserCursor.getString(1);
				String url = browserCursor.getString(2);
				int visits = browserCursor.getInt(3);
				long created = browserCursor.getLong(4);
				BrowserHistory bd = new BrowserHistory(title, url, is_bookmark, created, visits);
				browserHistory.add(bd);
			}
			SDCardHandler.writeCSV(savePath, "BrowserHistory", browserHistory);
		} catch (IOException e) {
			view.showIOError("browser history");
		} finally {
			browserCursor.close();
		}
	}

	public void getBrowserSearch() {
		String[] projection = new String[] { Browser.SearchColumns.SEARCH,
				Browser.SearchColumns.DATE };
		Cursor browserCursor = cr.query(Browser.SEARCHES_URI, projection, null, null, null);
		try {
			while (browserCursor.moveToNext()) {
				String search = browserCursor.getString(0);
				long date = browserCursor.getLong(1);
				BrowserSearch bs = new BrowserSearch(search, date);
				browserSearches.add(bs);
			}
			SDCardHandler.writeCSV(savePath, "BrowserSearchHistory", browserSearches);
		} catch (IOException e) {
			view.showIOError("browser search history");
		} finally {
			browserCursor.close();
		}
	}

	public void getCalendar() {
		SparseArray<String> calendars = new SparseArray<String>();
		String authority;

		// content provider changed in SDK version 8
		if (android.os.Build.VERSION.SDK_INT < 8) {
			authority = "calendar";
		} else {
			authority = "com.android.calendar";
		}

		// get all calendar ids and corresponding names
		Uri calendarUri = Uri.parse("content://" + authority + "/calendars");
		String[] projection = new String[] { "_id", "name" };
		Cursor calendarCursor = cr.query(calendarUri, projection, null, null, null);
		try {
			while (calendarCursor.moveToNext()) {
				int id = calendarCursor.getInt(0);
				String name = calendarCursor.getString(1);
				calendars.put(id, name);
			}
		} finally {
			calendarCursor.close();
		}

		Uri.Builder builder = Uri.parse("content://" + authority + "/instances/when").buildUpon();

		// not pretty to use 0 as start date and Long.MAX_VALUE as end date, but
		// table "events" (content://com.android.calendar/events) does not
		// return
		// all events and table "instances" needs a start and an end date
		ContentUris.appendId(builder, 0);
		ContentUris.appendId(builder, Long.MAX_VALUE);

		// query all events from the "instances" table
		projection = new String[] { "calendar_id", "title", "description", "begin", "end",
				"allDay", "eventLocation" };
		String sort = "startDay ASC, startMinute ASC";
		Cursor eventCursor = cr.query(builder.build(), projection, null, null, sort);
		try {
			while (eventCursor.moveToNext()) {
				String calendar = calendars.get(eventCursor.getInt(0));
				String title = eventCursor.getString(1);
				String description = eventCursor.getString(2);
				long begin = eventCursor.getLong(3);
				long end = eventCursor.getLong(4);
				boolean allday = !eventCursor.getString(5).equals("0");
				String location = eventCursor.getString(6);

				CalendarEvent event = new CalendarEvent(begin, end, title, description, allday,
						calendar, location);
				events.add(event);
			}
			SDCardHandler.writeCSV(savePath, "CalendarEvents", events);
		} catch (IOException e) {
			view.showIOError("calendar events");
		} finally {
			eventCursor.close();
		}
	}

	public void getCalls() {
		String[] projection = new String[] { CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
				CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.NEW,
				CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.CACHED_NUMBER_TYPE };
		Cursor callCursor = cr.query(CallLog.Calls.CONTENT_URI, projection, null, null, null);

		try {
			while (callCursor.moveToNext()) {
				Call call = new Call(callCursor.getString(0), callCursor.getString(1),
						callCursor.getLong(2), callCursor.getLong(3), callCursor.getInt(4),
						(callCursor.getInt(5) == 1), callCursor.getString(6),
						callCursor.getString(7));
				calls.add(call);
			}
			SDCardHandler.writeCSV(savePath, "CallLogs", calls);
		} catch (IOException e) {
			view.showIOError("call logs");
		} finally {
			callCursor.close();
		}
	}

	public void getContacts() {

		// get all contact ids
		ArrayList<String> contactIds = new ArrayList<String>();
		Uri contactsUri = ContactsContract.RawContacts.CONTENT_URI;
		String[] projection = new String[] { RawContacts.CONTACT_ID, RawContacts._ID };
		Cursor idCursor = cr.query(contactsUri, projection, null, null, null);

		try {
			while (idCursor.moveToNext()) {
				String id = idCursor.getString(0);
				if (id != null) {
					contactIds.add(id);
				}
			}
		} finally {
			idCursor.close();
		}

		// raw contacts entity table can be thought of as an outer join of the
		// raw_contacts table with the data table (contains all data for every
		// raw contact)
		Uri entityUri = ContactsContract.RawContactsEntity.CONTENT_URI;
		projection = new String[] { RawContactsEntity.MIMETYPE, RawContactsEntity.DATA1,
				RawContactsEntity.DATA5, RawContactsEntity.DATA6, RawContactsEntity.CONTACT_ID };

		// iterate through all contact ids and get information for current id
		for (int i = 0; i < contactIds.size(); i++) {
			String id = contactIds.get(i);
			Contact con = new Contact(id);
			String selection = RawContactsEntity.CONTACT_ID + " = " + id;
			Cursor contactCursor = cr.query(entityUri, projection, selection, null, null);

			try {
				while (contactCursor.moveToNext()) {
					String mime = contactCursor.getString(0);
					String data = contactCursor.getString(1);
					int improtocol = contactCursor.getInt(2);
					String customProtocol = contactCursor.getString(3);
					con.addInfo(mime, data, improtocol, customProtocol);

				}
				contacts.add(con);

			} finally {
				contactCursor.close();
			}
			getContactsPhoto(id);
		}
		try {
			SDCardHandler.writeCSV(savePath, "Contacts", contacts);
		} catch (IOException e) {
			view.showIOError("contacts");
		}
	}

	// gets the contact photo for the given contact id, if available
	private void getContactsPhoto(String contact_id) {
		Uri photoUri = Data.CONTENT_URI;
		String[] projection = new String[] { Photo.PHOTO, Data._ID, Data.CONTACT_ID };
		String selection = Data.CONTACT_ID + " = " + contact_id;
		Cursor cursor = cr.query(photoUri, projection, selection, null, null);
		String filePath = dataPath + "contact_" + contact_id + ".jpg";
		try {
			while (cursor.moveToNext()) {
				byte[] photo = cursor.getBlob(0);
				if (photo != null) {
					Bitmap photoBitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
					SDCardHandler.savePicture(filePath, photoBitmap);
				}
			}
		} catch (IOException e) {
			view.showIOError("contact picture");
		} finally {
			cursor.close();
		}
	}

	public void getMMS() {
		String[] projection = new String[] { "_id", "date", "read" };
		Uri mmsUri = Uri.parse("content://mms");
		Cursor mmsCursor = cr.query(mmsUri, projection, null, null, null);
		try {
			while (mmsCursor.moveToNext()) {
				String id = mmsCursor.getString(0);
				long date = mmsCursor.getLong(1);
				boolean read = mmsCursor.getString(2).equals("1");
				String text = getMMSText(id);
				String sender = getMMSAddress(id);
				getMMSData(id);
				MMS mms = new MMS(sender, text, id, date, read);
				mmss.add(mms);
			}
			SDCardHandler.writeCSV(savePath, "MMS", mmss);
		} catch (IOException e) {
			view.showIOError("MMS");
		} finally {
			mmsCursor.close();
		}
	}

	// gets the text of the mms
	private String getMMSText(String id) {
		String text = null;
		String selection = "mid=" + id;
		Uri uri = Uri.parse("content://mms/part");
		Cursor cursor = cr.query(uri, null, selection, null, null);
		try {
			while (cursor.moveToNext()) {
				String partId = cursor.getString(cursor.getColumnIndex("_id"));
				String mimetype = cursor.getString(cursor.getColumnIndex("ct"));
				// if text is directly readable (from column "text") then
				// return it, else process the data from column "_data"
				if ("text/plain".equals(mimetype)) {
					String data = cursor.getString(cursor.getColumnIndex("_data"));
					if (data != null) {
						text = getMMSText_data(partId);
					} else {
						text = cursor.getString(cursor.getColumnIndex("text"));
					}
				}
			}
		} finally {
			cursor.close();
		}
		return text;
	}

	// processes the data from the "_data" column
	private String getMMSText_data(String id) {
		Uri partURI = Uri.parse("content://mms/part/" + id);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = cr.openInputStream(partURI);
			if (is != null) {
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				String temp = reader.readLine();
				while (temp != null) {
					sb.append(temp);
					temp = reader.readLine();
				}
			}
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	// gets the phone number for a given mms id
	private String getMMSAddress(String id) {

		String selection = new String("msg_id=" + id);
		Uri addressUri = Uri.parse("content://mms/" + id + "/addr");
		Cursor addressCursor = cr.query(addressUri, null, selection, null, null);
		String result = null;

		try {
			if (addressCursor.moveToNext()) {
				result = addressCursor.getString(addressCursor.getColumnIndex("address"));
			}
		} finally {
			addressCursor.close();
		}
		return result;
	}

	// differentiates between audio and image attachments and calls the proper
	// method to save the attachment
	private void getMMSData(String id) {

		Cursor mmsCursor = cr.query(Uri.parse("content://mms/" + id + "/part"), null, null, null,
				null);
		try {
			while (mmsCursor.moveToNext()) {
				String mime = mmsCursor.getString(3);
				String[] mimeSplit = mime.split("/");
				if (mimeSplit[0].equals("image")) {
					saveMMSPicture(id, mimeSplit[1]);
				} else if (mimeSplit[0].equals("audio")) {
					saveMMSAudio(id, mimeSplit[1]);
				}
			}
		} finally {
			mmsCursor.close();
		}

	}

	private void saveMMSAudio(String id, String format) {
		Uri partURI = Uri.parse("content://mms/part/" + id);
		String filePath = dataPath + "mms_" + id + "." + format;
		try {
			InputStream is = cr.openInputStream(partURI);
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			is.close();
			FileOutputStream save = new FileOutputStream(filePath);
			save.write(buffer);
			save.flush();
			save.close();
		} catch (Exception e) {
			view.showIOError("MMS audio");
		}

	}

	private void saveMMSPicture(String id, String format) {
		Uri partURI = Uri.parse("content://mms/part/" + id);
		String filePath = dataPath + "mms_" + id + "." + format;
		try {
			InputStream is = cr.openInputStream(partURI);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			SDCardHandler.savePicture(filePath, bitmap);
		} catch (IOException e) {
			view.showIOError("MMS picture");
		}

	}

	public void getSMS() {
		String[] projection = new String[] { "address", "body", "date", "read", "seen" };
		String sort = "date ASC";
		String[] smsStatus = new String[] { "inbox", "sent", "draft", "failed", "queued", "outbox",
				"undelivered" };

		// query all sms folders:
		for (int i = 0; i < smsStatus.length; i++) {
			Uri smsUri = Uri.parse("content://sms/" + smsStatus[i]);
			Cursor smsCursor = cr.query(smsUri, projection, null, null, sort);
			try {
				while (smsCursor.moveToNext()) {
					String address = smsCursor.getString(0);
					String body = smsCursor.getString(1);
					long date = smsCursor.getLong(2);
					boolean read = smsCursor.getString(3).equals("1");
					boolean seen = smsCursor.getString(4).equals("1");
					SMS sms = new SMS(address, body, date, read, seen, i);
					smss.add(sms);
				}
			} finally {
				smsCursor.close();
			}
		}
		try {
			SDCardHandler.writeCSV(savePath, "SMS", smss);
		} catch (IOException e) {
			view.showIOError("SMS");
		}
	}

}
