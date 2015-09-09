package alexandervbarkov.android.bnr.locationtracker;

import java.util.Date;

import alexandervbarkov.android.bnr.locationtracker.DatabaseHelperRecord.CursorLocations;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class FragmentRecordMap extends SupportMapFragment implements LoaderCallbacks<Cursor> {
	private static final String ARG_RECORD_ID = "record_id";
	private static final int LOAD_LOCATIONS = 0;
	
	private GoogleMap mGoogleMap;
	private CursorLocations mCursorLocations;
	private long mRecordId;
	private Marker mFinishMarker;
	private PolylineOptions mLineOptions;
	private Polyline mLine;
	private Builder mBuilder;
	private boolean mCursorLoaded;
	private boolean mMapLoaded;
	private boolean mStartMarkerPlaced;
	private BroadcastReceiver mLocationReceiver = new ReceiverLocation() {
		@Override
		protected void onLocationReceived(Context context, Location location) {
			long trackingRecordId = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getLong(TrackerManager.PREF_CURRENT_RECORD_ID, -1);
			if(mRecordId != trackingRecordId)
				return;
			
			updateMap(location);
		}
		
		@Override
		protected void onProviderEngabledChanged(Context context, boolean enabled) {
			int toastText = enabled ? R.string.toast_frag_tracker_gps_enabled : R.string.toast_frag_tracker_gps_disabled;
			Toast.makeText(getActivity().getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
		}
	};
	
	public static FragmentRecordMap newInstance(long recordId) {
		Bundle args = new Bundle();
		args.putLong(ARG_RECORD_ID, recordId);
		FragmentRecordMap fragment = new FragmentRecordMap();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		mLineOptions = new PolylineOptions();
		mLineOptions.color(Color.parseColor("#7fff0000"));
		mLine = null;
		mBuilder = new Builder();
		mCursorLoaded = false;
		mMapLoaded = false;
		mStartMarkerPlaced = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, parent, savedInstanceState);
		
		// Stash a reference to the GoogleMap
		mGoogleMap = getMap();
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				mMapLoaded = true;
				if(mCursorLoaded)
					drawMap();
			}
		});
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		getActivity().registerReceiver(mLocationReceiver, new IntentFilter(TrackerManager.ACTION_LOCATION));
	}
	
	@Override
	public void onResume() {
		Bundle args = getArguments();
		if(args != null) {
			mRecordId = args.getLong(ARG_RECORD_ID, -1);
			if(mRecordId != -1) 
				getLoaderManager().initLoader(LOAD_LOCATIONS, args, this);
		}
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		getLoaderManager().destroyLoader(0);
		
		super.onPause();
	}
	
	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		
		super.onStop();
	}
	
	public void drawMap() {
		if(mGoogleMap == null || mCursorLocations == null)
			return;
		
		mLineOptions = new PolylineOptions();
		mLineOptions.color(Color.parseColor("#7fff0000"));
		mBuilder = new Builder();
		
		mGoogleMap.clear();
		
		boolean hasPoints = false;
		mCursorLocations.moveToFirst();
		while(!mCursorLocations.isAfterLast()) {
			hasPoints = true;
			mStartMarkerPlaced = true;
			
			// Create line
			Location location = mCursorLocations.getLocation();
			LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
			mLineOptions.add(point);
			mBuilder.include(point);
			
			// Create markers
			Resources r = getResources();
			if(mCursorLocations.isFirst()) {
				String startTime = new Date(location.getTime()).toString();
				MarkerOptions startMarker = new MarkerOptions().
						position(point).
						title(r.getString(R.string.label_frag_record_run_start)).
						snippet(r.getString(R.string.label_frag_record_run_started_at, startTime));
				mGoogleMap.addMarker(startMarker);
			}
			else if(mCursorLocations.isLast()) {
				String finishTime = new Date(location.getTime()).toString();
				MarkerOptions finishMarker = new MarkerOptions().
						position(point).
						title(r.getString(R.string.label_frag_record_run_finish)).
						snippet(r.getString(R.string.label_frag_record_run_finished_at, finishTime)).
						icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				mFinishMarker = mGoogleMap.addMarker(finishMarker);
			}
			
			mCursorLocations.moveToNext();
		}
		if(hasPoints) {
			mLine = mGoogleMap.addPolyline(mLineOptions);
			LatLngBounds bounds = mBuilder.build();
			CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(bounds, 50);
			mGoogleMap.moveCamera(movement);
		}
	}
	
	private void updateMap(Location location) {
		if(!mMapLoaded)
			return;
		
		LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
		mLineOptions.add(point);
		mBuilder.include(point);
		
		Resources r = getResources();
		
		if(!mStartMarkerPlaced) {
			mStartMarkerPlaced = true;
			String startTime = new Date(location.getTime()).toString();
			MarkerOptions startMarker = new MarkerOptions().
					position(point).
					title(r.getString(R.string.label_frag_record_run_start)).
					snippet(r.getString(R.string.label_frag_record_run_started_at, startTime));
			mGoogleMap.addMarker(startMarker);
		}
		
		if(mFinishMarker != null)
			mFinishMarker.remove();
		String finishTime = new Date(location.getTime()).toString();
		MarkerOptions finishMarker = new MarkerOptions().
				position(point).
				title(r.getString(R.string.label_frag_record_run_finish)).
				snippet(r.getString(R.string.label_frag_record_run_finished_at, finishTime)).
				icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		mFinishMarker = mGoogleMap.addMarker(finishMarker);
		
		LatLngBounds bounds = mBuilder.build();
		CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(bounds, 50);
		mGoogleMap.moveCamera(movement);
		if(mLine != null)
			mLine.remove();
		mLine = mGoogleMap.addPolyline(mLineOptions);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new LoaderLocationsListCursor(getActivity().getApplicationContext(), args.getLong(ARG_RECORD_ID, -1));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mCursorLocations = (CursorLocations)cursor;
		mCursorLoaded = true;
		if(mMapLoaded) 
			drawMap();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursorLocations.close();
		mCursorLocations = null;
	}
}