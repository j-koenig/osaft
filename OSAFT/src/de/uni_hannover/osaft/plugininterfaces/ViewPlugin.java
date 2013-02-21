package de.uni_hannover.osaft.plugininterfaces;

import java.io.File;

import javax.swing.JComponent;

import net.xeoh.plugins.base.Plugin;
import de.uni_hannover.osaft.adb.ADBThread;

public interface ViewPlugin extends Plugin {

	//has to return the name of the plugin
	public String getName();
	
	//has to return the jcomponent which will be visible 
	public JComponent getView();
	
	//called, everytime the button for this plugin was clicked
	public void triggered();
	
	//called, when plugin is initialized at application start
	public void init();
	
	//enables acces to the adbthread
	public void setADBThread(ADBThread adb);
	
	//sets current case folder. needed if plugin wants to store something
	public void setCaseFolder(File caseFolder);
	
	//when adb thread is ready, this method is called to update the gui after calling ADBThread.executeAndReturn() 
	public void reactToADBResult(String result, String[] executedCommands);
	
}
