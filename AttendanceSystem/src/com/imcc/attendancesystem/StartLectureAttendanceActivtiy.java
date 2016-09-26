package com.imcc.attendancesystem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imcc.attendancesystem.R;
import com.utility.ClassData;
import com.utility.CustomSpinnerAdapter;
import com.utility.DialogActivity;
import com.utility.SubjectData;
import com.utility.database.DatabaseContract;

public class StartLectureAttendanceActivtiy extends Activity implements android.view.View.OnClickListener
{
	private static final String TAG="AttendanceSystem";

	private String date;
	private String time;
	private int _selectedClassId=-1;
	private int _selectedSubjectId=-1;
	private ClassData[] _class;
	private SubjectData[] _subject;
	private int _facultyId;
	private String _userId;
	private TextView _txtLectureSubject;
	private EditText _editLectureTime;
	private EditText _editLectureDate;
	private Spinner _spinLectureSubject;
	private Spinner _spinLectureClass;
	private Button _btnStartSession;

/*	@Override
	public void onBackPressed() {
		// Disable back button
	}
*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_lecture_attendance);

		_txtLectureSubject=(TextView)findViewById(R.id.txtLectureSubject);
		_txtLectureSubject.setVisibility(View.INVISIBLE);
		_editLectureTime = (EditText) findViewById(R.id.editLectureTime);
		_editLectureDate = (EditText) findViewById(R.id.editLectureDate);
		_spinLectureSubject=(Spinner)findViewById(R.id.spinLectureSubject);
		_spinLectureSubject.setVisibility(View.INVISIBLE);	//Make Subject Spinner Invisible untill class is selected
		_spinLectureClass=(Spinner)findViewById(R.id.spinLectureClass);
		_btnStartSession = (Button) findViewById(R.id.btnStartSession);
		_btnStartSession.setOnClickListener(this);
	
		SharedPreferences sharedPref=getSharedPreferences("AttendanceSystem", Context.MODE_PRIVATE);	// Getting Loggedin Faculty id
		_userId=sharedPref.getString("userId", "null");
		if(_userId.startsWith("F")==true)
		{
			_userId=_userId.substring(_userId.indexOf('F')+1);
		}
		_facultyId=Integer.parseInt(_userId);
		Log.d(TAG,"Logged in FacultyId:"+_facultyId);
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		//Getting data from database to populate ClassName Spinner
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
			}

			_spinLectureClass.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item
					, _class, "ClassData"));
			_spinLectureClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
				{
					_spinLectureSubject.setVisibility(View.INVISIBLE);
					TextView txtid=(TextView)view.findViewById(R.id.txtSpinnerCustomId);
					_selectedClassId=Integer.parseInt(txtid.getText().toString());
					Log.d(TAG,"ID=============="+_selectedClassId);
					if(_selectedClassId!=-1)
					{
						Cursor cursorSubjectName=AttendanceSystemApplication.db.getResult("Select * from "+DatabaseContract.subject_Table.TABLE_NAME
								+" where "+DatabaseContract.class_Table.COLUMN_NAME_C_ID+" = "+_selectedClassId+" AND "
								+DatabaseContract.faculty_Table.COLUMN_NAME_F_ID+" = "+_facultyId);
						if(cursorSubjectName!=null && cursorSubjectName.getCount()!=0)
						{
							Log.d(TAG,"********************************************");
							_subject=new SubjectData[cursorSubjectName.getCount()+1];
							_subject[0]=new SubjectData(-1,"Subject",-1,_facultyId);
							while(cursorSubjectName.moveToNext()==true)
							{
								_subject[cursorSubjectName.getPosition()+1]=new SubjectData(
										cursorSubjectName.getInt(cursorSubjectName.getColumnIndex(DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID))
										,cursorSubjectName.getString(cursorSubjectName.getColumnIndex(DatabaseContract.subject_Table.COLUMN_NAME_SUB_NAME))
										,cursorSubjectName.getInt(cursorSubjectName.getColumnIndex(DatabaseContract.class_Table.COLUMN_NAME_C_ID))
										,cursorSubjectName.getInt(cursorSubjectName.getColumnIndex(DatabaseContract.faculty_Table.COLUMN_NAME_F_ID)));
							}
							//Populate Subject Spinner
							_spinLectureSubject.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item
									, _subject, "SubjectData"));
							_spinLectureSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
								@Override
								public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
								{
									TextView txtid=(TextView)view.findViewById(R.id.txtSpinnerCustomId);
									_selectedSubjectId=Integer.parseInt(txtid.getText().toString());											
								}

								@Override
								public void onNothingSelected(AdapterView<?> parent)
								{
								}
							});
							_spinLectureSubject.setVisibility(View.VISIBLE);
							_txtLectureSubject.setVisibility(View.VISIBLE);
						}
					}
					else
					if(_selectedClassId!=-1)
					{
						Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
						intentDialogActivity.putExtra("dialogMessage", "No Subject Found");
						startActivity(intentDialogActivity);
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{
				}
			});
		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Calendar currentDateTime = Calendar.getInstance();
		Date curdate = currentDateTime.getTime();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		date = dateformat.format(curdate);
		dateformat = new SimpleDateFormat("hh:mm:ss");
		time = dateformat.format(curdate);
		_editLectureTime.setText(time);
		_editLectureTime.setEnabled(false);
		_editLectureDate.setText(date);
		_editLectureDate.setEnabled(false);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btnStartSession:
			if(_selectedClassId!=-1 && _selectedSubjectId!=-1)
			{
				int lec_id=AttendanceSystemApplication.db.getMaxId(DatabaseContract.lecture_Table.TABLE_NAME
						,DatabaseContract.lecture_Table.COLUMN_NAME_L_ID)+1;
				Log.i("AttendanceSystem","Lecture Id="+lec_id+" ClassId="+_selectedClassId+" SubId="+_selectedSubjectId
						+" Date="+date+" Time="+time);

				AttendanceSystemApplication.db.lecture_table_insert(lec_id, _selectedClassId,_selectedSubjectId, date, time);
				
				
				
				//getting sharedpreference handle
				SharedPreferences sharedPref = getSharedPreferences("AttendanceSystem",Context.MODE_PRIVATE);
				SharedPreferences.Editor editor=sharedPref.edit();
				editor.putInt("classId", _selectedClassId);
				editor.commit();
				Intent lectureAttendance=new Intent(getApplicationContext(),LectureAttendanceActivity.class);
				lectureAttendance.putExtra(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID, lec_id);
				startActivity(lectureAttendance);
			}
			else
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				startActivity(intentDialogActivity);
				if(_selectedClassId==-1)
					intentDialogActivity.putExtra("dialogMessage", "Please Select class and try agian");
				else
				{
					if(_selectedSubjectId==-1)
						intentDialogActivity.putExtra("dialogMessage", "Please Select subject and try agian");
				}
			}
			break;
		}
	}
}
