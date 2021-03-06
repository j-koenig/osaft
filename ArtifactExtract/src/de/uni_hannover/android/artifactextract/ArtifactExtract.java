package de.uni_hannover.android.artifactextract;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import de.uni_hannover.android.artifactextract.util.SDCardHandler;

/**
 * 
 * Main Activity
 * 
 * @author Jannis Koenig
 * 
 */
public class ArtifactExtract extends Activity {

	private ProgressDialog pd;
	private CheckBox browserHCheck, browserSCheck, calendarCheck, callCheck, contactCheck, mmsCheck, smsCheck;
	private Gatherer gatherer;
	// all found data is saved in the directory "artifacts" on the sd card
	private final String DIRECTORY = Environment.getExternalStorageDirectory() + "/artifacts/";
	
	@Override
	public void onResume() {
//		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
//		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
//		lock.disableKeyguard();
//		Log.d("bypass", "resume da");
		super.onResume();
	}

	@Override
	/**
	 * Gets called on start of app. Checks availability of SD card and initiates GUI objects. 
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//shows the app, even if screen lock is active
		this.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);		

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
			try {
				SDCardHandler.mkDir(DIRECTORY);
			} catch (IOException e) {
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

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			Toast t = Toast.makeText(getApplicationContext(), "Collected data saved to SD card", Toast.LENGTH_LONG);
			t.show();
		}
	};

	/**
	 * selects all checkboxes
	 */
	public void selectAll(View v) {
		browserHCheck.setChecked(true);
		browserSCheck.setChecked(true);
		calendarCheck.setChecked(true);
		callCheck.setChecked(true);
		contactCheck.setChecked(true);
		mmsCheck.setChecked(true);
		smsCheck.setChecked(true);
	}

	/**
	 * deselects all checkboxes
	 */
	public void unselectAll(View v) {
		browserHCheck.setChecked(false);
		browserSCheck.setChecked(false);
		calendarCheck.setChecked(false);
		callCheck.setChecked(false);
		contactCheck.setChecked(false);
		mmsCheck.setChecked(false);
		smsCheck.setChecked(false);
	}

	/**
	 * executed if button "Execute" is clicked; checks all checkboxes and calls
	 * the appropriate methods from the gatherer class in a background thread
	 * 
	 */
	public void capture(View v) {
		if (nothingSelected()) {
			Toast t = Toast.makeText(getApplicationContext(), "Please select at least one type of data", Toast.LENGTH_LONG);
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

	/**
	 * Shows a toast with an error message, when there was an error while
	 * accessing the sd card
	 * 
	 * @param where
	 *            specifies in which method the error occured
	 */
	public void showIOError(String where) {
		Toast t = Toast.makeText(getApplicationContext(), "SD card error while extracting " + where, Toast.LENGTH_LONG);
		t.show();
	}

	private boolean nothingSelected() {
		if (browserHCheck.isChecked() || browserSCheck.isChecked() || calendarCheck.isChecked() || callCheck.isChecked()
				|| contactCheck.isChecked() || mmsCheck.isChecked() || smsCheck.isChecked()) {
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
	/**
	 * closes the app when back button pressed
	 */
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}
