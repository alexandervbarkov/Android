package alexandervbarkov.android.bnr.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UtilsSettings {
	private static final String SETTINGS = "settings";
	private static final String SHOW_MOTTO = "com.example.todo.show_motto";
	private static final String MOTTO = "com.example.todo.motto";
	private static final String THEME = "com.example.todo.theme";
	
	public static void saveSettings(Context c, boolean showMotto, String motto, boolean theme) {
		SharedPreferences sp = c.getSharedPreferences(SETTINGS, 0);
		Editor e = sp.edit();
		e.putBoolean(SHOW_MOTTO, showMotto);
		e.putString(MOTTO, motto);
		e.putBoolean(THEME, theme);
		e.commit();
	}
	
	public static boolean getShowMotto(Context c) {
		SharedPreferences sp = c.getSharedPreferences(SETTINGS, 0);
		return sp.getBoolean(SHOW_MOTTO, false);
	}
	
	public static String getMotto(Context c) {
		SharedPreferences sp = c.getSharedPreferences(SETTINGS, 0);
		return sp.getString(MOTTO, null);
	}
	
	public static boolean getTheme(Context c) {
		SharedPreferences sp = c.getSharedPreferences(SETTINGS, 0);
		return sp.getBoolean(THEME, false);
	}
}