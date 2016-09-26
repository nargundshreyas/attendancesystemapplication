package com.utility;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

import com.device.secugen.SecugenDevice;
import com.imcc.attendancesystem.AttendanceSystemApplication;

public class Client {
	
	Socket socket;
	
	public Client() {
		
		try {
			System.out.println("in client");
			socket = new Socket(WebService.serverIpAddress,6000);
			System.out.println("server found");
			DataInputStream din = new DataInputStream(socket.getInputStream());
			String FileName=din.readUTF();
			int ExpectedBytes= din.readInt();
			byte[] data;
			int length;
			int readBytes = 0;
			
			while(readBytes!=ExpectedBytes)
			{
				if((length=din.available())>0)
				{
					data = new byte[length];
					readBytes+=length;
					din.read(data);
					AttendanceSystemApplication.secugenDevice.dumpFile(SecugenDevice.directoryName+File.separator+FileName, data);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}
}
