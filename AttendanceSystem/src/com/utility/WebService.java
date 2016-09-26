package com.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.imcc.attendancesystem.AttendanceSystemApplication;
import com.utility.database.DatabaseContract;

public class WebService {
	public final static String serverIpAddress = "192.168.1.94:8080";
	public static Student[] studentData;
	public static ClassData[] classData;
	public static Faculty[] facultyData;
	public static SubjectData[] subjectData;
	public static LectureData[] lectureData;
	public static AttendanceData[] attendanceData;
	public static Object[] object;
	
	public static void dumpDataOnClient() {
		// Deleting Existing Table Data if any
		AttendanceSystemApplication.db
				.deleteTableData(DatabaseContract.attendance_Table.TABLE_NAME);
		AttendanceSystemApplication.db
				.deleteTableData(DatabaseContract.lecture_Table.TABLE_NAME);
		AttendanceSystemApplication.db
				.deleteTableData(DatabaseContract.student_Table.TABLE_NAME);
		AttendanceSystemApplication.db
				.deleteTableData(DatabaseContract.subject_Table.TABLE_NAME);
		AttendanceSystemApplication.db
				.deleteTableData(DatabaseContract.faculty_Table.TABLE_NAME);
		AttendanceSystemApplication.db
				.deleteTableData(DatabaseContract.class_Table.TABLE_NAME);

		ClassData[] localClassData = (ClassData[]) getDataFromServer("ClassData");
		if(localClassData!=null)
		for (int i = 0; i < localClassData.length; i++) {
			AttendanceSystemApplication.db.class_table_insert(
					localClassData[i].getClassId(),
					localClassData[i].getClassName());
		}

		Faculty[] localFacultyData = (Faculty[]) getDataFromServer("FacultyData");
		if(localFacultyData!=null)
			for (int i = 0; i < localFacultyData.length; i++) {
			AttendanceSystemApplication.db.faculty_table_insert(
					localFacultyData[i].getFacultyId(),
					localFacultyData[i].getFacultyFirstName(),
					localFacultyData[i].getFacultyLastName(), "");
		}
		SubjectData[] localSubjectData = (SubjectData[]) getDataFromServer("SubjectData");
		if(localSubjectData!=null)
		for (int i = 0; i < localSubjectData.length; i++) {
			AttendanceSystemApplication.db.subject_table_insert(
					localSubjectData[i].getSubjectId(),
					localSubjectData[i].getSubjectName(),
					localSubjectData[i].getFacultyId(),
					localSubjectData[i].getClassId());
		}
		Student[] localStudentData = (Student[]) getDataFromServer("Student");
		if(localStudentData!=null)
		for (int i = 0; i < localStudentData.length; i++) {
			AttendanceSystemApplication.db.student_table_insert(
					localStudentData[i].getRollNo(),
					localStudentData[i].getFirstName(),
					localStudentData[i].getLastName(),
					localStudentData[i].getFingerPrint(),
					localStudentData[i].getPhoto(),
					localStudentData[i].getClassId());
		}
		LectureData[] localLectureData = (LectureData[]) getDataFromServer("LectureData");
		if(localLectureData!=null)
		for (int i = 0; i < localLectureData.length; i++) {
			AttendanceSystemApplication.db.lecture_table_insert(
					localLectureData[i].getL_id(),
					localLectureData[i].getC_id(),
					localLectureData[i].getSub_id(),
					localLectureData[i].getL_date(),
					localLectureData[i].getL_time());
		}
		AttendanceData[] localAttendanceData = (AttendanceData[]) getDataFromServer("AttendanceData");
		if(localAttendanceData!=null)
		for (int i = 0; i < localAttendanceData.length; i++) {
			AttendanceSystemApplication.db.attendance_table_insert(
					localAttendanceData[i].getL_id(), ""
							+ localAttendanceData[i].getS_id());
		}
	}

