package de.uni_hannover.osaft.plugins.connnectorappdata.view;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ContactInfoPanel extends JPanel implements ActionListener {

	private JButton btnOpenPicture;
	private JLabel lblPicture;
	private GridBagLayout gridBagLayout;
	private JLabel label;

	public ContactInfoPanel() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 151, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblContactPhoto = new JLabel("Contact Picture");
		GridBagConstraints gbc_lblContactPhoto = new GridBagConstraints();
		gbc_lblContactPhoto.gridwidth = 2;
		gbc_lblContactPhoto.insets = new Insets(0, 0, 5, 0);
		gbc_lblContactPhoto.gridx = 0;
		gbc_lblContactPhoto.gridy = 0;
		add(lblContactPhoto, gbc_lblContactPhoto);

		lblPicture = new JLabel("No Picture");
		GridBagConstraints gbc_lblPicture = new GridBagConstraints();
		gbc_lblPicture.gridwidth = 2;
		gbc_lblPicture.insets = new Insets(0, 0, 5, 0);
		gbc_lblPicture.gridx = 0;
		gbc_lblPicture.gridy = 1;
		add(lblPicture, gbc_lblPicture);

		btnOpenPicture = new JButton("Open Picture");
		btnOpenPicture.addActionListener(this);
		btnOpenPicture.setEnabled(false);
		GridBagConstraints gbc_btnOpenPicture = new GridBagConstraints();
		gbc_btnOpenPicture.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpenPicture.gridwidth = 2;
		gbc_btnOpenPicture.insets = new Insets(0, 0, 5, 0);
		gbc_btnOpenPicture.gridx = 0;
		gbc_btnOpenPicture.gridy = 2;
		add(btnOpenPicture, gbc_btnOpenPicture);

		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 3;
		add(lblName, gbc_lblName);
		
		label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 1;
		gbc_label.gridy = 3;
		add(label, gbc_label);

		JLabel lblNumbers = new JLabel("Numbers:");
		GridBagConstraints gbc_lblNumbers = new GridBagConstraints();
		gbc_lblNumbers.anchor = GridBagConstraints.WEST;
		gbc_lblNumbers.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumbers.gridx = 0;
		gbc_lblNumbers.gridy = 4;
		add(lblNumbers, gbc_lblNumbers);

		JLabel lblOrganisation = new JLabel("Organisation:");
		GridBagConstraints gbc_lblOrganisation = new GridBagConstraints();
		gbc_lblOrganisation.anchor = GridBagConstraints.WEST;
		gbc_lblOrganisation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrganisation.gridx = 0;
		gbc_lblOrganisation.gridy = 5;
		add(lblOrganisation, gbc_lblOrganisation);

		JLabel lblEmail = new JLabel("Email:");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 6;
		add(lblEmail, gbc_lblEmail);

		JLabel lblAddress = new JLabel("Address:");
		GridBagConstraints gbc_lblAddress = new GridBagConstraints();
		gbc_lblAddress.anchor = GridBagConstraints.WEST;
		gbc_lblAddress.insets = new Insets(0, 0, 5, 5);
		gbc_lblAddress.gridx = 0;
		gbc_lblAddress.gridy = 7;
		add(lblAddress, gbc_lblAddress);

		JLabel lblWebsite = new JLabel("Website:");
		GridBagConstraints gbc_lblWebsite = new GridBagConstraints();
		gbc_lblWebsite.anchor = GridBagConstraints.WEST;
		gbc_lblWebsite.insets = new Insets(0, 0, 5, 5);
		gbc_lblWebsite.gridx = 0;
		gbc_lblWebsite.gridy = 8;
		add(lblWebsite, gbc_lblWebsite);

		JLabel lblIm = new JLabel("IM:");
		GridBagConstraints gbc_lblIm = new GridBagConstraints();
		gbc_lblIm.anchor = GridBagConstraints.WEST;
		gbc_lblIm.insets = new Insets(0, 0, 5, 5);
		gbc_lblIm.gridx = 0;
		gbc_lblIm.gridy = 9;
		add(lblIm, gbc_lblIm);

		JLabel lblSkype = new JLabel("Skype:");
		GridBagConstraints gbc_lblSkype = new GridBagConstraints();
		gbc_lblSkype.anchor = GridBagConstraints.WEST;
		gbc_lblSkype.insets = new Insets(0, 0, 5, 5);
		gbc_lblSkype.gridx = 0;
		gbc_lblSkype.gridy = 10;
		add(lblSkype, gbc_lblSkype);

		JLabel lblNotes = new JLabel("Notes:");
		GridBagConstraints gbc_lblNotes = new GridBagConstraints();
		gbc_lblNotes.anchor = GridBagConstraints.WEST;
		gbc_lblNotes.insets = new Insets(0, 0, 0, 5);
		gbc_lblNotes.gridx = 0;
		gbc_lblNotes.gridy = 11;
		add(lblNotes, gbc_lblNotes);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnOpenPicture)) {
			Desktop dt = Desktop.getDesktop();
			try {
				dt.open(new File("/home/jannis/2013-01-02_16-55-14/data/contact_11.jpg"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void setInfo(String name, String numbers, String organisation, String emails,
			String addresses, String websites, String im, String skype, String notes, File picFile) {
		btnOpenPicture.setEnabled(true);
		try {
			BufferedImage picture = ImageIO.read(picFile);
			lblPicture.setIcon(new ImageIcon(picture.getScaledInstance(-1,
					gridBagLayout.rowHeights[1], Image.SCALE_FAST)));
			lblPicture.setText("");
			lblPicture.revalidate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setInfo(String name, String numbers, String organisation, String emails,
			String addresses, String websites, String im, String skype, String notes) {
		lblPicture.setText("No Picture");
		lblPicture.setIcon(null);
		btnOpenPicture.setEnabled(false);
		label.setText(name);
	}

}
