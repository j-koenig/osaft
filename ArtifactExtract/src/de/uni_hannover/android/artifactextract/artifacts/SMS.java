package de.uni_hannover.android.artifactextract.artifacts;

public class SMS implements Artifact {

	private String body, address;
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
	public SMS(String address, String body, long date, boolean read, boolean seen, int status) {
		this.address = address;
		this.body = body;
		this.date = date;
		this.read = read;
		this.seen = seen;
		this.status = status;
	}

	@Override
	public String getCSV() {
		return address + ", " + date + ", " + body.replace(",", "ESCAPED_COMMA") + ", " + read
				+ ", " + seen + ", " + status;
	}

}
