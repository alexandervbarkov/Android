package alexandervbarkov.android.hf.navigationdrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		long number = getArguments().getLong(MainActivity.ARG_NUMBER);
		((TextView)view.findViewById(R.id.tv_number)).setText(Long.toString(number));
		return view;
	}

}
