package com.imcc.attendancesystem;

import java.io.File;

import SecuGen.FDxSDKPro.JSGFPLib;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imcc.attendancesystem.R;
import com.imcc.attendancesystem.R.color;
import com.utility.DialogActivity;
import com.utility.Faculty;
import com.utility.FingerprintData;
import com.utility.Student;
import com.utility.ValidationEngine;
import com.utility.database.DatabaseContract;

public class RegisterFacultyActivity extends Activity implements OnClickListener
{

	private static final String TAG="AttendanceSystem";

	private Faculty _registerFaculty;

	private EditText _editFacultyFirstName;
	private EditText _editFacultyLastName;
	private Button _btnRegisterFaculty;
	private ImageView _imgViewThumbImpressionRegisterFaculty;
	
/*	@Override
	public void onBackPressed() {
		// Disable back button
	}
*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_faculty);
		_btnRegisterFaculty=(Button)findViewById(R.id.btnRegisterFaculty);
		_btnRegisterFaculty.setOnClickListener(this);
		_imgViewThumbImpressionRegisterFaculty=(ImageView)findViewById(R.id.imgViewThumbImpressionRegisterFaculty);
		_editFacultyFirstName=(EditText)findViewById(R.id.editFacultyFirstName);
		_editFacultyFirstName.setHintTextColor(color.color_white);
		_editFacultyLastName=(EditText)findViewById(R.id.editFacultyLastName);
		_editFacultyLastName.setHintTextColor(color.color_white);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		_btnRegisterFaculty.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnRegisterFaculty)
		{
			int facultyId=AttendanceSystemApplication.db.getMaxId(DatabaseContract.faculty_Table.TABLE_NAME
					, DatabaseContract.faculty_Table.COLUMN_NAME_F_ID)+1;
			String firstName=_editFacultyFirstName.getText().toString();
			String lastName=_editFacultyLastName.getText().toString();
			_registerFaculty=new Faculty(facultyId,firstName,lastName,null);
			String registerFileName = File.separator + "sdcard"
					+ File.separator + "AttendanceSystem" + File.separator + "Fingerprint" + File.separator
					+"F"
					+ _registerFaculty.getFacultyId() + ".raw";
			Log.d(TAG,"Filename RegisterStudent: "+registerFileName);
			if(AttendanceSystemApplication.secugenDevice.isOpen()==false)
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				intentDialogActivity.putExtra("dialogMessage","Biometric Device not Connected.Please try again.");
				startActivity(intentDialogActivity);
			}

			if(ValidationEngine.validate(_editFacultyFirstName, ValidationEngine.VALIDATE_CHARACTERS_ONLY)
				&& ValidationEngine.validate(_editFacultyLastName, ValidationEngine.VALIDATE_CHARACTERS_ONLY)
				&& AttendanceSystemApplication.secugenDevice.register(_imgViewThumbImpressionRegisterFaculty, null, registerFileName, 0)==true)
			{
				//Add Database Entry
				Log.i(TAG,"FId="+_registerFaculty.getFacultyId()+" FFName="+ _registerFaculty.getFacultyFirstName()
						+" FLName="+_registerFaculty.getFacultyLastName()+"Photo=No Photo");
				AttendanceSystemApplication.db.faculty_table_insert(_registerFaculty.getFacultyId(), _registerFaculty.getFacultyFirstName(),
						_registerFaculty.getFacultyLastName(), "No Photo");

				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				intentDialogActivity.putExtra("dialogMessage","Registration Complete!!");
				startActivity(intentDialogActivity);
			}
			else
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				if(ValidationEngine.VALIDATE_ERROR==true)
					intentDialogActivity.putExtra("dialogMessage",ValidationEngine.VALIDATE_ERROR_MESSAGE);
				startActivity(intentDialogActivity);
			}
		}
	}
}
