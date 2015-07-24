package com.example.streamingvideo;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class ActivityPlayer extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_player);
		
		FragmentManager fm = getFragmentManager();
		if(fm.findFragmentById(R.id.cont_frag) == null)
			fm.beginTransaction().add(R.id.cont_frag, new FragmentPlayer()).commit();
	}
}