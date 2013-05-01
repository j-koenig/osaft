package de.uni_hannover.osaft.plugininterfaces;

import javax.swing.JComponent;

import net.xeoh.plugins.base.Plugin;
import de.uni_hannover.osaft.adb.ADBThread;

/**
 * Interface for all ViewPlugins. These plugins are represented by a JButton and
 * JComponent in the main window
 * 
 * @author Jannis Koenig
 * 
 */
public interface ViewPlugin extends Plugin {

	/**
	 * has to return the name of the plugin
	 */
	public String getName();

	/**
	 * has to return the {@link JComponent} which will be visible in the main
	 * view
	 */
	public JComponent getView();

	/**
	 * called, everytime the button for this plugin is clicked
	 */
	public void triggered();

	/**
	 * when {@link ADBThread} is ready, this method is called to update the GUI
	 * after calling {@link ADBThread.executeAndReturn()}.
	 * {@link executedCommands} can be used to verify which commands where
	 * processed
	 */
	public void reactToADBResult(String result, String[] executedCommands);

}
