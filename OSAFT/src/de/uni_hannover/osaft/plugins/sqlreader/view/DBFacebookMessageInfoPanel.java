package de.uni_hannover.osaft.plugins.sqlreader.view;

import java.io.File;

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
	/**
	 * Called if a row in the gmail table is selected. Refreshes the info for the
	 * current row.
	 */
	public void setInfo(String text, File directory, String filename) {
		txtrText.setText("Text: \n" + text);
		txtrText.setSelectionEnd(0);
		txtrText.setSelectionStart(0);

		// TODO: filehandling
	}
}
