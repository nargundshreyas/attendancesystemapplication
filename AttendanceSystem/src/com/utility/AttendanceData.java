package com.utility;

public class AttendanceData 
{
	private int l_id;
	private int s_id;
	
	public AttendanceData(int l_id,int s_id)
	{
		setL_id(l_id);
		setS_id(s_id);
	}
	
	public int getL_id() 
	{
		return l_id;
	}
	public void setL_id(int l_id)
	{
		this.l_id = l_id;
	}
	public int getS_id() 
	{
		return s_id;
	}
	public void setS_id(int s_id) 
	{
		this.s_id = s_id;
	}
}
