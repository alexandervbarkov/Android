package alexandervbarkov.android.bnr.todo;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class TaskList {
	private static TaskList sTaskList;
	private Context mAppContext;
	private ArrayList<Task> mTasks;
	private static final String TAG = "ToDo";
	private static final String FILENAME = "tasks.json";
	private JSONSerializer mSerializer;
	
	private TaskList(Context appContext){
		mAppContext = appContext;
		mSerializer = new JSONSerializer(mAppContext, FILENAME);
		try {
			mTasks = mSerializer.loadTasks();
		}
		catch (Exception e) {
			mTasks = new ArrayList<Task>();
			Log.d(TAG, "Could not load tasks", e);
		}
	}
	
	public static TaskList getTaskList(Context c) {
		if(sTaskList == null)
			sTaskList = new TaskList(c.getApplicationContext());
		return sTaskList;
	}
	
	public void addTask(Task t) {
		mTasks.add(t);
	}
	
	public void deleteTask(Task t, Activity a) {
		t.deletePhoto(a);
		mTasks.remove(t);
	}
	
	public ArrayList<Task> getTasks() {
		return mTasks;
	}
	
	public Task getTask(UUID id) {
		for(Task t : mTasks) {
			if(t.getId().equals(id))
				return t;
		}
		return null;
	}
	
	public boolean saveTasks() {
		try {
			mSerializer.saveTasks(mTasks);
			return true;
		}
		catch (Exception e){
			Log.d(TAG, "Could not save data", e);
			return false;
		}
	}
	
	public void showTaskTitles() {
		for(Task t : mTasks)
			Log.d("ToDo", t.getTitle());
	}
	
	public int getSize() {
		return mTasks.size();
	}
}