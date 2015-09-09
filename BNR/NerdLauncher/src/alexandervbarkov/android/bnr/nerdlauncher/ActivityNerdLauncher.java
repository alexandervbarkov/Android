package alexandervbarkov.android.bnr.nerdlauncher;

import alexandervbarkov.android.bnr.nerdlauncher.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ActivityNerdLauncher extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_nerd_launcher);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f;
		if(fm.findFragmentById(R.id.cont_frag) != null)
			f = fm.findFragmentById(R.id.cont_frag);
		else {
			f = new ListFragmentNerdLauncher();
			ft.add(R.id.cont_frag, f).commit();
		}
	}
}
