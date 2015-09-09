package alexandervbarkov.android.bnr.todo;

import android.support.v4.app.Fragment;

public class ActivitySettings extends SingleFragmentActivity {
	@Override
	protected Fragment createFragment() {
		return new FragmentSettings();
	}
}