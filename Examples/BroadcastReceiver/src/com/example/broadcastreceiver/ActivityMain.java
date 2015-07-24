package com.example.broadcastreceiver;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class ActivityMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		FragmentManager fm = getFragmentManager();
		if(fm.findFragmentById(R.id.activity_main) == null)
			fm.beginTransaction().add(R.id.activity_main, new FragmentMain()).commit();
	}
}