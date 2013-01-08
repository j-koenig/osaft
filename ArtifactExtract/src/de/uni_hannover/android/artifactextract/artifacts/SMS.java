package de.uni_hannover.android.artifactextract.artifacts;

import android.util.Log;

public class SMS implements Artifact {

	private String body, address, personID;
	private long date;
	private boolean read, seen;
	private int status;
	public static final int STATUS_INBOX = 0;
	public static final int STATUS_SENT = 1;
	public static final int STATUS_DRAFT = 2;
	public static final int STATUS_FAILED = 3;
	public static final int STATUS_QUEUED = 4;
	public static final int STATUS_OUTBOX = 5;
	public static final int STATUS_UNDELIVERED = 6;

	public static final String FILENAME = "SMS";

	/**
	 * 
	 * @param address
	 *            number of receiver
	 * @param body
	 *            text of sms
	 * @param date
	 *            time in seconds since epoch
	 * @param read
	 *            true if sms was read by receiver
	 * @param seen
	 *            true if statusmessage for sms was read (seen != read)
	 * @param status
	 *            status of the sms
	 */
	public SMS(String address, String personID, String body, long date, boolean read, boolean seen,
			int status) {
		this.address = address;
		this.body = body;
		this.date = date;
		this.read = read;
		this.seen = seen;
		this.status = status;
		this.personID = personID;
	}

	@Override
	public String getCSV() {
		return address + "," + ((personID == null) ? "" : personID) + "," + date + ","
				+ body.replace(",", "ESCAPED_COMMA") + "," + read + "," + seen + "," + status;
	}

}
