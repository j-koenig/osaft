package de.uni_hannover.osaft.plugins.general;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import de.uni_hannover.osaft.adb.ADBThread;
import de.uni_hannover.osaft.plugininterfaces.ViewPlugin;

//TODO: muss noch shcön klickibunti sein
//TODO: liste mit allen apps? "pm list packages"
//TODO: alle jemals verbundenen wifis? is das hier an der richtigen stelle?

@PluginImplementation
public class GeneralInformationView implements ViewPlugin {

	final private String batteryStatsCommand = "shell dumpsys battery";
	// final private String modelStatsCommand = "shell cat /system/build.prop";
	final private String getPropCommand = "shell getprop";
	final private String imeiCommand = "shell dumpsys iphonesubinfo";

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

	@Init
	public void init() {
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
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblPhone = new JLabel("Phone Details:");
		GridBagConstraints gbc_lblPhone = new GridBagConstraints();
		gbc_lblPhone.anchor = GridBagConstraints.WEST;
		gbc_lblPhone.insets = new Insets(0, 0, 5, 5);
		gbc_lblPhone.gridx = 0;
		gbc_lblPhone.gridy = 0;
		panel.add(lblPhone, gbc_lblPhone);

		JLabel lblManufacturer = new JLabel("Manufacturer:");
		GridBagConstraints gbc_lblManufacturer = new GridBagConstraints();
		gbc_lblManufacturer.anchor = GridBagConstraints.WEST;
		gbc_lblManufacturer.insets = new Insets(0, 0, 5, 5);
		gbc_lblManufacturer.gridx = 0;
		gbc_lblManufacturer.gridy = 1;
		panel.add(lblManufacturer, gbc_lblManufacturer);

		lblActualManufacturer = new JLabel("unknown");
		GridBagConstraints gbc_lblActualManufacturer = new GridBagConstraints();
		gbc_lblActualManufacturer.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualManufacturer.gridx = 1;
		gbc_lblActualManufacturer.gridy = 1;
		panel.add(lblActualManufacturer, gbc_lblActualManufacturer);

		JLabel lblModel = new JLabel("Model:");
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.anchor = GridBagConstraints.WEST;
		gbc_lblModel.insets = new Insets(0, 0, 5, 5);
		gbc_lblModel.gridx = 0;
		gbc_lblModel.gridy = 2;
		panel.add(lblModel, gbc_lblModel);

		lblActualModel = new JLabel("unknown");
		GridBagConstraints gbc_lblActualModel = new GridBagConstraints();
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
		gbc_lblActualBatteryLevel.insets = new Insets(0, 0, 5, 5);
		gbc_lblActualBatteryLevel.gridx = 1;
		gbc_lblActualBatteryLevel.gridy = 8;
		panel.add(lblActualBatteryLevel, gbc_lblActualBatteryLevel);

		JLabel lblBatteryTemperature = new JLabel("Battery Temperature:");
		GridBagConstraints gbc_lblBatteryTemperature = new GridBagConstraints();
		gbc_lblBatteryTemperature.anchor = GridBagConstraints.WEST;
		gbc_lblBatteryTemperature.insets = new Insets(0, 0, 0, 5);
		gbc_lblBatteryTemperature.gridx = 0;
		gbc_lblBatteryTemperature.gridy = 9;
		panel.add(lblBatteryTemperature, gbc_lblBatteryTemperature);

		lblActualBatteryTemperature = new JLabel("unknown");
		GridBagConstraints gbc_lblActualBatteryTemperature = new GridBagConstraints();
		gbc_lblActualBatteryTemperature.insets = new Insets(0, 0, 0, 5);
		gbc_lblActualBatteryTemperature.gridx = 1;
		gbc_lblActualBatteryTemperature.gridy = 9;
		panel.add(lblActualBatteryTemperature, gbc_lblActualBatteryTemperature);
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
		adb.executeAndReturn(batteryStatsCommand, this);
		adb.executeAndReturn(getPropCommand, this);
		adb.executeAndReturn(imeiCommand, this);
	}

	@Override
	public void setADBThread(ADBThread adb) {
		this.adb = adb;
	}

	@Override
	public void setCaseFolder(File caseFolder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reactToADBResult(String result, String executedCommand) {
		String[] resultLineByLine = result.split("\\r?\\n");
		if (executedCommand.equals(batteryStatsCommand)) {
			for (int i = 0; i < resultLineByLine.length; i++) {
				if (resultLineByLine[i].contains("level")) {
					lblActualBatteryLevel.setText(extractProp(resultLineByLine[i]));
				} else if (resultLineByLine[i].contains("temperature")) {
					lblActualBatteryTemperature.setText(extractProp(resultLineByLine[i]));
				}

			}
		} else if (executedCommand.equals(getPropCommand)) {
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
		} else if (executedCommand.equals(imeiCommand)) {
			for (int i = 0; i < resultLineByLine.length; i++) {
				if (resultLineByLine[i].contains("Phone Type")) {
					lblActualPhoneType.setText(resultLineByLine[i].substring(resultLineByLine[i].indexOf("=") + 2));
				} else if (resultLineByLine[i].contains("Device ID")) {
					lblActualImei.setText(resultLineByLine[i].substring(resultLineByLine[i].indexOf("=") + 2));
				}
			}
		}

	}

	private String extractProp(String longProp) {
		return longProp.substring(longProp.indexOf(':') + 1).replace("[", "").replace("]", "");
	}

}
