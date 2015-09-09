package alexandervbarkov.android.bnr.todo;

import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class ActivityCamera extends SingleFragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Hide window title 
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// Hide status bar
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected Fragment createFragment() {
		String filename = getIntent().getStringExtra(FragmentTask.EXTRA_FILENAME);
		return FragmentCamera.newInstance(filename);
	}
}