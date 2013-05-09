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
package de.uni_hannover.osaft.plugins.locationmap.controller;

import de.uni_hannover.osaft.plugins.locationmap.view.LocationMapView;

//TODO: add functionality to parse "logcat -b radio" for cell informarion

public class LocationMapController {
	
	private LocationMapView view;
	
	public LocationMapController(LocationMapView view) {
		this.view = view;
	}

}
