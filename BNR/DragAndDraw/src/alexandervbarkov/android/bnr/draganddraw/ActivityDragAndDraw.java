package alexandervbarkov.android.bnr.draganddraw;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

public class ActivityDragAndDraw extends ActivitySingleFragment {
	@Override
	protected Fragment createFragment() {
		return new FragmentDragAndDraw();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
	}
}