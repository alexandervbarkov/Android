package alexandervbarkov.android.bnr.photogallery;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ThumbnailCacher {
	private LruCache<String, Bitmap> mMemoryCache;
	
	public ThumbnailCacher() {
		final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize);
	}
	
	public void addBitmapToMemory(String key, Bitmap image) {
		if(getBitmapFromMemory(key) == null)
			mMemoryCache.put(key, image);
	}
	
	public Bitmap getBitmapFromMemory(String key) {
		if(key == null) 
			return null;
		return mMemoryCache.get(key);
	}
	
	public void removeBitmapFromMemory(String key) {
		mMemoryCache.remove(key);
	}
	
	public void removeAllBitmapsFromMemory() {
		mMemoryCache.evictAll();
	}
}