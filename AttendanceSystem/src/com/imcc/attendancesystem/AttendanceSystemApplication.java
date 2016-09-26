package com.imcc.attendancesystem;

import java.io.File;

import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.device.secugen.SecugenDevice;
import com.utility.UsbReceiver;
import com.utility.WebService;
import com.utility.database.DBManager;

public class AttendanceSystemApplication extends Application 
{
	public static SecugenDevice secugenDevice;

	// Receiver Components
	public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	public static BroadcastReceiver usbReceiver;
	public static IntentFilter filter;
	public static PendingIntent permissionIntent;
	public static DBManager db;

	@Override
	public void onCreate() {
		super.onCreate();
		// permissions for device
		AttendanceSystemApplication.permissionIntent = PendingIntent
				.getBroadcast(this, 0, new Intent(
						AttendanceSystemApplication.ACTION_USB_PERMISSION), 0);
		AttendanceSystemApplication.filter = new IntentFilter(
				AttendanceSystemApplication.ACTION_USB_PERMISSION);

		AttendanceSystemApplication.usbReceiver = new UsbReceiver();

		this.registerReceiver(AttendanceSystemApplication.usbReceiver,
				AttendanceSystemApplication.filter);

	}
}