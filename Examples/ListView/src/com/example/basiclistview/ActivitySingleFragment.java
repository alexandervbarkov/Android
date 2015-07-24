package com.example.basiclistview;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public abstract class ActivitySingleFragment extends Activity {
	protected abstract Fragment createFragment();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_fragment_container);
		
		FragmentManager fm = getFragmentManager();
		Fragment f = fm.findFragmentById(R.id.container_fragment);
		if(f == null) {
			f = createFragment();
			fm.beginTransaction().add(R.id.container_fragment, f).commit();
		}
	}
}
