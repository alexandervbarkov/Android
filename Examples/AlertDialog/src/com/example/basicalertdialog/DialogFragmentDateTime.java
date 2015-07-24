package com.example.basicalertdialog;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class DialogFragmentDateTime extends DialogFragment {
	private Calendar mDateTime;
	private Button mBtnDate;
	private Button mBtnTime;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDateTime = (Calendar)getArguments().getSerializable(FragmentMain.EXTRA_DATE_TIME);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_time, null);
		
		mBtnDate = (Button)v.findViewById(R.id.btn_date);
		mBtnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DialogFragmentDate df = DialogFragmentDate.newInstance(mDateTime);
				df.setTargetFragment(getTargetFragment(), FragmentMain.REQUEST_CODE_DATE);
				df.show(fm, "date");
			}
		});

		mBtnTime = (Button)v.findViewById(R.id.btn_time);
		mBtnTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				DialogFragmentTime df = DialogFragmentTime.newInstance(mDateTime);
				df.setTargetFragment(getTargetFragment(), FragmentMain.REQUEST_CODE_TIME);
				df.show(fm, "time");
			}
		});
		
		return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.title_date_time)
					.setView(v)
					.create();		
	}
	
	public static DialogFragmentDateTime newInstance(Calendar c) {
		Bundle args = new Bundle();
		args.putSerializable(FragmentMain.EXTRA_DATE_TIME, c);
		
		DialogFragmentDateTime df = new DialogFragmentDateTime();
		df.setArguments(args);
		
		return df;
	}
}