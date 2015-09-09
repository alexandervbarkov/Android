package alexandervbarkov.android.bnr.todo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class UtilsImage {
	public static BitmapDrawable getScaledImage(Activity activity, String path, int width, int height) {
		Bitmap image = BitmapFactory.decodeFile(path);
		if(image == null) {
			Log.d("ToDo", "Image is null path=" + path);
			return null;
		}
		int iWidth = image.getWidth();
		int iHeight = image.getHeight();
		double iRatio = (double)iWidth / (double)iHeight;
		//Log.d("ToDo", "width = " +  width + " height = " + width);
		
		if(iWidth > iHeight)
			height = (int)Math.round((double)width / iRatio);
		else
			width = (int)Math.round((double)height * iRatio);
		
		float sx = (float)width / (float)iWidth;
		float sy = (float)height / (float)iHeight;
		
		Matrix m = new Matrix();
		m.postScale(sx, sy);
		
		Bitmap scaledImage = Bitmap.createBitmap(image, 0, 0, iWidth, iHeight, m, true);
		
		//Log.d("ToDo", "width = " +  scaledImage.getWidth() + " height = " + scaledImage.getHeight());
		return new BitmapDrawable(activity.getResources(), scaledImage);
	}
	
	public static void setScaledImageView(ImageView imageView, BitmapDrawable image) {
		LayoutParams params = imageView.getLayoutParams();
		params.width = image.getBitmap().getWidth();
		params.height = image.getBitmap().getHeight();
		imageView.setLayoutParams(params);
	}
	
	public static void resetScaledImageView(ImageView imageView, int width, int height) {
		LayoutParams params = imageView.getLayoutParams();
		params.width = width;
		params.height = height;
		imageView.setLayoutParams(params);
	}
	
	public static void cleanImageView(ImageView imageView) {
		if(imageView.getDrawable() instanceof BitmapDrawable) {
			BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
			b.getBitmap().recycle();
			imageView.setImageDrawable(null);
		}
	}
}