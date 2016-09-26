package com.utility;

import java.sql.Date;
import java.sql.Time;

public class LectureData 
{
	private int l_id;
	private String l_date;
	private String l_time;
	private int c_id;
	private int sub_id;
	
	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public int getSub_id() {
		return sub_id;
	}

	public void setSub_id(int sub_id) {
		this.sub_id = sub_id;
	}

	public LectureData(int l_id,String l_date,String l_time,int c_id,int sub_id)
	{
		setL_id(l_id);
		setL_date(l_date);
		setL_time(l_time);
		setC_id(c_id);
		setSub_id(sub_id);
	}
	
	public LectureData(int l_id,long l_date,long l_time,int c_id,int sub_id)
	{
		setL_id(l_id);
		setL_date(Long.toString(l_date));
		setL_time(Long.toString(l_time));
		setC_id(c_id);
		setSub_id(sub_id);
	}

	public int getL_id() 
	{
		return l_id;
	}
	public void setL_id(int l_id) 
	{
		this.l_id = l_id;
	}
	public String getL_date() 
	{
		return l_date;
	}
	public void setL_date(String l_date) 
	{
		this.l_date = l_date;
	}
	public String getL_time() 
	{
		return l_time;
	}
	public void setL_time(String l_time) 
	{
		this.l_time = l_time;
	}
}
