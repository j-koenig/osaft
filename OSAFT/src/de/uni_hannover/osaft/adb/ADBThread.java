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
package de.uni_hannover.osaft.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

/**
 * This class regulates the access to the adb binary. It contains a
 * {@link BlockingQueue} which stores all commands that should be executed. The
 * thread waits until a command is added and runs this command. Newer commands
 * will be queued and executed after the current job is finished. Implements the
 * singleton-pattern (no public constructor; just one instance of this class)
 * 
 * @author Jannis Koenig
 * 
 */
public class ADBThread implements Runnable {

	private BlockingQueue<ADBSwingWorker> commands;
	private OSAFTView view;
	private String adbExecutable, currentDevice;
	// Singleton:
	private static ADBThread instance;
	private Runtime rt;
	
	private static final Logger log =  Logger.getLogger(ADBThread.class.getName());

	private ADBThread() {
		commands = new LinkedBlockingQueue<ADBSwingWorker>();
		rt = Runtime.getRuntime();
	}

	/**
	 * When started, the thread waits for an adb command and starts an
	 * {@link ADBSwingWorker}. The get()-method of the {@link ADBSwingWorker}
	 * blocks this thread until the job is finished.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// Blocking queue blocks thread until something is added
				ADBSwingWorker asw = commands.take();
				asw.execute();
				// wait for current swingworker (get() blocks the thread until
				// swingworker is done):
				asw.get();
			} catch (InterruptedException e) {
				log.log(Level.WARNING, e.toString(), e);
			} catch (Exception e) {
				log.log(Level.WARNING, e.toString(), e);
			}

		}

	}
	/**
	 * This methods provides the possibility to execute a single adb command. It
	 * queues the command in the {@link BlockingQueue} and returns the result to
	 * the given {@link ViewPlugin}. Shows a progressbar if showProgressBar is
	 * set to true
	 * 
	 * @param cmd
	 *            the adb command
	 * @param plugin
	 *            the {@link ViewPlugin} to return the result
	 * @param showProgressBar
	 *            if true a progressbar will be visible for the time the command
	 *            is executed
	 * @return the result of the command
	 */
	public ADBSwingWorker executeAndReturn(String cmd, ViewPlugin plugin, boolean showProgressBar) {
		String[] command = new String[1];
		command[0] = cmd;
		ADBSwingWorker asw = new ADBSwingWorker(command, adbExecutable, currentDevice, view, plugin, showProgressBar);
		commands.add(asw);
		return asw;
	}

	/**
	 * This method provides the possibility to interact with the adb shell. The
	 * commands from the [@link String}-Array cmds will be processed one after
	 * another and the last command has to be the {@link String} "exit" to exit
	 * the adb shell.
	 * 
	 * @param cmds
	 *            the commands to execute
	 * @param plugin
	 *            the {@link ViewPlugin} to return the result
	 * @param showProgressBar
	 *            if true a progressbar will be visible for the time the command
	 *            is executed
	 * @return the result of the command
	 */
	public ADBSwingWorker interactWithShell(String[] cmds, ViewPlugin plugin, boolean showProgressBar) {
		ADBSwingWorker asw = new ADBSwingWorker(cmds, adbExecutable, currentDevice, view, plugin, showProgressBar);
		commands.add(asw);
		return asw;
	}

	/**
	 * This method returns the IDs of the attached devices.
	 * 
	 */
	public ArrayList<String> getDevices() {
		Process p;
		ArrayList<String> output = new ArrayList<String>();
		try {
			// use the runtime to execute the adb command:
			p = rt.exec(adbExecutable + " devices");
			Reader r = new InputStreamReader(p.getInputStream());
			BufferedReader in = new BufferedReader(r);
			String line;

			// skip daemon-status messages:
			while (!(line = in.readLine()).contains("List of devices attached")) {
			}
			while ((line = in.readLine()) != null) {
				if (!line.trim().equals("")) {
					String[] split = line.split("\\s+");
					output.add(split[0]);
				}
			}
			p.waitFor();

			return output;
		} catch (IOException e) {
			log.log(Level.WARNING, e.toString(), e);
		} catch (InterruptedException e) {
			log.log(Level.WARNING, e.toString(), e);
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

	public void setCurrentDevice(String curDev) {
		currentDevice = curDev;
	}
	
	public void setFileHandler(FileHandler fh) {
		log.addHandler(fh);
	}

}
