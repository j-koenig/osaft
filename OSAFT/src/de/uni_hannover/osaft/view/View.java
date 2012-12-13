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

public class View extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private Controller c;
	private JPanel viewPanel, buttonPanel;
	private JScrollPane scrollButtonPane;
	private ArrayList<ViewPlugin> viewPluginList;
	private ArrayList<JButton> pluginButtonList;
	private PluginManagerUtil pmu;
	private CardLayout viewPanelLayout;

	public View(PluginManagerUtil pmu) {
		this.pmu = pmu;
		c = new Controller();
		viewPluginList = new ArrayList<ViewPlugin>();
		pluginButtonList = new ArrayList<JButton>();
		initGUI();
	}

	private void initGUI() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		buttonPanel = new JPanel();
		scrollButtonPane = new JScrollPane(buttonPanel);
		getContentPane().add(scrollButtonPane);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		viewPanelLayout = new CardLayout();
		viewPanel = new JPanel(viewPanelLayout);
		getContentPane().add(viewPanel);

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
			viewPanel.add(vp.getView(), String.valueOf(viewPluginList.indexOf(vp)));
			System.out.println(String.valueOf(viewPluginList.indexOf(vp)));

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		viewPanelLayout.show(viewPanel, String.valueOf(pluginButtonList.indexOf(b)));
	}
}
