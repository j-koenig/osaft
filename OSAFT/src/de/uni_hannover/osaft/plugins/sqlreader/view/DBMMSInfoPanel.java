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
 * {@link JPanel} that shows detailed informations about selected mms. Provides
 * a picture preview if there is a picture in the attachment. Extends the
 * {@link MMSInfoPanel} from the {@link ConnectorAppDataView}-Plugin 
 * 
 * @author Jannis Koenig
 * 
 */
public class DBMMSInfoPanel extends MMSInfoPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * Called if a row in the mms table is selected. Refreshes the info for the
	 * current row.
	 */
	public void setInfo(String text, String mimetype, File directory, String filename) {
		txtrText.setText("Text: \n" + text);
		lblActualFilename.setText(filename);
		btnOpenFile.setEnabled(true);
		btnOpenFolder.setEnabled(true);

		if (mimetype.startsWith("image")) {
			try {
				BufferedImage picture = ImageIO.read(new File(directory + File.separator + "mms_parts" + File.separator + filename));
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
		txtrText.setSelectionEnd(0);
		txtrText.setSelectionStart(0);

	}

}
