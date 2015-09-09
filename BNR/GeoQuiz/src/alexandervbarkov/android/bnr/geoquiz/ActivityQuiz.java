package alexandervbarkov.android.bnr.geoquiz;

import alexandervbarkov.android.bnr.geoquiz.R;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Log;

public class ActivityQuiz extends Activity {

	//private static final String TAG = "ActivityQuiz";
	private ImageButton mIbtnTrue;
	private ImageButton mIbtnFalse;
	private ImageButton mIbtnShowAnswer;
	private ImageButton mIbtnPrevious;
	private ImageButton mIbtnNext;
	private TextView mTvQuestion;
	private TextView mTvApiLevel;
	private TrueFalse [] mQuestionsAnswers = new TrueFalse [] {
		new TrueFalse(R.string.question_oceans, true),
		new TrueFalse(R.string.question_mideast, false),
		new TrueFalse(R.string.question_africa, false),
		new TrueFalse(R.string.question_americas, true),
		new TrueFalse(R.string.question_asia, true),
		new TrueFalse(R.string.question_turkey, false)
	};
	private int mIndex = 0;
	private boolean [] mAnswersShown = new boolean [mQuestionsAnswers.length];
	private static final String KEY_INDEX = "index";
	private static final String KEY_ANSWERS_SHOWN = "is_answer_shown";
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState != null) {
			mIndex = savedInstanceState.getInt(KEY_INDEX, 0);
			mAnswersShown = savedInstanceState.getBooleanArray(KEY_ANSWERS_SHOWN);
		}
		
		setContentView(R.layout.activity_quiz);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionbar = getActionBar();
			actionbar.setSubtitle("Geography Quiz");
		}
		
		mTvQuestion = (TextView) findViewById(R.id.tv_question);
		updateQuestion();
		
		mIbtnTrue = (ImageButton) findViewById(R.id.ibtn_true);
		mIbtnTrue.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAnswer(true);
			}
		});
		
		mIbtnFalse = (ImageButton) findViewById(R.id.ibtn_false);
		mIbtnFalse.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				checkAnswer(false);
			}
		});
		
		mIbtnShowAnswer = (ImageButton) findViewById(R.id.ibtn_show_answer);
		mIbtnShowAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ActivityQuiz.this, ActivityAnswer.class);
				i.putExtra(ActivityAnswer.EXTRA_ANSWER, mQuestionsAnswers[mIndex].isAnswer());
				i.putExtra(ActivityAnswer.EXTRA_ANSWER_SHOWN, mAnswersShown[mIndex]);
				startActivityForResult(i, 0);
			}
		});
		
		mIbtnPrevious = (ImageButton) findViewById(R.id.ibtn_previous);
		mIbtnPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIndex = (mIndex == 0) ? mQuestionsAnswers.length - 1 : --mIndex;
				updateQuestion();
			}
		});
		
		mIbtnNext = (ImageButton) findViewById(R.id.ibtn_next);
		mIbtnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mIndex = (mIndex == mQuestionsAnswers.length - 1) ? 0 : ++mIndex;
				updateQuestion();
			}
		});

		mTvApiLevel = (TextView) findViewById(R.id.tv_api_level);
		mTvApiLevel.setText(Build.VERSION.RELEASE);
		
		//Log.d(TAG, "onCreate");
	}
	
	private void updateQuestion() {
		mTvQuestion.setText(mQuestionsAnswers[mIndex].getQuestion());
	}
	
	private void checkAnswer(boolean answer) {
		if(mAnswersShown[mIndex])
			Toast.makeText(ActivityQuiz.this, R.string.toast_answer_shown, Toast.LENGTH_LONG).show();
		else {
			if(mQuestionsAnswers[mIndex].isAnswer() == answer)
				Toast.makeText(ActivityQuiz.this, R.string.toast_correct, Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(ActivityQuiz.this, R.string.toast_incorrect, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override 
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(KEY_INDEX, mIndex);
		savedInstanceState.putBooleanArray(KEY_ANSWERS_SHOWN, mAnswersShown);
		//Log.d(TAG, "onSaveInstanceState");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data ==  null)
			return;
		mAnswersShown[mIndex] = data.getBooleanExtra(ActivityAnswer.EXTRA_ANSWER_SHOWN, false);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//Log.d(TAG, "onStart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//Log.d(TAG, "onResume");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//Log.d(TAG, "onPause");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//Log.d(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Log.d(TAG, "onDestroy");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_quiz, menu);
		return true;
	}
}
