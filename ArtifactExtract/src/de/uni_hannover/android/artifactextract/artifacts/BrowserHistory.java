package de.uni_hannover.android.artifactextract.artifacts;

public class BrowserHistory implements Artifact {

	public String url, title;
	public boolean is_bookmark;
	public long created;
	public int visits;
	
	public static String FILENAME = "BrowserHistory";

	/**  
	 * @param title title of the website
	 * @param url url of website
	 * @param is_bookmark false if site was just visited but is no bookmark
	 * @param created time in seconds since the epoch
	 * @param visits number of visits
	 */
	public BrowserHistory(String title, String url, boolean is_bookmark, long created, int visits) {
		this.title = title;
		this.url = url;
		this.is_bookmark = is_bookmark;
		this.created = created;
		this.visits = visits;
	}

	@Override
	public String getCSV() {
		return title.replace(",", "ESCAPED_COMMA") + ", " + url + ", " + visits + ", " + created
				+ ", " + is_bookmark;
	}

}
