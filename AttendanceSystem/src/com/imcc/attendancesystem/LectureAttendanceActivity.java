package com.imcc.attendancesystem;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.utility.DialogActivity;
import com.utility.FingerprintData;
import com.utility.Student;
import com.utility.database.DatabaseContract;

public class LectureAttendanceActivity extends Activity implements android.view.View.OnClickListener
{
	private static final String TAG="AttendanceSystem";
	private FingerprintData _fingerprint;

	private ImageView _imgViewLectureAttendance;
	private ImageView _imgViewRedCrossTrasparent;
	private Button _btnMarkAttendance;
	private Button _btnEndSession;
	private TextView _txtLectureAttendanceStudentName;
	private ImageView _imgViewStudentPhoto;
	private int _lectId;
	private int _selectedClassId;

	@Override
	public void onBackPressed() {
		// Disable back button
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lecture_attendance);
		_lectId=getIntent().getIntExtra(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID, -1);
		Log.d(TAG,"L_id="+_lectId);
		_imgViewLectureAttendance=(ImageView)findViewById(R.id.imgViewLectureAttendance);
		_imgViewStudentPhoto=(ImageView)findViewById(R.id.imgViewStudentPhoto);
		_imgViewRedCrossTrasparent=(ImageView)findViewById(R.id.imgViewRedCrossTrasparent);
		_btnMarkAttendance=(Button)findViewById(R.id.btnMarkAttendance);
		_btnMarkAttendance.setOnClickListener(this);
		_btnEndSession=(Button)findViewById(R.id.btnEndLecture);
		_btnEndSession.setOnClickListener(this);
		_txtLectureAttendanceStudentName=(TextView)findViewById(R.id.txtLectureAttendanceStudentName);

