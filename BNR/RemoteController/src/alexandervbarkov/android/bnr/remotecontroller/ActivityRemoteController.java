package alexandervbarkov.android.bnr.remotecontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;

public class ActivityRemoteController extends ActivitySingleFragment {
	@Override
	protected Fragment createFragment() {
		return new FragmentRemoteController();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
	}
}