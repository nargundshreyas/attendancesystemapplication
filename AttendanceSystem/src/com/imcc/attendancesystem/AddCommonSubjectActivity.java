package com.imcc.attendancesystem;

import com.imcc.attendancesystem.R;
import com.imcc.attendancesystem.R.color;
import com.utility.DialogActivity;
import com.utility.ValidationEngine;
import com.utility.database.DatabaseContract;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddCommonSubjectActivity extends Activity 
{
	private EditText _editCommonSubjectName;
	private Button _btnAddCommonSubjectName;
	
/*	@Override
	public void onBackPressed() {
		// Disable back button
	}
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_common_subject);
		_editCommonSubjectName=(EditText)findViewById(R.id.editCommonSubjectName);
		_editCommonSubjectName.setHintTextColor(color.color_white);
		_btnAddCommonSubjectName=(Button)findViewById(R.id.btnAddCommonSubjectName);
		_btnAddCommonSubjectName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String subjectName=_editCommonSubjectName.getText().toString().trim();
				Cursor cursorClassData=AttendanceSystemApplication.db.getResult("Select * from "+DatabaseContract.class_Table.TABLE_NAME);
				if(cursorClassData.getCount()>0)
				{
					while(cursorClassData.moveToNext()==true)
					{
						int classId=cursorClassData.getInt(cursorClassData.getColumnIndex(DatabaseContract.class_Table.COLUMN_NAME_C_ID));
						if(ValidationEngine.validate(_editCommonSubjectName, ValidationEngine.VALIDATE_ALPHANUMERIC_ONLY))
						{	AttendanceSystemApplication.db.subject_table_insert(
								AttendanceSystemApplication.db.getMaxId(DatabaseContract.subject_Table.TABLE_NAME
										, DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID)+1, subjectName, 0, classId);
						}
						else
						{
							if(ValidationEngine.VALIDATE_ERROR)
							{
								Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
								intentDialogActivity.putExtra("dialogMessage", ValidationEngine.VALIDATE_ERROR_MESSAGE);
								startActivity(intentDialogActivity);
							}
						}
					}
				}
			}
		});
	}
}
