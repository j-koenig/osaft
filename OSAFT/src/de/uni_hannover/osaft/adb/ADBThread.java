package de.uni_hannover.osaft.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

public class ADBThread implements Runnable {

	private BlockingQueue<ADBSwingWorker> commands;
	private OSAFTView view;
	private String adbExecutable;
	// Singleton:
	private static ADBThread instance;
	private Runtime rt;

	private ADBThread() {
		commands = new LinkedBlockingQueue<ADBSwingWorker>();
		rt = Runtime.getRuntime();
	}

	@Override
	public void run() {
		while (true) {
			try {
				// Blocking queue blocks thread until something is added
				ADBSwingWorker asw = commands.take();
				// TODO: möglichkeit prozess zu unterbrechen?
				asw.execute();
				// wait for current swingworker (get() blocks the thread until
				// swingworker is done)
				asw.get();
				System.out.println("jaaa!");
			} catch (InterruptedException e) {
				// TODO: handle exception
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	// TODO: vor ausführen checken, ob device ausgewählt is
	// TODO: unterscheidung zwischen win und unix (nötig?)
	// TODO: eine methode, die es erlaubt auf der shell zu arbeiten
	// TODO: checken ob pfad zu adb richtig is (also adb funktioniert)

	public ADBSwingWorker executeAndReturn(String cmd, ViewPlugin plugin) {

		// idee hier: swingworker wird in adbthread queue eingereiht und dort
		// ausgeführt. wenn die aufgabe erfüllt ist, wird die methode done() im
		// swingworker aufgerufen. diese gibt dann den rückgabewert des
		// doInBackground() an das übergebene plugin weiter (über die methode
		// reactToADBResult())

		ADBSwingWorker asw = new ADBSwingWorker(cmd, adbExecutable, view, plugin);
		commands.add(asw);
		return asw;

	}

	// public void execute(String cmd) {
	// commands.add(cmd);
	// System.out.println("added command!");
	// }

	public ArrayList<String> getDevices() {
		Process p;
		ArrayList<String> output = new ArrayList<String>();
		try {
			p = rt.exec(adbExecutable + " devices");
			Reader r = new InputStreamReader(p.getInputStream());
			BufferedReader in = new BufferedReader(r);
			String line;
			
			//skip daemon-status messages:
			while(!(line = in.readLine()).contains("List of devices attached"))	{}
			while ((line = in.readLine()) != null) {
				if (!line.trim().equals("")) {
					String[] split = line.split("\\s+");
					output.add(split[0]);
				}
			}			
			p.waitFor();

			return output;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public static ADBThread getInstance() {
		if (instance == null) {
			instance = new ADBThread();
		}
		return instance;
	}

	public void setView(OSAFTView view) {
		this.view = view;
	}

	public void setADBExecutable(String path) {
		adbExecutable = path;
	}

}
