package alexandervbarkov.android.bnr.locationtracker;

import alexandervbarkov.android.bnr.locationtracker.DatabaseHelperRecord.CursorLocations;
import alexandervbarkov.android.bnr.locationtracker.DatabaseHelperRecord.CursorRecords;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class TrackerManager {
	private static final String TAG = "TrackerManager";
	public static final String ACTION_LOCATION = "alexandervbarkov.android.bnr.locationtracker.ACTION_LOCATION";
	public static final String PREF_FILE = "records";
	public static final String PREF_CURRENT_RECORD_ID = "TrackerManager.currentRecordId";
	public static final String EXTRA_NOTIFICATION = "notification";
	
	private static TrackerManager sTrackerManager;
	private Context mApplicationContext;
	private LocationManager mLocationManager;
	private DatabaseHelperRecord mHelper;
	private SharedPreferences mPrefs;
	private long mCurrentRecordId;
	
	private TrackerManager(Context applicationContext) {
		mApplicationContext = applicationContext;
		mLocationManager = (LocationManager)mApplicationContext.getSystemService(Context.LOCATION_SERVICE);
		mHelper = new DatabaseHelperRecord(mApplicationContext);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
		mCurrentRecordId = mPrefs.getLong(PREF_CURRENT_RECORD_ID, -1);
	}
	
	public static TrackerManager get(Context applicationContext) {
		if(sTrackerManager == null)
			sTrackerManager = new TrackerManager(applicationContext);
		return sTrackerManager;
	}
	
	private PendingIntent getLocationPendingIntent(boolean create) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flag = create ? 0 : PendingIntent.FLAG_NO_CREATE;
		return PendingIntent.getBroadcast(mApplicationContext, 0, broadcast, flag);
	}
	
	public void startLocationUpdates() {
		String provider = LocationManager.GPS_PROVIDER;
		
		/*// Get the last known location and broadcast it if there is one
		Location lastKnownLocation = mLocationManager.getLastKnownLocation(provider);
		if(lastKnownLocation != null) {
			// Reset the time to now
			lastKnownLocation.setTime(System.currentTimeMillis());
			broadcastLocation(lastKnownLocation);
		}*/
		
		PendingIntent pi = getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, 1000, 0, pi);
	}
	
	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if(pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}
	
	public boolean isTracking() {
		return getLocationPendingIntent(false) != null;
	}
	
	/*private void broadcastLocation(Location location) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
		mApplicationContext.sendBroadcast(broadcast);
	}*/
	
	private Record insertRecord() {
		Record record = new Record();
		record.setId(mHelper.insertRecord(record));
		return record;
	}
	
	public void startTrackingRecord(Record record) {
		mCurrentRecordId = record.getId();
		mPrefs.edit().putLong(PREF_CURRENT_RECORD_ID, mCurrentRecordId).commit();
		startLocationUpdates();
		showNotification(record);
	}
	
	public Record startNewRecord() {
		// Insert record into the db
		Record record = insertRecord();
		startTrackingRecord(record);
		return record;
	}
	
	public void stopTrackingRecord() {
		stopLocationUpdates();
		mCurrentRecordId = -1;
		mPrefs.edit().remove(PREF_CURRENT_RECORD_ID).commit();
		cancelNotification();
	}
	
	public CursorRecords queryRecords() {
		return mHelper.queryRecords();
	}
	
	public Record getRecord(long id) {
		Record record = null;
		CursorRecords cursor = mHelper.queryRecords(id);
		cursor.moveToFirst();
		// If there is a row, get a record
		if(!cursor.isAfterLast())
			record = cursor.getRecord();
		cursor.close();
		return record;
	}
	
	public boolean isTrackingRecord(Record record) {
		return record != null && record.getId() == mCurrentRecordId;
	}
	
	public void updateRecordTitle(Record record) {
		if(record.getId() != -1) 
			mHelper.updateRecordTitle(record);
		else
			Log.e(TAG, "Changing title of a record without id; ingoring.");
	}
	
	public void updateRecordDistance(long id, double distance) {
		if(id != -1) 
			mHelper.updateRecordDistance(id, distance);
		else
			Log.e(TAG, "Changing distance of a record without id; ingoring.");
	}
	
	public void deleteRecord(Record record) {
		long i = mHelper.deleteRecord(record);
		//Log.d("lt", "Record id: " + record.getId() + " Records deleted:" + i);
	}
	
	public void insertLocation(Location location) {
		if(mCurrentRecordId != -1) 
			mHelper.insertLocation(mCurrentRecordId, location);
		else
			Log.e(TAG, "Location received with no tracking record; ingoring.");
	}
	
	public Location getLastLocationForRecord(long recordId) {
		Location location = null;
		CursorLocations cursor = mHelper.queryLastLocationForRecord(recordId);
		cursor.moveToFirst();
		if(!cursor.isAfterLast())
			location = cursor.getLocation();
		cursor.close();
		return location;
	}
	
	public CursorLocations queryAllLocationsForRecord(long recordId) {
		return mHelper.queryAllLocationForRecord(recordId);
	}
	
	public void deleteAllLocationsForRecord(Record record) {
		long i = mHelper.deleteAllLocationsForRecord(record);
		//Log.d("lt", "Record id: " + record.getId() + " Locations deleted:" + i);
	}
	
	private void showNotification(Record record) {
		Resources r = mApplicationContext.getResources();
		Intent i = new Intent(mApplicationContext, ActivityTrackerWithMap.class);
		i.putExtra(ActivityTrackerWithMap.EXTRA_RECORD_ID, record.getId());
		i.putExtra(EXTRA_NOTIFICATION, true);
		PendingIntent pi = PendingIntent.getActivity(mApplicationContext, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
		
		Notification notification = new Builder(mApplicationContext).
				setOngoing(true).
				setTicker(r.getString(R.string.notification_receiver_tracking_location_title)).
				setSmallIcon(R.drawable.ic_stat_notify_tracking).
				setContentTitle(r.getString(R.string.notification_receiver_tracking_location_title)).
				setContentText(r.getString(R.string.notification_receiver_tracking_location_text)).
				setContentIntent(pi).
				build();
		
		NotificationManager nm = (NotificationManager)mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(0, notification);
	}
	
	private void cancelNotification() {
		NotificationManager nm = (NotificationManager)mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(0);
	}
}