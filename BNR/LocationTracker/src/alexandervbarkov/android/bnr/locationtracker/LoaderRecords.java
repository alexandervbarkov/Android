package alexandervbarkov.android.bnr.locationtracker;

import android.content.Context;

public class LoaderRecords extends LoaderData<Record> {
	private long mRecordId;
	
	public LoaderRecords(Context context, long recordId) {
		super(context);
		mRecordId = recordId;
	}

	@Override
	public Record loadInBackground() {
		return TrackerManager.get(getContext()).getRecord(mRecordId);
	}
}