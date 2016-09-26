package com.utility.database;

import com.imcc.attendancesystem.AttendanceSystemApplication;
import com.utility.WebService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager 
{

	private DatabaseHelper _db;
	private SQLiteDatabase _dab;

	public DBManager(Context context) 
	{

		_db= new DatabaseHelper(context,"AttendanceSystem", null, 1);
		_dab=_db.getWritableDatabase();					
	}
	
	public void close()
	{
		_dab.close();
	}

	public void class_table_insert(int C_ID,String C_NAME)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.class_Table.COLUMN_NAME_C_ID,C_ID);
		values.put(DatabaseContract.class_Table.COLUMN_NAME_C_NAME,C_NAME);
		Log.i("TableName",DatabaseContract.class_Table.TABLE_NAME);
		long ins=_dab.insert(DatabaseContract.class_Table.TABLE_NAME,null,values);
		Log.i("INSERT","Row Count:"+ins);
	}

	public void faculty_table_insert(int F_ID,String F_FIRST_NAME,String F_LAST_NAME,String F_PHOTO)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.faculty_Table.COLUMN_NAME_F_ID,F_ID);
		values.put(DatabaseContract.faculty_Table.COLUMN_NAME_F_FIRST_NAME,F_FIRST_NAME);
		values.put(DatabaseContract.faculty_Table.COLUMN_NAME_F_LAST_NAME,F_LAST_NAME);
		values.put(DatabaseContract.faculty_Table.COLUMN_NAME_F_PHOTO, F_PHOTO);
		long ins=_dab.insert(DatabaseContract.faculty_Table.TABLE_NAME,null,values);
		Log.i("INSERT","Row Count:"+ins);
	}


	public void subject_table_insert(int SUB_ID,String SUB_NAME,int F_ID,int C_ID)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID,SUB_ID);
		values.put(DatabaseContract.subject_Table.COLUMN_NAME_SUB_NAME,SUB_NAME);
		values.put(DatabaseContract.faculty_Table.COLUMN_NAME_F_ID,F_ID);
		values.put(DatabaseContract.class_Table.COLUMN_NAME_C_ID,C_ID);
		long ins=_dab.insert(DatabaseContract.subject_Table.TABLE_NAME,null,values);
		Log.i("INSERT","Row Count:"+ins);
	}

	public void student_table_insert(String S_ID,String S_FIRST_NAME,String S_LAST_NAME,String S_FINGERPRINT,String S_PHOTO,int C_ID)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.student_Table.COLUMN_NAME_S_ID,S_ID);
		values.put(DatabaseContract.student_Table.COLUMN_NAME_S_FIRST_NAME,S_FIRST_NAME);
		values.put(DatabaseContract.student_Table.COLUMN_NAME_S_LAST_NAME,S_LAST_NAME);
		values.put(DatabaseContract.student_Table.COLUMN_NAME_S_FINGERPRINT,S_FINGERPRINT);
		values.put(DatabaseContract.student_Table.COLUMN_NAME_S_PHOTO,S_PHOTO);
		values.put(DatabaseContract.class_Table.COLUMN_NAME_C_ID,C_ID);
		long ins=_dab.insert(DatabaseContract.student_Table.TABLE_NAME,null,values);
		Log.i("INSERT","Row Count:"+ins);
	}

	public void lecture_table_insert(int L_ID,int C_ID,int SUB_ID,String L_DATE,String L_TIMe)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID,L_ID);
		values.put(DatabaseContract.class_Table.COLUMN_NAME_C_ID,C_ID);
		values.put(DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID,SUB_ID);
		values.put(DatabaseContract.lecture_Table.COLUMN_NAME_L_DATE,L_DATE);
		values.put(DatabaseContract.lecture_Table.COLUMN_NAME_L_TIME, L_TIMe);
		long ins=_dab.insert(DatabaseContract.lecture_Table.TABLE_NAME, null,values);
		Log.i("Insert","Row Count:"+ins);
	}
	
	public void attendance_table_insert(int L_ID,String S_ID)
	{
		ContentValues values = new ContentValues();
		values.put(DatabaseContract.lecture_Table.COLUMN_NAME_L_ID,L_ID);
		values.put(DatabaseContract.student_Table.COLUMN_NAME_S_ID,S_ID);
		long ins=_dab.insert(DatabaseContract.attendance_Table.TABLE_NAME, null, values);
		Log.i("Insert","Row Count:"+ins);
	}
	
	public int getMaxId(String tableName,String fieldName)
	{
		int maxId=-1;
		String getMaxIdQuery="Select max("+fieldName+") from "+tableName;
		String[] arguements=new String[2];
		arguements[0]=tableName;
		arguements[1]=fieldName;
		Cursor maxIdCursor=_dab.rawQuery(getMaxIdQuery, null);
		if(maxIdCursor.moveToFirst())
			maxId=maxIdCursor.getInt(0);
		return maxId;
	}

	public Cursor getResult(String query)
	{
		return _dab.rawQuery(query, null);
	}
	

	public int isEmpty()	// Checks if Database is Empty
	{
		Cursor cursor=AttendanceSystemApplication.db.getResult("select * from "+DatabaseContract.class_Table.TABLE_NAME);
		if(cursor.moveToFirst()==false)
		{
			cursor=AttendanceSystemApplication.db.getResult("select * from "+DatabaseContract.subject_Table.TABLE_NAME);
			if(cursor.moveToFirst()==false)
			{
				cursor=AttendanceSystemApplication.db.getResult("select * from "+DatabaseContract.faculty_Table.TABLE_NAME);
				if(cursor.moveToFirst()==false)
				{
					cursor=AttendanceSystemApplication.db.getResult("select * from "+DatabaseContract.student_Table.TABLE_NAME);
					if(cursor.moveToFirst()==false)
					{
						cursor=AttendanceSystemApplication.db.getResult("select * from "+DatabaseContract.lecture_Table.TABLE_NAME);
						if(cursor.moveToFirst()==false)
						{
							cursor=AttendanceSystemApplication.db.getResult("select * from "+DatabaseContract.attendance_Table.TABLE_NAME);
							if(cursor.moveToFirst()==false)
							{
								return 1;
							}
							
						}
						
					}
				}
			}
		}
		return 0;
	}
	
	public boolean ifExists(String field,String colName,String tableName)
	{
		Cursor cursor=AttendanceSystemApplication.db.getResult("select "+colName+" from "+tableName);
		while(cursor.moveToNext())
		{
			if(cursor.getString(cursor.getColumnIndex(colName)).equals(field))
			{
				return true;
			}
			
		}
		return false;
	}
	
	public void deleteTableData(String tableName)
	{
		_dab.rawQuery("delete from "+tableName, null);
	}
	private class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("PRAGMA foreign_keys=ON;");
			// TODO Auto-generated method stub
			db.execSQL(DatabaseContract.class_Table.SQL_CREATE_TABLE);
			db.execSQL(DatabaseContract.faculty_Table.SQL_CREATE_TABLE);
			db.execSQL(DatabaseContract.subject_Table.SQL_CREATE_TABLE);
			db.execSQL(DatabaseContract.student_Table.SQL_CREATE_TABLE);
			db.execSQL(DatabaseContract.lecture_Table.SQL_CREATE_TABLE);
			db.execSQL(DatabaseContract.attendance_Table.SQL_CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
			// TODO Auto-generated method stub
			db.execSQL(DatabaseContract.class_Table.SQL_DROP_TABLE);
			db.execSQL(DatabaseContract.faculty_Table.SQL_DROP_TABLE);
			db.execSQL(DatabaseContract.subject_Table.SQL_DROP_TABLE);
			db.execSQL(DatabaseContract.student_Table.SQL_DROP_TABLE);
			db.execSQL(DatabaseContract.lecture_Table.SQL_DROP_TABLE);
			db.execSQL(DatabaseContract.attendance_Table.SQL_DROP_TABLE);
			onCreate(db);
//			WebService.dumpDataOnClient();		// Dumps Data in Client Database from server
		}
	}
}
