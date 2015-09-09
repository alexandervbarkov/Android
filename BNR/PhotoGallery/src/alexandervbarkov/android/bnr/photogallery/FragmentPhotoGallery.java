package alexandervbarkov.android.bnr.photogallery;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

public class FragmentPhotoGallery extends FragmentVisible {
	//private static final String TAG = "FragmentPhotoGallery";
	public static final String EXTRA_URL = "alexandervbarkov.android.bnr.photogallery.url";
	private static final String DIALOG_ABOUT = "alexandervbarkov.android.bnr.locationtracker.about";
	
	private GridView mGridView;
	private ArrayList<GalleryItem> mItems;
	private int mPage;
	private int mSearchPage;
	private boolean mDoneLoading;
	private ThumbnailDownloader<ImageView> mThumbnailDownloader;
	private ThumbnailCacher mThumbnailCacher;
	private AdapterGalleryItem<GalleryItem> mAdapter;
	
	// Caching and preloading
	private boolean mInitialSetupDone;
	private int mLastVisiblePosition;
	private int mFirstVisiblePosition;
	private int mItemsOnScreen;
	private int mItemsInRow;
	
	private SearchView mSearchView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		mItems = new ArrayList<GalleryItem>();
		mPage = 1;
		mSearchPage = 1;
		mDoneLoading = false;
		
		mInitialSetupDone = false;
		
		mAdapter = new AdapterGalleryItem<GalleryItem>(mItems);
		mThumbnailCacher = new ThumbnailCacher();
		mThumbnailDownloader = new ThumbnailDownloader<ImageView>(new Handler(), mThumbnailCacher);
		mThumbnailDownloader.setListener(new ThumbnailDownloader.Listener<ImageView>() {
			@Override
			public void onThumbnailDownloaded(ImageView imageView, Bitmap bitmap) {
				if(isVisible() == true)
					imageView.setImageBitmap(bitmap);
				initialSetup();
				if(mItemsOnScreen != 0)
					mInitialSetupDone = true;
			}
		});
		mThumbnailDownloader.start();
		mThumbnailDownloader.getLooper();

