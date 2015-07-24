package com.example.loader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityMain extends Activity {
	private ListView mListView;
	private AdapterList mAdapter;
	private LoaderCallbacks<List<String>> mLoaderCallbacks = new LoaderCallbacks<List<String>>() {
		@Override
		public Loader<List<String>> onCreateLoader(int id, Bundle args) {
			return new ListLoader(getApplicationContext());
		}

		@Override
		public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
			mAdapter.setData(data);
		}

		@Override
		public void onLoaderReset(Loader<List<String>> loader) {
			
		}
	};
	
	private class AdapterList extends ArrayAdapter<String> {
		List<String> mList;
		
		public AdapterList(Context context, List<String> list) {
			super(context, 0, list);
			mList = list;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = getLayoutInflater().inflate(R.layout.item, parent, false);
			
			TextView item = (TextView)convertView.findViewById(R.id.item);
			item.setText(mList.get(position));
			
			return convertView;
		}
		
		public void setData(List<String> data) {
			mList = data;
			clear();
			addAll(data);
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mAdapter = new AdapterList(getApplicationContext(), new ArrayList<String>());
		mListView = (ListView)findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
	
		getLoaderManager().initLoader(0, null, mLoaderCallbacks);
	}
}