package alexandervbarkov.android.bnr.locationtracker;

import alexandervbarkov.android.bnr.locationtracker.DatabaseHelperRecord.CursorRecords;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentRecordsList extends ListFragment implements LoaderCallbacks<Cursor> {
	//private static final String TAG = "FragmentRecordsList";
	private static final int REQUEST_CODE_NEW_RECORD = 0;
	private static final int REQUEST_CODE_VIEW_RECORD = 1;
	private static final String DIALOG_ABOUT = "alexandervbarkov.android.bnr.locationtracker.about";
	
	private SharedPreferences mPrefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_records_list, root, false);
		
		ListView lvRecordsList = (ListView)v.findViewById(android.R.id.list);
		lvRecordsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		lvRecordsList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}
			
			@Override
			public void onDestroyActionMode(ActionMode mode) {
				
			}
			
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.cont_menu_frag_records_list, menu);
				return true;
			}
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				if(item.getItemId() == R.id.item_frag_records_list_delete_record) {
					CursorRecords cursor = (CursorRecords)((CursorAdapterRecords)getListAdapter()).getCursor();
					cursor.moveToFirst();
					if(!cursor.isAfterLast()) {
						TrackerManager trackerManager = TrackerManager.get(getActivity().getApplicationContext());
						Record record;
						for(int i = 0; i < getListAdapter().getCount(); ++i) {
							if(getListView().isItemChecked(i)) {
								record = cursor.getRecord();
								if(trackerManager.isTrackingRecord(record))
									trackerManager.stopTrackingRecord();
								trackerManager.deleteRecord(record);
								trackerManager.deleteAllLocationsForRecord(record);
							}
							cursor.moveToNext();
						}
						getLoaderManager().destroyLoader(0);
						getLoaderManager().initLoader(0, null, FragmentRecordsList.this);
						mode.finish();
					}
					return true;
				}
				else
					return false;
			}
			
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				
			}
		});
		
		return v;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.opt_menu_frag_records_list, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if(itemId == R.id.item_frag_records_list_new_record) {
			Intent i = new Intent(getActivity().getApplicationContext(), ActivityTrackerWithMap.class);
			startActivityForResult(i, REQUEST_CODE_NEW_RECORD);
			return true;
		} 
		else if(itemId == R.id.item_frag_records_list_about) {
			FragmentDialogAbout dialog = new FragmentDialogAbout();
			dialog.show(getActivity().getSupportFragmentManager(), DIALOG_ABOUT);
			return true;
		}
		else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getLoaderManager().destroyLoader(0);
		getLoaderManager().initLoader(0, null, this);
		//getLoaderManager().restartLoader(0, null, this);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity().getApplicationContext(), ActivityTrackerWithMap.class);
		// id argument is given by the CursorAdapter because we named a column _id
		i.putExtra(ActivityTracker.EXTRA_RECORD_ID, id);
		startActivityForResult(i, REQUEST_CODE_VIEW_RECORD);
	}
	
	private static class CursorAdapterRecords extends CursorAdapter {
		private CursorRecords mCursor;
		private long mCurrentId;
		
		public CursorAdapterRecords(Context context, Cursor cursor, long currentId) {
			super(context, cursor, 0);
			mCursor = (CursorRecords)cursor;
			mCurrentId = currentId;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Record record = mCursor.getRecord();
			TextView tvTitle = (TextView)view.findViewById(R.id.tv_frag_records_list_list_item_title);
			tvTitle.setText(record.getTitle());
			tvTitle.setTextColor(Color.parseColor("#000000"));
			ImageView ivIcon = (ImageView)view.findViewById(R.id.iv_frag_records_list_list_item_icon);
			if(record.getId() == mCurrentId)
				ivIcon.setVisibility(View.VISIBLE);
			else
				ivIcon.setVisibility(View.GONE);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// Use layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(R.layout.fragment_records_list_list_item, parent, false);
		}
	}
	
	private static class LoaderRecordsListCursor extends LoaderSQLiteCursor {
		public LoaderRecordsListCursor(Context context) {
			super(context);
		}

		@Override
		public Cursor loadCursor() {
			return TrackerManager.get(getContext()).queryRecords();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new LoaderRecordsListCursor(getActivity().getApplicationContext());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		long currentRecordId = mPrefs.getLong(TrackerManager.PREF_CURRENT_RECORD_ID, -1);
		CursorAdapterRecords adapter = new CursorAdapterRecords(getActivity().getApplicationContext(), (CursorRecords)cursor, currentRecordId);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Stop using the cursor via the adapter
		setListAdapter(null);
	}
}