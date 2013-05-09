package de.uni_hannover.osaft.plugins.connnectorappdata.view;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * {@link JPanel} that shows detailed informations about selected mms. Provides
 * a picture preview if there is a picture in the attachment GENERATED WITH
 * WINDOWBUILDER
 * 
 * @author Jannis Koenig
 * 
 */
public class MMSInfoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	protected JButton btnOpenFile, btnOpenFolder;
	protected GridBagLayout gbl_mmsInfo;
	protected JTextArea txtrText;
	protected JLabel lblActualFilename;
	protected JLabel lblPreview;
	protected JPanel panel;
	protected File currentFile;

	private static final Logger log = Logger.getLogger(MMSInfoPanel.class.getName());

	public MMSInfoPanel() {
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
		gbc_lblFilename.anchor = GridBagConstraints.WEST;
		gbc_lblFilename.fill = GridBagConstraints.VERTICAL;
		gbc_lblFilename.insets = new Insets(0, 0, 5, 5);
		gbc_lblFilename.gridx = 0;
		gbc_lblFilename.gridy = 2;
		add(lblFilename, gbc_lblFilename);

		btnOpenFile = new JButton("Open File");
		btnOpenFile.addActionListener(this);
		btnOpenFile.setEnabled(false);

		lblActualFilename = new JLabel("");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewLabel.anchor = GridBagConstraints.NORTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 2;
		add(lblActualFilename, gbc_lblNewLabel);
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
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
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
				log.log(Level.WARNING, e.toString(), e);
			}
		}

		// FIXME: works on windows but not on ubuntu..
		else if (e.getSource().equals(btnOpenFolder)) {
			Desktop dt = Desktop.getDesktop();
			try {
				dt.open(new File(currentFile.getParent()));
			} catch (IOException e1) {
				log.log(Level.WARNING, e.toString(), e);
			}
		}

	}

	/**
	 * Called if a row in the mms table is selected. Refreshes the info for the
	 * current row.
	 */
	public void setInfo(String id, String text, boolean hasAttachment, File directory) {
		txtrText.setText("Text: \n " + text);
		txtrText.setSelectionEnd(0);
		txtrText.setSelectionStart(0);

		File dataDir = new File(directory + "/data/");

		if (hasAttachment) {
			btnOpenFile.setEnabled(true);
			btnOpenFolder.setEnabled(true);

			// ArtifactExtract app saves the attachment with this pattern: "mms"
			// + "id" + "fileextension"
			String filenameWithoutExtension = "mms_" + id;

			File[] files = dataDir.listFiles();
			// iterate over dataDir to find attachments. Filenames are split
			// with the character "." to compare the filenames without the
			// fileextension
			for (int i = 0; i < files.length; i++) {
				String[] splittedFilename = files[i].getName().split("\\.");
				if (splittedFilename.length > 0 && splittedFilename[0].equals(filenameWithoutExtension)) {
					currentFile = files[i];
				}
			}

			lblActualFilename.setText(currentFile.getName());
			// get mimetype of found file
			String mimeType = URLConnection.guessContentTypeFromName(currentFile.getName());
			// picture file
			if (mimeType != null && mimeType.startsWith("image")) {
				try {
					BufferedImage picture = ImageIO.read(new File(directory + "/data/" + currentFile.getName()));
					lblPreview.setIcon(new ImageIcon(picture.getScaledInstance(-1, gbl_mmsInfo.rowHeights[1], Image.SCALE_FAST)));
					lblPreview.setText("");
					lblPreview.revalidate();
				} catch (IOException e) {
					log.log(Level.WARNING, e.toString(), e);
				}
			} else {
				lblPreview.setIcon(null);
				lblPreview.setText("Preview not possible");
				lblPreview.revalidate();
			}
		} else {
			lblActualFilename.setText("");
			lblPreview.setIcon(null);
			lblPreview.setText("Preview not possible");
			lblPreview.revalidate();
			btnOpenFile.setEnabled(false);
			btnOpenFolder.setEnabled(false);
		}
	}

}
