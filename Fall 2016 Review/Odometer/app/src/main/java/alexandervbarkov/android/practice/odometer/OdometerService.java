package alexandervbarkov.android.practice.odometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class OdometerService extends Service {
	private LocationTracker locationTracker;
	private Location oldLocation;
	private double distance;

	public void setLocationTracker(LocationTracker locationTracker) {
		this.locationTracker = locationTracker;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LocationListener locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if(oldLocation != null) {
					distance += location.distanceTo(oldLocation);
					locationTracker.onLocationChanged(distance);
				}
				oldLocation = location;
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}
		};
		try {
			((LocationManager)getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		}
		catch(SecurityException e) {
			locationTracker.onMissingPermissions();
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new OdometerBinder();
	}

	public class OdometerBinder extends Binder {
		public OdometerService getOdometerService() {
			return OdometerService.this;
		}
	}

	public interface LocationTracker {
		void onLocationChanged(double distance);

		void onMissingPermissions();
	}
}
