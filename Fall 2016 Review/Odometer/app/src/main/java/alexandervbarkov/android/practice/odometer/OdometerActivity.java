package alexandervbarkov.android.practice.odometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class OdometerActivity extends Activity {
	private TextView tvDistance;
	private boolean boundToService;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			boundToService = true;
			((OdometerService.OdometerBinder)service).getOdometerService().setLocationTracker(new OdometerService.LocationTracker() {
				@Override
				public void onLocationChanged(double distance) {
					String distanceFormatted = new DecimalFormat("#").format(distance);
					tvDistance.setText(distanceFormatted + " m");
				}

				@Override
				public void onMissingPermissions() {
					Toast.makeText(OdometerActivity.this, "Missing GPS permissions", Toast.LENGTH_LONG).show();
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			boundToService = false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_odometer);
		tvDistance = (TextView)findViewById(R.id.tv_distance);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, OdometerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(boundToService) {
			unbindService(serviceConnection);
			boundToService = false;
		}
	}
}
