package alexandervbarkov.android.bnr.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
	private static final String TAG = "NotificationReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received result: " + getResultCode());
		if(getResultCode() != Activity.RESULT_OK)
			// A foreground activity canceled the broadcast
			return;
		int requestCode = intent.getIntExtra("REQUEST_CODE", 0);
		Notification notification = intent.getParcelableExtra("NOTIFICATION");
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(requestCode, notification);
	}
}