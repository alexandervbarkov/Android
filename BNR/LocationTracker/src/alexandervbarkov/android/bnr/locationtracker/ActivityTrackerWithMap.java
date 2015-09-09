package alexandervbarkov.android.bnr.locationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ActivityTrackerWithMap extends FragmentActivity implements FragmentTracker.Callbacks {
	public static final String EXTRA_RECORD_ID = "alexandervbarkov.android.bnr.locationtracker.record_id";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_two_fragments);
		
		FragmentManager fm = getSupportFragmentManager();
		
		if(fm.findFragmentById(R.id.cont_frag_1) == null) {
			long recordId = getIntent().getLongExtra(EXTRA_RECORD_ID, -1);
			Fragment fragment;
			if(recordId != -1)
				fragment = FragmentTracker.newInstance(recordId);
			else
				fragment = new FragmentTracker();
			fm.beginTransaction().add(R.id.cont_frag_1, fragment).commit();
		}

		if(fm.findFragmentById(R.id.cont_frag_2) == null) {
			long recordId = getIntent().getLongExtra(EXTRA_RECORD_ID, -1);
			Fragment fragment;
			if(recordId != -1)
				fragment = FragmentRecordMap.newInstance(recordId);
			else
				fragment = new FragmentRecordMap();
			fm.beginTransaction().add(R.id.cont_frag_2, fragment).commit();
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean notifaction = intent.getBooleanExtra(TrackerManager.EXTRA_NOTIFICATION, false);
		if(notifaction) {
			restartMapFragment();
			restartTrackerFragment();
		}
	}

	@Override
	public void restartMapFragment() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.cont_frag_2);
		if(fragment != null) {
			fm.beginTransaction().remove(fragment).commit();
			long recordId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(TrackerManager.PREF_CURRENT_RECORD_ID, -1);
			if(recordId != -1)
				fragment = FragmentRecordMap.newInstance(recordId);
			else
				fragment = new FragmentRecordMap();
			fm.beginTransaction().add(R.id.cont_frag_2, fragment).commit();
		}
	}
	
	public void restartTrackerFragment() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.cont_frag_1);
		if(fragment != null) {
			fm.beginTransaction().remove(fragment).commit();
			long recordId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(TrackerManager.PREF_CURRENT_RECORD_ID, -1);
			if(recordId != -1)
				fragment = FragmentTracker.newInstance(recordId);
			else
				fragment = new FragmentTracker();
			fm.beginTransaction().add(R.id.cont_frag_1, fragment).commit();
		}
	}
}