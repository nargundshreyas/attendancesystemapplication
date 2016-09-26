package com.utility;

import com.imcc.attendancesystem.R;
import com.imcc.attendancesystem.R.id;
import com.imcc.attendancesystem.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity {

	private TextView _txtViewDialogMessage;
	private Button _btnDialogOk;
	private Intent _intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		Log.d("AttendanceSystem", "In Dialog Acivitiy");
		_btnDialogOk=(Button)findViewById(R.id.btnDialogOk);
		_txtViewDialogMessage=(TextView)findViewById(R.id.txtViewDialogMessage);
		_txtViewDialogMessage.setVisibility(View.GONE);
		_intent=getIntent();
		String message=_intent.getExtras().getString("dialogMessage");
		_txtViewDialogMessage.setText(message);
		_txtViewDialogMessage.setVisibility(View.VISIBLE);
		_btnDialogOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	
	}

}
