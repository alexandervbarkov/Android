package alexandervbarkov.android.bnr.photogallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public abstract class ActivitySingleFragment extends FragmentActivity {
	protected abstract Fragment createFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_single_fragment);
		
		FragmentManager fm = getSupportFragmentManager();
		if(fm.findFragmentById(R.id.cont_frag) == null)
			fm.beginTransaction().add(R.id.cont_frag, createFragment()).commit();
	}
}