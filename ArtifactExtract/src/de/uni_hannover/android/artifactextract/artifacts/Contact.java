package de.uni_hannover.android.artifactextract.artifacts;

import java.util.ArrayList;

import android.provider.ContactsContract;
import android.util.Log;

public class Contact implements Artifact {

	private String name, organisation, notes, id, skypename;
	private ArrayList<String> numbers, emails, addresses, ims, websites;

	public Contact(String id) {
		name = null;
		organisation = null;
		notes = null;
		this.id = id;
		emails = new ArrayList<String>();
		addresses = new ArrayList<String>();
		websites = new ArrayList<String>();
		numbers = new ArrayList<String>();
		ims = new ArrayList<String>();
		skypename = null;
	}

	public String getCSV() {
		String res = id + ", ";
		if (name != null) {
			res += "Name: " + name.replace(",", "ESCAPED_COMMA") + ", ";
		}
		if (organisation != null) {
			res += "Organisation: " + organisation.replace(",", "ESCAPED_COMMA") + ", ";
		}
		for (int i = 0; i < numbers.size(); i++) {
			res += "Number: " + numbers.get(i).replace(",", "ESCAPED_COMMA") + ", ";
		}
		for (int i = 0; i < emails.size(); i++) {
			if (!emails.get(i).equals("")) {
				res += "Email: " + emails.get(i).replace(",", "ESCAPED_COMMA") + ", ";
			}
		}
		for (int i = 0; i < addresses.size(); i++) {
			res += "Address: " + addresses.get(i).replace(",", "ESCAPED_COMMA") + ", ";
		}
		for (int i = 0; i < websites.size(); i++) {
			res += "Website: " + websites.get(i).replace(",", "ESCAPED_COMMA") + ", ";
		}
		for (int i = 0; i < ims.size(); i++) {
			res += ims.get(i).replace(",", "ESCAPED_COMMA") + ", ";
		}
		if ((notes != null) && (!notes.equals(""))) {
			res += "Notes: " + notes.replace(",", "ESCAPED_COMMA") + ", ";
		}
		if (skypename != null) {
			res += "Skype Nickname: " + skypename.replace(",", "ESCAPED_COMMA") + ", ";
		}
		if (res == "") {
			Log.e("Contact.getCSV()", "emtpy contact");
			return "Empty Contact";
		}

		return res.substring(0, res.length() - 2);
	}

	/**
	 * This method checks which mimetype the new information has and saves it to
	 * the appropriate variable resp. adds it to the appropriate list
	 * 
	 * @param mime
	 *            mimetype
	 * @param data
	 *            contact information
	 * @param imProtocol
	 *            if data is information about an IM account this parameter
	 *            tells which protocol is used for the given account (see
	 *            https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Im.html)
	 * @param customProtocol 
	 * 	          if imProtocol is -1 this parameter is used to deliver the name of the protocol
	 */
	public void addInfo(String mime, String data, int imProtocol, String customProtocol) {
		if (mime.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
			numbers.add(data);
		} else if (mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
			emails.add(data);
		} else if (mime.equals(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)) {
			websites.add(data);
		} else if (mime.equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)) {
			addresses.add(data);
		} else if (mime.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
			name = data;
		} else if (mime.equals(ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)) {
			notes = data;
		} else if (mime.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
			organisation = data;
		} else if (mime.equals(ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)) {
			// imProtocol == -1 => custom IM protocol
			if (imProtocol == -1) {
				ims.add(customProtocol + ": " + data);
			} else {
				ims.add(imProtocolAsString(imProtocol) + ": " + data);
			}
		} else if (mime.equals("vnd.android.cursor.item/com.skype.android.skypecall.action")) {
			skypename = data;
		}

	}

	private String imProtocolAsString(int imProtocol) {
		switch (imProtocol) {
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_AIM:
			return "AIM";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN:
			return "MSN";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_YAHOO:
			return "YAHOO";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK:
			return "Google Talk";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_ICQ:
			return "ICQ";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER:
			return "Jabber";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_NETMEETING:
			return "Netmeeting";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ:
			return "QQ";
		case ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE:
			return "Skype";
		default:
			return "";
		}
	}

}
