package com.imcc.attendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.utility.database.DatabaseContract;

public class ShowAttendanceActivity extends Activity {
	private int _selectedLectId;
	private int _selectedClassId;
	private int _selectedSubjectId;
	private int _totalStudentCount;
	private int _presentStudentCount;
	private int _absentStudentCount;
	private TextView _txtViewStudents[];
	private TableLayout _tblLayout;
	private TableRow _tblRow[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_attendance);

		_tblLayout=(TableLayout)findViewById(R.id.tblStudentAttendance);
		
		SharedPreferences sharedPref=getSharedPreferences("ViewAttendanceInfo", Context.MODE_PRIVATE);
		// Getting Logged in Faculty id
		_selectedLectId=sharedPref.getInt("l_id", -1);
		_selectedClassId=sharedPref.getInt("class_id", -1);
		_selectedSubjectId=sharedPref.getInt("sub_id", -1);
		System.out.println("Lecture id : "+ _selectedLectId);

		// Count totoal students in selcted class
		int totalStudentCount=-1,presentStudentCount=-1,absentStudentCount=-1;
		Cursor cursorTotalStudentCount=
				AttendanceSystemApplication.db.getResult("Select count ( "+
						DatabaseContract.student_Table.COLUMN_NAME_S_ID+
						" ) from "+
						DatabaseContract.student_Table.TABLE_NAME+
						" where "+DatabaseContract.class_Table.COLUMN_NAME_C_ID+
						" = "+ _selectedClassId);
		if(cursorTotalStudentCount.getCount()>0)
		{
			cursorTotalStudentCount.moveToFirst();			
			_totalStudentCount=cursorTotalStudentCount.getInt(0);
			System.out.println("Total Students : "+ _totalStudentCount);
		}

		//Query To find out present students on selcted lecture
		Cursor cursorAttendance=AttendanceSystemApplication.db.getResult("Select * from " +
				DatabaseContract.attendance_Table.TABLE_NAME +
				" where "+DatabaseContract.lecture_Table.COLUMN_NAME_L_ID +
				" = "+_selectedLectId);
		if(cursorAttendance.getCount()>0)
		{
			Cursor cursorPresentstudents=
					AttendanceSystemApplication.db.getResult("Select count ( "+
							DatabaseContract.lecture_Table.COLUMN_NAME_L_ID +
							" ) "+" from " +
							DatabaseContract.attendance_Table.TABLE_NAME +
							" where "+DatabaseContract.lecture_Table.COLUMN_NAME_L_ID +
							" = "+_selectedLectId);
			if(cursorPresentstudents.getCount()>0)
			{
				cursorPresentstudents.moveToFirst();
				_presentStudentCount=cursorPresentstudents.getInt(0);
				_absentStudentCount=_totalStudentCount-_presentStudentCount;
				System.out.println("Present studnets: "+_presentStudentCount);
				System.out.println("Absent studnets: "+_absentStudentCount);
			}

			//Display Total Present and Absent Students on XML
			TextView txtTotalStudents=(TextView)findViewById(R.id.txtTotalStudents);
			TextView txtPresentStudents=(TextView)findViewById(R.id.txtPresentStudents);
			TextView txtAbsentStudents=(TextView)findViewById(R.id.txtAbsentStudents);
			txtTotalStudents.setText("Total Students : "+_totalStudentCount);
			txtPresentStudents.setText("Present Sutdents : "+_presentStudentCount);
			txtAbsentStudents.setText("Absent Students : "+_absentStudentCount);
			txtTotalStudents.setVisibility(View.VISIBLE);
			txtPresentStudents.setVisibility(View.VISIBLE);
			txtAbsentStudents.setVisibility(View.VISIBLE);

//			Allocate memory for Texviews to display student record
			_tblRow=new TableRow[cursorAttendance.getCount()+1];
			_txtViewStudents=new TextView[cursorAttendance.getCount()+1];
			
			int i=0;
			for(cursorAttendance.move(0);cursorAttendance.moveToNext();i++)
			{
				if(_tblRow!=null)
				{
					_tblRow[i]=new TableRow(getApplicationContext());
					TableRow.LayoutParams tblRowParameters=new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, R.dimen.scr_show_attendance_column_height);
//					_tblRow[i].setLayoutParams(tblRowParameters);
					_txtViewStudents[i]=new TextView(getApplicationContext());
//					_txtViewStudents[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
//					_txtViewStudents[i].setTextSize(R.dimen.font_size);
					_txtViewStudents[i].setTextColor(Color.WHITE);
				}
				String rollNo=cursorAttendance.getString(
						cursorAttendance.getColumnIndex(
								DatabaseContract.student_Table.COLUMN_NAME_S_ID));
				Cursor cursorStudentName=AttendanceSystemApplication.db.getResult("Select * from "+
						DatabaseContract.student_Table.TABLE_NAME+" where "+
						DatabaseContract.student_Table.COLUMN_NAME_S_ID+" = "+rollNo);
				if(cursorStudentName.moveToFirst())
				{
					String firstName=cursorStudentName.getString(cursorStudentName.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_FIRST_NAME));
					String lastName=cursorStudentName.getString(cursorStudentName.getColumnIndex(DatabaseContract.student_Table.COLUMN_NAME_S_LAST_NAME));
					String studentRecord=rollNo+" "+firstName+" "+lastName;
					System.out.println("Student : "+studentRecord);
					
					_txtViewStudents[i].setText(studentRecord);
					_txtViewStudents[i].setVisibility(View.VISIBLE);
					_tblRow[i].addView(_txtViewStudents[i]);
					_tblLayout.addView(_tblRow[i]);
				}
			}
		}
	}
}