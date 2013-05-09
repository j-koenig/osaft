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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.uni_hannover.osaft.Main;

/**
 * {@link JPanel} that shows detailed contact informations CREATED WITH
 * WINDOWBUILDER
 * 
 * @author Jannis Koenig
 * 
 */
public class ContactInfoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton btnOpenPicture;
	private JLabel lblPicture;
	private JTextField txtNumbers;
	private GridBagLayout gridBagLayout;
	private File currentPic;
	private JTextField txtName;
	private JTextField txtOrganisation;
	private JTextField txtEmail;
	private JTextField txtAddress;
	private JTextField txtWebsite;
	private JTextField txtIm;
	private JTextField txtSkype;
	private JTextField txtNotes;
	private JButton btnOpenFolder;
	
	private static final Logger log = Logger.getLogger(ContactInfoPanel.class.getName());

	public ContactInfoPanel() {
		log.addHandler(Main.fh);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 151, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
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

		btnOpenFolder = new JButton("Open Folder");
		btnOpenFolder.setEnabled(false);
		btnOpenFolder.addActionListener(this);
		GridBagConstraints gbc_btnOpenFolder = new GridBagConstraints();
		gbc_btnOpenFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnOpenFolder.gridwidth = 2;
		gbc_btnOpenFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnOpenFolder.gridx = 0;
		gbc_btnOpenFolder.gridy = 3;
		add(btnOpenFolder, gbc_btnOpenFolder);

		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 4;
		add(lblName, gbc_lblName);

		txtName = new JTextField();
		txtName.setEditable(false);
		GridBagConstraints gbc_txtName = new GridBagConstraints();
		gbc_txtName.gridwidth = 2;
		gbc_txtName.insets = new Insets(0, 0, 5, 0);
		gbc_txtName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtName.gridx = 0;
		gbc_txtName.gridy = 5;
		add(txtName, gbc_txtName);
		txtName.setColumns(10);

		JLabel lblNumbers = new JLabel("Numbers:");
		GridBagConstraints gbc_lblNumbers = new GridBagConstraints();
		gbc_lblNumbers.anchor = GridBagConstraints.WEST;
		gbc_lblNumbers.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumbers.gridx = 0;
		gbc_lblNumbers.gridy = 6;
		add(lblNumbers, gbc_lblNumbers);

		txtNumbers = new JTextField("");
		txtNumbers.setEditable(false);
		GridBagConstraints gbc_lblActualnumbers = new GridBagConstraints();
		gbc_lblActualnumbers.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblActualnumbers.gridwidth = 2;
		gbc_lblActualnumbers.insets = new Insets(0, 0, 5, 0);
		gbc_lblActualnumbers.gridx = 0;
		gbc_lblActualnumbers.gridy = 7;
		add(txtNumbers, gbc_lblActualnumbers);

		JLabel lblOrganisation = new JLabel("Organisation:");
		GridBagConstraints gbc_lblOrganisation = new GridBagConstraints();
		gbc_lblOrganisation.anchor = GridBagConstraints.WEST;
		gbc_lblOrganisation.insets = new Insets(0, 0, 5, 5);
		gbc_lblOrganisation.gridx = 0;
		gbc_lblOrganisation.gridy = 9;
		add(lblOrganisation, gbc_lblOrganisation);

		txtOrganisation = new JTextField();
		txtOrganisation.setEditable(false);
		GridBagConstraints gbc_txtOrganisation = new GridBagConstraints();
		gbc_txtOrganisation.gridwidth = 2;
		gbc_txtOrganisation.insets = new Insets(0, 0, 5, 0);
		gbc_txtOrganisation.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtOrganisation.gridx = 0;
		gbc_txtOrganisation.gridy = 10;
		add(txtOrganisation, gbc_txtOrganisation);
		txtOrganisation.setColumns(10);

		JLabel lblEmail = new JLabel("Email:");
		GridBagConstraints gbc_lblEmail = new GridBagConstraints();
		gbc_lblEmail.anchor = GridBagConstraints.WEST;
		gbc_lblEmail.insets = new Insets(0, 0, 5, 5);
		gbc_lblEmail.gridx = 0;
		gbc_lblEmail.gridy = 11;
		add(lblEmail, gbc_lblEmail);

		txtEmail = new JTextField();
		txtEmail.setEditable(false);
		GridBagConstraints gbc_txtEmail = new GridBagConstraints();
		gbc_txtEmail.gridwidth = 2;
		gbc_txtEmail.insets = new Insets(0, 0, 5, 0);
		gbc_txtEmail.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtEmail.gridx = 0;
		gbc_txtEmail.gridy = 12;
		add(txtEmail, gbc_txtEmail);
		txtEmail.setColumns(10);

		JLabel lblAddress = new JLabel("Address:");
		GridBagConstraints gbc_lblAddress = new GridBagConstraints();
		gbc_lblAddress.anchor = GridBagConstraints.WEST;
		gbc_lblAddress.insets = new Insets(0, 0, 5, 5);
		gbc_lblAddress.gridx = 0;
		gbc_lblAddress.gridy = 13;
		add(lblAddress, gbc_lblAddress);

		txtAddress = new JTextField();
		txtAddress.setEditable(false);
		GridBagConstraints gbc_txtAddress = new GridBagConstraints();
		gbc_txtAddress.gridwidth = 2;
		gbc_txtAddress.insets = new Insets(0, 0, 5, 0);
		gbc_txtAddress.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtAddress.gridx = 0;
		gbc_txtAddress.gridy = 14;
		add(txtAddress, gbc_txtAddress);
		txtAddress.setColumns(10);

		JLabel lblWebsite = new JLabel("Website:");
		GridBagConstraints gbc_lblWebsite = new GridBagConstraints();
		gbc_lblWebsite.anchor = GridBagConstraints.WEST;
		gbc_lblWebsite.insets = new Insets(0, 0, 5, 5);
		gbc_lblWebsite.gridx = 0;
		gbc_lblWebsite.gridy = 15;
		add(lblWebsite, gbc_lblWebsite);

		txtWebsite = new JTextField();
		txtWebsite.setEditable(false);
		GridBagConstraints gbc_txtWebsite = new GridBagConstraints();
		gbc_txtWebsite.gridwidth = 2;
		gbc_txtWebsite.insets = new Insets(0, 0, 5, 0);
		gbc_txtWebsite.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtWebsite.gridx = 0;
		gbc_txtWebsite.gridy = 16;
		add(txtWebsite, gbc_txtWebsite);
		txtWebsite.setColumns(10);

		JLabel lblIm = new JLabel("IM:");
		GridBagConstraints gbc_lblIm = new GridBagConstraints();
		gbc_lblIm.anchor = GridBagConstraints.WEST;
		gbc_lblIm.insets = new Insets(0, 0, 5, 5);
		gbc_lblIm.gridx = 0;
		gbc_lblIm.gridy = 17;
		add(lblIm, gbc_lblIm);

		txtIm = new JTextField();
		txtIm.setEditable(false);
		GridBagConstraints gbc_txtIm = new GridBagConstraints();
		gbc_txtIm.gridwidth = 2;
		gbc_txtIm.insets = new Insets(0, 0, 5, 0);
		gbc_txtIm.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtIm.gridx = 0;
		gbc_txtIm.gridy = 18;
		add(txtIm, gbc_txtIm);
		txtIm.setColumns(10);

		JLabel lblSkype = new JLabel("Skype:");
		GridBagConstraints gbc_lblSkype = new GridBagConstraints();
		gbc_lblSkype.anchor = GridBagConstraints.WEST;
		gbc_lblSkype.insets = new Insets(0, 0, 5, 5);
		gbc_lblSkype.gridx = 0;
		gbc_lblSkype.gridy = 19;
		add(lblSkype, gbc_lblSkype);

		txtSkype = new JTextField();
		txtSkype.setEditable(false);
		GridBagConstraints gbc_txtSkype = new GridBagConstraints();
		gbc_txtSkype.gridwidth = 2;
		gbc_txtSkype.insets = new Insets(0, 0, 5, 0);
		gbc_txtSkype.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSkype.gridx = 0;
		gbc_txtSkype.gridy = 20;
		add(txtSkype, gbc_txtSkype);
		txtSkype.setColumns(10);

		JLabel lblNotes = new JLabel("Notes:");
		GridBagConstraints gbc_lblNotes = new GridBagConstraints();
		gbc_lblNotes.anchor = GridBagConstraints.WEST;
		gbc_lblNotes.insets = new Insets(0, 0, 5, 5);
		gbc_lblNotes.gridx = 0;
		gbc_lblNotes.gridy = 21;
		add(lblNotes, gbc_lblNotes);

		txtNotes = new JTextField();
		txtNotes.setEditable(false);
		GridBagConstraints gbc_txtNotes = new GridBagConstraints();
		gbc_txtNotes.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNotes.gridwidth = 2;
		gbc_txtNotes.gridy = 22;
		gbc_txtNotes.gridx = 0;
		add(txtNotes, gbc_txtNotes);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnOpenPicture)) {
			Desktop dt = Desktop.getDesktop();
			try {
				dt.open(currentPic);
			} catch (IOException e1) {
				log.log(Level.WARNING, e.toString(), e);
			}
		}
		// FIXME: works on windows but not on linux
		else if (e.getSource().equals(btnOpenFolder)) {
			Desktop dt = Desktop.getDesktop();
			try {
				dt.open(new File(currentPic.getParent()));
			} catch (IOException e1) {
				log.log(Level.WARNING, e.toString(), e);
			}
		}

	}

	/**
	 * Called if a row in the contacts table is selected. Refreshes the info for
	 * the current row. Contact picture is available
	 */
	public void setInfo(String name, String numbers, String organisation, String emails, String addresses, String websites, String im,
			String skype, String notes, File picFile) {
		btnOpenPicture.setEnabled(true);
		btnOpenFolder.setEnabled(true);
		currentPic = picFile;

		txtName.setText(name);
		txtNumbers.setText(numbers);
		txtOrganisation.setText(organisation);
		txtEmail.setText(emails);
		txtAddress.setText(addresses);
		txtWebsite.setText(websites);
		txtIm.setText(im);
		txtSkype.setText(skype);
		txtNotes.setText(notes);

		try {
			BufferedImage picture = ImageIO.read(picFile);
			lblPicture.setIcon(new ImageIcon(picture.getScaledInstance(-1, gridBagLayout.rowHeights[1], Image.SCALE_FAST)));
			lblPicture.setText("");
			lblPicture.revalidate();
		} catch (IOException e) {
			log.log(Level.WARNING, e.toString(), e);
		}
	}
	/**
	 * Called if a row in the contacts table is selected. Refreshes the info for
	 * the current row. Contact picture is not available
	 */
	public void setInfo(String name, String numbers, String organisation, String emails, String addresses, String websites, String im,
			String skype, String notes) {
		lblPicture.setText("No Picture");
		lblPicture.setIcon(null);
		btnOpenPicture.setEnabled(false);
		btnOpenFolder.setEnabled(false);

		txtName.setText(name);
		txtNumbers.setText(numbers);
		txtOrganisation.setText(organisation);
		txtEmail.setText(emails);
		txtAddress.setText(addresses);
		txtWebsite.setText(websites);
		txtIm.setText(im);
		txtSkype.setText(skype);
		txtNotes.setText(notes);
	}

}
