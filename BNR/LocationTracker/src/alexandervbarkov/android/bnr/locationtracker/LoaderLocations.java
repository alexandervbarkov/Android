package alexandervbarkov.android.bnr.locationtracker;

import android.content.Context;
import android.location.Location;

public class LoaderLocations extends LoaderData<Location> {
	private long mRecordId;
	
	public LoaderLocations(Context context, long recordId) {
		super(context);
		mRecordId = recordId;
	}

	@Override
	public Location loadInBackground() {
		return TrackerManager.get(getContext()).getLastLocationForRecord(mRecordId);
	}
}