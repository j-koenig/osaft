package de.uni_hannover.osaft;

import java.io.File;

import javax.swing.UIManager;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.uni_hannover.osaft.view.View;

public class Main {

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}

		PluginManager pm = PluginManagerFactory.createPluginManager();
		pm.addPluginsFrom(new File("bin/").toURI());
		PluginManagerUtil pmu = new PluginManagerUtil(pm);
		View frame = new View("OSAFT", pmu);
		frame.setVisible(true);
	}

}
