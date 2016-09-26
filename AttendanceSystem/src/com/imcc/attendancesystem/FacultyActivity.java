package com.imcc.attendancesystem;

import com.imcc.attendancesystem.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FacultyActivity extends Activity implements android.view.View.OnClickListener
{
	Button _btnStartLectureAttendance;
	Button _btnViewAttendance;
	Button _btnFacultyActivityLogout;
	private static int backPressCount=0;
	
	@Override
	public void onBackPressed() 
	{
		backPressCount++;
		if(backPressCount==1)
		{
			Toast.makeText(getApplication(),"Press back again to Logout.",Toast.LENGTH_LONG).show();
		}
		if(backPressCount==2)
		{
			Intent intentLogout=new Intent(getApplicationContext(),Logout.class);
			startActivity(intentLogout);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		backPressCount=0;
		setContentView(R.layout.activity_faculty);
		_btnStartLectureAttendance=(Button)findViewById(R.id.btnStartLectureAttendance);
		_btnStartLectureAttendance.setOnClickListener(this);
		_btnViewAttendance=(Button)findViewById(R.id.btnViewAttendance);
		_btnViewAttendance.setOnClickListener(this);
		_btnFacultyActivityLogout=(Button)findViewById(R.id.btnFacultyActivityLogout);
		_btnFacultyActivityLogout.setOnClickListener(this);
	}

	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.btnViewAttendance:
			Intent viewAttendanceActivity=new Intent(getApplicationContext(),ViewAttendanceActivity.class);
			startActivity(viewAttendanceActivity);
			break;
		case R.id.btnStartLectureAttendance:
			Intent startLectureAttendance=new Intent(getApplicationContext(),StartLectureAttendanceActivtiy.class);
			startActivity(startLectureAttendance);
			break;
		case R.id.btnFacultyActivityLogout:
			Intent adminActivityLogout=new Intent(getApplicationContext(),Logout.class);
			startActivity(adminActivityLogout);
			break;		
		default:
			break;

		}
	}
}
