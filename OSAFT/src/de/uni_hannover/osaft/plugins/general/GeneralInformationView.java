/*******************************************************************************
 * Copyright (c) 2013 Jannis Koenig.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jannis Koenig - initial API and implementation
 ******************************************************************************/
package de.uni_hannover.osaft.plugins.general;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;
import de.uni_hannover.osaft.view.OSAFTView;

//TODO: add more style to UI

/**
 * This class implements the {@link ViewPlugin} and presents information about
 * the attached phone. Partially edited with Googles WindowBuilder
 * 
 * @author Jannis Koenig
 * 
 */
@PluginImplementation
public class GeneralInformationView implements ViewPlugin {

	final private String batteryStatsCommand = "shell dumpsys battery";
	// final private String modelStatsCommand = "shell cat /system/build.prop";
	final private String getPropCommand = "shell getprop";
	final private String imeiCommand = "shell dumpsys iphonesubinfo";
	final private String[] suCheckCommands = new String[] { "su", "exit", "exit" };
	final private String[] wifisCommands = new String[] { "su", "cat /data/misc/wifi/wpa_supplicant.conf", "exit", "exit" };
	final private String installedPackagesCommand = "shell pm list packages";

	private JPanel panel;
	private ADBThread adb;
	private JLabel lblActualManufacturer;
	private JLabel lblActualModel;
	private JLabel lblActualID;
	private JLabel lblActualAndroidVersion;
	private JLabel lblActualSerialNo;
	private JLabel lblActualImei;
	private JLabel lblActualPhoneType;
	private JLabel lblActualBatteryLevel;
	private JLabel lblActualBatteryTemperature;
	private JLabel lblActualRootStatus;
	private JLabel lblInstalledApplications;
	private JScrollPane scrollPane;
	private JList list;
	private JScrollPane scrollPane_1;
	private JTextArea textArea;
	private JLabel lblSavedWifis;

