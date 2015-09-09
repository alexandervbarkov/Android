package alexandervbarkov.android.bnr.draganddraw;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ViewBoxDrawing extends View {
	private static final String TAG = "ViewBoxDrawing";
	private static final String KEY_STATE = "alexandervbarkov.android.bnr.draganddraw.state";
	private static final String KEY_BOXES = "alexandervbarkov.android.bnr.draganddraw.BOXES";
	
	private Box mCurrentBox;
	private ArrayList<Box> mBoxes;
	private Paint mBoxPaint;
	private Paint mBackgroundPaint;
	private int mOrientation;

	public ViewBoxDrawing(Context context) {
		this(context, null);
	}

	public ViewBoxDrawing(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mBoxes = new ArrayList<Box>();
		
		mBoxPaint = new Paint();
		mBoxPaint.setColor(0x22ff0000);
		
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(0xfff8efe0);
		
		mOrientation = getResources().getConfiguration().orientation;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		PointF current = new PointF(event.getX(), event.getY());
		Log.d(TAG, "x=" + current.x + " y=" + current.y);
		
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG, "ACTION_DOWN");
				// Reset drawing state
				mCurrentBox = new Box(current);
				mBoxes.add(mCurrentBox);
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG, "ACTION_UP");
				if(mCurrentBox != null) {
					mCurrentBox.setCurrent(current);
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG, "ACTION_MOVE");
				mCurrentBox = null;
				break;
			case MotionEvent.ACTION_CANCEL:
				Log.d(TAG, "ACTION_CANCEL");
				mCurrentBox = null;
				break;
		}
			
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPaint(mBackgroundPaint);
		
		for(Box box : mBoxes) {
			float left = Math.min(box.getOrigin().x, box.getCurrent().x);
			float right = Math.max(box.getOrigin().x, box.getCurrent().x);
			float top = Math.min(box.getOrigin().y, box.getCurrent().y);
			float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
			canvas.drawRect(left, top, right, bottom, mBoxPaint);
		}
	}
	
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if(mOrientation != newConfig.orientation) {
			mOrientation = newConfig.orientation;
			for(int i = 0; i < mBoxes.size(); ++i) {
				PointF temp = new PointF(mBoxes.get(i).getOrigin().y, mBoxes.get(i).getOrigin().x);
				mBoxes.get(i).setOrigin(temp);
				temp = new PointF(mBoxes.get(i).getCurrent().y, mBoxes.get(i).getCurrent().x);
				mBoxes.get(i).setCurrent(temp);
			}
		}
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle savedInstanceState = new Bundle();
		
		savedInstanceState.putParcelable(KEY_STATE, super.onSaveInstanceState());
		savedInstanceState.putSerializable(KEY_BOXES, mBoxes);
		
		return savedInstanceState;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle savedInstanceState = (Bundle)state;
		
		super.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_STATE));
		
		mBoxes = (ArrayList<Box>)savedInstanceState.getSerializable(KEY_BOXES);
	}
}