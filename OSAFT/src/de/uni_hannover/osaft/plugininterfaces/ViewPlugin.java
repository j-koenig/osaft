package de.uni_hannover.osaft.plugininterfaces;

import javax.swing.JComponent;

import net.xeoh.plugins.base.Plugin;

public interface ViewPlugin extends Plugin {

	public String getName();
	
	public JComponent getView();
	
	public void triggered();
	
	public void init();
}
