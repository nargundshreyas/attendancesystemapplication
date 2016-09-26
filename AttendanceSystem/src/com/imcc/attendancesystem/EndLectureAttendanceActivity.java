package com.imcc.attendancesystem;

import java.io.File;

import com.utility.DialogActivity;
import com.utility.FingerprintData;
import com.utility.database.DatabaseContract;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class EndLectureAttendanceActivity extends Activity implements OnClickListener
{
	private static String TAG="AttendanceSystem";
	private String _userId=null;
	private int _lectId=-1;

	private ImageView _imgViewFacultyThumbImpression;
	private Button _btnEndSession;
	private FingerprintData _fingerprint;
	private ImageView _imgViewRedCross;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_session);
		_btnEndSession=(Button)findViewById(com.imcc.attendancesystem.R.id.btnEndAttendanceSession);
		_btnEndSession.setOnClickListener(this);
		_imgViewFacultyThumbImpression=(ImageView)findViewById(R.id.imgViewFacultyThumbImpression);
		_imgViewRedCross=(ImageView)findViewById(R.id.imgViewRedCrossTrasparent);
		
		_lectId=getIntent().getIntExtra(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID, -1);
		
		SharedPreferences sharedPref=getSharedPreferences("AttendanceSystem", Context.MODE_PRIVATE);	// Getting Loggedin Faculty id
		_userId=sharedPref.getString("userId", "null");
		Log.d(TAG,"Logged in userId:"+_userId);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==com.imcc.attendancesystem.R.id.btnEndAttendanceSession)
		{
			if(AttendanceSystemApplication.secugenDevice.isOpen()==false)
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				intentDialogActivity.putExtra("dialogMessage", "Biometric Device not Connected.Please try again.");
				startActivity(intentDialogActivity);
			}


			String sourceDirectory= File.separator + "sdcard"+ File.separator + "AttendanceSystem"+
					File.separator+"Fingerprint";
			_fingerprint=AttendanceSystemApplication.secugenDevice.authenticate(sourceDirectory);

			if(_fingerprint.b!=null)
			{
				_imgViewFacultyThumbImpression.setImageBitmap(AttendanceSystemApplication.secugenDevice.toGrayscale(_fingerprint.b));

				if(_userId.equals(_fingerprint.dataBuffer))
				{
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
		}
	}
}