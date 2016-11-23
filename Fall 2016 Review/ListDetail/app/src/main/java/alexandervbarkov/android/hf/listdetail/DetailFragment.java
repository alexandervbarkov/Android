package alexandervbarkov.android.hf.listdetail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	public static String ARG_NUMBER = "number";
	private long number;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null)
			number = savedInstanceState.getLong(ARG_NUMBER);
		else {
			number = getArguments().getLong(ListDetailActivity.EXTRA_NUMBER);
			InnerFragment innerFragment = new InnerFragment();
			innerFragment.setArguments(getArguments());
			getChildFragmentManager().beginTransaction().replace(R.id.fl_inner_fragment_cont, innerFragment).commit();
		}
		return inflater.inflate(R.layout.fragment_detail, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(getView() != null)
			((TextView)getView().findViewById(R.id.tv_number)).setText(Long.toString(number));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ARG_NUMBER, number);
	}
}
