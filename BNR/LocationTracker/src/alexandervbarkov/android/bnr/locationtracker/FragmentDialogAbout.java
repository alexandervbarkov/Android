package alexandervbarkov.android.bnr.locationtracker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDialogAbout extends DialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dialog_about, parent, false);
		
		getDialog().setTitle(R.string.title_frag_dialog_about);
		
		return v;
	}
}