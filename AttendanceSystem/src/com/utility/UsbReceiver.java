package com.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class UsbReceiver extends BroadcastReceiver
{
	private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
	private static final String TAG = "AttendanceSystem";
	
	@Override
	public void onReceive(Context context,Intent intent)
	{
		String action = intent.getAction();
		Log.d(TAG,"UsbReceiver!!");
		if (ACTION_USB_PERMISSION.equals(action))
		{
			synchronized (this)
			{
				UsbDevice device = (UsbDevice) intent
						.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
				{
					if (device != null)
					{
						Log.d(TAG, "UsbReceiver permission granted for device "+device);
					}
				} 
				else
					Log.e(TAG,"UsbReceiver permission denied for device "+ device);		
			}

		}
	}
}