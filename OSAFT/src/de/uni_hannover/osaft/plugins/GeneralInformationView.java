package de.uni_hannover.osaft.plugins;

import java.awt.TextField;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;

@PluginImplementation
public class GeneralInformationView implements ViewPlugin {

	private JPanel panel;
	
	@Init
	public void init() {
		panel = new JPanel();
		panel.add(new JButton("load information"));
	}
	
	@Override
	public String getName() {
		return "General Information";
	}

	@Override
	public JPanel getView() {
		return panel;		
	}

}
