package de.uni_hannover.android.artifactextract.artifacts;

public class MMS implements Artifact {

	private String sender, text, id;
	private long date;
	private boolean read;

	/**
	 * 
	 * @param sender sender of the mms
	 * @param text text in the mms
	 * @param id id of the mms (used to find associated picture, audio files)
	 * @param date time in seconds since epoch
	 * @param read true if mms was read by receiver
	 */
	public MMS(String sender, String text, String id, long date, boolean read) {
		this.sender = sender;
		this.text = text;
		this.id = id;
		this.date = date;
		this.read = read;
	}

	@Override
	public String getCSV() {
		return id + ", " + sender + ", " + date + ", " + text.replace(",", "ESCAPED_COMMA") + ", "
				+ read;
	}

}
