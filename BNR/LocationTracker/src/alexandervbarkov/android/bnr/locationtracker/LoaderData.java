package alexandervbarkov.android.bnr.locationtracker;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class LoaderData<D> extends AsyncTaskLoader<D> {
	private D mData;

	public LoaderData(Context context) {
		super(context);
	}
	
	@Override
	protected void onStartLoading() {
		if(mData != null) 
			deliverResult(mData);
		else
			forceLoad();
	}
	
	@Override
	public void deliverResult(D data) {
		mData = data;
		if(isStarted())
			super.deliverResult(data);
	}
}