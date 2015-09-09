package alexandervbarkov.android.bnr.locationtracker;

import android.content.Context;
import android.database.Cursor;

public class LoaderLocationsListCursor extends LoaderSQLiteCursor {
	private long mRecordId;
	
	public LoaderLocationsListCursor(Context context, long recordId) {
		super(context);
		mRecordId = recordId;
	}

	@Override
	public Cursor loadCursor() {
		return TrackerManager.get(getContext()).queryAllLocationsForRecord(mRecordId);
	}
}