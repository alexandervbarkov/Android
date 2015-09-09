package alexandervbarkov.android.bnr.todo;

import alexandervbarkov.android.bnr.todo.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class FragmentSettings extends Fragment {
	private boolean mShowMotto;
	private String mMotto;
	private boolean mTheme;
	private CheckBox mCbMotto;
	private EditText mEtMotto;
	private CheckBox mCbTheme;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//getActivity().setTheme(R.style.dark_theme);
		super.onCreate(savedInstanceState);
		
		getActivity().setTitle(R.string.title_settings);
		
		mShowMotto = UtilsSettings.getShowMotto(getActivity().getApplication());
		mMotto = UtilsSettings.getMotto(getActivity().getApplication());
		mTheme = UtilsSettings.getTheme(getActivity().getApplication()); 
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_settings, parent, false);
		
		mEtMotto = (EditText)v.findViewById(R.id.et_motto);
		mEtMotto.setText(mMotto);
		mEtMotto.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() != 0)
					mMotto = s.toString(); 
				else
					mMotto = null;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mCbMotto = (CheckBox)v.findViewById(R.id.cb_motto);
		mCbMotto.setChecked(mShowMotto);
		mCbMotto.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mShowMotto = isChecked;
				if(mShowMotto)
					mEtMotto.setVisibility(View.VISIBLE);
				else
					mEtMotto.setVisibility(View.GONE);
			}
		});
		
		mCbTheme = (CheckBox)v.findViewById(R.id.cb_theme);
		mCbTheme.setChecked(mTheme);
		mCbTheme.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mTheme = isChecked;
				getActivity().recreate();
			}
		});
		
		if(mShowMotto)
			mEtMotto.setVisibility(View.VISIBLE);
		else
			mEtMotto.setVisibility(View.GONE);
		
		return v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		UtilsSettings.saveSettings(getActivity().getApplicationContext(), mShowMotto, mMotto, mTheme);
	}
}
