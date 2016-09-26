package com.utility.database;

import android.provider.BaseColumns;

public class DatabaseContract {

	public DatabaseContract() {

	};

	public static abstract class class_Table implements BaseColumns {
		public static final String TABLE_NAME = "class_table";
		public static final String COLUMN_NAME_C_ID = "c_id";
		public static final String COLUMN_NAME_C_NAME = "c_name";

		public static final String SQL_CREATE_TABLE = "create table "
				+ DatabaseContract.class_Table.TABLE_NAME + "("
				+ COLUMN_NAME_C_ID + " int primary key,"
				+ COLUMN_NAME_C_NAME + " varchar(50) not null)";

		public static final String SQL_DROP_TABLE = "drop table " + TABLE_NAME;

	}

	public static abstract class faculty_Table implements BaseColumns {
		public static final String TABLE_NAME = "faculty_table";
		public static final String COLUMN_NAME_F_ID = "f_id";
		public static final String COLUMN_NAME_F_FIRST_NAME = "f_first_name";
		public static final String COLUMN_NAME_F_LAST_NAME = "f_last_name";
		public static final String COLUMN_NAME_F_PHOTO = "f_photo";

		public static final String SQL_CREATE_TABLE = "create table "
				+ DatabaseContract.faculty_Table.TABLE_NAME + "("
				+ COLUMN_NAME_F_ID + " int primary key,"
				+ COLUMN_NAME_F_FIRST_NAME + " varchar(50) not null,"
				+ COLUMN_NAME_F_LAST_NAME + " varchar(50) not null,"
				+ COLUMN_NAME_F_PHOTO + " text not null)";

		public static final String SQL_DROP_TABLE = "drop table " + TABLE_NAME;

	}

	public static abstract class subject_Table implements BaseColumns {
		public static final String TABLE_NAME = "subject_table";
		public static final String COLUMN_NAME_SUB_ID = "sub_id";
		public static final String COLUMN_NAME_SUB_NAME = "sub_name";

		public static final String SQL_CREATE_TABLE = "create table "
				+ DatabaseContract.subject_Table.TABLE_NAME + "("
				+ COLUMN_NAME_SUB_ID + " int primary key,"
				+ COLUMN_NAME_SUB_NAME + " text not null,"
				+ DatabaseContract.faculty_Table.COLUMN_NAME_F_ID
				+ " varchar(20) not null,"
				+DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+" int not null"
				+",foreign key("
				+ DatabaseContract.faculty_Table.COLUMN_NAME_F_ID
				+ ") references " + DatabaseContract.faculty_Table.TABLE_NAME
				+ "(" + DatabaseContract.faculty_Table.COLUMN_NAME_F_ID
				+ ") on update cascade on delete cascade"
				+",foreign key("
				+DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+") references "+DatabaseContract.class_Table.TABLE_NAME
				+"("+DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+") on update cascade on delete cascade"
				+")";

		public static final String SQL_DROP_TABLE = "drop table " + TABLE_NAME;

	}

	public static abstract class student_Table implements BaseColumns {
		public static final String TABLE_NAME = "student_table";
		public static final String COLUMN_NAME_S_ID = "s_id";
		public static final String COLUMN_NAME_S_FIRST_NAME = "s_first_name";
		public static final String COLUMN_NAME_S_LAST_NAME = "s_last_name";
		public static final String COLUMN_NAME_S_FINGERPRINT = "s_fingerprint";
		public static final String COLUMN_NAME_S_PHOTO = "s_photo";

		public static final String SQL_CREATE_TABLE = "create table "
				+ DatabaseContract.student_Table.TABLE_NAME + "("
				+ COLUMN_NAME_S_ID + " varchar(20) primary key,"
				+ COLUMN_NAME_S_FIRST_NAME + " text not null,"
				+ COLUMN_NAME_S_LAST_NAME + " text not null,"
				+ COLUMN_NAME_S_FINGERPRINT + " text not null,"
				+ COLUMN_NAME_S_PHOTO + " text not null,"
				+ DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+ " int not null,foreign key("
				+ DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+ ") references " + DatabaseContract.class_Table.TABLE_NAME
				+ "(" + DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+ ") on update cascade on delete cascade)";

		public static final String SQL_DROP_TABLE = "drop table " + TABLE_NAME;

	}

	public static abstract class lecture_Table implements BaseColumns {
		public static final String TABLE_NAME = "lecture_table";
		public static final String COLUMN_NAME_L_ID = "l_id";
		public static final String COLUMN_NAME_L_DATE = "l_date";
		public static final String COLUMN_NAME_L_TIME = "l_time";

		public static final String SQL_CREATE_TABLE = "create table "
				+ DatabaseContract.lecture_Table.TABLE_NAME + "("
				+ COLUMN_NAME_L_ID + " int primary key ,"
				+ COLUMN_NAME_L_DATE + " date not null," + COLUMN_NAME_L_TIME
				+ " time not null,"
				+ DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+ " int not null,"
				+ DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID
				+ " int not null,foreign key("
				+ DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+ ") references " + DatabaseContract.class_Table.TABLE_NAME
				+ "(" + DatabaseContract.class_Table.COLUMN_NAME_C_ID
				+ ") ON delete cascade ON update cascade,foreign key("
				+ DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID
				+ ") references " + DatabaseContract.subject_Table.TABLE_NAME
				+ "(" + DatabaseContract.subject_Table.COLUMN_NAME_SUB_ID
				+ ") ON update cascade ON delete cascade)";

		public static final String SQL_DROP_TABLE = "drop table " + TABLE_NAME;

	}

	public static abstract class attendance_Table implements BaseColumns {
		public static final String TABLE_NAME = "attendance_table";

		public static final String SQL_CREATE_TABLE = "create table "
				+ DatabaseContract.attendance_Table.TABLE_NAME + "("
				+ DatabaseContract.lecture_Table.COLUMN_NAME_L_ID
				+ " int not null,"
				+ DatabaseContract.student_Table.COLUMN_NAME_S_ID
				+ " varchar(20) not null,foreign key("
				+ DatabaseContract.lecture_Table.COLUMN_NAME_L_ID
				+ ") references " + DatabaseContract.lecture_Table.TABLE_NAME
				+ "(" + DatabaseContract.lecture_Table.COLUMN_NAME_L_ID
				+ ") on update cascade on delete cascade,foreign key("
				+ DatabaseContract.student_Table.COLUMN_NAME_S_ID
				+ ") references " + DatabaseContract.student_Table.TABLE_NAME
				+ "(" + DatabaseContract.student_Table.COLUMN_NAME_S_ID
				+ ") on update cascade on delete cascade)";

		public static final String SQL_DROP_TABLE = "drop table " + TABLE_NAME;

	}
}
