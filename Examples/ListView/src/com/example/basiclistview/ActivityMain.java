package com.example.basiclistview;

import android.app.Fragment;

public class ActivityMain extends ActivitySingleFragment {

	@Override
	protected Fragment createFragment() {
		return new ListFragmentNumbers();
	}
}
