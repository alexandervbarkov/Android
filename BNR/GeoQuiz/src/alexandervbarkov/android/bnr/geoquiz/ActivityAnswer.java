package alexandervbarkov.android.bnr.geoquiz;

import alexandervbarkov.android.bnr.geoquiz.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityAnswer extends Activity {
	private ImageButton mIbtnShowAnswerYes;
	private TextView mTvAnswer;
	private boolean mAnswer;
	private boolean mAnswerShown;
	private static final String KEY_ANSWER_SHOWN = "answer_shown";
	public static final String EXTRA_ANSWER = "com.bnr.geoquiz.extra_answer";
	public static final String EXTRA_ANSWER_SHOWN = "com.bnr.geoquiz.extra_answer_shown";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_answer);
		
		mAnswerShown = getIntent().getBooleanExtra(ActivityAnswer.EXTRA_ANSWER_SHOWN, false);
		
		if(savedInstanceState != null)
			mAnswerShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN, false);
		
		mTvAnswer = (TextView) findViewById(R.id.tv_answer);	
		
		mAnswer = getIntent().getBooleanExtra(ActivityAnswer.EXTRA_ANSWER, false);
		
		isAnswerShown(mAnswerShown);
		
		mIbtnShowAnswerYes = (ImageButton) findViewById(R.id.ibtn_show_answer_yes);
		mIbtnShowAnswerYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mAnswer)
					mTvAnswer.setText(R.string.t_true);
				else
					mTvAnswer.setText(R.string.t_false);
				mAnswerShown = true;
				mIbtnShowAnswerYes.setVisibility(View.INVISIBLE);
				isAnswerShown(mAnswerShown);
			}
		});
		
		if(mAnswerShown) {
			mIbtnShowAnswerYes.setVisibility(View.INVISIBLE);
			if(mAnswer)
				mTvAnswer.setText(R.string.t_true);
			else
				mTvAnswer.setText(R.string.t_false);
		}
	}

	private void isAnswerShown(boolean isShown) {
		Intent data = new Intent();
		data.putExtra(EXTRA_ANSWER_SHOWN, isShown);
		setResult(RESULT_OK, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		savedInstanceState.putBoolean(KEY_ANSWER_SHOWN, mAnswerShown);
	}
	
}