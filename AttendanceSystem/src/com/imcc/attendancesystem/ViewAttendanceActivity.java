package com.imcc.attendancesystem;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.utility.ClassData;
import com.utility.CustomSpinnerAdapter;
import com.utility.DialogActivity;
import com.utility.LectureData;
import com.utility.SubjectData;
import com.utility.database.DatabaseContract;

public class ViewAttendanceActivity extends Activity implements OnClickListener 
{
	private static final String TAG="AttendanceSystem";
	private ImageButton ibCalendar;
	private Calendar cal;
	private int day;
	private int month;
	private int year;
	private EditText et;

	private ClassData[] _class;
	private SubjectData[] _subject;
	private LectureData[] _lecture;
	private String _userId;
	private int _facultyId;
	private int _selectedClassId;
	private int _selectedSubjectId;
	private int _selectedLectId;
	private TextView _txtLectureSubject;
	private TextView _txtLectureList;
	private Button _btnViewAttendance;
	private Spinner _spinLectureClass;
	private Spinner _spinLectureList;
	private Spinner _spinLectureSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_attendance);

		ibCalendar = (ImageButton) findViewById(R.id.imgButtonCalendar);
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		et = (EditText) findViewById(R.id.editLectureDate);
		ibCalendar.setOnClickListener(this);

		_txtLectureSubject=(TextView)findViewById(R.id.txtLectureSubject);
		_txtLectureList=(TextView)findViewById(R.id.txtLectureList);
		_btnViewAttendance=(Button)findViewById(R.id.btnViewAttendance);
		_btnViewAttendance.setOnClickListener(this);
		_spinLectureClass=(Spinner)findViewById(R.id.spinLectureClass);
		_spinLectureList=(Spinner)findViewById(R.id.spinLectureList);
		_spinLectureSubject=(Spinner)findViewById(R.id.spinLectureSubject);

		SharedPreferences sharedPref=getSharedPreferences("AttendanceSystem", Context.MODE_PRIVATE);	// Getting Logged in Faculty id
		_userId=sharedPref.getString("userId", "null");
		if(_userId.startsWith("F")==true)
		{
			_userId=_userId.substring(_userId.indexOf('F')+1);
		}
		_facultyId=Integer.parseInt(_userId);
		//		Log.d(TAG,"Logged in FacultyId:"+_facultyId);

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
					//					Log.d(TAG,"ID=============="+_selectedClassId);
					if(_selectedClassId!=-1)
					{
						Cursor cursorSubjectName=AttendanceSystemApplication.db.getResult("Select * from "+DatabaseContract.subject_Table.TABLE_NAME
								+" where "+DatabaseContract.class_Table.COLUMN_NAME_C_ID+" = "+_selectedClassId+" AND "
								+DatabaseContract.faculty_Table.COLUMN_NAME_F_ID+" = "+_facultyId);

						if(cursorSubjectName!=null && cursorSubjectName.getCount()!=0)
						{
							//							Log.d(TAG,"********************************************");
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

		et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) 
			{	
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				String lectureDate=et.getText().toString().trim();
				if(lectureDate.equals("")==false)
				{

					//Populate Lecture List spinner
					String lectureListQuery="select * from "+
							DatabaseContract.lecture_Table.TABLE_NAME+" where "+
							DatabaseContract.class_Table.COLUMN_NAME_C_ID+" = "+_selectedClassId+
							" AND "+DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID+" = "+_selectedSubjectId +
							" AND "
							+DatabaseContract.lecture_Table.COLUMN_NAME_L_DATE+" = '" + lectureDate + "'";

					Cursor cursorLectureList=AttendanceSystemApplication.db.getResult(lectureListQuery);
					//					Log.d(TAG,"RowCount:"+cursorLectureList.getCount());

					if(cursorLectureList.getCount()>0)
					{
						_lecture=new LectureData[cursorLectureList.getCount()];
						cursorLectureList.move(-1);
						for(int i=0;cursorLectureList.moveToNext()==true;i++)
						{
							String dbDate=cursorLectureList.getString(cursorLectureList.getColumnIndex
									(DatabaseContract.lecture_Table.COLUMN_NAME_L_DATE));
							if(dbDate.equals(lectureDate))
							{

								_lecture[i]=new LectureData(cursorLectureList.getInt(
										cursorLectureList.getColumnIndex(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID))
										,cursorLectureList.getString(cursorLectureList.getColumnIndex
												(DatabaseContract.lecture_Table.COLUMN_NAME_L_DATE))
												,cursorLectureList.getString(cursorLectureList.getColumnIndex
														(DatabaseContract.lecture_Table.COLUMN_NAME_L_TIME))
														,cursorLectureList.getInt(cursorLectureList.getColumnIndex
																(DatabaseContract.class_Table.COLUMN_NAME_C_ID))
																,cursorLectureList.getInt(cursorLectureList.getColumnIndex
																		(DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID)));
								System.out.println("Lecture Data : " + i);
								System.out.println("Lecture Id : "+_lecture[i].getL_id());
								System.out.println("Lecture CId : "+_lecture[i].getC_id());
								System.out.println("Lecture Date : "+_lecture[i].getL_date());
								System.out.println("Lecture Time : "+_lecture[i].getL_time());
								System.out.println("-----------------------------------------");
							}
						}
						System.out.println("LectureData Length:"+_lecture.length);
						_spinLectureList.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item
								, _lecture, "LectureData"));
						_spinLectureList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								TextView txtLectureId=(TextView)findViewById(R.id.txtSpinnerCustomId);
								_selectedLectId=Integer.parseInt(txtLectureId.getText()+"");
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});
						_spinLectureList.setVisibility(View.VISIBLE);

					}
				}				
			}
		});

	}


	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v)
	{
		if(v.getId()==R.id.btnViewAttendance)
		{
			System.out.println("Selected Lecture:"+_spinLectureList.getSelectedItem());
			SharedPreferences sharedPref =getSharedPreferences("ViewAttendanceInfo",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor=sharedPref.edit();
			System.out.println("Lect Id (ViewAttendace)"+ _selectedLectId);
			editor.putInt("l_id", _selectedLectId);
			editor.putInt("class_id",_selectedClassId);
			editor.putInt("sub_id", _selectedSubjectId);
			editor.commit();
			Intent showAttendance=new Intent(getApplicationContext(),ShowAttendanceActivity.class);
			startActivity(showAttendance);
		}
		showDialog(0);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id)
	{
		return new DatePickerDialog(this, datePickerListener, year, month, day);
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener()
	{
		public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay)
		{
			if(selectedMonth  < 10)
			{
				et.setText(selectedYear+ "-" + "0" + (selectedMonth + 1) + "-" + selectedDay );
			}
			if(selectedDay<10)
			{
				et.setText(selectedYear+ "-" + (selectedMonth + 1) + "-" + "0" + selectedDay );
			}
			if(selectedDay < 10 && selectedMonth  <10)
			{
				et.setText(selectedYear+ "-" + "0" + (selectedMonth + 1) + "-" + "0" + selectedDay );
			}
		}
	};
}
