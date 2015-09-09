package alexandervbarkov.android.bnr.locationtracker;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentTracker extends Fragment {
	//private static final String TAG = "FragmentTracking";
	private static final String ARG_RECORD_ID = "record_id";
	private static final int LOAD_RECORD = 0;
	private static final int LOAD_LOCATION = 1;
	private static final String DIALOG_ABOUT = "alexandervbarkov.android.bnr.locationtracker.about";
	private static final String PREF_METRIC = "metric";
	private static final double MI = 0.621371;
	
	private EditText mEtTitle;
	private Switch mSwitchTracking;
	private TextView mTvStartTime, mTvDuration, mTvDistance, mTvCurrentSpeed, mTvAverageSpeed;
	private TrackerManager mTrackerManager;
	private Location mLastLocation;
	private double mCurrentSpeed;
	private Record mRecord;
	private boolean mMetric;
	private BroadcastReceiver mLocationReceiver = new ReceiverLocation() {
		@Override
		protected void onLocationReceived(Context context, Location location) {
			if(!mTrackerManager.isTrackingRecord(mRecord))
				return;
			location.setTime(new Date().getTime());
			if(mLastLocation != null) {
				double newDistance = mLastLocation.distanceTo(location) / 1000;
				if(newDistance > 0.0005) {
					mRecord.setDistance(mRecord.getDistance() + newDistance);
					mCurrentSpeed = newDistance / ((double)(location.getTime() - mLastLocation.getTime()) / (1000 * 3600));
				}
				else 
					mCurrentSpeed = 0;
			}
			mLastLocation = location;
			if(isVisible())
				updateUI();
			
			// Test
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
			Log.d("lt", "startTime=" + sdf.format(mRecord.getStartTime()) + 
					" currentTime=" + sdf.format(new Date(location.getTime())) + 
					" elapsedTime=" + ((location.getTime() - mRecord.getStartTime().getTime()) / 1000) + 
					" offBy=" + ((location.getTime() - new Date().getTime()) / 1000));
		}
		
		@Override
		protected void onProviderEngabledChanged(Context context, boolean enabled) {
			int toastText = enabled ? R.string.toast_frag_tracker_gps_enabled : R.string.toast_frag_tracker_gps_disabled;
			Toast.makeText(getActivity().getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
		}
	};
	private Callbacks mCallbacks;
	
	public static FragmentTracker newInstance(long recordId) {
		Bundle args = new Bundle();
		args.putLong(ARG_RECORD_ID, recordId);
		FragmentTracker ft = new FragmentTracker();
		ft.setArguments(args);
		return ft;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mCallbacks = (Callbacks)activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		setHasOptionsMenu(true);
		
		mTrackerManager = TrackerManager.get(getActivity().getApplicationContext());
		
		mMetric = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean(PREF_METRIC, false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		if(NavUtils.getParentActivityName(getActivity()) != null)
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		View v = inflater.inflate(R.layout.fragment_tracker, root, false);
		
		mEtTitle = (EditText)v.findViewById(R.id.et_frag_tracker_title);
		mSwitchTracking = (Switch)v.findViewById(R.id.switch_frag_tracker_tracking);
		mTvStartTime = (TextView)v.findViewById(R.id.tv_frag_tracker_start_time);
		mTvDistance = (TextView)v.findViewById(R.id.tv_frag_tracker_distance);
		mTvDuration = (TextView)v.findViewById(R.id.tv_frag_tracker_duration);
		mTvCurrentSpeed = (TextView)v.findViewById(R.id.tv_frag_tracker_current_speed);
		mTvAverageSpeed = (TextView)v.findViewById(R.id.tv_frag_tracker_average_speed);
		
		mEtTitle.setEnabled(false);
		
		mSwitchTracking.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					if(mRecord == null) { 
						mRecord = mTrackerManager.startNewRecord();
						mCallbacks.restartMapFragment();
						mEtTitle.setEnabled(true);
					}
					else
						mTrackerManager.startTrackingRecord(mRecord);
					setupUI();
					updateUI();
				}
				else {
					mTrackerManager.stopTrackingRecord();
					updateUI();
				}
			}
		});
		mSwitchTracking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mSwitchTracking.isEnabled())
					Toast.makeText(getActivity(), R.string.toast_frag_record_tracker_already_tracking, Toast.LENGTH_LONG).show();
			}
		});
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		getActivity().registerReceiver(mLocationReceiver, new IntentFilter(TrackerManager.ACTION_LOCATION));
	}
	
	@Override
	public void onResume() {
		// Check for a record id as an argument. If found, find the run
		Bundle args = getArguments();
		if(args != null) {
			long recordId = args.getLong(ARG_RECORD_ID, -1);
			if(recordId != -1) {
				getLoaderManager().initLoader(LOAD_RECORD, args, new LoaderCallbacksRecords());
				getLoaderManager().initLoader(LOAD_LOCATION, args, new LoaderCallbacksLocations());
			}
		}
		else if(mRecord != null) {
			args = new Bundle();
			args.putLong(ARG_RECORD_ID, mRecord.getId());
			getLoaderManager().initLoader(LOAD_RECORD, args, new LoaderCallbacksRecords());
			getLoaderManager().initLoader(LOAD_LOCATION, args, new LoaderCallbacksLocations());
			args = null;
		}
		
		if(mRecord != null) {
			setupUI();
			updateUI();
		}
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if(mRecord != null) {
			String title = mEtTitle.getText().toString();
			if(title.length() != 0)
				mRecord.setTitle(title);
			else 
				mRecord.setTitle("");
			mTrackerManager.updateRecordTitle(mRecord);
		}
		getLoaderManager().destroyLoader(LOAD_RECORD);
		getLoaderManager().destroyLoader(LOAD_LOCATION);
		
		super.onPause();
	}
	
	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		
		super.onStop();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		mCallbacks = null;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.opt_menu_frag_tracker, menu);
		
		MenuItem item = menu.findItem(R.id.item_frag_tracker_units);
		item.setTitle(mMetric ? getString(R.string.item_frag_tracker_imperial) : getString(R.string.item_frag_tracker_metric));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if(itemId == R.id.item_frag_tracker_delete_record) {
			if(mRecord != null) {
				if(mTrackerManager.isTrackingRecord(mRecord))
					mTrackerManager.stopTrackingRecord();
				mTrackerManager.deleteRecord(mRecord);
				mTrackerManager.deleteAllLocationsForRecord(mRecord);
			}
			getActivity().finish();
			return true;
		}
		else if(itemId == R.id.item_frag_tracker_units) {
			mMetric = !mMetric;
			PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putBoolean(PREF_METRIC, mMetric).commit();
			item.setTitle(mMetric ? getString(R.string.item_frag_tracker_imperial) : getString(R.string.item_frag_tracker_metric));
			setupUI();
			updateUI();
			return true;
		}
		else if(itemId == R.id.item_frag_tracker_about) {
			FragmentDialogAbout dialog = new FragmentDialogAbout();
			dialog.show(getActivity().getSupportFragmentManager(), DIALOG_ABOUT);
			return true;
		}
		else if(itemId == android.R.id.home) {
			Intent i = new Intent(getActivity().getApplicationContext(), ActivityRecordsList.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return true;
		}
		else
			return super.onOptionsItemSelected(item);
	}
	
	private void setupUI() {
		if(mRecord != null) {
			mEtTitle.setEnabled(true);
			mEtTitle.setText(mRecord.getTitle());
			mSwitchTracking.setChecked(mTrackerManager.isTrackingRecord(mRecord));
			mTvStartTime.setText(mRecord.getFormattedTime());
			mTvDistance.setText(Double.toString(Math.floor(mRecord.getDistance() * 10 * (mMetric ? 1 : MI)) / 10) + (mMetric ? " km" : " mi"));
			long trackingRecord = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getLong(TrackerManager.PREF_CURRENT_RECORD_ID, -1);
			if(trackingRecord != -1) {
				if(mRecord == null) {
					mSwitchTracking.setEnabled(false);
				}
				else {
					if(mRecord.getId() != trackingRecord) {
						mSwitchTracking.setEnabled(false);
					}
				}
			}
		}
	}
	
	private void updateUI() {
		if(mRecord != null && mLastLocation != null) {
			mTvDuration.setText(Record.formatDuration(mRecord.getDurationSeconds(mLastLocation.getTime())));
			mTvDistance.setText(Double.toString(Math.floor(mRecord.getDistance() * 10 * (mMetric ? 1 : MI)) / 10) + (mMetric ? " km" : " mi"));
			mTvAverageSpeed.setText(Double.toString(Math.floor((mRecord.getDistance() / ((double)mRecord.getDurationSeconds(mLastLocation.getTime()) / 3600)) * 10 * 
					(mMetric ? 1 : MI)) / 10) + (mMetric ? " kph" : " mph"));
			if(mSwitchTracking.isChecked())
				mTvCurrentSpeed.setText(Double.toString(Math.floor(mCurrentSpeed * 10 * (mMetric ? 1 : MI)) / 10) + (mMetric ? " kph" : " mph"));
			else
				mTvCurrentSpeed.setText(Double.toString(0.0) + (mMetric ? " kph" : " mph"));
		}
	}
	
	private class LoaderCallbacksRecords implements LoaderCallbacks<Record> {
		@Override
		public Loader<Record> onCreateLoader(int id, Bundle args) {
			Log.d("lt", "started loader records");
			return new LoaderRecords(getActivity().getApplicationContext(), args.getLong(ARG_RECORD_ID));
		}

		@Override
		public void onLoadFinished(Loader<Record> loader, Record record) {
			Log.d("lt", "finished loader records");
			mRecord = record;
			setupUI();
			updateUI();
		}

		@Override
		public void onLoaderReset(Loader<Record> loader) {
			
		}
	}
	
	private class LoaderCallbacksLocations implements LoaderCallbacks<Location> {
		@Override
		public Loader<Location> onCreateLoader(int id, Bundle args) {
			Log.d("lt", "started loader locations");
			return new LoaderLocations(getActivity().getApplicationContext(), args.getLong(ARG_RECORD_ID));
		}

		@Override
		public void onLoadFinished(Loader<Location> loader, Location location) {
			Log.d("lt", "finished loader locations");
			mLastLocation = location;
			updateUI();
		}

		@Override
		public void onLoaderReset(Loader<Location> loader) {
			
		}
	}
	
	public interface Callbacks {
		public void restartMapFragment();
	}
}