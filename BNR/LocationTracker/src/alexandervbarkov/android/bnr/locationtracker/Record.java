package alexandervbarkov.android.bnr.locationtracker;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

public class Record {
	private long mId;
	private String mTitle;
	private Date mStartTime;
	private double mDistance;
	
	public Record() {
		mId = -1;
		mTitle = "";
		mStartTime = new Date();
		mDistance = 0;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public Date getStartTime() {
		return mStartTime;
	}

	public void setStartTime(Date startTime) {
		mStartTime = startTime;
	}

	public double getDistance() {
		return mDistance;
	}

	public void setDistance(double distance) {
		mDistance = distance;
	}

	public String getFormattedTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(mStartTime);
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
	
	public int getDurationSeconds(long endMilliseconds) {
		return (int)((endMilliseconds - mStartTime.getTime()) / 1000);
	}
	
	@SuppressLint("DefaultLocale")
	public static String formatDuration(int durationSeconds) {
		int seconds = durationSeconds % 60;
		int minutes = ((durationSeconds - seconds) / 60) % 60; 
		int hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
}