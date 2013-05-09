package de.uni_hannover.osaft.plugins.locationmap.controller;

import de.uni_hannover.osaft.plugins.locationmap.view.LocationMapView;

//TODO: add functionality to parse "logcat -b radio" for cell informarion

public class LocationMapController {
	
	private LocationMapView view;
	
	public LocationMapController(LocationMapView view) {
		this.view = view;
	}

}
