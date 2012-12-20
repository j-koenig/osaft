package de.uni_hannover.android.artifactextract.artifacts;

public class BrowserSearch implements Artifact{
	
	private String search;
	private long date;
	
	public static String FILENAME = "BrowserSearchHistory";
	
	/**
	 * 
	 * @param search the string that was searched
	 * @param date time in seconds since epoch
	 */
	public BrowserSearch(String search, long date) {
		this.search = search;
		this.date = date;
	}

	@Override
	public String getCSV() {
		return date + "," + search.replace(",", "ESCAPED_COMMA");
	}

}
