package de.uni_hannover.osaft.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
/**
 * Main view element. Partially edited with Googles WindowBuilder
 * 
 * @author Jannis Koenig
 * 
 */
public class OSAFTView extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private OSAFTController controller;
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
	private JLabel actualCurrentCaseLabel;
	private JButton refreshDevicesButton;
	private JComboBox devicesCombo;
	private JButton btnChange;

	public OSAFTView(String title, PluginManagerUtil pmu) {
		super(title);
		this.pmu = pmu;
		progressDialog = new JDialog(this, "Executing ADB command", false);
		viewPluginList = new ArrayList<ViewPlugin>();
		pluginButtonList = new ArrayList<JButton>();

		actualCurrentCaseLabel = new JLabel("No folder chosen");
		controller = new OSAFTController(this);
		initGUI();

		controller.setCurrentDevice(devicesCombo.getSelectedItem().toString());
	}

	private void initGUI() {

		JProgressBar dpb = new JProgressBar();
		dpb.setIndeterminate(true);
		progressDialog.getContentPane().add(BorderLayout.CENTER, dpb);
		progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		progressDialog.setSize(300, 10);
		progressDialog.setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		getContentPane().setLayout(new BorderLayout(0, 0));

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		scrollButtonPane = new JScrollPane(buttonPanel);

		// created with WindowBuilder START
		JPanel devicesPanel = new JPanel();
		devicesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		devicesPanel.setPreferredSize(new Dimension(0, 120));
		GridBagLayout gbl_devicesPanel = new GridBagLayout();
		gbl_devicesPanel.columnWidths = new int[] { 278, 0, 0 };
		gbl_devicesPanel.rowHeights = new int[] { 35, 20, 28, 38, 0 };
		gbl_devicesPanel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_devicesPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		devicesPanel.setLayout(gbl_devicesPanel);
		GridBagConstraints gbc_selectedPhoneLabel = new GridBagConstraints();
		gbc_selectedPhoneLabel.anchor = GridBagConstraints.SOUTH;
		gbc_selectedPhoneLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_selectedPhoneLabel.insets = new Insets(0, 0, 5, 5);
		gbc_selectedPhoneLabel.gridx = 0;
		gbc_selectedPhoneLabel.gridy = 0;
		JLabel selectedPhoneLabel = new JLabel("Selected phone:");
		devicesPanel.add(selectedPhoneLabel, gbc_selectedPhoneLabel);

		devicesCombo = new JComboBox();
		devicesCombo.addActionListener(this);
		GridBagConstraints gbc_devicesCombo = new GridBagConstraints();
		gbc_devicesCombo.fill = GridBagConstraints.BOTH;
		gbc_devicesCombo.insets = new Insets(0, 0, 5, 5);
		gbc_devicesCombo.gridx = 0;
		gbc_devicesCombo.gridy = 1;
		devicesPanel.add(devicesCombo, gbc_devicesCombo);

		fillDevicesComboBox();

		btnChange = new JButton("Change");
		btnChange.addActionListener(this);
		GridBagConstraints gbc_btnChange = new GridBagConstraints();
		gbc_btnChange.gridheight = 2;
		gbc_btnChange.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnChange.gridx = 1;
		gbc_btnChange.gridy = 2;
		devicesPanel.add(btnChange, gbc_btnChange);

		refreshDevicesButton = new JButton("Refresh");
		refreshDevicesButton.addActionListener(this);
		GridBagConstraints gbc_refreshDevicesButton = new GridBagConstraints();
		gbc_refreshDevicesButton.insets = new Insets(0, 0, 5, 0);
		gbc_refreshDevicesButton.gridx = 1;
		gbc_refreshDevicesButton.gridy = 1;
		devicesPanel.add(refreshDevicesButton, gbc_refreshDevicesButton);

		JLabel currentCaseLabel = new JLabel("Current case folder:");
		GridBagConstraints gbc_currentCaseLabel = new GridBagConstraints();
		gbc_currentCaseLabel.anchor = GridBagConstraints.SOUTH;
		gbc_currentCaseLabel.insets = new Insets(0, 0, 5, 5);
		gbc_currentCaseLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentCaseLabel.gridx = 0;
		gbc_currentCaseLabel.gridy = 2;
		devicesPanel.add(currentCaseLabel, gbc_currentCaseLabel);

		GridBagConstraints gbc_actualCurrentCaseLabel = new GridBagConstraints();
		gbc_actualCurrentCaseLabel.anchor = GridBagConstraints.NORTHWEST;
		gbc_actualCurrentCaseLabel.insets = new Insets(0, 0, 0, 5);
		gbc_actualCurrentCaseLabel.gridx = 0;
		gbc_actualCurrentCaseLabel.gridy = 3;
		devicesPanel.add(actualCurrentCaseLabel, gbc_actualCurrentCaseLabel);

		// created with WindowBuilder END
		JPanel buttonAndDevicesPanel = new JPanel(new BorderLayout());
		buttonAndDevicesPanel.setPreferredSize(new Dimension(300, 0));
		buttonAndDevicesPanel.add(devicesPanel, BorderLayout.NORTH);
		buttonAndDevicesPanel.add(scrollButtonPane, BorderLayout.CENTER);

		getContentPane().add(buttonAndDevicesPanel, BorderLayout.WEST);

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

		int generalInformationButtonPosition = 0;
		
		// iterate over different viewplugins and add a JButton and the
		// corresponding view to the cardlayouted viewPanel
		for (Iterator<ViewPlugin> iterator = plugins.iterator(); iterator.hasNext();) {
			ViewPlugin vp = (ViewPlugin) iterator.next();
			//checks on which poisition general information button is
			//TODO: hardcoded string compare is ugly; maybe IDs as identification for plugins?
			if(vp.getName().equals("General Information")) {
				generalInformationButtonPosition = pluginButtonList.size();
			}
			viewPluginList.add(vp);
			JButton b = new JButton(vp.getName());
			pluginButtonList.add(b);
			b.addActionListener(this);
			b.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) b.getMaximumSize().getHeight()));
			buttonPanel.add(b);
			// adds all plugins to the cardlayout of viewPanel
			viewPanel.add(vp.getView(), String.valueOf(viewPluginList.indexOf(vp)));
		}
		//shows general information tab on startup
