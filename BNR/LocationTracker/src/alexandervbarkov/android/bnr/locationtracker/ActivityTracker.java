package alexandervbarkov.android.bnr.locationtracker;

import android.support.v4.app.Fragment;

public class ActivityTracker extends ActivitySingleFragment {
	public static final String EXTRA_RECORD_ID = "alexandervbarkov.android.bnr.locationtracker.record_id";
	
	@Override
	protected Fragment createFragment() {
		long recordId = getIntent().getLongExtra(EXTRA_RECORD_ID, -1);
		if(recordId != -1)
			return FragmentTracker.newInstance(recordId);
		else
			return new FragmentTracker();
	}
}