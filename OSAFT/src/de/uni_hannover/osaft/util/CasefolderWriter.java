package de.uni_hannover.osaft.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

public class CasefolderWriter {

	private static CasefolderWriter instance;
	private File casefolder;
	private ADBThread adb;
	private OSAFTView view;
	private ArrayList<GeoLocation> geoLocations;

	private CasefolderWriter() {
		adb = ADBThread.getInstance();
	}

	public void writeRawImage(byte[] rawImage, String filename, String subfolder) {
		if (casefolder == null) {
			showWarning();
			return;
		}
		try {
			InputStream in = new ByteArrayInputStream(rawImage);
			BufferedImage photo = ImageIO.read(in);
			ImageIO.write(photo, "png", new File(casefolder + File.separator + subfolder + filename + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pullFileToCaseFolder(String file, String subfolder, ViewPlugin view, boolean progressBar) {
		if (casefolder == null) {
			showWarning();
			return;
		}
		adb.executeAndReturn("pull " + file + " " + casefolder + File.separator + subfolder, view, progressBar);
	}

	public void pullFileToCaseFolder(String file, ViewPlugin view, boolean progressBar) {
		if (casefolder == null) {
			showWarning();
			return;
		}
		adb.executeAndReturn("pull " + file + " " + casefolder, view, progressBar);
	}

	public void mkDir(String dir) {
		if (casefolder == null) {
			showWarning();
			return;
		}
		new File(casefolder.getAbsolutePath() + File.separator + dir).mkdirs();
	}

	public File getCaseFolder() {
		if (casefolder == null) {
			showWarning();
		}
		return casefolder;
	}

	public void setCaseFolderAndCreateSubfolders(File casefolder) {
		setCaseFolder(casefolder);
		mkDir("contact_photos");
		mkDir("gmail");
		mkDir("databases" + File.separator + "twitter");
		mkDir("gmail");
		mkDir("mms_parts");
		mkDir("whatsapp");
		mkDir("facebook");
	}

	public void setCaseFolder(File casefolder) {
		this.casefolder = casefolder;
		view.setCurrentCaseText(casefolder.toString());
		try {
			loadLocationObject();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadLocationObject() throws FileNotFoundException, IOException, ClassNotFoundException {
		File locationsFile = new File(casefolder.getAbsolutePath() + File.separator + "locations.object");
		if (locationsFile.exists()) {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(locationsFile)));
			geoLocations = (ArrayList<GeoLocation>) ois.readObject();
			ois.close();
		} else {
			geoLocations = new ArrayList<GeoLocation>();
		}
	}

	private void saveLocationObject() throws FileNotFoundException, IOException {
		File locationsFile = new File(casefolder.getAbsolutePath() + File.separator + "locations.object");
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(locationsFile)));
		oos.writeObject(geoLocations);
		oos.close();
	}

	public void addLocation(GeoLocation loc) {
		geoLocations.add(loc);
		try {
			saveLocationObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}

	}

	public ArrayList<GeoLocation> getGeoLocations() {
		return geoLocations;
	}

	public void setView(OSAFTView view) {
		this.view = view;
	}

	private void showWarning() {
		JOptionPane.showMessageDialog(view, "No casefolder set. Please chose a casefolder!", "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static CasefolderWriter getInstance() {
		if (instance == null) {
			instance = new CasefolderWriter();
		}
		return instance;
	}

}
