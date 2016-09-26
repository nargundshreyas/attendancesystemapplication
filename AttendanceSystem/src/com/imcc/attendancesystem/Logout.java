package com.imcc.attendancesystem;

import com.utility.DialogActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Logout extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPref=getSharedPreferences("AttendanceSystem", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sharedPref.edit();
		editor.putString("userId", "null");
		editor.putInt("classId", -1);
		editor.commit();
		
		AttendanceSystemApplication.db.close();
		
		Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
		intentDialogActivity.putExtra("dialogMessage", "You have been Logged out.");
		startActivity(intentDialogActivity);

		Intent intentLogin=new Intent(getApplicationContext(),LoginActivity.class);
		startActivity(intentLogin);
	}
}
