package com.example.basicalertdialog;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentMain extends Fragment {
	private Calendar mDateTime;
	private TextView mTvDateTime;
	private Button mBtnDateTime;
	public static final int REQUEST_CODE_TIME_DATE = 0;
	public static final int REQUEST_CODE_DATE = 1;
	public static final int REQUEST_CODE_TIME = 2;
	public static final String EXTRA_DATE_TIME = "com.example.basicalertdialog.datetime";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		if(savedInstanceState != null)
			mDateTime = (Calendar)savedInstanceState.getSerializable(EXTRA_DATE_TIME);
		else	
			mDateTime = Calendar.getInstance();
		
		View v = inflater.inflate(R.layout.fragment_main, parent, false);
		
		mTvDateTime = (TextView)v.findViewById(R.id.tv_date_time);
		if(savedInstanceState != null)
			updateDateTime();
		else
			mTvDateTime.setText("Set date and time by clicking the button below");
		
		mBtnDateTime = (Button)v.findViewById(R.id.btn_date_time);
		mBtnDateTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DialogFragmentDateTime df = DialogFragmentDateTime.newInstance(mDateTime);
				df.setTargetFragment(FragmentMain.this, REQUEST_CODE_TIME_DATE);
				df.show(fm, "dateTime");
			}
		});
		
		return v;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == REQUEST_CODE_DATE) {
				Calendar c = (Calendar)data.getSerializableExtra(FragmentMain.EXTRA_DATE_TIME);
				mDateTime.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
			if(requestCode == REQUEST_CODE_TIME) {
				Calendar c = (Calendar)data.getSerializableExtra(FragmentMain.EXTRA_DATE_TIME);
				mDateTime.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
				mDateTime.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
			}
			updateDateTime();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_DATE_TIME, mDateTime);
	}
	
	private void updateDateTime() {
		String s = 	Integer.toString(mDateTime.get(Calendar.DATE)) + "/" +
					Integer.toString(mDateTime.get(Calendar.MONTH + 1)) + "/" +
					Integer.toString(mDateTime.get(Calendar.YEAR)) + " " +
					Integer.toString(mDateTime.get(Calendar.HOUR_OF_DAY)) + ":" +
					Integer.toString(mDateTime.get(Calendar.MINUTE));
		mTvDateTime.setText(s);
	}
}