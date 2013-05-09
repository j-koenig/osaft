package de.uni_hannover.osaft.plugins.testplugin;

import javax.swing.JButton;
import javax.swing.JComponent;

import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;

//@PluginImplementation
public class TestPlugin implements ViewPlugin {

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public JComponent getView() {
		return new JButton("Test");
	}

	@Override
	public void triggered() {
		// do nothing
	}

	@Override
	public void reactToADBResult(String result, String[] executedCommands) {
		// do nothing
	}

}
