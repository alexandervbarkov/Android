package alexandervbarkov.android.bnr.hellomoon;

import alexandervbarkov.android.bnr.hellomoon.R;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

public class VideoPlayer {
	private MediaPlayer mPlayer;
	private Context mC;
	private SurfaceView mSv;
	private SurfaceHolder mSh;
	private int mId;
	
	public VideoPlayer(Context c, SurfaceView sv, SurfaceHolder sh, int id) {
		mC = c;
		mSv = sv;
		mSh = sh;
		mId = id;
	}
	
	public void play() {
		if(mPlayer == null) {
			mPlayer = MediaPlayer.create(mC, mId);
			if(mPlayer == null)
				Toast.makeText(mC, R.string.toast_cannot_play, Toast.LENGTH_SHORT).show();
			else {
				setVideoSize();
				mPlayer.setDisplay(mSh);
				mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						stop();
					}
				});
				mPlayer.start();
			}
		}
		else {
			if(mPlayer.isPlaying() != true) {
				mPlayer.start();
			}
		}
	}
	
	public void pause() {
		if(mPlayer != null && mPlayer.isPlaying() == true)
			mPlayer.pause();
	}
	
	public void stop() {
		if(mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
	
	public void setVideoSize() {
		int vh = mPlayer.getVideoHeight();
		int vw = mPlayer.getVideoWidth();
		float vr = (float)vw / (float)vh;
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)mC).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int sh = dm.heightPixels;
		int sw = dm.widthPixels;
		float sr = (float)sw / (float)sh;
		
		//Log.d("VideoPlayer", "vh=" + vh + " vw=" + vw + " vr=" + vr + " sh=" + sh + " sw=" + sw + " sr=" + sr);
		LayoutParams lp = mSv.getLayoutParams();
		if(sr < vr) {
			lp.height = (int)(((float)sw / (float)vr));
			lp.width = sw;
		}
		else {
			lp.height = sh;
			lp.width = (int)(((float)sh * (float)vr));
		}
		//Log.d("VideoPlayer", "vh=" + lp.height + " vw=" + lp.width + " sh=" + sh + " sw=" + sw);
		mSv.setLayoutParams(lp);
	}
	
	public boolean isVideoSet() {
		return (mPlayer ==  null) ? false : true;
	}
}