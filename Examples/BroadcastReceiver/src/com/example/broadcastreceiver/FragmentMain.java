package com.example.broadcastreceiver;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentMain extends Fragment {
	public static final String ACTION = "com.example.broadcastreceiver.mybroadcast";
	
	private Button btnSendBroadcast;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		
		btnSendBroadcast = (Button)v.findViewById(R.id.btn_send_broadcast);
		btnSendBroadcast.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMyBroadcast();
			}
		});
		
		return v;
	}
	
	private void sendMyBroadcast() {
		Intent i = new Intent();
		i.setAction(ACTION);
		getActivity().sendBroadcast(i);
	}

}