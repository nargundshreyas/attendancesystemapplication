package com.imcc.attendancesystem;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.imcc.attendancesystem.R;
import com.utility.DialogActivity;
import com.utility.FingerprintData;

public class LoginActivity extends Activity implements android.view.View.OnClickListener 
{
	private static final String TAG="AttendanceSystem";

	private Bitmap _grayBitmap;
	private FingerprintData _fingerprint;

	// UI Components
	private Button _btnLogin;
	private ImageView _imgViewThumbImpression;
	private ImageView _imgViewRedCross;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		_btnLogin = (Button) findViewById(R.id.btnLogin);
		_imgViewThumbImpression = (ImageView) findViewById(R.id.imgViewThumbImpressionLogin);
		_imgViewRedCross = (ImageView) findViewById(R.id.imgViewRedCross);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if(AttendanceSystemApplication.secugenDevice.initSGFP(getApplicationContext(),AttendanceSystemApplication.permissionIntent)==0)
		{
			Log.i(TAG,"Exiting Application Device not attached!!!!!!!!");
			Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
			intentDialogActivity.putExtra("dialogMessage", "Please attach Secugen Biometric Scanner and Relaunch Application");
			startActivity(intentDialogActivity);
			finish();
		}
		_imgViewThumbImpression.setImageBitmap(_grayBitmap);
		_btnLogin.setOnClickListener(this);		

		//Start Auto Capture Feature
		//AttendanceSystemApplication.secugenDevice.autoCapture.start();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	@Override
	public void onClick(View v)
	{

		if (v.getId() == R.id.btnLogin)
		{
			String sourceDirectory= File.separator + "sdcard"+ File.separator + "AttendanceSystem" + File.separator + "Fingerprint" ;

			if(AttendanceSystemApplication.secugenDevice.isOpen()==true)
			{
				_fingerprint=AttendanceSystemApplication.secugenDevice.authenticate(sourceDirectory);
				if(_fingerprint.b!=null)
				{
					_imgViewThumbImpression.setImageBitmap(AttendanceSystemApplication.secugenDevice.toGrayscale(_fingerprint.b));

					//getting sharedpreference handle
					SharedPreferences sharedPref = getSharedPreferences("AttendanceSystem",Context.MODE_PRIVATE);
					SharedPreferences.Editor editor=sharedPref.edit();

					if(_fingerprint.matchId==0)
					{
						editor.putString("userId", _fingerprint.dataBuffer);
						editor.commit();
						Log.d(TAG,"userId:"+sharedPref.getString("userId", "null"));
						_imgViewRedCross.setImageResource(R.drawable.green_tick);
						_imgViewRedCross.setVisibility(View.VISIBLE);
						Intent admin=new Intent(getBaseContext(),AdminActivity.class);
						startActivity(admin);
					}
					else
						if(_fingerprint.matchId==1)
						{
							editor.putString("userId", _fingerprint.dataBuffer);
							editor.commit();
							Log.d(TAG,"userId:"+sharedPref.getString("userId", "null"));
							_imgViewRedCross.setImageResource(R.drawable.green_tick);
							_imgViewRedCross.setVisibility(View.VISIBLE);
							Intent faculty=new Intent(getBaseContext(),FacultyActivity.class);
							startActivity(faculty);
						}
						else
						{
							//Set Red transparent cross on image view

							_imgViewRedCross.setImageResource(R.drawable.red_cross_transparent);
							_imgViewRedCross.setVisibility(View.VISIBLE);
							Log.i(TAG,"match not found!!!!!");
							Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
							intentDialogActivity.putExtra("dialogMessage", "Unknown Fingerprint Pattern.Please try again!!");
							startActivity(intentDialogActivity);
						
						}
				}
				else
				{
					Log.e(TAG, "Bitmap is null!");
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Could not capture fingerprint image.Please try again!!");
					startActivity(intentDialogActivity);
				}
			}
			else
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				intentDialogActivity.putExtra("dialogMessage", "Please attach Secugen Biometric Scanner and Relaunch Application");
				startActivity(intentDialogActivity);
				finish();
			}
		}

	}
}