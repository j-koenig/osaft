package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.io.File;

import de.uni_hannover.osaft.plugins.connnectorappdata.view.MMSInfoPanel;

public class DBFacebookMessageInfoPanel extends MMSInfoPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setInfo(String text, File directory, String filename) {
		txtrText.setText("Text: \n" + text);	
		txtrText.setSelectionEnd(0);
		txtrText.setSelectionStart(0);
		
		//TODO: filehandling
	}
}
