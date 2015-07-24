package com.example.handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	Handler mHandler;
	ProgressBar mPb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mPb = (ProgressBar)findViewById(R.id.pb_main);
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mPb.setProgress(msg.arg1);
			}
		};
		MyHandlerThread mMyHandlerThread = new MyHandlerThread(mHandler, "test");
		mMyHandlerThread.start();
	}
	
	private class MyHandlerThread extends HandlerThread {
		Handler handler;
		
		public MyHandlerThread(Handler handler, String name) {
			super(name);
			
			this.handler = handler;
		}
		
		@Override
		protected void onLooperPrepared() {
			for(int i = 0; i < 100; ++i) {
				Message msg = Message.obtain();
				msg.arg1 = i;
				handler.sendMessage(msg);
				try {
					sleep(100);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}