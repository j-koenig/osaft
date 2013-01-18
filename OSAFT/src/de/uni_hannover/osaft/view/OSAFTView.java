package de.uni_hannover.osaft.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.uni_hannover.osaft.controller.OSAFTController;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;

//TODO: config file für pfad zu adb; adb auf pc, mac linux ausführbar machen
//TODO: einstellungsmenü für pfad zu adb ändern
//TODO: aktuellen casefolder irgendwo anzeigen (vllt im gleichen panel wie aktuell ausgewähltes phone)

public class OSAFTView extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private OSAFTController c;
	private JPanel viewPanel, buttonPanel;
	private JScrollPane scrollButtonPane;
	private ArrayList<ViewPlugin> viewPluginList;
	private ArrayList<JButton> pluginButtonList;
	private PluginManagerUtil pmu;
	private CardLayout viewPanelLayout;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnEdit;
	private JMenuItem mntmExit;
	final private JDialog progressDialog;

	public OSAFTView(String title, PluginManagerUtil pmu) {
		super(title);
		this.pmu = pmu;
		progressDialog = new JDialog(this, "Executing ADB command", false);		
		viewPluginList = new ArrayList<ViewPlugin>();
		pluginButtonList = new ArrayList<JButton>();
		
		initGUI();
		c = new OSAFTController(this, pmu);
	}

	private void initGUI() {
				
	    JProgressBar dpb = new JProgressBar();
	    dpb.setIndeterminate(true);
	    progressDialog.add(BorderLayout.CENTER, dpb);
	    progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    progressDialog.setSize(300, 10);
		progressDialog.setLocationRelativeTo(null);			

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		getContentPane().setLayout(new BorderLayout(0, 0));
		buttonPanel = new JPanel();
		scrollButtonPane = new JScrollPane(buttonPanel);
		getContentPane().add(scrollButtonPane, BorderLayout.WEST);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		

		viewPanelLayout = new CardLayout();
		viewPanel = new JPanel(viewPanelLayout);
		getContentPane().add(viewPanel, BorderLayout.CENTER);

		menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);

		mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		ArrayList<ViewPlugin> plugins = new ArrayList<ViewPlugin>(pmu.getPlugins(ViewPlugin.class));
		Collections.sort(plugins, new Comparator<ViewPlugin>() {
			public int compare(ViewPlugin p1, ViewPlugin p2) {
				return p1.getName().compareTo(p2.getName());
			}
		});

		for (Iterator<ViewPlugin> iterator = plugins.iterator(); iterator.hasNext();) {
			ViewPlugin vp = (ViewPlugin) iterator.next();
			viewPluginList.add(vp);
			JButton b = new JButton(vp.getName());
			pluginButtonList.add(b);
			b.addActionListener(this);
			b.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) b.getMaximumSize().getHeight()));
			buttonPanel.add(b);
			// adds all plugins to the cardlayout of viewPanel
			// TODO: falls plugin nicht richtig funktioniert: nicht hinzufügen!
			// TODO: reihenfolge sollte immer die gleiche sein
			viewPanel.add(vp.getView(), String.valueOf(viewPluginList.indexOf(vp)));

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mntmExit) {
			System.exit(0);
		} else {
			JButton b = (JButton) e.getSource();
			int index = pluginButtonList.indexOf(b);
			viewPanelLayout.show(viewPanel, String.valueOf(index));
			viewPluginList.get(index).triggered();
			//c.dumpsys();
			// c.logcat();
		}
	}
	
	public JDialog getProgressDialog() {
		return progressDialog;
	}

}
