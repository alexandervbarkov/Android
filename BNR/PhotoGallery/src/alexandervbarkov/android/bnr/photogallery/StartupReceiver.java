package alexandervbarkov.android.bnr.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {
	private static final String TAG = "StartupReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Received broadcast intent: " + intent.getAction());
		boolean isOn = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PollService.PREF_IS_ALARM_ON, false);
		PollService.setContext(context);
		PollService.setServiceAlarm(isOn);
	}
}