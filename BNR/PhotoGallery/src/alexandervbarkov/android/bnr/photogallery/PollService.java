package alexandervbarkov.android.bnr.photogallery;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class PollService extends IntentService {
	private static final String TAG = "pollservice";
	private static final int POLL_INTERVAL = 1000 * 15;	// 15 seconds
	public static final String PREF_IS_ALARM_ON = "is_alarm_on";
	public static final String ACTION_SHOW_NOTIFICATION = "alexandervbarkov.android.bnr.photogallery.SHOW_NOTIFICATION";
	public static final String PERM_PRIVATE = "alexandervbarkov.android.bnr.photogallery.PRIVATE";
	
	private static Context sContext;
	private static int sPage;
	private static int sSearchPage;

	public PollService() {
		super(TAG);
	}
	
	public static void setup(Context context, int page, int searchPage) {
		sContext = context;
		sPage = page;
		sSearchPage = searchPage;
	}
	
	public static void setContext(Context context) {
		sContext = context;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Check for connectivity 
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		boolean isNetworkingAvailable = cm.getBackgroundDataSetting() && cm.getActiveNetworkInfo() !=  null;
		if(!isNetworkingAvailable)
			return;
		
		Log.d("pg", "Service started with intent " + intent);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
		String lastItemId = prefs.getString(FlickrFetchr.PREF_LAST_ITEM_ID, null);
		ArrayList<GalleryItem> items;
		if(sContext == null) {
			Log.d("pg", "sContext is null");
			sContext = getApplicationContext();
		}
		else
			Log.d("pg", "sContext is not null");
		if(query != null) {
			Log.d("pg", "Started searching for images of " + query);
			items = new FlickrFetchr().searchItems(query, sSearchPage, sContext);
		}
		else {
			Log.d("pg", "Started fetching recent images");
			items = new FlickrFetchr().getRecent(sPage, sContext);
		}
		if(items.size() == 0)
			return;
		String itemId = items.get(0).getId();
		if(itemId.equals(lastItemId))
			Log.d("ps", "Got an old item: " + itemId);
		else {
			Log.d("ps", "Got a new item: " + itemId);
			Resources r = getResources();
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, ActivityPhotoGallery.class), 0);
			
			Notification notification = new Builder(this).
					setTicker(r.getString(R.string.notification_title_new_pictures)).
					setSmallIcon(android.R.drawable.ic_menu_report_image).
					setContentTitle(r.getString(R.string.notification_title_new_pictures)).
					setContentText(r.getString(R.string.notification_text_total)).
					setContentIntent(pi).
					setAutoCancel(true).
					build();
			
			showBackgroundNotification(0, notification);
		}
		prefs.edit().putString(FlickrFetchr.PREF_LAST_ITEM_ID, itemId).commit();
	}
	
	public static void setServiceAlarm(boolean isOn) {
		Intent i = new Intent(sContext, PollService.class);
		PendingIntent pi = PendingIntent.getService(sContext, 0, i, 0);
		
		AlarmManager alarmManager = (AlarmManager)sContext.getSystemService(Context.ALARM_SERVICE);
		if(isOn) 
			alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), POLL_INTERVAL, pi);
		else {
			alarmManager.cancel(pi);
			pi.cancel();
		}
		
		PreferenceManager.getDefaultSharedPreferences(sContext).edit().putBoolean(PREF_IS_ALARM_ON, isOn).commit();
	}
	
	public static boolean isServiceAlarmOn() {
		Intent i = new Intent(sContext, PollService.class);
		PendingIntent pi = PendingIntent.getService(sContext, 0, i, PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}
	
	private void showBackgroundNotification(int requestCode, Notification notification) {
		Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
		i.putExtra("REQUEST_CODE", requestCode);
		i.putExtra("NOTIFICATION", notification);
		sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
	}
}