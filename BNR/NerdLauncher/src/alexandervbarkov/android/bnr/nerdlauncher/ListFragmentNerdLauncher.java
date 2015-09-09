package alexandervbarkov.android.bnr.nerdlauncher;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import alexandervbarkov.android.bnr.nerdlauncher.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListFragmentNerdLauncher extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent startupIntent =  new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> activities = getActivity().getPackageManager().queryIntentActivities(startupIntent, 0);
		
		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo a, ResolveInfo b) {
				PackageManager pm = getActivity().getPackageManager();
				return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(), b.loadLabel(pm).toString());
			}
		});	
		
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(100);
		
		ArrayAdapterResolveInfo adapter = new ArrayAdapterResolveInfo(activities);
		setListAdapter(adapter);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ResolveInfo activity = (ResolveInfo)l.getAdapter().getItem(position);
		ActivityInfo info = activity.activityInfo;
		if(info == null) return;
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setClassName(info.applicationInfo.packageName, info.name);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
	
	private class ArrayAdapterResolveInfo extends  ArrayAdapter<ResolveInfo> {
		public ArrayAdapterResolveInfo(List<ResolveInfo> activities) {
			super(getActivity(), 0, activities);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.launcher_list_item, null);
			
			ResolveInfo activity = getItem(position);
			
			ImageView ivIcon = (ImageView)convertView.findViewById(R.id.iv_launcher_icon);
			ivIcon.setImageDrawable(activity.loadIcon(getActivity().getPackageManager()));
			
			TextView tvLabel = (TextView)convertView.findViewById(R.id.tv_launcher_label);
			tvLabel.setText(activity.loadLabel(getActivity().getPackageManager()));
			
			return convertView;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.options_menu_nerd_launcher, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_tasks:
				Intent i = new Intent(getActivity(), ActivityNerdTasks.class);
				startActivity(i);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}