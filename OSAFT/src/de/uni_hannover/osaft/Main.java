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
package de.uni_hannover.osaft;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.uni_hannover.osaft.util.CasefolderWriter;
import de.uni_hannover.osaft.view.OSAFTView;

/**
 * Main class. Sets look and feel and initializes the jspf
 * 
 * @author Jannis Koenig
 * 
 */
public class Main {
	private static final Logger log = Logger.getLogger(Main.class.getName());
	public static FileHandler fh;

	public static void main(String[] args) {

		// initialize jspf:
		PluginManager pm = PluginManagerFactory.createPluginManager();

		String classpath = System.getProperty("java.class.path");
		int jarPos = classpath.indexOf("OSAFT.jar");
		int jarPathPos = classpath.lastIndexOf(File.pathSeparatorChar, jarPos) + 1;
		// if packed as executable jar, check for plugins folder, add the
		// containing plugins and initialize logging filehandler in jarfolder
		try {
			if (jarPos != -1) {
				String path = classpath.substring(jarPathPos, jarPos);
				CasefolderWriter.getInstance().setJarFolder(new File(path));
				// if pluginfolder does not exist: create it
				File pluginFolder = new File(path + "plugins/");
				if (!pluginFolder.exists()) {
					pluginFolder.mkdir();
				}
				pm.addPluginsFrom(pluginFolder.toURI());
				fh = new FileHandler(path + "osaft.log");
				fh.setFormatter(new SimpleFormatter());
			} else {
				pm.addPluginsFrom(new File("plugins/").toURI());
				fh = new FileHandler("osaft.log");
				fh.setFormatter(new SimpleFormatter());
			}
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		log.addHandler(fh);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.log(Level.WARNING, e.toString(), e);
		}

		// FIXME maybe it works also like this:
		// File pluginFolder = new File("plugins/");
		// if (!pluginFolder.exists()) {
		// pluginFolder.mkdir();
		// }
		// pm.addPluginsFrom(pluginFolder.toURI());

		try {
			pm.addPluginsFrom(new URI("classpath://*"));
		} catch (URISyntaxException e) {
			log.log(Level.WARNING, e.toString(), e);
		}
		final PluginManagerUtil pmu = new PluginManagerUtil(pm);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				OSAFTView frame = new OSAFTView("OSAFT", pmu);
				frame.setVisible(true);
			}
		});
	}
}
