package alexandervbarkov.android.bnr.todo;

import alexandervbarkov.android.bnr.todo.R;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class ActivityTaskList extends SingleFragmentActivity implements ListFragmentTaskList.Callbacks, FragmentTask.Callbacks {
	@Override
	protected int getLayoutResId() {
		return R.layout.activity_pane;
	}

	@Override
	public void onTaskUpdated(Task t) {
		FragmentManager fm = getSupportFragmentManager();
		ListFragmentTaskList taskList = (ListFragmentTaskList)fm.findFragmentById(R.id.cont_fragment);
		taskList.updateUI();
	}
	
	@Override
	protected Fragment createFragment() {
		return new ListFragmentTaskList();
	}

	@Override
	public void onTaskSelected(Task t) {
		if(findViewById(R.id.cont_fragment_two) == null) {
			Intent i = new Intent(this, ActivityViewPagerTask.class);
			i.putExtra(FragmentTask.EXTRA_TASK_ID, t.getId());
			startActivityForResult(i, ListFragmentTaskList.REQUEST_CODE_TASK);
		}
		else {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			Fragment oldDetail = fm.findFragmentById(R.id.cont_fragment_two);
			if(oldDetail != null)
				ft.remove(oldDetail);
			
			Fragment newDetail = FragmentTask.newInstance(t.getId());
			ft.add(R.id.cont_fragment_two, newDetail);
			ft.commit();
		}
	}

	@Override
	public void onDeleteTask(Task t) {
		TaskList.getTaskList(this).deleteTask(t, this);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.remove(fm.findFragmentById(R.id.cont_fragment_two));
		ft.commit();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		recreate();
	}
}