		PollService.setup(getActivity().getApplicationContext(), mPage, mSearchPage);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_gallery, root, false);
		
		mGridView = (GridView)v.findViewById(R.id.gv_frag_photo_gallery);
		mGridView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getInt(FlickrFetchr.PREF_TOTAL, 0) > 0) {
					if(mDoneLoading && mInitialSetupDone) {
						mItemsInRow = mGridView.getNumColumns();
						// Scrolled up
						if(mLastVisiblePosition < mGridView.getLastVisiblePosition()) {
							preloadOnScrollUp();
							mLastVisiblePosition = mGridView.getLastVisiblePosition();
							removeOnScrollUp();
						}
						//Scrolled down
						else if (mLastVisiblePosition > mGridView.getLastVisiblePosition()){
							preloadOnScrollDown();
							mLastVisiblePosition = mGridView.getLastVisiblePosition();
							removeOnScrollDown();
						}
						// Reached end of the list. Load next page
						int pages = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getInt(FlickrFetchr.PREF_PAGES, 1);
						if(mGridView.getLastVisiblePosition() >= (mItems.size() - mItemsOnScreen - mItemsInRow) && pages > mPage && pages > mSearchPage) {
							Log.d("pg", "Loading new page");
							mDoneLoading = false;
							mFirstVisiblePosition = mGridView.getFirstVisiblePosition();
							if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(FlickrFetchr.PREF_SEARCH_QUERY, null) == null)
								++mPage;
							else
								++mSearchPage;
							updateItems();
						}
					}
				}
			}
		});
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				GalleryItem item = mItems.get(position);
				String photoPageUri = item.getPhotoPageUrl();
				
				Intent i = new Intent(getActivity().getApplicationContext(), ActivityPhotoPage.class);
				i.putExtra(EXTRA_URL, photoPageUri);
				startActivity(i);
			}
		});
		ViewTreeObserver vto = mGridView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				initialSetup();
				if(mItemsOnScreen != 0)
					mInitialSetupDone = true;
			}
		});
		
		updateItems();
		
		setupAdapter();
		
		return v;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		mItemsOnScreen = mGridView.getLastVisiblePosition() - mGridView.getFirstVisiblePosition() + 1;
		mItemsInRow = mGridView.getNumColumns();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		mThumbnailDownloader.clearQueue();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mThumbnailDownloader.quit();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.options_menu_fragment_photo_gallery, menu);
		
		// Pull out the search view
		MenuItem searchItem = menu.findItem(R.id.optm_frag_photo_gallery_search);
		mSearchView = (SearchView)searchItem.getActionView();
		
		// Get the data from searchable.xml as a SearchableInfo
		SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
		ComponentName componentName = getActivity().getComponentName();
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
		
		mSearchView.setSearchableInfo(searchableInfo);
		mSearchView.setSubmitButtonEnabled(true);
		setSearchViewText();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.optm_frag_photo_gallery_search:
				getActivity().onSearchRequested();
				getActivity().invalidateOptionsMenu();
				return true;
			case R.id.optm_frag_photo_gallery_clear:
				PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString(FlickrFetchr.PREF_SEARCH_QUERY, null).commit();
				PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putBoolean(FlickrFetchr.PREF_NEW_SEARCH, true).commit();
				updateItems();
				getActivity().invalidateOptionsMenu();
				return true;
			case R.id.optm_frag_photo_gallery_poll:
				boolean startAlarm = !PollService.isServiceAlarmOn();
				PollService.setServiceAlarm(startAlarm);
				getActivity().invalidateOptionsMenu();
				return true;
			case R.id.item_frag_photo_gallery_about:
				FragmentDialogAbout dialog = new FragmentDialogAbout();
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_ABOUT);
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		MenuItem miPole = menu.findItem(R.id.optm_frag_photo_gallery_poll);
		if(PollService.isServiceAlarmOn())
			miPole.setTitle(R.string.item_title_frag_photo_gallery_stop_poll);
		else
			miPole.setTitle(R.string.item_title_frag_photo_gallery_start_poll);
		
		MenuItem miSearch = menu.findItem(R.id.optm_frag_photo_gallery_search);
		miSearch.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				setSearchViewText();
				return true;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return false;
			}
		});
		
		MenuItem miClear = menu.findItem(R.id.optm_frag_photo_gallery_clear);
		if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(FlickrFetchr.PREF_SEARCH_QUERY, null) == null)
			miClear.setIcon(R.drawable.ic_menu_refresh);
		else
			miClear.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		
		setSearchViewText();
	}
	
	public void setSearchViewText() {
		String query = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
		if(query != null) {
			mSearchView.setQuery(query, false);
			int id = getActivity().getResources().getIdentifier("android:id/search_src_text", null, null);
			EditText text = (EditText)mSearchView.findViewById(id);
			text.selectAll();
		}
	}
	
	public void updateItems() {
		new TaskFetchItems().execute();
	}
	
	private class TaskFetchItems extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {
		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			Activity activity = getActivity();
			if(activity == null)
				return new ArrayList<GalleryItem>();
			String query = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
			boolean newSearch = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getBoolean(FlickrFetchr.PREF_NEW_SEARCH, true);
			if(newSearch) {
				mPage = 1;
				mSearchPage = 1;
			}
			if(query != null) {
				Log.d("pg", "Started searching for images of " + query);
				return new FlickrFetchr().searchItems(query, mSearchPage, getActivity().getApplicationContext());
			}
			else {
				Log.d("pg", "Started fetching recent images");
				return new FlickrFetchr().getRecent(mPage, getActivity().getApplicationContext());
			}
		}
		
		@Override
		protected void onPostExecute(ArrayList<GalleryItem> items) {
			// testing
			String query = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
			if(query != null)
				Log.d("pg", "Finished searching images for " + query + ". Items size=" + items.size());
			else
				Log.d("pg", "Finished fetching recent images. Items size=" + items.size());
			// finished testing. delete code above
			if(PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getBoolean(FlickrFetchr.PREF_NEW_SEARCH, true)) {
				mItems.clear();
				mItems.addAll(items);
				mThumbnailCacher.removeAllBitmapsFromMemory();
				mInitialSetupDone = false;
				initialSetup();
				if(mItemsOnScreen != 0)
					mInitialSetupDone = true;
			}
			else
				mItems.addAll(items);
			
			PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putBoolean(FlickrFetchr.PREF_NEW_SEARCH, false).commit();
			setupAdapter();
			int total = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getInt(FlickrFetchr.PREF_TOTAL, 0);
			Toast.makeText(getActivity(), total + " " + getString(R.string.toast_total), Toast.LENGTH_LONG).show();
			mDoneLoading = true;
			
			getActivity().invalidateOptionsMenu();
		}
	}
	
	private void setupAdapter() {
		if(getActivity() == null || mGridView == null) 
			return;
		
		if(mItems.size() > 0) {	
			mGridView.setAdapter(mAdapter);
			mGridView.setSelection(mFirstVisiblePosition);
		}
		else
			mGridView.setAdapter(null);
	}
	
	@SuppressWarnings("hiding")
	private class AdapterGalleryItem<GalleryItem> extends ArrayAdapter<GalleryItem> {
		AdapterGalleryItem(ArrayList<GalleryItem> items) {
			super(getActivity(), 0, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.gv_item, parent, false);

			ImageView ivItem = (ImageView)convertView.findViewById(R.id.iv_frag_photo_gallery_gv_item);
			GalleryItem item = getItem(position);
			
			Bitmap bitmap = mThumbnailCacher.getBitmapFromMemory(((alexandervbarkov.android.bnr.photogallery.GalleryItem)item).getUrl());
			if(bitmap != null)
				ivItem.setImageBitmap(bitmap);
			else
				mThumbnailDownloader.queueThummbnail(ivItem, ((alexandervbarkov.android.bnr.photogallery.GalleryItem)item).getUrl());
			
			return convertView;
		}
		
	}
	
	private void initialSetup() {
		if(!mInitialSetupDone) {
			mItemsOnScreen = mGridView.getLastVisiblePosition() - mGridView.getFirstVisiblePosition() + 1;
			mLastVisiblePosition = mItemsOnScreen - 1;
			mFirstVisiblePosition = 0;
			mItemsInRow = mGridView.getNumColumns();
			Log.d("pg", "Started initialSetup");
			initialPreload();
		}
	}
	
	private void initialPreload() {
		Log.d("pg", "initialPreload mItemsOnScreen=" + mItemsOnScreen);
		for(int i = 1; i <= mItemsOnScreen; ++i) {
			if(mItems.size() > mLastVisiblePosition + i) {
				preloadImage(i + mLastVisiblePosition);
				Log.d("pg", "Initial preloading position " + (i + mLastVisiblePosition) + " mPage=" + mPage + " mSearchPage=" + mSearchPage);
			}
		}
	}
	
	private void preloadOnScrollUp() {
		if(mLastVisiblePosition + mItemsOnScreen + mItemsInRow < mItems.size()) {
			for(int i = 1, position; i <= mItemsInRow; ++i) {
				position = i + mLastVisiblePosition + mItemsOnScreen;
				preloadImage(position);
				//Log.d("pg", "Preloaded image in position=" + position + " last visible item is in position=" + mLastVisiblePosition);
			}
		}
	}
	
	private void preloadOnScrollDown() {
		if(mLastVisiblePosition - (2 * mItemsOnScreen) - mItemsInRow - 1>= 0) {
			for(int i = 1, position; i <= mItemsInRow; ++i) {
				position = mLastVisiblePosition - (2 * mItemsOnScreen) - i - 1;
				preloadImage(position);
				//Log.d("pg", "Preloaded image in position=" + position + " first visible item is in position=" + mLastVisiblePosition);
			}
		}
	}
	
	private void preloadImage(int position) {
		mAdapter.getView(position, null, mGridView);
	}
	
	private void removeOnScrollUp() {
		if(mLastVisiblePosition - (2 * mItemsOnScreen) - mItemsInRow - 1>= 0) {
			for(int i = 1, position; i <= mItemsInRow; ++i) {
				position = mLastVisiblePosition - (2 * mItemsOnScreen) - i - 1;
				removeImage(position);
				//Log.d("pg", "Removed image in position=" + position);
			}
		}
	}
	
	private void removeOnScrollDown() {
		if(mLastVisiblePosition + mItemsOnScreen + mItemsInRow < mItems.size()) {
			for(int i = 1, position; i <= mItemsInRow; ++i) {
				position = i + mLastVisiblePosition + mItemsOnScreen;
				removeImage(position);
				//Log.d("pg", "Removed image in position=" + position);
			}
		}
	}
	
	private void removeImage(int position) {
		View v = mAdapter.getView(position, null, mGridView);
		ImageView imageView = (ImageView)v.findViewById(R.id.iv_frag_photo_gallery_gv_item);
		//Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
		imageView.setImageBitmap(null);
		//bitmap.recycle();
		if(mThumbnailCacher.getBitmapFromMemory(mItems.get(position).getUrl()) != null)
			mThumbnailCacher.removeBitmapFromMemory(mItems.get(position).getUrl());
	}
}