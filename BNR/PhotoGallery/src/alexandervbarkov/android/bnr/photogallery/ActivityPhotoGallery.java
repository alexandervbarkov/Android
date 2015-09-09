package alexandervbarkov.android.bnr.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

public class ActivityPhotoGallery extends ActivitySingleFragment {
	private static final String TAG = "ActivityPhotoGallery";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment createFragment() {
		return new FragmentPhotoGallery();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		FragmentPhotoGallery fragment = (FragmentPhotoGallery)getSupportFragmentManager().findFragmentById(R.id.cont_frag);
		
		if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit().putString(FlickrFetchr.PREF_SEARCH_QUERY, query).commit();
			PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit().putBoolean(FlickrFetchr.PREF_NEW_SEARCH, true).commit();
		}
		
		fragment.updateItems();
	}
}