package alexandervbarkov.android.hf.listdetail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class InnerFragment extends Fragment {
	private long number;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		number = getArguments().getLong(ListDetailActivity.EXTRA_NUMBER);
		return inflater.inflate(R.layout.fragment_inner, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		if(getView() != null)
			getView().findViewById(R.id.btn_click_me).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), Long.toString(number), Toast.LENGTH_SHORT).show();
				}
			});
	}
}
