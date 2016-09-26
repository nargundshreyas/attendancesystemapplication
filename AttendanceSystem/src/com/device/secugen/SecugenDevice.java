package com.device.secugen;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPosition;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;
import SecuGen.FDxSDKPro.SGImpressionType;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.utility.FingerprintData;

public class SecugenDevice	implements SGFingerPresentEvent// Singleton Class 
{
	private static SecugenDevice _secugenDevice;	// static object for singleton class

	private static final long IMAGE_QUALITY_CONSTANT=70;
	private static final String TAG = "AttendanceSystem";
	private static final long SECURITY_LEVEL=SGFDxSecurityLevel.SL_NORMAL;
	public static String directoryName=File.separator+"sdcard"+File.separator+"AttendanceSystem";
	private static String fileName=null;;

	private JSGFPLib _sgfp;
	private int[] _maxTemplateSize;
	private int[] _imgQuality;
	private int[] _intbuffer;
	private boolean[] _fingerPresent;
	private boolean[] _matched;
	private byte[] _storedTemplate;
	private int _imageWidth;
	private int _imageHeight;
	private Bitmap _grayBitmap;
	private SGFingerInfo _fingerInfo;
	private FingerprintData _fingerprint;
	private Context context;
	private AlertDialog.Builder _alert;

	//Auto on Feature
	public Handler handlerAutoCapture = new Handler(){ 
		// @Override
		public void handleMessage(Message msg) 
		{
			//			authenticate();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	public SGAutoOnEventNotifier autoCapture;

	private SecugenDevice()	{}

	public static SecugenDevice getSecugenDevice()
	{
		if(_secugenDevice==null)
		{
			_secugenDevice=new SecugenDevice();
		}
		return _secugenDevice;
	}

	public boolean isOpen()
	{
		if(_sgfp.SetLedOn(true)==SGFDxErrorCode.SGFDX_ERROR_NONE)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			if(_sgfp.SetLedOn(false)==SGFDxErrorCode.SGFDX_ERROR_NONE)
				return true;
		}
		return false;
	}

	public int initSGFP(Context context,PendingIntent _permissionIntent)
	{
		try
		{
			// Initialize _sgfp Object and get USB Permissions
			this.context=context;
			_sgfp = new JSGFPLib((UsbManager) context.getSystemService(Context.USB_SERVICE));
			_sgfp.Init(SGFDxDeviceName.SG_DEV_AUTO);
			UsbDevice usbDevice = _sgfp.GetUsbDevice();
			Log.e("AttendenceSystem",""+_sgfp.GetLastError());
			// Before requesting permission for USB Host make sure you have added
			// uses-feature tag in AndroidManifest.xml
			if(usbDevice!=null)
			{
				_sgfp.GetUsbManager().requestPermission(usbDevice, _permissionIntent);

				if (_sgfp != null) 
				{
					if (SGFDxErrorCode.SGFDX_ERROR_NONE == _sgfp.OpenDevice(0))
					{
						SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
						_sgfp.GetDeviceInfo(deviceInfo);
						_imageWidth = deviceInfo.imageWidth;
						_imageHeight = deviceInfo.imageHeight;
						_maxTemplateSize=new int[1];
						_sgfp.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
						_sgfp.GetMaxTemplateSize(_maxTemplateSize);
						autoCapture=new SGAutoOnEventNotifier(_sgfp, this);
						return 1;
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.e("MyApp", "error in sgfplib", e);
			e.printStackTrace();
		}
		return 0;
	}

	public FingerprintData authenticate(String sourceDirectory)
	{
		_fingerPresent = new boolean[1];
		_imgQuality=new int[1];
		_fingerprint=new FingerprintData(_imageWidth*_imageHeight,_maxTemplateSize[0]);

		_sgfp.FingerPresent(_fingerPresent);// check if finger is present on sensor

		if (_fingerPresent[0] == true)  
		{
			long result = _sgfp.GetImage(_fingerprint.buffer);	// Get finger print

			_sgfp.GetImageQuality(_imageWidth, _imageHeight, _fingerprint.buffer, _imgQuality);	// check for quality of image


			if(_imgQuality[0]<IMAGE_QUALITY_CONSTANT)
			{
				Log.d(TAG, "Image Quality:"+_imgQuality[0]);				
				//Creating Bitmap from the buffer to display fingerprint
				_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
				_fingerprint.b.setHasAlpha(false);
				_intbuffer = new int[_imageWidth * _imageHeight];
				for (int i = 0; i < _intbuffer.length; ++i)
					_intbuffer[i] = (int) _fingerprint.buffer[i];
				_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);
				return _fingerprint;
			}
			else
			{
				// Creating Fingerprint Template
				_fingerInfo=new SGFingerInfo();
				_fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_UK ;
				_fingerInfo.ImageQuality = _imgQuality[0] ;
				_fingerInfo.ImpressionType =SGImpressionType.SG_IMPTYPE_LP;
				_fingerInfo.ViewNumber = 1;
				long error=_sgfp.CreateTemplate(_fingerInfo, _fingerprint.buffer,_fingerprint.templateBuffer);

				//Creating Bitmap from the buffer to display fingerprint
				_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
				_fingerprint.b.setHasAlpha(false);
				_intbuffer = new int[_imageWidth * _imageHeight];

				for (int i = 0; i < _intbuffer.length; ++i)
					_intbuffer[i] = (int) _fingerprint.buffer[i];

				_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);

				_matched=new boolean[1];
				_storedTemplate=new byte[_maxTemplateSize[0]];

				try
				{
					File templateDirectory=new File(sourceDirectory);
					if(templateDirectory.exists())
					{	
						File[] savedTemplates=templateDirectory.listFiles();
						for(int i=0;i<savedTemplates.length;i++)
						{
							File templateList=null;
							if(savedTemplates[i].isFile())
							{
								fileName=sourceDirectory+File.separator+savedTemplates[i].getName();
								templateList = new File(fileName);			
								FileInputStream fin = new FileInputStream(templateList);
								fin.read(_storedTemplate, 0, _storedTemplate.length);
								fin.close();
							}
							_sgfp.MatchTemplate(_storedTemplate, _fingerprint.templateBuffer, SECURITY_LEVEL, _matched);
							if(_matched[0]==true)
							{
								String userName=templateList.getName().trim();
								userName=userName.substring(0, userName.lastIndexOf('.'));
								if(userName.equals("admin"))	
								{
									_fingerprint.matchId=0;	// 0 being Admin match
								}
								else
									if(userName.startsWith("F")==true)
									{
										_fingerprint.matchId=1;	// 1 being Faculty match
									}
									else
									{
										_fingerprint.matchId=2;	// 2 being Student match
									}
								_fingerprint.dataBuffer=""+templateList.getName();
								_fingerprint.dataBuffer=_fingerprint.dataBuffer.trim();
								if(_fingerprint.dataBuffer.contains(".")==true)
									_fingerprint.dataBuffer=_fingerprint.dataBuffer.substring(0, _fingerprint.dataBuffer.lastIndexOf('.'));
								Log.d(TAG,"Template _matched with file "+templateList.getName()+"\n MatchId:"+_fingerprint.matchId);
								break;
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				_sgfp.MatchTemplate(_storedTemplate, _fingerprint.templateBuffer, SECURITY_LEVEL, _matched);
				return _fingerprint;
			}
		}
		return _fingerprint;
	}


	public boolean register(ImageView imgView,byte[] oldTemplate,String registerFileName,int matchAttempt)
	{
		_fingerPresent=new boolean[1];
		_imgQuality=new int[1];
		_matched=new boolean[1];
		if(oldTemplate==null && matchAttempt==0 && imgView!=null)		// First Attempt 
		{
			_fingerprint=new FingerprintData(_imageWidth*_imageHeight,_maxTemplateSize[0]);
			_sgfp.FingerPresent(_fingerPresent);// check if finger is present on sensor
			if (_fingerPresent[0] == true)  
			{
				_sgfp.GetImage(_fingerprint.buffer);	// Get finger print

				_sgfp.GetImageQuality(_imageWidth, _imageHeight, _fingerprint.buffer, _imgQuality);	// check for quality of image

				if(_imgQuality[0]<IMAGE_QUALITY_CONSTANT)
				{
					Log.d(TAG, "Image Quality:"+_imgQuality[0]);				
					//Creating Bitmap from the buffer to display fingerprint
					_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
					_fingerprint.b.setHasAlpha(false);
					_intbuffer = new int[_imageWidth * _imageHeight];
					for (int i = 0; i < _intbuffer.length; ++i)
						_intbuffer[i] = (int) _fingerprint.buffer[i];
					_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);

					imgView.setImageBitmap(_secugenDevice.toGrayscale(_fingerprint.b));
					return false;
				}
				else
				{
					Log.d(TAG, "Image Quality:"+_imgQuality[0]);
					// Creating Fingerprint Template for First Time
					_fingerInfo=new SGFingerInfo();
					_fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_UK ;
					_fingerInfo.ImageQuality = _imgQuality[0] ;
					_fingerInfo.ImpressionType =SGImpressionType.SG_IMPTYPE_LP;
					_fingerInfo.ViewNumber = 1;
					long error=_sgfp.CreateTemplate(_fingerInfo, _fingerprint.buffer,_fingerprint.templateBuffer);
					if(error==0)
					{
						//Creating Bitmap from the buffer to display fingerprint
						_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
						_fingerprint.b.setHasAlpha(false);
						_intbuffer = new int[_imageWidth * _imageHeight];

						for (int i = 0; i < _intbuffer.length; ++i)
							_intbuffer[i] = (int) _fingerprint.buffer[i];

						_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);
						imgView.setImageBitmap(_secugenDevice.toGrayscale(_fingerprint.b));
						register(imgView,_fingerprint.templateBuffer,registerFileName,1);
					}
					else
					{
						return false;
					}
				}
			}
		}
		else
			if(oldTemplate!=null && matchAttempt==1 && imgView!=null) // Second Attempt
			{
				_sgfp.FingerPresent(_fingerPresent);// check if finger is present on sensor
				if (_fingerPresent[0] == true)  
				{
					long result = _sgfp.GetImage(_fingerprint.buffer);	// Get finger print

					_sgfp.GetImageQuality(_imageWidth, _imageHeight, _fingerprint.buffer, _imgQuality);	// check for quality of image

					if(_imgQuality[0]<IMAGE_QUALITY_CONSTANT)
					{
						Log.d(TAG, "Image Quality:"+_imgQuality[0]);				
						//Creating Bitmap from the buffer to display fingerprint
						_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
						_fingerprint.b.setHasAlpha(false);
						_intbuffer = new int[_imageWidth * _imageHeight];
						for (int i = 0; i < _intbuffer.length; ++i)
							_intbuffer[i] = (int) _fingerprint.buffer[i];
						_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);
						imgView.setImageBitmap(_secugenDevice.toGrayscale(_fingerprint.b));
						return false;
					}
					else
					{
						Log.d(TAG, "Image Quality:"+_imgQuality[0]);
						// Creating Fingerprint Template for First Time
						_fingerInfo=new SGFingerInfo();
						_fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_UK ;
						_fingerInfo.ImageQuality = _imgQuality[0] ;
						_fingerInfo.ImpressionType =SGImpressionType.SG_IMPTYPE_LP;
						_fingerInfo.ViewNumber = 1;
						long error=_sgfp.CreateTemplate(_fingerInfo, _fingerprint.buffer,_fingerprint.templateBuffer);
						if(error==0)
						{
							//Creating Bitmap from the buffer to display fingerprint
							_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
							_fingerprint.b.setHasAlpha(false);
							_intbuffer = new int[_imageWidth * _imageHeight];

							for (int i = 0; i < _intbuffer.length; ++i)
								_intbuffer[i] = (int) _fingerprint.buffer[i];

							_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);
							imgView.setImageBitmap(_secugenDevice.toGrayscale(_fingerprint.b));
							_sgfp.MatchTemplate(oldTemplate, _fingerprint.templateBuffer, SecugenDevice.SECURITY_LEVEL, _matched);
							if(_matched[0]==true)
								register(imgView,_fingerprint.templateBuffer,registerFileName,2);
							else
								register(imgView,oldTemplate,registerFileName,1);
						}
						else
						{
							return false;
						}
					}
				}
			}
			else
				if(oldTemplate!=null && matchAttempt==2 && imgView!=null) // Second Attempt
				{
					_sgfp.FingerPresent(_fingerPresent);// check if finger is present on sensor
					if (_fingerPresent[0] == true)  
					{
						long result = _sgfp.GetImage(_fingerprint.buffer);	// Get finger print

						_sgfp.GetImageQuality(_imageWidth, _imageHeight, _fingerprint.buffer, _imgQuality);	// check for quality of image

						if(_imgQuality[0]<IMAGE_QUALITY_CONSTANT)
						{
							Log.d(TAG, "Image Quality:"+_imgQuality[0]);				
							//Creating Bitmap from the buffer to display fingerprint
							_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
							_fingerprint.b.setHasAlpha(false);
							_intbuffer = new int[_imageWidth * _imageHeight];
							for (int i = 0; i < _intbuffer.length; ++i)
								_intbuffer[i] = (int) _fingerprint.buffer[i];
							_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);
							imgView.setImageBitmap(_secugenDevice.toGrayscale(_fingerprint.b));
							return false;
						}
						else
						{
							Log.d(TAG, "Image Quality:"+_imgQuality[0]);
							// Creating Fingerprint Template for First Time
							_fingerInfo=new SGFingerInfo();
							_fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_UK ;
							_fingerInfo.ImageQuality = _imgQuality[0] ;
							_fingerInfo.ImpressionType =SGImpressionType.SG_IMPTYPE_LP;
							_fingerInfo.ViewNumber = 1;
							long error=_sgfp.CreateTemplate(_fingerInfo, _fingerprint.buffer,_fingerprint.templateBuffer);
							if(error==0)
							{
								//Creating Bitmap from the buffer to display fingerprint
								_fingerprint.b = Bitmap.createBitmap(_imageWidth, _imageHeight,Bitmap.Config.ARGB_8888);
								_fingerprint.b.setHasAlpha(false);
								_intbuffer = new int[_imageWidth * _imageHeight];

								for (int i = 0; i < _intbuffer.length; ++i)
									_intbuffer[i] = (int) _fingerprint.buffer[i];

								_fingerprint.b.setPixels(_intbuffer, 0, _imageWidth, 0, 0, _imageWidth,_imageHeight);
								imgView.setImageBitmap(_secugenDevice.toGrayscale(_fingerprint.b));
								_sgfp.MatchTemplate(oldTemplate, _fingerprint.templateBuffer, SecugenDevice.SECURITY_LEVEL, _matched);
								if(_matched[0]==true)
								{
									// Store Template and Account Information
									dumpFile(registerFileName,_fingerprint.templateBuffer);
									Log.d(TAG,"Registeration Successful!!!!");
									return true;
								}
								else
									register(imgView,oldTemplate,registerFileName,1);
							}
							else
							{
								return false;
							}
						}
					}
				}
		return true;
	}

	@Override
	public void SGFingerPresentCallback()
	{
		Log.d(TAG,"SGFingerPresentCallback()");
		handlerAutoCapture.sendMessage(new Message());
	}

	public void dumpFile(String fileName, byte[] buffer)
	{
		// To dump images and templates to SD card
		try 
		{
			File directory=new File(directoryName);
			if(directory.exists()==false)
				directory.mkdir();
			File myFile = new File(fileName);			
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			fOut.write(buffer, 0, buffer.length);
			myFile.setWritable(false);
			fOut.close();
		}
		catch (Exception e)
		{
			Log.e(TAG,"Exception when writing file" + fileName + "\n"+ e.getMessage() + "\n");
			e.printStackTrace();
		}

	}

	public Bitmap toGrayscale(Bitmap bmpOriginal) 
	{
		// Converts image to grayscale (NEW)
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		for (int y = 0; y < height; ++y) 
		{
			for (int x = 0; x < width; ++x) 
			{
				int color = bmpOriginal.getPixel(x, y);
				int r = (color >> 16) & 0xFF;
				int g = (color >> 8) & 0xFF;
				int b = color & 0xFF;
				int gray = (r + g + b) / 3;
				color = Color.rgb(gray, gray, gray);
				// color = Color.rgb(r/3, g/3, b/3);
				bmpGrayscale.setPixel(x, y, color);
			}
		}
		return bmpGrayscale;
	}

}