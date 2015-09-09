package alexandervbarkov.android.bnr.photogallery;

import android.support.v4.app.Fragment;

public class ActivityPhotoPage extends ActivitySingleFragment {

	@Override
	protected Fragment createFragment() {
		return new FragmentPhotoPage();
	}
}