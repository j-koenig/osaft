package de.uni_hannover.osaft.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

	public void pullFileToCaseFolder(String file, String subfolder, ViewPlugin view) {
		if (casefolder == null) {
			showWarning();
			return;
		}
		if (subfolder != null) {
			adb.executeAndReturn("pull " + file + " " + casefolder + File.separator + subfolder, view, false);
		} else {
			adb.executeAndReturn("pull " + file + " " + casefolder, view, false);
		}
	}

	public void mkDir(String dir) {
		if (casefolder == null) {
			showWarning();
			return;
		}
		new File(casefolder.getAbsolutePath() + File.separator + dir).mkdirs();
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

	public File getCaseFolder() {
		if (casefolder == null) {
			showWarning();
		}
		return casefolder;
	}

	public void setCaseFolder(File casefolder) {
		this.casefolder = casefolder;
	}

	public void setView(OSAFTView view) {
		this.view = view;
	}

}
