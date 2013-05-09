/*******************************************************************************
 * Copyright (c) 2013 Jannis Koenig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jannis Koenig - initial API and implementation
 ******************************************************************************/
package de.uni_hannover.osaft.plugins.locationmap.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Date;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import de.uni_hannover.osaft.util.GeoLocation;

/**
 * Wrapper class for {@link GeoLocation}-objects. Objects of this class are
 * drawable on the {@link LocationMapView}
 * 
 * @author Jannis Koenig
 * 
 */
public class LocationNeedle implements MapMarker {

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
