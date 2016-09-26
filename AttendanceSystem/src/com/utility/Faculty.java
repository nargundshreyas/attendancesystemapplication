package com.utility;

public class Faculty
{
	private int facultyId;
	private String facultyFirstName;
	private String facultyLastName;
	private String facultyPhoto;
	
	public Faculty(int facultyId,String facultyFirstName,String facultyLastName,String facultyPhoto)
	{
		if(facultyPhoto==null || facultyPhoto=="")
			facultyPhoto="none";
		setFacultyId(facultyId);
		setFacultyFirstName(facultyFirstName);
		setFacultyLastName(facultyLastName);
		setFacultyPhoto(facultyPhoto);
	}

	public String getFacultyPhoto() {
		return facultyPhoto;
	}

	public void setFacultyPhoto(String facultyPhoto) {
		this.facultyPhoto = facultyPhoto;
	}

	public int getFacultyId()
	{
		return facultyId;
	}

	public void setFacultyId(int facultyId)
	{
		this.facultyId = facultyId;
	}

	public String getFacultyFirstName() {
		return facultyFirstName;
	}

	public void setFacultyFirstName(String facultyFirstName) {
		this.facultyFirstName = facultyFirstName;
	}

	public String getFacultyLastName() {
		return facultyLastName;
	}

	public void setFacultyLastName(String facultyLastName) {
		this.facultyLastName = facultyLastName;
	}

}
