package com.imcc.attendancesystem;

import com.imcc.attendancesystem.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AdminActivity extends Activity implements OnClickListener
{
	Button _btnRegisterStudent;
	Button _btnRegisterFaculty;
	Button _btnAddClass;
	Button _btnAddSubject;
	Button _btnAdminActivityLogout;
	private static int backPressCount=0;
	
	@Override
	public void onBackPressed() 
	{
		backPressCount++;
		if(backPressCount==1)
		{
			Toast.makeText(getApplication(),"Press back again to Logout.",Toast.LENGTH_LONG ).show();;
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
		setContentView(R.layout.activity_admin);
		_btnRegisterStudent=(Button)findViewById(R.id.btnRegisterStudent);
		_btnRegisterStudent.setOnClickListener(this);
		_btnRegisterFaculty=(Button)findViewById(R.id.btnRegisterFaculty);
		_btnRegisterFaculty.setOnClickListener(this);
		_btnAddClass=(Button)findViewById(R.id.btnAddClass);
		_btnAddClass.setOnClickListener(this);
		_btnAddSubject=(Button)findViewById(R.id.btnAddSubject);
		_btnAddSubject.setOnClickListener(this);
		_btnAdminActivityLogout=(Button)findViewById(R.id.btnAdminActivityLogout);
		_btnAdminActivityLogout.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.btnRegisterStudent:
			Intent registerStudent=new Intent(getApplicationContext(),RegisterStudentActivity.class);
			startActivity(registerStudent);
			break;
		case R.id.btnRegisterFaculty:
			Intent registerFaculty=new Intent(getApplicationContext(),RegisterFacultyActivity.class);
			startActivity(registerFaculty);
			break;
		case R.id.btnAddClass:
			Intent addClass=new Intent(getApplicationContext(),AddClassActivity.class);
			startActivity(addClass);
			break;
		case R.id.btnAddSubject:
			Intent addSubject=new Intent(getApplicationContext(),AddSubjectActivity.class);
			startActivity(addSubject);
			break;
		case R.id.btnAdminActivityLogout:
			Intent adminActivityLogout=new Intent(getApplicationContext(),Logout.class);
			startActivity(adminActivityLogout);
			break;		
		default:
				break;
		}
	}
}
