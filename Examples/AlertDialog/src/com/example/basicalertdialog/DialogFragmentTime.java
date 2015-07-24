package com.example.basicalertdialog;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class DialogFragmentTime extends DialogFragment {
	private Calendar mTime;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mTime = (Calendar)getArguments().getSerializable(FragmentMain.EXTRA_DATE_TIME);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.time_picker, null);
		
		TimePicker timePicker = (TimePicker)v.findViewById(R.id.time_picker);
		timePicker.setCurrentHour(mTime.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(mTime.get(Calendar.MINUTE));
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				mTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mTime.set(Calendar.MINUTE, minute);
				getArguments().putSerializable(FragmentMain.EXTRA_DATE_TIME, mTime);
			}
		});
		
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.title_time)
				.setView(v)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						sendResults(Activity.RESULT_OK);
					}
				})
				.create();
	}
	
	public static DialogFragmentTime newInstance(Calendar c) {
		Bundle args = new Bundle();
		args.putSerializable(FragmentMain.EXTRA_DATE_TIME, c);
		
		DialogFragmentTime df = new DialogFragmentTime();
		df.setArguments(args);
		
		return df;
	}
	
	private void sendResults(int resultCode) {
		Intent data = new Intent();
		data.putExtra(FragmentMain.EXTRA_DATE_TIME, mTime);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
	}
}