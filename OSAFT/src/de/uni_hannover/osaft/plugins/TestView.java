package de.uni_hannover.osaft.plugins;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;

@PluginImplementation
public class TestView implements ViewPlugin {

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public JPanel getView() {
		JButton b = new JButton("Testbutton");
		JPanel p = new JPanel();
		p.add(b);
		return p;
	}

}
