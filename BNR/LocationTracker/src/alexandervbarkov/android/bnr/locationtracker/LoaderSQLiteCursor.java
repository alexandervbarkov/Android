package alexandervbarkov.android.bnr.locationtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public abstract class LoaderSQLiteCursor extends AsyncTaskLoader<Cursor> {
	private Cursor mCursor;
	
	public LoaderSQLiteCursor(Context context) {
		super(context);
	}

	public abstract Cursor loadCursor(); 
	
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = loadCursor();
		if(cursor != null)
			// Make sure that the date is available in memory once it is passed over the main thread
			cursor.getCount();
		return cursor;
	}

	@Override
	public void deliverResult(Cursor data) {
		Cursor oldCursor = mCursor;
		mCursor = data;
		if(isStarted())
			super.deliverResult(data);
		if(oldCursor != null && oldCursor != data && !oldCursor.isClosed())
			oldCursor.close();
	}
	
	@Override
	protected void onStartLoading() {
		if(mCursor != null)
			deliverResult(mCursor);
		if(takeContentChanged() || mCursor == null)
			forceLoad();
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(Cursor data) {
		if(data != null && !data.isClosed()) 
			data.close();
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		
		onStopLoading();
		if(mCursor != null && !mCursor.isClosed())
			mCursor.close();
		mCursor = null;
	}
}