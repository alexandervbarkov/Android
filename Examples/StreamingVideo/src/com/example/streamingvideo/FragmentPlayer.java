package com.example.streamingvideo;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

public class FragmentPlayer extends Fragment {
	VideoView mPlayer;
	String mLink;
	Uri mURI;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLink = "https://ia800303.us.archive.org/7/items/BrianGreene_2012/BrianGreene_2012.mp4";
		//mLink = "https://ia601507.us.archive.org/31/items/20030322-bbs-sheetz/20030322-bbs-sheetz1_512kb.mp4";
		//mLink = "rtsp://192.168.0.2:8554/stream";
		//mLink = "http://192.168.0.2:8080/stream";
		mURI = Uri.parse(mLink);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_player, container, false);
		
		mPlayer = (VideoView)v.findViewById(R.id.vv_frag_player_player);
		mPlayer.setMediaController(new MediaController(getActivity()));
		mPlayer.setVideoURI(mURI);
		mPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mPlayer.start();
			}
		});
		mPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d("StreamingVideo", "what=" + what + " extra=" + extra);
				return false;
			}
		});
		
		return v;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mPlayer = null;
	}
}