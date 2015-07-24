package com.example.basiclistview;

import java.util.ArrayList;

import android.content.Context;

public class Numbers {
	private static Numbers sNumbers;
	private Context mContext;
	private ArrayList<Number> mAllNumbers;
	
	private Numbers(Context context) {
		mContext = context;
		mAllNumbers = new ArrayList<Number>();
		for(int i = 0; i < 100; ++i)
			mAllNumbers.add(new Number(i));
	}
	
	public static Numbers getNumbers(Context context) {
		if(sNumbers == null)
			sNumbers = new Numbers(context.getApplicationContext());
		return sNumbers;
	}
	
	public ArrayList<Number> getAllNumbers() {
		return mAllNumbers;
	}
	
	public Number getNumber(int position) {
		return mAllNumbers.get(position);
	}
	
}