//		viewPanelLayout.show(viewPanel, String.valueOf(generalInformationButtonPosition));
		pluginButtonList.get(generalInformationButtonPosition).doClick();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(mntmExit)) {
			System.exit(0);
		} else if (e.getSource().equals(refreshDevicesButton)) {
			fillDevicesComboBox();
		} else if (e.getSource().equals(devicesCombo)) {
			if (devicesCombo.getItemCount() != 0) {
				controller.setCurrentDevice(devicesCombo.getSelectedItem().toString());
			}
		} else if (e.getSource().equals(btnChange)) {
			controller.changeCaseFolder();
		} else {
			JButton b = (JButton) e.getSource();
			int index = pluginButtonList.indexOf(b);
			viewPanelLayout.show(viewPanel, String.valueOf(index));
			viewPluginList.get(index).triggered();
		}
	}

	public JDialog getProgressDialog() {
		return progressDialog;
	}

	private void fillDevicesComboBox() {
		ArrayList<String> devices = controller.getDevices();
		devicesCombo.removeAllItems();
		if (devices.size() == 0) {
			devicesCombo.addItem("No device attached");
			return;
		}
		for (int i = 0; i < devices.size(); i++) {
			devicesCombo.addItem(devices.get(i));
		}
	}

	public void setCurrentCaseText(String folder) {
		actualCurrentCaseLabel.setText(folder.substring(folder.lastIndexOf(File.separatorChar) + 1));
		actualCurrentCaseLabel.setToolTipText(folder);
	}

}
