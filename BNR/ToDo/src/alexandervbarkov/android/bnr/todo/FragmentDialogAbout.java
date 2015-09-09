package alexandervbarkov.android.bnr.todo;

import alexandervbarkov.android.bnr.todo.R;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FragmentDialogAbout extends DialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_about, parent, false);
		
		getDialog().setTitle(R.string.title_about);
		
		return v;
	}
}