package com.utility;

public class SubjectData 
{
	private int subjectId;
	private String subjectName;
	private int facultyId;
	private int classId;

	
	public SubjectData(int subjectId,String subjectName,int classId,int facultyId)
	{
		setSubjectId(subjectId);
		setSubjectName(subjectName);
		setClassId(classId);
		setFacultyId(facultyId);
	}
	public int getFacultyId() 
	{
		return facultyId;
	}
	public int getSubjectId() 
	{
		return subjectId;
	}
	public void setFacultyId(int facultyId) 
	{
		this.facultyId = facultyId;
	}
	public void setSubjectId(int subjectId) 
	{
		this.subjectId = subjectId;
	}
	public String getSubjectName() 
	{
		return subjectName;
	}
	public void setSubjectName(String subjectName) 
	{
		this.subjectName = subjectName;
	}
	public int getClassId() 
	{
		return classId;
	}
	public void setClassId(int classId) 
	{
		this.classId = classId;
	}
}
