package com.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.imcc.attendancesystem.R;

public class CustomSpinnerAdapter extends ArrayAdapter
{
	private  Context _spinnerContext;
	private Faculty[] _faculty;
	private ClassData[] _class;
	private SubjectData[] _subject;
	private LectureData[] _lecture;
	private int[] _id;
	private String[] _data;
	
	public CustomSpinnerAdapter(Context context,int textViewResourceId,Object[] objects,String className) 
	{
		super(context, textViewResourceId, objects);
		_spinnerContext=context;
		if(className.equals("Faculty")==true)
		{
			this._faculty=(Faculty[]) objects;
			_id=new int[_faculty.length];
			_data=new String[_faculty.length];
			for(int i=0;i<_faculty.length;i++)
			{
				_id[i]=_faculty[i].getFacultyId();
				_data[i]=_faculty[i].getFacultyFirstName()+" "+_faculty[i].getFacultyLastName();
			}
		}
		else
		if(className.equals("ClassData")==true)
		{
			this._class=(ClassData[]) objects;
			_id=new int[_class.length];
			_data=new String[_class.length];
			for(int i=0;i<_class.length;i++)
			{
				_id[i]=_class[i].getClassId();
				_data[i]=_class[i].getClassName();
			}
		}
		else
		if(className.equals("SubjectData")==true)
		{
			this._subject=(SubjectData[]) objects;
			_id=new int[_subject.length];
			_data=new String[_subject.length];
			for(int i=0;i<_subject.length;i++)
			{
				_id[i]=_subject[i].getSubjectId();
				_data[i]=_subject[i].getSubjectName();
			}
		}
		else
		if(className.equals("LectureData")==true)
		{
			this._lecture=(LectureData[]) objects;
			_id=new int[_lecture.length];
			_data=new String[_lecture.length];
			for(int i=0;i<_lecture.length;i++)
			{
				_id[i]=_lecture[i].getL_id();
				_data[i]=_lecture[i].getL_time();
			}
		}
	}
	
	
	public View getDropDownView(int position,View view,ViewGroup viewGroup)
	{
		return getCustomView(position,view,viewGroup);
	}

	@Override public View getView(int position, View view, ViewGroup viewGroup) 
	{
		return getCustomView(position, view, viewGroup); 
	}

	public View getCustomView(int position, View view, ViewGroup viewGroup) 
	{
		
		LayoutInflater inflater = (LayoutInflater) _spinnerContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View spinnerItem=view;
		if(spinnerItem==null)
			spinnerItem = inflater.inflate(R.layout.spinner_custom, viewGroup, false);
		TextView txtSpinnerCustomId=(TextView) spinnerItem.findViewById(R.id.txtSpinnerCustomId);
		TextView txtSpinnerCustomData=(TextView)spinnerItem.findViewById(R.id.txtSpinnerCustomData);
		txtSpinnerCustomId.setText(""+_id[position]);
		txtSpinnerCustomData.setText(""+_data[position]);
		return spinnerItem; 
	}

}
