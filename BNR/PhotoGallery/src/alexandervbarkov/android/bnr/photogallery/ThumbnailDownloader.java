package alexandervbarkov.android.bnr.photogallery;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class ThumbnailDownloader<Token> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD_THUMBNAIL = 0;
	
	private Handler mHandler;
	private Handler mMainHandler;
	private Map<Token, String> mRequestMap = Collections.synchronizedMap(new HashMap<Token, String>());
	public Listener<Token> mListener;
	private ThumbnailCacher mThumbnailCacher;
	
	public ThumbnailDownloader(Handler handler, ThumbnailCacher thumbnailCacher) {
		super(TAG);
		
		mMainHandler = handler;
		mThumbnailCacher = thumbnailCacher;
	}

	public interface Listener<Token> {
		void onThumbnailDownloaded(Token token, Bitmap thumbnail);
	}
	
	public void setListener(Listener<Token> listener) {
		mListener = listener;
	}
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onLooperPrepared() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == MESSAGE_DOWNLOAD_THUMBNAIL) {
					@SuppressWarnings("unchecked")
					Token token = (Token)msg.obj;
					//Log.i(TAG, "Got a request for URL: " + mRequestMap.get(token));
					handleRequest(token);
				}
			}
		};
	}
	
	public void queueThummbnail(Token token, String url) {
		//Log.d("pg", "Got a URL: " + url);
		mRequestMap.put(token, url);
		mHandler.obtainMessage(MESSAGE_DOWNLOAD_THUMBNAIL, token).sendToTarget();
	}

	private void handleRequest(final Token token) {
		try {
			final String url = mRequestMap.get(token);
			if(url == null)
				return;
			
			Log.d("uri", "Started downloading " + url);
			byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
			final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			mThumbnailCacher.addBitmapToMemory(url, bitmap);
			Log.d("uri", "Finished downloading " + url + " and created bitmap");
			
			mMainHandler.post(new Runnable() {
				@Override
				public void run() {
					if(mRequestMap.get(token) != url)
						return;
					mRequestMap.remove(token);
					mListener.onThumbnailDownloaded(token, bitmap);
				}
			});
		}
		catch (IOException ioe) {
			Log.e(TAG, "Error downloading image", ioe);
		}
	}
	
	public void clearQueue() {
		mHandler.removeMessages(MESSAGE_DOWNLOAD_THUMBNAIL);
		mRequestMap.clear();
	}
}