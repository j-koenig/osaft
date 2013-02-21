package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class DBGmailInfoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton btnOpenFile, btnOpenFolder;
	private GridBagLayout gbl_mmsInfo;
	private JTextArea txtrText;
	private JLabel lblPreview;
	private JPanel panel;
	private File currentFile, caseFolder;
	private JComboBox filenameComboBox;

	public DBGmailInfoPanel() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gbl_mmsInfo = new GridBagLayout();
		gbl_mmsInfo.columnWidths = new int[] { 64, 67, 0 };
		gbl_mmsInfo.rowHeights = new int[] { 31, 154, 15, 0, 25, 311, 0 };
		gbl_mmsInfo.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_mmsInfo.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gbl_mmsInfo);

		JLabel attachmentLabel = new JLabel("Attachment preview:");
		attachmentLabel.setFont(new Font(attachmentLabel.getFont().getName(), Font.BOLD, attachmentLabel.getFont().getSize()));
		attachmentLabel.setHorizontalAlignment(SwingConstants.CENTER);
		attachmentLabel.setVerticalAlignment(SwingConstants.TOP);
		GridBagConstraints gbc_attachmentLabel = new GridBagConstraints();
		gbc_attachmentLabel.gridwidth = 2;
		gbc_attachmentLabel.fill = GridBagConstraints.BOTH;
		gbc_attachmentLabel.insets = new Insets(0, 0, 5, 0);
		gbc_attachmentLabel.gridx = 0;
		gbc_attachmentLabel.gridy = 0;
		add(attachmentLabel, gbc_attachmentLabel);

		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(panel, gbc_panel);
		panel.setLayout(new BorderLayout(0, 0));

		lblPreview = new JLabel("Preview not possible", JLabel.CENTER);
		panel.add(lblPreview, BorderLayout.CENTER);

		JLabel lblFilename = new JLabel("Filename: ");
		lblFilename.setFont(new Font(lblFilename.getFont().getName(), Font.BOLD, lblFilename.getFont().getSize()));
		GridBagConstraints gbc_lblFilename = new GridBagConstraints();
		gbc_lblFilename.anchor = GridBagConstraints.EAST;
		gbc_lblFilename.fill = GridBagConstraints.VERTICAL;
		gbc_lblFilename.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilename.gridx = 0;
		gbc_lblFilename.gridy = 2;
		add(lblFilename, gbc_lblFilename);

		btnOpenFile = new JButton("Open File");
		btnOpenFile.addActionListener(this);

		filenameComboBox = new JComboBox();
		filenameComboBox.setEnabled(false);
		filenameComboBox.addActionListener(this);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		add(filenameComboBox, gbc_comboBox);
		btnOpenFile.setEnabled(false);
		GridBagConstraints gbc_btnOpenFile = new GridBagConstraints();
		gbc_btnOpenFile.gridwidth = 2;
		gbc_btnOpenFile.fill = GridBagConstraints.BOTH;
		gbc_btnOpenFile.insets = new Insets(0, 0, 5, 0);
		gbc_btnOpenFile.gridx = 0;
		gbc_btnOpenFile.gridy = 3;
		add(btnOpenFile, gbc_btnOpenFile);

		btnOpenFolder = new JButton("Open Folder");
		btnOpenFolder.addActionListener(this);
		btnOpenFolder.setEnabled(false);
		GridBagConstraints gbc_btnOpenFolder = new GridBagConstraints();
		gbc_btnOpenFolder.gridwidth = 2;
		gbc_btnOpenFolder.fill = GridBagConstraints.BOTH;
		gbc_btnOpenFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnOpenFolder.gridx = 0;
		gbc_btnOpenFolder.gridy = 4;
		add(btnOpenFolder, gbc_btnOpenFolder);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
		add(scrollPane, gbc_scrollPane);

		txtrText = new JTextArea();
		txtrText.setEditable(false);
		txtrText.setWrapStyleWord(true);
		scrollPane.setViewportView(txtrText);
		txtrText.setText("Text:");
		txtrText.setLineWrap(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnOpenFile)) {
			Desktop dt = Desktop.getDesktop();
			try {
				dt.open(currentFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// TODO: funzt unter windows, aber nicht ubuntu. ka...
		else if (e.getSource().equals(btnOpenFolder)) {
			Desktop dt = Desktop.getDesktop();
			System.out.println(currentFile.getParent());
			try {
				dt.open(new File(currentFile.getParent()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource().equals(filenameComboBox)) {
			if (filenameComboBox.getItemCount() != 0) {
				reactToComboBox(filenameComboBox.getSelectedIndex());
			}
		}

	}

	private void reactToComboBox(int chosenItem) {

		currentFile = new File(caseFolder + File.separator + "gmail" + File.separator + filenameComboBox.getItemAt(chosenItem).toString());
		// mimetype:
		String mimeType = URLConnection.guessContentTypeFromName(currentFile.getName());
		if (mimeType != null && mimeType.startsWith("image")) {
			try {
				BufferedImage picture = ImageIO.read(currentFile);
				lblPreview.setIcon(new ImageIcon(picture.getScaledInstance(-1, gbl_mmsInfo.rowHeights[1], Image.SCALE_FAST)));
				lblPreview.setText("");
				lblPreview.revalidate();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("CANT FIND FILE!");
			}
		} else {
			lblPreview.setIcon(null);
			lblPreview.setText("Preview not possible");
			lblPreview.revalidate();
		}
	}

	public void setInfo(String text, File caseFolder, ArrayList<String> filenames) {

		this.caseFolder = caseFolder;
		txtrText.setText("Text:\n" + text);

		if (filenames != null) {
			filenameComboBox.removeAllItems();
			filenameComboBox.setEnabled(true);
			btnOpenFile.setEnabled(true);
			btnOpenFolder.setEnabled(true);
			for (int i = 0; i < filenames.size(); i++) {
				filenameComboBox.addItem(filenames.get(i));
			}
			reactToComboBox(0);
		} else {
			filenameComboBox.removeAllItems();
			filenameComboBox.setEnabled(false);
			btnOpenFile.setEnabled(false);
			btnOpenFolder.setEnabled(false);
			lblPreview.setIcon(null);
			lblPreview.setText("Preview not possible");
			lblPreview.revalidate();
		}

		txtrText.setSelectionStart(0);
		txtrText.setSelectionEnd(0);
	}
}
