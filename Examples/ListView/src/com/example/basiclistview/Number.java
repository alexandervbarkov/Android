package com.example.basiclistview;

public class Number {
	private int mValue;
	private boolean mOdd;
	
	Number(int value) {
		mValue = value;
		mOdd = (value % 2 == 1);
	}
	
	public String toString() {
		return String.valueOf(mValue);
	}
	
	public int getValue() {
		return mValue;
	}
	
	public void setValue(int value) {
		mValue = value;
	}
	
	public boolean isOdd() {
		return mOdd;
	}
	
	public void setOdd(boolean odd) {
		mOdd = odd;
	}
}
