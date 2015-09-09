package alexandervbarkov.android.bnr.locationtracker;

import android.support.v4.app.Fragment;

public class ActivityRecordsList extends ActivitySingleFragment {
	@Override
	protected Fragment createFragment() {
		return new FragmentRecordsList();
	}
}