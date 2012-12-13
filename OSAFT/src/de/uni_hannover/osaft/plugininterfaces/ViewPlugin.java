package de.uni_hannover.osaft.plugininterfaces;

import javax.swing.JPanel;

import net.xeoh.plugins.base.Plugin;

public interface ViewPlugin extends Plugin {

	public String getName();
	
	public JPanel getView();
}
