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
package de.uni_hannover.osaft.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.uni_hannover.osaft.Main;
import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.util.CasefolderWriter;
import de.uni_hannover.osaft.view.OSAFTView;

/**
 * Corresponding controller to {@link OSAFTView}
 * 
 * @author Jannis Koenig
 * 
 */
public class OSAFTController {
	private ADBThread adb;
	private Properties properties;
	private JFileChooser fc;
	private OSAFTView view;
	private String currentDevice;
	private CasefolderWriter cfw;
	private File jarFolder;
	
	private static final Logger log = Logger.getLogger(OSAFTController.class.getName());

	public OSAFTController(OSAFTView view) {
		log.addHandler(Main.fh);
		this.view = view;
		fc = new JFileChooser();
		adb = ADBThread.getInstance();
		cfw = CasefolderWriter.getInstance();
		cfw.setView(view);
		jarFolder = cfw.getJarFolder();
		initProperties();
		adb.setView(view);
		// start the ADBThread
		Thread adbThread = new Thread(adb);
		adbThread.start();
	}

	private void initProperties() {
		// initialize the properties file:
		properties = new Properties();
		File f = (jarFolder == null) ? new File("osaft.properties") : new File(jarFolder.getAbsolutePath() + File.separator
				+ "osaft.properties");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			properties.load(new FileInputStream(f));
		} catch (IOException e) {
			log.log(Level.WARNING, e.toString(), e);
		}
		// if there is no entry for the adb executable, the user has to choose
		// the path to the binary
		if (properties.getProperty("adb") == null) {
			JOptionPane.showMessageDialog(view,
					"Properties file does not contain the path to adb! Please choose the path to the adb executable", "Information",
					JOptionPane.INFORMATION_MESSAGE);
			changeADBPath();
		}
		adb.setADBExecutable(properties.getProperty("adb"));

		String possibleCaseFolder = properties.getProperty("casefolder");
		if (possibleCaseFolder != null) {
			cfw.setCaseFolder(new File(possibleCaseFolder));
		}

	}

	/**
	 * Called if user wants to change the path to the adb executable
	 */
	public void changeADBPath() {
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fc.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.getName().equals("adb") && !file.getName().equals("adb.exe")) {
				JOptionPane.showMessageDialog(view, "Please choose a valid executable", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			properties.setProperty("adb", file.toString());
			storeProperties();
			adb.setADBExecutable(properties.getProperty("adb"));
		} else if (returnVal == JFileChooser.CANCEL_OPTION) {
			JOptionPane.showMessageDialog(view, "This application won't work without adb. Please choose a valid executable", "Warning",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * Called if user wants to change the current case folder
	 */
	public void changeCaseFolder() {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File caseFolder = fc.getSelectedFile();

			// initialize the chosen case folder (generate subfolders)
			cfw.setCaseFolderAndCreateSubfolders(caseFolder);
			properties.setProperty("casefolder", caseFolder.toString());
			storeProperties();
		}
	}

	private void storeProperties() {
		try {
			File f = (jarFolder == null) ? new File("osaft.properties") : new File(jarFolder.getAbsolutePath() + File.separator
					+ "osaft.properties");
			properties.store(new FileOutputStream(f), "");
		} catch (IOException e) {
			log.log(Level.WARNING, e.toString(), e);
		}
	}

	public String getCurrentDevice() {
		return currentDevice;
	}

	public void setCurrentDevice(String currentDevice) {
		this.currentDevice = currentDevice;
		adb.setCurrentDevice(currentDevice);
	}

	public ArrayList<String> getDevices() {
		return adb.getDevices();
	}

}
