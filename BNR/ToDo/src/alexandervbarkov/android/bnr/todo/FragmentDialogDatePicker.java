package alexandervbarkov.android.bnr.todo;

import java.util.Calendar;

import alexandervbarkov.android.bnr.todo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class FragmentDialogDatePicker extends DialogFragment {
	private Calendar mDateTime;
	private boolean mDateSet;
	public static final String EXTRA_TASK_DATE_TIME = "bnr.android.todo.task_date_time";
	public static final String EXTRA_TASK_DATE_SET = "bnr.android.todo.task_date_set";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDateTime = (Calendar)getArguments().getSerializable(EXTRA_TASK_DATE_TIME);
		mDateSet = getArguments().getBoolean(EXTRA_TASK_DATE_SET);
		
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);
		
		DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_picker);
		datePicker.init(mDateTime.get(Calendar.YEAR), mDateTime.get(Calendar.MONTH), mDateTime.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int month, int day) {
				mDateTime.set(year, month, day);
				getArguments().putSerializable(EXTRA_TASK_DATE_TIME, mDateTime);
			}
		});
		
		TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);
		timePicker.setCurrentHour(mDateTime.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(mDateTime.get(Calendar.MINUTE));
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				mDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				mDateTime.set(Calendar.MINUTE, minute);
				getArguments().putSerializable(EXTRA_TASK_DATE_TIME, mDateTime);
			}
		});
		
		Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.title_date_picker);
		builder.setView(v);
		builder.setPositiveButton(R.string.btn_date_add, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult(Activity.RESULT_OK);
			}
		});
		if(mDateSet == true)
			builder.setNegativeButton(R.string.btn_date_delete, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_FIRST_USER, null);
				}
			});
		
		return builder.create();
	}
	
	public static FragmentDialogDatePicker newInstance(Calendar c, boolean dateSet) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TASK_DATE_TIME, c);
		args.putBoolean(EXTRA_TASK_DATE_SET, dateSet);
		FragmentDialogDatePicker f = new FragmentDialogDatePicker();
		f.setArguments(args);
		
		return f;
	}
	
	private void sendResult(int resultCode) {
		if(getTargetFragment() == null)
			return;
		
		Intent i = new Intent();
		i.putExtra(EXTRA_TASK_DATE_TIME, mDateTime);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}
}