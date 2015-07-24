package com.example.basicalertdialog;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

public class DialogFragmentDate extends DialogFragment {
	private Calendar mDate;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDate = (Calendar)getArguments().getSerializable(FragmentMain.EXTRA_DATE_TIME);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
		
		DatePicker datePicker = (DatePicker)v.findViewById(R.id.date_picker);
		datePicker.init(mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month, int day) {
				mDate.set(year, month, day);
			}
		});
		
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.title_date)
				.setView(v)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						sendResults(Activity.RESULT_OK);
					}
				})
				.create();
	}
	
	public static DialogFragmentDate newInstance(Calendar c) {
		Bundle args = new Bundle();
		args.putSerializable(FragmentMain.EXTRA_DATE_TIME, c);
		
		DialogFragmentDate df = new DialogFragmentDate();
		df.setArguments(args);
		
		return df;
	}
	
	private void sendResults(int resultCode) {
		Intent data = new Intent();
		data.putExtra(FragmentMain.EXTRA_DATE_TIME, mDate);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
	}
}