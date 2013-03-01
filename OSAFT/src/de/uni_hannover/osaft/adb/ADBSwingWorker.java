package de.uni_hannover.osaft.adb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

public class ADBSwingWorker extends SwingWorker<String, Object> {

	private String[] commands;
	private String adbExecutable, currentDevice;
	private Runtime rt;
	private OSAFTView view;
	private ViewPlugin plugin;
	private int exitCode;
	private boolean showProgressBar;

	public ADBSwingWorker(String[] commands, String adbExecutable, String currentDevice, OSAFTView view, ViewPlugin plugin,
			boolean showProgressBar) {
		this.commands = commands;
		this.view = view;
		this.adbExecutable = adbExecutable;
		this.plugin = plugin;
		this.currentDevice = currentDevice;
		this.showProgressBar = showProgressBar;
		rt = Runtime.getRuntime();
	}

	@Override
	protected String doInBackground() {
		String output = "";
		try {
			if (showProgressBar) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						view.getProgressDialog().setVisible(true);
					}
				});
			}
			// interaction with shell:
			if (commands.length > 1) {
				String line;
				Process p = rt.exec(adbExecutable + " -s " + currentDevice + " shell");

				BufferedOutputStream bufferout = new BufferedOutputStream(p.getOutputStream());
				PrintWriter commandInput = new PrintWriter((new OutputStreamWriter(bufferout)), true);
				for (int i = 0; i < commands.length; i++) {
					commandInput.println(commands[i]);
				}
				commandInput.close();
				bufferout.close();

				Reader r = new InputStreamReader(p.getInputStream());
				BufferedReader in = new BufferedReader(r);

				while ((line = in.readLine()) != null) {
					output += line + "\n";
				}
				in.close();
				r.close();
				exitCode = p.waitFor();
			}
			// only one command:
			else {
				Process p = rt.exec(adbExecutable + " -s " + currentDevice + " " + commands[0]);
				Reader r = new InputStreamReader(p.getInputStream());
				BufferedReader in = new BufferedReader(r);
				String line;
				while ((line = in.readLine()) != null) {
					output += line + "\n";
				}
				in.close();
				r.close();
				exitCode = p.waitFor();
			}
		} catch (IOException e) {
			//TODO
		} catch (InterruptedException e) {
			//TODO
		}
		return output;
	}

	@Override
	public void done() {
		view.getProgressDialog().setVisible(false);
		try {
			plugin.reactToADBResult(get(), commands);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getExitCode() {
		return exitCode;
	}

}
