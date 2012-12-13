package de.uni_hannover.osaft.controller;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;

public class Controller {
	Runtime runtime;
	String caseFolder;

	public Controller() {
		runtime = Runtime.getRuntime();
		caseFolder = "/home/jannis/tmp";
	}

	public boolean testForSu() {
		try {
			String line;
			Process p = runtime.exec("adb shell");
			
			BufferedOutputStream bufferout = new BufferedOutputStream(p.getOutputStream());
			PrintWriter commandInput = new PrintWriter((new OutputStreamWriter(bufferout)), true);
			commandInput.println("su");
			commandInput.println("logcat -d");
			commandInput.println("exit");
			commandInput.println("exit");
			commandInput.close();
			bufferout.close();
			
			Reader r = new InputStreamReader(p.getInputStream());
			BufferedReader in = new BufferedReader(r);

			while ((line = in.readLine()) != null)
				System.out.println(line);
			in.close();

			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void copySDCard() {
		String mountPoint = null;
		try {
			String line;
			Process p = runtime.exec("adb shell mount");
			Reader r = new InputStreamReader(p.getInputStream());
			BufferedReader in = new BufferedReader(r);

			while ((line = in.readLine()) != null) {
				if(line.contains("sdcard")) {
					String[] lineSplit = line.split(" ");
					for (int i = 0; i < lineSplit.length; i++) {
						if(lineSplit[i].contains("sdcard")) {
							mountPoint = lineSplit[i];
							break;
						}
					}
					break;
				}
			}
			System.out.println(mountPoint);
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mountPoint == null) {
			System.out.println("ERROR! No mountpoint found");
			return;
		}
		
		System.out.println("copying sdcard content...");
		try {
			Process p = runtime.exec("adb pull " + mountPoint + " " + caseFolder + "/sdcard/");
			Reader r = new InputStreamReader(p.getInputStream());
			BufferedReader in = new BufferedReader(r);
			String line;

			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done!");
	}
	
	public void logcat() {
		try {
			String line;
			Process p = runtime.exec("adb logcat -d");
			Reader r = new InputStreamReader(p.getInputStream());
			BufferedReader in = new BufferedReader(r);

			while ((line = in.readLine()) != null)
				System.out.println(line);
			in.close();		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
