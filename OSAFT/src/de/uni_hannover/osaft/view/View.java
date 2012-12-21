package de.uni_hannover.osaft.view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.xeoh.plugins.base.util.PluginManagerUtil;
import de.uni_hannover.osaft.controller.Controller;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import javax.swing.JMenuBar;
import java.awt.BorderLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenu;
import java.awt.SystemColor;

public class View extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Controller c;
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

	public View(String title, PluginManagerUtil pmu) {
		super(title);
		this.pmu = pmu;
		c = new Controller();
		viewPluginList = new ArrayList<ViewPlugin>();
		pluginButtonList = new ArrayList<JButton>();
		initGUI();
	}

	private void initGUI() {

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

		for (Iterator<ViewPlugin> iterator = pmu.getPlugins(ViewPlugin.class).iterator(); iterator
				.hasNext();) {
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
		}
	}

}
