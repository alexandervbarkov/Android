package com.example.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "MyBroadcastReceiver received broadcast.", Toast.LENGTH_LONG).show();
		Log.d("MyBroadcastReceiver", "MyBroadcastReceiver received broadcast.");
	}

}