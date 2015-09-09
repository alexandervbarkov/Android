package alexandervbarkov.android.bnr.hellomoon;


import alexandervbarkov.android.bnr.hellomoon.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ActivityHelloMoon extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_hello_moon);
	}
}