	public static Object[] getDataFromServer(String tableName) {
		String functionName = null;

		if (tableName.equals("Student") == true)
			functionName = "getStudentData";
		else
		if (tableName.equals("SubjectData") == true)
			functionName = "getSubjectData";
		else
		if (tableName.equals("LectureData") == true)
			functionName = "getLectureData";
		else
		if (tableName.equals("FacultyData") == true)
			functionName = "getFacultyData";
		else
		if (tableName.equals("ClassData") == true)
			functionName = "getClassData";
		else
		if (tableName.equals("AttendanceData") == true)
			functionName = "getAttendanceData";
		else
			functionName="getMediaData";
		
		// To remove Network on main thread exception
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		String url = "http://"+serverIpAddress
				+ "/AttendanceSystemServiceFinal/rest/Services/"+functionName;
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget);
//			System.out.println("getting response.........." + response);
			HttpEntity entity = response.getEntity();
			if (entity != null) 
			{
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
//				System.out.println(result + "********");
				instream.close();

				JSONArray obj = new JSONArray(result);
				JSONObject Object = obj.getJSONObject(0);

				if (tableName.equals("Student") == true)
					object = new Student[obj.length()];
				else
				if (tableName.equals("SubjectData") == true)
					object = new SubjectData[obj.length()];
				else
				if (tableName.equals("LectureData") == true)
					object = new LectureData[obj.length()];
				else
				if (tableName.equals("FacultyData") == true)
					object = new Faculty[obj.length()];
				else
				if (tableName.equals("ClassData") == true)
					object = new ClassData[obj.length()];
				else
				if (tableName.equals("AttendanceData") == true)
					object = new AttendanceData[obj.length()];
				else
				if(functionName.equals("getMediaData"))
				{
					System.out.println("------------------------Before Client Call-----------------------------");
					new Client();
				}
				for (int i = 0; i < obj.length(); i++) 
				{
					JSONObject ProgramObject = obj.getJSONObject(i);

					if (tableName.equals("Student") == true) 
					{
						object[i] = new Student(""
								+ ProgramObject.getInt("s_id"),
								ProgramObject.getString("s_first_name"),
								ProgramObject.getString("s_last_name"),
								ProgramObject.getInt("c_id"),
								ProgramObject.getString("s_fingerprint"),
								ProgramObject.getString("s_photo"),
								ProgramObject.getLong("s_Photo_Size"),
								ProgramObject.getLong("s_FringerPrint_Size") );
						String fileName = ProgramObject.getString("s_photo");
//						System.out.println("Photo Size:"+ProgramObject.getLong("s_Photo_Size"));
/*						if (fileName.equals("none") == false) 
						{
							System.out.println("getting file name ..."+ fileName);
//							System.out.println("getting file size ..."+ProgramObject.getString("s_Photo_Byte").getBytes("utf-8").length);							
							byte[] temp = new byte[(int) ProgramObject.getLong("s_Photo_Size")] ; 
							temp=ProgramObject.getString("s_Photo_Byte").getBytes("utf-8");
							// Change filpath for photo
							// Environment.getExternalStorageDirectory()
							FileOutputStream fos = new FileOutputStream(
									Environment.getExternalStorageDirectory()
											+ File.separator
											+ "AttendanceSystem"
											+ File.separator + "Photo"
											+ File.separator + fileName);
							fos.write(temp);
							System.out.println("content......" + temp);
							fos.close();
						}

						String fileNameFingerrPrint = ProgramObject
								.getString("s_fingerprint");

						System.out.println("getting file name ..."
								+ fileNameFingerrPrint);
						byte[] tempF = ProgramObject.getString(
								"s_Fingerprint_Byte").getBytes("utf-8");
						System.out.println("getting F file size ..."+tempF.length);
						// Change filpath for fingerprint
						
						FileOutputStream fos2 = new FileOutputStream(
								Environment.getExternalStorageDirectory()
										+ File.separator + "AttendanceSystem"
										+ File.separator + "Fingerprint"
										+ File.separator + fileNameFingerrPrint);
						fos2.write(tempF);
						System.out.println("content......" + tempF);
						fos2.close();
*/					}
					if (tableName.equals("SubjectData") == true) {
						object[i] = new SubjectData(
								ProgramObject.getInt("sub_id"),
								ProgramObject.getString("sub_name"),
								ProgramObject.getInt("c_id"),
								ProgramObject.getInt("f_id"));
					}
					if (tableName.equals("LectureData") == true) {
						object[i] = new LectureData(
								ProgramObject.getInt("l_id"),
								ProgramObject.getString("l_date"),
								ProgramObject.getString("l_time"),
								ProgramObject.getInt("c_id"),
								ProgramObject.getInt("sub_id"));
					}
					if (tableName.equals("FacultyData") == true) {
						object[i] = new Faculty(ProgramObject.getInt("f_id"),
								ProgramObject.getString("f_first_name"),
								ProgramObject.getString("f_last_name"),
								ProgramObject.getString("f_photo"));
					}
					if (tableName.equals("ClassData") == true) {
						object[i] = new ClassData(ProgramObject.getInt("c_id"),
								ProgramObject.getString("c_name"));
					}
					if (tableName.equals("AttendanceData") == true) {
						object[i] = new AttendanceData(
								ProgramObject.getInt("l_id"),
								ProgramObject.getInt("s_id"));
					}
				}
				
				return object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			Log.e("in", "try block");
			line = reader.readLine();
			// Log.e("line", line);
			if (line == null) {
				Log.e("Line", "is null");
			} else {
				Log.e("Line", "is not null");
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				Log.e("line", "close");
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
