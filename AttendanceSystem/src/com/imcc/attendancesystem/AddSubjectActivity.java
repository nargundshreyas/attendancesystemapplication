package com.imcc.attendancesystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.imcc.attendancesystem.R;
import com.imcc.attendancesystem.R.color;
import com.utility.ClassData;
import com.utility.CustomSpinnerAdapter;
import com.utility.DialogActivity;
import com.utility.Faculty;
import com.utility.ValidationEngine;
import com.utility.database.DBManager;
import com.utility.database.DatabaseContract;

public class AddSubjectActivity extends Activity
{
	private static final String TAG="AttendanceSystem";

	private int _selectedFacultyId;
	private int _selectedClassId;
	private Button _btnAddSubjectName;
	private EditText _editSubjectName;
	private Spinner _spinSubjectFacultyName;
	private Spinner _spinSubjectClassName;
	private Faculty[] _faculty;
	private ClassData[] _class;
	private CustomSpinnerAdapter _adapter;

/*	@Override
	public void onBackPressed() {
		// Disable back button
	}
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_subject);
		_btnAddSubjectName=(Button)findViewById(R.id.btnAddSubjectName);
		_editSubjectName=(EditText)findViewById(R.id.editSubjectName);
		_editSubjectName.setHintTextColor(color.color_white);
		_spinSubjectFacultyName=(Spinner)findViewById(R.id.spinSubjectFacultyName);
		//Getting data from database to populate FacultyName Spinner
		Cursor cursorFacultyData=AttendanceSystemApplication.db.getResult("Select * from "+DatabaseContract.faculty_Table.TABLE_NAME);
		if(cursorFacultyData!=null && cursorFacultyData.getCount()!=0)
		{
			Log.d(TAG,"Faculty Data Count:"+cursorFacultyData.getCount());
			_faculty=new Faculty[cursorFacultyData.getCount()+1];
			_faculty[0]=new Faculty(-1, "Faculty", "Name",null);
			while(cursorFacultyData.moveToNext()==true)
			{
				_faculty[cursorFacultyData.getPosition()+1]=new Faculty(cursorFacultyData.getInt(cursorFacultyData.getColumnIndex(DatabaseContract.faculty_Table.COLUMN_NAME_F_ID))
						, cursorFacultyData.getString(cursorFacultyData.getColumnIndex(DatabaseContract.faculty_Table.COLUMN_NAME_F_FIRST_NAME))
						, cursorFacultyData.getString(cursorFacultyData.getColumnIndex(DatabaseContract.faculty_Table.COLUMN_NAME_F_LAST_NAME))
						,cursorFacultyData.getString(cursorFacultyData.getColumnIndex(DatabaseContract.faculty_Table.COLUMN_NAME_F_PHOTO)));
			}
		}
		else
		{
			Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
			intentDialogActivity.putExtra("dialogMessage", "You need to add Faculty first to add subject");
			startActivity(intentDialogActivity);
			finish();
		}

		_spinSubjectClassName=(Spinner)findViewById(R.id.spinSubjectClassName);
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
		}
		else
		{
			Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
			intentDialogActivity.putExtra("dialogMessage", "You need to add class first to add subject");
			startActivity(intentDialogActivity);
			finish();
			
		}
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		_btnAddSubjectName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if(_selectedFacultyId==-1 && _selectedClassId==-1)
				{
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					if(_selectedClassId==-1)
						intentDialogActivity.putExtra("dialogMessage", "Please select Class.");
					else
						intentDialogActivity.putExtra("dialogMessage", "Please select Subject Faculty.");
					startActivity(intentDialogActivity);
				}
				else
				if(ValidationEngine.validate(_editSubjectName,ValidationEngine.VALIDATE_ALPHANUMERIC_ONLY))
				{
					int id=AttendanceSystemApplication.db.getMaxId(DatabaseContract.subject_Table.TABLE_NAME
							, DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID)+1;
					Log.i("Table","SubId="+id+" SubName="+_editSubjectName.getText().toString()
							+" FacultyId="+_selectedFacultyId+" ClassId:"+_selectedClassId);
					AttendanceSystemApplication.db.subject_table_insert(id
							, _editSubjectName.getText().toString(),
							_selectedFacultyId,_selectedClassId);

					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Subject Added Successfully.");
					startActivity(intentDialogActivity);

//					alert.setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) 
//						{
//							Intent admin=new Intent(getApplicationContext(),AdminActivity.class);
//							startActivity(admin);
//						}
//					});
//					alert.create().show();
				}
				else
				{
					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					if(ValidationEngine.VALIDATE_ERROR)
						intentDialogActivity.putExtra("dialogMessage", ValidationEngine.VALIDATE_ERROR_MESSAGE);
					startActivity(intentDialogActivity);
				}
			}
		});

		if(_faculty==null || _class==null)
		{
			Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
			if(_faculty==null)
				intentDialogActivity.putExtra("dialogMessage","No Faculty Available.");
			else
				intentDialogActivity.putExtra("dialogMessage","No Class Available.");
			startActivity(intentDialogActivity);
		}
		if(_faculty!=null)
		{
			_adapter=new CustomSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, _faculty,"Faculty");
			_spinSubjectFacultyName.setAdapter(_adapter);
			_spinSubjectFacultyName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
				{
					TextView txtid=(TextView)view.findViewById(R.id.txtSpinnerCustomId);
					_selectedFacultyId=Integer.parseInt(txtid.getText().toString());
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});

		}


		if(_class!=null)
		{
			_adapter=new CustomSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, _class, "ClassData");
			_spinSubjectClassName.setAdapter(_adapter);
			_spinSubjectClassName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
				{
					TextView txtid=(TextView)view.findViewById(R.id.txtSpinnerCustomId);
					_selectedClassId=Integer.parseInt(txtid.getText().toString());			
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});
		}
	}
}
