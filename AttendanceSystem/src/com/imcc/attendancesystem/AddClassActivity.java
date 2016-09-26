package com.imcc.attendancesystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.imcc.attendancesystem.R;
import com.imcc.attendancesystem.R.color;
import com.utility.DialogActivity;
import com.utility.ValidationEngine;
import com.utility.database.DatabaseContract;

public class AddClassActivity extends Activity
{
	private EditText _editClassName;
	private Button _btnAddClassName;

	/*	@Override
	public void onBackPressed() {
		// Disable back button
	}

	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_class);
		_editClassName=(EditText)findViewById(R.id.editClassName);
		_editClassName.setHintTextColor(color.color_white);
		_btnAddClassName=(Button)findViewById(R.id.btnAddClassName);
		_btnAddClassName.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(ValidationEngine.isNull(_editClassName))
				{
					int id=AttendanceSystemApplication.db.getMaxId(DatabaseContract.class_Table.TABLE_NAME
							, DatabaseContract.class_Table.COLUMN_NAME_C_ID)+1;
					Log.i("Table","CID="+id+" CName="+_editClassName.getText().toString());
					AttendanceSystemApplication.db.class_table_insert(id , _editClassName.getText().toString().trim());

					Intent intentDialogActivity=new Intent(getApplicationContext(),DialogActivity.class);
					intentDialogActivity.putExtra("dialogMessage", "Class created Successfully.");
					startActivity(intentDialogActivity);
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
		});
	}
}