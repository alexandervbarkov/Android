package alexandervbarkov.android.bnr.remotecontroller;

import alexandervbarkov.android.bnr.remotecontroller.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FragmentRemoteController extends Fragment {
	private TextView mTvSelected;
	private TextView mTvInput;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_remote_controller, parent, false);
		
		mTvSelected = (TextView)v.findViewById(R.id.tv_frag_remote_controller_selected);
		
		mTvInput = (TextView)v.findViewById(R.id.tv_frag_remote_controller_input);
		
		OnClickListener buttonListenerNumber = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button button = (Button)v;
				mTvInput.setText(mTvInput.getText().toString() + button.getText().toString());
			}
		};
		
		TableLayout table = (TableLayout)v.findViewById(R.id.tbll_frag_remote_controller);
		int number = 1;
		for(int i = 2; i < table.getChildCount() - 1; ++i) {
			TableRow row = (TableRow)table.getChildAt(i);
			for(int j = 0; j < row.getChildCount(); ++j) {
				Button button = (Button)row.getChildAt(j);
				button.setText("" + number);
				button.setOnClickListener(buttonListenerNumber);
				++number;
			}
		}
		
		TableRow row = (TableRow)table.getChildAt(table.getChildCount() - 1);
		
		Button btnZero = (Button)row.getChildAt(1);
		btnZero.setText("0");
		btnZero.setOnClickListener(buttonListenerNumber);
		
		Button btnClear = (Button)row.getChildAt(0);
		btnClear.setText(getText(R.string.btn_remote_controller_clear));
		btnClear.setTextAppearance(getActivity(), R.style.remote_controller_button_remote_controler_button_bold);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTvInput.setText("");
			}
		});
		
		Button btnEnter = (Button)row.getChildAt(2);
		btnEnter.setText(getText(R.string.btn_remote_controller_enter));
		btnEnter.setTextAppearance(getActivity(), R.style.remote_controller_button_remote_controler_button_bold);
		btnEnter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTvSelected.setText(mTvInput.getText());
				mTvInput.setText("");
			}
		});
		
		return v;
	}
}