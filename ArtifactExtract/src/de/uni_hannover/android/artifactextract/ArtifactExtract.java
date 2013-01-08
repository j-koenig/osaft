package de.uni_hannover.android.artifactextract;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import de.uni_hannover.android.artifactextract.util.SDCardHandler;

//main activity
public class ArtifactExtract extends Activity {

	private ProgressDialog pd;
	private CheckBox browserHCheck, browserSCheck, calendarCheck, callCheck, contactCheck,
			mmsCheck, smsCheck;
	private Gatherer gatherer;
	// all found data is saved in the directory "artifacts" on the sd card
	private final String DIRECTORY = Environment.getExternalStorageDirectory() + "/artifacts/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// check for sd card
		if (!SDCardHandler.isMounted()) {
			if (SDCardHandler.isRemoved()) {
				exit("Please insert a SD card");
			} else if (SDCardHandler.isShared()) {
				exit("Please stop USB mass storage mode");
			} else {
				exit("SD card error!");
			}
		} else {
			if (!SDCardHandler.mkDir(DIRECTORY)) {
				exit("No write access to SD card");
			}
			setContentView(R.layout.activity_artifact_extract);
			browserHCheck = (CheckBox) findViewById(R.id.browserHCheck);
			browserSCheck = (CheckBox) findViewById(R.id.browserSCheck);
			calendarCheck = (CheckBox) findViewById(R.id.calendarCheck);
			callCheck = (CheckBox) findViewById(R.id.callCheck);
			contactCheck = (CheckBox) findViewById(R.id.contactCheck);
			mmsCheck = (CheckBox) findViewById(R.id.mmsCheck);
			smsCheck = (CheckBox) findViewById(R.id.smsCheck);
			selectAll(null);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			Toast t = Toast.makeText(getApplicationContext(), "Collected data saved to SD card",
					Toast.LENGTH_LONG);
			t.show();
		}
	};

	// selects all checkboxes
	public void selectAll(View v) {
		browserHCheck.setChecked(true);
		browserSCheck.setChecked(true);
		calendarCheck.setChecked(true);
		callCheck.setChecked(true);
		contactCheck.setChecked(true);
		mmsCheck.setChecked(true);
		smsCheck.setChecked(true);
	}

	// deselects all checkboxes
	public void unselectAll(View v) {
		browserHCheck.setChecked(false);
		browserSCheck.setChecked(false);
		calendarCheck.setChecked(false);
		callCheck.setChecked(false);
		contactCheck.setChecked(false);
		mmsCheck.setChecked(false);
		smsCheck.setChecked(false);
	}

	// executed if button "Execute" is clicked; checks all checkboxes and calls
	// the appropraite methods from the gatherer class in a background thread
	public void capture(View v) {
		if (nothingSelected()) {
			Toast t = Toast.makeText(getApplicationContext(),
					"Please select at least one type of data", Toast.LENGTH_LONG);
			t.show();
			return;
		}
		gatherer = new Gatherer(DIRECTORY, getContentResolver(), this);
		// start processdialog
		pd = ProgressDialog.show(this, "", "Gathering data", true, false);

		// run background thread
		new Thread(new Runnable() {
			public void run() {
				try {
					if (browserHCheck.isChecked()) {
						gatherer.getBrowserHistory();
					}
					if (browserSCheck.isChecked()) {
						gatherer.getBrowserSearch();
					}
					if (calendarCheck.isChecked()) {
						gatherer.getCalendar();
					}
					if (callCheck.isChecked()) {
						gatherer.getCalls();
					}
					if (contactCheck.isChecked()) {
						gatherer.getContacts();
					}
					if (mmsCheck.isChecked()) {
						gatherer.getMMS();
					}
					if (smsCheck.isChecked()) {
						gatherer.getSMS();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);

			}
		}).start();

	}

	public void showIOError(String where) {
		Toast t = Toast.makeText(getApplicationContext(),
				"SD card error while extracting " + where, Toast.LENGTH_LONG);
		t.show();
	}

	private boolean nothingSelected() {
		if (browserHCheck.isChecked() || browserSCheck.isChecked() || calendarCheck.isChecked()
				|| callCheck.isChecked() || contactCheck.isChecked() || mmsCheck.isChecked()
				|| smsCheck.isChecked()) {
			return false;
		}
		return true;
	}

	private void exit(String reason) {
		Toast t = Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG);
		t.show();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
