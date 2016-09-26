package com.imcc.attendancesystem;

import java.io.File;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import com.device.secugen.SecugenDevice;
import com.utility.UsbReceiver;
import com.utility.WebService;
import com.utility.database.DBManager;

public class SplashScreenActivity extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		//Get device object
		AttendanceSystemApplication.secugenDevice = SecugenDevice
				.getSecugenDevice();

		// Creating Folder Structure of Application for First Time
		File attendanceSystemFolder = new File(File.separator + "sdcard"
				+ File.separator + "AttendanceSystem");

		//Fingerprint Folder structure
		if (attendanceSystemFolder.exists() == true) {
			File fingeprintFolder = new File(File.separator + "sdcard"
					+ File.separator + "AttendanceSystem" + File.separator
					+ "Fingerprint");
			if (!fingeprintFolder.exists())
				fingeprintFolder.mkdir();
		} else {
			File fingerprintFolder = new File(File.separator + "sdcard"
					+ File.separator + "AttendanceSystem" + File.separator
					+ "Fingerprint");
			attendanceSystemFolder.mkdir();
			fingerprintFolder.mkdir();
		}

		//Photo Folder structure
		if (attendanceSystemFolder.exists() == true) {
			File photoFolder = new File(File.separator + "sdcard"
					+ File.separator + "AttendanceSystem" + File.separator
					+ "Photo");
			if (!photoFolder.exists())
				photoFolder.mkdir();
		} else {
			File photoFolder = new File(File.separator + "sdcard"
					+ File.separator + "AttendanceSystem" + File.separator
					+ "Photo");
			attendanceSystemFolder.mkdir();
			photoFolder.mkdir();
		}


		// Creating local Database for first time
		AttendanceSystemApplication.db = new DBManager(this);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				File adminFile = new File(File.separator + "sdcard"
						+ File.separator + "AttendanceSystem" + File.separator+"Fingerprint"+File.separator
						+ "admin.raw");

// 				webservice call
/*
  				if(AttendanceSystemApplication.db.isEmpty()==1)
					WebService.dumpDataOnClient(); 	//Download data from server through
*/
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreenActivity.this,
						LoginActivity.class);
				startActivity(i);

				if (!adminFile.exists()) {
					Intent registerAdmin = new Intent(SplashScreenActivity.this,
							RegisterAdminActivity.class);
					startActivity(registerAdmin);
					finish();
				}

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}