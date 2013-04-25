package de.uni_hannover.osaft.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.util.CasefolderWriter;
import de.uni_hannover.osaft.view.OSAFTView;

/**
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

	public OSAFTController(OSAFTView view) {
		this.view = view;
		fc = new JFileChooser();
		adb = ADBThread.getInstance();
		cfw = CasefolderWriter.getInstance();
		cfw.setView(view);
		initProperties();
		adb.setView(view);
		// start the ADBThread
		Thread adbThread = new Thread(adb);
		adbThread.start();
	}

	private void initProperties() {
		// initialize the properties file:
		properties = new Properties();
		File f = new File("osaft.properties");
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			properties.load(new FileInputStream(f));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			view.setCurrentCaseText(possibleCaseFolder);
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
			cfw.setCaseFolder(caseFolder);
			cfw.mkDir("contact_photos");
			cfw.mkDir("gmail");
			cfw.mkDir("databases" + File.separator + "twitter");
			cfw.mkDir("gmail");
			cfw.mkDir("mms_parts");
			cfw.mkDir("whatsapp");
			view.setCurrentCaseText(caseFolder.toString());
			properties.setProperty("casefolder", caseFolder.toString());
			storeProperties();
		}
	}

	private void storeProperties() {
		try {
			properties.store(new FileOutputStream(new File("osaft.properties")), "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
