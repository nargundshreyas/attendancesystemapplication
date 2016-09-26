package com.utility;

import android.graphics.Bitmap;

public class FingerprintData 
{
	public Bitmap b;
	public byte[] buffer;
	public byte[] templateBuffer;
	public String dataBuffer;
	public long matchId;
	public FingerprintData(int bufferSize,int templateBufferSize) 
	{
		b=null;
		buffer=new byte[bufferSize];
		templateBuffer=new byte[templateBufferSize];
		dataBuffer=new String();
		matchId=-1;
	}
}
