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

public class SDCardHandler {

	public static boolean isMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static boolean isShared() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_SHARED);
	}

	public static boolean isRemoved() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED);
	}

	// creates a directory on the sd card
	public static boolean mkDir(String directory) {
		File dir = new File(directory);
		if (!dir.exists()) {
			Log.d("SDCardHandler", "creating directory " + directory);
			return dir.mkdirs();
		}
		return true;
	}

	// iterates through the given ArrayList and calls the getCSV() method for
	// each element in the list. The results will be written to the file
	// <type>.csv
	public static void writeCSV(String directory, String type, ArrayList<Artifact> artifactList)
			throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(
				new File(directory, type + ".csv")));
		for (Artifact artifact : artifactList) {
			writer.write(artifact.getCSV());
			writer.newLine();
		}
		writer.close();
	}

	// saves the given bitmap as a jpeg into the given directory
	public static void savePicture(String directory, Bitmap bitmap) throws IOException {
		FileOutputStream picFile = new FileOutputStream(directory);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, picFile);
		picFile.flush();
		picFile.close();
	}

}
