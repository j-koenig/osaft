package de.uni_hannover.osaft;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.UIManager;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.uni_hannover.osaft.view.OSAFTView;

public class Main {

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}

		PluginManager pm = PluginManagerFactory.createPluginManager();

		String classpath = System.getProperty("java.class.path");
		int jarPos = classpath.indexOf("OSAFT.jar");		
		int jarPathPos = classpath.lastIndexOf(File.pathSeparatorChar, jarPos) + 1;
		//if packed as executable jar, check for plugins folder and add the containing plugins
		if (jarPos != -1) {
			String path = classpath.substring(jarPathPos, jarPos);
			//if pluginfolder does not exist: create it
			File pluginFolder = new File(path + "plugins/");
			pluginFolder.mkdir();
			pm.addPluginsFrom(pluginFolder.toURI());
		}		
		
		//FIXME:scheint auch einfach so zu funzen!
//		File pluginFolder = new File("plugins");
//		if (!pluginFolder.exists()) {
//			pluginFolder.mkdir();
//		}
//		pm.addPluginsFrom(new File("plugins").toURI());

		try {
			pm.addPluginsFrom(new URI("classpath://*"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PluginManagerUtil pmu = new PluginManagerUtil(pm);
		OSAFTView frame = new OSAFTView("OSAFT", pmu);
		frame.setVisible(true);
	}

}
