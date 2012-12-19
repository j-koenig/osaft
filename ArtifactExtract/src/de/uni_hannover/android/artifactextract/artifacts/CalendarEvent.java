package de.uni_hannover.android.artifactextract.artifacts;

public class CalendarEvent implements Artifact {

	private long start;
	private long end;
	private String description;
	private String title;
	private boolean allday;
	private String calendar;
	private String location;
	
	public static String FILENAME = "CalendarEvents";

	/**
	 * 
	 * @param start time in seconds since epoch
	 * @param end time in seconds since epoch
	 * @param title title of the event
	 * @param description description of the event
	 * @param allday true if event lasts the whole day
	 * @param calendar name of the related calendar
	 * @param location 
	 */
	public CalendarEvent(long start, long end, String title, String description, boolean allday,
			String calendar, String location) {
		this.start = start;
		this.end = end;
		this.title = title;
		this.description = description;
		this.allday = allday;
		this.calendar = calendar;
		this.location = location;
	}

	public String getCSV() {
		return calendar + ", " + title.replace(",", "ESCAPED_COMMA") + ", "
				+ description.replace(",", "ESCAPED_COMMA") + ", " + start + ", " + end + ", "
				+ location.replace(",", "ESCAPED_COMMA") + ", " + allday;
	}
}
