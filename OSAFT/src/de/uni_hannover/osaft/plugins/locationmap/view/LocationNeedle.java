package de.uni_hannover.osaft.plugins.locationmap.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Date;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import de.uni_hannover.osaft.util.GeoLocation;

public class LocationNeedle implements MapMarker{

	private Rectangle r;
	private Color c;
	private GeoLocation loc;

	public LocationNeedle(GeoLocation loc, Color c) {
		super();
		this.c = c;
		this.loc = loc;
	}

	@Override
	public double getLat() {
		return loc.getLat();
	}

	@Override
	public double getLon() {
		return loc.getLon();
	}
	
	public String getSource() {
		return loc.getSource();
	}
	
	public Date getTimestamp() {
		return loc.getTimestamp();
	}

	@Override
	public void paint(Graphics g, Point p) {
		r = new Rectangle(p.x - 10, p.y - 20, 20, 20);
		g.drawPolygon(new int[] { p.x - 10, p.x + 10, p.x }, new int[] { p.y - 20, p.y - 20, p.y }, 3);
		g.setColor(c);
		g.fillPolygon(new int[] { p.x - 10, p.x + 10, p.x }, new int[] { p.y - 20, p.y - 20, p.y }, 3);
	}

	public Rectangle getRect() {
		return r;
	}
	
	public String toString() { 
		return loc.toString();
	}
	
}
