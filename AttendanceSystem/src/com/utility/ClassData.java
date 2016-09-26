package com.utility;

public class ClassData 
{
	private int classId;
	private String className;
	
	public ClassData(int classId,String className)
	{
		setClassId(classId);
		setClassName(className);
	}
	public int getClassId()
	{
		return classId;
	}
	public void setClassId(int classId)
	{
		this.classId = classId;
	}
	public String getClassName()
	{
		return className;
	}
	public void setClassName(String className)
	{
		this.className = className;
	}
}
