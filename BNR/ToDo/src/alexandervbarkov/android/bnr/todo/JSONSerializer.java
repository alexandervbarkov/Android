package alexandervbarkov.android.bnr.todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class JSONSerializer {
	private Context mContext;
	private String mFileName;
	
	public JSONSerializer(Context c, String f) {
		mContext = c;
		mFileName = f;
	}
	
	public void saveTasks(ArrayList<Task> tasks) throws JSONException, IOException {
		JSONArray ja = new JSONArray();
		for(Task t : tasks) 
			ja.put(t.toJSON());
		
		//Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(ja.toString());
		}
		finally {
			if(writer != null)
				writer.close();
		}
	}
	
	public ArrayList<Task> loadTasks() throws JSONException, IOException {
		ArrayList<Task> tasks = new ArrayList<Task>();
		BufferedReader reader = null;
		try {
			//Open and read the file into the StringBuilder
			InputStream in = mContext.openFileInput(mFileName);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
				jsonString.append(line);
			//Parse JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			//Build the array of tasks using JSONObjects
			for(int i = 0; i < array.length(); ++i)
				tasks.add(new Task(array.getJSONObject(i)));
		}
		catch(Exception e) {
			Log.e("ToDo", "Could not load data", e);
		}
		finally {
			if(reader != null)
				reader.close();
		}
		return tasks;
	}
}