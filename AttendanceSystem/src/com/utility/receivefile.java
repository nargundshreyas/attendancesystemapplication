package com.utility;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.imcc.attendancesystem.R;
 
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class receivefile extends Activity {
     
	 HttpClient httpclient;
	 String url;
	 HttpResponse response;
   Button b1;
   Bitmap bitmap = null;
   InputStream instream;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
							
				//To remove Network on main thread exception
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy); 
				
				url="http://192.168.1.114:8080/AttendenceSystemService/rest/Services/getStudentData";
				httpclient = new DefaultHttpClient();
					
				HttpGet httpget = new HttpGet(url); 
				try {
					response = httpclient.execute(httpget);
					
					 System.out.println("getting response.........."+response);
					 
					 HttpEntity entity = response.getEntity();
					 if (entity != null){
					
					    instream = entity.getContent();
					   String gettingJson = convertStreamToString(instream);
					
					   System.out.println("getting data from server......"+gettingJson);
					   
					   try {
						
						   JSONArray  obj = new JSONArray(gettingJson);
						   for (int i = 0; i < obj.length(); i++) 
							{
							   JSONObject ProgramObject = obj.getJSONObject(i);
							   String fileName= ProgramObject.getString("s_photo");
							   
							   System.out.println("getting file name ..."+fileName);
							   byte [] temp = ProgramObject.getString("s_Photo").getBytes("utf-8");
							   
							  
							  
							  FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/attendence/photo/"+fileName);
							   fos.write(temp);
							   System.out.println("content......"+temp);
							   fos.close();
							   
							   String fileNameFingerrPrint= ProgramObject.getString("s_fingerprint");
							   
							   System.out.println("getting file name ..."+fileNameFingerrPrint);
							   byte [] tempF = ProgramObject.getString("s_Fingerprint").getBytes("utf-8");
							   
							
							  
							  FileOutputStream fos2 = new FileOutputStream(Environment.getExternalStorageDirectory() + "/attendence/fingerprint/"+fileNameFingerrPrint);
							  fos2.write(tempF);
							   System.out.println("content......"+tempF);
							   fos2.close();
							   
							
							
							}
						
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
					
					
					 }
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
		});
        
        
       
    
    } 
    
    public byte[] objectToBytArray( Object ob ){
		   return ((ob.toString()).getBytes());
		  }
    
    private static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        Log.e("in","try block");
	        line = reader.readLine();
	        Log.e("line",line);
	        if(line==null)
	        {
	            Log.e("Line","is null");
	        }
	        else{
	            Log.e("Line","is not null");
	            sb.append(line );

	        }
	         } catch (IOException e) {
	        	 	e.printStackTrace();
	         	} finally {
	         	try {
	            Log.e("line","close");
	            is.close();
	         		} 
	         	catch (IOException e) {
	            e.printStackTrace();
	         	}
	    }
	    return sb.toString();
	}
   
}
