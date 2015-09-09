package alexandervbarkov.android.bnr.todo;

import java.util.Calendar;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class Task {
	private static final String JSON_ID = "id";
	private static final String JSON_TITLE = "title";
	private static final String JSON_DATE_SET = "date_set";
	private static final String JSON_YEAR = "year";
	private static final String JSON_MONTH = "month";
	private static final String JSON_DAY = "day";
	private static final String JSON_HOUR = "hour";
	private static final String JSON_MINUTE = "minute";
	private static final String JSON_DONE = "done";
	private static final String JSON_PHOTO = "photo";
	private static final String JSON_DESCRIPTION = "description";
	
	private UUID mId;
	private String mTitle;
	private Calendar mDate;
	private boolean mDateSet;
	private boolean mDone;
	private Photo mPhoto;
	private String mDescription;
	
	Task() {
		mId = UUID.randomUUID();
		mTitle = "";
		mDate = Calendar.getInstance();
		mDateSet = false;
		mDone = false;
		mPhoto = null;
		mDescription = "";
	}
	
	public Task(JSONObject json) throws JSONException {
		mId = UUID.fromString(json.getString(JSON_ID));
		if(json.has(JSON_TITLE))
			mTitle = json.getString(JSON_TITLE);
		else
			mTitle = null;
		mDateSet = json.getBoolean(JSON_DATE_SET);
		mDate = Calendar.getInstance();
		mDate.set(json.getInt(JSON_YEAR), json.getInt(JSON_MONTH), json.getInt(JSON_DAY), json.getInt(JSON_HOUR), json.getInt(JSON_MINUTE));
		mDone = json.getBoolean(JSON_DONE);
		if(json.has(JSON_PHOTO))
			mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
		else 
			mPhoto = null;
		if(json.has(JSON_DESCRIPTION))
			mDescription = json.getString(JSON_DESCRIPTION);
		else
			mDescription = null;
	}

	public String getTitle() {
		return mTitle;
	}
	
	@Override
	public String toString() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public UUID getId() {
		return mId;
	}

	public Calendar getDate() {
		return mDate;
	}

	public void setDate(Calendar date) {
		mDate = date;
	}

	public boolean isDateSet() {
		return mDateSet;
	}
	
	public void setDateSet(boolean dateSet) {
		mDateSet = dateSet;
	}
	
	public boolean isDone() {
		return mDone;
	}

	public void setDone(boolean done) {
		mDone = done;
	}
	
	public Photo getPhoto() {
		return mPhoto;
	}
	
	public void setPhoto(Photo photo) {
		mPhoto = photo;
	}
	
	public void deletePhoto(Activity a) {
		if(getPhoto() != null) {
			a.deleteFile(getPhoto().getFilename());
			setPhoto(null);
		}
	}
	
	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public static String getFormattedDate(Calendar c) {
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		String s = months[c.get(Calendar.MONTH)] + " " + 
				Integer.toString(c.get(Calendar.DATE)) + ", " + 
				Integer.toString(c.get(Calendar.YEAR)) + " ";
		s += (c.get(Calendar.HOUR) == 0) ? "12:" : Integer.toString(c.get(Calendar.HOUR)) + ":";
		s += (c.get(Calendar.MINUTE) < 10) ? "0" : "";
		s += Integer.toString(c.get(Calendar.MINUTE)) + " ";
		s += (c.get(Calendar.AM_PM) == 0) ? "AM" : "PM";
		return s;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		if(mTitle != null)
			json.put(JSON_TITLE, mTitle);
		json.put(JSON_DATE_SET, mDateSet);
		json.put(JSON_YEAR, mDate.get(Calendar.YEAR));
		json.put(JSON_MONTH, mDate.get(Calendar.MONTH));
		json.put(JSON_DAY, mDate.get(Calendar.DAY_OF_MONTH));
		json.put(JSON_HOUR, mDate.get(Calendar.HOUR_OF_DAY));
		json.put(JSON_MINUTE, mDate.get(Calendar.MINUTE));
		json.put(JSON_DONE, mDone);
		if(mPhoto != null) 
			json.put(JSON_PHOTO, mPhoto.toJSON());
		if(mDescription != null)
			json.put(JSON_DESCRIPTION, mDescription);
		return json;
	}
}