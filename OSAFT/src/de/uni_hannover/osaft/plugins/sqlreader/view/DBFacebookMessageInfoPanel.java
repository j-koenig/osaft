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
package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.uni_hannover.osaft.Main;
import de.uni_hannover.osaft.plugins.connnectorappdata.view.ConnectorAppDataView;
import de.uni_hannover.osaft.plugins.connnectorappdata.view.MMSInfoPanel;

/**
 * {@link JPanel} that shows detailed informations about selected facebook
 * message. Provides a picture preview if there is a picture in the attachment.
 * Extends the {@link MMSInfoPanel} from the {@link ConnectorAppDataView}-Plugin
 * 
 * @author Jannis Koenig
 * 
 */
public class DBFacebookMessageInfoPanel extends MMSInfoPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DBFacebookMessageInfoPanel.class.getName());

	/**
	 * Called if a row in the gmail table is selected. Refreshes the info for
	 * the current row.
	 */
	
	public DBFacebookMessageInfoPanel() {
		super();
		log.addHandler(Main.fh);
	}
	
	public void setInfo(String text, File directory, String filename) {
		txtrText.setText("Text: \n" + text);
		txtrText.setSelectionEnd(0);
		txtrText.setSelectionStart(0);

		if (filename != null) {
			currentFile = new File(directory + File.separator + "facebook" + File.separator + filename);
			String mimeType = URLConnection.guessContentTypeFromName(currentFile.getName());
			if (mimeType != null && mimeType.startsWith("image")) {
				try {
					BufferedImage picture = ImageIO.read(currentFile);
					lblPreview.setIcon(new ImageIcon(picture.getScaledInstance(-1, gbl_mmsInfo.rowHeights[1], Image.SCALE_FAST)));
					lblPreview.setText("");
					lblPreview.revalidate();
					btnOpenFile.setEnabled(true);
					btnOpenFolder.setEnabled(true);
				} catch (IOException e) {
					log.log(Level.WARNING, e.toString(), e);
				}
			} else {
				lblPreview.setIcon(null);
				lblPreview.setText("Preview not possible");
				lblPreview.revalidate();
				btnOpenFile.setEnabled(false);
				btnOpenFolder.setEnabled(false);
			}
		} else {
			lblPreview.setIcon(null);
			lblPreview.setText("Preview not possible");
			lblPreview.revalidate();
			btnOpenFile.setEnabled(false);
			btnOpenFolder.setEnabled(false);
		}
		
	}
}
