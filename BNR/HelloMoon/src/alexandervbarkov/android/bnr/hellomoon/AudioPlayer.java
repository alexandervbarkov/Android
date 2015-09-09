package alexandervbarkov.android.bnr.hellomoon;

import alexandervbarkov.android.bnr.hellomoon.R;
import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {
	private MediaPlayer mPlayer;
	
	public void play(Context context) {
		if(mPlayer == null) {
			mPlayer = MediaPlayer.create(context, R.raw.one_small_step);
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stop();
				}
			});
			mPlayer.start();
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
}