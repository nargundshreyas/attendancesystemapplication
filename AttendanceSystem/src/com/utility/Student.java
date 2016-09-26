package com.utility;

public class Student
{
	private String rollNo;
	private String firstName;
	private String lastName;
	private int classId;
	private String fingerPrint;
	private String photo;
	private long sPhotoSize,sFingerprintSize;
	public long getsPhotoSize() {
		return sPhotoSize;
	}

	public void setsPhotoSize(long sPhotoSize) {
		this.sPhotoSize = sPhotoSize;
	}

	public long getsFingerprintSize() {
		return sFingerprintSize;
	}

	public void setsFingerprintSize(long sFingerprintSize) {
		this.sFingerprintSize = sFingerprintSize;
	}

	public String getFingerPrint() 
	{
		return fingerPrint;
	}

	public void setFingerPrint(String fingerPrint) 
	{
		this.fingerPrint = fingerPrint;
	}

	public String getPhoto() 
	{
		return photo;
	}

	public void setPhoto(String photo) 
	{
		this.photo = photo;
	}

	public Student(String rollNo,String firstName,String lastName,int classId,String fingerPrint,String photo)
	{
		if(fingerPrint==null)
			fingerPrint="none";
		if(photo==null)
			photo="none";
		setRollNo(rollNo);
		setFirstName(firstName);
		setLastName(lastName);
		setClassId(classId);
		setFingerPrint(fingerPrint);
		setPhoto(photo);
		sPhotoSize=0;
		sFingerprintSize=0;
	}

	public Student(String rollNo,String firstName,String lastName,int classId,String fingerPrint,String photo,long photoSize,long fingerprintSize)
	{
		if(fingerPrint==null)
			fingerPrint="none";
		if(photo==null)
			photo="none";
		setRollNo(rollNo);
		setFirstName(firstName);
		setLastName(lastName);
		setClassId(classId);
		setFingerPrint(fingerPrint);
		setPhoto(photo);
		sPhotoSize=photoSize;
		sFingerprintSize=fingerprintSize;
	}

	public String getRollNo()
	{
		return rollNo;
	}

	public void setRollNo(String rollNo) 
	{
		this.rollNo = rollNo;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName) 
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName) 
	{
		this.lastName = lastName;
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
