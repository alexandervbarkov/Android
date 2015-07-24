package com.example.savingfiles;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class ActivityMain extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new FragmentMain()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class FragmentMain extends Fragment {
		private Person mPerson;
		private EditText mEtName;
		private EditText mEtAge;
		private static final String NAME = "com.example.savingfiles.name";
		private static final String AGE = "com.example.savingfiles.age";
		
		public FragmentMain() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setRetainInstance(true);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			
			mPerson = new Person();
			
			mEtName = (EditText)rootView.findViewById(R.id.et_name);
			mEtName.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					if(arg0.length() != 0)
						mPerson.setName(arg0.toString());
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					
				}
			});
			
			mEtAge = (EditText)rootView.findViewById(R.id.et_age);
			mEtAge.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.length() != 0)
						mPerson.setAge(Integer.parseInt(s.toString()));
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			
			loadData();
			
			return rootView;
		}
		
		@Override
		public void onPause() {
			super.onPause();
			
			saveData();
		}
		
		private void saveData() {
			SharedPreferences sp = getActivity().getPreferences(MODE_PRIVATE);
			Editor e = sp.edit();
			e.putString(NAME, mEtName.getText().toString());
			e.putInt(AGE, Integer.parseInt(mEtAge.getText().toString()));
			e.commit();
		}
		
		private void loadData() {
			SharedPreferences sp = getActivity().getPreferences(MODE_PRIVATE);
			if(sp.contains(NAME)) 
				mEtName.setText(sp.getString(NAME, ""));
			if(sp.contains(AGE))
				mEtAge.setText(Integer.toString(sp.getInt(AGE, 0)));
		}
	}
}
