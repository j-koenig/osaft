package de.uni_hannover.osaft.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

public class OSAFTController {
	// private Runtime runtime;
	private File caseFolder;
	private ADBThread adb;
	private Properties properties;
	private JFileChooser fc;
	private OSAFTView view;
	private PluginManagerUtil pmu;
	private String currentDevice;

	public OSAFTController(OSAFTView view, PluginManagerUtil pmu) {
		this.view = view;
		this.pmu = pmu;
		// runtime = Runtime.getRuntime();
		fc = new JFileChooser();
		adb = ADBThread.getInstance();
		initProperties();

		for (Iterator<ViewPlugin> iterator = pmu.getPlugins(ViewPlugin.class).iterator(); iterator.hasNext();) {
			ViewPlugin plugin = (ViewPlugin) iterator.next();
			// TODO: exception fangen, wenn plugin nicht alle methoden vom
			// interface implementiert?
			plugin.setADBThread(adb);
		}
		adb.setView(view);
		Thread adbThread = new Thread(adb);
		adbThread.start();

		// System.out.println(adb.getDevices());

	}

	private void initProperties() {
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
			caseFolder = new File(possibleCaseFolder);
			// refresh path to casefolder for each plugin
			for (Iterator<ViewPlugin> iterator = pmu.getPlugins(ViewPlugin.class).iterator(); iterator.hasNext();) {
				ViewPlugin plugin = (ViewPlugin) iterator.next();
				plugin.setCaseFolder(caseFolder);
			}
		}

	}

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

	public void changeCaseFolder() {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			caseFolder = fc.getSelectedFile();

			File f = new File(caseFolder.getAbsolutePath() + File.separator + "contact_photos");
			f.mkdirs();
			f = new File(caseFolder.getAbsolutePath() + File.separator + "databases");
			f.mkdirs();
			f = new File(caseFolder.getAbsolutePath() + File.separator + "gmail");
			f.mkdirs();
			f = new File(caseFolder.getAbsolutePath() + File.separator + "mms_parts");
			f.mkdirs();
			f = new File(caseFolder.getAbsolutePath() + File.separator + "whatsapp");
			f.mkdirs();
			// TODO: unterordner struktur bauen
			// refresh path to casefolder for each plugin
			for (Iterator<ViewPlugin> iterator = pmu.getPlugins(ViewPlugin.class).iterator(); iterator.hasNext();) {
				ViewPlugin plugin = (ViewPlugin) iterator.next();
				plugin.setCaseFolder(caseFolder);
			}
			view.setCurrentCaseText(caseFolder.toString());
			properties.setProperty("casefolder", caseFolder.toString());
			storeProperties();
		}
	}

	// public boolean testForSu() {
	// try {
	// String line;
	// Process p = runtime.exec("adb shell");
	//
	// BufferedOutputStream bufferout = new
	// BufferedOutputStream(p.getOutputStream());
	// PrintWriter commandInput = new PrintWriter((new
	// OutputStreamWriter(bufferout)), true);
	// commandInput.println("su");
	// commandInput.println("logcat -d");
	// commandInput.println("exit");
	// commandInput.println("exit");
	// commandInput.close();
	// bufferout.close();
	//
	// Reader r = new InputStreamReader(p.getInputStream());
	// BufferedReader in = new BufferedReader(r);
	//
	// while ((line = in.readLine()) != null)
	// System.out.println(line);
	// in.close();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return false;
	// }
	//
	// public void copySDCard() {
	// String mountPoint = null;
	// try {
	// String line;
	// Process p = runtime.exec("adb shell mount");
	// Reader r = new InputStreamReader(p.getInputStream());
	// BufferedReader in = new BufferedReader(r);
	//
	// while ((line = in.readLine()) != null) {
	// if (line.contains("sdcard")) {
	// String[] lineSplit = line.split(" ");
	// for (int i = 0; i < lineSplit.length; i++) {
	// if (lineSplit[i].contains("sdcard")) {
	// mountPoint = lineSplit[i];
	// break;
	// }
	// }
	// break;
	// }
	// }
	// System.out.println(mountPoint);
	// in.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// if (mountPoint == null) {
	// System.out.println("ERROR! No mountpoint found");
	// return;
	// }
	//
	// System.out.println("copying sdcard content...");
	// try {
	// Process p = runtime.exec("adb pull " + mountPoint + " " + caseFolder +
	// "/sdcard/");
	// Reader r = new InputStreamReader(p.getInputStream());
	// BufferedReader in = new BufferedReader(r);
	// String line;
	//
	// while ((line = in.readLine()) != null) {
	// System.out.println(line);
	// }
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// System.out.println("done!");
	// }
	//
	// public void logcat() {
	// try {
	// String line;
	// Process p = runtime.exec(adbExecutable + " logcat -d");
	// Reader r = new InputStreamReader(p.getInputStream());
	// BufferedReader in = new BufferedReader(r);
	//
	// while ((line = in.readLine()) != null)
	// System.out.println(line);
	// in.close();
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	public void dumpsys() {

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
