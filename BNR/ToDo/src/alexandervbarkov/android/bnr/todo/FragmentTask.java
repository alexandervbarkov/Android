package alexandervbarkov.android.bnr.todo;

import java.util.Calendar;
import java.util.UUID;


import alexandervbarkov.android.bnr.todo.R;
import alexandervbarkov.android.bnr.todo.ListFragmentTaskList.Callbacks;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentTask extends Fragment {
	public static final String EXTRA_TASK_ID = "bnr.android.todo.taskid";
	public static final String EXTRA_FILENAME = "bnr.android.todo.filename";
	public static final String EXTRA_IMAGE_PATH = "bnr.android.todo.image_path";
	public static final String EXTRA_THEME = "bnr.android.todo.theme";
	private static final String DIALOG_DATE = "bnr.android.todo.date";
	private static final String DIALOG_IMAGE = "bnr.android.todo.image";
	private static final String DIALOG_ABOUT = "bnr.android.todo.about";
	private static final int REQUEST_CODE_DATE = 0;
	private static final int REQUEST_CODE_CAMERA = 1;
	private static final int REQUEST_CODE_IMAGE = 2;
	private static final int REQUEST_CODE_SETTINGS = 3;
	
	private Task mTask;
	private EditText mEtTitle;
	private CheckBox mCbDone;
	private ImageButton mIbtnDate;
	private TextView mTvDate;
	private ImageButton mIbtnCamera;
	private ImageView mIvTask;
	private ImageButton mIbtnSend;
	private EditText mEtDescription;
	private Callbacks mCallbacks;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		UUID taskId = (UUID)getArguments().getSerializable(EXTRA_TASK_ID);
		mTask = TaskList.getTaskList(getActivity()).getTask(taskId);
		
		setHasOptionsMenu(true);
		
		mCallbacks.onTaskUpdated(mTask);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInsteanceState) {
		View v = inflater.inflate(R.layout.fragment_task, parent, false);
		
		if(NavUtils.getParentActivityName(getActivity()) != null)
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		PackageManager pm = getActivity().getPackageManager();
		boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || 
							pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) || 
							(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
		mIbtnCamera = (ImageButton)v.findViewById(R.id.ibtn_camera);
		if(!hasCamera)
			mIbtnCamera.setEnabled(false);
		else {
			mIbtnCamera.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), ActivityCamera.class);
					i.putExtra(EXTRA_FILENAME, mTask.getId().toString() + ".jpg");
					startActivityForResult(i, REQUEST_CODE_CAMERA);
				}
			});
		}
		
		mIvTask = (ImageView)v.findViewById(R.id.iv_task);
		mIvTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mTask.getPhoto() != null) {
					String path = getActivity().getFileStreamPath(mTask.getPhoto().getFilename()).getAbsolutePath();
					FragmentDialogImage dialog = FragmentDialogImage.newInstance(path);
					dialog.setTargetFragment(FragmentTask.this, REQUEST_CODE_IMAGE);
					dialog.show(getActivity().getSupportFragmentManager(), DIALOG_IMAGE);
				}
			}
		});
		
		mEtTitle = (EditText) v.findViewById(R.id.et_task_title);
		mEtTitle.setText(mTask.toString());
		mEtTitle.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() != 0)
					mTask.setTitle(s.toString());
				else
					mTask.setTitle("");
				mCallbacks.onTaskUpdated(mTask);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mIbtnDate = (ImageButton)v.findViewById(R.id.ibtn_date);
		mIbtnDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentDialogDatePicker dialog = FragmentDialogDatePicker.newInstance(mTask.getDate(), mTask.isDateSet()); 
				dialog.setTargetFragment(FragmentTask.this, REQUEST_CODE_DATE);
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_DATE);
			}
		});
		
		mTvDate = (TextView)v.findViewById(R.id.tv_date);
		updateDate();
		
		mCbDone = (CheckBox)v.findViewById(R.id.cb_done);
		mCbDone.setChecked(mTask.isDone());
		mCbDone.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mTask.setDone(isChecked);
				mCallbacks.onTaskUpdated(mTask);
			}
		});
		
		mIbtnSend = (ImageButton)v.findViewById(R.id.ibtn_send);
		mIbtnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT, getReport());
				i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
				i = Intent.createChooser(i, getString(R.string.report_send));
				startActivity(i);
				//Log.d("ToDo", getReport());
			}
		});
		
		mEtDescription = (EditText) v.findViewById(R.id.et_description);
		mEtDescription.setText(mTask.getDescription());
		mEtDescription.addTextChangedListener(new TextWatcher() {	
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() != 0)
					mTask.setDescription(s.toString());
				else
					mTask.setDescription("");
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		showPhoto();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		TaskList.getTaskList(getActivity()).saveTasks();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		UtilsImage.cleanImageView(mIvTask);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_DATE && resultCode == Activity.RESULT_OK) {
			mTask.setDate((Calendar)data.getSerializableExtra(FragmentDialogDatePicker.EXTRA_TASK_DATE_TIME));
			mTask.setDateSet(true);
			updateDate();
			mCallbacks.onTaskUpdated(mTask);
		}
		
		if(requestCode == REQUEST_CODE_DATE && resultCode == Activity.RESULT_FIRST_USER) {
			mTask.setDateSet(false);
			updateDate();
			mCallbacks.onTaskUpdated(mTask);
		}
		
		if(requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
			mTask.setPhoto(new Photo(mTask.getId().toString() + ".jpg"));
			showPhoto();
			mCallbacks.onTaskUpdated(mTask);
		}
		
		if(requestCode ==  REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
			mTask.deletePhoto(getActivity());
			mIvTask.setImageDrawable(null);
			showPhoto();
			mCallbacks.onTaskUpdated(mTask);
		}
		
		if(requestCode == REQUEST_CODE_SETTINGS) {
			Log.d("ToDo", "recreated in task " + UtilsSettings.getTheme(getActivity().getApplication()));
			getActivity().recreate();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		if(getActivity().findViewById(R.id.cont_fragment_two) == null)
			inflater.inflate(R.menu.fragment_task_option, menu);
		else
			inflater.inflate(R.menu.fragment_task_option_two, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				if(NavUtils.getParentActivityName(getActivity()) != null)
					NavUtils.navigateUpFromSameTask(getActivity());
				return true;
			case R.id.item_delete_task:
				if(getActivity().findViewById(R.id.cont_fragment_two) == null)
					((ActivityViewPagerTask)getActivity()).deletePage();
				else {
					mCallbacks.onDeleteTask(mTask);
					mCallbacks.onTaskUpdated(mTask);
				}
				return true;
			case R.id.item_settings:
				Intent i2 = new Intent(getActivity(), ActivitySettings.class);
				startActivityForResult(i2, REQUEST_CODE_SETTINGS);
				return true;
			case R.id.item_about:
				FragmentDialogAbout dialog = new FragmentDialogAbout();
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_ABOUT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public static FragmentTask newInstance(UUID id) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TASK_ID, id);
		FragmentTask ft = new FragmentTask();
		ft.setArguments(args);
		return ft;
	}
	
	private void updateDate() {
		if(mTask.isDateSet() == true) {
			mTvDate.setVisibility(View.VISIBLE);
			mTvDate.setText(Task.getFormattedDate(mTask.getDate()));
		}
		else
			mTvDate.setVisibility(View.GONE);
	}
	
	private void showPhoto() {
		if(mTask.getPhoto() != null) {
			String path = getActivity().getFileStreamPath(mTask.getPhoto().getFilename()).getAbsolutePath();
			BitmapDrawable b = UtilsImage.getScaledImage(getActivity(), path, mIvTask.getLayoutParams().width, mIvTask.getLayoutParams().height);
			if(b != null) {
				UtilsImage.setScaledImageView(mIvTask, b);
				mIvTask.setVisibility(View.VISIBLE);
				mIvTask.setImageDrawable(b);
			}
		}
		else {
			int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
			int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
			UtilsImage.resetScaledImageView(mIvTask, width, height);
			mIvTask.setVisibility(View.GONE);
		}
	}
	
	private String getReport() {
		String report;
		if(mTask.isDone() == true) 
			report = getString(R.string.report_done);
		else
			report = getString(R.string.report_not_done);
		
		if(mTask.getTitle() != null)
			report += mTask.getTitle();
		
		report += "\n";
		
		if(mTask.isDateSet() == true)
			report += getString(R.string.report_date) + Task.getFormattedDate(mTask.getDate()) + "\n";
		
		if(mTask.getDescription() != null)
			report += mTask.getDescription();
		
		return report;
	}
	
	public interface Callbacks {
		void onTaskUpdated(Task t);
		void onDeleteTask(Task t);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks)activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
}