package com.example.basicalertdialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ActivityMain extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.container_fragment);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.cont_frag);
		if(f == null) {
			f = new FragmentMain();
			fm.beginTransaction().add(R.id.cont_frag, f).commit();
		}
	}
}