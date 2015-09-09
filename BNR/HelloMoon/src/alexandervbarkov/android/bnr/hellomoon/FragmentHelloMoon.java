package alexandervbarkov.android.bnr.hellomoon;

import alexandervbarkov.android.bnr.hellomoon.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentHelloMoon extends Fragment {
	private VideoPlayer mPlayer;
	private SurfaceView mSv;
	private SurfaceHolder mSh;
	private int mId;
	private Button mBtnPlay;
	private Button mBtnPause;
	private Button mBtnStop;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_hello_moon, parent, false);

		mSv = (SurfaceView)v.findViewById(R.id.sv_video);
		mSh = mSv.getHolder();
		mId = R.raw.apollo_17_stroll;
		mPlayer = new VideoPlayer(getActivity(), mSv, mSh, mId);
		
		mBtnPlay = (Button)v.findViewById(R.id.btn_play);
		mBtnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlayer.play();
			}
		});

		mBtnPause = (Button)v.findViewById(R.id.btn_pause);
		mBtnPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlayer.pause();
				
			}
		});
		
		mBtnStop = (Button)v.findViewById(R.id.btn_stop);
		mBtnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlayer.stop();
			}
		});
		
		return v;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mPlayer.stop();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if(mPlayer.isVideoSet() == true)
			mPlayer.setVideoSize();
		
		getActivity().setTitle(R.string.app_name);
	}
	
}