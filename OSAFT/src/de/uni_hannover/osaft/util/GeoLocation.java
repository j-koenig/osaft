package de.uni_hannover.osaft.util;

import java.io.Serializable;
import java.util.Date;

public class GeoLocation implements Serializable {

	private static final long serialVersionUID = 1L;
	private double lon;
	private double lat;
	private String source;
	private Date timestamp;
	
	public GeoLocation(double lat, double lon, String source, Date timestamp) {
		this.lon = lon;
		this.lat = lat;
		this.source = source;
		this.timestamp = timestamp;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return "Source: " + source + ", Latitude: " + lat + ", Longitude: " + lon + ", Timestamp: " + timestamp.toString();
	}

}
