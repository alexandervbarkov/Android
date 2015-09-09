package alexandervbarkov.android.bnr.todo;

import java.util.ArrayList;
import java.util.UUID;

import alexandervbarkov.android.bnr.todo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

public class ActivityViewPagerTask extends FragmentActivity implements FragmentTask.Callbacks {
	private ViewPager mViewPager;
	FragmentStatePagerAdapter mPagerAdapter;
	private ArrayList<Task> mTasks;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(UtilsSettings.getTheme(getApplication()))
			setTheme(R.style.dark_theme);
		else
			setTheme(R.style.AppTheme);
		
		super.onCreate(savedInstanceState);
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.view_pager);
		setContentView(mViewPager);
		mTasks = TaskList.getTaskList(this).getTasks();
		setPager();
		
		UUID id = (UUID)getIntent().getSerializableExtra(FragmentTask.EXTRA_TASK_ID);
		for(int i = 0; i < mTasks.size(); ++i) {
			if(id.equals(mTasks.get(i).getId())) {
				//setCurrentItem makes viewpager open the selected item instead of the first item in the list
				mViewPager.setCurrentItem(i);
				//setAppTitle(i);
				break;
			}
		}
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {	
			@Override
			public void onPageSelected(int position) {
				//setAppTitle(position);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if(UtilsSettings.getShowMotto(getApplication()))
			getActionBar().setSubtitle(UtilsSettings.getMotto(getApplication()));
		else
			getActionBar().setSubtitle(null);
	}
	
	private void setPager() {
		mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				Task task = mTasks.get(position);
				return FragmentTask.newInstance(task.getId());
			}

			@Override
			public int getCount() {
				return mTasks.size();
			}
		};
		mViewPager.setAdapter(mPagerAdapter);
	}
	
	private void setAppTitle(int position) {
		Task task = mTasks.get(position);
		if(task.getTitle() != null)
			setTitle(task.getTitle());
	}
	
	public void deletePage() {
		int index = mViewPager.getCurrentItem();
		Log.d("ToDo", "Deleting item with id=" + mTasks.get(index).getId());
		TaskList.getTaskList(this).deleteTask(mTasks.get(index), this);
		mTasks = TaskList.getTaskList(this).getTasks();
		if(mTasks.size() == 0) {
			if(NavUtils.getParentActivityName(this) != null)
				NavUtils.navigateUpFromSameTask(this);
		}
		else {
			setPager();
			if(index == mPagerAdapter.getCount())
				mViewPager.setCurrentItem(index - 1);
			else
				mViewPager.setCurrentItem(index);
		}
	}

	@Override
	public void onTaskUpdated(Task t) {
		
	}

	@Override
	public void onDeleteTask(Task t) {
		
	}
}