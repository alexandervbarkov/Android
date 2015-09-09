package alexandervbarkov.android.bnr.nerdlauncher;

import java.util.List;

import alexandervbarkov.android.bnr.nerdlauncher.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListFragmentNerdTasks extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(Integer.MAX_VALUE);
		
		ArrayAdapterRunningTaskInfo adapter = new ArrayAdapterRunningTaskInfo(tasks);
		setListAdapter(adapter);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		RunningTaskInfo info = (RunningTaskInfo)l.getAdapter().getItem(position);
		am.moveTaskToFront(info.id, ActivityManager.MOVE_TASK_WITH_HOME);
	}
	
	private class ArrayAdapterRunningTaskInfo extends  ArrayAdapter<RunningTaskInfo> {
		public ArrayAdapterRunningTaskInfo(List<RunningTaskInfo> tasks) {
			super(getActivity(), 0, tasks);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.launcher_list_item, null);
			
			RunningTaskInfo task = getItem(position);
			try {
				ApplicationInfo activity = getActivity().getPackageManager().getApplicationInfo(task.topActivity.getPackageName(), PackageManager.GET_META_DATA);
				
				ImageView ivIcon = (ImageView)convertView.findViewById(R.id.iv_launcher_icon);
				ivIcon.setImageDrawable(activity.loadIcon(getActivity().getPackageManager()));
				
				TextView tvLabel = (TextView)convertView.findViewById(R.id.tv_launcher_label);
				tvLabel.setText(activity.loadLabel(getActivity().getPackageManager()));
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.options_menu_nerd_tasks, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_activities:
				Intent i = new Intent(getActivity(), ActivityNerdLauncher.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}