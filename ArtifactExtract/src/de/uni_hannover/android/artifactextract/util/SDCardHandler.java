package de.uni_hannover.android.artifactextract.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import de.uni_hannover.android.artifactextract.artifacts.Artifact;

/**
 * Provides static methods to access the SD card
 * 
 * @author Jannis Koenig
 * 
 */
public class SDCardHandler {

	/**
	 * 
	 * @return true if SD card is already mounted
	 */
	public static boolean isMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 
	 * @return true if SD card is in status "shared"
	 */
	public static boolean isShared() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED);
	}

	/**
	 * 
	 * @return true if no SD card is inserted
	 */
	public static boolean isRemoved() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED);
	}

	/**
	 * creates a directory on the sd card
	 * 
	 * @param directory
	 *            name for new directory
	 * 
	 */
	public static void mkDir(String directory) throws IOException {
		File dir = new File(directory);
		if (!dir.exists()) {
			Log.d("SDCardHandler", "creating directory " + directory);
			if (!dir.mkdirs()) {
				throw new IOException();
			}
		}
	}

	/**
	 * iterates through the given ArrayList and calls the getCSV() method for
	 * each element in the list. The results will be written to the file
	 * <type>.csv
	 * 
	 */
	public static void writeCSV(String directory, String type, ArrayList<Artifact> artifactList) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(directory, type + ".csv")));

		for (Artifact artifact : artifactList) {
			writer.write(artifact.getCSV());
			writer.newLine();
		}
		writer.close();
	}

	/**
	 * saves the given bitmap as a jpeg into the given directory
	 * 
	 */
	public static void savePicture(String directory, Bitmap bitmap) throws IOException {
		FileOutputStream picFile = new FileOutputStream(directory);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picFile);
		picFile.flush();
		picFile.close();
	}

}
