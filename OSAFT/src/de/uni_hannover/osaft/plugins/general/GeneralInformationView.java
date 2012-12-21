package de.uni_hannover.osaft.plugins.general;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
	public JComponent getView() {
		return panel;		
	}

	@Override
	public void triggered() {
		System.out.println("general wurde gewählt");
	}

}
