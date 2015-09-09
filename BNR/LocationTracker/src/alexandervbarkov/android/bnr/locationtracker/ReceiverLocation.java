package alexandervbarkov.android.bnr.locationtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

public class ReceiverLocation extends BroadcastReceiver {
	//private static final String TAG = "ReceiverLocation";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Location location = (Location)intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
		if(location != null) {
			onLocationReceived(context, location);
			return;
		}
		if(intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
			boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
			onProviderEngabledChanged(context, enabled);
		}
	}
	
	protected void onLocationReceived(Context context, Location location) {
		//Log.d(TAG, this + " got locaiton from " + location.getProvider() + ": " + location.getLatitude() + ", " + location.getLongitude());
	}
	
	protected void onProviderEngabledChanged(Context context, boolean enabled) {
		//Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
	}
}