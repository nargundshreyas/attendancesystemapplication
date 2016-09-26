package com.imcc.attendancesystem;

import java.io.File;

import com.imcc.attendancesystem.R;
import com.utility.DialogActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class RegisterAdminActivity extends Activity
{
	private static final String TAG="AttendanceSystem";
	ImageView _imgViewRegisterAdmin;
	Button _btnRegisterAdmin;

	/*	@Override
	public void onBackPressed() {
		// Disable back button
	}
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_admin);
		_btnRegisterAdmin=(Button)findViewById(R.id.btnRegisterAdmin);
		_imgViewRegisterAdmin=(ImageView)findViewById(R.id.imgViewRegisterAdmin);
	}
	public void onResume()
	{
		super.onResume();
		if(AttendanceSystemApplication.secugenDevice.initSGFP(getApplicationContext(),AttendanceSystemApplication.permissionIntent)==0)
		{
			Log.i(TAG,"Exiting Application Device not attached!!!!!!!!");
			Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
			intentDialogActivity.putExtra("dialogMessage", "Please attach Secugen Biometric Scanner and Relaunch Application");
			startActivity(intentDialogActivity);
		}
		_btnRegisterAdmin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v)
			{
				String registerFileName=File.separator+"sdcard"+File.separator+"AttendanceSystem"
						+File.separator + "Fingerprint"+File.separator+"admin.raw";

				if(AttendanceSystemApplication.secugenDevice.isOpen()==false)
				{
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Please attach Secugen Biometric Scanner and Relaunch Application");
					startActivity(intentDialogActivity);
				}

				if(AttendanceSystemApplication.secugenDevice.register(_imgViewRegisterAdmin, null, registerFileName, 0)==true)
				{
					Log.i(TAG,"Admin Registered!!!!!!!!");
					Intent adminActivity=new Intent(getApplicationContext(),AdminActivity.class);
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Admin Registered!!You will be Redirected to Home Screen.");
					startActivity(intentDialogActivity);
					startActivity(adminActivity);
				}

			}
		});
	}
}