	/**
	 * Initializes the GUI
	 */
	public GeneralInformationView() {
		adb = ADBThread.getInstance();
		initGUI();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initGUI() {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 39, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblPhone = new JLabel("Phone Details:");
		lblPhone.setFont(new Font("Dialog", Font.BOLD, 14));
		GridBagConstraints gbc_lblPhone = new GridBagConstraints();
		gbc_lblPhone.anchor = GridBagConstraints.WEST;
		gbc_lblPhone.insets = new Insets(0, 0, 5, 5);
		gbc_lblPhone.gridx = 0;
		gbc_lblPhone.gridy = 0;
		panel.add(lblPhone, gbc_lblPhone);

		lblInstalledApplications = new JLabel("Installed Applications:");
		GridBagConstraints gbc_lblInstalledApplications = new GridBagConstraints();
		gbc_lblInstalledApplications.anchor = GridBagConstraints.WEST;
		gbc_lblInstalledApplications.insets = new Insets(0, 0, 5, 0);
		gbc_lblInstalledApplications.gridx = 2;
		gbc_lblInstalledApplications.gridy = 0;
		panel.add(lblInstalledApplications, gbc_lblInstalledApplications);

		JLabel lblManufacturer = new JLabel("Manufacturer:");
		GridBagConstraints gbc_lblManufacturer = new GridBagConstraints();
		gbc_lblManufacturer.anchor = GridBagConstraints.WEST;
		gbc_lblManufacturer.insets = new Insets(0, 0, 5, 5);
		gbc_lblManufacturer.gridx = 0;
		gbc_lblManufacturer.gridy = 1;
		panel.add(lblManufacturer, gbc_lblManufacturer);

		lblActualManufacturer = new JLabel("unknown");
		GridBagConstraints gbc_lblActualManufacturer = new GridBagConstraints();
		gbc_lblActualManufacturer.anchor = GridBagConstraints.WEST;
		gbc_lblActualManufacturer.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualManufacturer.gridx = 1;
		gbc_lblActualManufacturer.gridy = 1;
		panel.add(lblActualManufacturer, gbc_lblActualManufacturer);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 10;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 2;
		gbc_scrollPane.gridy = 1;
		panel.add(scrollPane, gbc_scrollPane);

		list = new JList();
		scrollPane.setViewportView(list);

		JLabel lblModel = new JLabel("Model:");
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.anchor = GridBagConstraints.WEST;
		gbc_lblModel.insets = new Insets(0, 0, 5, 5);
		gbc_lblModel.gridx = 0;
		gbc_lblModel.gridy = 2;
		panel.add(lblModel, gbc_lblModel);

		lblActualModel = new JLabel("unknown");
		GridBagConstraints gbc_lblActualModel = new GridBagConstraints();
		gbc_lblActualModel.anchor = GridBagConstraints.WEST;
		gbc_lblActualModel.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualModel.gridx = 1;
		gbc_lblActualModel.gridy = 2;
		panel.add(lblActualModel, gbc_lblActualModel);

		JLabel lblModelID = new JLabel("Model ID:");
		GridBagConstraints gbc_lblModelID = new GridBagConstraints();
		gbc_lblModelID.anchor = GridBagConstraints.WEST;
		gbc_lblModelID.insets = new Insets(0, 0, 5, 5);
		gbc_lblModelID.gridx = 0;
		gbc_lblModelID.gridy = 3;
		panel.add(lblModelID, gbc_lblModelID);

		lblActualID = new JLabel("unknown");
		GridBagConstraints gbc_lblActualID = new GridBagConstraints();
		gbc_lblActualID.anchor = GridBagConstraints.WEST;
		gbc_lblActualID.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualID.gridx = 1;
		gbc_lblActualID.gridy = 3;
		panel.add(lblActualID, gbc_lblActualID);

		JLabel lblAndroidVersion = new JLabel("Android Version:");
		GridBagConstraints gbc_lblAndroidVersion = new GridBagConstraints();
		gbc_lblAndroidVersion.anchor = GridBagConstraints.WEST;
		gbc_lblAndroidVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblAndroidVersion.gridx = 0;
		gbc_lblAndroidVersion.gridy = 4;
		panel.add(lblAndroidVersion, gbc_lblAndroidVersion);

		lblActualAndroidVersion = new JLabel("unknown");
		GridBagConstraints gbc_lblActualAndroidVersion = new GridBagConstraints();
		gbc_lblActualAndroidVersion.anchor = GridBagConstraints.WEST;
		gbc_lblActualAndroidVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualAndroidVersion.gridx = 1;
		gbc_lblActualAndroidVersion.gridy = 4;
		panel.add(lblActualAndroidVersion, gbc_lblActualAndroidVersion);

		JLabel lblSerialNo = new JLabel("Serial No.:");
		GridBagConstraints gbc_lblSerialNo = new GridBagConstraints();
		gbc_lblSerialNo.anchor = GridBagConstraints.WEST;
		gbc_lblSerialNo.insets = new Insets(0, 0, 5, 5);
		gbc_lblSerialNo.gridx = 0;
		gbc_lblSerialNo.gridy = 5;
		panel.add(lblSerialNo, gbc_lblSerialNo);

		lblActualSerialNo = new JLabel("unknown");
		GridBagConstraints gbc_lblActualSerialNo = new GridBagConstraints();
		gbc_lblActualSerialNo.anchor = GridBagConstraints.WEST;
		gbc_lblActualSerialNo.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualSerialNo.gridx = 1;
		gbc_lblActualSerialNo.gridy = 5;
		panel.add(lblActualSerialNo, gbc_lblActualSerialNo);

		JLabel lblImei = new JLabel("IMEI:");
		GridBagConstraints gbc_lblImei = new GridBagConstraints();
		gbc_lblImei.anchor = GridBagConstraints.WEST;
		gbc_lblImei.insets = new Insets(0, 0, 5, 5);
		gbc_lblImei.gridx = 0;
		gbc_lblImei.gridy = 6;
		panel.add(lblImei, gbc_lblImei);

		lblActualImei = new JLabel("unknown");
		GridBagConstraints gbc_lblActualImei = new GridBagConstraints();
		gbc_lblActualImei.anchor = GridBagConstraints.WEST;
		gbc_lblActualImei.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualImei.gridx = 1;
		gbc_lblActualImei.gridy = 6;
		panel.add(lblActualImei, gbc_lblActualImei);

		JLabel lblPhoneType = new JLabel("Phone Type:");
		GridBagConstraints gbc_lblPhoneType = new GridBagConstraints();
		gbc_lblPhoneType.anchor = GridBagConstraints.WEST;
		gbc_lblPhoneType.insets = new Insets(0, 0, 5, 5);
		gbc_lblPhoneType.gridx = 0;
		gbc_lblPhoneType.gridy = 7;
		panel.add(lblPhoneType, gbc_lblPhoneType);

		lblActualPhoneType = new JLabel("unknown");
		GridBagConstraints gbc_lblActualPhoneType = new GridBagConstraints();
		gbc_lblActualPhoneType.anchor = GridBagConstraints.WEST;
		gbc_lblActualPhoneType.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualPhoneType.gridx = 1;
		gbc_lblActualPhoneType.gridy = 7;
		panel.add(lblActualPhoneType, gbc_lblActualPhoneType);

		JLabel lblBatteryLevel = new JLabel("Battery Level:");
		GridBagConstraints gbc_lblBatteryLevel = new GridBagConstraints();
		gbc_lblBatteryLevel.anchor = GridBagConstraints.WEST;
		gbc_lblBatteryLevel.insets = new Insets(0, 0, 5, 5);
		gbc_lblBatteryLevel.gridx = 0;
		gbc_lblBatteryLevel.gridy = 8;
		panel.add(lblBatteryLevel, gbc_lblBatteryLevel);

		lblActualBatteryLevel = new JLabel("unknown");
		GridBagConstraints gbc_lblActualBatteryLevel = new GridBagConstraints();
		gbc_lblActualBatteryLevel.anchor = GridBagConstraints.WEST;
		gbc_lblActualBatteryLevel.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualBatteryLevel.gridx = 1;
		gbc_lblActualBatteryLevel.gridy = 8;
		panel.add(lblActualBatteryLevel, gbc_lblActualBatteryLevel);

		JLabel lblBatteryTemperature = new JLabel("Battery Temperature:");
		GridBagConstraints gbc_lblBatteryTemperature = new GridBagConstraints();
		gbc_lblBatteryTemperature.anchor = GridBagConstraints.WEST;
		gbc_lblBatteryTemperature.insets = new Insets(0, 0, 5, 5);
		gbc_lblBatteryTemperature.gridx = 0;
		gbc_lblBatteryTemperature.gridy = 9;
		panel.add(lblBatteryTemperature, gbc_lblBatteryTemperature);

		lblActualBatteryTemperature = new JLabel("unknown");
		GridBagConstraints gbc_lblActualBatteryTemperature = new GridBagConstraints();
		gbc_lblActualBatteryTemperature.anchor = GridBagConstraints.WEST;
		gbc_lblActualBatteryTemperature.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualBatteryTemperature.gridx = 1;
		gbc_lblActualBatteryTemperature.gridy = 9;
		panel.add(lblActualBatteryTemperature, gbc_lblActualBatteryTemperature);

		JLabel lblRootStatus = new JLabel("Root Status:");
		GridBagConstraints gbc_lblRootStatus = new GridBagConstraints();
		gbc_lblRootStatus.anchor = GridBagConstraints.WEST;
		gbc_lblRootStatus.insets = new Insets(0, 0, 5, 5);
		gbc_lblRootStatus.gridx = 0;
		gbc_lblRootStatus.gridy = 10;
		panel.add(lblRootStatus, gbc_lblRootStatus);

		lblActualRootStatus = new JLabel("unknown");
		GridBagConstraints gbc_lblActualRootStatus = new GridBagConstraints();
		gbc_lblActualRootStatus.anchor = GridBagConstraints.WEST;
		gbc_lblActualRootStatus.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualRootStatus.gridx = 1;
		gbc_lblActualRootStatus.gridy = 10;
		panel.add(lblActualRootStatus, gbc_lblActualRootStatus);

		lblSavedWifis = new JLabel("Saved WiFis (rooted Phones):");
		GridBagConstraints gbc_lblSavedWifis = new GridBagConstraints();
		gbc_lblSavedWifis.anchor = GridBagConstraints.WEST;
		gbc_lblSavedWifis.insets = new Insets(0, 0, 5, 0);
		gbc_lblSavedWifis.gridx = 2;
		gbc_lblSavedWifis.gridy = 11;
		panel.add(lblSavedWifis, gbc_lblSavedWifis);

		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 2;
		gbc_scrollPane_1.gridy = 12;
		panel.add(scrollPane_1, gbc_scrollPane_1);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane_1.setViewportView(textArea);
	}

	@Override
	public String getName() {
		return "General Information";
	}

	@Override
	public JComponent getView() {
		return panel;
	}

	/**
	 * When the {@link JButton} in the {@link OSAFTView} for this plugin is
	 * pressed it collects the information with three adb commands
	 */
	@Override
	public void triggered() {
		textArea.setText("");
		adb.executeAndReturn(batteryStatsCommand, this, false);
		adb.executeAndReturn(getPropCommand, this, false);
		adb.executeAndReturn(imeiCommand, this, false);
		adb.executeAndReturn(installedPackagesCommand, this, false);
		adb.interactWithShell(suCheckCommands, this, false);
		adb.interactWithShell(wifisCommands, this, false);
	}

	/**
	 * Depending on the executed command this method sets the information about
	 * the phone
	 */
	@Override
	public void reactToADBResult(String result, String[] executedCommand) {
		String[] resultLineByLine = result.split("\\r?\\n");
		if (executedCommand[0].equals(batteryStatsCommand)) {
			for (int i = 0; i < resultLineByLine.length; i++) {
				if (resultLineByLine[i].contains("level")) {
					lblActualBatteryLevel.setText(extractProp(resultLineByLine[i]));
				} else if (resultLineByLine[i].contains("temperature")) {
					double temp = Double.parseDouble(extractProp(resultLineByLine[i]));
					temp = temp / 10;
					lblActualBatteryTemperature.setText(temp + "° C");
				}

			}
		} else if (executedCommand[0].equals(getPropCommand)) {
			for (int i = 0; i < resultLineByLine.length; i++) {
				if (resultLineByLine[i].contains("ro.product.manufacturer")) {
					lblActualManufacturer.setText(extractProp(resultLineByLine[i]));
				} else if (resultLineByLine[i].contains("ro.product.model")) {
					lblActualModel.setText(extractProp(resultLineByLine[i]));
				} else if (resultLineByLine[i].contains("ril.model_id")) {
					lblActualID.setText(extractProp(resultLineByLine[i]));
				} else if (resultLineByLine[i].contains("ro.build.version.release")) {
					lblActualAndroidVersion.setText(extractProp(resultLineByLine[i]));
				} else if (resultLineByLine[i].contains("ro.serialno")) {
					lblActualSerialNo.setText(extractProp(resultLineByLine[i]));
				}
			}
		} else if (executedCommand[0].equals(imeiCommand)) {
			for (int i = 0; i < resultLineByLine.length; i++) {
				if (resultLineByLine[i].contains("Phone Type")) {
					lblActualPhoneType.setText(resultLineByLine[i].substring(resultLineByLine[i].indexOf("=") + 2));
				} else if (resultLineByLine[i].contains("Device ID")) {
					lblActualImei.setText(resultLineByLine[i].substring(resultLineByLine[i].indexOf("=") + 2));
				}
			}
		} else if (executedCommand.equals(suCheckCommands)) {
			lblActualRootStatus.setText("rooted");
			for (int i = 0; i < resultLineByLine.length; i++) {
				if (resultLineByLine[i].toLowerCase().contains("permission") || resultLineByLine[i].toLowerCase().contains("not found")) {
					lblActualRootStatus.setText("not rooted");
				}
			}
		} else if (executedCommand[0].equals(installedPackagesCommand)) {
			String[] packages = new String[resultLineByLine.length];
			if (resultLineByLine.length > 1) {
				for (int i = 0; i < resultLineByLine.length; i++) {
					if (resultLineByLine[i].length() > 1) {
						packages[i] = resultLineByLine[i].split(":")[1];
					}
				}
			}
			list.setListData(packages);
		} else if (executedCommand.equals(wifisCommands)) {
			// FIXME: formatting on windows
			if (!result.toLowerCase().contains("/data/misc/wifi/wpa_supplicant.conf: permission denied")) {
				for (int i = 8; i < resultLineByLine.length - 3; i++) {
					textArea.setText(textArea.getText() + resultLineByLine[i] + "\n");
				}
			}
		}
	}

	private String extractProp(String longProp) {
		return longProp.substring(longProp.indexOf(':') + 1).replace("[", "").replace("]", "");
	}

}
