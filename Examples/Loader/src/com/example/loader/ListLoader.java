package com.example.loader;

import java.util.ArrayList;
import java.util.List;
import android.content.AsyncTaskLoader;
import android.content.Context;

public class ListLoader extends AsyncTaskLoader<List<String>> {
	public ListLoader(Context context) {
		super(context);
	}
	
	@Override
	protected void onStartLoading() {
		forceLoad();
	}

	@Override
	public List<String> loadInBackground() {
		List<String> data = new ArrayList<String>();
		for(int i = 0; i < 20; ++i)
			data.add(Integer.toString(i));
		return data;
	}
	
	@Override
	public void deliverResult(List<String> data) {
		super.deliverResult(data);
	}
}