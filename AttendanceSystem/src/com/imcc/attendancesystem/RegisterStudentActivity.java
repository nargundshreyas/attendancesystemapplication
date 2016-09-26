package com.imcc.attendancesystem;

import java.io.File;

import SecuGen.FDxSDKPro.JSGFPLib;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imcc.attendancesystem.R;
import com.imcc.attendancesystem.R.color;
import com.utility.ClassData;
import com.utility.CustomSpinnerAdapter;
import com.utility.DialogActivity;
import com.utility.FingerprintData;
import com.utility.Student;
import com.utility.ValidationEngine;
import com.utility.database.DatabaseContract;

public class RegisterStudentActivity extends Activity implements OnClickListener
{

	private static final String TAG="AttendanceSystem";
	private Student _registerStudent;
	private int _selectedClassId;
	private ClassData[] _class;

	private EditText _editStudentRollNo;
	private Spinner _spinStudentClass;
	private EditText _editStudentFirstName;
	private EditText _editStudentLastName;
	private Button _btnRegister;
	private ImageView _imgViewThumbImpressionRegisterStudent;

	/*	@Override
	public void onBackPressed() {
		// Disable back button
	}
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_student);
		_btnRegister=(Button)findViewById(R.id.btnRegister);
		_imgViewThumbImpressionRegisterStudent=(ImageView)findViewById(R.id.imgViewThumbImpressionRegisterStudent);
		_editStudentFirstName=(EditText)findViewById(R.id.editStudentFirstName);
		_editStudentFirstName.setHintTextColor(color.color_white);
		_editStudentLastName=(EditText)findViewById(R.id.editStudentLastName);
		_editStudentLastName.setHintTextColor(color.color_white);
		_editStudentRollNo=(EditText)findViewById(R.id.editStudentRollNo);
		_editStudentRollNo.setHintTextColor(color.color_white);
		_spinStudentClass=(Spinner)findViewById(R.id.spinStudentClass);

		//Populate Student Class Spinner with Class Data
		Cursor cursorClassName=AttendanceSystemApplication.db.getResult("Select * from "+DatabaseContract.class_Table.TABLE_NAME);
		if(cursorClassName!=null && cursorClassName.getCount()!=0)
		{
			_class=new ClassData[cursorClassName.getCount()+1];
			_class[0]=new ClassData(-1,"Class");
			while(cursorClassName.moveToNext()==true)
			{
				_class[cursorClassName.getPosition()+1]=new ClassData(
						cursorClassName.getInt(cursorClassName.getColumnIndex(DatabaseContract.class_Table.COLUMN_NAME_C_ID))
						,cursorClassName.getString(cursorClassName.getColumnIndex(DatabaseContract.class_Table.COLUMN_NAME_C_NAME)));
				Log.i(TAG,""+cursorClassName.getPosition()+1+"      "
						+_class[cursorClassName.getPosition()+1].getClassId()+"       "
						+_class[cursorClassName.getPosition()+1].getClassName()+"\n");
			}
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		_btnRegister.setOnClickListener(this);

		if(_class!=null)
		{
			_spinStudentClass.setAdapter(
					new CustomSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, _class, "ClassData"));
			_spinStudentClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
				{
					TextView txtid=(TextView)view.findViewById(R.id.txtSpinnerCustomId);
					_selectedClassId=Integer.parseInt(txtid.getText().toString());			
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}
		else
		{
			Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
			intentDialogActivity.putExtra("dialogMessage", "No class Available");
			startActivity(intentDialogActivity);
		}
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnRegister)
		{
			String registerFileName=null;
			String firstName=_editStudentFirstName.getText().toString();
			String lastName=_editStudentLastName.getText().toString();
			String rollNo=_editStudentRollNo.getText().toString();

			if(_selectedClassId!=-1)
			{
				_registerStudent=new Student(rollNo,firstName,lastName,_selectedClassId,null,null);

				registerFileName = File.separator + "sdcard"
						+ File.separator + "AttendanceSystem" + File.separator
						+ "Fingerprint" + File.separator
						+ _registerStudent.getRollNo() + ".raw";
			}
			if(_selectedClassId!=-1)
			{
				boolean registrationResult=false;
				if(AttendanceSystemApplication.secugenDevice.isOpen()==false)
				{
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Biometric Device not Connected.Please try again.");
					startActivity(intentDialogActivity);
				}

				if(ValidationEngine.validate(_editStudentFirstName,ValidationEngine.VALIDATE_CHARACTERS_ONLY)==true
						&& ValidationEngine.validate(_editStudentLastName,ValidationEngine.VALIDATE_CHARACTERS_ONLY)==true
						&& ValidationEngine.validate(_editStudentRollNo, ValidationEngine.VALIDATE_ALPHANUMERIC_ONLY)==true
						&& (registrationResult=AttendanceSystemApplication.secugenDevice.register(_imgViewThumbImpressionRegisterStudent, null, registerFileName, 0))==true )
				{
					//Add Database Entry
					Log.i("Table","Roll="+_registerStudent.getRollNo()+" FName="+_registerStudent.getFirstName()
							+" LName="+_registerStudent.getLastName()+" Fingerprint=None Photo=None ClassId="+_registerStudent.getClassId());
					AttendanceSystemApplication.db.student_table_insert(_registerStudent.getRollNo(),_registerStudent.getFirstName(),_registerStudent.getLastName(),
							"No fingerprint file Path", "No Photo", _registerStudent.getClassId());

					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Registration Complete!!");
					startActivity(intentDialogActivity);
					finish();
				}
				else
				{
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					if(registrationResult==false)
						intentDialogActivity.putExtra("dialogMessage", "Bad Fingerprint Image quality.Please try again.");
					if(ValidationEngine.VALIDATE_ERROR==true)
						intentDialogActivity.putExtra("dialogMessage", ValidationEngine.VALIDATE_ERROR_MESSAGE);
					
					startActivity(intentDialogActivity);
				}
			}
			else
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				if(_selectedClassId==-1)
					intentDialogActivity.putExtra("dialogMessage", "Please Select Class and try again.");
				else
					intentDialogActivity.putExtra("dialogMessage", "Incorrect Input.");
				startActivity(intentDialogActivity);
			}
		}
	}
}
