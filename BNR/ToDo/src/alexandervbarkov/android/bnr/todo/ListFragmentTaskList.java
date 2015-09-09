package alexandervbarkov.android.bnr.todo;

import java.util.ArrayList;

import alexandervbarkov.android.bnr.todo.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListFragmentTaskList extends ListFragment {
	private static final String DIALOG_ABOUT = "bnr.android.todo.about";
	private static final int REQUEST_CODE_SETTINGS = 0;
	public static final int REQUEST_CODE_TASK = 1;
	
	private ArrayList<Task> mTasks;
	private Callbacks mCallbacks;
	private int mSelectedTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		
		mTasks = TaskList.getTaskList(getActivity()).getTasks();
		ArrayAdapterTasks adapter = new ArrayAdapterTasks(mTasks);
		setListAdapter(adapter);
		
		setHasOptionsMenu(true);
		
		mCallbacks.onTaskUpdated(null);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list_fragment_task_list, parent, false);
		
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		/*if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			// Use floating context menu 
			registerForContextMenu(listView);
		else {*/
			// Use contextual action bar
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.list_fragment_task_list_context, menu);
					return true;
				}
				
				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					switch(item.getItemId()) {
						case R.id.item_delete_task:
							boolean isSelectedDeleted = false;
							ArrayAdapterTasks adapter = (ArrayAdapterTasks)getListAdapter();
							for(int i = adapter.getCount() - 1; i >= 0; --i) {
								if(getListView().isItemChecked(i)) {
									Log.d("ToDo", "Deleting item with id=" + adapter.getItem(i).getId());
									TaskList.getTaskList(getActivity()).deleteTask(adapter.getItem(i), getActivity());
									TaskList.getTaskList(getActivity()).saveTasks();
									if(mSelectedTask == i)
										isSelectedDeleted = true;
								}
							}
							mode.finish();
							adapter.notifyDataSetChanged();
							if(isSelectedDeleted) {
								if(getActivity().findViewById(R.id.cont_fragment_two) != null) {
									FragmentManager fm = getActivity().getSupportFragmentManager();
									if(fm.findFragmentById(R.id.cont_fragment_two) != null)
										fm.beginTransaction().remove(fm.findFragmentById(R.id.cont_fragment_two)).commit();
								}
							}
							return true;
						default:
							return false;
					}
				}
				
				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
					
				}
				
				@Override
				public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
					return false;
				}
				
				@Override
				public void onDestroyActionMode(ActionMode arg0) {
					
				}
			});
		//}
			
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if(UtilsSettings.getShowMotto(getActivity().getApplication()))
			getActivity().getActionBar().setSubtitle(UtilsSettings.getMotto(getActivity().getApplication()));
		else
			getActivity().getActionBar().setSubtitle(null);
		
		//Update values in the list when return from task activity
		updateUI();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		TaskList.getTaskList(getActivity()).saveTasks();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("ToDo", "recreated in list " + UtilsSettings.getTheme(getActivity().getApplication()));
		getActivity().recreate();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.list_fragment_task_list_option, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_new_task:
				Task t =  new Task();
				TaskList.getTaskList(getActivity()).addTask(t);
				mSelectedTask = TaskList.getTaskList(getActivity()).getSize() - 1;
				mCallbacks.onTaskSelected(t);
				mCallbacks.onTaskUpdated(t);
				updateUI();
				return true;
			case R.id.item_settings:
				Intent i2 = new Intent(getActivity(), ActivitySettings.class);
				startActivityForResult(i2, REQUEST_CODE_SETTINGS);
				return true;
			case R.id.item_about:
				FragmentDialogAbout dialog = new FragmentDialogAbout();
				dialog.show(getActivity().getSupportFragmentManager(), DIALOG_ABOUT);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.list_fragment_task_list_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		ArrayAdapterTasks adapter = (ArrayAdapterTasks)getListAdapter();
		Task t = adapter.getItem(position);
		switch(item.getItemId()) {
			case R.id.item_delete_task:
				TaskList.getTaskList(getActivity()).deleteTask(t, getActivity());
				adapter.notifyDataSetChanged();
				if(getActivity().findViewById(R.id.cont_fragment_two) != null) {
					FragmentManager fm = getActivity().getSupportFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.remove(fm.findFragmentById(R.id.cont_fragment_two));
					ft.commit();
				}
				return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Task t = ((ArrayAdapterTasks)(getListAdapter())).getItem(position);
		mCallbacks.onTaskSelected(t);
		mSelectedTask = position;
	}
	
	private class ArrayAdapterTasks extends ArrayAdapter<Task> {
		public ArrayAdapterTasks(ArrayList<Task> tasks) {
			super(getActivity(), 0, tasks);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//if not given a view, inflate one
			if(convertView == null)
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_task, null);
			
			//configure the view for this task
			Task t = getItem(position);
			
			ImageView ivList = (ImageView)convertView.findViewById(R.id.iv_list);
			showPhoto(t, ivList);
			
			TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_list_task_title);
			if(t.getTitle() != null)
				tvTitle.setText(t.getTitle());
			
			TextView tvDate = (TextView)convertView.findViewById(R.id.tv_list_task_date);
			if(t.isDateSet() == true) {
				tvDate.setVisibility(View.VISIBLE);
				String dateTime = Task.getFormattedDate(t.getDate());
				tvDate.setText(dateTime);
			}
			else
				tvDate.setVisibility(View.INVISIBLE);
			
			CheckBox cbDone = (CheckBox)convertView.findViewById(R.id.cb_list_task_done);
			cbDone.setChecked(t.isDone());
			
			return convertView;
		}
		
		private void showPhoto(Task task, ImageView imageView) {
			if(task.getPhoto() != null) {
				String path = getActivity().getFileStreamPath(task.getPhoto().getFilename()).getAbsolutePath();
				BitmapDrawable b = UtilsImage.getScaledImage(getActivity(), path, imageView.getLayoutParams().width, imageView.getLayoutParams().height);
				//ImageUtils.setScaledImageView(imageView, b);
				if(b !=  null) {
					imageView.setVisibility(View.VISIBLE);
					imageView.setImageDrawable(b);
				}
			}
			else {
				int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
				int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
				UtilsImage.resetScaledImageView(imageView, width, height);
				imageView.setVisibility(View.GONE);
			}
		}
	}
	
	public void updateUI() {
		((ArrayAdapterTasks)getListAdapter()).notifyDataSetChanged();
	}
	
	public interface Callbacks {
		void onTaskSelected(Task t);
		void onTaskUpdated(Task t);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (Callbacks)activity;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}
}