		SharedPreferences sharedPref=getSharedPreferences("AttendanceSystem",Context.MODE_PRIVATE);
		_selectedClassId=sharedPref.getInt("classId", -1);
	}

	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.btnMarkAttendance:
			_imgViewRedCrossTrasparent.setVisibility(View.GONE);
			_imgViewStudentPhoto.setVisibility(View.GONE);
			_imgViewLectureAttendance.setVisibility(View.INVISIBLE);
			_txtLectureAttendanceStudentName.setVisibility(View.INVISIBLE);
			String matchedUserId=null;
			String sourceDirectory= File.separator + "sdcard"+ File.separator + "AttendanceSystem"+
					File.separator+"Fingerprint";
			if(AttendanceSystemApplication.secugenDevice.isOpen()==false)
			{
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				intentDialogActivity.putExtra("dialogMessage", "Biometric Device not Connected.Please try again.");
				startActivity(intentDialogActivity);
			}
			_fingerprint=AttendanceSystemApplication.secugenDevice.authenticate(sourceDirectory);
			if(_fingerprint.b!=null && _fingerprint.matchId!=-1)
			{
				_imgViewLectureAttendance.setImageBitmap(AttendanceSystemApplication.secugenDevice.toGrayscale(_fingerprint.b));
				_imgViewLectureAttendance.setVisibility(View.VISIBLE);
				if(_fingerprint.matchId==2)
				{
					if(_fingerprint.dataBuffer!=null)
						matchedUserId=_fingerprint.dataBuffer;
					Cursor cursorStudentInfo=AttendanceSystemApplication.db.getResult("Select * from "+DatabaseContract.student_Table.TABLE_NAME
							+" where "+DatabaseContract.student_Table.COLUMN_NAME_S_ID+" = "+matchedUserId);
					_txtLectureAttendanceStudentName.setTextColor(Color.WHITE);
					if(cursorStudentInfo!=null  && _selectedClassId!=-1 )
					{
						if(cursorStudentInfo.moveToFirst()==true)
						{
							Student student=new Student(cursorStudentInfo.getString(cursorStudentInfo.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_ID)),
									cursorStudentInfo.getString(cursorStudentInfo.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_FIRST_NAME)),
									cursorStudentInfo.getString(cursorStudentInfo.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_LAST_NAME)),
									cursorStudentInfo.getInt(cursorStudentInfo.getColumnIndex(DatabaseContract.class_Table.COLUMN_NAME_C_ID)),
									cursorStudentInfo.getString(cursorStudentInfo.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_FINGERPRINT)),
									cursorStudentInfo.getString(cursorStudentInfo.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_PHOTO)));
							Cursor cursorClassName=AttendanceSystemApplication.db.getResult("Select "+DatabaseContract.class_Table.COLUMN_NAME_C_NAME+" from "
									+DatabaseContract.class_Table.TABLE_NAME+" where "+DatabaseContract.class_Table.COLUMN_NAME_C_ID+" = "
									+student.getClassId());
							if(cursorClassName.moveToFirst()==true  && student.getClassId()==_selectedClassId )
							{
								//Get student name, Class name , student photo for the present student and set visibility of txtView and imgView to true
								String className=cursorClassName.getString(cursorClassName.getColumnIndex(DatabaseContract.class_Table.COLUMN_NAME_C_NAME));
								_txtLectureAttendanceStudentName.setText(student.getFirstName()+" "+student.getLastName()+"\n"+className);
								_txtLectureAttendanceStudentName.setVisibility(View.VISIBLE);
								if(cursorStudentInfo.getString(
										cursorStudentInfo.getColumnIndex(
												DatabaseContract.student_Table.COLUMN_NAME_S_PHOTO)).equals("none")==true)
								{
									File fileStudentPhoto=new File(File.separator+"sdcard"+File.separator
											+"AttendanceSystem"+File.separator+"Photo"+File.separator+student.getRollNo());
									if(fileStudentPhoto.exists())
									{
										Bitmap bmStudentPhoto=BitmapFactory.decodeFile(fileStudentPhoto.getAbsolutePath());
										_imgViewStudentPhoto.setImageBitmap(bmStudentPhoto);
									}
								}
								
								_imgViewRedCrossTrasparent.setImageResource(R.drawable.green_tick);
								_imgViewRedCrossTrasparent.setVisibility(View.VISIBLE);
								_imgViewStudentPhoto.setVisibility(View.VISIBLE);

								Log.d(TAG,student.getFirstName()+" "+student.getLastName()+"\n"+className);
								_txtLectureAttendanceStudentName.setEnabled(true);
								Log.i("Table","LectId="+_lectId+" StudId="+_fingerprint.dataBuffer);					

								//Check if student has been marked present already before marking him present
								Cursor cursorCheckIfMarkedPresent=AttendanceSystemApplication.db.getResult("Select * from "+
										DatabaseContract.attendance_Table.TABLE_NAME+" where "+DatabaseContract.lecture_Table.COLUMN_NAME_L_ID+
										" ="+_lectId+" AND "+DatabaseContract.student_Table.COLUMN_NAME_S_ID+" ="+_fingerprint.dataBuffer);
								if(cursorCheckIfMarkedPresent.moveToFirst()==false)
									AttendanceSystemApplication.db.attendance_table_insert(_lectId, _fingerprint.dataBuffer);
								else
								{
									Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
									intentDialogActivity.putExtra("dialogMessage", "Already marked Present");
									startActivity(intentDialogActivity);
								}
							}
							else
							{
								_imgViewRedCrossTrasparent.setImageResource(R.drawable.red_cross_transparent);
								_imgViewRedCrossTrasparent.setVisibility(View.VISIBLE);
								Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
								intentDialogActivity.putExtra("dialogMessage", "No Student found in mentioned class.Please try again.");
								startActivity(intentDialogActivity);
							}
						}
					}
				}
				else
				{
					_imgViewLectureAttendance.setImageBitmap(AttendanceSystemApplication.secugenDevice.toGrayscale(_fingerprint.b));
					_imgViewLectureAttendance.setVisibility(View.VISIBLE);
					Log.i(TAG,"match not found!!!!!");
					_imgViewRedCrossTrasparent.setImageResource(R.drawable.red_cross_transparent);
					_imgViewRedCrossTrasparent.setVisibility(View.VISIBLE);
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Unkown Fingerprint Pattern.Please try again!!");
					startActivity(intentDialogActivity);
				}
			}
			else
			{
				Log.i(TAG,"match not found!!!!!");
				_imgViewRedCrossTrasparent.setImageResource(R.drawable.red_cross_transparent);
				_imgViewRedCrossTrasparent.setVisibility(View.VISIBLE);
				Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
				intentDialogActivity.putExtra("dialogMessage", "Unkown Fingerprint Pattern.Please try again!!");
				startActivity(intentDialogActivity);
			}
			break;
			
		case R.id.btnEndLecture:
			Intent endSessionActivity=new Intent(getApplicationContext(),EndLectureAttendanceActivity.class);
			endSessionActivity.putExtra(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID, _lectId);
			startActivity(endSessionActivity);
			break;
		default:
			break;
		}
	}
}
