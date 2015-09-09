package alexandervbarkov.android.bnr.locationtracker;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

public class ReceiverTrackingLocation extends ReceiverLocation {
	//private static final String TAG = "ReceiverTrackingLocation";
	
	@Override
	protected void onLocationReceived(Context context, Location location) {
		location.setTime(new Date().getTime());
		TrackerManager trackerManager = TrackerManager.get(context);
		long trackingRecordId = PreferenceManager.getDefaultSharedPreferences(context).getLong(TrackerManager.PREF_CURRENT_RECORD_ID, -1);
		Location lastLocation = trackerManager.getLastLocationForRecord(trackingRecordId);
		if(lastLocation != null) {
			double distance = lastLocation.distanceTo(location) / 1000;
			if(distance > 0.0005) {
				Record record = trackerManager.getRecord(trackingRecordId);
				distance += record.getDistance();
				//Log.d("lt", "distance=" + distance);
				trackerManager.updateRecordDistance(trackingRecordId, distance);
			}
		}
		TrackerManager.get(context).insertLocation(location);
		//Log.d(TAG, "Location received");
	}
}