package de.uni_hannover.osaft.adb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
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

	public ADBSwingWorker(String[] commands, String adbExecutable, String currentDevice, OSAFTView view, ViewPlugin plugin) {
		this.commands = commands;
		this.view = view;
		this.adbExecutable = adbExecutable;
		this.plugin = plugin;
		this.currentDevice = currentDevice;
		rt = Runtime.getRuntime();
	}

	@Override
	// TODO: kann nich einfach exception werfen
	protected String doInBackground() throws Exception {
		System.out.println("Ich tue was");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				view.getProgressDialog().setVisible(true);
			}
		});

		String output = "";
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
		} else {
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

		return output;

	}

	@Override
	public void done() {
		System.out.println("fertig");
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
