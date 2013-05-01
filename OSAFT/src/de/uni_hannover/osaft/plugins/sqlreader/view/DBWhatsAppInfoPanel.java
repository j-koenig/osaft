package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.uni_hannover.osaft.plugins.connnectorappdata.view.ConnectorAppDataView;
import de.uni_hannover.osaft.plugins.connnectorappdata.view.MMSInfoPanel;

/**
 * {@link JPanel} that shows detailed informations about selected whatsapp
 * message. Provides a picture preview if there is a picture in the attachment.
 * Extends the {@link MMSInfoPanel} from the {@link ConnectorAppDataView}-Plugin
 * 
 * @author Jannis Koenig
 * 
 */
public class DBWhatsAppInfoPanel extends MMSInfoPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Called if a row in the gmail table is selected. Refreshes the info for
	 * the current row.
	 */
	public void setInfo(String text, File directory, String filename) {
		txtrText.setText("Text: \n" + text);
		if (!filename.equals("")) {
			lblActualFilename.setText(filename);
			btnOpenFile.setEnabled(true);
			btnOpenFolder.setEnabled(true);

			if (filename.startsWith("IMG")) {
				currentFile = new File(directory + File.separator + "whatsapp" + File.separator + "WhatsApp Images" + File.separator
						+ filename);
				try {
					BufferedImage picture = ImageIO.read(currentFile);
					lblPreview.setIcon(new ImageIcon(picture.getScaledInstance(-1, gbl_mmsInfo.rowHeights[1], Image.SCALE_FAST)));
					lblPreview.setText("");
					lblPreview.revalidate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(filename.startsWith("VID")){
				currentFile = new File(directory + File.separator + "whatsapp" + File.separator + "WhatsApp Video" + File.separator
						+ filename);
				lblPreview.setIcon(null);
				lblPreview.setText("Preview not possible");
				lblPreview.revalidate();
			} else if(filename.startsWith("AUD")) {
				currentFile = new File(directory + File.separator + "whatsapp" + File.separator + "WhatsApp Audio" + File.separator
						+ filename);
				lblPreview.setIcon(null);
				lblPreview.setText("Preview not possible");
				lblPreview.revalidate();
			} else {
				lblPreview.setIcon(null);
				lblPreview.setText("Preview not possible");
				lblPreview.revalidate();
			}

		} else {
			btnOpenFile.setEnabled(false);
			btnOpenFolder.setEnabled(false);
			lblPreview.setIcon(null);
			lblPreview.setText("Preview not possible");
			lblPreview.revalidate();
			lblActualFilename.setText("");
		}
		txtrText.setSelectionEnd(0);
		txtrText.setSelectionStart(0);

	}

}
