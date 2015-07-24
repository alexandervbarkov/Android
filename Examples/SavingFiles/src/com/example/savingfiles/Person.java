package com.example.savingfiles;

public class Person {
	private String mName;
	private int mAge;
	
	public Person() {
		mName = new String();
		mAge = 0;
	}
	
	public Person(String name, int age) {
		mName = name;
		mAge = age;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public int getAge() {
		return mAge;
	}

	public void setAge(int age) {
		mAge = age;
	}	
}