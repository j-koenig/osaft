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
package de.uni_hannover.osaft.plugins.testplugin;

import javax.swing.JButton;
import javax.swing.JComponent;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;

//@PluginImplementation
public class TestPlugin implements ViewPlugin {

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public JComponent getView() {
		return new JButton("Test");
	}

	@Override
	public void triggered() {
		// do nothing
	}

	@Override
	public void reactToADBResult(String result, String[] executedCommands) {
		// do nothing
	}

}
