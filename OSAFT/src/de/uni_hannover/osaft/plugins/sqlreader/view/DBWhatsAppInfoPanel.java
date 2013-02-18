package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.uni_hannover.osaft.plugins.connnectorappdata.view.MMSInfoPanel;

public class DBWhatsAppInfoPanel extends MMSInfoPanel {

	private static final long serialVersionUID = 1L;

	public void setInfo(String text, File directory, String filename) {
		txtrText.setText("Text: \n");		
		if (!filename.equals("")) {
			lblActualFilename.setText(filename);
			btnOpenFile.setEnabled(true);
			btnOpenFolder.setEnabled(true);
			currentFile = new File(directory + File.separator + "whatsapp" + File.separator + filename);
			
			if (filename.startsWith("IMG")) {
				try {					
					BufferedImage picture = ImageIO.read(currentFile);
					lblPreview.setIcon(new ImageIcon(picture.getScaledInstance(-1,
							gbl_mmsInfo.rowHeights[1], Image.SCALE_FAST)));
					lblPreview.setText("");
					lblPreview.revalidate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("CANT FIND FILE!");
				}
			}
			else {
				lblPreview.setIcon(null);
				lblPreview.setText("Preview not possible");
				lblPreview.revalidate();
			}						
			
		}
		else {
			btnOpenFile.setEnabled(false);
			btnOpenFolder.setEnabled(false);
			lblPreview.setIcon(null);
			lblPreview.setText("Preview not possible");
			lblPreview.revalidate();
			lblActualFilename.setText("");
		}

	}

}
