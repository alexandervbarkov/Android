package alexandervbarkov.android.bnr.todo;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import alexandervbarkov.android.bnr.todo.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentCamera extends Fragment {
	private SurfaceView mSvCamera;
	private Camera mCamera;
	private ImageButton mIbtnTake;
	private View mFlPbImage;
	private String mFilename;
	private ShutterCallback mShutterCallback;
	private PictureCallback mPictureCallback;
	public static final String EXTRA_CAMERA_ID = "bnr.android.todo.cameraid";
	private static final String TAG = "FragmentCamera";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mFilename = getArguments().getString(FragmentTask.EXTRA_FILENAME);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_camera, parent, false);
		
		mSvCamera = (SurfaceView)v.findViewById(R.id.sv_camera);
		SurfaceHolder sH = mSvCamera.getHolder();
		sH.addCallback(new Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if(mCamera != null)
						mCamera.setPreviewDisplay(holder);
				}
				catch(IOException e) {
					Log.e(TAG, "Error setting up preview dispaly", e);
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				if(mCamera != null) {
					Parameters p = mCamera.getParameters();
					List<Size> sizes = p.getSupportedPreviewSizes();
					DisplayMetrics dm = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
					int h = dm.heightPixels;
					int w = dm.widthPixels;
					Size optimalSize = getOptimalSize(sizes, w, h);
					p.setPreviewSize(optimalSize.width, optimalSize.height);
					sizes = p.getSupportedPictureSizes();
					optimalSize = getOptimalSize(sizes, w, h);
					p.setPictureSize(optimalSize.width, optimalSize.height);
					mCamera.setParameters(p);
					int rotation = getCameraOrientation(getActivity(), 0);
					mCamera.setDisplayOrientation(rotation);
					try {
						mCamera.startPreview();
					}
					catch(Exception e) {
						Log.e(TAG, "Could not start preview", e);
						mCamera.release();
						mCamera = null;
					}
				}
			}
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if(mCamera != null)
					mCamera.stopPreview();
			}
		});
		
		mShutterCallback = new ShutterCallback() {
			@Override
			public void onShutter() {
				mFlPbImage.setVisibility(View.VISIBLE);
			}
		};
		
		mPictureCallback = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				data = getRotatedImage(data, getCameraOrientation(getActivity(), 0));
				FileOutputStream fos = null;
				boolean success = true;
				try {
					fos = getActivity().openFileOutput(mFilename, Context.MODE_PRIVATE);
					fos.write(data);
				}
				catch (Exception e) {
					Log.e(TAG, "Error writing to file " + mFilename, e);
					success = false;
				}
				finally {
					try {
						if(fos != null)
							fos.close();
					}
					catch(Exception e) {
						Log.e(TAG, "Error closing file " + mFilename, e);
						success = false;
					}
				}
				if(success) {
					getActivity().setResult(Activity.RESULT_OK);
					Log.i(TAG, "File saved at " + mFilename);
				}
				else
					getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
			}
		};
		
		mIbtnTake = (ImageButton)v.findViewById(R.id.ibtn_take);
		mIbtnTake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCamera != null)
					mCamera.takePicture(mShutterCallback, null, mPictureCallback);
			}
		});
		
		mFlPbImage = v.findViewById(R.id.fl_pb_image);
		mFlPbImage.setVisibility(View.INVISIBLE);
		
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mCamera = Camera.open(0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if(mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	public static FragmentCamera newInstance(String filename) {
		Bundle args = new Bundle();
		args.putString(FragmentTask.EXTRA_FILENAME, filename);
		FragmentCamera fc = new FragmentCamera();
		fc.setArguments(args);
		return fc;
	}
	
	private Size getOptimalSize(List<Size> sizes, int w, int h) {
		if (sizes == null) return null;
		
        Size optimalSize = sizes.get(0);
        double screenRatio = (double)w / (double)h; 
        double bestRatio = (double)optimalSize.width / (double)optimalSize.height;
        double closestHeight = Math.abs((double)h - (double)optimalSize.height);
        
        for(int i = 1; i < sizes.size(); ++i) {
        	if(Math.abs(screenRatio - bestRatio) >= Math.abs(screenRatio - ((double)sizes.get(i).width / (double)sizes.get(i).height))) {
        		if(closestHeight > Math.abs((double)h - (double)sizes.get(i).height)) {
	        		bestRatio = (double)sizes.get(i).width / (double)sizes.get(i).height;
	        		optimalSize = sizes.get(i);
	        		closestHeight = Math.abs((double)h - (double)sizes.get(i).height);
        		}
        	}
        }
        return optimalSize;
    }
	
	private int getCameraOrientation(Activity activity, int cameraId) {
	    CameraInfo info = new Camera.CameraInfo();
	    Camera.getCameraInfo(cameraId, info);
	    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
	    int degrees = 0;
	    switch (rotation) {
		    case Surface.ROTATION_0:
		            degrees = 0;
		            break;
		    case Surface.ROTATION_90:
		            degrees = 90;
		            break;
		    case Surface.ROTATION_180:
		            degrees = 180;
		            break;
		    case Surface.ROTATION_270:
		            degrees = 270;
		            break;
	    }

	    int result;
	    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	            result = (info.orientation + degrees) % 360;
	            result = (360 - result) % 360; // compensate the mirror
	    } else { // back-facing
	            result = (info.orientation - degrees + 360) % 360;
	    }
	    return result;
	}
	
	private byte[] getRotatedImage(byte[] data, int degrees) {
		Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
		Matrix m = new Matrix();
		m.postRotate(degrees);
		image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), m, true);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}
}