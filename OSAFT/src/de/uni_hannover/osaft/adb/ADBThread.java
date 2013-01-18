package de.uni_hannover.osaft.adb;

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

	private ADBThread() {
		commands = new LinkedBlockingQueue<ADBSwingWorker>();
	}

	@Override
	public void run() {
		while (true) {
			try {
				// Blocking queue blocks thread until something is added
				ADBSwingWorker asw = commands.take();
				//TODO: möglichkeit prozess zu unterbrechen?
				asw.execute();
				//wait for current swingworker (get() blocks the thread until swingworker is done)
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
