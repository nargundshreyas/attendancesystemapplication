package com.utility;

import java.io.File;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ParseStudentData	// Parse Excel File Containing Student Data 
{
	private Workbook _workBook;
	private Sheet _sheet;
	public int studentFirstNameColumnIndex=-1;
	public int studentLastNameColumnIndex=-1;
	public int studentRollNoColumnIndex=-1;
	public int studentClassNameColumnIndex=-1;
	private int _totalRows=0;
	private int _totalColumns=0;

	ParseStudentData(File dataFile)
	{
		try 
		{
			_workBook=Workbook.getWorkbook(dataFile);
			if(_workBook!=null)
			{
				_sheet=_workBook.getSheet(0);
				if(_sheet!=null)
				{
					_totalRows=_sheet.getRows();
					_totalColumns=_sheet.getColumns();
					int row=0,column=0;
					for(column=0;column<_totalColumns;column++)
					{
						Cell cell=_sheet.getCell(row, column);
						if(cell.getContents().trim().contains("FirstName")==true)
							studentFirstNameColumnIndex=column;
						else
						if(cell.getContents().trim().contains("LastName")==true)
							studentLastNameColumnIndex=column;
						else
						if(cell.getContents().trim().contains("Roll")==true)
							studentRollNoColumnIndex=column;
						else
						if(cell.getContents().trim().contains("Class")==true)
							studentClassNameColumnIndex=column;					
					}
				}
			}
		}		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getRollNo()
	{
		ArrayList<String> arrayListRollNo=new ArrayList<String>();
		for(int row=0;row<_totalRows;row++)
		{
			Cell cell=_sheet.getCell(row, studentRollNoColumnIndex);
			if(cell.getContents()!=null)
				arrayListRollNo.add(cell.getContents().trim());
			System.out.println("Cell("+row+" , "+studentRollNoColumnIndex+")="+cell.getContents());
		}
		return arrayListRollNo;
	}

}
