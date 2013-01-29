package de.uni_hannover.osaft.adb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

public class ADBSwingWorker extends SwingWorker<String, Object> {

	private String command, adbExecutable, currentDevice;
	private Runtime rt;
	private OSAFTView view;
	private ViewPlugin plugin;

	public ADBSwingWorker(String command, String adbExecutable, String currentDevice,
			OSAFTView view, ViewPlugin plugin) {
		this.command = command;
		this.view = view;
		this.adbExecutable = adbExecutable;
		this.plugin = plugin;
		this.currentDevice = currentDevice;
		rt = Runtime.getRuntime();
	}

	@Override
	protected String doInBackground() throws Exception {
		System.out.println("Ich tue was");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.getProgressDialog().setVisible(true);
			}
		});

		Process p = rt.exec(adbExecutable + " -s " + currentDevice + " " + command);

		Reader r = new InputStreamReader(p.getInputStream());
		BufferedReader in = new BufferedReader(r);
		String line;
		String output = "";
		while ((line = in.readLine()) != null) {
			output += line + "\n";
		}

		// TODO: errorcode irgendwie dem viewplugin übergeben und der kann dann
		// damit was machen

		int exit = p.waitFor();
		System.out.println(exit);

		return output;

	}

	@Override
	public void done() {
		System.out.println("fertig");
		view.getProgressDialog().setVisible(false);
		try {
			plugin.reactToADBResult(get(), command);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
