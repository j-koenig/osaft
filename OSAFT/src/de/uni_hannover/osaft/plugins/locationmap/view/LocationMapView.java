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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.plugins.locationmap.controller.LocationMapController;
import de.uni_hannover.osaft.util.CasefolderWriter;
import de.uni_hannover.osaft.util.GeoLocation;

/**
 * This class implements the {@link ViewPlugin}. It shows a map of the world and
 * allows adding MapMarkers to display all found geolocations. The corresponding
 * controller is {@link LocationMapController}.
 * 
 * @author Jannis Koenig
 * 
 */
@PluginImplementation
public class LocationMapView implements ViewPlugin, MouseMotionListener, MouseListener {

	private JPanel mainPanel;
	private JMapViewer map;
	private JLabel positionLabel, instructionLabel;
	private LocationMapController controller;
	private CasefolderWriter cfw;
	private ArrayList<LocationNeedle> geoLocationNeedles;

	public LocationMapView() {
		initGui();
		cfw = CasefolderWriter.getInstance();
		controller = new LocationMapController(this);
		geoLocationNeedles = new ArrayList<LocationNeedle>();
	}

	public void initGui() {
		mainPanel = new JPanel(new BorderLayout());
		positionLabel = new JLabel();
		instructionLabel = new JLabel("Right Mouse To Move, Mousewheel To Zoom", JLabel.RIGHT);
		mainPanel.add(positionLabel, BorderLayout.SOUTH);
		// mainPanel.add(instructionLabel, BorderLayout.NORTH);
		map = new JMapViewer();
		map.addMouseListener(this);
		map.addMouseMotionListener(this);
		mainPanel.add(map);
	}

	private void refreshPositions() {
		geoLocationNeedles.clear();
		map.removeAllMapMarkers();
		ArrayList<GeoLocation> geoLocations = cfw.getGeoLocations();
		if (geoLocations != null) {
			for (int i = 0; i < geoLocations.size(); i++) {
				GeoLocation loc = geoLocations.get(i);
				LocationNeedle locNee = new LocationNeedle(loc, Color.BLUE);
				map.addMapMarker(locNee);
				geoLocationNeedles.add(locNee);
			}
		}
	}

	@Override
	public String getName() {
		return "Location Map";
	}

	@Override
	public JComponent getView() {
		return mainPanel;
	}

	@Override
	public void triggered() {
		refreshPositions();
	}

	@Override
	public void reactToADBResult(String result, String[] executedCommands) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		for (int i = 0; i < geoLocationNeedles.size(); i++) {
			LocationNeedle locNee = geoLocationNeedles.get(i);
			if (locNee.getRect().contains(e.getPoint())) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection strSel = new StringSelection(locNee.toString());
				clipboard.setContents(strSel, null);
			}
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Coordinate c = map.getPosition(e.getPoint());
		String lat = c.getLat() + "";
		String lon = c.getLon() + "";
		positionLabel.setText("Latitude: " + lat + ", " + "Longitude: " + lon);

		for (int i = 0; i < geoLocationNeedles.size(); i++) {
			LocationNeedle locNee = geoLocationNeedles.get(i);
			if (locNee.getRect().contains(e.getPoint())) {
				positionLabel.setText("Source: " + locNee.getSource() + ", Time: " + locNee.getTimestamp());
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// not used
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// not used
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// not used
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// not used
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// not used
	}

}
