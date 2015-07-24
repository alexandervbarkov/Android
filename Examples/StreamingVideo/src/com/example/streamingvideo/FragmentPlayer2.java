package com.example.streamingvideo;

import java.io.IOException;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPlayer2 extends Fragment {
	String mLink;
	Uri mURI;
	MediaPlayer mPlayer;
	SurfaceView mSv;
	SurfaceHolder mSh;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_player2, container, false);
		
		Log.d("FragmentPlayer2", "FragmentPlayer2");
		
		//mLink = "android.resource://" + getActivity().getApplication().getPackageName() + "/" + R.raw.apollo_17_stroll;
		//mLink = "rtsp://192.168.0.2:8554/stream";
		//mLink = "http://192.168.0.2:8080/stream";
		mLink = "https://ia800303.us.archive.org/7/items/BrianGreene_2012/BrianGreene_2012.mp4";
		mURI = Uri.parse(mLink);
		mSv = (SurfaceView)v.findViewById(R.id.sv_frag_player2_player);
		mSh = mSv.getHolder();
		mPlayer = new MediaPlayer();
		mSh.addCallback(new Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				mPlayer.setDisplay(mSh);
				try {
					mPlayer.setDataSource(getActivity(), mURI);
				} 
				catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
					e.printStackTrace();
				}
				mPlayer.prepareAsync();
				mPlayer.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mPlayer.start();
					}
				});
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
			}
		});
		
		return v;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mPlayer.release();
		mPlayer = null;
	}
}