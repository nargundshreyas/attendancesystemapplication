package com.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.PatternMatcher;
import android.widget.EditText;

public class ValidationEngine 
{
	public static String VALIDATE_CHARACTERS_ONLY="[a-zA-Z]+";
	public static String VALIDATE_NUMBERS_ONLY="^[0-9]+$";
	public static String VALIDATE_ALPHANUMERIC_ONLY="[A-Za-z\\-\\ ]*[0-9]*[A-Za-z\\-\\ ]*";

	public static boolean VALIDATE_ERROR=false;
	public static String VALIDATE_ERROR_MESSAGE="Incorrect Input.";

	public static boolean isNull(EditText et)
	{
		String editTextString=et.getText().toString().trim();
		if(editTextString.equals("") || editTextString.equals(null) || editTextString.isEmpty())
		{	
			VALIDATE_ERROR=true;
			VALIDATE_ERROR_MESSAGE="Fields Cannot be Empty.";
			return false;
		}
		return true;
	}
	
	public static boolean validate(EditText et,String pattern)
	{
		VALIDATE_ERROR=false;
		VALIDATE_ERROR_MESSAGE="Incorrect Input";
		String editTextString=et.getText().toString().trim();
		System.out.println("EditText:"+editTextString);
		Pattern patternString=Pattern.compile(pattern);
		Matcher matcher=patternString.matcher(editTextString);
		
		if(isNull(et)==false)
			return false;
		
		if(matcher.matches()==true)
			return true;
		else
		{
			VALIDATE_ERROR=true;
			if(pattern.equals(VALIDATE_ALPHANUMERIC_ONLY))
				VALIDATE_ERROR_MESSAGE="Only Alphabates and Numbers allowed.";
			if(pattern.equals(VALIDATE_CHARACTERS_ONLY))
				VALIDATE_ERROR_MESSAGE="Only Alphabates allowed.";
			if(pattern.equals(VALIDATE_NUMBERS_ONLY))
				VALIDATE_ERROR_MESSAGE="Only Numbers allowed.";
			return false;
		}
	}
}
